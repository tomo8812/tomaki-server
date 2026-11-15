package maki.chunksmithfixes.mixin;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodRegionStore;
import com.kishku7.chunksmith.lod.net.CsLodGrowth;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import maki.chunksmithfixes.ExploredLodRefresh;
import maki.chunksmithfixes.LodUpdateQueue;
import maki.chunksmithfixes.LodWriteEvents;
import maki.chunksmithfixes.LodWriteOrder;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.kishku7.chunksmith.lod.CsLodStoreSink", remap = false)
public abstract class StoreSinkMixin {
   @Unique
   private Path maki$root;
   @Unique
   private final LodWriteOrder maki$order = new LodWriteOrder();
   @Shadow
   @Final
   private CsLodRegionStore store;
   @Shadow
   @Final
   private AtomicLong written;
   @Shadow
   @Final
   private AtomicLong bytes;

   @Inject(method = "<init>", at = @At("RETURN"), require = 1)
   private void maki$root(Path root, int capacity, CallbackInfo ci) {
      this.maki$root = root;
   }

   @Inject(method = "offer(Ljava/lang/Object;)Z", at = @At("HEAD"), require = 1)
   private void maki$submitted(Object value, CallbackInfoReturnable<Boolean> ci) {
      if (value instanceof CsLodChunk record) {
         Runnable acknowledge = LodUpdateQueue.snapshotAcknowledgement(record);
         CompletableFuture<Void> saved = ExploredLodRefresh.submitted(this.maki$root, record);
         if (acknowledge != null) {
            saved.thenRun(acknowledge);
         }

         this.maki$order.submitted(record);
         LodWriteEvents.submitted(record);
      }
   }

   @Inject(method = "persist", at = @At("HEAD"), cancellable = true, require = 1)
   private void maki$persist(CsLodChunk record, CallbackInfo ci) {
      ci.cancel();
      long started = System.nanoTime();
      LodWriteEvents.Outcome outcome = LodWriteEvents.Outcome.FAILED;

      try {
         LodWriteOrder.Result result = this.maki$order.write(record, () -> this.store.write(record));
         outcome = result.written() ? LodWriteEvents.Outcome.WRITTEN : LodWriteEvents.Outcome.SUPERSEDED;
         if (result.written()) {
            CsLodGrowth.noteWrite(System.currentTimeMillis());
            this.written.incrementAndGet();
            this.bytes.addAndGet(result.bytes());
         }
      } catch (IOException error) {
         LoggerFactory.getLogger("Chunksmith")
            .warn("Chunksmith: failed to write LOD for chunk {},{}: {}", new Object[]{record.getChunkX(), record.getChunkZ(), error.toString()});
      } finally {
         ExploredLodRefresh.completed(record, outcome);
         LodWriteEvents.completed(record, System.nanoTime() - started, outcome);
      }
   }
}
