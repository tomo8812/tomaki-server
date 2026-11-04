@file:JvmMultifileClass
@file:JvmName("BuildersKt")
@file:SourceDebugExtension(["SMAP\nBuilders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Builders.kt\nkotlinx/coroutines/BuildersKt__BuildersKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,102:1\n1#2:103\n*E\n"])

package kotlinx.coroutines

import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.CoroutineContext.Key
import kotlin.jvm.internal.SourceDebugExtension

@Throws(java/lang/InterruptedException::class)
public fun <T> runBlocking(context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext, block: (CoroutineScope, Continuation<Any>) -> Any?): Any {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   val currentThread: Thread = Thread.currentThread()
   val contextInterceptor: ContinuationInterceptor = context.get(ContinuationInterceptor.Key as Key) as ContinuationInterceptor
   val var10: EventLoop
   val var11: CoroutineContext
   if (contextInterceptor == null) {
      var10 = ThreadLocalEventLoop.INSTANCE.eventLoop
      var11 = CoroutineContextKt.newCoroutineContext(GlobalScope.INSTANCE, context.plus(var10 as CoroutineContext))
   } else {
      var var12: EventLoop
      run label33@{
         var12 = contextInterceptor as? EventLoop
         if ((contextInterceptor as? EventLoop) != null) {
            var12 = if (var12.shouldBeProcessedFromContext()) var12 else null
            if (var12 != null) {
               return@label33
            }
         }

         var12 = ThreadLocalEventLoop.INSTANCE.currentOrNull$kotlinx_coroutines_core()
      }

      var10 = var12
      var11 = CoroutineContextKt.newCoroutineContext(GlobalScope.INSTANCE, context)
   }

   val coroutine: BlockingCoroutine = BlockingCoroutine(var11, currentThread, var10)
   coroutine.start(CoroutineStart.DEFAULT, coroutine, block)
   return (T)coroutine.joinBlocking()
}
