package maki.mapshare;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerSharing {
    private static final Logger LOG = LoggerFactory.getLogger("MakiMapShare");

    private static MinecraftServer server;
    private static ShareState state;
    private static boolean failed;
    private static boolean dirty;
    private static boolean publish;
    private static long generation;
    private static long bootTime;

    private static final Map<UUID, Watch> WATCH = new HashMap<>();
    private static final Map<UUID, Death> DEATHS = new LinkedHashMap<>();
    private static final Map<UUID, Integer> BUDGET = new HashMap<>();

    private ServerSharing() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ServerSharing::tick);
        NeoForge.EVENT_BUS.addListener(ServerSharing::death);
        NeoForge.EVENT_BUS.addListener(ServerSharing::logout);
        NeoForge.EVENT_BUS.addListener(ServerSharing::stopped);
    }

    private static Path file() {
        return server.getWorldPath(LevelResource.ROOT).resolve("data/maki-diorama-share.json");
    }

    private static void ensure(MinecraftServer currentServer) {
        if (server == currentServer) {
            return;
        }

        server = currentServer;
        state = new ShareState();
        failed = false;
        dirty = false;
        publish = false;
        generation = 0L;
        bootTime = System.currentTimeMillis();
        WATCH.clear();
        DEATHS.clear();
        BUDGET.clear();

        try {
            if (Files.exists(file())) {
                try (BufferedReader reader = Files.newBufferedReader(file(), StandardCharsets.UTF_8)) {
                    state = ShareNetwork.GSON.fromJson(reader, ShareState.class);
                }
                state.validate();
            }
        } catch (IOException | RuntimeException exception) {
            failed = true;
            LOG.error("Map sharing disabled: existing archive could not be read; original file was preserved", exception);
        }
    }

    public static void receive(ServerPlayer player, JsonObject payload) {
        ensure(player.getServer());
        if (failed) {
            return;
        }

        UUID playerId = player.getUUID();
        if (BUDGET.merge(playerId, 1, Integer::sum) > 4096) {
            return;
        }

        String operation = payload.get("op").getAsString();
        long revision = payload.has("rev") ? Math.max(0L, payload.get("rev").getAsLong()) : 0L;
        String playerName = player.getGameProfile().getName();
        boolean changed = false;

        switch (operation) {
            case "put":
                changed = state.putManual(
                    playerId,
                    playerName,
                    ShareNetwork.GSON.fromJson(payload.get("mark"), ShareState.Mark.class)
                );
                break;
            case "delete":
                changed = state.deleteManual(playerId, UUID.fromString(payload.get("source").getAsString()));
                break;
            case "importDeath":
                ShareState.Mark imported = ShareNetwork.GSON.fromJson(payload.get("mark"), ShareState.Mark.class);
                if (imported != null && imported.created() < bootTime) {
                    ShareState.Mark normalized = new ShareState.Mark(
                        imported.source(),
                        playerId,
                        playerName,
                        true,
                        "",
                        "",
                        imported.x(),
                        imported.y(),
                        imported.z(),
                        imported.dimension(),
                        -10460052,
                        imported.created(),
                        0L
                    );
                    changed = state.addDeath(normalized, true);
                }
                break;
            case "migrationDone":
                changed = state.migrated.add(playerId);
                break;
            case "request":
                WATCH.put(playerId, new Watch(ShareState.count(payload.get("count").getAsInt()), revision));
                publish = true;
                break;
            default:
                return;
        }

        Watch watch = WATCH.get(playerId);
        if (watch != null) {
            WATCH.put(playerId, new Watch(watch.count(), Math.max(watch.ack(), revision)));
        }

        if (changed) {
            dirty = true;
            publish = true;
        } else if (watch != null && revision > 0L) {
            publish = true;
        }
    }

    private static void death(LivingDeathEvent event) {
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ensure(player.getServer());
        if (failed) {
            return;
        }

        BlockPos position = player.blockPosition();
        DEATHS.put(
            player.getUUID(),
            new Death(
                player,
                new ShareState.Mark(
                    UUID.randomUUID(),
                    player.getUUID(),
                    player.getGameProfile().getName(),
                    true,
                    "",
                    "",
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    player.level().dimension().location().toString(),
                    -10460052,
                    System.currentTimeMillis(),
                    0L
                )
            )
        );
    }

    private static void tick(ServerTickEvent.Post event) {
        ensure(event.getServer());
        if (failed) {
            return;
        }

        for (Death death : DEATHS.values()) {
            if (death.player().isDeadOrDying() && state.addDeath(death.mark(), false)) {
                dirty = true;
                publish = true;
            }
        }
        DEATHS.clear();

        if (dirty) {
            try {
                save();
                dirty = false;
            } catch (IOException exception) {
                LOG.error("Could not save shared waypoints; will retry", exception);
                return;
            }
        }

        if (publish) {
            long snapshotGeneration = ++generation;
            for (Map.Entry<UUID, Watch> entry : WATCH.entrySet()) {
                ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                if (player != null
                    && ((ICommonPacketListener) player.connection).hasChannel(ShareNetwork.Message.TYPE)) {
                    snapshot(player, entry.getValue(), snapshotGeneration);
                }
            }
            publish = false;
        }

        if (server.getTickCount() % 20 == 0) {
            BUDGET.clear();
        }
    }

    private static void snapshot(ServerPlayer player, Watch watch, long snapshotGeneration) {
        JsonObject begin = ShareNetwork.object("begin");
        begin.addProperty("generation", snapshotGeneration);
        begin.addProperty("world", state.worldId.toString());
        begin.addProperty("ack", watch.ack());
        send(player, begin);

        for (ShareState.View view : state.view(watch.count())) {
            JsonObject row = ShareNetwork.object("row");
            row.addProperty("generation", snapshotGeneration);
            row.add("view", ShareNetwork.GSON.toJsonTree(view));
            send(player, row);
        }

        JsonObject end = ShareNetwork.object("end");
        end.addProperty("generation", snapshotGeneration);
        send(player, end);
    }

    private static void send(ServerPlayer player, JsonObject payload) {
        PacketDistributor.sendToPlayer(player, ShareNetwork.message(payload), new CustomPacketPayload[0]);
    }

    private static void save() throws IOException {
        Path target = file();
        Files.createDirectories(target.getParent());
        Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
        Files.writeString(temporary, ShareNetwork.GSON.toJson(state), StandardCharsets.UTF_8);

        if (Files.exists(target)) {
            Files.copy(
                target,
                target.resolveSibling(target.getFileName() + ".bak"),
                StandardCopyOption.REPLACE_EXISTING
            );
        }

        try {
            Files.move(
                temporary,
                target,
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void logout(PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        WATCH.remove(playerId);
        BUDGET.remove(playerId);
    }

    private static void stopped(ServerStoppedEvent event) {
        if (server != event.getServer()) {
            return;
        }

        if (dirty && !failed) {
            try {
                save();
            } catch (IOException exception) {
                LOG.error("Final shared waypoint save failed", exception);
            }
        }

        server = null;
        state = null;
        WATCH.clear();
        DEATHS.clear();
        BUDGET.clear();
    }

    private record Death(ServerPlayer player, ShareState.Mark mark) {
    }

    private record Watch(int count, long ack) {
    }
}
