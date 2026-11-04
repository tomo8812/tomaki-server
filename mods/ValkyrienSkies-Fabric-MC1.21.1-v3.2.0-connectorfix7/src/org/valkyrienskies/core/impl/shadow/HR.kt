package org.valkyrienskies.core.impl.shadow

import kotlin.jvm.internal.DefaultConstructorMarker

public data class HR : HN {
   private Integer e;
   private Hf f;
   private Integer g;
   private Hf h;
   private HP i;
   public HI.h b;
   public java.lang.Float c;
   public HR.a d;
   private double j;
   private HO l;

   fun HR(var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: HI.h, var7: java.lang.Float, var8: HR.a, var9: Double) {
      this.e = var1;
      this.f = var2;
      this.g = var3;
      this.h = var4;
      this.i = var5;
      this.b = var6;
      this.c = var7;
      this.d = var8;
      this.j = var9;
      this.l = HO.RACK_AND_PINION;
   }

   @JvmSynthetic
   fun HR(
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: HI.h,
      var7: java.lang.Float,
      var8: HR.a,
      var9: Double,
      var11: Int,
      var12: DefaultConstructorMarker
   ) {
      if ((var11 and 16) != 0) {
         var5 = null;
      }

      if ((var11 and 32) != 0) {
         var6 = null;
      }

      if ((var11 and 64) != 0) {
         var7 = null;
      }

      if ((var11 and 128) != 0) {
         var8 = null;
      }

      if ((var11 and 256) != 0) {
         var9 = 1.0E-10;
      }

      this(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @JvmName(name = "a")
   override final fun a(): Int {
      this.e;
   }

   @JvmName(name = "b")
   override final fun b(): Hf {
      this.f;
   }

   @JvmName(name = "c")
   override final fun c(): Int {
      this.g;
   }

   @JvmName(name = "d")
   override final fun d(): Hf {
      this.h;
   }

   @JvmName(name = "e")
   override final fun e(): HP {
      this.i;
   }

   @JvmName(name = "f")
   fun f(): HI.h {
      this.b;
   }

   @JvmName(name = "g")
   fun g(): java.lang.Float {
      this.c;
   }

   @JvmName(name = "h")
   fun h(): HR.a {
      this.d;
   }

   @JvmName(name = "o")
   override final fun o(): Double {
      this.j;
   }

   @JvmName(name = "p")
   override final fun p(): HO {
      this.l;
   }

   fun i(): Int {
      this.e;
   }

   fun j(): Hf {
      this.f;
   }

   fun k(): Int {
      this.g;
   }

   fun l(): Hf {
      this.h;
   }

   fun m(): HP {
      this.i;
   }

   fun n(): HI.h {
      this.b;
   }

   fun q(): java.lang.Float {
      this.c;
   }

   fun r(): HR.a {
      this.d;
   }

   fun s(): Double {
      this.j;
   }

   @JvmStatic
   fun a(var0: Int, var1: Hf, var2: Int, var3: Hf, var4: HP, var5: HI.h, var6: java.lang.Float, var7: HR.a, var8: Double): HR {
      HR(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: HR, var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: HI.h, var7: java.lang.Float, var8: HR.a, var9: Double, var11: Int): HR {
      if ((var11 and 1) != 0) {
         var1 = var0.e;
      }

      if ((var11 and 2) != 0) {
         var2 = var0.f;
      }

      if ((var11 and 4) != 0) {
         var3 = var0.g;
      }

      if ((var11 and 8) != 0) {
         var4 = var0.h;
      }

      if ((var11 and 16) != 0) {
         var5 = var0.i;
      }

      if ((var11 and 32) != 0) {
         var6 = var0.b;
      }

      if ((var11 and 64) != 0) {
         var7 = var0.c;
      }

      if ((var11 and 128) != 0) {
         var8 = var0.d;
      }

      if ((var11 and 256) != 0) {
         var9 = var0.j;
      }

      HR(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public override fun toString(): String {
      return "HR(h=${this.e}, f=${this.f}, i=${this.g}, g=${this.h}, a=${this.i}, e=${this.b}, j=${this.c}, d=${this.d}, b=${this.j})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            ((if (this.e == null) 0 else this.e.hashCode()) * 31 + this.f.hashCode()) * 31
                                                               + (if (this.g == null) 0 else this.g.hashCode())
                                                         )
                                                         * 31
                                                      + this.h.hashCode()
                                                )
                                                * 31
                                             + (if (this.i == null) 0 else this.i.hashCode())
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
         + java.lang.Double.hashCode(this.j);
   }

   public override operator fun equals(other: Any?): Boolean {
      label70@
      if (this === other) {
         return true;
      } else {
         return other is HR
            && this.e == (other as HR).e
            && this.f == (other as HR).f
            && this.g == (other as HR).g
            && this.h == (other as HR).h
            && this.i == (other as HR).i
            && this.b == (other as HR).b
            && this.c == (other as HR).c
            && this.d == (other as HR).d
            && this.j == (other as HR).j;
      }
   }

   // $VF: Class flags could not be determined
   internal class a {
      public int a;
      public int b;
      public float c;

      fun a(var1: Int, var2: Int, var3: Float) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
      }

      fun a(): Int {
         this.a;
      }

      fun b(): Int {
         this.b;
      }

      fun c(): Float {
         this.c;
      }

      fun d(): Int {
         this.a;
      }

      fun e(): Int {
         this.b;
      }

      fun f(): Float {
         this.c;
      }

      @JvmStatic
      fun a(var0: Int, var1: Int, var2: Float): HR.a {
         HR.a(var0, var1, var2);
      }

      @JvmStatic
      @JvmSynthetic
      fun a(var0: HR.a, var1: Int, var2: Int, var3: Float, var4: Int): HR.a {
         if ((var4 and 1) != 0) {
            var1 = var0.a;
         }

         if ((var4 and 2) != 0) {
            var2 = var0.b;
         }

         if ((var4 and 4) != 0) {
            var3 = var0.c;
         }

         HR.a(var1, var2, var3);
      }

      override final fun toString(): java.lang.String {
         "Data(rackTeeth=${this.a}, pinionTeeth=${this.b}, rackLength=${this.c})";
      }

      override final fun hashCode(): Int {
         (Integer.hashCode(this.a) * 31 + Integer.hashCode(this.b)) * 31 + java.lang.Float.hashCode(this.c);
      }

      override final fun equals(other: Any): Boolean {
         label34@
         if (this === other) {
            true;
         } else {
            other is HR.a && this.a == (other as HR.a).a && this.b == (other as HR.a).b && this.c == (other as HR.a).c;
         }
      }
   }
}
