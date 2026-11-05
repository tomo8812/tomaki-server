package maki.chunksmithcache.mixin;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import maki.chunksmithcache.CacheManager;
import maki.chunksmithcache.PromotionLedger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public abstract class ChunkMapReadMixin {
   @Shadow
   @Final
   private ServerLevel level;

   @Inject(method = "readChunk(Lnet/minecraft/world/level/ChunkPos;)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"), cancellable = true)
   private void maki$readCacheOnMiss(ChunkPos var1, CallbackInfoReturnable<CompletableFuture<Optional<CompoundTag>>> var2) {
      CompletableFuture<Optional<CompoundTag>> var3 = (CompletableFuture<Optional<CompoundTag>>)var2.getReturnValue();
      if (var3 != null) {
         var2.setReturnValue(
            var3.thenCompose(var2x -> var2x.isPresent() ? CompletableFuture.completedFuture(var2x) : CacheManager.loadIfPresent(this.level, var1))
         );
      }
   }

   @Inject(method = "scheduleChunkLoad(Lnet/minecraft/world/level/ChunkPos;)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"), cancellable = true)
   private void maki$promoteLoadedCache(ChunkPos var1, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> var2) {
      CompletableFuture<ChunkAccess> var3 = (CompletableFuture<ChunkAccess>)var2.getReturnValue();
      if (var3 != null) {
         var2.setReturnValue(var3.<ChunkAccess>thenApply(var2x -> {
            if (var2x != null && PromotionLedger.consume(this.level, var1)) {
               var2x.setUnsaved(true);
            }

            return (ChunkAccess)var2x;
         }).whenComplete((var2x, var3x) -> {
            if (var3x != null) {
               PromotionLedger.discard(this.level, var1);
            }
         }));
      }
   }
}
