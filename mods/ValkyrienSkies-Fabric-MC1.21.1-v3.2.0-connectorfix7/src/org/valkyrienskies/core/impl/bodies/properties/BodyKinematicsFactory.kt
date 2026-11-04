package org.valkyrienskies.core.impl.bodies.properties

import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder

public object BodyKinematicsFactory {
   private final val transformFactory: BodyTransformFactory = BodyTransformFactory.INSTANCE

   public fun create(velocity: Vector3dc, angularVelocity: Vector3dc, transform: BodyTransform): BodyKinematics {
      return BodyKinematicsImpl(velocity, angularVelocity, transform)
   }

   public fun create(
      velocity: Vector3dc,
      angularVelocity: Vector3dc,
      position: Vector3dc,
      rotation: Quaterniondc,
      scaling: Vector3dc,
      positionInModel: Vector3dc
   ): BodyKinematics {
      return this.create(velocity, angularVelocity, transformFactory.create(position, rotation, scaling, positionInModel))
   }

   public fun newBuilder(): Builder {
      return BodyKinematicsBuilderImpl()
   }
}
