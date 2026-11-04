package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@DelicateCoroutinesApi
public object GlobalScope : CoroutineScope {
   public open val coroutineContext: CoroutineContext
      public open get() {
         return EmptyCoroutineContext.INSTANCE as CoroutineContext
      }

}
