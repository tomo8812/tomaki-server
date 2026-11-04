package org.valkyrienskies.core.api.physics

import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.PhysTickOnly

@VsBeta
@PhysTickOnly
public interface ContactPoint {
   public val position: Vector3dc

   public val normal: Vector3dc

   public val separation: Float

   public val velocity: Vector3dc
}
