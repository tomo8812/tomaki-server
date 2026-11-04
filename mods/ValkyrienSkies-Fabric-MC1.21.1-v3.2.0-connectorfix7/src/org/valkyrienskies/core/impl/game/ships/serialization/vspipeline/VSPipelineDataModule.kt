package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline

import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV1Updater
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV1UpdaterImpl
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV2Updater
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV2UpdaterImpl
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV3Updater
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV3UpdaterImpl
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV4Updater
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV4UpdaterImpl
import org.valkyrienskies.core.impl.shadow.dP
import org.valkyrienskies.core.impl.shadow.dW

@dW
public interface VSPipelineDataModule {
   @dP
   public abstract fun converter(converter: VSPipelineDataConverterImpl): VSPipelineDataConverter {
   }

   @dP
   public abstract fun v1(impl: VSPipelineDataV1UpdaterImpl): VSPipelineDataV1Updater {
   }

   @dP
   public abstract fun v2(impl: VSPipelineDataV2UpdaterImpl): VSPipelineDataV2Updater {
   }

   @dP
   public abstract fun v3(impl: VSPipelineDataV3UpdaterImpl): VSPipelineDataV3Updater {
   }

   @dP
   public abstract fun v4(impl: VSPipelineDataV4UpdaterImpl): VSPipelineDataV4Updater {
   }
}
