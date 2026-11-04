package maki.mapshare;

import com.google.gson.JsonObject;
import dev.diorama.Settings;
import dev.diorama.WorldKey;
import dev.diorama.waypoint.Waypoint;
import dev.diorama.waypoint.WaypointStore;
import dev.diorama.waypoint.Waypoint.Kind;
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
import java.util.Map.Entry;
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
   private static final Map<UUID, ShareState.Mark> sent = new LinkedHashMap<>();
   private static ClientSharing.Memory memory = new ClientSharing.Memory();
   private static final Map<UUID, Waypoint> projections = new HashMap<>();

   private static void clear() {
      connection = null;
      store = null;
      key = null;
      ticks = 0;
      lastCount = 0;
      revision = 0L;
      ready = false;
      views = List.of();
      staging = null;
      sent.clear();
      projections.clear();
      memory = new ClientSharing.Memory();
   }

   public static boolean active() {
      return ready && Minecraft.getInstance().getConnection() == connection;
   }

   private static boolean channel() {
      ClientPacketListener var0 = Minecraft.getInstance().getConnection();
      return var0 != null && ((ICommonPacketListener)var0).hasChannel(ShareNetwork.Message.TYPE);
   }

   public static boolean serverDeaths() {
      return active();
   }

   public static List<Waypoint> raw(WaypointStore var0) {
      return ((StoreAccess)var0).maki$raw();
   }

   private static UUID me() {
      return Minecraft.getInstance().player.getUUID();
   }

   private static ShareState.Mark draft(Waypoint var0, boolean var1) {
      return new ShareState.Mark(
         var0.id,
         me(),
         Minecraft.getInstance().player.getGameProfile().getName(),
         var1,
         var0.name,
         var0.initials,
         var0.x,
         var0.y,
         var0.z,
         var0.dimension,
         var0.color,
         var0.created,
         0L
      );
   }

   private static Map<UUID, ShareState.Mark> own() {
      LinkedHashMap var0 = new LinkedHashMap();

      for (Waypoint var2 : raw(store)) {
         if (var2.kind == Kind.MANUAL) {
            var0.put(var2.id, draft(var2, false));
         }
      }

      return var0;
   }

   private static void send(JsonObject var0) {
      var0.addProperty("rev", ++revision);
      PacketDistributor.sendToServer(ShareNetwork.message(var0), new CustomPacketPayload[0]);
   }

   private static void put(String var0, ShareState.Mark var1) {
      JsonObject var2 = ShareNetwork.object(var0);
      var2.add("mark", ShareNetwork.GSON.toJsonTree(var1));
      send(var2);
   }

   private static void delete(UUID var0) {
      JsonObject var1 = ShareNetwork.object("delete");
      var1.addProperty("source", var0.toString());
      send(var1);
   }

   private static void request() {
      JsonObject var0 = ShareNetwork.object("request");
      lastCount = (Integer)SharingMod.HISTORY.get();
      var0.addProperty("count", lastCount);
      send(var0);
   }

   public static void tick() {
      Minecraft var0 = Minecraft.getInstance();
      if (var0.level != null && var0.player != null && channel()) {
         WaypointStore var1 = WaypointStore.get();
         if (var1 != null) {
            if (var0.getConnection() != connection || var1 != store) {
               clear();
               connection = var0.getConnection();
               store = var1;
               key = WorldKey.current();
               loadMemory();
               Map var2 = own();

               for (UUID var4 : memory.known) {
                  if (!var2.containsKey(var4)) {
                     delete(var4);
                  }
               }

               for (ShareState.Mark var10 : var2.values()) {
                  put("put", var10);
               }

               for (Waypoint var11 : raw(store)) {
                  if (var11.kind == Kind.DEATH) {
                     put("importDeath", draft(var11, true));
                  }
               }

               send(ShareNetwork.object("migrationDone"));
               sent.putAll(var2);
               request();
            }

            if (++ticks % 10 == 0) {
               Map var5 = own();

               for (UUID var12 : sent.keySet()) {
                  if (!var5.containsKey(var12)) {
                     delete(var12);
                  }
               }

               for (Entry var13 : var5.entrySet()) {
                  if (!((ShareState.Mark)var13.getValue()).equals(sent.get(var13.getKey()))) {
                     put("put", (ShareState.Mark)var13.getValue());
                  }
               }

               sent.clear();
               sent.putAll(var5);
               if (lastCount != (Integer)SharingMod.HISTORY.get()) {
                  request();
               }
            }
         }
      } else {
         if (connection != null) {
            clear();
         }
      }
   }

   public static void receive(JsonObject var0) {
      if (connection != null && Minecraft.getInstance().getConnection() == connection) {
         String var1 = var0.get("op").getAsString();
         long var2 = var0.get("generation").getAsLong();
         switch (var1) {
            case "begin":
               pendingGeneration = var2;
               pendingAck = var0.get("ack").getAsLong();
               staging = new ArrayList<>();
               break;
            case "row":
               if (staging == null || var2 != pendingGeneration) {
                  return;
               }

               if (staging.size() >= 100000) {
                  staging = null;
                  return;
               }

               ShareState.View var6 = (ShareState.View)ShareNetwork.GSON.fromJson(var0.get("view"), ShareState.View.class);
               if (var6 == null || !ShareState.valid(var6.mark()) || var6.age() < 0 || var6.age() > 1000) {
                  throw new IllegalArgumentException("Invalid server map marker");
               }

               staging.add(var6);
               break;
            case "end":
               if (staging == null || var2 != pendingGeneration) {
                  return;
               }

               views = List.copyOf(staging);
               staging = null;
               ready = true;
               projections.clear();
               if (pendingAck >= revision) {
                  mergeOwn();
               }
         }
      }
   }

   private static void mergeOwn() {
      List var0 = views.stream().filter(var0x -> !var0x.mark().death() && var0x.mark().owner().equals(me())).toList();
      HashSet var1 = new HashSet();

      for (ShareState.View var3 : var0) {
         var1.add(var3.mark().source());
      }

      List var8 = raw(store);
      boolean var9 = var8.removeIf(var1x -> var1x.kind == Kind.MANUAL && memory.known.contains(var1x.id) && !var1.contains(var1x.id));

      for (ShareState.View var5 : var0) {
         ShareState.Mark var6 = var5.mark();
         Waypoint var7 = var8.stream().filter(var1x -> var1x.kind == Kind.MANUAL && var1x.id.equals(var6.source())).findFirst().orElse(null);
         if (var7 == null) {
            var7 = new Waypoint();
            var7.id = var6.source();
            var8.add(var7);
            var9 = true;
         }

         if (!draft(var7, false).equals(var6)) {
            var7.name = var6.name();
            var7.initials = var6.initials();
            var7.x = var6.x();
            var7.y = var6.y();
            var7.z = var6.z();
            var7.dimension = var6.dimension();
            var7.color = var6.color();
            var7.created = var6.created();
            var9 = true;
         }
      }

      memory.known = var1;
      saveMemory();
      sent.clear();
      sent.putAll(own());
      if (var9) {
         store.changed();
      }
   }

   public static List<Waypoint> overlay(WaypointStore var0, List<Waypoint> var1, String var2) {
      if (active() && var0 == store) {
         ArrayList var3 = new ArrayList();

         for (Waypoint var5 : var1) {
            if (var5.kind != Kind.DEATH) {
               var3.add(var5);
            }
         }

         for (ShareState.View var10 : views) {
            ShareState.Mark var6 = var10.mark();
            if ((!var6.death() || Settings.get().deathMarkers && var10.age() <= (Integer)SharingMod.HISTORY.get())
               && (var2 == null || var2.equals(var6.dimension()))
               && (var6.death() || !var6.owner().equals(me()) || !var1.stream().anyMatch(var1x -> var1x.id.equals(var6.source())))) {
               UUID var7 = var6.displayId();
               Waypoint var8 = projections.computeIfAbsent(var7, var3x -> {
                  Waypoint var4 = new Waypoint(var10.label(), var6.x(), var6.y(), var6.z(), var6.dimension(), var6.color());
                  var4.id = var7;
                  var4.initials = var6.death() ? Integer.toString(var10.age()) : var6.initials();
                  var4.kind = var6.death() ? Kind.DEATH : Kind.MANUAL;
                  var4.created = var6.created();
                  return var4;
               });
               var8.visible = !memory.hidden.contains(var7);
               var3.add(var8);
            }
         }

         return var3;
      } else {
         return var1;
      }
   }

   public static boolean shared(Waypoint var0) {
      return active() && projections.containsKey(var0.id);
   }

   public static void visible(Waypoint var0, boolean var1) {
      if (var1) {
         memory.hidden.remove(var0.id);
      } else {
         memory.hidden.add(var0.id);
      }

      var0.visible = var1;
      saveMemory();
   }

   private static Path memoryFile() {
      return WorldKey.folder(key).resolve("maki-share-sync.json");
   }

   private static void loadMemory() {
      try {
         Path var0 = memoryFile();
         if (Files.exists(var0)) {
            memory = (ClientSharing.Memory)ShareNetwork.GSON.fromJson(Files.readString(var0, StandardCharsets.UTF_8), ClientSharing.Memory.class);
            if (memory == null || memory.known == null || memory.hidden == null) {
               throw new IllegalArgumentException("Invalid sharing preferences");
            }
         }
      } catch (Exception var1) {
         memory = new ClientSharing.Memory();
         LoggerFactory.getLogger("MakiMapShare").warn("Could not read sharing preferences; native waypoints were preserved", var1);
      }
   }

   private static void saveMemory() {
      try {
         Path var0 = memoryFile();
         Files.createDirectories(var0.getParent());
         Path var1 = var0.resolveSibling(var0.getFileName() + ".tmp");
         Files.writeString(var1, ShareNetwork.GSON.toJson(memory), StandardCharsets.UTF_8);

         try {
            Files.move(var1, var0, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException var3) {
            Files.move(var1, var0, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception var4) {
         LoggerFactory.getLogger("MakiMapShare").warn("Could not save local sharing preferences", var4);
      }
   }

   public static final class Memory {
      public Set<UUID> known = new HashSet<>();
      public Set<UUID> hidden = new HashSet<>();
   }
}
