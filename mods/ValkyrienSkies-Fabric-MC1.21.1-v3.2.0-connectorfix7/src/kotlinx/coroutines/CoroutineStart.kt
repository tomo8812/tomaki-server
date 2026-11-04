package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationKt
import kotlinx.coroutines.intrinsics.CancellableKt
import kotlinx.coroutines.intrinsics.UndispatchedKt

public enum class CoroutineStart {
   DEFAULT,
   LAZY,
   @ExperimentalCoroutinesApi
   ATOMIC,
   UNDISPATCHED;

   @InternalCoroutinesApi
   public operator fun <T> invoke(block: (Continuation<Any>) -> Any?, completion: Continuation<Any>) {
      when (CoroutineStart.WhenMappings.$EnumSwitchMapping$0[this.ordinal()]) {
         1 -> CancellableKt.startCoroutineCancellable(block, completion)
         2 -> ContinuationKt.startCoroutine(block, completion)
         3 -> UndispatchedKt.startCoroutineUndispatched(block, completion)
         4 -> {}
         else -> throw NoWhenBranchMatchedException()
      }
   }

   @InternalCoroutinesApi
   public operator fun <R, T> invoke(block: (Any, Continuation<Any>) -> Any?, receiver: Any, completion: Continuation<Any>) {
      when (CoroutineStart.WhenMappings.$EnumSwitchMapping$0[this.ordinal()]) {
         1 -> CancellableKt.startCoroutineCancellable$default(block, receiver, completion, null, 4, null)
         2 -> ContinuationKt.startCoroutine(block, receiver, completion)
         3 -> UndispatchedKt.startCoroutineUndispatched(block, receiver, completion)
         4 -> {}
         else -> throw NoWhenBranchMatchedException()
      }
   }

   @InternalCoroutinesApi
   public final val isLazy: Boolean
      public final get() {
         return this === LAZY
      }


   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<CoroutineStart> {
      arrayOf(DEFAULT, LAZY, ATOMIC, UNDISPATCHED)
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class WhenMappings {
      @JvmStatic
      @JvmSynthetic
      public int[] $EnumSwitchMapping$0;

      @JvmStatic
      fun {
         val var0: IntArray = IntArray(CoroutineStart.values().length)

         try {
            var0[CoroutineStart.DEFAULT.ordinal()] = 1
         } catch (var5: NoSuchFieldError) {
         }

         try {
            var0[CoroutineStart.ATOMIC.ordinal()] = 2
         } catch (var4: NoSuchFieldError) {
         }

         try {
            var0[CoroutineStart.UNDISPATCHED.ordinal()] = 3
         } catch (var3: NoSuchFieldError) {
         }

         try {
            var0[CoroutineStart.LAZY.ordinal()] = 4
         } catch (var2: NoSuchFieldError) {
         }

         $EnumSwitchMapping$0 = var0
      }
   }
}
