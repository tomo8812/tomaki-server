package org.valkyrienskies.core.internal.joints

public enum class VSJointType {
   FIXED,
   DISTANCE,
   PRISMATIC,
   SPHERICAL,
   REVOLUTE,
   GEAR,
   RACK_AND_PINION,
   D6;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<VSJointType> {
      arrayOf(FIXED, DISTANCE, PRISMATIC, SPHERICAL, REVOLUTE, GEAR, RACK_AND_PINION, D6)
   }
}
