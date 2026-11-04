package org.valkyrienskies.eureka.util

import net.minecraft.class_1263
import net.minecraft.class_1661
import net.minecraft.class_1703
import net.minecraft.class_1735

public fun class_1703.inventorySlots(addSlot: (class_1735) -> class_1735, inventory: class_1661) {
   var var3: Byte = 3

   repeat(var3) { var4 ->
      val j: Int = var4
      val var7: Byte = 9

      repeat(var7) { var8 ->
         addSlot(class_1735(inventory as class_1263, var8 + j * 9 + 9, 8 + var8 * 18, 84 + j * 18))
      }
   }

   var3 = 9

   repeat(var3) { var12 ->
      addSlot(class_1735(inventory as class_1263, var12, 8 + var12 * 18, 142))
   }
}
