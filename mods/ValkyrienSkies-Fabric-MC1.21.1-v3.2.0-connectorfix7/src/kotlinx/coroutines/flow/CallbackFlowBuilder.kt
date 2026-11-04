package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.internal.ChannelFlow

private class CallbackFlowBuilder<T>(block: (ProducerScope<Any>, Continuation<Unit>) -> Any?,
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = -2,
   onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) : ChannelFlowBuilder(block, context, capacity, onBufferOverflow) {
   private final val block: (ProducerScope<Any>, Continuation<Unit>) -> Any?

   init {
      this.block = block
   }

   protected override suspend fun collectTo(scope: ProducerScope<Any>) {
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
      // 01: instanceof kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1
      // 04: ifeq 27
      // 07: aload 2
      // 08: checkcast kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1
      // 0b: astore 4
      // 0d: aload 4
      // 0f: getfield kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1.label I
      // 12: ldc -2147483648
      // 14: iand
      // 15: ifeq 27
      // 18: aload 4
      // 1a: dup
      // 1b: getfield kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1.label I
      // 1e: ldc -2147483648
      // 20: isub
      // 21: putfield kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1.label I
      // 24: goto 32
      // 27: new kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1
      // 2a: dup
      // 2b: aload 0
      // 2c: aload 2
      // 2d: invokespecial kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1.<init> (Lkotlinx/coroutines/flow/CallbackFlowBuilder;Lkotlin/coroutines/Continuation;)V
      // 30: astore 4
      // 32: aload 4
      // 34: getfield kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1.result Ljava/lang/Object;
      // 37: astore 3
      // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 3b: astore 5
      // 3d: aload 4
      // 3f: getfield kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1.label I
      // 42: tableswitch 92 0 1 22 54
      // 58: aload 3
      // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 5c: aload 0
      // 5d: aload 1
      // 5e: aload 4
      // 60: aload 4
      // 62: aload 1
      // 63: putfield kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1.L$0 Ljava/lang/Object;
      // 66: aload 4
      // 68: bipush 1
      // 69: putfield kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1.label I
      // 6c: invokespecial kotlinx/coroutines/flow/ChannelFlowBuilder.collectTo (Lkotlinx/coroutines/channels/ProducerScope;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 6f: dup
      // 70: aload 5
      // 72: if_acmpne 86
      // 75: aload 5
      // 77: areturn
      // 78: aload 4
      // 7a: getfield kotlinx/coroutines/flow/CallbackFlowBuilder$collectTo$1.L$0 Ljava/lang/Object;
      // 7d: checkcast kotlinx/coroutines/channels/ProducerScope
      // 80: astore 1
      // 81: aload 3
      // 82: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 85: aload 3
      // 86: pop
      // 87: aload 1
      // 88: invokeinterface kotlinx/coroutines/channels/ProducerScope.isClosedForSend ()Z 1
      // 8d: ifne 9a
      // 90: new java/lang/IllegalStateException
      // 93: dup
      // 94: ldc "'awaitClose { yourCallbackOrListener.cancel() }' should be used in the end of callbackFlow block.\nOtherwise, a callback/listener may leak in case of external cancellation.\nSee callbackFlow API documentation for the details."
      // 96: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 99: athrow
      // 9a: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 9d: areturn
      // 9e: new java/lang/IllegalStateException
      // a1: dup
      // a2: ldc "call to 'resume' before 'invoke' with coroutine"
      // a4: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // a7: athrow
   }

   protected override fun create(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): ChannelFlow<Any> {
      return CallbackFlowBuilder<>(this.block, context, capacity, onBufferOverflow)
   }
}
