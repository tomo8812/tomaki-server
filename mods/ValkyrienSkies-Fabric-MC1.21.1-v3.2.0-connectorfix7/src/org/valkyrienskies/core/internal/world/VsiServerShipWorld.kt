package org.valkyrienskies.core.internal.world

import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.internal.ShipTeleportData
import org.valkyrienskies.core.internal.VsiGameServer
import org.valkyrienskies.core.internal.physics.PhysicsEntityData
import org.valkyrienskies.core.internal.physics.PhysicsEntityServer
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTask
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTasks

@GameTickOnly
public interface VsiServerShipWorld : VsiShipWorld, ServerShipWorld {
   public val allShips: VsiQueryableShipData<ServerShip>

   public val loadedShips: VsiQueryableShipData<LoadedServerShip>

   public var players: Set<VsiPlayer>

   public abstract fun setGameServer(gameServer: VsiGameServer?) {
   }

   public abstract fun getIPlayersWatchingShipChunk(chunkX: Int, chunkZ: Int, dimensionId: String): Iterator<VsiPlayer> {
   }

   public abstract fun getChunkWatchTasks(): VsiChunkWatchTasks {
   }

   public abstract fun setExecutedChunkWatchTasks(watchTasks: Iterable<VsiChunkWatchTask>, unwatchTasks: Iterable<VsiChunkUnwatchTask>) {
   }

   public abstract fun createNewShipAtBlock(
      blockPosInWorldCoordinates: Vector3ic,
      createShipObjectImmediately: Boolean,
      scaling: Double = 1.0,
      dimensionId: String
   ): ServerShip {
   }

   public abstract fun createPhysicsEntity(physicsEntityData: PhysicsEntityData, dimensionId: String): PhysicsEntityServer {
   }

   public abstract fun deletePhysicsEntity(id: Long) {
   }

   public abstract fun allocateShipId(dimensionId: String): Long {
   }

   public abstract fun retrieveLoadedPhysicsEntities(): Map<Long, PhysicsEntityServer> {
   }

   public abstract fun onDisconnect(player: VsiPlayer) {
   }

   public abstract fun deleteShip(ship: ServerShip) {
   }

   public abstract fun teleportShip(ship: ServerShip, teleportData: ShipTeleportData) {
   }

   public abstract fun teleportPhysicsEntity(physicsEntityServer: PhysicsEntityServer, teleportData: ShipTeleportData) {
   }

   public val dimensionToGroundBodyIdImmutable: Map<String, Long>
}
