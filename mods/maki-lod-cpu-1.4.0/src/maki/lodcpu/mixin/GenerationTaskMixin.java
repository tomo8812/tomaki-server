package maki.lodcpu.mixin;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.iterator.ChunkIterator;
import com.kishku7.chunksmith.util.ChunkResidency;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import maki.lodcpu.CpuGovernor;
import maki.lodcpu.PlayerCenter;
import maki.lodcpu.ResourceBudgetApi;
import maki.lodcpu.TaskOwnership;
import maki.lodcpu.WorkTelemetry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.kishku7.chunksmith.GenerationTask", remap = false)
public abstract class GenerationTaskMixin {
   @Shadow
   @Final
   private AtomicInteger dispatchLimit;
   @Shadow
   @Final
   private AtomicInteger inFlight;
   @Shadow
   @Final
   private Selection selection;
   @Shadow
   private volatile boolean writeQueueStalled;
   @Shadow
   private volatile boolean chunkResidencyStalled;
   @Shadow
   private volatile boolean heapStalled;
   @Shadow
   private boolean stopped;
   @Unique
   private final WorkTelemetry maki$metrics = new WorkTelemetry();

   @Unique
   private boolean maki$owned() {
      return TaskOwnership.isOwned((GenerationTask)this);
   }

   @Inject(method = "run()V", at = @At("HEAD"), require = 1)
   private void maki$taskStarted(CallbackInfo ci) {
      if (this.maki$owned()) {
         PlayerCenter.nativeTaskStarted((GenerationTask)this);
      }
   }

   @Inject(method = "stop(Z)V", at = @At("HEAD"), require = 1)
   private void maki$taskStopping(boolean cancelled, CallbackInfo ci) {
      if (this.maki$owned()) {
         PlayerCenter.nativeTaskStopping((GenerationTask)this, cancelled);
      }
   }

   @Redirect(method = "run()V", at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/iterator/ChunkIterator;next()Ljava/lang/Object;"), require = 1)
   private Object maki$sharedBackfillBarrier(ChunkIterator iterator) {
      Object next = iterator.next();
      if (!this.maki$owned()) {
         return next;
      }

      while (!this.stopped && ResourceBudgetApi.blockFollowDispatch()) {
         ResourceBudgetApi.noteFollowInFlight(this.inFlight.get());
         LockSupport.parkNanos(1000000L);
      }

      return next;
   }

   @Unique
   private void maki$clamp() {
      int upper = CpuGovernor.cap();

      int old;
      do {
         old = this.dispatchLimit.get();
         if (old <= upper) {
            return;
         }
      } while (!this.dispatchLimit.compareAndSet(old, upper));
   }

   @Inject(method = "adjustFromTickHealth()V", at = @At("HEAD"), require = 1)
   private void maki$beforeHealth(CallbackInfo ci) {
      if (this.maki$owned()) {
         CpuGovernor.markActive();
         this.maki$clamp();
         if (this.selection.world().getName().equals("minecraft:overworld") && this.maki$metrics.due()) {
            this.maki$metrics
               .observe(
                  this.dispatchLimit.get(),
                  this.inFlight.get(),
                  this.selection.world().getQueuedChunkWrites(),
                  this.writeQueueStalled,
                  this.chunkResidencyStalled,
                  this.heapStalled,
                  ChunkResidency.addedChunks()
               );
            CpuGovernor.observe(this.maki$metrics);
         }
      }
   }

   @Inject(method = "adjustFromTickHealth()V", at = @At("RETURN"), require = 1)
   private void maki$afterHealth(CallbackInfo ci) {
      if (this.maki$owned()) {
         this.maki$clamp();
      }
   }

   @Inject(method = "rampUp(Z)V", at = @At("HEAD"), cancellable = true, require = 1)
   private void maki$preventRamp(boolean burst, CallbackInfo ci) {
      if (this.maki$owned()) {
         this.maki$clamp();
         if (this.dispatchLimit.get() >= CpuGovernor.cap()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "adjustFromChunkLatency(J)V", at = @At("HEAD"), require = 1)
   private void maki$completion(long elapsed, CallbackInfo ci) {
      if (this.maki$owned()) {
         this.maki$metrics.completed(elapsed);
      }
   }
}
