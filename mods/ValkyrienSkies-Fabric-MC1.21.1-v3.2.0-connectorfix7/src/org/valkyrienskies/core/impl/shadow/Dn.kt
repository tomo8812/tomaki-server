package org.valkyrienskies.core.impl.shadow

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.internal.collision.VsiConvexPolygonc
import org.valkyrienskies.core.util.VectorConversionsKt

public object Dn : Di {
   public override fun a(p0: VsiConvexPolygonc, p1: VsiConvexPolygonc, p2: Iterator<Vector3dc>, p3: Dd, p4: Db, p5: Db, p6: Vector3dc?) {
      var var8: Double = java.lang.Double.MAX_VALUE
      var4.b = true
      var3 = var3

      while (var3.hasNext()) {
         val var10: Vector3dc = var3.next() as Vector3dc
         val var33: Double
         if (Math.abs(var33 = Dc.a.a(Dj.a(var1, var10, var5), Dj.a(var2, var10, var6))) < 1.0E-6) {
            var4.b = false
            return
         }

         if (var7 != null) {
            val var16: Double
            val var18: Double
            val var34: Double
            if (!(Math.abs(var34 = var7.dot(var10)) < 1.0E-6) && (var18 = Math.abs(var16 = var33 / var34)) < var8) {
               var8 = var18
               var4.c.set(var7)
               var4.d = var16
            }
         } else {
            val var35: Double
            if ((var35 = Math.abs(var33)) < var8) {
               var8 = var35
               var4.c.set(var10)
               var4.d = var33
            }
         }
      }

      if (var8 == java.lang.Double.MAX_VALUE) {
         var4.b = false
      }
   }

   public override fun a(p0: VsiConvexPolygonc, p1: Vector3dc, p2: VsiConvexPolygonc, p3: Iterator<Vector3dc>, p4: Db, p5: Db, p6: Double, p7: Vector3dc?): Vector3dc {
      var var10: Vector3dc = null
      var4 = var4

      while (var4.hasNext()) {
         val var11: Vector3dc = var4.next() as Vector3dc
         val var13: Double
         if ((var13 = a(var1, var2, var3, var11, var5, var6)) == 0.0) {
            return var2
         }

         var var12: Vector3dc = null
         if (var9 != null) {
            val var16: Double
            if (Math.abs(var16 = var11.dot(var9)) < 1.0E-6) {
               continue
            }

            var12 = var2.add(Vector3d(var13 * var9.x() / var16, var13 * var9.y() / var16, var13 * var9.z() / var16), Vector3d())
         } else {
            if (Math.abs(var11.y()) >= Math.sin(Math.toRadians(var7))) {
               val var10000: Vector3dc = var12 = var2.add(Vector3d(0.0, var13 / var11.y(), 0.0), Vector3d())
               val var17: Double
               if ((var17 = VectorConversionsKt.horizontalLength(var10000)) > 1.0E-6 && Math.abs(var12.y()) / var17 >= Math.tan(Math.toRadians(var7))) {
                  var12 = null
               }
            }

            if (var12 == null) {
               val var23: Vector3d
               if (!(var23 = Vector3d(var11.x(), 0.0, var11.z()).normalize()).isFinite()) {
                  continue
               }

               val var25: Double = var11.dot(var23)
               var12 = var2.add(Vector3d(var13 * var23.x() / var25, 0.0, var13 * var23.z() / var25), Vector3d())
            }
         }

         if (var10 == null) {
            var10 = var12
         } else if (Vector3d(var12).sub(var2).lengthSquared() < Vector3d(var10).sub(var2).lengthSquared()) {
            var10 = var12
         }
      }

      return var10
   }

   public override fun a(p0: VsiConvexPolygonc, p1: Vector3dc, p2: VsiConvexPolygonc, p3: Iterator<Vector3dc>, p4: Db, p5: Db): Vector3dc {
      var var7: Vector3dc = null
      var4 = var4

      while (var4.hasNext()) {
         var var8: Vector3dc = var4.next() as Vector3dc
         val var10: Double
         if ((var10 = a(var1, var2, var3, var8, var5, var6)) == 0.0) {
            return var2
         }

         val var9: Vector3d
         if ((var9 = Vector3d(var8.x(), 0.0, var8.z()).normalize()).isFinite()) {
            val var14: Double = var8.dot(var9)
            val var10000: Vector3d = var2.add(Vector3d(var10 * var9.x() / var14, 0.0, var10 * var9.z() / var14), Vector3d())
            var8 = var10000
            if (var7 == null) {
               var7 = var8
            } else if (Vector3d(var8).sub(var2).lengthSquared() < Vector3d(var7).sub(var2).lengthSquared()) {
               var7 = var8
            }
         }
      }

      return var7
   }

   @JvmStatic
   private fun a(p0: Vector3dc, p1: Vector3dc): Double {
      return Vector3d(var0.x(), 0.0, var0.z()).angleCos(Vector3d(var1.x(), 0.0, var1.z()))
   }

   public override fun a(p0: VsiConvexPolygonc, p1: VsiConvexPolygonc, p2: Vector3dc, p3: Iterator<Vector3dc>): Df {
      val var5: Db = Db.a.a()
      val var6: Db = Db.a.a()
      val var7: De = De.a.a()
      var var8: Double = 0.0
      var7.b = true
      var4 = var4

      while (var4.hasNext()) {
         val var10: Vector3dc = var4.next() as Vector3dc
         val var12: Double
         if ((var12 = a(var1, var2, var3, var10, var5, var6)) != 0.0) {
            var7.b = false
            if (var12 > var8) {
               var8 = var12
               var7.c.set(var10)
               var7.d = var12
               if (var12 == java.lang.Double.POSITIVE_INFINITY) {
                  break
               }
            }
         }
      }

      return var7
   }

   @JvmStatic
   public fun a(p0: VsiConvexPolygonc, p1: VsiConvexPolygonc, p2: Vector3dc, p3: Db, p4: Db): Double {
      return Dc.a.a(Dj.a(var0, var2, var3), Dj.a(var1, var2, var4))
   }

   @JvmStatic
   private fun a(p0: VsiConvexPolygonc, p1: Vector3dc, p2: VsiConvexPolygonc, p3: Vector3dc, p4: Db, p5: Db): Double {
      return Dc.a.a(Dj.a(var0, var3, var4), Dj.a(var2, var3, var5), var3.dot(var1))
   }

   @JvmStatic
   public fun a(p0: VsiConvexPolygonc, p1: VsiConvexPolygonc, p2: Vector3dc, p3: Vector3dc, p4: Db, p5: Db): Double {
      return Dc.a.b(Dj.a(var0, var3, var4), Dj.a(var1, var3, var5), var2.dot(var3))
   }
}
