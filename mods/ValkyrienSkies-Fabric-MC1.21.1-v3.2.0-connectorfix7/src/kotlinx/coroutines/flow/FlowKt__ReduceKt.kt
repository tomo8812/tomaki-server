@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nReduce.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Reduce.kt\nkotlinx/coroutines/flow/FlowKt__ReduceKt\n+ 2 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt\n*L\n1#1,172:1\n126#2,15:173\n126#2,15:188\n126#2,15:203\n126#2,15:218\n126#2,15:233\n*S KotlinDebug\n*F\n+ 1 Reduce.kt\nkotlinx/coroutines/flow/FlowKt__ReduceKt\n*L\n72#1:173,15\n92#1:188,15\n106#1:203,15\n124#1:218,15\n137#1:233,15\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef

public suspend fun <S, T : Any> Flow<Any>.reduce(operation: (Any, Any, Continuation<Any>) -> Any?): Any {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1
   // 04: ifeq 27
   // 07: aload 2
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1
   // 0b: astore 5
   // 0d: aload 5
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 5
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1
   // 2a: dup
   // 2b: aload 2
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 5
   // 31: aload 5
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1.result Ljava/lang/Object;
   // 36: astore 4
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 6
   // 3d: aload 5
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1.label I
   // 42: tableswitch 125 0 1 22 83
   // 58: aload 4
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: new kotlin/jvm/internal/Ref$ObjectRef
   // 60: dup
   // 61: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 64: astore 3
   // 65: aload 3
   // 66: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // 69: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 6c: aload 0
   // 6d: new kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$2
   // 70: dup
   // 71: aload 3
   // 72: aload 1
   // 73: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$2.<init> (Lkotlin/jvm/internal/Ref$ObjectRef;Lkotlin/jvm/functions/Function3;)V
   // 76: checkcast kotlinx/coroutines/flow/FlowCollector
   // 79: aload 5
   // 7b: aload 5
   // 7d: aload 3
   // 7e: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1.L$0 Ljava/lang/Object;
   // 81: aload 5
   // 83: bipush 1
   // 84: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1.label I
   // 87: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 8c: dup
   // 8d: aload 6
   // 8f: if_acmpne a5
   // 92: aload 6
   // 94: areturn
   // 95: aload 5
   // 97: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$reduce$1.L$0 Ljava/lang/Object;
   // 9a: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // 9d: astore 3
   // 9e: aload 4
   // a0: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // a3: aload 4
   // a5: pop
   // a6: aload 3
   // a7: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // aa: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // ad: if_acmpne ba
   // b0: new java/util/NoSuchElementException
   // b3: dup
   // b4: ldc "Empty flow can't be reduced"
   // b6: invokespecial java/util/NoSuchElementException.<init> (Ljava/lang/String;)V
   // b9: athrow
   // ba: aload 3
   // bb: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // be: areturn
   // bf: new java/lang/IllegalStateException
   // c2: dup
   // c3: ldc "call to 'resume' before 'invoke' with coroutine"
   // c5: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // c8: athrow
}

public suspend inline fun <T, R> Flow<Any>.fold(initial: Any, crossinline operation: (Any, Any, Continuation<Any>) -> Any?): Any {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1
   // 04: ifeq 27
   // 07: aload 3
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1
   // 0b: astore 7
   // 0d: aload 7
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 7
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1
   // 2a: dup
   // 2b: aload 3
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 7
   // 31: aload 7
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1.result Ljava/lang/Object;
   // 36: astore 6
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 8
   // 3d: aload 7
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1.label I
   // 42: tableswitch 115 0 1 22 88
   // 58: aload 6
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: bipush 0
   // 5e: istore 4
   // 60: new kotlin/jvm/internal/Ref$ObjectRef
   // 63: dup
   // 64: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 67: astore 5
   // 69: aload 5
   // 6b: aload 1
   // 6c: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 6f: aload 0
   // 70: new kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2
   // 73: dup
   // 74: aload 5
   // 76: aload 2
   // 77: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2.<init> (Lkotlin/jvm/internal/Ref$ObjectRef;Lkotlin/jvm/functions/Function3;)V
   // 7a: checkcast kotlinx/coroutines/flow/FlowCollector
   // 7d: aload 7
   // 7f: aload 7
   // 81: aload 5
   // 83: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1.L$0 Ljava/lang/Object;
   // 86: aload 7
   // 88: bipush 1
   // 89: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1.label I
   // 8c: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 91: dup
   // 92: aload 8
   // 94: if_acmpne ae
   // 97: aload 8
   // 99: areturn
   // 9a: bipush 0
   // 9b: istore 4
   // 9d: aload 7
   // 9f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$1.L$0 Ljava/lang/Object;
   // a2: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // a5: astore 5
   // a7: aload 6
   // a9: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // ac: aload 6
   // ae: pop
   // af: aload 5
   // b1: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // b4: areturn
   // b5: new java/lang/IllegalStateException
   // b8: dup
   // b9: ldc "call to 'resume' before 'invoke' with coroutine"
   // bb: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // be: athrow
}

fun <T, R> Flow<out T>.`fold$$forInline`(initial: R, operation: (R?, T?, Continuation<in R>?) -> Any, `$completion`: Continuation<in R>) {
   val accumulator: ObjectRef = ObjectRef()
   accumulator.element = initial
   val var10001: FlowCollector = { value: Any ->
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
      // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1
      // 04: ifeq 27
      // 07: aload 2
      // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1
      // 0b: astore 5
      // 0d: aload 5
      // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1.label I
      // 12: ldc -2147483648
      // 14: iand
      // 15: ifeq 27
      // 18: aload 5
      // 1a: dup
      // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1.label I
      // 1e: ldc -2147483648
      // 20: isub
      // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1.label I
      // 24: goto 32
      // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1
      // 2a: dup
      // 2b: aload 0
      // 2c: aload 2
      // 2d: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2;Lkotlin/coroutines/Continuation;)V
      // 30: astore 5
      // 32: aload 5
      // 34: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1.result Ljava/lang/Object;
      // 37: astore 4
      // 39: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 3c: astore 6
      // 3e: aload 5
      // 40: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1.label I
      // 43: tableswitch 96 0 1 21 71
      // 58: aload 4
      // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 5d: aload 0
      // 5e: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2.$accumulator Lkotlin/jvm/internal/Ref$ObjectRef;
      // 61: astore 3
      // 62: aload 0
      // 63: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2.$operation Lkotlin/jvm/functions/Function3;
      // 66: aload 0
      // 67: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2.$accumulator Lkotlin/jvm/internal/Ref$ObjectRef;
      // 6a: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 6d: aload 1
      // 6e: aload 5
      // 70: aload 5
      // 72: aload 3
      // 73: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1.L$0 Ljava/lang/Object;
      // 76: aload 5
      // 78: bipush 1
      // 79: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1.label I
      // 7c: invokeinterface kotlin/jvm/functions/Function3.invoke (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 4
      // 81: dup
      // 82: aload 6
      // 84: if_acmpne 9a
      // 87: aload 6
      // 89: areturn
      // 8a: aload 5
      // 8c: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$fold$2$emit$1.L$0 Ljava/lang/Object;
      // 8f: checkcast kotlin/jvm/internal/Ref$ObjectRef
      // 92: astore 3
      // 93: aload 4
      // 95: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 98: aload 4
      // 9a: aload 3
      // 9b: swap
      // 9c: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 9f: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // a2: areturn
      // a3: new java/lang/IllegalStateException
      // a6: dup
      // a7: ldc "call to 'resume' before 'invoke' with coroutine"
      // a9: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // ac: athrow
   }
   InlineMarker.mark(0)
   `$this$fold`.collect(var10001, `$completion`)
   InlineMarker.mark(1)
   accumulator.element
}

public suspend fun <T> Flow<Any>.single(): Any {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1
   // 0b: astore 4
   // 0d: aload 4
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 4
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 4
   // 31: aload 4
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1.result Ljava/lang/Object;
   // 36: astore 3
   // 37: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3a: astore 5
   // 3c: aload 4
   // 3e: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1.label I
   // 41: tableswitch 122 0 1 23 82
   // 58: aload 3
   // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5c: new kotlin/jvm/internal/Ref$ObjectRef
   // 5f: dup
   // 60: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 63: astore 2
   // 64: aload 2
   // 65: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // 68: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 6b: aload 0
   // 6c: new kotlinx/coroutines/flow/FlowKt__ReduceKt$single$2
   // 6f: dup
   // 70: aload 2
   // 71: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$single$2.<init> (Lkotlin/jvm/internal/Ref$ObjectRef;)V
   // 74: checkcast kotlinx/coroutines/flow/FlowCollector
   // 77: aload 4
   // 79: aload 4
   // 7b: aload 2
   // 7c: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1.L$0 Ljava/lang/Object;
   // 7f: aload 4
   // 81: bipush 1
   // 82: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1.label I
   // 85: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 8a: dup
   // 8b: aload 5
   // 8d: if_acmpne a1
   // 90: aload 5
   // 92: areturn
   // 93: aload 4
   // 95: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$single$1.L$0 Ljava/lang/Object;
   // 98: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // 9b: astore 2
   // 9c: aload 3
   // 9d: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // a0: aload 3
   // a1: pop
   // a2: aload 2
   // a3: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // a6: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // a9: if_acmpne b6
   // ac: new java/util/NoSuchElementException
   // af: dup
   // b0: ldc "Flow is empty"
   // b2: invokespecial java/util/NoSuchElementException.<init> (Ljava/lang/String;)V
   // b5: athrow
   // b6: aload 2
   // b7: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // ba: areturn
   // bb: new java/lang/IllegalStateException
   // be: dup
   // bf: ldc "call to 'resume' before 'invoke' with coroutine"
   // c1: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // c4: athrow
}

public suspend fun <T> Flow<Any>.singleOrNull(): Any? {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1
   // 0b: astore 8
   // 0d: aload 8
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 8
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 8
   // 31: aload 8
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.result Ljava/lang/Object;
   // 36: astore 7
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 9
   // 3d: aload 8
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.label I
   // 42: tableswitch 165 0 1 22 99
   // 58: aload 7
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: new kotlin/jvm/internal/Ref$ObjectRef
   // 60: dup
   // 61: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 64: astore 2
   // 65: aload 2
   // 66: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // 69: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 6c: aload 0
   // 6d: astore 3
   // 6e: bipush 0
   // 6f: istore 4
   // 71: new kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$$inlined$collectWhile$1
   // 74: dup
   // 75: aload 2
   // 76: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$$inlined$collectWhile$1.<init> (Lkotlin/jvm/internal/Ref$ObjectRef;)V
   // 79: astore 5
   // 7b: nop
   // 7c: aload 3
   // 7d: aload 5
   // 7f: checkcast kotlinx/coroutines/flow/FlowCollector
   // 82: aload 8
   // 84: aload 8
   // 86: aload 2
   // 87: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.L$0 Ljava/lang/Object;
   // 8a: aload 8
   // 8c: aload 5
   // 8e: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.L$1 Ljava/lang/Object;
   // 91: aload 8
   // 93: bipush 1
   // 94: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.label I
   // 97: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 9c: dup
   // 9d: aload 9
   // 9f: if_acmpne c3
   // a2: aload 9
   // a4: areturn
   // a5: bipush 0
   // a6: istore 4
   // a8: aload 8
   // aa: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.L$1 Ljava/lang/Object;
   // ad: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$$inlined$collectWhile$1
   // b0: astore 5
   // b2: aload 8
   // b4: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$singleOrNull$1.L$0 Ljava/lang/Object;
   // b7: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // ba: astore 2
   // bb: nop
   // bc: aload 7
   // be: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // c1: aload 7
   // c3: pop
   // c4: goto d3
   // c7: astore 6
   // c9: aload 6
   // cb: aload 5
   // cd: checkcast kotlinx/coroutines/flow/FlowCollector
   // d0: invokestatic kotlinx/coroutines/flow/internal/FlowExceptions_commonKt.checkOwnership (Lkotlinx/coroutines/flow/internal/AbortFlowException;Lkotlinx/coroutines/flow/FlowCollector;)V
   // d3: nop
   // d4: aload 2
   // d5: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // d8: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // db: if_acmpne e2
   // de: aconst_null
   // df: goto e6
   // e2: aload 2
   // e3: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // e6: areturn
   // e7: new java/lang/IllegalStateException
   // ea: dup
   // eb: ldc "call to 'resume' before 'invoke' with coroutine"
   // ed: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // f0: athrow
}

public suspend fun <T> Flow<Any>.first(): Any {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1
   // 0b: astore 8
   // 0d: aload 8
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 8
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 8
   // 31: aload 8
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.result Ljava/lang/Object;
   // 36: astore 7
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 9
   // 3d: aload 8
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.label I
   // 42: tableswitch 171 0 1 22 99
   // 58: aload 7
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: new kotlin/jvm/internal/Ref$ObjectRef
   // 60: dup
   // 61: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 64: astore 2
   // 65: aload 2
   // 66: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // 69: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 6c: aload 0
   // 6d: astore 3
   // 6e: bipush 0
   // 6f: istore 4
   // 71: new kotlinx/coroutines/flow/FlowKt__ReduceKt$first$$inlined$collectWhile$1
   // 74: dup
   // 75: aload 2
   // 76: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$first$$inlined$collectWhile$1.<init> (Lkotlin/jvm/internal/Ref$ObjectRef;)V
   // 79: astore 5
   // 7b: nop
   // 7c: aload 3
   // 7d: aload 5
   // 7f: checkcast kotlinx/coroutines/flow/FlowCollector
   // 82: aload 8
   // 84: aload 8
   // 86: aload 2
   // 87: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.L$0 Ljava/lang/Object;
   // 8a: aload 8
   // 8c: aload 5
   // 8e: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.L$1 Ljava/lang/Object;
   // 91: aload 8
   // 93: bipush 1
   // 94: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.label I
   // 97: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 9c: dup
   // 9d: aload 9
   // 9f: if_acmpne c3
   // a2: aload 9
   // a4: areturn
   // a5: bipush 0
   // a6: istore 4
   // a8: aload 8
   // aa: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.L$1 Ljava/lang/Object;
   // ad: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$first$$inlined$collectWhile$1
   // b0: astore 5
   // b2: aload 8
   // b4: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$1.L$0 Ljava/lang/Object;
   // b7: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // ba: astore 2
   // bb: nop
   // bc: aload 7
   // be: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // c1: aload 7
   // c3: pop
   // c4: goto d3
   // c7: astore 6
   // c9: aload 6
   // cb: aload 5
   // cd: checkcast kotlinx/coroutines/flow/FlowCollector
   // d0: invokestatic kotlinx/coroutines/flow/internal/FlowExceptions_commonKt.checkOwnership (Lkotlinx/coroutines/flow/internal/AbortFlowException;Lkotlinx/coroutines/flow/FlowCollector;)V
   // d3: nop
   // d4: aload 2
   // d5: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // d8: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // db: if_acmpne e8
   // de: new java/util/NoSuchElementException
   // e1: dup
   // e2: ldc "Expected at least one element"
   // e4: invokespecial java/util/NoSuchElementException.<init> (Ljava/lang/String;)V
   // e7: athrow
   // e8: aload 2
   // e9: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // ec: areturn
   // ed: new java/lang/IllegalStateException
   // f0: dup
   // f1: ldc "call to 'resume' before 'invoke' with coroutine"
   // f3: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // f6: athrow
}

public suspend fun <T> Flow<Any>.first(predicate: (Any, Continuation<Boolean>) -> Any?): Any {
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
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3
   // 00b: astore 9
   // 00d: aload 9
   // 00f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 9
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 9
   // 031: aload 9
   // 033: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.result Ljava/lang/Object;
   // 036: astore 8
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 10
   // 03d: aload 9
   // 03f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.label I
   // 042: tableswitch 206 0 1 22 108
   // 058: aload 8
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: new kotlin/jvm/internal/Ref$ObjectRef
   // 060: dup
   // 061: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 064: astore 3
   // 065: aload 3
   // 066: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // 069: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 06c: aload 0
   // 06d: astore 4
   // 06f: bipush 0
   // 070: istore 5
   // 072: new kotlinx/coroutines/flow/FlowKt__ReduceKt$first$$inlined$collectWhile$2
   // 075: dup
   // 076: aload 1
   // 077: aload 3
   // 078: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$first$$inlined$collectWhile$2.<init> (Lkotlin/jvm/functions/Function2;Lkotlin/jvm/internal/Ref$ObjectRef;)V
   // 07b: astore 6
   // 07d: nop
   // 07e: aload 4
   // 080: aload 6
   // 082: checkcast kotlinx/coroutines/flow/FlowCollector
   // 085: aload 9
   // 087: aload 9
   // 089: aload 1
   // 08a: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.L$0 Ljava/lang/Object;
   // 08d: aload 9
   // 08f: aload 3
   // 090: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.L$1 Ljava/lang/Object;
   // 093: aload 9
   // 095: aload 6
   // 097: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.L$2 Ljava/lang/Object;
   // 09a: aload 9
   // 09c: bipush 1
   // 09d: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.label I
   // 0a0: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 0a5: dup
   // 0a6: aload 10
   // 0a8: if_acmpne 0d5
   // 0ab: aload 10
   // 0ad: areturn
   // 0ae: bipush 0
   // 0af: istore 5
   // 0b1: aload 9
   // 0b3: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.L$2 Ljava/lang/Object;
   // 0b6: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$first$$inlined$collectWhile$2
   // 0b9: astore 6
   // 0bb: aload 9
   // 0bd: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.L$1 Ljava/lang/Object;
   // 0c0: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // 0c3: astore 3
   // 0c4: aload 9
   // 0c6: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$first$3.L$0 Ljava/lang/Object;
   // 0c9: checkcast kotlin/jvm/functions/Function2
   // 0cc: astore 1
   // 0cd: nop
   // 0ce: aload 8
   // 0d0: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0d3: aload 8
   // 0d5: pop
   // 0d6: goto 0e5
   // 0d9: astore 7
   // 0db: aload 7
   // 0dd: aload 6
   // 0df: checkcast kotlinx/coroutines/flow/FlowCollector
   // 0e2: invokestatic kotlinx/coroutines/flow/internal/FlowExceptions_commonKt.checkOwnership (Lkotlinx/coroutines/flow/internal/AbortFlowException;Lkotlinx/coroutines/flow/FlowCollector;)V
   // 0e5: nop
   // 0e6: aload 3
   // 0e7: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 0ea: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // 0ed: if_acmpne 10b
   // 0f0: new java/util/NoSuchElementException
   // 0f3: dup
   // 0f4: new java/lang/StringBuilder
   // 0f7: dup
   // 0f8: invokespecial java/lang/StringBuilder.<init> ()V
   // 0fb: ldc "Expected at least one element matching the predicate "
   // 0fd: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
   // 100: aload 1
   // 101: invokevirtual java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
   // 104: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
   // 107: invokespecial java/util/NoSuchElementException.<init> (Ljava/lang/String;)V
   // 10a: athrow
   // 10b: aload 3
   // 10c: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 10f: areturn
   // 110: new java/lang/IllegalStateException
   // 113: dup
   // 114: ldc "call to 'resume' before 'invoke' with coroutine"
   // 116: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 119: athrow
}

public suspend fun <T> Flow<Any>.firstOrNull(): Any? {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1
   // 0b: astore 8
   // 0d: aload 8
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 8
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 8
   // 31: aload 8
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.result Ljava/lang/Object;
   // 36: astore 7
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 9
   // 3d: aload 8
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.label I
   // 42: tableswitch 144 0 1 22 92
   // 58: aload 7
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: new kotlin/jvm/internal/Ref$ObjectRef
   // 60: dup
   // 61: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 64: astore 2
   // 65: aload 0
   // 66: astore 3
   // 67: bipush 0
   // 68: istore 4
   // 6a: new kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$1
   // 6d: dup
   // 6e: aload 2
   // 6f: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$1.<init> (Lkotlin/jvm/internal/Ref$ObjectRef;)V
   // 72: astore 5
   // 74: nop
   // 75: aload 3
   // 76: aload 5
   // 78: checkcast kotlinx/coroutines/flow/FlowCollector
   // 7b: aload 8
   // 7d: aload 8
   // 7f: aload 2
   // 80: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.L$0 Ljava/lang/Object;
   // 83: aload 8
   // 85: aload 5
   // 87: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.L$1 Ljava/lang/Object;
   // 8a: aload 8
   // 8c: bipush 1
   // 8d: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.label I
   // 90: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 95: dup
   // 96: aload 9
   // 98: if_acmpne bc
   // 9b: aload 9
   // 9d: areturn
   // 9e: bipush 0
   // 9f: istore 4
   // a1: aload 8
   // a3: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.L$1 Ljava/lang/Object;
   // a6: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$1
   // a9: astore 5
   // ab: aload 8
   // ad: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$1.L$0 Ljava/lang/Object;
   // b0: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // b3: astore 2
   // b4: nop
   // b5: aload 7
   // b7: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // ba: aload 7
   // bc: pop
   // bd: goto cc
   // c0: astore 6
   // c2: aload 6
   // c4: aload 5
   // c6: checkcast kotlinx/coroutines/flow/FlowCollector
   // c9: invokestatic kotlinx/coroutines/flow/internal/FlowExceptions_commonKt.checkOwnership (Lkotlinx/coroutines/flow/internal/AbortFlowException;Lkotlinx/coroutines/flow/FlowCollector;)V
   // cc: nop
   // cd: aload 2
   // ce: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // d1: areturn
   // d2: new java/lang/IllegalStateException
   // d5: dup
   // d6: ldc "call to 'resume' before 'invoke' with coroutine"
   // d8: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // db: athrow
}

public suspend fun <T> Flow<Any>.firstOrNull(predicate: (Any, Continuation<Boolean>) -> Any?): Any? {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3
   // 04: ifeq 27
   // 07: aload 2
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3
   // 0b: astore 9
   // 0d: aload 9
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 9
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3
   // 2a: dup
   // 2b: aload 2
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 9
   // 31: aload 9
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.result Ljava/lang/Object;
   // 36: astore 8
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 10
   // 3d: aload 9
   // 3f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.label I
   // 42: tableswitch 147 0 1 22 95
   // 58: aload 8
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: new kotlin/jvm/internal/Ref$ObjectRef
   // 60: dup
   // 61: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 64: astore 3
   // 65: aload 0
   // 66: astore 4
   // 68: bipush 0
   // 69: istore 5
   // 6b: new kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2
   // 6e: dup
   // 6f: aload 1
   // 70: aload 3
   // 71: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2.<init> (Lkotlin/jvm/functions/Function2;Lkotlin/jvm/internal/Ref$ObjectRef;)V
   // 74: astore 6
   // 76: nop
   // 77: aload 4
   // 79: aload 6
   // 7b: checkcast kotlinx/coroutines/flow/FlowCollector
   // 7e: aload 9
   // 80: aload 9
   // 82: aload 3
   // 83: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.L$0 Ljava/lang/Object;
   // 86: aload 9
   // 88: aload 6
   // 8a: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.L$1 Ljava/lang/Object;
   // 8d: aload 9
   // 8f: bipush 1
   // 90: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.label I
   // 93: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 98: dup
   // 99: aload 10
   // 9b: if_acmpne bf
   // 9e: aload 10
   // a0: areturn
   // a1: bipush 0
   // a2: istore 5
   // a4: aload 9
   // a6: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.L$1 Ljava/lang/Object;
   // a9: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2
   // ac: astore 6
   // ae: aload 9
   // b0: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$firstOrNull$3.L$0 Ljava/lang/Object;
   // b3: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // b6: astore 3
   // b7: nop
   // b8: aload 8
   // ba: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // bd: aload 8
   // bf: pop
   // c0: goto cf
   // c3: astore 7
   // c5: aload 7
   // c7: aload 6
   // c9: checkcast kotlinx/coroutines/flow/FlowCollector
   // cc: invokestatic kotlinx/coroutines/flow/internal/FlowExceptions_commonKt.checkOwnership (Lkotlinx/coroutines/flow/internal/AbortFlowException;Lkotlinx/coroutines/flow/FlowCollector;)V
   // cf: nop
   // d0: aload 3
   // d1: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // d4: areturn
   // d5: new java/lang/IllegalStateException
   // d8: dup
   // d9: ldc "call to 'resume' before 'invoke' with coroutine"
   // db: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // de: athrow
}

public suspend fun <T> Flow<Any>.last(): Any {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1
   // 0b: astore 4
   // 0d: aload 4
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 4
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 4
   // 31: aload 4
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1.result Ljava/lang/Object;
   // 36: astore 3
   // 37: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3a: astore 5
   // 3c: aload 4
   // 3e: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1.label I
   // 41: tableswitch 122 0 1 23 82
   // 58: aload 3
   // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5c: new kotlin/jvm/internal/Ref$ObjectRef
   // 5f: dup
   // 60: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 63: astore 2
   // 64: aload 2
   // 65: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // 68: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 6b: aload 0
   // 6c: new kotlinx/coroutines/flow/FlowKt__ReduceKt$last$2
   // 6f: dup
   // 70: aload 2
   // 71: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$last$2.<init> (Lkotlin/jvm/internal/Ref$ObjectRef;)V
   // 74: checkcast kotlinx/coroutines/flow/FlowCollector
   // 77: aload 4
   // 79: aload 4
   // 7b: aload 2
   // 7c: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1.L$0 Ljava/lang/Object;
   // 7f: aload 4
   // 81: bipush 1
   // 82: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1.label I
   // 85: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 8a: dup
   // 8b: aload 5
   // 8d: if_acmpne a1
   // 90: aload 5
   // 92: areturn
   // 93: aload 4
   // 95: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$last$1.L$0 Ljava/lang/Object;
   // 98: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // 9b: astore 2
   // 9c: aload 3
   // 9d: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // a0: aload 3
   // a1: pop
   // a2: aload 2
   // a3: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // a6: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
   // a9: if_acmpne b6
   // ac: new java/util/NoSuchElementException
   // af: dup
   // b0: ldc "Expected at least one element"
   // b2: invokespecial java/util/NoSuchElementException.<init> (Ljava/lang/String;)V
   // b5: athrow
   // b6: aload 2
   // b7: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // ba: areturn
   // bb: new java/lang/IllegalStateException
   // be: dup
   // bf: ldc "call to 'resume' before 'invoke' with coroutine"
   // c1: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // c4: athrow
}

public suspend fun <T> Flow<Any>.lastOrNull(): Any? {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1
   // 0b: astore 4
   // 0d: aload 4
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 4
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 4
   // 31: aload 4
   // 33: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1.result Ljava/lang/Object;
   // 36: astore 3
   // 37: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3a: astore 5
   // 3c: aload 4
   // 3e: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1.label I
   // 41: tableswitch 95 0 1 23 75
   // 58: aload 3
   // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5c: new kotlin/jvm/internal/Ref$ObjectRef
   // 5f: dup
   // 60: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
   // 63: astore 2
   // 64: aload 0
   // 65: new kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$2
   // 68: dup
   // 69: aload 2
   // 6a: invokespecial kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$2.<init> (Lkotlin/jvm/internal/Ref$ObjectRef;)V
   // 6d: checkcast kotlinx/coroutines/flow/FlowCollector
   // 70: aload 4
   // 72: aload 4
   // 74: aload 2
   // 75: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1.L$0 Ljava/lang/Object;
   // 78: aload 4
   // 7a: bipush 1
   // 7b: putfield kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1.label I
   // 7e: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 83: dup
   // 84: aload 5
   // 86: if_acmpne 9a
   // 89: aload 5
   // 8b: areturn
   // 8c: aload 4
   // 8e: getfield kotlinx/coroutines/flow/FlowKt__ReduceKt$lastOrNull$1.L$0 Ljava/lang/Object;
   // 91: checkcast kotlin/jvm/internal/Ref$ObjectRef
   // 94: astore 2
   // 95: aload 3
   // 96: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 99: aload 3
   // 9a: pop
   // 9b: aload 2
   // 9c: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
   // 9f: areturn
   // a0: new java/lang/IllegalStateException
   // a3: dup
   // a4: ldc "call to 'resume' before 'invoke' with coroutine"
   // a6: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // a9: athrow
}
