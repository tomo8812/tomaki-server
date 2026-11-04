package org.valkyrienskies.core.api.event

public fun interface EventConsumer<T> {
   public abstract fun accept(event: Any, handler: RegisteredListener) {
   }
}
