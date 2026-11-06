package maki.idlepregen.mixin;

import maki.idlepregen.IdlePregenHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.valkyrienskies.core.impl.shadow.FQ", remap = false)
public abstract class VsPipelineMixin {
    @ModifyVariable(
            method = "a(D)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0,
            remap = false
    )
    private double maki$freezePhysicsDelta(double deltaSeconds) {
        return IdlePregenHooks.adjustVsPhysicsDelta(this, deltaSeconds);
    }

    @Inject(method = "a(D)V", at = @At("RETURN"), remap = false)
    private void maki$completeIdlePhysicsStep(CallbackInfo ci) {
        IdlePregenHooks.completeVsPhysicsStep(this);
    }

    @Inject(method = "postTickGame", at = @At("TAIL"), remap = false)
    private void maki$syncIdleGameFrame(CallbackInfo ci) {
        IdlePregenHooks.afterVsGameFrame(this);
    }
}
