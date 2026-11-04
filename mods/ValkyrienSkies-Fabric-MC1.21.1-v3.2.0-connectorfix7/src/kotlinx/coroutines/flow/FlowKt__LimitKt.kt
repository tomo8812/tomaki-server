@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nLimit.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,141:1\n1#2:142\n107#3:143\n107#3:144\n107#3:145\n107#3:146\n*S KotlinDebug\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt\n*L\n22#1:143\n33#1:144\n52#1:145\n83#1:146\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.internal.AbortFlowException
import kotlinx.coroutines.flow.internal.FlowExceptions_commonKt

public fun <T> Flow<Any>.drop(count: Int): Flow<Any> {
   if (count < 0) {
      throw IllegalArgumentException(("Drop count should be non-negative, but had $count").toString())
   } else {
      return FlowKt__LimitKt$drop$$inlined$unsafeFlow$1(`$this$drop`, count)
   }
}

public fun <T> Flow<Any>.dropWhile(predicate: (Any, Continuation<Boolean>) -> Any?): Flow<Any> {
   return FlowKt__LimitKt$dropWhile$$inlined$unsafeFlow$1(`$this$dropWhile`, predicate)
}

public fun <T> Flow<Any>.take(count: Int): Flow<Any> {
   if (count <= 0) {
      throw IllegalArgumentException(("Requested element count $count should be positive").toString())
   } else {
      return FlowKt__LimitKt$take$$inlined$unsafeFlow$1(`$this$take`, count)
   }
}

private suspend fun <T> FlowCollector<Any>.emitAbort(value: Any) {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1
   // 04: ifeq 27
   // 07: aload 2
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1
   // 0b: astore 4
   // 0d: aload 4
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 4
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1
   // 2a: dup
   // 2b: aload 2
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 4
   // 31: aload 4
   // 33: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1.result Ljava/lang/Object;
   // 36: astore 3
   // 37: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3a: astore 5
   // 3c: aload 4
   // 3e: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1.label I
   // 41: tableswitch 81 0 1 23 57
   // 58: aload 3
   // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5c: aload 0
   // 5d: aload 1
   // 5e: aload 4
   // 60: aload 4
   // 62: aload 0
   // 63: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1.L$0 Ljava/lang/Object;
   // 66: aload 4
   // 68: bipush 1
   // 69: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1.label I
   // 6c: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 71: dup
   // 72: aload 5
   // 74: if_acmpne 88
   // 77: aload 5
   // 79: areturn
   // 7a: aload 4
   // 7c: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$emitAbort$1.L$0 Ljava/lang/Object;
   // 7f: checkcast kotlinx/coroutines/flow/FlowCollector
   // 82: astore 0
   // 83: aload 3
   // 84: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 87: aload 3
   // 88: pop
   // 89: new kotlinx/coroutines/flow/internal/AbortFlowException
   // 8c: dup
   // 8d: aload 0
   // 8e: invokespecial kotlinx/coroutines/flow/internal/AbortFlowException.<init> (Lkotlinx/coroutines/flow/FlowCollector;)V
   // 91: athrow
   // 92: new java/lang/IllegalStateException
   // 95: dup
   // 96: ldc "call to 'resume' before 'invoke' with coroutine"
   // 98: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 9b: athrow
}

public fun <T> Flow<Any>.takeWhile(predicate: (Any, Continuation<Boolean>) -> Any?): Flow<Any> {
   return FlowKt__LimitKt$takeWhile$$inlined$unsafeFlow$1(`$this$takeWhile`, predicate)
}

public fun <T, R> Flow<Any>.transformWhile(transform: (FlowCollector<Any>, Any, Continuation<Boolean>) -> Any?): Flow<Any> {
   return FlowKt.flow({
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2)
}

internal suspend inline fun <T> Flow<Any>.collectWhile(crossinline predicate: (Any, Continuation<Boolean>) -> Any?) {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1
   // 04: ifeq 27
   // 07: aload 2
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1
   // 0b: astore 7
   // 0d: aload 7
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 7
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1
   // 2a: dup
   // 2b: aload 2
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 7
   // 31: aload 7
   // 33: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1.result Ljava/lang/Object;
   // 36: astore 6
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 8
   // 3d: aload 7
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1.label I
   // 42: tableswitch 115 0 1 22 75
   // 58: aload 6
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: bipush 0
   // 5e: istore 3
   // 5f: new kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1
   // 62: dup
   // 63: aload 1
   // 64: invokespecial kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1.<init> (Lkotlin/jvm/functions/Function2;)V
   // 67: astore 4
   // 69: nop
   // 6a: aload 0
   // 6b: aload 4
   // 6d: checkcast kotlinx/coroutines/flow/FlowCollector
   // 70: aload 7
   // 72: aload 7
   // 74: aload 4
   // 76: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1.L$0 Ljava/lang/Object;
   // 79: aload 7
   // 7b: bipush 1
   // 7c: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1.label I
   // 7f: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 84: dup
   // 85: aload 8
   // 87: if_acmpne a1
   // 8a: aload 8
   // 8c: areturn
   // 8d: bipush 0
   // 8e: istore 3
   // 8f: aload 7
   // 91: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$1.L$0 Ljava/lang/Object;
   // 94: checkcast kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1
   // 97: astore 4
   // 99: nop
   // 9a: aload 6
   // 9c: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 9f: aload 6
   // a1: pop
   // a2: goto b1
   // a5: astore 5
   // a7: aload 5
   // a9: aload 4
   // ab: checkcast kotlinx/coroutines/flow/FlowCollector
   // ae: invokestatic kotlinx/coroutines/flow/internal/FlowExceptions_commonKt.checkOwnership (Lkotlinx/coroutines/flow/internal/AbortFlowException;Lkotlinx/coroutines/flow/FlowCollector;)V
   // b1: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // b4: areturn
   // b5: new java/lang/IllegalStateException
   // b8: dup
   // b9: ldc "call to 'resume' before 'invoke' with coroutine"
   // bb: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // be: athrow
}

fun <T> Flow<out T>.`collectWhile$$forInline`(predicate: (T?, Continuation<? super java.lang.Boolean>?) -> Any, `$completion`: Continuation<in Unit>) {
   val collector: <unrepresentable> = object : FlowCollector<Any> {
      public override suspend fun emit(value: Any) {
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
         // 01: instanceof kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1
         // 04: ifeq 27
         // 07: aload 2
         // 08: checkcast kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1
         // 0b: astore 4
         // 0d: aload 4
         // 0f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1.label I
         // 12: ldc -2147483648
         // 14: iand
         // 15: ifeq 27
         // 18: aload 4
         // 1a: dup
         // 1b: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1.label I
         // 1e: ldc -2147483648
         // 20: isub
         // 21: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1.label I
         // 24: goto 32
         // 27: new kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1
         // 2a: dup
         // 2b: aload 0
         // 2c: aload 2
         // 2d: invokespecial kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1;Lkotlin/coroutines/Continuation;)V
         // 30: astore 4
         // 32: aload 4
         // 34: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1.result Ljava/lang/Object;
         // 37: astore 3
         // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
         // 3b: astore 5
         // 3d: aload 4
         // 3f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1.label I
         // 42: tableswitch 98 0 1 22 59
         // 58: aload 3
         // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
         // 5c: aload 0
         // 5d: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1.$predicate Lkotlin/jvm/functions/Function2;
         // 60: aload 1
         // 61: aload 4
         // 63: aload 4
         // 65: aload 0
         // 66: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1.L$0 Ljava/lang/Object;
         // 69: aload 4
         // 6b: bipush 1
         // 6c: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1.label I
         // 6f: invokeinterface kotlin/jvm/functions/Function2.invoke (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
         // 74: dup
         // 75: aload 5
         // 77: if_acmpne 8b
         // 7a: aload 5
         // 7c: areturn
         // 7d: aload 4
         // 7f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1.L$0 Ljava/lang/Object;
         // 82: checkcast kotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1
         // 85: astore 0
         // 86: aload 3
         // 87: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
         // 8a: aload 3
         // 8b: checkcast java/lang/Boolean
         // 8e: invokevirtual java/lang/Boolean.booleanValue ()Z
         // 91: ifne a0
         // 94: new kotlinx/coroutines/flow/internal/AbortFlowException
         // 97: dup
         // 98: aload 0
         // 99: checkcast kotlinx/coroutines/flow/FlowCollector
         // 9c: invokespecial kotlinx/coroutines/flow/internal/AbortFlowException.<init> (Lkotlinx/coroutines/flow/FlowCollector;)V
         // 9f: athrow
         // a0: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // a3: areturn
         // a4: new java/lang/IllegalStateException
         // a7: dup
         // a8: ldc "call to 'resume' before 'invoke' with coroutine"
         // aa: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
         // ad: athrow
      }
   }

   try {
      val var10001: FlowCollector = collector
      InlineMarker.mark(0)
      `$this$collectWhile`.collect(var10001, `$completion`)
      InlineMarker.mark(1)
   } catch (var6: AbortFlowException) {
      FlowExceptions_commonKt.checkOwnership(var6, collector)
   }

   Unit.INSTANCE
}
