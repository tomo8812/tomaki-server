package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline

import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV4
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV5
import org.valkyrienskies.core.impl.shadow.EI

public interface VSPipelineDataConverter {
   public abstract fun convertToModel(data: VSPipelineDataV5): EI {
   }

   public abstract fun convertShipData(data: List<ServerShipDataV4>): List<ShipData> {
   }
}
