package org.valkyrienskies.core.impl.game.ships.serialization.shiptransform

import javax.inject.Inject
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.dto.ShipTransformDataV0
import org.valkyrienskies.core.impl.shadow.Ew
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class ShipTransformConverterImpl @Inject  public constructor() : ShipTransformConverter {
   public override fun convertToModel(data: ShipTransformDataV0): ShipTransform {
      return Ew.a(
         data.shipPositionInWorldCoordinates,
         data.shipPositionInShipCoordinates,
         data.shipCoordinatesToWorldCoordinatesRotation,
         data.shipCoordinatesToWorldCoordinatesScaling
      )
   }

   public override fun convertToDto(model: ShipTransform): ShipTransformDataV0 {
      return ShipTransformDataV0(model.positionInWorld, model.positionInShip, model.shipToWorldRotation, model.shipToWorldScaling)
   }
}
