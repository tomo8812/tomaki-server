@file:JvmName(name = "ValkyrienSkies")
@file:SourceDebugExtension(["SMAP\nValkyrienSkies.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ValkyrienSkies.kt\norg/valkyrienskies/mod/api/ValkyrienSkies\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,600:1\n300#1:610\n301#1,4:612\n418#1,4:616\n306#1:620\n300#1:621\n301#1,4:623\n418#1,4:627\n306#1:631\n284#1,2:632\n300#1:634\n301#1,4:636\n418#1,4:640\n306#1:644\n286#1:645\n284#1,2:646\n300#1:648\n301#1,4:650\n418#1,4:654\n306#1:658\n286#1:659\n300#1:660\n301#1,4:662\n418#1,4:666\n306#1:670\n418#1,4:671\n418#1,4:675\n418#1,4:679\n418#1,4:683\n1#2:601\n1#2:611\n1#2:622\n1#2:635\n1#2:649\n1#2:661\n1557#3:602\n1628#3,3:603\n1557#3:606\n1628#3,3:607\n*S KotlinDebug\n*F\n+ 1 ValkyrienSkies.kt\norg/valkyrienskies/mod/api/ValkyrienSkies\n*L\n248#1:610\n248#1:612,4\n248#1:616,4\n248#1:620\n251#1:621\n251#1:623,4\n251#1:627,4\n251#1:631\n266#1:632,2\n266#1:634\n266#1:636,4\n266#1:640,4\n266#1:644\n266#1:645\n269#1:646,2\n269#1:648\n269#1:650,4\n269#1:654,4\n269#1:658\n269#1:659\n285#1:660\n285#1:662,4\n285#1:666,4\n285#1:670\n304#1:671,4\n333#1:675,4\n338#1:679,4\n560#1:683,4\n248#1:611\n251#1:622\n266#1:635\n269#1:649\n285#1:661\n234#1:602\n234#1:603,3\n241#1:606\n241#1:607,3\n*E\n"])

package org.valkyrienskies.mod.api

import java.util.ArrayList
import kotlin.contracts.InvocationKind
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1297
import net.minecraft.class_1923
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2374
import net.minecraft.class_238
import net.minecraft.class_2382
import net.minecraft.class_243
import net.minecraft.class_4587
import net.minecraft.class_4587.class_4665
import org.joml.Matrix4dc
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Quaterniondc
import org.joml.Quaternionf
import org.joml.Quaternionfc
import org.joml.Vector2i
import org.joml.Vector2ic
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector3ic
import org.joml.Vector4f
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.util.functions.DoubleTernaryConsumer
import org.valkyrienskies.core.api.world.ClientShipWorld
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.mod.common.config.VSGameConfig

public final val vsApi: VsApi by LazyKt.lazy({ 
   try {
      val var10000: Any = Class.forName("org.valkyrienskies.mod.common.ValkyrienSkiesMod").getDeclaredMethod("getApi").invoke(null)
      var10000 as VsApi
   } catch (var3: Exception) {
      throw IllegalStateException("Failed initialize the Valkyrien Skies API. Suggestion: Ensure that you have Valkyrien Skies installed.", var3)
   }
})
   public final get() {
      return vsApi$delegate.getValue() as VsApi
   }


public fun isConnectivityEnabled(isClient: Boolean): Boolean {
   return if (isClient) VSGameConfig.CLIENT.Connectivity.enableClientConnectivity else VSCoreConfig.SERVER.sp.enableConnectivity
}

public final val dimensionId: String
   public final get() {
      return vsApi.getDimensionId(`$this$dimensionId`)
   }


public final val dimensionId: String?
   public final get() {
      return if (`$this$dimensionId` != null) dimensionId else null
   }


public final val shipWorld: ServerShipWorld?
   public final get() {
      return vsApi.getServerShipWorld(`$this$shipWorld`)
   }


public final val shipWorld: ClientShipWorld?
   public final get() {
      return vsApi.getClientShipWorld(`$this$shipWorld`)
   }


public final val shipWorld: ShipWorld?
   public final get() {
      return vsApi.getShipWorld(`$this$shipWorld`)
   }


public fun class_1937?.getShipById(id: Long): Ship? {
   val var10000: ShipWorld = shipWorld
   if (var10000 != null) {
      val var3: QueryableShipData = var10000.allShips
      if (var3 != null) {
         return var3.getById(id)
      }
   }

   return null
}

public fun class_1937?.isBlockInShipyard(blockX: Int, blockY: Int, blockZ: Int): Boolean {
   return isChunkInShipyard(`$this$isBlockInShipyard`, blockX shr 4, blockZ shr 4)
}

public fun class_1937?.isBlockInShipyard(pos: class_2338): Boolean {
   return isBlockInShipyard(`$this$isBlockInShipyard`, pos.method_10263(), pos.method_10264(), pos.method_10260())
}

public fun class_1937?.isChunkInShipyard(chunkX: Int, chunkZ: Int): Boolean {
   return vsApi.isChunkInShipyard(`$this$isChunkInShipyard`, chunkX, chunkZ)
}

public fun class_1937?.getShipManagingChunk(chunkX: Int, chunkZ: Int): Ship? {
   return vsApi.getShipManagingChunk(`$this$getShipManagingChunk`, chunkX, chunkZ)
}

public fun class_1937?.getShipManagingChunk(pos: class_1923?): Ship? {
   return if (pos != null) getShipManagingChunk(`$this$getShipManagingChunk`, pos.field_9181, pos.field_9180) else null
}

public fun class_1937?.getShipManagingBlock(x: Int, y: Int, z: Int): Ship? {
   return getShipManagingChunk(`$this$getShipManagingBlock`, x shr 4, z shr 4)
}

public fun class_1937?.getShipManagingBlock(pos: class_2338?): Ship? {
   return if (pos != null) getShipManagingBlock(`$this$getShipManagingBlock`, pos.method_10263(), pos.method_10264(), pos.method_10260()) else null
}

public fun class_1937?.getShipManagingBlock(x: Double, y: Double, z: Double): Ship? {
   return getShipManagingBlock(`$this$getShipManagingBlock`, (int)x, (int)y, (int)z)
}

public fun class_1937?.getShipManagingBlock(v: Vector3dc?): Ship? {
   return if (v != null) getShipManagingBlock(`$this$getShipManagingBlock`, v.x(), v.y(), v.z()) else null
}

public fun class_1937?.getShipManagingBlock(v: class_2374?): Ship? {
   return if (v != null) getShipManagingBlock(`$this$getShipManagingBlock`, v.method_10216(), v.method_10214(), v.method_10215()) else null
}

public fun class_1297?.getShipManagingEntity(): Ship? {
   if (`$this$getShipManagingEntity` != null) {
      val var10000: class_1937 = `$this$getShipManagingEntity`.method_37908()
      if (var10000 != null) {
         return getShipManagingBlock(var10000, `$this$getShipManagingEntity`.method_19538() as class_2374)
      }
   }

   return null
}

public fun class_1937?.toWorld(aabb: AABBdc, dest: AABBd): AABBd {
   var var10000: Ship = getShipManagingBlock(`$this$toWorld`, aabb.minX(), aabb.minY(), aabb.minZ())
   if (var10000 == null) {
      val var9: AABBd = dest.set(aabb)
      return var9
   } else {
      var10000 = getShipManagingBlock(`$this$toWorld`, aabb.maxX(), aabb.maxY(), aabb.maxZ())
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

public fun class_1937?.toWorld(aabb: AABBd): AABBd {
   return toWorld(`$this$toWorld`, aabb, aabb)
}

public fun class_1937?.toWorld(aabb: class_238): class_238 {
   return toMinecraft(toWorld(`$this$toWorld`, toJOML(aabb)))
}

public fun class_1937?.positionToWorld(pos: class_243): class_243 {
   return positionToWorld(getShipManagingBlock(`$this$positionToWorld`, pos as class_2374), pos)
}

public fun class_1937?.positionToWorld(pos: Vector3d): Vector3d {
   return positionToWorld(`$this$positionToWorld`, pos, pos)
}

public fun class_1937?.positionToWorld(pos: Vector3dc, dest: Vector3d): Vector3d {
   return positionToWorld(getShipManagingBlock(`$this$positionToWorld`, pos), pos, dest)
}

public fun Ship?.positionToWorld(pos: class_243): class_243 {
   return positionToWorld(if (`$this$positionToWorld` != null) `$this$positionToWorld`.transform else null, pos)
}

public fun Ship?.positionToWorld(pos: Vector3d): Vector3d {
   return positionToWorld(`$this$positionToWorld`, pos, pos)
}

public fun Ship?.positionToWorld(pos: Vector3dc, dest: Vector3d): Vector3d {
   return positionToWorld(if (`$this$positionToWorld` != null) `$this$positionToWorld`.transform else null, pos, dest)
}

public fun Ship?.positionToShip(pos: class_243): class_243 {
   return positionToShip(if (`$this$positionToShip` != null) `$this$positionToShip`.transform else null, pos)
}

public fun Ship?.positionToShip(pos: Vector3d): Vector3d {
   return positionToShip(`$this$positionToShip`, pos, pos)
}

public fun Ship?.positionToShip(pos: Vector3dc, dest: Vector3d): Vector3d {
   return positionToShip(if (`$this$positionToShip` != null) `$this$positionToShip`.transform else null, pos, dest)
}

public fun ShipTransform?.positionToWorld(pos: class_243): class_243 {
   if (`$this$positionToWorld` != null) {
      val var10000: Matrix4dc = `$this$positionToWorld`.shipToWorld
      if (var10000 != null) {
         val var2: class_243 = transformPosition(var10000, pos as class_2374)
         if (var2 != null) {
            return var2
         }
      }
   }

   return pos
}

public fun ShipTransform?.positionToWorld(pos: Vector3d): Vector3d {
   return positionToWorld(`$this$positionToWorld`, pos, pos)
}

public fun ShipTransform?.positionToWorld(pos: Vector3dc, dest: Vector3d): Vector3d {
   if (`$this$positionToWorld` != null) {
      val var10000: Matrix4dc = `$this$positionToWorld`.shipToWorld
      if (var10000 != null) {
         val var3: Vector3d = var10000.transformPosition(pos, dest)
         if (var3 != null) {
            return var3
         }
      }
   }

   val var4: Vector3d = dest.set(pos)
   return var4
}

public fun ShipTransform?.positionToShip(pos: class_243): class_243 {
   if (`$this$positionToShip` != null) {
      val var10000: Matrix4dc = `$this$positionToShip`.worldToShip
      if (var10000 != null) {
         val var2: class_243 = transformPosition(var10000, pos as class_2374)
         if (var2 != null) {
            return var2
         }
      }
   }

   return pos
}

public fun ShipTransform?.positionToShip(pos: Vector3d): Vector3d {
   return positionToShip(`$this$positionToShip`, pos, pos)
}

public fun ShipTransform?.positionToShip(pos: Vector3dc, dest: Vector3d): Vector3d {
   if (`$this$positionToShip` != null) {
      val var10000: Matrix4dc = `$this$positionToShip`.worldToShip
      if (var10000 != null) {
         val var3: Vector3d = var10000.transformPosition(pos, dest)
         if (var3 != null) {
            return var3
         }
      }
   }

   val var4: Vector3d = dest.set(pos)
   return var4
}

public fun class_1937?.getShipsIntersecting(x: Double, y: Double, z: Double): Iterable<Ship> {
   return vsApi.getShipsIntersecting(`$this$getShipsIntersecting`, x, y, z)
}

public fun class_1937?.getShipsIntersecting(aabb: AABBdc?): Iterable<Ship> {
   return vsApi.getShipsIntersecting(`$this$getShipsIntersecting`, aabb)
}

public fun class_1937?.positionToNearbyShips(x: Double, y: Double, z: Double): Iterable<Vector3d> {
   val `$this$map$iv`: java.lang.Iterable = getShipsIntersecting(`$this$positionToNearbyShips`, x, y, z)
   val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10))

   for (`item$iv$iv` in `$this$map$iv`) {
      `destination$iv$iv`.add(positionToShip(`item$iv$iv` as Ship, Vector3d(x, y, z)))
   }

   return `destination$iv$iv`
}

public fun class_1937?.positionToNearbyShips(x: Double, y: Double, z: Double, aabb: AABBdc?): Iterable<Vector3d> {
   if (`$this$positionToNearbyShips` != null && aabb != null) {
      val `$this$map$iv`: java.lang.Iterable = getShipsIntersecting(`$this$positionToNearbyShips`, aabb)
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10))

      for (`item$iv$iv` in `$this$map$iv`) {
         `destination$iv$iv`.add(positionToShip(`item$iv$iv` as Ship, Vector3d(x, y, z)))
      }

      return `destination$iv$iv`
   } else {
      return CollectionsKt.emptyList()
   }
}

public fun class_1937?.positionToNearbyShips(x: Double, y: Double, z: Double, aabbRadius: Double): Iterable<Vector3d> {
   return positionToNearbyShips(`$this$positionToNearbyShips`, x, y, z, newAABBWithRadius(x, y, z, aabbRadius))
}

public fun class_1937?.positionToNearbyShips(x: Double, y: Double, z: Double, cb: DoubleTernaryConsumer) {
   for (`ship$iv` in getShipsIntersecting(`$this$positionToNearbyShips`, x, y, z)) {
      val `$this$transformPositionInline$iv$iv`: Matrix4dc = `ship$iv`.worldToShip
      cb.accept(
         `$this$transformPositionInline$iv$iv`.m00() * x
            + `$this$transformPositionInline$iv$iv`.m10() * y
            + `$this$transformPositionInline$iv$iv`.m20() * z
            + `$this$transformPositionInline$iv$iv`.m30(),
         `$this$transformPositionInline$iv$iv`.m01() * x
            + `$this$transformPositionInline$iv$iv`.m11() * y
            + `$this$transformPositionInline$iv$iv`.m21() * z
            + `$this$transformPositionInline$iv$iv`.m31(),
         `$this$transformPositionInline$iv$iv`.m02() * x
            + `$this$transformPositionInline$iv$iv`.m12() * y
            + `$this$transformPositionInline$iv$iv`.m22() * z
            + `$this$transformPositionInline$iv$iv`.m32()
      )
   }
}

public fun class_1937?.positionToNearbyShips(x: Double, y: Double, z: Double, aabb: AABBdc?, cb: DoubleTernaryConsumer) {
   var var10000: java.lang.Iterable
   run label25@{
      if (aabb != null) {
         var10000 = getShipsIntersecting(`$this$positionToNearbyShips`, aabb)
         if (var10000 != null) {
            return@label25
         }
      }

      var10000 = getShipsIntersecting(`$this$positionToNearbyShips`, x, y, z)
   }

   for (`ship$iv` in var10000) {
      val var23: Matrix4dc = `ship$iv`.worldToShip
      cb.accept(
         var23.m00() * x + var23.m10() * y + var23.m20() * z + var23.m30(),
         var23.m01() * x + var23.m11() * y + var23.m21() * z + var23.m31(),
         var23.m02() * x + var23.m12() * y + var23.m22() * z + var23.m32()
      )
   }
}

public fun class_1937?.positionToNearbyShips(x: Double, y: Double, z: Double, aabbRadius: Double, cb: DoubleTernaryConsumer) {
   positionToNearbyShips(`$this$positionToNearbyShips`, x, y, z, newAABBWithRadius(x, y, z, aabbRadius), cb)
}

public fun class_1937?.positionToNearbyShipsAndWorld(x: Double, y: Double, z: Double): Iterable<Vector3d> {
   return CollectionsKt.plus(CollectionsKt.listOf(Vector3d(x, y, z)), positionToNearbyShips(`$this$positionToNearbyShipsAndWorld`, x, y, z))
}

public fun class_1937?.positionToNearbyShipsAndWorld(x: Double, y: Double, z: Double, aabb: AABBdc?): Iterable<Vector3d> {
   return CollectionsKt.plus(CollectionsKt.listOf(Vector3d(x, y, z)), positionToNearbyShips(`$this$positionToNearbyShipsAndWorld`, x, y, z, aabb))
}

public fun class_1937?.positionToNearbyShipsAndWorld(x: Double, y: Double, z: Double, aabbRadius: Double): Iterable<Vector3d> {
   return positionToNearbyShipsAndWorld(`$this$positionToNearbyShipsAndWorld`, x, y, z, newAABBWithRadius(x, y, z, aabbRadius))
}

public fun class_1937?.positionToNearbyShipsAndWorld(x: Double, y: Double, z: Double, cb: DoubleTernaryConsumer) {
   cb.accept(x, y, z)

   for (`ship$iv$iv` in getShipsIntersecting(`$this$positionToNearbyShipsAndWorld`, x, y, z)) {
      val `$this$transformPositionInline$iv$iv$iv`: Matrix4dc = `ship$iv$iv`.worldToShip
      cb.accept(
         `$this$transformPositionInline$iv$iv$iv`.m00() * x
            + `$this$transformPositionInline$iv$iv$iv`.m10() * y
            + `$this$transformPositionInline$iv$iv$iv`.m20() * z
            + `$this$transformPositionInline$iv$iv$iv`.m30(),
         `$this$transformPositionInline$iv$iv$iv`.m01() * x
            + `$this$transformPositionInline$iv$iv$iv`.m11() * y
            + `$this$transformPositionInline$iv$iv$iv`.m21() * z
            + `$this$transformPositionInline$iv$iv$iv`.m31(),
         `$this$transformPositionInline$iv$iv$iv`.m02() * x
            + `$this$transformPositionInline$iv$iv$iv`.m12() * y
            + `$this$transformPositionInline$iv$iv$iv`.m22() * z
            + `$this$transformPositionInline$iv$iv$iv`.m32()
      )
   }
}

public fun class_1937?.positionToNearbyShipsAndWorld(x: Double, y: Double, z: Double, aabb: AABBdc?, cb: DoubleTernaryConsumer) {
   var var10000: java.lang.Iterable
   run label25@{
      cb.accept(x, y, z)
      if (aabb != null) {
         var10000 = getShipsIntersecting(`$this$positionToNearbyShipsAndWorld`, aabb)
         if (var10000 != null) {
            return@label25
         }
      }

      var10000 = getShipsIntersecting(`$this$positionToNearbyShipsAndWorld`, x, y, z)
   }

   for (`ship$iv$iv` in var10000) {
      val var30: Matrix4dc = `ship$iv$iv`.worldToShip
      cb.accept(
         var30.m00() * x + var30.m10() * y + var30.m20() * z + var30.m30(),
         var30.m01() * x + var30.m11() * y + var30.m21() * z + var30.m31(),
         var30.m02() * x + var30.m12() * y + var30.m22() * z + var30.m32()
      )
   }
}

public fun class_1937?.positionToNearbyShipsAndWorld(x: Double, y: Double, z: Double, aabbRadius: Double, cb: DoubleTernaryConsumer) {
   positionToNearbyShipsAndWorld(`$this$positionToNearbyShipsAndWorld`, x, y, z, newAABBWithRadius(x, y, z, aabbRadius), cb)
}

private fun newAABBWithRadius(x: Double, y: Double, z: Double, r: Double): AABBd {
   return AABBd(x - r, y - r, z - r, x + r, y + r, z + r)
}

private inline fun class_1937?.positionToNearbyShipsAndWorld(x: Double, y: Double, z: Double, aabb: AABBdc?, cb: (Double, Double, Double) -> Unit) {
   var var10000: java.lang.Iterable
   run label25@{
      cb(x, y, z)
      if (aabb != null) {
         var10000 = getShipsIntersecting(`$this$positionToNearbyShipsAndWorld`, aabb)
         if (var10000 != null) {
            return@label25
         }
      }

      var10000 = getShipsIntersecting(`$this$positionToNearbyShipsAndWorld`, x, y, z)
   }

   for (`ship$iv` in var10000) {
      val var17: Matrix4dc = `ship$iv`.worldToShip
      cb(
         var17.m00() * x + var17.m10() * y + var17.m20() * z + var17.m30(),
         var17.m01() * x + var17.m11() * y + var17.m21() * z + var17.m31(),
         var17.m02() * x + var17.m12() * y + var17.m22() * z + var17.m32()
      )
   }
}

private inline fun class_1937?.positionToNearbyShips(x: Double, y: Double, z: Double, aabb: AABBdc?, cb: (Double, Double, Double) -> Unit) {
   var var10000: java.lang.Iterable
   run label25@{
      if (aabb != null) {
         var10000 = getShipsIntersecting(`$this$positionToNearbyShips`, aabb)
         if (var10000 != null) {
            return@label25
         }
      }

      var10000 = getShipsIntersecting(`$this$positionToNearbyShips`, x, y, z)
   }

   for (ship in var10000) {
      val var15: Matrix4dc = ship.worldToShip
      cb(
         var15.m00() * x + var15.m10() * y + var15.m20() * z + var15.m30(),
         var15.m01() * x + var15.m11() * y + var15.m21() * z + var15.m31(),
         var15.m02() * x + var15.m12() * y + var15.m22() * z + var15.m32()
      )
   }
}

public fun class_1937?.distance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
   return Math.sqrt(distanceSquared(`$this$distance`, x1, y1, z1, x2, y2, z2))
}

public fun class_1937?.distance(v1: class_2374, v2: class_2374): Double {
   return Math.sqrt(distanceSquared(`$this$distance`, v1, v2))
}

public fun class_1937?.distanceSquared(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
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
   val ship1: Ship = getShipManagingBlock(`$this$distanceSquared`, x1, y1, z1)
   val ship2: Ship = getShipManagingBlock(`$this$distanceSquared`, x2, y2, z2)
   if (!(ship1 == ship2)) {
      if (ship1 != null) {
         val var10000: Matrix4dc = ship1.shipToWorld
         if (var10000 != null) {
            val var50: Double = var10000.m00() * x1 + var10000.m10() * y1 + var10000.m20() * z1 + var10000.m30()
            val z: Double = var10000.m02() * x1 + var10000.m12() * y1 + var10000.m22() * z1 + var10000.m32()
            val y: Double = var10000.m01() * x1 + var10000.m11() * y1 + var10000.m21() * z1 + var10000.m31()
            inWorldX1 = var50
            inWorldY1 = y
            inWorldZ1 = z
         }
      }

      if (ship2 != null) {
         val var51: Matrix4dc = ship2.shipToWorld
         if (var51 != null) {
            val var52: Double = var51.m00() * x2 + var51.m10() * y2 + var51.m20() * z2 + var51.m30()
            val var40: Double = var51.m02() * x2 + var51.m12() * y2 + var51.m22() * z2 + var51.m32()
            val var41: Double = var51.m01() * x2 + var51.m11() * y2 + var51.m21() * z2 + var51.m31()
            inWorldX2 = var52
            inWorldY2 = var41
            inWorldZ2 = var40
         }
      }
   }

   return (inWorldX2 - inWorldX1) * (inWorldX2 - inWorldX1)
      + (inWorldY2 - inWorldY1) * (inWorldY2 - inWorldY1)
      + (inWorldZ2 - inWorldZ1) * (inWorldZ2 - inWorldZ1)
   }

public fun class_1937?.distanceSquared(v1: class_2374, v2: class_2374): Double {
   return distanceSquared(
      `$this$distanceSquared`, v1.method_10216(), v1.method_10214(), v1.method_10215(), v2.method_10216(), v2.method_10214(), v2.method_10215()
   )
}

public fun class_1937?.distanceSquared(v1: class_2382, v2: class_2382): Double {
   return distanceSquared(
      `$this$distanceSquared`,
      (double)v1.method_10263(),
      (double)v1.method_10264(),
      (double)v1.method_10260(),
      (double)v2.method_10263(),
      (double)v2.method_10264(),
      (double)v2.method_10260()
   )
}

public fun class_1937?.distanceToCenterSquared(v1: class_2382, x2: Double, y2: Double, z2: Double): Double {
   return distanceSquared(
      `$this$distanceToCenterSquared`, (double)v1.method_10263() + 0.5, (double)v1.method_10264() + 0.5, (double)v1.method_10260() + 0.5, x2, y2, z2
   )
}

public fun class_1937?.distanceToCenterSquared(v1: class_2382, v2: class_2374): Double {
   return distanceToCenterSquared(`$this$distanceToCenterSquared`, v1, v2.method_10216(), v2.method_10214(), v2.method_10215())
}

public fun class_1937?.closerThan(v1: class_2374, v2: class_2374, distance: Double): Boolean {
   return distanceSquared(`$this$closerThan`, v1, v2) < squared(distance)
}

public fun class_1937?.closerThan(v1: class_2382, v2: class_2382, distance: Double): Boolean {
   return distanceSquared(`$this$closerThan`, v1, v2) < squared(distance)
}

public fun class_1937?.closerToCenterThan(v1: class_2382, x2: Double, y2: Double, z2: Double, distance: Double): Boolean {
   return distanceToCenterSquared(`$this$closerToCenterThan`, v1, x2, y2, z2) < squared(distance)
}

public fun class_1937?.closerToCenterThan(v1: class_2382, v2: class_2374, distance: Double): Boolean {
   return distanceToCenterSquared(`$this$closerToCenterThan`, v1, v2) < squared(distance)
}

private fun Double.squared(): Double {
   return `$this$squared` * `$this$squared`
}

private inline fun <T> Matrix4dc.transformPositionInline(x: Double, y: Double, z: Double, transformed: (Double, Double, Double) -> Any): Any {
   contract {
      callsInPlace(transformed, InvocationKind.EXACTLY_ONCE)
   }

   return (T)transformed(
      `$this$transformPositionInline`.m00() * x
         + `$this$transformPositionInline`.m10() * y
         + `$this$transformPositionInline`.m20() * z
         + `$this$transformPositionInline`.m30(),
      `$this$transformPositionInline`.m01() * x
         + `$this$transformPositionInline`.m11() * y
         + `$this$transformPositionInline`.m21() * z
         + `$this$transformPositionInline`.m31(),
      `$this$transformPositionInline`.m02() * x
         + `$this$transformPositionInline`.m12() * y
         + `$this$transformPositionInline`.m22() * z
         + `$this$transformPositionInline`.m32()
   )
}

public fun Vector3i.set(v: class_2382): Vector3i {
   `$this$set`.x = v.method_10263()
   `$this$set`.y = v.method_10264()
   `$this$set`.z = v.method_10260()
   return `$this$set`
}

public fun Vector3d.set(v: class_2382): Vector3d {
   `$this$set`.x = v.method_10263()
   `$this$set`.y = v.method_10264()
   `$this$set`.z = v.method_10260()
   return `$this$set`
}

public fun Vector3f.set(v: class_2382): Vector3f {
   `$this$set`.x = v.method_10263()
   `$this$set`.y = v.method_10264()
   `$this$set`.z = v.method_10260()
   return `$this$set`
}

public fun Vector3d.set(v: class_2374): Vector3d {
   `$this$set`.x = v.method_10216()
   `$this$set`.y = v.method_10214()
   `$this$set`.z = v.method_10215()
   return `$this$set`
}

public fun AABBd.set(v: class_238): AABBd {
   `$this$set`.minX = v.field_1323
   `$this$set`.minY = v.field_1322
   `$this$set`.minZ = v.field_1321
   `$this$set`.maxX = v.field_1320
   `$this$set`.maxY = v.field_1325
   `$this$set`.maxZ = v.field_1324
   return `$this$set`
}

public fun Vector3ic.toBlockPos(): class_2338 {
   return class_2338(`$this$toBlockPos`.x(), `$this$toBlockPos`.y(), `$this$toBlockPos`.z())
}

public fun Vector3dc.toMinecraft(): class_243 {
   return class_243(`$this$toMinecraft`.x(), `$this$toMinecraft`.y(), `$this$toMinecraft`.z())
}

public fun AABBdc.toMinecraft(): class_238 {
   return class_238(
      `$this$toMinecraft`.minX(),
      `$this$toMinecraft`.minY(),
      `$this$toMinecraft`.minZ(),
      `$this$toMinecraft`.maxX(),
      `$this$toMinecraft`.maxY(),
      `$this$toMinecraft`.maxZ()
   )
}

public fun class_238.toJOML(): AABBd {
   return set(AABBd(), `$this$toJOML`)
}

public fun Vector2ic.toChunkPos(): class_1923 {
   return class_1923(`$this$toChunkPos`.x(), `$this$toChunkPos`.y())
}

public fun class_1923.toJOML(): Vector2i {
   return set(Vector2i(), `$this$toJOML`)
}

public fun class_243.toJOML(): Vector3d {
   return set(Vector3d(), `$this$toJOML`)
}

public fun Vector3d.set(v: class_243): Vector3d {
   `$this$set`.x = v.field_1352
   `$this$set`.y = v.field_1351
   `$this$set`.z = v.field_1350
   return `$this$set`
}

public fun Vector2i.set(pos: class_1923): Vector2i {
   `$this$set`.x = pos.field_9181
   `$this$set`.y = pos.field_9180
   return `$this$set`
}

@JvmOverloads
public fun Matrix4dc.transformDirection(v: class_2382, dest: Vector3d = Vector3d()): Vector3d {
   val var10000: Vector3d = `$this$transformDirection`.transformDirection(
      dest.set((double)v.method_10263(), (double)v.method_10264(), (double)v.method_10260())
   )
   return var10000
}

@JvmOverloads
public fun Matrix4dc.transformDirection(dir: class_2350, dest: Vector3d = Vector3d()): Vector3d {
   val var10001: class_2382 = dir.method_10163()
   return transformDirection(`$this$transformDirection`, var10001, dest)
}

public fun Matrix4dc.transform(v: Vector4f): Vector4f {
   v.set(
      (float)(
         `$this$transform`.m00() * (double)v.x()
            + `$this$transform`.m01() * (double)v.y()
            + `$this$transform`.m02() * (double)v.z()
            + `$this$transform`.m03() * (double)v.w()
      ),
      (float)(
         `$this$transform`.m10() * (double)v.x()
            + `$this$transform`.m11() * (double)v.y()
            + `$this$transform`.m12() * (double)v.z()
            + `$this$transform`.m13() * (double)v.w()
      ),
      (float)(
         `$this$transform`.m20() * (double)v.x()
            + `$this$transform`.m21() * (double)v.y()
            + `$this$transform`.m22() * (double)v.z()
            + `$this$transform`.m23() * (double)v.w()
      ),
      (float)(
         `$this$transform`.m30() * (double)v.x()
            + `$this$transform`.m31() * (double)v.y()
            + `$this$transform`.m32() * (double)v.z()
            + `$this$transform`.m33() * (double)v.w()
      )
   )
   return v
}

public fun Matrix4dc.transformPosition(v: class_2374): class_243 {
   val `x$iv`: Double = v.method_10216()
   val `y$iv`: Double = v.method_10214()
   val `z$iv`: Double = v.method_10215()
   return class_243(
      `$this$transformPosition`.m00() * `x$iv`
         + `$this$transformPosition`.m10() * `y$iv`
         + `$this$transformPosition`.m20() * `z$iv`
         + `$this$transformPosition`.m30(),
      `$this$transformPosition`.m01() * `x$iv`
         + `$this$transformPosition`.m11() * `y$iv`
         + `$this$transformPosition`.m21() * `z$iv`
         + `$this$transformPosition`.m31(),
      `$this$transformPosition`.m02() * `x$iv`
         + `$this$transformPosition`.m12() * `y$iv`
         + `$this$transformPosition`.m22() * `z$iv`
         + `$this$transformPosition`.m32()
   )
}

public fun class_4587.multiply(modelTransform: Matrix4dc): class_4587 {
   val last: class_4665 = `$this$multiply`.method_23760()
   val var10000: Matrix4f = Matrix4f(last.method_23761()).mul(toFloat(modelTransform))
   last.method_23761().set(var10000)
   return `$this$multiply`
}

public fun class_2382.toJOML(): Vector3i {
   return set(Vector3i(), `$this$toJOML`)
}

public fun class_2382.toJOMLd(): Vector3d {
   return set(Vector3d(), `$this$toJOMLd`)
}

public fun class_2382.toJOMLf(): Vector3f {
   return set(Vector3f(), `$this$toJOMLf`)
}

public fun class_2374.toJOML(): Vector3d {
   return set(Vector3d(), `$this$toJOML`)
}

public fun Quaterniondc.toFloat(): Quaternionfc {
   return Quaternionf(`$this$toFloat`.x(), `$this$toFloat`.y(), `$this$toFloat`.z(), `$this$toFloat`.w())
}

public fun Matrix4dc.toFloat(): Matrix4fc {
   val var10000: Matrix4f = Matrix4f().set(`$this$toFloat`)
   return var10000
}

@JvmOverloads
fun Matrix4dc.transformDirection(v: class_2382): Vector3d {
   transformDirection$default(`$this$transformDirection`, v, null, 2, null)
}

@JvmOverloads
fun Matrix4dc.transformDirection(dir: class_2350): Vector3d {
   transformDirection$default(`$this$transformDirection`, dir, null, 2, null)
}
