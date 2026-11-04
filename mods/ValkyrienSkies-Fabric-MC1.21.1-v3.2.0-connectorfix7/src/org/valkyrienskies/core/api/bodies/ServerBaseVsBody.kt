package org.valkyrienskies.core.api.bodies

import org.joml.Matrix3dc
import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta

public interface ServerBaseVsBody : BaseVsBody {
   public val mass: Double

   public open val centerOfMass: Vector3dc
      public open get() {
         return this.getTransform().positionInModel
      }


   public val momentOfInertia: Matrix3dc

   public var isStatic: Boolean

   public var buoyantFactor: Double

   @VsBeta
   public var doFluidDrag: Boolean

   public abstract fun setTransform(position: Vector3dc, rotation: Quaterniondc, scaling: Vector3dc) {
   }

   public open fun setPosition(position: Vector3dc) {
      this.setTransform(position, this.getTransform().rotation, this.getTransform().scaling)
   }

   public open fun setRotation(rotation: Quaterniondc) {
      this.setTransform(this.getTransform().position, rotation, this.getTransform().scaling)
   }

   public open fun setScaling(scaling: Vector3dc) {
      this.setTransform(this.getTransform().position, this.getTransform().rotation, scaling)
   }
}
