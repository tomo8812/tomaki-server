package org.valkyrienskies.mod.mixin.feature.fluid_camera_fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3610;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4184.class)
public abstract class MixinCamera {
   @Unique
   private boolean isShipWater = false;

   @Shadow
   public abstract class_243 method_19326();

   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/BlockGetter;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
      ),
      method = "getFluidInCamera"
   )
   private class_3610 getFluidInCamera(class_1922 instance, class_2338 blockPos, Operation<class_3610> getFluidState) {
      class_3610[] fluidState = new class_3610[]{(class_3610)getFluidState.call(new Object[]{instance, blockPos})};
      this.isShipWater = false;
      if (fluidState[0].method_15769() && instance instanceof class_1937 level) {
         double origX = this.method_19326().field_1352;
         double origY = this.method_19326().field_1351;
         double origZ = this.method_19326().field_1350;
         VSGameUtilsKt.transformToNearbyShipsAndWorld(level, origX, origY, origZ, 1.0, (x, y, z) -> {
            fluidState[0] = instance.method_8320(class_2338.method_49637(x, y, z)).method_26227();
            if (!fluidState[0].method_15769()) {
               this.isShipWater = true;
            }
         });
      }

      return fluidState[0];
   }

   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/material/FluidState;getHeight(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
      ),
      method = "getFluidInCamera"
   )
   private float fluidHeightOverride(class_3610 instance, class_1922 arg, class_2338 arg2, Operation<Float> getHeight) {
      return !instance.method_15769() && this.isShipWater && instance.method_15771() ? 1.0F : (Float)getHeight.call(new Object[]{instance, arg, arg2});
   }
}
