package org.valkyrienskies.core.api.events

import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.core.api.world.ClientShipWorld

@VsBeta
@GameTickOnly
public interface StartUpdateRenderTransformsEvent {
   public val shipWorld: ClientShipWorld
}
