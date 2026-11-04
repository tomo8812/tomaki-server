package kotlinx.coroutines.flow.internal

import kotlinx.coroutines.internal.Symbol
import org.jetbrains.annotations.NotNull

@JvmField
@NotNull
internal final val NULL: Symbol = Symbol("NULL")

@JvmField
@NotNull
internal final val UNINITIALIZED: Symbol = Symbol("UNINITIALIZED")

@JvmField
@NotNull
internal final val DONE: Symbol = Symbol("DONE")
