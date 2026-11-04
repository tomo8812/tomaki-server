package org.valkyrienskies.core.api.ships.properties

import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.bodies.properties.BodyPose

/** @deprecated */
@Deprecated(message = "DO NOT USE. DO NOT LEAVE IN FOR VS 2.5. Replace with BodyPose")
public interface PhysPose : BodyPose {
   public val pos: Vector3dc

   public val rot: Quaterniondc

   public open val position: Vector3dc
      public open get() {
         return this.pos
      }


   public open val rotation: Quaterniondc
      public open get() {
         return this.rot
      }

}
