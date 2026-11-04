package org.valkyrienskies.core.impl.shadow

public enum class IG(p0: Int) {
   GAUSS_SEIDEL(0),
   JACOBI(1),
   COLORED_GAUSS_SEIDEL(2);

   public final val solverType: Int

   init {
      this.solverType = var3
   }

   @JvmStatic
   @JvmSynthetic
   fun a(): Array<IG> {
      val var0: Array<IG>
      (var0 = arrayOfNulls(3))[0] = GAUSS_SEIDEL
      var0[1] = JACOBI
      var0[2] = COLORED_GAUSS_SEIDEL
      var0
   }

   @JvmStatic
   fun {
      val var0: Array<IG>
      (var0 = arrayOfNulls(3))[0] = GAUSS_SEIDEL
      var0[1] = JACOBI
      var0[2] = COLORED_GAUSS_SEIDEL
   }
}
