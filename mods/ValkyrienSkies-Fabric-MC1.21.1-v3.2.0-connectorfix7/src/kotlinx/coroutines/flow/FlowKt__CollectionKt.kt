@file:JvmMultifileClass
@file:JvmName("FlowKt")

package kotlinx.coroutines.flow

import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation

public suspend fun <T> Flow<Any>.toList(destination: MutableList<Any> = ...): List<Any> {
   return FlowKt.toCollection(`$this$toList`, destination, `$completion`)
}

@JvmSynthetic
fun `toList$default`(var0: Flow, var1: java.util.List, var2: Continuation, var3: Int, var4: Any) {
   if ((var3 and 1) != 0) {
      var1 = ArrayList()
   }

   FlowKt.toList(var0, var1, var2)
}

public suspend fun <T> Flow<Any>.toSet(destination: MutableSet<Any> = ...): Set<Any> {
   return FlowKt.toCollection(`$this$toSet`, destination, `$completion`)
}

@JvmSynthetic
fun `toSet$default`(var0: Flow, var1: java.util.Set, var2: Continuation, var3: Int, var4: Any) {
   if ((var3 and 1) != 0) {
      var1 = LinkedHashSet()
   }

   FlowKt.toSet(var0, var1, var2)
}

public suspend fun <T, C : MutableCollection<in Any>> Flow<Any>.toCollection(destination: Any): Any {
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
   // 01: instanceof kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1
   // 04: ifeq 27
   // 07: aload 2
   // 08: checkcast kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1
   // 0b: astore 4
   // 0d: aload 4
   // 0f: getfield kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 4
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1
   // 2a: dup
   // 2b: aload 2
   // 2c: invokespecial kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 4
   // 31: aload 4
   // 33: getfield kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1.result Ljava/lang/Object;
   // 36: astore 3
   // 37: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3a: astore 5
   // 3c: aload 4
   // 3e: getfield kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1.label I
   // 41: tableswitch 84 0 1 23 67
   // 58: aload 3
   // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5c: aload 0
   // 5d: new kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$2
   // 60: dup
   // 61: aload 1
   // 62: invokespecial kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$2.<init> (Ljava/util/Collection;)V
   // 65: checkcast kotlinx/coroutines/flow/FlowCollector
   // 68: aload 4
   // 6a: aload 4
   // 6c: aload 1
   // 6d: putfield kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1.L$0 Ljava/lang/Object;
   // 70: aload 4
   // 72: bipush 1
   // 73: putfield kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1.label I
   // 76: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 7b: dup
   // 7c: aload 5
   // 7e: if_acmpne 92
   // 81: aload 5
   // 83: areturn
   // 84: aload 4
   // 86: getfield kotlinx/coroutines/flow/FlowKt__CollectionKt$toCollection$1.L$0 Ljava/lang/Object;
   // 89: checkcast java/util/Collection
   // 8c: astore 1
   // 8d: aload 3
   // 8e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 91: aload 3
   // 92: pop
   // 93: aload 1
   // 94: areturn
   // 95: new java/lang/IllegalStateException
   // 98: dup
   // 99: ldc "call to 'resume' before 'invoke' with coroutine"
   // 9b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 9e: athrow
}
