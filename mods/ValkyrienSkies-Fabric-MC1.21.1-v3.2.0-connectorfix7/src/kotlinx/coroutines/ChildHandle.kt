package kotlinx.coroutines

/** @deprecated */
@Deprecated(message = "This is internal API and may be removed in the future releases", level = DeprecationLevel.ERROR)
@InternalCoroutinesApi
public interface ChildHandle : DisposableHandle {
   public val parent: Job?

   @InternalCoroutinesApi
   public abstract fun childCancelled(cause: Throwable): Boolean {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @InternalCoroutinesApi
      @JvmStatic
      @JvmSynthetic
      fun `getParent$annotations`() {
      }
   }
}
