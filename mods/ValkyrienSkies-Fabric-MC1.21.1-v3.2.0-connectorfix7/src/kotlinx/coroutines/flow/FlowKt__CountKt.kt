@file:JvmMultifileClass
@file:JvmName("FlowKt")

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation

public suspend fun <T> Flow<Any>.count(): Int {
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
   // 00: aload 1
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__CountKt$count$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__CountKt$count$1
   // 0b: astore 4
   // 0d: aload 4
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 4
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__CountKt$count$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__CountKt$count$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__CountKt$count$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 4
   // 31: aload 4
   // 33: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$1.result Ljava/lang/Object;
   // 36: astore 3
   // 37: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3a: astore 5
   // 3c: aload 4
   // 3e: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$1.label I
   // 41: tableswitch 98 0 1 23 75
   // 58: aload 3
   // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5c: new kotlin/jvm/internal/Ref$IntRef
   // 5f: dup
   // 60: invokespecial kotlin/jvm/internal/Ref$IntRef.<init> ()V
   // 63: astore 2
   // 64: aload 0
   // 65: new kotlinx/coroutines/flow/FlowKt__CountKt$count$2
   // 68: dup
   // 69: aload 2
   // 6a: invokespecial kotlinx/coroutines/flow/FlowKt__CountKt$count$2.<init> (Lkotlin/jvm/internal/Ref$IntRef;)V
   // 6d: checkcast kotlinx/coroutines/flow/FlowCollector
   // 70: aload 4
   // 72: aload 4
   // 74: aload 2
   // 75: putfield kotlinx/coroutines/flow/FlowKt__CountKt$count$1.L$0 Ljava/lang/Object;
   // 78: aload 4
   // 7a: bipush 1
   // 7b: putfield kotlinx/coroutines/flow/FlowKt__CountKt$count$1.label I
   // 7e: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 83: dup
   // 84: aload 5
   // 86: if_acmpne 9a
   // 89: aload 5
   // 8b: areturn
   // 8c: aload 4
   // 8e: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$1.L$0 Ljava/lang/Object;
   // 91: checkcast kotlin/jvm/internal/Ref$IntRef
   // 94: astore 2
   // 95: aload 3
   // 96: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 99: aload 3
   // 9a: pop
   // 9b: aload 2
   // 9c: getfield kotlin/jvm/internal/Ref$IntRef.element I
   // 9f: invokestatic kotlin/coroutines/jvm/internal/Boxing.boxInt (I)Ljava/lang/Integer;
   // a2: areturn
   // a3: new java/lang/IllegalStateException
   // a6: dup
   // a7: ldc "call to 'resume' before 'invoke' with coroutine"
   // a9: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // ac: athrow
}

public suspend fun <T> Flow<Any>.count(predicate: (Any, Continuation<Boolean>) -> Any?): Int {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__CountKt$count$3
   // 04: ifeq 27
   // 07: aload 2
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__CountKt$count$3
   // 0b: astore 5
   // 0d: aload 5
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$3.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 5
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$3.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__CountKt$count$3.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__CountKt$count$3
   // 2a: dup
   // 2b: aload 2
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__CountKt$count$3.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 5
   // 31: aload 5
   // 33: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$3.result Ljava/lang/Object;
   // 36: astore 4
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 6
   // 3d: aload 5
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$3.label I
   // 42: tableswitch 101 0 1 22 76
   // 58: aload 4
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: new kotlin/jvm/internal/Ref$IntRef
   // 60: dup
   // 61: invokespecial kotlin/jvm/internal/Ref$IntRef.<init> ()V
   // 64: astore 3
   // 65: aload 0
   // 66: new kotlinx/coroutines/flow/FlowKt__CountKt$count$4
   // 69: dup
   // 6a: aload 1
   // 6b: aload 3
   // 6c: invokespecial kotlinx/coroutines/flow/FlowKt__CountKt$count$4.<init> (Lkotlin/jvm/functions/Function2;Lkotlin/jvm/internal/Ref$IntRef;)V
   // 6f: checkcast kotlinx/coroutines/flow/FlowCollector
   // 72: aload 5
   // 74: aload 5
   // 76: aload 3
   // 77: putfield kotlinx/coroutines/flow/FlowKt__CountKt$count$3.L$0 Ljava/lang/Object;
   // 7a: aload 5
   // 7c: bipush 1
   // 7d: putfield kotlinx/coroutines/flow/FlowKt__CountKt$count$3.label I
   // 80: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 85: dup
   // 86: aload 6
   // 88: if_acmpne 9e
   // 8b: aload 6
   // 8d: areturn
   // 8e: aload 5
   // 90: getfield kotlinx/coroutines/flow/FlowKt__CountKt$count$3.L$0 Ljava/lang/Object;
   // 93: checkcast kotlin/jvm/internal/Ref$IntRef
   // 96: astore 3
   // 97: aload 4
   // 99: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 9c: aload 4
   // 9e: pop
   // 9f: aload 3
   // a0: getfield kotlin/jvm/internal/Ref$IntRef.element I
   // a3: invokestatic kotlin/coroutines/jvm/internal/Boxing.boxInt (I)Ljava/lang/Integer;
   // a6: areturn
   // a7: new java/lang/IllegalStateException
   // aa: dup
   // ab: ldc "call to 'resume' before 'invoke' with coroutine"
   // ad: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // b0: athrow
}
