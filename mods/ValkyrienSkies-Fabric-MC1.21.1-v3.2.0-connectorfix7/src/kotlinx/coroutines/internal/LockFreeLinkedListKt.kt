package kotlinx.coroutines.internal

@PublishedApi
internal const val UNDECIDED: Int = 0

@PublishedApi
internal const val SUCCESS: Int = 1

@PublishedApi
internal const val FAILURE: Int = 2

@PublishedApi
internal final val CONDITION_FALSE: Any = Symbol("CONDITION_FALSE")

@PublishedApi
internal fun Any.unwrap(): LockFreeLinkedListNode {
   val var10000: Removed = `$this$unwrap` as? Removed
   if ((`$this$unwrap` as? Removed) != null && (`$this$unwrap` as? Removed).ref != null) {
      return var10000.ref
   } else {
      return `$this$unwrap` as LockFreeLinkedListNode
   }
}
