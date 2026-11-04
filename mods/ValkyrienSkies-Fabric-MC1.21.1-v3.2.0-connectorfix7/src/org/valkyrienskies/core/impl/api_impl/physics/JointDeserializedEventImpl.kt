package org.valkyrienskies.core.impl.api_impl.physics

import org.valkyrienskies.core.api.events.JointDeserializedEvent
import org.valkyrienskies.core.api.events.JointDeserializedEvent.Outcome

public data class JointDeserializedEventImpl(jointId: Int,
      shipId0: Long?,
      shipId1: Long?,
      jointType: String,
      dimensionId: String?,
      outcome: Outcome,
      reason: String?
   ) :
   JointDeserializedEvent {
   public open val jointId: Int
   public open val shipId0: Long?
   public open val shipId1: Long?
   public open val jointType: String
   public open val dimensionId: String?
   public open val outcome: Outcome
   public open val reason: String?

   init {
      this.jointId = jointId
      this.shipId0 = shipId0
      this.shipId1 = shipId1
      this.jointType = jointType
      this.dimensionId = dimensionId
      this.outcome = outcome
      this.reason = reason
   }

   public operator fun component1(): Int {
      return this.jointId
   }

   public operator fun component2(): Long? {
      return this.shipId0
   }

   public operator fun component3(): Long? {
      return this.shipId1
   }

   public operator fun component4(): String {
      return this.jointType
   }

   public operator fun component5(): String? {
      return this.dimensionId
   }

   public operator fun component6(): Outcome {
      return this.outcome
   }

   public operator fun component7(): String? {
      return this.reason
   }

   public fun copy(
      jointId: Int = this.jointId,
      shipId0: Long? = this.shipId0,
      shipId1: Long? = this.shipId1,
      jointType: String = this.jointType,
      dimensionId: String? = this.dimensionId,
      outcome: Outcome = this.outcome,
      reason: String? = this.reason
   ): JointDeserializedEventImpl {
      return JointDeserializedEventImpl(jointId, shipId0, shipId1, jointType, dimensionId, outcome, reason)
   }

   public override fun toString(): String {
      return "JointDeserializedEventImpl(jointId=${this.jointId}, shipId0=${this.shipId0}, shipId1=${this.shipId1}, jointType=${this.jointType}, dimensionId=${this.dimensionId}, outcome=${this.outcome}, reason=${this.reason})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (Integer.hashCode(this.jointId) * 31 + (if (this.shipId0 == null) 0 else this.shipId0.hashCode())) * 31
                                             + (if (this.shipId1 == null) 0 else this.shipId1.hashCode())
                                       )
                                       * 31
                                    + this.jointType.hashCode()
                              )
                              * 31
                           + (if (this.dimensionId == null) 0 else this.dimensionId.hashCode())
                     )
                     * 31
                  + this.outcome.hashCode()
            )
            * 31
         + (if (this.reason == null) 0 else this.reason.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label58@
      if (this === other) {
         return true
      } else {
         return other is JointDeserializedEventImpl
            && this.jointId == (other as JointDeserializedEventImpl).jointId
            && this.shipId0 == (other as JointDeserializedEventImpl).shipId0
            && this.shipId1 == (other as JointDeserializedEventImpl).shipId1
            && this.jointType == (other as JointDeserializedEventImpl).jointType
            && this.dimensionId == (other as JointDeserializedEventImpl).dimensionId
            && this.outcome === (other as JointDeserializedEventImpl).outcome
            && this.reason == (other as JointDeserializedEventImpl).reason
         }
   }
}
