package dev.diorama;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;

public final class TrackedPlayers {
   private static final Map<UUID, TrackedPlayers.Tracked> PLAYERS = new HashMap<>();
   private static final Map<UUID, RemotePlayer> STAND_INS = new HashMap<>();
   private static @Nullable ClientLevel standInLevel;

   private TrackedPlayers() {
   }

   static void update(CompoundTag tag) {
      UUID id = uuid(tag);
      if (id != null) {
         boolean removed = tag.getBoolean("r");
         if (removed) {
            PLAYERS.remove(id);
            STAND_INS.remove(id);
         } else {
            PLAYERS.put(id, new TrackedPlayers.Tracked(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"), tag.getString("d")));
         }
      }
   }

   private static @Nullable UUID uuid(CompoundTag tag) {
      int[] ints = tag.getIntArray("i");
      long[] longs = tag.getLongArray("i");
      if (ints.length == 4) {
         return UUIDUtil.uuidFromIntArray(ints);
      } else {
         return longs.length == 2 ? new UUID(longs[0], longs[1]) : null;
      }
   }

   public static void clear() {
      PLAYERS.clear();
      STAND_INS.clear();
      standInLevel = null;
   }

   public static List<RemotePlayer> unloaded(ClientLevel level) {
      Minecraft mc = Minecraft.getInstance();
      ClientPacketListener connection = mc.getConnection();
      if (!PLAYERS.isEmpty() && connection != null && mc.player != null) {
         if (standInLevel != level) {
            STAND_INS.clear();
            standInLevel = level;
         }

         String dimension = level.dimension().location().toString();
         List<RemotePlayer> out = new ArrayList<>();

         for (Entry<UUID, TrackedPlayers.Tracked> e : PLAYERS.entrySet()) {
            UUID id = e.getKey();
            TrackedPlayers.Tracked tracked = e.getValue();
            if (!id.equals(mc.player.getUUID()) && tracked.dimension().equals(dimension) && level.getPlayerByUUID(id) == null) {
               PlayerInfo info = connection.getPlayerInfo(id);
               if (info != null && (info.getGameMode() != GameType.SPECTATOR || mc.player.isSpectator())) {
                  RemotePlayer standIn = STAND_INS.computeIfAbsent(id, uuid -> new RemotePlayer(level, info.getProfile()));
                  standIn.moveTo(tracked.x(), tracked.y(), tracked.z());
                  out.add(standIn);
               }
            }
         }

         return out;
      } else {
         return List.of();
      }
   }

   private record Tracked(double x, double y, double z, String dimension) {
   }
}
