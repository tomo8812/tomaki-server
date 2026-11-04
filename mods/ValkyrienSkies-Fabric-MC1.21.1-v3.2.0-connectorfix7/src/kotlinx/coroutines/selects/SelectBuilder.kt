package kotlinx.coroutines.selects

import kotlin.coroutines.Continuation
import kotlin.internal.LowPriorityInOverloadResolution
import kotlinx.coroutines.ExperimentalCoroutinesApi

public sealed interface SelectBuilder<R> {
   public abstract operator fun SelectClause0.invoke(block: (Continuation<Any>) -> Any?) {
   }

   public abstract operator fun <Q> SelectClause1<Any>.invoke(block: (Any, Continuation<Any>) -> Any?) {
   }

   public abstract operator fun <P, Q> SelectClause2<Any, Any>.invoke(param: Any, block: (Any, Continuation<Any>) -> Any?) {
   }

   public open operator fun <P, Q> SelectClause2<Any?, Any>.invoke(block: (Any, Continuation<Any>) -> Any?) {
   }

   @Deprecated(message = "Replaced with the same extension function", replaceWith = @ReplaceWith(expression = "onTimeout", imports = ["kotlinx.coroutines.selects.onTimeout"]), level = DeprecationLevel.ERROR)
   @ExperimentalCoroutinesApi
   @LowPriorityInOverloadResolution
   public open fun onTimeout(timeMillis: Long, block: (Continuation<Any>) -> Any?) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <R, P, Q> invoke(`$this`: SelectBuilder<in R>, `$receiver`: SelectClause2<in P, out Q>, block: (Q?, Continuation<in R>?) -> Any) {
         `$this`.invoke(`$receiver`, null, block)
      }

      /** @deprecated */
      @Deprecated(message = "Replaced with the same extension function", replaceWith = @ReplaceWith(expression = "onTimeout", imports = ["kotlinx.coroutines.selects.onTimeout"]), level = DeprecationLevel.ERROR)
      @ExperimentalCoroutinesApi
      @LowPriorityInOverloadResolution
      @JvmStatic
      fun <R> onTimeout(`$this`: SelectBuilder<in R>, timeMillis: Long, block: (Continuation<in R>?) -> Any) {
         OnTimeoutKt.onTimeout(`$this`, timeMillis, block)
      }
   }
}
