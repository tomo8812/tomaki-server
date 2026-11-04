package org.valkyrienskies.core.impl.shadow

import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.internal.collision.VsiConvexPolygonc

public open class Dh(p0: List<Vector3dc>, p1: List<Vector3dc>, p2: Long? = null, p3: AABBdc) : VsiConvexPolygonc {
   private final val c: List<Vector3dc>
   private final val b: List<Vector3dc>
   private final val d: Long?
   private final val a: AABBdc

   init {
      this.b = var1
      this.c = var2
      this.d = var3
      this.e = var4
   }

   public open val g: Iterable<Vector3dc>
      public open get() {
         return this.b
      }


   public open val f: Iterable<Vector3dc>
      public open get() {
         return this.c
      }


   public open val h: Long?
      public open get() {
         return this.d
      }


   public open val e: AABBdc
      public open get() {
         return this.e
      }


   public companion object a {
      @JvmStatic
      public fun a(p0: List<Vector3dc>, p1: List<Vector3dc>, p2: AABBdc): Dh {
         return Dh(var0, var1, null, var2)
      }
   }
}
