package kotlinx.coroutines

import org.jetbrains.annotations.NotNull

@PublishedApi
internal class ChildContinuation(child: CancellableContinuationImpl<*>) : JobCancellingNode {
   @JvmField
   @NotNull
   public final val child: CancellableContinuationImpl<*>

   init {
      this.child = child
   }

   public override operator fun invoke(cause: Throwable?) {
      this.child.parentCancelled$kotlinx_coroutines_core(this.child.getContinuationCancellationCause(this.getJob()))
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
