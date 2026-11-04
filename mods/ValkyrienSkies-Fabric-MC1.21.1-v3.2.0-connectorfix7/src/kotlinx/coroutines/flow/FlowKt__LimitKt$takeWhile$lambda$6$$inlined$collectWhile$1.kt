package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.internal.AbortFlowException

@SourceDebugExtension(["SMAP\nLimit.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1\n+ 2 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt\n*L\n1#1,141:1\n86#2,5:142\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1` : FlowCollector<T> {
   @JvmSynthetic
   Function2 $predicate$inlined;
   @JvmSynthetic
   FlowCollector $this_unsafeFlow$inlined;

   fun `FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1`(var1: Function2, var2: FlowCollector) {
      this.$predicate$inlined = var1
      this.$this_unsafeFlow$inlined = var2
   }

   public override suspend fun emit(value: Any) {
      var `$continuation`: Continuation
      run label50@{
         if (var2 is FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1$1) {
            `$continuation` = var2 as FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1$1
            if (((var2 as FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label50
            }
         }

         `$continuation` = FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1$1(this, var2)
      }

      var var13: Boolean
      run label56@{
         run label55@{
            val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var valuex: Any
var var10000: Any
            when (`$continuation`.label) {
               0 -> {
                  ResultKt.throwOnFailure(`$result`)
                  val var6: Continuation = `$continuation`
                  valuex = value
                  val var11: Function2 = this.$predicate$inlined
                  `$continuation`.L$0 = this
                  `$continuation`.L$1 = value
                  `$continuation`.label = 1
                  InlineMarker.mark(6)
                  var10000 = var11(value, `$continuation`)
                  InlineMarker.mark(7)
                  if (var10000 === var5) {
                     return var5
                  }
               }
               1 -> {
                  valuex = `$continuation`.L$1
                  this = `$continuation`.L$0 as FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1
                  ResultKt.throwOnFailure(`$result`)
                  var10000 = `$result`
               }
               2 -> {
                  this = `$continuation`.L$0 as FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1
                  ResultKt.throwOnFailure(`$result`)
                  return@label55
               }
               else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
            }

            if (!var10000 as java.lang.Boolean) {
               var13 = false
               return@label56
            }

            val var12: FlowCollector = this.$this_unsafeFlow$inlined
            `$continuation`.L$0 = this
            `$continuation`.L$1 = null
            `$continuation`.label = 2
            if (var12.emit(valuex, `$continuation`) === var5) {
               return var5
            }
         }

         var13 = true
      }

      if (!var13) {
         throw AbortFlowException(this as FlowCollector<*>)
      } else {
         return Unit.INSTANCE
      }
   }
}
