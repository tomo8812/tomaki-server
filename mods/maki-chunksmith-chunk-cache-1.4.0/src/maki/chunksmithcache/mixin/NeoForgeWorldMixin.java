package maki.chunksmithcache.mixin;

import com.kishku7.chunksmith.ducks.MinecraftServerExtension;
import com.kishku7.chunksmith.mixin.ServerChunkCacheMixin;
import com.kishku7.chunksmith.platform.NeoForgeWorld;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import maki.chunksmithcache.CacheManager;
import maki.chunksmithcache.LodCommitTracker;
import maki.chunksmithcache.PipelineMetrics;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.kishku7.chunksmith.platform.NeoForgeWorld", remap = false)
public abstract class NeoForgeWorldMixin {
   @Shadow
   @Final
   private ServerLevel world;

   @Inject(method = "isChunkGenerated(II)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true, require = 1)
   private void maki$cachedFullCountsAsGenerated(int x, int z, CallbackInfoReturnable<CompletableFuture<Boolean>> cir) {
      if (CacheManager.chunksmithGeneratedHint(this.world, new ChunkPos(x, z))) {
         cir.setReturnValue(CompletableFuture.completedFuture(true));
      }
   }

   @Redirect(
      method = "lambda$getChunkAtAsync$8",
      at = @At(
         value = "INVOKE",
         target = "Lcom/kishku7/chunksmith/lod/LodSupport;offer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;)V"
      ),
      require = 1
   )
   private void maki$offerAndCapture(ServerLevel level, LevelChunk chunk) {
      long started = System.nanoTime();

      CompletableFuture<Boolean> lodWritten;
      try {
         lodWritten = LodCommitTracker.offer(level, chunk);
      } finally {
         PipelineMetrics.time("lod-offer", System.nanoTime() - started);
      }

      CacheManager.captureFromChunksmith(level, chunk, lodWritten);
   }

   @Redirect(
      method = "getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;",
      at = @At(
         value = "INVOKE",
         target = "Lcom/kishku7/chunksmith/mixin/ServerChunkCacheMixin;invokeGetChunkFutureMainThread(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Ljava/util/concurrent/CompletableFuture;"
      ),
      require = 1
   )
   private CompletableFuture<ChunkResult<ChunkAccess>> maki$measureFull(ServerChunkCacheMixin source, int x, int z, ChunkStatus status, boolean create) {
      long started = System.nanoTime();
      CompletableFuture<ChunkResult<ChunkAccess>> future = source.invokeGetChunkFutureMainThread(x, z, status, create);
      future.whenComplete((value, error) -> PipelineMetrics.time("full-future", System.nanoTime() - started));
      return future;
   }

   @Redirect(
      method = "getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;",
      at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/platform/NeoForgeWorld;ticketSafePoint()Ljava/util/concurrent/Executor;"),
      require = 2
   )
   private Executor maki$measureSafePoint(NeoForgeWorld ignored) {
      return task -> {
         long queued = System.nanoTime();
         ((MinecraftServerExtension)this.world.getServer()).chunksmith$atTicketSafePoint(() -> {
            PipelineMetrics.time("ticket-safe-point-wait", System.nanoTime() - queued);
            long started = System.nanoTime();

            try {
               task.run();
            } finally {
               PipelineMetrics.time("ticket-safe-point-work", System.nanoTime() - started);
            }
         });
      };
   }
}
