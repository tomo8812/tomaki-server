package kotlinx.coroutines

import kotlinx.coroutines.internal.Symbol
import org.jetbrains.annotations.NotNull

private const val UNDECIDED: Int = 0
private const val SUSPENDED: Int = 1
private const val RESUMED: Int = 2
private const val DECISION_SHIFT: Int = 29
private const val INDEX_MASK: Int = 536870911
private const val NO_INDEX: Int = 536870911

@JvmField
@NotNull
internal final val RESUME_TOKEN: Symbol = Symbol("RESUME_TOKEN")

private final val decision: Int
   private final inline get() {
      return `$this$decision` shr 29
   }


private final val index: Int
   private final inline get() {
      return `$this$index` and 536870911
   }


private inline fun decisionAndIndex(decision: Int, index: Int): Int {
   return (decision shl 29) + index
}
