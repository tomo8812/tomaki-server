package org.valkyrienskies.core.api.world

import org.jetbrains.annotations.ApiStatus
import org.joml.Vector3ic
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition

public interface ShipWorld {
   public val allShips: QueryableShipData<Ship>

   public val loadedShips: QueryableShipData<LoadedShip>

   public abstract fun isChunkInShipyard(chunkX: Int, chunkZ: Int, dimensionId: String): Boolean {
   }

   public abstract fun isBlockInShipyard(blockX: Int, blockY: Int, blockZ: Int, dimensionId: String): Boolean {
   }

   @Deprecated(message = "redundant", replaceWith = @ReplaceWith(expression = "loadedShips.getIntersecting(aabb)", imports = []))
   public abstract fun getShipObjectsIntersecting(aabb: AABBdc): List<LoadedShip> {
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "allShips", imports = {}))
   public open val queryableShipData: QueryableShipData<Ship>
      public open get() {
         return this.allShips
      }


   @ApiStatus.Experimental
   public abstract fun isConnectedByAir(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int, dimension: String): ConnectionStatus {
   }

   @ApiStatus.Experimental
   public abstract fun isConnectedBySolid(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int, dimension: String): ConnectionStatus {
   }

   @ApiStatus.Experimental
   public abstract fun isIsolatedAir(x: Int, y: Int, z: Int, dimension: String): ConnectionStatus {
   }

   @ApiStatus.Experimental
   public abstract fun isIsolatedSolid(x: Int, y: Int, z: Int, dimension: String): ConnectionStatus {
   }

   @ApiStatus.Experimental
   public abstract fun getSolidComponentSize(x: Int, y: Int, z: Int, dimension: String): Long {
   }

   @ApiStatus.Experimental
   public abstract fun getAllSolidComponentsFromClaim(dimensionId: String, chunkClaim: ChunkClaim): Set<Vector3ic> {
   }

   @ApiStatus.Experimental
   public abstract fun getAirComponentSize(x: Int, y: Int, z: Int, dimension: String): Long {
   }

   @ApiStatus.Experimental
   public abstract fun traverseSolidComponent(x: Int, y: Int, z: Int, action: (Int, Int, Int, Long) -> Unit, dimension: String) {
   }

   @ApiStatus.Experimental
   public abstract fun traverseAirComponent(x: Int, y: Int, z: Int, action: (Int, Int, Int, Long) -> Unit, dimension: String) {
   }

   @ApiStatus.Experimental
   public abstract fun indexSolidComponent(x: Int, y: Int, z: Int, dimension: String): Set<Vector3ic> {
   }

   @ApiStatus.Experimental
   public abstract fun indexSolidComponent(x: Int, y: Int, z: Int, out: Set<Vector3ic>, dimension: String): Set<Vector3ic> {
   }

   @ApiStatus.Experimental
   public abstract fun indexAirComponent(x: Int, y: Int, z: Int, dimension: String): Set<Vector3ic> {
   }

   @ApiStatus.Experimental
   public abstract fun indexAirComponent(x: Int, y: Int, z: Int, out: Set<Vector3ic>, dimension: String): Set<Vector3ic> {
   }

   @ApiStatus.Experimental
   public abstract fun indexSolidComponentVoxels(x: Int, y: Int, z: Int, dimension: String): Set<SparseVoxelPosition> {
   }

   @ApiStatus.Experimental
   public abstract fun indexAirComponentVoxels(x: Int, y: Int, z: Int, dimension: String): Set<SparseVoxelPosition> {
   }

   @ApiStatus.Experimental
   public abstract fun indexAirComponentVoxels(x: Int, y: Int, z: Int, out: Set<SparseVoxelPosition>, dimension: String): Set<SparseVoxelPosition> {
   }

   @ApiStatus.Experimental
   public abstract fun indexSolidComponentVoxels(x: Int, y: Int, z: Int, out: Set<SparseVoxelPosition>, dimension: String): Set<SparseVoxelPosition> {
   }

   @ApiStatus.Experimental
   public abstract fun getAllAirComponentsFromClaim(dimensionId: String, chunkClaim: ChunkClaim): Set<Vector3ic> {
   }
}
