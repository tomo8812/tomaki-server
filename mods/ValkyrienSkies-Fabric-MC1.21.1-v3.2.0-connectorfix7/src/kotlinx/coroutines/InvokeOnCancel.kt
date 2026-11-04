package kotlinx.coroutines

private class InvokeOnCancel(handler: (Throwable?) -> Unit) : CancelHandler {
   private final val handler: (Throwable?) -> Unit

   init {
      this.handler = handler
   }

   public override operator fun invoke(cause: Throwable?) {
      this.handler(cause)
   }

   public override fun toString(): String {
      return "InvokeOnCancel[${classSimpleName}@${hexAddress}]"
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
