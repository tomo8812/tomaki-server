package maki.mapshare;

import com.google.gson.JsonObject;
import dev.diorama.Settings;
import dev.diorama.WorldKey;
import dev.diorama.waypoint.Waypoint;
import dev.diorama.waypoint.Waypoint.Kind;
import dev.diorama.waypoint.WaypointStore;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import maki.mapshare.mixin.StoreAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.LoggerFactory;

public final class ClientSharing {
    private static Object connection;
    private static WaypointStore store;
    private static String key;
    private static int ticks;
    private static int lastCount;
    private static long revision;
    private static long pendingGeneration;
    private static long pendingAck;
    private static boolean ready;
    private static List<ShareState.View> views = List.of();
    private static List<ShareState.View> staging;
    private static final ClientSyncState sync = new ClientSyncState();
    private static ClientSharing.Memory memory = new ClientSharing.Memory();
    private static final Map<UUID, Waypoint> projections = new HashMap<>();

    private static void clear() {
        connection = null;
        store = null;
        key = null;
        ticks = 0;
        lastCount = 0;
        revision = 0L;
        pendingGeneration = 0L;
        pendingAck = 0L;
        ready = false;
        views = List.of();
        staging = null;
        sync.clear();
        projections.clear();
        memory = new ClientSharing.Memory();
    }

    public static boolean active() {
        return ready && Minecraft.getInstance().getConnection() == connection;
    }

    private static boolean channel() {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        return listener != null && ((ICommonPacketListener) listener).hasChannel(ShareNetwork.Message.TYPE);
    }

    public static boolean serverDeaths() {
        return active();
    }

    public static List<Waypoint> raw(WaypointStore waypointStore) {
        return ((StoreAccess) waypointStore).maki$raw();
    }

    private static UUID me() {
        return Minecraft.getInstance().player.getUUID();
    }

    private static ShareState.Mark draft(Waypoint waypoint, boolean death) {
        return new ShareState.Mark(
            waypoint.id,
            me(),
            Minecraft.getInstance().player.getGameProfile().getName(),
            death,
            waypoint.name,
            waypoint.initials,
            waypoint.x,
            waypoint.y,
            waypoint.z,
            waypoint.dimension,
            waypoint.color,
            waypoint.created,
            0L
        );
    }

    private static Map<UUID, ShareState.Mark> own() {
        Map<UUID, ShareState.Mark> result = new LinkedHashMap<>();
        for (Waypoint waypoint : raw(store)) {
            if (waypoint.kind == Kind.MANUAL) {
                result.put(waypoint.id, draft(waypoint, false));
            }
        }
        return result;
    }

    private static void send(JsonObject payload) {
        payload.addProperty("rev", ++revision);
        PacketDistributor.sendToServer(ShareNetwork.message(payload), new CustomPacketPayload[0]);
    }

    private static void put(String operation, ShareState.Mark mark) {
        JsonObject payload = ShareNetwork.object(operation);
        payload.add("mark", ShareNetwork.GSON.toJsonTree(mark));
        send(payload);
    }

    private static void delete(UUID source) {
        JsonObject payload = ShareNetwork.object("delete");
        payload.addProperty("source", source.toString());
        send(payload);
    }

    private static void request() {
        JsonObject payload = ShareNetwork.object("request");
        lastCount = SharingMod.HISTORY.get();
        payload.addProperty("count", lastCount);
        send(payload);
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || !channel()) {
            if (connection != null) {
                clear();
            }
            return;
        }

        WaypointStore currentStore = WaypointStore.get();
        if (currentStore == null) {
            return;
        }

        if (minecraft.getConnection() != connection || currentStore != store) {
            clear();
            connection = minecraft.getConnection();
            store = currentStore;
            key = WorldKey.current();
            loadMemory();

            Map<UUID, ShareState.Mark> current = own();
            for (UUID source : memory.known) {
                if (!current.containsKey(source)) {
                    delete(source);
                }
            }
            for (ShareState.Mark mark : current.values()) {
                put("put", mark);
            }
            for (Waypoint waypoint : raw(store)) {
                if (waypoint.kind == Kind.DEATH) {
                    put("importDeath", draft(waypoint, true));
                }
            }

            send(ShareNetwork.object("migrationDone"));
            sync.markSent(current);
            request();
        }

        if (++ticks % 10 == 0) {
            Map<UUID, ShareState.Mark> current = own();
            Map<UUID, ShareState.Mark> sent = sync.sent();

            for (UUID source : sent.keySet()) {
                if (!current.containsKey(source)) {
                    delete(source);
                }
            }
            for (Map.Entry<UUID, ShareState.Mark> entry : current.entrySet()) {
                if (!entry.getValue().equals(sent.get(entry.getKey()))) {
                    put("put", entry.getValue());
                }
            }

            sync.markSent(current);
            if (lastCount != SharingMod.HISTORY.get()) {
                request();
            }
        }
    }

    public static void receive(JsonObject payload) {
        if (connection == null || Minecraft.getInstance().getConnection() != connection) {
            return;
        }

        String operation = payload.get("op").getAsString();
        long generation = payload.get("generation").getAsLong();
        switch (operation) {
            case "begin":
                pendingGeneration = generation;
                pendingAck = payload.get("ack").getAsLong();
                staging = new ArrayList<>();
                break;
            case "row":
                if (staging == null || generation != pendingGeneration) {
                    return;
                }
                if (staging.size() >= 100000) {
                    staging = null;
                    return;
                }

                ShareState.View view = ShareNetwork.GSON.fromJson(payload.get("view"), ShareState.View.class);
                if (view == null || !ShareState.valid(view.mark()) || view.age() < 0 || view.age() > 1000) {
                    throw new IllegalArgumentException("Invalid server map marker");
                }
                staging.add(view);
                break;
            case "end":
                if (staging == null || generation != pendingGeneration) {
                    return;
                }

                views = List.copyOf(staging);
                staging = null;
                ready = true;
                projections.clear();

                Map<UUID, ShareState.Mark> serverOwn = new LinkedHashMap<>();
                for (ShareState.View viewEntry : views) {
                    ShareState.Mark mark = viewEntry.mark();
                    if (!mark.death() && mark.owner().equals(me())) {
                        serverOwn.put(mark.source(), mark);
                    }
                }
                sync.acceptSnapshot(serverOwn, pendingAck);

                if (sync.isAcknowledgedThrough(revision)) {
                    mergeOwn();
                }
                break;
            default:
                break;
        }
    }

    private static void mergeOwn() {
        Map<UUID, ShareState.Mark> localBeforeMerge = own();
        Set<UUID> unsentLocal = sync.unsentChanges(localBeforeMerge);
        Map<UUID, ShareState.Mark> serverOwn = sync.acknowledged();
        Set<UUID> serverIds = new HashSet<>(serverOwn.keySet());
        List<Waypoint> localWaypoints = raw(store);

        boolean changed = localWaypoints.removeIf(waypoint ->
            waypoint.kind == Kind.MANUAL
                && memory.known.contains(waypoint.id)
                && !serverIds.contains(waypoint.id)
                && !unsentLocal.contains(waypoint.id)
        );

        for (ShareState.Mark mark : serverOwn.values()) {
            if (unsentLocal.contains(mark.source())) {
                continue;
            }

            Waypoint waypoint = localWaypoints.stream()
                .filter(candidate -> candidate.kind == Kind.MANUAL && candidate.id.equals(mark.source()))
                .findFirst()
                .orElse(null);

            if (waypoint == null) {
                waypoint = new Waypoint();
                waypoint.id = mark.source();
                localWaypoints.add(waypoint);
                changed = true;
            }

            if (!draft(waypoint, false).equals(mark)) {
                waypoint.name = mark.name();
                waypoint.initials = mark.initials();
                waypoint.x = mark.x();
                waypoint.y = mark.y();
                waypoint.z = mark.z();
                waypoint.dimension = mark.dimension();
                waypoint.color = mark.color();
                waypoint.created = mark.created();
                changed = true;
            }
        }

        memory.known = serverIds;
        saveMemory();

        if (changed) {
            store.changed();
        }

        sync.alignSent(own(), unsentLocal);
    }

    public static List<Waypoint> overlay(WaypointStore waypointStore, List<Waypoint> source, String dimension) {
        if (!active() || waypointStore != store) {
            return source;
        }

        List<Waypoint> result = new ArrayList<>();
        for (Waypoint waypoint : source) {
            if (waypoint.kind != Kind.DEATH) {
                result.add(waypoint);
            }
        }

        for (ShareState.View view : views) {
            ShareState.Mark mark = view.mark();
            if ((!mark.death() || Settings.get().deathMarkers && view.age() <= SharingMod.HISTORY.get())
                && (dimension == null || dimension.equals(mark.dimension()))
                && (mark.death()
                    || !mark.owner().equals(me())
                    || source.stream().noneMatch(waypoint -> waypoint.id.equals(mark.source())))) {
                UUID displayId = mark.displayId();
                Waypoint projection = projections.computeIfAbsent(displayId, ignored -> {
                    Waypoint waypoint = new Waypoint(view.label(), mark.x(), mark.y(), mark.z(), mark.dimension(), mark.color());
                    waypoint.id = displayId;
                    waypoint.initials = mark.death() ? Integer.toString(view.age()) : mark.initials();
                    waypoint.kind = mark.death() ? Kind.DEATH : Kind.MANUAL;
                    waypoint.created = mark.created();
                    return waypoint;
                });
                projection.visible = !memory.hidden.contains(displayId);
                result.add(projection);
            }
        }
        return result;
    }

    public static boolean shared(Waypoint waypoint) {
        return active() && projections.containsKey(waypoint.id);
    }

    public static void visible(Waypoint waypoint, boolean visible) {
        if (visible) {
            memory.hidden.remove(waypoint.id);
        } else {
            memory.hidden.add(waypoint.id);
        }

        waypoint.visible = visible;
        saveMemory();
    }

    private static Path memoryFile() {
        return WorldKey.folder(key).resolve("maki-share-sync.json");
    }

    private static void loadMemory() {
        try {
            Path file = memoryFile();
            if (Files.exists(file)) {
                memory = ShareNetwork.GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), ClientSharing.Memory.class);
                if (memory == null || memory.known == null || memory.hidden == null) {
                    throw new IllegalArgumentException("Invalid sharing preferences");
                }
            }
        } catch (Exception exception) {
            memory = new ClientSharing.Memory();
            LoggerFactory.getLogger("MakiMapShare").warn("Could not read sharing preferences; native waypoints were preserved", exception);
        }
    }

    private static void saveMemory() {
        try {
            Path file = memoryFile();
            Files.createDirectories(file.getParent());
            Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(temporary, ShareNetwork.GSON.toJson(memory), StandardCharsets.UTF_8);

            try {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception exception) {
            LoggerFactory.getLogger("MakiMapShare").warn("Could not save local sharing preferences", exception);
        }
    }

    public static final class Memory {
        public Set<UUID> known = new HashSet<>();
        public Set<UUID> hidden = new HashSet<>();
    }
}
