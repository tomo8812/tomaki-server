package org.valkyrienskies.core.impl.shadow

import kotlin.jvm.internal.DefaultConstructorMarker

public data class HJ : HN {
   private Integer g;
   private Hf h;
   private Integer i;
   private Hf j;
   private HP l;
   public java.lang.Float b;
   public java.lang.Float c;
   public java.lang.Float d;
   public java.lang.Float e;
   public java.lang.Float f;
   private double m;
   private HO n;

   fun HJ(
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: java.lang.Float,
      var7: java.lang.Float,
      var8: java.lang.Float,
      var9: java.lang.Float,
      var10: java.lang.Float,
      var11: Double
   ) {
      this.g = var1
      this.h = var2
      this.i = var3
      this.j = var4
      this.l = var5
      this.b = var6
      this.c = var7
      this.d = var8
      this.e = var9
      this.f = var10
      this.m = var11
      this.n = HO.DISTANCE
   }

   @JvmSynthetic
   fun HJ(
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: java.lang.Float,
      var7: java.lang.Float,
      var8: java.lang.Float,
      var9: java.lang.Float,
      var10: java.lang.Float,
      var11: Double,
      var13: Int,
      var14: DefaultConstructorMarker
   ) {
      if ((var13 and 16) != 0) {
         var5 = null
      }

      if ((var13 and 32) != 0) {
         var6 = null
      }

      if ((var13 and 64) != 0) {
         var7 = null
      }

      if ((var13 and 128) != 0) {
         var8 = null
      }

      if ((var13 and 256) != 0) {
         var9 = null
      }

      if ((var13 and 512) != 0) {
         var10 = null
      }

      if ((var13 and 1024) != 0) {
         var11 = 1.0E-10
      }

      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11)
   }

   @JvmName(name = "a")
   override final fun a(): Int {
      this.g
   }

   @JvmName(name = "b")
   override final fun b(): Hf {
      this.h
   }

   @JvmName(name = "c")
   override final fun c(): Int {
      this.i
   }

   @JvmName(name = "d")
   override final fun d(): Hf {
      this.j
   }

   @JvmName(name = "e")
   override final fun e(): HP {
      this.l
   }

   @JvmName(name = "f")
   fun f(): java.lang.Float {
      this.b
   }

   @JvmName(name = "g")
   fun g(): java.lang.Float {
      this.c
   }

   @JvmName(name = "h")
   fun h(): java.lang.Float {
      this.d
   }

   @JvmName(name = "i")
   fun i(): java.lang.Float {
      this.e
   }

   @JvmName(name = "j")
   fun j(): java.lang.Float {
      this.f
   }

   @JvmName(name = "o")
   override final fun o(): Double {
      this.m
   }

   @JvmName(name = "p")
   override final fun p(): HO {
      this.n
   }

   fun k(): Int {
      this.g
   }

   fun l(): Hf {
      this.h
   }

   fun m(): Int {
      this.i
   }

   fun n(): Hf {
      this.j
   }

   fun q(): HP {
      this.l
   }

   fun r(): java.lang.Float {
      this.b
   }

   fun s(): java.lang.Float {
      this.c
   }

   fun t(): java.lang.Float {
      this.d
   }

   fun u(): java.lang.Float {
      this.e
   }

   fun v(): java.lang.Float {
      this.f
   }

   fun w(): Double {
      this.m
   }

   @JvmStatic
   fun a(
      var0: Int,
      var1: Hf,
      var2: Int,
      var3: Hf,
      var4: HP,
      var5: java.lang.Float,
      var6: java.lang.Float,
      var7: java.lang.Float,
      var8: java.lang.Float,
      var9: java.lang.Float,
      var10: Double
   ): HJ {
      HJ(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(
      var0: HJ,
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: java.lang.Float,
      var7: java.lang.Float,
      var8: java.lang.Float,
      var9: java.lang.Float,
      var10: java.lang.Float,
      var11: Double,
      var13: Int
   ): HJ {
      if ((var13 and 1) != 0) {
         var1 = var0.g
      }

      if ((var13 and 2) != 0) {
         var2 = var0.h
      }

      if ((var13 and 4) != 0) {
         var3 = var0.i
      }

      if ((var13 and 8) != 0) {
         var4 = var0.j
      }

      if ((var13 and 16) != 0) {
         var5 = var0.l
      }

      if ((var13 and 32) != 0) {
         var6 = var0.b
      }

      if ((var13 and 64) != 0) {
         var7 = var0.c
      }

      if ((var13 and 128) != 0) {
         var8 = var0.d
      }

      if ((var13 and 256) != 0) {
         var9 = var0.e
      }

      if ((var13 and 512) != 0) {
         var10 = var0.f
      }

      if ((var13 and 1024) != 0) {
         var11 = var0.m
      }

      HJ(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11)
   }

   public override fun toString(): String {
      val var1: StringBuilder
      (var1 = StringBuilder())
         .append("HJ(i=")
         .append(this.g)
         .append(", e=")
         .append(this.h)
         .append(", j=")
         .append(this.i)
         .append(", f=")
         .append(this.j)
         .append(", a=")
         .append(this.l)
         .append(", h=")
         .append(this.b)
         .append(", g=")
         .append(this.c)
         .append(", l=")
         .append(this.d)
         .append(", k=")
         .append(this.e)
         .append(", d=")
         .append(this.f)
         .append(", b=")
         .append(this.m)
         .append(')')
         return var1.toString()
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              ((if (this.g == null) 0 else this.g.hashCode()) * 31 + this.h.hashCode()) * 31
                                                                                 + (if (this.i == null) 0 else this.i.hashCode())
                                                                           )
                                                                           * 31
                                                                        + this.j.hashCode()
                                                                  )
                                                                  * 31
                                                               + (if (this.l == null) 0 else this.l.hashCode())
                                                         )
                                                         * 31
                                                      + (if (this.b == null) 0 else this.b.hashCode())
                                                )
                                                * 31
                                             + (if (this.c == null) 0 else this.c.hashCode())
                                       )
                                       * 31
                                    + (if (this.d == null) 0 else this.d.hashCode())
                              )
                              * 31
                           + (if (this.e == null) 0 else this.e.hashCode())
                     )
                     * 31
                  + (if (this.f == null) 0 else this.f.hashCode())
            )
            * 31
         + java.lang.Double.hashCode(this.m)
      }

   public override operator fun equals(other: Any?): Boolean {
      label82@
      if (this === other) {
         return true
      } else {
         return other is HJ
            && this.g == (other as HJ).g
            && this.h == (other as HJ).h
            && this.i == (other as HJ).i
            && this.j == (other as HJ).j
            && this.l == (other as HJ).l
            && this.b == (other as HJ).b
            && this.c == (other as HJ).c
            && this.d == (other as HJ).d
            && this.e == (other as HJ).e
            && this.f == (other as HJ).f
            && this.m == (other as HJ).m
         }
   }
}
