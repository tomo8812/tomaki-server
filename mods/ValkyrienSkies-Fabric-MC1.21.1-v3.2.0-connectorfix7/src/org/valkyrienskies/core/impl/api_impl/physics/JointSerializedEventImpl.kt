package org.valkyrienskies.core.impl.api_impl.physics

import org.valkyrienskies.core.api.events.JointSerializedEvent
import org.valkyrienskies.core.api.events.JointSerializedEvent.Operation

public data class JointSerializedEventImpl(jointId: Int, shipId0: Long?, shipId1: Long?, jointType: String, dimensionId: String, operation: Operation) :
   JointSerializedEvent {
   public open val jointId: Int
   public open val shipId0: Long?
   public open val shipId1: Long?
   public open val jointType: String
   public open val dimensionId: String
   public open val operation: Operation

   init {
      this.jointId = jointId
      this.shipId0 = shipId0
      this.shipId1 = shipId1
      this.jointType = jointType
      this.dimensionId = dimensionId
      this.operation = operation
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

   public operator fun component5(): String {
      return this.dimensionId
   }

   public operator fun component6(): Operation {
      return this.operation
   }

   public fun copy(
      jointId: Int = this.jointId,
      shipId0: Long? = this.shipId0,
      shipId1: Long? = this.shipId1,
      jointType: String = this.jointType,
      dimensionId: String = this.dimensionId,
      operation: Operation = this.operation
   ): JointSerializedEventImpl {
      return JointSerializedEventImpl(jointId, shipId0, shipId1, jointType, dimensionId, operation)
   }

   public override fun toString(): String {
      return "JointSerializedEventImpl(jointId=${this.jointId}, shipId0=${this.shipId0}, shipId1=${this.shipId1}, jointType=${this.jointType}, dimensionId=${this.dimensionId}, operation=${this.operation})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (Integer.hashCode(this.jointId) * 31 + (if (this.shipId0 == null) 0 else this.shipId0.hashCode())) * 31
                                    + (if (this.shipId1 == null) 0 else this.shipId1.hashCode())
                              )
                              * 31
                           + this.jointType.hashCode()
                     )
                     * 31
                  + this.dimensionId.hashCode()
            )
            * 31
         + this.operation.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label52@
      if (this === other) {
         return true
      } else {
         return other is JointSerializedEventImpl
            && this.jointId == (other as JointSerializedEventImpl).jointId
            && this.shipId0 == (other as JointSerializedEventImpl).shipId0
            && this.shipId1 == (other as JointSerializedEventImpl).shipId1
            && this.jointType == (other as JointSerializedEventImpl).jointType
            && this.dimensionId == (other as JointSerializedEventImpl).dimensionId
            && this.operation === (other as JointSerializedEventImpl).operation
         }
   }
}
