package org.valkyrienskies.mod.common.assembly

import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.function.BiFunction
import java.util.function.Function
import kotlin.enums.EnumEntries
import kotlin.enums.EnumEntriesKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.markers.KMappedMarker
import net.minecraft.class_1923
import net.minecraft.class_1937
import net.minecraft.class_2246
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2487
import net.minecraft.class_2586
import net.minecraft.class_2680
import net.minecraft.class_2818
import net.minecraft.class_2826
import net.minecraft.class_3215
import net.minecraft.class_3218
import net.minecraft.class_3222
import net.minecraft.class_3227
import net.minecraft.class_3491
import net.minecraft.class_3492
import net.minecraft.class_3499
import net.minecraft.class_3828
import net.minecraft.class_3829
import net.minecraft.class_4076
import net.minecraft.class_4538
import net.minecraft.class_5425
import net.minecraft.class_2338.class_2339
import net.minecraft.class_3499.class_3501
import net.minecraft.class_7225.class_7874
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.Logger
import org.joml.Matrix4dc
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.core.internal.ships.VsiServerShip
import org.valkyrienskies.core.util.events.EventEmitterImpl
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.networking.PacketStopChunkUpdatesKt
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils
import org.valkyrienskies.mod.common.util.MinecraftPlayer
import org.valkyrienskies.mod.common.util.SplittingDisablerAttachment
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.common.world.VSTicketType
import org.valkyrienskies.mod.mixin.accessors.server.level.ServerChunkCacheAccessor
import org.valkyrienskies.mod.util.LoggingKt
import org.valkyrienskies.mod.util.StructureTemplateFillFromVoxelSet

@SourceDebugExtension(["SMAP\nShipAssembler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipAssembler.kt\norg/valkyrienskies/mod/common/assembly/ShipAssembler\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,791:1\n1#2:792\n774#3:793\n865#3,2:794\n1863#3,2:796\n1368#3:798\n1454#3,5:799\n1557#3:804\n1628#3,3:805\n1863#3,2:808\n1557#3:810\n1628#3,3:811\n1557#3:814\n1628#3,3:815\n1557#3:818\n1628#3,3:819\n1863#3,2:822\n1557#3:824\n1628#3,3:825\n1557#3:828\n1628#3,3:829\n1557#3:832\n1628#3,3:833\n1734#3,3:836\n827#3:839\n855#3,2:840\n1863#3,2:842\n1734#3,3:844\n1863#3,2:847\n*S KotlinDebug\n*F\n+ 1 ShipAssembler.kt\norg/valkyrienskies/mod/common/assembly/ShipAssembler\n*L\n179#1:793\n179#1:794,2\n224#1:796,2\n229#1:798\n229#1:799,5\n230#1:804\n230#1:805,3\n232#1:808,2\n291#1:810\n291#1:811,3\n431#1:814\n431#1:815,3\n450#1:818\n450#1:819,3\n451#1:822,2\n505#1:824\n505#1:825,3\n608#1:828\n608#1:829,3\n756#1:832\n756#1:833,3\n304#1:836,3\n309#1:839\n309#1:840,2\n312#1:842,2\n652#1:844,3\n658#1:847,2\n*E\n"])
public object ShipAssembler {
   public final val ASSEMBLY_LOGGER: Logger = LoggingKt.logger("(Valkyrien Skies) Sandwich Factory")
   private final val failedMove: org.valkyrienskies.mod.common.assembly.ShipAssembler.MoveContext = ShipAssembler.MoveContext(false, Vector3d(), Vector3d())

   @JvmStatic
   public fun findMinAndMax(blocks: Iterable<class_2338>): Pair<class_2338, class_2338> {
      val minCorner: class_2339 = class_2339(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)
      val maxCorner: class_2339 = class_2339(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE)

      for (pos in blocks) {
         minCorner.method_33097(Math.min(minCorner.method_10263(), pos.method_10263()))
         minCorner.method_33098(Math.min(minCorner.method_10264(), pos.method_10264()))
         minCorner.method_33099(Math.min(minCorner.method_10260(), pos.method_10260()))
         maxCorner.method_33097(Math.max(maxCorner.method_10263(), pos.method_10263()))
         maxCorner.method_33098(Math.max(maxCorner.method_10264(), pos.method_10264()))
         maxCorner.method_33099(Math.max(maxCorner.method_10260(), pos.method_10260()))
      }

      return TuplesKt.to(minCorner, maxCorner)
   }

   @JvmStatic
   private fun getDistinctChunksFromBlockPosSet(blocks: Set<class_2338>): Set<class_1923> {
      val chunkSet: HashSet = HashSet()

      for (blockPos in blocks) {
         chunkSet.add(class_1923(blockPos))
      }

      return chunkSet
   }

   @JvmStatic
   public fun assembleToShipFull(level: class_3218, blocks: Set<class_2338>, scale: Double = 1.0): org.valkyrienskies.mod.common.assembly.ShipAssembler.AssembleContext {
      if (blocks.isEmpty()) {
         val var22: RuntimeException = RuntimeException("Assembly function received an empty set of blocks")
         ASSEMBLY_LOGGER.error(var22)
         throw var22
      } else {
         var minB: class_2338
         var maxB: class_2338
         var fromCenter: Vector3d
         var fromShip: ServerShip
         var var26: Double
         run label103@{
            val error: Pair = findMinAndMax(blocks)
            minB = error.component1() as class_2338
            maxB = error.component2() as class_2338
            val oldMin: Vector3d = VectorConversionsMCKt.toJOMLD(minB as class_2382)
            fromCenter = VectorConversionsMCKt.toJOMLD(maxB as class_2382).get(Vector3d()).sub(oldMin).add(1.0, 1.0, 1.0).div(2.0).get(Vector3d()).add(oldMin)
            val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level, fromCenter)
            fromShip = if (var10000 != null) var10000 else VSGameUtilsKt.getShipManagingPos(level, fromCenter)
            if (fromShip != null) {
               val var24: ShipTransform = fromShip.getTransform()
               if (var24 != null) {
                  val var25: Vector3dc = var24.getScaling()
                  if (var25 != null) {
                     var26 = var25.x()
                     return@label103
                  }
               }
            }

            var26 = 1.0
         }

         run label107@{
            if (fromShip != null) {
               val var27: Matrix4dc = fromShip.getShipToWorld()
               if (var27 != null) {
                  var28 = var27.transformPosition(fromCenter.get(Vector3d()))
                  if (var28 != null) {
                     return@label107
                  }
               }
            }

            var28 = fromCenter.get(Vector3d())
         }

         val toShip: ServerShip = shipObjectWorld.createNewShipAtBlock(Vector3i(var28, 2), false, scale * var26, dimensionId)
         toShip.isStatic = fromShip == null || fromShip.isStatic
         EntityShipCollisionUtils.markShipAsRecentlySpawned(toShip.getId(), level.method_8510(), 40L)
         val var16: ShipAssembler.MoveContext = moveBlocksFromTo$default(
            level, blocks, fromShip, toShip, minB, maxB, toShip.getChunkClaim().getCenterBlockCoordinates(yRange, Vector3i()), false, 128, null
         )
         val wasSuccessful: Boolean = var16.component1()
         val toCenter: Vector3d = var16.component3()
         if (!wasSuccessful) {
            shipObjectWorld.deleteShip(toShip)
            val var23: AssertionError = AssertionError("Couldn't move blocks")
            ASSEMBLY_LOGGER.error(var23)
            throw var23
         } else {
            run label111@{
               val it: Vector3d = Vector3d(toShip.inertiaData.getCenterOfMass()).sub(Vector3d(toCenter))
               if (fromShip != null) {
                  val var29: Matrix4dc = fromShip.getShipToWorld()
                  if (var29 != null) {
                     var30 = var29.transformDirection(it)
                     if (var30 != null) {
                        return@label111
                     }
                  }
               }

               var30 = it
            }

            var var10001: VsiCore
            var var10002: Vector3dc
            run label114@{
               var31 = toShip as VsiServerShip
               var10001 = vsCore
               if (fromShip != null) {
                  var10002 = fromShip.getVelocity()
                  if (var10002 != null) {
                     return@label114
                  }
               }

               var10002 = Vector3d()
            }

            var var10003: Vector3dc
            run label117@{
               if (fromShip != null) {
                  var10003 = fromShip.getAngularVelocity()
                  if (var10003 != null) {
                     return@label117
                  }
               }

               var10003 = Vector3d()
            }

            var var32: Vector3d
            var var10004: VsiCore
            run label121@{
               var10004 = vsCore
               if (fromShip != null) {
                  val var10005: Matrix4dc = fromShip.getShipToWorld()
                  if (var10005 != null) {
                     var32 = var10005.transformPosition(Vector3d(fromCenter))
                     if (var32 != null) {
                        return@label121
                     }
                  }
               }

               var32 = fromCenter
            }

            var var35: Quaterniondc
            run label125@{
               var32 = var32.add(var30)
               var34 = var32
               if (fromShip != null) {
                  val var10006: ShipTransform = fromShip.getTransform()
                  if (var10006 != null) {
                     var35 = var10006.shipToWorldRotation
                     if (var35 != null) {
                        return@label125
                     }
                  }
               }

               var35 = Quaterniond()
            }

            var31.unsafeSetKinematics(
               var10001.newBodyKinematics(
                  var10002, var10003, var10004.newBodyTransform(var34, var35, Vector3d(scale * var26, scale * var26, scale * var26), toCenter)
               )
            )
            toShip.isStatic = false
            return ShipAssembler.AssembleContext(toShip, fromCenter, toCenter)
         }
      }
   }

   @JvmStatic
   public fun moveBlocksFromTo(
      level: class_3218,
      blocks: Set<class_2338>,
      fromShip: ServerShip?,
      toShip: ServerShip?,
      minStructurePos: class_2338,
      maxStructurePos: class_2338,
      toCenter: Vector3i,
      removeOriginal: Boolean = true
   ): org.valkyrienskies.mod.common.assembly.ShipAssembler.MoveContext {
      val eventData: java.lang.Iterable = blocks
      val oldMin: java.util.Collection = ArrayList()

      for (fromCenter in eventData) {
         val toChunkCenter: class_2680 = level.method_8320(fromCenter as class_2338)
         if (!toChunkCenter.method_26215() && !VSGameUtilsKt.inAssemblyBlacklist(toChunkCenter)) {
            oldMin.add(fromCenter)
         }
      }

      val blocksx: java.util.Set = CollectionsKt.toSet(oldMin as java.util.List)
      if (blocksx.isEmpty()) {
         return failedMove
      } else {
         val var47: Long = if (fromShip != null) fromShip.getId() else -1L
         val var48: java.util.Map = LinkedHashMap()
         val var49: Vector3d = VectorConversionsMCKt.toJOMLD(minStructurePos as class_2382)
         val var50: Vector3d = VectorConversionsMCKt.toJOMLD(maxStructurePos as class_2382)
         val var51: Vector3d = var50.get(Vector3d()).sub(var49).add(1.0, 1.0, 1.0).div(2.0)
         val var52: Vector3d = var51.get(Vector3d()).add(var49)
         val var53: BooleanRef = BooleanRef()
         var53.element = true
         if (fromShip is LoadedServerShip) {
            val var54: SplittingDisablerAttachment = (fromShip as LoadedServerShip).getAttachment(SplittingDisablerAttachment.class)
            var53.element = var54 == null || var54.canSplit()
            if (var54 != null) {
               var54.disableSplitting()
            }
         }

         var var55: class_3499
         var var106: StructureTemplateFillFromVoxelSet
         var var10001: class_1937
         var var10002: java.lang.Iterable
         var var10003: java.util.List
         run label162@{
            val var10000: EventEmitterImpl = VSAssemblyEvents.INSTANCE.beforeCopy
            val var10004: Vector3dc = var49
            val var10005: Vector3dc = var50
            var10000.emit(VSAssemblyEvents.BeforeCopy(level, var10004, var10005, var52, fromShip, blocksx, var48))
            var55 = class_3499()
            var106 = var55 as StructureTemplateFillFromVoxelSet
            var10001 = level as class_1937
            var10002 = blocksx
            if (fromShip != null) {
               val var46: java.util.List = CollectionsKt.listOf(fromShip)
               var10003 = var46
               if (var46 != null) {
                  return@label162
               }
            }

            var10003 = CollectionsKt.emptyList()
         }

         var106.vs$fillFromVoxelSet(
            var10001, var10002, var10003, ShipAssembler.SingleItemMap<>(var47, var52, Vector3d(), null, 8, null), minStructurePos, maxStructurePos
         )
         val var56: class_1923 = class_1923(toCenter.x shr 4, toCenter.z shr 4)
         val var59: Int = ((minStructurePos.method_10263() + maxStructurePos.method_10263()) / 2 shr 4) - var56.field_9181
         val deltaZ: Int = ((minStructurePos.method_10260() + maxStructurePos.method_10260()) / 2 shr 4) - var56.field_9180
         val chunksToBeUpdated: java.util.Map = LinkedHashMap()

         for (cornerOfShip in getDistinctChunksFromBlockPosSet(blocksx)) {
            chunksToBeUpdated.put(
               cornerOfShip as class_1923,
               Pair(cornerOfShip as class_1923, class_1923((cornerOfShip as class_1923).field_9181 - var59, (cornerOfShip as class_1923).field_9180 - deltaZ))
            )
         }

         val var69: java.lang.Iterable = CollectionsKt.toList(chunksToBeUpdated.values())
         val `destination$iv$ivx`: java.util.Collection = ArrayList()

         for (`$i$f$map` in var69) {
            CollectionsKt.addAll(`destination$iv$ivx`, TuplesKt.toList(`$i$f$map` as Pair))
         }

         val var61: java.util.List = `destination$iv$ivx` as java.util.List
         val var76: java.lang.Iterable = `destination$iv$ivx` as java.util.List
         val `destination$iv$ivxx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(`destination$iv$ivx` as java.util.List, 10))

         for (var96 in var76) {
            `destination$iv$ivxx`.add(VectorConversionsMCKt.toJOML(var96 as class_1923))
         }

         val var63: java.util.List = `destination$iv$ivxx` as java.util.List
         val var107: java.util.List = level.method_18456()

         for (var84 in var107) {
            val var88: class_3222 = var84 as class_3222
            ASSEMBLY_LOGGER.debug("Pausing chunk updates for ${(var84 as class_3222).method_5477()}")
            val var97: SimplePacketNetworking = vsCore.simplePacketNetworking
            val var110: MinecraftPlayer = playerWrapper
            PacketStopChunkUpdatesKt.sendStopChunkUpdates(var97, var63, var110)
         }

         if (removeOriginal) {
            val var67: Int = 114

            for (var78 in blocksx) {
               val var108: class_2586 = level.method_8321(var78)
               if (var108 != null) {
                  if (var108 is class_3829) {
                     class_3829.method_16825(var108)
                  } else {
                     var108.method_58690(class_2487(), level.method_30349() as class_7874)
                  }

                  level.method_8544(var78)
               }

               level.method_8652(var78, class_2246.field_10124.method_9564(), var67)
            }

            for (var79 in blocksx) {
               level.method_14178().method_17293().method_15513(var79)
            }
         }

         val var80: Vector3d = Vector3d(toCenter).sub(var51).ceil()
         val var68: class_2338 = class_2338((int)var80.x, (int)var80.y, (int)var80.z)
         val var74: Vector3d = VectorConversionsMCKt.toJOMLD(var68 as class_2382).add(var51)
         val var81: class_3492 = class_3492()
            .method_16184(ShipAssembler.ICopyableProcessor(ShipAssembler.SingleItemMap<>(var47, if (toShip != null) toShip.getId() else -1L, -1L, { it: Long ->
               it
            }), ShipAssembler.SingleItemMap<>(var47, Pair(var52, Vector3d(var74)), Pair(Vector3d(), Vector3d()), null, 8, null)))
            var81.method_15119(var68)
         VSAssemblyEvents.INSTANCE.onPasteBeforeBlocksAreLoaded
            .emit(VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded(level, fromShip, toShip, Pair(var52, var74), var48))
            var55.method_15172(level as class_5425, var68, var68, var81, level.field_9229, 2)
         val var99: java.lang.Iterable = blocksx
         val `destination$iv$ivxxx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(blocksx, 10))

         for (`item$iv$iv` in var99) {
            `destination$iv$ivxxx`.add(
               class_2338(
                  var68.method_10263() + ((`item$iv$iv` as class_2338).method_10263() - minStructurePos.method_10263()),
                  var68.method_10264() + ((`item$iv$iv` as class_2338).method_10264() - minStructurePos.method_10264()),
                  var68.method_10260() + ((`item$iv$iv` as class_2338).method_10260() - minStructurePos.method_10260())
               )
            )
         }

         INSTANCE.initSkyLightForShip$valkyrienskies_1_21_1(level, `destination$iv$ivxxx` as MutableList<class_2338>)
         val var90: Int = level.method_8503().method_3780()
         val var109: MinecraftServer = level.method_8503()
         VSGameUtilsKt.executeIf(
            var109,
            { 
               val `$this$all$iv`: java.lang.Iterable = `$chunkPoses`
               var var10000: Boolean
               if (`$chunkPoses` is java.util.Collection && (`$chunkPoses` as java.util.Collection).isEmpty()) {
                  var10000 = true
               } else {
                  val var5: java.util.Iterator = `$this$all$iv`.iterator()

                  while (true) {
                     if (!var5.hasNext()) {
                        var10000 = true
                        break
                     }

                     if (!VSGameUtilsKt.isChunkLoadedForVS(`$level` as class_1937, var5.next() as class_1923)) {
                        var10000 = false
                        break
                     }
                  }
               }

               var10000 || `$level`.method_8503().method_3780() - `$timeAtExecution` > 60
            },
            { 
               if (`$level`.method_8503().method_3780() - `$timeAtExecution` > 60) {
                  ASSEMBLY_LOGGER.warn("Timed out waiting for chunks to start ticking after assembly! Forcibly resuming...")
                  ASSEMBLY_LOGGER.warn("All chunks involved in assembly: $`$chunkPoses`")
                  val splittingDisabler: java.lang.Iterable = `$chunkPoses`
                  val var19: Logger = ASSEMBLY_LOGGER
                  val chunkSections: java.util.Collection = ArrayList()

                  for (sectionPos in splittingDisabler) {
                     if (!VSGameUtilsKt.isChunkLoadedForVS(`$level` as class_1937, sectionPos as class_1923)) {
                        chunkSections.add(sectionPos)
                     }
                  }

                  var19.warn("Chunks that were not loaded: ${chunkSections as java.util.List}")
               }

               val var10000: java.util.List = `$level`.method_18456()

               for (var27 in var10000) {
                  val var29: class_3222 = var27 as class_3222
                  ASSEMBLY_LOGGER.debug("Resuming chunk updates for ${(var27 as class_3222).method_5477()}")
                  val var33: SimplePacketNetworking = vsCore.simplePacketNetworking
                  val var10002: MinecraftPlayer = playerWrapper
                  PacketStopChunkUpdatesKt.sendRestartChunkUpdates(var33, `$chunkPosesJOML`, var10002)
               }

               VSAssemblyEvents.INSTANCE.onPasteAfterBlocksAreLoaded
                  .emit(VSAssemblyEvents.OnPasteAfterBlocksAreLoaded(`$level`, `$fromShip`, `$toShip`, Pair(`$fromCenter`, `$centerOfShip`), `$eventData`))
                  if (VSCoreConfig.SERVER.sp.enableConnectivity) {
                  for (var24 in `$chunkPoses`) {
                     val var38: class_2818 = `$level`.method_8497(var24.field_9181, var24.field_9180)
                     if (var38 != null) {
                        val var26: class_2818 = var38
                        val var39: Array<class_2826> = var38.method_12006()
                        if (var39 != null) {
                           val var28: Array<class_2826> = var39
                           var var30: Int = 0

                           for (var32 in var38.method_32890()..var30) {
                              val var34: Vector3i = Vector3i(var24.field_9181, var26.method_31604(var30), var24.field_9180)
                              val var40: class_2826 = var28[var30]
                              if (var28[var30] != null && !var28[var30].method_38292()) {
                                 shipObjectWorld
                                    .forceUpdateConnectivityChunk(dimensionId, var34.x, var34.y, var34.z, VSGameUtilsKt.toDenseVoxelUpdate(var40, var34))
                                 }
                           }
                        }
                     }
                  }
               }

               if (`$fromShip` is LoadedServerShip) {
                  val var22: SplittingDisablerAttachment = (`$fromShip` as LoadedServerShip).getAttachment(SplittingDisablerAttachment.class)
                  if (`$wasSplittingEnabled`.element) {
                     if (var22 != null) {
                        var22.enableSplitting()
                     }
                  }
               }
            }
         )
         return ShipAssembler.MoveContext(true, var52, var74)
      }
   }

   @JvmStatic
   public fun assembleToShip(level: class_3218, blocks: Set<class_2338>, scale: Double = 1.0): ServerShip {
      return assembleToShipFull(level, blocks, scale).ship
   }

   @Deprecated(message = "Old")
   public fun assembleToShip(level: class_1937, blocks: List<class_2338>, removeOriginal: Boolean, scale: Double = 1.0, shouldDisableSplitting: Boolean = false): ServerShip {
      return assembleToShip(level as class_3218, CollectionsKt.toSet(blocks), scale)
   }

   @JvmStatic
   public fun batchAssembleToShips(level: class_3218, blockSets: List<Set<class_2338>>, scale: Double = 1.0): List<
         org.valkyrienskies.mod.common.assembly.ShipAssembler.AssembleContext
      > {
      if (blockSets.isEmpty()) {
         return CollectionsKt.emptyList()
      } else {
         val results: java.util.List = ArrayList()
         val allDestChunkPoses: LinkedHashSet = LinkedHashSet()
         val allSourceChunkPoses: LinkedHashSet = LinkedHashSet()
         val pendingAssemblies: java.util.List = ArrayList()

         @Metadata(
            mv = {2, 0, 0},
            k = 1,
            xi = 48,
            d1 = "\u0000I\n\u0000\n\u0002\u0010\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0012*\u0001\u0000\b\u008a\b\u0018\u00002\u00020\u0001Bk\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\u0006\u0010\r\u001a\u00020\f\u0012\u0006\u0010\u000e\u001a\u00020\n\u0012\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0002\u0012\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0002¢\u0006\u0004\b\u0012\u0010\u0013J\u0016\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002HÆ\u0003¢\u0006\u0004\b\u0014\u0010\u0015J\u0012\u0010\u0016\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0004\b\u0016\u0010\u0017J\u0010\u0010\u0018\u001a\u00020\u0005HÆ\u0003¢\u0006\u0004\b\u0018\u0010\u0017J\u0010\u0010\u0019\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0019\u0010\u001aJ\u0010\u0010\u001b\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u001b\u0010\u001aJ\u0010\u0010\u001c\u001a\u00020\nHÆ\u0003¢\u0006\u0004\b\u001c\u0010\u001dJ\u0010\u0010\u001e\u001a\u00020\fHÆ\u0003¢\u0006\u0004\b\u001e\u0010\u001fJ\u0010\u0010 \u001a\u00020\nHÆ\u0003¢\u0006\u0004\b \u0010\u001dJ\u0016\u0010!\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0002HÆ\u0003¢\u0006\u0004\b!\u0010\u0015J\u0016\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0002HÆ\u0003¢\u0006\u0004\b\"\u0010\u0015J\u0088\u0001\u0010#\u001a\u00020\u00002\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\f2\b\b\u0002\u0010\u000e\u001a\u00020\n2\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00022\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0002HÆ\u0001¢\u0006\u0004\b#\u0010$J\u001a\u0010'\u001a\u00020&2\b\u0010%\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b'\u0010(J\u0010\u0010*\u001a\u00020)HÖ\u0001¢\u0006\u0004\b*\u0010+J\u0010\u0010-\u001a\u00020,HÖ\u0001¢\u0006\u0004\b-\u0010.R\u001d\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006¢\u0006\f\n\u0004\b\u0004\u0010/\u001a\u0004\b0\u0010\u0015R\u0019\u0010\u0006\u001a\u0004\u0018\u00010\u00058\u0006¢\u0006\f\n\u0004\b\u0006\u00101\u001a\u0004\b2\u0010\u0017R\u0017\u0010\u0007\u001a\u00020\u00058\u0006¢\u0006\f\n\u0004\b\u0007\u00101\u001a\u0004\b3\u0010\u0017R\u0017\u0010\b\u001a\u00020\u00038\u0006¢\u0006\f\n\u0004\b\b\u00104\u001a\u0004\b5\u0010\u001aR\u0017\u0010\t\u001a\u00020\u00038\u0006¢\u0006\f\n\u0004\b\t\u00104\u001a\u0004\b6\u0010\u001aR\u0017\u0010\u000b\u001a\u00020\n8\u0006¢\u0006\f\n\u0004\b\u000b\u00107\u001a\u0004\b8\u0010\u001dR\u0017\u0010\r\u001a\u00020\f8\u0006¢\u0006\f\n\u0004\b\r\u00109\u001a\u0004\b:\u0010\u001fR\u0017\u0010\u000e\u001a\u00020\n8\u0006¢\u0006\f\n\u0004\b\u000e\u00107\u001a\u0004\b;\u0010\u001dR\u001d\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00028\u0006¢\u0006\f\n\u0004\b\u0010\u0010/\u001a\u0004\b<\u0010\u0015R\u001d\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00028\u0006¢\u0006\f\n\u0004\b\u0011\u0010/\u001a\u0004\b=\u0010\u0015¨\u0006>",
            d2 = {
                  "org/valkyrienskies/mod/common/assembly/ShipAssembler.batchAssembleToShips.PendingAssembly",
                  "",
                  "",
                  "Lnet/minecraft/class_2338;",
                  "blocks",
                  "Lorg/valkyrienskies/core/api/ships/ServerShip;",
                  "fromShip",
                  "toShip",
                  "minB",
                  "maxB",
                  "Lorg/joml/Vector3d;",
                  "fromCenter",
                  "Lorg/joml/Vector3i;",
                  "toCenter",
                  "offset",
                  "Lnet/minecraft/class_1923;",
                  "sourceChunks",
                  "destChunks",
                  "<init>",
                  "(Ljava/util/Set;Lorg/valkyrienskies/core/api/ships/ServerShip;Lorg/valkyrienskies/core/api/ships/ServerShip;Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;Lorg/joml/Vector3d;Lorg/joml/Vector3i;Lorg/joml/Vector3d;Ljava/util/Set;Ljava/util/Set;)V",
                  "component1",
                  "()Ljava/util/Set;",
                  "component2",
                  "()Lorg/valkyrienskies/core/api/ships/ServerShip;",
                  "component3",
                  "component4",
                  "()Lnet/minecraft/class_2338;",
                  "component5",
                  "component6",
                  "()Lorg/joml/Vector3d;",
                  "component7",
                  "()Lorg/joml/Vector3i;",
                  "component8",
                  "component9",
                  "component10",
                  "copy",
                  "(Ljava/util/Set;Lorg/valkyrienskies/core/api/ships/ServerShip;Lorg/valkyrienskies/core/api/ships/ServerShip;Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;Lorg/joml/Vector3d;Lorg/joml/Vector3i;Lorg/joml/Vector3d;Ljava/util/Set;Ljava/util/Set;)Lorg/valkyrienskies/mod/common/assembly/ShipAssembler$batchAssembleToShips$PendingAssembly;",
                  "other",
                  "",
                  "equals",
                  "(Ljava/lang/Object;)Z",
                  "",
                  "hashCode",
                  "()I",
                  "",
                  "toString",
                  "()Ljava/lang/String;",
                  "Ljava/util/Set;",
                  "getBlocks",
                  "Lorg/valkyrienskies/core/api/ships/ServerShip;",
                  "getFromShip",
                  "getToShip",
                  "Lnet/minecraft/class_2338;",
                  "getMinB",
                  "getMaxB",
                  "Lorg/joml/Vector3d;",
                  "getFromCenter",
                  "Lorg/joml/Vector3i;",
                  "getToCenter",
                  "getOffset",
                  "getSourceChunks",
                  "getDestChunks",
                  "valkyrienskies-1-21-1"
            }
         )
         final class PendingAssembly {
            private final java.util.Set<class_2338> blocks;
            private final ServerShip fromShip;
            private final ServerShip toShip;
            private final class_2338 minB;
            private final class_2338 maxB;
            private final Vector3d fromCenter;
            private final Vector3i toCenter;
            private final Vector3d offset;
            private final java.util.Set<class_1923> sourceChunks;
            private final java.util.Set<class_1923> destChunks;

            public PendingAssembly(
               java.util.Set<? extends class_2338> blocks,
               ServerShip fromShip,
               ServerShip toShip,
               class_2338 minB,
               class_2338 maxB,
               Vector3d fromCenter,
               Vector3i toCenter,
               Vector3d offset,
               java.util.Set<? extends class_1923> sourceChunks,
               java.util.Set<? extends class_1923> destChunks
            ) {
               this.blocks = blocks
               this.fromShip = fromShip
               this.toShip = toShip
               this.minB = minB
               this.maxB = maxB
               this.fromCenter = fromCenter
               this.toCenter = toCenter
               this.offset = offset
               this.sourceChunks = sourceChunks
               this.destChunks = destChunks
            }

            public final java.util.Set<class_2338> getBlocks() {
               this.blocks
            }

            public final ServerShip getFromShip() {
               this.fromShip
            }

            public final ServerShip getToShip() {
               this.toShip
            }

            public final class_2338 getMinB() {
               this.minB
            }

            public final class_2338 getMaxB() {
               this.maxB
            }

            public final Vector3d getFromCenter() {
               this.fromCenter
            }

            public final Vector3i getToCenter() {
               this.toCenter
            }

            public final Vector3d getOffset() {
               this.offset
            }

            public final java.util.Set<class_1923> getSourceChunks() {
               this.sourceChunks
            }

            public final java.util.Set<class_1923> getDestChunks() {
               this.destChunks
            }

            public final java.util.Set<class_2338> component1() {
               this.blocks
            }

            public final ServerShip component2() {
               this.fromShip
            }

            public final ServerShip component3() {
               this.toShip
            }

            public final class_2338 component4() {
               this.minB
            }

            public final class_2338 component5() {
               this.maxB
            }

            public final Vector3d component6() {
               this.fromCenter
            }

            public final Vector3i component7() {
               this.toCenter
            }

            public final Vector3d component8() {
               this.offset
            }

            public final java.util.Set<class_1923> component9() {
               this.sourceChunks
            }

            public final java.util.Set<class_1923> component10() {
               this.destChunks
            }

            public final PendingAssembly copy(
               java.util.Set<? extends class_2338> blocks,
               ServerShip fromShip,
               ServerShip toShip,
               class_2338 minB,
               class_2338 maxB,
               Vector3d fromCenter,
               Vector3i toCenter,
               Vector3d offset,
               java.util.Set<? extends class_1923> sourceChunks,
               java.util.Set<? extends class_1923> destChunks
            ) {
               object {
                  public final val blocks: Set<class_2338>
                  public final val fromShip: ServerShip?
                  public final val toShip: ServerShip
                  public final val minB: class_2338
                  public final val maxB: class_2338
                  public final val fromCenter: Vector3d
                  public final val toCenter: Vector3i
                  public final val offset: Vector3d
                  public final val sourceChunks: Set<class_1923>
                  public final val destChunks: Set<class_1923>

                  init {
                     this.blocks = blocks
                     this.fromShip = fromShip
                     this.toShip = toShip
                     this.minB = minB
                     this.maxB = maxB
                     this.fromCenter = fromCenter
                     this.toCenter = toCenter
                     this.offset = offset
                     this.sourceChunks = sourceChunks
                     this.destChunks = destChunks
                  }

                  public operator fun component1(): Set<class_2338> {
                     this.blocks
                  }

                  public operator fun component2(): ServerShip? {
                     this.fromShip
                  }

                  public operator fun component3(): ServerShip {
                     this.toShip
                  }

                  public operator fun component4(): class_2338 {
                     this.minB
                  }

                  public operator fun component5(): class_2338 {
                     this.maxB
                  }

                  public operator fun component6(): Vector3d {
                     this.fromCenter
                  }

                  public operator fun component7(): Vector3i {
                     this.toCenter
                  }

                  public operator fun component8(): Vector3d {
                     this.offset
                  }

                  public operator fun component9(): Set<class_1923> {
                     this.sourceChunks
                  }

                  public operator fun component10(): Set<class_1923> {
                     this.destChunks
                  }

                  public fun copy(
                     blocks: Set<class_2338> = this.blocks,
                     fromShip: ServerShip? = this.fromShip,
                     toShip: ServerShip = this.toShip,
                     minB: class_2338 = this.minB,
                     maxB: class_2338 = this.maxB,
                     fromCenter: Vector3d = this.fromCenter,
                     toCenter: Vector3i = this.toCenter,
                     offset: Vector3d = this.offset,
                     sourceChunks: Set<class_1923> = this.sourceChunks,
                     destChunks: Set<class_1923> = this.destChunks
                  ): org.valkyrienskies.mod.common.assembly.ShipAssembler.batchAssembleToShips.PendingAssembly {
                     blocks.PendingAssembly(blocks, fromShip, toShip, minB, maxB, fromCenter, toCenter, offset, sourceChunks, destChunks)
                  }

                  public override fun toString(): String {
                     "PendingAssembly(blocks=${this.blocks}, fromShip=${this.fromShip}, toShip=${this.toShip}, minB=${this.minB}, maxB=${this.maxB}, fromCenter=${this.fromCenter}, toCenter=${this.toCenter}, offset=${this.offset}, sourceChunks=${this.sourceChunks}, destChunks=${this.destChunks})"
                  }

                  public override fun hashCode(): Int {
                     (
                              (
                                       (
                                                (
                                                         (
                                                                  (
                                                                           (
                                                                                    (
                                                                                             this.blocks.hashCode() * 31
                                                                                                + (if (this.fromShip == null) 0 else this.fromShip.hashCode())
                                                                                          )
                                                                                          * 31
                                                                                       + this.toShip.hashCode()
                                                                                 )
                                                                                 * 31
                                                                              + this.minB.hashCode()
                                                                        )
                                                                        * 31
                                                                     + this.maxB.hashCode()
                                                               )
                                                               * 31
                                                            + this.fromCenter.hashCode()
                                                      )
                                                      * 31
                                                   + this.toCenter.hashCode()
                                             )
                                             * 31
                                          + this.offset.hashCode()
                                    )
                                    * 31
                                 + this.sourceChunks.hashCode()
                           )
                           * 31
                        + this.destChunks.hashCode()
                     }

                  public override operator fun equals(other: Any?): Boolean {
                     label76@
                     if (this === other) {
                        true
                     } else {
                        other is PendingAssembly
                           && this.blocks == (other as PendingAssembly).blocks
                           && this.fromShip == (other as PendingAssembly).fromShip
                           && this.toShip == (other as PendingAssembly).toShip
                           && this.minB == (other as PendingAssembly).minB
                           && this.maxB == (other as PendingAssembly).maxB
                           && this.fromCenter == (other as PendingAssembly).fromCenter
                           && this.toCenter == (other as PendingAssembly).toCenter
                           && this.offset == (other as PendingAssembly).offset
                           && this.sourceChunks == (other as PendingAssembly).sourceChunks
                           && this.destChunks == (other as PendingAssembly).destChunks
                        }
                  }
               }
            }

            // $VF: synthetic method
            public static PendingAssembly copy$default(
               PendingAssembly var0,
               java.util.Set var1,
               ServerShip var2,
               ServerShip var3,
               class_2338 var4,
               class_2338 var5,
               Vector3d var6,
               Vector3i var7,
               Vector3d var8,
               java.util.Set var9,
               java.util.Set var10,
               int var11,
               Object var12
            ) {
               if ((var11 and 1) != 0) {
                  var1 = var0.blocks
               }

               if ((var11 and 2) != 0) {
                  var2 = var0.fromShip
               }

               if ((var11 and 4) != 0) {
                  var3 = var0.toShip
               }

               if ((var11 and 8) != 0) {
                  var4 = var0.minB
               }

               if ((var11 and 16) != 0) {
                  var5 = var0.maxB
               }

               if ((var11 and 32) != 0) {
                  var6 = var0.fromCenter
               }

               if ((var11 and 64) != 0) {
                  var7 = var0.toCenter
               }

               if ((var11 and 128) != 0) {
                  var8 = var0.offset
               }

               if ((var11 and 256) != 0) {
                  var9 = var0.sourceChunks
               }

               if ((var11 and 512) != 0) {
                  var10 = var0.destChunks
               }

               var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10)
            }

            @Override
            public java.lang.String toString() {
               "PendingAssembly(blocks=${this.blocks}, fromShip=${this.fromShip}, toShip=${this.toShip}, minB=${this.minB}, maxB=${this.maxB}, fromCenter=${this.fromCenter}, toCenter=${this.toCenter}, offset=${this.offset}, sourceChunks=${this.sourceChunks}, destChunks=${this.destChunks})"
            }

            @Override
            public int hashCode() {
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              (
                                                                                       this.blocks.hashCode() * 31
                                                                                          + (if (this.fromShip == null) 0 else this.fromShip.hashCode())
                                                                                    )
                                                                                    * 31
                                                                                 + this.toShip.hashCode()
                                                                           )
                                                                           * 31
                                                                        + this.minB.hashCode()
                                                                  )
                                                                  * 31
                                                               + this.maxB.hashCode()
                                                         )
                                                         * 31
                                                      + this.fromCenter.hashCode()
                                                )
                                                * 31
                                             + this.toCenter.hashCode()
                                       )
                                       * 31
                                    + this.offset.hashCode()
                              )
                              * 31
                           + this.sourceChunks.hashCode()
                     )
                     * 31
                  + this.destChunks.hashCode()
               }

            @Override
            public boolean equals(Object other) {
               label76@
               if (this === other) {
                  true
               } else {
                  other is PendingAssembly
                     && this.blocks == (other as PendingAssembly).blocks
                     && this.fromShip == (other as PendingAssembly).fromShip
                     && this.toShip == (other as PendingAssembly).toShip
                     && this.minB == (other as PendingAssembly).minB
                     && this.maxB == (other as PendingAssembly).maxB
                     && this.fromCenter == (other as PendingAssembly).fromCenter
                     && this.toCenter == (other as PendingAssembly).toCenter
                     && this.offset == (other as PendingAssembly).offset
                     && this.sourceChunks == (other as PendingAssembly).sourceChunks
                     && this.destChunks == (other as PendingAssembly).destChunks
                  }
            }
         }
         for (blockSet in blockSets) {
            if (!blockSet.isEmpty()) {
               var allChunkPosesJOML: class_2338
               var preloadStart: class_2338
               var preloadMs: Vector3d
               var cp: Vector3d
               var phase3Start: ServerShip
               var var153: Double
               run label357@{
                  val allChunkPoses: Pair = findMinAndMax(blockSet)
                  allChunkPosesJOML = allChunkPoses.component1() as class_2338
                  preloadStart = allChunkPoses.component2() as class_2338
                  val `$i$f$forEach`: Vector3d = VectorConversionsMCKt.toJOMLD(allChunkPosesJOML as class_2382)
                  preloadMs = VectorConversionsMCKt.toJOMLD(preloadStart as class_2382).get(Vector3d()).sub(`$i$f$forEach`).add(1.0, 1.0, 1.0).div(2.0)
                  cp = preloadMs.get(Vector3d()).add(`$i$f$forEach`)
                  val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level, cp)
                  phase3Start = if (var10000 != null) var10000 else VSGameUtilsKt.getShipManagingPos(level, cp)
                  if (phase3Start != null) {
                     val var151: ShipTransform = phase3Start.getTransform()
                     if (var151 != null) {
                        val var152: Vector3dc = var151.getScaling()
                        if (var152 != null) {
                           var153 = var152.x()
                           return@label357
                        }
                     }
                  }

                  var153 = 1.0
               }

               run label361@{
                  if (phase3Start != null) {
                     val var154: Matrix4dc = phase3Start.getShipToWorld()
                     if (var154 != null) {
                        var155 = var154.transformPosition(cp.get(Vector3d()))
                        if (var155 != null) {
                           return@label361
                        }
                     }
                  }

                  var155 = cp.get(Vector3d())
               }

               val destChunkPoses: ServerShip = shipObjectWorld.createNewShipAtBlock(Vector3i(var155, 2), false, scale * var153, dimensionId)
               destChunkPoses.isStatic = phase3Start == null || phase3Start.isStatic
               EntityShipCollisionUtils.markShipAsRecentlySpawned(destChunkPoses.getId(), level.method_8510(), 40L)
               val timeAtExecution: Vector3i = destChunkPoses.getChunkClaim().getCenterBlockCoordinates(yRange, Vector3i())
               val fromId: class_1923 = class_1923(timeAtExecution.x shr 4, timeAtExecution.z shr 4)
               val oldMin: Int = ((allChunkPosesJOML.method_10263() + preloadStart.method_10263()) / 2 shr 4) - fromId.field_9181
               val oldMax: Int = ((allChunkPosesJOML.method_10260() + preloadStart.method_10260()) / 2 shr 4) - fromId.field_9180
               val offset: java.util.Set = getDistinctChunksFromBlockPosSet(blockSet)
               val centerOfShip: java.lang.Iterable = offset
               val removeFlags: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(offset, 10))

               for (it in centerOfShip) {
                  removeFlags.add(class_1923((it as class_1923).field_9181 - oldMin, (it as class_1923).field_9180 - oldMax))
               }

               val fromCenter: java.util.Set = CollectionsKt.toSet(removeFlags as java.util.List)
               allSourceChunkPoses.addAll(offset)
               allDestChunkPoses.addAll(fromCenter)
               pendingAssemblies.add(
                  blockSet.PendingAssembly(
                     blockSet, phase3Start, destChunkPoses, allChunkPosesJOML, preloadStart, cp, timeAtExecution, preloadMs, offset, fromCenter
                  )
               )
            }
         }

         val var55: Long = System.currentTimeMillis() - System.currentTimeMillis()
         if (pendingAssemblies.size() > 5) {
            ASSEMBLY_LOGGER.info(
               "Batch assembly phase 1 (create ${pendingAssemblies.size()} ships): $var55ms (${var55 / (long)pendingAssemblies.size()}ms/ship)"
            )
         }

         if (pendingAssemblies.isEmpty()) {
            return CollectionsKt.emptyList()
         } else {
            val var58: java.lang.Iterable = SetsKt.plus(allSourceChunkPoses, allDestChunkPoses)
            val var66: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var58, 10))

            for (var78 in var58) {
               var66.add(VectorConversionsMCKt.toJOML(var78 as class_1923))
            }

            val var57: java.util.List = var66 as java.util.List
            val var156: java.util.List = level.method_18456()

            for (var67 in var156) {
               val var72: class_3222 = var67 as class_3222
               val var79: SimplePacketNetworking = vsCore.simplePacketNetworking
               val var10002: MinecraftPlayer = playerWrapper
               PacketStopChunkUpdatesKt.sendStopChunkUpdates(var79, var57, var10002)
            }

            val var60: Long = System.currentTimeMillis()
            val var65: class_3215 = level.method_14178()
            val var157: java.util.Iterator = allDestChunkPoses.iterator()
            val var68: java.util.Iterator = var157

            while (var68.hasNext()) {
               val var158: Any = var68.next()
               var65.method_17297(VSTicketType.SHIP_CHUNK, var158 as class_1923, 0, var158 as class_1923)
            }

            (var65 as ServerChunkCacheAccessor).callRunDistanceManagerUpdates()
            val var159: java.util.Iterator = allDestChunkPoses.iterator()
            val var69: java.util.Iterator = var159

            while (var69.hasNext()) {
               val var160: Any = var69.next()
               level.method_8497((var160 as class_1923).field_9181, (var160 as class_1923).field_9180)
            }

            val var70: Long = System.currentTimeMillis() - var60
            if (pendingAssemblies.size() > 5) {
               ASSEMBLY_LOGGER.info("Batch assembly preload (${allDestChunkPoses.size()} dest chunks): $var70ms")
            }

            for (var84 in pendingAssemblies) {
               val var85: java.util.List = ArrayList()

               for (var90 in var84.blocks) {
                  val var93: class_2680 = level.method_8320(var90)
                  if (!var93.method_26215() && !VSGameUtilsKt.inAssemblyBlacklist(var93)) {
                     var85.add(TuplesKt.to(var90, var93))
                  }
               }

               if (var85.isEmpty()) {
                  shipObjectWorld.deleteShip(var84.toShip)
               } else {
                  val var95: java.lang.Iterable = var85
                  val `destination$iv$ivx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var85, 10))

                  for (var103 in var95) {
                     `destination$iv$ivx`.add((var103 as Pair).getFirst() as class_2338)
                  }

                  val var88: java.util.Set = CollectionsKt.toSet(`destination$iv$ivx` as java.util.List)
                  val var161: ServerShip = var84.fromShip
                  val var92: Long = if (var161 != null) var161.getId() else -1L
                  val var96: java.util.Map = LinkedHashMap()
                  val var98: Vector3d = VectorConversionsMCKt.toJOMLD(var84.minB as class_2382)
                  val var100: Vector3d = VectorConversionsMCKt.toJOMLD(var84.maxB as class_2382)
                  val var102: Vector3d = var84.offset
                  val var104: Vector3d = var84.fromCenter
                  var var106: Boolean = true
                  if (var84.fromShip is LoadedServerShip) {
                     val var108: SplittingDisablerAttachment = (var84.fromShip as LoadedServerShip).getAttachment(SplittingDisablerAttachment.class)
                     var106 = var108 == null || var108.canSplit()
                     if (var108 != null) {
                        var108.disableSplitting()
                     }
                  }

                  VSAssemblyEvents.INSTANCE.beforeCopy.emit(VSAssemblyEvents.BeforeCopy(level, var98, var100, var104, var84.fromShip, var88, var96))
                  val var111: Vector3d = Vector3d(var84.toCenter).sub(var102).ceil()
                  val var109: class_2338 = class_2338((int)var111.x, (int)var111.y, (int)var111.z)
                  val var110: Vector3d = VectorConversionsMCKt.toJOMLD(var109 as class_2382).add(var102)
                  val var112: Int = 114
                  if (var85.size() <= 8) {
                     val var114: ArrayList = ArrayList(var85.size())

                     for (var122 in var85) {
                        val var127: class_2338 = var122.component1() as class_2338
                        val var130: class_2680 = var122.component2() as class_2680
                        val var163: class_2586 = level.method_8321(var127)
                        val `$this$mapTo$iv$iv`: class_2487 = if (var163 != null) var163.method_38242(level.method_30349() as class_7874) else null
                        val `item$iv$iv`: class_2338 = class_2338(
                           var109.method_10263() + (var127.method_10263() - var84.minB.method_10263()),
                           var109.method_10264() + (var127.method_10264() - var84.minB.method_10264()),
                           var109.method_10260() + (var127.method_10260() - var84.minB.method_10260())
                        )
                        var114.add(`item$iv$iv`)
                        val itx: class_2586 = level.method_8321(var127)
                        if (itx != null) {
                           if (itx is class_3829) {
                              class_3829.method_16825(itx)
                           } else {
                              itx.method_58690(class_2487(), level.method_30349() as class_7874)
                           }

                           level.method_8544(var127)
                        }

                        level.method_8500(var127).method_12010(var127, class_2246.field_10124.method_9564(), false)
                        level.method_8500(`item$iv$iv`).method_12010(`item$iv$iv`, var130, false)
                        if (`$this$mapTo$iv$iv` != null) {
                           `$this$mapTo$iv$iv`.method_10569("x", `item$iv$iv`.method_10263())
                           `$this$mapTo$iv$iv`.method_10569("y", `item$iv$iv`.method_10264())
                           `$this$mapTo$iv$iv`.method_10569("z", `item$iv$iv`.method_10260())
                           val var166: class_2586 = level.method_8321(`item$iv$iv`)
                           if (var166 != null) {
                              var166.method_58690(`$this$mapTo$iv$iv`, level.method_30349() as class_7874)
                           }
                        }
                     }

                     INSTANCE.initSkyLightForShip$valkyrienskies_1_21_1(level, var114)
                  } else {
                     var var115: class_3499
                     var var185: java.lang.Iterable
                     var var188: java.util.List
                     var var10001: class_1937
                     run label387@{
                        var115 = class_3499()
                        var169 = var115 as StructureTemplateFillFromVoxelSet
                        var10001 = level as class_1937
                        var185 = var88
                        val var10003: ServerShip = var84.fromShip
                        if (var10003 != null) {
                           val var54: java.util.List = CollectionsKt.listOf(var10003)
                           var188 = var54
                           if (var54 != null) {
                              return@label387
                           }
                        }

                        var188 = CollectionsKt.emptyList()
                     }

                     var169.vs$fillFromVoxelSet(
                        var10001, var185, var188, ShipAssembler.SingleItemMap<>(var92, var104, Vector3d(), null, 8, null), var84.minB, var84.maxB
                     )

                     for (var123 in var88) {
                        val itx: class_2586 = level.method_8321(var123)
                        if (itx != null) {
                           if (itx is class_3829) {
                              class_3829.method_16825(itx)
                           } else {
                              itx.method_58690(class_2487(), level.method_30349() as class_7874)
                           }

                           level.method_8544(var123)
                        }

                        level.method_8652(var123, class_2246.field_10124.method_9564(), var112)
                     }

                     for (var124 in var88) {
                        level.method_14178().method_17293().method_15513(var124)
                     }

                     val var120: class_3492 = class_3492()
                        .method_16184(ShipAssembler.ICopyableProcessor(ShipAssembler.SingleItemMap<>(var92, var84.toShip.getId(), -1L, { it: Long ->
                           it
                        }), ShipAssembler.SingleItemMap<>(var92, Pair(var104, Vector3d(var110)), Pair(Vector3d(), Vector3d()), null, 8, null)))
                        var120.method_15119(var109)
                     VSAssemblyEvents.INSTANCE.onPasteBeforeBlocksAreLoaded
                        .emit(VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded(level, var84.fromShip, var84.toShip, Pair(var104, var110), var96))
                        var115.method_15172(level as class_5425, var109, var109, var120, level.field_9229, 2)
                     val var135: java.lang.Iterable = var88
                     val `destination$iv$ivxx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var88, 10))

                     for (var140 in var135) {
                        `destination$iv$ivxx`.add(
                           class_2338(
                              var109.method_10263() + ((var140 as class_2338).method_10263() - var84.minB.method_10263()),
                              var109.method_10264() + ((var140 as class_2338).method_10264() - var84.minB.method_10264()),
                              var109.method_10260() + ((var140 as class_2338).method_10260() - var84.minB.method_10260())
                           )
                        )
                     }

                     INSTANCE.initSkyLightForShip$valkyrienskies_1_21_1(level, `destination$iv$ivxx` as MutableList<class_2338>)
                  }

                  run label400@{
                     val itx: Vector3d = Vector3d(var84.toShip.inertiaData.getCenterOfMass()).sub(Vector3d(var110))
                     val var172: ServerShip = var84.fromShip
                     if (var172 != null) {
                        val var173: Matrix4dc = var172.getShipToWorld()
                        if (var173 != null) {
                           var174 = var173.transformDirection(itx)
                           if (var174 != null) {
                              return@label400
                           }
                        }
                     }

                     var174 = itx
                  }

                  run label404@{
                     val var175: ServerShip = var84.fromShip
                     if (var175 != null) {
                        val var176: ShipTransform = var175.getTransform()
                        if (var176 != null) {
                           val var177: Vector3dc = var176.getScaling()
                           if (var177 != null) {
                              var178 = var177.x()
                              return@label404
                           }
                        }
                     }

                     var178 = 1.0
                  }

                  var var184: VsiCore
                  var var187: Vector3dc
                  run label407@{
                     val var179: ServerShip = var84.toShip
                     var180 = var179 as VsiServerShip
                     var184 = vsCore
                     val var186: ServerShip = var84.fromShip
                     if (var186 != null) {
                        var187 = var186.getVelocity()
                        if (var187 != null) {
                           return@label407
                        }
                     }

                     var187 = Vector3d()
                  }

                  var var190: Vector3dc
                  run label410@{
                     val var189: ServerShip = var84.fromShip
                     if (var189 != null) {
                        var190 = var189.getAngularVelocity()
                        if (var190 != null) {
                           return@label410
                        }
                     }

                     var190 = Vector3d()
                  }

                  var var192: Vector3d
                  var var10004: VsiCore
                  run label414@{
                     var10004 = vsCore
                     val var10005: ServerShip = var84.fromShip
                     if (var10005 != null) {
                        val var191: Matrix4dc = var10005.getShipToWorld()
                        if (var191 != null) {
                           var192 = var191.transformPosition(Vector3d(var104))
                           if (var192 != null) {
                              return@label414
                           }
                        }
                     }

                     var192 = var104
                  }

                  var var196: Quaterniondc
                  run label418@{
                     var192 = var192.add(var174)
                     var194 = var192
                     val var10006: ServerShip = var84.fromShip
                     if (var10006 != null) {
                        val var195: ShipTransform = var10006.getTransform()
                        if (var195 != null) {
                           var196 = var195.shipToWorldRotation
                           if (var196 != null) {
                              return@label418
                           }
                        }
                     }

                     var196 = Quaterniond()
                  }

                  val var10007: Vector3dc = Vector3d(scale * var178, scale * var178, scale * var178)
                  var180.unsafeSetKinematics(var184.newBodyKinematics(var187, var190, var10004.newBodyTransform(var194, var196, var10007, var110)))
                  var84.toShip.isStatic = false
                  results.add(ShipAssembler.AssembleContext(var84.toShip, var104, var110))
                  if (var84.fromShip is LoadedServerShip && var106) {
                     val var181: SplittingDisablerAttachment = (var84.fromShip as LoadedServerShip).getAttachment(SplittingDisablerAttachment.class)
                     if (var181 != null) {
                        var181.enableSplitting()
                     }
                  }
               }
            }

            val var82: Long = System.currentTimeMillis() - System.currentTimeMillis()
            if (pendingAssemblies.size() > 5) {
               ASSEMBLY_LOGGER.info(
                  "Batch assembly phase 3 (move blocks for ${pendingAssemblies.size()} ships): $var82ms (${var82 / (long)pendingAssemblies.size()}ms/ship)"
               )
            }

            val var89: Int = level.method_8503().method_3780()
            val var183: MinecraftServer = level.method_8503()
            VSGameUtilsKt.executeIf(
               var183,
               { 
                  val `$this$all$iv`: java.lang.Iterable = `$destChunkPoses`
                  var var10000: Boolean
                  if (`$destChunkPoses` is java.util.Collection && (`$destChunkPoses` as java.util.Collection).isEmpty()) {
                     var10000 = true
                  } else {
                     val var5: java.util.Iterator = `$this$all$iv`.iterator()

                     while (true) {
                        if (!var5.hasNext()) {
                           var10000 = true
                           break
                        }

                        if (!VSGameUtilsKt.isChunkLoadedForVS(`$level` as class_1937, var5.next() as class_1923)) {
                           var10000 = false
                           break
                        }
                     }
                  }

                  var10000 || `$level`.method_8503().method_3780() - `$timeAtExecution` > 60
               },
               { 
                  if (`$level`.method_8503().method_3780() - `$timeAtExecution` > 60) {
                     ASSEMBLY_LOGGER.warn("Batch assembly: timed out waiting for ${`$destChunkPoses`.size()} chunks")
                  }

                  val var10000: java.util.List = `$level`.method_18456()

                  for (chunkSections in var10000) {
                     val sectionY: class_3222 = chunkSections as class_3222
                     val sectionPos: SimplePacketNetworking = vsCore.simplePacketNetworking
                     val var10002: MinecraftPlayer = playerWrapper
                     PacketStopChunkUpdatesKt.sendRestartChunkUpdates(sectionPos, `$allChunkPosesJOML`, var10002)
                  }

                  if (VSCoreConfig.SERVER.sp.enableConnectivity) {
                     val var21: java.util.Iterator = `$destChunkPoses`.iterator()
                     val var13: java.util.Iterator = var21

                     while (var13.hasNext()) {
                        val var22: Any = var13.next()
                        val var14: class_1923 = var22 as class_1923
                        val var23: class_2818 = `$level`.method_8497((var22 as class_1923).field_9181, (var22 as class_1923).field_9180)
                        if (var23 != null) {
                           val var15: class_2818 = var23
                           val var24: Array<class_2826> = var23.method_12006()
                           if (var24 != null) {
                              val var16: Array<class_2826> = var24
                              var var17: Int = 0

                              for (var18 in var23.method_32890()..var17) {
                                 val var19: Vector3i = Vector3i(var14.field_9181, var15.method_31604(var17), var14.field_9180)
                                 val var25: class_2826 = var16[var17]
                                 if (var16[var17] != null && !var16[var17].method_38292()) {
                                    shipObjectWorld
                                       .forceUpdateConnectivityChunk(dimensionId, var19.x, var19.y, var19.z, VSGameUtilsKt.toDenseVoxelUpdate(var25, var19))
                                    }
                              }
                           }
                        }
                     }
                  }
               }
            )
            return results
         }
      }
   }

   public fun isValidShipBlock(state: class_2680?): Boolean {
      if (state == null) {
         return false
      } else if (state.method_26215()) {
         return false
      } else {
         return !VSGameUtilsKt.inAssemblyBlacklist(state)
      }
   }

   public fun deleteShip(level: class_3218, ship: ServerShip, deleteBlocks: Boolean, dropBlocks: Boolean): Int {
      if (ship is LoadedServerShip) {
         val aabb: SplittingDisablerAttachment = (ship as LoadedServerShip).getAttachment(SplittingDisablerAttachment.class)
         if (aabb != null) {
            aabb.disableSplitting()
         }
      }

      if (deleteBlocks) {
         val var10000: AABBic = ship.getShipAABB()
         if (var10000 == null) {
            return 0
         }

         VSGameUtilsKt.forEach(var10000, { x: Int, y: Int, z: Int ->
            if (`$dropBlocks`) {
               `$level`.method_22352(class_2338(x, y, z), true)
            } else {
               `$level`.method_8652(class_2338(x, y, z), class_2246.field_10124.method_9564(), 2)
            }

            Unit.INSTANCE
         })
      }

      vsCore.deleteShips(shipObjectWorld, CollectionsKt.listOf(ship))
      return 1
   }

   internal fun initSkyLightForShip(level: class_3218, destPositions: List<class_2338>) {
      if (!destPositions.isEmpty()) {
         val lightEngine: class_3227 = level.method_14178().method_17293()
         val pos: java.lang.Iterable = destPositions
         val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(destPositions, 10))

         for (`item$iv$iv` in pos) {
            `destination$iv$iv`.add(class_4076.method_18682(`item$iv$iv` as class_2338))
         }

         for (var17 in CollectionsKt.distinct(`destination$iv$iv` as java.util.List)) {
            lightEngine.method_15551(var17, false)
         }

         val var16: HashSet = HashSet()

         for (var19 in destPositions) {
            if (var16.add(var19)) {
               lightEngine.method_15513(var19)
            }

            for (var21 in ShipAssembler.EntriesMappings.entries$0) {
               val var22: class_2338 = var19.method_10093(var21)
               if (var16.add(var22)) {
                  lightEngine.method_15513(var22)
               }
            }
         }
      }
   }

   public data class AssembleContext(ship: ServerShip, fromCenter: Vector3d, toCenter: Vector3d) {
      public final val ship: ServerShip
      public final val fromCenter: Vector3d
      public final val toCenter: Vector3d

      init {
         this.ship = ship
         this.fromCenter = fromCenter
         this.toCenter = toCenter
      }

      public operator fun component1(): ServerShip {
         return this.ship
      }

      public operator fun component2(): Vector3d {
         return this.fromCenter
      }

      public operator fun component3(): Vector3d {
         return this.toCenter
      }

      public fun copy(ship: ServerShip = this.ship, fromCenter: Vector3d = this.fromCenter, toCenter: Vector3d = this.toCenter): org.valkyrienskies.mod.common.assembly.ShipAssembler.AssembleContext {
         return ShipAssembler.AssembleContext(ship, fromCenter, toCenter)
      }

      public override fun toString(): String {
         return "AssembleContext(ship=${this.ship}, fromCenter=${this.fromCenter}, toCenter=${this.toCenter})"
      }

      public override fun hashCode(): Int {
         return (this.ship.hashCode() * 31 + this.fromCenter.hashCode()) * 31 + this.toCenter.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is ShipAssembler.AssembleContext
               && this.ship == (other as ShipAssembler.AssembleContext).ship
               && this.fromCenter == (other as ShipAssembler.AssembleContext).fromCenter
               && this.toCenter == (other as ShipAssembler.AssembleContext).toCenter
            }
      }
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class EntriesMappings {
      @JvmStatic
      @JvmSynthetic
      public EnumEntries<class_2350> entries$0 = EnumEntriesKt.enumEntries(class_2350.values() as Array<java.lang.Enum>);
   }

   public class ICopyableProcessor(oldShipIdToNewShipId: Map<Long, Long>, centerPositions: Map<Long, Pair<Vector3d, Vector3d>>) : class_3491 {
      public final val oldShipIdToNewShipId: Map<Long, Long>
      public final val centerPositions: Map<Long, Pair<Vector3d, Vector3d>>

      init {
         this.oldShipIdToNewShipId = oldShipIdToNewShipId
         this.centerPositions = centerPositions
      }

      fun method_15110(
         levelReader: class_4538,
         oldBPos: class_2338,
         newBPos: class_2338,
         oldStructureBlockInfo: class_3501,
         newStructureBlockInfo: class_3501,
         structurePlaceSettings: class_3492
      ): class_3501? {
         val block: class_2248 = newStructureBlockInfo.comp_1342().method_26204()
         if (block !is ICopyableBlock) {
            newStructureBlockInfo
         } else {
            val var10000: ICopyableBlock = block as ICopyableBlock
            val var10001: class_3218 = (levelReader as class_5425).method_8410()
            val var10003: class_2680 = newStructureBlockInfo.comp_1342()
            var10000.onPaste(var10001, newBPos, var10003, this.oldShipIdToNewShipId, this.centerPositions, newStructureBlockInfo.comp_1343())
            newStructureBlockInfo
         }
      }

      fun method_16772(): class_3828<*>? {
         null
      }
   }

   public data class MoveContext(wasSuccessful: Boolean, fromCenter: Vector3d, toCenter: Vector3d) {
      public final val wasSuccessful: Boolean
      public final val fromCenter: Vector3d
      public final val toCenter: Vector3d

      init {
         this.wasSuccessful = wasSuccessful
         this.fromCenter = fromCenter
         this.toCenter = toCenter
      }

      public operator fun component1(): Boolean {
         return this.wasSuccessful
      }

      public operator fun component2(): Vector3d {
         return this.fromCenter
      }

      public operator fun component3(): Vector3d {
         return this.toCenter
      }

      public fun copy(wasSuccessful: Boolean = this.wasSuccessful, fromCenter: Vector3d = this.fromCenter, toCenter: Vector3d = this.toCenter): org.valkyrienskies.mod.common.assembly.ShipAssembler.MoveContext {
         return ShipAssembler.MoveContext(wasSuccessful, fromCenter, toCenter)
      }

      public override fun toString(): String {
         return "MoveContext(wasSuccessful=${this.wasSuccessful}, fromCenter=${this.fromCenter}, toCenter=${this.toCenter})"
      }

      public override fun hashCode(): Int {
         return (java.lang.Boolean.hashCode(this.wasSuccessful) * 31 + this.fromCenter.hashCode()) * 31 + this.toCenter.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is ShipAssembler.MoveContext
               && this.wasSuccessful == (other as ShipAssembler.MoveContext).wasSuccessful
               && this.fromCenter == (other as ShipAssembler.MoveContext).fromCenter
               && this.toCenter == (other as ShipAssembler.MoveContext).toCenter
            }
      }
   }

   public class SingleItemMap<K, V>(mkey: Any, mvalue: Any, default: Any, defaultFn: ((Any) -> Any)? = null) : java.util.Map<K, V>, KMappedMarker {
      public final val mkey: Any
      public final val mvalue: Any
      public final val default: Any
      public final val defaultFn: ((Any) -> Any)?
      public open val size: Int
      public open val keys: Set<Any>
      public open val values: Collection<Any>
      public open val entries: Set<kotlin.collections.Map.Entry<Any, Any>>

      init {
         this.mkey = (K)mkey
         this.mvalue = (V)mvalue
         this.default = (V)defaultx
         this.defaultFn = defaultFn
         this.size = 1
         this.keys = SetsKt.setOf(this.mkey)
         this.values = SetsKt.setOf(this.mvalue)
         this.entries = SetsKt.setOf(object : kotlin.collections.Map.Entry<Any, Any> {
            public open val key: Any
            public open val value: Any

            {
               this.key = `$receiver`.mkey
               this.value = `$receiver`.mvalue
            }

            override fun setValue(newValue: V): V {
               throw UnsupportedOperationException("Operation is not supported for read-only collection")
            }
         })
      }

      public override fun isEmpty(): Boolean {
         return false
      }

      public override fun containsKey(key: Any): Boolean {
         return true
      }

      public override fun containsValue(value: Any): Boolean {
         return true
      }

      public override operator fun get(key: Any): Any? {
         var var10000: Any
         if (key == this.mkey) {
            var10000 = this.mvalue
         } else {
            if (this.defaultFn != null) {
               var10000 = this.defaultFn(key)
               if (var10000 != null) {
                  return (V)var10000
               }
            }

            var10000 = this.default
         }

         return (V)var10000
      }

      override fun clear() {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun put(key: K, value: V): V {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun putAll(from: MutableMap<K, V>) {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun remove(key: Any): V {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun remove(key: Any, value: Any): Boolean {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun replaceAll(p0: BiFunction<in K, in V, out V>) {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun putIfAbsent(p0: K, p1: V): V {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun replace(p0: K, p1: V, p2: V): Boolean {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun replace(p0: K, p1: V): V {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun computeIfAbsent(p0: K, p1: Function<in K, out V>): V {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun computeIfPresent(p0: K, p1: BiFunction<in K, in V, out V>): V {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun compute(p0: K, p1: BiFunction<in K, in V, out V>): V {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      override fun merge(p0: K, p1: V, p2: BiFunction<in V, in V, out V>): V {
         throw UnsupportedOperationException("Operation is not supported for read-only collection")
      }

      // $VF: bridge methodSupplier
      override final fun size(): Int {
         this.size
      }

      // $VF: bridge methodSupplier
      override final fun keySet(): MutableSet<K> {
         this.keys
      }

      // $VF: bridge methodSupplier
      override final fun values(): MutableCollection<V> {
         this.values
      }

      // $VF: bridge methodSupplier
      override final fun entrySet(): MutableSet<MutableMap.MutableEntry<K, V>> {
         this.entries
      }
   }
}
