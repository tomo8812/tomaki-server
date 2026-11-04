package org.valkyrienskies.core.internal.physics

import java.util.ArrayList
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ShipPhysicsListener
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform

/** @deprecated */
@Deprecated(message = "To be replaced by VsBodies eventually.")
public data class PhysicsEntityServer(id: Long,
   dimensionId: String,
   linearVelocity: Vector3dc,
   angularVelocity: Vector3dc,
   inertiaData: ShipInertiaData,
   shipTransform: ShipTransform,
   prevTickShipTransform: ShipTransform,
   shipTeleportId: Int,
   collisionShapeData: VSCollisionShapeData,
   collisionMask: Int = -1,
   staticFrictionCoefficient: Double = 0.3,
   dynamicFrictionCoefficient: Double = 0.3,
   restitutionCoefficient: Double = 0.6,
   isStatic: Boolean = false,
   needsUpdating: Boolean = false,
   physicsListeners: MutableList<ShipPhysicsListener> = ArrayList() as java.util.List
) {
   public final val id: Long
   public final val dimensionId: String
   public final var linearVelocity: Vector3dc
   public final var angularVelocity: Vector3dc
   public final var inertiaData: ShipInertiaData
   public final var shipTransform: ShipTransform
   public final var prevTickShipTransform: ShipTransform
   public final var shipTeleportId: Int
   public final var collisionShapeData: VSCollisionShapeData
   public final var collisionMask: Int
   public final var staticFrictionCoefficient: Double
   public final var dynamicFrictionCoefficient: Double
   public final var restitutionCoefficient: Double
   public final var isStatic: Boolean
   public final var needsUpdating: Boolean
   public final val physicsListeners: MutableList<ShipPhysicsListener>

   init {
      this.id = id
      this.dimensionId = dimensionId
      this.linearVelocity = linearVelocity
      this.angularVelocity = angularVelocity
      this.inertiaData = inertiaData
      this.shipTransform = shipTransform
      this.prevTickShipTransform = prevTickShipTransform
      this.shipTeleportId = shipTeleportId
      this.collisionShapeData = collisionShapeData
      this.collisionMask = collisionMask
      this.staticFrictionCoefficient = staticFrictionCoefficient
      this.dynamicFrictionCoefficient = dynamicFrictionCoefficient
      this.restitutionCoefficient = restitutionCoefficient
      this.isStatic = isStatic
      this.needsUpdating = needsUpdating
      this.physicsListeners = physicsListeners
   }

   public operator fun component1(): Long {
      return this.id
   }

   public operator fun component2(): String {
      return this.dimensionId
   }

   public operator fun component3(): Vector3dc {
      return this.linearVelocity
   }

   public operator fun component4(): Vector3dc {
      return this.angularVelocity
   }

   public operator fun component5(): ShipInertiaData {
      return this.inertiaData
   }

   public operator fun component6(): ShipTransform {
      return this.shipTransform
   }

   public operator fun component7(): ShipTransform {
      return this.prevTickShipTransform
   }

   public operator fun component8(): Int {
      return this.shipTeleportId
   }

   public operator fun component9(): VSCollisionShapeData {
      return this.collisionShapeData
   }

   public operator fun component10(): Int {
      return this.collisionMask
   }

   public operator fun component11(): Double {
      return this.staticFrictionCoefficient
   }

   public operator fun component12(): Double {
      return this.dynamicFrictionCoefficient
   }

   public operator fun component13(): Double {
      return this.restitutionCoefficient
   }

   public operator fun component14(): Boolean {
      return this.isStatic
   }

   public operator fun component15(): Boolean {
      return this.needsUpdating
   }

   public operator fun component16(): MutableList<ShipPhysicsListener> {
      return this.physicsListeners
   }

   public fun copy(
      id: Long = this.id,
      dimensionId: String = this.dimensionId,
      linearVelocity: Vector3dc = this.linearVelocity,
      angularVelocity: Vector3dc = this.angularVelocity,
      inertiaData: ShipInertiaData = this.inertiaData,
      shipTransform: ShipTransform = this.shipTransform,
      prevTickShipTransform: ShipTransform = this.prevTickShipTransform,
      shipTeleportId: Int = this.shipTeleportId,
      collisionShapeData: VSCollisionShapeData = this.collisionShapeData,
      collisionMask: Int = this.collisionMask,
      staticFrictionCoefficient: Double = this.staticFrictionCoefficient,
      dynamicFrictionCoefficient: Double = this.dynamicFrictionCoefficient,
      restitutionCoefficient: Double = this.restitutionCoefficient,
      isStatic: Boolean = this.isStatic,
      needsUpdating: Boolean = this.needsUpdating,
      physicsListeners: MutableList<ShipPhysicsListener> = this.physicsListeners
   ): PhysicsEntityServer {
      return PhysicsEntityServer(
         id,
         dimensionId,
         linearVelocity,
         angularVelocity,
         inertiaData,
         shipTransform,
         prevTickShipTransform,
         shipTeleportId,
         collisionShapeData,
         collisionMask,
         staticFrictionCoefficient,
         dynamicFrictionCoefficient,
         restitutionCoefficient,
         isStatic,
         needsUpdating,
         physicsListeners
      )
   }

   public override fun toString(): String {
      return "PhysicsEntityServer(id=${this.id}, dimensionId=${this.dimensionId}, linearVelocity=${this.linearVelocity}, angularVelocity=${this.angularVelocity}, inertiaData=${this.inertiaData}, shipTransform=${this.shipTransform}, prevTickShipTransform=${this.prevTickShipTransform}, shipTeleportId=${this.shipTeleportId}, collisionShapeData=${this.collisionShapeData}, collisionMask=${this.collisionMask}, staticFrictionCoefficient=${this.staticFrictionCoefficient}, dynamicFrictionCoefficient=${this.dynamicFrictionCoefficient}, restitutionCoefficient=${this.restitutionCoefficient}, isStatic=${this.isStatic}, needsUpdating=${this.needsUpdating}, physicsListeners=${this.physicsListeners})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              (
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           (
                                                                                                                                    java.lang.Long.hashCode(
                                                                                                                                             this.id
                                                                                                                                          )
                                                                                                                                          * 31
                                                                                                                                       + this.dimensionId
                                                                                                                                          .hashCode()
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + this.linearVelocity.hashCode()
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + this.angularVelocity.hashCode()
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this.inertiaData.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + this.shipTransform.hashCode()
                                                                                             )
                                                                                             * 31
                                                                                          + this.prevTickShipTransform.hashCode()
                                                                                    )
                                                                                    * 31
                                                                                 + Integer.hashCode(this.shipTeleportId)
                                                                           )
                                                                           * 31
                                                                        + this.collisionShapeData.hashCode()
                                                                  )
                                                                  * 31
                                                               + Integer.hashCode(this.collisionMask)
                                                         )
                                                         * 31
                                                      + java.lang.Double.hashCode(this.staticFrictionCoefficient)
                                                )
                                                * 31
                                             + java.lang.Double.hashCode(this.dynamicFrictionCoefficient)
                                       )
                                       * 31
                                    + java.lang.Double.hashCode(this.restitutionCoefficient)
                              )
                              * 31
                           + java.lang.Boolean.hashCode(this.isStatic)
                     )
                     * 31
                  + java.lang.Boolean.hashCode(this.needsUpdating)
            )
            * 31
         + this.physicsListeners.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label112@
      if (this === other) {
         return true
      } else {
         return other is PhysicsEntityServer
            && this.id == (other as PhysicsEntityServer).id
            && this.dimensionId == (other as PhysicsEntityServer).dimensionId
            && this.linearVelocity == (other as PhysicsEntityServer).linearVelocity
            && this.angularVelocity == (other as PhysicsEntityServer).angularVelocity
            && this.inertiaData == (other as PhysicsEntityServer).inertiaData
            && this.shipTransform == (other as PhysicsEntityServer).shipTransform
            && this.prevTickShipTransform == (other as PhysicsEntityServer).prevTickShipTransform
            && this.shipTeleportId == (other as PhysicsEntityServer).shipTeleportId
            && this.collisionShapeData == (other as PhysicsEntityServer).collisionShapeData
            && this.collisionMask == (other as PhysicsEntityServer).collisionMask
            && java.lang.Double.compare(this.staticFrictionCoefficient, (other as PhysicsEntityServer).staticFrictionCoefficient) == 0
            && java.lang.Double.compare(this.dynamicFrictionCoefficient, (other as PhysicsEntityServer).dynamicFrictionCoefficient) == 0
            && java.lang.Double.compare(this.restitutionCoefficient, (other as PhysicsEntityServer).restitutionCoefficient) == 0
            && this.isStatic == (other as PhysicsEntityServer).isStatic
            && this.needsUpdating == (other as PhysicsEntityServer).needsUpdating
            && this.physicsListeners == (other as PhysicsEntityServer).physicsListeners
         }
   }
}
