package org.valkyrienskies.mod.mixin.feature.ai.goal.villagers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1309;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_4123;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_4123.class)
public class MixinSleepInBed {
   @WrapOperation(
      method = "checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"),
      require = 1
   )
   private boolean vs$startConditionsCloserToCenter(
      class_2338 bedPos, class_2374 entityPos, double dist, Operation<Boolean> original, @Local(argsOnly = true) class_3218 level
   ) {
      return (Boolean)original.call(new Object[]{bedPos, vs$entityPosInBedShipSpace(level, bedPos, entityPos), dist});
   }

   @WrapOperation(
      method = "canStillUse(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;J)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"),
      require = 1
   )
   private boolean vs$canStillUseCloserToCenter(
      class_2338 bedPos, class_2374 entityPos, double dist, Operation<Boolean> original, @Local(argsOnly = true) class_3218 level
   ) {
      return (Boolean)original.call(new Object[]{bedPos, vs$entityPosInBedShipSpace(level, bedPos, entityPos), dist});
   }

   @WrapOperation(
      method = "canStillUse(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;J)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getY()D"),
      require = 1
   )
   private double vs$canStillUseEntityY(class_1309 entity, Operation<Double> original, @Local(argsOnly = true) class_3218 level, @Local class_2338 bedPos) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(level, bedPos);
      if (ship == null) {
         return (Double)original.call(new Object[]{entity});
      }

      Vector3dc shipPos = ship.getTransform().getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(entity.method_19538()), new Vector3d());
      return shipPos.y();
   }

   private static class_2374 vs$entityPosInBedShipSpace(class_3218 level, class_2338 bedPos, class_2374 entityPos) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(level, bedPos);
      if (ship == null) {
         return entityPos;
      }

      Vector3dc shipPos = ship.getTransform()
         .getWorldToShip()
         .transformPosition(new Vector3d(entityPos.method_10216(), entityPos.method_10214(), entityPos.method_10215()), new Vector3d());
      return new class_243(shipPos.x(), shipPos.y(), shipPos.z());
   }
}
