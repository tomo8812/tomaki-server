package kotlinx.coroutines

internal abstract class JobNode : CompletionHandlerBase, DisposableHandle, Incomplete {
   public final lateinit var job: JobSupport

   public open val isActive: Boolean
      public open get() {
         return true
      }


   public open val list: NodeList?
      public open get() {
         return null
      }


   public override fun dispose() {
      this.job.removeNode$kotlinx_coroutines_core(this)
   }

   public override fun toString(): String {
      return "${classSimpleName}@${hexAddress}[job@${hexAddress}]"
   }
}
