package org.valkyrienskies.core.api.physics.blockstates

import org.joml.Vector3dc
import org.joml.primitives.AABBic

public interface LiquidState {
   public val density: Double

   public val dragCoefficient: Double

   public val velocity: Vector3dc

   public val shape: LiquidBlockShape

   public interface Builder {
      public abstract fun boxShape(boundingBox: AABBic): org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder {
      }

      public abstract fun shape(shape: LiquidBlockShape): org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder {
      }

      public abstract fun density(density: Double): org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder {
      }

      public abstract fun dragCoefficient(dragCoefficient: Double): org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder {
      }

      public abstract fun velocity(velocity: Vector3dc): org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder {
      }

      public abstract fun build(): LiquidState {
      }
   }
}
