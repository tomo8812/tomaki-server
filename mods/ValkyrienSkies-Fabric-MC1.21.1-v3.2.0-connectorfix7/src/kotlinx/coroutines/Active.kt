package kotlinx.coroutines

private object Active : NotCompleted {
   public override fun toString(): String {
      return "Active"
   }
}
