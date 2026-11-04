package org.valkyrienskies.eureka

import net.minecraft.class_1661
import net.minecraft.class_1703
import net.minecraft.class_2561
import net.minecraft.class_3917
import net.minecraft.class_3929
import net.minecraft.class_465
import org.valkyrienskies.eureka.registry.RegistrySupplier

private data class ClientScreenRegistar<T extends class_1703>(type: RegistrySupplier<class_3917<Any>>, factory: (Any, class_1661, class_2561) -> class_465<Any>) {
   public final val type: RegistrySupplier<class_3917<Any>>
   public final val factory: (Any, class_1661, class_2561) -> class_465<Any>

   init {
      this.type = type
      this.factory = factory
   }

   public fun register() {
      class_3929.method_17542(this.type.get(), { abstractContainerMenu: class_1703, inventory: class_1661, component: class_2561 ->
         `$tmp0`(abstractContainerMenu, inventory, component) as class_465
      })
   }

   public operator fun component1(): RegistrySupplier<class_3917<Any>> {
      return this.type
   }

   public operator fun component2(): (Any, class_1661, class_2561) -> class_465<Any> {
      return this.factory
   }

   public fun copy(type: RegistrySupplier<class_3917<Any>> = this.type, factory: (Any, class_1661, class_2561) -> class_465<Any> = this.factory): ClientScreenRegistar<
         Any
      > {
      return ClientScreenRegistar<>(type, factory)
   }

   public override fun toString(): String {
      return "ClientScreenRegistar(type=${this.type}, factory=${this.factory})"
   }

   public override fun hashCode(): Int {
      return this.type.hashCode() * 31 + this.factory.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is ClientScreenRegistar && this.type == (other as ClientScreenRegistar).type && this.factory == (other as ClientScreenRegistar).factory
      }
   }
}
