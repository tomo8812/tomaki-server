package maki.chunksmithcache.mixin;

import java.util.concurrent.CompletableFuture;
import maki.chunksmithcache.BackfillGuard;
import maki.chunksmithcache.CacheManager;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public abstract class ChunkMapSaveMixin {
   @Shadow
   @Final
   private ServerLevel level;

   @Inject(method = "save(Lnet/minecraft/world/level/chunk/ChunkAccess;)Z", at = @At("HEAD"), cancellable = true)
   private void maki$backfillVeto(ChunkAccess var1, CallbackInfoReturnable<Boolean> var2) {
      if (BackfillGuard.suppress(this.level, var1.getPos())) {
         var1.setUnsaved(false);
         var2.setReturnValue(false);
      }
   }

   @Redirect(
      method = "save(Lnet/minecraft/world/level/chunk/ChunkAccess;)Z",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ChunkMap;write(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Ljava/util/concurrent/CompletableFuture;"
      ),
      require = 1
   )
   private CompletableFuture<Void> maki$observeNormalWrite(ChunkMap map, ChunkPos pos, CompoundTag tag, ChunkAccess savingChunk) {
      try {
         CacheManager.prepareNormalWrite(this.level, pos, tag);
      } catch (RuntimeException error) {
         savingChunk.setUnsaved(true);
         throw error;
      }

      CompletableFuture<Void> written = map.write(pos, tag);
      CacheManager.onNormalWrite(this.level, pos, tag, written);
      return written;
   }
}
