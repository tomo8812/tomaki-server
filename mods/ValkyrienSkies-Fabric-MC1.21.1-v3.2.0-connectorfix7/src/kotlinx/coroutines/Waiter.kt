package kotlinx.coroutines

import kotlinx.coroutines.internal.Segment

internal interface Waiter {
   public abstract fun invokeOnCancellation(segment: Segment<*>, index: Int) {
   }
}
