package org.valkyrienskies.core.internal.physics

public data class VSCapsuleCollisionShapeData(radius: Double, length: Double) : VSCollisionShapeData {
   public final val radius: Double
   public final val length: Double

   init {
      this.radius = radius
      this.length = length
   }

   public open val shapeType: VSCollisionShapes
      public open get() {
         return VSCollisionShapes.CAPSULE
      }


   public operator fun component1(): Double {
      return this.radius
   }

   public operator fun component2(): Double {
      return this.length
   }

   public fun copy(radius: Double = this.radius, length: Double = this.length): VSCapsuleCollisionShapeData {
      return VSCapsuleCollisionShapeData(radius, length)
   }

   public override fun toString(): String {
      return "VSCapsuleCollisionShapeData(radius=${this.radius}, length=${this.length})"
   }

   public override fun hashCode(): Int {
      return java.lang.Double.hashCode(this.radius) * 31 + java.lang.Double.hashCode(this.length)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is VSCapsuleCollisionShapeData
            && java.lang.Double.compare(this.radius, (other as VSCapsuleCollisionShapeData).radius) == 0
            && java.lang.Double.compare(this.length, (other as VSCapsuleCollisionShapeData).length) == 0
         }
   }
}
