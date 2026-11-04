package org.valkyrienskies.mod.common.util

import net.minecraft.class_1297
import net.minecraft.class_1531
import net.minecraft.class_1937
import net.minecraft.class_3532
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.VSGameUtilsKt

public object EntityLerper {
   public fun lerpStep(dragInfo: EntityDraggingInformation, refship: Ship, entity: class_1297) {
      if (refship is ClientShip && dragInfo.isEntityBeingDraggedByAShip()) {
         val ship: ClientShip = refship as ClientShip
         dragInfo.ticksSinceLastServerPacket = dragInfo.ticksSinceLastServerPacket + 1
         if (dragInfo.ticksSinceLastServerPacket > 19) {
         }

         if (dragInfo.lerpSteps > 0) {
            if (dragInfo.changedShipLastTick) {
               dragInfo.changedShipLastTick = false
            }

            var var10000: class_1937 = entity.method_37908()
            var var10001: Vector3dc = dragInfo.lerpPositionOnShip
            val var37: LoadedShip = VSGameUtilsKt.getLoadedShipManagingPos(var10000, var10001)
            var10000 = entity.method_37908()
            var10001 = dragInfo.relativePositionOnShip
            val posship: LoadedShip = VSGameUtilsKt.getLoadedShipManagingPos(var10000, var10001)
            if (dragInfo.lerpPositionOnShip != null && var37 != null && var37.getId() != ship.getId()) {
               dragInfo.lerpPositionOnShip = ship.getTransform().worldToShip
                  .transformPosition(var37.getTransform().shipToWorld.transformPosition(Vector3d(dragInfo.lerpPositionOnShip), Vector3d()), Vector3d()) as Vector3dc
            }

            if (dragInfo.relativePositionOnShip != null && posship != null && posship.getId() != ship.getId()) {
               dragInfo.relativePositionOnShip = ship.getTransform().worldToShip
                  .transformPosition(posship.getShipToWorld().transformPosition(Vector3d(dragInfo.relativePositionOnShip), Vector3d()), Vector3d()) as Vector3dc
            }

            val var41: Vector3dc = dragInfo.relativePositionOnShip
            if (var41 == null) {
               return
            }

            val var39: Double = var41.x()
            val var42: Vector3dc = dragInfo.relativePositionOnShip
            val currentY: Double = var42.y()
            val var43: Vector3dc = dragInfo.relativePositionOnShip
            val currentZ: Double = var43.z()
            val var44: Vector3dc = dragInfo.lerpPositionOnShip
            val lerpX: Double = var44.x()
            val var45: Vector3dc = dragInfo.lerpPositionOnShip
            val lerpY: Double = var45.y()
            val var46: Vector3dc = dragInfo.lerpPositionOnShip
            val lerpZ: Double = var46.z()
            val var47: java.lang.Double = dragInfo.relativeYawOnShip
            if (var47 == null) {
               return
            }

            val currentYaw: Double = var47
            val var48: java.lang.Double = dragInfo.lerpYawOnShip
            if (var48 == null) {
               return
            }

            val lerpYaw: Double = var48
            val newX: Double = var39 + (lerpX - var39) / dragInfo.lerpSteps
            val newY: Double = currentY + (lerpY - currentY) / dragInfo.lerpSteps
            val newZ: Double = currentZ + (lerpZ - currentZ) / dragInfo.lerpSteps
            val newPos: Vector3d = VSGameUtilsKt.toWorldCoordinates(entity.method_37908(), newX, newY, newZ, Vector3d())
            val currentYawWorld: Double = this.yawToWorld(ship, currentYaw)
            val lerpYawWorld: Double = this.yawToWorld(ship, lerpYaw)
            dragInfo.relativePositionOnShip = Vector3d(newX, newY, newZ) as Vector3dc
            entity.method_5814(newPos.x(), newPos.y(), newPos.z())
            if (entity !is class_1531) {
               val newYaw: Float = (float)(currentYawWorld + class_3532.method_15338(lerpYawWorld - currentYawWorld) / dragInfo.lerpSteps)
               entity.method_36456(newYaw)
               dragInfo.relativeYawOnShip = this.yawToShip(ship, (double)newYaw)
            }

            dragInfo.lerpSteps = dragInfo.lerpSteps - 1
         }
      }
   }

   public fun lerpHeadStep(dragInfo: EntityDraggingInformation, refship: Ship, entity: class_1297) {
      if (entity !is class_1531) {
         if (refship is ClientShip) {
            val ship: ClientShip = refship as ClientShip
            if (dragInfo.headLerpSteps > 0) {
               var var10000: java.lang.Double = dragInfo.relativeHeadYawOnShip
               if (var10000 == null) {
                  return
               }

               val currentHeadYaw: Double = var10000
               var10000 = dragInfo.lerpHeadYawOnShip
               if (var10000 == null) {
                  return
               }

               val lerpHeadYaw: Double = var10000
               val currentHeadYawWorld: Double = this.yawToWorld(ship, currentHeadYaw)
               val newHeadYaw: Double = currentHeadYawWorld
                  + class_3532.method_15338(this.yawToWorld(ship, lerpHeadYaw) - currentHeadYawWorld) / dragInfo.headLerpSteps
                  val var10001: Float = entity.method_36455()
               val var10002: java.lang.Double = dragInfo.lerpPitchOnShip
               entity.method_36457(var10001 + ((float)var10002.doubleValue() - entity.method_36455()) / (float)dragInfo.headLerpSteps)
               entity.method_5847((float)newHeadYaw)
               dragInfo.relativeHeadYawOnShip = this.yawToShip(ship, newHeadYaw)
               dragInfo.headLerpSteps = dragInfo.headLerpSteps + -1
            }
         }
      }
   }

   public fun yawToWorld(ship: Ship, yaw: Double): Double {
      val newLookIdeal: Vector3d = ship.transform.transformDirectionNoScalingFromShipToWorld(Vector3d(Math.sin(yaw), 0.0, Math.cos(yaw)), Vector3d())
      return class_3532.method_15338(Math.atan2(newLookIdeal.x(), newLookIdeal.z()) * 180.0 / Math.PI)
   }

   public fun yawToShip(ship: Ship, yaw: Double): Double {
      val newLookIdeal: Vector3d = ship.transform
         .transformDirectionNoScalingFromWorldToShip(Vector3d(Math.sin(yaw * Math.PI / 180.0), 0.0, Math.cos(yaw * Math.PI / 180.0)), Vector3d())
         return Math.atan2(newLookIdeal.x(), newLookIdeal.z())
   }
}
