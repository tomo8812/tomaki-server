package org.valkyrienskies.core.api.world.connectivity

import org.valkyrienskies.core.api.util.DoublePair

public interface DoubleComponentAugmentation : DoubleAugmentation {
   public abstract fun splitDouble(value: Double, component1: Component, component2: Component): DoublePair {
   }
}
