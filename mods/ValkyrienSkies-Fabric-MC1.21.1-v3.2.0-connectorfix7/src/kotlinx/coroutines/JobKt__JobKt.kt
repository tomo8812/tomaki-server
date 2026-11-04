@file:JvmMultifileClass
@file:JvmName("JobKt")
@file:SourceDebugExtension(["SMAP\nJob.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Job.kt\nkotlinx/coroutines/JobKt__JobKt\n+ 2 CompletionHandler.kt\nkotlinx/coroutines/CompletionHandlerKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,685:1\n13#2:686\n1295#3,2:687\n1295#3,2:689\n1295#3,2:691\n1295#3,2:693\n*S KotlinDebug\n*F\n+ 1 Job.kt\nkotlinx/coroutines/JobKt__JobKt\n*L\n494#1:686\n521#1:687,2\n535#1:689,2\n629#1:691,2\n653#1:693,2\n*E\n"])

package kotlinx.coroutines

import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension

public fun Job(parent: Job? = null): CompletableJob {
   return JobImpl(parent)
}

@Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
@JvmName(name = "Job")
@JvmSynthetic
public fun Job0(parent: Job? = ...): Job {
   return JobKt.Job(parent)
}

/** @deprecated */
@JvmSynthetic
fun `Job$default`(var0: Job, var1: Int, var2: Any): Job {
   if ((var1 and 1) != 0) {
      var0 = null
   }

   JobKt.Job(var0)
}

internal fun Job.disposeOnCompletion(handle: DisposableHandle): DisposableHandle {
   return `$this$disposeOnCompletion`.invokeOnCompletion(DisposeOnCompletion(handle))
}

public suspend fun Job.cancelAndJoin() {
   Job.DefaultImpls.cancel$default(`$this$cancelAndJoin`, (CancellationException)null, 1, null)
   val var10000: Any = `$this$cancelAndJoin`.join(`$completion`)
   return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
}

public fun Job.cancelChildren(cause: CancellationException? = null) {
   for (`element$iv` in `$this$cancelChildren`.children) {
      (`element$iv` as Job).cancel(cause)
   }
}

@Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun Job.cancelChildren() {
   JobKt.cancelChildren(`$this$cancelChildren`, (CancellationException)null)
}

@Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun Job.cancelChildren(cause: Throwable? = null) {
   for (`element$iv` in `$this$cancelChildren`.children) {
      val it: Job = `element$iv` as Job
      val var10000: JobSupport = `element$iv` as Job as? JobSupport
      if ((it as? JobSupport) != null) {
         var10000.cancelInternal(orCancellation$JobKt__JobKt(cause, `$this$cancelChildren`))
      }
   }
}

public final val isActive: Boolean
   public final get() {
      val var10000: Job = `$this$isActive`.get(Job.Key) as Job
      return var10000 == null || var10000.isActive
   }


public fun CoroutineContext.cancel(cause: CancellationException? = null) {
   val var10000: Job = `$this$cancel`.get(Job.Key) as Job
   if (var10000 != null) {
      var10000.cancel(cause)
   }
}

@Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun CoroutineContext.cancel() {
   JobKt.cancel(`$this$cancel`, (CancellationException)null)
}

public fun Job.ensureActive() {
   if (!`$this$ensureActive`.isActive) {
      throw `$this$ensureActive`.getCancellationException()
   }
}

public fun CoroutineContext.ensureActive() {
   val var10000: Job = `$this$ensureActive`.get(Job.Key) as Job
   if (var10000 != null) {
      JobKt.ensureActive(var10000)
   }
}

public fun Job.cancel(message: String, cause: Throwable? = null) {
   `$this$cancel`.cancel(ExceptionsKt.CancellationException(message, cause))
}

@Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun CoroutineContext.cancel(cause: Throwable? = null): Boolean {
   val var3: Element = `$this$cancel`.get(Job.Key)
   val var10000: JobSupport = var3 as? JobSupport
   if ((var3 as? JobSupport) == null) {
      return false
   } else {
      var10000.cancelInternal(orCancellation$JobKt__JobKt(cause, var10000))
      return true
   }
}

public fun CoroutineContext.cancelChildren(cause: CancellationException? = null) {
   val var10000: Job = `$this$cancelChildren`.get(Job.Key) as Job
   if (var10000 != null) {
      val var8: Sequence = var10000.children
      if (var8 != null) {
         for (`element$iv` in var8) {
            (`element$iv` as Job).cancel(cause)
         }
      }
   }
}

@Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun CoroutineContext.cancelChildren() {
   JobKt.cancelChildren(`$this$cancelChildren`, (CancellationException)null)
}

public final val job: Job
   public final get() {
      val var10000: Job = `$this$job`.get(Job.Key) as Job
      if (var10000 == null) {
         throw IllegalStateException(("Current context doesn't contain Job in it: $`$this$job`").toString())
      } else {
         return var10000
      }
   }


@Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun CoroutineContext.cancelChildren(cause: Throwable? = null) {
   var var10000: JobSupport = `$this$cancelChildren`.get(Job.Key) as Job
   if (var10000 != null) {
      val job: Job = var10000

      for (`element$iv` in var10000.children) {
         val it: Job = `element$iv` as Job
         var10000 = `element$iv` as Job as? JobSupport
         if ((it as? JobSupport) != null) {
            var10000.cancelInternal(orCancellation$JobKt__JobKt(cause, job))
         }
      }
   }
}

private fun Throwable?.orCancellation(job: Job): Throwable {
   var var10000: java.lang.Throwable = `$this$orCancellation`
   if (`$this$orCancellation` == null) {
      var10000 = JobCancellationException("Job was cancelled", null, job)
   }

   return var10000
}
