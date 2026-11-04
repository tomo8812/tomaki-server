package org.valkyrienskies.mod.common

import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_18
import net.minecraft.class_2487
import net.minecraft.class_7225.class_7874
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.valkyrienskies.core.internal.world.VsiPipeline

public class ShipSavedData : class_18 {
   public final lateinit var pipeline: VsiPipeline

   public final var loadingException: Throwable?
      private set

   fun method_75(compoundTag: class_2487, provider: class_7874): class_2487 {
      val logger: Logger = LoggerFactory.getLogger("VS2")
      val bytes: ByteArray = vsCore.serializePipeline(this.pipeline)
      logger.info(" ShipSavedData.save(): pipeline bytes = {} KB", bytes.length / 1024)
      compoundTag.method_10570("vs_pipeline", bytes)
      compoundTag
   }

   fun method_79(): Boolean {
      true
   }

   @SourceDebugExtension(["SMAP\nShipSavedData.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipSavedData.kt\norg/valkyrienskies/mod/common/ShipSavedData$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,77:1\n1#2:78\n*E\n"])
   public companion object {
      public const val SAVED_DATA_ID: String = "vs_ship_data"
      private const val QUERYABLE_SHIP_DATA_NBT_KEY: String = "queryable_ship_data"
      private const val CHUNK_ALLOCATOR_NBT_KEY: String = "chunk_allocator"
      private const val PIPELINE_NBT_KEY: String = "vs_pipeline"

      public fun createEmpty(): ShipSavedData {
         val var1: ShipSavedData = ShipSavedData()
         var1.pipeline = vsCore.newPipeline()
         return var1
      }

      public fun load(compoundTag: class_2487): ShipSavedData {
         val logger: Logger = LoggerFactory.getLogger("VS2")
         val data: ShipSavedData = ShipSavedData()
         val queryableShipDataAsBytes: ByteArray = compoundTag.method_10547("queryable_ship_data")
         val chunkAllocatorAsBytes: ByteArray = compoundTag.method_10547("chunk_allocator")
         val pipelineAsBytes: ByteArray = compoundTag.method_10547("vs_pipeline")
         logger.info(
            " ShipSavedData.load(): pipeline bytes = {} KB, legacy queryable = {} KB, legacy allocator = {} KB",
            arrayOf(pipelineAsBytes.length / 1024, queryableShipDataAsBytes.length / 1024, chunkAllocatorAsBytes.length / 1024)
         )

         try {
            if (pipelineAsBytes.length != 0) {
               data.pipeline = vsCore.newPipeline(pipelineAsBytes)
               logger.info(" ShipSavedData.load(): loaded pipeline from {} KB of data", pipelineAsBytes.length / 1024)
            } else {
               if (queryableShipDataAsBytes.length != 0) {
                  if (chunkAllocatorAsBytes.length != 0) {
                     data.pipeline = vsCore.newPipelineLegacyData(queryableShipDataAsBytes, chunkAllocatorAsBytes)
                     logger.info(" ShipSavedData.load(): loaded pipeline from legacy data")
                     return data
                  }
               }

               throw IllegalStateException("Couldn't find serialized ship data")
            }
         } catch (var8: Exception) {
            logger.error(" ShipSavedData.load(): FAILED to load pipeline: {}", var8.getMessage())
            data.loadingException = var8
         }

         return data
      }
   }
}
