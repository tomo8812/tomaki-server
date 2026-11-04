package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline

import java.util.ArrayList
import javax.inject.Inject
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.ServerShipDataConverter
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV4
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV5
import org.valkyrienskies.core.impl.shadow.EI
import org.valkyrienskies.core.impl.shadow.Ek
import org.valkyrienskies.core.impl.shadow.Eo
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class VSPipelineDataConverterImpl @Inject  public constructor(converter: ServerShipDataConverter) : VSPipelineDataConverter {
   private final val converter: ServerShipDataConverter

   init {
      this.converter = converter
   }

   public override fun convertToModel(data: VSPipelineDataV5): EI {
      return EI(Eo<>(this.convertShipData(data.ships)), data.chunkAllocator, Ek.a.a(data.nextJointId, data.joints))
   }

   public override fun convertShipData(data: List<ServerShipDataV4>): List<ShipData> {
      val var5: java.lang.Iterable
      val var2: java.lang.Iterable = var5 = data
      data = ArrayList(CollectionsKt.collectionSizeOrDefault(var5, 10))

      for (var3 in var2) {
         data.add(this.converter.convertToModel(var3 as ServerShipDataV4))
      }

      return data
   }
}
