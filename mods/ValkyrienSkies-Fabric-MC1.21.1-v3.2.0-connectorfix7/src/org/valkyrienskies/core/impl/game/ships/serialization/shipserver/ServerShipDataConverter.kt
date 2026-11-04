package org.valkyrienskies.core.impl.game.ships.serialization.shipserver

import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV4

public interface ServerShipDataConverter {
   public abstract fun convertToDto(model: ShipData): ServerShipDataV4 {
   }

   public abstract fun convertToModel(dto: ServerShipDataV4): ShipData {
   }
}
