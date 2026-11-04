package kotlinx.coroutines.internal

import kotlin.jvm.internal.Intrinsics

public open class LockFreeLinkedListHead : LockFreeLinkedListNode {
   public final val isEmpty: Boolean
      public final get() {
         return this.getNext() === this
      }


   @JvmSynthetic
   public inline fun <reified T : LockFreeLinkedListNode> forEach(block: (Any) -> Unit) {
      val var10000: Any = this.getNext()

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var cur: LockFreeLinkedListNode = var10000 as LockFreeLinkedListNode
      while (true) {
         if (!(cur == this)) break
         Intrinsics.reifiedOperationMarker(3, "T")
         if (cur is LockFreeLinkedListNode) {
            block(cur)
         }

         cur = cur.nextNode
      }
   }

   public fun remove(): Nothing {
      throw IllegalStateException("head cannot be removed".toString())
   }

   public open val isRemoved: Boolean
      public open get() {
         return false
      }


   protected override fun nextIfRemoved(): LockFreeLinkedListNode? {
      return null
   }

   internal fun validate() {
      var prev: LockFreeLinkedListNode = this
      val var10000: Any = this.getNext()
      var cur: LockFreeLinkedListNode = var10000 as LockFreeLinkedListNode

      while (!(cur == this)) {
         val next: LockFreeLinkedListNode = cur.nextNode
         cur.validateNode$kotlinx_coroutines_core(prev, next)
         prev = cur
         cur = next
      }

      val var10002: Any = this.getNext()
      this.validateNode$kotlinx_coroutines_core(prev, var10002 as LockFreeLinkedListNode)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun remove(): Boolean {
      this.remove()
   }
}
