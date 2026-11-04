package org.valkyrienskies.core.api.event

import java.util.function.Consumer
import java.util.function.Predicate

public interface ListenableEvent<T> {
   public abstract fun on(cb: EventConsumer<Any>): RegisteredListener {
   }

   public open fun on(cb: Consumer<in Any>): RegisteredListener {
      return this.on({ value: Any, var2: RegisteredListener ->
         `$cb`.accept(value)
      })
   }

   public open fun once(cb: Consumer<in Any>): RegisteredListener {
      return this.once({ p0: Any ->
         `$tmp0`(p0)
      }, cb)
   }

   public open fun once(predicate: Predicate<in Any>, cb: Consumer<in Any>): RegisteredListener {
      return this.on({ value: Any, handler: RegisteredListener ->
         if (`$predicate`.test(value)) {
            `$cb`.accept(value)
            handler.unregister()
         }
      })
   }
}
