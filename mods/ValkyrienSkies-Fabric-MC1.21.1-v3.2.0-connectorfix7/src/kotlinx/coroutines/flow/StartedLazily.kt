package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation

private class StartedLazily : SharingStarted {
   public override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> {
      return FlowKt.flow({
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as (FlowCollector<in SharingCommand>?, Continuation<in Unit>?) -> Any)
   }

   public override fun toString(): String {
      return "SharingStarted.Lazily"
   }
}
