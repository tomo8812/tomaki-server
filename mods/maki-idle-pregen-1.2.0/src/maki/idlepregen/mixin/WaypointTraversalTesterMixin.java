package maki.idlepregen.mixin;

import maki.idlepregen.MillenaireInitTelemetry;
import net.minecraft.core.BlockPos;
import org.millenaire.village.WaypointTraversalTester;
import org.millenaire.village.WaypointTraversalTester.Result;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WaypointTraversalTester.class, remap = false)
public abstract class WaypointTraversalTesterMixin {
   @Inject(method = "findPath", at = @At("HEAD"))
   private void maki$countTraversalSearch(BlockPos from, BlockPos to, CallbackInfoReturnable<Result> cir) {
      MillenaireInitTelemetry.recordTraversalSearch();
   }
}
