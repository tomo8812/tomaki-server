package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Errors.kt\nkotlinx/coroutines/flow/FlowKt__ErrorsKt\n*L\n1#1,113:1\n59#2,3:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_catch$inlined;
   @JvmSynthetic
   Function3 $action$inlined;

   fun `FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1`(var1: Flow, var2: Function3) {
      this.$this_catch$inlined = var1
      this.$action$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label48@{
         if (var2 is FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1$1) {
            `$continuation` = var2 as FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1$1
            if (((var2 as FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label48
            }
         }

         `$continuation` = FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `$this$catch_u24lambda_u240`: FlowCollector
var var10000: Any
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            val var6: Continuation = `$continuation`
            `$this$catch_u24lambda_u240` = collector
            val var12: Flow = this.$this_catch$inlined
            `$continuation`.L$0 = this
            `$continuation`.L$1 = collector
            `$continuation`.label = 1
            var10000 = FlowKt.catchImpl(var12, collector, `$continuation`)
            if (var10000 === var5) {
               return var5
            }
         }
         1 -> {
            `$this$catch_u24lambda_u240` = `$continuation`.L$1 as FlowCollector
            this = `$continuation`.L$0 as FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1
            ResultKt.throwOnFailure(`$result`)
            var10000 = `$result`
         }
         2 -> {
            ResultKt.throwOnFailure(`$result`)
            return Unit.INSTANCE
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      val exception: java.lang.Throwable = var10000 as java.lang.Throwable
      if (var10000 as java.lang.Throwable != null) {
         val var13: Function3 = this.$action$inlined
         `$continuation`.L$0 = null
         `$continuation`.L$1 = null
         `$continuation`.label = 2
         InlineMarker.mark(6)
         val var14: Any = var13(`$this$catch_u24lambda_u240`, exception, `$continuation`)
         InlineMarker.mark(7)
         if (var14 === var5) {
            return var5
         }
      }

      return Unit.INSTANCE
   }
}
