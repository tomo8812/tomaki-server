package org.valkyrienskies.core.impl.shadow

public enum class HO {
   FIXED,
   DISTANCE,
   SPHERICAL,
   REVOLUTE,
   PRISMATIC,
   GEAR,
   RACK_AND_PINION,
   D6;

   @JvmStatic
   @JvmSynthetic
   fun a(): Array<HO> {
      val var0: Array<HO>
      (var0 = arrayOfNulls(8))[0] = FIXED
      var0[1] = DISTANCE
      var0[2] = SPHERICAL
      var0[3] = REVOLUTE
      var0[4] = PRISMATIC
      var0[5] = GEAR
      var0[6] = RACK_AND_PINION
      var0[7] = D6
      var0
   }

   @JvmStatic
   fun {
      val var0: Array<HO>
      (var0 = arrayOfNulls(8))[0] = FIXED
      var0[1] = DISTANCE
      var0[2] = SPHERICAL
      var0[3] = REVOLUTE
      var0[4] = PRISMATIC
      var0[5] = GEAR
      var0[6] = RACK_AND_PINION
      var0[7] = D6
   }
}
