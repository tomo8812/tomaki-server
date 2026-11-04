package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.ScopeCoroutine
import kotlinx.coroutines.internal.ThreadContextKt

@SourceDebugExtension(["SMAP\nCoroutineContext.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineContext.kt\nkotlinx/coroutines/UndispatchedCoroutine\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 CoroutineContext.kt\nkotlinx/coroutines/CoroutineContextKt\n*L\n1#1,314:1\n1#2:315\n107#3,13:316\n*S KotlinDebug\n*F\n+ 1 CoroutineContext.kt\nkotlinx/coroutines/UndispatchedCoroutine\n*L\n269#1:316,13\n*E\n"])
internal class UndispatchedCoroutine<T>(context: CoroutineContext, uCont: Continuation<Any>) : ScopeCoroutine(
      if (context.get(UndispatchedMarker.INSTANCE) == null) context.plus(UndispatchedMarker.INSTANCE as CoroutineContext) else context, uCont
   ) {
   private final val threadStateToRecover: ThreadLocal<Pair<CoroutineContext, Any?>> = ThreadLocal()
   private final var threadLocalIsSet: Boolean

   public fun saveThreadContext(context: CoroutineContext, oldValue: Any?) {
      this.threadLocalIsSet = true
      this.threadStateToRecover.set(TuplesKt.to(context, oldValue))
   }

   public fun clearThreadContext(): Boolean {
      val var1: Boolean = this.threadLocalIsSet && this.threadStateToRecover.get() == null
      this.threadStateToRecover.remove()
      return !var1
   }

   protected override fun afterResume(state: Any?) {
      if (this.threadLocalIsSet) {
         val var10000: Pair = this.threadStateToRecover.get()
         if (var10000 != null) {
            ThreadContextKt.restoreThreadContext(var10000.component1() as CoroutineContext, var10000.component2())
         }

         this.threadStateToRecover.remove()
      }

      val result: Any = CompletionStateKt.recoverResult(state, this.uCont)
      val `continuation$iv`: Continuation = this.uCont
      val var14: CoroutineContext = this.uCont.getContext()
      val var15: Any = ThreadContextKt.updateThreadContext(var14, null)
      val `undispatchedCompletion$iv`: UndispatchedCoroutine = if (var15 != ThreadContextKt.NO_THREAD_ELEMENTS)
         CoroutineContextKt.updateUndispatchedCompletion(`continuation$iv`, var14, var15)
         else
         null

      try {
         this.uCont.resumeWith(result)
      } finally {
         if (`undispatchedCompletion$iv` == null || `undispatchedCompletion$iv`.clearThreadContext()) {
            ThreadContextKt.restoreThreadContext(var14, var15)
         }
      }
   }
}
