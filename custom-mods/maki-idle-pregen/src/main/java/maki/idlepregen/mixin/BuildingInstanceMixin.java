package maki.idlepregen.mixin;

import maki.idlepregen.MillenaireInitGuard;
import maki.idlepregen.MillenaireInitTelemetry;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BuildingInstance.class, remap = false)
public abstract class BuildingInstanceMixin {
    @Inject(method = "backfillBedPositions", at = @At("HEAD"), cancellable = true)
    private void maki$guardBedBackfill(ServerLevel level, BuildingPlan plan, CallbackInfo ci) {
        BuildingInstance building = (BuildingInstance)(Object)this;
        if (!MillenaireInitGuard.allowBedBackfill(level, building, plan)) {
            ci.cancel();
            return;
        }
        MillenaireInitTelemetry.beginBedBackfill(building, plan);
    }

    @Inject(method = "backfillBedPositions", at = @At("RETURN"))
    private void maki$measureBedBackfill(ServerLevel level, BuildingPlan plan, CallbackInfo ci) {
        MillenaireInitTelemetry.endBedBackfill();
    }
}
