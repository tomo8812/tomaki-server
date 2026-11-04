@file:SourceDebugExtension(["SMAP\nDebugStrings.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DebugStrings.kt\nkotlinx/coroutines/DebugStringsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,22:1\n1#2:23\n*E\n"])

package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.DispatchedContinuation

internal final val hexAddress: String
   internal final get() {
      return Integer.toHexString(System.identityHashCode(`$this$hexAddress`))
   }


internal fun Continuation<*>.toDebugString(): String {
   val var10000: java.lang.String
   if (`$this$toDebugString` is DispatchedContinuation) {
      var10000 = `$this$toDebugString`.toString()
   } else {
      val var1: Continuation = `$this$toDebugString`

      var it: Any
      try {
         it = Result.constructor_impl/* $VF was: constructor-impl */("$var1@${hexAddress}")
      } catch (var4: java.lang.Throwable) {
         it = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var4))
      }

      var10000 = (
         if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(it) == null) it else "${`$this$toDebugString`.getClass().getName()}@${hexAddress}"
      ) as java.lang.String
   }

   return var10000
}

internal final val classSimpleName: String
   internal final get() {
      return `$this$classSimpleName`.getClass().getSimpleName()
   }

