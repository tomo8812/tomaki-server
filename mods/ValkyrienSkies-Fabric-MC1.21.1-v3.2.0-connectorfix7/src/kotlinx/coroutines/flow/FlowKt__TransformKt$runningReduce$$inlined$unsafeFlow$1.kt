package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.flow.internal.NullSurrogateKt

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n*L\n1#1,113:1\n126#2,10:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__TransformKt$runningReduce$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_runningReduce$inlined;
   @JvmSynthetic
   Function3 $operation$inlined;

   fun `FlowKt__TransformKt$runningReduce$$inlined$unsafeFlow$1`(var1: Flow, var2: Function3) {
      this.$this_runningReduce$inlined = var1
      this.$operation$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val `$this$runningReduce_u24lambda_u2410`: FlowCollector = collector
      val accumulator: ObjectRef = ObjectRef()
      accumulator.element = NullSurrogateKt.NULL
      val var10000: Any = this.$this_runningReduce$inlined
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
               // 000: aload 2
               // 001: instanceof kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1
               // 004: ifeq 027
               // 007: aload 2
               // 008: checkcast kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1
               // 00b: astore 5
               // 00d: aload 5
               // 00f: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.label I
               // 012: ldc -2147483648
               // 014: iand
               // 015: ifeq 027
               // 018: aload 5
               // 01a: dup
               // 01b: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.label I
               // 01e: ldc -2147483648
               // 020: isub
               // 021: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.label I
               // 024: goto 032
               // 027: new kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1
               // 02a: dup
               // 02b: aload 0
               // 02c: aload 2
               // 02d: invokespecial kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1;Lkotlin/coroutines/Continuation;)V
               // 030: astore 5
               // 032: aload 5
               // 034: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.result Ljava/lang/Object;
               // 037: astore 4
               // 039: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
               // 03c: astore 6
               // 03e: aload 5
               // 040: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.label I
               // 043: tableswitch 185 0 2 25 98 173
               // 05c: aload 4
               // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 061: aload 0
               // 062: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1.$accumulator Lkotlin/jvm/internal/Ref$ObjectRef;
               // 065: aload 0
               // 066: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1.$accumulator Lkotlin/jvm/internal/Ref$ObjectRef;
               // 069: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 06c: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
               // 06f: if_acmpne 076
               // 072: aload 1
               // 073: goto 0c0
               // 076: astore 3
               // 077: aload 0
               // 078: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1.$operation Lkotlin/jvm/functions/Function3;
               // 07b: aload 0
               // 07c: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1.$accumulator Lkotlin/jvm/internal/Ref$ObjectRef;
               // 07f: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 082: aload 1
               // 083: aload 5
               // 085: aload 5
               // 087: aload 0
               // 088: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.L$0 Ljava/lang/Object;
               // 08b: aload 5
               // 08d: aload 3
               // 08e: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.L$1 Ljava/lang/Object;
               // 091: aload 5
               // 093: bipush 1
               // 094: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.label I
               // 097: invokeinterface kotlin/jvm/functions/Function3.invoke (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 4
               // 09c: dup
               // 09d: aload 6
               // 09f: if_acmpne 0be
               // 0a2: aload 6
               // 0a4: areturn
               // 0a5: aload 5
               // 0a7: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.L$1 Ljava/lang/Object;
               // 0aa: checkcast kotlin/jvm/internal/Ref$ObjectRef
               // 0ad: astore 3
               // 0ae: aload 5
               // 0b0: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.L$0 Ljava/lang/Object;
               // 0b3: checkcast kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1
               // 0b6: astore 0
               // 0b7: aload 4
               // 0b9: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 0bc: aload 4
               // 0be: aload 3
               // 0bf: swap
               // 0c0: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 0c3: aload 0
               // 0c4: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
               // 0c7: aload 0
               // 0c8: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1.$accumulator Lkotlin/jvm/internal/Ref$ObjectRef;
               // 0cb: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 0ce: aload 5
               // 0d0: aload 5
               // 0d2: aconst_null
               // 0d3: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.L$0 Ljava/lang/Object;
               // 0d6: aload 5
               // 0d8: aconst_null
               // 0d9: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.L$1 Ljava/lang/Object;
               // 0dc: aload 5
               // 0de: bipush 2
               // 0df: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningReduce$1$1$emit$1.label I
               // 0e2: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
               // 0e7: dup
               // 0e8: aload 6
               // 0ea: if_acmpne 0f7
               // 0ed: aload 6
               // 0ef: areturn
               // 0f0: aload 4
               // 0f2: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 0f5: aload 4
               // 0f7: pop
               // 0f8: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 0fb: areturn
               // 0fc: new java/lang/IllegalStateException
               // 0ff: dup
               // 100: ldc "call to 'resume' before 'invoke' with coroutine"
               // 102: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // 105: athrow
            },
            `$completion`
         )
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
