package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation
import kotlin.internal.LowPriorityInOverloadResolution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.selects.SelectClause1

@ObsoleteCoroutinesApi
public interface ActorScope<E> : CoroutineScope, ReceiveChannel<E> {
   public val channel: Channel<Any>

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
      @JvmStatic
      @JvmSynthetic
      fun cancel(`$this`: ActorScope) {
         ReceiveChannel.DefaultImpls.cancel(`$this`)
      }

      /** @deprecated */
      @Deprecated(message = "Deprecated in the favour of 'tryReceive'. Please note that the provided replacement does not rethrow channel's close cause as 'poll' did, for the precise replacement please refer to the 'poll' documentation", replaceWith = @ReplaceWith(expression = "tryReceive().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
      @JvmStatic
      fun <E> poll(`$this`: ActorScope<E>): E {
         ReceiveChannel.DefaultImpls.poll(`$this`)
      }

      /** @deprecated */
      @Deprecated(message = "Deprecated in favor of 'receiveCatching'. Please note that the provided replacement does not rethrow channel's close cause as 'receiveOrNull' did, for the detailed replacement please refer to the 'receiveOrNull' documentation", replaceWith = @ReplaceWith(expression = "receiveCatching().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
      @LowPriorityInOverloadResolution
      @JvmStatic
      fun <E> receiveOrNull(`$this`: ActorScope<E>, `$completion`: Continuation<in E>) {
         ReceiveChannel.DefaultImpls.receiveOrNull(`$this`, `$completion`)
      }

      /** @deprecated */
      @JvmStatic
      fun <E> getOnReceiveOrNull(`$this`: ActorScope<E>): SelectClause1<E> {
         ReceiveChannel.DefaultImpls.getOnReceiveOrNull(`$this`)
      }
   }
}
