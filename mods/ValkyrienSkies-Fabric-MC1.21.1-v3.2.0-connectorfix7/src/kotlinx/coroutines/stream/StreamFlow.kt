package kotlinx.coroutines.stream

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.stream.Stream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

private class StreamFlow<T>(stream: Stream<Any>) : Flow<T> {
   private final val stream: Stream<Any>
   @Volatile
   @Volatile
   private int consumed;
   @JvmStatic
   private AtomicIntegerFieldUpdater consumed$FU = AtomicIntegerFieldUpdater.newUpdater(StreamFlow.class, "consumed");

   init {
      this.stream = stream
      this.consumed = 0
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:365)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:552)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1058)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.insertSemaphore(FinallyProcessor.java:351)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:98)
      //
      // Bytecode:
      // 000: aload 2
      // 001: instanceof kotlinx/coroutines/stream/StreamFlow$collect$1
      // 004: ifeq 027
      // 007: aload 2
      // 008: checkcast kotlinx/coroutines/stream/StreamFlow$collect$1
      // 00b: astore 6
      // 00d: aload 6
      // 00f: getfield kotlinx/coroutines/stream/StreamFlow$collect$1.label I
      // 012: ldc -2147483648
      // 014: iand
      // 015: ifeq 027
      // 018: aload 6
      // 01a: dup
      // 01b: getfield kotlinx/coroutines/stream/StreamFlow$collect$1.label I
      // 01e: ldc -2147483648
      // 020: isub
      // 021: putfield kotlinx/coroutines/stream/StreamFlow$collect$1.label I
      // 024: goto 032
      // 027: new kotlinx/coroutines/stream/StreamFlow$collect$1
      // 02a: dup
      // 02b: aload 0
      // 02c: aload 2
      // 02d: invokespecial kotlinx/coroutines/stream/StreamFlow$collect$1.<init> (Lkotlinx/coroutines/stream/StreamFlow;Lkotlin/coroutines/Continuation;)V
      // 030: astore 6
      // 032: aload 6
      // 034: getfield kotlinx/coroutines/stream/StreamFlow$collect$1.result Ljava/lang/Object;
      // 037: astore 5
      // 039: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 03c: astore 7
      // 03e: aload 6
      // 040: getfield kotlinx/coroutines/stream/StreamFlow$collect$1.label I
      // 043: tableswitch 189 0 1 21 122
      // 058: aload 5
      // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 05d: getstatic kotlinx/coroutines/stream/StreamFlow.consumed$FU Ljava/util/concurrent/atomic/AtomicIntegerFieldUpdater;
      // 060: aload 0
      // 061: bipush 0
      // 062: bipush 1
      // 063: invokevirtual java/util/concurrent/atomic/AtomicIntegerFieldUpdater.compareAndSet (Ljava/lang/Object;II)Z
      // 066: ifne 076
      // 069: new java/lang/IllegalStateException
      // 06c: dup
      // 06d: ldc "Stream.consumeAsFlow can be collected only once"
      // 06f: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 072: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 075: athrow
      // 076: nop
      // 077: aload 0
      // 078: getfield kotlinx/coroutines/stream/StreamFlow.stream Ljava/util/stream/Stream;
      // 07b: invokeinterface java/util/stream/Stream.iterator ()Ljava/util/Iterator; 1
      // 080: astore 3
      // 081: aload 3
      // 082: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 087: ifeq 0e4
      // 08a: aload 3
      // 08b: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 090: astore 4
      // 092: aload 1
      // 093: aload 4
      // 095: aload 6
      // 097: aload 6
      // 099: aload 0
      // 09a: putfield kotlinx/coroutines/stream/StreamFlow$collect$1.L$0 Ljava/lang/Object;
      // 09d: aload 6
      // 09f: aload 1
      // 0a0: putfield kotlinx/coroutines/stream/StreamFlow$collect$1.L$1 Ljava/lang/Object;
      // 0a3: aload 6
      // 0a5: aload 3
      // 0a6: putfield kotlinx/coroutines/stream/StreamFlow$collect$1.L$2 Ljava/lang/Object;
      // 0a9: aload 6
      // 0ab: bipush 1
      // 0ac: putfield kotlinx/coroutines/stream/StreamFlow$collect$1.label I
      // 0af: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
      // 0b4: dup
      // 0b5: aload 7
      // 0b7: if_acmpne 0e0
      // 0ba: aload 7
      // 0bc: areturn
      // 0bd: aload 6
      // 0bf: getfield kotlinx/coroutines/stream/StreamFlow$collect$1.L$2 Ljava/lang/Object;
      // 0c2: checkcast java/util/Iterator
      // 0c5: astore 3
      // 0c6: aload 6
      // 0c8: getfield kotlinx/coroutines/stream/StreamFlow$collect$1.L$1 Ljava/lang/Object;
      // 0cb: checkcast kotlinx/coroutines/flow/FlowCollector
      // 0ce: astore 1
      // 0cf: aload 6
      // 0d1: getfield kotlinx/coroutines/stream/StreamFlow$collect$1.L$0 Ljava/lang/Object;
      // 0d4: checkcast kotlinx/coroutines/stream/StreamFlow
      // 0d7: astore 0
      // 0d8: nop
      // 0d9: aload 5
      // 0db: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 0de: aload 5
      // 0e0: pop
      // 0e1: goto 081
      // 0e4: aload 0
      // 0e5: getfield kotlinx/coroutines/stream/StreamFlow.stream Ljava/util/stream/Stream;
      // 0e8: invokeinterface java/util/stream/Stream.close ()V 1
      // 0ed: goto 0fc
      // 0f0: astore 3
      // 0f1: aload 0
      // 0f2: getfield kotlinx/coroutines/stream/StreamFlow.stream Ljava/util/stream/Stream;
      // 0f5: invokeinterface java/util/stream/Stream.close ()V 1
      // 0fa: aload 3
      // 0fb: athrow
      // 0fc: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 0ff: areturn
      // 100: new java/lang/IllegalStateException
      // 103: dup
      // 104: ldc "call to 'resume' before 'invoke' with coroutine"
      // 106: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 109: athrow
   }
}
