package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Merge.kt\nkotlinx/coroutines/flow/FlowKt__MergeKt\n*L\n1#1,113:1\n83#2,2:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__MergeKt$flattenConcat$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_flattenConcat$inlined;

   fun `FlowKt__MergeKt$flattenConcat$$inlined$unsafeFlow$1`(var1: Flow) {
      this.$this_flattenConcat$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val `$this$flattenConcat_u24lambda_u240`: FlowCollector = collector
      val var10000: Any = this.$this_flattenConcat$inlined
         .collect(
            { value: Flow<Any> ->
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
               // 01: instanceof kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1
               // 04: ifeq 27
               // 07: aload 2
               // 08: checkcast kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1
               // 0b: astore 4
               // 0d: aload 4
               // 0f: getfield kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1.label I
               // 12: ldc -2147483648
               // 14: iand
               // 15: ifeq 27
               // 18: aload 4
               // 1a: dup
               // 1b: getfield kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1.label I
               // 1e: ldc -2147483648
               // 20: isub
               // 21: putfield kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1.label I
               // 24: goto 32
               // 27: new kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1
               // 2a: dup
               // 2b: aload 0
               // 2c: aload 2
               // 2d: invokespecial kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1;Lkotlin/coroutines/Continuation;)V
               // 30: astore 4
               // 32: aload 4
               // 34: getfield kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1.result Ljava/lang/Object;
               // 37: astore 3
               // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
               // 3b: astore 5
               // 3d: aload 4
               // 3f: getfield kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1.label I
               // 42: tableswitch 61 0 1 22 51
               // 58: aload 3
               // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 5c: aload 0
               // 5d: getfield kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
               // 60: aload 1
               // 61: aload 4
               // 63: aload 4
               // 65: bipush 1
               // 66: putfield kotlinx/coroutines/flow/FlowKt__MergeKt$flattenConcat$1$1$emit$1.label I
               // 69: invokestatic kotlinx/coroutines/flow/FlowKt.emitAll (Lkotlinx/coroutines/flow/FlowCollector;Lkotlinx/coroutines/flow/Flow;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
               // 6c: dup
               // 6d: aload 5
               // 6f: if_acmpne 7a
               // 72: aload 5
               // 74: areturn
               // 75: aload 3
               // 76: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 79: aload 3
               // 7a: pop
               // 7b: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 7e: areturn
               // 7f: new java/lang/IllegalStateException
               // 82: dup
               // 83: ldc "call to 'resume' before 'invoke' with coroutine"
               // 85: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // 88: athrow
            },
            `$completion`
         )
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
