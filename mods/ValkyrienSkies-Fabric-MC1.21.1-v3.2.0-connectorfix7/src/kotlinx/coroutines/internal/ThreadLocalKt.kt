package kotlinx.coroutines.internal

internal fun <T> commonThreadLocal(name: Symbol): ThreadLocal<Any> {
   return ThreadLocal()
}

/** @deprecated */
@JvmSynthetic
fun `CommonThreadLocal$annotations`() {
}
