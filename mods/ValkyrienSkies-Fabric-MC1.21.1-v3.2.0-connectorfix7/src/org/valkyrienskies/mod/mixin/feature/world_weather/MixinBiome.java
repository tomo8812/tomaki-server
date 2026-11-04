package org.valkyrienskies.mod.mixin.feature.world_weather;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1937;
import net.minecraft.class_1944;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_4538;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.CompatUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_1959.class)
public class MixinBiome {
   @WrapOperation(
      method = {"shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", "shouldSnow"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z")
   )
   boolean useTemperatureAtWorldPos(class_1959 instance, class_2338 blockPos, Operation<Boolean> original, @Local(argsOnly = true) class_4538 levelReader) {
      return levelReader instanceof class_1937 level
         ? (Boolean)original.call(new Object[]{instance, class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(level, blockPos))})
         : (Boolean)original.call(new Object[]{instance, blockPos});
   }

   @WrapOperation(
      method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/LevelReader;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"
      )
   )
   int useCombinedLighting(class_4538 instance, class_1944 lightLayer, class_2338 pos, Operation<Integer> original) {
      return instance instanceof class_1937 level
         ? CompatUtil.INSTANCE.getCompoundBrightness(level, lightLayer, pos, null)
         : (Integer)original.call(new Object[]{instance, lightLayer, pos});
   }
}
