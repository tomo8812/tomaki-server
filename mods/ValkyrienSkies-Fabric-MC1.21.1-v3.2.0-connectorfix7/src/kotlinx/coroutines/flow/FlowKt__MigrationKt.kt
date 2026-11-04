@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nMigration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Migration.kt\nkotlinx/coroutines/flow/FlowKt__MigrationKt\n+ 2 Merge.kt\nkotlinx/coroutines/flow/FlowKt__MergeKt\n*L\n1#1,496:1\n193#2:497\n*S KotlinDebug\n*F\n+ 1 Migration.kt\nkotlinx/coroutines/flow/FlowKt__MigrationKt\n*L\n435#1:497\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension

internal fun noImpl(): Nothing {
   throw UnsupportedOperationException("Not implemented, should not be called")
}

@Deprecated(message = "Collect flow in the desired context instead", level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.observeOn(context: CoroutineContext): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Collect flow in the desired context instead", level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.publishOn(context: CoroutineContext): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Use 'flowOn' instead", level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.subscribeOn(context: CoroutineContext): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'onErrorXxx' is 'catch'. Use 'catch { emitAll(fallback) }'", replaceWith = @ReplaceWith(expression = "catch { emitAll(fallback) }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.onErrorResume(fallback: Flow<Any>): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'onErrorXxx' is 'catch'. Use 'catch { emitAll(fallback) }'", replaceWith = @ReplaceWith(expression = "catch { emitAll(fallback) }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.onErrorResumeNext(fallback: Flow<Any>): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Use 'launchIn' with 'onEach', 'onCompletion' and 'catch' instead", level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.subscribe() {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Use 'launchIn' with 'onEach', 'onCompletion' and 'catch' instead", level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.subscribe(onEach: (Any, Continuation<Unit>) -> Any?) {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Use 'launchIn' with 'onEach', 'onCompletion' and 'catch' instead", level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.subscribe(onEach: (Any, Continuation<Unit>) -> Any?, onError: (Throwable, Continuation<Unit>) -> Any?) {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue is 'flatMapConcat'", replaceWith = @ReplaceWith(expression = "flatMapConcat(mapper)", imports = []), level = DeprecationLevel.ERROR)
public fun <T, R> Flow<Any>.flatMap(mapper: (Any, Continuation<Flow<Any>>) -> Any?): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'concatMap' is 'flatMapConcat'", replaceWith = @ReplaceWith(expression = "flatMapConcat(mapper)", imports = []), level = DeprecationLevel.ERROR)
public fun <T, R> Flow<Any>.concatMap(mapper: (Any) -> Flow<Any>): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'merge' is 'flattenConcat'", replaceWith = @ReplaceWith(expression = "flattenConcat()", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Flow<Any>>.merge(): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'flatten' is 'flattenConcat'", replaceWith = @ReplaceWith(expression = "flattenConcat()", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Flow<Any>>.flatten(): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'compose' is 'let'", replaceWith = @ReplaceWith(expression = "let(transformer)", imports = []), level = DeprecationLevel.ERROR)
public fun <T, R> Flow<Any>.compose(transformer: (Flow<Any>) -> Flow<Any>): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'skip' is 'drop'", replaceWith = @ReplaceWith(expression = "drop(count)", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.skip(count: Int): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'forEach' is 'collect'", replaceWith = @ReplaceWith(expression = "collect(action)", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.forEach(action: (Any, Continuation<Unit>) -> Any?) {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow has less verbose 'scan' shortcut", replaceWith = @ReplaceWith(expression = "scan(initial, operation)", imports = []), level = DeprecationLevel.ERROR)
public fun <T, R> Flow<Any>.scanFold(initial: Any, operation: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'onErrorXxx' is 'catch'. Use 'catch { emit(fallback) }'", replaceWith = @ReplaceWith(expression = "catch { emit(fallback) }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.onErrorReturn(fallback: Any): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'onErrorXxx' is 'catch'. Use 'catch { e -> if (predicate(e)) emit(fallback) else throw e }'", replaceWith = @ReplaceWith(expression = "catch { e -> if (predicate(e)) emit(fallback) else throw e }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.onErrorReturn(fallback: Any, predicate: (Throwable) -> Boolean = { it ->
      true
   } as Function1): Flow<Any> {
   return FlowKt.catch(`$this$onErrorReturn`, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function3)
}

@Deprecated(message = "Flow analogue of 'startWith' is 'onStart'. Use 'onStart { emit(value) }'", replaceWith = @ReplaceWith(expression = "onStart { emit(value) }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.startWith(value: Any): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'startWith' is 'onStart'. Use 'onStart { emitAll(other) }'", replaceWith = @ReplaceWith(expression = "onStart { emitAll(other) }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.startWith(other: Flow<Any>): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'concatWith' is 'onCompletion'. Use 'onCompletion { emit(value) }'", replaceWith = @ReplaceWith(expression = "onCompletion { emit(value) }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.concatWith(value: Any): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'concatWith' is 'onCompletion'. Use 'onCompletion { if (it == null) emitAll(other) }'", replaceWith = @ReplaceWith(expression = "onCompletion { if (it == null) emitAll(other) }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.concatWith(other: Flow<Any>): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'combineLatest' is 'combine'", replaceWith = @ReplaceWith(expression = "this.combine(other, transform)", imports = []), level = DeprecationLevel.ERROR)
public fun <T1, T2, R> Flow<Any>.combineLatest(other: Flow<Any>, transform: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt.combine(`$this$combineLatest`, other, transform)
}

@Deprecated(message = "Flow analogue of 'combineLatest' is 'combine'", replaceWith = @ReplaceWith(expression = "combine(this, other, other2, transform)", imports = []), level = DeprecationLevel.ERROR)
public fun <T1, T2, T3, R> Flow<Any>.combineLatest(other: Flow<Any>, other2: Flow<Any>, transform: (Any, Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt.combine(`$this$combineLatest`, other, other2, transform)
}

@Deprecated(message = "Flow analogue of 'combineLatest' is 'combine'", replaceWith = @ReplaceWith(expression = "combine(this, other, other2, other3, transform)", imports = []), level = DeprecationLevel.ERROR)
public fun <T1, T2, T3, T4, R> Flow<Any>.combineLatest(
   other: Flow<Any>,
   other2: Flow<Any>,
   other3: Flow<Any>,
   transform: (Any, Any, Any, Any, Continuation<Any>) -> Any?
): Flow<Any> {
   return FlowKt.combine(`$this$combineLatest`, other, other2, other3, transform)
}

@Deprecated(message = "Flow analogue of 'combineLatest' is 'combine'", replaceWith = @ReplaceWith(expression = "combine(this, other, other2, other3, transform)", imports = []), level = DeprecationLevel.ERROR)
public fun <T1, T2, T3, T4, T5, R> Flow<Any>.combineLatest(
   other: Flow<Any>,
   other2: Flow<Any>,
   other3: Flow<Any>,
   other4: Flow<Any>,
   transform: (Any, Any, Any, Any, Any, Continuation<Any>) -> Any?
): Flow<Any> {
   return FlowKt.combine(`$this$combineLatest`, other, other2, other3, other4, transform)
}

@Deprecated(message = "Use 'onStart { delay(timeMillis) }'", replaceWith = @ReplaceWith(expression = "onStart { delay(timeMillis) }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.delayFlow(timeMillis: Long): Flow<Any> {
   return FlowKt.onStart(`$this$delayFlow`, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2)
}

@Deprecated(message = "Use 'onEach { delay(timeMillis) }'", replaceWith = @ReplaceWith(expression = "onEach { delay(timeMillis) }", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.delayEach(timeMillis: Long): Flow<Any> {
   return FlowKt.onEach(`$this$delayEach`, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2)
}

@Deprecated(message = "Flow analogues of 'switchMap' are 'transformLatest', 'flatMapLatest' and 'mapLatest'", replaceWith = @ReplaceWith(expression = "this.flatMapLatest(transform)", imports = []), level = DeprecationLevel.ERROR)
public fun <T, R> Flow<Any>.switchMap(transform: (Any, Continuation<Flow<Any>>) -> Any?): Flow<Any> {
   return FlowKt.transformLatest(`$this$switchMap`, FlowKt__MigrationKt$switchMap$$inlined$flatMapLatest$1(transform, null))
}

@Deprecated(message = "'scanReduce' was renamed to 'runningReduce' to be consistent with Kotlin standard library", replaceWith = @ReplaceWith(expression = "runningReduce(operation)", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.scanReduce(operation: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt.runningReduce(`$this$scanReduce`, operation)
}

@Deprecated(message = "Flow analogue of 'publish()' is 'shareIn'. \npublish().connect() is the default strategy (no extra call is needed), \npublish().autoConnect() translates to 'started = SharingStared.Lazily' argument, \npublish().refCount() translates to 'started = SharingStared.WhileSubscribed()' argument.", replaceWith = @ReplaceWith(expression = "this.shareIn(scope, 0)", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.publish(): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'publish(bufferSize)' is 'buffer' followed by 'shareIn'. \npublish().connect() is the default strategy (no extra call is needed), \npublish().autoConnect() translates to 'started = SharingStared.Lazily' argument, \npublish().refCount() translates to 'started = SharingStared.WhileSubscribed()' argument.", replaceWith = @ReplaceWith(expression = "this.buffer(bufferSize).shareIn(scope, 0)", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.publish(bufferSize: Int): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'replay()' is 'shareIn' with unlimited replay. \nreplay().connect() is the default strategy (no extra call is needed), \nreplay().autoConnect() translates to 'started = SharingStared.Lazily' argument, \nreplay().refCount() translates to 'started = SharingStared.WhileSubscribed()' argument.", replaceWith = @ReplaceWith(expression = "this.shareIn(scope, Int.MAX_VALUE)", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.replay(): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'replay(bufferSize)' is 'shareIn' with the specified replay parameter. \nreplay().connect() is the default strategy (no extra call is needed), \nreplay().autoConnect() translates to 'started = SharingStared.Lazily' argument, \nreplay().refCount() translates to 'started = SharingStared.WhileSubscribed()' argument.", replaceWith = @ReplaceWith(expression = "this.shareIn(scope, bufferSize)", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.replay(bufferSize: Int): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}

@Deprecated(message = "Flow analogue of 'cache()' is 'shareIn' with unlimited replay and 'started = SharingStared.Lazily' argument'", replaceWith = @ReplaceWith(expression = "this.shareIn(scope, Int.MAX_VALUE, started = SharingStared.Lazily)", imports = []), level = DeprecationLevel.ERROR)
public fun <T> Flow<Any>.cache(): Flow<Any> {
   FlowKt.noImpl()
   throw KotlinNothingValueException()
}
