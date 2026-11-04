package kotlinx.coroutines

@InternalCoroutinesApi
public class CompletionHandlerException(message: String, cause: Throwable) : RuntimeException(message, cause)
