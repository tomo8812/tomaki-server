package org.valkyrienskies.core.api.ships

import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.properties.ShipTransform

public interface ClientShipTransformProvider {
   public abstract fun provideNextTransform(prevShipTransform: ShipTransform, shipTransform: ShipTransform, latestNetworkTransform: ShipTransform): BodyTransform? {
   }

   public abstract fun provideNextRenderTransform(prevShipTransform: ShipTransform, shipTransform: ShipTransform, partialTick: Double): BodyTransform? {
   }
}
