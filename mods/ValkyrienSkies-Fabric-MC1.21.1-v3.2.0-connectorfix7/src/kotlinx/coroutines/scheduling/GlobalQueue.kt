package kotlinx.coroutines.scheduling

import kotlinx.coroutines.internal.LockFreeTaskQueue

internal class GlobalQueue : LockFreeTaskQueue(false)
