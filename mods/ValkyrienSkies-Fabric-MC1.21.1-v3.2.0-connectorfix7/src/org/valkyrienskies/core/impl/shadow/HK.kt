package org.valkyrienskies.core.impl.shadow

import kotlin.jvm.internal.DefaultConstructorMarker

public data class HK : HN {
   private Integer b;
   private Hf c;
   private Integer d;
   private Hf e;
   private HP f;
   private double g;
   private HO h;

   fun HK(var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: Double) {
      this.b = var1
      this.c = var2
      this.d = var3
      this.e = var4
      this.f = var5
      this.g = var6
      this.h = HO.FIXED
   }

   @JvmSynthetic
   fun HK(var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: Double, var8: Int, var9: DefaultConstructorMarker) {
      if ((var8 and 16) != 0) {
         var5 = null
      }

      if ((var8 and 32) != 0) {
         var6 = 1.0E-10
      }

      this(var1, var2, var3, var4, var5, var6)
   }

   @JvmName(name = "a")
   override final fun a(): Int {
      this.b
   }

   @JvmName(name = "b")
   override final fun b(): Hf {
      this.c
   }

   @JvmName(name = "c")
   override final fun c(): Int {
      this.d
   }

   @JvmName(name = "d")
   override final fun d(): Hf {
      this.e
   }

   @JvmName(name = "e")
   override final fun e(): HP {
      this.f
   }

   @JvmName(name = "o")
   override final fun o(): Double {
      this.g
   }

   @JvmName(name = "p")
   override final fun p(): HO {
      this.h
   }

   fun f(): Int {
      this.b
   }

   fun g(): Hf {
      this.c
   }

   fun h(): Int {
      this.d
   }

   fun i(): Hf {
      this.e
   }

   fun j(): HP {
      this.f
   }

   fun k(): Double {
      this.g
   }

   @JvmStatic
   fun a(var0: Int, var1: Hf, var2: Int, var3: Hf, var4: HP, var5: Double): HK {
      HK(var0, var1, var2, var3, var4, var5)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: HK, var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: Double, var8: Int): HK {
      if ((var8 and 1) != 0) {
         var1 = var0.b
      }

      if ((var8 and 2) != 0) {
         var2 = var0.c
      }

      if ((var8 and 4) != 0) {
         var3 = var0.d
      }

      if ((var8 and 8) != 0) {
         var4 = var0.e
      }

      if ((var8 and 16) != 0) {
         var5 = var0.f
      }

      if ((var8 and 32) != 0) {
         var6 = var0.g
      }

      HK(var1, var2, var3, var4, var5, var6)
   }

   public override fun toString(): String {
      return "HK(f=${this.b}, d=${this.c}, g=${this.d}, e=${this.e}, a=${this.f}, b=${this.g})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (((if (this.b == null) 0 else this.b.hashCode()) * 31 + this.c.hashCode()) * 31 + (if (this.d == null) 0 else this.d.hashCode())) * 31
                           + this.e.hashCode()
                     )
                     * 31
                  + (if (this.f == null) 0 else this.f.hashCode())
            )
            * 31
         + java.lang.Double.hashCode(this.g)
      }

   public override operator fun equals(other: Any?): Boolean {
      label52@
      if (this === other) {
         return true
      } else {
         return other is HK
            && this.b == (other as HK).b
            && this.c == (other as HK).c
            && this.d == (other as HK).d
            && this.e == (other as HK).e
            && this.f == (other as HK).f
            && this.g == (other as HK).g
         }
   }
}
