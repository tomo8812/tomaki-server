package org.valkyrienskies.mod.common.world

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
import it.unimi.dsi.fastutil.longs.LongIterator
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentHashMap.KeySetView
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KProperty
import net.minecraft.class_1923
import net.minecraft.class_3218
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.Logger
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.util.ShipObserverPlayer
import org.valkyrienskies.mod.common.util.ShipSettings
import org.valkyrienskies.mod.util.ClassLogger
import org.valkyrienskies.mod.util.LoggingKt

@SourceDebugExtension(["SMAP\nShipActivationManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipActivationManager.kt\norg/valkyrienskies/mod/common/world/ShipActivationManager\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,395:1\n381#2,7:396\n381#2,7:406\n381#2,7:413\n774#3:403\n865#3,2:404\n*S KotlinDebug\n*F\n+ 1 ShipActivationManager.kt\norg/valkyrienskies/mod/common/world/ShipActivationManager\n*L\n167#1:396,7\n261#1:406,7\n319#1:413,7\n220#1:403\n220#1:404,2\n*E\n"])
public object ShipActivationManager {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property1(
         PropertyReference1Impl(ShipActivationManager.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
      )
   );

   private final val logger: Logger by LoggingKt.logger().provideDelegate_j8jxBIw/* $VF was: provideDelegate-j8jxBIw */(INSTANCE, $$delegatedProperties[0])
      private final get() {
         return ClassLogger.getValue_impl/* $VF was: getValue-impl */(logger$delegate, this, $$delegatedProperties[0])
      }


   private const val OCCUPY_MARGIN: Double = 2.0
   private final val controlledHeartbeat: MutableSet<Long>
   private final val forced: HashMap<Long, org.valkyrienskies.mod.common.world.ShipActivationManager.ForcedEntry> = HashMap()
   private final val worldForceRefs: HashMap<String, Long2IntOpenHashMap> = HashMap()
   private final val keepActiveCache: HashMap<Long, org.valkyrienskies.mod.common.world.ShipActivationManager.ObserverPos> = HashMap()
   private final var activeShipIdsSnapshot: Set<Long> = SetsKt.emptySet()
   private final var freezeForced: Boolean
   private final var aboardMemoTick: Long = java.lang.Long.MIN_VALUE
   private final val aboardMemo: HashMap<Long, Boolean> = HashMap()

   @JvmStatic
   public final val activeShipIds: Set<Long>
      public final get() {
         return activeShipIdsSnapshot
      }


   @JvmStatic
   public fun markControlled(shipId: Long) {
      controlledHeartbeat.add(shipId)
   }

   @JvmStatic
   public fun tick(shipWorld: VsiServerShipWorld, server: MinecraftServer) {
      var var10000: java.util.Set
      if (controlledHeartbeat.isEmpty()) {
         var10000 = SetsKt.emptySet()
      } else {
         val fz: HashSet = HashSet<>(controlledHeartbeat)
         controlledHeartbeat.clear()
         var10000 = fz
      }

      val controlled: java.util.Set = var10000
      val var24: Double = VSCoreConfig.SERVER.physics.shipLoadFreezeSeconds
      if (var24 != 0.0) {
         if (!freezeForced) {
            INSTANCE.logger.info("VS2: forcing shipLoadFreezeSeconds 0.0 (was $var24s) to prevent the post-load cruise clamp")
            freezeForced = true
         }

         VSCoreConfig.SERVER.physics.shipLoadFreezeSeconds = 0.0
      }

      val activeIds: HashSet = HashSet()

      for (`$this$filter$iv` in shipWorld.loadedShips) {
         val var48: ShipSettings = `$this$filter$iv`.getAttachment(ShipSettings.class)
         val id: Boolean = var48 != null && var48.keepActive
         val `$this$filterTo$iv$iv`: Boolean = `$this$filter$iv`.getAttachment(SeatedControllingPlayer.class) != null
         val wasLoggable: Boolean = controlled.contains(`$this$filter$iv`.getId())
         val var49: class_3218 = VSGameUtilsKt.getLevelFromDimensionId(server, `$this$filter$iv`.getChunkClaimDimension())
         if (var49 != null) {
            val `$i$f$filterTo`: class_3218 = var49
            if (id
               || `$this$filterTo$iv$iv`
               || wasLoggable
               || !id && !`$this$filterTo$iv$iv` && !wasLoggable && INSTANCE.playersAboardMemo(`$this$filter$iv`, var49)) {
               activeIds.add(`$this$filter$iv`.getId())
               val `element$iv$iv`: LongOpenHashSet = INSTANCE.worldChunksUnder(`$this$filter$iv`)
               val it: Boolean = !forced.containsKey(`$this$filter$iv`.getId())
               val dIter: java.util.Map = forced
               var iter: Any = `$this$filter$iv`.getId()
               var dvIter: Any = dIter.get(iter)
               val var50: Any
               if (dvIter == null) {
                  val var44: Any = ShipActivationManager.ForcedEntry(`$this$filter$iv`.getChunkClaimDimension())
                  dIter.put(iter, var44)
                  var50 = var44
               } else {
                  var50 = dvIter
               }

               val var15: ShipActivationManager.ForcedEntry = var50 as ShipActivationManager.ForcedEntry
               (var50 as ShipActivationManager.ForcedEntry).loggable = id || `$this$filterTo$iv$iv` || wasLoggable
               var var51: LongIterator = `element$iv$iv`.iterator()
               val var38: LongIterator = var51

               while (var38.hasNext()) {
                  iter = var38.nextLong()
                  if (var15.chunks.add((long)iter)) {
                     INSTANCE.forceWorldChunk(`$i$f$filterTo`, `$this$filter$iv`.getChunkClaimDimension(), (long)iter)
                  }
               }

               var51 = var15.chunks.iterator()
               val var40: LongIterator = var51

               while (var40.hasNext()) {
                  val var41: Long = var40.nextLong()
                  if (!`element$iv$iv`.contains(var41)) {
                     INSTANCE.unforceWorldChunk(`$i$f$filterTo`, var15.dimensionId, var41)
                     var40.remove()
                  }
               }

               val var42: LongOpenHashSet = LongOpenHashSet()
               `$this$filter$iv`.getActiveChunksSet().iterateChunkPos({ vcx: Int, vcz: Int ->
                  `$desiredVoxel`.add(class_1923.method_8331(vcx, vcz))
                  Unit.INSTANCE
               })
               var51 = var42.iterator()
               dvIter = var51

               while (dvIter.hasNext()) {
                  val var45: Long = dvIter.nextLong()
                  if (var15.voxelChunks.add(var45)) {
                     val cp: class_1923 = class_1923(var45)
                     `$i$f$filterTo`.method_14178().method_17297(VSTicketType.SHIP_ACTIVE_VOXEL, cp, 0, cp)
                  }
               }

               var51 = var15.voxelChunks.iterator()
               val var46: LongIterator = var51

               while (var46.hasNext()) {
                  val reason: Long = var46.nextLong()
                  if (!var42.contains(reason)) {
                     val cp: class_1923 = class_1923(reason)
                     `$i$f$filterTo`.method_14178().method_17300(VSTicketType.SHIP_ACTIVE_VOXEL, cp, 0, cp)
                     var46.remove()
                  }
               }

               if (it && var15.loggable) {
                  INSTANCE.logger
                     .info(
                        "Keeping ship ${`$this$filter$iv`.getId()} active (${if (id)
                           "keepActive"
                           else
                           (if (`$this$filterTo$iv$iv`) "piloted" else "controlled")}) — ${var15.chunks.size()} world chunks force-ticked"
                     )
                  }
            }
         }
      }

      if (!forced.isEmpty()) {
         var10000 = forced.keySet()
         val var30: java.lang.Iterable = var10000
         val var31: java.util.Collection = ArrayList()

         for (var35 in var30) {
            if (!activeIds.contains(var35 as java.lang.Long)) {
               var31.add(var35)
            }
         }

         for (var56 in var31 as java.util.List) {
            val var29: Long = (var56 as java.lang.Number).longValue()
            val var57: ShipActivationManager.ForcedEntry = forced.get(var29)
            val var32: Boolean = var57 != null && var57.loggable
            INSTANCE.release(var29, server)
            if (var32) {
               INSTANCE.logger.info("Ship $var29 no longer kept active — released world-chunk tickets")
            }
         }
      }

      activeShipIdsSnapshot = activeIds
   }

   private fun release(id: Long, server: MinecraftServer) {
      val var10000: ShipActivationManager.ForcedEntry = forced.remove(id)
      if (var10000 != null) {
         val entry: ShipActivationManager.ForcedEntry = var10000
         val var9: class_3218 = VSGameUtilsKt.getLevelFromDimensionId(server, var10000.dimensionId)
         if (var9 != null) {
            val level: class_3218 = var9
            val var10: LongIterator = var10000.chunks.iterator()
            val cIter: LongIterator = var10

            while (cIter.hasNext()) {
               this.unforceWorldChunk(level, entry.dimensionId, cIter.nextLong())
            }

            val var11: LongIterator = entry.voxelChunks.iterator()
            val vIter: LongIterator = var11

            while (vIter.hasNext()) {
               val cp: class_1923 = class_1923(vIter.nextLong())
               level.method_14178().method_17300(VSTicketType.SHIP_ACTIVE_VOXEL, cp, 0, cp)
            }
         }
      }
   }

   @JvmStatic
   public fun clearAll(server: MinecraftServer) {
      val var10000: java.util.Set = forced.keySet()

      for (var4 in CollectionsKt.toList(var10000)) {
         INSTANCE.release((var4 as java.lang.Number).longValue(), server)
      }

      worldForceRefs.clear()
      controlledHeartbeat.clear()
      keepActiveCache.clear()
      aboardMemo.clear()
      aboardMemoTick = java.lang.Long.MIN_VALUE
   }

   private fun forceWorldChunk(level: class_3218, dim: String, packed: Long) {
      val n: java.util.Map = worldForceRefs
      val `value$iv`: Any = worldForceRefs.get(dim)
      val var10000: Any
      if (`value$iv` == null) {
         val var11: Any = Long2IntOpenHashMap()
         n.put(dim, var11)
         var10000 = var11
      } else {
         var10000 = `value$iv`
      }

      val m: Long2IntOpenHashMap = var10000 as Long2IntOpenHashMap
      val var10: Int = (var10000 as Long2IntOpenHashMap).get(packed)
      if (var10 == 0) {
         level.method_14178().method_12124(class_1923(packed), true)
      }

      m.put(packed, var10 + 1)
   }

   private fun unforceWorldChunk(level: class_3218, dim: String, packed: Long) {
      val var10000: Long2IntOpenHashMap = worldForceRefs.get(dim)
      if (var10000 != null) {
         val n: Int = var10000.get(packed)
         if (n <= 1) {
            var10000.remove(packed)
            if (var10000.isEmpty()) {
               worldForceRefs.remove(dim)
            }

            level.method_14178().method_12124(class_1923(packed), false)
         } else {
            var10000.put(packed, n - 1)
         }
      }
   }

   private fun worldChunksUnder(ship: LoadedServerShip): LongOpenHashSet {
      val aabb: AABBdc = ship.getWorldAABB()
      val minCX: Int = ((int)Math.floor(aabb.minX()) shr 4) - 1
      val maxCX: Int = ((int)Math.floor(aabb.maxX()) shr 4) + 1
      val minCZ: Int = ((int)Math.floor(aabb.minZ()) shr 4) - 1
      val maxCZ: Int = ((int)Math.floor(aabb.maxZ()) shr 4) + 1
      val out: LongOpenHashSet = LongOpenHashSet()

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var cx: Int = minCX
      while (true) {
         if (cx <= maxCX) break
         // $VF: Unable to resugar Kotlin loop from Java for loop
         var cz: Int = minCZ
         while (true) {
            if (cz <= maxCZ) break
            out.add(class_1923.method_8331(cx, cz))

            cz++
         }

         cx++
      }

      return out
   }

   private fun playersAboardMemo(ship: LoadedServerShip, level: class_3218): Boolean {
      val tick: Long = level.method_8503().method_3780()
      if (tick != aboardMemoTick) {
         aboardMemo.clear()
         aboardMemoTick = tick
      }

      val `$this$getOrPut$iv`: java.util.Map = aboardMemo
      val `key$iv`: Any = ship.getId()
      val `value$iv`: Any = `$this$getOrPut$iv`.get(`key$iv`)
      val var10000: Any
      if (`value$iv` == null) {
         val `answer$iv`: Any = INSTANCE.playersAboard(ship, level)
         `$this$getOrPut$iv`.put(`key$iv`, `answer$iv`)
         var10000 = `answer$iv`
      } else {
         var10000 = `value$iv`
      }

      return var10000 as java.lang.Boolean
   }

   private fun playersAboard(ship: LoadedServerShip, level: class_3218): Boolean {
      val players: java.util.List = level.method_18456()
      if (players.isEmpty()) {
         return false
      } else {
         val var10000: AABBdc = ship.getWorldAABB()
         if (var10000 == null) {
            return false
         } else {
            val minX: Double = var10000.minX() - 2.0
            val maxX: Double = var10000.maxX() + 2.0
            val minY: Double = var10000.minY() - 2.0
            val maxY: Double = var10000.maxY() + 2.0
            val minZ: Double = var10000.minZ() - 2.0
            val maxZ: Double = var10000.maxZ() + 2.0

            for (p in players) {
               var var19: Double = p.method_23317()
               if (minX <= var19 && var19 <= maxX) {
                  var19 = p.method_23318()
                  if (minY <= var19 && var19 <= maxY) {
                     var19 = p.method_23321()
                     if (minZ <= var19 && var19 <= maxZ) {
                        return true
                     }
                  }
               }
            }

            return false
         }
      }
   }

   @JvmStatic
   public fun activeShipObservers(shipWorld: VsiServerShipWorld, server: MinecraftServer): Set<VsiPlayer> {
      val observers: HashSet = HashSet()
      val loadedActive: HashSet = HashSet()

      for (entry in shipWorld.loadedShips) {
         val var10000: ShipSettings = entry.getAttachment(ShipSettings.class)
         val pos: Boolean = var10000 != null && var10000.keepActive
         if (!pos) {
            keepActiveCache.remove(entry.getId())
         }

         val piloted: Boolean = entry.getAttachment(SeatedControllingPlayer.class) != null
         var occupied: Boolean = false
         if (!pos && !piloted) {
            val aabb: class_3218 = VSGameUtilsKt.getLevelFromDimensionId(server, entry.getChunkClaimDimension())
            occupied = aabb != null && INSTANCE.playersAboardMemo(entry, aabb)
         }

         if (pos || piloted || occupied) {
            val var19: AABBdc = entry.getWorldAABB()
            val cx: Double = (var19.minX() + var19.maxX()) * 0.5
            val cy: Double = (var19.minY() + var19.maxY()) * 0.5
            val cz: Double = (var19.minZ() + var19.maxZ()) * 0.5
            observers.add(ShipObserverPlayer(entry.getId(), entry.getChunkClaimDimension(), cx, cy, cz, 0))
            loadedActive.add(entry.getId())
            if (pos) {
               keepActiveCache.put(entry.getId(), ShipActivationManager.ObserverPos(entry.getChunkClaimDimension(), cx, cy, cz))
            }
         }
      }

      if (!keepActiveCache.isEmpty()) {
         val var16: java.util.Iterator = keepActiveCache.entrySet().iterator()

         while (var16.hasNext()) {
            val var20: Any = var16.next()
            val var17: Entry = var20 as Entry
            if (!loadedActive.contains((var20 as Entry).getKey())) {
               val var21: VsiQueryableShipData = shipWorld.allShips
               val var10001: Any = var17.getKey()
               if (var21.getById((var10001 as java.lang.Number).longValue()) == null) {
                  var16.remove()
               } else {
                  val var22: Any = var17.getValue()
                  val var18: ShipActivationManager.ObserverPos = var22 as ShipActivationManager.ObserverPos
                  val var10003: Any = var17.getKey()
                  observers.add(ShipObserverPlayer((var10003 as java.lang.Number).longValue(), var18.dimensionId, var18.x, var18.y, var18.z, 0))
               }
            }
         }
      }

      return observers
   }

   @JvmStatic
   fun {
      val var10000: KeySetView = ConcurrentHashMap.newKeySet()
      controlledHeartbeat = var10000
   }

   private class ForcedEntry(dimensionId: String) {
      public final val dimensionId: String
      public final val chunks: LongOpenHashSet
      public final val voxelChunks: LongOpenHashSet
      public final var loggable: Boolean

      init {
         this.dimensionId = dimensionId
         this.chunks = LongOpenHashSet()
         this.voxelChunks = LongOpenHashSet()
      }
   }

   private class ObserverPos(dimensionId: String, x: Double, y: Double, z: Double) {
      public final val dimensionId: String
      public final val x: Double
      public final val y: Double
      public final val z: Double

      init {
         this.dimensionId = dimensionId
         this.x = x
         this.y = y
         this.z = z
      }
   }
}
