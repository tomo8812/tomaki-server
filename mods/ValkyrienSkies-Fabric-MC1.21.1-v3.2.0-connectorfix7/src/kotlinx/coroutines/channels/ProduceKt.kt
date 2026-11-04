@file:SourceDebugExtension(["SMAP\nProduce.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Produce.kt\nkotlinx/coroutines/channels/ProduceKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n*L\n1#1,151:1\n1#2:152\n314#3,11:153\n*S KotlinDebug\n*F\n+ 1 Produce.kt\nkotlinx/coroutines/channels/ProduceKt\n*L\n48#1:153,11\n*E\n"])

package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineContextKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

public suspend fun ProducerScope<*>.awaitClose(block: () -> Unit = ...) {
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
   // 001: instanceof kotlinx/coroutines/channels/ProduceKt$awaitClose$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ProduceKt$awaitClose$1
   // 00b: astore 10
   // 00d: aload 10
   // 00f: getfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 10
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ProduceKt$awaitClose$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ProduceKt$awaitClose$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 10
   // 031: aload 10
   // 033: getfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.result Ljava/lang/Object;
   // 036: astore 9
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 11
   // 03d: aload 10
   // 03f: getfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.label I
   // 042: tableswitch 241 0 1 22 187
   // 058: aload 9
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: aload 10
   // 05f: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
   // 064: getstatic kotlinx/coroutines/Job.Key Lkotlinx/coroutines/Job$Key;
   // 067: checkcast kotlin/coroutines/CoroutineContext$Key
   // 06a: invokeinterface kotlin/coroutines/CoroutineContext.get (Lkotlin/coroutines/CoroutineContext$Key;)Lkotlin/coroutines/CoroutineContext$Element; 2
   // 06f: aload 0
   // 070: if_acmpne 077
   // 073: bipush 1
   // 074: goto 078
   // 077: bipush 0
   // 078: ifne 08f
   // 07b: bipush 0
   // 07c: istore 4
   // 07e: ldc "awaitClose() can only be invoked from the producer context"
   // 080: astore 4
   // 082: new java/lang/IllegalStateException
   // 085: dup
   // 086: aload 4
   // 088: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
   // 08b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 08e: athrow
   // 08f: nop
   // 090: bipush 0
   // 091: istore 3
   // 092: aload 10
   // 094: aload 0
   // 095: putfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.L$0 Ljava/lang/Object;
   // 098: aload 10
   // 09a: aload 1
   // 09b: putfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.L$1 Ljava/lang/Object;
   // 09e: aload 10
   // 0a0: bipush 1
   // 0a1: putfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.label I
   // 0a4: aload 10
   // 0a6: checkcast kotlin/coroutines/Continuation
   // 0a9: astore 4
   // 0ab: bipush 0
   // 0ac: istore 5
   // 0ae: new kotlinx/coroutines/CancellableContinuationImpl
   // 0b1: dup
   // 0b2: aload 4
   // 0b4: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.intercepted (Lkotlin/coroutines/Continuation;)Lkotlin/coroutines/Continuation;
   // 0b7: bipush 1
   // 0b8: invokespecial kotlinx/coroutines/CancellableContinuationImpl.<init> (Lkotlin/coroutines/Continuation;I)V
   // 0bb: astore 6
   // 0bd: aload 6
   // 0bf: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.initCancellability ()V
   // 0c2: aload 6
   // 0c4: checkcast kotlinx/coroutines/CancellableContinuation
   // 0c7: astore 7
   // 0c9: bipush 0
   // 0ca: istore 8
   // 0cc: aload 0
   // 0cd: new kotlinx/coroutines/channels/ProduceKt$awaitClose$4$1
   // 0d0: dup
   // 0d1: aload 7
   // 0d3: invokespecial kotlinx/coroutines/channels/ProduceKt$awaitClose$4$1.<init> (Lkotlinx/coroutines/CancellableContinuation;)V
   // 0d6: checkcast kotlin/jvm/functions/Function1
   // 0d9: invokeinterface kotlinx/coroutines/channels/ProducerScope.invokeOnClose (Lkotlin/jvm/functions/Function1;)V 2
   // 0de: nop
   // 0df: nop
   // 0e0: aload 6
   // 0e2: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.getResult ()Ljava/lang/Object;
   // 0e5: dup
   // 0e6: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 0e9: if_acmpne 0f4
   // 0ec: aload 10
   // 0ee: checkcast kotlin/coroutines/Continuation
   // 0f1: invokestatic kotlin/coroutines/jvm/internal/DebugProbesKt.probeCoroutineSuspended (Lkotlin/coroutines/Continuation;)V
   // 0f4: dup
   // 0f5: aload 11
   // 0f7: if_acmpne 119
   // 0fa: aload 11
   // 0fc: areturn
   // 0fd: bipush 0
   // 0fe: istore 3
   // 0ff: aload 10
   // 101: getfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.L$1 Ljava/lang/Object;
   // 104: checkcast kotlin/jvm/functions/Function0
   // 107: astore 1
   // 108: aload 10
   // 10a: getfield kotlinx/coroutines/channels/ProduceKt$awaitClose$1.L$0 Ljava/lang/Object;
   // 10d: checkcast kotlinx/coroutines/channels/ProducerScope
   // 110: astore 0
   // 111: nop
   // 112: aload 9
   // 114: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 117: aload 9
   // 119: nop
   // 11a: pop
   // 11b: aload 1
   // 11c: invokeinterface kotlin/jvm/functions/Function0.invoke ()Ljava/lang/Object; 1
   // 121: pop
   // 122: goto 12f
   // 125: astore 3
   // 126: aload 1
   // 127: invokeinterface kotlin/jvm/functions/Function0.invoke ()Ljava/lang/Object; 1
   // 12c: pop
   // 12d: aload 3
   // 12e: athrow
   // 12f: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 132: areturn
   // 133: new java/lang/IllegalStateException
   // 136: dup
   // 137: ldc "call to 'resume' before 'invoke' with coroutine"
   // 139: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 13c: athrow
}

@JvmSynthetic
fun `awaitClose$default`(var0: ProducerScope, var1: Function0, var2: Continuation, var3: Int, var4: Any) {
   if ((var3 and 1) != 0) {
      var1 = {
      }
   }

   awaitClose(var0, var1, var2)
}

@ExperimentalCoroutinesApi
public fun <E> CoroutineScope.produce(
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = 0,
   block: (ProducerScope<Any>, Continuation<Unit>) -> Any?
): ReceiveChannel<Any> {
   return produce(`$this$produce`, context, capacity, BufferOverflow.SUSPEND, CoroutineStart.DEFAULT, null, block)
}

@InternalCoroutinesApi
public fun <E> CoroutineScope.produce(
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = 0,
   start: CoroutineStart = CoroutineStart.DEFAULT,
   onCompletion: ((Throwable?) -> Unit)? = null,
   block: (ProducerScope<Any>, Continuation<Unit>) -> Any?
): ReceiveChannel<Any> {
   return produce(`$this$produce`, context, capacity, BufferOverflow.SUSPEND, start, onCompletion, block)
}

internal fun <E> CoroutineScope.produce(
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = 0,
   onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
   start: CoroutineStart = CoroutineStart.DEFAULT,
   onCompletion: ((Throwable?) -> Unit)? = null,
   block: (ProducerScope<Any>, Continuation<Unit>) -> Any?
): ReceiveChannel<Any> {
   val coroutine: ProducerCoroutine = ProducerCoroutine(
      CoroutineContextKt.newCoroutineContext(`$this$produce`, context), ChannelKt.Channel$default(capacity, onBufferOverflow, null, 4, null)
   )
   if (onCompletion != null) {
      coroutine.invokeOnCompletion(onCompletion)
   }

   coroutine.start(start, coroutine, block)
   return coroutine
}
