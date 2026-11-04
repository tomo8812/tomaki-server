@file:SourceDebugExtension(["SMAP\nStackTraceRecovery.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,216:1\n1735#2,6:217\n12744#2,2:225\n1627#2,6:229\n12744#2,2:235\n1627#2,6:238\n37#3,2:223\n26#4:227\n26#4:228\n1#5:237\n*S KotlinDebug\n*F\n+ 1 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n*L\n43#1:217,6\n131#1:225,2\n141#1:229,6\n173#1:235,2\n194#1:238,6\n106#1:223,2\n133#1:227\n135#1:228\n*E\n"])

package kotlinx.coroutines.internal

import _COROUTINE.ArtificialStackFrames
import java.util.ArrayDeque
import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension

private const val baseContinuationImplClass: String = "kotlin.coroutines.jvm.internal.BaseContinuationImpl"
private const val stackTraceRecoveryClass: String = "kotlinx.coroutines.internal.StackTraceRecoveryKt"
private final val ARTIFICIAL_FRAME: StackTraceElement = ArtificialStackFrames().coroutineBoundary()
private final val baseContinuationImplClassName: String
private final val stackTraceRecoveryClassName: String

internal fun <E : Throwable> recoverStackTrace(exception: Any): Any {
   label16@
   if (!RECOVER_STACK_TRACES) {
      return (E)exception
   } else {
      val var10000: java.lang.Throwable = ExceptionsConstructorKt.tryCopyException(exception)
      return (E)(if (var10000 == null) exception else sanitizeStackTrace(var10000))
   }
}

private fun <E : Throwable> Any.sanitizeStackTrace(): Any {
   var stackTrace: Array<StackTraceElement>
   var size: Int
   var var10000: Int
   run label45@{
      stackTrace = `$this$sanitizeStackTrace`.getStackTrace()
      size = stackTrace.length
      val startIndex: Array<Any> = stackTrace
      var adjustment: Int = stackTrace.length + -1
      if (0 <= stackTrace.length + -1) {
         do {
            val trace: Int = adjustment--
            if (stackTraceRecoveryClassName == startIndex[trace].getClassName()) {
               var10000 = trace
               return@label45
            }
         } while (0 <= adjustment)
      }

      var10000 = -1
   }

   val var12: Int = var10000 + 1
   val var13: Int = firstFrameIndex(stackTrace, baseContinuationImplClassName)
   val var14: Int = if (var13 == -1) 0 else size - var13
   var var16: Int = 0
   val var17: Int = size - var10000 - var14
   val var10: Array<StackTraceElement> = arrayOfNulls(size - var10000 - var14)

   while (var16 < var17) {
      var10[var16] = if (var16 == 0) ARTIFICIAL_FRAME else stackTrace[var12 + var16 - 1]
      var16++
   }

   `$this$sanitizeStackTrace`.setStackTrace(var10)
   return (E)`$this$sanitizeStackTrace`
}

internal inline fun <E : Throwable> recoverStackTrace(exception: Any, continuation: Continuation<*>): Any {
   return (E)(if (RECOVER_STACK_TRACES && continuation is CoroutineStackFrame)
      access$recoverFromStackFrame(exception, continuation as CoroutineStackFrame)
      else
      exception)
}

private fun <E : Throwable> recoverFromStackFrame(exception: Any, continuation: CoroutineStackFrame): Any {
   val var2: Pair = causeAndStacktrace(exception)
   val cause: java.lang.Throwable = var2.component1() as java.lang.Throwable
   val recoveredStacktrace: Array<StackTraceElement> = var2.component2() as Array<StackTraceElement>
   val var10000: java.lang.Throwable = ExceptionsConstructorKt.tryCopyException(cause)
   if (var10000 == null) {
      return (E)exception
   } else {
      val stacktrace: ArrayDeque = createStackTrace(continuation)
      if (stacktrace.isEmpty()) {
         return (E)exception
      } else {
         if (cause != exception) {
            mergeRecoveredTraces(recoveredStacktrace, stacktrace)
         }

         return (E)createFinalException(cause, var10000, stacktrace)
      }
   }
}

private fun <E : Throwable> createFinalException(cause: Any, result: Any, resultStackTrace: ArrayDeque<StackTraceElement>): Any {
   resultStackTrace.addFirst(ARTIFICIAL_FRAME)
   val causeTrace: Array<StackTraceElement> = cause.getStackTrace()
   val size: Int = firstFrameIndex(causeTrace, baseContinuationImplClassName)
   if (size == -1) {
      result.setStackTrace(resultStackTrace.toArray(arrayOfNulls(0)))
      return (E)result
   } else {
      val mergedStackTrace: Array<StackTraceElement> = arrayOfNulls(resultStackTrace.size() + size)

      repeat(size) { i ->
         mergedStackTrace[i] = causeTrace[i]
      }

      val var11: java.util.Iterator = resultStackTrace.iterator()
      var `thisCollection$iv`: Int = 0

      while (var11.hasNext()) {
         mergedStackTrace[size + `thisCollection$iv`++] = var11.next() as StackTraceElement
      }

      result.setStackTrace(mergedStackTrace)
      return (E)result
   }
}

private fun <E : Throwable> Any.causeAndStacktrace(): Pair<Any, Array<StackTraceElement>> {
   val cause: java.lang.Throwable = `$this$causeAndStacktrace`.getCause()
   val var10000: Pair
   if (cause != null && cause.getClass() == `$this$causeAndStacktrace`.getClass()) {
      val var10: Array<StackTraceElement> = `$this$causeAndStacktrace`.getStackTrace()
      val `$i$f$emptyArray`: Array<Any> = var10
      var var5: Int = 0
      val var6: Int = var10.length

      while (true) {
         if (var5 >= var6) {
            var12 = false
            break
         }

         if (isArtificial((StackTraceElement)`$i$f$emptyArray`[var5])) {
            var12 = true
            break
         }

         var5++
      }

      var10000 = if (var12) TuplesKt.to(cause, var10) else TuplesKt.to(`$this$causeAndStacktrace`, arrayOfNulls(0))
   } else {
      var10000 = TuplesKt.to(`$this$causeAndStacktrace`, arrayOfNulls(0))
   }

   return var10000
}

private fun mergeRecoveredTraces(recoveredStacktrace: Array<StackTraceElement>, result: ArrayDeque<StackTraceElement>) {
   val lastFrameIndex: Array<Any> = recoveredStacktrace
   var element: Int = 0
   val var6: Int = recoveredStacktrace.length

   var var10000: Int
   while (true) {
      if (element >= var6) {
         var10000 = -1
         break
      }

      if (isArtificial((StackTraceElement)lastFrameIndex[element])) {
         var10000 = element
         break
      }

      element++
   }

   val startIndex: Int = var10000 + 1
   var var10: Int = recoveredStacktrace.length - 1
   if (startIndex <= recoveredStacktrace.length - 1) {
      while (true) {
         if (elementWiseEquals(recoveredStacktrace[var10], result.getLast() as StackTraceElement)) {
            result.removeLast()
         }

         result.addFirst(recoveredStacktrace[var10])
         if (var10 == startIndex) {
            break
         }

         var10--
      }
   }
}

internal suspend inline fun recoverAndThrow(exception: Throwable): Nothing {
   if (!RECOVER_STACK_TRACES) {
      throw exception
   } else if (`$completion` !is CoroutineStackFrame) {
      throw exception
   } else {
      throw access$recoverFromStackFrame(exception, `$completion` as CoroutineStackFrame)
   }
}

fun `recoverAndThrow$$forInline`(exception: java.lang.Throwable, `$completion`: Continuation<*>) {
   if (!RECOVER_STACK_TRACES) {
      throw exception
   } else {
      InlineMarker.mark(0)
      val it: Continuation = `$completion`
      if (`$completion` !is CoroutineStackFrame) {
         throw exception
      } else {
         throw access$recoverFromStackFrame(exception, it as CoroutineStackFrame)
      }
   }
}

@PublishedApi
internal inline fun <E : Throwable> unwrap(exception: Any): Any {
   return (E)(if (!RECOVER_STACK_TRACES) exception else unwrapImpl(exception))
}

@PublishedApi
internal fun <E : Throwable> unwrapImpl(exception: Any): Any {
   val cause: java.lang.Throwable = exception.getCause()
   if (cause != null && cause.getClass() == exception.getClass()) {
      val `$this$any$iv`: Array<Any> = exception.getStackTrace()
      var var4: Int = 0
      val var5: Int = `$this$any$iv`.length

      var var10000: Boolean
      while (true) {
         if (var4 >= var5) {
            var10000 = false
            break
         }

         if (isArtificial((StackTraceElement)`$this$any$iv`[var4])) {
            var10000 = true
            break
         }

         var4++
      }

      return (E)(if (var10000) cause else exception)
   } else {
      return (E)exception
   }
}

private fun createStackTrace(continuation: CoroutineStackFrame): ArrayDeque<StackTraceElement> {
   val stack: ArrayDeque = ArrayDeque()
   var var10000: StackTraceElement = continuation.getStackTraceElement()
   if (var10000 != null) {
      stack.add(var10000)
   }

   var last: CoroutineStackFrame = continuation

   while (true) {
      val var8: CoroutineStackFrame = if (last is CoroutineStackFrame) last else null
      if ((if (last is CoroutineStackFrame) last else null) == null) {
         break
      }

      val var9: CoroutineStackFrame = var8.getCallerFrame()
      if (var9 == null) {
         break
      }

      last = var9
      var10000 = var9.getStackTraceElement()
      if (var10000 != null) {
         stack.add(var10000)
      }
   }

   return stack
}

internal fun StackTraceElement.isArtificial(): Boolean {
   return StringsKt.startsWith$default(`$this$isArtificial`.getClassName(), ARTIFICIAL_FRAME_PACKAGE_NAME, false, 2, null)
}

private fun Array<StackTraceElement>.firstFrameIndex(methodName: String): Int {
   val `$this$indexOfFirst$iv`: Array<Any> = `$this$firstFrameIndex`
   var `index$iv`: Int = 0
   val var5: Int = `$this$firstFrameIndex`.length

   var var10000: Int
   while (true) {
      if (`index$iv` >= var5) {
         var10000 = -1
         break
      }

      if (methodName == `$this$indexOfFirst$iv`[`index$iv`].getClassName()) {
         var10000 = `index$iv`
         break
      }

      `index$iv`++
   }

   return var10000
}

private fun StackTraceElement.elementWiseEquals(e: StackTraceElement): Boolean {
   return `$this$elementWiseEquals`.getLineNumber() == e.getLineNumber()
      && `$this$elementWiseEquals`.getMethodName() == e.getMethodName()
      && `$this$elementWiseEquals`.getFileName() == e.getFileName()
      && `$this$elementWiseEquals`.getClassName() == e.getClassName()
   }

internal fun Throwable.initCause(cause: Throwable) {
   `$this$initCause`.initCause(cause)
}

/** @deprecated */
@JvmSynthetic
fun `CoroutineStackFrame$annotations`() {
}

/** @deprecated */
@JvmSynthetic
fun `StackTraceElement$annotations`() {
}

@JvmSynthetic
fun `access$recoverFromStackFrame`(exception: java.lang.Throwable, continuation: CoroutineStackFrame): java.lang.Throwable {
   recoverFromStackFrame(exception, continuation)
}

fun {
   var var0: Any
   try {
      var0 = Result.constructor_impl/* $VF was: constructor-impl */(Class.forName("kotlin.coroutines.jvm.internal.BaseContinuationImpl").getCanonicalName())
   } catch (var4: java.lang.Throwable) {
      var0 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var4))
   }

   baseContinuationImplClassName = (
      if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(var0) == null) var0 else "kotlin.coroutines.jvm.internal.BaseContinuationImpl"
   ) as java.lang.String

   try {
      var0 = Result.constructor_impl/* $VF was: constructor-impl */(Class.forName("kotlinx.coroutines.internal.StackTraceRecoveryKt").getCanonicalName())
   } catch (var3: java.lang.Throwable) {
      var0 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var3))
   }

   stackTraceRecoveryClassName = (
      if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(var0) == null) var0 else "kotlinx.coroutines.internal.StackTraceRecoveryKt"
   ) as java.lang.String
}
