package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,113:1\n176#2:114\n177#2,2:116\n179#2:119\n13607#3:115\n13608#3:118\n*S KotlinDebug\n*F\n+ 1 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n*L\n176#1:115\n176#1:118\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$8` : Flow<java.lang.Long> {
   @JvmSynthetic
   long[] $this_asFlow$inlined;

   fun `FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$8`(var1: LongArray) {
      this.$this_asFlow$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label43@{
         if (var2 is FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$8$1) {
            `$continuation` = var2 as FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$8$1
            if (((var2 as FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$8$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label43
            }
         }

         `$continuation` = FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$8$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `$this$asFlow_u24lambda_u2415`: FlowCollector
var `$this$forEach$iv`: LongArray
var var10: Int
var var11: Int
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            `$this$asFlow_u24lambda_u2415` = collector
            `$this$forEach$iv` = this.$this_asFlow$inlined
            var10 = 0
            var11 = this.$this_asFlow$inlined.length
         }
         1 -> {
            var11 = `$continuation`.I$1
            var10 = `$continuation`.I$0
            `$this$forEach$iv` = `$continuation`.L$1 as LongArray
            `$this$asFlow_u24lambda_u2415` = `$continuation`.L$0 as FlowCollector
            ResultKt.throwOnFailure(`$result`)
            var10++
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      while (var10 < var11) {
         val var10001: java.lang.Long = Boxing.boxLong(`$this$forEach$iv`[var10])
         `$continuation`.L$0 = `$this$asFlow_u24lambda_u2415`
         `$continuation`.L$1 = `$this$forEach$iv`
         `$continuation`.I$0 = var10
         `$continuation`.I$1 = var11
         `$continuation`.label = 1
         if (`$this$asFlow_u24lambda_u2415`.emit(var10001, `$continuation`) === var5) {
            return var5
         }

         var10++
      }

      return Unit.INSTANCE
   }
}
