package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt\n*L\n1#1,113:1\n23#2,5:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__LimitKt$drop$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_drop$inlined;
   @JvmSynthetic
   int $count$inlined;

   fun `FlowKt__LimitKt$drop$$inlined$unsafeFlow$1`(var1: Flow, var2: Int) {
      this.$this_drop$inlined = var1
      this.$count$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val `$this$drop_u24lambda_u241`: FlowCollector = collector
      val skipped: IntRef = IntRef()
      val var10000: Any = this.$this_drop$inlined
         .collect(
            { value: Any ->
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
               // 01: instanceof kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1
               // 04: ifeq 27
               // 07: aload 2
               // 08: checkcast kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1
               // 0b: astore 4
               // 0d: aload 4
               // 0f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1.label I
               // 12: ldc -2147483648
               // 14: iand
               // 15: ifeq 27
               // 18: aload 4
               // 1a: dup
               // 1b: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1.label I
               // 1e: ldc -2147483648
               // 20: isub
               // 21: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1.label I
               // 24: goto 32
               // 27: new kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1
               // 2a: dup
               // 2b: aload 0
               // 2c: aload 2
               // 2d: invokespecial kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1;Lkotlin/coroutines/Continuation;)V
               // 30: astore 4
               // 32: aload 4
               // 34: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1.result Ljava/lang/Object;
               // 37: astore 3
               // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
               // 3b: astore 5
               // 3d: aload 4
               // 3f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1.label I
               // 42: tableswitch 105 0 1 22 67
               // 58: aload 3
               // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 5c: aload 0
               // 5d: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1.$skipped Lkotlin/jvm/internal/Ref$IntRef;
               // 60: getfield kotlin/jvm/internal/Ref$IntRef.element I
               // 63: aload 0
               // 64: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1.$count I
               // 67: if_icmplt 8f
               // 6a: aload 0
               // 6b: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
               // 6e: aload 1
               // 6f: aload 4
               // 71: aload 4
               // 73: bipush 1
               // 74: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1$emit$1.label I
               // 77: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
               // 7c: dup
               // 7d: aload 5
               // 7f: if_acmpne 8a
               // 82: aload 5
               // 84: areturn
               // 85: aload 3
               // 86: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 89: aload 3
               // 8a: pop
               // 8b: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 8e: areturn
               // 8f: aload 0
               // 90: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1.$skipped Lkotlin/jvm/internal/Ref$IntRef;
               // 93: aload 0
               // 94: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1.$skipped Lkotlin/jvm/internal/Ref$IntRef;
               // 97: getfield kotlin/jvm/internal/Ref$IntRef.element I
               // 9a: bipush 1
               // 9b: iadd
               // 9c: putfield kotlin/jvm/internal/Ref$IntRef.element I
               // 9f: aload 0
               // a0: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$drop$2$1.$skipped Lkotlin/jvm/internal/Ref$IntRef;
               // a3: getfield kotlin/jvm/internal/Ref$IntRef.element I
               // a6: pop
               // a7: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // aa: areturn
               // ab: new java/lang/IllegalStateException
               // ae: dup
               // af: ldc "call to 'resume' before 'invoke' with coroutine"
               // b1: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // b4: athrow
            },
            `$completion`
         )
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
