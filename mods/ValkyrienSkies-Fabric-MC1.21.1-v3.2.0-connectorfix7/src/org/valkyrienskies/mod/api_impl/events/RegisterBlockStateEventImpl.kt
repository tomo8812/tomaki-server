package org.valkyrienskies.mod.api_impl.events

import java.util.ArrayList
import net.minecraft.class_2680
import org.valkyrienskies.core.api.physics.blockstates.LiquidState
import org.valkyrienskies.core.api.physics.blockstates.SolidState
import org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder
import org.valkyrienskies.core.internal.physics.blockstates.VsiBlockState
import org.valkyrienskies.mod.api.events.RegisterBlockStateEvent

public class RegisterBlockStateEventImpl : RegisterBlockStateEvent {
   public final val toRegister: MutableList<Pair<class_2680, VsiBlockState>> = ArrayList() as java.util.List

   public override fun newLiquidStateBuilder(): Builder {
      return vsCore.newLiquidStateBuilder()
   }

   public override fun buildLiquidState(block: (Builder) -> Unit): LiquidState {
      val var2: LiquidState.Builder = vsCore.newLiquidStateBuilder()
      block(var2)
      return var2.build()
   }

   public override fun newSolidStateBuilder(): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
      return vsCore.newSolidStateBuilder()
   }

   public override fun buildSolidState(block: (org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder) -> Unit): SolidState {
      val var2: SolidState.Builder = vsCore.newSolidStateBuilder()
      block(var2)
      return var2.build()
   }

   public override fun register(state: class_2680, solidState: SolidState) {
      this.toRegister.add(Pair(state, VsiBlockState(solidState, null)))
   }

   public override fun register(state: class_2680, liquidState: LiquidState) {
      this.toRegister.add(Pair(state, VsiBlockState(null, liquidState)))
   }

   public override fun register(state: class_2680, liquidState: LiquidState, solidState: SolidState) {
      this.toRegister.add(Pair(state, VsiBlockState(solidState, liquidState)))
   }
}
