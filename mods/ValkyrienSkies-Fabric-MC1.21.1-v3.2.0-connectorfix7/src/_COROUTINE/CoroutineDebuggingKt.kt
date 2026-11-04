@file:SourceDebugExtension(["SMAP\nCoroutineDebugging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineDebugging.kt\n_COROUTINE/CoroutineDebuggingKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,66:1\n1#2:67\n*E\n"])

package _COROUTINE

import kotlin.jvm.internal.SourceDebugExtension

internal final val ARTIFICIAL_FRAME_PACKAGE_NAME: String = "_COROUTINE"

private fun Throwable.artificialFrame(name: String): StackTraceElement {
   val `$this$artificialFrame_u24lambda_u240`: StackTraceElement = `$this$artificialFrame`.getStackTrace()[0]
   return StackTraceElement(
      "${ARTIFICIAL_FRAME_PACKAGE_NAME}.$name",
      "_",
      `$this$artificialFrame_u24lambda_u240`.getFileName(),
      `$this$artificialFrame_u24lambda_u240`.getLineNumber()
   )
}

@JvmSynthetic
fun `access$artificialFrame`(`$receiver`: java.lang.Throwable, name: java.lang.String): StackTraceElement {
   artificialFrame(`$receiver`, name)
}
