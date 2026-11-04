package org.valkyrienskies.mod.mixin.feature.ai.node_evaluator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_11;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1408;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.config.VSGameConfig;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_1408.class)
public class MixinPathNavigation {
   @Shadow
   @Final
   protected class_1937 field_6677;
   @Shadow
   @Final
   protected class_1308 field_6684;
   @Unique
   private ShipTransform vs$buildTransform = null;
   @Unique
   private Long vs$buildShipId = null;

   @Inject(method = "getGroundY(Lnet/minecraft/world/phys/Vec3;)D", at = @At("HEAD"), cancellable = true)
   private void vs$bypassGroundSnapForShipMobs(class_243 vec3, CallbackInfoReturnable<Double> cir) {
      if (VSGameConfig.SERVER.getAiOnShips()) {
         if (this.field_6684 instanceof IEntityDraggingInformationProvider dragProvider && dragProvider.getDraggingInformation().isEntityBeingDraggedByAShip()) {
            cir.setReturnValue(vec3.field_1351);
         }
      }
   }

   @WrapOperation(
      method = "moveTo(DDDD)Z",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;createPath(DDDI)Lnet/minecraft/world/level/pathfinder/Path;"
      )
   )
   private class_11 onMoveToCreatePath(class_1408 instance, double d, double e, double f, int i, Operation<class_11> original) {
      class_243 transformedPos = VSGameUtilsKt.toWorldCoordinates(this.field_6677, new class_243(d, e, f));
      return (class_11)original.call(new Object[]{instance, transformedPos.field_1352, transformedPos.field_1351, transformedPos.field_1350, i});
   }

   @WrapOperation(
      method = "isStableDestination",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
      )
   )
   private class_2680 vs$getBlockStateIsNotStable(class_1937 instance, class_2338 blockPos, Operation<class_2680> original) {
      class_2680 originalState = (class_2680)original.call(new Object[]{instance, blockPos});
      if (originalState.method_26216(instance, blockPos)) {
         return originalState;
      }

      class_2338 above = blockPos.method_10084();

      for (Ship ship : VSGameUtilsKt.getShipsIntersecting(instance, new class_238(above))) {
         Vector3d candidate = ship.getTransform()
            .getWorldToShip()
            .transformPosition(new Vector3d(above.method_10263(), above.method_10264(), above.method_10260()), new Vector3d());
         Vector3dc upVector = ship.getTransform()
            .getShipToWorld()
            .transformDirection(VectorConversionsMCKt.toJOMLD(class_2350.field_11036.method_10163()))
            .normalize();
         class_2350 closestDirection = class_2350.method_10142(upVector.x(), upVector.y(), upVector.z()).method_10153();
         class_2338 candidatePos = class_2338.method_49637(candidate.x, candidate.y, candidate.z).method_10093(closestDirection);
         class_2680 candidateState = (class_2680)original.call(new Object[]{instance, candidatePos});
         if (candidateState.method_26216(instance, candidatePos)) {
            return candidateState;
         }
      }

      return originalState;
   }

   @Inject(method = "moveTo(Lnet/minecraft/world/level/pathfinder/Path;D)Z", at = @At("HEAD"))
   private void vs$captureBuildTransform(class_11 path, double speed, CallbackInfoReturnable<Boolean> cir) {
      Long shipId = this.vs$shipDraggingMob();
      if (shipId == null) {
         this.vs$buildShipId = null;
         this.vs$buildTransform = null;
      } else {
         Ship ship = VSGameUtilsKt.getAllShips(this.field_6677).getById(shipId);
         if (ship == null) {
            this.vs$buildShipId = null;
            this.vs$buildTransform = null;
         } else {
            this.vs$buildTransform = ship.getTransform();
            this.vs$buildShipId = shipId;
         }
      }
   }

   @WrapOperation(
      method = "tick",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/pathfinder/Path;getNextEntityPos(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private class_243 vs$reanchorSteer(class_11 instance, class_1297 entity, Operation<class_243> original) {
      class_243 worldNode = (class_243)original.call(new Object[]{instance, entity});
      return !this.vs$shipPathStillActive() ? worldNode : this.vs$reanchor(worldNode);
   }

   @WrapOperation(method = "followThePath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getX()D"))
   private double vs$reanchorReachedX(class_1308 mobInstance, Operation<Double> original) {
      double world = (Double)original.call(new Object[]{mobInstance});
      return !this.vs$shipPathStillActive() ? world : this.vs$mobInBuildFrame().field_1352;
   }

   @WrapOperation(method = "followThePath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getY()D"))
   private double vs$reanchorReachedY(class_1308 mobInstance, Operation<Double> original) {
      double world = (Double)original.call(new Object[]{mobInstance});
      return !this.vs$shipPathStillActive() ? world : this.vs$mobInBuildFrame().field_1351;
   }

   @WrapOperation(method = "followThePath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getZ()D"))
   private double vs$reanchorReachedZ(class_1308 mobInstance, Operation<Double> original) {
      double world = (Double)original.call(new Object[]{mobInstance});
      return !this.vs$shipPathStillActive() ? world : this.vs$mobInBuildFrame().field_1350;
   }

   @Unique
   private Long vs$shipDraggingMob() {
      if (!VSGameConfig.SERVER.getAiOnShips()) {
         return null;
      } else {
         return this.field_6684 instanceof IEntityDraggingInformationProvider p && p.getDraggingInformation().isEntityBeingDraggedByAShip()
            ? p.getDraggingInformation().getLastShipStoodOn()
            : null;
      }
   }

   @Unique
   private boolean vs$shipPathStillActive() {
      if (this.vs$buildShipId != null && this.vs$buildTransform != null) {
         Long now = this.vs$shipDraggingMob();
         return now != null && now.equals(this.vs$buildShipId);
      } else {
         return false;
      }
   }

   @Unique
   private class_243 vs$reanchor(class_243 worldNode) {
      Ship shipNow = VSGameUtilsKt.getAllShips(this.field_6677).getById(this.vs$buildShipId);
      if (shipNow == null) {
         return worldNode;
      }

      Matrix4dc worldToShipBuild = this.vs$buildTransform.getWorldToShip();
      Matrix4dc shipToWorldNow = shipNow.getTransform().getShipToWorld();
      Vector3d local = worldToShipBuild.transformPosition(new Vector3d(worldNode.field_1352, worldNode.field_1351, worldNode.field_1350), new Vector3d());
      Vector3d live = shipToWorldNow.transformPosition(local, new Vector3d());
      return new class_243(live.x, live.y, live.z);
   }

   @Unique
   private class_243 vs$mobInBuildFrame() {
      Ship shipNow = VSGameUtilsKt.getAllShips(this.field_6677).getById(this.vs$buildShipId);
      if (shipNow == null) {
         return new class_243(this.field_6684.method_23317(), this.field_6684.method_23318(), this.field_6684.method_23321());
      }

      Matrix4dc worldToShipNow = shipNow.getTransform().getWorldToShip();
      Matrix4dc shipToWorldBuild = this.vs$buildTransform.getShipToWorld();
      Vector3d local = worldToShipNow.transformPosition(
         new Vector3d(this.field_6684.method_23317(), this.field_6684.method_23318(), this.field_6684.method_23321()), new Vector3d()
      );
      Vector3d build = shipToWorldBuild.transformPosition(local, new Vector3d());
      return new class_243(build.x, build.y, build.z);
   }
}
