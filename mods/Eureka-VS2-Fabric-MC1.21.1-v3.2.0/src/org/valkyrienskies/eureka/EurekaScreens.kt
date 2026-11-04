package org.valkyrienskies.eureka

import net.minecraft.class_1661
import net.minecraft.class_1703
import net.minecraft.class_3917
import net.minecraft.class_5321
import net.minecraft.class_7701
import net.minecraft.class_7924
import org.valkyrienskies.eureka.gui.engine.EngineScreenMenu
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu
import org.valkyrienskies.eureka.registry.DeferredRegister
import org.valkyrienskies.eureka.registry.RegistrySupplier

public object EurekaScreens {
   private final val SCREENS: DeferredRegister<class_3917<*>>
   public final val SHIP_HELM: RegistrySupplier<class_3917<ShipHelmScreenMenu>> = ShipHelmScreenMenu.Companion.factory withName "ship_helm"
   public final val ENGINE: RegistrySupplier<class_3917<EngineScreenMenu>> = EngineScreenMenu.Companion.factory withName "engine"

   public fun register() {
      SCREENS.applyAll()
   }

   private infix fun <T : class_1703> ((Int, class_1661) -> Any).withName(name: String): RegistrySupplier<class_3917<Any>> {
      return SCREENS.register(name, { 
         class_3917({ i: Int, inventory: class_1661 ->
            `$tmp0`(i, inventory) as class_1703
         }, class_7701.field_40182)
      })
   }

   @JvmStatic
   fun {
      val var10000: DeferredRegister.Companion = DeferredRegister.Companion
      val var10002: class_5321 = class_7924.field_41207
      SCREENS = var10000.create("vs_eureka", var10002)
   }
}
