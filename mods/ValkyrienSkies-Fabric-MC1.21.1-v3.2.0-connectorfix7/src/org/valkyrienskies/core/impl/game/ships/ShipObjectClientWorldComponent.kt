package org.valkyrienskies.core.impl.game.ships

import org.valkyrienskies.core.impl.shadow.EE
import org.valkyrienskies.core.impl.shadow.dZ

@dZ(a = [EE::class, EE.a::class])
public interface ShipObjectClientWorldComponent {
   public abstract fun newWorld(): ShipObjectClientWorld {
   }

   @dZ.b
   public interface Factory {
      public abstract fun newShipObjectClientWorldComponent(): ShipObjectClientWorldComponent {
      }
   }
}
