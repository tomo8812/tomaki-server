package kotlinx.coroutines

private class InvokeOnCompletion(handler: (Throwable?) -> Unit) : JobNode {
   private final val handler: (Throwable?) -> Unit

   init {
      this.handler = handler
   }

   public override operator fun invoke(cause: Throwable?) {
      this.handler(cause)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
