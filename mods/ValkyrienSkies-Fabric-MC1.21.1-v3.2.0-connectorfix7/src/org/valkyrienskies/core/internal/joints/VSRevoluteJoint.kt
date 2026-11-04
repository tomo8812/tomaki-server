package org.valkyrienskies.core.internal.joints

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.util.PhysTickOnly
import org.valkyrienskies.core.internal.joints.VSD6Joint.AngularLimitPair

@PhysTickOnly
public data class VSRevoluteJoint(shipId0: Long?,
      pose0: VSJointPose,
      shipId1: Long?,
      pose1: VSJointPose,
      maxForceTorque: VSJointMaxForceTorque? = null,
      compliance: Double = 1.0E-10,
      angularLimitPair: AngularLimitPair? = null,
      driveVelocity: org.valkyrienskies.core.internal.joints.VSRevoluteJoint.VSRevoluteDriveVelocity? = null,
      driveForceLimit: Float? = null,
      driveGearRatio: Float? = null,
      driveFreeSpin: Boolean? = null
   ) :
   VSJoint {
   public open val shipId0: Long?
   public open val pose0: VSJointPose
   public open val shipId1: Long?
   public open val pose1: VSJointPose
   public open val maxForceTorque: VSJointMaxForceTorque?
   public open val compliance: Double
   public final val angularLimitPair: AngularLimitPair?
   public final val driveVelocity: org.valkyrienskies.core.internal.joints.VSRevoluteJoint.VSRevoluteDriveVelocity?
   public final val driveForceLimit: Float?
   public final val driveGearRatio: Float?
   public final val driveFreeSpin: Boolean?
   public open var shouldBeSerialized: Boolean
   public open val jointType: VSJointType

   init {
      this.shipId0 = shipId0
      this.pose0 = pose0
      this.shipId1 = shipId1
      this.pose1 = pose1
      this.maxForceTorque = maxForceTorque
      this.compliance = compliance
      this.angularLimitPair = angularLimitPair
      this.driveVelocity = driveVelocity
      this.driveForceLimit = driveForceLimit
      this.driveGearRatio = driveGearRatio
      this.driveFreeSpin = driveFreeSpin
      this.jointType = VSJointType.REVOLUTE
   }

   public override fun offsetLocalPositions(offset0: Vector3dc, offset1: Vector3dc): VSJoint {
      val var10002: java.lang.Long = this.shipId0
      val var10005: Vector3d = this.pose0.pos.add(offset0, Vector3d())
      val var10003: VSJointPose = VSJointPose(var10005, this.pose0.rot)
      val var10004: java.lang.Long = this.shipId1
      val var10007: Vector3d = this.pose1.pos.add(offset1, Vector3d())
      return VSRevoluteJoint(
         var10002,
         var10003,
         var10004,
         VSJointPose(var10007, this.pose1.rot),
         this.maxForceTorque,
         this.compliance,
         this.angularLimitPair,
         this.driveVelocity,
         this.driveForceLimit,
         this.driveGearRatio,
         this.driveFreeSpin
      )
   }

   public override fun scaleLocalPositions(scale0: Double, scale1: Double): VSJoint {
      val var10002: java.lang.Long = this.shipId0
      val var10005: Vector3d = this.pose0.pos.mul(scale0, Vector3d())
      val var10003: VSJointPose = VSJointPose(var10005, this.pose0.rot)
      val var10004: java.lang.Long = this.shipId1
      val var10007: Vector3d = this.pose1.pos.mul(scale1, Vector3d())
      return VSRevoluteJoint(
         var10002,
         var10003,
         var10004,
         VSJointPose(var10007, this.pose1.rot),
         this.maxForceTorque,
         this.compliance,
         this.angularLimitPair,
         this.driveVelocity,
         this.driveForceLimit,
         this.driveGearRatio,
         this.driveFreeSpin
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

   public operator fun component7(): AngularLimitPair? {
      return this.angularLimitPair
   }

   public operator fun component8(): org.valkyrienskies.core.internal.joints.VSRevoluteJoint.VSRevoluteDriveVelocity? {
      return this.driveVelocity
   }

   public operator fun component9(): Float? {
      return this.driveForceLimit
   }

   public operator fun component10(): Float? {
      return this.driveGearRatio
   }

   public operator fun component11(): Boolean? {
      return this.driveFreeSpin
   }

   public fun copy(
      shipId0: Long? = this.shipId0,
      pose0: VSJointPose = this.pose0,
      shipId1: Long? = this.shipId1,
      pose1: VSJointPose = this.pose1,
      maxForceTorque: VSJointMaxForceTorque? = this.maxForceTorque,
      compliance: Double = this.compliance,
      angularLimitPair: AngularLimitPair? = this.angularLimitPair,
      driveVelocity: org.valkyrienskies.core.internal.joints.VSRevoluteJoint.VSRevoluteDriveVelocity? = this.driveVelocity,
      driveForceLimit: Float? = this.driveForceLimit,
      driveGearRatio: Float? = this.driveGearRatio,
      driveFreeSpin: Boolean? = this.driveFreeSpin
   ): VSRevoluteJoint {
      return VSRevoluteJoint(
         shipId0, pose0, shipId1, pose1, maxForceTorque, compliance, angularLimitPair, driveVelocity, driveForceLimit, driveGearRatio, driveFreeSpin
      )
   }

   public override fun toString(): String {
      return "VSRevoluteJoint(shipId0=${this.shipId0}, pose0=${this.pose0}, shipId1=${this.shipId1}, pose1=${this.pose1}, maxForceTorque=${this.maxForceTorque}, compliance=${this.compliance}, angularLimitPair=${this.angularLimitPair}, driveVelocity=${this.driveVelocity}, driveForceLimit=${this.driveForceLimit}, driveGearRatio=${this.driveGearRatio}, driveFreeSpin=${this.driveFreeSpin})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              (
                                                                                       (if (this.shipId0 == null) 0 else this.shipId0.hashCode()) * 31
                                                                                          + this.pose0.hashCode()
                                                                                    )
                                                                                    * 31
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
                                             + (if (this.angularLimitPair == null) 0 else this.angularLimitPair.hashCode())
                                       )
                                       * 31
                                    + (if (this.driveVelocity == null) 0 else this.driveVelocity.hashCode())
                              )
                              * 31
                           + (if (this.driveForceLimit == null) 0 else this.driveForceLimit.hashCode())
                     )
                     * 31
                  + (if (this.driveGearRatio == null) 0 else this.driveGearRatio.hashCode())
            )
            * 31
         + (if (this.driveFreeSpin == null) 0 else this.driveFreeSpin.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label82@
      if (this === other) {
         return true
      } else {
         return other is VSRevoluteJoint
            && this.shipId0 == (other as VSRevoluteJoint).shipId0
            && this.pose0 == (other as VSRevoluteJoint).pose0
            && this.shipId1 == (other as VSRevoluteJoint).shipId1
            && this.pose1 == (other as VSRevoluteJoint).pose1
            && this.maxForceTorque == (other as VSRevoluteJoint).maxForceTorque
            && java.lang.Double.compare(this.compliance, (other as VSRevoluteJoint).compliance) == 0
            && this.angularLimitPair == (other as VSRevoluteJoint).angularLimitPair
            && this.driveVelocity == (other as VSRevoluteJoint).driveVelocity
            && this.driveForceLimit == (other as VSRevoluteJoint).driveForceLimit
            && this.driveGearRatio == (other as VSRevoluteJoint).driveGearRatio
            && this.driveFreeSpin == (other as VSRevoluteJoint).driveFreeSpin
         }
   }

   public data class VSRevoluteDriveVelocity(velocity: Float, autoWake: Boolean = true) {
      public final val velocity: Float
      public final val autoWake: Boolean

      init {
         this.velocity = velocity
         this.autoWake = autoWake
      }

      public operator fun component1(): Float {
         return this.velocity
      }

      public operator fun component2(): Boolean {
         return this.autoWake
      }

      public fun copy(velocity: Float = this.velocity, autoWake: Boolean = this.autoWake): org.valkyrienskies.core.internal.joints.VSRevoluteJoint.VSRevoluteDriveVelocity {
         return VSRevoluteJoint.VSRevoluteDriveVelocity(velocity, autoWake)
      }

      public override fun toString(): String {
         return "VSRevoluteDriveVelocity(velocity=${this.velocity}, autoWake=${this.autoWake})"
      }

      public override fun hashCode(): Int {
         return java.lang.Float.hashCode(this.velocity) * 31 + java.lang.Boolean.hashCode(this.autoWake)
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is VSRevoluteJoint.VSRevoluteDriveVelocity
               && java.lang.Float.compare(this.velocity, (other as VSRevoluteJoint.VSRevoluteDriveVelocity).velocity) == 0
               && this.autoWake == (other as VSRevoluteJoint.VSRevoluteDriveVelocity).autoWake
            }
      }
   }
}
