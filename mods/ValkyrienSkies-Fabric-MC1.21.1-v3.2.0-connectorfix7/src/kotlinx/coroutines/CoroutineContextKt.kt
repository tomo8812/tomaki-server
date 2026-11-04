package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.internal.ThreadContextKt

private const val DEBUG_THREAD_NAME_SEPARATOR: String = " @"

@ExperimentalCoroutinesApi
public fun CoroutineScope.newCoroutineContext(context: CoroutineContext): CoroutineContext {
   val combined: CoroutineContext = foldCopies(`$this$newCoroutineContext`.coroutineContext, context, true)
   val debug: CoroutineContext = if (DEBUG) combined.plus(CoroutineId(COROUTINE_ID.incrementAndGet()) as CoroutineContext) else combined
   return if (combined != Default && combined.get(ContinuationInterceptor.Key as Key) == null) debug.plus(Default as CoroutineContext) else debug
}

@InternalCoroutinesApi
public fun CoroutineContext.newCoroutineContext(addedContext: CoroutineContext): CoroutineContext {
   return if (!hasCopyableElements(addedContext))
      `$this$newCoroutineContext`.plus(addedContext)
      else
      foldCopies(`$this$newCoroutineContext`, addedContext, false)
   }

private fun CoroutineContext.hasCopyableElements(): Boolean {
   return `$this$hasCopyableElements`.fold(false, { result, it ->
      result || it is CopyableThreadContextElement
   }) as java.lang.Boolean
}

private fun foldCopies(originalContext: CoroutineContext, appendContext: CoroutineContext, isNewCoroutine: Boolean): CoroutineContext {
   val hasElementsLeft: Boolean = hasCopyableElements(originalContext)
   val hasElementsRight: Boolean = hasCopyableElements(appendContext)
   if (!hasElementsLeft && !hasElementsRight) {
      return originalContext.plus(appendContext)
   } else {
      val leftoverContext: ObjectRef = ObjectRef()
      leftoverContext.element = appendContext
      val folded: CoroutineContext = originalContext.fold(
         EmptyCoroutineContext.INSTANCE,
         { result: CoroutineContext, element: Element ->
            if (element !is CopyableThreadContextElement) {
               return result.plus(element as CoroutineContext)
            } else {
               val newElement: Element = (leftoverContext.element as CoroutineContext).get(element.getKey())
               if (newElement == null) {
                  return result.plus(
                     (if (isNewCoroutine) (element as CopyableThreadContextElement).copyForChild() else element as CopyableThreadContextElement) as CoroutineContext
                  )
               } else {
                  leftoverContext.element = (leftoverContext.element as CoroutineContext).minusKey(element.getKey())
                  return result.plus((element as CopyableThreadContextElement).mergeForChild(newElement))
               }
            }
         } as Function2
      ) as CoroutineContext
      if (hasElementsRight) {
         leftoverContext.element = (leftoverContext.element as CoroutineContext)
            .fold(
               EmptyCoroutineContext.INSTANCE,
               { result, element ->
                  if (element is CopyableThreadContextElement)
                     result.plus((element as CopyableThreadContextElement).copyForChild() as CoroutineContext)
                     else
                     result.plus(element as CoroutineContext)
                  }
            )
         }

      return folded.plus(leftoverContext.element as CoroutineContext)
   }
}

internal inline fun <T> withCoroutineContext(context: CoroutineContext, countOrElement: Any?, block: () -> Any): Any {
   try {
      return (T)block()
   } finally {
      InlineMarker.finallyStart(1)
      ThreadContextKt.restoreThreadContext(context, ThreadContextKt.updateThreadContext(context, countOrElement))
      InlineMarker.finallyEnd(1)
   }
}

internal inline fun <T> withContinuationContext(continuation: Continuation<*>, countOrElement: Any?, block: () -> Any): Any {
   val context: CoroutineContext = continuation.getContext()
   val oldValue: Any = ThreadContextKt.updateThreadContext(context, countOrElement)
   val undispatchedCompletion: UndispatchedCoroutine = if (oldValue != ThreadContextKt.NO_THREAD_ELEMENTS)
      updateUndispatchedCompletion(continuation, context, oldValue)
      else
      null

   try {
      return (T)block()
   } finally {
      InlineMarker.finallyStart(1)
      if (undispatchedCompletion == null || undispatchedCompletion.clearThreadContext()) {
         ThreadContextKt.restoreThreadContext(context, oldValue)
      }

      InlineMarker.finallyEnd(1)
   }
}

internal fun Continuation<*>.updateUndispatchedCompletion(context: CoroutineContext, oldValue: Any?): UndispatchedCoroutine<*>? {
   if (`$this$updateUndispatchedCompletion` !is CoroutineStackFrame) {
      return null
   } else if (context.get(UndispatchedMarker.INSTANCE) == null) {
      return null
   } else {
      val completion: UndispatchedCoroutine = undispatchedCompletion(`$this$updateUndispatchedCompletion` as CoroutineStackFrame)
      if (completion != null) {
         completion.saveThreadContext(context, oldValue)
      }

      return completion
   }
}

internal tailrec fun CoroutineStackFrame.undispatchedCompletion(): UndispatchedCoroutine<*>? {
   while (`$this$undispatchedCompletion` !is DispatchedCoroutine) {
      val var10000: CoroutineStackFrame = `$this$undispatchedCompletion`.getCallerFrame()
      if (var10000 == null) {
         return null
      }

      if (var10000 is UndispatchedCoroutine) {
         return var10000 as UndispatchedCoroutine<*>
      }

      `$this$undispatchedCompletion` = var10000
   }

   return null
}

internal final val coroutineName: String?
   internal final get() {
      if (!DEBUG) {
         return null
      } else {
         val var10000: CoroutineId = `$this$coroutineName`.get(CoroutineId.Key) as CoroutineId
         if (var10000 == null) {
            return null
         } else {
            run label26@{
               val var3: CoroutineName = `$this$coroutineName`.get(CoroutineName.Key) as CoroutineName
               if (var3 != null) {
                  var4 = var3.name
                  if (var4 != null) {
                     return@label26
                  }
               }

               var4 = "coroutine"
            }

            return "$var4#${var10000.id}"
         }
      }
   }

