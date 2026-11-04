package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key
import kotlin.coroutines.CoroutineContext.Element.DefaultImpls

private object UndispatchedMarker : Element, Key<UndispatchedMarker> {
   public open val key: Key<*>
      public open get() {
         return this as Key<*>
      }


   fun <R> fold(initial: R, operation: (R?, Element?) -> R): R {
      DefaultImpls.fold(this, initial, operation)
   }

   fun <E extends Element> get(key: Key<E>): E? {
      DefaultImpls.get(this, key)
   }

   fun minusKey(key: Key<*>): CoroutineContext {
      DefaultImpls.minusKey(this, key)
   }

   fun plus(context: CoroutineContext): CoroutineContext {
      DefaultImpls.plus(this, context)
   }
}
