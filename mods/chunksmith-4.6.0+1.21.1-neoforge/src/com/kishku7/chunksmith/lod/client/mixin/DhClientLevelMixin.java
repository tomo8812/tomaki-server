package com.kishku7.chunksmith.lod.client.mixin;

import com.kishku7.chunksmith.lod.client.render.DhPushGuard;
import com.seibel.distanthorizons.core.level.DhClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DhClientLevel.class, remap = false)
public class DhClientLevelMixin {
   @Inject(method = "shouldProcessChunkUpdate", at = @At("HEAD"), cancellable = true, remap = false)
   private void chunksmith$alwaysAcceptOurPushes(CallbackInfoReturnable<Boolean> cir) {
      if (DhPushGuard.isPushing()) {
         DhPushGuard.forced();
         cir.setReturnValue(Boolean.TRUE);
      }
   }
}
