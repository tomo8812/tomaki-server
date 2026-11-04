package net.caffeinemc.mods.lithium.mixin.util.chunk_status_tracking;

import java.util.concurrent.Executor;
import net.caffeinemc.mods.lithium.common.world.chunk.ChunkStatusTracker;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ChunkHolder.class, priority = 1010)
public abstract class ChunkHolderMixin extends GenerationChunkHolder {
   public ChunkHolderMixin(ChunkPos chunkPos) {
      super(chunkPos);
   }

   @Inject(
      method = "updateFutures(Lnet/minecraft/server/level/ChunkMap;Ljava/util/concurrent/Executor;)V",
      locals = LocalCapture.CAPTURE_FAILHARD,
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/FullChunkStatus;isOrAfter(Lnet/minecraft/server/level/FullChunkStatus;)Z", ordinal = 6),
      require = 0
   )
   private void trackUpdate(ChunkMap chunkMap, Executor executor, CallbackInfo ci, FullChunkStatus prevStatus, FullChunkStatus status) {
      ServerLevel serverLevel = chunkMap.level;
      boolean loaded = status.isOrAfter(FullChunkStatus.FULL);
      boolean wasLoaded = prevStatus.isOrAfter(FullChunkStatus.FULL);
      if (!loaded && wasLoaded) {
         ChunkStatusTracker.onChunkInaccessible(serverLevel, this.pos);
      } else if (!wasLoaded && this.getChunkIfPresentUnchecked(ChunkStatus.FULL) instanceof LevelChunk chunk) {
         ChunkStatusTracker.onChunkAccessible(serverLevel, chunk);
      }
   }
}
