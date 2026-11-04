package org.valkyrienskies.core.internal.joints

import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.util.PhysTickOnly

@PhysTickOnly
public data class VSJointPose(pos: Vector3dc, rot: Quaterniondc) {
   public final val pos: Vector3dc
   public final val rot: Quaterniondc

   init {
      this.pos = pos
      this.rot = rot
   }

   public operator fun component1(): Vector3dc {
      return this.pos
   }

   public operator fun component2(): Quaterniondc {
      return this.rot
   }

   public fun copy(pos: Vector3dc = this.pos, rot: Quaterniondc = this.rot): VSJointPose {
      return VSJointPose(pos, rot)
   }

   public override fun toString(): String {
      return "VSJointPose(pos=${this.pos}, rot=${this.rot})"
   }

   public override fun hashCode(): Int {
      return this.pos.hashCode() * 31 + this.rot.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is VSJointPose && this.pos == (other as VSJointPose).pos && this.rot == (other as VSJointPose).rot
      }
   }
}
