package org.valkyrienskies.core.internal.world

import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.world.LevelYRange
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate

public interface VsiShipWorld : ShipWorld {
   public val allShips: VsiQueryableShipData<Ship>

   public val loadedShips: VsiQueryableShipData<LoadedShip>

   public abstract fun onSetBlock(
      posX: Int,
      posY: Int,
      posZ: Int,
      dimensionId: String,
      oldBlockType: VsiBlockType,
      newBlockType: VsiBlockType,
      oldBlockMass: Double,
      newBlockMass: Double
   ) {
   }

   public abstract fun addTerrainUpdates(dimensionId: String, terrainUpdates: List<VsiTerrainUpdate>) {
   }

   public abstract fun forceUpdateConnectivityChunk(dimensionId: String, chunkX: Int, chunkY: Int, chunkZ: Int, update: VsiTerrainUpdate) {
   }

   public abstract fun addDimension(dimensionId: String, yRange: LevelYRange, gravity: Vector3dc, seaLevel: Double = 62.0, maxY: Double = 962.0) {
   }

   public abstract fun updateDimension(dimensionId: String, gravity: Vector3dc, seaLevel: Double? = null, maxY: Double? = null) {
   }

   public abstract fun removeDimension(dimensionId: String) {
   }
}
