package org.valkyrienskies.core.api.bodies.properties

import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta

@VsBeta
public interface BodyPose {
   public val position: Vector3dc

   public val rotation: Quaterniondc
}
