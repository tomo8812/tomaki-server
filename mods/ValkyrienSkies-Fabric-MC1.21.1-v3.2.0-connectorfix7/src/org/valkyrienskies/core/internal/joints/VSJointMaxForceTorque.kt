package org.valkyrienskies.core.internal.joints

import org.valkyrienskies.core.api.util.PhysTickOnly

@PhysTickOnly
public data class VSJointMaxForceTorque(maxForce: Float, maxTorque: Float) {
   public final val maxForce: Float
   public final val maxTorque: Float

   init {
      this.maxForce = maxForce
      this.maxTorque = maxTorque
   }

   public operator fun component1(): Float {
      return this.maxForce
   }

   public operator fun component2(): Float {
      return this.maxTorque
   }

   public fun copy(maxForce: Float = this.maxForce, maxTorque: Float = this.maxTorque): VSJointMaxForceTorque {
      return VSJointMaxForceTorque(maxForce, maxTorque)
   }

   public override fun toString(): String {
      return "VSJointMaxForceTorque(maxForce=${this.maxForce}, maxTorque=${this.maxTorque})"
   }

   public override fun hashCode(): Int {
      return java.lang.Float.hashCode(this.maxForce) * 31 + java.lang.Float.hashCode(this.maxTorque)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is VSJointMaxForceTorque
            && java.lang.Float.compare(this.maxForce, (other as VSJointMaxForceTorque).maxForce) == 0
            && java.lang.Float.compare(this.maxTorque, (other as VSJointMaxForceTorque).maxTorque) == 0
         }
   }
}
