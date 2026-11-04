package org.valkyrienskies.mod.mixin.feature.ai.goal;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1308;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_4112;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4112.class)
public class MixinMoveToTargetSink {
   @WrapOperation(method = "reachedTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;distManhattan(Lnet/minecraft/core/Vec3i;)I"))
   private int onDistManhattan(class_2338 instance, class_2382 vec3i, Operation<Integer> original, @Local(argsOnly = true) class_1308 mob) {
      return (Integer)original.call(new Object[]{class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(mob.method_37908(), instance)), vec3i});
   }
}
