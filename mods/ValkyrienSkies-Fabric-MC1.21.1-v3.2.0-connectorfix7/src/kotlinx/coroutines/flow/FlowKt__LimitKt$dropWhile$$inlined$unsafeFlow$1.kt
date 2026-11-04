package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.BooleanRef

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt\n*L\n1#1,113:1\n34#2,10:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__LimitKt$dropWhile$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   Flow $this_dropWhile$inlined;
   @JvmSynthetic
   Function2 $predicate$inlined;

   fun `FlowKt__LimitKt$dropWhile$$inlined$unsafeFlow$1`(var1: Flow, var2: Function2) {
      this.$this_dropWhile$inlined = var1
      this.$predicate$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val `$this$dropWhile_u24lambda_u242`: FlowCollector = collector
      val matched: BooleanRef = BooleanRef()
      val var10000: Any = this.$this_dropWhile$inlined
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
               // 000: aload 2
               // 001: instanceof kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1
               // 004: ifeq 027
               // 007: aload 2
               // 008: checkcast kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1
               // 00b: astore 4
               // 00d: aload 4
               // 00f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.label I
               // 012: ldc -2147483648
               // 014: iand
               // 015: ifeq 027
               // 018: aload 4
               // 01a: dup
               // 01b: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.label I
               // 01e: ldc -2147483648
               // 020: isub
               // 021: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.label I
               // 024: goto 032
               // 027: new kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1
               // 02a: dup
               // 02b: aload 0
               // 02c: aload 2
               // 02d: invokespecial kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.<init> (Lkotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1;Lkotlin/coroutines/Continuation;)V
               // 030: astore 4
               // 032: aload 4
               // 034: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.result Ljava/lang/Object;
               // 037: astore 3
               // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
               // 03b: astore 5
               // 03d: aload 4
               // 03f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.label I
               // 042: tableswitch 210 0 3 30 71 120 196
               // 060: aload 3
               // 061: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 064: aload 0
               // 065: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1.$matched Lkotlin/jvm/internal/Ref$BooleanRef;
               // 068: getfield kotlin/jvm/internal/Ref$BooleanRef.element Z
               // 06b: ifeq 093
               // 06e: aload 0
               // 06f: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
               // 072: aload 1
               // 073: aload 4
               // 075: aload 4
               // 077: bipush 1
               // 078: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.label I
               // 07b: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
               // 080: dup
               // 081: aload 5
               // 083: if_acmpne 08e
               // 086: aload 5
               // 088: areturn
               // 089: aload 3
               // 08a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 08d: aload 3
               // 08e: pop
               // 08f: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 092: areturn
               // 093: aload 0
               // 094: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1.$predicate Lkotlin/jvm/functions/Function2;
               // 097: aload 1
               // 098: aload 4
               // 09a: aload 4
               // 09c: aload 0
               // 09d: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.L$0 Ljava/lang/Object;
               // 0a0: aload 4
               // 0a2: aload 1
               // 0a3: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.L$1 Ljava/lang/Object;
               // 0a6: aload 4
               // 0a8: bipush 2
               // 0a9: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.label I
               // 0ac: invokeinterface kotlin/jvm/functions/Function2.invoke (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
               // 0b1: dup
               // 0b2: aload 5
               // 0b4: if_acmpne 0ce
               // 0b7: aload 5
               // 0b9: areturn
               // 0ba: aload 4
               // 0bc: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.L$1 Ljava/lang/Object;
               // 0bf: astore 1
               // 0c0: aload 4
               // 0c2: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.L$0 Ljava/lang/Object;
               // 0c5: checkcast kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1
               // 0c8: astore 0
               // 0c9: aload 3
               // 0ca: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 0cd: aload 3
               // 0ce: checkcast java/lang/Boolean
               // 0d1: invokevirtual java/lang/Boolean.booleanValue ()Z
               // 0d4: ifne 110
               // 0d7: aload 0
               // 0d8: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1.$matched Lkotlin/jvm/internal/Ref$BooleanRef;
               // 0db: bipush 1
               // 0dc: putfield kotlin/jvm/internal/Ref$BooleanRef.element Z
               // 0df: aload 0
               // 0e0: getfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1.$this_unsafeFlow Lkotlinx/coroutines/flow/FlowCollector;
               // 0e3: aload 1
               // 0e4: aload 4
               // 0e6: aload 4
               // 0e8: aconst_null
               // 0e9: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.L$0 Ljava/lang/Object;
               // 0ec: aload 4
               // 0ee: aconst_null
               // 0ef: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.L$1 Ljava/lang/Object;
               // 0f2: aload 4
               // 0f4: bipush 3
               // 0f5: putfield kotlinx/coroutines/flow/FlowKt__LimitKt$dropWhile$1$1$emit$1.label I
               // 0f8: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
               // 0fd: dup
               // 0fe: aload 5
               // 100: if_acmpne 10b
               // 103: aload 5
               // 105: areturn
               // 106: aload 3
               // 107: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
               // 10a: aload 3
               // 10b: pop
               // 10c: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 10f: areturn
               // 110: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 113: areturn
               // 114: new java/lang/IllegalStateException
               // 117: dup
               // 118: ldc "call to 'resume' before 'invoke' with coroutine"
               // 11a: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // 11d: athrow
            },
            `$completion`
         )
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
