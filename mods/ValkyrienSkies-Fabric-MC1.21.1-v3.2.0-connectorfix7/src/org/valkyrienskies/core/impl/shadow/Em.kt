package org.valkyrienskies.core.impl.shadow

import org.joml.Matrix3dc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData

public data class Em(p0: Double, p1: Matrix3dc, p2: Vector3dc, p3: Boolean = false) : ShipInertiaData {
   public open val e: Double
   public open val f: Matrix3dc
   public open val b: Vector3dc
   public final val c: Boolean

   init {
      this.a = var1
      this.b = var3
      this.c = var4
      this.d = var5
   }

   public open val d: Matrix3dc
      public open get() {
         return this.f
      }


   public open val a: Vector3dc
      public open get() {
         return this.b
      }


   public operator fun b(): Double {
      return this.a
   }

   public operator fun c(): Matrix3dc {
      return this.b
   }

   public operator fun d(): Vector3dc {
      return this.c
   }

   public operator fun e(): Boolean {
      return this.d
   }

   @JvmStatic
   public fun a(p0: Double = ..., p1: Matrix3dc = ..., p2: Vector3dc = ..., p3: Boolean = ...): Em {
      return Em(var0, var2, var3, var4)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: Em, var1: Double, var3: Matrix3dc, var4: Vector3dc, var5: Boolean, var6: Int): Em {
      if ((var6 and 1) != 0) {
         var1 = var0.a
      }

      if ((var6 and 2) != 0) {
         var3 = var0.b
      }

      if ((var6 and 4) != 0) {
         var4 = var0.c
      }

      if ((var6 and 8) != 0) {
         var5 = var0.d
      }

      Em(var1, var3, var4, var5)
   }

   public override fun toString(): String {
      return "PhysInertia(mass=${this.a}, momentOfInertiaTensor=${this.b}, centerOfMassInShip=${this.c}, cmChangedThisTick=${this.d})"
   }

   public override fun hashCode(): Int {
      return ((java.lang.Double.hashCode(this.a) * 31 + this.b.hashCode()) * 31 + this.c.hashCode()) * 31 + java.lang.Boolean.hashCode(this.d)
   }

   public override operator fun equals(p0: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is Em
            && java.lang.Double.compare(this.a, (other as Em).a) == 0
            && this.b == (other as Em).b
            && this.c == (other as Em).c
            && this.d == (other as Em).d
         }
   }
}
