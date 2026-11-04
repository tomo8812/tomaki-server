@file:SourceDebugExtension(["SMAP\nThreadContextElement.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ThreadContextElement.kt\nkotlinx/coroutines/ThreadContextElementKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,288:1\n266#1:289\n1#2:290\n*S KotlinDebug\n*F\n+ 1 ThreadContextElement.kt\nkotlinx/coroutines/ThreadContextElementKt\n*L\n287#1:289\n*E\n"])

package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.ThreadLocalElement
import kotlinx.coroutines.internal.ThreadLocalKey

public fun <T> ThreadLocal<Any>.asContextElement(value: Any = `$this$asContextElement`.get()): ThreadContextElement<Any> {
   return (ThreadContextElement<T>)ThreadLocalElement<>(value, `$this$asContextElement`)
}

public suspend inline fun ThreadLocal<*>.isPresent(): Boolean {
   return Boxing.boxBoolean(`$completion`.getContext().get(ThreadLocalKey(`$this$isPresent`)) != null)
}

fun ThreadLocal<*>.`isPresent$$forInline`(`$completion`: Continuation<? super java.lang.Boolean>) {
   InlineMarker.mark(3)
   null.getContext().get(ThreadLocalKey(`$this$isPresent`)) != null
}

public suspend inline fun ThreadLocal<*>.ensurePresent() {
   if (`$completion`.getContext().get(ThreadLocalKey(`$this$ensurePresent`)) == null) {
      throw IllegalStateException(("ThreadLocal $`$this$ensurePresent` is missing from context ${`$completion`.getContext()}").toString())
   } else {
      return Unit.INSTANCE
   }
}

fun ThreadLocal<*>.`ensurePresent$$forInline`(`$completion`: Continuation<in Unit>) {
   InlineMarker.mark(3)
   if (!null.getContext().get(ThreadLocalKey(`$this$ensurePresent`)) != null) {
      val var10000: StringBuilder = StringBuilder().append("ThreadLocal ").append(`$this$ensurePresent`).append(" is missing from context ")
      InlineMarker.mark(3)
      throw IllegalStateException(var10000.append(null.getContext()).toString().toString())
   } else {
      Unit.INSTANCE
   }
}
