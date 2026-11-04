package org.valkyrienskies.core.api.bodies

import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.util.Identified

public interface BaseVsBody : Identified {
   public val id: Long

   public val dimension: String

   public val transform: BodyKinematics

   public val prevTickTransform: BodyKinematics

   public val aabb: AABBdc

   public open val velocity: Vector3dc
      public open get() {
         return this.transform.velocity
      }


   public open val angularVelocity: Vector3dc
      public open get() {
         return this.transform.angularVelocity
      }

}
