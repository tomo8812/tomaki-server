package org.valkyrienskies.mod.mixin.feature.ai.goal.bees;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_4466;
import net.minecraft.class_4466.class_4470;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4470.class)
public class MixinEnterHiveGoal {
   @Shadow
   @Final
   class_4466 field_20367;

   @WrapOperation(
      method = "canBeeUse",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z")
   )
   private boolean onCloserToCenterThan(class_2338 instance, class_2374 position, double v, Operation<Boolean> original) {
      return (Boolean)original.call(
         new Object[]{class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(this.field_20367.method_37908(), instance)), position, v}
      );
   }
}
