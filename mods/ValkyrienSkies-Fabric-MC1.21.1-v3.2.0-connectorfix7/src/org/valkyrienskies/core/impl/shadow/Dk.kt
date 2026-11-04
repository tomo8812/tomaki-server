package org.valkyrienskies.core.impl.shadow

import java.util.ArrayList
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.internal.collision.VsiConvexPolygonc
import org.valkyrienskies.core.internal.collision.VsiEntityPolygonCollider
import org.valkyrienskies.core.util.AABBdUtilKt
import org.valkyrienskies.core.util.VectorConversionsKt

public object Dk : VsiEntityPolygonCollider {
   @JvmStatic
   private Vector3dc b = Vector3d(1.0, 0.0, 0.0);
   @JvmStatic
   private Vector3dc c = Vector3d(0.0, 1.0, 0.0);
   @JvmStatic
   private Vector3dc d = Vector3d(0.0, 0.0, 1.0);
   @JvmStatic
   private Vector3dc[] e;

   public override fun createPolygonFromAABB(aabb: AABBdc, transform: Matrix4dc?, shipFrom: Long?): VsiConvexPolygonc {
      return Dq.a_.a(aabb, transform, shipFrom)
   }

   public override fun adjustEntityMovementForPolygonCollisions(
      movement: Vector3dc,
      entityBoundingBox: AABBdc,
      entityStepHeight: Double,
      collidingPolygons: List<VsiConvexPolygonc>
   ): Pair<Vector3dc, Long?> {
      if (VectorConversionsKt.horizontalLengthSq(movement) > 1.0E-8 && entityStepHeight > 0.0) {
         val var10001: AABBd = entityBoundingBox.translate(
            a$default(this, entityBoundingBox, collidingPolygons, Vector3d(0.0, -0.2, 0.0), 45.0, null, 16).getFirst() as Vector3dc, AABBd()
         )
         if (this.a(var10001, collidingPolygons, 45.0)) {
            val var21: Double = 45.0
            if (!(entityStepHeight >= 0.0)) {
               throw IllegalArgumentException(("StepHeight was $entityStepHeight, which is less than 0.0!").toString())
            }

            var var49: Vector3dc
            val var26: Double = (var49 = this.a(entityBoundingBox, collidingPolygons, Vector3d(0.0, entityStepHeight, 0.0), 45.0, c).getFirst() as Vector3dc)
               .y()
               var var10000: Pair
            if (!(0.0 <= var26) || !(var26 <= entityStepHeight)) {
               var10000 = null
            } else {
               val var53: AABBd = entityBoundingBox.translate(var49, AABBd())
               val var27: Vector3dc = a$default(this, var53, collidingPolygons, Vector3d(movement.x(), 0.0, movement.z()), 45.0, null, 16).getFirst() as Vector3dc
               val var28: Double = var49.y() - movement.y()
               val var10: AABBd = var53.translate(var27, AABBd())
               val var51: Pair
               val var11: Vector3dc = (var51 = this.a(var10, collidingPolygons, Vector3d(0.0, -var28, 0.0), 45.0, c)).getFirst() as Vector3dc
               val var52: java.lang.Long = var51.getSecond() as java.lang.Long
               var49 = Vector3d(var49).add(var27).add(var11)
               val var48: AABBd = entityBoundingBox.translate(var49, AABBd())
               if (!this.a(var48, collidingPolygons, 45.0)) {
                  val var37: Pair = a$default(this, var48, collidingPolygons, Vector3d(0.0, -var28 - var11.y(), 0.0), 45.0, null, 16)
                  val var38: Vector3d
                  val var39: Double = (var38 = Vector3d(var49).add(var37.getFirst() as Vector3dc)).y()
                  if (!(0.0 <= var39) || !(var39 <= entityStepHeight)) {
                     var10000 = null
                  } else {
                     var10000 = Pair(a(var38, movement), var37.getSecond())
                  }
               } else {
                  val var55: Double = var49.y()
                  if (!(0.0 <= var55) || !(var55 <= entityStepHeight)) {
                     var10000 = null
                  } else {
                     var10000 = Pair(a(var49, movement), var52)
                  }
               }
            }

            if (var10000 != null) {
               if (VectorConversionsKt.differenceHorLengthSq(var10000.getFirst() as Vector3dc, movement) < 1.0E-8) {
                  var10000 = var10000
               } else {
                  val var42: Pair = this.a(entityBoundingBox, collidingPolygons, movement, 45.0)
                  if (!(
                     VectorConversionsKt.differenceHorLengthSq(var10000.getFirst() as Vector3dc, movement)
                        <= VectorConversionsKt.differenceHorLengthSq(var42.getFirst() as Vector3dc, movement)
                  )) {
                     return var42
                  }

                  var10000 = var10000
               }

               return var10000
            }
         }
      }

      return this.a(entityBoundingBox, collidingPolygons, movement, 45.0)
   }

   fun a(var1: AABBdc, var2: Vector3dc, var3: Double, var5: MutableList<VsiConvexPolygonc>): Pair<Vector3dc, java.lang.Long> {
      if (VectorConversionsKt.horizontalLengthSq(var2) > 1.0E-8 && var3 > 0.0) {
         val var10001: AABBd = var1.translate(a$default(this, var1, var5, Vector3d(0.0, -0.2, 0.0), 45.0, null, 16).getFirst() as Vector3dc, AABBd())
         if (this.a(var10001, var5, 45.0)) {
            val var10000: Pair = this.a(var1, var5, var2, 45.0, var3)
            if (var10000 == null) {
               this.a(var1, var5, var2, 45.0)
            }

            if (VectorConversionsKt.differenceHorLengthSq(var10000.getFirst() as Vector3dc, var2) < 1.0E-8) {
               var10000
            }

            val var7: Pair = this.a(var1, var5, var2, 45.0)
            if (VectorConversionsKt.differenceHorLengthSq(var10000.getFirst() as Vector3dc, var2)
               <= VectorConversionsKt.differenceHorLengthSq(var7.getFirst() as Vector3dc, var2)) {
               var10000
            }

            var7
         }
      }

      this.a(var1, var5, var2, 45.0)
   }

   fun a(var1: AABBdc, var2: MutableList<VsiConvexPolygonc>, var3: Vector3dc, var4: Double, var6: Double): Pair<Vector3dc, java.lang.Long> {
      if (!(var6 >= 0.0)) {
         throw IllegalArgumentException(("StepHeight was $var6, which is less than 0.0!").toString())
      } else {
         val var25: Vector3dc
         val var9: Double = (var25 = this.a(var1, var2, Vector3d(0.0, var6, 0.0), 45.0, c).getFirst() as Vector3dc).y()
         if (!(0.0 <= var9) || !(var9 <= var6)) {
            null
         } else {
            val var29: AABBd = var1.translate(var25, AABBd())
            val var10: Vector3dc = a$default(this, var29, var2, Vector3d(var3.x(), 0.0, var3.z()), 45.0, null, 16).getFirst() as Vector3dc
            val var11: Double = var25.y() - var3.y()
            val var5: AABBd = var29.translate(var10, AABBd())
            val var27: Pair
            val var8: Vector3dc = (var27 = this.a(var5, var2, Vector3d(0.0, -var11, 0.0), 45.0, c)).getFirst() as Vector3dc
            val var28: java.lang.Long = var27.getSecond() as java.lang.Long
            val var26: Vector3d = Vector3d(var25).add(var10).add(var8)
            var1 = var1.translate(var26, AABBd())
            if (!this.a(var1, var2, 45.0)) {
               val var31: Pair = a$default(this, var1, var2, Vector3d(0.0, -var11 - var8.y(), 0.0), 45.0, null, 16)
               val var21: Vector3d
               val var22: Double = (var21 = Vector3d(var26).add(var31.getFirst() as Vector3dc)).y()
               if (!(0.0 <= var22) || !(var22 <= var6)) {
                  null
               } else {
                  Pair(a(var21, var3), var31.getSecond())
               }
            } else {
               val var20: Double = var26.y()
               if (!(0.0 <= var20) || !(var20 <= var6)) {
                  null
               } else {
                  Pair(a(var26, var3), var28)
               }
            }
         }
      }
   }

   fun a(var1: AABBdc, var2: MutableList<VsiConvexPolygonc>, var3: Vector3dc, var4: Double): Pair<Vector3dc, java.lang.Long> {
      a$default(this, var1, var2, var3, 45.0, null, 16)
   }

   fun a(var1: AABBdc, var2: MutableList<VsiConvexPolygonc>, var3: Vector3dc, var4: Double, var6: Vector3dc): Pair<Vector3dc, java.lang.Long> {
      val var7: AABBdc = a(var1)
      val var8: VsiConvexPolygonc = Dq.a_.a$default(Dq.a_, var1, null, null, 6)
      var2 = CollectionsKt.sortedWith(var2, Dk$b(var7))
      var var22: Vector3dc = Vector3d(var3)
      var var21: java.lang.Long = null

      val var10: java.lang.Iterable
      for (var11 in var10 = var2) {
         if (AABBdUtilKt.intersectsAABB(AABBdUtilKt.extend(AABBd(var8.aabb), var22), var11.aabb)) {
            val var12: java.util.List = a(var11.normals)
            val var13: Df
            val var18: Double
            if ((var18 = if ((var13 = Dn.INSTANCE.a(var8, var11, var22, var12.iterator())).b()) 0.0 else var13.d()) < 1.0) {
               var var10000: Vector3d = var22.mul(var18, Vector3d())
               val var25: Vector3dc = var10000
               var10000 = var22.mul(1.0 - var18, Vector3d())
               var22 = var10000
               var21 = var11.shipFrom
               val var14: AABBd = var1.translate(var25, AABBd())
               val var30: Dq.a_ = Dq.a_
               val var15: Dq = Dq.a_.a$default(var30, var14, null, null, 6)
               if (var6 == null && INSTANCE.a(var14, var2, var4)) {
                  val var26: Vector3dc = Dn.INSTANCE.a(var15, var22, var11, var12.iterator(), Db.a.a(), Db.a.a(), var4, c)
                  if (var22.y() < 0.0 && var26.y() > var22.y() && var26.y() < 0.34 || var22.y() > 0.0 && var26.y() < var22.y() && var26.y() > 0.34) {
                     var10000 = var25.add(var26, Vector3d())
                     var22 = var10000
                     continue
                  }
               }

               val var28: Vector3d
               var22 = var28
            }
         }
      }

      Pair(a(var22, var3), var21)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: Dk, var1: AABBdc, var2: java.util.List, var3: Vector3dc, var4: Double, var6: Vector3dc, var7: Int): Pair {
      var0.a(var1, var2, var3, var4, null)
   }

   @JvmStatic
   fun a(var0: Vector3dc, var1: Vector3dc): Vector3dc {
      if (Math.abs((var0 = Vector3d(var0)).x() - var1.x()) < 1.0E-8) {
         var0.x = var1.x()
      }

      if (Math.abs(var0.y() - var1.y()) < 1.0E-8) {
         var0.y = var1.y()
      }

      if (Math.abs(var0.z() - var1.z()) < 1.0E-8) {
         var0.z = var1.z()
      }

      var0 as Vector3dc
   }

   @JvmStatic
   fun a(var0: AABBdc, var1: Double, var3: Double): AABBdc {
      val var5: Double = var0.maxY() - var0.minY()
      val var7: Double
      AABBd(var0.minX(), var7 + var3 * var5, var0.minZ(), var0.maxX(), var0.minY() + var5 * 0.0, var0.maxZ()) as AABBdc
   }

   fun a(var1: AABBdc, var2: MutableList<VsiConvexPolygonc>, var3: Double): Boolean {
      val var5: Double = (var1.maxY() - var1.minY()) * 0.001
      var var7: AABBdc = a(var1, 0.0, 0.001)
      val var8: Dq = Dq.a_.a$default(Dq.a_, var7, null, null, 6)
      var2 = CollectionsKt.sortedWith(var2, Dk$a(var7))
      var var19: Vector3dc = Vector3d()

      var var11: java.lang.Iterable
      for (var20 in var11 = var2) {
         if (AABBdUtilKt.intersectsAABB(var8.e, var20.aabb)) {
            var19 = Dn.INSTANCE.a(var8, var19, var20, a(var20.normals).iterator(), Db.a.a(), Db.a.a())
         }
      }

      val var21: Double = var5 * Math.tan(Math.toRadians(90.0 - var3))
      var7 = a(var1, 0.0, -0.001)
      val var10000: AABBd = AABBd(var7.minX() + var21, var7.minY(), var7.minZ() + var21, var7.maxX() - var21, var7.maxY(), var7.maxZ() - var21)
         .translate(var19)
         val var10: Dq = Dq.a_.a$default(Dq.a_, var10000, null, null, 6)

      for (var13 in var11 = var2) {
         if (AABBdUtilKt.intersectsAABB(var10.e, var13.aabb)
            && Di.a(Dn.INSTANCE, var10, Vector3d(), var13, a(var13.normals).iterator(), Db.a.a(), Db.a.a(), var3, null, 128, null).lengthSquared() > 1.0E-8) {
            true
         }
      }

      false
   }

   @JvmStatic
   fun a(var0: AABBdc): AABBdc {
      AABBd(var0.minX(), var0.minY(), var0.minZ(), var0.maxX(), var0.minY() + 0.1 * (var0.maxY() - var0.minY()), var0.maxZ()) as AABBdc
   }

   @JvmStatic
   fun a(var0: MutableIterable<Vector3dc>): MutableList<Vector3dc> {
      val var1: ArrayList = ArrayList()

      for (var5 in e) {
         var1.add(var5)
      }

      for (var9 in var0) {
         var1.add(var9)

         for (var6 in e) {
            val var10000: Vector3d = var9.cross(var6, Vector3d()).normalize()
            if ((var6 = var10000).lengthSquared() > 1.0E-6) {
               var1.add(var6)
            }
         }
      }

      var1 as java.util.List
   }

   @JvmStatic
   fun {
      val var0: Array<Vector3dc>
      (var0 = arrayOfNulls(3))[0] = b
      var0[1] = c
      var0[2] = d
      e = var0
   }
}
