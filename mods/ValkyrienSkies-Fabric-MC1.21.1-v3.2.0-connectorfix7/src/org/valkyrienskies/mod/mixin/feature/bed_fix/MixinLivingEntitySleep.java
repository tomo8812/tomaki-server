package org.valkyrienskies.mod.mixin.feature.bed_fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_1309.class)
public abstract class MixinLivingEntitySleep {
   @WrapOperation(method = "setPosToBed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setPos(DDD)V"), require = 1)
   private void vs$setPosToBedOnShip(class_1309 self, double x, double y, double z, Operation<Void> original, @Local(argsOnly = true) class_2338 bedPos) {
      class_1937 level = self.method_37908();
      if (!VSGameUtilsKt.isBlockInShipyard(level, bedPos)) {
         original.call(new Object[]{self, x, y, z});
      } else {
         Ship ship = VSGameUtilsKt.getShipManagingPos(level, bedPos);
         if (ship != null) {
            Vector3dc worldPos = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(x, y, z), new Vector3d());
            original.call(new Object[]{self, worldPos.x(), worldPos.y(), worldPos.z()});
         }
      }
   }

   @WrapOperation(method = "method_18404", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setPos(DDD)V"), require = 1)
   private void vs$worldConvertWakeStandup(class_1309 instance, double x, double y, double z, Operation<Void> original) {
      if (!instance.method_37908().method_8608() && !(instance instanceof class_1657)) {
         class_1937 level = instance.method_37908();
         class_2338 standPos = class_2338.method_49637(x, y, z);
         if (!VSGameUtilsKt.isBlockInShipyard(level, standPos)) {
            original.call(new Object[]{instance, x, y, z});
         } else {
            Ship ship = VSGameUtilsKt.getShipManagingPos(level, standPos);
            if (ship != null) {
               Vector3dc worldPos = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(x, y, z), new Vector3d());
               original.call(new Object[]{instance, worldPos.x(), worldPos.y(), worldPos.z()});
            }
         }
      } else {
         original.call(new Object[]{instance, x, y, z});
      }
   }

   @Inject(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true, require = 1)
   private void vs$holdSleepingMobStill(class_243 input, CallbackInfo ci) {
      class_1309 self = (class_1309)this;
      if (!(self instanceof class_1657)) {
         if (self.method_6113()) {
            self.method_18799(class_243.field_1353);
            ci.cancel();
         }
      }
   }
}
