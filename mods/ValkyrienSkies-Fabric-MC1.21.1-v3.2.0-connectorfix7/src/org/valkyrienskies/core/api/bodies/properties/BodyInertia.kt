package org.valkyrienskies.core.api.bodies.properties

import org.joml.Matrix3dc
import org.joml.Vector3dc

public interface BodyInertia {
   public val inertiaTensor: Matrix3dc

   public val centerOfMass: Vector3dc

   public val mass: Double
}
