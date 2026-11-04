package kotlinx.coroutines.flow

import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 3 CoroutineScope.kt\nkotlinx/coroutines/CoroutineScopeKt\n*L\n1#1,113:1\n77#2:114\n78#2,7:116\n329#3:115\n*S KotlinDebug\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n*L\n77#1:115\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Function2 $action$inlined;
   @JvmSynthetic
   Flow $this_onStart$inlined;

   fun `FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1`(var1: Function2, var2: Flow) {
      this.$action$inlined = var1
      this.$this_onStart$inlined = var2
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
      // 001: instanceof kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1
      // 004: ifeq 024
      // 007: aload 2
      // 008: checkcast kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1
      // 00b: astore 3
      // 00c: aload 3
      // 00d: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.label I
      // 010: ldc -2147483648
      // 012: iand
      // 013: ifeq 024
      // 016: aload 3
      // 017: dup
      // 018: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.label I
      // 01b: ldc -2147483648
      // 01d: isub
      // 01e: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.label I
      // 021: goto 02e
      // 024: new kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1
      // 027: dup
      // 028: aload 0
      // 029: aload 2
      // 02a: invokespecial kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.<init> (Lkotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1;Lkotlin/coroutines/Continuation;)V
      // 02d: astore 3
      // 02e: aload 3
      // 02f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.result Ljava/lang/Object;
      // 032: astore 4
      // 034: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 037: astore 5
      // 039: aload 3
      // 03a: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.label I
      // 03d: tableswitch 234 0 2 27 122 219
      // 058: aload 4
      // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 05d: aload 1
      // 05e: aload 3
      // 05f: checkcast kotlin/coroutines/Continuation
      // 062: astore 6
      // 064: astore 7
      // 066: bipush 0
      // 067: istore 8
      // 069: new kotlinx/coroutines/flow/internal/SafeCollector
      // 06c: dup
      // 06d: aload 7
      // 06f: aload 3
      // 070: astore 9
      // 072: bipush 0
      // 073: istore 10
      // 075: aload 3
      // 076: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
      // 07b: nop
      // 07c: invokespecial kotlinx/coroutines/flow/internal/SafeCollector.<init> (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/CoroutineContext;)V
      // 07f: astore 11
      // 081: nop
      // 082: aload 0
      // 083: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1.$action$inlined Lkotlin/jvm/functions/Function2;
      // 086: aload 11
      // 088: aload 3
      // 089: aload 3
      // 08a: aload 0
      // 08b: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 08e: aload 3
      // 08f: aload 7
      // 091: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 094: aload 3
      // 095: aload 11
      // 097: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.L$2 Ljava/lang/Object;
      // 09a: aload 3
      // 09b: bipush 1
      // 09c: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.label I
      // 09f: bipush 6
      // 0a1: invokestatic kotlin/jvm/internal/InlineMarker.mark (I)V
      // 0a4: invokeinterface kotlin/jvm/functions/Function2.invoke (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 0a9: bipush 7
      // 0ab: invokestatic kotlin/jvm/internal/InlineMarker.mark (I)V
      // 0ae: dup
      // 0af: aload 5
      // 0b1: if_acmpne 0dc
      // 0b4: aload 5
      // 0b6: areturn
      // 0b7: bipush 0
      // 0b8: istore 8
      // 0ba: aload 3
      // 0bb: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.L$2 Ljava/lang/Object;
      // 0be: checkcast kotlinx/coroutines/flow/internal/SafeCollector
      // 0c1: astore 11
      // 0c3: aload 3
      // 0c4: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 0c7: checkcast kotlinx/coroutines/flow/FlowCollector
      // 0ca: astore 7
      // 0cc: aload 3
      // 0cd: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 0d0: checkcast kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1
      // 0d3: astore 0
      // 0d4: nop
      // 0d5: aload 4
      // 0d7: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 0da: aload 4
      // 0dc: pop
      // 0dd: aload 11
      // 0df: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // 0e2: goto 0ef
      // 0e5: astore 9
      // 0e7: aload 11
      // 0e9: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // 0ec: aload 9
      // 0ee: athrow
      // 0ef: aload 0
      // 0f0: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1.$this_onStart$inlined Lkotlinx/coroutines/flow/Flow;
      // 0f3: aload 7
      // 0f5: aload 3
      // 0f6: aload 3
      // 0f7: aconst_null
      // 0f8: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.L$0 Ljava/lang/Object;
      // 0fb: aload 3
      // 0fc: aconst_null
      // 0fd: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.L$1 Ljava/lang/Object;
      // 100: aload 3
      // 101: aconst_null
      // 102: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.L$2 Ljava/lang/Object;
      // 105: aload 3
      // 106: bipush 2
      // 107: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1$1.label I
      // 10a: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
      // 10f: dup
      // 110: aload 5
      // 112: if_acmpne 122
      // 115: aload 5
      // 117: areturn
      // 118: bipush 0
      // 119: istore 8
      // 11b: aload 4
      // 11d: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 120: aload 4
      // 122: pop
      // 123: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 126: areturn
      // 127: new java/lang/IllegalStateException
      // 12a: dup
      // 12b: ldc "call to 'resume' before 'invoke' with coroutine"
      // 12d: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 130: athrow
   }
}
