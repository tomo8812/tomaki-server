package maki.chunksmithfixes.mixin;

import maki.chunksmithfixes.LodRefreshPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.kishku7.chunksmith.lod.ExploredLod", remap = false)
public abstract class ExploredLodMixin {
   @Inject(method = "shouldTake", at = @At("HEAD"), cancellable = true, require = 1)
   private static void maki$refreshDirty(boolean settingOn, boolean lodOn, boolean full, boolean dirty, boolean stored, CallbackInfoReturnable<Boolean> ci) {
      ci.setReturnValue(LodRefreshPolicy.shouldTake(settingOn, lodOn, full, dirty, stored));
   }
}
