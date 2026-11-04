package org.valkyrienskies.mod.mixin.feature.fluid_escaping_ship_config;

import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_3609;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import org.joml.primitives.AABBic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.config.VSGameConfig;

@Mixin(class_3609.class)
public class MixinFlowingFluid {
   @Inject(at = @At("HEAD"), method = "canSpreadTo", cancellable = true)
   private void beforeCanSpreadTo(
      class_1922 level,
      class_2338 fromPos,
      class_2680 fromBlockState,
      class_2350 direction,
      class_2338 toPos,
      class_2680 toBlockState,
      class_3610 toFluidState,
      class_3611 fluid,
      CallbackInfoReturnable<Boolean> cir
   ) {
      if (VSGameConfig.SERVER.getPreventFluidEscapingShip() && level instanceof class_1937) {
         Ship ship = VSGameUtilsKt.getShipManagingPos((class_1937)level, toPos);
         if (ship != null && ship.getShipAABB() != null) {
            AABBic a = ship.getShipAABB();
            int x = toPos.method_10263();
            int y = toPos.method_10264();
            int z = toPos.method_10260();
            if (x < a.minX() || y < a.minY() || z < a.minZ() || x >= a.maxX() || y >= a.maxY() || z >= a.maxZ()) {
               cir.setReturnValue(false);
            }
         }
      }
   }
}
