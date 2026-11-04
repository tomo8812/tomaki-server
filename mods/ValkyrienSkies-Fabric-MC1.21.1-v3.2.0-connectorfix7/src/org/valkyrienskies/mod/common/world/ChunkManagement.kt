package org.valkyrienskies.mod.common.world

import java.util.SortedSet
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KProperty
import net.minecraft.class_1657
import net.minecraft.class_1923
import net.minecraft.class_1937
import net.minecraft.class_2596
import net.minecraft.class_2672
import net.minecraft.class_2818
import net.minecraft.class_3218
import net.minecraft.class_3222
import net.minecraft.class_3898
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.Logger
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTask
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTasks
import org.valkyrienskies.mod.common.VS2ChunkAllocator
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.util.MinecraftPlayer
import org.valkyrienskies.mod.mixin.accessors.server.level.ChunkMapAccessor
import org.valkyrienskies.mod.util.ClassLogger
import org.valkyrienskies.mod.util.LoggingKt

@SourceDebugExtension(["SMAP\nChunkManagement.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChunkManagement.kt\norg/valkyrienskies/mod/common/world/ChunkManagement\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,148:1\n1863#2,2:149\n1863#2,2:151\n*S KotlinDebug\n*F\n+ 1 ChunkManagement.kt\norg/valkyrienskies/mod/common/world/ChunkManagement\n*L\n28#1:149,2\n80#1:151,2\n*E\n"])
public object ChunkManagement {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property1(PropertyReference1Impl(ChunkManagement.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1)
   );

   private final val logger: Logger by LoggingKt.logger().provideDelegate_j8jxBIw/* $VF was: provideDelegate-j8jxBIw */(INSTANCE, $$delegatedProperties[0])
      private final get() {
         return ClassLogger.getValue_impl/* $VF was: getValue-impl */(logger$delegate, this, $$delegatedProperties[0])
      }


   @JvmStatic
   public fun tickChunkLoading(shipWorld: VsiServerShipWorld, server: MinecraftServer) {
      val var2: VsiChunkWatchTasks = shipWorld.getChunkWatchTasks()
      val chunkWatchTasks: SortedSet = var2.component1()
      val chunkUnwatchTasks: SortedSet = var2.component2()

      for (`element$iv` in chunkWatchTasks) {
         val chunkUnwatchTask: VsiChunkWatchTask = `element$iv` as VsiChunkWatchTask
         INSTANCE.logger
            .debug(
               "Watch task for dimension ${(`element$iv` as VsiChunkWatchTask).dimensionId}: ${(`element$iv` as VsiChunkWatchTask).chunkX} : ${(`element$iv` as VsiChunkWatchTask).chunkZ}"
            )
            val chunkPos: class_1923 = class_1923(chunkUnwatchTask.chunkX, chunkUnwatchTask.chunkZ)
         val var10000: class_3218 = VSGameUtilsKt.getLevelFromDimensionId(server, chunkUnwatchTask.dimensionId)
         if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkPos.field_9181, chunkPos.field_9180)) {
            var10000.method_14178().method_17297(VSTicketType.SHIP_CHUNK, chunkPos, 0, chunkPos)
         } else {
            var10000.method_14178().method_12124(chunkPos, true)
         }

         val level: Boolean = VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkPos.field_9181, chunkPos.field_9180)
         val player: Function0 = if (level) { 
            VSGameUtilsKt.isChunkLoadedForVS(`$level` as class_1937, `$chunkPos`)
         } else { 
            VSGameUtilsKt.isTickingChunk(`$level` as class_1937, `$chunkPos`)
         }
         val var30: MinecraftServer = var10000.method_8503()
         VSGameUtilsKt.executeIf(var30, player, { 
            for (player in `$chunkWatchTask`.playersNeedWatching) {
               if (player is MinecraftPlayer) {
                  val serverPlayer: class_3222 = (player as MinecraftPlayer).playerEntityReference.get() as class_3222
                  if (serverPlayer != null) {
                     if (!(`$chunkWatchTask`.dimensionId == (player as MinecraftPlayer).dimension)) {
                        INSTANCE.logger.warn("Player received watch task for chunk in dimension that they are not also in!")
                     }

                     val var10000: class_3898 = `$level`.method_14178().field_17254
                     val map: ChunkMapAccessor = var10000 as ChunkMapAccessor
                     if (`$isShipyard`) {
                        val chunk: class_2818 = `$level`.method_14178().method_21730(`$chunkPos`.field_9181, `$chunkPos`.field_9180)
                        if (chunk != null) {
                           serverPlayer.field_13987.method_14364(class_2672(chunk, `$level`.method_22336(), null, null) as class_2596)
                        }
                     } else {
                        map.callMarkChunkPendingToSend(serverPlayer, `$chunkPos`)
                     }
                  }
               }
            }
         })
      }

      for (var20 in chunkUnwatchTasks) {
         val var21: VsiChunkUnwatchTask = var20 as VsiChunkUnwatchTask
         INSTANCE.logger
            .debug(
               "Unwatch task for dimension ${(var20 as VsiChunkUnwatchTask).dimensionId}: ${(var20 as VsiChunkUnwatchTask).chunkX} : ${(var20 as VsiChunkUnwatchTask).chunkZ}"
            )
            val var23: class_1923 = class_1923(var21.chunkX, var21.chunkZ)
         var var24: Boolean = false
         if (var21.shouldUnload) {
            val var31: class_3218 = VSGameUtilsKt.getLevelFromDimensionId(server, var21.dimensionId)
            if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(var23.field_9181, var23.field_9180)) {
               if (shipWorld.allShips.getById(var21.ship.getId()) == null) {
                  var24 = true
                  val chunk: class_2818 = var31.method_14178().method_21730(var23.field_9181, var23.field_9180)
                  if (chunk != null) {
                     chunk.method_12008(false)
                  }

                  var31.method_14178().method_17300(VSTicketType.SHIP_CHUNK, var23, 0, var23)
               }
            } else {
               var31.method_14178().method_12124(var23, false)
            }
         }

         if (!var24) {
            for (var28 in var21.playersNeedUnwatching) {
               if (var28 is MinecraftPlayer) {
                  val var32: class_1657 = mcPlayer
                  val var29: class_3222 = var32 as class_3222
                  val var33: class_3218 = VSGameUtilsKt.getLevelFromDimensionId(server, var21.dimensionId)
                  val var34: class_3898 = var33.method_14178().field_17254
                  (var34 as ChunkMapAccessor).callDropChunk(var29, var23)
               }
            }
         }
      }

      shipWorld.setExecutedChunkWatchTasks(chunkWatchTasks, chunkUnwatchTasks)
   }

   @JvmStatic
   public fun getPendingTrackingUpdates(): List<Any> {
      return CollectionsKt.emptyList()
   }

   @JvmStatic
   public fun clearPendingState() {
   }
}
