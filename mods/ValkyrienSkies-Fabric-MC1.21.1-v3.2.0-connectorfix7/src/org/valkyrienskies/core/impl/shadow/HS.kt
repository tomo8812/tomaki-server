package org.valkyrienskies.core.impl.shadow

import kotlin.jvm.internal.DefaultConstructorMarker

public data class HS : HN {
   private Integer g;
   private Hf h;
   private Integer i;
   private Hf j;
   private HP l;
   public HI.a b;
   public HS.a c;
   public java.lang.Float d;
   public java.lang.Float e;
   public java.lang.Boolean f;
   private double m;
   private HO n;

   fun HS(
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: HI.a,
      var7: HS.a,
      var8: java.lang.Float,
      var9: java.lang.Float,
      var10: java.lang.Boolean,
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
      this.n = HO.REVOLUTE
   }

   @JvmSynthetic
   fun HS(
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: HI.a,
      var7: HS.a,
      var8: java.lang.Float,
      var9: java.lang.Float,
      var10: java.lang.Boolean,
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
   fun f(): HI.a {
      this.b
   }

   @JvmName(name = "g")
   fun g(): HS.a {
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
   fun j(): java.lang.Boolean {
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

   fun r(): HI.a {
      this.b
   }

   fun s(): HS.a {
      this.c
   }

   fun t(): java.lang.Float {
      this.d
   }

   fun u(): java.lang.Float {
      this.e
   }

   fun v(): java.lang.Boolean {
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
      var5: HI.a,
      var6: HS.a,
      var7: java.lang.Float,
      var8: java.lang.Float,
      var9: java.lang.Boolean,
      var10: Double
   ): HS {
      HS(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(
      var0: HS,
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: HI.a,
      var7: HS.a,
      var8: java.lang.Float,
      var9: java.lang.Float,
      var10: java.lang.Boolean,
      var11: Double,
      var13: Int
   ): HS {
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

      HS(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11)
   }

   public override fun toString(): String {
      val var1: StringBuilder
      (var1 = StringBuilder())
         .append("HS(k=")
         .append(this.g)
         .append(", i=")
         .append(this.h)
         .append(", l=")
         .append(this.i)
         .append(", j=")
         .append(this.j)
         .append(", b=")
         .append(this.l)
         .append(", a=")
         .append(this.b)
         .append(", h=")
         .append(this.c)
         .append(", e=")
         .append(this.d)
         .append(", g=")
         .append(this.e)
         .append(", f=")
         .append(this.f)
         .append(", c=")
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
         return other is HS
            && this.g == (other as HS).g
            && this.h == (other as HS).h
            && this.i == (other as HS).i
            && this.j == (other as HS).j
            && this.l == (other as HS).l
            && this.b == (other as HS).b
            && this.c == (other as HS).c
            && this.d == (other as HS).d
            && this.e == (other as HS).e
            && this.f == (other as HS).f
            && this.m == (other as HS).m
         }
   }

   public data class a(p0: Float, p1: Boolean = true) {
      public final val b: Float
      public final val a: Boolean

      init {
         this.a = var1
         this.b = var2
      }

      public operator fun c(): Float {
         return this.a
      }

      public operator fun d(): Boolean {
         return this.b
      }

      @JvmStatic
      public fun a(p0: Float = ..., p1: Boolean = ...): org.valkyrienskies.core.impl.shadow.HS.a {
         return HS.a(var0, var1)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: HS.a, var1: Float, var2: Boolean, var3: Int): HS.a {
         if ((var3 and 1) != 0) {
            var1 = var0.a
         }

         if ((var3 and 2) != 0) {
            var2 = var0.b
         }

         HS.a(var1, var2)
      }

      public override fun toString(): String {
         return "a(b=${this.a}, a=${this.b})"
      }

      public override fun hashCode(): Int {
         val var10000: Int = java.lang.Float.hashCode(this.a) * 31
         var var10001: Byte = this.b
         if (this.b) {
            var10001 = 1
         }

         return var10000 + var10001
      }

      public override operator fun equals(p0: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is HS.a && this.a == (other as HS.a).a && this.b == (other as HS.a).b
         }
      }
   }
}
