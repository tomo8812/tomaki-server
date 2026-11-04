package kotlinx.coroutines.channels

import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
public enum class TickerMode {
   FIXED_PERIOD,
   FIXED_DELAY;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<TickerMode> {
      arrayOf(FIXED_PERIOD, FIXED_DELAY)
   }
}
