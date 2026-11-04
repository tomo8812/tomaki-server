package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.functions.Function4
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Errors.kt\nkotlinx/coroutines/flow/FlowKt__ErrorsKt\n*L\n1#1,113:1\n133#2,15:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__ErrorsKt$retryWhen$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_retryWhen$inlined;
   @JvmSynthetic
   Function4 $predicate$inlined;

   fun `FlowKt__ErrorsKt$retryWhen$$inlined$unsafeFlow$1`(var1: Flow, var2: Function4) {
      this.$this_retryWhen$inlined = var1
      this.$predicate$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label84@{
         if (var2 is FlowKt__ErrorsKt$retryWhen$$inlined$unsafeFlow$1$1) {
            `$continuation` = var2 as FlowKt__ErrorsKt$retryWhen$$inlined$unsafeFlow$1$1
            if (((var2 as FlowKt__ErrorsKt$retryWhen$$inlined$unsafeFlow$1$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label84
            }
         }

         `$continuation` = FlowKt__ErrorsKt$retryWhen$$inlined$unsafeFlow$1$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `$this$retryWhen_u24lambda_u242`: FlowCollector
var var17: Long
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            val var6: Continuation = `$continuation`
            `$this$retryWhen_u24lambda_u242` = collector
            var17 = 0L
         }
         1 -> {
            val var18: Boolean = (boolean)`$continuation`.I$0
            var17 = `$continuation`.J$0
            `$this$retryWhen_u24lambda_u242` = `$continuation`.L$1 as FlowCollector
            this = `$continuation`.L$0 as FlowKt__ErrorsKt$retryWhen$$inlined$unsafeFlow$1
            ResultKt.throwOnFailure(`$result`)
            val var23: java.lang.Throwable = `$result` as java.lang.Throwable
            if (`$result` as java.lang.Throwable != null) {
               val var10000: Function4 = this.$predicate$inlined
               val var10003: java.lang.Long = Boxing.boxLong(var17)
               `$continuation`.L$0 = this
               `$continuation`.L$1 = `$this$retryWhen_u24lambda_u242`
               `$continuation`.L$2 = var23
               `$continuation`.J$0 = var17
               `$continuation`.label = 2
               InlineMarker.mark(6)
               val var27: Any = var10000(`$this$retryWhen_u24lambda_u242`, var23, var10003, `$continuation`)
               InlineMarker.mark(7)
               if (var27 === var5) {
                  return var5
               }

               if (!var27 as java.lang.Boolean) {
                  throw var23
               }

               var17 += 1L
               if (false) {
                  return Unit.INSTANCE
               }
            } else if (var18 == 0) {
               return Unit.INSTANCE
            }
         }
         2 -> {
            var17 = `$continuation`.J$0
            val cause: java.lang.Throwable = `$continuation`.L$2 as java.lang.Throwable
            `$this$retryWhen_u24lambda_u242` = `$continuation`.L$1 as FlowCollector
            this = `$continuation`.L$0 as FlowKt__ErrorsKt$retryWhen$$inlined$unsafeFlow$1
            ResultKt.throwOnFailure(`$result`)
            if (!`$result` as java.lang.Boolean) {
               throw cause
            }

            var17 = var17 + 1L
            if (false) {
               return Unit.INSTANCE
            }
            break
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      while (true) {
         val var28: Flow = this.$this_retryWhen$inlined
         `$continuation`.L$0 = this
         `$continuation`.L$1 = `$this$retryWhen_u24lambda_u242`
         `$continuation`.L$2 = null
         `$continuation`.J$0 = var17
         `$continuation`.I$0 = false
         `$continuation`.label = 1
         val var29: Any = FlowKt.catchImpl(var28, `$this$retryWhen_u24lambda_u242`, `$continuation`)
         if (var29 === var5) {
            return var5
         }

         val var24: java.lang.Throwable = var29 as java.lang.Throwable
         if (var29 as java.lang.Throwable != null) {
            val var30: Function4 = this.$predicate$inlined
            val var32: java.lang.Long = Boxing.boxLong(var17)
            `$continuation`.L$0 = this
            `$continuation`.L$1 = `$this$retryWhen_u24lambda_u242`
            `$continuation`.L$2 = var24
            `$continuation`.J$0 = var17
            `$continuation`.label = 2
            InlineMarker.mark(6)
            val var31: Any = var30(`$this$retryWhen_u24lambda_u242`, var24, var32, `$continuation`)
            InlineMarker.mark(7)
            if (var31 === var5) {
               return var5
            }

            if (!var31 as java.lang.Boolean) {
               throw var24
            }

            var17 += 1L
            if (false) {
               return Unit.INSTANCE
            }
         } else if (false == 0) {
            return Unit.INSTANCE
         }
      }
   }
}
