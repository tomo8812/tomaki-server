package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline

import org.valkyrienskies.core.impl.game.ships.serialization.ChainUpdater
import org.valkyrienskies.core.impl.game.ships.serialization.ChainUpdaterImpl
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV1
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV1Updater
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV2
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV2Updater
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV3
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV3Updater
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV4
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV4Updater
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV5
import org.valkyrienskies.core.impl.shadow.dW
import org.valkyrienskies.core.impl.shadow.dX

@dW
public class VSPipelineChainUpdaterModule {
   @dX
   public fun updater(v1: VSPipelineDataV1Updater, v2: VSPipelineDataV2Updater, v3: VSPipelineDataV3Updater, v4: VSPipelineDataV4Updater): ChainUpdater<
         VSPipelineDataV5
      > {
      val var5: Array<Pair>
      (var5 = arrayOfNulls(4))[0] = TuplesKt.to(VSPipelineDataV1.class, v1)
      var5[1] = TuplesKt.to(VSPipelineDataV2.class, v2)
      var5[2] = TuplesKt.to(VSPipelineDataV3.class, v3)
      var5[3] = TuplesKt.to(VSPipelineDataV4.class, v4)
      return ChainUpdaterImpl<>(VSPipelineDataV5::class.java, var5)
   }
}
