package org.valkyrienskies.core.api.events

import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.core.api.world.ServerShipWorld

@VsBeta
@GameTickOnly
public interface TickEndEvent {
   public val world: ServerShipWorld
}
