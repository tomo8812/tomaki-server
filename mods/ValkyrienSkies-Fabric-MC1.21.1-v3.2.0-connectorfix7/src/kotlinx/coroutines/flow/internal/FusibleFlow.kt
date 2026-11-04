package kotlinx.coroutines.flow.internal

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow

@InternalCoroutinesApi
public interface FusibleFlow<T> : Flow<T> {
   public abstract fun fuse(context: CoroutineContext = ..., capacity: Int = ..., onBufferOverflow: BufferOverflow = ...): Flow<Any> {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      @JvmSynthetic
      fun `fuse$default`(var0: FusibleFlow, var1: CoroutineContext, var2: Int, var3: BufferOverflow, var4: Int, var5: Any): Flow {
         if (var5 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: fuse")
         } else {
            if ((var4 and 1) != 0) {
               var1 = EmptyCoroutineContext.INSTANCE as CoroutineContext
            }

            if ((var4 and 2) != 0) {
               var2 = -3
            }

            if ((var4 and 4) != 0) {
               var3 = BufferOverflow.SUSPEND
            }

            var0.fuse(var1, var2, var3)
         }
      }
   }
}
