package org.valkyrienskies.core.internal

import com.fasterxml.jackson.databind.ObjectMapper
import org.joml.Matrix3dc
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsCoreApi
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.internal.collision.VsiEntityPolygonCollider
import org.valkyrienskies.core.internal.collision.VsiSolidShapeUtils
import org.valkyrienskies.core.internal.config.VsiConfigModel
import org.valkyrienskies.core.internal.hooks.VsiCoreHooks
import org.valkyrienskies.core.internal.physics.blockstates.VsiBlockState
import org.valkyrienskies.core.internal.world.VsiClientShipWorld
import org.valkyrienskies.core.internal.world.VsiPipeline
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.core.internal.world.chunks.VsiBlockTypes
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate.Builder

public interface VsiCore : VsCoreApi, VsiCoreCommands {
   public val simplePacketNetworking: SimplePacketNetworking

   public val hooks: VsiCoreHooks

   public val blockTypes: VsiBlockTypes

   public val entityPolygonCollider: VsiEntityPolygonCollider

   public val solidShapeUtils: VsiSolidShapeUtils

   public val dummyShipWorldClient: VsiClientShipWorld

   public val dummyShipWorldServer: VsiServerShipWorld

   public abstract fun registerBlockStates(states: Iterable<VsiBlockState>) {
   }

   public abstract fun newEmptyVoxelShapeUpdate(chunkX: Int, chunkY: Int, chunkZ: Int, overwrite: Boolean): VsiTerrainUpdate {
   }

   public abstract fun newDeleteTerrainUpdate(chunkX: Int, chunkY: Int, chunkZ: Int): VsiTerrainUpdate {
   }

   public abstract fun newDenseTerrainUpdateBuilder(chunkX: Int, chunkY: Int, chunkZ: Int): Builder {
   }

   public abstract fun newSparseTerrainUpdateBuilder(chunkX: Int, chunkY: Int, chunkZ: Int): Builder {
   }

   public abstract fun newPipelineLegacyData(queryableShipDataBytes: ByteArray, chunkAllocatorBytes: ByteArray): VsiPipeline {
   }

   public abstract fun newPipeline(): VsiPipeline {
   }

   public abstract fun newPipeline(data: ByteArray): VsiPipeline {
   }

   public abstract fun serializePipeline(pipeline: VsiPipeline): ByteArray {
   }

   public abstract fun newChunkClaim(claimX: Int, claimZ: Int): ChunkClaim {
   }

   public abstract fun newChunkClaimFromChunkPos(chunkX: Int, chunkZ: Int): ChunkClaim {
   }

   public abstract fun newSolidStateBuilder(): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
   }

   public abstract fun newLiquidStateBuilder(): org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder {
   }

   public abstract fun newSolidStateBoxesShapeBuilder(): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
   }

   public abstract fun newShipTransform(
      positionInWorld: Vector3dc = Vector3d() as Vector3dc,
      positionInShip: Vector3dc = Vector3d() as Vector3dc,
      shipToWorldRotation: Quaterniondc = Quaterniond() as Quaterniondc,
      shipToWorldScaling: Vector3dc = Vector3d(1.0) as Vector3dc
   ): ShipTransform {
   }

   public abstract fun newShipTeleportData(
      newPos: Vector3dc = Vector3d() as Vector3dc,
      newRot: Quaterniondc = Quaterniond() as Quaterniondc,
      newVel: Vector3dc = Vector3d() as Vector3dc,
      newOmega: Vector3dc = Vector3d() as Vector3dc,
      newDimension: String? = null,
      newScale: Double? = null,
      newPosInShip: Vector3dc? = null
   ): ShipTeleportData {
   }

   public abstract fun newShipInertiaData(centerOfMassInShip: Vector3dc, mass: Double, momentOfInertiaTensor: Matrix3dc): ShipInertiaData {
   }

   public abstract fun getServerConfig(): VsiConfigModel {
   }

   public val defaultMapper: ObjectMapper

   @Deprecated(message = "This method isn't tied to a world, which is bad if we want to have different chunk allocation strategies per world in the future.", replaceWith = @ReplaceWith(expression = "ShipWorld.isChunkInShipyard(chunkX, chunkZ, DimensionId)", imports = []))
   public abstract fun isChunkInShipyard(chunkX: Int, chunkZ: Int): Boolean {
   }

   @Deprecated(message = "Surely we can do better than this")
   public var clientUsesUDP: Boolean
}
