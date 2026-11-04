@file:SourceDebugExtension(["SMAP\nMainDispatchers.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainDispatchers.kt\nkotlinx/coroutines/internal/MainDispatchersKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,134:1\n1#2:135\n*E\n"])

package kotlinx.coroutines.internal

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher

private const val FAST_SERVICE_LOADER_PROPERTY_NAME: String = "kotlinx.coroutines.fast.service.loader"
private final val SUPPORT_MISSING: Boolean = true

@InternalCoroutinesApi
public fun MainDispatcherFactory.tryCreateDispatcher(factories: List<MainDispatcherFactory>): MainCoroutineDispatcher {
   var var2: MainCoroutineDispatcher
   try {
      var2 = `$this$tryCreateDispatcher`.createDispatcher(factories)
   } catch (var4: java.lang.Throwable) {
      var2 = createMissingDispatcher(var4, `$this$tryCreateDispatcher`.hintOnError())
   }

   return var2
}

@InternalCoroutinesApi
public fun MainCoroutineDispatcher.isMissing(): Boolean {
   return `$this$isMissing`.immediate is MissingMainCoroutineDispatcher
}

private fun createMissingDispatcher(cause: Throwable? = null, errorHint: String? = null): MissingMainCoroutineDispatcher {
   if (SUPPORT_MISSING) {
      return MissingMainCoroutineDispatcher(cause, errorHint)
   } else if (cause != null) {
      throw cause
   } else {
      throwMissingMainDispatcherException()
      throw KotlinNothingValueException()
   }
}

internal fun throwMissingMainDispatcherException(): Nothing {
   throw IllegalStateException(
      "Module with the Main dispatcher is missing. Add dependency providing the Main dispatcher, e.g. 'kotlinx-coroutines-android' and ensure it has the same version as 'kotlinx-coroutines-core'"
   )
}
