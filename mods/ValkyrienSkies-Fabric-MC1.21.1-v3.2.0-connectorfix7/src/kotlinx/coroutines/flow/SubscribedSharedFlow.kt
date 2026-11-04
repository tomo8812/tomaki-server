package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation

private class SubscribedSharedFlow<T>(sharedFlow: SharedFlow<Any>, action: (FlowCollector<Any>, Continuation<Unit>) -> Any?) : SharedFlow<T> {
   private final val sharedFlow: SharedFlow<Any>
   private final val action: (FlowCollector<Any>, Continuation<Unit>) -> Any?

   init {
      this.sharedFlow = sharedFlow
      this.action = action
   }

   public open val replayCache: List<Any>

   public override suspend fun collect(collector: FlowCollector<Any>): Nothing {
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
      // 01: instanceof kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1
      // 04: ifeq 27
      // 07: aload 2
      // 08: checkcast kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1
      // 0b: astore 4
      // 0d: aload 4
      // 0f: getfield kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1.label I
      // 12: ldc -2147483648
      // 14: iand
      // 15: ifeq 27
      // 18: aload 4
      // 1a: dup
      // 1b: getfield kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1.label I
      // 1e: ldc -2147483648
      // 20: isub
      // 21: putfield kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1.label I
      // 24: goto 32
      // 27: new kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1
      // 2a: dup
      // 2b: aload 0
      // 2c: aload 2
      // 2d: invokespecial kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1.<init> (Lkotlinx/coroutines/flow/SubscribedSharedFlow;Lkotlin/coroutines/Continuation;)V
      // 30: astore 4
      // 32: aload 4
      // 34: getfield kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1.result Ljava/lang/Object;
      // 37: astore 3
      // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 3b: astore 5
      // 3d: aload 4
      // 3f: getfield kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1.label I
      // 42: tableswitch 81 0 1 22 67
      // 58: aload 3
      // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 5c: aload 0
      // 5d: getfield kotlinx/coroutines/flow/SubscribedSharedFlow.sharedFlow Lkotlinx/coroutines/flow/SharedFlow;
      // 60: new kotlinx/coroutines/flow/SubscribedFlowCollector
      // 63: dup
      // 64: aload 1
      // 65: aload 0
      // 66: getfield kotlinx/coroutines/flow/SubscribedSharedFlow.action Lkotlin/jvm/functions/Function2;
      // 69: invokespecial kotlinx/coroutines/flow/SubscribedFlowCollector.<init> (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/jvm/functions/Function2;)V
      // 6c: checkcast kotlinx/coroutines/flow/FlowCollector
      // 6f: aload 4
      // 71: aload 4
      // 73: bipush 1
      // 74: putfield kotlinx/coroutines/flow/SubscribedSharedFlow$collect$1.label I
      // 77: invokeinterface kotlinx/coroutines/flow/SharedFlow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
      // 7c: dup
      // 7d: aload 5
      // 7f: if_acmpne 8a
      // 82: aload 5
      // 84: areturn
      // 85: aload 3
      // 86: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 89: aload 3
      // 8a: pop
      // 8b: new kotlin/KotlinNothingValueException
      // 8e: dup
      // 8f: invokespecial kotlin/KotlinNothingValueException.<init> ()V
      // 92: athrow
      // 93: new java/lang/IllegalStateException
      // 96: dup
      // 97: ldc "call to 'resume' before 'invoke' with coroutine"
      // 99: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 9c: athrow
   }
}
