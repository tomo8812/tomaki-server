@file:SourceDebugExtension(["SMAP\nAwait.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Await.kt\nkotlinx/coroutines/AwaitKt\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,127:1\n37#2,2:128\n13579#3,2:130\n1855#4,2:132\n*S KotlinDebug\n*F\n+ 1 Await.kt\nkotlinx/coroutines/AwaitKt\n*L\n42#1:128,2\n54#1:130,2\n66#1:132,2\n*E\n"])

package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.jvm.internal.SourceDebugExtension

public suspend fun <T> awaitAll(vararg deferreds: Deferred<Any>): List<Any> {
   return if (deferreds.length == 0) CollectionsKt.emptyList() else AwaitAll(deferreds).await(`$completion`)
}

public suspend fun <T> Collection<Deferred<Any>>.awaitAll(): List<Any> {
   return if (`$this$awaitAll`.isEmpty()) CollectionsKt.emptyList() else AwaitAll(`$this$awaitAll`.toArray(arrayOfNulls(0))).await(`$completion`)
}

public suspend fun joinAll(vararg jobs: Job) {
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
   // 01: instanceof kotlinx/coroutines/AwaitKt$joinAll$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/AwaitKt$joinAll$1
   // 0b: astore 10
   // 0d: aload 10
   // 0f: getfield kotlinx/coroutines/AwaitKt$joinAll$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 10
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/AwaitKt$joinAll$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/AwaitKt$joinAll$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/AwaitKt$joinAll$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/AwaitKt$joinAll$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 10
   // 31: aload 10
   // 33: getfield kotlinx/coroutines/AwaitKt$joinAll$1.result Ljava/lang/Object;
   // 36: astore 9
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 11
   // 3d: aload 10
   // 3f: getfield kotlinx/coroutines/AwaitKt$joinAll$1.label I
   // 42: tableswitch 149 0 1 22 102
   // 58: aload 9
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: aload 0
   // 5e: astore 2
   // 5f: bipush 0
   // 60: istore 3
   // 61: bipush 0
   // 62: istore 4
   // 64: aload 2
   // 65: arraylength
   // 66: istore 5
   // 68: iload 4
   // 6a: iload 5
   // 6c: if_icmpge d2
   // 6f: aload 2
   // 70: iload 4
   // 72: aaload
   // 73: astore 6
   // 75: aload 6
   // 77: astore 7
   // 79: bipush 0
   // 7a: istore 8
   // 7c: aload 7
   // 7e: aload 10
   // 80: aload 10
   // 82: aload 2
   // 83: putfield kotlinx/coroutines/AwaitKt$joinAll$1.L$0 Ljava/lang/Object;
   // 86: aload 10
   // 88: iload 4
   // 8a: putfield kotlinx/coroutines/AwaitKt$joinAll$1.I$0 I
   // 8d: aload 10
   // 8f: iload 5
   // 91: putfield kotlinx/coroutines/AwaitKt$joinAll$1.I$1 I
   // 94: aload 10
   // 96: bipush 1
   // 97: putfield kotlinx/coroutines/AwaitKt$joinAll$1.label I
   // 9a: invokeinterface kotlinx/coroutines/Job.join (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 9f: dup
   // a0: aload 11
   // a2: if_acmpne cb
   // a5: aload 11
   // a7: areturn
   // a8: bipush 0
   // a9: istore 3
   // aa: bipush 0
   // ab: istore 8
   // ad: aload 10
   // af: getfield kotlinx/coroutines/AwaitKt$joinAll$1.I$1 I
   // b2: istore 5
   // b4: aload 10
   // b6: getfield kotlinx/coroutines/AwaitKt$joinAll$1.I$0 I
   // b9: istore 4
   // bb: aload 10
   // bd: getfield kotlinx/coroutines/AwaitKt$joinAll$1.L$0 Ljava/lang/Object;
   // c0: checkcast [Lkotlinx/coroutines/Job;
   // c3: astore 2
   // c4: aload 9
   // c6: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // c9: aload 9
   // cb: pop
   // cc: iinc 4 1
   // cf: goto 68
   // d2: nop
   // d3: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // d6: areturn
   // d7: new java/lang/IllegalStateException
   // da: dup
   // db: ldc "call to 'resume' before 'invoke' with coroutine"
   // dd: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // e0: athrow
}

public suspend fun Collection<Job>.joinAll() {
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
   // 01: instanceof kotlinx/coroutines/AwaitKt$joinAll$3
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/AwaitKt$joinAll$3
   // 0b: astore 9
   // 0d: aload 9
   // 0f: getfield kotlinx/coroutines/AwaitKt$joinAll$3.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 9
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/AwaitKt$joinAll$3.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/AwaitKt$joinAll$3.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/AwaitKt$joinAll$3
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/AwaitKt$joinAll$3.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 9
   // 31: aload 9
   // 33: getfield kotlinx/coroutines/AwaitKt$joinAll$3.result Ljava/lang/Object;
   // 36: astore 8
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 10
   // 3d: aload 9
   // 3f: getfield kotlinx/coroutines/AwaitKt$joinAll$3.label I
   // 42: tableswitch 133 0 1 22 102
   // 58: aload 8
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: aload 0
   // 5e: checkcast java/lang/Iterable
   // 61: astore 2
   // 62: bipush 0
   // 63: istore 3
   // 64: aload 2
   // 65: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
   // 6a: astore 4
   // 6c: aload 4
   // 6e: invokeinterface java/util/Iterator.hasNext ()Z 1
   // 73: ifeq c2
   // 76: aload 4
   // 78: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
   // 7d: astore 5
   // 7f: aload 5
   // 81: checkcast kotlinx/coroutines/Job
   // 84: astore 6
   // 86: bipush 0
   // 87: istore 7
   // 89: aload 6
   // 8b: aload 9
   // 8d: aload 9
   // 8f: aload 4
   // 91: putfield kotlinx/coroutines/AwaitKt$joinAll$3.L$0 Ljava/lang/Object;
   // 94: aload 9
   // 96: bipush 1
   // 97: putfield kotlinx/coroutines/AwaitKt$joinAll$3.label I
   // 9a: invokeinterface kotlinx/coroutines/Job.join (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 9f: dup
   // a0: aload 10
   // a2: if_acmpne be
   // a5: aload 10
   // a7: areturn
   // a8: bipush 0
   // a9: istore 3
   // aa: bipush 0
   // ab: istore 7
   // ad: aload 9
   // af: getfield kotlinx/coroutines/AwaitKt$joinAll$3.L$0 Ljava/lang/Object;
   // b2: checkcast java/util/Iterator
   // b5: astore 4
   // b7: aload 8
   // b9: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // bc: aload 8
   // be: pop
   // bf: goto 6c
   // c2: nop
   // c3: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // c6: areturn
   // c7: new java/lang/IllegalStateException
   // ca: dup
   // cb: ldc "call to 'resume' before 'invoke' with coroutine"
   // cd: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // d0: athrow
}
