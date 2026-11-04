package org.valkyrienskies.core.impl.shadow

import java.util.EnumMap
import kotlin.jvm.internal.DefaultConstructorMarker
import org.joml.Vector3fc

public data class HI : HN {
   private Integer l;
   private Hf m;
   private Integer n;
   private Hf o;
   private HP p;
   public EnumMap<HI.b, HI.e> b;
   public HI.k c;
   public EnumMap<HI.b, HI.l> d;
   public HI.a e;
   public HI.i f;
   public HI.j g;
   public EnumMap<HI.c, HI.d> h;
   public HI.f i;
   public HI.g j;
   private double q;
   private HO r;

   fun HI(
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: EnumMap<HI.b, HI.e>,
      var7: HI.k,
      var8: EnumMap<HI.b, HI.l>,
      var9: HI.a,
      var10: HI.i,
      var11: HI.j,
      var12: EnumMap<HI.c, HI.d>,
      var13: HI.f,
      var14: HI.g,
      var15: Double
   ) {
      this.l = var1
      this.m = var2
      this.n = var3
      this.o = var4
      this.p = var5
      this.b = var6
      this.c = var7
      this.d = var8
      this.e = var9
      this.f = var10
      this.g = var11
      this.h = var12
      this.i = var13
      this.j = var14
      this.q = var15
      this.r = HO.D6
   }

   @JvmSynthetic
   fun HI(
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: EnumMap,
      var7: HI.k,
      var8: EnumMap,
      var9: HI.a,
      var10: HI.i,
      var11: HI.j,
      var12: EnumMap,
      var13: HI.f,
      var14: HI.g,
      var15: Double,
      var17: Int,
      var18: DefaultConstructorMarker
   ) {
      if ((var17 and 16) != 0) {
         var5 = null
      }

      if ((var17 and 32) != 0) {
         var6 = null
      }

      if ((var17 and 64) != 0) {
         var7 = null
      }

      if ((var17 and 128) != 0) {
         var8 = null
      }

      if ((var17 and 256) != 0) {
         var9 = null
      }

      if ((var17 and 512) != 0) {
         var10 = null
      }

      if ((var17 and 1024) != 0) {
         var11 = null
      }

      if ((var17 and 2048) != 0) {
         var12 = null
      }

      if ((var17 and 4096) != 0) {
         var13 = null
      }

      if ((var17 and 8192) != 0) {
         var14 = null
      }

      if ((var17 and 16384) != 0) {
         var15 = 1.0E-10
      }

      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15)
   }

   @JvmName(name = "a")
   override final fun a(): Int {
      this.l
   }

   @JvmName(name = "b")
   override final fun b(): Hf {
      this.m
   }

   @JvmName(name = "c")
   override final fun c(): Int {
      this.n
   }

   @JvmName(name = "d")
   override final fun d(): Hf {
      this.o
   }

   @JvmName(name = "e")
   override final fun e(): HP {
      this.p
   }

   @JvmName(name = "f")
   fun f(): EnumMap<HI.b, HI.e> {
      this.b
   }

   @JvmName(name = "g")
   fun g(): HI.k {
      this.c
   }

   @JvmName(name = "h")
   fun h(): EnumMap<HI.b, HI.l> {
      this.d
   }

   @JvmName(name = "i")
   fun i(): HI.a {
      this.e
   }

   @JvmName(name = "j")
   fun j(): HI.i {
      this.f
   }

   @JvmName(name = "k")
   fun k(): HI.j {
      this.g
   }

   @JvmName(name = "l")
   fun l(): EnumMap<HI.c, HI.d> {
      this.h
   }

   @JvmName(name = "m")
   fun m(): HI.f {
      this.i
   }

   @JvmName(name = "n")
   fun n(): HI.g {
      this.j
   }

   @JvmName(name = "o")
   override final fun o(): Double {
      this.q
   }

   @JvmName(name = "p")
   override final fun p(): HO {
      this.r
   }

   fun q(): Int {
      this.l
   }

   fun r(): Hf {
      this.m
   }

   fun s(): Int {
      this.n
   }

   fun t(): Hf {
      this.o
   }

   fun u(): HP {
      this.p
   }

   fun v(): EnumMap<HI.b, HI.e> {
      this.b
   }

   fun w(): HI.k {
      this.c
   }

   fun x(): EnumMap<HI.b, HI.l> {
      this.d
   }

   fun y(): HI.a {
      this.e
   }

   fun z(): HI.i {
      this.f
   }

   fun A(): HI.j {
      this.g
   }

   fun B(): EnumMap<HI.c, HI.d> {
      this.h
   }

   fun C(): HI.f {
      this.i
   }

   fun D(): HI.g {
      this.j
   }

   fun E(): Double {
      this.q
   }

   @JvmStatic
   fun a(
      var0: Int,
      var1: Hf,
      var2: Int,
      var3: Hf,
      var4: HP,
      var5: EnumMap<HI.b, HI.e>,
      var6: HI.k,
      var7: EnumMap<HI.b, HI.l>,
      var8: HI.a,
      var9: HI.i,
      var10: HI.j,
      var11: EnumMap<HI.c, HI.d>,
      var12: HI.f,
      var13: HI.g,
      var14: Double
   ): HI {
      HI(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(
      var0: HI,
      var1: Int,
      var2: Hf,
      var3: Int,
      var4: Hf,
      var5: HP,
      var6: EnumMap,
      var7: HI.k,
      var8: EnumMap,
      var9: HI.a,
      var10: HI.i,
      var11: HI.j,
      var12: EnumMap,
      var13: HI.f,
      var14: HI.g,
      var15: Double,
      var17: Int
   ): HI {
      if ((var17 and 1) != 0) {
         var1 = var0.l
      }

      if ((var17 and 2) != 0) {
         var2 = var0.m
      }

      if ((var17 and 4) != 0) {
         var3 = var0.n
      }

      if ((var17 and 8) != 0) {
         var4 = var0.o
      }

      if ((var17 and 16) != 0) {
         var5 = var0.p
      }

      if ((var17 and 32) != 0) {
         var6 = var0.b
      }

      if ((var17 and 64) != 0) {
         var7 = var0.c
      }

      if ((var17 and 128) != 0) {
         var8 = var0.d
      }

      if ((var17 and 256) != 0) {
         var9 = var0.e
      }

      if ((var17 and 512) != 0) {
         var10 = var0.f
      }

      if ((var17 and 1024) != 0) {
         var11 = var0.g
      }

      if ((var17 and 2048) != 0) {
         var12 = var0.h
      }

      if ((var17 and 4096) != 0) {
         var13 = var0.i
      }

      if ((var17 and 8192) != 0) {
         var14 = var0.j
      }

      if ((var17 and 16384) != 0) {
         var15 = var0.q
      }

      HI(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15)
   }

   public override fun toString(): String {
      val var1: StringBuilder
      (var1 = StringBuilder())
         .append("HI(l=")
         .append(this.l)
         .append(", i=")
         .append(this.m)
         .append(", m=")
         .append(this.n)
         .append(", j=")
         .append(this.o)
         .append(", a=")
         .append(this.p)
         .append(", k=")
         .append(this.b)
         .append(", d=")
         .append(this.c)
         .append(", h=")
         .append(this.d)
         .append(", p=")
         .append(this.e)
         .append(", o=")
         .append(this.f)
         .append(", n=")
         .append(this.g)
         .append(", g=")
         var1.append(this.h).append(", e=").append(this.i).append(", f=").append(this.j).append(", b=").append(this.q).append(')')
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
                                                                              (
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           (
                                                                                                                                    if (this.l == null)
                                                                                                                                       0
                                                                                                                                       else
                                                                                                                                       this.l.hashCode()
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + this.m.hashCode()
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + (
                                                                                                                        if (this.n == null)
                                                                                                                           0
                                                                                                                           else
                                                                                                                           this.n.hashCode()
                                                                                                                     )
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this.o.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + (if (this.p == null) 0 else this.p.hashCode())
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
                                             + (if (this.g == null) 0 else this.g.hashCode())
                                       )
                                       * 31
                                    + (if (this.h == null) 0 else this.h.hashCode())
                              )
                              * 31
                           + (if (this.i == null) 0 else this.i.hashCode())
                     )
                     * 31
                  + (if (this.j == null) 0 else this.j.hashCode())
            )
            * 31
         + java.lang.Double.hashCode(this.q)
      }

   public override operator fun equals(other: Any?): Boolean {
      label106@
      if (this === other) {
         return true
      } else {
         return other is HI
            && this.l == (other as HI).l
            && this.m == (other as HI).m
            && this.n == (other as HI).n
            && this.o == (other as HI).o
            && this.p == (other as HI).p
            && this.b == (other as HI).b
            && this.c == (other as HI).c
            && this.d == (other as HI).d
            && this.e == (other as HI).e
            && this.f == (other as HI).f
            && this.g == (other as HI).g
            && this.h == (other as HI).h
            && this.i == (other as HI).i
            && this.j == (other as HI).j
            && this.q == (other as HI).q
         }
   }

   public data class a(p0: Float, p1: Float, p2: Float? = null, p3: Float? = null, p4: Float? = null, p5: Float? = null) {
      public final val c: Float
      public final val f: Float
      public final val d: Float?
      public final val a: Float?
      public final val e: Float?
      public final val b: Float?

      init {
         this.a = var1
         this.b = var2
         this.c = var3
         this.d = var4
         this.e = var5
         this.f = var6
      }

      public operator fun g(): Float {
         return this.a
      }

      public operator fun h(): Float {
         return this.b
      }

      public operator fun i(): Float? {
         return this.c
      }

      public operator fun j(): Float? {
         return this.d
      }

      public operator fun k(): Float? {
         return this.e
      }

      public operator fun l(): Float? {
         return this.f
      }

      @JvmStatic
      public fun a(p0: Float = ..., p1: Float = ..., p2: Float? = ..., p3: Float? = ..., p4: Float? = ..., p5: Float? = ...): org.valkyrienskies.core.impl.shadow.HI.a {
         return HI.a(var0, var1, var2, var3, var4, var5)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(
         var0: HI.a, var1: Float, var2: Float, var3: java.lang.Float, var4: java.lang.Float, var5: java.lang.Float, var6: java.lang.Float, var7: Int
      ): HI.a {
         if ((var7 and 1) != 0) {
            var1 = var0.a
         }

         if ((var7 and 2) != 0) {
            var2 = var0.b
         }

         if ((var7 and 4) != 0) {
            var3 = var0.c
         }

         if ((var7 and 8) != 0) {
            var4 = var0.d
         }

         if ((var7 and 16) != 0) {
            var5 = var0.e
         }

         if ((var7 and 32) != 0) {
            var6 = var0.f
         }

         HI.a(var1, var2, var3, var4, var5, var6)
      }

      public override fun toString(): String {
         return "a(c=${this.a}, f=${this.b}, d=${this.c}, a=${this.d}, e=${this.e}, b=${this.f})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           ((java.lang.Float.hashCode(this.a) * 31 + java.lang.Float.hashCode(this.b)) * 31 + (if (this.c == null) 0 else this.c.hashCode()))
                                 * 31
                              + (if (this.d == null) 0 else this.d.hashCode())
                        )
                        * 31
                     + (if (this.e == null) 0 else this.e.hashCode())
               )
               * 31
            + (if (this.f == null) 0 else this.f.hashCode())
         }

      public override operator fun equals(p0: Any?): Boolean {
         label52@
         if (this === other) {
            return true
         } else {
            return other is HI.a
               && this.a == (other as HI.a).a
               && this.b == (other as HI.a).b
               && this.c == (other as HI.a).c
               && this.d == (other as HI.a).d
               && this.e == (other as HI.a).e
               && this.f == (other as HI.a).f
            }
      }
   }

   public enum class b {
      X,
      Y,
      Z,
      TWIST,
      SWING1,
      SWING2;

      @JvmStatic
      @JvmSynthetic
      fun a(): Array<HI.b> {
         val var0: Array<HI.b>
         (var0 = arrayOfNulls(6))[0] = X
         var0[1] = Y
         var0[2] = Z
         var0[3] = TWIST
         var0[4] = SWING1
         var0[5] = SWING2
         var0
      }

      @JvmStatic
      fun {
         val var0: Array<HI.b>
         (var0 = arrayOfNulls(6))[0] = X
         var0[1] = Y
         var0[2] = Z
         var0[3] = TWIST
         var0[4] = SWING1
         var0[5] = SWING2
      }
   }

   public enum class c {
      X,
      Y,
      Z,
      SWING,
      TWIST,
      SLERP;

      @JvmStatic
      @JvmSynthetic
      fun a(): Array<HI.c> {
         val var0: Array<HI.c>
         (var0 = arrayOfNulls(6))[0] = X
         var0[1] = Y
         var0[2] = Z
         var0[3] = SWING
         var0[4] = TWIST
         var0[5] = SLERP
         var0
      }

      @JvmStatic
      fun {
         val var0: Array<HI.c>
         (var0 = arrayOfNulls(6))[0] = X
         var0[1] = Y
         var0[2] = Z
         var0[3] = SWING
         var0[4] = TWIST
         var0[5] = SLERP
      }
   }

   public data class d(p0: Float, p1: Float, p2: Float, p3: Boolean = false) {
      public final val c: Float
      public final val a: Float
      public final val b: Float
      public final val d: Boolean

      init {
         this.a = var1
         this.b = var2
         this.c = var3
         this.d = var4
      }

      public operator fun e(): Float {
         return this.a
      }

      public operator fun f(): Float {
         return this.b
      }

      public operator fun g(): Float {
         return this.c
      }

      public operator fun h(): Boolean {
         return this.d
      }

      @JvmStatic
      public fun a(p0: Float = ..., p1: Float = ..., p2: Float = ..., p3: Boolean = ...): org.valkyrienskies.core.impl.shadow.HI.d {
         return HI.d(var0, var1, var2, var3)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: HI.d, var1: Float, var2: Float, var3: Float, var4: Boolean, var5: Int): HI.d {
         if ((var5 and 1) != 0) {
            var1 = var0.a
         }

         if ((var5 and 2) != 0) {
            var2 = var0.b
         }

         if ((var5 and 4) != 0) {
            var3 = var0.c
         }

         if ((var5 and 8) != 0) {
            var4 = var0.d
         }

         HI.d(var1, var2, var3, var4)
      }

      public override fun toString(): String {
         return "d(c=${this.a}, a=${this.b}, b=${this.c}, d=${this.d})"
      }

      public override fun hashCode(): Int {
         val var10000: Int = ((java.lang.Float.hashCode(this.a) * 31 + java.lang.Float.hashCode(this.b)) * 31 + java.lang.Float.hashCode(this.c)) * 31
         var var10001: Byte = this.d
         if (this.d) {
            var10001 = 1
         }

         return var10000 + var10001
      }

      public override operator fun equals(p0: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is HI.d && this.a == (other as HI.d).a && this.b == (other as HI.d).b && this.c == (other as HI.d).c && this.d == (other as HI.d).d
         }
      }
   }

   public enum class e {
      LOCKED,
      LIMITED,
      FREE;

      @JvmStatic
      @JvmSynthetic
      fun a(): Array<HI.e> {
         val var0: Array<HI.e>
         (var0 = arrayOfNulls(3))[0] = LOCKED
         var0[1] = LIMITED
         var0[2] = FREE
         var0
      }

      @JvmStatic
      fun {
         val var0: Array<HI.e>
         (var0 = arrayOfNulls(3))[0] = LOCKED
         var0[1] = LIMITED
         var0[2] = FREE
      }
   }

   public data class f(p0: Hf, p1: Boolean = true) {
      public final val b: Hf
      public final val a: Boolean

      init {
         this.a = var1
         this.b = var2
      }

      public operator fun c(): Hf {
         return this.a
      }

      public operator fun d(): Boolean {
         return this.b
      }

      @JvmStatic
      public fun a(p0: Hf = ..., p1: Boolean = ...): org.valkyrienskies.core.impl.shadow.HI.f {
         return HI.f(var0, var1)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: HI.f, var1: Hf, var2: Boolean, var3: Int): HI.f {
         if ((var3 and 1) != 0) {
            var1 = var0.a
         }

         if ((var3 and 2) != 0) {
            var2 = var0.b
         }

         HI.f(var1, var2)
      }

      public override fun toString(): String {
         return "f(b=${this.a}, a=${this.b})"
      }

      public override fun hashCode(): Int {
         val var10000: Int = this.a.hashCode() * 31
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
            return other is HI.f && this.a == (other as HI.f).a && this.b == (other as HI.f).b
         }
      }
   }

   public data class g(p0: Vector3fc, p1: Vector3fc, p2: Boolean = true) {
      public final val c: Vector3fc
      public final val a: Vector3fc
      public final val b: Boolean

      init {
         this.a = var1
         this.b = var2
         this.c = var3
      }

      public operator fun d(): Vector3fc {
         return this.a
      }

      public operator fun e(): Vector3fc {
         return this.b
      }

      public operator fun f(): Boolean {
         return this.c
      }

      @JvmStatic
      public fun a(p0: Vector3fc = ..., p1: Vector3fc = ..., p2: Boolean = ...): org.valkyrienskies.core.impl.shadow.HI.g {
         return HI.g(var0, var1, var2)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: HI.g, var1: Vector3fc, var2: Vector3fc, var3: Boolean, var4: Int): HI.g {
         if ((var4 and 1) != 0) {
            var1 = var0.a
         }

         if ((var4 and 2) != 0) {
            var2 = var0.b
         }

         if ((var4 and 4) != 0) {
            var3 = var0.c
         }

         HI.g(var1, var2, var3)
      }

      public override fun toString(): String {
         return "g(c=${this.a}, a=${this.b}, b=${this.c})"
      }

      public override fun hashCode(): Int {
         val var10000: Int = (this.a.hashCode() * 31 + this.b.hashCode()) * 31
         var var10001: Byte = this.c
         if (this.c) {
            var10001 = 1
         }

         return var10000 + var10001
      }

      public override operator fun equals(p0: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is HI.g && this.a == (other as HI.g).a && this.b == (other as HI.g).b && this.c == (other as HI.g).c
         }
      }
   }

   // $VF: Class flags could not be determined
   internal class h {
      public int a;
      public int b;

      fun h(var1: Int, var2: Int) {
         this.a = var1
         this.b = var2
      }

      fun a(): Int {
         this.a
      }

      fun b(): Int {
         this.b
      }

      fun c(): Int {
         this.a
      }

      fun d(): Int {
         this.b
      }

      @JvmStatic
      fun a(var0: Int, var1: Int): HI.h {
         HI.h(var0, var1)
      }

      @JvmStatic
      @JvmSynthetic
      fun a(var0: HI.h, var1: Int, var2: Int, var3: Int): HI.h {
         if ((var3 and 1) != 0) {
            var1 = var0.a
         }

         if ((var3 and 2) != 0) {
            var2 = var0.b
         }

         HI.h(var1, var2)
      }

      override final fun toString(): java.lang.String {
         "Hinges(hinge0=${this.a}, hinge1=${this.b})"
      }

      override final fun hashCode(): Int {
         Integer.hashCode(this.a) * 31 + Integer.hashCode(this.b)
      }

      override final fun equals(other: Any): Boolean {
         label28@
         if (this === other) {
            true
         } else {
            other is HI.h && this.a == (other as HI.h).a && this.b == (other as HI.h).b
         }
      }
   }

   public data class i(p0: Float, p1: Float, p2: Float? = null, p3: Float? = null, p4: Float? = null, p5: Float? = null) {
      public final val e: Float
      public final val f: Float
      public final val c: Float?
      public final val a: Float?
      public final val d: Float?
      public final val b: Float?

      init {
         this.a = var1
         this.b = var2
         this.c = var3
         this.d = var4
         this.e = var5
         this.f = var6
      }

      public operator fun g(): Float {
         return this.a
      }

      public operator fun h(): Float {
         return this.b
      }

      public operator fun i(): Float? {
         return this.c
      }

      public operator fun j(): Float? {
         return this.d
      }

      public operator fun k(): Float? {
         return this.e
      }

      public operator fun l(): Float? {
         return this.f
      }

      @JvmStatic
      public fun a(p0: Float = ..., p1: Float = ..., p2: Float? = ..., p3: Float? = ..., p4: Float? = ..., p5: Float? = ...): org.valkyrienskies.core.impl.shadow.HI.i {
         return HI.i(var0, var1, var2, var3, var4, var5)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(
         var0: HI.i, var1: Float, var2: Float, var3: java.lang.Float, var4: java.lang.Float, var5: java.lang.Float, var6: java.lang.Float, var7: Int
      ): HI.i {
         if ((var7 and 1) != 0) {
            var1 = var0.a
         }

         if ((var7 and 2) != 0) {
            var2 = var0.b
         }

         if ((var7 and 4) != 0) {
            var3 = var0.c
         }

         if ((var7 and 8) != 0) {
            var4 = var0.d
         }

         if ((var7 and 16) != 0) {
            var5 = var0.e
         }

         if ((var7 and 32) != 0) {
            var6 = var0.f
         }

         HI.i(var1, var2, var3, var4, var5, var6)
      }

      public override fun toString(): String {
         return "i(e=${this.a}, f=${this.b}, c=${this.c}, a=${this.d}, d=${this.e}, b=${this.f})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           ((java.lang.Float.hashCode(this.a) * 31 + java.lang.Float.hashCode(this.b)) * 31 + (if (this.c == null) 0 else this.c.hashCode()))
                                 * 31
                              + (if (this.d == null) 0 else this.d.hashCode())
                        )
                        * 31
                     + (if (this.e == null) 0 else this.e.hashCode())
               )
               * 31
            + (if (this.f == null) 0 else this.f.hashCode())
         }

      public override operator fun equals(p0: Any?): Boolean {
         label52@
         if (this === other) {
            return true
         } else {
            return other is HI.i
               && this.a == (other as HI.i).a
               && this.b == (other as HI.i).b
               && this.c == (other as HI.i).c
               && this.d == (other as HI.i).d
               && this.e == (other as HI.i).e
               && this.f == (other as HI.i).f
            }
      }
   }

   public data class j(p0: Float, p1: Float, p2: Float, p3: Float, p4: Float? = null, p5: Float? = null, p6: Float? = null, p7: Float? = null) {
      public final val f: Float
      public final val e: Float
      public final val h: Float
      public final val g: Float
      public final val c: Float?
      public final val a: Float?
      public final val d: Float?
      public final val b: Float?

      init {
         this.a = var1
         this.b = var2
         this.c = var3
         this.d = var4
         this.e = var5
         this.f = var6
         this.g = var7
         this.h = var8
      }

      public operator fun i(): Float {
         return this.a
      }

      public operator fun j(): Float {
         return this.b
      }

      public operator fun k(): Float {
         return this.c
      }

      public operator fun l(): Float {
         return this.d
      }

      public operator fun m(): Float? {
         return this.e
      }

      public operator fun n(): Float? {
         return this.f
      }

      public operator fun o(): Float? {
         return this.g
      }

      public operator fun p(): Float? {
         return this.h
      }

      @JvmStatic
      public fun a(p0: Float = ..., p1: Float = ..., p2: Float = ..., p3: Float = ..., p4: Float? = ..., p5: Float? = ..., p6: Float? = ..., p7: Float? = ...): org.valkyrienskies.core.impl.shadow.HI.j {
         return HI.j(var0, var1, var2, var3, var4, var5, var6, var7)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(
         var0: HI.j,
         var1: Float,
         var2: Float,
         var3: Float,
         var4: Float,
         var5: java.lang.Float,
         var6: java.lang.Float,
         var7: java.lang.Float,
         var8: java.lang.Float,
         var9: Int
      ): HI.j {
         if ((var9 and 1) != 0) {
            var1 = var0.a
         }

         if ((var9 and 2) != 0) {
            var2 = var0.b
         }

         if ((var9 and 4) != 0) {
            var3 = var0.c
         }

         if ((var9 and 8) != 0) {
            var4 = var0.d
         }

         if ((var9 and 16) != 0) {
            var5 = var0.e
         }

         if ((var9 and 32) != 0) {
            var6 = var0.f
         }

         if ((var9 and 64) != 0) {
            var7 = var0.g
         }

         if ((var9 and 128) != 0) {
            var8 = var0.h
         }

         HI.j(var1, var2, var3, var4, var5, var6, var7, var8)
      }

      public override fun toString(): String {
         return "j(f=${this.a}, e=${this.b}, h=${this.c}, g=${this.d}, c=${this.e}, a=${this.f}, d=${this.g}, b=${this.h})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      (java.lang.Float.hashCode(this.a) * 31 + java.lang.Float.hashCode(this.b)) * 31
                                                         + java.lang.Float.hashCode(this.c)
                                                   )
                                                   * 31
                                                + java.lang.Float.hashCode(this.d)
                                          )
                                          * 31
                                       + (if (this.e == null) 0 else this.e.hashCode())
                                 )
                                 * 31
                              + (if (this.f == null) 0 else this.f.hashCode())
                        )
                        * 31
                     + (if (this.g == null) 0 else this.g.hashCode())
               )
               * 31
            + (if (this.h == null) 0 else this.h.hashCode())
         }

      public override operator fun equals(p0: Any?): Boolean {
         label64@
         if (this === other) {
            return true
         } else {
            return other is HI.j
               && this.a == (other as HI.j).a
               && this.b == (other as HI.j).b
               && this.c == (other as HI.j).c
               && this.d == (other as HI.j).d
               && this.e == (other as HI.j).e
               && this.f == (other as HI.j).f
               && this.g == (other as HI.j).g
               && this.h == (other as HI.j).h
            }
      }
   }

   public data class k(p0: Float, p1: Float? = null, p2: Float? = null) {
      public final val b: Float
      public final val c: Float?
      public final val a: Float?

      init {
         this.a = var1
         this.b = var2
         this.c = var3
      }

      public operator fun d(): Float {
         return this.a
      }

      public operator fun e(): Float? {
         return this.b
      }

      public operator fun f(): Float? {
         return this.c
      }

      @JvmStatic
      public fun a(p0: Float = ..., p1: Float? = ..., p2: Float? = ...): org.valkyrienskies.core.impl.shadow.HI.k {
         return HI.k(var0, var1, var2)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: HI.k, var1: Float, var2: java.lang.Float, var3: java.lang.Float, var4: Int): HI.k {
         if ((var4 and 1) != 0) {
            var1 = var0.a
         }

         if ((var4 and 2) != 0) {
            var2 = var0.b
         }

         if ((var4 and 4) != 0) {
            var3 = var0.c
         }

         HI.k(var1, var2, var3)
      }

      public override fun toString(): String {
         return "k(b=${this.a}, c=${this.b}, a=${this.c})"
      }

      public override fun hashCode(): Int {
         return (java.lang.Float.hashCode(this.a) * 31 + (if (this.b == null) 0 else this.b.hashCode())) * 31 + (if (this.c == null) 0 else this.c.hashCode())
      }

      public override operator fun equals(p0: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is HI.k && this.a == (other as HI.k).a && this.b == (other as HI.k).b && this.c == (other as HI.k).c
         }
      }
   }

   public data class l(p0: Float, p1: Float, p2: Float? = null, p3: Float? = null, p4: Float? = null, p5: Float? = null) {
      public final val c: Float
      public final val f: Float
      public final val d: Float?
      public final val a: Float?
      public final val e: Float?
      public final val b: Float?

      init {
         this.a = var1
         this.b = var2
         this.c = var3
         this.d = var4
         this.e = var5
         this.f = var6
      }

      public operator fun g(): Float {
         return this.a
      }

      public operator fun h(): Float {
         return this.b
      }

      public operator fun i(): Float? {
         return this.c
      }

      public operator fun j(): Float? {
         return this.d
      }

      public operator fun k(): Float? {
         return this.e
      }

      public operator fun l(): Float? {
         return this.f
      }

      @JvmStatic
      public fun a(p0: Float = ..., p1: Float = ..., p2: Float? = ..., p3: Float? = ..., p4: Float? = ..., p5: Float? = ...): org.valkyrienskies.core.impl.shadow.HI.l {
         return HI.l(var0, var1, var2, var3, var4, var5)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(
         var0: HI.l, var1: Float, var2: Float, var3: java.lang.Float, var4: java.lang.Float, var5: java.lang.Float, var6: java.lang.Float, var7: Int
      ): HI.l {
         if ((var7 and 1) != 0) {
            var1 = var0.a
         }

         if ((var7 and 2) != 0) {
            var2 = var0.b
         }

         if ((var7 and 4) != 0) {
            var3 = var0.c
         }

         if ((var7 and 8) != 0) {
            var4 = var0.d
         }

         if ((var7 and 16) != 0) {
            var5 = var0.e
         }

         if ((var7 and 32) != 0) {
            var6 = var0.f
         }

         HI.l(var1, var2, var3, var4, var5, var6)
      }

      public override fun toString(): String {
         return "l(c=${this.a}, f=${this.b}, d=${this.c}, a=${this.d}, e=${this.e}, b=${this.f})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           ((java.lang.Float.hashCode(this.a) * 31 + java.lang.Float.hashCode(this.b)) * 31 + (if (this.c == null) 0 else this.c.hashCode()))
                                 * 31
                              + (if (this.d == null) 0 else this.d.hashCode())
                        )
                        * 31
                     + (if (this.e == null) 0 else this.e.hashCode())
               )
               * 31
            + (if (this.f == null) 0 else this.f.hashCode())
         }

      public override operator fun equals(p0: Any?): Boolean {
         label52@
         if (this === other) {
            return true
         } else {
            return other is HI.l
               && this.a == (other as HI.l).a
               && this.b == (other as HI.l).b
               && this.c == (other as HI.l).c
               && this.d == (other as HI.l).d
               && this.e == (other as HI.l).e
               && this.f == (other as HI.l).f
            }
      }
   }
}
