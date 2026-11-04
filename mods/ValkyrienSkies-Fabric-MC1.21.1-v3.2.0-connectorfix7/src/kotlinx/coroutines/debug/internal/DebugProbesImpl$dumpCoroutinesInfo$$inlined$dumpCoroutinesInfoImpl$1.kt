package kotlinx.coroutines.debug.internal

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner

{ owner: CoroutineOwner<*> ->
   val var10000: DebugCoroutineInfo
   if (DebugProbesImpl.access$isFinished(DebugProbesImpl.INSTANCE, owner)) {
      var10000 = null
   } else {
      val var7: CoroutineContext = owner.info.context
      var10000 = if (var7 != null) DebugCoroutineInfo(owner.info, var7) else null
   }

   return var10000
}