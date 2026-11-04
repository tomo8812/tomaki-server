@file:SourceDebugExtension(["SMAP\nTickerChannels.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TickerChannels.kt\nkotlinx/coroutines/channels/TickerChannelsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,112:1\n1#2:113\n*E\n"])

package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
public fun ticker(
   delayMillis: Long,
   initialDelayMillis: Long = delayMillis,
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   mode: TickerMode = TickerMode.FIXED_PERIOD
): ReceiveChannel<Unit> {
   if (delayMillis < 0L) {
      throw IllegalArgumentException(("Expected non-negative delay, but has $delayMillis ms").toString())
   } else if (initialDelayMillis < 0L) {
      throw IllegalArgumentException(("Expected non-negative initial delay, but has $initialDelayMillis ms").toString())
   } else {
      return ProduceKt.produce(GlobalScope.INSTANCE, Unconfined.plus(context), 0, {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as (ProducerScope<in Unit>?, Continuation<in Unit>?) -> Any)
   }
}

private suspend fun fixedPeriodTicker(delayMillis: Long, initialDelayMillis: Long, channel: SendChannel<Unit>) {
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
   // 000: aload 5
   // 002: instanceof kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1
   // 005: ifeq 029
   // 008: aload 5
   // 00a: checkcast kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1
   // 00d: astore 17
   // 00f: aload 17
   // 011: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.label I
   // 014: ldc -2147483648
   // 016: iand
   // 017: ifeq 029
   // 01a: aload 17
   // 01c: dup
   // 01d: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.label I
   // 020: ldc -2147483648
   // 022: isub
   // 023: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.label I
   // 026: goto 034
   // 029: new kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1
   // 02c: dup
   // 02d: aload 5
   // 02f: invokespecial kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 032: astore 17
   // 034: aload 17
   // 036: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.result Ljava/lang/Object;
   // 039: astore 16
   // 03b: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03e: astore 18
   // 040: aload 17
   // 042: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.label I
   // 045: tableswitch 456 0 4 35 105 198 340 421
   // 068: aload 16
   // 06a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 06d: invokestatic kotlinx/coroutines/AbstractTimeSourceKt.getTimeSource ()Lkotlinx/coroutines/AbstractTimeSource;
   // 070: dup
   // 071: ifnull 07a
   // 074: invokevirtual kotlinx/coroutines/AbstractTimeSource.nanoTime ()J
   // 077: goto 07e
   // 07a: pop
   // 07b: invokestatic java/lang/System.nanoTime ()J
   // 07e: lload 2
   // 07f: invokestatic kotlinx/coroutines/EventLoop_commonKt.delayToNanos (J)J
   // 082: ladd
   // 083: lstore 6
   // 085: lload 2
   // 086: aload 17
   // 088: aload 17
   // 08a: aload 4
   // 08c: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.L$0 Ljava/lang/Object;
   // 08f: aload 17
   // 091: lload 0
   // 092: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$0 J
   // 095: aload 17
   // 097: lload 6
   // 099: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$1 J
   // 09c: aload 17
   // 09e: bipush 1
   // 09f: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.label I
   // 0a2: invokestatic kotlinx/coroutines/DelayKt.delay (JLkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 0a5: dup
   // 0a6: aload 18
   // 0a8: if_acmpne 0cc
   // 0ab: aload 18
   // 0ad: areturn
   // 0ae: aload 17
   // 0b0: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$1 J
   // 0b3: lstore 6
   // 0b5: aload 17
   // 0b7: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$0 J
   // 0ba: lstore 0
   // 0bb: aload 17
   // 0bd: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.L$0 Ljava/lang/Object;
   // 0c0: checkcast kotlinx/coroutines/channels/SendChannel
   // 0c3: astore 4
   // 0c5: aload 16
   // 0c7: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0ca: aload 16
   // 0cc: pop
   // 0cd: lload 0
   // 0ce: invokestatic kotlinx/coroutines/EventLoop_commonKt.delayToNanos (J)J
   // 0d1: lstore 8
   // 0d3: nop
   // 0d4: lload 6
   // 0d6: lload 8
   // 0d8: ladd
   // 0d9: lstore 6
   // 0db: aload 4
   // 0dd: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 0e0: aload 17
   // 0e2: aload 17
   // 0e4: aload 4
   // 0e6: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.L$0 Ljava/lang/Object;
   // 0e9: aload 17
   // 0eb: lload 6
   // 0ed: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$0 J
   // 0f0: aload 17
   // 0f2: lload 8
   // 0f4: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$1 J
   // 0f7: aload 17
   // 0f9: bipush 2
   // 0fa: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.label I
   // 0fd: invokeinterface kotlinx/coroutines/channels/SendChannel.send (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 102: dup
   // 103: aload 18
   // 105: if_acmpne 12a
   // 108: aload 18
   // 10a: areturn
   // 10b: aload 17
   // 10d: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$1 J
   // 110: lstore 8
   // 112: aload 17
   // 114: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$0 J
   // 117: lstore 6
   // 119: aload 17
   // 11b: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.L$0 Ljava/lang/Object;
   // 11e: checkcast kotlinx/coroutines/channels/SendChannel
   // 121: astore 4
   // 123: aload 16
   // 125: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 128: aload 16
   // 12a: pop
   // 12b: invokestatic kotlinx/coroutines/AbstractTimeSourceKt.getTimeSource ()Lkotlinx/coroutines/AbstractTimeSource;
   // 12e: dup
   // 12f: ifnull 138
   // 132: invokevirtual kotlinx/coroutines/AbstractTimeSource.nanoTime ()J
   // 135: goto 13c
   // 138: pop
   // 139: invokestatic java/lang/System.nanoTime ()J
   // 13c: lstore 10
   // 13e: lload 6
   // 140: lload 10
   // 142: lsub
   // 143: lconst_0
   // 144: invokestatic kotlin/ranges/RangesKt.coerceAtLeast (JJ)J
   // 147: lstore 12
   // 149: lload 12
   // 14b: lconst_0
   // 14c: lcmp
   // 14d: ifne 1bc
   // 150: lload 8
   // 152: lconst_0
   // 153: lcmp
   // 154: ifeq 1bc
   // 157: lload 8
   // 159: lload 10
   // 15b: lload 6
   // 15d: lsub
   // 15e: lload 8
   // 160: lrem
   // 161: lsub
   // 162: lstore 14
   // 164: lload 10
   // 166: lload 14
   // 168: ladd
   // 169: lstore 6
   // 16b: lload 14
   // 16d: invokestatic kotlinx/coroutines/EventLoop_commonKt.delayNanosToMillis (J)J
   // 170: aload 17
   // 172: aload 17
   // 174: aload 4
   // 176: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.L$0 Ljava/lang/Object;
   // 179: aload 17
   // 17b: lload 6
   // 17d: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$0 J
   // 180: aload 17
   // 182: lload 8
   // 184: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$1 J
   // 187: aload 17
   // 189: bipush 3
   // 18a: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.label I
   // 18d: invokestatic kotlinx/coroutines/DelayKt.delay (JLkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 190: dup
   // 191: aload 18
   // 193: if_acmpne 1b8
   // 196: aload 18
   // 198: areturn
   // 199: aload 17
   // 19b: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$1 J
   // 19e: lstore 8
   // 1a0: aload 17
   // 1a2: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$0 J
   // 1a5: lstore 6
   // 1a7: aload 17
   // 1a9: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.L$0 Ljava/lang/Object;
   // 1ac: checkcast kotlinx/coroutines/channels/SendChannel
   // 1af: astore 4
   // 1b1: aload 16
   // 1b3: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 1b6: aload 16
   // 1b8: pop
   // 1b9: goto 0d3
   // 1bc: lload 12
   // 1be: invokestatic kotlinx/coroutines/EventLoop_commonKt.delayNanosToMillis (J)J
   // 1c1: aload 17
   // 1c3: aload 17
   // 1c5: aload 4
   // 1c7: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.L$0 Ljava/lang/Object;
   // 1ca: aload 17
   // 1cc: lload 6
   // 1ce: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$0 J
   // 1d1: aload 17
   // 1d3: lload 8
   // 1d5: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$1 J
   // 1d8: aload 17
   // 1da: bipush 4
   // 1db: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.label I
   // 1de: invokestatic kotlinx/coroutines/DelayKt.delay (JLkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 1e1: dup
   // 1e2: aload 18
   // 1e4: if_acmpne 209
   // 1e7: aload 18
   // 1e9: areturn
   // 1ea: aload 17
   // 1ec: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$1 J
   // 1ef: lstore 8
   // 1f1: aload 17
   // 1f3: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.J$0 J
   // 1f6: lstore 6
   // 1f8: aload 17
   // 1fa: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedPeriodTicker$1.L$0 Ljava/lang/Object;
   // 1fd: checkcast kotlinx/coroutines/channels/SendChannel
   // 200: astore 4
   // 202: aload 16
   // 204: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 207: aload 16
   // 209: pop
   // 20a: goto 0d3
   // 20d: new java/lang/IllegalStateException
   // 210: dup
   // 211: ldc "call to 'resume' before 'invoke' with coroutine"
   // 213: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 216: athrow
}

private suspend fun fixedDelayTicker(delayMillis: Long, initialDelayMillis: Long, channel: SendChannel<Unit>) {
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
   // 000: aload 5
   // 002: instanceof kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1
   // 005: ifeq 029
   // 008: aload 5
   // 00a: checkcast kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1
   // 00d: astore 7
   // 00f: aload 7
   // 011: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.label I
   // 014: ldc -2147483648
   // 016: iand
   // 017: ifeq 029
   // 01a: aload 7
   // 01c: dup
   // 01d: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.label I
   // 020: ldc -2147483648
   // 022: isub
   // 023: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.label I
   // 026: goto 034
   // 029: new kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1
   // 02c: dup
   // 02d: aload 5
   // 02f: invokespecial kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 032: astore 7
   // 034: aload 7
   // 036: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.result Ljava/lang/Object;
   // 039: astore 6
   // 03b: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03e: astore 8
   // 040: aload 7
   // 042: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.label I
   // 045: tableswitch 219 0 3 31 70 134 192
   // 064: aload 6
   // 066: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 069: lload 2
   // 06a: aload 7
   // 06c: aload 7
   // 06e: aload 4
   // 070: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.L$0 Ljava/lang/Object;
   // 073: aload 7
   // 075: lload 0
   // 076: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.J$0 J
   // 079: aload 7
   // 07b: bipush 1
   // 07c: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.label I
   // 07f: invokestatic kotlinx/coroutines/DelayKt.delay (JLkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 082: dup
   // 083: aload 8
   // 085: if_acmpne 0a2
   // 088: aload 8
   // 08a: areturn
   // 08b: aload 7
   // 08d: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.J$0 J
   // 090: lstore 0
   // 091: aload 7
   // 093: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.L$0 Ljava/lang/Object;
   // 096: checkcast kotlinx/coroutines/channels/SendChannel
   // 099: astore 4
   // 09b: aload 6
   // 09d: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0a0: aload 6
   // 0a2: pop
   // 0a3: aload 4
   // 0a5: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 0a8: aload 7
   // 0aa: aload 7
   // 0ac: aload 4
   // 0ae: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.L$0 Ljava/lang/Object;
   // 0b1: aload 7
   // 0b3: lload 0
   // 0b4: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.J$0 J
   // 0b7: aload 7
   // 0b9: bipush 2
   // 0ba: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.label I
   // 0bd: invokeinterface kotlinx/coroutines/channels/SendChannel.send (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 0c2: dup
   // 0c3: aload 8
   // 0c5: if_acmpne 0e2
   // 0c8: aload 8
   // 0ca: areturn
   // 0cb: aload 7
   // 0cd: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.J$0 J
   // 0d0: lstore 0
   // 0d1: aload 7
   // 0d3: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.L$0 Ljava/lang/Object;
   // 0d6: checkcast kotlinx/coroutines/channels/SendChannel
   // 0d9: astore 4
   // 0db: aload 6
   // 0dd: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0e0: aload 6
   // 0e2: pop
   // 0e3: lload 0
   // 0e4: aload 7
   // 0e6: aload 7
   // 0e8: aload 4
   // 0ea: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.L$0 Ljava/lang/Object;
   // 0ed: aload 7
   // 0ef: lload 0
   // 0f0: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.J$0 J
   // 0f3: aload 7
   // 0f5: bipush 3
   // 0f6: putfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.label I
   // 0f9: invokestatic kotlinx/coroutines/DelayKt.delay (JLkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 0fc: dup
   // 0fd: aload 8
   // 0ff: if_acmpne 11c
   // 102: aload 8
   // 104: areturn
   // 105: aload 7
   // 107: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.J$0 J
   // 10a: lstore 0
   // 10b: aload 7
   // 10d: getfield kotlinx/coroutines/channels/TickerChannelsKt$fixedDelayTicker$1.L$0 Ljava/lang/Object;
   // 110: checkcast kotlinx/coroutines/channels/SendChannel
   // 113: astore 4
   // 115: aload 6
   // 117: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 11a: aload 6
   // 11c: pop
   // 11d: goto 0a3
   // 120: new java/lang/IllegalStateException
   // 123: dup
   // 124: ldc "call to 'resume' before 'invoke' with coroutine"
   // 126: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 129: athrow
}
