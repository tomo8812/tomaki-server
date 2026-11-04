package org.valkyrienskies.mod.common

import com.mojang.serialization.Lifecycle
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.util.NoSuchElementException
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1937
import net.minecraft.class_2246
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2370
import net.minecraft.class_2378
import net.minecraft.class_2680
import net.minecraft.class_2960
import net.minecraft.class_3218
import net.minecraft.class_5321
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.Wing
import org.valkyrienskies.core.api.ships.WingManager
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition
import org.valkyrienskies.core.internal.world.VsiShipWorld
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.mod.common.block.WingBlock
import org.valkyrienskies.mod.common.config.MassDatapackResolver
import org.valkyrienskies.mod.common.config.VSGameConfig
import org.valkyrienskies.mod.common.hooks.VSGameEvents
import org.valkyrienskies.mod.common.util.BuoyancyHandlerAttachment
import org.valkyrienskies.mod.common.util.SplitHandler

@SourceDebugExtension(["SMAP\nBlockStateInfoProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BlockStateInfoProvider.kt\norg/valkyrienskies/mod/common/BlockStateInfo\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,300:1\n1062#2:301\n1863#2,2:303\n1863#2,2:305\n1062#2:307\n1755#2,3:308\n1#3:302\n*S KotlinDebug\n*F\n+ 1 BlockStateInfoProvider.kt\norg/valkyrienskies/mod/common/BlockStateInfo\n*L\n63#1:301\n258#1:303,2\n283#1:305,2\n64#1:307\n67#1:308,3\n*E\n"])
public object BlockStateInfo {
   public final val REGISTRY: class_2370<BlockStateInfoProvider> =
      class_2370(class_5321.method_29180(class_2960.method_60655("valkyrienskies", "blockstate_providers")), Lifecycle.experimental())
      private final lateinit var SORTED_REGISTRY: List<BlockStateInfoProvider>

   private final var _cache: ThreadLocal<org.valkyrienskies.mod.common.BlockStateInfo.Cache> = ThreadLocal.withInitial({ 
      BlockStateInfo.Cache()
   })

   @JvmStatic
   public fun isSortedRegistryInitialized(): Boolean {
      return SORTED_REGISTRY != null
   }

   public fun init() {
      class_2378.method_10230(REGISTRY as class_2378, class_2960.method_60655("valkyrienskies", "data"), MassDatapackResolver.INSTANCE)
      class_2378.method_10230(REGISTRY as class_2378, class_2960.method_60655("valkyrienskies", "default"), DefaultBlockStateInfoProvider.INSTANCE)
      SORTED_REGISTRY = CollectionsKt.sortedWith(REGISTRY as java.lang.Iterable, BlockStateInfo$init$$inlined$sortedByDescending$1())
      VSGameEvents.INSTANCE.registriesCompleted.on({ var0: Unit, var1: RegisteredListener ->
         SORTED_REGISTRY = CollectionsKt.sortedWith(REGISTRY as java.lang.Iterable, BlockStateInfo$init$lambda$2$$inlined$sortedByDescending$1())
      })
      VSGameEvents.INSTANCE.configUpdated.on({ p0: Any ->
         `$tmp0`(p0)
      })
   }

   public final val cache: org.valkyrienskies.mod.common.BlockStateInfo.Cache
      public final get() {
         val var10000: Any = _cache.get()
         return var10000 as BlockStateInfo.Cache
      }


   private fun invalidateCache() {
      _cache = ThreadLocal.withInitial({ 
         BlockStateInfo.Cache()
      })
   }

   public fun get(blockState: class_2680): Pair<Double, VsiBlockType>? {
      return this.cache.get(blockState)
   }

   private fun iterateRegistry(blockState: class_2680): Pair<Double, VsiBlockType> {
      var var10000: java.util.List = SORTED_REGISTRY
      if (SORTED_REGISTRY == null) {
         Intrinsics.throwUninitializedPropertyAccessException("SORTED_REGISTRY")
         var10000 = null
      }

      var var3: java.util.Iterator = var10000.iterator()

      do {
         if (!var3.hasNext()) {
            var12 = null
            break
         }

         var12 = (var3.next() as BlockStateInfoProvider).getBlockStateMass(blockState)
      } while (var12 == null)

      if (var12 == null) {
         throw NoSuchElementException("No element of the collection was transformed to a non-null value.")
      } else {
         var var10001: java.util.List = SORTED_REGISTRY
         if (SORTED_REGISTRY == null) {
            Intrinsics.throwUninitializedPropertyAccessException("SORTED_REGISTRY")
            var10001 = null
         }

         var3 = var10001.iterator()

         do {
            if (!var3.hasNext()) {
               var13 = null
               break
            }

            var13 = (var3.next() as BlockStateInfoProvider).getBlockStateType(blockState)
         } while (var13 == null)

         if (var13 == null) {
            throw NoSuchElementException("No element of the collection was transformed to a non-null value.")
         } else {
            return Pair(var12, var13)
         }
      }
   }

   public fun onSetBlock(level: class_1937, blockPos: class_2338, prevBlockState: class_2680, newBlockState: class_2680) {
      this.onSetBlock(level, blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260(), prevBlockState, newBlockState)
   }

   public fun onSetBlock(level: class_1937, x: Int, y: Int, z: Int, prevBlockState: class_2680, newBlockState: class_2680) {
      if (SORTED_REGISTRY != null) {
         val shipObjectWorld: VsiShipWorld = shipObjectWorld
         var var10000: Pair = this.get(prevBlockState)
         if (var10000 != null) {
            val prevBlockMass: Double = (var10000.component1() as java.lang.Number).doubleValue()
            val prevBlockType: VsiBlockType = var10000.component2() as VsiBlockType
            var10000 = this.get(newBlockState)
            if (var10000 != null) {
               val newBlockMass: Double = (var10000.component1() as java.lang.Number).doubleValue()
               val newBlockType: VsiBlockType = var10000.component2() as VsiBlockType
               if (level is class_3218) {
                  val loadedShip: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, x shr 4, z shr 4)
                  if (loadedShip != null) {
                     val var35: WingManager = loadedShip.wingManager
                     val dimension: Boolean = prevBlockState.method_26204() is WingBlock
                     val allComponentsInClaim: class_2248 = newBlockState.method_26204()
                     val newTotal: Wing = if (allComponentsInClaim is WingBlock)
                        (allComponentsInClaim as WingBlock).getWing(level, class_2338(x, y, z), newBlockState)
                        else
                        null
                        if (newTotal != null) {
                        var35.setWing(var35.getFirstWingGroupId(), x, y, z, newTotal)
                     } else if (dimension) {
                        var35.setWing(var35.getFirstWingGroupId(), x, y, z, null)
                     }
                  }
               }

               shipObjectWorld.onSetBlock(x, y, z, dimensionId, prevBlockType, newBlockType, prevBlockMass, newBlockMass)
               if (level is class_3218) {
                  val var29: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, x shr 4, z shr 4)
                  if (var29 != null && VSGameConfig.SERVER.enablePocketBuoyancy) {
                     val var30: BuoyancyHandlerAttachment = var29.getAttachment(BuoyancyHandlerAttachment.class)
                     val var31: java.lang.String = var29.getChunkClaimDimension()
                     val var32: java.util.Set = shipObjectWorld.getAllAirComponentsFromClaim(var31, var29.getChunkClaim())
                     var var33: Double = 0.0
                     val centerSum: Vector3d = Vector3d(0.0, 0.0, 0.0)
                     if (!var32.isEmpty()) {
                        for (component in var32) {
                           if (shipObjectWorld.isIsolatedAir(component.x(), component.y(), component.z(), var31) === ConnectionStatus.DISCONNECTED) {
                              val componentSize: Long = shipObjectWorld.getAirComponentSize(component.x(), component.y(), component.z(), var31)
                              val componentCenter: Vector3dc = onSetBlock$centerFromVoxelSet(
                                 shipObjectWorld.indexAirComponentVoxels(component.x(), component.y(), component.z(), var31)
                              )
                              var33 += componentSize
                              centerSum.add(
                                 componentCenter.x() * (double)componentSize,
                                 componentCenter.y() * (double)componentSize,
                                 componentCenter.z() * (double)componentSize
                              )
                           }
                        }
                     }

                     if (var33 > 0.0) {
                        centerSum.div(var33)
                     } else {
                        centerSum.set(0.0, 0.0, 0.0)
                     }

                     if (var30 != null) {
                        val var36: BuoyancyHandlerAttachment.BuoyancyData = var30.buoyancyData
                        if (var36 != null) {
                           var36.pocketVolumeTotal = var33
                        }
                     }

                     val var37: AABBic = var29.getShipAABB()
                     if ((var37 == null || var37.containsPoint((float)centerSum.x(), (float)centerSum.y(), (float)centerSum.z())) && var30 != null) {
                        val var38: BuoyancyHandlerAttachment.BuoyancyData = var30.buoyancyData
                        if (var38 != null) {
                           var38.pocketCenterAverage = centerSum as Vector3dc
                        }
                     }
                  }

                  if (vsCore.hooks.getEnableConnectivity()) {
                     val var39: SplitHandler = splitHandler
                     val var10002: ServerShip = VSGameUtilsKt.getShipManagingPos(level as class_3218, (double)x, (double)y, (double)z)
                     var39.queueSplit(level, if (var10002 != null) var10002.getId() else null)
                  }
               }
            }
         }
      }
   }

   public fun remassShip(level: class_1937, ship: Ship): Boolean {
      if (level !is class_3218) {
         return false
      } else if (ship !is LoadedServerShip) {
         return false
      } else if (SORTED_REGISTRY == null) {
         return false
      } else {
         val aabb: AABBic = ship.shipAABB
         if (aabb == null) {
            return false
         } else {
            val wasStatic: Boolean = (ship as LoadedServerShip).isStatic()
            (ship as LoadedServerShip).setStatic(true)
            val var10001: class_2680 = class_2246.field_10124.method_9564()
            var var10000: Pair = this.get(var10001)
            if (var10000 == null) {
               return false
            } else {
               val airBlockMass: Double = (var10000.component1() as java.lang.Number).doubleValue()
               val airBlockType: VsiBlockType = var10000.component2() as VsiBlockType
               val var30: java.lang.Iterable = class_2338.method_10094(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ())

               for (`element$iv` in var30) {
                  val it: class_2338 = `element$iv` as class_2338
                  val state: class_2680 = (level as class_3218).method_8320(`element$iv` as class_2338)
                  val var31: BlockStateInfo = INSTANCE
                  var10000 = var31.get(state)
                  if (var10000 == null) {
                     return false
                  }

                  val realBlockMass: Double = (var10000.component1() as java.lang.Number).doubleValue()
                  val realBlockType: VsiBlockType = var10000.component2() as VsiBlockType
                  if (!(realBlockType == airBlockType)) {
                     shipObjectWorld.onSetBlock(it.method_10263(), it.method_10264(), it.method_10260(), dimensionId, realBlockType, airBlockType, 0.0, 0.0)
                  }
               }

               shipObjectWorld
                  .onSetBlock(
                     aabb.minX(), aabb.minY(), aabb.minZ(), dimensionId, airBlockType, airBlockType, (ship as LoadedServerShip).getInertiaData().getMass(), 0.0
                  )
                  val var33: java.lang.Iterable = class_2338.method_10094(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ())

               for (var23 in var33) {
                  val var24: class_2338 = var23 as class_2338
                  val var26: class_2680 = (level as class_3218).method_8320(var23 as class_2338)
                  val var34: BlockStateInfo = INSTANCE
                  var10000 = var34.get(var26)
                  if (var10000 == null) {
                     return false
                  }

                  val var28: Double = (var10000.component1() as java.lang.Number).doubleValue()
                  val var29: VsiBlockType = var10000.component2() as VsiBlockType
                  if (!(var29 == airBlockType)) {
                     shipObjectWorld.onSetBlock(var24.method_10263(), var24.method_10264(), var24.method_10260(), dimensionId, airBlockType, var29, 0.0, var28)
                  }
               }

               (ship as LoadedServerShip).setStatic(wasStatic)
               return true
            }
         }
      }
   }

   @JvmStatic
   fun MutableSet<SparseVoxelPosition>.`onSetBlock$centerFromVoxelSet`(): Vector3dc {
      val center: Vector3d = Vector3d(0.0, 0.0, 0.0)
      if (`$this$onSetBlock_u24centerFromVoxelSet`.isEmpty()) {
         center as Vector3dc
      } else {
         for (voxel in `$this$onSetBlock_u24centerFromVoxelSet`) {
            center.add(
               (double)voxel.x + (double)(voxel.extent - 1L) / 2.0 + 0.5,
               (double)voxel.y + (double)(voxel.extent - 1L) / 2.0 + 0.5,
               (double)voxel.z + (double)(voxel.extent - 1L) / 2.0 + 0.5
            )
         }

         center.div((double)`$this$onSetBlock_u24centerFromVoxelSet`.size())
         center as Vector3dc
      }
   }

   public class Cache {
      private final val blockStateCache: Int2ObjectOpenHashMap<Pair<Double, VsiBlockType>> = Int2ObjectOpenHashMap(2048, 0.5F)

      public fun get(blockState: class_2680): Pair<Double, VsiBlockType>? {
         val blockId: Int = class_2248.method_9507(blockState)
         return if (blockId == -1) null else this.blockStateCache.computeIfAbsent(blockId, { it: Int ->
            BlockStateInfo.INSTANCE.iterateRegistry(`$blockState`)
         }) as Pair
      }
   }
}
