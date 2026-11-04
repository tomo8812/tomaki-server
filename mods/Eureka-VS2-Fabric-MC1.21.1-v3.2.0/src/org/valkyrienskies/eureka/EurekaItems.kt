package org.valkyrienskies.eureka

import net.minecraft.class_1761
import net.minecraft.class_1792
import net.minecraft.class_2960
import net.minecraft.class_5321
import net.minecraft.class_7924
import org.valkyrienskies.eureka.registry.DeferredRegister
import org.valkyrienskies.eureka.registry.RegistrySupplier

public object EurekaItems {
   internal final val ITEMS: DeferredRegister<class_1792>
   public final val TAB: class_5321<class_1761>

   public fun register() {
      EurekaBlocks.INSTANCE.registerItems(ITEMS)
      ITEMS.applyAll()
   }

   private infix fun class_1792.byName(name: String): RegistrySupplier<class_1792> {
      return ITEMS.register(name, { 
         `$this_byName`
      })
   }

   @JvmStatic
   fun {
      val var10000: DeferredRegister.Companion = DeferredRegister.Companion
      val var10002: class_5321 = class_7924.field_41197
      ITEMS = var10000.create("vs_eureka", var10002)
      val var0: class_5321 = class_5321.method_29179(class_7924.field_44688, class_2960.method_60655("vs_eureka", "eureka_tab"))
      TAB = var0
   }
}
