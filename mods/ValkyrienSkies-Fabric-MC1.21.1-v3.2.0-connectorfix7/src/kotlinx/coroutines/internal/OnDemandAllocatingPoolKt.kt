package kotlinx.coroutines.internal

private const val IS_CLOSED_MASK: Int = Integer.MIN_VALUE

private inline fun loop(block: () -> Unit): Nothing {
   while (true) {
      block()
   }
}
