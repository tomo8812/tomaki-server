package org.valkyrienskies.eureka.gui.engine

import net.minecraft.class_1263
import net.minecraft.class_1735
import net.minecraft.class_1799
import net.minecraft.class_1802
import org.valkyrienskies.eureka.registry.FuelRegistry

public class FuelSlot(container: class_1263, slot: Int, x: Int, y: Int) : class_1735(container, slot, x, y) {
   fun method_7680(stack: class_1799): Boolean {
      FuelRegistry.INSTANCE.get(stack) > 0 || this.isBucket(stack)
   }

   fun method_7676(stack: class_1799): Int {
      if (this.isBucket(stack)) 1 else super.method_7676(stack)
   }

   private fun isBucket(stack: class_1799): Boolean {
      return stack.method_7909() === class_1802.field_8550
   }
}
