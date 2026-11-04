package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,113:1\n185#2:114\n186#2,2:116\n188#2:119\n1855#3:115\n1856#3:118\n*S KotlinDebug\n*F\n+ 1 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n*L\n185#1:115\n185#1:118\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$9` : Flow<Integer> {
   @JvmSynthetic
   IntRange $this_asFlow$inlined;

   fun `FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$9`(var1: IntRange) {
      this.$this_asFlow$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label43@{
         if (var2 is FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$9$1) {
            `$continuation` = var2 as FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$9$1
            if (((var2 as FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$9$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label43
            }
         }

         `$continuation` = FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$9$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `$this$asFlow_u24lambda_u2417`: FlowCollector
var var10: java.util.Iterator
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            `$this$asFlow_u24lambda_u2417` = collector
            var10 = (this.$this_asFlow$inlined as java.lang.Iterable).iterator()
         }
         1 -> {
            var10 = `$continuation`.L$1 as java.util.Iterator
            `$this$asFlow_u24lambda_u2417` = `$continuation`.L$0 as FlowCollector
            ResultKt.throwOnFailure(`$result`)
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      while (var10.hasNext()) {
         val var10001: Int = Boxing.boxInt((var10 as IntIterator).nextInt())
         `$continuation`.L$0 = `$this$asFlow_u24lambda_u2417`
         `$continuation`.L$1 = var10
         `$continuation`.label = 1
         if (`$this$asFlow_u24lambda_u2417`.emit(var10001, `$continuation`) === var5) {
            return var5
         }
      }

      return Unit.INSTANCE
   }
}
