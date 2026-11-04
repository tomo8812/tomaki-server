package kotlinx.coroutines.channels

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.internal.Symbol

private final val NO_ELEMENT: Symbol = Symbol("NO_ELEMENT")

@Deprecated(message = "BroadcastChannel is deprecated in the favour of SharedFlow and StateFlow, and is no longer supported", level = DeprecationLevel.WARNING)
@ObsoleteCoroutinesApi
public fun <E> BroadcastChannel(capacity: Int): BroadcastChannel<Any> {
   var var10000: BroadcastChannel
   when (capacity) {
      -2 -> var10000 = BroadcastChannelImpl(Channel.Factory.CHANNEL_DEFAULT_CAPACITY)
      -1 -> var10000 = ConflatedBroadcastChannel()
      0 -> throw IllegalArgumentException("Unsupported 0 capacity for BroadcastChannel")
      Integer.MAX_VALUE -> throw IllegalArgumentException("Unsupported UNLIMITED capacity for BroadcastChannel")
      else -> var10000 = BroadcastChannelImpl(capacity)
   }

   return var10000
}

@JvmSynthetic
fun `access$getNO_ELEMENT$p`(): Symbol {
   NO_ELEMENT
}
