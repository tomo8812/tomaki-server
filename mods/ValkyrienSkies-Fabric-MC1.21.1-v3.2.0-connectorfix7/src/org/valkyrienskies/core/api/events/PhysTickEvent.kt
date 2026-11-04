package org.valkyrienskies.core.api.events

import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.PhysTickOnly
import org.valkyrienskies.core.api.world.PhysLevel

@VsBeta
@PhysTickOnly
public interface PhysTickEvent {
   public val world: PhysLevel

   public val delta: Double
}
