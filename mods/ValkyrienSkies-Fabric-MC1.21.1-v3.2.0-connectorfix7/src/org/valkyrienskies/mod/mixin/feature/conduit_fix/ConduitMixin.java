package org.valkyrienskies.mod.mixin.feature.conduit_fix;

import java.util.List;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2597;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_2597.class)
public class ConduitMixin extends class_2586 {
   public ConduitMixin(class_2591<?> blockEntityType, class_2338 blockPos, class_2680 blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   @Redirect(
      method = "applyEffects",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z"),
      require = 0
   )
   private static boolean closerThan(class_2338 instance, class_2382 vec3i, double distance, class_1937 level, class_2338 blockPos, List<class_2338> list) {
      double retValue = VSGameUtilsKt.squaredDistanceBetweenInclShips(
         level, instance.method_10263(), instance.method_10264(), instance.method_10260(), vec3i.method_10263(), vec3i.method_10264(), vec3i.method_10260()
      );
      return retValue < distance * distance;
   }

   @Redirect(
      method = "updateDestroyTarget",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z"),
      require = 0
   )
   private static boolean closerThan2(
      class_2338 instance,
      class_2382 vec3i,
      double distance,
      class_1937 level,
      class_2338 blockPos,
      class_2680 blockState,
      List<class_2338> list,
      class_2597 conduitBlockEntity
   ) {
      return closerThan(instance, vec3i, distance, level, blockPos, list);
   }
}
