package org.valkyrienskies.core.internal.joints

import org.valkyrienskies.core.api.util.PhysTickOnly

@PhysTickOnly
public data class VSJointAndId(jointId: Int, joint: VSJoint) {
   public final val jointId: Int
   public final val joint: VSJoint

   init {
      this.jointId = jointId
      this.joint = joint
   }

   public operator fun component1(): Int {
      return this.jointId
   }

   public operator fun component2(): VSJoint {
      return this.joint
   }

   public fun copy(jointId: Int = this.jointId, joint: VSJoint = this.joint): VSJointAndId {
      return VSJointAndId(jointId, joint)
   }

   public override fun toString(): String {
      return "VSJointAndId(jointId=${this.jointId}, joint=${this.joint})"
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.jointId) * 31 + this.joint.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is VSJointAndId && this.jointId == (other as VSJointAndId).jointId && this.joint == (other as VSJointAndId).joint
      }
   }
}
