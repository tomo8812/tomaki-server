package org.valkyrienskies.core.impl.bodies.properties

import org.joml.Quaterniond
import org.joml.Vector3d
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics

public class BodyKinematicsBuilderImpl : BodyKinematics.Builder {
   public open val position: Vector3d = Vector3d()
   public open val positionInModel: Vector3d = Vector3d()
   public open val rotation: Quaterniond = Quaterniond()
   public open val scaling: Vector3d = Vector3d()
   public open val velocity: Vector3d = Vector3d()
   public open val angularVelocity: Vector3d = Vector3d()

   public override fun build(): BodyKinematics {
      return BodyKinematicsFactory.INSTANCE
         .create(
            Vector3d(this.velocity),
            Vector3d(this.angularVelocity),
            Vector3d(this.position),
            Quaterniond(this.rotation),
            Vector3d(this.scaling),
            Vector3d(this.positionInModel)
         )
      }
}
