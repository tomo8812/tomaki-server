package org.valkyrienskies.mod.util

import java.util.ArrayList
import net.minecraft.class_1923
import net.minecraft.class_1937
import net.minecraft.class_2818
import net.minecraft.class_2826
import net.minecraft.class_310
import net.minecraft.class_638
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.internal.world.VsiClientShipWorld
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.config.VSGameConfig

public object ClientConnectivityUpdateQueue {
   public final val toInitialize: ArrayDeque<Pair<class_1923, Boolean>> = ArrayDeque()

   @JvmStatic
   public fun queueChunkForInitialization(pos: class_1923, forceUpdate: Boolean) {
      toInitialize.addLast(Pair(pos, forceUpdate))
   }

   public fun onRegistriesCompleted() {
      while (true) {
         val var10000: Pair = toInitialize.removeFirstOrNull() as Pair
         if (var10000 != null) {
            val pos: class_1923 = var10000.component1() as class_1923
            val shouldForce: Boolean = var10000.component2() as java.lang.Boolean
            val var17: class_638 = class_310.method_1551().field_1687
            if (var17 != null) {
               val level: class_638 = var17
               val var18: class_2818 = var17.method_8497(pos.field_9181, pos.field_9180)
               if (var18 != null) {
                  val worldChunk: class_2818 = var18
                  val clientShipWorld: VsiClientShipWorld = shipObjectWorld
                  if (clientShipWorld != null && VSGameConfig.CLIENT.Connectivity.enableClientConnectivity) {
                     val voxelShapeUpdates: ArrayList = ArrayList()
                     val chunkSections: Array<class_2826> = var18.method_12006()
                     var sectionY: Int = 0

                     for (update in chunkSections.length..sectionY) {
                        val var19: class_2826 = chunkSections[sectionY]
                        val chunkPos: Vector3ic = Vector3i(pos.field_9181, worldChunk.method_31604(sectionY), pos.field_9180)
                        if (!var19.method_38292()) {
                           voxelShapeUpdates.add(VSGameUtilsKt.toDenseVoxelUpdate(var19, chunkPos))
                        } else {
                           voxelShapeUpdates.add(vsCore.newEmptyVoxelShapeUpdate(chunkPos.x(), chunkPos.y(), chunkPos.z(), true))
                        }
                     }

                     if (!shouldForce) {
                        clientShipWorld.addTerrainUpdates(vsApi.getDimensionId(var17 as class_1937), voxelShapeUpdates)
                     } else {
                        val var20: java.util.Iterator = voxelShapeUpdates.iterator()
                        val var14: java.util.Iterator = var20

                        while (var14.hasNext()) {
                           val var21: Any = var14.next()
                           clientShipWorld.forceUpdateConnectivityChunk(
                              vsApi.getDimensionId(level as class_1937),
                              (var21 as VsiTerrainUpdate).chunkX,
                              (var21 as VsiTerrainUpdate).chunkY,
                              (var21 as VsiTerrainUpdate).chunkZ,
                              var21 as VsiTerrainUpdate
                           )
                        }
                     }
                  }
               }
            }

            if (!(toInitialize as java.util.Collection).isEmpty()) {
               continue
            }
         }

         return
      }
   }
}
