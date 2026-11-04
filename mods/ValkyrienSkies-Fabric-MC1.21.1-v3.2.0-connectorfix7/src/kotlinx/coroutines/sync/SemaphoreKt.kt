package kotlinx.coroutines.sync

import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.jvm.internal.InlineMarker
import kotlinx.coroutines.internal.Symbol
import kotlinx.coroutines.internal.SystemPropsKt

private final val MAX_SPIN_CYCLES: Int = SystemPropsKt.systemProp$default("kotlinx.coroutines.semaphore.maxSpinCycles", 100, 0, 0, 12, null)
private final val PERMIT: Symbol = Symbol("PERMIT")
private final val TAKEN: Symbol = Symbol("TAKEN")
private final val BROKEN: Symbol = Symbol("BROKEN")
private final val CANCELLED: Symbol = Symbol("CANCELLED")
private final val SEGMENT_SIZE: Int = SystemPropsKt.systemProp$default("kotlinx.coroutines.semaphore.segmentSize", 16, 0, 0, 12, null)

public fun Semaphore(permits: Int, acquiredPermits: Int = 0): Semaphore {
   return SemaphoreImpl(permits, acquiredPermits)
}

public suspend inline fun <T> Semaphore.withPermit(action: () -> Any): Any {
   contract {
      callsInPlace(action, InvocationKind.EXACTLY_ONCE)
   }
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
   // 01: instanceof kotlinx/coroutines/sync/SemaphoreKt$withPermit$1
   // 04: ifeq 27
   // 07: aload 2
   // 08: checkcast kotlinx/coroutines/sync/SemaphoreKt$withPermit$1
   // 0b: astore 6
   // 0d: aload 6
   // 0f: getfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 6
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/sync/SemaphoreKt$withPermit$1
   // 2a: dup
   // 2b: aload 2
   // 2c: invokespecial kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 6
   // 31: aload 6
   // 33: getfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.result Ljava/lang/Object;
   // 36: astore 5
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 7
   // 3d: aload 6
   // 3f: getfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.label I
   // 42: tableswitch 138 0 1 22 65
   // 58: aload 5
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: bipush 0
   // 5e: istore 3
   // 5f: nop
   // 60: aload 0
   // 61: aload 6
   // 63: aload 6
   // 65: aload 0
   // 66: putfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.L$0 Ljava/lang/Object;
   // 69: aload 6
   // 6b: aload 1
   // 6c: putfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.L$1 Ljava/lang/Object;
   // 6f: aload 6
   // 71: bipush 1
   // 72: putfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.label I
   // 75: invokeinterface kotlinx/coroutines/sync/Semaphore.acquire (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 7a: dup
   // 7b: aload 7
   // 7d: if_acmpne 9e
   // 80: aload 7
   // 82: areturn
   // 83: bipush 0
   // 84: istore 3
   // 85: aload 6
   // 87: getfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.L$1 Ljava/lang/Object;
   // 8a: checkcast kotlin/jvm/functions/Function0
   // 8d: astore 1
   // 8e: aload 6
   // 90: getfield kotlinx/coroutines/sync/SemaphoreKt$withPermit$1.L$0 Ljava/lang/Object;
   // 93: checkcast kotlinx/coroutines/sync/Semaphore
   // 96: astore 0
   // 97: aload 5
   // 99: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 9c: aload 5
   // 9e: pop
   // 9f: nop
   // a0: aload 1
   // a1: invokeinterface kotlin/jvm/functions/Function0.invoke ()Ljava/lang/Object; 1
   // a6: astore 4
   // a8: bipush 1
   // a9: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
   // ac: aload 0
   // ad: invokeinterface kotlinx/coroutines/sync/Semaphore.release ()V 1
   // b2: bipush 1
   // b3: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
   // b6: aload 4
   // b8: areturn
   // b9: astore 4
   // bb: bipush 1
   // bc: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
   // bf: aload 0
   // c0: invokeinterface kotlinx/coroutines/sync/Semaphore.release ()V 1
   // c5: bipush 1
   // c6: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
   // c9: aload 4
   // cb: athrow
   // cc: new java/lang/IllegalStateException
   // cf: dup
   // d0: ldc "call to 'resume' before 'invoke' with coroutine"
   // d2: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // d5: athrow
}

fun <T> Semaphore.`withPermit$$forInline`(action: () -> T, `$completion`: Continuation<in T>) {
   InlineMarker.mark(0)
   `$this$withPermit`.acquire(`$completion`)
   InlineMarker.mark(1)

   try {
      action()
   } finally {
      InlineMarker.finallyStart(1)
      `$this$withPermit`.release()
      InlineMarker.finallyEnd(1)
   }
}

private fun createSegment(id: Long, prev: SemaphoreSegment?): SemaphoreSegment {
   return SemaphoreSegment(id, prev, 0)
}

@JvmSynthetic
fun `access$createSegment`(id: Long, prev: SemaphoreSegment): SemaphoreSegment {
   createSegment(id, prev)
}

@JvmSynthetic
fun `access$getSEGMENT_SIZE$p`(): Int {
   SEGMENT_SIZE
}

@JvmSynthetic
fun `access$getPERMIT$p`(): Symbol {
   PERMIT
}

@JvmSynthetic
fun `access$getTAKEN$p`(): Symbol {
   TAKEN
}

@JvmSynthetic
fun `access$getMAX_SPIN_CYCLES$p`(): Int {
   MAX_SPIN_CYCLES
}

@JvmSynthetic
fun `access$getBROKEN$p`(): Symbol {
   BROKEN
}

@JvmSynthetic
fun `access$getCANCELLED$p`(): Symbol {
   CANCELLED
}
