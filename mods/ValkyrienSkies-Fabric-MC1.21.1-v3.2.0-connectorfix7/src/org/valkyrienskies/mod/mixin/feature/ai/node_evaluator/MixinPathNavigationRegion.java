package org.valkyrienskies.mod.mixin.feature.ai.node_evaluator;

import java.util.Iterator;
import net.minecraft.class_1937;
import net.minecraft.class_1950;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2680;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.config.VSGameConfig;

@Mixin(class_1950.class)
public abstract class MixinPathNavigationRegion {
   @Unique
   private static final ThreadLocal<Boolean> vs$inOverlay = ThreadLocal.withInitial(() -> false);

   @Inject(method = "getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("RETURN"), cancellable = true)
   private void vs$overlayShipBlock(class_2338 pos, CallbackInfoReturnable<class_2680> cir) {
      if (VSGameConfig.SERVER.getAiOnShips() && !vs$inOverlay.get()) {
         if (((class_2680)cir.getReturnValue()).method_26215()) {
            vs$inOverlay.set(true);

            try {
               class_1937 level = ((PathNavigationRegionAccessor)this).getLevel();
               Iterator var4 = VSGameUtilsKt.getShipsIntersecting(level, new class_238(pos)).iterator();

               class_2680 shipState;
               do {
                  if (!var4.hasNext()) {
                     return;
                  }

                  Ship ship = (Ship)var4.next();
                  Vector3d shipPos = ship.getTransform()
                     .getWorldToShip()
                     .transformPosition(new Vector3d(pos.method_10263() + 0.5, pos.method_10264() + 0.5, pos.method_10260() + 0.5), new Vector3d());
                  class_2338 shipBlockPos = class_2338.method_49637(shipPos.x, shipPos.y, shipPos.z);
                  shipState = level.method_8320(shipBlockPos);
               } while (shipState.method_26215());

               cir.setReturnValue(shipState);
            } finally {
               vs$inOverlay.set(false);
            }
         }
      }
   }
}
