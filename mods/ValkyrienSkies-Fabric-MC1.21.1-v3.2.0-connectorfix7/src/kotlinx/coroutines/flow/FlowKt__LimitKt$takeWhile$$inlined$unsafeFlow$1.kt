package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.internal.AbortFlowException
import kotlinx.coroutines.flow.internal.FlowExceptions_commonKt

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt\n*L\n1#1,113:1\n85#2:114\n126#2,15:115\n*S KotlinDebug\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt\n*L\n85#1:115,15\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__LimitKt$takeWhile$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_takeWhile$inlined;
   @JvmSynthetic
   Function2 $predicate$inlined;

   fun `FlowKt__LimitKt$takeWhile$$inlined$unsafeFlow$1`(var1: Flow, var2: Function2) {
      this.$this_takeWhile$inlined = var1
      this.$predicate$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label51@{
         if (var2 is FlowKt__LimitKt$takeWhile$$inlined$unsafeFlow$1$1) {
            `$continuation` = var2 as FlowKt__LimitKt$takeWhile$$inlined$unsafeFlow$1$1
            if (((var2 as FlowKt__LimitKt$takeWhile$$inlined$unsafeFlow$1$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label51
            }
         }

         `$continuation` = FlowKt__LimitKt$takeWhile$$inlined$unsafeFlow$1$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `collector$iv`: FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            val `$this$collectWhile$iv`: Flow = this.$this_takeWhile$inlined
            val `$completion$iv`: Continuation = `$continuation`
            `collector$iv` = FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1(this.$predicate$inlined, collector)

            var var10000: Any
            try {
               val var10001: FlowCollector = `collector$iv`
               `$continuation`.L$0 = `collector$iv`
               `$continuation`.label = 1
               var10000 = `$this$collectWhile$iv`.collect(var10001, `$completion$iv`)
            } catch (var14: AbortFlowException) {
               FlowExceptions_commonKt.checkOwnership(var14, `collector$iv`)
               return Unit.INSTANCE
            }

            if (var10000 === var5) {
               return var5
            }
            break
         }
         1 -> {
            `collector$iv` = `$continuation`.L$0 as FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1

            try {
               ResultKt.throwOnFailure(`$result`)
               break;
            } catch (var15: AbortFlowException) {
               FlowExceptions_commonKt.checkOwnership(var15, `$continuation`.L$0 as FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1)
               return Unit.INSTANCE
            }
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      try {
      } catch (var13: AbortFlowException) {
         FlowExceptions_commonKt.checkOwnership(var13, `collector$iv`)
      }

      return Unit.INSTANCE
   }
}
