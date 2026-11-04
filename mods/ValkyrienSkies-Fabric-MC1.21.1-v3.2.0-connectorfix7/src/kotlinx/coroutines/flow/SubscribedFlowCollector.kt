package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nShare.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Share.kt\nkotlinx/coroutines/flow/SubscribedFlowCollector\n+ 2 CoroutineScope.kt\nkotlinx/coroutines/CoroutineScopeKt\n*L\n1#1,426:1\n329#2:427\n*S KotlinDebug\n*F\n+ 1 Share.kt\nkotlinx/coroutines/flow/SubscribedFlowCollector\n*L\n417#1:427\n*E\n"])
internal class SubscribedFlowCollector<T>(collector: FlowCollector<Any>, action: (FlowCollector<Any>, Continuation<Unit>) -> Any?) : FlowCollector<T> {
   private final val collector: FlowCollector<Any>
   private final val action: (FlowCollector<Any>, Continuation<Unit>) -> Any?

   init {
      this.collector = collector
      this.action = action
   }

   public override suspend fun emit(value: Any) {
      return this.collector.emit((T)value, `$completion`)
   }

   public suspend fun onSubscription() {
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
      // 000: aload 1
      // 001: instanceof kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1
      // 004: ifeq 027
      // 007: aload 1
      // 008: checkcast kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1
      // 00b: astore 5
      // 00d: aload 5
      // 00f: getfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.label I
      // 012: ldc -2147483648
      // 014: iand
      // 015: ifeq 027
      // 018: aload 5
      // 01a: dup
      // 01b: getfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.label I
      // 01e: ldc -2147483648
      // 020: isub
      // 021: putfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.label I
      // 024: goto 032
      // 027: new kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1
      // 02a: dup
      // 02b: aload 0
      // 02c: aload 1
      // 02d: invokespecial kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.<init> (Lkotlinx/coroutines/flow/SubscribedFlowCollector;Lkotlin/coroutines/Continuation;)V
      // 030: astore 5
      // 032: aload 5
      // 034: getfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.result Ljava/lang/Object;
      // 037: astore 4
      // 039: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 03c: astore 6
      // 03e: aload 5
      // 040: getfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.label I
      // 043: tableswitch 198 0 2 25 92 182
      // 05c: aload 4
      // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 061: new kotlinx/coroutines/flow/internal/SafeCollector
      // 064: dup
      // 065: aload 0
      // 066: getfield kotlinx/coroutines/flow/SubscribedFlowCollector.collector Lkotlinx/coroutines/flow/FlowCollector;
      // 069: bipush 0
      // 06a: istore 3
      // 06b: aload 5
      // 06d: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
      // 072: nop
      // 073: invokespecial kotlinx/coroutines/flow/internal/SafeCollector.<init> (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/CoroutineContext;)V
      // 076: astore 2
      // 077: nop
      // 078: aload 0
      // 079: getfield kotlinx/coroutines/flow/SubscribedFlowCollector.action Lkotlin/jvm/functions/Function2;
      // 07c: aload 2
      // 07d: aload 5
      // 07f: aload 5
      // 081: aload 0
      // 082: putfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.L$0 Ljava/lang/Object;
      // 085: aload 5
      // 087: aload 2
      // 088: putfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.L$1 Ljava/lang/Object;
      // 08b: aload 5
      // 08d: bipush 1
      // 08e: putfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.label I
      // 091: invokeinterface kotlin/jvm/functions/Function2.invoke (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 096: dup
      // 097: aload 6
      // 099: if_acmpne 0b9
      // 09c: aload 6
      // 09e: areturn
      // 09f: aload 5
      // 0a1: getfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.L$1 Ljava/lang/Object;
      // 0a4: checkcast kotlinx/coroutines/flow/internal/SafeCollector
      // 0a7: astore 2
      // 0a8: aload 5
      // 0aa: getfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.L$0 Ljava/lang/Object;
      // 0ad: checkcast kotlinx/coroutines/flow/SubscribedFlowCollector
      // 0b0: astore 0
      // 0b1: nop
      // 0b2: aload 4
      // 0b4: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 0b7: aload 4
      // 0b9: pop
      // 0ba: aload 2
      // 0bb: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // 0be: goto 0c8
      // 0c1: astore 3
      // 0c2: aload 2
      // 0c3: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // 0c6: aload 3
      // 0c7: athrow
      // 0c8: aload 0
      // 0c9: getfield kotlinx/coroutines/flow/SubscribedFlowCollector.collector Lkotlinx/coroutines/flow/FlowCollector;
      // 0cc: instanceof kotlinx/coroutines/flow/SubscribedFlowCollector
      // 0cf: ifeq 105
      // 0d2: aload 0
      // 0d3: getfield kotlinx/coroutines/flow/SubscribedFlowCollector.collector Lkotlinx/coroutines/flow/FlowCollector;
      // 0d6: checkcast kotlinx/coroutines/flow/SubscribedFlowCollector
      // 0d9: aload 5
      // 0db: aload 5
      // 0dd: aconst_null
      // 0de: putfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.L$0 Ljava/lang/Object;
      // 0e1: aload 5
      // 0e3: aconst_null
      // 0e4: putfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.L$1 Ljava/lang/Object;
      // 0e7: aload 5
      // 0e9: bipush 2
      // 0ea: putfield kotlinx/coroutines/flow/SubscribedFlowCollector$onSubscription$1.label I
      // 0ed: invokevirtual kotlinx/coroutines/flow/SubscribedFlowCollector.onSubscription (Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 0f0: dup
      // 0f1: aload 6
      // 0f3: if_acmpne 100
      // 0f6: aload 6
      // 0f8: areturn
      // 0f9: aload 4
      // 0fb: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 0fe: aload 4
      // 100: pop
      // 101: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 104: areturn
      // 105: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 108: areturn
      // 109: new java/lang/IllegalStateException
      // 10c: dup
      // 10d: ldc "call to 'resume' before 'invoke' with coroutine"
      // 10f: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 112: athrow
   }
}
