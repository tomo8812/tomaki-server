package kotlinx.coroutines.channels

import kotlin.contracts.InvocationKind

public inline fun <T> ChannelResult<Any>.getOrElse(onFailure: (Throwable?) -> Any): Any {
   contract {
      callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
   }

   return (T)(if (`$this$getOrElse_u2dWpGqRn0` is ChannelResult.Failed)
      onFailure(ChannelResult.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$getOrElse_u2dWpGqRn0`))
      else
      `$this$getOrElse_u2dWpGqRn0`)
}

public inline fun <T> ChannelResult<Any>.onSuccess(action: (Any) -> Unit): ChannelResult<Any> {
   contract {
      callsInPlace(action, InvocationKind.AT_MOST_ONCE)
   }

   if (`$this$onSuccess_u2dWpGqRn0` !is ChannelResult.Failed) {
      action(`$this$onSuccess_u2dWpGqRn0`)
   }

   return `$this$onSuccess_u2dWpGqRn0`
}

public inline fun <T> ChannelResult<Any>.onFailure(action: (Throwable?) -> Unit): ChannelResult<Any> {
   contract {
      callsInPlace(action, InvocationKind.AT_MOST_ONCE)
   }

   if (`$this$onFailure_u2dWpGqRn0` is ChannelResult.Failed) {
      action(ChannelResult.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$onFailure_u2dWpGqRn0`))
   }

   return `$this$onFailure_u2dWpGqRn0`
}

public inline fun <T> ChannelResult<Any>.onClosed(action: (Throwable?) -> Unit): ChannelResult<Any> {
   contract {
      callsInPlace(action, InvocationKind.AT_MOST_ONCE)
   }

   if (`$this$onClosed_u2dWpGqRn0` is ChannelResult.Closed) {
      action(ChannelResult.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$onClosed_u2dWpGqRn0`))
   }

   return `$this$onClosed_u2dWpGqRn0`
}

public fun <E> Channel(capacity: Int = 0, onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND, onUndeliveredElement: ((Any) -> Unit)? = null): Channel<
      Any
   > {
   var var10000: Channel
   when (capacity) {
      -2 -> var10000 = if (onBufferOverflow === BufferOverflow.SUSPEND)
            BufferedChannel(Channel.Factory.CHANNEL_DEFAULT_CAPACITY, onUndeliveredElement)
            else
            ConflatedBufferedChannel(1, onBufferOverflow, onUndeliveredElement)
         -1 -> {
         if (onBufferOverflow != BufferOverflow.SUSPEND) {
            throw IllegalArgumentException("CONFLATED capacity cannot be used with non-default onBufferOverflow".toString())
         }

         var10000 = ConflatedBufferedChannel(1, BufferOverflow.DROP_OLDEST, onUndeliveredElement)
         break
      }
      0 -> var10000 = if (onBufferOverflow === BufferOverflow.SUSPEND)
            BufferedChannel(0, onUndeliveredElement)
            else
            ConflatedBufferedChannel(1, onBufferOverflow, onUndeliveredElement)
         Integer.MAX_VALUE -> var10000 = BufferedChannel(Integer.MAX_VALUE, onUndeliveredElement)
      else -> var10000 = if (onBufferOverflow === BufferOverflow.SUSPEND)
            BufferedChannel(capacity, onUndeliveredElement)
            else
            ConflatedBufferedChannel(capacity, onBufferOverflow, onUndeliveredElement)
      }

   return var10000
}

@Deprecated(message = "Since 1.4.0, binary compatibility with earlier versions", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> Channel(capacity: Int = 0): Channel<Any> {
   return Channel$default(capacity, null, null, 6, null)
}
