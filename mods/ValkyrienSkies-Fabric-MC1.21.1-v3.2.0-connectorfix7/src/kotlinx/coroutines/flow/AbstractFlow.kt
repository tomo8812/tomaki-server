package kotlinx.coroutines.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
public abstract class AbstractFlow<T> : Flow<T>, CancellableFlow<T> {
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
      // 00: aload 2
      // 01: instanceof kotlinx/coroutines/flow/AbstractFlow$collect$1
      // 04: ifeq 27
      // 07: aload 2
      // 08: checkcast kotlinx/coroutines/flow/AbstractFlow$collect$1
      // 0b: astore 6
      // 0d: aload 6
      // 0f: getfield kotlinx/coroutines/flow/AbstractFlow$collect$1.label I
      // 12: ldc -2147483648
      // 14: iand
      // 15: ifeq 27
      // 18: aload 6
      // 1a: dup
      // 1b: getfield kotlinx/coroutines/flow/AbstractFlow$collect$1.label I
      // 1e: ldc -2147483648
      // 20: isub
      // 21: putfield kotlinx/coroutines/flow/AbstractFlow$collect$1.label I
      // 24: goto 32
      // 27: new kotlinx/coroutines/flow/AbstractFlow$collect$1
      // 2a: dup
      // 2b: aload 0
      // 2c: aload 2
      // 2d: invokespecial kotlinx/coroutines/flow/AbstractFlow$collect$1.<init> (Lkotlinx/coroutines/flow/AbstractFlow;Lkotlin/coroutines/Continuation;)V
      // 30: astore 6
      // 32: aload 6
      // 34: getfield kotlinx/coroutines/flow/AbstractFlow$collect$1.result Ljava/lang/Object;
      // 37: astore 5
      // 39: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 3c: astore 7
      // 3e: aload 6
      // 40: getfield kotlinx/coroutines/flow/AbstractFlow$collect$1.label I
      // 43: tableswitch 112 0 1 21 74
      // 58: aload 5
      // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 5d: new kotlinx/coroutines/flow/internal/SafeCollector
      // 60: dup
      // 61: aload 1
      // 62: aload 6
      // 64: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
      // 69: invokespecial kotlinx/coroutines/flow/internal/SafeCollector.<init> (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/CoroutineContext;)V
      // 6c: astore 3
      // 6d: nop
      // 6e: aload 0
      // 6f: aload 3
      // 70: checkcast kotlinx/coroutines/flow/FlowCollector
      // 73: aload 6
      // 75: aload 6
      // 77: aload 3
      // 78: putfield kotlinx/coroutines/flow/AbstractFlow$collect$1.L$0 Ljava/lang/Object;
      // 7b: aload 6
      // 7d: bipush 1
      // 7e: putfield kotlinx/coroutines/flow/AbstractFlow$collect$1.label I
      // 81: invokevirtual kotlinx/coroutines/flow/AbstractFlow.collectSafely (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 84: dup
      // 85: aload 7
      // 87: if_acmpne 9e
      // 8a: aload 7
      // 8c: areturn
      // 8d: aload 6
      // 8f: getfield kotlinx/coroutines/flow/AbstractFlow$collect$1.L$0 Ljava/lang/Object;
      // 92: checkcast kotlinx/coroutines/flow/internal/SafeCollector
      // 95: astore 3
      // 96: nop
      // 97: aload 5
      // 99: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 9c: aload 5
      // 9e: pop
      // 9f: aload 3
      // a0: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // a3: goto af
      // a6: astore 4
      // a8: aload 3
      // a9: invokevirtual kotlinx/coroutines/flow/internal/SafeCollector.releaseIntercepted ()V
      // ac: aload 4
      // ae: athrow
      // af: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // b2: areturn
      // b3: new java/lang/IllegalStateException
      // b6: dup
      // b7: ldc "call to 'resume' before 'invoke' with coroutine"
      // b9: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // bc: athrow
   }

   public abstract suspend fun collectSafely(collector: FlowCollector<Any>) {
   }
}
