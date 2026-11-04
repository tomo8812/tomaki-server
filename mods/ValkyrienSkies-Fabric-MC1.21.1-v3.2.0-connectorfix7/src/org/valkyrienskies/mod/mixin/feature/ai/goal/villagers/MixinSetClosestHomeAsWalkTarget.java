package org.valkyrienskies.mod.mixin.feature.ai.goal.villagers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1314;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_4290;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4290.class)
public class MixinSetClosestHomeAsWalkTarget {
   @WrapOperation(method = "method_47054", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;distSqr(Lnet/minecraft/core/Vec3i;)D"))
   private static double onDistSqr(class_2338 instance, class_2382 vec3i, Operation<Double> original, @Local(argsOnly = true) class_1314 livingEntity) {
      return (Double)original.call(new Object[]{class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(livingEntity.method_37908(), instance)), vec3i});
   }
}
