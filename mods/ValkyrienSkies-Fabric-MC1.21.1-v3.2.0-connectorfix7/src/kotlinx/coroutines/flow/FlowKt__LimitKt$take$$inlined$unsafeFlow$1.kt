package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlinx.coroutines.flow.internal.AbortFlowException
import kotlinx.coroutines.flow.internal.FlowExceptions_commonKt

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt\n*L\n1#1,113:1\n53#2,3:114\n66#2,4:117\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__LimitKt$take$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_take$inlined;
   @JvmSynthetic
   int $count$inlined;

   fun `FlowKt__LimitKt$take$$inlined$unsafeFlow$1`(var1: Flow, var2: Int) {
      this.$this_take$inlined = var1
      this.$count$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label51@{
         if (var2 is FlowKt__LimitKt$take$$inlined$unsafeFlow$1$1) {
            `$continuation` = var2 as FlowKt__LimitKt$take$$inlined$unsafeFlow$1$1
            if (((var2 as FlowKt__LimitKt$take$$inlined$unsafeFlow$1$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label51
            }
         }

         `$continuation` = FlowKt__LimitKt$take$$inlined$unsafeFlow$1$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `$this$take_u24lambda_u244`: FlowCollector
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            `$this$take_u24lambda_u244` = collector
            val consumed: IntRef = IntRef()

            var var14: Flow
            try {
               var14 = this.$this_take$inlined
               val var10001: FlowCollector = { value: Any ->
                  // $VF: Couldn't be decompiled
                  // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
                  // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
                  //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
                  //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
                  //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
                  //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
                  //   at org.jetbrains.java.decompiler.modules.decompiler.exps.AssignmentExprent.toJava(AssignmentExprent.java:161)
                  //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
                  //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
                  //
                  // Bytecode:
                  // 00: aload 2
                  // 01: instanceof kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1
                  // 04: ifeq 27
                  // 07: aload 2
                  // 08: checkcast kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1
                  // 0b: astore 4
                  // 0d: aload 4
                  // 0f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1.label I
                  // 12: ldc -2147483648
                  // 14: iand
                  // 15: ifeq 27
                  // 18: aload 4
                  // 1a: dup
                  // 1b: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1.label I
                  // 1e: ldc -2147483648
                  // 20: isub
                  // 21: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1.label I
                  // 24: goto 32
                  // 27: new kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1
                  // 2a: dup
                  // 2b: aload 0
                  // 2c: aload 2
                  // 2d: invokespecial kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1;Lkotlin/coroutines/Continuation;)V
                  // 30: astore 4
                  // 32: aload 4
                  // 34: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1.result Ljava/lang/Object;
                  // 37: astore 3
                  // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
                  // 3b: astore 5
                  // 3d: aload 4
                  // 3f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1.label I
                  // 42: tableswitch 132 0 2 26 87 122
                  // 5c: aload 3
                  // 5d: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
                  // 60: aload 0
                  // 61: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1.$consumed Lkotlin/jvm/internal/Ref$IntRef;
                  // 64: aload 0
                  // 65: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1.$consumed Lkotlin/jvm/internal/Ref$IntRef;
                  // 68: getfield kotlin/jvm/internal/Ref$IntRef.element I
                  // 6b: bipush 1
                  // 6c: iadd
                  // 6d: putfield kotlin/jvm/internal/Ref$IntRef.element I
                  // 70: aload 0
                  // 71: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1.$consumed Lkotlin/jvm/internal/Ref$IntRef;
                  // 74: getfield kotlin/jvm/internal/Ref$IntRef.element I
                  // 77: aload 0
                  // 78: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1.$count I
                  // 7b: if_icmpge a3
                  // 7e: aload 0
                  // 7f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
                  // 82: aload 1
                  // 83: aload 4
                  // 85: aload 4
                  // 87: bipush 1
                  // 88: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1.label I
                  // 8b: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
                  // 90: dup
                  // 91: aload 5
                  // 93: if_acmpne 9e
                  // 96: aload 5
                  // 98: areturn
                  // 99: aload 3
                  // 9a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
                  // 9d: aload 3
                  // 9e: pop
                  // 9f: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
                  // a2: areturn
                  // a3: aload 0
                  // a4: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
                  // a7: aload 1
                  // a8: aload 4
                  // aa: aload 4
                  // ac: bipush 2
                  // ad: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$take$2$1$emit$1.label I
                  // b0: invokestatic kotlinx/coroutines/flow/FlowKt__LimitKt.access$emitAbort$FlowKt__LimitKt (Lkotlinx/coroutines/flow/FlowCollector;Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
                  // b3: dup
                  // b4: aload 5
                  // b6: if_acmpne c1
                  // b9: aload 5
                  // bb: areturn
                  // bc: aload 3
                  // bd: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
                  // c0: aload 3
                  // c1: pop
                  // c2: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
                  // c5: areturn
                  // c6: new java/lang/IllegalStateException
                  // c9: dup
                  // ca: ldc "call to 'resume' before 'invoke' with coroutine"
                  // cc: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
                  // cf: athrow
               }
               `$continuation`.L$0 = `$this$take_u24lambda_u244`
               `$continuation`.label = 1
               var14 = (Flow)var14.collect(var10001, `$continuation`)
            } catch (var11: AbortFlowException) {
               FlowExceptions_commonKt.checkOwnership(var11, collector)
               return Unit.INSTANCE
            }

            if (var14 === var5) {
               return var5
            }
            break
         }
         1 -> {
            `$this$take_u24lambda_u244` = `$continuation`.L$0 as FlowCollector

            try {
               ResultKt.throwOnFailure(`$result`)
               break;
            } catch (var12: AbortFlowException) {
               FlowExceptions_commonKt.checkOwnership(var12, `$continuation`.L$0 as FlowCollector<*>)
               return Unit.INSTANCE
            }
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      try {
      } catch (var10: AbortFlowException) {
         FlowExceptions_commonKt.checkOwnership(var10, `$this$take_u24lambda_u244`)
      }

      return Unit.INSTANCE
   }
}
