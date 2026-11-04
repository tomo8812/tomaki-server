package kotlinx.coroutines

@InternalCoroutinesApi
public object NonDisposableHandle : DisposableHandle, ChildHandle {
   public open val parent: Job?
      public open get() {
         return null
      }


   public override fun dispose() {
   }

   public override fun childCancelled(cause: Throwable): Boolean {
      return false
   }

   public override fun toString(): String {
      return "NonDisposableHandle"
   }
}
