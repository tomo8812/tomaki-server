package maki.idlepregen.mixin;

import maki.idlepregen.MillenaireInitTelemetry;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import org.millenaire.entity.MillWalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MillWalkNodeEvaluator.class, remap = false)
public abstract class MillWalkNodeEvaluatorMixin {
    @Inject(method = "getPathType", at = @At("HEAD"))
    private void maki$countPathTypeLookup(
            PathfindingContext context,
            int x,
            int y,
            int z,
            CallbackInfoReturnable<PathType> cir) {
        MillenaireInitTelemetry.recordPathTypeLookup();
    }
}
