package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlinx.coroutines.selects.SelectClause1

private open class DeferredCoroutine<T>(parentContext: CoroutineContext, active: Boolean) : AbstractCoroutine(parentContext, true, active), Deferred<T> {
   public override fun getCompleted(): Any {
      return (T)this.getCompletedInternal$kotlinx_coroutines_core()
   }

   public override suspend fun await(): Any {
      return await$suspendImpl(this, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <T> `await$suspendImpl`(`$this`: DeferredCoroutine<T>, `$completion`: Continuation<in T>) {
      val var10000: Any = `$this`.awaitInternal(`$completion`)
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else var10000
   }

   public open val onAwait: SelectClause1<Any>
      public open get() {
         val var10000: SelectClause1 = this.getOnAwaitInternal()
         return var10000
      }

}
