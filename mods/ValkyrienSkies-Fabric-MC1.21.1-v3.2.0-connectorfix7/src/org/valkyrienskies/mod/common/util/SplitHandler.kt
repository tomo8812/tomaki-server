package org.valkyrienskies.mod.common.util

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2382
import net.minecraft.class_3218
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.mod.api.ValkyrienSkies
import org.valkyrienskies.mod.common.assembly.ShipAssembler
import org.valkyrienskies.mod.common.config.VSGameConfig
import org.valkyrienskies.mod.util.ClassLogger

@SourceDebugExtension(["SMAP\nSplitHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SplitHandler.kt\norg/valkyrienskies/mod/common/util/SplitHandler\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 AttachmentHolder.kt\norg/valkyrienskies/core/api/attachment/AttachmentHolderKt\n*L\n1#1,205:1\n1863#2,2:206\n1557#2:209\n1628#2,3:210\n66#3:208\n*S KotlinDebug\n*F\n+ 1 SplitHandler.kt\norg/valkyrienskies/mod/common/util/SplitHandler\n*L\n45#1:206,2\n132#1:209\n132#1:210,3\n55#1:208\n*E\n"])
public class SplitHandler(doEdges: Boolean, doCorners: Boolean) {
   private final val doEdges: Boolean
   private final val doCorners: Boolean
   private final val splitQueue: HashMap<String, HashMap<Long, Int>>

   init {
      this.doEdges = doEdges
      this.doCorners = doCorners
      this.splitQueue = HashMap<>()
   }

   public fun queueSplit(level: class_1937, shipId: Long?) {
      val var10000: HashMap = this.splitQueue.get(dimensionId)
      if (var10000 == null || var10000.put(shipId ?: -1L, VSGameConfig.SERVER.defaultSplitGraceTimer) == null) {
         this.splitQueue.put(dimensionId, MapsKt.hashMapOf(arrayOf(TuplesKt.to(shipId ?: -1L, VSGameConfig.SERVER.defaultSplitGraceTimer))))
      }
   }

   public fun tick(level: class_3218) {
      if (this.splitQueue.get(dimensionId) != null) {
         var var10000: HashMap = this.splitQueue.get(dimensionId)
         if (!var10000.isEmpty()) {
            val splitsToProcess: HashSet = HashSet()
            var10000 = this.splitQueue.get(dimensionId)

            for (var10000 in (var10000 as HashMap).keySet()) {
               val `$i$f$forEach`: Long = (var10000 as java.lang.Number).longValue()
               var10000 = this.splitQueue.get(dimensionId)
               var10000 = (var10000 as HashMap).get(`$i$f$forEach`)
               if ((var10000 as java.lang.Number).intValue() <= 0) {
                  splitsToProcess.add(`$i$f$forEach`)
               } else {
                  val it: java.lang.Long = `$i$f$forEach`
                  var10000 = this.splitQueue.get(dimensionId)
                  var10000 = var10000 as java.util.Map
                  var var10002: Any = this.splitQueue.get(dimensionId)
                  var10002 = (var10002 as HashMap).get(`$i$f$forEach`)
                  var10000.put(it, (var10002 as java.lang.Number).intValue() - 1)
               }
            }

            for (`element$iv` in splitsToProcess) {
               val var12: Long = (`element$iv` as java.lang.Number).longValue()
               var10000 = this.splitQueue.get(dimensionId)
               var10000.remove(var12)
               split$default(this, level as class_1937, var12, null, 4, null)
            }
         }
      }
   }

   public fun split(level: class_1937, shipId: Long, after: Consumer<ServerShip>? = null) {
      if (level is class_3218) {
         var loadedShip: LoadedServerShip
         run label178@{
            loadedShip = shipObjectWorld.loadedShips.getById(shipId)
            if (loadedShip != null) {
               val var10000: SplittingDisablerAttachment = loadedShip.getAttachment(SplittingDisablerAttachment.class)
               if (var10000 == null || var10000.canSplit()) {
                  return@label178
               }
            }

            if (loadedShip != null || !VSGameConfig.SERVER.enableWorldSplitting) {
               return
            }
         }

         val var30: HashSet = HashSet
         val var10002: VsiServerShipWorld = shipObjectWorld
         if (loadedShip == null) {
            return
         }

         val var10003: java.lang.String = loadedShip.getChunkClaimDimension()
         if (var10003 == null) {
            return
         }

         val var10004: ChunkClaim = loadedShip.getChunkClaim()
         if (var10004 == null) {
            return
         }

         var30./* $VF: Unable to resugar constructor */<init>(var10002.getAllSolidComponentsFromClaim(var10003, var10004))
         if (!var30.isEmpty()) {
            val var31: Any = CollectionsKt.first(var30)
            var var16: class_2338 = ValkyrienSkies.toBlockPos(var31 as Vector3ic)
            var var17: Long = -1L
            val var32: java.util.Iterator = var30.iterator()
            val disconnected: java.util.Iterator = var32

            while (disconnected.hasNext()) {
               val toIgnore: Vector3ic = disconnected.next() as Vector3ic
               if (shipObjectWorld.isIsolatedSolid(toIgnore.x(), toIgnore.y(), toIgnore.z(), dimensionId) === ConnectionStatus.DISCONNECTED) {
                  val toAssemble: Long = shipObjectWorld.getSolidComponentSize(toIgnore.x(), toIgnore.y(), toIgnore.z(), dimensionId)
                  if (toAssemble > var17) {
                     var16 = ValkyrienSkies.toBlockPos(toIgnore)
                     var17 = toAssemble
                  }
               }
            }

            if (var17 == -1L) {
               return
            }

            var30.remove(VectorConversionsMCKt.toJOML(var16 as class_2382))
            val var18: HashSet = HashSet()
            val var33: java.util.Iterator = var30.iterator()
            val var19: java.util.Iterator = var33

            while (var19.hasNext()) {
               val var21: Vector3ic = var19.next() as Vector3ic
               if (shipObjectWorld.isIsolatedSolid(var21.x(), var21.y(), var21.z(), dimensionId) === ConnectionStatus.DISCONNECTED
                  && !(var21 == var16)
                  && shipObjectWorld
                     .isConnectedBySolid(var16.method_10263(), var16.method_10264(), var16.method_10260(), var21.x(), var21.y(), var21.z(), dimensionId) === ConnectionStatus.DISCONNECTED
                  )
                {
                  var18.add(ValkyrienSkies.toBlockPos(var21))
               }
            }

            val var20: HashSet = HashSet()
            val var34: java.util.Iterator = var18.iterator()
            val var22: java.util.Iterator = var34

            while (var22.hasNext()) {
               val var35: Any = var22.next()
               val component: class_2338 = var35 as class_2338
               var var36: class_2338 = var18.iterator()
               val componentx: java.util.Iterator = var36

               while (componentx.hasNext()) {
                  var36 = (class_2338)componentx.next()
                  val newShip: class_2338 = var36
                  if (!(component == var36)) {
                     if (shipObjectWorld
                           .isConnectedBySolid(
                              component.method_10263(),
                              component.method_10264(),
                              component.method_10260(),
                              newShip.method_10263(),
                              newShip.method_10264(),
                              newShip.method_10260(),
                              dimensionId
                           ) === ConnectionStatus.CONNECTED
                        && !var20.contains(newShip)
                        && !var20.contains(component)) {
                        var20.add(component)
                     }

                     if (shipObjectWorld.isIsolatedSolid(newShip.method_10263(), newShip.method_10264(), newShip.method_10260(), dimensionId) === ConnectionStatus.CONNECTED
                        && !var20.contains(newShip)
                        && !var20.contains(component)) {
                        var20.add(component)
                     }
                  }
               }
            }

            var18.removeAll(var20)
            if (var18.isEmpty()) {
               return
            }

            val var38: SplittingDisablerAttachment = loadedShip.getAttachment(SplittingDisablerAttachment.class)
            if (var38 != null) {
               var38.disableSplitting()
            }

            val var23: HashSet = HashSet()
            val var39: java.util.Iterator = var18.iterator()
            var var24: java.util.Iterator = var39

            while (var24.hasNext()) {
               val var40: Any = var24.next()
               val var26: class_2338 = var40 as class_2338
               if (shipObjectWorld
                  .isIsolatedSolid(
                     (var40 as class_2338).method_10263(), (var40 as class_2338).method_10264(), (var40 as class_2338).method_10260(), dimensionId
                  ) === ConnectionStatus.DISCONNECTED) {
                  var23.add(
                     split$getAllBlocksFromSparseVoxelSet(
                        shipObjectWorld.indexSolidComponentVoxels(var26.method_10263(), var26.method_10264(), var26.method_10260(), dimensionId)
                     )
                  )
               }
            }

            if (var23.isEmpty()) {
               val var44: SplittingDisablerAttachment = loadedShip.getAttachment(SplittingDisablerAttachment.class)
               if (var44 != null) {
                  var44.enableSplitting()
               }

               return
            }

            val var41: java.util.Iterator = var23.iterator()
            var24 = var41

            while (var24.hasNext()) {
               val var42: Any = var24.next()
               val var27: java.util.Set = var42 as java.util.Set
               if (!(var42 as java.util.Set).isEmpty()) {
                  val var29: ServerShip = ShipAssembler.assembleToShip(level as class_3218, var27, 1.0)
                  if (after != null) {
                     after.accept(var29)
                  }
               }
            }

            val var43: SplittingDisablerAttachment = loadedShip.getAttachment(SplittingDisablerAttachment.class)
            if (var43 != null) {
               var43.enableSplitting()
            }
         }
      }
   }

   @JvmStatic
   fun `split$getAllBlocksFromSparseVoxelSet`(set: MutableSet<SparseVoxelPosition>): MutableSet<class_2338> {
      val blocks: HashSet = HashSet()

      for (voxel in set) {
         val `$this$map$iv`: java.lang.Iterable = voxel.getContaining()
         val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10))

         for (`item$iv$iv` in `$this$map$iv`) {
            `destination$iv$iv`.add(ValkyrienSkies.toBlockPos(`item$iv$iv` as Vector3ic))
         }

         blocks.addAll(`destination$iv$iv` as java.util.List)
      }

      blocks as java.util.Set
   }

   public companion object {
      public final val SPLITLOGGER: ClassLogger
      public final val offsetsToCheck: ArrayList<class_2382>

      public fun getOffsets(doEdges: Boolean, doCorners: Boolean): ArrayList<class_2382> {
         val list: ArrayList = ArrayList<>(this.offsetsToCheck)
         if (doEdges) {
            list.add(class_2382(1, 1, 0))
            list.add(class_2382(1, -1, 0))
            list.add(class_2382(-1, 1, 0))
            list.add(class_2382(-1, -1, 0))
            list.add(class_2382(1, 0, 1))
            list.add(class_2382(1, 0, -1))
            list.add(class_2382(-1, 0, 1))
            list.add(class_2382(-1, 0, -1))
            list.add(class_2382(0, 1, 1))
            list.add(class_2382(0, 1, -1))
            list.add(class_2382(0, -1, 1))
            list.add(class_2382(0, -1, -1))
         }

         if (doCorners) {
            list.add(class_2382(1, 1, 1))
            list.add(class_2382(1, 1, -1))
            list.add(class_2382(1, -1, 1))
            list.add(class_2382(1, -1, -1))
            list.add(class_2382(-1, 1, 1))
            list.add(class_2382(-1, 1, -1))
            list.add(class_2382(-1, -1, 1))
            list.add(class_2382(-1, -1, -1))
         }

         return list
      }
   }
}
