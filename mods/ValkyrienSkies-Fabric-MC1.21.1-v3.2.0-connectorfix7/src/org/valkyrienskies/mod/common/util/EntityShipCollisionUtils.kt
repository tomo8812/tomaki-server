package org.valkyrienskies.mod.common.util

import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1297
import net.minecraft.class_1657
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_238
import net.minecraft.class_2382
import net.minecraft.class_243
import net.minecraft.class_265
import net.minecraft.class_3218
import net.minecraft.class_3532
import net.minecraft.class_4076
import net.minecraft.class_638
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.internal.collision.VsiConvexPolygonc
import org.valkyrienskies.core.internal.collision.VsiEntityPolygonCollider
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData
import org.valkyrienskies.core.util.AABBdUtilKt
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.mixinducks.feature.tickets.PlayerKnownShipsDuck
import org.valkyrienskies.mod.util.BugFixUtil

@SourceDebugExtension(["SMAP\nEntityShipCollisionUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EntityShipCollisionUtils.kt\norg/valkyrienskies/mod/common/util/EntityShipCollisionUtils\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,406:1\n1863#2,2:407\n*S KotlinDebug\n*F\n+ 1 EntityShipCollisionUtils.kt\norg/valkyrienskies/mod/common/util/EntityShipCollisionUtils\n*L\n392#1:407,2\n*E\n"])
public object EntityShipCollisionUtils {
   private final val recentlySpawnedShips: ConcurrentHashMap<Long, Long> = ConcurrentHashMap()
   private const val SPAWN_GRACE_PERIOD_TICKS: Long = 100L
   private const val PARTICLE_COLLISION_BOX_EXPANSION: Double = 0.00390625
   private final val collider: VsiEntityPolygonCollider = vsCore.entityPolygonCollider
   private final val roughAABBCacheServer: ConcurrentHashMap<Long, org.valkyrienskies.mod.common.util.EntityShipCollisionUtils.RoughAABBEntry> =
      ConcurrentHashMap()
      private final val roughAABBCacheClient: ConcurrentHashMap<Long, org.valkyrienskies.mod.common.util.EntityShipCollisionUtils.RoughAABBEntry> =
      ConcurrentHashMap()
      private final var lastCacheSweepServer: Long = java.lang.Long.MIN_VALUE
   private final var lastCacheSweepClient: Long = java.lang.Long.MIN_VALUE
   private const val CACHE_SWEEP_INTERVAL_TICKS: Long = 1200L
   private final val worldFreezes: ConcurrentLinkedQueue<org.valkyrienskies.mod.common.util.EntityShipCollisionUtils.WorldFreezeEntry> =
      ConcurrentLinkedQueue()

   @JvmOverloads
   @JvmStatic
   public fun markShipAsRecentlySpawned(shipId: Long, currentTick: Long, durationTicks: Long = 100L) {
      recentlySpawnedShips.put(shipId, currentTick + durationTicks)
   }

   @JvmStatic
   public fun isInSpawnGracePeriod(shipId: Long, currentTick: Long): Boolean {
      val var10000: java.lang.Long = recentlySpawnedShips.get(shipId)
      if (var10000 != null) {
         if (currentTick > var10000) {
            recentlySpawnedShips.remove(shipId)
            return false
         } else {
            return true
         }
      } else {
         return false
      }
   }

   private fun getShipyardChunkAABBAround(ship: Ship): AABBi {
      val box: AABBi = AABBi()
      var var10000: AABBic = ship.shipAABB
      val minY: Int = (if (var10000 != null) var10000.minY() else class_3532.method_15357(ship.transform.getPosition().y())) - 16
      var10000 = ship.shipAABB
      ship.activeChunksSet
         .forEach(
            { x: Int, z: Int ->
               `$box`.union(class_4076.method_18688(x), `$minY`, class_4076.method_18688(z))
                  .union(class_4076.method_32205(x, 15), `$maxY`, class_4076.method_32205(z, 15))
               }
         )
         return box
   }

   private fun roughWorldAABB(ship: Ship, level: class_1937, gameTime: Long): AABBdc {
      val var7: ConcurrentHashMap
      if (level.field_9236) {
         var7 = roughAABBCacheClient
         if (gameTime - lastCacheSweepClient >= 1200L) {
            roughAABBCacheClient.clear()
            lastCacheSweepClient = gameTime
         }
      } else {
         var7 = roughAABBCacheServer
         if (gameTime - lastCacheSweepServer >= 1200L) {
            roughAABBCacheServer.clear()
            lastCacheSweepServer = gameTime
         }
      }

      val var10000: Any = var7.computeIfAbsent(ship.id, { p0: Any ->
         `$tmp0`(p0) as EntityShipCollisionUtils.RoughAABBEntry
      })
      val entry: EntityShipCollisionUtils.RoughAABBEntry = var10000 as EntityShipCollisionUtils.RoughAABBEntry
      if ((var10000 as EntityShipCollisionUtils.RoughAABBEntry).gameTime != gameTime) {
         AABBdUtilKt.toAABBd(this.getShipyardChunkAABBAround(ship), entry.aabb).transform(ship.shipToWorld)
         entry.gameTime = gameTime
      }

      return entry.aabb
   }

   @JvmStatic
   public fun isCollidingWithUnloadedShips(entity: class_1297): Boolean {
      val level: class_1937 = entity.method_37908()
      if (level is class_3218 || level.field_9236 && level is class_638) {
         if (level.field_9236 && level is class_638 && !shipObjectWorld.isSyncedWithServer) {
            return true
         } else {
            val allShips: VsiQueryableShipData = allShips
            if (CollectionsKt.none(allShips)) {
               return false
            } else {
               val gameTime: Long = level.method_8510()
               val var10000: class_238 = entity.method_5829()
               val aabb: AABBd = VectorConversionsMCKt.toJOML(var10000)

               for (ship in allShips) {
                  if (ship.chunkClaimDimension == dimensionId
                     && INSTANCE.roughWorldAABB(ship, level, gameTime).intersectsAABB(aabb)
                     && !isInSpawnGracePeriod(ship.id, gameTime)) {
                     if (entity is PlayerKnownShipsDuck && !(entity as PlayerKnownShipsDuck).vs_isKnownShip(ship.id)) {
                        return true
                     }

                     val aabbInShip: AABBd = AABBd(aabb).transform(ship.worldToShip)
                     val var9: EntityShipCollisionUtils = INSTANCE
                     if (!var9.areAllChunksLoaded(ship, aabbInShip, level)) {
                        return true
                     }
                  }
               }

               return false
            }
         }
      } else {
         return false
      }
   }

   private fun areAllChunksLoaded(ship: Ship, aABB: AABBdc, level: class_1937): Boolean {
      val minX: Int = class_3532.method_15357(aABB.minX() - 1.0E-7) - 1 shr 4
      val maxX: Int = class_3532.method_15357(aABB.maxX() + 1.0E-7) + 1 shr 4
      val minZ: Int = class_3532.method_15357(aABB.minZ() - 1.0E-7) - 1 shr 4
      val maxZ: Int = class_3532.method_15357(aABB.maxZ() + 1.0E-7) + 1 shr 4
      var chunkX: Int = minX
      if (minX <= maxX) {
         while (true) {
            var chunkZ: Int = minZ
            if (minZ <= maxZ) {
               while (true) {
                  if (ship.activeChunksSet.contains(chunkX, chunkZ) && level.method_22338(chunkX, chunkZ) == null) {
                     return false
                  }

                  if (chunkZ == maxZ) {
                     break
                  }

                  chunkZ++
               }
            }

            if (chunkX == maxX) {
               break
            }

            chunkX++
         }
      }

      return true
   }

   @JvmStatic
   public fun worldAABBForShip(ship: Ship): AABBd {
      val sb: AABBic = ship.shipAABB
      val var10000: AABBd
      if (sb != null) {
         val p: AABBd = AABBd(
               (double)sb.minX(), (double)sb.minY(), (double)sb.minZ(), (double)(sb.maxX() + 1), (double)(sb.maxY() + 1), (double)(sb.maxZ() + 1)
            )
            .transform(ship.shipToWorld)
            var10000 = p
      } else {
         val var3: Vector3dc = ship.transform.getPosition()
         var10000 = AABBd(var3.x() - 32.0, var3.y() - 32.0, var3.z() - 32.0, var3.x() + 32.0, var3.y() + 32.0, var3.z() + 32.0)
      }

      return var10000
   }

   @JvmStatic
   public fun markWorldFreeze(level: class_1937, aabb: AABBd, durationTicks: Long) {
      worldFreezes.add(EntityShipCollisionUtils.WorldFreezeEntry(dimensionId, aabb, level.method_8510() + durationTicks))
   }

   @JvmStatic
   public fun isInWorldFreeze(entity: class_1297): Boolean {
      if (worldFreezes.isEmpty()) {
         return false
      } else {
         val now: Long = entity.method_37908().method_8510()
         val var10000: class_1937 = entity.method_37908()
         val dim: java.lang.String = dimensionId
         val px: Double = entity.method_23317()
         val py: Double = entity.method_23318()
         val pz: Double = entity.method_23321()
         var held: Boolean = false
         val var13: java.util.Iterator = worldFreezes.iterator()
         val it: java.util.Iterator = var13

         while (it.hasNext()) {
            val e: EntityShipCollisionUtils.WorldFreezeEntry = it.next() as EntityShipCollisionUtils.WorldFreezeEntry
            if (now - e.deadlineGameTime > 0L) {
               it.remove()
            } else if (!held
               && e.dimensionId == dim
               && px >= e.aabb.minX()
               && px <= e.aabb.maxX()
               && py >= e.aabb.minY()
               && py <= e.aabb.maxY()
               && pz >= e.aabb.minZ()
               && pz <= e.aabb.maxZ()) {
               held = true
            }
         }

         return held
      }
   }

   @JvmStatic
   public fun shouldHoldGravity(entity: class_1297): Boolean {
      if (entity is class_1657) {
         return false
      } else if (isInWorldFreeze(entity)) {
         return true
      } else {
         val level: class_1937 = entity.method_37908()
         if (level is class_3218 || level.field_9236 && level is class_638) {
            val allShips: VsiQueryableShipData = allShips
            if (CollectionsKt.none(allShips)) {
               return false
            } else {
               val gameTime: Long = level.method_8510()
               val var10000: class_238 = entity.method_5829()
               val aabb: AABBd = VectorConversionsMCKt.toJOML(var10000)

               for (ship in allShips) {
                  if (ship.chunkClaimDimension == dimensionId
                     && INSTANCE.roughWorldAABB(ship, level, gameTime).intersectsAABB(aabb)
                     && isInSpawnGracePeriod(ship.id, gameTime)) {
                     return true
                  }
               }

               return false
            }
         } else {
            return false
         }
      }
   }

   public fun adjustEntityMovementForShipCollisions(entity: class_1297?, movement: class_243, entityBoundingBox: class_238, world: class_1937): class_243 {
      val inflation: Double = if (entity is class_1657) 0.5 else 0.1
      val stepHeight: Double = if (entity != null) entity.method_49476() else 0.0
      val var10002: class_243 = class_243(movement.method_10216(), movement.method_10214() + stepHeight / 2, movement.method_10215())
      val var10003: class_238 = entityBoundingBox.method_1009(inflation, inflation + stepHeight / (double)2, inflation)
      val collidingShipPolygons: java.util.List = this.getShipPolygonsCollidingWithEntity(entity, var10002, var10003, world)
      if (collidingShipPolygons.isEmpty()) {
         return movement
      } else {
         val collisionBoundingBox: class_238 = if (entity == null) entityBoundingBox.method_1014(0.00390625) else entityBoundingBox
         val var10000: VsiEntityPolygonCollider = collider
         val var10001: Vector3dc = VectorConversionsMCKt.toJOML(movement)
         val var11: Pair = var10000.adjustEntityMovementForPolygonCollisions(
            var10001, VectorConversionsMCKt.toJOML(collisionBoundingBox), stepHeight, collidingShipPolygons
         )
         val newMovement: Vector3dc = var11.component1() as Vector3dc
         val shipCollidingWith: java.lang.Long = var11.component2() as java.lang.Long
         val isGliding: Boolean = entity is IEntityDraggingInformationProvider && (entity as IEntityDraggingInformationProvider).vs$isGliding()
         val finalMovement: Vector3dc = if (isGliding)
            Vector3d(
               if (Math.abs(newMovement.x()) > Math.abs(movement.method_10216())) movement.method_10216() else newMovement.x(),
               if (Math.abs(newMovement.y()) > Math.abs(movement.method_10214())) movement.method_10214() else newMovement.y(),
               if (Math.abs(newMovement.z()) > Math.abs(movement.method_10215())) movement.method_10215() else newMovement.z()
            )
            else
            newMovement
            if (entity != null && !isGliding) {
            val var19: class_1937 = entity.method_37908()
            val var20: class_2338 = entity.method_23312()
            val standingOnShip: LoadedShip = VSGameUtilsKt.getLoadedShipManagingPos(var19, var20 as class_2382)
            if (shipCollidingWith != null && standingOnShip != null && standingOnShip.getId() == shipCollidingWith) {
               (entity as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn = shipCollidingWith

               for (entityRiding in entity.method_5736()) {
                  (entityRiding as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn = shipCollidingWith
               }
            }
         }

         return VectorConversionsMCKt.toMinecraft(finalMovement)
      }
   }

   public fun getShipPolygonsCollidingWithEntity(entity: class_1297?, movement: class_243, entityBoundingBox: class_238, world: class_1937): List<
         VsiConvexPolygonc
      > {
      val entityBoxWithMovement: class_238 = entityBoundingBox.method_18804(movement)
      val collidingPolygons: java.util.List = ArrayList()

      for (shipObject in shipObjectWorld.loadedShips
         .getIntersecting(AABBdUtilKt.extend(VectorConversionsMCKt.toJOML(entityBoundingBox), VectorConversionsMCKt.toJOML(movement)), dimensionId)) {
         val shipTransform: ShipTransform = shipObject.getTransform()
         val var10000: VsiEntityPolygonCollider = collider
         val entityBoundingBoxInShipCoordinates: AABBdc = VsiEntityPolygonCollider.createPolygonFromAABB$default(
               var10000, VectorConversionsMCKt.toJOML(entityBoxWithMovement), shipTransform.worldToShip, null, 4, null
            )
            .getEnclosingAABB(AABBd())
            if (!BugFixUtil.INSTANCE.isCollisionBoxTooBig(VectorConversionsMCKt.toMinecraft(entityBoundingBoxInShipCoordinates))) {
            val shipBlockCollisionStream: java.lang.Iterable = world.method_20812(entity, VectorConversionsMCKt.toMinecraft(entityBoundingBoxInShipCoordinates))

            for (`element$iv` in shipBlockCollisionStream) {
               (`element$iv` as class_265)
                  .method_1089(
                     { minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double ->
                        `$collidingPolygons`.add(
                           vsCore.entityPolygonCollider
                              .createPolygonFromAABB(AABBd(minX, minY, minZ, maxX, maxY, maxZ), `$shipTransform`.shipToWorld, `$shipObject`.getId())
                        )
                     }
                  )
               }
         }
      }

      return collidingPolygons
   }

   @JvmOverloads
   @JvmStatic
   fun markShipAsRecentlySpawned(shipId: Long, currentTick: Long) {
      markShipAsRecentlySpawned$default(shipId, currentTick, 0L, 4, null)
   }

   private class RoughAABBEntry {
      public final var gameTime: Long = java.lang.Long.MIN_VALUE
      public final val aabb: AABBd = AABBd()
   }

   private class WorldFreezeEntry(dimensionId: String, aabb: AABBd, deadlineGameTime: Long) {
      public final val dimensionId: String
      public final val aabb: AABBd
      public final val deadlineGameTime: Long

      init {
         this.dimensionId = dimensionId
         this.aabb = aabb
         this.deadlineGameTime = deadlineGameTime
      }
   }
}
