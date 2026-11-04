@file:SourceDebugExtension(["SMAP\nFuture.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Future.kt\nkotlinx/coroutines/future/FutureKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n*L\n1#1,197:1\n1#2:198\n314#3,11:199\n*S KotlinDebug\n*F\n+ 1 Future.kt\nkotlinx/coroutines/future/FutureKt\n*L\n172#1:199,11\n*E\n"])

package kotlinx.coroutines.future

import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutionException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableDeferredKt
import kotlinx.coroutines.CoroutineContextKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExceptionsKt
import kotlinx.coroutines.Job
import kotlinx.coroutines.JobKt

public fun <T> CoroutineScope.future(
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   start: CoroutineStart = CoroutineStart.DEFAULT,
   block: (CoroutineScope, Continuation<Any>) -> Any?
): CompletableFuture<Any> {
   if (start.isLazy) {
      throw IllegalArgumentException(("$start start is not supported").toString())
   } else {
      val newContext: CoroutineContext = CoroutineContextKt.newCoroutineContext(`$this$future`, context)
      val future: CompletableFuture = CompletableFuture()
      val coroutine: CompletableFutureCoroutine = CompletableFutureCoroutine(newContext, future)
      future.handle(coroutine)
      coroutine.start(start, coroutine, block)
      return future
   }
}

public fun <T> Deferred<Any>.asCompletableFuture(): CompletableFuture<Any> {
   val future: CompletableFuture = CompletableFuture()
   setupCancellation(`$this$asCompletableFuture`, future)
   `$this$asCompletableFuture`.invokeOnCompletion({ it: Throwable? ->
      try {
         future.complete($this$asCompletableFuture.getCompleted())
      } catch (var3: java.lang.Throwable) {
         future.completeExceptionally(var3)
      }
   } as (java.lang.Throwable?) -> Unit)
   return future
}

public fun Job.asCompletableFuture(): CompletableFuture<Unit> {
   val future: CompletableFuture = CompletableFuture()
   setupCancellation(`$this$asCompletableFuture`, future)
   `$this$asCompletableFuture`.invokeOnCompletion({ cause: Throwable? ->
      if (cause == null) {
         future.complete(Unit.INSTANCE)
      } else {
         future.completeExceptionally(cause)
      }
   } as (java.lang.Throwable?) -> Unit)
   return future
}

private fun Job.setupCancellation(future: CompletableFuture<*>) {
   future.handle({ var1: Any, exception: java.lang.Throwable ->
      var var10000: Job = `$this_setupCancellation`
      val var10001: CancellationException
      if (exception != null) {
         var var6: CancellationException = exception as? CancellationException
         if ((exception as? CancellationException) == null) {
            var6 = ExceptionsKt.CancellationException("CompletableFuture was completed exceptionally", exception)
         }

         var10001 = var6
         var10000 = `$this_setupCancellation`
      } else {
         var10001 = null
      }

      var10000.cancel(var10001)
      Unit.INSTANCE
   })
}

public fun <T> CompletionStage<Any>.asDeferred(): Deferred<Any> {
   val future: CompletableFuture = `$this$asDeferred`.toCompletableFuture()
   if (future.isDone()) {
      var var9: Deferred
      try {
         var9 = CompletableDeferredKt.CompletableDeferred(future.get())
      } catch (var8: java.lang.Throwable) {
         var var10: java.lang.Throwable
         run label36@{
            var10 = var8 as? ExecutionException
            if ((var8 as? ExecutionException) != null) {
               var10 = var10.getCause()
               if (var10 != null) {
                  return@label36
               }
            }

            var10 = var8
         }

         val var5: CompletableDeferred = CompletableDeferredKt.CompletableDeferred$default(null, 1, null)
         var5.completeExceptionally(var10)
         var9 = var5
      }

      return var9
   } else {
      val result: CompletableDeferred = CompletableDeferredKt.CompletableDeferred$default(null, 1, null)
      `$this$asDeferred`.handle({ p0: Any, p1: java.lang.Throwable ->
         `$tmp0`(p0, p1)
      })
      JobKt.cancelFutureOnCompletion(result, future)
      return result
   }
}

public suspend fun <T> CompletionStage<Any>.await(): Any {
   val future: CompletableFuture = `$this$await`.toCompletableFuture()
   if (future.isDone()) {
      try {
         return future.get()
      } catch (var10: ExecutionException) {
         var var11: java.lang.Throwable = var10.getCause()
         if (var11 == null) {
            var11 = var10
         }

         throw var11
      }
   } else {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
      `cancellable$iv`.initCancellability()
      val cont: CancellableContinuation = `cancellable$iv`
      val consumer: ContinuationHandler = ContinuationHandler(`cancellable$iv`)
      `$this$await`.handle(consumer)
      cont.invokeOnCancellation({ it: Throwable? ->
         future.cancel(false)
         consumer.cont = null
      } as (java.lang.Throwable?) -> Unit)
      val var10000: Any = `cancellable$iv`.getResult()
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return var10000
   }
}
