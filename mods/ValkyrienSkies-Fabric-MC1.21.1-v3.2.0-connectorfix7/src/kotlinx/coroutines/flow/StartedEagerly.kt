package kotlinx.coroutines.flow

private class StartedEagerly : SharingStarted {
   public override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> {
      return FlowKt.flowOf(SharingCommand.START)
   }

   public override fun toString(): String {
      return "SharingStarted.Eagerly"
   }
}
