package org.valkyrienskies.core.internal.ships

import org.valkyrienskies.core.api.ships.LoadedServerShip

public interface VsiLoadedServerShip : LoadedServerShip, VsiServerShip, VsiLoadedShip {
   public abstract fun isOnPortalCoolDown(): Boolean {
   }

   public abstract fun handleInsidePortal() {
   }

   public abstract fun decayPortalCoolDown() {
   }
}
