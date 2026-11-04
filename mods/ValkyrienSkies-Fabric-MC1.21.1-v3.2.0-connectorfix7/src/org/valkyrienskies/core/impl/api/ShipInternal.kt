package org.valkyrienskies.core.impl.api

import org.valkyrienskies.core.internal.ships.VsiShip

public interface ShipInternal : VsiShip {
   public var chunkClaimDimension: String

   public abstract fun onSetBlock(
      posX: Int,
      posY: Int,
      posZ: Int,
      newBlockIsAir: Boolean,
      oldBlockMass: Double,
      newBlockMass: Double,
      isRunningOnServer: Boolean
   ) {
   }
}
