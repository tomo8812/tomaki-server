package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

private class CancellableFlowImpl<T>(flow: Flow<Any>) : CancellableFlow<T> {
   private final val flow: Flow<Any>

   init {
      this.flow = flow
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val var10000: Any = this.flow
         .collect(
            { it: Any ->
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
               // 01: instanceof kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1
               // 04: ifeq 27
               // 07: aload 2
               // 08: checkcast kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1
               // 0b: astore 5
               // 0d: aload 5
               // 0f: getfield kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1.label I
               // 12: ldc -2147483648
               // 14: iand
               // 15: ifeq 27
               // 18: aload 5
               // 1a: dup
               // 1b: getfield kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1.label I
               // 1e: ldc -2147483648
               // 20: isub
               // 21: putfield kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1.label I
               // 24: goto 32
               // 27: new kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1
               // 2a: dup
               // 2b: aload 0
               // 2c: aload 2
               // 2d: invokespecial kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1.<init> (Lkotlinx/coroutines/flow/CancellableFlowImpl$collect$2;Lkotlin/coroutines/Continuation;)V
               // 30: astore 5
               // 32: aload 5
               // 34: getfield kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1.result Ljava/lang/Object;
               // 37: astore 4
               // 39: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
               // 3c: astore 6
               // 3e: aload 5
               // 40: getfield kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1.label I
               // 43: tableswitch 78 0 1 21 66
               // 58: aload 4
               // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 5d: bipush 0
               // 5e: istore 3
               // 5f: aload 5
               // 61: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
               // 66: nop
               // 67: invokestatic kotlinx/coroutines/JobKt.ensureActive (Lkotlin/coroutines/CoroutineContext;)V
               // 6a: aload 0
               // 6b: getfield kotlinx/coroutines/flow/CancellableFlowImpl$collect$2.$collector Lkotlinx/coroutines/flow/FlowCollector;
               // 6e: aload 1
               // 6f: aload 5
               // 71: aload 5
               // 73: bipush 1
               // 74: putfield kotlinx/coroutines/flow/CancellableFlowImpl$collect$2$emit$1.label I
               // 77: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
               // 7c: dup
               // 7d: aload 6
               // 7f: if_acmpne 8c
               // 82: aload 6
               // 84: areturn
               // 85: aload 4
               // 87: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 8a: aload 4
               // 8c: pop
               // 8d: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 90: areturn
               // 91: new java/lang/IllegalStateException
               // 94: dup
               // 95: ldc "call to 'resume' before 'invoke' with coroutine"
               // 97: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // 9a: athrow
            },
            `$completion`
         )
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
