package org.valkyrienskies.core.impl.shadow

import org.joml.Matrix4d
import org.joml.Matrix4dc
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc

public data class Hg(p0: Vector3dc, p1: Quaterniondc, p2: Vector3dc, p3: Vector3dc) {
   public final val c: Vector3dc
   public final val d: Quaterniondc
   public final val f: Vector3dc
   public final val b: Vector3dc
   private final val e: Matrix4dc
   private final val a: Matrix4dc

   init {
      this.b = var1
      this.c = var2
      this.d = var3
      this.e = var4
      val var5: Matrix4d
      this.f = var5
      val var6: Matrix4d
      this.g = var6
   }

   public fun a(p0: Vector3dc, p1: Vector3d): Vector3d {
      var2.set(var1)
      if (this === h) {
         return var2
      } else {
         return var1
      }
   }

   public fun b(p0: Vector3dc, p1: Vector3d): Vector3d {
      var2.set(var1)
      if (this === h) {
         return var2
      } else {
         return var1
      }
   }

   public fun c(p0: Vector3dc, p1: Vector3d): Vector3d {
      var2.set(var1)
      if (this === h) {
         return var2
      } else {
         return var1
      }
   }

   public fun d(p0: Vector3dc, p1: Vector3d): Vector3d {
      var2.set(var1)
      if (this === h) {
         return var2
      } else {
         return var1
      }
   }

   public fun a(p0: Vector3d): Vector3d {
      return this.a(var1, var1)
   }

   public fun b(p0: Vector3d): Vector3d {
      val var10001: Vector3dc = var1
      var1.set(var10001)
      if (this === h) {
         return var1
      } else {
         val var5: Vector3d
         return var5
      }
   }

   public fun c(p0: Vector3d): Vector3d {
      val var10001: Vector3dc = var1
      var1.set(var10001)
      if (this === h) {
         return var1
      } else {
         val var5: Vector3d
         return var5
      }
   }

   public fun d(p0: Vector3d): Vector3d {
      val var10001: Vector3dc = var1
      var1.set(var10001)
      if (this === h) {
         return var1
      } else {
         val var5: Vector3d
         return var5
      }
   }

   public operator fun f(): Vector3dc {
      return this.b
   }

   public operator fun g(): Quaterniondc {
      return this.c
   }

   public operator fun h(): Vector3dc {
      return this.d
   }

   public operator fun i(): Vector3dc {
      return this.e
   }

   @JvmStatic
   public fun a(p0: Vector3dc = ..., p1: Quaterniondc = ..., p2: Vector3dc = ..., p3: Vector3dc = ...): Hg {
      return Hg(var0, var1, var2, var3)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: Hg, var1: Vector3dc, var2: Quaterniondc, var3: Vector3dc, var4: Vector3dc, var5: Int): Hg {
      if ((var5 and 1) != 0) {
         var1 = var0.b
      }

      if ((var5 and 2) != 0) {
         var2 = var0.c
      }

      if ((var5 and 4) != 0) {
         var3 = var0.d
      }

      if ((var5 and 8) != 0) {
         var4 = var0.e
      }

      Hg(var1, var2, var3, var4)
   }

   public override fun toString(): String {
      return "Hg(c=${this.b}, d=${this.c}, f=${this.d}, b=${this.e})"
   }

   public override fun hashCode(): Int {
      return ((this.b.hashCode() * 31 + this.c.hashCode()) * 31 + this.d.hashCode()) * 31 + this.e.hashCode()
   }

   public override operator fun equals(p0: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is Hg && this.b == (other as Hg).b && this.c == (other as Hg).c && this.d == (other as Hg).d && this.e == (other as Hg).e
      }
   }

   public companion object a {
      public final val a: Hg

      @JvmStatic
      public fun a(p0: Vector3dc, p1: Quaterniondc): Hg {
         return Hg(var0, var1, Vector3d(), Vector3d())
      }
   }
}
