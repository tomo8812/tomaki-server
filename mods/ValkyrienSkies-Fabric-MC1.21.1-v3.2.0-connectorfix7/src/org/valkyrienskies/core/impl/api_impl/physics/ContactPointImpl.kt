package org.valkyrienskies.core.impl.api_impl.physics

import org.joml.Vector3dc
import org.valkyrienskies.core.api.physics.ContactPoint

public data class ContactPointImpl(position: Vector3dc, normal: Vector3dc, separation: Float, velocity: Vector3dc) : ContactPoint {
   public open val position: Vector3dc
   public open val normal: Vector3dc
   public open val separation: Float
   public open val velocity: Vector3dc

   init {
      this.position = position
      this.normal = normal
      this.separation = separation
      this.velocity = velocity
   }

   public override fun toString(): String {
      return "ContactPoint(position=${this.position}, normal=${this.normal}, separation=${this.separation}, velocity=${this.velocity})"
   }

   public operator fun component1(): Vector3dc {
      return this.position
   }

   public operator fun component2(): Vector3dc {
      return this.normal
   }

   public operator fun component3(): Float {
      return this.separation
   }

   public operator fun component4(): Vector3dc {
      return this.velocity
   }

   public fun copy(
      position: Vector3dc = this.position,
      normal: Vector3dc = this.normal,
      separation: Float = this.separation,
      velocity: Vector3dc = this.velocity
   ): ContactPointImpl {
      return ContactPointImpl(position, normal, separation, velocity)
   }

   public override fun hashCode(): Int {
      return ((this.position.hashCode() * 31 + this.normal.hashCode()) * 31 + java.lang.Float.hashCode(this.separation)) * 31 + this.velocity.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is ContactPointImpl
            && this.position == (other as ContactPointImpl).position
            && this.normal == (other as ContactPointImpl).normal
            && java.lang.Float.compare(this.separation, (other as ContactPointImpl).separation) == 0
            && this.velocity == (other as ContactPointImpl).velocity
         }
   }
}
