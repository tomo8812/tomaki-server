package org.valkyrienskies.core.impl.shadow

public enum class FA {
   CREATE,
   UPDATE,
   DELETE;

   @JvmStatic
   @JvmSynthetic
   fun a(): Array<FA> {
      val var0: Array<FA>
      (var0 = arrayOfNulls(3))[0] = CREATE
      var0[1] = UPDATE
      var0[2] = DELETE
      var0
   }

   @JvmStatic
   fun {
      val var0: Array<FA>
      (var0 = arrayOfNulls(3))[0] = CREATE
      var0[1] = UPDATE
      var0[2] = DELETE
   }
}
