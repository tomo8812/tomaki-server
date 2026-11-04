package kotlinx.coroutines.debug.internal

import java.io.Serializable
import java.lang.Thread.State
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Key
import kotlinx.coroutines.CoroutineId
import kotlinx.coroutines.CoroutineName

@PublishedApi
internal class DebuggerInfo(source: DebugCoroutineInfoImpl, context: CoroutineContext) : Serializable {
   public final val coroutineId: Long?
   public final val dispatcher: String?
   public final val name: String?
   public final val state: String
   public final val lastObservedThreadState: String?
   public final val lastObservedThreadName: String?
   public final val lastObservedStackTrace: List<StackTraceElement>
   public final val sequenceNumber: Long

   init {
      var var6: java.lang.String
      run label41@{
         val var10001: CoroutineId = context.get(CoroutineId.Key) as CoroutineId
         this.coroutineId = if (var10001 != null) var10001.id else null
         val var3: ContinuationInterceptor = context.get(ContinuationInterceptor.Key as Key) as ContinuationInterceptor
         this.dispatcher = if (var3 != null) var3.toString() else null
         val var4: CoroutineName = context.get(CoroutineName.Key) as CoroutineName
         this.name = if (var4 != null) var4.name else null
         this.state = source.state
         if (source.lastObservedThread != null) {
            val var5: State = source.lastObservedThread.getState()
            if (var5 != null) {
               var6 = var5.toString()
               return@label41
            }
         }

         var6 = null
      }

      this.lastObservedThreadState = var6
      this.lastObservedThreadName = if (source.lastObservedThread != null) source.lastObservedThread.getName() else null
      this.lastObservedStackTrace = source.lastObservedStackTrace$kotlinx_coroutines_core()
      this.sequenceNumber = source.sequenceNumber
   }
}
