package kotlinx.coroutines.flow

import java.util.concurrent.CancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.InlineMarker

@Deprecated(message = "Applying 'cancellable' to a SharedFlow has no effect. See the SharedFlow documentation on Operator Fusion.", replaceWith = @ReplaceWith(expression = "this", imports = []), level = DeprecationLevel.ERROR)
public fun <T> SharedFlow<Any>.cancellable(): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Applying 'flowOn' to SharedFlow has no effect. See the SharedFlow documentation on Operator Fusion.", replaceWith = @ReplaceWith(expression = "this", imports = []), level = DeprecationLevel.ERROR)
public fun <T> SharedFlow<Any>.flowOn(context: CoroutineContext): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Applying 'conflate' to StateFlow has no effect. See the StateFlow documentation on Operator Fusion.", replaceWith = @ReplaceWith(expression = "this", imports = []), level = DeprecationLevel.ERROR)
public fun <T> StateFlow<Any>.conflate(): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Applying 'distinctUntilChanged' to StateFlow has no effect. See the StateFlow documentation on Operator Fusion.", replaceWith = @ReplaceWith(expression = "this", imports = []), level = DeprecationLevel.ERROR)
public fun <T> StateFlow<Any>.distinctUntilChanged(): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(
   message = "isActive is resolved into the extension of outer CoroutineScope which is likely to be an error.Use currentCoroutineContext().isActive or cancellable() operator instead or specify the receiver of isActive explicitly. Additionally, flow {} builder emissions are cancellable by default.",
   replaceWith = @ReplaceWith(expression = "currentCoroutineContext().isActive", imports = {}),
   level = DeprecationLevel.ERROR
)
public final val isActive: Boolean
   public final get() {
      FlowKt.noImpl()
      throw KotlinNothingValueException()
   }


@Deprecated(message = "cancel() is resolved into the extension of outer CoroutineScope which is likely to be an error.Use currentCoroutineContext().cancel() instead or specify the receiver of cancel() explicitly", replaceWith = @ReplaceWith(expression = "currentCoroutineContext().cancel(cause)", imports = []), level = DeprecationLevel.ERROR)
public fun FlowCollector<*>.cancel(cause: CancellationException? = null) {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(
   message = "coroutineContext is resolved into the property of outer CoroutineScope which is likely to be an error.Use currentCoroutineContext() instead or specify the receiver of coroutineContext explicitly",
   replaceWith = @ReplaceWith(expression = "currentCoroutineContext()", imports = {}),
   level = DeprecationLevel.ERROR
)
public final val coroutineContext: CoroutineContext
   public final get() {
      FlowKt.noImpl()
      throw KotlinNothingValueException()
   }


@Deprecated(message = "SharedFlow never completes, so this operator typically has not effect, it can only catch exceptions from 'onSubscribe' operator", replaceWith = @ReplaceWith(expression = "this", imports = []), level = DeprecationLevel.WARNING)
@InlineOnly
public inline fun <T> SharedFlow<Any>.catch(noinline action: (FlowCollector<Any>, Throwable, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt.catch(`$this$catch`, action)
}

@Deprecated(message = "SharedFlow never completes, so this operator has no effect.", replaceWith = @ReplaceWith(expression = "this", imports = []), level = DeprecationLevel.WARNING)
@InlineOnly
public inline fun <T> SharedFlow<Any>.retry(retries: Long = java.lang.Long.MAX_VALUE, noinline predicate: (Throwable, Continuation<Boolean>) -> Any? = {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2): Flow<Any> {
   return FlowKt.retry(`$this$retry`, retries, predicate)
}

@Deprecated(message = "SharedFlow never completes, so this operator has no effect.", replaceWith = @ReplaceWith(expression = "this", imports = []), level = DeprecationLevel.WARNING)
@InlineOnly
public inline fun <T> SharedFlow<Any>.retryWhen(noinline predicate: (FlowCollector<Any>, Throwable, Long, Continuation<Boolean>) -> Any?): Flow<Any> {
   return FlowKt.retryWhen(`$this$retryWhen`, predicate)
}

@Deprecated(message = "SharedFlow never completes, so this terminal operation never completes.", level = DeprecationLevel.WARNING)
@InlineOnly
public suspend inline fun <T> SharedFlow<Any>.toList(): List<Any> {
   val var10000: Flow = `$this$toList`
   InlineMarker.mark(0)
   val var2: Any = FlowKt.toList$default(var10000, null, `$completion`, 1, null)
   InlineMarker.mark(1)
   return var2
}

@InlineOnly
public suspend inline fun <T> SharedFlow<Any>.toList(destination: MutableList<Any>): Nothing {
   val var10000: Flow = `$this$toList`
   InlineMarker.mark(0)
   FlowKt.toList(var10000, destination, `$completion`)
   InlineMarker.mark(1)
   throw IllegalStateException("this code is supposed to be unreachable")
}

@Deprecated(message = "SharedFlow never completes, so this terminal operation never completes.", level = DeprecationLevel.WARNING)
@InlineOnly
public suspend inline fun <T> SharedFlow<Any>.toSet(): Set<Any> {
   val var10000: Flow = `$this$toSet`
   InlineMarker.mark(0)
   val var2: Any = FlowKt.toSet$default(var10000, null, `$completion`, 1, null)
   InlineMarker.mark(1)
   return var2
}

@InlineOnly
public suspend inline fun <T> SharedFlow<Any>.toSet(destination: MutableSet<Any>): Nothing {
   val var10000: Flow = `$this$toSet`
   InlineMarker.mark(0)
   FlowKt.toSet(var10000, destination, `$completion`)
   InlineMarker.mark(1)
   throw IllegalStateException("this code is supposed to be unreachable")
}

@Deprecated(message = "SharedFlow never completes, so this terminal operation never completes.", level = DeprecationLevel.WARNING)
@InlineOnly
public suspend inline fun <T> SharedFlow<Any>.count(): Int {
   val var10000: Flow = `$this$count`
   InlineMarker.mark(0)
   val var2: Any = FlowKt.count(var10000, `$completion`)
   InlineMarker.mark(1)
   return var2
}
