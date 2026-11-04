package org.valkyrienskies.mod.mixin.feature.ai.goal;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1314;
import net.minecraft.class_1367;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_1367.class)
public class MixinMoveToBlockGoal {
   @Shadow
   @Final
   protected class_1314 field_6516;

   @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"))
   private boolean onCloserToCenterThan(class_2338 instance, class_2374 position, double v, Operation<Boolean> original) {
      return (Boolean)original.call(
         new Object[]{class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(this.field_6516.method_37908(), instance)), position, v}
      );
   }
}
