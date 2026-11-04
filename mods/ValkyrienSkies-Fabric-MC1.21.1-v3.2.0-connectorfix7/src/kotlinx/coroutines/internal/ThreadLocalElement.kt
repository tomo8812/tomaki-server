package kotlinx.coroutines.internal

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key
import kotlinx.coroutines.ThreadContextElement

internal class ThreadLocalElement<T>(value: Any, threadLocal: ThreadLocal<Any>) : ThreadContextElement<T> {
   private final val value: Any
   private final val threadLocal: ThreadLocal<Any>
   public open val key: Key<*>

   init {
      this.value = (T)value
      this.threadLocal = threadLocal
      this.key = ThreadLocalKey(this.threadLocal)
   }

   public override fun updateThreadContext(context: CoroutineContext): Any {
      val oldState: Any = this.threadLocal.get()
      this.threadLocal.set(this.value)
      return (T)oldState
   }

   public override fun restoreThreadContext(context: CoroutineContext, oldState: Any) {
      this.threadLocal.set((T)oldState)
   }

   public open fun minusKey(key: Key<*>): CoroutineContext {
      return if (this.key == key) EmptyCoroutineContext.INSTANCE as CoroutineContext else this as CoroutineContext
   }

   public open operator fun <E : Element> get(key: Key<Any>): Any? {
      val var10000: Element
      if (this.key == key) {
         var10000 = this
      } else {
         var10000 = null
      }

      return (E)var10000
   }

   public override fun toString(): String {
      return "ThreadLocal(value=${this.value}, threadLocal = ${this.threadLocal})"
   }

   fun <R> fold(initial: R, operation: (R?, Element?) -> R): R {
      ThreadContextElement.DefaultImpls.fold(this, initial, operation)
   }

   fun plus(context: CoroutineContext): CoroutineContext {
      ThreadContextElement.DefaultImpls.plus(this, context)
   }
}
