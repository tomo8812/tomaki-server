package org.valkyrienskies.core.impl.game

import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.bodies.properties.BodyKinematicsFactory
import org.valkyrienskies.core.internal.ShipTeleportData

public data class ShipTeleportDataImpl(newPos: Vector3dc = Vector3d() as Vector3dc,
      newRot: Quaterniondc = Quaterniond() as Quaterniondc,
      newVel: Vector3dc = Vector3d() as Vector3dc,
      newOmega: Vector3dc = Vector3d() as Vector3dc,
      newDimension: String? = null,
      newScale: Double? = null,
      newPosInShip: Vector3dc? = null
   ) :
   ShipTeleportData {
   public open val newPos: Vector3dc
   public open val newRot: Quaterniondc
   public open val newVel: Vector3dc
   public open val newOmega: Vector3dc
   public open val newDimension: String?
   public open val newScale: Double?
   public open val newPosInShip: Vector3dc?

   init {
      this.newPos = newPos
      this.newRot = newRot
      this.newVel = newVel
      this.newOmega = newOmega
      this.newDimension = newDimension
      this.newScale = newScale
      this.newPosInShip = newPosInShip
   }

   public override fun createNewShipTransform(oldShipTransform: ShipTransform): BodyKinematics {
      val var2: BodyKinematicsFactory = BodyKinematicsFactory.INSTANCE
      val var3: Vector3dc = this.newOmega
      val var4: Vector3dc = this.newVel
      var var12: Vector3dc
      if (this.newPosInShip != null) {
         var12 = this.newPos
         val var10001: Vector3d = this.newPosInShip.sub(oldShipTransform.positionInShip, Vector3d())
         val var10002: java.lang.Double = this.newScale
         var12 = var12.add(var10001.mul(var10002 ?: 1.0).rotate(this.newRot), Vector3d())
      } else {
         var12 = this.newPos
      }

      var12 = this.newPosInShip
      if (var12 == null) {
         var12 = oldShipTransform.positionInShip
      }

      val var7: Quaterniondc = this.newRot
      val var14: java.lang.Double = this.newScale
      val var15: Vector3dc = if (var14 != null) Vector3d(var14.doubleValue()) else oldShipTransform.shipToWorldScaling
      return var2.create(var4, var3, var12, var7, var15, var12)
   }

   public operator fun component1(): Vector3dc {
      return this.newPos
   }

   public operator fun component2(): Quaterniondc {
      return this.newRot
   }

   public operator fun component3(): Vector3dc {
      return this.newVel
   }

   public operator fun component4(): Vector3dc {
      return this.newOmega
   }

   public operator fun component5(): String? {
      return this.newDimension
   }

   public operator fun component6(): Double? {
      return this.newScale
   }

   public operator fun component7(): Vector3dc? {
      return this.newPosInShip
   }

   public fun copy(
      newPos: Vector3dc = this.newPos,
      newRot: Quaterniondc = this.newRot,
      newVel: Vector3dc = this.newVel,
      newOmega: Vector3dc = this.newOmega,
      newDimension: String? = this.newDimension,
      newScale: Double? = this.newScale,
      newPosInShip: Vector3dc? = this.newPosInShip
   ): ShipTeleportDataImpl {
      return ShipTeleportDataImpl(newPos, newRot, newVel, newOmega, newDimension, newScale, newPosInShip)
   }

   public override fun toString(): String {
      return "ShipTeleportDataImpl(newPos=${this.newPos}, newRot=${this.newRot}, newVel=${this.newVel}, newOmega=${this.newOmega}, newDimension=${this.newDimension}, newScale=${this.newScale}, newPosInShip=${this.newPosInShip})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (((this.newPos.hashCode() * 31 + this.newRot.hashCode()) * 31 + this.newVel.hashCode()) * 31 + this.newOmega.hashCode()) * 31
                           + (if (this.newDimension == null) 0 else this.newDimension.hashCode())
                     )
                     * 31
                  + (if (this.newScale == null) 0 else this.newScale.hashCode())
            )
            * 31
         + (if (this.newPosInShip == null) 0 else this.newPosInShip.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label58@
      if (this === other) {
         return true
      } else {
         return other is ShipTeleportDataImpl
            && this.newPos == (other as ShipTeleportDataImpl).newPos
            && this.newRot == (other as ShipTeleportDataImpl).newRot
            && this.newVel == (other as ShipTeleportDataImpl).newVel
            && this.newOmega == (other as ShipTeleportDataImpl).newOmega
            && this.newDimension == (other as ShipTeleportDataImpl).newDimension
            && this.newScale == (other as ShipTeleportDataImpl).newScale
            && this.newPosInShip == (other as ShipTeleportDataImpl).newPosInShip
         }
   }

   fun ShipTeleportDataImpl() {
      this(null, null, null, null, null, null, null, 127, null)
   }
}
