package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation

public interface ChannelIterator<E> {
   public abstract suspend operator fun hasNext(): Boolean {
   }

   @Deprecated(message = "Since 1.3.0, binary compatibility with versions <= 1.2.x", level = DeprecationLevel.HIDDEN)
   @JvmName(name = "next")
   @JvmSynthetic
   public open suspend fun next0(): Any {
   }

   public abstract operator fun next(): Any {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls
}
