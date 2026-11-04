package org.valkyrienskies.mod.mixin.feature.ai.goal.villagers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1646;
import net.minecraft.class_2338;
import net.minecraft.class_4122;
import net.minecraft.class_4208;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4122.class)
public class MixinSetWalkTargetFromBlockMemory {
   @WrapOperation(
      method = "method_47101",
      require = 1,
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/GlobalPos;pos()Lnet/minecraft/core/BlockPos;")
   )
   private static class_2338 onGlobalPos(class_4208 instance, Operation<class_2338> original, @Local(argsOnly = true) class_1646 villager) {
      class_2338 shipyardPos = (class_2338)original.call(new Object[]{instance});
      return class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(villager.method_37908(), shipyardPos));
   }
}
