package org.valkyrienskies.core.impl.shadow

import java.net.SocketAddress
import org.joml.Vector3dc
import org.joml.Vector3ic
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.world.LevelYRange
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData
import org.valkyrienskies.core.internal.world.VsiClientShipWorld
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate

public object Ei : VsiClientShipWorld {
   public open val c: VsiQueryableShipData<ClientShip>

   public override fun tickNetworking(p0: SocketAddress) {
      throw UnsupportedOperationException()
   }

   public override fun postTick() {
      throw UnsupportedOperationException()
   }

   public override fun updateRenderTransforms(p0: Double) {
      throw UnsupportedOperationException()
   }

   public override fun destroyWorld() {
      throw UnsupportedOperationException()
   }

   public open val b: Boolean
      public open get() {
         throw UnsupportedOperationException()
      }


   public override fun onSetBlock(p0: Int, p1: Int, p2: Int, p3: String, p4: VsiBlockType, p5: VsiBlockType, p6: Double, p7: Double) {
   }

   public override fun addTerrainUpdates(p0: String, p1: List<VsiTerrainUpdate>) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun forceUpdateConnectivityChunk(p0: String, p1: Int, p2: Int, p3: Int, p4: VsiTerrainUpdate) {
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

   public open val a: VsiQueryableShipData<ClientShip>
      public open get() {
         return this.c
      }


   public override fun getPhysEntityClientRenderTransform(p0: Long): ShipTransform? {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun isChunkInShipyard(p0: Int, p1: Int, p2: String): Boolean {
      return false
   }

   public override fun isBlockInShipyard(p0: Int, p1: Int, p2: Int, p3: String): Boolean {
      return false
   }

   @Deprecated(message = "redundant", replaceWith = @ReplaceWith(expression = "loadedShips.getIntersecting(aabb)", imports = []))
   public override fun getShipObjectsIntersecting(p0: AABBdc): List<ClientShip> {
      return CollectionsKt.emptyList()
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

   public override fun getSolidComponentSize(p0: Int, p1: Int, p2: Int, p3: String): Long {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getAllSolidComponentsFromClaim(p0: String, p1: ChunkClaim): Set<Vector3ic> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun getAirComponentSize(p0: Int, p1: Int, p2: Int, p3: String): Long {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun traverseSolidComponent(p0: Int, p1: Int, p2: Int, p3: (Int, Int, Int, Long) -> Unit, p4: String) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun traverseAirComponent(p0: Int, p1: Int, p2: Int, p3: (Int, Int, Int, Long) -> Unit, p4: String) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexSolidComponent(p0: Int, p1: Int, p2: Int, p3: String): Set<Vector3ic> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun indexSolidComponent(p0: Int, p1: Int, p2: Int, p3: Set<Vector3ic>, p4: String): Set<Vector3ic> {
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

   public override fun getAllAirComponentsFromClaim(p0: String, p1: ChunkClaim): Set<Vector3ic> {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getAllShips(): QueryableShipData {
      this.a as QueryableShipData
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getLoadedShips(): QueryableShipData {
      this.c as QueryableShipData
   }
}
