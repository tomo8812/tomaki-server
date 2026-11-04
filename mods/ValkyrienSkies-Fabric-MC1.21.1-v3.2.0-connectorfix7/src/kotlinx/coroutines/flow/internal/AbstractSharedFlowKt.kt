package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import org.jetbrains.annotations.NotNull

@JvmField
@NotNull
internal final val EMPTY_RESUMES: Array<Continuation<Unit>?>
