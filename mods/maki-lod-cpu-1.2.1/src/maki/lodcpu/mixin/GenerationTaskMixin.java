package maki.lodcpu.mixin;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.util.ChunkResidency;
import java.util.concurrent.atomic.AtomicInteger;
import maki.lodcpu.CpuGovernor;
import maki.lodcpu.WorkTelemetry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
   @Unique
   private final WorkTelemetry maki$metrics = new WorkTelemetry();

   @Unique
   private void maki$clamp() {
      int var1 = CpuGovernor.cap();

      int var2;
      do {
         var2 = this.dispatchLimit.get();
         if (var2 <= var1) {
            return;
         }
      } while (!this.dispatchLimit.compareAndSet(var2, var1));
   }

   @Inject(method = "adjustFromTickHealth()V", at = @At("HEAD"), require = 1)
   private void maki$beforeHealth(CallbackInfo var1) {
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

   @Inject(method = "adjustFromTickHealth()V", at = @At("RETURN"), require = 1)
   private void maki$afterHealth(CallbackInfo var1) {
      this.maki$clamp();
   }

   @Inject(method = "rampUp(Z)V", at = @At("HEAD"), cancellable = true, require = 1)
   private void maki$preventRamp(boolean var1, CallbackInfo var2) {
      this.maki$clamp();
      if (this.dispatchLimit.get() >= CpuGovernor.cap()) {
         var2.cancel();
      }
   }

   @Inject(method = "adjustFromChunkLatency(J)V", at = @At("HEAD"), require = 1)
   private void maki$completion(long var1, CallbackInfo var3) {
      this.maki$metrics.completed(var1);
   }
}
