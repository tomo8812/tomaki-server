package org.valkyrienskies.mod.mixin.feature.world_weather;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_2386;
import net.minecraft.class_2488;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.CompatUtil;

@Mixin({class_2386.class, class_2488.class})
public class MixinMeltingBlocks {
   @WrapOperation(
      method = "randomTick",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerLevel;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"
      )
   )
   int useCombinedBrightness(class_3218 instance, class_1944 lightLayer, class_2338 pos, Operation<Integer> original) {
      return CompatUtil.INSTANCE.getCompoundBrightness(instance, lightLayer, pos, null);
   }
}
