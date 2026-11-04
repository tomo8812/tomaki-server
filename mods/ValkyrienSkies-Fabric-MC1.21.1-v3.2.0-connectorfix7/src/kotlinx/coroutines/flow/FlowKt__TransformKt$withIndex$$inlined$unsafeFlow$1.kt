package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n*L\n1#1,113:1\n69#2,5:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__TransformKt$withIndex$$inlined$unsafeFlow$1` : Flow<IndexedValue<? extends T>> {
   @JvmSynthetic
   Flow $this_withIndex$inlined;

   fun `FlowKt__TransformKt$withIndex$$inlined$unsafeFlow$1`(var1: Flow) {
      this.$this_withIndex$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val `$this$withIndex_u24lambda_u247`: FlowCollector = collector
      val index: IntRef = IntRef()
      val var10000: Any = this.$this_withIndex$inlined
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
               // 01: instanceof kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1
               // 04: ifeq 27
               // 07: aload 2
               // 08: checkcast kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1
               // 0b: astore 6
               // 0d: aload 6
               // 0f: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1.label I
               // 12: ldc -2147483648
               // 14: iand
               // 15: ifeq 27
               // 18: aload 6
               // 1a: dup
               // 1b: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1.label I
               // 1e: ldc -2147483648
               // 20: isub
               // 21: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1.label I
               // 24: goto 32
               // 27: new kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1
               // 2a: dup
               // 2b: aload 0
               // 2c: aload 2
               // 2d: invokespecial kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1;Lkotlin/coroutines/Continuation;)V
               // 30: astore 6
               // 32: aload 6
               // 34: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1.result Ljava/lang/Object;
               // 37: astore 5
               // 39: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
               // 3c: astore 7
               // 3e: aload 6
               // 40: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1.label I
               // 43: tableswitch 110 0 1 21 98
               // 58: aload 5
               // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 5d: aload 0
               // 5e: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
               // 61: new kotlin/collections/IndexedValue
               // 64: dup
               // 65: aload 0
               // 66: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1.$index Lkotlin/jvm/internal/Ref$IntRef;
               // 69: getfield kotlin/jvm/internal/Ref$IntRef.element I
               // 6c: istore 3
               // 6d: aload 0
               // 6e: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1.$index Lkotlin/jvm/internal/Ref$IntRef;
               // 71: iload 3
               // 72: bipush 1
               // 73: iadd
               // 74: putfield kotlin/jvm/internal/Ref$IntRef.element I
               // 77: iload 3
               // 78: istore 3
               // 79: bipush 0
               // 7a: istore 4
               // 7c: iload 3
               // 7d: ifge 8a
               // 80: new java/lang/ArithmeticException
               // 83: dup
               // 84: ldc "Index overflow has happened"
               // 86: invokespecial java/lang/ArithmeticException.<init> (Ljava/lang/String;)V
               // 89: athrow
               // 8a: iload 3
               // 8b: aload 1
               // 8c: invokespecial kotlin/collections/IndexedValue.<init> (ILjava/lang/Object;)V
               // 8f: aload 6
               // 91: aload 6
               // 93: bipush 1
               // 94: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$withIndex$1$1$emit$1.label I
               // 97: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
               // 9c: dup
               // 9d: aload 7
               // 9f: if_acmpne ac
               // a2: aload 7
               // a4: areturn
               // a5: aload 5
               // a7: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // aa: aload 5
               // ac: pop
               // ad: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // b0: areturn
               // b1: new java/lang/IllegalStateException
               // b4: dup
               // b5: ldc "call to 'resume' before 'invoke' with coroutine"
               // b7: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // ba: athrow
            },
            `$completion`
         )
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
