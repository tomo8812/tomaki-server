package kotlinx.coroutines.flow

import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 3 CoroutineScope.kt\nkotlinx/coroutines/CoroutineScopeKt\n*L\n1#1,113:1\n182#2,7:114\n189#2,7:122\n329#3:121\n*S KotlinDebug\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n*L\n188#1:121\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_onEmpty$inlined;
   @JvmSynthetic
   Function2 $action$inlined;

   fun `FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1`(var1: Flow, var2: Function2) {
      this.$this_onEmpty$inlined = var1
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
      // 001: instanceof kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1
      // 004: ifeq 024
      // 007: aload 2
      // 008: checkcast kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1
      // 00b: astore 3
      // 00c: aload 3
      // 00d: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.label I
      // 010: ldc -2147483648
      // 012: iand
      // 013: ifeq 024
      // 016: aload 3
      // 017: dup
      // 018: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.label I
      // 01b: ldc -2147483648
      // 01d: isub
      // 01e: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.label I
      // 021: goto 02e
      // 024: new kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1
      // 027: dup
      // 028: aload 0
      // 029: aload 2
      // 02a: invokespecial kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.<init> (Lkotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1;Lkotlin/coroutines/Continuation;)V
      // 02d: astore 3
      // 02e: aload 3
      // 02f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.result Ljava/lang/Object;
      // 032: astore 4
      // 034: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 037: astore 5
      // 039: aload 3
      // 03a: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.label I
      // 03d: tableswitch 280 0 2 27 114 236
      // 058: aload 4
      // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 05d: aload 1
      // 05e: aload 3
      // 05f: checkcast kotlin/coroutines/Continuation
      // 062: astore 6
      // 064: astore 7
      // 066: bipush 0
      // 067: istore 8
      // 069: new kotlin/jvm/internal/Ref$BooleanRef
      // 06c: dup
      // 06d: invokespecial kotlin/jvm/internal/Ref$BooleanRef.<init> ()V
      // 070: astore 9
      // 072: aload 9
      // 074: bipush 1
      // 075: putfield kotlin/jvm/internal/Ref$BooleanRef.element Z
      // 078: aload 0
      // 079: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1.$this_onEmpty$inlined Lkotlinx/coroutines/flow/Flow;
      // 07c: new kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$1$1
      // 07f: dup
      // 080: aload 9
      // 082: aload 7
      // 084: invokespecial kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$1$1.<init> (Lkotlin/jvm/internal/Ref$BooleanRef;Lkotlinx/coroutines/flow/FlowCollector;)V
      // 087: checkcast kotlinx/coroutines/flow/FlowCollector
      // 08a: aload 3
      // 08b: aload 3
      // 08c: aload 0
      // 08d: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 090: aload 3
      // 091: aload 7
      // 093: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 096: aload 3
      // 097: aload 9
      // 099: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$2 Ljava/lang/Object;
      // 09c: aload 3
      // 09d: bipush 1
      // 09e: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.label I
      // 0a1: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
      // 0a6: dup
      // 0a7: aload 5
      // 0a9: if_acmpne 0d3
      // 0ac: aload 5
      // 0ae: areturn
      // 0af: bipush 0
      // 0b0: istore 8
      // 0b2: aload 3
      // 0b3: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$2 Ljava/lang/Object;
      // 0b6: checkcast kotlin/jvm/internal/Ref$BooleanRef
      // 0b9: astore 9
      // 0bb: aload 3
      // 0bc: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 0bf: checkcast kotlinx/coroutines/flow/FlowCollector
      // 0c2: astore 7
      // 0c4: aload 3
      // 0c5: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 0c8: checkcast kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1
      // 0cb: astore 0
      // 0cc: aload 4
      // 0ce: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 0d1: aload 4
      // 0d3: pop
      // 0d4: aload 9
      // 0d6: getfield kotlin/jvm/internal/Ref$BooleanRef.element Z
      // 0d9: ifeq 150
      // 0dc: new kotlinx/coroutines/flow/internal/SafeCollector
      // 0df: dup
      // 0e0: aload 7
      // 0e2: aload 3
      // 0e3: astore 10
      // 0e5: bipush 0
      // 0e6: istore 11
      // 0e8: aload 3
      // 0e9: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
      // 0ee: nop
      // 0ef: invokespecial kotlinx/coroutines/flow/internal/SafeCollector.<init> (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/CoroutineContext;)V
      // 0f2: astore 12
      // 0f4: nop
      // 0f5: aload 0
      // 0f6: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1.$action$inlined Lkotlin/jvm/functions/Function2;
      // 0f9: aload 12
      // 0fb: aload 3
      // 0fc: aload 3
      // 0fd: aload 12
      // 0ff: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 102: aload 3
      // 103: aconst_null
      // 104: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 107: aload 3
      // 108: aconst_null
      // 109: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$2 Ljava/lang/Object;
      // 10c: aload 3
      // 10d: bipush 2
      // 10e: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.label I
      // 111: bipush 6
      // 113: invokestatic kotlin/jvm/internal/InlineMarker.mark (I)V
      // 116: invokeinterface kotlin/jvm/functions/Function2.invoke (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 11b: bipush 7
      // 11d: invokestatic kotlin/jvm/internal/InlineMarker.mark (I)V
      // 120: dup
      // 121: aload 5
      // 123: if_acmpne 13d
      // 126: aload 5
      // 128: areturn
      // 129: bipush 0
      // 12a: istore 8
      // 12c: aload 3
      // 12d: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 130: checkcast kotlinx/coroutines/flow/internal/SafeCollector
      // 133: astore 12
      // 135: nop
      // 136: aload 4
      // 138: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 13b: aload 4
      // 13d: pop
      // 13e: aload 12
      // 140: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // 143: goto 150
      // 146: astore 10
      // 148: aload 12
      // 14a: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // 14d: aload 10
      // 14f: athrow
      // 150: nop
      // 151: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 154: areturn
      // 155: new java/lang/IllegalStateException
      // 158: dup
      // 159: ldc "call to 'resume' before 'invoke' with coroutine"
      // 15b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 15e: athrow
   }
}
