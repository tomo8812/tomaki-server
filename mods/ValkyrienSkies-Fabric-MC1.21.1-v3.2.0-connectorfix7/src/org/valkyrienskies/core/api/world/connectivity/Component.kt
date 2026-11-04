package org.valkyrienskies.core.api.world.connectivity

public interface Component {
   public abstract fun getSize(): Long {
   }

   public abstract fun collectAugmentation(key: DoubleAugmentation): Double {
   }
}
