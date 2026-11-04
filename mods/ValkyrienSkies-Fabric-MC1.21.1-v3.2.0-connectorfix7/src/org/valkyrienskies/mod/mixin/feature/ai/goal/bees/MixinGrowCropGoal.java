package org.valkyrienskies.mod.mixin.feature.ai.goal.bees;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3481;
import net.minecraft.class_4466.class_4474;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4474.class)
public class MixinGrowCropGoal {
   @WrapOperation(
      method = "tick",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
      )
   )
   private class_2680 onTick(class_1937 instance, class_2338 blockPos, Operation<class_2680> original) {
      for (Vector3d candidate : VSGameUtilsKt.transformToNearbyShipsAndWorld(
         instance, blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260(), 1.5
      )) {
         class_2680 blockState = instance.method_8320(class_2338.method_49637(candidate.x, candidate.y, candidate.z));
         if (blockState.method_26164(class_3481.field_20342)) {
            return blockState;
         }
      }

      return (class_2680)original.call(new Object[]{instance, blockPos});
   }
}
