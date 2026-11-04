package kotlinx.coroutines.sync

import kotlin.coroutines.Continuation
import kotlinx.coroutines.selects.SelectClause2

public interface Mutex {
   public val isLocked: Boolean

   public abstract fun tryLock(owner: Any? = ...): Boolean {
   }

   public abstract suspend fun lock(owner: Any? = ...) {
   }

   public val onLock: SelectClause2<Any?, Mutex>

   public abstract fun holdsLock(owner: Any): Boolean {
   }

   public abstract fun unlock(owner: Any? = ...) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      @JvmSynthetic
      fun `tryLock$default`(var0: Mutex, var1: Any, var2: Int, var3: Any): Boolean {
         if (var3 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: tryLock")
         } else {
            if ((var2 and 1) != 0) {
               var1 = null
            }

            var0.tryLock(var1)
         }
      }

      @JvmStatic
      @JvmSynthetic
      fun `lock$default`(var0: Mutex, var1: Any, var2: Continuation, var3: Int, var4: Any) {
         if (var4 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: lock")
         } else {
            if ((var3 and 1) != 0) {
               var1 = null
            }

            var0.lock(var1, var2)
         }
      }

      /** @deprecated */
      @Deprecated(message = "Mutex.onLock deprecated without replacement. For additional details please refer to #2794", level = DeprecationLevel.WARNING)
      @JvmStatic
      @JvmSynthetic
      fun `getOnLock$annotations`() {
      }

      @JvmStatic
      @JvmSynthetic
      fun `unlock$default`(var0: Mutex, var1: Any, var2: Int, var3: Any) {
         if (var3 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: unlock")
         } else {
            if ((var2 and 1) != 0) {
               var1 = null
            }

            var0.unlock(var1)
         }
      }
   }
}
