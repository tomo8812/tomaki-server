package org.valkyrienskies.core.impl.api_impl.physics

import org.valkyrienskies.core.api.events.CollisionEvent
import org.valkyrienskies.core.api.physics.ContactPoint
import org.valkyrienskies.core.api.world.PhysLevel

public data class CollisionEventImpl(dimensionId: String, physLevel: PhysLevel, shipIdA: Long, shipIdB: Long, contactPoints: Collection<ContactPoint>) :
   CollisionEvent {
   public open val dimensionId: String
   public open val physLevel: PhysLevel
   public open val shipIdA: Long
   public open val shipIdB: Long
   public open val contactPoints: Collection<ContactPoint>

   init {
      this.dimensionId = dimensionId
      this.physLevel = physLevel
      this.shipIdA = shipIdA
      this.shipIdB = shipIdB
      this.contactPoints = contactPoints
   }

   public override fun toString(): String {
      return "ContactInfo(dimensionId='${this.dimensionId}', contactPoints=${this.contactPoints})"
   }

   public operator fun component1(): String {
      return this.dimensionId
   }

   public operator fun component2(): PhysLevel {
      return this.physLevel
   }

   public operator fun component3(): Long {
      return this.shipIdA
   }

   public operator fun component4(): Long {
      return this.shipIdB
   }

   public operator fun component5(): Collection<ContactPoint> {
      return this.contactPoints
   }

   public fun copy(
      dimensionId: String = this.dimensionId,
      physLevel: PhysLevel = this.physLevel,
      shipIdA: Long = this.shipIdA,
      shipIdB: Long = this.shipIdB,
      contactPoints: Collection<ContactPoint> = this.contactPoints
   ): CollisionEventImpl {
      return CollisionEventImpl(dimensionId, physLevel, shipIdA, shipIdB, contactPoints)
   }

   public override fun hashCode(): Int {
      return (
               ((this.dimensionId.hashCode() * 31 + this.physLevel.hashCode()) * 31 + java.lang.Long.hashCode(this.shipIdA)) * 31
                  + java.lang.Long.hashCode(this.shipIdB)
            )
            * 31
         + this.contactPoints.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label46@
      if (this === other) {
         return true
      } else {
         return other is CollisionEventImpl
            && this.dimensionId == (other as CollisionEventImpl).dimensionId
            && this.physLevel == (other as CollisionEventImpl).physLevel
            && this.shipIdA == (other as CollisionEventImpl).shipIdA
            && this.shipIdB == (other as CollisionEventImpl).shipIdB
            && this.contactPoints == (other as CollisionEventImpl).contactPoints
         }
   }
}
