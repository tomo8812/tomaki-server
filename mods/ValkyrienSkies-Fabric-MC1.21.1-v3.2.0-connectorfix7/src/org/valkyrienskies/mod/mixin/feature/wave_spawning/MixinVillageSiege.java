package org.valkyrienskies.mod.mixin.feature.wave_spawning;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1419;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_1419.class)
public class MixinVillageSiege {
   @WrapOperation(
      method = "tryToSetupSiege",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/ai/village/VillageSiege;findRandomSpawnPos(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private class_243 onFindRandomSpawnPos(class_1419 instance, class_3218 k, class_2338 l, Operation<class_243> original) {
      class_2338 transformedCenter = class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(k, l));
      class_243 result = (class_243)original.call(new Object[]{instance, k, transformedCenter});
      return result != null ? result : (class_243)original.call(new Object[]{instance, k, l});
   }

   @WrapOperation(
      method = "trySpawn",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/ai/village/VillageSiege;findRandomSpawnPos(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private class_243 onFindRandomSpawnPos2(class_1419 instance, class_3218 k, class_2338 l, Operation<class_243> original) {
      class_2338 transformedCenter = class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(k, l));
      class_243 result = (class_243)original.call(new Object[]{instance, k, transformedCenter});
      return result != null ? result : (class_243)original.call(new Object[]{instance, k, l});
   }
}
