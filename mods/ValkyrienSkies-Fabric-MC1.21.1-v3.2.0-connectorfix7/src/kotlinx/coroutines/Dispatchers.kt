package kotlinx.coroutines

import kotlinx.coroutines.internal.MainDispatcherLoader
import kotlinx.coroutines.scheduling.DefaultIoScheduler
import kotlinx.coroutines.scheduling.DefaultScheduler

public object Dispatchers {
   @JvmStatic
   public final val Default: CoroutineDispatcher = DefaultScheduler.INSTANCE as CoroutineDispatcher

   @JvmStatic
   public final val Unconfined: CoroutineDispatcher = kotlinx.coroutines.Unconfined.INSTANCE as CoroutineDispatcher

   @JvmStatic
   public final val IO: CoroutineDispatcher = DefaultIoScheduler.INSTANCE as CoroutineDispatcher

   @JvmStatic
   public final val Main: MainCoroutineDispatcher
      public final get() {
         return MainDispatcherLoader.dispatcher
      }


   @DelicateCoroutinesApi
   public fun shutdown() {
      DefaultExecutor.INSTANCE.shutdown()
      DefaultScheduler.INSTANCE.shutdown$kotlinx_coroutines_core()
   }
}
