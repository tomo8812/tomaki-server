@file:SourceDebugExtension(["SMAP\nExceptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Exceptions.kt\nkotlinx/coroutines/ExceptionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,76:1\n1#2:77\n*E\n"])

package kotlinx.coroutines

import java.util.concurrent.CancellationException
import kotlin.jvm.internal.SourceDebugExtension

public fun CancellationException(message: String?, cause: Throwable?): CancellationException {
   val var2: CancellationException = CancellationException(message)
   var2.initCause(cause)
   return var2
}

internal inline fun Throwable.addSuppressedThrowable(other: Throwable) {
   kotlin.ExceptionsKt.addSuppressed(`$this$addSuppressedThrowable`, other)
}
