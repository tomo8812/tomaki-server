package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n*L\n1#1,113:1\n51#2,5:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__EmittersKt$unsafeTransform$$inlined$unsafeFlow$1` : Flow<R> {
   @JvmSynthetic
   Flow $this_unsafeTransform$inlined;
   @JvmSynthetic
   Function3 $transform$inlined;

   fun `FlowKt__EmittersKt$unsafeTransform$$inlined$unsafeFlow$1`(var1: Flow, var2: Function3) {
      this.$this_unsafeTransform$inlined = var1
      this.$transform$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val `$this$unsafeTransform_u24lambda_u240`: FlowCollector = collector
      val var10000: Any = this.$this_unsafeTransform$inlined
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
               // 01: instanceof kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1
               // 04: ifeq 27
               // 07: aload 2
               // 08: checkcast kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1
               // 0b: astore 4
               // 0d: aload 4
               // 0f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
               // 12: ldc -2147483648
               // 14: iand
               // 15: ifeq 27
               // 18: aload 4
               // 1a: dup
               // 1b: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
               // 1e: ldc -2147483648
               // 20: isub
               // 21: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
               // 24: goto 32
               // 27: new kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1
               // 2a: dup
               // 2b: aload 0
               // 2c: aload 2
               // 2d: invokespecial kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1;Lkotlin/coroutines/Continuation;)V
               // 30: astore 4
               // 32: aload 4
               // 34: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.result Ljava/lang/Object;
               // 37: astore 3
               // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
               // 3b: astore 5
               // 3d: aload 4
               // 3f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
               // 42: tableswitch 67 0 1 22 57
               // 58: aload 3
               // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 5c: aload 0
               // 5d: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1.$transform Lkotlin/jvm/functions/Function3;
               // 60: aload 0
               // 61: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
               // 64: aload 1
               // 65: aload 4
               // 67: aload 4
               // 69: bipush 1
               // 6a: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
               // 6d: invokeinterface kotlin/jvm/functions/Function3.invoke (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 4
               // 72: dup
               // 73: aload 5
               // 75: if_acmpne 80
               // 78: aload 5
               // 7a: areturn
               // 7b: aload 3
               // 7c: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 7f: aload 3
               // 80: pop
               // 81: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 84: areturn
               // 85: new java/lang/IllegalStateException
               // 88: dup
               // 89: ldc "call to 'resume' before 'invoke' with coroutine"
               // 8b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // 8e: athrow
            },
            `$completion`
         )
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   fun `collect$$forInline`(collector: FlowCollector, `$completion`: Continuation) {
      InlineMarker.mark(4)
      FlowKt__EmittersKt$unsafeTransform$$inlined$unsafeFlow$1$1(this, `$completion`)
      InlineMarker.mark(5)
      val `$this$unsafeTransform_u24lambda_u240`: FlowCollector = collector
      val var10000: Flow = this.$this_unsafeTransform$inlined
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
         // 01: instanceof kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1
         // 04: ifeq 27
         // 07: aload 2
         // 08: checkcast kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1
         // 0b: astore 4
         // 0d: aload 4
         // 0f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
         // 12: ldc -2147483648
         // 14: iand
         // 15: ifeq 27
         // 18: aload 4
         // 1a: dup
         // 1b: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
         // 1e: ldc -2147483648
         // 20: isub
         // 21: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
         // 24: goto 32
         // 27: new kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1
         // 2a: dup
         // 2b: aload 0
         // 2c: aload 2
         // 2d: invokespecial kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1;Lkotlin/coroutines/Continuation;)V
         // 30: astore 4
         // 32: aload 4
         // 34: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.result Ljava/lang/Object;
         // 37: astore 3
         // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
         // 3b: astore 5
         // 3d: aload 4
         // 3f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
         // 42: tableswitch 67 0 1 22 57
         // 58: aload 3
         // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
         // 5c: aload 0
         // 5d: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1.$transform Lkotlin/jvm/functions/Function3;
         // 60: aload 0
         // 61: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
         // 64: aload 1
         // 65: aload 4
         // 67: aload 4
         // 69: bipush 1
         // 6a: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1.label I
         // 6d: invokeinterface kotlin/jvm/functions/Function3.invoke (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 4
         // 72: dup
         // 73: aload 5
         // 75: if_acmpne 80
         // 78: aload 5
         // 7a: areturn
         // 7b: aload 3
         // 7c: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
         // 7f: aload 3
         // 80: pop
         // 81: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // 84: areturn
         // 85: new java/lang/IllegalStateException
         // 88: dup
         // 89: ldc "call to 'resume' before 'invoke' with coroutine"
         // 8b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
         // 8e: athrow
      }
      InlineMarker.mark(0)
      var10000.collect(var10001, `$completion`)
      InlineMarker.mark(1)
      Unit.INSTANCE
   }
}
