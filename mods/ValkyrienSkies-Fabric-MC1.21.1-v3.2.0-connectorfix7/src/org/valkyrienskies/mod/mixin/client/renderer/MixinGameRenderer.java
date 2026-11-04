package org.valkyrienskies.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_4184;
import net.minecraft.class_5498;
import net.minecraft.class_638;
import net.minecraft.class_757;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.internal.world.VsiClientShipWorld;
import org.valkyrienskies.mod.client.IVSCamera;
import org.valkyrienskies.mod.client.ShipCameraZoom;
import org.valkyrienskies.mod.client.ShipMountPerspective;
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;
import org.valkyrienskies.mod.common.util.EntityDragger;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;
import org.valkyrienskies.mod.mixinducks.client.MinecraftDuck;

@Mixin(class_757.class)
public abstract class MixinGameRenderer {
   @Shadow
   @Final
   private class_310 field_4015;
   @Shadow
   @Final
   private class_4184 field_18765;

   @Shadow
   protected abstract double method_3196(class_4184 var1, float var2, boolean var3);

   @Shadow
   public abstract Matrix4f method_22973(double var1);

   @Unique
   private static class_239 entityRaycastNoTransform(class_1297 entity, double maxDistance, float tickDelta, boolean includeFluids) {
      class_243 vec3d = entity.method_5836(tickDelta);
      class_243 vec3d2 = entity.method_5828(tickDelta);
      class_243 vec3d3 = vec3d.method_1031(vec3d2.field_1352 * maxDistance, vec3d2.field_1351 * maxDistance, vec3d2.field_1350 * maxDistance);
      return RaycastUtilsKt.clipIncludeShips(
         entity.method_37908(),
         new class_3959(vec3d, vec3d3, class_3960.field_17559, includeFluids ? class_242.field_1347 : class_242.field_1348, entity),
         false
      );
   }

   @WrapOperation(
      method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;")
   )
   public class_239 modifyCrosshairTargetBlocks(class_1297 receiver, double maxDistance, float tickDelta, boolean includeFluids, Operation<class_239> pick) {
      class_239 original = entityRaycastNoTransform(receiver, maxDistance, tickDelta, includeFluids);
      ((MinecraftDuck)this.field_4015).vs$setOriginalCrosshairTarget(original);
      return (class_239)pick.call(new Object[]{receiver, maxDistance, tickDelta, includeFluids});
   }

   @WrapOperation(
      method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D")
   )
   public double correctDistanceChecks(class_243 instance, class_243 vec3, Operation<Double> original) {
      return VSGameUtilsKt.squaredDistanceBetweenInclShips(this.field_4015.field_1687, instance, vec3, original);
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void preRender(class_9779 deltaTracker, boolean bl, CallbackInfo ci) {
      class_638 clientWorld = this.field_4015.field_1687;
      if (clientWorld != null) {
         VsiClientShipWorld shipWorld = IShipObjectWorldClientProvider.class.cast(this.field_4015).getShipObjectWorld();
         if (shipWorld == null) {
            return;
         }

         float partialTick = deltaTracker.method_60637(true);
         shipWorld.updateRenderTransforms(partialTick);

         for (class_1297 entity : clientWorld.method_18112()) {
            if (EntityDragger.isDraggable(entity)) {
               Vector3dc entityShouldBeHere = null;
               ShipMountedToData shipMountedToData = VSGameUtilsKt.getShipMountedToData(entity, partialTick);
               if (shipMountedToData != null) {
                  ClientShip shipMountedTo = (ClientShip)shipMountedToData.getShipMountedTo();
                  Vector3dc passengerPos = shipMountedToData.getMountPosInShip();
                  entityShouldBeHere = shipMountedTo.getRenderTransform().getShipToWorld().transformPosition(passengerPos, new Vector3d());
                  entity.method_5814(entityShouldBeHere.x(), entityShouldBeHere.y(), entityShouldBeHere.z());
                  entity.field_6014 = entityShouldBeHere.x();
                  entity.field_6036 = entityShouldBeHere.y();
                  entity.field_5969 = entityShouldBeHere.z();
                  entity.field_6038 = entityShouldBeHere.x();
                  entity.field_5971 = entityShouldBeHere.y();
                  entity.field_5989 = entityShouldBeHere.z();
               } else {
                  EntityDraggingInformation entityDraggingInformation = ((IEntityDraggingInformationProvider)entity).getDraggingInformation();
                  Long lastShipStoodOn = entityDraggingInformation.getLastShipStoodOn();
                  if (lastShipStoodOn != null && entityDraggingInformation.isEntityBeingDraggedByAShip()) {
                     ClientShip shipObject = VSGameUtilsKt.getShipObjectWorld(clientWorld).getLoadedShips().getById(lastShipStoodOn);
                     if (shipObject != null) {
                        entityDraggingInformation.setCachedLastPosition(new Vector3d(entity.field_6014, entity.field_6036, entity.field_5969));
                        entityDraggingInformation.setRestoreCachedLastPosition(true);
                        Vector3dc entityAddedVelocity = entityDraggingInformation.getAddedMovementLastTick();
                        double entityMovementX = entity.method_23317() - entityAddedVelocity.x() - entity.field_6014;
                        double entityMovementY = entity.method_23318() - entityAddedVelocity.y() - entity.field_6036;
                        double entityMovementZ = entity.method_23321() - entityAddedVelocity.z() - entity.field_5969;
                        Vector3dc entityShouldBeHerePreTransform = new Vector3d(
                           entity.field_6014 + entityMovementX * partialTick,
                           entity.field_6036 + entityMovementY * partialTick,
                           entity.field_5969 + entityMovementZ * partialTick
                        );
                        entityShouldBeHere = shipObject.getRenderTransform()
                           .getShipToWorldMatrix()
                           .transformPosition(
                              shipObject.getPrevTickShipTransform().getWorldToShipMatrix().transformPosition(entityShouldBeHerePreTransform, new Vector3d())
                           );
                     }
                  }

                  if (entityShouldBeHere != null && partialTick < 0.99999) {
                     entity.field_6014 = (entityShouldBeHere.x() - entity.method_23317() * partialTick) / (1.0 - partialTick);
                     entity.field_6036 = (entityShouldBeHere.y() - entity.method_23318() * partialTick) / (1.0 - partialTick);
                     entity.field_5969 = (entityShouldBeHere.z() - entity.method_23321() * partialTick) / (1.0 - partialTick);
                  }
               }
            }
         }
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void postRender(class_9779 deltaTracker, boolean bl, CallbackInfo ci) {
      class_638 clientWorld = this.field_4015.field_1687;
      if (clientWorld != null) {
         for (class_1297 entity : clientWorld.method_18112()) {
            EntityDraggingInformation vsEntity = ((IEntityDraggingInformationProvider)entity).getDraggingInformation();
            if (vsEntity.getRestoreCachedLastPosition()) {
               vsEntity.setRestoreCachedLastPosition(false);
               Vector3dc cachedLastPosition = vsEntity.getCachedLastPosition();
               if (cachedLastPosition != null) {
                  entity.field_6014 = cachedLastPosition.x();
                  entity.field_6036 = cachedLastPosition.y();
                  entity.field_5969 = cachedLastPosition.z();
               } else {
                  System.err.println("How was cachedLastPosition was null?");
               }
            }
         }
      }
   }

   @WrapOperation(
      method = "renderLevel",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;prepareCullFrustum(Lnet/minecraft/world/phys/Vec3;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"
      )
   )
   private void setupCameraWithMountedShip(
      class_761 instance, class_243 vec3, Matrix4f rotationMatrix, Matrix4f matrix4f2, Operation<Void> prepareCullFrustum, class_9779 deltaTracker
   ) {
      class_4184 camera = this.field_18765;
      if (camera == null) {
         prepareCullFrustum.call(new Object[]{instance, vec3, rotationMatrix, matrix4f2});
      } else {
         ((IVSCamera)camera).resetShipMountedRenderTransform();
         ShipCameraZoom.setShipCameraActive(false);
         class_638 clientLevel = this.field_4015.field_1687;
         class_1297 player = this.field_4015.field_1724;
         if (clientLevel != null && player != null) {
            float partialTicks = deltaTracker.method_60637(true);
            ShipMountedToData shipMountedToData = VSGameUtilsKt.getShipMountedToData(player, partialTicks);
            if (shipMountedToData == null) {
               prepareCullFrustum.call(new Object[]{instance, vec3, rotationMatrix, matrix4f2});
            } else {
               class_1297 playerVehicle = player.method_5854();
               if (playerVehicle == null) {
                  prepareCullFrustum.call(new Object[]{instance, vec3, rotationMatrix, matrix4f2});
               } else {
                  ClientShip clientShip = (ClientShip)shipMountedToData.getShipMountedTo();
                  boolean standing = playerVehicle instanceof ShipMountingEntity;
                  class_5498 cameraType = this.field_4015.field_1690.method_31044();
                  if (standing && !ShipMountPerspective.isShipViewEngaged()) {
                     prepareCullFrustum.call(new Object[]{instance, vec3, rotationMatrix, matrix4f2});
                  } else {
                     boolean thirdPersonForSetup;
                     boolean mirroredForSetup;
                     if (standing) {
                        thirdPersonForSetup = true;
                        mirroredForSetup = false;
                     } else {
                        thirdPersonForSetup = !cameraType.method_31034();
                        mirroredForSetup = cameraType.method_31035();
                     }

                     ShipCameraZoom.setShipCameraActive(thirdPersonForSetup);
                     ((IVSCamera)camera)
                        .setupWithShipMounted(
                           this.field_4015.field_1687,
                           (class_1297)(this.field_4015.method_1560() == null ? this.field_4015.field_1724 : this.field_4015.method_1560()),
                           thirdPersonForSetup,
                           mirroredForSetup,
                           partialTicks,
                           clientShip,
                           shipMountedToData.getMountPosInShip()
                        );
                     Quaternionf quaternionf = camera.method_23767().conjugate(new Quaternionf());
                     Matrix4f newRotationMatrix = new Matrix4f().rotation(quaternionf);
                     rotationMatrix.set(newRotationMatrix);
                     double fov = this.method_3196(camera, partialTicks, true);
                     Matrix4f projectionMatrixNew = this.method_22973(
                        Math.max(fov, ((Integer)this.field_4015.field_1690.method_41808().method_41753()).intValue())
                     );
                     prepareCullFrustum.call(new Object[]{instance, camera.method_19326(), rotationMatrix, projectionMatrixNew});
                  }
               }
            }
         } else {
            prepareCullFrustum.call(new Object[]{instance, vec3, rotationMatrix, matrix4f2});
         }
      }
   }

   @ModifyReturnValue(method = "getDepthFar", at = @At("RETURN"))
   public float includeShipsIn(float originalDepth) {
      float maxDistance = originalDepth;

      for (ClientShip ship : VSGameUtilsKt.getShipObjectWorld(class_310.method_1551()).getLoadedShips()) {
         class_243 cameraPos = this.field_18765.method_19326();
         AABBdc shipAABB = ship.getRenderAABB();
         double furthestDistanceSq = 0.0;
         double dMinX = shipAABB.minX() - cameraPos.method_10216();
         double dMaxX = shipAABB.maxX() - cameraPos.method_10216();
         double dMinY = shipAABB.minY() - cameraPos.method_10214();
         double dMaxY = shipAABB.maxY() - cameraPos.method_10214();
         double dMinZ = shipAABB.minZ() - cameraPos.method_10215();
         double dMaxZ = shipAABB.maxZ() - cameraPos.method_10215();
         double furthestDist = Math.sqrt(
            Math.max(dMinX * dMinX, dMaxX * dMaxX) + Math.max(dMinY * dMinY, dMaxY * dMaxY) + Math.max(dMinZ * dMinZ, dMaxZ * dMaxZ)
         );
         maxDistance = Math.max(maxDistance, (float)furthestDist);
      }

      return maxDistance;
   }
}
