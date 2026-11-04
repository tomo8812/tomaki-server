package kotlinx.coroutines

internal class DisposeOnCompletion(handle: DisposableHandle) : JobNode {
   private final val handle: DisposableHandle

   init {
      this.handle = handle
   }

   public override operator fun invoke(cause: Throwable?) {
      this.handle.dispose()
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
