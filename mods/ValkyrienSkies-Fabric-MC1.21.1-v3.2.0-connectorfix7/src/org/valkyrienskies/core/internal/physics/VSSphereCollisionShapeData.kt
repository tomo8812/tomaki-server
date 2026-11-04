package org.valkyrienskies.core.internal.physics

public data class VSSphereCollisionShapeData(radius: Double) : VSCollisionShapeData {
   public final val radius: Double

   init {
      this.radius = radius
   }

   public open val shapeType: VSCollisionShapes
      public open get() {
         return VSCollisionShapes.SPHERE
      }


   public operator fun component1(): Double {
      return this.radius
   }

   public fun copy(radius: Double = this.radius): VSSphereCollisionShapeData {
      return VSSphereCollisionShapeData(radius)
   }

   public override fun toString(): String {
      return "VSSphereCollisionShapeData(radius=${this.radius})"
   }

   public override fun hashCode(): Int {
      return java.lang.Double.hashCode(this.radius)
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is VSSphereCollisionShapeData && java.lang.Double.compare(this.radius, (other as VSSphereCollisionShapeData).radius) == 0
      }
   }
}
