package org.valkyrienskies.mod.common.util

import org.joml.Vector3d
import org.joml.Vector3dc

public class EntityDraggingInformation {
   public final var addedMovementLastTick: Vector3dc = Vector3d() as Vector3dc
   public final var addedYawRotLastTick: Double
   public final var changedShipLastTick: Boolean
   public final var shouldImpulseMovement: Boolean

   public final var lastShipStoodOn: Long?
      public final set(value) {
         if (value != null) {
            this.ticksSinceStoodOnShip = 0
         }

         this.shouldImpulseMovement = !(this.lastShipStoodOn == value) && value != null
         this.changedShipLastTick = !(this.lastShipStoodOn == value) && this.lastShipStoodOn != null && value != null
         this.lastShipStoodOn = value
      }


   public final var lastShipStoodOnServerWriteOnly: Long?

   public final var ticksSinceStoodOnShip: Int
      public final set(value) {
         this.shouldImpulseMovement = false
         this.ticksSinceStoodOnShip = value
      }


   public final var ticksSinceLastServerPacket: Int
   public final var ignoreNextGroundStand: Boolean
   public final var mountedToEntity: Boolean
   public final var lerpPositionOnShip: Vector3dc?
   public final var relativeVelocityOnShip: Vector3dc?
   public final var lerpYawOnShip: Double?
   public final var lerpHeadYawOnShip: Double?
   public final var lerpPitchOnShip: Double?
   public final var relativePositionOnShip: Vector3dc?
   public final var previousRelativeVelocityOnShip: Vector3dc?
   public final var relativeYawOnShip: Double?
   public final var relativeHeadYawOnShip: Double?
   public final var relativePitchOnShip: Double?
   public final var draggedArmorStandRelYaw: Double?
   public final var lerpSteps: Int
   public final var headLerpSteps: Int
   public final var cachedLastPosition: Vector3dc?
   public final var restoreCachedLastPosition: Boolean
   public final var serverRelativePlayerPosition: Vector3dc?
   public final var serverRelativePlayerYaw: Double?

   public fun isEntityBeingDraggedByAShip(): Boolean {
      return this.lastShipStoodOn != null && this.ticksSinceStoodOnShip < 25 && !this.mountedToEntity
   }

   public fun bestRelativeEntityPosition(): Vector3dc? {
      val var10000: Vector3dc
      if (this.serverRelativePlayerPosition != null) {
         var10000 = this.serverRelativePlayerPosition
      } else if (this.relativePositionOnShip != null) {
         var10000 = this.relativePositionOnShip
      } else {
         var10000 = null
      }

      return var10000
   }

   public companion object {
      public const val TICKS_TO_DRAG_ENTITIES: Int = 25
   }
}
