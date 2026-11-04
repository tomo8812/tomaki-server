package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.flow.internal.NullSurrogateKt
import org.jetbrains.annotations.NotNull

private class DistinctFlowImpl<T>(upstream: Flow<Any>, keySelector: (Any) -> Any?, areEquivalent: (Any?, Any?) -> Boolean) : Flow<T> {
   private final val upstream: Flow<Any>

   @JvmField
   @NotNull
   public final val keySelector: (Any) -> Any?

   @JvmField
   @NotNull
   public final val areEquivalent: (Any?, Any?) -> Boolean

   init {
      this.upstream = upstream
      this.keySelector = keySelector
      this.areEquivalent = areEquivalent
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val previousKey: ObjectRef = ObjectRef()
      previousKey.element = NullSurrogateKt.NULL
      val var10000: Any = this.upstream
         .collect(
            { value: Any ->
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
               // 01: instanceof kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1
               // 04: ifeq 27
               // 07: aload 2
               // 08: checkcast kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1
               // 0b: astore 5
               // 0d: aload 5
               // 0f: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1.label I
               // 12: ldc -2147483648
               // 14: iand
               // 15: ifeq 27
               // 18: aload 5
               // 1a: dup
               // 1b: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1.label I
               // 1e: ldc -2147483648
               // 20: isub
               // 21: putfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1.label I
               // 24: goto 32
               // 27: new kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1
               // 2a: dup
               // 2b: aload 0
               // 2c: aload 2
               // 2d: invokespecial kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1.<init> (Lkotlinx/coroutines/flow/DistinctFlowImpl$collect$2;Lkotlin/coroutines/Continuation;)V
               // 30: astore 5
               // 32: aload 5
               // 34: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1.result Ljava/lang/Object;
               // 37: astore 4
               // 39: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
               // 3c: astore 6
               // 3e: aload 5
               // 40: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1.label I
               // 43: tableswitch 133 0 1 21 117
               // 58: aload 4
               // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 5d: aload 0
               // 5e: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2.this$0 Lkotlinx/coroutines/flow/DistinctFlowImpl;
               // 61: getfield kotlinx/coroutines/flow/DistinctFlowImpl.keySelector Lkotlin/jvm/functions/Function1;
               // 64: aload 1
               // 65: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
               // 6a: astore 3
               // 6b: aload 0
               // 6c: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2.$previousKey Lkotlin/jvm/internal/Ref$ObjectRef;
               // 6f: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 72: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
               // 75: if_acmpeq 95
               // 78: aload 0
               // 79: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2.this$0 Lkotlinx/coroutines/flow/DistinctFlowImpl;
               // 7c: getfield kotlinx/coroutines/flow/DistinctFlowImpl.areEquivalent Lkotlin/jvm/functions/Function2;
               // 7f: aload 0
               // 80: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2.$previousKey Lkotlin/jvm/internal/Ref$ObjectRef;
               // 83: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 86: aload 3
               // 87: invokeinterface kotlin/jvm/functions/Function2.invoke (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
               // 8c: checkcast java/lang/Boolean
               // 8f: invokevirtual java/lang/Boolean.booleanValue ()Z
               // 92: ifne c4
               // 95: aload 0
               // 96: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2.$previousKey Lkotlin/jvm/internal/Ref$ObjectRef;
               // 99: aload 3
               // 9a: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 9d: aload 0
               // 9e: getfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2.$collector Lkotlinx/coroutines/flow/FlowCollector;
               // a1: aload 1
               // a2: aload 5
               // a4: aload 5
               // a6: bipush 1
               // a7: putfield kotlinx/coroutines/flow/DistinctFlowImpl$collect$2$emit$1.label I
               // aa: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
               // af: dup
               // b0: aload 6
               // b2: if_acmpne bf
               // b5: aload 6
               // b7: areturn
               // b8: aload 4
               // ba: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // bd: aload 4
               // bf: pop
               // c0: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // c3: areturn
               // c4: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // c7: areturn
               // c8: new java/lang/IllegalStateException
               // cb: dup
               // cc: ldc "call to 'resume' before 'invoke' with coroutine"
               // ce: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // d1: athrow
            },
            `$completion`
         )
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
