package maki.chunksmithcache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class BackfillGuard {
   private static final Map<String, Session> ACTIVE = new ConcurrentHashMap<>();

   private BackfillGuard() {
   }

   public static void arm(ServerLevel level, ChunkPos target, Set<Long> suppressed) {
      ACTIVE.put(key(level), new Session(target.toLong(), Set.copyOf(suppressed)));
   }

   public static boolean active(ServerLevel level, ChunkPos pos) {
      if (level == null || pos == null) {
         return false;
      }

      Session session = ACTIVE.get(key(level));
      return session != null && session.target() == pos.toLong();
   }

   public static boolean suppress(ServerLevel level, ChunkPos pos) {
      if (level == null || pos == null) {
         return false;
      }

      Session session = ACTIVE.get(key(level));
      return session != null && session.suppressed().contains(pos.toLong());
   }

   public static boolean activePoi(Object poiManager, ChunkPos pos) {
      if (poiManager != null && pos != null) {
         for (ServerLevel level : BackfillController.levels()) {
            if (level.getPoiManager() == poiManager && suppress(level, pos)) {
               return true;
            }
         }
      }

      return false;
   }

   public static void clear(ServerLevel level, ChunkPos target) {
      String key = key(level);
      Session session = ACTIVE.get(key);
      if (session != null && session.target() == target.toLong()) {
         ACTIVE.remove(key, session);
      }
   }

   private static String key(ServerLevel level) {
      return level.dimension().location().toString();
   }

   private record Session(long target, Set<Long> suppressed) {
   }
}
