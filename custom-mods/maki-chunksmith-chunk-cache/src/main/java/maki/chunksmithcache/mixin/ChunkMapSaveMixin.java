package maki.chunksmithcache.mixin;

import maki.chunksmithcache.BackfillGuard;
import maki.chunksmithcache.CacheManager;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public abstract class ChunkMapSaveMixin {
   @Shadow
   @Final
   private ServerLevel level;

   @Inject(method = "save(Lnet/minecraft/world/level/chunk/ChunkAccess;)Z", at = @At("HEAD"), cancellable = true)
   private void maki$backfillVeto(ChunkAccess var1, CallbackInfoReturnable<Boolean> var2) {
      if (BackfillGuard.suppressAndRemember(this.level, var1.getPos())) {
         var1.setUnsaved(false);
         var2.setReturnValue(false);
      }
   }

   @Inject(method = "save(Lnet/minecraft/world/level/chunk/ChunkAccess;)Z", at = @At("RETURN"))
   private void maki$removePromotedCache(ChunkAccess var1, CallbackInfoReturnable<Boolean> var2) {
      CacheManager.onNormalSave(this.level, var1, Boolean.TRUE.equals(var2.getReturnValue()));
   }
}
