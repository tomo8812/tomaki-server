package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation
import kotlin.internal.LowPriorityInOverloadResolution
import kotlinx.coroutines.internal.SystemPropsKt
import kotlinx.coroutines.selects.SelectClause1

public interface Channel<E> : SendChannel<E>, ReceiveChannel<E> {
   @JvmStatic
   Channel.Factory Factory = Channel.Factory.$$INSTANCE;

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @Deprecated(message = "Deprecated in the favour of 'trySend' method", replaceWith = @ReplaceWith(expression = "trySend(element).isSuccess", imports = []), level = DeprecationLevel.ERROR)
      @JvmStatic
      fun <E> offer(`$this`: Channel<E>, element: E): Boolean {
         SendChannel.DefaultImpls.offer(`$this`, (E)element)
      }

      /** @deprecated */
      @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
      @JvmStatic
      @JvmSynthetic
      fun cancel(`$this`: Channel) {
         ReceiveChannel.DefaultImpls.cancel(`$this`)
      }

      /** @deprecated */
      @Deprecated(message = "Deprecated in the favour of 'tryReceive'. Please note that the provided replacement does not rethrow channel's close cause as 'poll' did, for the precise replacement please refer to the 'poll' documentation", replaceWith = @ReplaceWith(expression = "tryReceive().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
      @JvmStatic
      fun <E> poll(`$this`: Channel<E>): E {
         ReceiveChannel.DefaultImpls.poll(`$this`)
      }

      /** @deprecated */
      @Deprecated(message = "Deprecated in favor of 'receiveCatching'. Please note that the provided replacement does not rethrow channel's close cause as 'receiveOrNull' did, for the detailed replacement please refer to the 'receiveOrNull' documentation", replaceWith = @ReplaceWith(expression = "receiveCatching().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
      @LowPriorityInOverloadResolution
      @JvmStatic
      fun <E> receiveOrNull(`$this`: Channel<E>, `$completion`: Continuation<in E>) {
         ReceiveChannel.DefaultImpls.receiveOrNull(`$this`, `$completion`)
      }

      /** @deprecated */
      @JvmStatic
      fun <E> getOnReceiveOrNull(`$this`: Channel<E>): SelectClause1<E> {
         ReceiveChannel.DefaultImpls.getOnReceiveOrNull(`$this`)
      }
   }

   public companion object Factory {
      @JvmStatic
      @JvmSynthetic
      Channel.Factory $$INSTANCE = Channel.Factory();
      public const val UNLIMITED: Int = Integer.MAX_VALUE
      public const val RENDEZVOUS: Int = 0
      public const val CONFLATED: Int = -1
      public const val BUFFERED: Int = -2
      internal const val OPTIONAL_CHANNEL: Int = -3
      public const val DEFAULT_BUFFER_PROPERTY_NAME: String = "kotlinx.coroutines.channels.defaultBuffer"
      internal final val CHANNEL_DEFAULT_CAPACITY: Int = SystemPropsKt.systemProp("kotlinx.coroutines.channels.defaultBuffer", 64, 1, 2147483646)
   }
}
