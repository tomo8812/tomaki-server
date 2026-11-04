package org.valkyrienskies.core.impl.shadow

import java.nio.ByteBuffer
import java.util.EnumMap
import java.util.EnumSet
import java.util.Map.Entry
import kotlin.jvm.internal.Intrinsics
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3f

public object IH {
   fun a(var1: HN, var2: ByteBuffer) {
      var2.put(II.a(var1.a() != null))
      var var10000: Int = var1.a()
      if (var10000 != null) {
         var2.putInt(var10000.intValue())
      }

      a(var1.b(), var2)
      var2.put(II.a(var1.c() != null))
      var10000 = var1.c()
      if (var10000 != null) {
         var2.putInt(var10000.intValue())
      }

      a(var1.d(), var2)
      var2.put(II.a(var1.e() != null))
      val var10: HP = var1.e()
      if (var10 != null) {
         a(var10, var2)
      }

      var2.putDouble(var1.o())
   }

   fun a(var1: ByteBuffer): Triple<Pair<Integer, Hf>, Pair<Integer, Hf>, Pair<HP, java.lang.Double>> {
      Triple(
         Pair(if (II.a(var1.get())) var1.getInt() else null, j(var1)),
         Pair(if (II.a(var1.get())) var1.getInt() else null, j(var1)),
         Pair(if (II.a(var1.get())) v(var1) else null, var1.getDouble())
      )
   }

   fun a(var1: HK, var2: ByteBuffer) {
      this.a(var1, var2)
   }

   fun b(var1: ByteBuffer): HK {
      val var6: Triple
      val var2: Pair = (var6 = this.a(var1)).component1() as Pair
      val var3: Pair = var6.component2() as Pair
      val var7: Pair = var6.component3() as Pair
      HK(
         var2.getFirst() as Int,
         var2.getSecond() as Hf,
         var3.getFirst() as Int,
         var3.getSecond() as Hf,
         var7.getFirst() as HP,
         (var7.getSecond() as java.lang.Number).doubleValue()
      )
   }

   fun a(var1: HJ, var2: ByteBuffer) {
      this.a(var1, var2)
      var2.put(II.a(var1.b != null))
      if (var1.b != null) {
         var2.putFloat(var1.b.floatValue())
      }

      var2.put(II.a(var1.c != null))
      if (var1.c != null) {
         var2.putFloat(var1.c.floatValue())
      }

      var2.put(II.a(var1.d != null))
      if (var1.d != null) {
         var2.putFloat(var1.d.floatValue())
      }

      var2.put(II.a(var1.e != null))
      if (var1.e != null) {
         var2.putFloat(var1.e.floatValue())
      }

      var2.put(II.a(var1.f != null))
      if (var1.f != null) {
         var2.putFloat(var1.f.floatValue())
      }
   }

   fun c(var1: ByteBuffer): HJ {
      val var2: Triple
      val var3: Pair = (var2 = this.a(var1)).component1() as Pair
      val var4: Pair = var2.component2() as Pair
      val var13: Pair = var2.component3() as Pair
      HJ(
         var3.getFirst() as Int,
         var3.getSecond() as Hf,
         var4.getFirst() as Int,
         var4.getSecond() as Hf,
         var13.getFirst() as HP,
         if (II.a(var1.get())) var1.getFloat() else null,
         if (II.a(var1.get())) var1.getFloat() else null,
         if (II.a(var1.get())) var1.getFloat() else null,
         if (II.a(var1.get())) var1.getFloat() else null,
         if (II.a(var1.get())) var1.getFloat() else null,
         (var13.getSecond() as java.lang.Number).doubleValue()
      )
   }

   fun a(var1: HT, var2: ByteBuffer) {
      this.a(var1, var2)
      var2.put(II.a(var1.b != null))
      if (var1.b != null) {
         val var4: HI.i = var1.b
         a(var1.b, var2)
      }
   }

   fun d(var1: ByteBuffer): HT {
      val var2: Triple
      val var3: Pair = (var2 = this.a(var1)).component1() as Pair
      val var4: Pair = var2.component2() as Pair
      val var9: Pair = var2.component3() as Pair
      HT(
         var3.getFirst() as Int,
         var3.getSecond() as Hf,
         var4.getFirst() as Int,
         var4.getSecond() as Hf,
         var9.getFirst() as HP,
         if (II.a(var1.get())) n(var1) else null,
         (var9.getSecond() as java.lang.Number).doubleValue()
      )
   }

   fun a(var1: HS, var2: ByteBuffer) {
      this.a(var1, var2)
      var2.put(II.a(var1.b != null))
      if (var1.b != null) {
         val var4: HI.a = var1.b
         a(var1.b, var2)
      }

      var2.put(II.a(var1.c != null))
      if (var1.c != null) {
         val var6: HS.a = var1.c
         a(var1.c, var2)
      }

      var2.put(II.a(var1.d != null))
      if (var1.d != null) {
         var2.putFloat(var1.d.floatValue())
      }

      var2.put(II.a(var1.e != null))
      if (var1.e != null) {
         var2.putFloat(var1.e.floatValue())
      }

      var2.put(II.a(var1.f != null))
      if (var1.f != null) {
         var2.put(II.a(var1.f))
      }
   }

   fun e(var1: ByteBuffer): HS {
      val var2: Triple
      val var3: Pair = (var2 = this.a(var1)).component1() as Pair
      val var4: Pair = var2.component2() as Pair
      val var13: Pair = var2.component3() as Pair
      HS(
         var3.getFirst() as Int,
         var3.getSecond() as Hf,
         var4.getFirst() as Int,
         var4.getSecond() as Hf,
         var13.getFirst() as HP,
         if (II.a(var1.get())) m(var1) else null,
         if (II.a(var1.get())) t(var1) else null,
         if (II.a(var1.get())) var1.getFloat() else null,
         if (II.a(var1.get())) var1.getFloat() else null,
         if (II.a(var1.get())) II.a(var1.get()) else null,
         (var13.getSecond() as java.lang.Number).doubleValue()
      )
   }

   fun a(var1: HQ, var2: ByteBuffer) {
      this.a(var1, var2)
      var2.put(II.a(var1.b != null))
      if (var1.b != null) {
         val var4: HI.l = var1.b
         a(var1.b, var2)
      }
   }

   fun f(var1: ByteBuffer): HQ {
      val var2: Triple
      val var3: Pair = (var2 = this.a(var1)).component1() as Pair
      val var4: Pair = var2.component2() as Pair
      val var9: Pair = var2.component3() as Pair
      HQ(
         var3.getFirst() as Int,
         var3.getSecond() as Hf,
         var4.getFirst() as Int,
         var4.getSecond() as Hf,
         var9.getFirst() as HP,
         if (II.a(var1.get())) l(var1) else null,
         (var9.getSecond() as java.lang.Number).doubleValue()
      )
   }

   fun a(var1: HL, var2: ByteBuffer) {
      this.a(var1, var2)
      var2.put(II.a(var1.b != null))
      if (var1.b != null) {
         val var4: HI.h = var1.b
         a(var1.b, var2)
      }

      var2.put(II.a(var1.c != null))
      if (var1.c != null) {
         var2.putFloat(var1.c.floatValue())
      }
   }

   fun g(var1: ByteBuffer): HL {
      val var2: Triple
      val var3: Pair = (var2 = this.a(var1)).component1() as Pair
      val var4: Pair = var2.component2() as Pair
      val var10: Pair = var2.component3() as Pair
      HL(
         var3.getFirst() as Int,
         var3.getSecond() as Hf,
         var4.getFirst() as Int,
         var4.getSecond() as Hf,
         var10.getFirst() as HP,
         if (II.a(var1.get())) p(var1) else null,
         if (II.a(var1.get())) var1.getFloat() else null,
         (var10.getSecond() as java.lang.Number).doubleValue()
      )
   }

   fun a(var1: HR, var2: ByteBuffer) {
      this.a(var1, var2)
      var2.put(II.a(var1.b != null))
      if (var1.b != null) {
         val var4: HI.h = var1.b
         a(var1.b, var2)
      }

      var2.put(II.a(var1.c != null))
      if (var1.c != null) {
         var2.putFloat(var1.c.floatValue())
      }

      var2.put(II.a(var1.d != null))
      if (var1.d != null) {
         val var8: HR.a = var1.d
         a(var1.d, var2)
      }
   }

   fun h(var1: ByteBuffer): HR {
      val var2: Triple
      val var3: Pair = (var2 = this.a(var1)).component1() as Pair
      val var4: Pair = var2.component2() as Pair
      val var11: Pair = var2.component3() as Pair
      HR(
         var3.getFirst() as Int,
         var3.getSecond() as Hf,
         var4.getFirst() as Int,
         var4.getSecond() as Hf,
         var11.getFirst() as HP,
         if (II.a(var1.get())) p(var1) else null,
         if (II.a(var1.get())) var1.getFloat() else null,
         if (II.a(var1.get())) q(var1) else null,
         (var11.getSecond() as java.lang.Number).doubleValue()
      )
   }

   fun a(var1: HI, var2: ByteBuffer) {
      this.a(var1, var2)
      a(var1.b, var2)
      var2.put(II.a(var1.c != null))
      if (var1.c != null) {
         val var4: HI.k = var1.c
         a(var1.c, var2)
      }

      a(var1.d, var2)
      var2.put(II.a(var1.e != null))
      if (var1.e != null) {
         val var6: HI.a = var1.e
         a(var1.e, var2)
      }

      var2.put(II.a(var1.f != null))
      if (var1.f != null) {
         val var8: HI.i = var1.f
         a(var1.f, var2)
      }

      var2.put(II.a(var1.g != null))
      if (var1.g != null) {
         val var10: HI.j = var1.g
         a(var1.g, var2)
      }

      a(var1.h, var2)
      var2.put(II.a(var1.i != null))
      if (var1.i != null) {
         INSTANCE.a(var1.i, var2)
      }

      var2.put(II.a(var1.j != null))
      if (var1.j != null) {
         val var14: HI.g = var1.j
         a(var1.j, var2)
      }
   }

   fun i(var1: ByteBuffer): HI {
      val var2: Triple
      val var3: Pair = (var2 = this.a(var1)).component1() as Pair
      val var4: Pair = var2.component2() as Pair
      val var19: Pair = var2.component3() as Pair
      val var5: Int = var3.getFirst() as Int
      val var20: Hf = var3.getSecond() as Hf
      val var6: Int = var4.getFirst() as Int
      val var21: Hf = var4.getSecond() as Hf
      var var10000: EnumMap
      if (var1.get() == 0) {
         var10000 = null
      } else {
         val var10: Int = var1.getInt()
         val var11: EnumMap = EnumMap<>(HI.b::class.java)
         var var12: Int = 0

         while (var12 < var10) {
            var12++
            val var13: java.lang.Enum = HI.b.class.getEnumConstants()[var1.getInt()]
            val var37: Any
            if (HI.e::class.java == HI.e::class.java) {
               var37 = HI.e.values()[var1.getInt()]
            } else if (HI.e::class.java == HI.l::class.java) {
               var37 = l(var1)
            } else {
               if (!(HI.e::class.java == HI.d::class.java)) {
                  throw IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", HI.e.class))
               }

               var37 = r(var1)
            }

            if (var37 == null) {
               throw NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.joints.D6Joint.D6Motion")
            }

            var11.put(var13, var37 as HI.e)
         }

         var10000 = var11
      }

      val var8: HI.k = if (II.a(var1.get())) k(var1) else null
      if (var1.get() == 0) {
         var10000 = null
      } else {
         val var26: Int = var1.getInt()
         val var28: EnumMap = EnumMap<>(HI.b::class.java)
         var var31: Int = 0

         while (var31 < var26) {
            var31++
            val var34: java.lang.Enum = HI.b.class.getEnumConstants()[var1.getInt()]
            val var39: Any
            if (HI.l::class.java == HI.e::class.java) {
               var39 = HI.e.values()[var1.getInt()]
            } else if (HI.l::class.java == HI.l::class.java) {
               var39 = l(var1)
            } else {
               if (!(HI.l::class.java == HI.d::class.java)) {
                  throw IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", HI.l.class))
               }

               var39 = r(var1)
            }

            if (var39 == null) {
               throw NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.joints.D6Joint.LinearLimitPair")
            }

            var28.put(var34, var39 as HI.l)
         }

         var10000 = var28
      }

      val var24: HI.a = if (II.a(var1.get())) m(var1) else null
      val var25: HI.i = if (II.a(var1.get())) n(var1) else null
      val var27: HI.j = if (II.a(var1.get())) o(var1) else null
      if (var1.get() == 0) {
         var10000 = null
      } else {
         val var29: Int = var1.getInt()
         val var32: EnumMap = EnumMap<>(HI.c::class.java)
         var var35: Int = 0

         while (var35 < var29) {
            var35++
            val var16: java.lang.Enum = HI.c.class.getEnumConstants()[var1.getInt()]
            val var41: Any
            if (HI.d::class.java == HI.e::class.java) {
               var41 = HI.e.values()[var1.getInt()]
            } else if (HI.d::class.java == HI.l::class.java) {
               var41 = l(var1)
            } else {
               if (!(HI.d::class.java == HI.d::class.java)) {
                  throw IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", HI.d.class))
               }

               var41 = r(var1)
            }

            if (var41 == null) {
               throw NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.joints.D6Joint.D6JointDrive")
            }

            var32.put(var16, var41 as HI.d)
         }

         var10000 = var32
      }

      HI(
         var5,
         var20,
         var6,
         var21,
         var19.getFirst() as HP,
         var10000,
         var8,
         var10000,
         var24,
         var25,
         var27,
         var10000,
         if (II.a(var1.get())) this.w(var1) else null,
         if (II.a(var1.get())) s(var1) else null,
         (var19.getSecond() as java.lang.Number).doubleValue()
      )
   }

   @JvmStatic
   fun a(var0: Int, var1: ByteBuffer) {
      var1.putInt(var0)
   }

   @JvmStatic
   fun u(var0: ByteBuffer): Int {
      var0.getInt()
   }

   @JvmStatic
   fun a(var0: Hf, var1: ByteBuffer) {
      var1.putDouble(var0.a.x())
      var1.putDouble(var0.a.y())
      var1.putDouble(var0.a.z())
      var1.putDouble(var0.b.w())
      var1.putDouble(var0.b.x())
      var1.putDouble(var0.b.y())
      var1.putDouble(var0.b.z())
   }

   @JvmStatic
   fun j(var0: ByteBuffer): Hf {
      Hf(Vector3d(var0.getDouble(), var0.getDouble(), var0.getDouble()), Quaterniond(var0.getDouble(), var0.getDouble(), var0.getDouble(), var0.getDouble()))
   }

   @JvmStatic
   fun a(var0: HP, var1: ByteBuffer) {
      var1.putFloat(var0.a)
      var1.putFloat(var0.b)
   }

   @JvmStatic
   fun v(var0: ByteBuffer): HP {
      HP(var0.getFloat(), var0.getFloat())
   }

   @JvmStatic
   fun a(var0: HI.k, var1: ByteBuffer) {
      var1.putFloat(var0.a)
      var1.put(II.a(var0.b != null))
      if (var0.b != null) {
         var1.putFloat(var0.b.floatValue())
      }

      var1.put(II.a(var0.c != null))
      if (var0.c != null) {
         var1.putFloat(var0.c.floatValue())
      }
   }

   @JvmStatic
   fun k(var0: ByteBuffer): HI.k {
      HI.k(var0.getFloat(), if (II.a(var0.get())) var0.getFloat() else null, if (II.a(var0.get())) var0.getFloat() else null)
   }

   @JvmStatic
   fun a(var0: HI.l, var1: ByteBuffer) {
      var1.putFloat(var0.a)
      var1.putFloat(var0.b)
      var1.put(II.a(var0.c != null))
      if (var0.c != null) {
         var1.putFloat(var0.c.floatValue())
      }

      var1.put(II.a(var0.d != null))
      if (var0.d != null) {
         var1.putFloat(var0.d.floatValue())
      }

      var1.put(II.a(var0.e != null))
      if (var0.e != null) {
         var1.putFloat(var0.e.floatValue())
      }

      var1.put(II.a(var0.f != null))
      if (var0.f != null) {
         var1.putFloat(var0.f.floatValue())
      }
   }

   @JvmStatic
   fun l(var0: ByteBuffer): HI.l {
      HI.l(
         var0.getFloat(),
         var0.getFloat(),
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null
      )
   }

   @JvmStatic
   fun a(var0: HI.a, var1: ByteBuffer) {
      var1.putFloat(var0.a)
      var1.putFloat(var0.b)
      var1.put(II.a(var0.c != null))
      if (var0.c != null) {
         var1.putFloat(var0.c.floatValue())
      }

      var1.put(II.a(var0.d != null))
      if (var0.d != null) {
         var1.putFloat(var0.d.floatValue())
      }

      var1.put(II.a(var0.e != null))
      if (var0.e != null) {
         var1.putFloat(var0.e.floatValue())
      }

      var1.put(II.a(var0.f != null))
      if (var0.f != null) {
         var1.putFloat(var0.f.floatValue())
      }
   }

   @JvmStatic
   fun m(var0: ByteBuffer): HI.a {
      HI.a(
         var0.getFloat(),
         var0.getFloat(),
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null
      )
   }

   @JvmStatic
   fun a(var0: HI.i, var1: ByteBuffer) {
      var1.putFloat(var0.a)
      var1.putFloat(var0.b)
      var1.put(II.a(var0.c != null))
      if (var0.c != null) {
         var1.putFloat(var0.c.floatValue())
      }

      var1.put(II.a(var0.d != null))
      if (var0.d != null) {
         var1.putFloat(var0.d.floatValue())
      }

      var1.put(II.a(var0.e != null))
      if (var0.e != null) {
         var1.putFloat(var0.e.floatValue())
      }

      var1.put(II.a(var0.f != null))
      if (var0.f != null) {
         var1.putFloat(var0.f.floatValue())
      }
   }

   @JvmStatic
   fun n(var0: ByteBuffer): HI.i {
      HI.i(
         var0.getFloat(),
         var0.getFloat(),
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null
      )
   }

   @JvmStatic
   fun a(var0: HI.j, var1: ByteBuffer) {
      var1.putFloat(var0.a)
      var1.putFloat(var0.b)
      var1.putFloat(var0.c)
      var1.putFloat(var0.d)
      var1.put(II.a(var0.e != null))
      if (var0.e != null) {
         var1.putFloat(var0.e.floatValue())
      }

      var1.put(II.a(var0.f != null))
      if (var0.f != null) {
         var1.putFloat(var0.f.floatValue())
      }

      var1.put(II.a(var0.g != null))
      if (var0.g != null) {
         var1.putFloat(var0.g.floatValue())
      }

      var1.put(II.a(var0.h != null))
      if (var0.h != null) {
         var1.putFloat(var0.h.floatValue())
      }
   }

   @JvmStatic
   fun o(var0: ByteBuffer): HI.j {
      HI.j(
         var0.getFloat(),
         var0.getFloat(),
         var0.getFloat(),
         var0.getFloat(),
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null,
         if (II.a(var0.get())) var0.getFloat() else null
      )
   }

   @JvmStatic
   fun a(var0: HI.h, var1: ByteBuffer) {
      var1.putInt(var0.a)
      var1.putInt(var0.b)
   }

   @JvmStatic
   fun p(var0: ByteBuffer): HI.h {
      HI.h(var0.getInt(), var0.getInt())
   }

   @JvmStatic
   fun a(var0: HR.a, var1: ByteBuffer) {
      var1.putInt(var0.a)
      var1.putInt(var0.b)
      var1.putFloat(var0.c)
   }

   @JvmStatic
   fun q(var0: ByteBuffer): HR.a {
      HR.a(var0.getInt(), var0.getInt(), var0.getFloat())
   }

   @JvmStatic
   fun a(var0: HI.d, var1: ByteBuffer) {
      var1.putFloat(var0.a)
      var1.putFloat(var0.b)
      var1.putFloat(var0.c)
      var1.put(II.a(var0.d))
   }

   @JvmStatic
   fun r(var0: ByteBuffer): HI.d {
      HI.d(var0.getFloat(), var0.getFloat(), var0.getFloat(), II.a(var0.get()))
   }

   fun a(var1: HI.f, var2: ByteBuffer) {
      a(var1.a, var2)
      val var3: Boolean = var1.b
      var2.put(II.a(var1.b))
   }

   fun w(var1: ByteBuffer): HI.f {
      HI.f(j(var1), II.a(var1.get()))
   }

   @JvmStatic
   fun a(var0: HI.g, var1: ByteBuffer) {
      var1.putFloat(var0.a.x())
      var1.putFloat(var0.a.y())
      var1.putFloat(var0.a.z())
      var1.putFloat(var0.b.x())
      var1.putFloat(var0.b.y())
      var1.putFloat(var0.b.z())
      val var2: Boolean = var0.c
      var1.put(II.a(var0.c))
   }

   @JvmStatic
   fun s(var0: ByteBuffer): HI.g {
      HI.g(Vector3f(var0.getFloat(), var0.getFloat(), var0.getFloat()), Vector3f(var0.getFloat(), var0.getFloat(), var0.getFloat()), II.a(var0.get()))
   }

   @JvmStatic
   fun a(var0: HS.a, var1: ByteBuffer) {
      var1.putFloat(var0.a)
      val var2: Boolean = var0.b
      var1.put(II.a(var0.b))
   }

   @JvmStatic
   fun t(var0: ByteBuffer): HS.a {
      HS.a(var0.getFloat(), II.a(var0.get()))
   }

   @JvmStatic
   fun <K extends java.lang.Enum<K>, V> a(var0: MutableMap<K, V>, var1: ByteBuffer) {
      if (var0 != null) {
         var1.put((byte)1)
         var1.putInt(var0.size())
         val var4: java.util.Iterator = var0.entrySet().iterator()

         while (var4.hasNext()) {
            val var2: Entry
            val var3: java.lang.Enum = (var2 = var4.next() as Entry).getKey() as java.lang.Enum
            val var5: Any = var2.getValue()
            var1.putInt(var3.ordinal())
            if (var5 is HI.e) {
               var1.putInt((var5 as HI.e).ordinal())
            } else if (var5 is HI.l) {
               a(var5 as HI.l, var1)
            } else if (var5 is HI.d) {
               a(var5 as HI.d, var1)
            }
         }
      } else {
         var1.put((byte)0)
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun <K extends java.lang.Enum<K>, V> x(var0: ByteBuffer): EnumMap<K, V> {
      if (var0.get() == 0) {
         null
      } else {
         val var1: Int = var0.getInt()
         Intrinsics.reifiedOperationMarker(4, "K")
         val var2: EnumMap = EnumMap((Class<K>)java.lang.Enum::class.java)
         var var3: Int = 0

         while (var3 < var1) {
            var3++
            Intrinsics.reifiedOperationMarker(4, "K")
            val var4: java.lang.Enum = java.lang.Enum::class.java.getEnumConstants()[var0.getInt()]
            Intrinsics.reifiedOperationMarker(4, "V")
            val var5: Class
            val var10000: Any
            if ((var5 = Object::class.java) == HI.e::class.java) {
               var10000 = HI.e.values()[var0.getInt()]
            } else if (var5 == HI.l::class.java) {
               var10000 = l(var0)
            } else {
               if (!(var5 == HI.d::class.java)) {
                  Intrinsics.reifiedOperationMarker(4, "V")
                  throw IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", Object.class))
               }

               var10000 = r(var0)
            }

            Intrinsics.reifiedOperationMarker(1, "V")
            var2.put(var4, var10000)
         }

         var2
      }
   }

   @JvmStatic
   fun <K extends java.lang.Enum<K>> a(var0: EnumSet<K>, var1: ByteBuffer) {
      if (var0 == null) {
         var1.put((byte)0)
      } else {
         var1.put((byte)1)
         var1.putInt(var0.size())

         for (var2 in var3 = var0) {
            var1.putInt(var2.ordinal())
         }
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun <K extends java.lang.Enum<K>> y(var0: ByteBuffer): EnumSet<K> {
      if (var0.get() == 0) {
         null
      } else {
         val var1: Int = var0.getInt()
         Intrinsics.reifiedOperationMarker(4, "K")
         val var2: EnumSet = EnumSet.noneOf(java.lang.Enum::class.java)
         var var3: Int = 0

         while (var3 < var1) {
            var3++
            Intrinsics.reifiedOperationMarker(4, "K")
            var2.add(java.lang.Enum::class.java.getEnumConstants()[var0.getInt()])
         }

         var2
      }
   }
}
