package org.valkyrienskies.core.internal.joints

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.util.PhysTickOnly
import org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimitPair

@PhysTickOnly
public data class VSPrismaticJoint(shipId0: Long?,
      pose0: VSJointPose,
      shipId1: Long?,
      pose1: VSJointPose,
      maxForceTorque: VSJointMaxForceTorque? = null,
      compliance: Double = 1.0E-10,
      linearLimitPair: LinearLimitPair? = null
   ) :
   VSJoint {
   public open val shipId0: Long?
   public open val pose0: VSJointPose
   public open val shipId1: Long?
   public open val pose1: VSJointPose
   public open val maxForceTorque: VSJointMaxForceTorque?
   public open val compliance: Double
   public final val linearLimitPair: LinearLimitPair?
   public open var shouldBeSerialized: Boolean
   public open val jointType: VSJointType

   init {
      this.shipId0 = shipId0
      this.pose0 = pose0
      this.shipId1 = shipId1
      this.pose1 = pose1
      this.maxForceTorque = maxForceTorque
      this.compliance = compliance
      this.linearLimitPair = linearLimitPair
      this.jointType = VSJointType.PRISMATIC
   }

   public override fun offsetLocalPositions(offset0: Vector3dc, offset1: Vector3dc): VSJoint {
      val var10002: java.lang.Long = this.shipId0
      val var10005: Vector3d = this.pose0.pos.add(offset0, Vector3d())
      val var10003: VSJointPose = VSJointPose(var10005, this.pose0.rot)
      val var10004: java.lang.Long = this.shipId1
      val var10007: Vector3d = this.pose1.pos.add(offset1, Vector3d())
      return VSPrismaticJoint(var10002, var10003, var10004, VSJointPose(var10007, this.pose1.rot), this.maxForceTorque, this.compliance, this.linearLimitPair)
   }

   public override fun scaleLocalPositions(scale0: Double, scale1: Double): VSJoint {
      val var10002: java.lang.Long = this.shipId0
      val var10005: Vector3d = this.pose0.pos.mul(scale0, Vector3d())
      val var10003: VSJointPose = VSJointPose(var10005, this.pose0.rot)
      val var10004: java.lang.Long = this.shipId1
      val var10007: Vector3d = this.pose1.pos.mul(scale1, Vector3d())
      return VSPrismaticJoint(var10002, var10003, var10004, VSJointPose(var10007, this.pose1.rot), this.maxForceTorque, this.compliance, this.linearLimitPair)
   }

   public operator fun component1(): Long? {
      return this.shipId0
   }

   public operator fun component2(): VSJointPose {
      return this.pose0
   }

   public operator fun component3(): Long? {
      return this.shipId1
   }

   public operator fun component4(): VSJointPose {
      return this.pose1
   }

   public operator fun component5(): VSJointMaxForceTorque? {
      return this.maxForceTorque
   }

   public operator fun component6(): Double {
      return this.compliance
   }

   public operator fun component7(): LinearLimitPair? {
      return this.linearLimitPair
   }

   public fun copy(
      shipId0: Long? = this.shipId0,
      pose0: VSJointPose = this.pose0,
      shipId1: Long? = this.shipId1,
      pose1: VSJointPose = this.pose1,
      maxForceTorque: VSJointMaxForceTorque? = this.maxForceTorque,
      compliance: Double = this.compliance,
      linearLimitPair: LinearLimitPair? = this.linearLimitPair
   ): VSPrismaticJoint {
      return VSPrismaticJoint(shipId0, pose0, shipId1, pose1, maxForceTorque, compliance, linearLimitPair)
   }

   public override fun toString(): String {
      return "VSPrismaticJoint(shipId0=${this.shipId0}, pose0=${this.pose0}, shipId1=${this.shipId1}, pose1=${this.pose1}, maxForceTorque=${this.maxForceTorque}, compliance=${this.compliance}, linearLimitPair=${this.linearLimitPair})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          ((if (this.shipId0 == null) 0 else this.shipId0.hashCode()) * 31 + this.pose0.hashCode()) * 31
                                             + (if (this.shipId1 == null) 0 else this.shipId1.hashCode())
                                       )
                                       * 31
                                    + this.pose1.hashCode()
                              )
                              * 31
                           + (if (this.maxForceTorque == null) 0 else this.maxForceTorque.hashCode())
                     )
                     * 31
                  + java.lang.Double.hashCode(this.compliance)
            )
            * 31
         + (if (this.linearLimitPair == null) 0 else this.linearLimitPair.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label58@
      if (this === other) {
         return true
      } else {
         return other is VSPrismaticJoint
            && this.shipId0 == (other as VSPrismaticJoint).shipId0
            && this.pose0 == (other as VSPrismaticJoint).pose0
            && this.shipId1 == (other as VSPrismaticJoint).shipId1
            && this.pose1 == (other as VSPrismaticJoint).pose1
            && this.maxForceTorque == (other as VSPrismaticJoint).maxForceTorque
            && java.lang.Double.compare(this.compliance, (other as VSPrismaticJoint).compliance) == 0
            && this.linearLimitPair == (other as VSPrismaticJoint).linearLimitPair
         }
   }
}
