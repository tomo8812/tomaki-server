package org.valkyrienskies.core.impl.shadow

public data class Db(p0: Double, p1: Double) : Dc {
   public final var b: Double
   public final var a: Double

   init {
      this.b = var1
      this.c = var3
   }

   public open val d: Double
      public open get() {
         return this.b
      }


   public open val c: Double
      public open get() {
         return this.c
      }


   public operator fun e(): Double {
      return this.b
   }

   public operator fun f(): Double {
      return this.c
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: Db, var1: Double, var3: Double, var5: Int): Db {
      if ((var5 and 1) != 0) {
         var1 = var0.b
      }

      if ((var5 and 2) != 0) {
         var3 = var0.c
      }

      Db(var1, var3)
   }

   public override fun toString(): String {
      return "CollisionRange(_min=${this.b}, _max=${this.c})"
   }

   public override fun hashCode(): Int {
      return java.lang.Double.hashCode(this.b) * 31 + java.lang.Double.hashCode(this.c)
   }

   public override operator fun equals(p0: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is Db && java.lang.Double.compare(this.b, (other as Db).b) == 0 && java.lang.Double.compare(this.c, (other as Db).c) == 0
      }
   }

   public companion object a {
      @JvmStatic
      public fun a(p0: Double, p1: Double): Db {
         return Db(0.0, 0.0)
      }

      public fun a(): Db {
         val var3: Double = 0.0
         val var1: Double = 0.0
         return Db(0.0, 0.0)
      }
   }
}
