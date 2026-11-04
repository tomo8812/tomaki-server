package org.valkyrienskies.mod.mixin.feature.ai.goal.bees;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_4466;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4466.class)
public abstract class MixinBee extends class_1297 {
   public MixinBee(class_1299<?> entityType, class_1937 level) {
      super(entityType, level);
   }

   @WrapOperation(method = "closerThan", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z"))
   private boolean onCloserThan(class_2338 instance, class_2382 vec3i, double v, Operation<Boolean> original) {
      return (Boolean)original.call(new Object[]{class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(this.method_37908(), instance)), vec3i, v});
   }
}
