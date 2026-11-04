package org.valkyrienskies.mod.common

import net.minecraft.class_2246
import net.minecraft.class_2248
import net.minecraft.class_2404
import net.minecraft.class_2680
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.mod.common.config.VSGameConfig

public object DefaultBlockStateInfoProvider : BlockStateInfoProvider {
   public open val priority: Int
      public open get() {
         return Integer.MIN_VALUE
      }


   public override fun getBlockStateMass(blockState: class_2680): Double {
      return if (blockState.method_26215()) 0.0 else VSGameConfig.SERVER.defaultBlockMass
   }

   public override fun getBlockStateType(blockState: class_2680): VsiBlockType {
      if (blockState.method_26215()) {
         return vsCore.blockTypes.air
      } else {
         val block: class_2248 = blockState.method_26204()
         if (block is class_2404) {
            return if (block == class_2246.field_10164) vsCore.blockTypes.lava else vsCore.blockTypes.water
         } else {
            return if (blockState.method_51367()) vsCore.blockTypes.solid else vsCore.blockTypes.air
         }
      }
   }
}
