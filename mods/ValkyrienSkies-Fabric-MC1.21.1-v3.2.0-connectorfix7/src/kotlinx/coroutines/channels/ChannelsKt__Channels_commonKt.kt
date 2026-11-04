@file:JvmMultifileClass
@file:JvmName("ChannelsKt")
@file:SourceDebugExtension(["SMAP\nChannels.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Channels.common.kt\nkotlinx/coroutines/channels/ChannelsKt__Channels_commonKt\n*L\n1#1,141:1\n82#1,11:142\n105#1:153\n82#1,6:154\n106#1,2:160\n92#1:162\n88#1,3:163\n33#1,5:166\n*S KotlinDebug\n*F\n+ 1 Channels.common.kt\nkotlinx/coroutines/channels/ChannelsKt__Channels_commonKt\n*L\n105#1:142,11\n116#1:153\n116#1:154,6\n116#1:160,2\n116#1:162\n116#1:163,3\n129#1:166,5\n*E\n"])

package kotlinx.coroutines.channels

import java.util.concurrent.CancellationException
import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.ExceptionsKt
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.selects.SelectClause1

@Deprecated(message = "BroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported", level = DeprecationLevel.WARNING)
@ObsoleteCoroutinesApi
public inline fun <E, R> BroadcastChannel<Any>.consume(block: (ReceiveChannel<Any>) -> Any): Any {
   val channel: ReceiveChannel = `$this$consume`.openSubscription()

   try {
      return (R)block(channel)
   } finally {
      InlineMarker.finallyStart(1)
      ReceiveChannel.DefaultImpls.cancel$default(channel, (CancellationException)null, 1, null)
      InlineMarker.finallyEnd(1)
   }
}

@Deprecated(message = "Deprecated in the favour of 'receiveCatching'", replaceWith = @ReplaceWith(expression = "receiveCatching().getOrNull()", imports = []), level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E : Any> ReceiveChannel<Any>.receiveOrNull(): Any? {
   return `$this$receiveOrNull`.receiveOrNull(`$completion`)
}

@Deprecated(message = "Deprecated in the favour of 'onReceiveCatching'", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E : Any> ReceiveChannel<Any>.onReceiveOrNull(): SelectClause1<Any?> {
   return `$this$onReceiveOrNull`.onReceiveOrNull
}

public inline fun <E, R> ReceiveChannel<Any>.consume(block: (ReceiveChannel<Any>) -> Any): Any {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   var cause: java.lang.Throwable = null

   try {
      return (R)block(`$this$consume`)
   } catch (var7: java.lang.Throwable) {
      cause = var7
      throw var7
   } finally {
      InlineMarker.finallyStart(1)
      ChannelsKt.cancelConsumed(`$this$consume`, cause)
      InlineMarker.finallyEnd(1)
   }
}

public suspend inline fun <E> ReceiveChannel<Any>.consumeEach(action: (Any) -> Unit) {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1
   // 00b: astore 12
   // 00d: aload 12
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 12
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 12
   // 031: aload 12
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.result Ljava/lang/Object;
   // 036: astore 11
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 13
   // 03d: aload 12
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.label I
   // 042: tableswitch 235 0 1 22 100
   // 058: aload 11
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: bipush 0
   // 05e: istore 3
   // 05f: aload 0
   // 060: astore 4
   // 062: bipush 0
   // 063: istore 5
   // 065: nop
   // 066: aconst_null
   // 067: astore 6
   // 069: nop
   // 06a: aload 4
   // 06c: astore 7
   // 06e: bipush 0
   // 06f: istore 8
   // 071: aload 7
   // 073: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 078: astore 9
   // 07a: aload 9
   // 07c: aload 12
   // 07e: aload 12
   // 080: aload 1
   // 081: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.L$0 Ljava/lang/Object;
   // 084: aload 12
   // 086: aload 4
   // 088: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.L$1 Ljava/lang/Object;
   // 08b: aload 12
   // 08d: aload 9
   // 08f: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.L$2 Ljava/lang/Object;
   // 092: aload 12
   // 094: bipush 1
   // 095: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.label I
   // 098: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 09d: dup
   // 09e: aload 13
   // 0a0: if_acmpne 0d6
   // 0a3: aload 13
   // 0a5: areturn
   // 0a6: bipush 0
   // 0a7: istore 3
   // 0a8: bipush 0
   // 0a9: istore 5
   // 0ab: bipush 0
   // 0ac: istore 8
   // 0ae: aload 12
   // 0b0: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.L$2 Ljava/lang/Object;
   // 0b3: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0b6: astore 9
   // 0b8: aconst_null
   // 0b9: astore 6
   // 0bb: aload 12
   // 0bd: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.L$1 Ljava/lang/Object;
   // 0c0: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0c3: astore 4
   // 0c5: aload 12
   // 0c7: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$1.L$0 Ljava/lang/Object;
   // 0ca: checkcast kotlin/jvm/functions/Function1
   // 0cd: astore 1
   // 0ce: nop
   // 0cf: aload 11
   // 0d1: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0d4: aload 11
   // 0d6: checkcast java/lang/Boolean
   // 0d9: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0dc: ifeq 0f4
   // 0df: aload 9
   // 0e1: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0e6: astore 10
   // 0e8: aload 1
   // 0e9: aload 10
   // 0eb: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
   // 0f0: pop
   // 0f1: goto 07a
   // 0f4: nop
   // 0f5: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 0f8: astore 7
   // 0fa: bipush 1
   // 0fb: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
   // 0fe: aload 4
   // 100: aload 6
   // 102: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 105: bipush 1
   // 106: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
   // 109: goto 129
   // 10c: astore 7
   // 10e: aload 7
   // 110: astore 6
   // 112: aload 7
   // 114: athrow
   // 115: astore 7
   // 117: bipush 1
   // 118: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
   // 11b: aload 4
   // 11d: aload 6
   // 11f: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 122: bipush 1
   // 123: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
   // 126: aload 7
   // 128: athrow
   // 129: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 12c: areturn
   // 12d: new java/lang/IllegalStateException
   // 130: dup
   // 131: ldc "call to 'resume' before 'invoke' with coroutine"
   // 133: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 136: athrow
}

fun <E> ReceiveChannel<out E>.`consumeEach$$forInline`(action: (E?) -> Unit, `$completion`: Continuation<in Unit>) {
   val `$this$consume$iv`: ReceiveChannel = `$this$consumeEach`
   var `cause$iv`: java.lang.Throwable = null

   try {
      val var9: ChannelIterator = `$this$consume$iv`.iterator()

      while (true) {
         InlineMarker.mark(3)
         InlineMarker.mark(0)
         val var10000: Any = var9.hasNext(null)
         InlineMarker.mark(1)
         if (!var10000 as java.lang.Boolean) {
            Unit.INSTANCE
         }

         action(var9.next())
      }
   } catch (var13: java.lang.Throwable) {
      `cause$iv` = var13
      throw var13
   } finally {
      InlineMarker.finallyStart(1)
      ChannelsKt.cancelConsumed(`$this$consumeEach`, `cause$iv`)
      InlineMarker.finallyEnd(1)
   }
}

public suspend fun <E> ReceiveChannel<Any>.toList(): List<Any> {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 1
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1
   // 004: ifeq 027
   // 007: aload 1
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1
   // 00b: astore 17
   // 00d: aload 17
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 17
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1
   // 02a: dup
   // 02b: aload 1
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 17
   // 031: aload 17
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.result Ljava/lang/Object;
   // 036: astore 16
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 18
   // 03d: aload 17
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.label I
   // 042: tableswitch 263 0 1 22 120
   // 058: aload 16
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: invokestatic kotlin/collections/CollectionsKt.createListBuilder ()Ljava/util/List;
   // 060: astore 2
   // 061: aload 2
   // 062: astore 3
   // 063: bipush 0
   // 064: istore 4
   // 066: aload 0
   // 067: astore 5
   // 069: bipush 0
   // 06a: istore 6
   // 06c: aload 5
   // 06e: astore 7
   // 070: bipush 0
   // 071: istore 8
   // 073: nop
   // 074: aconst_null
   // 075: astore 9
   // 077: nop
   // 078: aload 7
   // 07a: astore 10
   // 07c: bipush 0
   // 07d: istore 11
   // 07f: aload 10
   // 081: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 086: astore 12
   // 088: aload 12
   // 08a: aload 17
   // 08c: aload 17
   // 08e: aload 2
   // 08f: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.L$0 Ljava/lang/Object;
   // 092: aload 17
   // 094: aload 3
   // 095: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.L$1 Ljava/lang/Object;
   // 098: aload 17
   // 09a: aload 7
   // 09c: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.L$2 Ljava/lang/Object;
   // 09f: aload 17
   // 0a1: aload 12
   // 0a3: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.L$3 Ljava/lang/Object;
   // 0a6: aload 17
   // 0a8: bipush 1
   // 0a9: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.label I
   // 0ac: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0b1: dup
   // 0b2: aload 18
   // 0b4: if_acmpne 0f7
   // 0b7: aload 18
   // 0b9: areturn
   // 0ba: bipush 0
   // 0bb: istore 4
   // 0bd: bipush 0
   // 0be: istore 6
   // 0c0: bipush 0
   // 0c1: istore 8
   // 0c3: bipush 0
   // 0c4: istore 11
   // 0c6: aload 17
   // 0c8: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.L$3 Ljava/lang/Object;
   // 0cb: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ce: astore 12
   // 0d0: aconst_null
   // 0d1: astore 9
   // 0d3: aload 17
   // 0d5: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.L$2 Ljava/lang/Object;
   // 0d8: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0db: astore 7
   // 0dd: aload 17
   // 0df: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.L$1 Ljava/lang/Object;
   // 0e2: checkcast java/util/List
   // 0e5: astore 3
   // 0e6: aload 17
   // 0e8: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$toList$1.L$0 Ljava/lang/Object;
   // 0eb: checkcast java/util/List
   // 0ee: astore 2
   // 0ef: nop
   // 0f0: aload 16
   // 0f2: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0f5: aload 16
   // 0f7: checkcast java/lang/Boolean
   // 0fa: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0fd: ifeq 11d
   // 100: aload 12
   // 102: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 107: astore 13
   // 109: aload 13
   // 10b: astore 14
   // 10d: bipush 0
   // 10e: istore 15
   // 110: aload 3
   // 111: aload 14
   // 113: invokeinterface java/util/List.add (Ljava/lang/Object;)Z 2
   // 118: pop
   // 119: nop
   // 11a: goto 088
   // 11d: nop
   // 11e: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 121: astore 10
   // 123: aload 7
   // 125: aload 9
   // 127: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 12a: goto 142
   // 12d: astore 10
   // 12f: aload 10
   // 131: astore 9
   // 133: aload 10
   // 135: athrow
   // 136: astore 10
   // 138: aload 7
   // 13a: aload 9
   // 13c: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 13f: aload 10
   // 141: athrow
   // 142: nop
   // 143: nop
   // 144: aload 2
   // 145: invokestatic kotlin/collections/CollectionsKt.build (Ljava/util/List;)Ljava/util/List;
   // 148: areturn
   // 149: new java/lang/IllegalStateException
   // 14c: dup
   // 14d: ldc "call to 'resume' before 'invoke' with coroutine"
   // 14f: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 152: athrow
}

@Deprecated(message = "BroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported", level = DeprecationLevel.WARNING)
public suspend inline fun <E> BroadcastChannel<Any>.consumeEach(action: (Any) -> Unit) {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
   //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
   //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
   //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
   //   at java.base/java.util.Objects.checkIndex(Objects.java:365)
   //   at java.base/java.util.ArrayList.remove(ArrayList.java:552)
   //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1058)
   //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.insertSemaphore(FinallyProcessor.java:351)
   //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:98)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3
   // 00b: astore 12
   // 00d: aload 12
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 12
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 12
   // 031: aload 12
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.result Ljava/lang/Object;
   // 036: astore 11
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 13
   // 03d: aload 12
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.label I
   // 042: tableswitch 230 0 1 22 105
   // 058: aload 11
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: bipush 0
   // 05e: istore 3
   // 05f: aload 0
   // 060: astore 4
   // 062: bipush 0
   // 063: istore 5
   // 065: aload 4
   // 067: invokeinterface kotlinx/coroutines/channels/BroadcastChannel.openSubscription ()Lkotlinx/coroutines/channels/ReceiveChannel; 1
   // 06c: astore 6
   // 06e: nop
   // 06f: aload 6
   // 071: astore 7
   // 073: bipush 0
   // 074: istore 8
   // 076: aload 7
   // 078: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 07d: astore 9
   // 07f: aload 9
   // 081: aload 12
   // 083: aload 12
   // 085: aload 1
   // 086: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.L$0 Ljava/lang/Object;
   // 089: aload 12
   // 08b: aload 6
   // 08d: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.L$1 Ljava/lang/Object;
   // 090: aload 12
   // 092: aload 9
   // 094: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.L$2 Ljava/lang/Object;
   // 097: aload 12
   // 099: bipush 1
   // 09a: putfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.label I
   // 09d: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0a2: dup
   // 0a3: aload 13
   // 0a5: if_acmpne 0d8
   // 0a8: aload 13
   // 0aa: areturn
   // 0ab: bipush 0
   // 0ac: istore 3
   // 0ad: bipush 0
   // 0ae: istore 5
   // 0b0: bipush 0
   // 0b1: istore 8
   // 0b3: aload 12
   // 0b5: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.L$2 Ljava/lang/Object;
   // 0b8: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0bb: astore 9
   // 0bd: aload 12
   // 0bf: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.L$1 Ljava/lang/Object;
   // 0c2: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0c5: astore 6
   // 0c7: aload 12
   // 0c9: getfield kotlinx/coroutines/channels/ChannelsKt__Channels_commonKt$consumeEach$3.L$0 Ljava/lang/Object;
   // 0cc: checkcast kotlin/jvm/functions/Function1
   // 0cf: astore 1
   // 0d0: nop
   // 0d1: aload 11
   // 0d3: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0d6: aload 11
   // 0d8: checkcast java/lang/Boolean
   // 0db: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0de: ifeq 0f6
   // 0e1: aload 9
   // 0e3: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0e8: astore 10
   // 0ea: aload 1
   // 0eb: aload 10
   // 0ed: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
   // 0f2: pop
   // 0f3: goto 07f
   // 0f6: nop
   // 0f7: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 0fa: astore 7
   // 0fc: bipush 1
   // 0fd: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
   // 100: aload 6
   // 102: aconst_null
   // 103: bipush 1
   // 104: aconst_null
   // 105: invokestatic kotlinx/coroutines/channels/ReceiveChannel$DefaultImpls.cancel$default (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/util/concurrent/CancellationException;ILjava/lang/Object;)V
   // 108: bipush 1
   // 109: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
   // 10c: goto 124
   // 10f: astore 7
   // 111: bipush 1
   // 112: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
   // 115: aload 6
   // 117: aconst_null
   // 118: bipush 1
   // 119: aconst_null
   // 11a: invokestatic kotlinx/coroutines/channels/ReceiveChannel$DefaultImpls.cancel$default (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/util/concurrent/CancellationException;ILjava/lang/Object;)V
   // 11d: bipush 1
   // 11e: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
   // 121: aload 7
   // 123: athrow
   // 124: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 127: areturn
   // 128: new java/lang/IllegalStateException
   // 12b: dup
   // 12c: ldc "call to 'resume' before 'invoke' with coroutine"
   // 12e: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 131: athrow
}

/** @deprecated */
@Deprecated(message = "BroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported", level = DeprecationLevel.WARNING)
fun <E> BroadcastChannel<E>.`consumeEach$$forInline`(action: (E?) -> Unit, `$completion`: Continuation<in Unit>) {
   val `channel$iv`: ReceiveChannel = `$this$consumeEach`.openSubscription()

   try {
      val var9: ChannelIterator = `channel$iv`.iterator()

      while (true) {
         InlineMarker.mark(3)
         InlineMarker.mark(0)
         val var10000: Any = var9.hasNext(null)
         InlineMarker.mark(1)
         if (!var10000 as java.lang.Boolean) {
            Unit.INSTANCE
         }

         action(var9.next())
      }
   } finally {
      InlineMarker.finallyStart(1)
      ReceiveChannel.DefaultImpls.cancel$default(`channel$iv`, (CancellationException)null, 1, null)
      InlineMarker.finallyEnd(1)
   }
}

@PublishedApi
internal fun ReceiveChannel<*>.cancelConsumed(cause: Throwable?) {
   var var10000: ReceiveChannel = `$this$cancelConsumed`
   val var10001: CancellationException
   if (cause != null) {
      var var5: CancellationException = cause as? CancellationException
      if ((cause as? CancellationException) == null) {
         var5 = ExceptionsKt.CancellationException("Channel was consumed, consumer had failed", cause)
      }

      var10001 = var5
      var10000 = `$this$cancelConsumed`
   } else {
      var10001 = null
   }

   var10000.cancel(var10001)
}
