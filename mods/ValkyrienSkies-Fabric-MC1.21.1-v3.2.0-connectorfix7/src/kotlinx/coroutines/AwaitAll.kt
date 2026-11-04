package kotlinx.coroutines

import java.util.ArrayList
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nAwait.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Await.kt\nkotlinx/coroutines/AwaitAll\n+ 2 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n+ 3 CompletionHandler.kt\nkotlinx/coroutines/CompletionHandlerKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,127:1\n314#2,9:128\n323#2,2:141\n13#3:137\n19#3:140\n13579#4,2:138\n*S KotlinDebug\n*F\n+ 1 Await.kt\nkotlinx/coroutines/AwaitAll\n*L\n71#1:128,9\n71#1:141,2\n78#1:137\n90#1:140\n83#1:138,2\n*E\n"])
private class AwaitAll<T>(vararg deferreds: Any) {
   private final val deferreds: Array<out Deferred<Any>>
   @Volatile
   @Volatile
   private int notCompletedCount;
   @JvmStatic
   private AtomicIntegerFieldUpdater notCompletedCount$FU = AtomicIntegerFieldUpdater.newUpdater(AwaitAll.class, "notCompletedCount");

   init {
      this.deferreds = deferreds
      this.notCompletedCount = this.deferreds.length
   }

   public suspend fun await(): List<Any> {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
      `cancellable$iv`.initCancellability()
      val cont: CancellableContinuation = `cancellable$iv`
      var disposer: Int = 0
      val `$this$asHandler$iv`: Int = access$getDeferreds$p(this).length
      val `$i$f$getAsHandler`: Array<Any> = arrayOfNulls(`$this$asHandler$iv`)

      while (disposer < `$this$asHandler$iv`) {
         val deferred: Deferred = access$getDeferreds$p(this)[disposer]
         deferred.start()
         val `element$iv`: AwaitAll.AwaitAllNode = AwaitAll.AwaitAllNode(cont)
         `element$iv`.handle = deferred.invokeOnCompletion(`element$iv`)
         `$i$f$getAsHandler`[disposer] = `element$iv`
         disposer++
      }

      val var22: AwaitAll.DisposeHandlersOnCancel = AwaitAll.DisposeHandlersOnCancel(`$i$f$getAsHandler`)

      for (var29 in `$i$f$getAsHandler`) {
         var29.disposer = var22
      }

      if (cont.isCompleted) {
         var22.disposeAll()
      } else {
         cont.invokeOnCancellation(var22)
      }

      val var10000: Any = `cancellable$iv`.getResult()
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return var10000
   }

   @SourceDebugExtension(["SMAP\nAwait.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Await.kt\nkotlinx/coroutines/AwaitAll$AwaitAllNode\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,127:1\n11335#2:128\n11670#2,3:129\n*S KotlinDebug\n*F\n+ 1 Await.kt\nkotlinx/coroutines/AwaitAll$AwaitAllNode\n*L\n121#1:128\n121#1:129,3\n*E\n"])
   private inner class AwaitAllNode(continuation: CancellableContinuation<List<Any>>) : JobNode {
      private final val continuation: CancellableContinuation<List<Any>>
      public final lateinit var handle: DisposableHandle
      @Volatile
      @Volatile
      private Object _disposer;
      @JvmStatic
      private AtomicReferenceFieldUpdater _disposer$FU = AtomicReferenceFieldUpdater.newUpdater(AwaitAll.AwaitAllNode.class, Object.class, "_disposer");

      init {
         this.continuation = continuation
      }

      public final var disposer: kotlinx.coroutines.AwaitAll.DisposeHandlersOnCancel?
         public final get() {
            return _disposer$FU.get(this) as AwaitAll.DisposeHandlersOnCancel
         }

         public final set(value) {
            _disposer$FU.set(this, value)
         }


      public override operator fun invoke(cause: Throwable?) {
         if (cause != null) {
            val token: Any = this.continuation.tryResumeWithException(cause)
            if (token != null) {
               this.continuation.completeResume(token)
               val var10000: AwaitAll.DisposeHandlersOnCancel = this.disposer
               if (var10000 != null) {
                  var10000.disposeAll()
               }
            }
         } else if (AwaitAll.notCompletedCount$FU.decrementAndGet(AwaitAll.this) == 0) {
            val var14: Continuation = this.continuation
            val `$this$map$iv`: Array<Any> = AwaitAll.this.deferreds
            val `destination$iv$iv`: java.util.Collection = ArrayList(`$this$map$iv`.length)

            for (`item$iv$iv` in `$this$map$iv`) {
               `destination$iv$iv`.add(((Deferred)`item$iv$iv`).getCompleted())
            }

            var14.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(`destination$iv$iv` as java.util.List))
         }
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      fun invoke(p1: Any) {
         this.invoke(p1 as java.lang.Throwable)
         Unit.INSTANCE
      }
   }

   @SourceDebugExtension(["SMAP\nAwait.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Await.kt\nkotlinx/coroutines/AwaitAll$DisposeHandlersOnCancel\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,127:1\n13579#2,2:128\n*S KotlinDebug\n*F\n+ 1 Await.kt\nkotlinx/coroutines/AwaitAll$DisposeHandlersOnCancel\n*L\n96#1:128,2\n*E\n"])
   private inner class DisposeHandlersOnCancel(vararg nodes: Any) : CancelHandler {
      private final val nodes: Array<kotlinx.coroutines.AwaitAll.AwaitAllNode>

      init {
         this.nodes = nodes
      }

      public fun disposeAll() {
         for (`element$iv` in this.nodes) {
            `element$iv`.handle.dispose()
         }
      }

      public override operator fun invoke(cause: Throwable?) {
         this.disposeAll()
      }

      public override fun toString(): String {
         return "DisposeHandlersOnCancel[${this.nodes}]"
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      fun invoke(p1: Any) {
         this.invoke(p1 as java.lang.Throwable)
         Unit.INSTANCE
      }
   }
}
