package org.valkyrienskies.core.impl.game.ships.serialization.shiptransform

import org.valkyrienskies.core.impl.shadow.dP
import org.valkyrienskies.core.impl.shadow.dW

@dW
public interface ShipTransformModule {
   @dP
   public abstract fun converter(impl: ShipTransformConverterImpl): ShipTransformConverter {
   }
}
