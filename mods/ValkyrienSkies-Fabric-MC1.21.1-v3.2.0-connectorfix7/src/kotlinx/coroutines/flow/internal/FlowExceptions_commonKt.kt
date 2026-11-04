package kotlinx.coroutines.flow.internal

import kotlinx.coroutines.flow.FlowCollector

internal fun AbortFlowException.checkOwnership(owner: FlowCollector<*>) {
   if (`$this$checkOwnership`.owner != owner) {
      throw `$this$checkOwnership`
   }
}

@PublishedApi
internal inline fun checkIndexOverflow(index: Int): Int {
   if (index < 0) {
      throw ArithmeticException("Index overflow has happened")
   } else {
      return index
   }
}
