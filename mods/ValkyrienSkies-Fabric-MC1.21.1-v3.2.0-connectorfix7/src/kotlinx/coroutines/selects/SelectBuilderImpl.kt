package kotlinx.coroutines.selects

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.CoroutineStart

@PublishedApi
internal class SelectBuilderImpl<R>(uCont: Continuation<Any>) : SelectImplementation(uCont.getContext()) {
   private final val cont: CancellableContinuationImpl<Any>

   init {
      this.cont = CancellableContinuationImpl<>(IntrinsicsKt.intercepted(uCont), 1)
   }

   @PublishedApi
   internal fun getResult(): Any? {
      if (this.cont.isCompleted) {
         return this.cont.getResult()
      } else {
         BuildersKt.launch$default(CoroutineScopeKt.CoroutineScope(this.getContext()), null, CoroutineStart.UNDISPATCHED, {
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as Function2, 1, null)
         return this.cont.getResult()
      }
   }

   @PublishedApi
   internal fun handleBuilderException(e: Throwable) {
      this.cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(e)))
   }
}
