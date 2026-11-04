package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n*L\n1#1,113:1\n123#2,4:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Object[] $elements$inlined;

   fun `FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$1`(var1: Array<Any>) {
      this.$elements$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label43@{
         if (var2 is FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$1$1) {
            `$continuation` = var2 as FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$1$1
            if (((var2 as FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$1$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label43
            }
         }

         `$continuation` = FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$1$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `$this$flowOf_u24lambda_u248`: FlowCollector
var var8: Int
var var9: Int
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            `$this$flowOf_u24lambda_u248` = collector
            var8 = 0
            var9 = this.$elements$inlined.length
         }
         1 -> {
            var9 = `$continuation`.I$1
            var8 = `$continuation`.I$0
            `$this$flowOf_u24lambda_u248` = `$continuation`.L$1 as FlowCollector
            this = `$continuation`.L$0 as FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$1
            ResultKt.throwOnFailure(`$result`)
            var8++
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      while (var8 < var9) {
         val element: Any = this.$elements$inlined[var8]
         `$continuation`.L$0 = this
         `$continuation`.L$1 = `$this$flowOf_u24lambda_u248`
         `$continuation`.I$0 = var8
         `$continuation`.I$1 = var9
         `$continuation`.label = 1
         if (`$this$flowOf_u24lambda_u248`.emit(element, `$continuation`) === var5) {
            return var5
         }

         var8++
      }

      return Unit.INSTANCE
   }
}
