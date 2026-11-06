package maki.chunksmithcache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import maki.chunksmithcache.mixin.ChunkMapLifecycleAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class BackfillGuard {
   private static final Map<String, Map<Long, Session>> ACTIVE = new ConcurrentHashMap<>();

   private BackfillGuard() {
   }

   public static void arm(ServerLevel level, ChunkPos target, Set<Long> suppressed) {
      Set<Long> owned = ConcurrentHashMap.newKeySet();
      owned.addAll(suppressed);
      ACTIVE.computeIfAbsent(key(level),ignored -> new ConcurrentHashMap<>()).put(target.toLong(), new Session(target.toLong(), owned));
   }

   public static boolean active(ServerLevel level, ChunkPos pos) {
      if (level == null || pos == null) {
         return false;
      }

      return sessions(level).containsKey(pos.toLong());
   }

   public static boolean suppress(ServerLevel level, ChunkPos pos) {
      if (level == null || pos == null) {
         return false;
      }

      long packed=pos.toLong();
      boolean suppress=false;
      for(Session session:sessions(level).values()) {
         if(!session.suppressed().contains(packed))continue;
         if(playerNear(level,pos)) {
            // Permanent handoff also applies after the player leaves.
            session.suppressed().remove(packed);
         } else suppress=true;
      }
      return suppress;
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
      if (level == null || target == null) {
         return 0;
      }

      Session session = sessions(level).get(target.toLong());
      if (session == null || session.target() != target.toLong()) {
         return 0;
      }

      int released = 0;
      for (long packed : session.suppressed()) {
         if (packed == session.target()) {
            continue;
         }

         ChunkPos pos = new ChunkPos(packed);
         if (playerNear(level, pos) && level.getChunkSource().hasChunk(pos.x, pos.z) && session.suppressed().remove(packed)) {
            released++;
         }
      }
      return released;
   }

   public static boolean scopeAvailable(ServerLevel level, Set<Long> scope) {
      if (level == null || scope == null || scope.isEmpty()) {
         return false;
      }

      try {
         ChunkMapLifecycleAccessor chunks = (ChunkMapLifecycleAccessor)(Object)level.getChunkSource().chunkMap;
         for(Session session:sessions(level).values()) {
            for(long packed:scope)if(session.suppressed().contains(packed))return false;
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
   }

   public static boolean scopeDrained(ServerLevel level, ChunkPos target) {
      if (level == null || target == null) {
         return true;
      }

      Session session = sessions(level).get(target.toLong());
      if (session == null || session.target() != target.toLong()) {
         return true;
      }

      try {
         ChunkMapLifecycleAccessor chunks = (ChunkMapLifecycleAccessor)(Object)level.getChunkSource().chunkMap;
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
   }

   public static void clear(ServerLevel level, ChunkPos target) {
      if(level!=null&&target!=null)sessions(level).remove(target.toLong());
   }

   public static void releasePlayerTarget(ServerLevel level, ChunkPos target) {
      Session session=sessions(level).get(target.toLong());
      if(session!=null)session.suppressed().remove(target.toLong());
   }

   private static Map<Long,Session> sessions(ServerLevel level) {
      return ACTIVE.getOrDefault(key(level), java.util.Collections.emptyMap());
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

   private record Session(long target, Set<Long> suppressed) {
   }
}
