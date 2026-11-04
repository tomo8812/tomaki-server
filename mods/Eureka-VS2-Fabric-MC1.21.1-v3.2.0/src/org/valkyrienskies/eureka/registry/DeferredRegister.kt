package org.valkyrienskies.eureka.registry

import java.util.ServiceLoader
import kotlin.jvm.internal.markers.KMappedMarker
import net.minecraft.class_2378
import net.minecraft.class_5321
import org.valkyrienskies.eureka.services.DeferredRegisterBackend

public interface DeferredRegister<T> : java.lang.Iterable<RegistrySupplier<T>>, KMappedMarker {
   @JvmStatic
   DeferredRegister.Companion Companion = DeferredRegister.Companion.$$INSTANCE;

   public abstract fun <I : Any> register(name: String, builder: () -> Any): RegistrySupplier<Any> {
   }

   public abstract fun applyAll() {
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      DeferredRegister.Companion $$INSTANCE = DeferredRegister.Companion();
      private final val backend: DeferredRegisterBackend = $$INSTANCE.load()

      public fun <T> create(id: String, registry: class_5321<class_2378<Any>>): DeferredRegister<Any> {
         return backend.makeDeferredRegister(id, registry)
      }

      private fun load(): DeferredRegisterBackend {
         val var10000: Any = ServiceLoader.load(DeferredRegisterBackend.class).findFirst().orElseThrow({ 
            NullPointerException("Failed to load service for DeferredRegisterBackend")
         })
         return var10000 as DeferredRegisterBackend
      }
   }
}
