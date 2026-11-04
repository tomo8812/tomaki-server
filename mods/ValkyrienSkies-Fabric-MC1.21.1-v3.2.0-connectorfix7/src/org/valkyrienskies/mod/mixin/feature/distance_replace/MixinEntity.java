package org.valkyrienskies.mod.mixin.feature.distance_replace;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_1297.class)
public class MixinEntity {
   @Inject(method = "distanceTo", at = @At("HEAD"), cancellable = true)
   private void preDistanceTo(class_1297 entity, CallbackInfoReturnable<Float> cir) {
      cir.setReturnValue(class_3532.method_15355((float)class_1297.class.cast(this).method_5858(entity)));
   }

   @Inject(method = "distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D", at = @At("HEAD"), cancellable = true)
   private void preDistanceToSqr(class_243 vec, CallbackInfoReturnable<Double> cir) {
      cir.setReturnValue(VSGameUtilsKt.squaredDistanceToInclShips(class_1297.class.cast(this), vec.field_1352, vec.field_1351, vec.field_1350));
   }

   @Inject(method = "distanceToSqr(DDD)D", at = @At("HEAD"), cancellable = true)
   private void preDistanceToSqr(double x, double y, double z, CallbackInfoReturnable<Double> cir) {
      cir.setReturnValue(VSGameUtilsKt.squaredDistanceToInclShips(class_1297.class.cast(this), x, y, z));
   }
}
