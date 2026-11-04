package kotlinx.coroutines.channels

import java.util.concurrent.CancellationException
import kotlin.coroutines.Continuation
import kotlin.internal.LowPriorityInOverloadResolution
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.internal.StackTraceRecoveryKt
import kotlinx.coroutines.selects.SelectClause1

public interface ReceiveChannel<E> {
   public val isClosedForReceive: Boolean

   public val isEmpty: Boolean

   public abstract suspend fun receive(): Any {
   }

   public val onReceive: SelectClause1<Any>

   public abstract suspend fun receiveCatching(): ChannelResult<Any> {
   }

   public val onReceiveCatching: SelectClause1<ChannelResult<Any>>

   public abstract fun tryReceive(): ChannelResult<Any> {
   }

   public abstract operator fun iterator(): ChannelIterator<Any> {
   }

   public abstract fun cancel(cause: CancellationException? = ...) {
   }

   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public open fun cancel() {
   }

   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public abstract fun cancel(cause: Throwable? = ...): Boolean {
   }

   @Deprecated(message = "Deprecated in the favour of 'tryReceive'. Please note that the provided replacement does not rethrow channel's close cause as 'poll' did, for the precise replacement please refer to the 'poll' documentation", replaceWith = @ReplaceWith(expression = "tryReceive().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
   public open fun poll(): Any? {
   }

   @Deprecated(message = "Deprecated in favor of 'receiveCatching'. Please note that the provided replacement does not rethrow channel's close cause as 'receiveOrNull' did, for the detailed replacement please refer to the 'receiveOrNull' documentation", replaceWith = @ReplaceWith(expression = "receiveCatching().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
   @LowPriorityInOverloadResolution
   public open suspend fun receiveOrNull(): Any? {
   }

   public open val onReceiveOrNull: SelectClause1<Any?>
      public open get() {
      }


   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @DelicateCoroutinesApi
      @JvmStatic
      @JvmSynthetic
      fun `isClosedForReceive$annotations`() {
      }

      /** @deprecated */
      @ExperimentalCoroutinesApi
      @JvmStatic
      @JvmSynthetic
      fun `isEmpty$annotations`() {
      }

      @JvmStatic
      @JvmSynthetic
      fun `cancel$default`(var0: ReceiveChannel, var1: CancellationException, var2: Int, var3: Any) {
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
      @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
      @JvmStatic
      @JvmSynthetic
      fun cancel(`$this`: ReceiveChannel) {
         `$this`.cancel((CancellationException)null)
      }

      /** @deprecated */
      @JvmStatic
      @JvmSynthetic
      fun `cancel$default`(var0: ReceiveChannel, var1: java.lang.Throwable, var2: Int, var3: Any): Boolean {
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
      @Deprecated(message = "Deprecated in the favour of 'tryReceive'. Please note that the provided replacement does not rethrow channel's close cause as 'poll' did, for the precise replacement please refer to the 'poll' documentation", replaceWith = @ReplaceWith(expression = "tryReceive().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
      @JvmStatic
      fun <E> poll(`$this`: ReceiveChannel<out E>): E {
         val result: Any = `$this`.tryReceive_PtdJZtk/* $VF was: tryReceive-PtdJZtk */()
         if (isSuccess) {
            ChannelResult.getOrThrow_impl/* $VF was: getOrThrow-impl */(result)
         } else {
            val var10000: java.lang.Throwable = ChannelResult.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(result)
            if (var10000 == null) {
               null
            } else {
               throw StackTraceRecoveryKt.recoverStackTrace(var10000)
            }
         }
      }

      /** @deprecated */
      @JvmStatic
      fun <E> getOnReceiveOrNull(`$this`: ReceiveChannel<out E>): SelectClause1<E> {
         (`$this` as BufferedChannel).onReceiveOrNull
      }

      /** @deprecated */
      @Deprecated(message = "Deprecated in favor of onReceiveCatching extension", replaceWith = @ReplaceWith(expression = "onReceiveCatching", imports = []), level = DeprecationLevel.ERROR)
      @JvmStatic
      @JvmSynthetic
      fun `getOnReceiveOrNull$annotations`() {
      }
   }
}
