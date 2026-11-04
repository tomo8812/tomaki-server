package org.valkyrienskies.core.impl.shadow

public data class FB internal constructor(p0: Long, p1: Long) {
   public final val a: Long
   public final val b: Long

   init {
      this.b = var1
      this.c = var3
   }

   public operator fun c(): Long {
      return this.b
   }

   public operator fun d(): Long {
      return this.c
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: FB, var1: Long, var3: Long, var5: Int): FB {
      if ((var5 and 1) != 0) {
         var1 = var0.b
      }

      if ((var5 and 2) != 0) {
         var3 = var0.c
      }

      FB(var1, var3)
   }

   public override fun toString(): String {
      return "DisabledCollisionPair(ship0Id=${this.b}, ship1Id=${this.c})"
   }

   public override fun hashCode(): Int {
      return java.lang.Long.hashCode(this.b) * 31 + java.lang.Long.hashCode(this.c)
   }

   public override operator fun equals(p0: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is FB && this.b == (other as FB).b && this.c == (other as FB).c
      }
   }

   public companion object a {
      @JvmStatic
      internal fun a(p0: Long, p1: Long): FB {
         return if (var0 < var2) FB(var0, var2) else FB(var2, var0)
      }
   }
}
