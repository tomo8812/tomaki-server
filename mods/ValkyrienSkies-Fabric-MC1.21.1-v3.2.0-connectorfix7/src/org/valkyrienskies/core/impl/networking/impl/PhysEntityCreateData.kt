package org.valkyrienskies.core.impl.networking.impl

import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.internal.physics.VSCollisionShapeData

public data class PhysEntityCreateData(id: Long, dimensionId: String, shipTransform: ShipTransform, collisionShapeData: VSCollisionShapeData) {
   public final val id: Long
   public final val dimensionId: String
   public final val shipTransform: ShipTransform
   public final var collisionShapeData: VSCollisionShapeData

   init {
      this.id = id
      this.dimensionId = dimensionId
      this.shipTransform = shipTransform
      this.collisionShapeData = collisionShapeData
   }

   public operator fun component1(): Long {
      return this.id
   }

   public operator fun component2(): String {
      return this.dimensionId
   }

   public operator fun component3(): ShipTransform {
      return this.shipTransform
   }

   public operator fun component4(): VSCollisionShapeData {
      return this.collisionShapeData
   }

   public fun copy(
      id: Long = this.id,
      dimensionId: String = this.dimensionId,
      shipTransform: ShipTransform = this.shipTransform,
      collisionShapeData: VSCollisionShapeData = this.collisionShapeData
   ): PhysEntityCreateData {
      return PhysEntityCreateData(id, dimensionId, shipTransform, collisionShapeData)
   }

   public override fun toString(): String {
      return "PhysEntityCreateData(id=${this.id}, dimensionId=${this.dimensionId}, shipTransform=${this.shipTransform}, collisionShapeData=${this.collisionShapeData})"
   }

   public override fun hashCode(): Int {
      return ((java.lang.Long.hashCode(this.id) * 31 + this.dimensionId.hashCode()) * 31 + this.shipTransform.hashCode()) * 31
         + this.collisionShapeData.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is PhysEntityCreateData
            && this.id == (other as PhysEntityCreateData).id
            && this.dimensionId == (other as PhysEntityCreateData).dimensionId
            && this.shipTransform == (other as PhysEntityCreateData).shipTransform
            && this.collisionShapeData == (other as PhysEntityCreateData).collisionShapeData
         }
   }
}
