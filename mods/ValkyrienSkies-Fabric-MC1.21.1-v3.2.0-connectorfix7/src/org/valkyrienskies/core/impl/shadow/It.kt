package org.valkyrienskies.core.impl.shadow

public enum class It {
   EMPTY,
   DENSE,
   SPARSE,
   DELETE;

   @JvmStatic
   @JvmSynthetic
   fun a(): Array<It> {
      val var0: Array<It>
      (var0 = arrayOfNulls(4))[0] = EMPTY
      var0[1] = DENSE
      var0[2] = SPARSE
      var0[3] = DELETE
      var0
   }

   @JvmStatic
   fun {
      val var0: Array<It>
      (var0 = arrayOfNulls(4))[0] = EMPTY
      var0[1] = DENSE
      var0[2] = SPARSE
      var0[3] = DELETE
   }
}
