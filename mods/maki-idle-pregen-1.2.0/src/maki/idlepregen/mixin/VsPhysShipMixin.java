package maki.idlepregen.mixin;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import maki.idlepregen.IdlePregenHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.valkyrienskies.core.impl.game.ships.PhysShipImpl", remap = false)
public abstract class VsPhysShipMixin {
   @Shadow
   @Final
   private ArrayDeque<?> invForces;
   @Shadow
   @Final
   private ArrayDeque<?> invTorques;
   @Shadow
   @Final
   private ArrayDeque<?> rotForces;
   @Shadow
   @Final
   private ArrayDeque<?> rotTorques;
   @Shadow
   @Final
   private ArrayDeque<?> invPosForces;
   @Shadow
   @Final
   private ArrayDeque<?> invPosPositions;

   @Inject(method = "getPhysicsListeners$impl", at = @At("HEAD"), cancellable = true, remap = false)
   private void maki$suppressShipPhysicsListeners(CallbackInfoReturnable<List<?>> cir) {
      if (IdlePregenHooks.isVsIdlePhysicsStep()) {
         cir.setReturnValue(Collections.emptyList());
      }
   }

   @Inject(method = "applyQueuedForces", at = @At("HEAD"), cancellable = true, remap = false)
   private void maki$discardIdleQueuedForces(CallbackInfo ci) {
      if (IdlePregenHooks.isVsIdlePhysicsStep()) {
         this.invForces.clear();
         this.invTorques.clear();
         this.rotForces.clear();
         this.rotTorques.clear();
         this.invPosForces.clear();
         this.invPosPositions.clear();
         ci.cancel();
      }
   }
}
