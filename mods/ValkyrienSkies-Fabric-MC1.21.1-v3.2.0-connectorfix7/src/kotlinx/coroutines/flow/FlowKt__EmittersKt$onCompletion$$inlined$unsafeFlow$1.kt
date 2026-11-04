package kotlinx.coroutines.flow

import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 3 CoroutineScope.kt\nkotlinx/coroutines/CoroutineScopeKt\n*L\n1#1,113:1\n147#2,13:114\n160#2,6:128\n329#3:127\n*S KotlinDebug\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n*L\n159#1:127\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_onCompletion$inlined;
   @JvmSynthetic
   Function3 $action$inlined;

   fun `FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1`(var1: Flow, var2: Function3) {
      this.$this_onCompletion$inlined = var1
      this.$action$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:365)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:552)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1065)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:566)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //
      // Bytecode:
      // 000: aload 2
      // 001: instanceof kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1
      // 004: ifeq 024
      // 007: aload 2
      // 008: checkcast kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1
      // 00b: astore 3
      // 00c: aload 3
      // 00d: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.label I
      // 010: ldc -2147483648
      // 012: iand
      // 013: ifeq 024
      // 016: aload 3
      // 017: dup
      // 018: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.label I
      // 01b: ldc -2147483648
      // 01d: isub
      // 01e: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.label I
      // 021: goto 02e
      // 024: new kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1
      // 027: dup
      // 028: aload 0
      // 029: aload 2
      // 02a: invokespecial kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.<init> (Lkotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1;Lkotlin/coroutines/Continuation;)V
      // 02d: astore 3
      // 02e: aload 3
      // 02f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.result Ljava/lang/Object;
      // 032: astore 4
      // 034: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 037: astore 5
      // 039: aload 3
      // 03a: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.label I
      // 03d: tableswitch 307 0 3 31 86 167 263
      // 05c: aload 4
      // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 061: aload 1
      // 062: aload 3
      // 063: checkcast kotlin/coroutines/Continuation
      // 066: astore 6
      // 068: astore 7
      // 06a: bipush 0
      // 06b: istore 8
      // 06d: nop
      // 06e: aload 0
      // 06f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1.$this_onCompletion$inlined Lkotlinx/coroutines/flow/Flow;
      // 072: aload 7
      // 074: aload 3
      // 075: aload 3
      // 076: aload 0
      // 077: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 07a: aload 3
      // 07b: aload 7
      // 07d: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 080: aload 3
      // 081: bipush 1
      // 082: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.label I
      // 085: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
      // 08a: dup
      // 08b: aload 5
      // 08d: if_acmpne 0af
      // 090: aload 5
      // 092: areturn
      // 093: bipush 0
      // 094: istore 8
      // 096: aload 3
      // 097: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 09a: checkcast kotlinx/coroutines/flow/FlowCollector
      // 09d: astore 7
      // 09f: aload 3
      // 0a0: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 0a3: checkcast kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1
      // 0a6: astore 0
      // 0a7: nop
      // 0a8: aload 4
      // 0aa: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 0ad: aload 4
      // 0af: pop
      // 0b0: goto 0fb
      // 0b3: astore 9
      // 0b5: new kotlinx/coroutines/flow/ThrowingCollector
      // 0b8: dup
      // 0b9: aload 9
      // 0bb: invokespecial kotlinx/coroutines/flow/ThrowingCollector.<init> (Ljava/lang/Throwable;)V
      // 0be: checkcast kotlinx/coroutines/flow/FlowCollector
      // 0c1: aload 0
      // 0c2: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1.$action$inlined Lkotlin/jvm/functions/Function3;
      // 0c5: aload 9
      // 0c7: aload 3
      // 0c8: aload 3
      // 0c9: aload 9
      // 0cb: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 0ce: aload 3
      // 0cf: aconst_null
      // 0d0: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 0d3: aload 3
      // 0d4: bipush 2
      // 0d5: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.label I
      // 0d8: invokestatic kotlinx/coroutines/flow/FlowKt__EmittersKt.access$invokeSafely$FlowKt__EmittersKt (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/jvm/functions/Function3;Ljava/lang/Throwable;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 0db: dup
      // 0dc: aload 5
      // 0de: if_acmpne 0f7
      // 0e1: aload 5
      // 0e3: areturn
      // 0e4: bipush 0
      // 0e5: istore 8
      // 0e7: aload 3
      // 0e8: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 0eb: checkcast java/lang/Throwable
      // 0ee: astore 9
      // 0f0: aload 4
      // 0f2: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 0f5: aload 4
      // 0f7: pop
      // 0f8: aload 9
      // 0fa: athrow
      // 0fb: new kotlinx/coroutines/flow/internal/SafeCollector
      // 0fe: dup
      // 0ff: aload 7
      // 101: aload 3
      // 102: astore 10
      // 104: bipush 0
      // 105: istore 11
      // 107: aload 3
      // 108: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
      // 10d: nop
      // 10e: invokespecial kotlinx/coroutines/flow/internal/SafeCollector.<init> (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/CoroutineContext;)V
      // 111: astore 9
      // 113: nop
      // 114: aload 0
      // 115: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1.$action$inlined Lkotlin/jvm/functions/Function3;
      // 118: aload 9
      // 11a: aconst_null
      // 11b: aload 3
      // 11c: aload 3
      // 11d: aload 9
      // 11f: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 122: aload 3
      // 123: aconst_null
      // 124: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 127: aload 3
      // 128: bipush 3
      // 129: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.label I
      // 12c: bipush 6
      // 12e: invokestatic kotlin/jvm/internal/InlineMarker.mark (I)V
      // 131: invokeinterface kotlin/jvm/functions/Function3.invoke (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 4
      // 136: bipush 7
      // 138: invokestatic kotlin/jvm/internal/InlineMarker.mark (I)V
      // 13b: dup
      // 13c: aload 5
      // 13e: if_acmpne 158
      // 141: aload 5
      // 143: areturn
      // 144: bipush 0
      // 145: istore 8
      // 147: aload 3
      // 148: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 14b: checkcast kotlinx/coroutines/flow/internal/SafeCollector
      // 14e: astore 9
      // 150: nop
      // 151: aload 4
      // 153: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 156: aload 4
      // 158: pop
      // 159: aload 9
      // 15b: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // 15e: goto 16b
      // 161: astore 10
      // 163: aload 9
      // 165: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // 168: aload 10
      // 16a: athrow
      // 16b: nop
      // 16c: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 16f: areturn
      // 170: new java/lang/IllegalStateException
      // 173: dup
      // 174: ldc "call to 'resume' before 'invoke' with coroutine"
      // 176: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 179: athrow
   }
}
