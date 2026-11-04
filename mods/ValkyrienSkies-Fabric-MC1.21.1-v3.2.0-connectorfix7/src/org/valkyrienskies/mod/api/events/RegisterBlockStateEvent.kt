package org.valkyrienskies.mod.api.events

import net.minecraft.class_2680
import org.jetbrains.annotations.ApiStatus
import org.valkyrienskies.core.api.physics.blockstates.LiquidState
import org.valkyrienskies.core.api.physics.blockstates.SolidState
import org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder

@ApiStatus.Experimental
public interface RegisterBlockStateEvent {
   public abstract fun newLiquidStateBuilder(): Builder {
   }

   public abstract fun buildLiquidState(block: (Builder) -> Unit): LiquidState {
   }

   public abstract fun newSolidStateBuilder(): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
   }

   public abstract fun buildSolidState(block: (org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder) -> Unit): SolidState {
   }

   public abstract fun register(state: class_2680, solidState: SolidState) {
   }

   public abstract fun register(state: class_2680, liquidState: LiquidState) {
   }

   public abstract fun register(state: class_2680, liquidState: LiquidState, solidState: SolidState) {
   }
}
