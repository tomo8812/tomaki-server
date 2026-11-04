package org.valkyrienskies.mod.mixin.feature.ai.goal;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3486;
import net.minecraft.class_3610;
import net.minecraft.class_5493;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.api.ValkyrienSkies;

@Mixin(class_5493.class)
public class MixinGoalUtil {
   @WrapOperation(
      method = "isSolid",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
      )
   )
   private static class_2680 vs$getBlockStateIsSolid(class_1937 instance, class_2338 blockPos, Operation<class_2680> original) {
      class_2680 originalState = (class_2680)original.call(new Object[]{instance, blockPos});
      if (originalState.method_51367()) {
         return originalState;
      }

      for (Vector3d candidate : ValkyrienSkies.positionToNearbyShips(instance, blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260())) {
         class_2338 candidatePos = class_2338.method_49638(ValkyrienSkies.toMinecraft(candidate));
         class_2680 candidateState = instance.method_8320(candidatePos);
         if (candidateState.method_51367()) {
            return candidateState;
         }
      }

      return originalState;
   }

   @WrapOperation(
      method = "isWater",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
      )
   )
   private static class_3610 vs$getFluidStateIsWater(class_1937 instance, class_2338 blockPos, Operation<class_3610> original) {
      class_3610 originalState = (class_3610)original.call(new Object[]{instance, blockPos});
      if (originalState.method_15767(class_3486.field_15517)) {
         return originalState;
      }

      for (Vector3d candidate : ValkyrienSkies.positionToNearbyShips(instance, blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260())) {
         class_2338 candidatePos = class_2338.method_49638(ValkyrienSkies.toMinecraft(candidate));
         class_3610 candidateState = instance.method_8316(candidatePos);
         if (candidateState.method_15767(class_3486.field_15517)) {
            return candidateState;
         }
      }

      return originalState;
   }
}
