package kotlinx.coroutines.flow

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.channels.BufferOverflow
import org.jetbrains.annotations.NotNull

private class SharingConfig<T>(upstream: Flow<Any>, extraBufferCapacity: Int, onBufferOverflow: BufferOverflow, context: CoroutineContext) {
   @JvmField
   @NotNull
   public final val upstream: Flow<Any>

   @JvmField
   public final val extraBufferCapacity: Int

   @JvmField
   @NotNull
   public final val onBufferOverflow: BufferOverflow

   @JvmField
   @NotNull
   public final val context: CoroutineContext

   init {
      this.upstream = upstream
      this.extraBufferCapacity = extraBufferCapacity
      this.onBufferOverflow = onBufferOverflow
      this.context = context
   }
}
