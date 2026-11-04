package org.valkyrienskies.core.internal

import org.valkyrienskies.core.internal.world.VsiClientShipWorld

public interface VsiCoreClient : VsiCore {
   public abstract fun newShipWorldClient(): VsiClientShipWorld {
   }
}
