package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n+ 3 Iterators.kt\nkotlin/collections/CollectionsKt__IteratorsKt\n*L\n1#1,113:1\n99#2:114\n100#2,2:116\n102#2:119\n32#3:115\n33#3:118\n*S KotlinDebug\n*F\n+ 1 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n*L\n99#1:115\n99#1:118\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$4` : Flow<T> {
   @JvmSynthetic
   java.util.Iterator $this_asFlow$inlined;

   fun `FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$4`(var1: java.util.Iterator) {
      this.$this_asFlow$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label43@{
         if (var2 is FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$4$1) {
            `$continuation` = var2 as FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$4$1
            if (((var2 as FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$4$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label43
            }
         }

         `$continuation` = FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$4$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `$this$asFlow_u24lambda_u245`: FlowCollector
var var10: java.util.Iterator
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            `$this$asFlow_u24lambda_u245` = collector
            var10 = this.$this_asFlow$inlined
         }
         1 -> {
            var10 = `$continuation`.L$1 as java.util.Iterator
            `$this$asFlow_u24lambda_u245` = `$continuation`.L$0 as FlowCollector
            ResultKt.throwOnFailure(`$result`)
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      while (var10.hasNext()) {
         val value: Any = var10.next()
         `$continuation`.L$0 = `$this$asFlow_u24lambda_u245`
         `$continuation`.L$1 = var10
         `$continuation`.label = 1
         if (`$this$asFlow_u24lambda_u245`.emit(value, `$continuation`) === var5) {
            return var5
         }
      }

      return Unit.INSTANCE
   }
}
