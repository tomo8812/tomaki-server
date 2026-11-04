package kotlinx.coroutines

private class DisposeOnCancel(handle: DisposableHandle) : CancelHandler {
   private final val handle: DisposableHandle

   init {
      this.handle = handle
   }

   public override operator fun invoke(cause: Throwable?) {
      this.handle.dispose()
   }

   public override fun toString(): String {
      return "DisposeOnCancel[${this.handle}]"
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
