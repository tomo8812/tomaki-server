package org.valkyrienskies.core.impl.shadow

import kotlin.jvm.internal.DefaultConstructorMarker

public data class HL : HN {
   private Integer d;
   private Hf e;
   private Integer f;
   private Hf g;
   private HP h;
   public HI.h b;
   public java.lang.Float c;
   private double i;
   private HO j;

   fun HL(var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: HI.h, var7: java.lang.Float, var8: Double) {
      this.d = var1
      this.e = var2
      this.f = var3
      this.g = var4
      this.h = var5
      this.b = var6
      this.c = var7
      this.i = var8
      this.j = HO.GEAR
   }

   @JvmSynthetic
   fun HL(var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: HI.h, var7: java.lang.Float, var8: Double, var10: Int, var11: DefaultConstructorMarker) {
      if ((var10 and 16) != 0) {
         var5 = null
      }

      if ((var10 and 32) != 0) {
         var6 = null
      }

      if ((var10 and 64) != 0) {
         var7 = null
      }

      if ((var10 and 128) != 0) {
         var8 = 1.0E-10
      }

      this(var1, var2, var3, var4, var5, var6, var7, var8)
   }

   @JvmName(name = "a")
   override final fun a(): Int {
      this.d
   }

   @JvmName(name = "b")
   override final fun b(): Hf {
      this.e
   }

   @JvmName(name = "c")
   override final fun c(): Int {
      this.f
   }

   @JvmName(name = "d")
   override final fun d(): Hf {
      this.g
   }

   @JvmName(name = "e")
   override final fun e(): HP {
      this.h
   }

   @JvmName(name = "f")
   fun f(): HI.h {
      this.b
   }

   @JvmName(name = "g")
   fun g(): java.lang.Float {
      this.c
   }

   @JvmName(name = "o")
   override final fun o(): Double {
      this.i
   }

   @JvmName(name = "p")
   override final fun p(): HO {
      this.j
   }

   fun h(): Int {
      this.d
   }

   fun i(): Hf {
      this.e
   }

   fun j(): Int {
      this.f
   }

   fun k(): Hf {
      this.g
   }

   fun l(): HP {
      this.h
   }

   fun m(): HI.h {
      this.b
   }

   fun n(): java.lang.Float {
      this.c
   }

   fun q(): Double {
      this.i
   }

   @JvmStatic
   fun a(var0: Int, var1: Hf, var2: Int, var3: Hf, var4: HP, var5: HI.h, var6: java.lang.Float, var7: Double): HL {
      HL(var0, var1, var2, var3, var4, var5, var6, var7)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: HL, var1: Int, var2: Hf, var3: Int, var4: Hf, var5: HP, var6: HI.h, var7: java.lang.Float, var8: Double, var10: Int): HL {
      if ((var10 and 1) != 0) {
         var1 = var0.d
      }

      if ((var10 and 2) != 0) {
         var2 = var0.e
      }

      if ((var10 and 4) != 0) {
         var3 = var0.f
      }

      if ((var10 and 8) != 0) {
         var4 = var0.g
      }

      if ((var10 and 16) != 0) {
         var5 = var0.h
      }

      if ((var10 and 32) != 0) {
         var6 = var0.b
      }

      if ((var10 and 64) != 0) {
         var7 = var0.c
      }

      if ((var10 and 128) != 0) {
         var8 = var0.i
      }

      HL(var1, var2, var3, var4, var5, var6, var7, var8)
   }

   public override fun toString(): String {
      return "HL(h=${this.d}, f=${this.e}, i=${this.f}, g=${this.g}, a=${this.h}, e=${this.b}, d=${this.c}, b=${this.i})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   ((if (this.d == null) 0 else this.d.hashCode()) * 31 + this.e.hashCode()) * 31
                                                      + (if (this.f == null) 0 else this.f.hashCode())
                                                )
                                                * 31
                                             + this.g.hashCode()
                                       )
                                       * 31
                                    + (if (this.h == null) 0 else this.h.hashCode())
                              )
                              * 31
                           + (if (this.b == null) 0 else this.b.hashCode())
                     )
                     * 31
                  + (if (this.c == null) 0 else this.c.hashCode())
            )
            * 31
         + java.lang.Double.hashCode(this.i)
      }

   public override operator fun equals(other: Any?): Boolean {
      label64@
      if (this === other) {
         return true
      } else {
         return other is HL
            && this.d == (other as HL).d
            && this.e == (other as HL).e
            && this.f == (other as HL).f
            && this.g == (other as HL).g
            && this.h == (other as HL).h
            && this.b == (other as HL).b
            && this.c == (other as HL).c
            && this.i == (other as HL).i
         }
   }
}
