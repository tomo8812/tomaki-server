package org.valkyrienskies.core.impl.game.ships.serialization.shiptransform

import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.dto.ShipTransformDataV0

public interface ShipTransformConverter {
   public abstract fun convertToModel(data: ShipTransformDataV0): ShipTransform {
   }

   public abstract fun convertToDto(model: ShipTransform): ShipTransformDataV0 {
   }
}
