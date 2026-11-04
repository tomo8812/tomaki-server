package kotlinx.coroutines.sync

import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.InlineMarker
import kotlinx.coroutines.internal.Symbol

private final val NO_OWNER: Symbol = Symbol("NO_OWNER")
private final val ON_LOCK_ALREADY_LOCKED_BY_OWNER: Symbol = Symbol("ALREADY_LOCKED_BY_OWNER")
private const val TRY_LOCK_SUCCESS: Int = 0
private const val TRY_LOCK_FAILED: Int = 1
private const val TRY_LOCK_ALREADY_LOCKED_BY_OWNER: Int = 2
private const val HOLDS_LOCK_UNLOCKED: Int = 0
private const val HOLDS_LOCK_YES: Int = 1
private const val HOLDS_LOCK_ANOTHER_OWNER: Int = 2

public fun Mutex(locked: Boolean = false): Mutex {
   return MutexImpl(locked)
}

public suspend inline fun <T> Mutex.withLock(owner: Any? = ..., action: () -> Any): Any {
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
   // 00: aload 3
   // 01: instanceof kotlinx/coroutines/sync/MutexKt$withLock$1
   // 04: ifeq 27
   // 07: aload 3
   // 08: checkcast kotlinx/coroutines/sync/MutexKt$withLock$1
   // 0b: astore 7
   // 0d: aload 7
   // 0f: getfield kotlinx/coroutines/sync/MutexKt$withLock$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 7
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/sync/MutexKt$withLock$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/sync/MutexKt$withLock$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/sync/MutexKt$withLock$1
   // 2a: dup
   // 2b: aload 3
   // 2c: invokespecial kotlinx/coroutines/sync/MutexKt$withLock$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 7
   // 31: aload 7
   // 33: getfield kotlinx/coroutines/sync/MutexKt$withLock$1.result Ljava/lang/Object;
   // 36: astore 6
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 8
   // 3d: aload 7
   // 3f: getfield kotlinx/coroutines/sync/MutexKt$withLock$1.label I
   // 42: tableswitch 155 0 1 22 73
   // 58: aload 6
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: bipush 0
   // 5e: istore 4
   // 60: nop
   // 61: aload 0
   // 62: aload 1
   // 63: aload 7
   // 65: aload 7
   // 67: aload 0
   // 68: putfield kotlinx/coroutines/sync/MutexKt$withLock$1.L$0 Ljava/lang/Object;
   // 6b: aload 7
   // 6d: aload 1
   // 6e: putfield kotlinx/coroutines/sync/MutexKt$withLock$1.L$1 Ljava/lang/Object;
   // 71: aload 7
   // 73: aload 2
   // 74: putfield kotlinx/coroutines/sync/MutexKt$withLock$1.L$2 Ljava/lang/Object;
   // 77: aload 7
   // 79: bipush 1
   // 7a: putfield kotlinx/coroutines/sync/MutexKt$withLock$1.label I
   // 7d: invokeinterface kotlinx/coroutines/sync/Mutex.lock (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 82: dup
   // 83: aload 8
   // 85: if_acmpne ad
   // 88: aload 8
   // 8a: areturn
   // 8b: bipush 0
   // 8c: istore 4
   // 8e: aload 7
   // 90: getfield kotlinx/coroutines/sync/MutexKt$withLock$1.L$2 Ljava/lang/Object;
   // 93: checkcast kotlin/jvm/functions/Function0
   // 96: astore 2
   // 97: aload 7
   // 99: getfield kotlinx/coroutines/sync/MutexKt$withLock$1.L$1 Ljava/lang/Object;
   // 9c: astore 1
   // 9d: aload 7
   // 9f: getfield kotlinx/coroutines/sync/MutexKt$withLock$1.L$0 Ljava/lang/Object;
   // a2: checkcast kotlinx/coroutines/sync/Mutex
   // a5: astore 0
   // a6: aload 6
   // a8: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // ab: aload 6
   // ad: pop
   // ae: nop
   // af: aload 2
   // b0: invokeinterface kotlin/jvm/functions/Function0.invoke ()Ljava/lang/Object; 1
   // b5: astore 5
   // b7: bipush 1
   // b8: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
   // bb: aload 0
   // bc: aload 1
   // bd: invokeinterface kotlinx/coroutines/sync/Mutex.unlock (Ljava/lang/Object;)V 2
   // c2: bipush 1
   // c3: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
   // c6: aload 5
   // c8: areturn
   // c9: astore 5
   // cb: bipush 1
   // cc: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
   // cf: aload 0
   // d0: aload 1
   // d1: invokeinterface kotlinx/coroutines/sync/Mutex.unlock (Ljava/lang/Object;)V 2
   // d6: bipush 1
   // d7: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
   // da: aload 5
   // dc: athrow
   // dd: new java/lang/IllegalStateException
   // e0: dup
   // e1: ldc "call to 'resume' before 'invoke' with coroutine"
   // e3: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // e6: athrow
}

fun <T> Mutex.`withLock$$forInline`(owner: Any, action: () -> T, `$completion`: Continuation<in T>) {
   InlineMarker.mark(0)
   `$this$withLock`.lock(owner, `$completion`)
   InlineMarker.mark(1)

   try {
      action()
   } finally {
      InlineMarker.finallyStart(1)
      `$this$withLock`.unlock(owner)
      InlineMarker.finallyEnd(1)
   }
}

@JvmSynthetic
fun Mutex.`withLock$default`(owner: Any, action: Function0, `$completion`: Continuation, `$i$f$withLock`: Int, var5: Any) {
   if ((`$i$f$withLock` and 1) != 0) {
      owner = null
   }

   InlineMarker.mark(0)
   `$this$withLock_u24default`.lock(owner, `$completion`)
   InlineMarker.mark(1)

   try {
      action()
   } finally {
      InlineMarker.finallyStart(1)
      `$this$withLock_u24default`.unlock(owner)
      InlineMarker.finallyEnd(1)
   }
}

@JvmSynthetic
fun `access$getNO_OWNER$p`(): Symbol {
   NO_OWNER
}

@JvmSynthetic
fun `access$getON_LOCK_ALREADY_LOCKED_BY_OWNER$p`(): Symbol {
   ON_LOCK_ALREADY_LOCKED_BY_OWNER
}
