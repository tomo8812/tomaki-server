package org.valkyrienskies.core.internal.physics.blockstates

import org.valkyrienskies.core.api.physics.blockstates.LiquidState
import org.valkyrienskies.core.api.physics.blockstates.SolidState

public class VsiBlockState(solidState: SolidState?, liquidState: LiquidState?) {
   public final val solidState: SolidState?
   public final val liquidState: LiquidState?

   init {
      this.solidState = solidState
      this.liquidState = liquidState
   }

   public override operator fun equals(other: Any?): Boolean {
      return this === other
         || other is VsiBlockState && this.solidState == (other as VsiBlockState).solidState && this.liquidState == (other as VsiBlockState).liquidState
      }

   public override fun hashCode(): Int {
      return 31 * (if (this.solidState != null) this.solidState.hashCode() else 0) + (if (this.liquidState != null) this.liquidState.hashCode() else 0)
   }

   public override fun toString(): String {
      return "VsBlockState(solidState=${this.solidState}, liquidState=${this.liquidState})"
   }
}
