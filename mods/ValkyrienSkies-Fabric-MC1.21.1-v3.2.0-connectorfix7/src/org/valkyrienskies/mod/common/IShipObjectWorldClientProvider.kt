package org.valkyrienskies.mod.common

import org.valkyrienskies.core.internal.world.VsiClientShipWorld

public interface IShipObjectWorldClientProvider : IShipObjectWorldProvider {
   public val shipObjectWorld: VsiClientShipWorld?
}
