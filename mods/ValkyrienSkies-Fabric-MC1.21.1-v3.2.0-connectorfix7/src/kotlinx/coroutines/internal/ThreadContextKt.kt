package kotlinx.coroutines.internal

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.ThreadContextElement
import org.jetbrains.annotations.NotNull

@JvmField
@NotNull
internal final val NO_THREAD_ELEMENTS: Symbol = Symbol("NO_THREAD_ELEMENTS")

private final val countAll: (Any?, Element) -> Any? = { countOrElement, element ->
   if (element is ThreadContextElement) {
      val inCount: Int = if ((countOrElement as? Int) != null) countOrElement as? Int else 1
      if (inCount == 0) element else inCount + 1
   } else {
      countOrElement
   }
} as Function2

private final val findOne: (ThreadContextElement<*>?, Element) -> ThreadContextElement<*>? = { found, element ->
   if (found != null) {
      found
   } else {
      element as? ThreadContextElement
   }
} as Function2

private final val updateState: (ThreadState, Element) -> ThreadState = { state, element ->
   if (element is ThreadContextElement) {
      state.append(element as ThreadContextElement<*>, (element as ThreadContextElement).updateThreadContext(state.context))
   }

   state
} as Function2

internal fun threadContextElements(context: CoroutineContext): Any {
   val var10000: Any = context.fold(0, countAll)
   return var10000
}

internal fun updateThreadContext(context: CoroutineContext, countOrElement: Any?): Any? {
   var countOrElementx: Any = countOrElement
   if (countOrElement == null) {
      countOrElementx = threadContextElements(context)
   }

   if (countOrElementx === 0) {
      countOrElementx = NO_THREAD_ELEMENTS
   } else if (countOrElementx is Int) {
      countOrElementx = context.fold(ThreadState(context, (countOrElementx as java.lang.Number).intValue()), updateState)
   } else {
      countOrElementx = (countOrElementx as ThreadContextElement).updateThreadContext(context)
   }

   return countOrElementx
}

internal fun restoreThreadContext(context: CoroutineContext, oldState: Any?) {
   if (oldState != NO_THREAD_ELEMENTS) {
      if (oldState is ThreadState) {
         (oldState as ThreadState).restore(context)
      } else {
         val var10000: Any = context.fold(null, findOne)
         (var10000 as ThreadContextElement).restoreThreadContext(context, oldState)
      }
   }
}
