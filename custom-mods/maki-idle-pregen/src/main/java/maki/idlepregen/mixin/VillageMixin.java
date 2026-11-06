package maki.idlepregen.mixin;

import maki.idlepregen.MillenaireInitGuard;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.village.Village;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Village.class, remap = false)
public abstract class VillageMixin {
    @Inject(method = "rebuildWaypointGraphIfStale", at = @At("HEAD"), cancellable = true)
    private void maki$preflightWaypointGraph(
            ServerLevel level,
            long minIntervalTicks,
            CallbackInfoReturnable<Boolean> cir) {
        Village village = (Village)(Object)this;
        if (!MillenaireInitGuard.allowWaypointGraph(level, village)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "rebuildWaypointGraphIfStale",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/millenaire/village/Village;rebuildWaypointGraph(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void maki$consumeWaypointGraphBudget(
            ServerLevel level,
            long minIntervalTicks,
            CallbackInfoReturnable<Boolean> cir) {
        MillenaireInitGuard.consumeWaypointGraphPermit(level);
    }
}
