package org.valkyrienskies.eureka

import net.minecraft.class_2378
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769
import net.minecraft.class_2960
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.mod.common.BlockStateInfo
import org.valkyrienskies.mod.common.BlockStateInfoProvider

public object EurekaWeights : BlockStateInfoProvider {
   public open val priority: Int
      public open get() {
         return 200
      }


   public open fun getBlockStateMass(blockState: class_2680): Double? {
      return if (blockState.method_26204() == EurekaBlocks.INSTANCE.BALLAST.get())
         EurekaConfig.SERVER.ballastWeight
            + (EurekaConfig.SERVER.ballastNoWeight - EurekaConfig.SERVER.ballastWeight)
               * ((double)((blockState.method_11654(class_2741.field_12511 as class_2769) as java.lang.Number).intValue() + 1) / 16.0)
         else
         null
      }

   public open fun getBlockStateType(blockState: class_2680): VsiBlockType? {
      return null
   }

   public fun register() {
      class_2378.method_10230(BlockStateInfo.INSTANCE.getREGISTRY() as class_2378, class_2960.method_60655("vs_eureka", "ballast"), this)
   }
}
