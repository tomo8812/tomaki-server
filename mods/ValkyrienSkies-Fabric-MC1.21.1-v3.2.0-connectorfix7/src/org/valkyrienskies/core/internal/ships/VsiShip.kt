package org.valkyrienskies.core.internal.ships

import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.Ship

public interface VsiShip : Ship {
   public abstract fun setFromTransform(transform: BodyTransform) {
   }

   public abstract fun unsafeSetKinematics(kinematics: BodyKinematics) {
   }
}
