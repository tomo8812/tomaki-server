package maki.chunksmithcache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class BackfillGuard {
   private static final Map<String, Set<Long>> ACTIVE = new ConcurrentHashMap<>();

   private BackfillGuard() {
   }

   public static void arm(ServerLevel level, ChunkPos pos) {
      ACTIVE.computeIfAbsent(key(level), ignored -> ConcurrentHashMap.newKeySet()).add(pos.toLong());
   }

   public static boolean active(ServerLevel level, ChunkPos pos) {
      if (level == null || pos == null) {
         return false;
      }
      Set<Long> positions = ACTIVE.get(key(level));
      return positions != null && positions.contains(pos.toLong());
   }

   public static boolean activePoi(Object storage, ChunkPos pos) {
      if (storage == null || pos == null) {
         return false;
      }
      for (ServerLevel level : BackfillController.levels()) {
         if (level.getPoiManager() == storage && active(level, pos)) {
            return true;
         }
      }
      return false;
   }

   public static void clear(ServerLevel level, ChunkPos pos) {
      if (level == null || pos == null) {
         return;
      }
      String key = key(level);
      Set<Long> positions = ACTIVE.get(key);
      if (positions != null) {
         positions.remove(pos.toLong());
         if (positions.isEmpty()) {
            ACTIVE.remove(key, positions);
         }
      }
   }

   private static String key(ServerLevel level) {
      return level.dimension().location().toString();
   }
}
