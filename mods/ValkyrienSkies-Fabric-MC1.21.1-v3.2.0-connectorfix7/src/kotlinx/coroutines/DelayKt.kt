@file:SourceDebugExtension(["SMAP\nDelay.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Delay.kt\nkotlinx/coroutines/DelayKt\n+ 2 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n*L\n1#1,162:1\n314#2,11:163\n314#2,11:174\n*S KotlinDebug\n*F\n+ 1 Delay.kt\nkotlinx/coroutines/DelayKt\n*L\n106#1:163,11\n127#1:174,11\n*E\n"])

package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.time.Duration

public suspend fun awaitCancellation(): Nothing {
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
   // 00: aload 0
   // 01: instanceof kotlinx/coroutines/DelayKt$awaitCancellation$1
   // 04: ifeq 27
   // 07: aload 0
   // 08: checkcast kotlinx/coroutines/DelayKt$awaitCancellation$1
   // 0b: astore 7
   // 0d: aload 7
   // 0f: getfield kotlinx/coroutines/DelayKt$awaitCancellation$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 7
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/DelayKt$awaitCancellation$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/DelayKt$awaitCancellation$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/DelayKt$awaitCancellation$1
   // 2a: dup
   // 2b: aload 0
   // 2c: invokespecial kotlinx/coroutines/DelayKt$awaitCancellation$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 7
   // 31: aload 7
   // 33: getfield kotlinx/coroutines/DelayKt$awaitCancellation$1.result Ljava/lang/Object;
   // 36: astore 6
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 8
   // 3d: aload 7
   // 3f: getfield kotlinx/coroutines/DelayKt$awaitCancellation$1.label I
   // 42: tableswitch 121 0 1 22 102
   // 58: aload 6
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: bipush 0
   // 5e: istore 1
   // 5f: aload 7
   // 61: bipush 1
   // 62: putfield kotlinx/coroutines/DelayKt$awaitCancellation$1.label I
   // 65: aload 7
   // 67: checkcast kotlin/coroutines/Continuation
   // 6a: astore 2
   // 6b: bipush 0
   // 6c: istore 3
   // 6d: new kotlinx/coroutines/CancellableContinuationImpl
   // 70: dup
   // 71: aload 2
   // 72: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.intercepted (Lkotlin/coroutines/Continuation;)Lkotlin/coroutines/Continuation;
   // 75: bipush 1
   // 76: invokespecial kotlinx/coroutines/CancellableContinuationImpl.<init> (Lkotlin/coroutines/Continuation;I)V
   // 79: astore 4
   // 7b: aload 4
   // 7d: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.initCancellability ()V
   // 80: aload 4
   // 82: checkcast kotlinx/coroutines/CancellableContinuation
   // 85: pop
   // 86: bipush 0
   // 87: istore 5
   // 89: nop
   // 8a: nop
   // 8b: aload 4
   // 8d: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.getResult ()Ljava/lang/Object;
   // 90: dup
   // 91: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 94: if_acmpne 9f
   // 97: aload 7
   // 99: checkcast kotlin/coroutines/Continuation
   // 9c: invokestatic kotlin/coroutines/jvm/internal/DebugProbesKt.probeCoroutineSuspended (Lkotlin/coroutines/Continuation;)V
   // 9f: dup
   // a0: aload 8
   // a2: if_acmpne b1
   // a5: aload 8
   // a7: areturn
   // a8: bipush 0
   // a9: istore 1
   // aa: aload 6
   // ac: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // af: aload 6
   // b1: nop
   // b2: pop
   // b3: new kotlin/KotlinNothingValueException
   // b6: dup
   // b7: invokespecial kotlin/KotlinNothingValueException.<init> ()V
   // ba: athrow
   // bb: new java/lang/IllegalStateException
   // be: dup
   // bf: ldc "call to 'resume' before 'invoke' with coroutine"
   // c1: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // c4: athrow
}

public suspend fun delay(timeMillis: Long) {
   if (timeMillis <= 0L) {
      return Unit.INSTANCE
   } else {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
      `cancellable$iv`.initCancellability()
      val cont: CancellableContinuation = `cancellable$iv`
      if (timeMillis < java.lang.Long.MAX_VALUE) {
         delay.scheduleResumeAfterDelay(timeMillis, cont)
      }

      val var10000: Any = `cancellable$iv`.getResult()
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}

public suspend fun delay(duration: Duration) {
   val var10000: Any = delay(toDelayMillis_LRDsOJo/* $VF was: toDelayMillis-LRDsOJo */(duration), `$completion`)
   return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
}

internal final val delay: Delay
   internal final get() {
      val var1: Element = `$this$delay`.get(ContinuationInterceptor.Key as Key)
      var var10000: Delay = var1 as? Delay
      if ((var1 as? Delay) == null) {
         var10000 = DefaultDelay
      }

      return var10000
   }


internal fun Duration.toDelayMillis(): Long {
   return if (Duration.compareTo_LRDsOJo/* $VF was: compareTo-LRDsOJo */(
            `$this$toDelayMillis_u2dLRDsOJo`, Duration.Companion.getZERO_UwyO8pc/* $VF was: getZERO-UwyO8pc */()
         )
         > 0)
      RangesKt.coerceAtLeast(Duration.getInWholeMilliseconds_impl/* $VF was: getInWholeMilliseconds-impl */(`$this$toDelayMillis_u2dLRDsOJo`), 1L)
      else
      0L
   }
