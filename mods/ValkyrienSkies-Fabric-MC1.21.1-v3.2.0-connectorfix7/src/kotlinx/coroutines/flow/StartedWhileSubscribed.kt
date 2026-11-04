package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

@SourceDebugExtension(["SMAP\nSharingStarted.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SharingStarted.kt\nkotlinx/coroutines/flow/StartedWhileSubscribed\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,209:1\n1#2:210\n*E\n"])
private class StartedWhileSubscribed(stopTimeout: Long, replayExpiration: Long) : SharingStarted {
   private final val stopTimeout: Long
   private final val replayExpiration: Long

   init {
      this.stopTimeout = stopTimeout
      this.replayExpiration = replayExpiration
      if (this.stopTimeout < 0L) {
         throw IllegalArgumentException(("stopTimeout(${this.stopTimeout} ms) cannot be negative").toString())
      } else if (this.replayExpiration < 0L) {
         throw IllegalArgumentException(("replayExpiration(${this.replayExpiration} ms) cannot be negative").toString())
      }
   }

   public override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> {
      return FlowKt.distinctUntilChanged(FlowKt.dropWhile(FlowKt.transformLatest(subscriptionCount, {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function3), {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as (SharingCommand?, Continuation<? super java.lang.Boolean>?) -> Any))
   }

   public override fun toString(): String {
      val var2: java.util.List = CollectionsKt.createListBuilder(2)
      if (this.stopTimeout > 0L) {
         var2.add("stopTimeout=${this.stopTimeout}ms")
      }

      if (this.replayExpiration < java.lang.Long.MAX_VALUE) {
         var2.add("replayExpiration=${this.replayExpiration}ms")
      }

      return "SharingStarted.WhileSubscribed(${CollectionsKt.joinToString$default(CollectionsKt.build(var2), null, null, null, 0, null, null, 63, null)})"
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is StartedWhileSubscribed
         && this.stopTimeout == (other as StartedWhileSubscribed).stopTimeout
         && this.replayExpiration == (other as StartedWhileSubscribed).replayExpiration
      }

   @IgnoreJRERequirement
   public override fun hashCode(): Int {
      return java.lang.Long.hashCode(this.stopTimeout) * 31 + java.lang.Long.hashCode(this.replayExpiration)
   }
}
