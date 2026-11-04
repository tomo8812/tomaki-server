package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n*L\n1#1,113:1\n83#2,2:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$2` : Flow<T> {
   @JvmSynthetic
   Function1 $this_asFlow$inlined;

   fun `FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$2`(var1: Function1) {
      this.$this_asFlow$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label36@{
         if (var2 is FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$2$1) {
            `$continuation` = var2 as FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$2$1
            if (((var2 as FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$2$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label36
            }
         }

         `$continuation` = FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$2$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var var9: FlowCollector
var var10000: Any
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            val var6: Continuation = `$continuation`
            var9 = collector
            var10000 = this.$this_asFlow$inlined
            `$continuation`.L$0 = collector
            `$continuation`.label = 1
            InlineMarker.mark(6)
            var10000 = (Function1)var10000(`$continuation`)
            InlineMarker.mark(7)
            if (var10000 === var5) {
               return var5
            }
         }
         1 -> {
            var9 = `$continuation`.L$0 as FlowCollector
            ResultKt.throwOnFailure(`$result`)
            var10000 = (Function1)`$result`
         }
         2 -> {
            ResultKt.throwOnFailure(`$result`)
            return Unit.INSTANCE
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      `$continuation`.L$0 = null
      `$continuation`.label = 2
      return if (var9.emit(var10000, `$continuation`) === var5) var5 else Unit.INSTANCE
   }
}
