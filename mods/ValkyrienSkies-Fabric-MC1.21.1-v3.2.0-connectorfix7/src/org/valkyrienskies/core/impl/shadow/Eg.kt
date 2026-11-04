package org.valkyrienskies.core.impl.shadow

import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.ClientShipTransformProvider
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl

public object Eg : ClientShipTransformProvider {
   @JvmStatic
   public open fun a(p0: ShipTransform, p1: ShipTransform, p2: ShipTransform): ShipTransform {
      return ShipTransformImpl.Companion.createFromSlerp(var1, var2, 0.7)
   }

   @JvmStatic
   public open fun a(p0: ShipTransform, p1: ShipTransform, p2: Double): ShipTransform {
      return ShipTransformImpl.Companion.createFromSlerp(var0, var1, var2)
   }

   @JvmSynthetic
   override final fun provideNextTransform(prevShipTransform: ShipTransform, shipTransform: ShipTransform, latestNetworkTransform: ShipTransform): BodyTransform {
      a(prevShipTransform, shipTransform, latestNetworkTransform) as BodyTransform
   }

   @JvmSynthetic
   override final fun provideNextRenderTransform(prevShipTransform: ShipTransform, shipTransform: ShipTransform, partialTick: Double): BodyTransform {
      a(prevShipTransform, shipTransform, partialTick) as BodyTransform
   }
}
