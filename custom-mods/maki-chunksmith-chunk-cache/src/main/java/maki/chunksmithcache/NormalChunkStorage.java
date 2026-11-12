package maki.chunksmithcache;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

final class NormalChunkStorage {
   private static final java.util.concurrent.Semaphore SCANS=new java.util.concurrent.Semaphore(64);
   enum State {
      ABSENT,
      PROTO,
      FULL,
      UNKNOWN
   }

   private NormalChunkStorage() {
   }

   /** Call on a storage executor: header reads are blocking; scanner completion never joins. */
   static java.util.concurrent.CompletableFuture<State> inspectAsync(ServerLevel level, ChunkPos pos) {
      // Native scanner observes IOWorker.pendingWrites too. A missing region
      // header alone is not proof of absence while a normal FULL save is pending.
      return inspectStatus(level,pos);
   }

   private static java.util.concurrent.CompletableFuture<State> inspectStatus(ServerLevel level,ChunkPos pos) {
      if (!SCANS.tryAcquire()) return java.util.concurrent.CompletableFuture.completedFuture(State.UNKNOWN);
      try {
         FieldSelector statusSelector = new FieldSelector(StringTag.TYPE, "Status");
         CollectFields collector = new CollectFields(new FieldSelector[]{statusSelector});
         return level.getChunkSource().chunkScanner().scanChunk(pos, collector)
             .handle((ignored, error) -> {
               SCANS.release();
               return error != null ? State.UNKNOWN : collector.getResult() == null ? State.ABSENT
                  : collector.getResult() instanceof CompoundTag tag ? fromTag(tag) : State.UNKNOWN;
            });
      } catch (Throwable unavailable) {
         SCANS.release();
         return java.util.concurrent.CompletableFuture.completedFuture(State.UNKNOWN);
      }
   }

   static State fromTag(CompoundTag tag) {
      if (tag == null || !tag.contains("Status")) {
         return State.UNKNOWN;
      }

      String status = tag.getString("Status");
      if ("minecraft:full".equals(status) || "full".equals(status)) {
         return State.FULL;
      }

      return status.isEmpty() ? State.UNKNOWN : State.PROTO;
   }

}
