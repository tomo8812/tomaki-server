package kotlinx.coroutines

import kotlinx.coroutines.internal.Symbol
import org.jetbrains.annotations.NotNull

private final val COMPLETING_ALREADY: Symbol = Symbol("COMPLETING_ALREADY")

@JvmField
@NotNull
internal final val COMPLETING_WAITING_CHILDREN: Symbol = Symbol("COMPLETING_WAITING_CHILDREN")

private final val COMPLETING_RETRY: Symbol = Symbol("COMPLETING_RETRY")
private final val TOO_LATE_TO_CANCEL: Symbol = Symbol("TOO_LATE_TO_CANCEL")
private const val RETRY: Int = -1
private const val FALSE: Int = 0
private const val TRUE: Int = 1
private final val SEALED: Symbol = Symbol("SEALED")
private final val EMPTY_NEW: Empty = Empty(false)
private final val EMPTY_ACTIVE: Empty = Empty(true)

internal fun Any?.boxIncomplete(): Any? {
   return if (`$this$boxIncomplete` is Incomplete) IncompleteStateBox(`$this$boxIncomplete` as Incomplete) else `$this$boxIncomplete`
}

internal fun Any?.unboxState(): Any? {
   return if ((`$this$unboxState` as? IncompleteStateBox) != null && (`$this$unboxState` as? IncompleteStateBox).state != null)
      (`$this$unboxState` as? IncompleteStateBox).state
      else
      `$this$unboxState`
   }

@JvmSynthetic
fun `access$getEMPTY_ACTIVE$p`(): Empty {
   EMPTY_ACTIVE
}

@JvmSynthetic
fun `access$getEMPTY_NEW$p`(): Empty {
   EMPTY_NEW
}

@JvmSynthetic
fun `access$getCOMPLETING_ALREADY$p`(): Symbol {
   COMPLETING_ALREADY
}

@JvmSynthetic
fun `access$getTOO_LATE_TO_CANCEL$p`(): Symbol {
   TOO_LATE_TO_CANCEL
}

@JvmSynthetic
fun `access$getCOMPLETING_RETRY$p`(): Symbol {
   COMPLETING_RETRY
}

@JvmSynthetic
fun `access$getSEALED$p`(): Symbol {
   SEALED
}
