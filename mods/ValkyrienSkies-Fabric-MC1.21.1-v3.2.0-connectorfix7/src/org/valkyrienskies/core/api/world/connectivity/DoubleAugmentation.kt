package org.valkyrienskies.core.api.world.connectivity

public interface DoubleAugmentation {
   public val key: String

   public abstract fun combineDouble(a: Double, b: Double): Double {
   }
}
