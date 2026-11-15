package maki.chunksmithcontroller.mixin;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.iterator.ChunkIterator;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.lod.LodSinks;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.util.ChunkCoordinate;
import com.kishku7.chunksmith.util.ChunkResidency;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import maki.chunksmithcontroller.CpuGovernor;
import maki.chunksmithcontroller.DispatchCap;
import maki.chunksmithcontroller.PlayerCenter;
import maki.chunksmithcontroller.PregenControlHooks;
import maki.chunksmithcontroller.RequestContext;
import maki.chunksmithcontroller.ResourceBudgetApi;
import maki.chunksmithcontroller.TaskOwnership;
import maki.chunksmithcontroller.WorkTelemetry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
   @Shadow
   @Final
   private boolean ioThrottleEnabled;
   @Shadow
   @Final
   private int maxWorkingCount;
   @Shadow
   @Final
   private long maxLodQueue;
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

   @Inject(method = "run()V", at = @At("RETURN"), require = 1)
   private void maki$taskFinished(CallbackInfo ci) {
      if (this.maki$owned()) {
         PregenControlHooks.taskFinished((GenerationTask)this);
      }
   }

   @Inject(method = "stop(Z)V", at = @At("HEAD"), require = 1)
   private void maki$taskStopping(boolean cancelled, CallbackInfo ci) {
      if (this.maki$owned()) {
         PlayerCenter.nativeTaskStopping((GenerationTask)this, cancelled);
      }
   }

   @Redirect(method = "run()V", at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/iterator/ChunkIterator;next()Ljava/lang/Object;"), require = 1)
   private Object maki$candidateAdmission(ChunkIterator iterator) {
      GenerationTask task = (GenerationTask)this;
      if (this.maki$owned()) {
         while (!this.stopped && ResourceBudgetApi.blockFollowAdmission(task)) {
            LockSupport.parkNanos(1000000L);
         }
      }

      Object next = iterator.next();
      if (!this.maki$owned()) {
         return next;
      }

      if (next instanceof ChunkCoordinate coordinate) {
         String dimension = this.selection.world().getName();

         while (!this.stopped && !PregenControlHooks.reserveCandidate(dimension, coordinate.x(), coordinate.z(), task)) {
            LockSupport.parkNanos(1000000L);
         }
      }

      return next;
   }

   @Redirect(
      method = "run()V",
      at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/platform/World;isChunkGenerated(II)Ljava/util/concurrent/CompletableFuture;"),
      require = 1
   )
   private CompletableFuture<Boolean> maki$observeNativeCandidate(World world, int x, int z) {
      CompletableFuture<Boolean> result = world.isChunkGenerated(x, z);
      if (!this.maki$owned()) {
         return result;
      }

      GenerationTask owner = (GenerationTask)this;
      String dimension = this.selection.world().getName();
      return result.whenComplete((generated, error) -> {
         if (error != null) {
            PregenControlHooks.candidateFailed(dimension, x, z, owner);
         } else {
            PregenControlHooks.classify(dimension, x, z, owner, Boolean.TRUE.equals(generated));
         }
      });
   }

   @Inject(method = "lambda$run$1", at = @At("HEAD"), cancellable = true, require = 1)
   private void maki$admitDispatch(
      CsLodPresenceIndex index,
      ChunkCoordinate coordinate,
      boolean lodOnly,
      boolean horizon,
      boolean[] persisted,
      Boolean generated,
      CallbackInfoReturnable<CompletionStage<Void>> ci
   ) {
      if (this.maki$owned() && (!Boolean.TRUE.equals(generated) || index != null && !index.hasLod(coordinate.x(), coordinate.z()))) {
         if (!PregenControlHooks.beginActual(this.selection.world().getName(), coordinate.x(), coordinate.z(), (GenerationTask)this)) {
            PregenControlHooks.candidateFailed(this.selection.world().getName(), coordinate.x(), coordinate.z(), (GenerationTask)this);
            ci.setReturnValue(CompletableFuture.failedFuture(new IllegalStateException("Dispatch admission closed before native LOD mark")));
         }
      }
   }

   @Redirect(
      method = "lambda$run$1",
      at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/platform/World;getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;"),
      require = 2
   )
   private CompletableFuture<Void> maki$observeNativeRequest(World world, int x, int z) {
      GenerationTask owner = (GenerationTask)this;
      if (!this.maki$owned()) {
         return world.getChunkAtAsync(x, z);
      }

      String dimension = this.selection.world().getName();
      String token = ResourceBudgetApi.attemptToken(dimension, x, z, owner);
      ResourceBudgetApi.stageStarted(token, "FULL");
      long started = System.nanoTime();

      try {
         return RequestContext.with(new RequestContext.Request(token, dimension, x, z), () -> world.getChunkAtAsync(x, z))
            .whenComplete((value, error) -> ResourceBudgetApi.stageFinished(token, "FULL", System.nanoTime() - started, error == null));
      } catch (Throwable error) {
         ResourceBudgetApi.stageFinished(token, "FULL", System.nanoTime() - started, false);
         throw error;
      }
   }

   @Inject(method = "update(IIZZ)V", at = @At("HEAD"), require = 1)
   private void maki$coordinateCompleted(int x, int z, boolean loaded, boolean persisted, CallbackInfo ci) {
      GenerationTask owner = (GenerationTask)this;
      if (this.maki$owned()) {
         PregenControlHooks.nativeCoordinateFinished(this.selection.world().getName(), x, z, owner);
      }
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

   @Inject(method = "adjustFromLodQueue()V", at = @At("HEAD"), require = 1)
   private void maki$beforeDispatch(CallbackInfo ci) {
      if (this.maki$owned()) {
         CpuGovernor.markActive();
         this.maki$clamp();
         this.dispatchLimit
            .set(
               DispatchCap.apply(
                  this.dispatchLimit.get(),
                  CpuGovernor.cap(),
                  this.maxWorkingCount,
                  this.ioThrottleEnabled,
                  this.heapStalled || this.chunkResidencyStalled || this.writeQueueStalled,
                  LodSinks.get().queueDepth(),
                  this.maxLodQueue
               )
            );
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
