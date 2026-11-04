package org.valkyrienskies.core.api.physics.blockstates

public interface SolidState {
   public val elasticity: Double

   public val friction: Double

   public val hardness: Double

   public val shape: SolidBlockShape

   public interface Builder {
      public abstract fun elasticity(elasticity: Double): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
      }

      public abstract fun friction(friction: Double): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
      }

      public abstract fun hardness(hardness: Double): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
      }

      public abstract fun shape(shape: SolidBlockShape): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
      }

      public abstract fun boxesShape(shapeBuilder: (BoxesBlockShape.Builder) -> Unit): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
      }

      public abstract fun build(): SolidState {
      }
   }
}
