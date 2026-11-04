@file:SourceDebugExtension(["SMAP\nWhileSelect.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WhileSelect.kt\nkotlinx/coroutines/selects/WhileSelectKt\n+ 2 Select.kt\nkotlinx/coroutines/selects/SelectKt\n*L\n1#1,33:1\n55#2,8:34\n*S KotlinDebug\n*F\n+ 1 WhileSelect.kt\nkotlinx/coroutines/selects/WhileSelectKt\n*L\n31#1:34,8\n*E\n"])

package kotlinx.coroutines.selects

import kotlin.coroutines.Continuation
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
public suspend inline fun whileSelect(crossinline builder: (SelectBuilder<Boolean>) -> Unit) {
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
   // 01: instanceof kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1
   // 0b: astore 7
   // 0d: aload 7
   // 0f: getfield kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 7
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 7
   // 31: aload 7
   // 33: getfield kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1.result Ljava/lang/Object;
   // 36: astore 6
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 8
   // 3d: aload 7
   // 3f: getfield kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1.label I
   // 42: tableswitch 127 0 1 22 88
   // 58: aload 6
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: bipush 0
   // 5e: istore 2
   // 5f: bipush 0
   // 60: istore 3
   // 61: nop
   // 62: new kotlinx/coroutines/selects/SelectImplementation
   // 65: dup
   // 66: aload 7
   // 68: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
   // 6d: invokespecial kotlinx/coroutines/selects/SelectImplementation.<init> (Lkotlin/coroutines/CoroutineContext;)V
   // 70: astore 4
   // 72: bipush 0
   // 73: istore 5
   // 75: aload 0
   // 76: aload 4
   // 78: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
   // 7d: pop
   // 7e: aload 4
   // 80: aload 7
   // 82: aload 7
   // 84: aload 0
   // 85: putfield kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1.L$0 Ljava/lang/Object;
   // 88: aload 7
   // 8a: bipush 1
   // 8b: putfield kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1.label I
   // 8e: invokevirtual kotlinx/coroutines/selects/SelectImplementation.doSelect (Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 91: dup
   // 92: aload 8
   // 94: if_acmpne b1
   // 97: aload 8
   // 99: areturn
   // 9a: bipush 0
   // 9b: istore 2
   // 9c: bipush 0
   // 9d: istore 3
   // 9e: bipush 0
   // 9f: istore 5
   // a1: aload 7
   // a3: getfield kotlinx/coroutines/selects/WhileSelectKt$whileSelect$1.L$0 Ljava/lang/Object;
   // a6: checkcast kotlin/jvm/functions/Function1
   // a9: astore 0
   // aa: aload 6
   // ac: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // af: aload 6
   // b1: nop
   // b2: nop
   // b3: nop
   // b4: checkcast java/lang/Boolean
   // b7: invokevirtual java/lang/Boolean.booleanValue ()Z
   // ba: ifne 5f
   // bd: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // c0: areturn
   // c1: new java/lang/IllegalStateException
   // c4: dup
   // c5: ldc "call to 'resume' before 'invoke' with coroutine"
   // c7: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // ca: athrow
}

@ExperimentalCoroutinesApi
fun `whileSelect$$forInline`(builder: (SelectBuilder<? super java.lang.Boolean>?) -> Unit, `$completion`: Continuation<in Unit>) {
   val var10000: Any
   do {
      InlineMarker.mark(3)
      val `$this$select_u24lambda_u241$iv`: SelectImplementation = SelectImplementation(null.getContext())
      builder(`$this$select_u24lambda_u241$iv`)
      InlineMarker.mark(3)
      InlineMarker.mark(0)
      var10000 = `$this$select_u24lambda_u241$iv`.doSelect(null)
      InlineMarker.mark(1)
   } while (var10000 as java.lang.Boolean)

   Unit.INSTANCE
}
