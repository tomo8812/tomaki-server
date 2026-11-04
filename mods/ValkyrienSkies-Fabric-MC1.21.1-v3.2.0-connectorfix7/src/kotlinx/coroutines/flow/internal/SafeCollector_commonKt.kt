package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.InlineMarker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.internal.ScopeCoroutine

@JvmName(name = "checkContext")
internal fun SafeCollector<*>.checkContext(currentContext: CoroutineContext) {
   if ((currentContext.fold(
            0,
            { count: Int, element: Element ->
               val key: Key = element.getKey()
               val collectElement: Element = $this$checkContext.collectContext.get(key)
               if (key != Job.Key) {
                  return if (element != collectElement) Integer.MIN_VALUE else count + 1
               } else {
                  val collectJob: Job = collectElement as Job
                  val emissionParentJob: Job = SafeCollector_commonKt.transitiveCoroutineParent(element as Job, collectJob)
                  if (emissionParentJob != collectJob) {
                     throw IllegalStateException(
                        ("Flow invariant is violated:\n\t\tEmission from another coroutine is detected.\n\t\tChild of $emissionParentJob, expected child of $collectJob.\n\t\tFlowCollector is not thread-safe and concurrent emissions are prohibited.\n\t\tTo mitigate this restriction please use 'channelFlow' builder instead of 'flow'")
                           .toString()
                     )
                  } else {
                     return if (collectJob == null) count else count + 1
                  }
               }
            } as Function2
         ) as java.lang.Number)
         .intValue()
      != `$this$checkContext`.collectContextSize) {
      throw IllegalStateException(
         ("Flow invariant is violated:\n\t\tFlow was collected in ${`$this$checkContext`.collectContext},\n\t\tbut emission happened in $currentContext.\n\t\tPlease refer to 'flow' documentation or use 'flowOn' instead")
            .toString()
      )
   }
}

internal tailrec fun Job?.transitiveCoroutineParent(collectJob: Job?): Job? {
   while (`$this$transitiveCoroutineParent` != null) {
      if (`$this$transitiveCoroutineParent` === collectJob) {
         return `$this$transitiveCoroutineParent`
      }

      if (`$this$transitiveCoroutineParent` !is ScopeCoroutine) {
         return `$this$transitiveCoroutineParent`
      }

      `$this$transitiveCoroutineParent` = `$this$transitiveCoroutineParent`.parent
      collectJob = collectJob
   }

   return null
}

@PublishedApi
internal inline fun <T> unsafeFlow(crossinline block: (FlowCollector<Any>, Continuation<Unit>) -> Any?): Flow<Any> {
   return object : Flow<Any> {
      public override suspend fun collect(collector: FlowCollector<Any>) {
         val var10000: Any = block(collector, `$completion`)
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }
   }
}
