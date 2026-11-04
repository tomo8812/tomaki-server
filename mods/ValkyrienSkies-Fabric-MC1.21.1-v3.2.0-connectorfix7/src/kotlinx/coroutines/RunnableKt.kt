package kotlinx.coroutines

public inline fun Runnable(crossinline block: () -> Unit): Runnable {
   return {
      block()
   }
}
