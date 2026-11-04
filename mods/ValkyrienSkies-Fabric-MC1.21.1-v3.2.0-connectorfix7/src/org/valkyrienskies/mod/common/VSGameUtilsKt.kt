@file:SourceDebugExtension(["SMAP\nVSGameUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSGameUtils.kt\norg/valkyrienskies/mod/common/VSGameUtilsKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,629:1\n278#1,18:634\n278#1,18:652\n1863#2,2:630\n1863#2,2:632\n1#3:670\n*S KotlinDebug\n*F\n+ 1 VSGameUtils.kt\norg/valkyrienskies/mod/common/VSGameUtilsKt\n*L\n264#1:634,18\n272#1:652,18\n243#1:630,2\n256#1:632,2\n*E\n"])

package org.valkyrienskies.mod.common

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import java.util.ArrayList
import java.util.HashMap
import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1255
import net.minecraft.class_1297
import net.minecraft.class_1657
import net.minecraft.class_1923
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2374
import net.minecraft.class_238
import net.minecraft.class_2382
import net.minecraft.class_243
import net.minecraft.class_2680
import net.minecraft.class_2802
import net.minecraft.class_2826
import net.minecraft.class_2960
import net.minecraft.class_310
import net.minecraft.class_3215
import net.minecraft.class_3218
import net.minecraft.class_5321
import net.minecraft.class_638
import net.minecraft.server.MinecraftServer
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3ic
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.slf4j.LoggerFactory
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.api.events.TickEndEvent
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.util.functions.DoubleTernaryConsumer
import org.valkyrienskies.core.api.world.LevelYRange
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData
import org.valkyrienskies.core.internal.world.VsiClientShipWorld
import org.valkyrienskies.core.internal.world.VsiPipeline
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.core.internal.world.VsiShipWorld
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate
import org.valkyrienskies.core.util.AABBdUtilKt
import org.valkyrienskies.mod.common.entity.ShipMountedToData
import org.valkyrienskies.mod.common.entity.ShipMountedToDataProvider
import org.valkyrienskies.mod.common.util.DimensionIdProvider
import org.valkyrienskies.mod.common.util.EntityDragger
import org.valkyrienskies.mod.common.util.MinecraftPlayer
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.compat.SableCompat
import org.valkyrienskies.mod.mixin.accessors.resource.ResourceKeyAccessor
import org.valkyrienskies.mod.mixinducks.world.entity.PlayerDuck

private final val levelResourceKeyMap: MutableMap<String, class_5321<class_1937>> = HashMap() as java.util.Map

public final val vsCore: VsiCore
   public final get() {
      return vsCore
   }


public final val shipWorldNullable: VsiShipWorld?
   public final get() {
      val var10000: VsiShipWorld
      if (`$this$shipWorldNullable` == null) {
         var10000 = null
      } else if (`$this$shipWorldNullable` is class_3218) {
         val var1: MinecraftServer = (`$this$shipWorldNullable` as class_3218).method_8503()
         var10000 = shipObjectWorld
      } else {
         var10000 = if (`$this$shipWorldNullable`.field_9236 && `$this$shipWorldNullable` is class_638) shipObjectWorld else null
      }

      return var10000
   }


public final val shipObjectWorld: VsiShipWorld
   public final get() {
      var var10000: VsiShipWorld = shipWorldNullable
      if (var10000 == null) {
         var10000 = vsCore.dummyShipWorldClient
      }

      return var10000
   }


public final val allShips: VsiQueryableShipData<Ship>
   public final get() {
      return shipObjectWorld.allShips
   }


public final val shipObjectWorld: VsiServerShipWorld
   public final get() {
      var var10000: VsiServerShipWorld = (`$this$shipObjectWorld` as IShipObjectWorldServerProvider).shipObjectWorld
      if (var10000 == null) {
         var10000 = vsCore.dummyShipWorldServer
      }

      return var10000
   }


public final val vsPipeline: VsiPipeline
   public final get() {
      val var10000: VsiPipeline = (`$this$vsPipeline` as IShipObjectWorldServerProvider).vsPipeline
      return var10000
   }


public final val shipObjectWorld: VsiServerShipWorld
   public final get() {
      if (`$this$shipObjectWorld` != null) {
         val var10000: MinecraftServer = `$this$shipObjectWorld`.method_8503()
         if (var10000 != null) {
            val var1: VsiServerShipWorld = shipObjectWorld
            if (var1 != null) {
               return var1
            }
         }
      }

      return vsCore.dummyShipWorldServer
   }


public final val dimensionId: String
   public final get() {
      return (`$this$dimensionId` as DimensionIdProvider).dimensionId
   }


public fun getResourceKey(dimensionId: String): class_5321<class_1937> {
   val cached: class_5321 = levelResourceKeyMap.get(dimensionId)
   if (cached == null) {
      val var2: java.util.List = StringsKt.split$default(dimensionId, arrayOf(":"), false, 0, 6, null)
      val var9: class_5321 = ResourceKeyAccessor.callCreate(
         class_2960.method_60655(var2.get(0) as java.lang.String, var2.get(1) as java.lang.String),
         class_2960.method_60655(var2.get(2) as java.lang.String, var2.get(3) as java.lang.String)
      )
      levelResourceKeyMap.put(dimensionId, var9)
      return var9
   } else {
      return cached
   }
}

public fun MinecraftServer.executeIf(condition: () -> Boolean, toExecute: Runnable) {
   vsCore.getTickEndEvent().on({ ev: TickEndEvent, handler: RegisteredListener ->
      if (ev.world == shipObjectWorld) {
         if (`$condition`() as java.lang.Boolean) {
            `$toExecute`.run()
            handler.unregister()
         } else if (`$this_executeIf`.method_3780() - `$registeredAtTick` > 600) {
            LoggerFactory.getLogger("VS2").info(" executeIf timed out after 600 ticks — forcing execution")
            `$toExecute`.run()
            handler.unregister()
         }
      }
   })
}

public final val yRange: LevelYRange
   public final get() {
      return LevelYRange(`$this$yRange`.method_31607(), `$this$yRange`.method_31600() - 1)
   }


public fun class_1937.isTickingChunk(pos: class_1923): Boolean {
   return isTickingChunk(`$this$isTickingChunk`, pos.field_9181, pos.field_9180)
}

public fun class_1937.isTickingChunk(chunkX: Int, chunkZ: Int): Boolean {
   val var10000: class_2802 = `$this$isTickingChunk`.method_8398()
   return (var10000 as class_3215).method_37114(class_1923.method_8331(chunkX, chunkZ))
}

public fun class_1937.isChunkLoadedForVS(pos: class_1923): Boolean {
   if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(pos.field_9181, pos.field_9180)) {
      val var10000: class_2802 = `$this$isChunkLoadedForVS`.method_8398()
      return (var10000 as class_3215).method_21730(pos.field_9181, pos.field_9180) != null
   } else {
      return isTickingChunk(`$this$isChunkLoadedForVS`, pos)
   }
}

public fun MinecraftServer.getLevelFromDimensionId(dimensionId: String): class_3218? {
   return `$this$getLevelFromDimensionId`.method_3847(getResourceKey(dimensionId))
}

public final val shipObjectWorld: VsiClientShipWorld
   public final get() {
      var var10000: VsiClientShipWorld = (`$this$shipObjectWorld` as IShipObjectWorldClientProvider).shipObjectWorld
      if (var10000 == null) {
         var10000 = vsCore.dummyShipWorldClient
      }

      return var10000
   }


public final val shipObjectWorld: VsiClientShipWorld
   public final get() {
      val var10000: class_310 = class_310.method_1551()
      return shipObjectWorld
   }


public final val mcPlayer: class_1657
   public final get() {
      val var10000: Any = (`$this$mcPlayer` as MinecraftPlayer).playerEntityReference.get()
      return var10000 as class_1657
   }


public final val playerWrapper: MinecraftPlayer
   public final get() {
      return (`$this$playerWrapper` as PlayerDuck).vs_getPlayer()
   }


public fun class_1297.squaredDistanceToInclShips(x: Double, y: Double, z: Double): Double {
   val var8: class_243
   if (getShipMountedTo(`$this$squaredDistanceToInclShips`) != null) {
      val var10000: ShipMountedToData = getShipMountedToData(`$this$squaredDistanceToInclShips`, null)
      var8 = VectorConversionsMCKt.toMinecraft(var10000.mountPosInShip)
   } else {
      var8 = EntityDragger.INSTANCE.serversidePosition(`$this$squaredDistanceToInclShips`)
   }

   return squaredDistanceBetweenInclShips(`$this$squaredDistanceToInclShips`.method_37908(), x, y, z, var8.field_1352, var8.field_1351, var8.field_1350)
}

public fun class_1937?.squaredDistanceBetweenInclShips(v1: class_243, v2: class_243, originalDistance: Operation<Double>?): Double {
   if (originalDistance == null) {
      return squaredDistanceBetweenInclShips(
         `$this$squaredDistanceBetweenInclShips`, v1.field_1352, v1.field_1351, v1.field_1350, v2.field_1352, v2.field_1351, v2.field_1350
      )
   } else {
      val var10000: Any = originalDistance.call(
         arrayOf(toWorldCoordinates(`$this$squaredDistanceBetweenInclShips`, v1), toWorldCoordinates(`$this$squaredDistanceBetweenInclShips`, v2))
      )
      return (var10000 as java.lang.Number).doubleValue()
   }
}

public fun class_1937?.squaredDistanceBetweenInclShips(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
   var inWorldX1: Double = 0.0
   inWorldX1 = x1
   var inWorldY1: Double = 0.0
   inWorldY1 = y1
   var inWorldZ1: Double = 0.0
   inWorldZ1 = z1
   var inWorldX2: Double = 0.0
   inWorldX2 = x2
   var inWorldY2: Double = 0.0
   inWorldY2 = y2
   var inWorldZ2: Double = 0.0
   inWorldZ2 = z2
   var var10000: Ship = getShipManagingPos(`$this$squaredDistanceBetweenInclShips`, (int)x1 shr 4, (int)z1 shr 4)
   if (var10000 != null) {
      val var40: Matrix4dc = var10000.shipToWorld
      if (var40 != null) {
         inWorldX1 = var40.m00() * x1 + var40.m10() * y1 + var40.m20() * z1 + var40.m30()
         inWorldY1 = var40.m01() * x1 + var40.m11() * y1 + var40.m21() * z1 + var40.m31()
         inWorldZ1 = var40.m02() * x1 + var40.m12() * y1 + var40.m22() * z1 + var40.m32()
      }
   }

   var10000 = getShipManagingPos(`$this$squaredDistanceBetweenInclShips`, (int)x2 shr 4, (int)z2 shr 4)
   if (var10000 != null) {
      val var42: Matrix4dc = var10000.shipToWorld
      if (var42 != null) {
         inWorldX2 = var42.m00() * x2 + var42.m10() * y2 + var42.m20() * z2 + var42.m30()
         inWorldY2 = var42.m01() * x2 + var42.m11() * y2 + var42.m21() * z2 + var42.m31()
         inWorldZ2 = var42.m02() * x2 + var42.m12() * y2 + var42.m22() * z2 + var42.m32()
      }
   }

   return (inWorldX2 - inWorldX1) * (inWorldX2 - inWorldX1)
      + (inWorldY2 - inWorldY1) * (inWorldY2 - inWorldY1)
      + (inWorldZ2 - inWorldZ1) * (inWorldZ2 - inWorldZ1)
   }

private fun getShipObjectManagingPosImpl(world: class_1937?, chunkX: Int, chunkZ: Int): LoadedShip? {
   if (world != null && isChunkInShipyard(world, chunkX, chunkZ)) {
      val ship: Ship = shipObjectWorld.allShips.getByChunkPos(chunkX, chunkZ, dimensionId)
      if (ship != null) {
         return shipObjectWorld.loadedShips.getById(ship.id)
      }
   }

   return null
}

public fun class_1937.transformFromWorldToNearbyShipsAndWorld(aabb: class_238, cb: Consumer<class_238>) {
   val tmpAABB: AABBd = AABBd()
   cb.accept(aabb)

   for (`element$iv` in getShipsIntersecting(`$this$transformFromWorldToNearbyShipsAndWorld`, aabb)) {
      val var10001: AABBd = VectorConversionsMCKt.set(tmpAABB, aabb).transform((`element$iv` as Ship).worldToShip)
      cb.accept(VectorConversionsMCKt.toMinecraft(var10001))
   }
}

public fun class_1937.transformFromWorldToNearbyShips(aabb: class_238, cb: Consumer<class_238>) {
   val tmpAABB: AABBd = AABBd()

   for (`element$iv` in getShipsIntersecting(`$this$transformFromWorldToNearbyShips`, aabb)) {
      val var10001: AABBd = VectorConversionsMCKt.set(tmpAABB, aabb).transform((`element$iv` as Ship).worldToShip)
      cb.accept(VectorConversionsMCKt.toMinecraft(var10001))
   }
}

public fun class_1937?.transformToNearbyShipsAndWorld(x: Double, y: Double, z: Double, aabbRadius: Double): List<Vector3d> {
   val list: java.util.List = ArrayList()
   if (`$this$transformToNearbyShipsAndWorld` != null) {
      val `currentShip$iv`: Ship = getShipManagingPos(`$this$transformToNearbyShipsAndWorld`, x, y, z)
      val `aabb$iv`: AABBd = AABBdUtilKt.expand(AABBd(x, y, z, x, y, z), aabbRadius)
      val `posInWorld$iv`: Vector3d = Vector3d(x, y, z)
      val `temp0$iv`: Vector3d = Vector3d()
      if (`currentShip$iv` != null) {
         `currentShip$iv`.shipToWorld.transformPosition(`posInWorld$iv`)
         list.add(Vector3d(`posInWorld$iv`.x(), `posInWorld$iv`.y(), `posInWorld$iv`.z()))
      }

      for (`nearbyShip$iv` in shipObjectWorld.allShips.getIntersecting(`aabb$iv`, dimensionId)) {
         if (!(`nearbyShip$iv` == `currentShip$iv`)) {
            val `posInShip$iv`: Vector3d = `nearbyShip$iv`.worldToShip.transformPosition(`posInWorld$iv`, `temp0$iv`)
            list.add(Vector3d(`posInShip$iv`.x(), `posInShip$iv`.y(), `posInShip$iv`.z()))
         }
      }
   }

   return list
}

public fun class_1937?.transformToNearbyShipsAndWorld(x: Double, y: Double, z: Double, aabbRadius: Double, cb: DoubleTernaryConsumer) {
   if (`$this$transformToNearbyShipsAndWorld` != null) {
      val `currentShip$iv`: Ship = getShipManagingPos(`$this$transformToNearbyShipsAndWorld`, x, y, z)
      val `aabb$iv`: AABBd = AABBdUtilKt.expand(AABBd(x, y, z, x, y, z), aabbRadius)
      val `posInWorld$iv`: Vector3d = Vector3d(x, y, z)
      val `temp0$iv`: Vector3d = Vector3d()
      if (`currentShip$iv` != null) {
         `currentShip$iv`.shipToWorld.transformPosition(`posInWorld$iv`)
         cb.accept(`posInWorld$iv`.x(), `posInWorld$iv`.y(), `posInWorld$iv`.z())
      }

      for (`nearbyShip$iv` in shipObjectWorld.allShips.getIntersecting(`aabb$iv`, dimensionId)) {
         if (!(`nearbyShip$iv` == `currentShip$iv`)) {
            val `posInShip$iv`: Vector3d = `nearbyShip$iv`.worldToShip.transformPosition(`posInWorld$iv`, `temp0$iv`)
            cb.accept(`posInShip$iv`.x(), `posInShip$iv`.y(), `posInShip$iv`.z())
         }
      }
   }
}

public inline fun class_1937.transformToNearbyShipsAndWorld(x: Double, y: Double, z: Double, aabbRadius: Double, cb: (Double, Double, Double) -> Unit) {
   val currentShip: Ship = getShipManagingPos(`$this$transformToNearbyShipsAndWorld`, x, y, z)
   val aabb: AABBd = AABBdUtilKt.expand(AABBd(x, y, z, x, y, z), aabbRadius)
   val posInWorld: Vector3d = Vector3d(x, y, z)
   val temp0: Vector3d = Vector3d()
   if (currentShip != null) {
      currentShip.shipToWorld.transformPosition(posInWorld)
      cb(posInWorld.x(), posInWorld.y(), posInWorld.z())
   }

   for (nearbyShip in shipObjectWorld.allShips.getIntersecting(aabb, dimensionId)) {
      if (!(nearbyShip == currentShip)) {
         val posInShip: Vector3d = nearbyShip.worldToShip.transformPosition(posInWorld, temp0)
         cb(posInShip.x(), posInShip.y(), posInShip.z())
      }
   }
}

public fun class_1937.isChunkInShipyard(chunkX: Int, chunkZ: Int): Boolean {
   return (!sable || !SableCompat.INSTANCE.isChunkInSublevel(`$this$isChunkInShipyard`, chunkX, chunkZ))
      && shipObjectWorld.isChunkInShipyard(chunkX, chunkZ, dimensionId)
   }

public fun class_1937.isBlockInShipyard(blockX: Int, blockY: Int, blockZ: Int): Boolean {
   return (!sable || !SableCompat.INSTANCE.isBlockInSublevel(`$this$isBlockInShipyard`, blockX, blockY, blockZ))
      && shipObjectWorld.isBlockInShipyard(blockX, blockY, blockZ, dimensionId)
   }

public fun class_1937.isBlockInShipyard(pos: class_2338): Boolean {
   return isBlockInShipyard(`$this$isBlockInShipyard`, pos.method_10263(), pos.method_10264(), pos.method_10260())
}

public fun class_1937.isBlockInShipyard(pos: class_243): Boolean {
   return isBlockInShipyard(`$this$isBlockInShipyard`, (int)pos.field_1352, (int)pos.field_1351, (int)pos.field_1350)
}

public fun class_1937.isBlockInShipyard(x: Double, y: Double, z: Double): Boolean {
   return isBlockInShipyard(`$this$isBlockInShipyard`, (int)x, (int)y, (int)z)
}

public fun class_1937?.getLoadedShipManagingPos(chunkX: Int, chunkZ: Int): LoadedShip? {
   return getShipObjectManagingPosImpl(`$this$getLoadedShipManagingPos`, chunkX, chunkZ)
}

public fun class_1937?.getLoadedShipManagingPos(blockPos: class_2382): LoadedShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, blockPos.method_10263() shr 4, blockPos.method_10260() shr 4)
}

public fun class_1937?.getLoadedShipManagingPos(pos: Vector3dc): LoadedShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, (int)pos.x() shr 4, (int)pos.z() shr 4)
}

public fun class_1937?.getLoadedShipManagingPos(posX: Double, posY: Double, posZ: Double): LoadedShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, (int)posX shr 4, (int)posZ shr 4)
}

public fun class_1937?.getLoadedShipManagingPos(chunkPos: class_1923): LoadedShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, chunkPos.field_9181, chunkPos.field_9180)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(chunkX, chunkZ)", imports = []))
public fun class_1937?.getShipObjectManagingPos(chunkX: Int, chunkZ: Int): LoadedShip? {
   return getShipObjectManagingPosImpl(`$this$getShipObjectManagingPos`, chunkX, chunkZ)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(blockPos)", imports = []))
public fun class_1937?.getShipObjectManagingPos(blockPos: class_2382): LoadedShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, blockPos.method_10263() shr 4, blockPos.method_10260() shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(pos)", imports = []))
public fun class_1937?.getShipObjectManagingPos(pos: Vector3dc): LoadedShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, (int)pos.x() shr 4, (int)pos.z() shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(posX, posY, posZ)", imports = []))
public fun class_1937?.getShipObjectManagingPos(posX: Double, posY: Double, posZ: Double): LoadedShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, (int)posX shr 4, (int)posZ shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(chunkPos)", imports = []))
public fun class_1937?.getShipObjectManagingPos(chunkPos: class_1923): LoadedShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, chunkPos.field_9181, chunkPos.field_9180)
}

public fun class_638?.getLoadedShipManagingPos(chunkX: Int, chunkZ: Int): ClientShip? {
   return getShipObjectManagingPosImpl(`$this$getLoadedShipManagingPos` as class_1937, chunkX, chunkZ) as ClientShip
}

public fun class_638?.getLoadedShipManagingPos(blockPos: class_2382): ClientShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, blockPos.method_10263() shr 4, blockPos.method_10260() shr 4)
}

public fun class_638?.getLoadedShipManagingPos(posX: Double, posY: Double, posZ: Double): ClientShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, (int)posX shr 4, (int)posZ shr 4)
}

public fun class_638?.getLoadedShipManagingPos(pos: Vector3dc): ClientShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, (int)pos.x() shr 4, (int)pos.z() shr 4)
}

public fun class_638?.getLoadedShipManagingPos(pos: class_2374): ClientShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, (int)pos.method_10216() shr 4, (int)pos.method_10215() shr 4)
}

public fun class_638?.getLoadedShipManagingPos(chunkPos: class_1923): ClientShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, chunkPos.field_9181, chunkPos.field_9180)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(chunkX, chunkZ)", imports = []))
public fun class_638?.getShipObjectManagingPos(chunkX: Int, chunkZ: Int): ClientShip? {
   return getShipObjectManagingPosImpl(`$this$getShipObjectManagingPos` as class_1937, chunkX, chunkZ) as ClientShip
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(blockPos)", imports = []))
public fun class_638?.getShipObjectManagingPos(blockPos: class_2382): ClientShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, blockPos.method_10263() shr 4, blockPos.method_10260() shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(pos)", imports = []))
public fun class_638?.getShipObjectManagingPos(posX: Double, posY: Double, posZ: Double): ClientShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, (int)posX shr 4, (int)posZ shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(pos)", imports = []))
public fun class_638?.getShipObjectManagingPos(pos: Vector3dc): ClientShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, (int)pos.x() shr 4, (int)pos.z() shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(chunkPos)", imports = []))
public fun class_638?.getShipObjectManagingPos(pos: class_2374): ClientShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, (int)pos.method_10216() shr 4, (int)pos.method_10215() shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(chunkPos)", imports = []))
public fun class_638?.getShipObjectManagingPos(chunkPos: class_1923): ClientShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, chunkPos.field_9181, chunkPos.field_9180)
}

public fun class_3218?.getLoadedShipManagingPos(chunkX: Int, chunkZ: Int): LoadedServerShip? {
   return getShipObjectManagingPosImpl(`$this$getLoadedShipManagingPos` as class_1937, chunkX, chunkZ) as LoadedServerShip
}

public fun class_3218?.getLoadedShipManagingPos(blockPos: class_2382): LoadedServerShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, blockPos.method_10263() shr 4, blockPos.method_10260() shr 4)
}

public fun class_3218?.getLoadedShipManagingPos(chunkPos: class_1923): LoadedServerShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, chunkPos.field_9181, chunkPos.field_9180)
}

public fun class_3218?.getLoadedShipManagingPos(posX: Double, posY: Double, posZ: Double): LoadedServerShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, (int)posX shr 4, (int)posZ shr 4)
}

public fun class_3218?.getLoadedShipManagingPos(pos: Vector3dc): LoadedServerShip? {
   return getLoadedShipManagingPos(`$this$getLoadedShipManagingPos`, (int)pos.x() shr 4, (int)pos.z() shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(chunkX, chunkZ)", imports = []))
public fun class_3218?.getShipObjectManagingPos(chunkX: Int, chunkZ: Int): LoadedServerShip? {
   return getShipObjectManagingPosImpl(`$this$getShipObjectManagingPos` as class_1937, chunkX, chunkZ) as LoadedServerShip
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(blockPos)", imports = []))
public fun class_3218?.getShipObjectManagingPos(blockPos: class_2382): LoadedServerShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, blockPos.method_10263() shr 4, blockPos.method_10260() shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(pos)", imports = []))
public fun class_3218?.getShipObjectManagingPos(chunkPos: class_1923): LoadedServerShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, chunkPos.field_9181, chunkPos.field_9180)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(posX, posY, posZ)", imports = []))
public fun class_3218?.getShipObjectManagingPos(posX: Double, posY: Double, posZ: Double): LoadedServerShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, (int)posX shr 4, (int)posZ shr 4)
}

@Deprecated(message = "Use getLoadedShipManagingPos instead", replaceWith = @ReplaceWith(expression = "getLoadedShipManagingPos(pos)", imports = []))
public fun class_3218?.getShipObjectManagingPos(pos: Vector3dc): LoadedServerShip? {
   return getShipObjectManagingPos(`$this$getShipObjectManagingPos`, (int)pos.x() shr 4, (int)pos.z() shr 4)
}

private fun getShipManagingPosImpl(world: class_1937?, x: Int, z: Int): Ship? {
   return if (world != null && isChunkInShipyard(world, x, z)) shipObjectWorld.allShips.getByChunkPos(x, z, dimensionId) else null
}

public fun class_638?.transformRenderAABBToWorld(pos: class_2374, aabb: class_238): class_238 {
   val ship: ClientShip = getLoadedShipManagingPos(`$this$transformRenderAABBToWorld`, pos)
   return if (ship != null) transformRenderAABBToWorld(ship, aabb) else aabb
}

public fun ClientShip.transformRenderAABBToWorld(aabb: class_238): class_238 {
   val var10000: AABBd = VectorConversionsMCKt.toJOML(aabb).transform(`$this$transformRenderAABBToWorld`.renderTransform.shipToWorldMatrix)
   return VectorConversionsMCKt.toMinecraft(var10000)
}

public fun class_1297?.getShipManaging(): Ship? {
   val var3: Ship
   if (`$this$getShipManaging` != null) {
      val var10000: class_1937 = `$this$getShipManaging`.method_37908()
      val var10001: class_243 = `$this$getShipManaging`.method_19538()
      var3 = getShipManagingPos(var10000, var10001 as class_2374)
   } else {
      var3 = null
   }

   return var3
}

public fun class_1937?.getShipManagingPos(chunkX: Int, chunkZ: Int): Ship? {
   return getShipManagingPosImpl(`$this$getShipManagingPos`, chunkX, chunkZ)
}

public fun class_1937?.getShipManagingPos(blockPos: class_2338): Ship? {
   return getShipManagingPos(`$this$getShipManagingPos`, blockPos.method_10263() shr 4, blockPos.method_10260() shr 4)
}

public fun class_1937?.getShipManagingPos(pos: class_2374): Ship? {
   return getShipManagingPos(`$this$getShipManagingPos`, (int)pos.method_10216() shr 4, (int)pos.method_10215() shr 4)
}

public fun class_1937?.getShipManagingPos(pos: Vector3dc): Ship? {
   return getShipManagingPos(`$this$getShipManagingPos`, (int)pos.x() shr 4, (int)pos.z() shr 4)
}

public fun class_1937?.getShipManagingPos(posX: Double, posY: Double, posZ: Double): Ship? {
   return getShipManagingPos(`$this$getShipManagingPos`, (int)posX shr 4, (int)posZ shr 4)
}

public fun class_1937?.getShipManagingPos(posX: Float, posY: Float, posZ: Float): Ship? {
   return getShipManagingPos(`$this$getShipManagingPos`, (int)posX shr 4, (int)posZ shr 4)
}

public fun class_1937?.getShipManagingPos(chunkPos: class_1923): Ship? {
   return getShipManagingPos(`$this$getShipManagingPos`, chunkPos.field_9181, chunkPos.field_9180)
}

public fun class_3218?.getShipManagingPos(chunkX: Int, chunkZ: Int): ServerShip? {
   return getShipManagingPosImpl(`$this$getShipManagingPos` as class_1937, chunkX, chunkZ) as ServerShip
}

public fun class_3218?.getShipManagingPos(blockPos: class_2338): ServerShip? {
   return getShipManagingPos(`$this$getShipManagingPos`, blockPos.method_10263() shr 4, blockPos.method_10260() shr 4)
}

public fun class_3218?.getShipManagingPos(pos: Vector3dc): ServerShip? {
   return getShipManagingPos(`$this$getShipManagingPos`, (int)pos.x() shr 4, (int)pos.z() shr 4)
}

public fun class_3218?.getShipManagingPos(posX: Double, posY: Double, posZ: Double): ServerShip? {
   return getShipManagingPos(`$this$getShipManagingPos`, (int)posX shr 4, (int)posZ shr 4)
}

public fun class_3218?.getShipManagingPos(chunkPos: class_1923): ServerShip? {
   return getShipManagingPos(`$this$getShipManagingPos`, chunkPos.field_9181, chunkPos.field_9180)
}

public fun Ship.toWorldCoordinates(pos: class_2338): Vector3d {
   val var10000: Vector3d = `$this$toWorldCoordinates`.shipToWorld.transformPosition(VectorConversionsMCKt.toJOMLD(pos as class_2382))
   return var10000
}

public fun Ship.toWorldCoordinates(pos: class_243): class_243 {
   val var10000: Vector3d = `$this$toWorldCoordinates`.shipToWorld.transformPosition(VectorConversionsMCKt.toJOML(pos))
   return VectorConversionsMCKt.toMinecraft(var10000)
}

public fun class_1937?.toWorldCoordinates(pos: class_2338): class_243 {
   if (`$this$toWorldCoordinates` != null) {
      val var10000: Ship = getShipManagingPos(`$this$toWorldCoordinates`, pos)
      if (var10000 != null) {
         val var2: Vector3d = toWorldCoordinates(var10000, pos)
         if (var2 != null) {
            val var3: class_243 = VectorConversionsMCKt.toMinecraft(var2)
            if (var3 != null) {
               return var3
            }
         }
      }
   }

   return VectorConversionsMCKt.toMinecraft(VectorConversionsMCKt.toJOMLD(pos as class_2382))
}

public fun class_1937?.toWorldCoordinates(pos: class_243): class_243 {
   if (`$this$toWorldCoordinates` != null) {
      val var10000: Ship = getShipManagingPos(`$this$toWorldCoordinates`, pos as class_2374)
      if (var10000 != null) {
         val var2: class_243 = toWorldCoordinates(var10000, pos)
         if (var2 != null) {
            return var2
         }
      }
   }

   return pos
}

public fun class_638?.toShipRenderCoordinates(shipPos: class_243, pos: class_243): class_243 {
   if (`$this$toShipRenderCoordinates` != null) {
      val var3: ClientShip = getLoadedShipManagingPos(`$this$toShipRenderCoordinates`, shipPos as class_2374)
      if (var3 != null) {
         val var4: ShipTransform = var3.renderTransform
         if (var4 != null) {
            val var5: Matrix4dc = var4.worldToShip
            if (var5 != null) {
               val var6: Vector3d = var5.transformPosition(VectorConversionsMCKt.toJOML(pos))
               if (var6 != null) {
                  val var7: class_243 = VectorConversionsMCKt.toMinecraft(var6)
                  if (var7 != null) {
                     return var7
                  }
               }
            }
         }
      }
   }

   return pos
}

public fun class_1937?.toWorldCoordinates(pos: Vector3d): Vector3d {
   if (`$this$toWorldCoordinates` != null) {
      val var10000: Ship = getShipManagingPos(`$this$toWorldCoordinates`, pos)
      if (var10000 != null) {
         val var2: Matrix4dc = var10000.shipToWorld
         if (var2 != null) {
            val var3: Vector3d = var2.transformPosition(pos)
            if (var3 != null) {
               return var3
            }
         }
      }
   }

   return pos
}

@JvmOverloads
public fun class_1937?.toWorldCoordinates(x: Double, y: Double, z: Double, dest: Vector3d = Vector3d()): Vector3d {
   val var10000: Ship = getShipManagingPos(`$this$toWorldCoordinates`, x, y, z)
   if (var10000 != null) {
      val var8: Vector3d = toWorldCoordinates$default(var10000, x, y, z, null, 8, null)
      if (var8 != null) {
         return var8
      }
   }

   val var9: Vector3d = dest.set(x, y, z)
   return var9
}

@JvmOverloads
public fun Ship.toWorldCoordinates(x: Double, y: Double, z: Double, dest: Vector3d = Vector3d()): Vector3d {
   val var10000: Vector3d = `$this$toWorldCoordinates`.transform.shipToWorld.transformPosition(dest.set(x, y, z))
   return var10000
}

public fun class_2826.toDenseVoxelUpdate(chunkPos: Vector3ic): VsiTerrainUpdate {
   val update: VsiTerrainUpdate.Builder = vsCore.newDenseTerrainUpdateBuilder(chunkPos.x(), chunkPos.y(), chunkPos.z())
   val info: BlockStateInfo.Cache = BlockStateInfo.INSTANCE.cache
   val airType: VsiBlockType = vsCore.blockTypes.air

   repeat(15) { x ->
      repeat(15) { y ->
         repeat(15) { z ->
            var var8: VsiBlockType
            run label46@{
               val var10005: class_2680 = `$this$toDenseVoxelUpdate`.method_12254(x, y, z)
               val var10004: Pair = info.get(var10005)
               if (var10004 != null) {
                  var8 = var10004.getSecond() as VsiBlockType
                  if (var8 != null) {
                     return@label46
                  }
               }

               var8 = airType
            }

            update.addBlock(x, y, z, var8)
         }
      }
   }

   return update.build()
}

public fun class_1937?.getWorldCoordinates(blockPos: class_2338, pos: Vector3d): Vector3d {
   val var10000: LoadedShip = getLoadedShipManagingPos(`$this$getWorldCoordinates`, blockPos as class_2382)
   if (var10000 != null) {
      val var3: ShipTransform = var10000.getTransform()
      if (var3 != null) {
         val var4: Matrix4dc = var3.shipToWorld
         if (var4 != null) {
            val var5: Vector3d = var4.transformPosition(pos)
            if (var5 != null) {
               return var5
            }
         }
      }
   }

   return pos
}

public fun class_1937.getShipsIntersecting(aabb: class_238): Iterable<Ship> {
   return getShipsIntersecting(`$this$getShipsIntersecting`, VectorConversionsMCKt.toJOML(aabb))
}

public fun class_1937.getShipsIntersecting(aabb: AABBdc): Iterable<Ship> {
   return allShips.getIntersecting(aabb, dimensionId)
}

public fun class_1937?.transformAabbToWorld(aabb: class_238): class_238 {
   return VectorConversionsMCKt.toMinecraft(transformAabbToWorld(`$this$transformAabbToWorld`, VectorConversionsMCKt.toJOML(aabb)))
}

public fun class_1937?.transformAabbToWorld(aabb: AABBd): AABBd {
   if (`$this$transformAabbToWorld` != null) {
      val var10000: AABBd = transformAabbToWorld(`$this$transformAabbToWorld`, aabb, aabb)
      if (var10000 != null) {
         return var10000
      }
   }

   return aabb
}

public fun class_1937?.transformAabbToWorld(aabb: AABBdc, dest: AABBd): AABBd {
   var var10000: Ship = getShipManagingPos(`$this$transformAabbToWorld`, aabb.minX(), aabb.minY(), aabb.minZ())
   if (var10000 == null) {
      val var9: AABBd = dest.set(aabb)
      return var9
   } else {
      var10000 = getShipManagingPos(`$this$transformAabbToWorld`, aabb.maxX(), aabb.maxY(), aabb.maxZ())
      if (var10000 == null) {
         val var8: AABBd = dest.set(aabb)
         return var8
      } else if (var10000.id == var10000.id) {
         val var7: AABBd = aabb.transform(var10000.shipToWorld, dest)
         return var7
      } else {
         val var6: AABBd = dest.set(aabb)
         return var6
      }
   }
}

public fun class_1937.executeOrSchedule(runnable: Runnable) {
   val var4: class_1255
   if (!`$this$executeOrSchedule`.field_9236) {
      val var10000: MinecraftServer = `$this$executeOrSchedule`.method_8503()
      var4 = var10000 as class_1255
   } else {
      val var3: class_310 = class_310.method_1551()
      var4 = var3 as class_1255
   }

   if (var4.method_18854()) {
      runnable.run()
   } else {
      var4.execute(runnable)
   }
}

public fun getShipMountedToData(passenger: class_1297, partialTicks: Float? = null): ShipMountedToData? {
   val var10000: class_1297 = passenger.method_5854()
   if (var10000 == null) {
      return null
   } else if (var10000 is ShipMountedToDataProvider) {
      return (var10000 as ShipMountedToDataProvider).provideShipMountedToData(passenger, partialTicks)
   } else {
      val var5: class_1937 = passenger.method_37908()
      val var10001: class_243 = var10000.method_19538()
      val var6: LoadedShip = getLoadedShipManagingPos(var5, VectorConversionsMCKt.toJOML(var10001))
      if (var6 == null) {
         return null
      } else {
         val var7: class_243 = var10000.method_52538(passenger)
         return ShipMountedToData(var6, VectorConversionsMCKt.toJOML(var7))
      }
   }
}

public fun getShipMountedTo(entity: class_1297): LoadedShip? {
   val var10000: ShipMountedToData = getShipMountedToData$default(entity, null, 2, null)
   return if (var10000 != null) var10000.shipMountedTo else null
}

public fun class_1937.isPositionSealed(pos: class_2338): Boolean {
   val result: ConnectionStatus = shipObjectWorld.isIsolatedAir(pos.method_10263(), pos.method_10264(), pos.method_10260(), dimensionId)
   return result === ConnectionStatus.DISCONNECTED || result === ConnectionStatus.UNKNOWN
}

public fun class_1937.isPositionMaybeSealed(pos: class_2338): Boolean {
   val result: ConnectionStatus = shipObjectWorld.isIsolatedAir(pos.method_10263(), pos.method_10264(), pos.method_10260(), dimensionId)
   return result === ConnectionStatus.DISCONNECTED || result === ConnectionStatus.UNKNOWN
}

public fun class_1297?.applyShipVelocity(ship: Ship?) {
   if (`$this$applyShipVelocity` != null && ship != null) {
      val var10000: class_243 = `$this$applyShipVelocity`.method_19538()
      val shipSpeed: Vector3d = Vector3d(ship.velocity)
         .add(ship.angularVelocity.cross(VectorConversionsMCKt.toJOML(var10000).sub(ship.transform.positionInWorld), Vector3d()))
         .mul(0.05)
         `$this$applyShipVelocity`.method_5762(shipSpeed.x, shipSpeed.y, shipSpeed.z)
   }
}

public fun class_2680?.inAssemblyBlacklist(): Boolean {
   return `$this$inAssemblyBlacklist` != null && `$this$inAssemblyBlacklist`.method_26164(ValkyrienSkiesMod.INSTANCE.ASSEMBLE_BLACKLIST)
}

public fun AABBic.forEach(f: (Int, Int, Int) -> Unit) {
   var x: Int = `$this$forEach`.minX()
   val var3: Int = `$this$forEach`.maxX()
   if (x <= var3) {
      while (true) {
         var y: Int = `$this$forEach`.minY()
         val var5: Int = `$this$forEach`.maxY()
         if (y <= var5) {
            while (true) {
               var z: Int = `$this$forEach`.minZ()
               val var7: Int = `$this$forEach`.maxZ()
               if (z <= var7) {
                  while (true) {
                     f(x, y, z)
                     if (z == var7) {
                        break
                     }

                     z++
                  }
               }

               if (y == var5) {
                  break
               }

               y++
            }
         }

         if (x == var3) {
            break
         }

         x++
      }
   }
}

@JvmOverloads
fun class_1937?.toWorldCoordinates(x: Double, y: Double, z: Double): Vector3d {
   toWorldCoordinates$default(`$this$toWorldCoordinates`, x, y, z, null, 8, null)
}

@JvmOverloads
fun Ship.toWorldCoordinates(x: Double, y: Double, z: Double): Vector3d {
   toWorldCoordinates$default(`$this$toWorldCoordinates`, x, y, z, null, 8, null)
}
