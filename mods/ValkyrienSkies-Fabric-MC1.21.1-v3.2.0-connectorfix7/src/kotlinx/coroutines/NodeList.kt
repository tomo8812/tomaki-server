package kotlinx.coroutines

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.LockFreeLinkedListHead
import kotlinx.coroutines.internal.LockFreeLinkedListNode

@SourceDebugExtension(["SMAP\nJobSupport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JobSupport.kt\nkotlinx/coroutines/NodeList\n+ 2 LockFreeLinkedList.kt\nkotlinx/coroutines/internal/LockFreeLinkedListHead\n*L\n1#1,1454:1\n341#2,6:1455\n*S KotlinDebug\n*F\n+ 1 JobSupport.kt\nkotlinx/coroutines/NodeList\n*L\n1364#1:1455,6\n*E\n"])
internal class NodeList : LockFreeLinkedListHead, Incomplete {
   public open val isActive: Boolean
      public open get() {
         return true
      }


   public open val list: NodeList
      public open get() {
         return this
      }


   public fun getString(state: String): String {
      val var2: StringBuilder = StringBuilder()
      val `$this$getString_u24lambda_u241`: StringBuilder = var2
      var2.append("List{")
      var2.append(state)
      var2.append("}[")
      var var11: Boolean = true
      val `this_$iv`: LockFreeLinkedListHead = this
      val var10000: Any = this.getNext()

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var `cur$iv`: LockFreeLinkedListNode = var10000 as LockFreeLinkedListNode
      while (true) {
         if (!(`cur$iv` == `this_$iv`)) break
         if (`cur$iv` is JobNode) {
            val node: JobNode = `cur$iv` as JobNode
            if (var11) {
               var11 = false
            } else {
               `$this$getString_u24lambda_u241`.append(", ")
            }

            `$this$getString_u24lambda_u241`.append(node)
         }

         `cur$iv` = `cur$iv`.nextNode
      }

      `$this$getString_u24lambda_u241`.append("]")
      val var12: java.lang.String = var2.toString()
      return var12
   }

   public override fun toString(): String {
      return if (DEBUG) this.getString("Active") else super.toString()
   }
}
