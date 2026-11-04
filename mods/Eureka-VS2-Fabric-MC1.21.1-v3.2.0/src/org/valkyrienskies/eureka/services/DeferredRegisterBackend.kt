package org.valkyrienskies.eureka.services

import net.minecraft.class_2378
import net.minecraft.class_5321
import org.valkyrienskies.eureka.registry.DeferredRegister

public interface DeferredRegisterBackend {
   public abstract fun <T> makeDeferredRegister(id: String, registry: class_5321<class_2378<Any>>): DeferredRegister<Any> {
   }
}
