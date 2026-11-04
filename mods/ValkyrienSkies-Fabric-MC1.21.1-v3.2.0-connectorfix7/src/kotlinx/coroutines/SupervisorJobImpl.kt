package kotlinx.coroutines

private class SupervisorJobImpl(parent: Job?) : JobImpl(parent) {
   public override fun childCancelled(cause: Throwable): Boolean {
      return false
   }
}
