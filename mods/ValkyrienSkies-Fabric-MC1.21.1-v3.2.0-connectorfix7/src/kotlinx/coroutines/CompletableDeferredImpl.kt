package kotlinx.coroutines

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlinx.coroutines.selects.SelectClause1

private class CompletableDeferredImpl<T>(parent: Job?) : JobSupport(true), CompletableDeferred<T> {
   init {
      this.initParentJob(parent)
   }

   internal open val onCancelComplete: Boolean
      internal open get() {
         return true
      }


   public override fun getCompleted(): Any {
      return (T)this.getCompletedInternal$kotlinx_coroutines_core()
   }

   public override suspend fun await(): Any {
      val var10000: Any = this.awaitInternal(`$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else var10000
   }

   public open val onAwait: SelectClause1<Any>
      public open get() {
         val var10000: SelectClause1 = this.getOnAwaitInternal()
         return var10000
      }


   public override fun complete(value: Any): Boolean {
      return this.makeCompleting$kotlinx_coroutines_core(value)
   }

   public override fun completeExceptionally(exception: Throwable): Boolean {
      return this.makeCompleting$kotlinx_coroutines_core(CompletedExceptionally(exception, false, 2, null))
   }
}
