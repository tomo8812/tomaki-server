package org.valkyrienskies.core.impl.shadow

public open class Iz(p0: Int = 1,
      p1: Int = 4,
      p2: Int = 1,
      p3: Double = 1.0,
      p4: Double = 0.0,
      p5: Double = 0.0,
      p6: Double = 0.0,
      p7: Double = 0.05,
      p8: IG = IG.JACOBI,
      p9: Int = 4,
      p10: Double = 1.0,
      p11: Double = 10000.0,
      p12: Int = 64,
      p13: ID = ID.KRUNCH_CLASSIC,
      p14: Boolean = true
   ) :
   IA {
   public open var m: Int
   public open var i: Int
   public open var o: Int
   public open var j: Double
   public open var a: Double
   public open var b: Double
   public open var c: Double
   public open var l: Double
   public open var k: IG
   public open var e: Int
   public open var d: Double
   public open var f: Double
   public open var g: Int
   public open var h: ID
   public open var n: Boolean

   init {
      this.a = var1
      this.b = var2
      this.c = var3
      this.h = var4
      this.i = var6
      this.j = var8
      this.k = var10
      this.l = var12
      this.d = var14
      this.m = var15
      this.n = var16
      this.e = var18
      this.f = var20
      this.g = var21
      this.o = var22
   }

   open fun Iz() {
      this(0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, null, 0, 0.0, 0.0, 0, null, false, 32767, null)
   }
}
