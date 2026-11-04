package org.valkyrienskies.core.impl.shadow

import org.joml.Vector3d
import org.joml.Vector3dc

public data class De(p0: Boolean, p1: Vector3d, p2: Double) : Df {
   public final var b: Boolean
   public final var a: Vector3d
   public final var c: Double

   init {
      this.b = var1
      this.c = var2
      this.d = var3
   }

   public open val e: Boolean
      public open get() {
         return this.b
      }


   public open val d: Vector3dc
      public open get() {
         if (this.b) {
            throw Da("Cannot access collisionAxis because we are colliding!")
         } else if (this.d == java.lang.Double.POSITIVE_INFINITY) {
            throw Dl("Cannot access collisionAxis because we will never collide!")
         } else {
            return this.c
         }
      }


   public open val f: Double
      public open get() {
         if (this.b) {
            throw Da("Cannot access timeToCollision because we are colliding!")
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
   public fun a(p0: Boolean = ..., p1: Vector3d = ..., p2: Double = ...): De {
      return De(var0, var1, var2)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: De, var1: Boolean, var2: Vector3d, var3: Double, var5: Int): De {
      if ((var5 and 1) != 0) {
         var1 = var0.b
      }

      if ((var5 and 2) != 0) {
         var2 = var0.c
      }

      if ((var5 and 4) != 0) {
         var3 = var0.d
      }

      De(var1, var2, var3)
   }

   public override fun toString(): String {
      return "CollisionResultTimeToCollision(_initiallyColliding=${this.b}, _collisionAxis=${this.c}, _timeToCollision=${this.d})"
   }

   public override fun hashCode(): Int {
      return (java.lang.Boolean.hashCode(this.b) * 31 + this.c.hashCode()) * 31 + java.lang.Double.hashCode(this.d)
   }

   public override operator fun equals(p0: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is De && this.b == (other as De).b && this.c == (other as De).c && java.lang.Double.compare(this.d, (other as De).d) == 0
      }
   }

   public companion object a {
      @JvmStatic
      public fun a(): De {
         return De(false, Vector3d(), java.lang.Double.POSITIVE_INFINITY)
      }
   }
}
