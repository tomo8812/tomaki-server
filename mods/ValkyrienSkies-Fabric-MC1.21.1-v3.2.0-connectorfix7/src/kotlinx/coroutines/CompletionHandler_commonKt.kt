package kotlinx.coroutines

import kotlin.jvm.internal.Intrinsics

@JvmSynthetic
internal inline fun <reified T> ((Throwable?) -> Unit).isHandlerOf(): Boolean {
   Intrinsics.reifiedOperationMarker(3, "T")
   return `$this$isHandlerOf` is Any
}
