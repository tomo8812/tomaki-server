package kotlinx.coroutines

import kotlinx.coroutines.internal.MainDispatchersKt
import kotlinx.coroutines.internal.SystemPropsKt

private final val defaultMainDelayOptIn: Boolean = SystemPropsKt.systemProp("kotlinx.coroutines.main.delay", false)

@PublishedApi
internal final val DefaultDelay: Delay = initializeDefaultDelay()

private fun initializeDefaultDelay(): Delay {
   if (!defaultMainDelayOptIn) {
      return DefaultExecutor.INSTANCE
   } else {
      val main: MainCoroutineDispatcher = Main
      return if (!MainDispatchersKt.isMissing(main) && main is Delay) main as Delay else DefaultExecutor.INSTANCE
   }
}
