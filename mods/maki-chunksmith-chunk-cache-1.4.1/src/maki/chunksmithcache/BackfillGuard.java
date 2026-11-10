package maki.chunksmithcache;

import java.util.Set;
import maki.chunksmithcache.mixin.ChunkMapLifecycleAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class BackfillGuard {
   private static final BackfillScopes ACTIVE = new BackfillScopes();

   private BackfillGuard() {
   }

   public static void arm(ServerLevel level, ChunkPos target, Set<Long> suppressed) {
      ACTIVE.arm(key(level), target.toLong(), suppressed);
   }

   public static boolean active(ServerLevel level, ChunkPos pos) {
      return level != null && pos != null ? ACTIVE.get(key(level), pos.toLong()) != null : false;
   }

   public static boolean suppress(ServerLevel level, ChunkPos pos) {
      if (level != null && pos != null) {
         long packed = pos.toLong();
         boolean suppress = false;

         for (BackfillScopes.Session session : ACTIVE.sessions(key(level))) {
            if (session.suppressed().contains(packed)) {
               if (playerNear(level, pos)) {
                  session.suppressed().remove(packed);
               } else {
                  suppress = true;
               }
            }
         }

         return suppress;
      } else {
         return false;
      }
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

   public static int releasePlayerOwnedDependencies(ServerLevel level, ChunkPos target) {
      if (level != null && target != null) {
         BackfillScopes.Session session = ACTIVE.get(key(level), target.toLong());
         if (session != null && session.target() == target.toLong()) {
            int released = 0;

            for (long packed : session.suppressed()) {
               if (packed != session.target()) {
                  ChunkPos pos = new ChunkPos(packed);
                  if (playerNear(level, pos) && level.getChunkSource().hasChunk(pos.x, pos.z) && session.suppressed().remove(packed)) {
                     released++;
                  }
               }
            }

            return released;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public static boolean scopeAvailable(ServerLevel level, Set<Long> scope) {
      if (level != null && scope != null && !scope.isEmpty()) {
         try {
            ChunkMapLifecycleAccessor chunks = (ChunkMapLifecycleAccessor)level.getChunkSource().chunkMap;

            for (BackfillScopes.Session session : ACTIVE.sessions(key(level))) {
               for (long packed : scope) {
                  if (session.suppressed().contains(packed)) {
                     return false;
                  }
               }
            }

            for (long packed : scope) {
               if (chunks.maki$getUpdatingChunkMap().containsKey(packed)
                  || chunks.maki$getVisibleChunkMap().containsKey(packed)
                  || chunks.maki$getPendingUnloads().containsKey(packed)) {
                  return false;
               }
            }

            return true;
         } catch (Throwable ignored) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean scopeDrained(ServerLevel level, ChunkPos target) {
      if (level != null && target != null) {
         BackfillScopes.Session session = ACTIVE.get(key(level), target.toLong());
         if (session != null && session.target() == target.toLong()) {
            try {
               ChunkMapLifecycleAccessor chunks = (ChunkMapLifecycleAccessor)level.getChunkSource().chunkMap;

               for (long packed : session.suppressed()) {
                  if (chunks.maki$getUpdatingChunkMap().containsKey(packed)
                     || chunks.maki$getVisibleChunkMap().containsKey(packed)
                     || chunks.maki$getPendingUnloads().containsKey(packed)) {
                     return false;
                  }
               }

               return true;
            } catch (Throwable ignored) {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public static void clearLevel(ServerLevel level) {
      for (BackfillScopes.Session session : ACTIVE.sessions(key(level))) {
         ACTIVE.clear(key(level), session.target());
      }
   }

   public static void clear(ServerLevel level, ChunkPos target) {
      if (level != null && target != null) {
         ACTIVE.clear(key(level), target.toLong());
      }
   }

   public static void releasePlayerTarget(ServerLevel level, ChunkPos target) {
      BackfillScopes.Session session = ACTIVE.get(key(level), target.toLong());
      if (session != null) {
         session.suppressed().remove(target.toLong());
      }
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
