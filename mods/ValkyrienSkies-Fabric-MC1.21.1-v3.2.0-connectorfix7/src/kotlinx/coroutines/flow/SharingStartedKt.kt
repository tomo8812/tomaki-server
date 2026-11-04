package kotlinx.coroutines.flow

import kotlin.time.Duration
import kotlinx.coroutines.flow.SharingStarted.Companion

public fun Companion.WhileSubscribed(stopTimeout: Duration = ..., replayExpiration: Duration = ...): SharingStarted {
   return StartedWhileSubscribed(
      Duration.getInWholeMilliseconds_impl/* $VF was: getInWholeMilliseconds-impl */(stopTimeout),
      Duration.getInWholeMilliseconds_impl/* $VF was: getInWholeMilliseconds-impl */(replayExpiration)
   )
}

@JvmSynthetic
fun `WhileSubscribed-5qebJ5I$default`(var0: SharingStarted.Companion, var1: Long, var3: Long, var5: Int, var6: Any): SharingStarted {
   if ((var5 and 1) != 0) {
      var1 = Duration.Companion.getZERO_UwyO8pc/* $VF was: getZERO-UwyO8pc */()
   }

   if ((var5 and 2) != 0) {
      var3 = Duration.Companion.getINFINITE_UwyO8pc/* $VF was: getINFINITE-UwyO8pc */()
   }

   WhileSubscribed_5qebJ5I/* $VF was: WhileSubscribed-5qebJ5I */(var0, var1, var3)
}
