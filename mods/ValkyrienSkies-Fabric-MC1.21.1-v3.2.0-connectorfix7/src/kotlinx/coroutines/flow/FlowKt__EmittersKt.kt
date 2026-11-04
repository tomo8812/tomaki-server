@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nEmitters.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 2 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n+ 3 Exceptions.kt\nkotlinx/coroutines/ExceptionsKt\n*L\n1#1,222:1\n107#2:223\n107#2:224\n107#2:225\n107#2:226\n75#3:227\n*S KotlinDebug\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n*L\n50#1:223\n76#1:224\n146#1:225\n181#1:226\n218#1:227\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension

public inline fun <T, R> Flow<Any>.transform(crossinline transform: (FlowCollector<Any>, Any, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt.flow({
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2)
}

@PublishedApi
internal inline fun <T, R> Flow<Any>.unsafeTransform(crossinline transform: (FlowCollector<Any>, Any, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt__EmittersKt$unsafeTransform$$inlined$unsafeFlow$1(`$this$unsafeTransform`, transform)
}

public fun <T> Flow<Any>.onStart(action: (FlowCollector<Any>, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1(action, `$this$onStart`)
}

public fun <T> Flow<Any>.onCompletion(action: (FlowCollector<Any>, Throwable?, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt__EmittersKt$onCompletion$$inlined$unsafeFlow$1(`$this$onCompletion`, action)
}

public fun <T> Flow<Any>.onEmpty(action: (FlowCollector<Any>, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt__EmittersKt$onEmpty$$inlined$unsafeFlow$1(`$this$onEmpty`, action)
}

internal fun FlowCollector<*>.ensureActive() {
   if (`$this$ensureActive` is ThrowingCollector) {
      throw (`$this$ensureActive` as ThrowingCollector).e
   }
}

private suspend fun <T> FlowCollector<Any>.invokeSafely(action: (FlowCollector<Any>, Throwable?, Continuation<Unit>) -> Any?, cause: Throwable?) {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1
   // 04: ifeq 27
   // 07: aload 3
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1
   // 0b: astore 8
   // 0d: aload 8
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 8
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1
   // 2a: dup
   // 2b: aload 3
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 8
   // 31: aload 8
   // 33: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1.result Ljava/lang/Object;
   // 36: astore 7
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 9
   // 3d: aload 8
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1.label I
   // 42: tableswitch 112 0 1 22 59
   // 58: aload 7
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: nop
   // 5e: aload 1
   // 5f: aload 0
   // 60: aload 2
   // 61: aload 8
   // 63: aload 8
   // 65: aload 2
   // 66: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1.L$0 Ljava/lang/Object;
   // 69: aload 8
   // 6b: bipush 1
   // 6c: putfield kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1.label I
   // 6f: invokeinterface kotlin/jvm/functions/Function3.invoke (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 4
   // 74: dup
   // 75: aload 9
   // 77: if_acmpne 8e
   // 7a: aload 9
   // 7c: areturn
   // 7d: aload 8
   // 7f: getfield kotlinx/coroutines/flow/FlowKt__EmittersKt$invokeSafely$1.L$0 Ljava/lang/Object;
   // 82: checkcast java/lang/Throwable
   // 85: astore 2
   // 86: nop
   // 87: aload 7
   // 89: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 8c: aload 7
   // 8e: pop
   // 8f: goto ae
   // 92: astore 4
   // 94: aload 2
   // 95: ifnull ab
   // 98: aload 2
   // 99: aload 4
   // 9b: if_acmpeq ab
   // 9e: aload 4
   // a0: astore 5
   // a2: bipush 0
   // a3: istore 6
   // a5: aload 5
   // a7: aload 2
   // a8: invokestatic kotlin/ExceptionsKt.addSuppressed (Ljava/lang/Throwable;Ljava/lang/Throwable;)V
   // ab: aload 4
   // ad: athrow
   // ae: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // b1: areturn
   // b2: new java/lang/IllegalStateException
   // b5: dup
   // b6: ldc "call to 'resume' before 'invoke' with coroutine"
   // b8: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // bb: athrow
}
