package kotlinx.coroutines.selects

import kotlin.coroutines.CoroutineContext
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics

@SourceDebugExtension(["SMAP\nOnTimeout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OnTimeout.kt\nkotlinx/coroutines/selects/OnTimeout\n+ 2 Runnable.kt\nkotlinx/coroutines/RunnableKt\n*L\n1#1,66:1\n17#2:67\n*S KotlinDebug\n*F\n+ 1 OnTimeout.kt\nkotlinx/coroutines/selects/OnTimeout\n*L\n56#1:67\n*E\n"])
private class OnTimeout(timeMillis: Long) {
   private final val timeMillis: Long

   init {
      this.timeMillis = timeMillis
   }

   public final val selectClause: SelectClause0
      public final get() {
         val var10003: <unrepresentable> = <unrepresentable>.INSTANCE
         return SelectClause0Impl(this, TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10003, 3) as Function3, null, 4, null)
      }


   private fun register(select: SelectInstance<*>, ignoredParam: Any?) {
      if (this.timeMillis <= 0L) {
         select.selectInRegistrationPhase(Unit.INSTANCE)
      } else {
         val action: Runnable = OnTimeout$register$$inlined$Runnable$1(select, this)
         val var6: CoroutineContext = select.context
         select.disposeOnCompletion(delay.invokeOnTimeout(this.timeMillis, action, var6))
      }
   }
}
