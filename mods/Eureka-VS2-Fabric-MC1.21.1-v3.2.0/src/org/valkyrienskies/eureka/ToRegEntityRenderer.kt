package org.valkyrienskies.eureka

import net.minecraft.class_1297
import net.minecraft.class_1299
import net.minecraft.class_5617
import net.minecraft.class_5619
import org.valkyrienskies.eureka.registry.RegistrySupplier

private data class ToRegEntityRenderer<T extends class_1297>(type: RegistrySupplier<class_1299<Any>>, renderer: class_5617<in Any>) {
   public final val type: RegistrySupplier<class_1299<Any>>
   public final val renderer: class_5617<in Any>

   init {
      this.type = type
      this.renderer = renderer
   }

   public fun register() {
      class_5619.method_32173(this.type.get(), this.renderer)
   }

   public operator fun component1(): RegistrySupplier<class_1299<Any>> {
      return this.type
   }

   public operator fun component2(): class_5617<in Any> {
      return this.renderer
   }

   public fun copy(type: RegistrySupplier<class_1299<Any>> = this.type, renderer: class_5617<in Any> = this.renderer): ToRegEntityRenderer<Any> {
      return ToRegEntityRenderer<>(type, renderer)
   }

   public override fun toString(): String {
      return "ToRegEntityRenderer(type=${this.type}, renderer=${this.renderer})"
   }

   public override fun hashCode(): Int {
      return this.type.hashCode() * 31 + this.renderer.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is ToRegEntityRenderer && this.type == (other as ToRegEntityRenderer).type && this.renderer == (other as ToRegEntityRenderer).renderer
      }
   }
}
