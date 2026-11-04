package kotlinx.coroutines

import kotlinx.coroutines.internal.LimitedDispatcherKt

public abstract class MainCoroutineDispatcher : CoroutineDispatcher {
   public abstract val immediate: MainCoroutineDispatcher

   public override fun toString(): String {
      var var10000: java.lang.String = this.toStringInternalImpl()
      if (var10000 == null) {
         var10000 = "${classSimpleName}@${hexAddress}"
      }

      return var10000
   }

   public override fun limitedParallelism(parallelism: Int): CoroutineDispatcher {
      LimitedDispatcherKt.checkParallelism(parallelism)
      return this
   }

   @InternalCoroutinesApi
   protected fun toStringInternalImpl(): String? {
      val main: MainCoroutineDispatcher = Main
      if (this === main) {
         return "Dispatchers.Main"
      } else {
         var var3: MainCoroutineDispatcher
         try {
            var3 = main.immediate
         } catch (var5: UnsupportedOperationException) {
            var3 = null
         }

         return if (this === var3) "Dispatchers.Main.immediate" else null
      }
   }
}
