package org.valkyrienskies.core.impl.bodies.properties

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder
import org.valkyrienskies.core.impl.util.DoNotObfuscate

@DoNotObfuscate
public class BodyKinematicsImpl(velocity: Vector3dc, angularVelocity: Vector3dc, transform: BodyTransform) : BodyKinematics {
   public open val transform: BodyTransform
   public open val velocity: Vector3dc
   public open val angularVelocity: Vector3dc

   init {
      this.transform = transform
      this.velocity = Vector3d(velocity)
      this.angularVelocity = Vector3d(angularVelocity)
   }

   public override fun withTransform(transform: BodyTransform): BodyKinematics {
      return BodyKinematicsImpl(this.velocity, this.angularVelocity, transform)
   }

   @VsBeta
   public override fun toBuilder(): Builder {
      return BodyKinematicsFactory.INSTANCE.newBuilder().from(this)
   }
}
