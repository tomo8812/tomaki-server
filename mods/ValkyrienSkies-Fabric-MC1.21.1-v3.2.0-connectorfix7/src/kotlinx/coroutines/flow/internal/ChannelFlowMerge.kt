package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ProduceKt
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.SemaphoreKt

internal class ChannelFlowMerge<T>(flow: Flow<Flow<Any>>,
   concurrency: Int,
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = -2,
   onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) : ChannelFlow(context, capacity, onBufferOverflow) {
   private final val flow: Flow<Flow<Any>>
   private final val concurrency: Int

   init {
      this.flow = flow
      this.concurrency = concurrency
   }

   protected override fun create(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): ChannelFlow<Any> {
      return ChannelFlowMerge<>(this.flow, this.concurrency, context, capacity, onBufferOverflow)
   }

   public override fun produceImpl(scope: CoroutineScope): ReceiveChannel<Any> {
      return ProduceKt.produce(scope, this.context, this.capacity, this.getCollectToFun$kotlinx_coroutines_core())
   }

   protected override suspend fun collectTo(scope: ProducerScope<Any>) {
      val semaphore: Semaphore = SemaphoreKt.Semaphore$default(this.concurrency, 0, 2, null)
      val collector: SendingCollector = SendingCollector(scope)
      val job: Job = `$completion`.getContext().get(Job.Key) as Job
      val var10000: Any = this.flow
         .collect(
            { inner: Flow<Any> ->
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
               // 01: instanceof kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1
               // 04: ifeq 27
               // 07: aload 2
               // 08: checkcast kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1
               // 0b: astore 4
               // 0d: aload 4
               // 0f: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.label I
               // 12: ldc -2147483648
               // 14: iand
               // 15: ifeq 27
               // 18: aload 4
               // 1a: dup
               // 1b: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.label I
               // 1e: ldc -2147483648
               // 20: isub
               // 21: putfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.label I
               // 24: goto 32
               // 27: new kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1
               // 2a: dup
               // 2b: aload 0
               // 2c: aload 2
               // 2d: invokespecial kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.<init> (Lkotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2;Lkotlin/coroutines/Continuation;)V
               // 30: astore 4
               // 32: aload 4
               // 34: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.result Ljava/lang/Object;
               // 37: astore 3
               // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
               // 3b: astore 5
               // 3d: aload 4
               // 3f: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.label I
               // 42: tableswitch 142 0 1 22 79
               // 58: aload 3
               // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 5c: aload 0
               // 5d: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2.$job Lkotlinx/coroutines/Job;
               // 60: dup
               // 61: ifnull 6a
               // 64: invokestatic kotlinx/coroutines/JobKt.ensureActive (Lkotlinx/coroutines/Job;)V
               // 67: goto 6b
               // 6a: pop
               // 6b: aload 0
               // 6c: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2.$semaphore Lkotlinx/coroutines/sync/Semaphore;
               // 6f: aload 4
               // 71: aload 4
               // 73: aload 0
               // 74: putfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.L$0 Ljava/lang/Object;
               // 77: aload 4
               // 79: aload 1
               // 7a: putfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.L$1 Ljava/lang/Object;
               // 7d: aload 4
               // 7f: bipush 1
               // 80: putfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.label I
               // 83: invokeinterface kotlinx/coroutines/sync/Semaphore.acquire (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
               // 88: dup
               // 89: aload 5
               // 8b: if_acmpne a8
               // 8e: aload 5
               // 90: areturn
               // 91: aload 4
               // 93: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.L$1 Ljava/lang/Object;
               // 96: checkcast kotlinx/coroutines/flow/Flow
               // 99: astore 1
               // 9a: aload 4
               // 9c: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$emit$1.L$0 Ljava/lang/Object;
               // 9f: checkcast kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2
               // a2: astore 0
               // a3: aload 3
               // a4: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // a7: aload 3
               // a8: pop
               // a9: aload 0
               // aa: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2.$scope Lkotlinx/coroutines/channels/ProducerScope;
               // ad: checkcast kotlinx/coroutines/CoroutineScope
               // b0: aconst_null
               // b1: aconst_null
               // b2: new kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$1
               // b5: dup
               // b6: aload 1
               // b7: aload 0
               // b8: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2.$collector Lkotlinx/coroutines/flow/internal/SendingCollector;
               // bb: aload 0
               // bc: getfield kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2.$semaphore Lkotlinx/coroutines/sync/Semaphore;
               // bf: aconst_null
               // c0: invokespecial kotlinx/coroutines/flow/internal/ChannelFlowMerge$collectTo$2$1.<init> (Lkotlinx/coroutines/flow/Flow;Lkotlinx/coroutines/flow/internal/SendingCollector;Lkotlinx/coroutines/sync/Semaphore;Lkotlin/coroutines/Continuation;)V
               // c3: checkcast kotlin/jvm/functions/Function2
               // c6: bipush 3
               // c7: aconst_null
               // c8: invokestatic kotlinx/coroutines/BuildersKt.launch$default (Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;ILjava/lang/Object;)Lkotlinx/coroutines/Job;
               // cb: pop
               // cc: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // cf: areturn
               // d0: new java/lang/IllegalStateException
               // d3: dup
               // d4: ldc "call to 'resume' before 'invoke' with coroutine"
               // d6: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // d9: athrow
            },
            `$completion`
         )
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   protected override fun additionalToStringProps(): String {
      return "concurrency=${this.concurrency}"
   }
}
