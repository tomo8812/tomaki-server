package org.valkyrienskies.core.impl.shadow

import kotlin.jvm.internal.DefaultConstructorMarker

public data class HQ : HN {
   private Integer c;
   private Hf d;
   private Integer e;
   private Hf f;
   private HP g;
   public HI.l b;
   private double h;
   private HO i;

   fun HQ(var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: HI.l, var7: Double) {
      this.c = var1
      this.d = var2
      this.e = var3
      this.f = var4
      this.g = var5
      this.b = var6
      this.h = var7
      this.i = HO.PRISMATIC
   }

   @JvmSynthetic
   fun HQ(var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: HI.l, var7: Double, var9: Int, var10: DefaultConstructorMarker) {
      if ((var9 and 16) != 0) {
         var5 = null
      }

      if ((var9 and 32) != 0) {
         var6 = null
      }

      if ((var9 and 64) != 0) {
         var7 = 1.0E-10
      }

      this(var1, var2, var3, var4, var5, var6, var7)
   }

   @JvmName(name = "a")
   override final fun a(): Int {
      this.c
   }

   @JvmName(name = "b")
   override final fun b(): Hf {
      this.d
   }

   @JvmName(name = "c")
   override final fun c(): Int {
      this.e
   }

   @JvmName(name = "d")
   override final fun d(): Hf {
      this.f
   }

   @JvmName(name = "e")
   override final fun e(): HP {
      this.g
   }

   @JvmName(name = "f")
   fun f(): HI.l {
      this.b
   }

   @JvmName(name = "o")
   override final fun o(): Double {
      this.h
   }

   @JvmName(name = "p")
   override final fun p(): HO {
      this.i
   }

   fun g(): Int {
      this.c
   }

   fun h(): Hf {
      this.d
   }

   fun i(): Int {
      this.e
   }

   fun j(): Hf {
      this.f
   }

   fun k(): HP {
      this.g
   }

   fun l(): HI.l {
      this.b
   }

   fun m(): Double {
      this.h
   }

   @JvmStatic
   fun a(var0: Int, var1: Hf, var2: Int, var3: Hf, var4: HP, var5: HI.l, var6: Double): HQ {
      HQ(var0, var1, var2, var3, var4, var5, var6)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: HQ, var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: HI.l, var7: Double, var9: Int): HQ {
      if ((var9 and 1) != 0) {
         var1 = var0.c
      }

      if ((var9 and 2) != 0) {
         var2 = var0.d
      }

      if ((var9 and 4) != 0) {
         var3 = var0.e
      }

      if ((var9 and 8) != 0) {
         var4 = var0.f
      }

      if ((var9 and 16) != 0) {
         var5 = var0.g
      }

      if ((var9 and 32) != 0) {
         var6 = var0.b
      }

      if ((var9 and 64) != 0) {
         var7 = var0.h
      }

      HQ(var1, var2, var3, var4, var5, var6, var7)
   }

   public override fun toString(): String {
      return "HQ(g=${this.c}, e=${this.d}, h=${this.e}, f=${this.f}, a=${this.g}, d=${this.b}, b=${this.h})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          ((if (this.c == null) 0 else this.c.hashCode()) * 31 + this.d.hashCode()) * 31
                                             + (if (this.e == null) 0 else this.e.hashCode())
                                       )
                                       * 31
                                    + this.f.hashCode()
                              )
                              * 31
                           + (if (this.g == null) 0 else this.g.hashCode())
                     )
                     * 31
                  + (if (this.b == null) 0 else this.b.hashCode())
            )
            * 31
         + java.lang.Double.hashCode(this.h)
      }

   public override operator fun equals(other: Any?): Boolean {
      label58@
      if (this === other) {
         return true
      } else {
         return other is HQ
            && this.c == (other as HQ).c
            && this.d == (other as HQ).d
            && this.e == (other as HQ).e
            && this.f == (other as HQ).f
            && this.g == (other as HQ).g
            && this.b == (other as HQ).b
            && this.h == (other as HQ).h
         }
   }
}
