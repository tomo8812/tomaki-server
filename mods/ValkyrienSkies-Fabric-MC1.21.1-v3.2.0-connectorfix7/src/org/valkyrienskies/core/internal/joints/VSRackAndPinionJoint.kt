package org.valkyrienskies.core.internal.joints

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.util.PhysTickOnly
import org.valkyrienskies.core.internal.joints.VSD6Joint.Hinges

@PhysTickOnly
@UnavailableInKrunch
public data class VSRackAndPinionJoint(shipId0: Long?,
      pose0: VSJointPose,
      shipId1: Long?,
      pose1: VSJointPose,
      maxForceTorque: VSJointMaxForceTorque? = null,
      compliance: Double = 1.0E-10,
      hinges: Hinges? = null,
      ratio: Float? = null,
      data: org.valkyrienskies.core.internal.joints.VSRackAndPinionJoint.VSRackAndPinionJointData? = null
   ) :
   VSJoint {
   public open val shipId0: Long?
   public open val pose0: VSJointPose
   public open val shipId1: Long?
   public open val pose1: VSJointPose
   public open val maxForceTorque: VSJointMaxForceTorque?
   public open val compliance: Double
   public final val hinges: Hinges?
   public final val ratio: Float?
   public final val data: org.valkyrienskies.core.internal.joints.VSRackAndPinionJoint.VSRackAndPinionJointData?
   public open var shouldBeSerialized: Boolean
   public open val jointType: VSJointType

   init {
      this.shipId0 = shipId0
      this.pose0 = pose0
      this.shipId1 = shipId1
      this.pose1 = pose1
      this.maxForceTorque = maxForceTorque
      this.compliance = compliance
      this.hinges = hinges
      this.ratio = ratio
      this.data = data
      this.jointType = VSJointType.RACK_AND_PINION
   }

   public override fun offsetLocalPositions(offset0: Vector3dc, offset1: Vector3dc): VSJoint {
      val var10002: java.lang.Long = this.shipId0
      val var10005: Vector3d = this.pose0.pos.add(offset0, Vector3d())
      val var10003: VSJointPose = VSJointPose(var10005, this.pose0.rot)
      val var10004: java.lang.Long = this.shipId1
      val var10007: Vector3d = this.pose1.pos.add(offset1, Vector3d())
      return VSRackAndPinionJoint(
         var10002, var10003, var10004, VSJointPose(var10007, this.pose1.rot), this.maxForceTorque, this.compliance, this.hinges, this.ratio, this.data
      )
   }

   public override fun scaleLocalPositions(scale0: Double, scale1: Double): VSJoint {
      val var10002: java.lang.Long = this.shipId0
      val var10005: Vector3d = this.pose0.pos.mul(scale0, Vector3d())
      val var10003: VSJointPose = VSJointPose(var10005, this.pose0.rot)
      val var10004: java.lang.Long = this.shipId1
      val var10007: Vector3d = this.pose1.pos.mul(scale1, Vector3d())
      return VSRackAndPinionJoint(
         var10002, var10003, var10004, VSJointPose(var10007, this.pose1.rot), this.maxForceTorque, this.compliance, this.hinges, this.ratio, this.data
      )
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

   public operator fun component7(): Hinges? {
      return this.hinges
   }

   public operator fun component8(): Float? {
      return this.ratio
   }

   public operator fun component9(): org.valkyrienskies.core.internal.joints.VSRackAndPinionJoint.VSRackAndPinionJointData? {
      return this.data
   }

   public fun copy(
      shipId0: Long? = this.shipId0,
      pose0: VSJointPose = this.pose0,
      shipId1: Long? = this.shipId1,
      pose1: VSJointPose = this.pose1,
      maxForceTorque: VSJointMaxForceTorque? = this.maxForceTorque,
      compliance: Double = this.compliance,
      hinges: Hinges? = this.hinges,
      ratio: Float? = this.ratio,
      data: org.valkyrienskies.core.internal.joints.VSRackAndPinionJoint.VSRackAndPinionJointData? = this.data
   ): VSRackAndPinionJoint {
      return VSRackAndPinionJoint(shipId0, pose0, shipId1, pose1, maxForceTorque, compliance, hinges, ratio, data)
   }

   public override fun toString(): String {
      return "VSRackAndPinionJoint(shipId0=${this.shipId0}, pose0=${this.pose0}, shipId1=${this.shipId1}, pose1=${this.pose1}, maxForceTorque=${this.maxForceTorque}, compliance=${this.compliance}, hinges=${this.hinges}, ratio=${this.ratio}, data=${this.data})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
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
                           + (if (this.hinges == null) 0 else this.hinges.hashCode())
                     )
                     * 31
                  + (if (this.ratio == null) 0 else this.ratio.hashCode())
            )
            * 31
         + (if (this.data == null) 0 else this.data.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label70@
      if (this === other) {
         return true
      } else {
         return other is VSRackAndPinionJoint
            && this.shipId0 == (other as VSRackAndPinionJoint).shipId0
            && this.pose0 == (other as VSRackAndPinionJoint).pose0
            && this.shipId1 == (other as VSRackAndPinionJoint).shipId1
            && this.pose1 == (other as VSRackAndPinionJoint).pose1
            && this.maxForceTorque == (other as VSRackAndPinionJoint).maxForceTorque
            && java.lang.Double.compare(this.compliance, (other as VSRackAndPinionJoint).compliance) == 0
            && this.hinges == (other as VSRackAndPinionJoint).hinges
            && this.ratio == (other as VSRackAndPinionJoint).ratio
            && this.data == (other as VSRackAndPinionJoint).data
         }
   }

   public data class VSRackAndPinionJointData(rackTeeth: Int, pinionTeeth: Int, rackLength: Float) {
      public final val rackTeeth: Int
      public final val pinionTeeth: Int
      public final val rackLength: Float

      init {
         this.rackTeeth = rackTeeth
         this.pinionTeeth = pinionTeeth
         this.rackLength = rackLength
      }

      public operator fun component1(): Int {
         return this.rackTeeth
      }

      public operator fun component2(): Int {
         return this.pinionTeeth
      }

      public operator fun component3(): Float {
         return this.rackLength
      }

      public fun copy(rackTeeth: Int = this.rackTeeth, pinionTeeth: Int = this.pinionTeeth, rackLength: Float = this.rackLength): org.valkyrienskies.core.internal.joints.VSRackAndPinionJoint.VSRackAndPinionJointData {
         return VSRackAndPinionJoint.VSRackAndPinionJointData(rackTeeth, pinionTeeth, rackLength)
      }

      public override fun toString(): String {
         return "VSRackAndPinionJointData(rackTeeth=${this.rackTeeth}, pinionTeeth=${this.pinionTeeth}, rackLength=${this.rackLength})"
      }

      public override fun hashCode(): Int {
         return (Integer.hashCode(this.rackTeeth) * 31 + Integer.hashCode(this.pinionTeeth)) * 31 + java.lang.Float.hashCode(this.rackLength)
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is VSRackAndPinionJoint.VSRackAndPinionJointData
               && this.rackTeeth == (other as VSRackAndPinionJoint.VSRackAndPinionJointData).rackTeeth
               && this.pinionTeeth == (other as VSRackAndPinionJoint.VSRackAndPinionJointData).pinionTeeth
               && java.lang.Float.compare(this.rackLength, (other as VSRackAndPinionJoint.VSRackAndPinionJointData).rackLength) == 0
            }
      }
   }
}
