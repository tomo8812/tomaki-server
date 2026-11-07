package maki.chunksmithcache.mixin;

import com.kishku7.chunksmith.platform.World;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import maki.chunksmithcache.PipelineMetrics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.kishku7.chunksmith.GenerationTask", remap = false)
public abstract class GenerationRequestMixin {
   @Shadow
   @Final
   private long maxChunkMillis;
   @Shadow
   @Final
   private AtomicInteger dispatchLimit;

   @Inject(method = "adjustFromChunkLatency(J)V", at = @At("HEAD"), require = 1)
   private void maki$nativeLatency(long elapsed, CallbackInfo ci) {
      PipelineMetrics.time("native-dispatch", TimeUnit.MILLISECONDS.toNanos(Math.max(0L, elapsed)));
      if (elapsed > this.maxChunkMillis) {
         PipelineMetrics.count("native-slow-threshold");
      }
   }

   @Inject(method = "adjustFromChunkLatency(J)V", at = @At("RETURN"), require = 1)
   private void maki$nativeWidth(long elapsed, CallbackInfo ci) {
      PipelineMetrics.gauge("native-width-last", this.dispatchLimit.get());
   }

   @Redirect(
      method = "lambda$run$1",
      at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/platform/World;getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;"),
      require = 2
   )
   private CompletableFuture<Void> maki$measureRequest(World world, int x, int z) {
      long started = System.nanoTime();
      CompletableFuture<Void> future = world.getChunkAtAsync(x, z);
      future.whenComplete((value, error) -> {
         PipelineMetrics.time("adapter-request", System.nanoTime() - started);
         if (error != null) {
            PipelineMetrics.count("adapter-failed");
         }
      });
      return future;
   }
}
