@file:SourceDebugExtension(["SMAP\nCompletableDeferred.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CompletableDeferred.kt\nkotlinx/coroutines/CompletableDeferredKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,94:1\n1#2:95\n*E\n"])

package kotlinx.coroutines

import kotlin.jvm.internal.SourceDebugExtension

public fun <T> CompletableDeferred<Any>.completeWith(result: Result<Any>): Boolean {
   val var10000: java.lang.Throwable = Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(result)
   return if (var10000 == null) `$this$completeWith`.complete(result) else `$this$completeWith`.completeExceptionally(var10000)
}

public fun <T> CompletableDeferred(parent: Job? = null): CompletableDeferred<Any> {
   return CompletableDeferredImpl(parent)
}

public fun <T> CompletableDeferred(value: Any): CompletableDeferred<Any> {
   val var1: CompletableDeferredImpl = CompletableDeferredImpl(null)
   var1.complete(value)
   return var1
}
