package org.valkyrienskies.eureka

import java.util.ArrayList
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1661
import net.minecraft.class_1703
import net.minecraft.class_2561
import net.minecraft.class_3917
import net.minecraft.class_465
import org.valkyrienskies.eureka.registry.RegistrySupplier

@SourceDebugExtension(["SMAP\nEurekaClientScreens.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EurekaClientScreens.kt\norg/valkyrienskies/eureka/EurekaClientScreens\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,39:1\n1863#2,2:40\n*S KotlinDebug\n*F\n+ 1 EurekaClientScreens.kt\norg/valkyrienskies/eureka/EurekaClientScreens\n*L\n33#1:40,2\n*E\n"])
public object EurekaClientScreens {
   private final val SCREENS_CLIENT: MutableList<ClientScreenRegistar<*>> = ArrayList() as java.util.List

   public fun register() {
      for (`element$iv` in SCREENS_CLIENT) {
         (`element$iv` as ClientScreenRegistar).register()
      }
   }

   private infix fun <T : class_1703> RegistrySupplier<class_3917<Any>>.withScreen(screen: (Any, class_1661, class_2561) -> class_465<Any>): Boolean {
      return SCREENS_CLIENT.add(ClientScreenRegistar(`$this$withScreen`, screen))
   }

   @JvmStatic
   fun {
      EurekaScreens.INSTANCE.SHIP_HELM withScreen <unrepresentable>.INSTANCE as Function3
      EurekaScreens.INSTANCE.ENGINE withScreen <unrepresentable>.INSTANCE as Function3
   }
}
