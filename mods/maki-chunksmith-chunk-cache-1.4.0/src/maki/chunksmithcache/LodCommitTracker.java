package maki.chunksmithcache;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.lod.LodSupport;
import java.util.IdentityHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public final class LodCommitTracker {
   private static final IdentityHashMap<CsLodChunk, CompletableFuture<Boolean>> PENDING = new IdentityHashMap<>();
   private static final ThreadLocal<LodCommitTracker.Request> CURRENT = new ThreadLocal<>();

   private LodCommitTracker() {
   }

   public static CompletableFuture<Boolean> offer(ServerLevel level, LevelChunk chunk) {
      CompletableFuture<Boolean> result = new CompletableFuture<>();
      LodCommitTracker.Request previous = CURRENT.get();
      CURRENT.set(new LodCommitTracker.Request(chunk.getPos().x, chunk.getPos().z, result));

      try {
         LodSupport.offer(level, chunk);
      } catch (Throwable error) {
         result.completeExceptionally(error);
      } finally {
         if (previous == null) {
            CURRENT.remove();
         } else {
            CURRENT.set(previous);
         }
      }

      boolean registered;
      synchronized (PENDING) {
         registered = PENDING.containsValue(result);
      }

      if (!registered && !result.isDone()) {
         result.complete(false);
      }

      result.orTimeout(30L, TimeUnit.SECONDS).whenComplete((ok, error) -> {
         synchronized (PENDING) {
            PENDING.values().removeIf(value -> value == result);
         }

         if (error != null || !Boolean.TRUE.equals(ok)) {
            try {
               CsLodPresenceIndex index = LodSupport.presenceIndexFor(level.getServer(), level.dimension().location().toString());
               if (index != null) {
                  index.invalidate();
               }
            } catch (Throwable var6) {
            }
         }
      });
      return result;
   }

   public static void offered(Object value) {
      LodCommitTracker.Request request = CURRENT.get();
      if (request != null && value instanceof CsLodChunk record && record.getChunkX() == request.x && record.getChunkZ() == request.z) {
         track(record, request.future);
      }
   }

   static void track(CsLodChunk record, CompletableFuture<Boolean> future) {
      synchronized (PENDING) {
         PENDING.put(record, future);
      }
   }

   public static void written(CsLodChunk record, Throwable error) {
      CompletableFuture<Boolean> result;
      synchronized (PENDING) {
         result = PENDING.remove(record);
      }

      if (result != null) {
         if (error == null) {
            result.complete(true);
         } else {
            result.completeExceptionally(error);
         }
      }
   }

   private record Request(int x, int z, CompletableFuture<Boolean> future) {
   }
}
