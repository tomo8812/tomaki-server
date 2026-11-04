package kotlinx.coroutines

@PublishedApi
internal open class JobImpl(parent: Job?) : JobSupport(true), CompletableJob {
   internal open val handlesException: Boolean

   init {
      this.initParentJob(parent)
      this.handlesException = this.handlesException()
   }

   internal open val onCancelComplete: Boolean
      internal open get() {
         return true
      }


   public override fun complete(): Boolean {
      return this.makeCompleting$kotlinx_coroutines_core(Unit.INSTANCE)
   }

   public override fun completeExceptionally(exception: Throwable): Boolean {
      return this.makeCompleting$kotlinx_coroutines_core(CompletedExceptionally(exception, false, 2, null))
   }

   private fun handlesException(): Boolean {
      var var2: ChildHandle = this.getParentHandle$kotlinx_coroutines_core()
      var var10000: ChildHandleNode = var2 as? ChildHandleNode
      if ((var2 as? ChildHandleNode) != null) {
         val var4: JobSupport = var10000.getJob()
         if (var4 != null) {
            var parentJob: JobSupport = var4

            while (!parentJob.handlesException) {
               var2 = parentJob.parentHandle
               var10000 = var2 as? ChildHandleNode
               if ((var2 as? ChildHandleNode) != null) {
                  val var6: JobSupport = var10000.getJob()
                  if (var6 != null) {
                     parentJob = var6
                     continue
                  }
               }

               return false
            }

            return true
         }
      }

      return false
   }
}
