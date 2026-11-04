package kotlinx.coroutines.flow

import org.jetbrains.annotations.NotNull

internal class ThrowingCollector(e: Throwable) : FlowCollector<Object> {
   @JvmField
   @NotNull
   public final val e: Throwable

   init {
      this.e = e
   }

   public override suspend fun emit(value: Any?) {
      throw this.e
   }
}
