package org.valkyrienskies.core.impl.shadow

import java.util.function.DoubleUnaryOperator
import org.joml.Vector3dc
import org.joml.Vector3ic
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.util.AerodynamicUtils
import org.valkyrienskies.core.api.world.LevelYRange
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus
import org.valkyrienskies.core.api.world.connectivity.DoubleAugmentation
import org.valkyrienskies.core.api.world.connectivity.DoubleComponentAugmentation
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition
import org.valkyrienskies.core.internal.ShipTeleportData
import org.valkyrienskies.core.internal.VsiGameServer
import org.valkyrienskies.core.internal.physics.PhysicsEntityData
import org.valkyrienskies.core.internal.physics.PhysicsEntityServer
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTask
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTasks
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate

public object Ej : VsiServerShipWorld {
   public open var e: Set<VsiPlayer>
   public open val d: VsiQueryableShipData<LoadedServerShip>

   public override fun setGameServer(p0: VsiGameServer?) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun addTerrainUpdates(p0: String, p1: List<VsiTerrainUpdate>) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun forceUpdateConnectivityChunk(p0: String, p1: Int, p2: Int, p3: Int, p4: VsiTerrainUpdate) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getIPlayersWatchingShipChunk(p0: Int, p1: Int, p2: String): Iterator<VsiPlayer> {
      return CollectionsKt.emptyList().iterator()
   }

   public override fun getChunkWatchTasks(): VsiChunkWatchTasks {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun setExecutedChunkWatchTasks(p0: Iterable<VsiChunkWatchTask>, p1: Iterable<VsiChunkUnwatchTask>) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun createNewShipAtBlock(p0: Vector3ic, p1: Boolean, p2: Double, p3: String): ServerShip {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun createPhysicsEntity(p0: PhysicsEntityData, p1: String): PhysicsEntityServer {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun deletePhysicsEntity(p0: Long) {
   }

   public override fun allocateShipId(p0: String): Long {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun addDimension(p0: String, p1: LevelYRange, p2: Vector3dc, p3: Double, p4: Double) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun updateDimension(p0: String, p1: Vector3dc, p2: Double?, p3: Double?) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun removeDimension(p0: String) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun onDisconnect(p0: VsiPlayer) {
   }

   public override fun deleteShip(p0: ServerShip) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun teleportShip(p0: ServerShip, p1: ShipTeleportData) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun teleportPhysicsEntity(p0: PhysicsEntityServer, p1: ShipTeleportData) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public open val c: Map<String, Long>
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }


   public override fun onSetBlock(p0: Int, p1: Int, p2: Int, p3: String, p4: VsiBlockType, p5: VsiBlockType, p6: Double, p7: Double) {
   }

   public open val b: VsiQueryableShipData<ServerShip>
      public open get() {
         return this.d
      }


   public override fun retrieveLoadedPhysicsEntities(): Map<Long, PhysicsEntityServer> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun isChunkInShipyard(p0: Int, p1: Int, p2: String): Boolean {
      return false
   }

   public override fun isBlockInShipyard(p0: Int, p1: Int, p2: Int, p3: String): Boolean {
      return false
   }

   @Deprecated(message = "redundant", replaceWith = @ReplaceWith(expression = "loadedShips.getIntersecting(aabb)", imports = []))
   public override fun getShipObjectsIntersecting(p0: AABBdc): List<LoadedServerShip> {
      return CollectionsKt.emptyList()
   }

   public open val a: AerodynamicUtils
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }


   public override fun isConnectedByAir(p0: Int, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: String): ConnectionStatus {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun isConnectedBySolid(p0: Int, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: String): ConnectionStatus {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun isIsolatedAir(p0: Int, p1: Int, p2: Int, p3: String): ConnectionStatus {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun isIsolatedSolid(p0: Int, p1: Int, p2: Int, p3: String): ConnectionStatus {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun createDoubleSumAugmentation(p0: String, p1: String): DoubleAugmentation {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun createDoubleSumComponentAugmentation(p0: String, p1: String): DoubleComponentAugmentation {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getSolidAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String): Double {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun setSolidAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String, p5: Double) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun removeSolidAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun updateSolidAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String, p5: DoubleUnaryOperator) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun collectSolidAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String): Double {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun setSolidComponentAugmentation(p0: DoubleComponentAugmentation, p1: Int, p2: Int, p3: Int, p4: String, p5: Double) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getSolidComponentAugmentation(p0: DoubleComponentAugmentation, p1: Int, p2: Int, p3: Int, p4: String): Double {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun removeSolidComponentAugmentation(p0: DoubleComponentAugmentation, p1: Int, p2: Int, p3: Int, p4: String) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getFromEachSolidComponent(p0: DoubleAugmentation, p1: String, p2: ChunkClaim?): Map<Triple<Int, Int, Int>, Double> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getFromEachSolidComponentRoot(p0: DoubleComponentAugmentation, p1: String, p2: ChunkClaim?): Map<Triple<Int, Int, Int>, Double> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getSolidComponentSize(p0: Int, p1: Int, p2: Int, p3: String): Long {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getAllSolidComponentsFromClaim(p0: String, p1: ChunkClaim): Set<Vector3ic> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getAirAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String): Double {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun setAirAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String, p5: Double) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun removeAirAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun updateAirAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String, p5: DoubleUnaryOperator) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun collectAirAugmentation(p0: DoubleAugmentation, p1: Int, p2: Int, p3: Int, p4: String): Double {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun setAirComponentAugmentation(p0: DoubleComponentAugmentation, p1: Int, p2: Int, p3: Int, p4: String, p5: Double) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getAirComponentAugmentation(p0: DoubleComponentAugmentation, p1: Int, p2: Int, p3: Int, p4: String): Double {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun removeAirComponentAugmentation(p0: DoubleComponentAugmentation, p1: Int, p2: Int, p3: Int, p4: String) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getAirComponentSize(p0: Int, p1: Int, p2: Int, p3: String): Long {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun traverseSolidComponent(p0: Int, p1: Int, p2: Int, p3: (Int, Int, Int, Long) -> Unit, p4: String) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getFromEachAirComponent(p0: DoubleAugmentation, p1: String, p2: ChunkClaim?): Map<Triple<Int, Int, Int>, Double> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getFromEachAirComponentRoot(p0: DoubleComponentAugmentation, p1: String, p2: ChunkClaim?): Map<Triple<Int, Int, Int>, Double> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun traverseAirComponent(p0: Int, p1: Int, p2: Int, p3: (Int, Int, Int, Long) -> Unit, p4: String) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexSolidComponent(p0: Int, p1: Int, p2: Int, p3: String): Set<Vector3ic> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexAirComponent(p0: Int, p1: Int, p2: Int, p3: String): Set<Vector3ic> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexAirComponent(p0: Int, p1: Int, p2: Int, p3: Set<Vector3ic>, p4: String): Set<Vector3ic> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexSolidComponentVoxels(p0: Int, p1: Int, p2: Int, p3: String): Set<SparseVoxelPosition> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexAirComponentVoxels(p0: Int, p1: Int, p2: Int, p3: String): Set<SparseVoxelPosition> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexAirComponentVoxels(p0: Int, p1: Int, p2: Int, p3: Set<SparseVoxelPosition>, p4: String): Set<SparseVoxelPosition> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexSolidComponentVoxels(p0: Int, p1: Int, p2: Int, p3: Set<SparseVoxelPosition>, p4: String): Set<SparseVoxelPosition> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexSolidComponent(p0: Int, p1: Int, p2: Int, p3: Set<Vector3ic>, p4: String): Set<Vector3ic> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getAllAirComponentsFromClaim(p0: String, p1: ChunkClaim): Set<Vector3ic> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getAllShips(): QueryableShipData {
      this.b as QueryableShipData
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getLoadedShips(): QueryableShipData {
      this.d as QueryableShipData
   }
}
