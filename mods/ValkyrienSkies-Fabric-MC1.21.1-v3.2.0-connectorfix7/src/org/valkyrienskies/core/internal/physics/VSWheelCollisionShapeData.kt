package org.valkyrienskies.core.internal.physics

public data class VSWheelCollisionShapeData(wheelRadius: Double, halfThickness: Double, pointShellSize: Int) : VSCollisionShapeData {
   public final val wheelRadius: Double
   public final val halfThickness: Double
   public final val pointShellSize: Int

   init {
      this.wheelRadius = wheelRadius
      this.halfThickness = halfThickness
      this.pointShellSize = pointShellSize
   }

   public open val shapeType: VSCollisionShapes
      public open get() {
         return VSCollisionShapes.WHEEL
      }


   public operator fun component1(): Double {
      return this.wheelRadius
   }

   public operator fun component2(): Double {
      return this.halfThickness
   }

   public operator fun component3(): Int {
      return this.pointShellSize
   }

   public fun copy(wheelRadius: Double = this.wheelRadius, halfThickness: Double = this.halfThickness, pointShellSize: Int = this.pointShellSize): VSWheelCollisionShapeData {
      return VSWheelCollisionShapeData(wheelRadius, halfThickness, pointShellSize)
   }

   public override fun toString(): String {
      return "VSWheelCollisionShapeData(wheelRadius=${this.wheelRadius}, halfThickness=${this.halfThickness}, pointShellSize=${this.pointShellSize})"
   }

   public override fun hashCode(): Int {
      return (java.lang.Double.hashCode(this.wheelRadius) * 31 + java.lang.Double.hashCode(this.halfThickness)) * 31 + Integer.hashCode(this.pointShellSize)
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is VSWheelCollisionShapeData
            && java.lang.Double.compare(this.wheelRadius, (other as VSWheelCollisionShapeData).wheelRadius) == 0
            && java.lang.Double.compare(this.halfThickness, (other as VSWheelCollisionShapeData).halfThickness) == 0
            && this.pointShellSize == (other as VSWheelCollisionShapeData).pointShellSize
         }
   }
}
