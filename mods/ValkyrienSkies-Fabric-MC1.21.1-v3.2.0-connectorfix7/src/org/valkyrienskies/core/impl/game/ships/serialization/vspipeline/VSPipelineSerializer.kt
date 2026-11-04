package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.ArrayList
import javax.inject.Inject
import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.serialization.ChainUpdater
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.ServerShipDataConverter
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV0
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV0Updater
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV1
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineData
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV1
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV5
import org.valkyrienskies.core.impl.shadow.DQ
import org.valkyrienskies.core.impl.shadow.EI
import org.valkyrienskies.core.impl.shadow.El
import org.valkyrienskies.core.impl.shadow.Et
import org.valkyrienskies.core.impl.shadow.FQ
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil

public class VSPipelineSerializer @Inject  public constructor(objectMapper: ObjectMapper,
   shipDataMapper: ServerShipDataConverter,
   updater: ChainUpdater<VSPipelineDataV5>,
   v0Updater: ServerShipDataV0Updater,
   pipelineDataConverter: VSPipelineDataConverter
) {
   private final val objectMapper: ObjectMapper
   private final val shipDataMapper: ServerShipDataConverter
   private final val updater: ChainUpdater<VSPipelineDataV5>
   private final val v0Updater: ServerShipDataV0Updater
   private final val pipelineDataConverter: VSPipelineDataConverter

   init {
      this.objectMapper = objectMapper
      this.shipDataMapper = shipDataMapper
      this.updater = updater
      this.v0Updater = v0Updater
      this.pipelineDataConverter = pipelineDataConverter
   }

   public fun deserializeLegacy(queryableShipDataBytes: ByteArray, chunkAllocatorBytes: ByteArray): EI {
      return this.asVsPipelineModule(this.deserializeLegacyAsV1(queryableShipDataBytes, chunkAllocatorBytes))
   }

   public fun deserialize(bytes: ByteArray): EI {
      return this.pipelineDataConverter
         .convertToModel(this.updater.updateToLatest(this.objectMapper.readValue(bytes, VSPipelineSerializer$deserialize$$inlined$readValue$1())))
      }

   public fun serialize(pipeline: FQ): ByteArray {
      var var4: Et = pipeline.a
      var var3: java.util.List = pipeline.a.a as java.lang.Iterable
      val var13: ServerShipDataConverter = this.shipDataMapper
      var3 = ArrayList(CollectionsKt.collectionSizeOrDefault(var3, 10))

      for (var5 in var3) {
         var3.add(var13.convertToDto(var5 as ShipData))
      }

      val var9: java.util.List = var3
      val var11: Et = pipeline.a
      val var10000: ChunkAllocatorProvider = pipeline.a.c
      val var12: ChunkAllocator = (var10000 as DQ).a
      var4 = pipeline.a
      val var15: El = pipeline.a.b.b()
      val var17: ByteArray = this.objectMapper.writeValueAsBytes(VSPipelineDataV5(var12, var9, var15.a, var15.b))
      return var17
   }

   private fun asVsPipelineModule(pipelineData: VSPipelineData): EI {
      return this.pipelineDataConverter.convertToModel(this.updater.updateToLatest(pipelineData))
   }

   private fun deserializeLegacyAsV1(queryableShipDataBytes: ByteArray, chunkAllocatorBytes: ByteArray): VSPipelineDataV1 {
      val var6: java.util.List = VSJacksonUtil.INSTANCE.defaultMapper
         .readValue(queryableShipDataBytes, VSPipelineSerializer$deserializeLegacyAsV1$$inlined$readValue$1())
         val var3: ChunkAllocator = VSJacksonUtil.INSTANCE.defaultMapper
         .readValue(chunkAllocatorBytes, VSPipelineSerializer$deserializeLegacyAsV1$$inlined$readValue$2())
         val var7: java.lang.Iterable
      val var9: java.lang.Iterable = var7 = var6
      val var8: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var7, 10))

      for (var4 in var9) {
         var8.add(this.v0Updater.update(var4 as ServerShipDataV0))
      }

      return VSPipelineDataV1(var3, var8 as MutableList<ServerShipDataV1>)
   }
}
