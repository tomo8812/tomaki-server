package org.valkyrienskies.core.internal.joints

import java.util.EnumMap
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3fc
import org.valkyrienskies.core.api.util.PhysTickOnly

@PhysTickOnly
@UnavailableInKrunch
public data class VSD6Joint(shipId0: Long?,
      pose0: VSJointPose,
      shipId1: Long?,
      pose1: VSJointPose,
      maxForceTorque: VSJointMaxForceTorque? = null,
      compliance: Double = 1.0E-10,
      motions: EnumMap<org.valkyrienskies.core.internal.joints.VSD6Joint.D6Axis, org.valkyrienskies.core.internal.joints.VSD6Joint.D6Motion>? = null,
      distanceLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimit? = null,
      linearLimits: EnumMap<org.valkyrienskies.core.internal.joints.VSD6Joint.D6Axis, org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimitPair>? = null,
      twistLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.AngularLimitPair? = null,
      swingLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.LimitCone? = null,
      pyramidSwingLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.LimitPyramid? = null,
      drives: EnumMap<org.valkyrienskies.core.internal.joints.VSD6Joint.D6Drive, org.valkyrienskies.core.internal.joints.VSD6Joint.D6JointDrive>? = null,
      drivePosition: org.valkyrienskies.core.internal.joints.VSD6Joint.DrivePosition? = null,
      driveVelocity: org.valkyrienskies.core.internal.joints.VSD6Joint.DriveVelocity? = null
   ) :
   VSJoint {
   public open val shipId0: Long?
   public open val pose0: VSJointPose
   public open val shipId1: Long?
   public open val pose1: VSJointPose
   public open val maxForceTorque: VSJointMaxForceTorque?
   public open val compliance: Double
   public final val motions: EnumMap<org.valkyrienskies.core.internal.joints.VSD6Joint.D6Axis, org.valkyrienskies.core.internal.joints.VSD6Joint.D6Motion>?
   public final val distanceLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimit?
   public final val linearLimits: EnumMap<
      org.valkyrienskies.core.internal.joints.VSD6Joint.D6Axis,
      org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimitPair
   >?
   public final val twistLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.AngularLimitPair?
   public final val swingLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.LimitCone?
   public final val pyramidSwingLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.LimitPyramid?
   public final val drives: EnumMap<org.valkyrienskies.core.internal.joints.VSD6Joint.D6Drive, org.valkyrienskies.core.internal.joints.VSD6Joint.D6JointDrive>?
   public final val drivePosition: org.valkyrienskies.core.internal.joints.VSD6Joint.DrivePosition?
   public final val driveVelocity: org.valkyrienskies.core.internal.joints.VSD6Joint.DriveVelocity?
   public open var shouldBeSerialized: Boolean
   public open val jointType: VSJointType

   init {
      this.shipId0 = shipId0
      this.pose0 = pose0
      this.shipId1 = shipId1
      this.pose1 = pose1
      this.maxForceTorque = maxForceTorque
      this.compliance = compliance
      this.motions = motions
      this.distanceLimit = distanceLimit
      this.linearLimits = linearLimits
      this.twistLimit = twistLimit
      this.swingLimit = swingLimit
      this.pyramidSwingLimit = pyramidSwingLimit
      this.drives = drives
      this.drivePosition = drivePosition
      this.driveVelocity = driveVelocity
      this.jointType = VSJointType.D6
   }

   public override fun offsetLocalPositions(offset0: Vector3dc, offset1: Vector3dc): VSJoint {
      val var10002: java.lang.Long = this.shipId0
      val var10005: Vector3d = this.pose0.pos.add(offset0, Vector3d())
      val var10003: VSJointPose = VSJointPose(var10005, this.pose0.rot)
      val var10004: java.lang.Long = this.shipId1
      val var10007: Vector3d = this.pose1.pos.add(offset1, Vector3d())
      return VSD6Joint(
         var10002,
         var10003,
         var10004,
         VSJointPose(var10007, this.pose1.rot),
         this.maxForceTorque,
         this.compliance,
         this.motions,
         this.distanceLimit,
         this.linearLimits,
         this.twistLimit,
         this.swingLimit,
         this.pyramidSwingLimit,
         this.drives,
         this.drivePosition,
         this.driveVelocity
      )
   }

   public override fun scaleLocalPositions(scale0: Double, scale1: Double): VSJoint {
      val var10002: java.lang.Long = this.shipId0
      val var10005: Vector3d = this.pose0.pos.mul(scale0, Vector3d())
      val var10003: VSJointPose = VSJointPose(var10005, this.pose0.rot)
      val var10004: java.lang.Long = this.shipId1
      val var10007: Vector3d = this.pose1.pos.mul(scale1, Vector3d())
      return VSD6Joint(
         var10002,
         var10003,
         var10004,
         VSJointPose(var10007, this.pose1.rot),
         this.maxForceTorque,
         this.compliance,
         this.motions,
         this.distanceLimit,
         this.linearLimits,
         this.twistLimit,
         this.swingLimit,
         this.pyramidSwingLimit,
         this.drives,
         this.drivePosition,
         this.driveVelocity
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

   public operator fun component7(): EnumMap<
         org.valkyrienskies.core.internal.joints.VSD6Joint.D6Axis,
         org.valkyrienskies.core.internal.joints.VSD6Joint.D6Motion
      >? {
      return this.motions
   }

   public operator fun component8(): org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimit? {
      return this.distanceLimit
   }

   public operator fun component9(): EnumMap<
         org.valkyrienskies.core.internal.joints.VSD6Joint.D6Axis,
         org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimitPair
      >? {
      return this.linearLimits
   }

   public operator fun component10(): org.valkyrienskies.core.internal.joints.VSD6Joint.AngularLimitPair? {
      return this.twistLimit
   }

   public operator fun component11(): org.valkyrienskies.core.internal.joints.VSD6Joint.LimitCone? {
      return this.swingLimit
   }

   public operator fun component12(): org.valkyrienskies.core.internal.joints.VSD6Joint.LimitPyramid? {
      return this.pyramidSwingLimit
   }

   public operator fun component13(): EnumMap<
         org.valkyrienskies.core.internal.joints.VSD6Joint.D6Drive,
         org.valkyrienskies.core.internal.joints.VSD6Joint.D6JointDrive
      >? {
      return this.drives
   }

   public operator fun component14(): org.valkyrienskies.core.internal.joints.VSD6Joint.DrivePosition? {
      return this.drivePosition
   }

   public operator fun component15(): org.valkyrienskies.core.internal.joints.VSD6Joint.DriveVelocity? {
      return this.driveVelocity
   }

   public fun copy(
      shipId0: Long? = this.shipId0,
      pose0: VSJointPose = this.pose0,
      shipId1: Long? = this.shipId1,
      pose1: VSJointPose = this.pose1,
      maxForceTorque: VSJointMaxForceTorque? = this.maxForceTorque,
      compliance: Double = this.compliance,
      motions: EnumMap<org.valkyrienskies.core.internal.joints.VSD6Joint.D6Axis, org.valkyrienskies.core.internal.joints.VSD6Joint.D6Motion>? = this.motions,
      distanceLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimit? = this.distanceLimit,
      linearLimits: EnumMap<org.valkyrienskies.core.internal.joints.VSD6Joint.D6Axis, org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimitPair>? = this.linearLimits,
      twistLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.AngularLimitPair? = this.twistLimit,
      swingLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.LimitCone? = this.swingLimit,
      pyramidSwingLimit: org.valkyrienskies.core.internal.joints.VSD6Joint.LimitPyramid? = this.pyramidSwingLimit,
      drives: EnumMap<org.valkyrienskies.core.internal.joints.VSD6Joint.D6Drive, org.valkyrienskies.core.internal.joints.VSD6Joint.D6JointDrive>? = this.drives,
      drivePosition: org.valkyrienskies.core.internal.joints.VSD6Joint.DrivePosition? = this.drivePosition,
      driveVelocity: org.valkyrienskies.core.internal.joints.VSD6Joint.DriveVelocity? = this.driveVelocity
   ): VSD6Joint {
      return VSD6Joint(
         shipId0,
         pose0,
         shipId1,
         pose1,
         maxForceTorque,
         compliance,
         motions,
         distanceLimit,
         linearLimits,
         twistLimit,
         swingLimit,
         pyramidSwingLimit,
         drives,
         drivePosition,
         driveVelocity
      )
   }

   public override fun toString(): String {
      return "VSD6Joint(shipId0=${this.shipId0}, pose0=${this.pose0}, shipId1=${this.shipId1}, pose1=${this.pose1}, maxForceTorque=${this.maxForceTorque}, compliance=${this.compliance}, motions=${this.motions}, distanceLimit=${this.distanceLimit}, linearLimits=${this.linearLimits}, twistLimit=${this.twistLimit}, swingLimit=${this.swingLimit}, pyramidSwingLimit=${this.pyramidSwingLimit}, drives=${this.drives}, drivePosition=${this.drivePosition}, driveVelocity=${this.driveVelocity})"
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
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           (
                                                                                                                                    if (this.shipId0 == null)
                                                                                                                                       0
                                                                                                                                       else
                                                                                                                                       this.shipId0.hashCode()
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + this.pose0.hashCode()
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + (
                                                                                                                        if (this.shipId1 == null)
                                                                                                                           0
                                                                                                                           else
                                                                                                                           this.shipId1.hashCode()
                                                                                                                     )
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this.pose1.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + (
                                                                                                      if (this.maxForceTorque == null)
                                                                                                         0
                                                                                                         else
                                                                                                         this.maxForceTorque.hashCode()
                                                                                                   )
                                                                                             )
                                                                                             * 31
                                                                                          + java.lang.Double.hashCode(this.compliance)
                                                                                    )
                                                                                    * 31
                                                                                 + (if (this.motions == null) 0 else this.motions.hashCode())
                                                                           )
                                                                           * 31
                                                                        + (if (this.distanceLimit == null) 0 else this.distanceLimit.hashCode())
                                                                  )
                                                                  * 31
                                                               + (if (this.linearLimits == null) 0 else this.linearLimits.hashCode())
                                                         )
                                                         * 31
                                                      + (if (this.twistLimit == null) 0 else this.twistLimit.hashCode())
                                                )
                                                * 31
                                             + (if (this.swingLimit == null) 0 else this.swingLimit.hashCode())
                                       )
                                       * 31
                                    + (if (this.pyramidSwingLimit == null) 0 else this.pyramidSwingLimit.hashCode())
                              )
                              * 31
                           + (if (this.drives == null) 0 else this.drives.hashCode())
                     )
                     * 31
                  + (if (this.drivePosition == null) 0 else this.drivePosition.hashCode())
            )
            * 31
         + (if (this.driveVelocity == null) 0 else this.driveVelocity.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label106@
      if (this === other) {
         return true
      } else {
         return other is VSD6Joint
            && this.shipId0 == (other as VSD6Joint).shipId0
            && this.pose0 == (other as VSD6Joint).pose0
            && this.shipId1 == (other as VSD6Joint).shipId1
            && this.pose1 == (other as VSD6Joint).pose1
            && this.maxForceTorque == (other as VSD6Joint).maxForceTorque
            && java.lang.Double.compare(this.compliance, (other as VSD6Joint).compliance) == 0
            && this.motions == (other as VSD6Joint).motions
            && this.distanceLimit == (other as VSD6Joint).distanceLimit
            && this.linearLimits == (other as VSD6Joint).linearLimits
            && this.twistLimit == (other as VSD6Joint).twistLimit
            && this.swingLimit == (other as VSD6Joint).swingLimit
            && this.pyramidSwingLimit == (other as VSD6Joint).pyramidSwingLimit
            && this.drives == (other as VSD6Joint).drives
            && this.drivePosition == (other as VSD6Joint).drivePosition
            && this.driveVelocity == (other as VSD6Joint).driveVelocity
         }
   }

   public data class AngularLimitPair(lowerLimit: Float,
      upperLimit: Float,
      restitution: Float? = null,
      bounceThreshold: Float? = null,
      stiffness: Float? = null,
      damping: Float? = null
   ) {
      public final val lowerLimit: Float
      public final val upperLimit: Float
      public final val restitution: Float?
      public final val bounceThreshold: Float?
      public final val stiffness: Float?
      public final val damping: Float?

      init {
         this.lowerLimit = lowerLimit
         this.upperLimit = upperLimit
         this.restitution = restitution
         this.bounceThreshold = bounceThreshold
         this.stiffness = stiffness
         this.damping = damping
      }

      public operator fun component1(): Float {
         return this.lowerLimit
      }

      public operator fun component2(): Float {
         return this.upperLimit
      }

      public operator fun component3(): Float? {
         return this.restitution
      }

      public operator fun component4(): Float? {
         return this.bounceThreshold
      }

      public operator fun component5(): Float? {
         return this.stiffness
      }

      public operator fun component6(): Float? {
         return this.damping
      }

      public fun copy(
         lowerLimit: Float = this.lowerLimit,
         upperLimit: Float = this.upperLimit,
         restitution: Float? = this.restitution,
         bounceThreshold: Float? = this.bounceThreshold,
         stiffness: Float? = this.stiffness,
         damping: Float? = this.damping
      ): org.valkyrienskies.core.internal.joints.VSD6Joint.AngularLimitPair {
         return VSD6Joint.AngularLimitPair(lowerLimit, upperLimit, restitution, bounceThreshold, stiffness, damping)
      }

      public override fun toString(): String {
         return "AngularLimitPair(lowerLimit=${this.lowerLimit}, upperLimit=${this.upperLimit}, restitution=${this.restitution}, bounceThreshold=${this.bounceThreshold}, stiffness=${this.stiffness}, damping=${this.damping})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (java.lang.Float.hashCode(this.lowerLimit) * 31 + java.lang.Float.hashCode(this.upperLimit)) * 31
                                       + (if (this.restitution == null) 0 else this.restitution.hashCode())
                                 )
                                 * 31
                              + (if (this.bounceThreshold == null) 0 else this.bounceThreshold.hashCode())
                        )
                        * 31
                     + (if (this.stiffness == null) 0 else this.stiffness.hashCode())
               )
               * 31
            + (if (this.damping == null) 0 else this.damping.hashCode())
         }

      public override operator fun equals(other: Any?): Boolean {
         label52@
         if (this === other) {
            return true
         } else {
            return other is VSD6Joint.AngularLimitPair
               && java.lang.Float.compare(this.lowerLimit, (other as VSD6Joint.AngularLimitPair).lowerLimit) == 0
               && java.lang.Float.compare(this.upperLimit, (other as VSD6Joint.AngularLimitPair).upperLimit) == 0
               && this.restitution == (other as VSD6Joint.AngularLimitPair).restitution
               && this.bounceThreshold == (other as VSD6Joint.AngularLimitPair).bounceThreshold
               && this.stiffness == (other as VSD6Joint.AngularLimitPair).stiffness
               && this.damping == (other as VSD6Joint.AngularLimitPair).damping
            }
      }
   }

   public enum class D6Axis {
      X,
      Y,
      Z,
      TWIST,
      SWING1,
      SWING2;

      @JvmStatic
      @JvmSynthetic
      fun `$values`(): Array<VSD6Joint.D6Axis> {
         arrayOf(X, Y, Z, TWIST, SWING1, SWING2)
      }
   }

   public enum class D6Drive {
      X,
      Y,
      Z,
      SWING,
      TWIST,
      SLERP;

      @JvmStatic
      @JvmSynthetic
      fun `$values`(): Array<VSD6Joint.D6Drive> {
         arrayOf(X, Y, Z, SWING, TWIST, SLERP)
      }
   }

   public data class D6JointDrive(driveStiffness: Float, driveDamping: Float, driveForceLimit: Float, isAcceleration: Boolean = false) {
      public final val driveStiffness: Float
      public final val driveDamping: Float
      public final val driveForceLimit: Float
      public final val isAcceleration: Boolean

      init {
         this.driveStiffness = driveStiffness
         this.driveDamping = driveDamping
         this.driveForceLimit = driveForceLimit
         this.isAcceleration = isAcceleration
      }

      public operator fun component1(): Float {
         return this.driveStiffness
      }

      public operator fun component2(): Float {
         return this.driveDamping
      }

      public operator fun component3(): Float {
         return this.driveForceLimit
      }

      public operator fun component4(): Boolean {
         return this.isAcceleration
      }

      public fun copy(
         driveStiffness: Float = this.driveStiffness,
         driveDamping: Float = this.driveDamping,
         driveForceLimit: Float = this.driveForceLimit,
         isAcceleration: Boolean = this.isAcceleration
      ): org.valkyrienskies.core.internal.joints.VSD6Joint.D6JointDrive {
         return VSD6Joint.D6JointDrive(driveStiffness, driveDamping, driveForceLimit, isAcceleration)
      }

      public override fun toString(): String {
         return "D6JointDrive(driveStiffness=${this.driveStiffness}, driveDamping=${this.driveDamping}, driveForceLimit=${this.driveForceLimit}, isAcceleration=${this.isAcceleration})"
      }

      public override fun hashCode(): Int {
         return (
                  (java.lang.Float.hashCode(this.driveStiffness) * 31 + java.lang.Float.hashCode(this.driveDamping)) * 31
                     + java.lang.Float.hashCode(this.driveForceLimit)
               )
               * 31
            + java.lang.Boolean.hashCode(this.isAcceleration)
         }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is VSD6Joint.D6JointDrive
               && java.lang.Float.compare(this.driveStiffness, (other as VSD6Joint.D6JointDrive).driveStiffness) == 0
               && java.lang.Float.compare(this.driveDamping, (other as VSD6Joint.D6JointDrive).driveDamping) == 0
               && java.lang.Float.compare(this.driveForceLimit, (other as VSD6Joint.D6JointDrive).driveForceLimit) == 0
               && this.isAcceleration == (other as VSD6Joint.D6JointDrive).isAcceleration
            }
      }
   }

   public enum class D6Motion {
      LOCKED,
      LIMITED,
      FREE;

      @JvmStatic
      @JvmSynthetic
      fun `$values`(): Array<VSD6Joint.D6Motion> {
         arrayOf(LOCKED, LIMITED, FREE)
      }
   }

   public data class DrivePosition(pose: VSJointPose, autoWake: Boolean = true) {
      public final val pose: VSJointPose
      public final val autoWake: Boolean

      init {
         this.pose = pose
         this.autoWake = autoWake
      }

      public operator fun component1(): VSJointPose {
         return this.pose
      }

      public operator fun component2(): Boolean {
         return this.autoWake
      }

      public fun copy(pose: VSJointPose = this.pose, autoWake: Boolean = this.autoWake): org.valkyrienskies.core.internal.joints.VSD6Joint.DrivePosition {
         return VSD6Joint.DrivePosition(pose, autoWake)
      }

      public override fun toString(): String {
         return "DrivePosition(pose=${this.pose}, autoWake=${this.autoWake})"
      }

      public override fun hashCode(): Int {
         return this.pose.hashCode() * 31 + java.lang.Boolean.hashCode(this.autoWake)
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is VSD6Joint.DrivePosition
               && this.pose == (other as VSD6Joint.DrivePosition).pose
               && this.autoWake == (other as VSD6Joint.DrivePosition).autoWake
            }
      }
   }

   public data class DriveVelocity(linear: Vector3fc, angular: Vector3fc, autoWake: Boolean = true) {
      public final val linear: Vector3fc
      public final val angular: Vector3fc
      public final val autoWake: Boolean

      init {
         this.linear = linear
         this.angular = angular
         this.autoWake = autoWake
      }

      public operator fun component1(): Vector3fc {
         return this.linear
      }

      public operator fun component2(): Vector3fc {
         return this.angular
      }

      public operator fun component3(): Boolean {
         return this.autoWake
      }

      public fun copy(linear: Vector3fc = this.linear, angular: Vector3fc = this.angular, autoWake: Boolean = this.autoWake): org.valkyrienskies.core.internal.joints.VSD6Joint.DriveVelocity {
         return VSD6Joint.DriveVelocity(linear, angular, autoWake)
      }

      public override fun toString(): String {
         return "DriveVelocity(linear=${this.linear}, angular=${this.angular}, autoWake=${this.autoWake})"
      }

      public override fun hashCode(): Int {
         return (this.linear.hashCode() * 31 + this.angular.hashCode()) * 31 + java.lang.Boolean.hashCode(this.autoWake)
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is VSD6Joint.DriveVelocity
               && this.linear == (other as VSD6Joint.DriveVelocity).linear
               && this.angular == (other as VSD6Joint.DriveVelocity).angular
               && this.autoWake == (other as VSD6Joint.DriveVelocity).autoWake
            }
      }
   }

   public data class Hinges(hinge0: Int, hinge1: Int) {
      public final val hinge0: Int
      public final val hinge1: Int

      init {
         this.hinge0 = hinge0
         this.hinge1 = hinge1
      }

      public operator fun component1(): Int {
         return this.hinge0
      }

      public operator fun component2(): Int {
         return this.hinge1
      }

      public fun copy(hinge0: Int = this.hinge0, hinge1: Int = this.hinge1): org.valkyrienskies.core.internal.joints.VSD6Joint.Hinges {
         return VSD6Joint.Hinges(hinge0, hinge1)
      }

      public override fun toString(): String {
         return "Hinges(hinge0=${this.hinge0}, hinge1=${this.hinge1})"
      }

      public override fun hashCode(): Int {
         return Integer.hashCode(this.hinge0) * 31 + Integer.hashCode(this.hinge1)
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is VSD6Joint.Hinges && this.hinge0 == (other as VSD6Joint.Hinges).hinge0 && this.hinge1 == (other as VSD6Joint.Hinges).hinge1
         }
      }
   }

   public data class LimitCone(yLimitAngle: Float,
      zLimitAngle: Float,
      restitution: Float? = null,
      bounceThreshold: Float? = null,
      stiffness: Float? = null,
      damping: Float? = null
   ) {
      public final val yLimitAngle: Float
      public final val zLimitAngle: Float
      public final val restitution: Float?
      public final val bounceThreshold: Float?
      public final val stiffness: Float?
      public final val damping: Float?

      init {
         this.yLimitAngle = yLimitAngle
         this.zLimitAngle = zLimitAngle
         this.restitution = restitution
         this.bounceThreshold = bounceThreshold
         this.stiffness = stiffness
         this.damping = damping
      }

      public operator fun component1(): Float {
         return this.yLimitAngle
      }

      public operator fun component2(): Float {
         return this.zLimitAngle
      }

      public operator fun component3(): Float? {
         return this.restitution
      }

      public operator fun component4(): Float? {
         return this.bounceThreshold
      }

      public operator fun component5(): Float? {
         return this.stiffness
      }

      public operator fun component6(): Float? {
         return this.damping
      }

      public fun copy(
         yLimitAngle: Float = this.yLimitAngle,
         zLimitAngle: Float = this.zLimitAngle,
         restitution: Float? = this.restitution,
         bounceThreshold: Float? = this.bounceThreshold,
         stiffness: Float? = this.stiffness,
         damping: Float? = this.damping
      ): org.valkyrienskies.core.internal.joints.VSD6Joint.LimitCone {
         return VSD6Joint.LimitCone(yLimitAngle, zLimitAngle, restitution, bounceThreshold, stiffness, damping)
      }

      public override fun toString(): String {
         return "LimitCone(yLimitAngle=${this.yLimitAngle}, zLimitAngle=${this.zLimitAngle}, restitution=${this.restitution}, bounceThreshold=${this.bounceThreshold}, stiffness=${this.stiffness}, damping=${this.damping})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (java.lang.Float.hashCode(this.yLimitAngle) * 31 + java.lang.Float.hashCode(this.zLimitAngle)) * 31
                                       + (if (this.restitution == null) 0 else this.restitution.hashCode())
                                 )
                                 * 31
                              + (if (this.bounceThreshold == null) 0 else this.bounceThreshold.hashCode())
                        )
                        * 31
                     + (if (this.stiffness == null) 0 else this.stiffness.hashCode())
               )
               * 31
            + (if (this.damping == null) 0 else this.damping.hashCode())
         }

      public override operator fun equals(other: Any?): Boolean {
         label52@
         if (this === other) {
            return true
         } else {
            return other is VSD6Joint.LimitCone
               && java.lang.Float.compare(this.yLimitAngle, (other as VSD6Joint.LimitCone).yLimitAngle) == 0
               && java.lang.Float.compare(this.zLimitAngle, (other as VSD6Joint.LimitCone).zLimitAngle) == 0
               && this.restitution == (other as VSD6Joint.LimitCone).restitution
               && this.bounceThreshold == (other as VSD6Joint.LimitCone).bounceThreshold
               && this.stiffness == (other as VSD6Joint.LimitCone).stiffness
               && this.damping == (other as VSD6Joint.LimitCone).damping
            }
      }
   }

   public data class LimitPyramid(yLimitAngleMin: Float,
      yLimitAngleMax: Float,
      zLimitAngleMin: Float,
      zLimitAngleMax: Float,
      restitution: Float? = null,
      bounceThreshold: Float? = null,
      stiffness: Float? = null,
      damping: Float? = null
   ) {
      public final val yLimitAngleMin: Float
      public final val yLimitAngleMax: Float
      public final val zLimitAngleMin: Float
      public final val zLimitAngleMax: Float
      public final val restitution: Float?
      public final val bounceThreshold: Float?
      public final val stiffness: Float?
      public final val damping: Float?

      init {
         this.yLimitAngleMin = yLimitAngleMin
         this.yLimitAngleMax = yLimitAngleMax
         this.zLimitAngleMin = zLimitAngleMin
         this.zLimitAngleMax = zLimitAngleMax
         this.restitution = restitution
         this.bounceThreshold = bounceThreshold
         this.stiffness = stiffness
         this.damping = damping
      }

      public operator fun component1(): Float {
         return this.yLimitAngleMin
      }

      public operator fun component2(): Float {
         return this.yLimitAngleMax
      }

      public operator fun component3(): Float {
         return this.zLimitAngleMin
      }

      public operator fun component4(): Float {
         return this.zLimitAngleMax
      }

      public operator fun component5(): Float? {
         return this.restitution
      }

      public operator fun component6(): Float? {
         return this.bounceThreshold
      }

      public operator fun component7(): Float? {
         return this.stiffness
      }

      public operator fun component8(): Float? {
         return this.damping
      }

      public fun copy(
         yLimitAngleMin: Float = this.yLimitAngleMin,
         yLimitAngleMax: Float = this.yLimitAngleMax,
         zLimitAngleMin: Float = this.zLimitAngleMin,
         zLimitAngleMax: Float = this.zLimitAngleMax,
         restitution: Float? = this.restitution,
         bounceThreshold: Float? = this.bounceThreshold,
         stiffness: Float? = this.stiffness,
         damping: Float? = this.damping
      ): org.valkyrienskies.core.internal.joints.VSD6Joint.LimitPyramid {
         return VSD6Joint.LimitPyramid(yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax, restitution, bounceThreshold, stiffness, damping)
      }

      public override fun toString(): String {
         return "LimitPyramid(yLimitAngleMin=${this.yLimitAngleMin}, yLimitAngleMax=${this.yLimitAngleMax}, zLimitAngleMin=${this.zLimitAngleMin}, zLimitAngleMax=${this.zLimitAngleMax}, restitution=${this.restitution}, bounceThreshold=${this.bounceThreshold}, stiffness=${this.stiffness}, damping=${this.damping})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      (java.lang.Float.hashCode(this.yLimitAngleMin) * 31 + java.lang.Float.hashCode(this.yLimitAngleMax)) * 31
                                                         + java.lang.Float.hashCode(this.zLimitAngleMin)
                                                   )
                                                   * 31
                                                + java.lang.Float.hashCode(this.zLimitAngleMax)
                                          )
                                          * 31
                                       + (if (this.restitution == null) 0 else this.restitution.hashCode())
                                 )
                                 * 31
                              + (if (this.bounceThreshold == null) 0 else this.bounceThreshold.hashCode())
                        )
                        * 31
                     + (if (this.stiffness == null) 0 else this.stiffness.hashCode())
               )
               * 31
            + (if (this.damping == null) 0 else this.damping.hashCode())
         }

      public override operator fun equals(other: Any?): Boolean {
         label64@
         if (this === other) {
            return true
         } else {
            return other is VSD6Joint.LimitPyramid
               && java.lang.Float.compare(this.yLimitAngleMin, (other as VSD6Joint.LimitPyramid).yLimitAngleMin) == 0
               && java.lang.Float.compare(this.yLimitAngleMax, (other as VSD6Joint.LimitPyramid).yLimitAngleMax) == 0
               && java.lang.Float.compare(this.zLimitAngleMin, (other as VSD6Joint.LimitPyramid).zLimitAngleMin) == 0
               && java.lang.Float.compare(this.zLimitAngleMax, (other as VSD6Joint.LimitPyramid).zLimitAngleMax) == 0
               && this.restitution == (other as VSD6Joint.LimitPyramid).restitution
               && this.bounceThreshold == (other as VSD6Joint.LimitPyramid).bounceThreshold
               && this.stiffness == (other as VSD6Joint.LimitPyramid).stiffness
               && this.damping == (other as VSD6Joint.LimitPyramid).damping
            }
      }
   }

   public data class LinearLimit(extent: Float, stiffness: Float? = null, damping: Float? = null) {
      public final val extent: Float
      public final val stiffness: Float?
      public final val damping: Float?

      init {
         this.extent = extent
         this.stiffness = stiffness
         this.damping = damping
      }

      public operator fun component1(): Float {
         return this.extent
      }

      public operator fun component2(): Float? {
         return this.stiffness
      }

      public operator fun component3(): Float? {
         return this.damping
      }

      public fun copy(extent: Float = this.extent, stiffness: Float? = this.stiffness, damping: Float? = this.damping): org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimit {
         return VSD6Joint.LinearLimit(extent, stiffness, damping)
      }

      public override fun toString(): String {
         return "LinearLimit(extent=${this.extent}, stiffness=${this.stiffness}, damping=${this.damping})"
      }

      public override fun hashCode(): Int {
         return (java.lang.Float.hashCode(this.extent) * 31 + (if (this.stiffness == null) 0 else this.stiffness.hashCode())) * 31
            + (if (this.damping == null) 0 else this.damping.hashCode())
         }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is VSD6Joint.LinearLimit
               && java.lang.Float.compare(this.extent, (other as VSD6Joint.LinearLimit).extent) == 0
               && this.stiffness == (other as VSD6Joint.LinearLimit).stiffness
               && this.damping == (other as VSD6Joint.LinearLimit).damping
            }
      }
   }

   public data class LinearLimitPair(lowerLimit: Float,
      upperLimit: Float,
      restitution: Float? = null,
      bounceThreshold: Float? = null,
      stiffness: Float? = null,
      damping: Float? = null
   ) {
      public final val lowerLimit: Float
      public final val upperLimit: Float
      public final val restitution: Float?
      public final val bounceThreshold: Float?
      public final val stiffness: Float?
      public final val damping: Float?

      init {
         this.lowerLimit = lowerLimit
         this.upperLimit = upperLimit
         this.restitution = restitution
         this.bounceThreshold = bounceThreshold
         this.stiffness = stiffness
         this.damping = damping
      }

      public operator fun component1(): Float {
         return this.lowerLimit
      }

      public operator fun component2(): Float {
         return this.upperLimit
      }

      public operator fun component3(): Float? {
         return this.restitution
      }

      public operator fun component4(): Float? {
         return this.bounceThreshold
      }

      public operator fun component5(): Float? {
         return this.stiffness
      }

      public operator fun component6(): Float? {
         return this.damping
      }

      public fun copy(
         lowerLimit: Float = this.lowerLimit,
         upperLimit: Float = this.upperLimit,
         restitution: Float? = this.restitution,
         bounceThreshold: Float? = this.bounceThreshold,
         stiffness: Float? = this.stiffness,
         damping: Float? = this.damping
      ): org.valkyrienskies.core.internal.joints.VSD6Joint.LinearLimitPair {
         return VSD6Joint.LinearLimitPair(lowerLimit, upperLimit, restitution, bounceThreshold, stiffness, damping)
      }

      public override fun toString(): String {
         return "LinearLimitPair(lowerLimit=${this.lowerLimit}, upperLimit=${this.upperLimit}, restitution=${this.restitution}, bounceThreshold=${this.bounceThreshold}, stiffness=${this.stiffness}, damping=${this.damping})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (java.lang.Float.hashCode(this.lowerLimit) * 31 + java.lang.Float.hashCode(this.upperLimit)) * 31
                                       + (if (this.restitution == null) 0 else this.restitution.hashCode())
                                 )
                                 * 31
                              + (if (this.bounceThreshold == null) 0 else this.bounceThreshold.hashCode())
                        )
                        * 31
                     + (if (this.stiffness == null) 0 else this.stiffness.hashCode())
               )
               * 31
            + (if (this.damping == null) 0 else this.damping.hashCode())
         }

      public override operator fun equals(other: Any?): Boolean {
         label52@
         if (this === other) {
            return true
         } else {
            return other is VSD6Joint.LinearLimitPair
               && java.lang.Float.compare(this.lowerLimit, (other as VSD6Joint.LinearLimitPair).lowerLimit) == 0
               && java.lang.Float.compare(this.upperLimit, (other as VSD6Joint.LinearLimitPair).upperLimit) == 0
               && this.restitution == (other as VSD6Joint.LinearLimitPair).restitution
               && this.bounceThreshold == (other as VSD6Joint.LinearLimitPair).bounceThreshold
               && this.stiffness == (other as VSD6Joint.LinearLimitPair).stiffness
               && this.damping == (other as VSD6Joint.LinearLimitPair).damping
            }
      }
   }
}
