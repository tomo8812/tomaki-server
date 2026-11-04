package kotlinx.coroutines.internal

import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
public interface ThreadSafeHeapNode {
   public var heap: ThreadSafeHeap<*>?

   public var index: Int
}
