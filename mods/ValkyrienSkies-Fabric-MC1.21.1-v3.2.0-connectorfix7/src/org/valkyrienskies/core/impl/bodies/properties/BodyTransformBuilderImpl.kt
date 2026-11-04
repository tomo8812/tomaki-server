package org.valkyrienskies.core.impl.bodies.properties

import org.joml.Quaterniond
import org.joml.Vector3d
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.properties.ShipTransform

public class BodyTransformBuilderImpl(position: Vector3d = Vector3d(),
      positionInModel: Vector3d = Vector3d(),
      rotation: Quaterniond = Quaterniond(),
      scaling: Vector3d = Vector3d()
   ) :
   BodyTransform.Builder {
   public open var position: Vector3d
   public open var positionInModel: Vector3d
   public open var rotation: Quaterniond
   public open var scaling: Vector3d

   init {
      this.position = position
      this.positionInModel = positionInModel
      this.rotation = rotation
      this.scaling = scaling
   }

   public open fun from(transform: BodyTransform): BodyTransformBuilderImpl {
      this.position.set(transform.position)
      this.positionInModel.set(transform.positionInModel)
      this.rotation.set(transform.rotation)
      this.scaling.set(transform.scaling)
      return this
   }

   public open fun build(): ShipTransform {
      return BodyTransformImpl(this.position, this.rotation, this.scaling, this.positionInModel)
   }

   fun BodyTransformBuilderImpl() {
      this(null, null, null, null, 15, null)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun from(transform: BodyTransform): BodyTransform.Builder {
      this.from(transform) as BodyTransform.Builder
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun build(): BodyTransform {
      this.build() as BodyTransform
   }
}
