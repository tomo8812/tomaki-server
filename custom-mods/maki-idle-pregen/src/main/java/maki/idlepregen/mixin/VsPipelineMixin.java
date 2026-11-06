package maki.idlepregen.mixin;

import maki.idlepregen.IdlePregenHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.valkyrienskies.core.impl.shadow.FQ", remap = false)
public abstract class VsPipelineMixin {
    @Inject(method = "postTickGame", at = @At("HEAD"), cancellable = true, remap = false)
    private void maki$skipPhysicsFrameWhileIdle(CallbackInfo ci) {
        if (IdlePregenHooks.suppressVsGameFrame()) {
            ci.cancel();
        }
    }
}
