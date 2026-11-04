package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n*L\n1#1,113:1\n106#2,7:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__TransformKt$runningFold$$inlined$unsafeFlow$1` : Flow<R> {
   @JvmSynthetic
   Object $initial$inlined;
   @JvmSynthetic
   Flow $this_runningFold$inlined;
   @JvmSynthetic
   Function3 $operation$inlined;

   fun `FlowKt__TransformKt$runningFold$$inlined$unsafeFlow$1`(var1: Any, var2: Flow, var3: Function3) {
      this.$initial$inlined = var1
      this.$this_runningFold$inlined = var2
      this.$operation$inlined = var3
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      var `$continuation`: Continuation
      run label36@{
         if (var2 is FlowKt__TransformKt$runningFold$$inlined$unsafeFlow$1$1) {
            `$continuation` = var2 as FlowKt__TransformKt$runningFold$$inlined$unsafeFlow$1$1
            if (((var2 as FlowKt__TransformKt$runningFold$$inlined$unsafeFlow$1$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label36
            }
         }

         `$continuation` = FlowKt__TransformKt$runningFold$$inlined$unsafeFlow$1$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `$this$runningFold_u24lambda_u249`: FlowCollector
var accumulator: ObjectRef
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            `$this$runningFold_u24lambda_u249` = collector
            accumulator = ObjectRef()
            accumulator.element = this.$initial$inlined
            val var10001: Any = accumulator.element
            `$continuation`.L$0 = this
            `$continuation`.L$1 = collector
            `$continuation`.L$2 = accumulator
            `$continuation`.label = 1
            if (collector.emit(var10001, `$continuation`) === var5) {
               return var5
            }
         }
         1 -> {
            accumulator = `$continuation`.L$2 as ObjectRef
            `$this$runningFold_u24lambda_u249` = `$continuation`.L$1 as FlowCollector
            this = `$continuation`.L$0 as FlowKt__TransformKt$runningFold$$inlined$unsafeFlow$1
            ResultKt.throwOnFailure(`$result`)
         }
         2 -> {
            ResultKt.throwOnFailure(`$result`)
            return Unit.INSTANCE
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      val var10000: Flow = this.$this_runningFold$inlined
      val var11: FlowCollector = { value: Any ->
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
         // 01: instanceof kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1
         // 04: ifeq 27
         // 07: aload 2
         // 08: checkcast kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1
         // 0b: astore 5
         // 0d: aload 5
         // 0f: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.label I
         // 12: ldc -2147483648
         // 14: iand
         // 15: ifeq 27
         // 18: aload 5
         // 1a: dup
         // 1b: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.label I
         // 1e: ldc -2147483648
         // 20: isub
         // 21: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.label I
         // 24: goto 32
         // 27: new kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1
         // 2a: dup
         // 2b: aload 0
         // 2c: aload 2
         // 2d: invokespecial kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1;Lkotlin/coroutines/Continuation;)V
         // 30: astore 5
         // 32: aload 5
         // 34: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.result Ljava/lang/Object;
         // 37: astore 4
         // 39: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
         // 3c: astore 6
         // 3e: aload 5
         // 40: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.label I
         // 43: tableswitch 168 0 2 25 81 156
         // 5c: aload 4
         // 5e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
         // 61: aload 0
         // 62: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1.$accumulator Lkotlin/jvm/internal/Ref$ObjectRef;
         // 65: astore 3
         // 66: aload 0
         // 67: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1.$operation Lkotlin/jvm/functions/Function3;
         // 6a: aload 0
         // 6b: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1.$accumulator Lkotlin/jvm/internal/Ref$ObjectRef;
         // 6e: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
         // 71: aload 1
         // 72: aload 5
         // 74: aload 5
         // 76: aload 0
         // 77: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.L$0 Ljava/lang/Object;
         // 7a: aload 5
         // 7c: aload 3
         // 7d: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.L$1 Ljava/lang/Object;
         // 80: aload 5
         // 82: bipush 1
         // 83: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.label I
         // 86: invokeinterface kotlin/jvm/functions/Function3.invoke (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 4
         // 8b: dup
         // 8c: aload 6
         // 8e: if_acmpne ad
         // 91: aload 6
         // 93: areturn
         // 94: aload 5
         // 96: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.L$1 Ljava/lang/Object;
         // 99: checkcast kotlin/jvm/internal/Ref$ObjectRef
         // 9c: astore 3
         // 9d: aload 5
         // 9f: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.L$0 Ljava/lang/Object;
         // a2: checkcast kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1
         // a5: astore 0
         // a6: aload 4
         // a8: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
         // ab: aload 4
         // ad: aload 3
         // ae: swap
         // af: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
         // b2: aload 0
         // b3: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
         // b6: aload 0
         // b7: getfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1.$accumulator Lkotlin/jvm/internal/Ref$ObjectRef;
         // ba: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
         // bd: aload 5
         // bf: aload 5
         // c1: aconst_null
         // c2: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.L$0 Ljava/lang/Object;
         // c5: aload 5
         // c7: aconst_null
         // c8: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.L$1 Ljava/lang/Object;
         // cb: aload 5
         // cd: bipush 2
         // ce: putfield kotlinx/coroutines/flow/FlowKt__TransformKt$runningFold$1$1$emit$1.label I
         // d1: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
         // d6: dup
         // d7: aload 6
         // d9: if_acmpne e6
         // dc: aload 6
         // de: areturn
         // df: aload 4
         // e1: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
         // e4: aload 4
         // e6: pop
         // e7: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // ea: areturn
         // eb: new java/lang/IllegalStateException
         // ee: dup
         // ef: ldc "call to 'resume' before 'invoke' with coroutine"
         // f1: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
         // f4: athrow
      }
      `$continuation`.L$0 = null
      `$continuation`.L$1 = null
      `$continuation`.L$2 = null
      `$continuation`.label = 2
      return if (var10000.collect(var11, `$continuation`) === var5) var5 else Unit.INSTANCE
   }
}
