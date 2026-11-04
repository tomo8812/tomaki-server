@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nErrors.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Errors.kt\nkotlinx/coroutines/flow/FlowKt__ErrorsKt\n+ 2 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n*L\n1#1,224:1\n107#2:225\n107#2:227\n1#3:226\n163#4:228\n*S KotlinDebug\n*F\n+ 1 Errors.kt\nkotlinx/coroutines/flow/FlowKt__ErrorsKt\n*L\n58#1:225\n132#1:227\n221#1:228\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function4
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.Job
import kotlinx.coroutines.internal.StackTraceRecoveryKt

public fun <T> Flow<Any>.catch(action: (FlowCollector<Any>, Throwable, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1(`$this$catch`, action)
}

public fun <T> Flow<Any>.retry(retries: Long = java.lang.Long.MAX_VALUE, predicate: (Throwable, Continuation<Boolean>) -> Any? = {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2): Flow<Any> {
   if (retries <= 0L) {
      throw IllegalArgumentException(("Expected positive amount of retries, but had $retries").toString())
   } else {
      return FlowKt.retryWhen(`$this$retry`, {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function4)
   }
}

public fun <T> Flow<Any>.retryWhen(predicate: (FlowCollector<Any>, Throwable, Long, Continuation<Boolean>) -> Any?): Flow<Any> {
   return FlowKt__ErrorsKt$retryWhen$$inlined$unsafeFlow$1(`$this$retryWhen`, predicate)
}

internal suspend fun <T> Flow<Any>.catchImpl(collector: FlowCollector<Any>): Throwable? {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1
   // 04: ifeq 27
   // 07: aload 2
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1
   // 0b: astore 7
   // 0d: aload 7
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 7
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1
   // 2a: dup
   // 2b: aload 2
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 7
   // 31: aload 7
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1.result Ljava/lang/Object;
   // 36: astore 6
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 8
   // 3d: aload 7
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1.label I
   // 42: tableswitch 175 0 1 22 77
   // 58: aload 6
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: new kotlin/jvm/internal/Ref$ObjectRef
   // 60: dup
   // 61: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 64: astore 3
   // 65: nop
   // 66: aload 0
   // 67: new kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$2
   // 6a: dup
   // 6b: aload 1
   // 6c: aload 3
   // 6d: invokespecial kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$2.<init> (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/jvm/internal/Ref$ObjectRef;)V
   // 70: checkcast kotlinx/coroutines/flow/FlowCollector
   // 73: aload 7
   // 75: aload 7
   // 77: aload 3
   // 78: putfield kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1.L$0 Ljava/lang/Object;
   // 7b: aload 7
   // 7d: bipush 1
   // 7e: putfield kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1.label I
   // 81: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 86: dup
   // 87: aload 8
   // 89: if_acmpne a0
   // 8c: aload 8
   // 8e: areturn
   // 8f: aload 7
   // 91: getfield kotlinx/coroutines/flow/FlowKt__ErrorsKt$catchImpl$1.L$0 Ljava/lang/Object;
   // 94: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // 97: astore 3
   // 98: nop
   // 99: aload 6
   // 9b: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 9e: aload 6
   // a0: pop
   // a1: goto ef
   // a4: astore 4
   // a6: aload 3
   // a7: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // aa: checkcast java/lang/Throwable
   // ad: astore 5
   // af: aload 4
   // b1: aload 5
   // b3: invokestatic kotlinx/coroutines/flow/FlowKt__ErrorsKt.isSameExceptionAs$FlowKt__ErrorsKt (Ljava/lang/Throwable;Ljava/lang/Throwable;)Z
   // b6: ifne c8
   // b9: aload 4
   // bb: aload 7
   // bd: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
   // c2: invokestatic kotlinx/coroutines/flow/FlowKt__ErrorsKt.isCancellationCause$FlowKt__ErrorsKt (Ljava/lang/Throwable;Lkotlin/coroutines/CoroutineContext;)Z
   // c5: ifeq cb
   // c8: aload 4
   // ca: athrow
   // cb: aload 5
   // cd: ifnonnull d3
   // d0: aload 4
   // d2: areturn
   // d3: aload 4
   // d5: instanceof java/util/concurrent/CancellationException
   // d8: ifeq e5
   // db: aload 5
   // dd: aload 4
   // df: invokestatic kotlin/ExceptionsKt.addSuppressed (Ljava/lang/Throwable;Ljava/lang/Throwable;)V
   // e2: aload 5
   // e4: athrow
   // e5: aload 4
   // e7: aload 5
   // e9: invokestatic kotlin/ExceptionsKt.addSuppressed (Ljava/lang/Throwable;Ljava/lang/Throwable;)V
   // ec: aload 4
   // ee: athrow
   // ef: aconst_null
   // f0: areturn
   // f1: new java/lang/IllegalStateException
   // f4: dup
   // f5: ldc "call to 'resume' before 'invoke' with coroutine"
   // f7: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // fa: athrow
}

private fun Throwable.isCancellationCause(coroutineContext: CoroutineContext): Boolean {
   val job: Job = coroutineContext.get(Job.Key) as Job
   return job != null && job.isCancelled && isSameExceptionAs$FlowKt__ErrorsKt(`$this$isCancellationCause`, job.getCancellationException())
}

private fun Throwable.isSameExceptionAs(other: Throwable?): Boolean {
   return other != null
      && (if (!RECOVER_STACK_TRACES) other else StackTraceRecoveryKt.unwrapImpl(other))
         == (if (!RECOVER_STACK_TRACES) `$this$isSameExceptionAs` else StackTraceRecoveryKt.unwrapImpl(`$this$isSameExceptionAs`))
      }
