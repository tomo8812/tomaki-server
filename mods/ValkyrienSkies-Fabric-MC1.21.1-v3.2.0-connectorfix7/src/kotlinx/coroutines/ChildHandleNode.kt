package kotlinx.coroutines

import org.jetbrains.annotations.NotNull

internal class ChildHandleNode(childJob: ChildJob) : JobCancellingNode, ChildHandle {
   @JvmField
   @NotNull
   public final val childJob: ChildJob

   init {
      this.childJob = childJob
   }

   public open val parent: Job
      public open get() {
         return this.getJob()
      }


   public override operator fun invoke(cause: Throwable?) {
      this.childJob.parentCancelled(this.getJob())
   }

   public override fun childCancelled(cause: Throwable): Boolean {
      return this.getJob().childCancelled(cause)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
