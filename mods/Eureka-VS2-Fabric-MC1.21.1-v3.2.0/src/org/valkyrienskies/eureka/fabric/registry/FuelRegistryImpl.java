package org.valkyrienskies.eureka.fabric.registry;

import net.minecraft.class_1799;
import org.valkyrienskies.eureka.registry.FuelRegistry;

public class FuelRegistryImpl extends FuelRegistry {
   public FuelRegistryImpl() {
      INSTANCE = this;
   }

   @Override
   public int get(class_1799 stack) {
      Integer time = (Integer)net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(stack.method_7909());
      return time == null ? 0 : time;
   }
}
