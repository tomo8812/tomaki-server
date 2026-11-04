package org.valkyrienskies.core.impl.shadow

import org.joml.Vector3d
import org.joml.Vector3dc

public data class Dd(p0: Boolean, p1: Vector3d, p2: Double) : Dg {
   public final var a: Boolean
   public final val b: Vector3d
   public final var c: Double

   init {
      this.b = var1
      this.c = var2
      this.d = var3
   }

   public open val d: Boolean
      public open get() {
         return this.b
      }


   public open val e: Vector3dc
      public open get() {
         if (!this.b) {
            throw Dm("Cannot access collisionAxis because we are not colliding.")
         } else {
            return this.c
         }
      }


   public open val f: Double
      public open get() {
         if (!this.b) {
            throw Dm("Cannot access collisionRange because we are not colliding.")
         } else {
            return this.d
         }
      }


   public operator fun g(): Boolean {
      return this.b
   }

   public operator fun h(): Vector3d {
      return this.c
   }

   public operator fun i(): Double {
      return this.d
   }

   @JvmStatic
   public fun a(p0: Boolean = ..., p1: Vector3d = ..., p2: Double = ...): Dd {
      return Dd(var0, var1, var2)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: Dd, var1: Boolean, var2: Vector3d, var3: Double, var5: Int): Dd {
      if ((var5 and 1) != 0) {
         var1 = var0.b
      }

      if ((var5 and 2) != 0) {
         var2 = var0.c
      }

      if ((var5 and 4) != 0) {
         var3 = var0.d
      }

      Dd(var1, var2, var3)
   }

   public override fun toString(): String {
      return "CollisionResult(_colliding=${this.b}, _collisionAxis=${this.c}, _penetrationOffset=${this.d})"
   }

   public override fun hashCode(): Int {
      return (java.lang.Boolean.hashCode(this.b) * 31 + this.c.hashCode()) * 31 + java.lang.Double.hashCode(this.d)
   }

   public override operator fun equals(p0: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is Dd && this.b == (other as Dd).b && this.c == (other as Dd).c && java.lang.Double.compare(this.d, (other as Dd).d) == 0
      }
   }

   public companion object a {
      @JvmStatic
      public fun a(): Dd {
         return Dd(false, Vector3d(), 0.0)
      }
   }
}
