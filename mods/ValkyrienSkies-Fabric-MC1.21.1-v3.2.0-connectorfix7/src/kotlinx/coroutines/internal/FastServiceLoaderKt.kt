@file:SourceDebugExtension(["SMAP\nFastServiceLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FastServiceLoader.kt\nkotlinx/coroutines/internal/FastServiceLoaderKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,161:1\n1#2:162\n*E\n"])

package kotlinx.coroutines.internal

import kotlin.jvm.internal.SourceDebugExtension

internal final val ANDROID_DETECTED: Boolean

fun {
   var var0: Any
   try {
      var0 = Result.constructor_impl/* $VF was: constructor-impl */(Class.forName("android.os.Build"))
   } catch (var2: java.lang.Throwable) {
      var0 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var2))
   }

   ANDROID_DETECTED = Result.isSuccess_impl/* $VF was: isSuccess-impl */(var0)
}
