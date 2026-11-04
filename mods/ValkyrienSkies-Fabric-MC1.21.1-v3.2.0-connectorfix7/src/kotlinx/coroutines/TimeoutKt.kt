package kotlinx.coroutines

import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.time.Duration
import kotlin.time.DurationKt
import kotlin.time.DurationUnit
import kotlinx.coroutines.intrinsics.UndispatchedKt

public suspend fun <T> withTimeout(timeMillis: Long, block: (CoroutineScope, Continuation<Any>) -> Any?): Any {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   if (timeMillis <= 0L) {
      throw TimeoutCancellationException("Timed out immediately")
   } else {
      val var10000: Any = setupTimeout(TimeoutCoroutine(timeMillis, `$completion`), block)
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return var10000
   }
}

public suspend fun <T> withTimeout(timeout: Duration, block: (CoroutineScope, Continuation<Any>) -> Any?): Any {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   return withTimeout(DelayKt.toDelayMillis_LRDsOJo/* $VF was: toDelayMillis-LRDsOJo */(timeout), block, `$completion`)
}

public suspend fun <T> withTimeoutOrNull(timeMillis: Long, block: (CoroutineScope, Continuation<Any>) -> Any?): Any? {
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
   // 000: aload 3
   // 001: instanceof kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1
   // 004: ifeq 027
   // 007: aload 3
   // 008: checkcast kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1
   // 00b: astore 9
   // 00d: aload 9
   // 00f: getfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 9
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1
   // 02a: dup
   // 02b: aload 3
   // 02c: invokespecial kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 9
   // 031: aload 9
   // 033: getfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.result Ljava/lang/Object;
   // 036: astore 8
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 10
   // 03d: aload 9
   // 03f: getfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.label I
   // 042: tableswitch 183 0 1 22 129
   // 058: aload 8
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: lload 0
   // 05e: lconst_0
   // 05f: lcmp
   // 060: ifgt 065
   // 063: aconst_null
   // 064: areturn
   // 065: new kotlin/jvm/internal/Ref$ObjectRef
   // 068: dup
   // 069: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 06c: astore 4
   // 06e: nop
   // 06f: aload 9
   // 071: aload 2
   // 072: putfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.L$0 Ljava/lang/Object;
   // 075: aload 9
   // 077: aload 4
   // 079: putfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.L$1 Ljava/lang/Object;
   // 07c: aload 9
   // 07e: lload 0
   // 07f: putfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.J$0 J
   // 082: aload 9
   // 084: bipush 1
   // 085: putfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.label I
   // 088: aload 9
   // 08a: checkcast kotlin/coroutines/Continuation
   // 08d: astore 5
   // 08f: bipush 0
   // 090: istore 6
   // 092: new kotlinx/coroutines/TimeoutCoroutine
   // 095: dup
   // 096: lload 0
   // 097: aload 5
   // 099: invokespecial kotlinx/coroutines/TimeoutCoroutine.<init> (JLkotlin/coroutines/Continuation;)V
   // 09c: astore 7
   // 09e: aload 4
   // 0a0: aload 7
   // 0a2: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 0a5: aload 7
   // 0a7: aload 2
   // 0a8: invokestatic kotlinx/coroutines/TimeoutKt.setupTimeout (Lkotlinx/coroutines/TimeoutCoroutine;Lkotlin/jvm/functions/Function2;)Ljava/lang/Object;
   // 0ab: dup
   // 0ac: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 0af: if_acmpne 0ba
   // 0b2: aload 9
   // 0b4: checkcast kotlin/coroutines/Continuation
   // 0b7: invokestatic kotlin/coroutines/jvm/internal/DebugProbesKt.probeCoroutineSuspended (Lkotlin/coroutines/Continuation;)V
   // 0ba: dup
   // 0bb: aload 10
   // 0bd: if_acmpne 0e4
   // 0c0: aload 10
   // 0c2: areturn
   // 0c3: aload 9
   // 0c5: getfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.J$0 J
   // 0c8: pop2
   // 0c9: aload 9
   // 0cb: getfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.L$1 Ljava/lang/Object;
   // 0ce: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // 0d1: astore 4
   // 0d3: aload 9
   // 0d5: getfield kotlinx/coroutines/TimeoutKt$withTimeoutOrNull$1.L$0 Ljava/lang/Object;
   // 0d8: checkcast kotlin/jvm/functions/Function2
   // 0db: astore 2
   // 0dc: nop
   // 0dd: aload 8
   // 0df: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0e2: aload 8
   // 0e4: areturn
   // 0e5: astore 5
   // 0e7: aload 5
   // 0e9: getfield kotlinx/coroutines/TimeoutCancellationException.coroutine Lkotlinx/coroutines/Job;
   // 0ec: aload 4
   // 0ee: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 0f1: if_acmpne 0f6
   // 0f4: aconst_null
   // 0f5: areturn
   // 0f6: aload 5
   // 0f8: athrow
   // 0f9: new java/lang/IllegalStateException
   // 0fc: dup
   // 0fd: ldc "call to 'resume' before 'invoke' with coroutine"
   // 0ff: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 102: athrow
}

public suspend fun <T> withTimeoutOrNull(timeout: Duration, block: (CoroutineScope, Continuation<Any>) -> Any?): Any? {
   return withTimeoutOrNull(DelayKt.toDelayMillis_LRDsOJo/* $VF was: toDelayMillis-LRDsOJo */(timeout), block, `$completion`)
}

private fun <U, T : Any> setupTimeout(coroutine: TimeoutCoroutine<Any, Any>, block: (CoroutineScope, Continuation<Any>) -> Any?): Any? {
   JobKt.disposeOnCompletion(coroutine, delay.invokeOnTimeout(coroutine.time, coroutine, coroutine.getContext()))
   return UndispatchedKt.startUndispatchedOrReturnIgnoreTimeout(coroutine, coroutine, block)
}

internal fun TimeoutCancellationException(time: Long, delay: Delay, coroutine: Job): TimeoutCancellationException {
   var var5: java.lang.String
   run label22@{
      val var10000: DelayWithTimeoutDiagnostics = delay as? DelayWithTimeoutDiagnostics
      if ((delay as? DelayWithTimeoutDiagnostics) != null) {
         var5 = var10000.timeoutMessage_LRDsOJo/* $VF was: timeoutMessage-LRDsOJo */(DurationKt.toDuration(time, DurationUnit.MILLISECONDS))
         if (var5 != null) {
            return@label22
         }
      }

      var5 = "Timed out waiting for $time ms"
   }

   return TimeoutCancellationException(var5, coroutine)
}
