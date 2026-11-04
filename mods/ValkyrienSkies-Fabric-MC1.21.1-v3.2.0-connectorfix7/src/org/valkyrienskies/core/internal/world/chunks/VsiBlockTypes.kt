package org.valkyrienskies.core.internal.world.chunks

import org.valkyrienskies.core.internal.physics.blockstates.VsiBlockState

public interface VsiBlockTypes {
   public val air: VsiBlockType

   public val solid: VsiBlockType

   public val lava: VsiBlockType

   public val water: VsiBlockType

   public val empty: VsiBlockType

   public val airState: VsiBlockState

   public val solidState: VsiBlockState

   public val lavaState: VsiBlockState

   public val waterState: VsiBlockState

   public val emptyState: VsiBlockState

   public abstract fun getType(state: VsiBlockState): VsiBlockType? {
   }

   public abstract fun getState(type: VsiBlockType): VsiBlockState? {
   }
}
