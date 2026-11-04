package org.valkyrienskies.eureka.registry

public interface RegistrySupplier<T> {
   public val name: String

   public abstract fun get(): Any {
   }
}
