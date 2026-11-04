package org.valkyrienskies.core.impl.game.ships.serialization.shipinertia

import javax.inject.Inject
import org.joml.Matrix3d
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.impl.game.ships.ShipInertiaDataImpl
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.dto.ShipInertiaDataV0
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class ShipInertiaConverter @Inject  public constructor() {
   public fun convertToModel(data: ShipInertiaDataV0): ShipInertiaDataImpl {
      return ShipInertiaDataImpl(data.centerOfMassInShipSpace, data.shipMass, data.momentOfInertiaTensor)
   }

   public fun convertToDto(model: ShipInertiaData): ShipInertiaDataV0 {
      return ShipInertiaDataV0(Vector3d(model.centerOfMassInShip), model.getMass(), Matrix3d((model as ShipInertiaDataImpl).momentOfInertiaTensorToSave))
   }
}
