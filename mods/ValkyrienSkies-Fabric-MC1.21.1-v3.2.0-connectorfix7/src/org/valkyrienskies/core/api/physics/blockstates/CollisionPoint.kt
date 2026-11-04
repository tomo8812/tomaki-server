package org.valkyrienskies.core.api.physics.blockstates

import org.valkyrienskies.core.api.util.PhysTickOnly

@PhysTickOnly
public data class CollisionPoint(x: Double, y: Double, z: Double, radius: Double) {
   public final val x: Double
   public final val y: Double
   public final val z: Double
   public final val radius: Double

   init {
      this.x = x
      this.y = y
      this.z = z
      this.radius = radius
   }

   public constructor(x: Float, y: Float, z: Float, radius: Float) : this((double)x, (double)y, (double)z, (double)radius)
   public operator fun component1(): Double {
      return this.x
   }

   public operator fun component2(): Double {
      return this.y
   }

   public operator fun component3(): Double {
      return this.z
   }

   public operator fun component4(): Double {
      return this.radius
   }

   public fun copy(x: Double = this.x, y: Double = this.y, z: Double = this.z, radius: Double = this.radius): CollisionPoint {
      return CollisionPoint(x, y, z, radius)
   }

   public override fun toString(): String {
      return "CollisionPoint(x=${this.x}, y=${this.y}, z=${this.z}, radius=${this.radius})"
   }

   public override fun hashCode(): Int {
      return ((java.lang.Double.hashCode(this.x) * 31 + java.lang.Double.hashCode(this.y)) * 31 + java.lang.Double.hashCode(this.z)) * 31
         + java.lang.Double.hashCode(this.radius)
      }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is CollisionPoint
            && java.lang.Double.compare(this.x, (other as CollisionPoint).x) == 0
            && java.lang.Double.compare(this.y, (other as CollisionPoint).y) == 0
            && java.lang.Double.compare(this.z, (other as CollisionPoint).z) == 0
            && java.lang.Double.compare(this.radius, (other as CollisionPoint).radius) == 0
         }
   }
}
