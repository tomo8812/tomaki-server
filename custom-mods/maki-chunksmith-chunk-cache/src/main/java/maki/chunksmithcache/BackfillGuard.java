package maki.chunksmithcache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class BackfillGuard {
   private static final Map<String, Long> ACTIVE_TARGET = new ConcurrentHashMap<>();
   private static final Map<String, Set<Long>> ACTIVE_SCOPE = new ConcurrentHashMap<>();
   private static final Map<String, Set<Long>> QUARANTINE = new ConcurrentHashMap<>();

   private BackfillGuard() {
   }

   public static void arm(ServerLevel level, ChunkPos target, Set<Long> suppressed) {
      String key = key(level);
      ACTIVE_TARGET.put(key, target.toLong());
      ACTIVE_SCOPE.put(key, Set.copyOf(suppressed));
   }

   public static boolean active(ServerLevel level, ChunkPos pos) {
      if (level == null || pos == null) {
         return false;
      }

      Long target = ACTIVE_TARGET.get(key(level));
      return target != null && target == pos.toLong();
   }

   public static boolean suppress(ServerLevel level, ChunkPos pos) {
      return suppress(level, pos, false);
   }

   public static boolean suppressAndRemember(ServerLevel level, ChunkPos pos) {
      return suppress(level, pos, true);
   }

   public static boolean activePoi(Object poiManager, ChunkPos pos) {
      if (poiManager != null && pos != null) {
         for (ServerLevel level : BackfillController.levels()) {
            if (level.getPoiManager() == poiManager && suppressAndRemember(level, pos)) {
               return true;
            }
         }
      }

      return false;
   }

   public static void clear(ServerLevel level, ChunkPos target) {
      String key = key(level);
      Long active = ACTIVE_TARGET.get(key);
      if (active != null && active == target.toLong()) {
         ACTIVE_TARGET.remove(key, active);
         ACTIVE_SCOPE.remove(key);
         Set<Long> quarantine = QUARANTINE.get(key);
         if (quarantine != null) {
            quarantine.remove(target.toLong());
            if (quarantine.isEmpty()) {
               QUARANTINE.remove(key, quarantine);
            }
         }
      }
   }

   private static boolean suppress(ServerLevel level, ChunkPos pos, boolean remember) {
      if (level == null || pos == null) {
         return false;
      }

      String key = key(level);
      long packed = pos.toLong();
      Set<Long> scope = ACTIVE_SCOPE.get(key);
      Set<Long> quarantine = QUARANTINE.get(key);
      boolean owned = scope != null && scope.contains(packed) || quarantine != null && quarantine.contains(packed);
      if (!owned) {
         return false;
      }

      if (playerNear(level, pos)) {
         if (quarantine != null) {
            quarantine.remove(packed);
            if (quarantine.isEmpty()) {
               QUARANTINE.remove(key, quarantine);
            }
         }
         return false;
      }

      if (remember) {
         QUARANTINE.computeIfAbsent(key, ignored -> ConcurrentHashMap.newKeySet()).add(packed);
      }
      return true;
   }

   private static boolean playerNear(ServerLevel level, ChunkPos pos) {
      int radius = level.getServer().getPlayerList().getViewDistance() + 1;
      for (ServerPlayer player : level.players()) {
         ChunkPos at = player.chunkPosition();
         if (Math.abs(at.x - pos.x) <= radius && Math.abs(at.z - pos.z) <= radius) {
            return true;
         }
      }
      return false;
   }

   private static String key(ServerLevel level) {
      return level.dimension().location().toString();
   }
}
