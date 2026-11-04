package kotlinx.coroutines.channels

import java.util.concurrent.CancellationException
import kotlinx.coroutines.ObsoleteCoroutinesApi

/** @deprecated */
@Deprecated(message = "BroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported", level = DeprecationLevel.WARNING)
@ObsoleteCoroutinesApi
public interface BroadcastChannel<E> : SendChannel<E> {
   public abstract fun openSubscription(): ReceiveChannel<Any> {
   }

   public abstract fun cancel(cause: CancellationException? = ...) {
   }

   @Deprecated(message = "Binary compatibility only", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public abstract fun cancel(cause: Throwable? = ...): Boolean {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      @JvmSynthetic
      fun `cancel$default`(var0: BroadcastChannel, var1: CancellationException, var2: Int, var3: Any) {
         if (var3 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: cancel")
         } else {
            if ((var2 and 1) != 0) {
               var1 = null
            }

            var0.cancel(var1)
         }
      }

      /** @deprecated */
      @JvmStatic
      @JvmSynthetic
      fun `cancel$default`(var0: BroadcastChannel, var1: java.lang.Throwable, var2: Int, var3: Any): Boolean {
         if (var3 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: cancel")
         } else {
            if ((var2 and 1) != 0) {
               var1 = null
            }

            var0.cancel(var1)
         }
      }

      /** @deprecated */
      @Deprecated(message = "Deprecated in the favour of 'trySend' method", replaceWith = @ReplaceWith(expression = "trySend(element).isSuccess", imports = []), level = DeprecationLevel.ERROR)
      @JvmStatic
      fun <E> offer(`$this`: BroadcastChannel<E>, element: E): Boolean {
         SendChannel.DefaultImpls.offer(`$this`, (E)element)
      }
   }
}
