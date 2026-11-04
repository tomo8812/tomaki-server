package org.valkyrienskies.core.impl.shadow

import java.util.ArrayList
import kotlin.jvm.internal.DefaultConstructorMarker
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc

public class Dq private constructor(_points: List<Vector3d>, _normals: List<Vector3d>, _shipFrom: Long? = null, _aabb: AABBd) : Dh(var1, var2, var3, var4) {
   @JvmStatic
   public Dq.a_ a_ = Dq.a_(null);
   private java.util.List<Vector3d> b;
   private java.util.List<Vector3d> c;
   private AABBd d;
   @JvmStatic
   private int e = 8;
   @JvmStatic
   private int f = 3;

   init {
      this.b = var1
      this.c = var2
      this.d = var4
   }

   fun a(var1: AABBdc, var2: Matrix4dc): Dq {
      this.b.get(0).set(var1.minX(), var1.minY(), var1.minZ())
      this.b.get(1).set(var1.minX(), var1.minY(), var1.maxZ())
      this.b.get(2).set(var1.minX(), var1.maxY(), var1.minZ())
      this.b.get(3).set(var1.minX(), var1.maxY(), var1.maxZ())
      this.b.get(4).set(var1.maxX(), var1.minY(), var1.minZ())
      this.b.get(5).set(var1.maxX(), var1.minY(), var1.maxZ())
      this.b.get(6).set(var1.maxX(), var1.maxY(), var1.minZ())
      this.b.get(7).set(var1.maxX(), var1.maxY(), var1.maxZ())
      this.c.get(0).set(1.0, 0.0, 0.0)
      this.c.get(1).set(0.0, 1.0, 0.0)
      this.c.get(2).set(0.0, 0.0, 1.0)
      if (var2 != null) {
         var var3: Double = this.b.get(0).x()
         var var5: Double = this.b.get(0).y()
         var var7: Double = this.b.get(0).z()
         var var9: Double = this.b.get(0).x()
         var var11: Double = this.b.get(0).y()
         var var13: Double = this.b.get(0).z()

         for (var15 in this.b) {
            var2.transformPosition(var15)
            var3 = Math.min(var3, var15.x())
            var5 = Math.min(var5, var15.y())
            var7 = Math.min(var7, var15.z())
            var9 = Math.max(var9, var15.x())
            var11 = Math.max(var11, var15.y())
            var13 = Math.max(var13, var15.z())
         }

         this.d.setMin(var3, var5, var7)
         this.d.setMax(var9, var11, var13)

         for (var18 in this.c) {
            var2.transformDirection(var18).normalize()
         }
      } else {
         this.d.set(var1)
      }

      this
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: Dq, var1: AABBdc, var2: Matrix4dc, var3: Int): Dq {
      if ((var3 and 2) != 0) {
         var2 = null
      }

      var0.a(var1, var2)
   }

   @JvmSynthetic
   fun Dq(var1: java.util.List, var2: java.util.List, var3: java.lang.Long, var4: AABBd, var5: DefaultConstructorMarker) {
      this(var1, var2, var3, var4)
   }

   public companion object a_ {
      @JvmStatic
      fun a(var0: java.lang.Long): Dq {
         val var1: ArrayList = ArrayList(8)

         repeat(7) { var2 ->
            var1.add(Vector3d())
         }

         val var5: java.util.List = var1
         val var6: ArrayList = ArrayList(3)

         repeat(2) { var3 ->
            var6.add(Vector3d())
         }

         Dq(var5, var6, var0, AABBd(), null)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: Dq.a_, var1: java.lang.Long, var2: Int): Dq {
         if ((var2 and 1) != 0) {
            var1 = null
         }

         a(var1)
      }

      fun a(var1: AABBdc, var2: Matrix4dc, var3: java.lang.Long): Dq {
         a(var3).a(var1, var2)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: Dq.a_, var1: AABBdc, var2: Matrix4dc, var3: java.lang.Long, var4: Int): Dq {
         var0.a(var1, null, null)
      }
   }
}
