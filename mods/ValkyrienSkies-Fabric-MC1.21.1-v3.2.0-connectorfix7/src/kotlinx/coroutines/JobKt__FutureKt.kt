@file:JvmMultifileClass
@file:JvmName("JobKt")

package kotlinx.coroutines

import java.util.concurrent.Future

@InternalCoroutinesApi
public fun Job.cancelFutureOnCompletion(future: Future<*>): DisposableHandle {
   return `$this$cancelFutureOnCompletion`.invokeOnCompletion(CancelFutureOnCompletion(future))
}

public fun CancellableContinuation<*>.cancelFutureOnCancellation(future: Future<*>) {
   `$this$cancelFutureOnCancellation`.invokeOnCancellation(CancelFutureOnCancel(future))
}
