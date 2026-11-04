package kotlinx.coroutines.selects

import kotlinx.coroutines.Waiter

internal interface SelectInstanceInternal<R> : SelectInstance<R>, Waiter
