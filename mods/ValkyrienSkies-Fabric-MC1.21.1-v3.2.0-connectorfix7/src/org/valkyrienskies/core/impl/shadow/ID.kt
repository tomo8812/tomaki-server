package org.valkyrienskies.core.impl.shadow

public enum class ID(p0: Int) {
   KRUNCH_CLASSIC(0),
   KRUNCH_PHYSX(1),
   KRUNCH_JOLT(2);

   public final val backendType: Int

   init {
      this.backendType = var3
   }

   @JvmStatic
   @JvmSynthetic
   fun a(): Array<ID> {
      val var0: Array<ID>
      (var0 = arrayOfNulls(3))[0] = KRUNCH_CLASSIC
      var0[1] = KRUNCH_PHYSX
      var0[2] = KRUNCH_JOLT
      var0
   }

   @JvmStatic
   fun {
      val var0: Array<ID>
      (var0 = arrayOfNulls(3))[0] = KRUNCH_CLASSIC
      var0[1] = KRUNCH_PHYSX
      var0[2] = KRUNCH_JOLT
   }
}
