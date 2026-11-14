package maki.idlepregen.mixin;

import maki.idlepregen.IdlePregenApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "org.millenaire.entity.MillVillager", remap = false)
public abstract class MillVillagerMixin {
   @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true, require = 1)
   private void maki$pauseVillagerActivity(CallbackInfo ci) {
      if (IdlePregenApi.isActive()) {
         ci.cancel();
      }
   }
}
