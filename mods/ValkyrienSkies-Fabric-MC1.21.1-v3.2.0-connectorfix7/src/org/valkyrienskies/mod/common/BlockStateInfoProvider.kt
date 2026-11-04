package org.valkyrienskies.mod.common

import net.minecraft.class_2680
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType

public interface BlockStateInfoProvider {
   public val priority: Int

   public abstract fun getBlockStateMass(blockState: class_2680): Double? {
   }

   public abstract fun getBlockStateType(blockState: class_2680): VsiBlockType? {
   }
}
