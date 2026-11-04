package org.valkyrienskies.core.api.event

public interface EmittableEvent<T> {
   public abstract fun emit(value: Any) {
   }
}
