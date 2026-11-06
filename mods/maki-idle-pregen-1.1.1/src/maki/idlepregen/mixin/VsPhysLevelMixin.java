package maki.idlepregen.mixin;

import maki.idlepregen.IdlePregenHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.valkyrienskies.core.impl.shadow.FU", remap = false)
public abstract class VsPhysLevelMixin {
   @Inject(method = "physTick", at = @At("HEAD"), cancellable = true, remap = false)
   private void maki$suppressIdlePhysicsEvent(double delta, CallbackInfo ci) {
      if (IdlePregenHooks.isVsIdlePhysicsStep()) {
         ci.cancel();
      }
   }
}
