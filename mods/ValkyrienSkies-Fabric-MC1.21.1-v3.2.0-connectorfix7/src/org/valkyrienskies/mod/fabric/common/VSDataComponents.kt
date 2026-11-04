package org.valkyrienskies.mod.fabric.common

import net.minecraft.class_2338
import net.minecraft.class_2378
import net.minecraft.class_2960
import net.minecraft.class_7923
import net.minecraft.class_9331
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

public object VSDataComponents {
   private fun <T> register(name: String, builder: () -> class_9331<Any>): class_9331<Any> {
      val var10000: Any = class_2378.method_10230(class_7923.field_49658, class_2960.method_60655("valkyrienskies", name), builder())
      return var10000 as class_9331<T>
   }

   public fun registerDataComponents() {
      ValkyrienSkiesMod.INSTANCE.BLOCK_POS_COMPONENT = this.register("coordinate", { 
         val var10000: class_9331 = class_9331.method_57873().method_57881(class_2338.field_25064).method_57880()
         var10000
      })
   }
}
