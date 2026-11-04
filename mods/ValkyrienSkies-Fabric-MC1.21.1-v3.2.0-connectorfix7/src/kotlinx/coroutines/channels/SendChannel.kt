package kotlinx.coroutines.channels

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.internal.StackTraceRecoveryKt
import kotlinx.coroutines.selects.SelectClause2

public interface SendChannel<E> {
   public val isClosedForSend: Boolean

   public abstract suspend fun send(element: Any) {
   }

   public val onSend: SelectClause2<Any, SendChannel<Any>>

   public abstract fun trySend(element: Any): ChannelResult<Unit> {
   }

   public abstract fun close(cause: Throwable? = ...): Boolean {
   }

   public abstract fun invokeOnClose(handler: (Throwable?) -> Unit) {
   }

   @Deprecated(message = "Deprecated in the favour of 'trySend' method", replaceWith = @ReplaceWith(expression = "trySend(element).isSuccess", imports = []), level = DeprecationLevel.ERROR)
   public open fun offer(element: Any): Boolean {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @DelicateCoroutinesApi
      @JvmStatic
      @JvmSynthetic
      fun `isClosedForSend$annotations`() {
      }

      @JvmStatic
      @JvmSynthetic
      fun `close$default`(var0: SendChannel, var1: java.lang.Throwable, var2: Int, var3: Any): Boolean {
         if (var3 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: close")
         } else {
            if ((var2 and 1) != 0) {
               var1 = null
            }

            var0.close(var1)
         }
      }

      /** @deprecated */
      @Deprecated(message = "Deprecated in the favour of 'trySend' method", replaceWith = @ReplaceWith(expression = "trySend(element).isSuccess", imports = []), level = DeprecationLevel.ERROR)
      @JvmStatic
      fun <E> offer(`$this`: SendChannel<in E>, element: E): Boolean {
         val result: Any = `$this`.trySend_JP2dKIU/* $VF was: trySend-JP2dKIU */(element)
         if (isSuccess) {
            true
         } else {
            val var10000: java.lang.Throwable = ChannelResult.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(result)
            if (var10000 == null) {
               false
            } else {
               throw StackTraceRecoveryKt.recoverStackTrace(var10000)
            }
         }
      }
   }
}
