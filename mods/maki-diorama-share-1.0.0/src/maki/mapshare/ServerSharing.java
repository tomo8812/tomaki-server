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
import java.util.Map.Entry;
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
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
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
   private static final Map<UUID, ServerSharing.Watch> WATCH = new HashMap<>();
   private static final Map<UUID, ServerSharing.Death> DEATHS = new LinkedHashMap<>();
   private static final Map<UUID, Integer> budget = new HashMap<>();

   public static void register() {
      NeoForge.EVENT_BUS.addListener(ServerSharing::tick);
      NeoForge.EVENT_BUS.addListener(ServerSharing::death);
      NeoForge.EVENT_BUS.addListener(ServerSharing::logout);
      NeoForge.EVENT_BUS.addListener(ServerSharing::stopped);
   }

   private static Path file() {
      return server.getWorldPath(LevelResource.ROOT).resolve("data/maki-diorama-share.json");
   }

   private static void ensure(MinecraftServer var0) {
      if (server != var0) {
         server = var0;
         state = new ShareState();
         failed = false;
         dirty = false;
         publish = false;
         generation = 0L;
         bootTime = System.currentTimeMillis();
         WATCH.clear();
         DEATHS.clear();
         budget.clear();

         try {
            if (Files.exists(file())) {
               try (BufferedReader var1 = Files.newBufferedReader(file(), StandardCharsets.UTF_8)) {
                  state = (ShareState)ShareNetwork.GSON.fromJson(var1, ShareState.class);
               }

               state.validate();
            }
         } catch (IOException | RuntimeException var6) {
            failed = true;
            LOG.error("Map sharing disabled: existing archive could not be read; original file was preserved", var6);
         }
      }
   }

   public static void receive(ServerPlayer var0, JsonObject var1) {
      ensure(var0.getServer());
      if (!failed) {
         UUID var2 = var0.getUUID();
         int var3 = budget.merge(var2, 1, Integer::sum);
         if (var3 <= 4096) {
            String var4 = var1.get("op").getAsString();
            long var5 = var1.has("rev") ? Math.max(0L, var1.get("rev").getAsLong()) : 0L;
            String var7 = var0.getGameProfile().getName();
            boolean var8 = false;
            switch (var4) {
               case "put":
                  var8 = state.putManual(var2, var7, (ShareState.Mark)ShareNetwork.GSON.fromJson(var1.get("mark"), ShareState.Mark.class));
                  break;
               case "delete":
                  var8 = state.deleteManual(var2, UUID.fromString(var1.get("source").getAsString()));
                  break;
               case "importDeath":
                  ShareState.Mark var11 = (ShareState.Mark)ShareNetwork.GSON.fromJson(var1.get("mark"), ShareState.Mark.class);
                  if (var11 != null && var11.created() < bootTime) {
                     ShareState.Mark var12 = new ShareState.Mark(
                        var11.source(), var2, var7, true, "", "", var11.x(), var11.y(), var11.z(), var11.dimension(), -10460052, var11.created(), 0L
                     );
                     var8 = state.addDeath(var12, true);
                  }
                  break;
               case "migrationDone":
                  var8 = state.migrated.add(var2);
                  break;
               case "request":
                  WATCH.put(var2, new ServerSharing.Watch(ShareState.count(var1.get("count").getAsInt()), var5));
                  publish = true;
                  break;
               default:
                  return;
            }

            ServerSharing.Watch var13 = WATCH.get(var2);
            if (var13 != null) {
               WATCH.put(var2, new ServerSharing.Watch(var13.count(), Math.max(var13.ack(), var5)));
            }

            if (var8) {
               dirty = true;
               publish = true;
            } else if (var13 != null && var5 > 0L) {
               publish = true;
            }
         }
      }
   }

   private static void death(LivingDeathEvent var0) {
      if (!var0.isCanceled() && var0.getEntity() instanceof ServerPlayer var1) {
         ensure(var1.getServer());
         if (!failed) {
            BlockPos var3 = var1.blockPosition();
            DEATHS.put(
               var1.getUUID(),
               new ServerSharing.Death(
                  var1,
                  new ShareState.Mark(
                     UUID.randomUUID(),
                     var1.getUUID(),
                     var1.getGameProfile().getName(),
                     true,
                     "",
                     "",
                     var3.getX(),
                     var3.getY(),
                     var3.getZ(),
                     var1.level().dimension().location().toString(),
                     -10460052,
                     System.currentTimeMillis(),
                     0L
                  )
               )
            );
         }
      }
   }

   private static void tick(Post var0) {
      ensure(var0.getServer());
      if (!failed) {
         for (ServerSharing.Death var2 : DEATHS.values()) {
            if (var2.player().isDeadOrDying() && state.addDeath(var2.mark(), false)) {
               dirty = true;
               publish = true;
            }
         }

         DEATHS.clear();
         if (dirty) {
            try {
               save();
               dirty = false;
            } catch (IOException var6) {
               LOG.error("Could not save shared waypoints; will retry", var6);
               return;
            }
         }

         if (publish) {
            long var7 = ++generation;

            for (Entry var4 : WATCH.entrySet()) {
               ServerPlayer var5 = server.getPlayerList().getPlayer((UUID)var4.getKey());
               if (var5 != null && ((ICommonPacketListener)var5.connection).hasChannel(ShareNetwork.Message.TYPE)) {
                  snapshot(var5, (ServerSharing.Watch)var4.getValue(), var7);
               }
            }

            publish = false;
         }

         if (server.getTickCount() % 20 == 0) {
            budget.clear();
         }
      }
   }

   private static void snapshot(ServerPlayer var0, ServerSharing.Watch var1, long var2) {
      JsonObject var4 = ShareNetwork.object("begin");
      var4.addProperty("generation", var2);
      var4.addProperty("world", state.worldId.toString());
      var4.addProperty("ack", var1.ack());
      send(var0, var4);

      for (ShareState.View var6 : state.view(var1.count())) {
         JsonObject var7 = ShareNetwork.object("row");
         var7.addProperty("generation", var2);
         var7.add("view", ShareNetwork.GSON.toJsonTree(var6));
         send(var0, var7);
      }

      JsonObject var8 = ShareNetwork.object("end");
      var8.addProperty("generation", var2);
      send(var0, var8);
   }

   private static void send(ServerPlayer var0, JsonObject var1) {
      PacketDistributor.sendToPlayer(var0, ShareNetwork.message(var1), new CustomPacketPayload[0]);
   }

   private static void save() throws IOException {
      Path var0 = file();
      Files.createDirectories(var0.getParent());
      Path var1 = var0.resolveSibling(var0.getFileName() + ".tmp");
      Files.writeString(var1, ShareNetwork.GSON.toJson(state), StandardCharsets.UTF_8);
      if (Files.exists(var0)) {
         Files.copy(var0, var0.resolveSibling(var0.getFileName() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
      }

      try {
         Files.move(var1, var0, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException var3) {
         Files.move(var1, var0, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private static void logout(PlayerLoggedOutEvent var0) {
      WATCH.remove(var0.getEntity().getUUID());
      budget.remove(var0.getEntity().getUUID());
   }

   private static void stopped(ServerStoppedEvent var0) {
      if (server == var0.getServer()) {
         if (dirty && !failed) {
            try {
               save();
            } catch (IOException var2) {
               LOG.error("Final shared waypoint save failed", var2);
            }
         }

         server = null;
         state = null;
         WATCH.clear();
         DEATHS.clear();
         budget.clear();
      }
   }

   private record Death(ServerPlayer player, ShareState.Mark mark) {
   }

   private record Watch(int count, long ack) {
   }
}
