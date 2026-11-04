package org.valkyrienskies.mod.api

import net.minecraft.class_1297
import net.minecraft.class_1923
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_310
import net.minecraft.server.MinecraftServer
import org.jetbrains.annotations.ApiStatus
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.VsCoreApi
import org.valkyrienskies.core.api.event.ListenableEvent
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.world.ClientShipWorld
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.mod.api.events.PostRenderShipEvent
import org.valkyrienskies.mod.api.events.PreRenderShipEvent
import org.valkyrienskies.mod.api.events.RegisterBlockStateEvent

@ApiStatus.NonExtendable
public interface VsApi : VsCoreApi {
   public val registerBlockStateEvent: ListenableEvent<RegisterBlockStateEvent>
      public abstract get() {
      }


   public val preRenderShipEvent: ListenableEvent<PreRenderShipEvent>
      public abstract get() {
      }


   public val postRenderShipEvent: ListenableEvent<PostRenderShipEvent>
      public abstract get() {
      }


   public abstract fun isShipMountingEntity(entity: class_1297): Boolean {
   }

   public abstract fun getDimensionId(level: class_1937): String {
   }

   public abstract fun getShipMountedTo(entity: class_1297?): Ship? {
   }

   public abstract fun getMountPosInShip(entity: class_1297?): Vector3dc? {
   }

   public abstract fun getMountPosInShip(entity: class_1297?, partialTicks: Float): Vector3dc? {
   }

   public abstract fun getServerShipWorld(server: MinecraftServer?): ServerShipWorld? {
   }

   public abstract fun getClientShipWorld(client: class_310?): ClientShipWorld? {
   }

   public abstract fun getShipWorld(level: class_1937?): ShipWorld? {
   }

   @ApiStatus.Experimental
   public abstract fun getServerShipWorld(): ServerShipWorld? {
   }

   @ApiStatus.Experimental
   public abstract fun getClientShipWorld(): ClientShipWorld? {
   }

   public abstract fun isChunkInShipyard(level: class_1937?, chunkX: Int, chunkZ: Int): Boolean {
   }

   public abstract fun getShipManagingBlock(level: class_1937?, pos: class_2338?): Ship? {
   }

   public abstract fun getShipManagingChunk(level: class_1937?, pos: class_1923?): Ship? {
   }

   public abstract fun getShipManagingChunk(level: class_1937?, chunkX: Int, chunkZ: Int): Ship? {
   }

   public abstract fun getShipsIntersecting(level: class_1937?, aabb: AABBdc?): Iterable<Ship> {
   }

   public abstract fun getShipsIntersecting(level: class_1937?, x: Double, y: Double, z: Double): Iterable<Ship> {
   }
}
