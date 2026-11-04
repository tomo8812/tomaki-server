package kotlinx.coroutines

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.LockFreeLinkedListNode

@SourceDebugExtension(["SMAP\nLockFreeLinkedList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LockFreeLinkedList.kt\nkotlinx/coroutines/internal/LockFreeLinkedListNode$makeCondAddOp$1\n+ 2 JobSupport.kt\nkotlinx/coroutines/JobSupport\n*L\n1#1,367:1\n526#2:368\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `JobSupport$addLastAtomic$$inlined$addLastIf$1` : LockFreeLinkedListNode.CondAddOp {
   @JvmSynthetic
   JobSupport this$0;
   @JvmSynthetic
   Object $expect$inlined;

   fun `JobSupport$addLastAtomic$$inlined$addLastIf$1`(`$node`: LockFreeLinkedListNode, var2: JobSupport, var3: Any) {
      super(`$node`)
      this.this$0 = var2
      this.$expect$inlined = var3
   }

   public open fun prepare(affected: LockFreeLinkedListNode): Any? {
      return if (this.this$0.state === this.$expect$inlined) null else CONDITION_FALSE
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun prepare(affected: Any) {
      this.prepare(affected as LockFreeLinkedListNode)
   }
}
