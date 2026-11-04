package org.valkyrienskies.core.internal.physics

public data class VSBoxCollisionShapeData(lengthX: Double, lengthY: Double, lengthZ: Double) : VSCollisionShapeData {
   public final val lengthX: Double
   public final val lengthY: Double
   public final val lengthZ: Double

   init {
      this.lengthX = lengthX
      this.lengthY = lengthY
      this.lengthZ = lengthZ
   }

   public open val shapeType: VSCollisionShapes
      public open get() {
         return VSCollisionShapes.BOX
      }


   public operator fun component1(): Double {
      return this.lengthX
   }

   public operator fun component2(): Double {
      return this.lengthY
   }

   public operator fun component3(): Double {
      return this.lengthZ
   }

   public fun copy(lengthX: Double = this.lengthX, lengthY: Double = this.lengthY, lengthZ: Double = this.lengthZ): VSBoxCollisionShapeData {
      return VSBoxCollisionShapeData(lengthX, lengthY, lengthZ)
   }

   public override fun toString(): String {
      return "VSBoxCollisionShapeData(lengthX=${this.lengthX}, lengthY=${this.lengthY}, lengthZ=${this.lengthZ})"
   }

   public override fun hashCode(): Int {
      return (java.lang.Double.hashCode(this.lengthX) * 31 + java.lang.Double.hashCode(this.lengthY)) * 31 + java.lang.Double.hashCode(this.lengthZ)
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is VSBoxCollisionShapeData
            && java.lang.Double.compare(this.lengthX, (other as VSBoxCollisionShapeData).lengthX) == 0
            && java.lang.Double.compare(this.lengthY, (other as VSBoxCollisionShapeData).lengthY) == 0
            && java.lang.Double.compare(this.lengthZ, (other as VSBoxCollisionShapeData).lengthZ) == 0
         }
   }
}
