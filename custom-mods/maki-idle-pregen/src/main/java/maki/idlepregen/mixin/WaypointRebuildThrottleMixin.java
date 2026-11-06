package maki.idlepregen.mixin;

import maki.idlepregen.MillenaireInitGuard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "org.millenaire.village.WaypointRebuildThrottle", remap = false)
public abstract class WaypointRebuildThrottleMixin {
    @Shadow
    private long lastRequestTick;

    @Shadow
    private int backoff;

    @Unique
    private boolean maki$trackingAttempt;

    @Unique
    private long maki$lastRequestTickBeforeAttempt;

    @Unique
    private int maki$backoffBeforeAttempt;

    @Inject(method = "tryAcquire", at = @At("HEAD"))
    private void maki$snapshotBeforeAcquire(
            long now,
            long minIntervalTicks,
            CallbackInfoReturnable<Boolean> cir) {
        maki$trackingAttempt = MillenaireInitGuard.hasWaypointGraphAttempt();
        if (maki$trackingAttempt) {
            maki$lastRequestTickBeforeAttempt = lastRequestTick;
            maki$backoffBeforeAttempt = backoff;
        }
    }

    @Inject(method = "tryAcquire", at = @At("RETURN"), cancellable = true)
    private void maki$admitAfterAcquire(
            long now,
            long minIntervalTicks,
            CallbackInfoReturnable<Boolean> cir) {
        if (!maki$trackingAttempt) {
            return;
        }

        maki$trackingAttempt = false;
        if (!cir.getReturnValue()) {
            MillenaireInitGuard.finishWaypointGraphAttempt();
            return;
        }

        if (!MillenaireInitGuard.admitWaypointGraphAfterThrottle()) {
            lastRequestTick = maki$lastRequestTickBeforeAttempt;
            backoff = maki$backoffBeforeAttempt;
            cir.setReturnValue(false);
        }
    }
}
