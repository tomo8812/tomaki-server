package org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.dto

import com.fasterxml.jackson.annotation.JsonIncludeProperties
import org.joml.Matrix4d
import org.joml.Matrix4dc
import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc

@JsonIncludeProperties(["shipPositionInWorldCoordinates", "shipPositionInShipCoordinates", "shipCoordinatesToWorldCoordinatesRotation", "shipCoordinatesToWorldCoordinatesScaling"])
public data class ShipTransformDataV0(shipPositionInWorldCoordinates: Vector3dc,
   shipPositionInShipCoordinates: Vector3dc,
   shipCoordinatesToWorldCoordinatesRotation: Quaterniondc,
   shipCoordinatesToWorldCoordinatesScaling: Vector3dc
) {
   public final val shipPositionInWorldCoordinates: Vector3dc
   public final val shipPositionInShipCoordinates: Vector3dc
   public final val shipCoordinatesToWorldCoordinatesRotation: Quaterniondc
   public final val shipCoordinatesToWorldCoordinatesScaling: Vector3dc
   public final val shipToWorldMatrix: Matrix4dc
   public final val worldToShipMatrix: Matrix4dc

   init {
      this.shipPositionInWorldCoordinates = shipPositionInWorldCoordinates
      this.shipPositionInShipCoordinates = shipPositionInShipCoordinates
      this.shipCoordinatesToWorldCoordinatesRotation = shipCoordinatesToWorldCoordinatesRotation
      this.shipCoordinatesToWorldCoordinatesScaling = shipCoordinatesToWorldCoordinatesScaling
      var var10001: Matrix4d = Matrix4d()
         .translate(this.shipPositionInWorldCoordinates)
         .rotate(this.shipCoordinatesToWorldCoordinatesRotation)
         .scale(this.shipCoordinatesToWorldCoordinatesScaling)
         .translate(-this.shipPositionInShipCoordinates.x(), -this.shipPositionInShipCoordinates.y(), -this.shipPositionInShipCoordinates.z())
         this.shipToWorldMatrix = var10001
      var10001 = this.shipToWorldMatrix.invert(Matrix4d())
      this.worldToShipMatrix = var10001
   }

   public fun createEmptyAABB(): AABBdc {
      return AABBd(this.shipPositionInWorldCoordinates, this.shipPositionInWorldCoordinates)
   }

   public operator fun component1(): Vector3dc {
      return this.shipPositionInWorldCoordinates
   }

   public operator fun component2(): Vector3dc {
      return this.shipPositionInShipCoordinates
   }

   public operator fun component3(): Quaterniondc {
      return this.shipCoordinatesToWorldCoordinatesRotation
   }

   public operator fun component4(): Vector3dc {
      return this.shipCoordinatesToWorldCoordinatesScaling
   }

   public fun copy(
      shipPositionInWorldCoordinates: Vector3dc = this.shipPositionInWorldCoordinates,
      shipPositionInShipCoordinates: Vector3dc = this.shipPositionInShipCoordinates,
      shipCoordinatesToWorldCoordinatesRotation: Quaterniondc = this.shipCoordinatesToWorldCoordinatesRotation,
      shipCoordinatesToWorldCoordinatesScaling: Vector3dc = this.shipCoordinatesToWorldCoordinatesScaling
   ): ShipTransformDataV0 {
      return ShipTransformDataV0(
         shipPositionInWorldCoordinates, shipPositionInShipCoordinates, shipCoordinatesToWorldCoordinatesRotation, shipCoordinatesToWorldCoordinatesScaling
      )
   }

   public override fun toString(): String {
      return "ShipTransformDataV0(shipPositionInWorldCoordinates=${this.shipPositionInWorldCoordinates}, shipPositionInShipCoordinates=${this.shipPositionInShipCoordinates}, shipCoordinatesToWorldCoordinatesRotation=${this.shipCoordinatesToWorldCoordinatesRotation}, shipCoordinatesToWorldCoordinatesScaling=${this.shipCoordinatesToWorldCoordinatesScaling})"
   }

   public override fun hashCode(): Int {
      return (
               (this.shipPositionInWorldCoordinates.hashCode() * 31 + this.shipPositionInShipCoordinates.hashCode()) * 31
                  + this.shipCoordinatesToWorldCoordinatesRotation.hashCode()
            )
            * 31
         + this.shipCoordinatesToWorldCoordinatesScaling.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is ShipTransformDataV0
            && this.shipPositionInWorldCoordinates == (other as ShipTransformDataV0).shipPositionInWorldCoordinates
            && this.shipPositionInShipCoordinates == (other as ShipTransformDataV0).shipPositionInShipCoordinates
            && this.shipCoordinatesToWorldCoordinatesRotation == (other as ShipTransformDataV0).shipCoordinatesToWorldCoordinatesRotation
            && this.shipCoordinatesToWorldCoordinatesScaling == (other as ShipTransformDataV0).shipCoordinatesToWorldCoordinatesScaling
         }
   }
}
