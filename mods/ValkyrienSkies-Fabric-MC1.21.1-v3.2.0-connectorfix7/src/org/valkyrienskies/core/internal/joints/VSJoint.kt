package org.valkyrienskies.core.internal.joints

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.joml.Vector3dc
import org.valkyrienskies.core.api.util.PhysTickOnly

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes([@JsonSubTypes.Type(value = VSFixedJoint::class, name = "fixed"), @JsonSubTypes.Type(value = VSDistanceJoint::class, name = "distance"), @JsonSubTypes.Type(value = VSPrismaticJoint::class, name = "prismatic"), @JsonSubTypes.Type(value = VSSphericalJoint::class, name = "spherical"), @JsonSubTypes.Type(value = VSRevoluteJoint::class, name = "revolute"), @JsonSubTypes.Type(value = VSGearJoint::class, name = "gear"), @JsonSubTypes.Type(value = VSRackAndPinionJoint::class, name = "rack_and_pinion"), @JsonSubTypes.Type(value = VSD6Joint::class, name = "d6")])
@PhysTickOnly
public interface VSJoint {
   @JvmStatic
   VSJoint.Companion Companion = VSJoint.Companion.$$INSTANCE;

   public val shipId0: Long?

   public val pose0: VSJointPose

   public val shipId1: Long?

   public val pose1: VSJointPose

   public val maxForceTorque: VSJointMaxForceTorque?

   public val jointType: VSJointType

   public val compliance: Double

   public abstract fun offsetLocalPositions(offset0: Vector3dc, offset1: Vector3dc): VSJoint {
   }

   public abstract fun scaleLocalPositions(scale0: Double, scale1: Double): VSJoint {
   }

   public var shouldBeSerialized: Boolean

   public open fun serialized(): VSJoint {
      this.shouldBeSerialized = true
      return this
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      VSJoint.Companion $$INSTANCE = VSJoint.Companion();
      public const val DEFAULT_COMPLIANCE: Double = 1.0E-10
   }
}
