package org.valkyrienskies.core.internal.physics

import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform

/** @deprecated */
@Deprecated(message = "To be replaced by VsBodies eventually.")
public data class PhysicsEntityData(shipId: Long,
   transform: ShipTransform,
   inertiaData: ShipInertiaData,
   linearVelocity: Vector3dc,
   angularVelocity: Vector3dc,
   collisionShapeData: VSCollisionShapeData,
   collisionMask: Int = -1,
   staticFrictionCoefficient: Double = 0.3,
   dynamicFrictionCoefficient: Double = 0.3,
   restitutionCoefficient: Double = 0.6,
   isStatic: Boolean = false
) {
   public final val shipId: Long
   public final var transform: ShipTransform
   public final var inertiaData: ShipInertiaData
   public final var linearVelocity: Vector3dc
   public final var angularVelocity: Vector3dc
   public final var collisionShapeData: VSCollisionShapeData
   public final var collisionMask: Int
   public final var staticFrictionCoefficient: Double
   public final var dynamicFrictionCoefficient: Double
   public final var restitutionCoefficient: Double
   public final var isStatic: Boolean

   init {
      this.shipId = shipId
      this.transform = transform
      this.inertiaData = inertiaData
      this.linearVelocity = linearVelocity
      this.angularVelocity = angularVelocity
      this.collisionShapeData = collisionShapeData
      this.collisionMask = collisionMask
      this.staticFrictionCoefficient = staticFrictionCoefficient
      this.dynamicFrictionCoefficient = dynamicFrictionCoefficient
      this.restitutionCoefficient = restitutionCoefficient
      this.isStatic = isStatic
   }

   public fun copyPhysicsEntityDataWithNewId(newId: Long): PhysicsEntityData {
      return PhysicsEntityData(
         newId,
         this.transform,
         this.inertiaData,
         this.linearVelocity,
         this.angularVelocity,
         this.collisionShapeData,
         this.collisionMask,
         this.staticFrictionCoefficient,
         this.dynamicFrictionCoefficient,
         this.restitutionCoefficient,
         this.isStatic
      )
   }

   public operator fun component1(): Long {
      return this.shipId
   }

   public operator fun component2(): ShipTransform {
      return this.transform
   }

   public operator fun component3(): ShipInertiaData {
      return this.inertiaData
   }

   public operator fun component4(): Vector3dc {
      return this.linearVelocity
   }

   public operator fun component5(): Vector3dc {
      return this.angularVelocity
   }

   public operator fun component6(): VSCollisionShapeData {
      return this.collisionShapeData
   }

   public operator fun component7(): Int {
      return this.collisionMask
   }

   public operator fun component8(): Double {
      return this.staticFrictionCoefficient
   }

   public operator fun component9(): Double {
      return this.dynamicFrictionCoefficient
   }

   public operator fun component10(): Double {
      return this.restitutionCoefficient
   }

   public operator fun component11(): Boolean {
      return this.isStatic
   }

   public fun copy(
      shipId: Long = this.shipId,
      transform: ShipTransform = this.transform,
      inertiaData: ShipInertiaData = this.inertiaData,
      linearVelocity: Vector3dc = this.linearVelocity,
      angularVelocity: Vector3dc = this.angularVelocity,
      collisionShapeData: VSCollisionShapeData = this.collisionShapeData,
      collisionMask: Int = this.collisionMask,
      staticFrictionCoefficient: Double = this.staticFrictionCoefficient,
      dynamicFrictionCoefficient: Double = this.dynamicFrictionCoefficient,
      restitutionCoefficient: Double = this.restitutionCoefficient,
      isStatic: Boolean = this.isStatic
   ): PhysicsEntityData {
      return PhysicsEntityData(
         shipId,
         transform,
         inertiaData,
         linearVelocity,
         angularVelocity,
         collisionShapeData,
         collisionMask,
         staticFrictionCoefficient,
         dynamicFrictionCoefficient,
         restitutionCoefficient,
         isStatic
      )
   }

   public override fun toString(): String {
      return "PhysicsEntityData(shipId=${this.shipId}, transform=${this.transform}, inertiaData=${this.inertiaData}, linearVelocity=${this.linearVelocity}, angularVelocity=${this.angularVelocity}, collisionShapeData=${this.collisionShapeData}, collisionMask=${this.collisionMask}, staticFrictionCoefficient=${this.staticFrictionCoefficient}, dynamicFrictionCoefficient=${this.dynamicFrictionCoefficient}, restitutionCoefficient=${this.restitutionCoefficient}, isStatic=${this.isStatic})"
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
                                                                              (java.lang.Long.hashCode(this.shipId) * 31 + this.transform.hashCode()) * 31
                                                                                 + this.inertiaData.hashCode()
                                                                           )
                                                                           * 31
                                                                        + this.linearVelocity.hashCode()
                                                                  )
                                                                  * 31
                                                               + this.angularVelocity.hashCode()
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
      }

   public override operator fun equals(other: Any?): Boolean {
      label82@
      if (this === other) {
         return true
      } else {
         return other is PhysicsEntityData
            && this.shipId == (other as PhysicsEntityData).shipId
            && this.transform == (other as PhysicsEntityData).transform
            && this.inertiaData == (other as PhysicsEntityData).inertiaData
            && this.linearVelocity == (other as PhysicsEntityData).linearVelocity
            && this.angularVelocity == (other as PhysicsEntityData).angularVelocity
            && this.collisionShapeData == (other as PhysicsEntityData).collisionShapeData
            && this.collisionMask == (other as PhysicsEntityData).collisionMask
            && java.lang.Double.compare(this.staticFrictionCoefficient, (other as PhysicsEntityData).staticFrictionCoefficient) == 0
            && java.lang.Double.compare(this.dynamicFrictionCoefficient, (other as PhysicsEntityData).dynamicFrictionCoefficient) == 0
            && java.lang.Double.compare(this.restitutionCoefficient, (other as PhysicsEntityData).restitutionCoefficient) == 0
            && this.isStatic == (other as PhysicsEntityData).isStatic
         }
   }
}
