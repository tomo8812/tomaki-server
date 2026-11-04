package org.valkyrienskies.core.api.world

import java.util.function.DoubleUnaryOperator
import org.jetbrains.annotations.ApiStatus
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.util.AerodynamicUtils
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.core.api.world.connectivity.DoubleAugmentation
import org.valkyrienskies.core.api.world.connectivity.DoubleComponentAugmentation

@GameTickOnly
public interface ServerShipWorld : ShipWorld {
   public val allShips: QueryableShipData<ServerShip>

   public val loadedShips: QueryableShipData<LoadedServerShip>

   @Deprecated(message = "redundant", replaceWith = @ReplaceWith(expression = "loadedShips.getIntersecting(dimensionId, aabb)", imports = []))
   public abstract override fun getShipObjectsIntersecting(aabb: AABBdc): List<LoadedServerShip> {
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "allShips", imports = {}))
   public open val queryableShipData: QueryableShipData<ServerShip>
      public open get() {
         return this.allShips
      }


   public val aerodynamicUtils: AerodynamicUtils

   @ApiStatus.Experimental
   public abstract fun createDoubleSumAugmentation(modId: String, key: String): DoubleAugmentation {
   }

   @ApiStatus.Experimental
   public abstract fun createDoubleSumComponentAugmentation(modId: String, key: String): DoubleComponentAugmentation {
   }

   @ApiStatus.Experimental
   public abstract fun getSolidAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String): Double {
   }

   @ApiStatus.Experimental
   public abstract fun setSolidAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String, value: Double) {
   }

   @ApiStatus.Experimental
   public abstract fun removeSolidAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String) {
   }

   @ApiStatus.Experimental
   public abstract fun updateSolidAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String, update: DoubleUnaryOperator) {
   }

   @ApiStatus.Experimental
   public abstract fun collectSolidAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String): Double {
   }

   @ApiStatus.Experimental
   public abstract fun setSolidComponentAugmentation(key: DoubleComponentAugmentation, x: Int, y: Int, z: Int, dimension: String, value: Double) {
   }

   @ApiStatus.Experimental
   public abstract fun getSolidComponentAugmentation(key: DoubleComponentAugmentation, x: Int, y: Int, z: Int, dimension: String): Double {
   }

   @ApiStatus.Experimental
   public abstract fun removeSolidComponentAugmentation(key: DoubleComponentAugmentation, x: Int, y: Int, z: Int, dimension: String) {
   }

   @ApiStatus.Experimental
   public abstract fun getFromEachSolidComponent(key: DoubleAugmentation, dimension: String, chunkClaim: ChunkClaim? = null): Map<
         Triple<Int, Int, Int>,
         Double
      > {
   }

   @ApiStatus.Experimental
   public abstract fun getFromEachSolidComponentRoot(key: DoubleComponentAugmentation, dimension: String, chunkClaim: ChunkClaim? = null): Map<
         Triple<Int, Int, Int>,
         Double
      > {
   }

   @ApiStatus.Experimental
   public abstract fun getAirAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String): Double {
   }

   @ApiStatus.Experimental
   public abstract fun setAirAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String, value: Double) {
   }

   @ApiStatus.Experimental
   public abstract fun removeAirAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String) {
   }

   @ApiStatus.Experimental
   public abstract fun updateAirAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String, update: DoubleUnaryOperator) {
   }

   @ApiStatus.Experimental
   public abstract fun collectAirAugmentation(key: DoubleAugmentation, x: Int, y: Int, z: Int, dimension: String): Double {
   }

   @ApiStatus.Experimental
   public abstract fun setAirComponentAugmentation(key: DoubleComponentAugmentation, x: Int, y: Int, z: Int, dimension: String, value: Double) {
   }

   @ApiStatus.Experimental
   public abstract fun getAirComponentAugmentation(key: DoubleComponentAugmentation, x: Int, y: Int, z: Int, dimension: String): Double {
   }

   @ApiStatus.Experimental
   public abstract fun removeAirComponentAugmentation(key: DoubleComponentAugmentation, x: Int, y: Int, z: Int, dimension: String) {
   }

   @ApiStatus.Experimental
   public abstract fun getFromEachAirComponent(key: DoubleAugmentation, dimension: String, chunkClaim: ChunkClaim? = null): Map<Triple<Int, Int, Int>, Double> {
   }

   @ApiStatus.Experimental
   public abstract fun getFromEachAirComponentRoot(key: DoubleComponentAugmentation, dimension: String, chunkClaim: ChunkClaim? = null): Map<
         Triple<Int, Int, Int>,
         Double
      > {
   }
}
