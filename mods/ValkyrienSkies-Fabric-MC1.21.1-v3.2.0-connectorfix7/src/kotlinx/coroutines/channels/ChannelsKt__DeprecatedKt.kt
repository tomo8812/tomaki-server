@file:JvmMultifileClass
@file:JvmName("ChannelsKt")
@file:SourceDebugExtension(["SMAP\nDeprecated.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Deprecated.kt\nkotlinx/coroutines/channels/ChannelsKt__DeprecatedKt\n+ 2 Channels.common.kt\nkotlinx/coroutines/channels/ChannelsKt__Channels_commonKt\n*L\n1#1,479:1\n82#2,11:480\n82#2,11:491\n82#2,11:502\n82#2,11:513\n105#2:524\n82#2,6:525\n106#2:531\n92#2:532\n107#2:533\n88#2,3:534\n82#2,11:537\n105#2:548\n82#2,6:549\n106#2,2:555\n92#2:557\n88#2,3:558\n82#2,11:561\n82#2,11:572\n82#2,11:583\n105#2:594\n82#2,6:595\n106#2,2:601\n92#2:603\n88#2,3:604\n105#2:607\n82#2,6:608\n106#2,2:614\n92#2:616\n88#2,3:617\n105#2:620\n82#2,6:621\n106#2,2:627\n92#2:629\n88#2,3:630\n105#2:633\n82#2,6:634\n106#2,2:640\n92#2:642\n88#2,3:643\n105#2:646\n82#2,6:647\n106#2,2:653\n92#2:655\n88#2,3:656\n82#2,11:659\n105#2:670\n82#2,6:671\n106#2,2:677\n92#2:679\n88#2,3:680\n82#2,11:683\n82#2,11:694\n82#2,11:705\n*S KotlinDebug\n*F\n+ 1 Deprecated.kt\nkotlinx/coroutines/channels/ChannelsKt__DeprecatedKt\n*L\n34#1:480,11\n49#1:491,11\n63#1:502,11\n73#1:513,11\n84#1:524\n84#1:525,6\n84#1:531\n84#1:532\n84#1:533\n84#1:534,3\n95#1:537,11\n110#1:548\n110#1:549,6\n110#1:555,2\n110#1:557\n110#1:558,3\n121#1:561,11\n134#1:572,11\n147#1:583,11\n232#1:594\n232#1:595,6\n232#1:601,2\n232#1:603\n232#1:604,3\n241#1:607\n241#1:608,6\n241#1:614,2\n241#1:616\n241#1:617,3\n277#1:620\n277#1:621,6\n277#1:627,2\n277#1:629\n277#1:630,3\n285#1:633\n285#1:634,6\n285#1:640,2\n285#1:642\n285#1:643,3\n298#1:646\n298#1:647,6\n298#1:653,2\n298#1:655\n298#1:656,3\n403#1:659,11\n411#1:670\n411#1:671,6\n411#1:677,2\n411#1:679\n411#1:680,3\n418#1:683,11\n432#1:694,11\n446#1:705,11\n*E\n"])

package kotlinx.coroutines.channels

import java.util.ArrayList
import java.util.Comparator
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.GlobalScope

@PublishedApi
internal fun consumesAll(vararg channels: ReceiveChannel<*>): (Throwable?) -> Unit {
   return { cause: Throwable? ->
      var exception: java.lang.Throwable = null

      for (var6 in channels) {
         try {
            ChannelsKt.cancelConsumed(var6, cause)
         } catch (var10: java.lang.Throwable) {
            if (exception == null) {
               exception = var10
            } else {
               ExceptionsKt.addSuppressed(exception, var10)
            }
         }
      }

      if (exception != null) {
         throw exception
      }
   } as (java.lang.Throwable?) -> Unit
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.elementAt(index: Int): Any {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1
   // 00b: astore 13
   // 00d: aload 13
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 13
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 13
   // 031: aload 13
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.result Ljava/lang/Object;
   // 036: astore 12
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 14
   // 03d: aload 13
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.label I
   // 042: tableswitch 282 0 1 22 141
   // 058: aload 12
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: aload 0
   // 05e: astore 3
   // 05f: bipush 0
   // 060: istore 4
   // 062: nop
   // 063: aconst_null
   // 064: astore 5
   // 066: nop
   // 067: aload 3
   // 068: astore 6
   // 06a: bipush 0
   // 06b: istore 7
   // 06d: iload 1
   // 06e: ifge 091
   // 071: new java/lang/IndexOutOfBoundsException
   // 074: dup
   // 075: new java/lang/StringBuilder
   // 078: dup
   // 079: invokespecial java/lang/StringBuilder.<init> ()V
   // 07c: ldc "ReceiveChannel doesn't contain element at index "
   // 07e: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
   // 081: iload 1
   // 082: invokevirtual java/lang/StringBuilder.append (I)Ljava/lang/StringBuilder;
   // 085: bipush 46
   // 087: invokevirtual java/lang/StringBuilder.append (C)Ljava/lang/StringBuilder;
   // 08a: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
   // 08d: invokespecial java/lang/IndexOutOfBoundsException.<init> (Ljava/lang/String;)V
   // 090: athrow
   // 091: bipush 0
   // 092: istore 8
   // 094: aload 6
   // 096: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 09b: astore 9
   // 09d: aload 9
   // 09f: aload 13
   // 0a1: aload 13
   // 0a3: aload 3
   // 0a4: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.L$0 Ljava/lang/Object;
   // 0a7: aload 13
   // 0a9: aload 9
   // 0ab: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.L$1 Ljava/lang/Object;
   // 0ae: aload 13
   // 0b0: iload 1
   // 0b1: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.I$0 I
   // 0b4: aload 13
   // 0b6: iload 8
   // 0b8: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.I$1 I
   // 0bb: aload 13
   // 0bd: bipush 1
   // 0be: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.label I
   // 0c1: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0c6: dup
   // 0c7: aload 14
   // 0c9: if_acmpne 100
   // 0cc: aload 14
   // 0ce: areturn
   // 0cf: bipush 0
   // 0d0: istore 4
   // 0d2: bipush 0
   // 0d3: istore 7
   // 0d5: aload 13
   // 0d7: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.I$1 I
   // 0da: istore 8
   // 0dc: aload 13
   // 0de: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.I$0 I
   // 0e1: istore 1
   // 0e2: aload 13
   // 0e4: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.L$1 Ljava/lang/Object;
   // 0e7: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ea: astore 9
   // 0ec: aconst_null
   // 0ed: astore 5
   // 0ef: aload 13
   // 0f1: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAt$1.L$0 Ljava/lang/Object;
   // 0f4: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0f7: astore 3
   // 0f8: nop
   // 0f9: aload 12
   // 0fb: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0fe: aload 12
   // 100: checkcast java/lang/Boolean
   // 103: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 106: ifeq 128
   // 109: aload 9
   // 10b: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 110: astore 10
   // 112: iload 1
   // 113: iload 8
   // 115: iinc 8 1
   // 118: if_icmpne 09d
   // 11b: aload 10
   // 11d: astore 11
   // 11f: aload 3
   // 120: aload 5
   // 122: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 125: aload 11
   // 127: areturn
   // 128: new java/lang/IndexOutOfBoundsException
   // 12b: dup
   // 12c: new java/lang/StringBuilder
   // 12f: dup
   // 130: invokespecial java/lang/StringBuilder.<init> ()V
   // 133: ldc "ReceiveChannel doesn't contain element at index "
   // 135: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
   // 138: iload 1
   // 139: invokevirtual java/lang/StringBuilder.append (I)Ljava/lang/StringBuilder;
   // 13c: bipush 46
   // 13e: invokevirtual java/lang/StringBuilder.append (C)Ljava/lang/StringBuilder;
   // 141: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
   // 144: invokespecial java/lang/IndexOutOfBoundsException.<init> (Ljava/lang/String;)V
   // 147: athrow
   // 148: astore 6
   // 14a: aload 6
   // 14c: astore 5
   // 14e: aload 6
   // 150: athrow
   // 151: astore 6
   // 153: aload 3
   // 154: aload 5
   // 156: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 159: aload 6
   // 15b: athrow
   // 15c: new java/lang/IllegalStateException
   // 15f: dup
   // 160: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 163: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 166: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.elementAtOrNull(index: Int): Any? {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1
   // 00b: astore 15
   // 00d: aload 15
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 15
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 15
   // 031: aload 15
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.result Ljava/lang/Object;
   // 036: astore 14
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 16
   // 03d: aload 15
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.label I
   // 042: tableswitch 242 0 1 22 121
   // 058: aload 14
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: aload 0
   // 05e: astore 3
   // 05f: bipush 0
   // 060: istore 4
   // 062: nop
   // 063: aconst_null
   // 064: astore 5
   // 066: nop
   // 067: aload 3
   // 068: astore 6
   // 06a: bipush 0
   // 06b: istore 7
   // 06d: iload 1
   // 06e: ifge 07d
   // 071: aconst_null
   // 072: astore 13
   // 074: aload 3
   // 075: aload 5
   // 077: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 07a: aload 13
   // 07c: areturn
   // 07d: bipush 0
   // 07e: istore 8
   // 080: aload 6
   // 082: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 087: astore 9
   // 089: aload 9
   // 08b: aload 15
   // 08d: aload 15
   // 08f: aload 3
   // 090: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.L$0 Ljava/lang/Object;
   // 093: aload 15
   // 095: aload 9
   // 097: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.L$1 Ljava/lang/Object;
   // 09a: aload 15
   // 09c: iload 1
   // 09d: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.I$0 I
   // 0a0: aload 15
   // 0a2: iload 8
   // 0a4: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.I$1 I
   // 0a7: aload 15
   // 0a9: bipush 1
   // 0aa: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.label I
   // 0ad: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0b2: dup
   // 0b3: aload 16
   // 0b5: if_acmpne 0ec
   // 0b8: aload 16
   // 0ba: areturn
   // 0bb: bipush 0
   // 0bc: istore 7
   // 0be: bipush 0
   // 0bf: istore 4
   // 0c1: aload 15
   // 0c3: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.I$1 I
   // 0c6: istore 8
   // 0c8: aload 15
   // 0ca: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.I$0 I
   // 0cd: istore 1
   // 0ce: aload 15
   // 0d0: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.L$1 Ljava/lang/Object;
   // 0d3: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0d6: astore 9
   // 0d8: aconst_null
   // 0d9: astore 5
   // 0db: aload 15
   // 0dd: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$elementAtOrNull$1.L$0 Ljava/lang/Object;
   // 0e0: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0e3: astore 3
   // 0e4: nop
   // 0e5: aload 14
   // 0e7: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0ea: aload 14
   // 0ec: checkcast java/lang/Boolean
   // 0ef: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0f2: ifeq 114
   // 0f5: aload 9
   // 0f7: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0fc: astore 10
   // 0fe: iload 1
   // 0ff: iload 8
   // 101: iinc 8 1
   // 104: if_icmpne 089
   // 107: aload 10
   // 109: astore 12
   // 10b: aload 3
   // 10c: aload 5
   // 10e: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 111: aload 12
   // 113: areturn
   // 114: aconst_null
   // 115: astore 11
   // 117: aload 3
   // 118: aload 5
   // 11a: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 11d: aload 11
   // 11f: areturn
   // 120: astore 6
   // 122: aload 6
   // 124: astore 5
   // 126: aload 6
   // 128: athrow
   // 129: astore 6
   // 12b: aload 3
   // 12c: aload 5
   // 12e: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 131: aload 6
   // 133: athrow
   // 134: new java/lang/IllegalStateException
   // 137: dup
   // 138: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 13b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 13e: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.first(): Any {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 1
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1
   // 004: ifeq 027
   // 007: aload 1
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1
   // 00b: astore 10
   // 00d: aload 10
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 10
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1
   // 02a: dup
   // 02b: aload 1
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 10
   // 031: aload 10
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.result Ljava/lang/Object;
   // 036: astore 9
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 11
   // 03d: aload 10
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.label I
   // 042: tableswitch 181 0 1 22 88
   // 058: aload 9
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: aload 0
   // 05e: astore 2
   // 05f: bipush 0
   // 060: istore 3
   // 061: nop
   // 062: aconst_null
   // 063: astore 4
   // 065: nop
   // 066: aload 2
   // 067: astore 5
   // 069: bipush 0
   // 06a: istore 6
   // 06c: aload 5
   // 06e: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 073: astore 7
   // 075: aload 7
   // 077: aload 10
   // 079: aload 10
   // 07b: aload 2
   // 07c: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.L$0 Ljava/lang/Object;
   // 07f: aload 10
   // 081: aload 7
   // 083: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.L$1 Ljava/lang/Object;
   // 086: aload 10
   // 088: bipush 1
   // 089: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.label I
   // 08c: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 091: dup
   // 092: aload 11
   // 094: if_acmpne 0bd
   // 097: aload 11
   // 099: areturn
   // 09a: bipush 0
   // 09b: istore 3
   // 09c: bipush 0
   // 09d: istore 6
   // 09f: aload 10
   // 0a1: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.L$1 Ljava/lang/Object;
   // 0a4: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0a7: astore 7
   // 0a9: aconst_null
   // 0aa: astore 4
   // 0ac: aload 10
   // 0ae: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$first$1.L$0 Ljava/lang/Object;
   // 0b1: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0b4: astore 2
   // 0b5: nop
   // 0b6: aload 9
   // 0b8: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0bb: aload 9
   // 0bd: checkcast java/lang/Boolean
   // 0c0: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0c3: ifne 0d1
   // 0c6: new java/util/NoSuchElementException
   // 0c9: dup
   // 0ca: ldc_w "ReceiveChannel is empty."
   // 0cd: invokespecial java/util/NoSuchElementException.<init> (Ljava/lang/String;)V
   // 0d0: athrow
   // 0d1: aload 7
   // 0d3: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0d8: astore 8
   // 0da: aload 2
   // 0db: aload 4
   // 0dd: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 0e0: aload 8
   // 0e2: areturn
   // 0e3: astore 5
   // 0e5: aload 5
   // 0e7: astore 4
   // 0e9: aload 5
   // 0eb: athrow
   // 0ec: astore 5
   // 0ee: aload 2
   // 0ef: aload 4
   // 0f1: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 0f4: aload 5
   // 0f6: athrow
   // 0f7: new java/lang/IllegalStateException
   // 0fa: dup
   // 0fb: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 0fe: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 101: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.firstOrNull(): Any? {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 1
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1
   // 004: ifeq 027
   // 007: aload 1
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1
   // 00b: astore 11
   // 00d: aload 11
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 11
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1
   // 02a: dup
   // 02b: aload 1
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 11
   // 031: aload 11
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.result Ljava/lang/Object;
   // 036: astore 10
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 12
   // 03d: aload 11
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.label I
   // 042: tableswitch 182 0 1 22 88
   // 058: aload 10
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: aload 0
   // 05e: astore 2
   // 05f: bipush 0
   // 060: istore 3
   // 061: nop
   // 062: aconst_null
   // 063: astore 4
   // 065: nop
   // 066: aload 2
   // 067: astore 5
   // 069: bipush 0
   // 06a: istore 6
   // 06c: aload 5
   // 06e: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 073: astore 7
   // 075: aload 7
   // 077: aload 11
   // 079: aload 11
   // 07b: aload 2
   // 07c: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.L$0 Ljava/lang/Object;
   // 07f: aload 11
   // 081: aload 7
   // 083: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.L$1 Ljava/lang/Object;
   // 086: aload 11
   // 088: bipush 1
   // 089: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.label I
   // 08c: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 091: dup
   // 092: aload 12
   // 094: if_acmpne 0bd
   // 097: aload 12
   // 099: areturn
   // 09a: bipush 0
   // 09b: istore 3
   // 09c: bipush 0
   // 09d: istore 6
   // 09f: aload 11
   // 0a1: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.L$1 Ljava/lang/Object;
   // 0a4: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0a7: astore 7
   // 0a9: aconst_null
   // 0aa: astore 4
   // 0ac: aload 11
   // 0ae: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$firstOrNull$1.L$0 Ljava/lang/Object;
   // 0b1: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0b4: astore 2
   // 0b5: nop
   // 0b6: aload 10
   // 0b8: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0bb: aload 10
   // 0bd: checkcast java/lang/Boolean
   // 0c0: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0c3: ifne 0d2
   // 0c6: aconst_null
   // 0c7: astore 9
   // 0c9: aload 2
   // 0ca: aload 4
   // 0cc: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 0cf: aload 9
   // 0d1: areturn
   // 0d2: aload 7
   // 0d4: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0d9: astore 8
   // 0db: aload 2
   // 0dc: aload 4
   // 0de: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 0e1: aload 8
   // 0e3: areturn
   // 0e4: astore 5
   // 0e6: aload 5
   // 0e8: astore 4
   // 0ea: aload 5
   // 0ec: athrow
   // 0ed: astore 5
   // 0ef: aload 2
   // 0f0: aload 4
   // 0f2: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 0f5: aload 5
   // 0f7: athrow
   // 0f8: new java/lang/IllegalStateException
   // 0fb: dup
   // 0fc: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 0ff: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 102: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.indexOf(element: Any): Int {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1
   // 00b: astore 18
   // 00d: aload 18
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 18
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 18
   // 031: aload 18
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.result Ljava/lang/Object;
   // 036: astore 17
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 19
   // 03d: aload 18
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.label I
   // 042: tableswitch 288 0 1 22 119
   // 058: aload 17
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: new kotlin/jvm/internal/Ref$IntRef
   // 060: dup
   // 061: invokespecial kotlin/jvm/internal/Ref$IntRef.<init> ()V
   // 064: astore 3
   // 065: aload 0
   // 066: astore 4
   // 068: bipush 0
   // 069: istore 5
   // 06b: aload 4
   // 06d: astore 6
   // 06f: bipush 0
   // 070: istore 7
   // 072: nop
   // 073: aconst_null
   // 074: astore 8
   // 076: nop
   // 077: aload 6
   // 079: astore 9
   // 07b: bipush 0
   // 07c: istore 10
   // 07e: aload 9
   // 080: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 085: astore 11
   // 087: aload 11
   // 089: aload 18
   // 08b: aload 18
   // 08d: aload 1
   // 08e: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.L$0 Ljava/lang/Object;
   // 091: aload 18
   // 093: aload 3
   // 094: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.L$1 Ljava/lang/Object;
   // 097: aload 18
   // 099: aload 6
   // 09b: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.L$2 Ljava/lang/Object;
   // 09e: aload 18
   // 0a0: aload 11
   // 0a2: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.L$3 Ljava/lang/Object;
   // 0a5: aload 18
   // 0a7: bipush 1
   // 0a8: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.label I
   // 0ab: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0b0: dup
   // 0b1: aload 19
   // 0b3: if_acmpne 0f0
   // 0b6: aload 19
   // 0b8: areturn
   // 0b9: bipush 0
   // 0ba: istore 5
   // 0bc: bipush 0
   // 0bd: istore 7
   // 0bf: bipush 0
   // 0c0: istore 10
   // 0c2: aload 18
   // 0c4: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.L$3 Ljava/lang/Object;
   // 0c7: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ca: astore 11
   // 0cc: aconst_null
   // 0cd: astore 8
   // 0cf: aload 18
   // 0d1: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.L$2 Ljava/lang/Object;
   // 0d4: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0d7: astore 6
   // 0d9: aload 18
   // 0db: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.L$1 Ljava/lang/Object;
   // 0de: checkcast kotlin/jvm/internal/Ref$IntRef
   // 0e1: astore 3
   // 0e2: aload 18
   // 0e4: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$indexOf$1.L$0 Ljava/lang/Object;
   // 0e7: astore 1
   // 0e8: nop
   // 0e9: aload 17
   // 0eb: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0ee: aload 17
   // 0f0: checkcast java/lang/Boolean
   // 0f3: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0f6: ifeq 137
   // 0f9: aload 11
   // 0fb: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 100: astore 12
   // 102: aload 12
   // 104: astore 13
   // 106: bipush 0
   // 107: istore 14
   // 109: aload 1
   // 10a: aload 13
   // 10c: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
   // 10f: ifeq 125
   // 112: aload 3
   // 113: getfield kotlin/jvm/internal/Ref$IntRef.element I
   // 116: invokestatic kotlin/coroutines/jvm/internal/Boxing.boxInt (I)Ljava/lang/Integer;
   // 119: astore 16
   // 11b: aload 6
   // 11d: aload 8
   // 11f: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 122: aload 16
   // 124: areturn
   // 125: aload 3
   // 126: getfield kotlin/jvm/internal/Ref$IntRef.element I
   // 129: istore 15
   // 12b: aload 3
   // 12c: iload 15
   // 12e: bipush 1
   // 12f: iadd
   // 130: putfield kotlin/jvm/internal/Ref$IntRef.element I
   // 133: nop
   // 134: goto 087
   // 137: nop
   // 138: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 13b: astore 9
   // 13d: aload 6
   // 13f: aload 8
   // 141: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 144: goto 15c
   // 147: astore 9
   // 149: aload 9
   // 14b: astore 8
   // 14d: aload 9
   // 14f: athrow
   // 150: astore 9
   // 152: aload 6
   // 154: aload 8
   // 156: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 159: aload 9
   // 15b: athrow
   // 15c: nop
   // 15d: bipush -1
   // 15e: invokestatic kotlin/coroutines/jvm/internal/Boxing.boxInt (I)Ljava/lang/Integer;
   // 161: areturn
   // 162: new java/lang/IllegalStateException
   // 165: dup
   // 166: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 169: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 16c: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.last(): Any {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 1
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1
   // 004: ifeq 027
   // 007: aload 1
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1
   // 00b: astore 11
   // 00d: aload 11
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 11
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1
   // 02a: dup
   // 02b: aload 1
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 11
   // 031: aload 11
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.result Ljava/lang/Object;
   // 036: astore 10
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 12
   // 03d: aload 11
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.label I
   // 042: tableswitch 296 0 2 26 92 200
   // 05c: aload 10
   // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 061: aload 0
   // 062: astore 2
   // 063: bipush 0
   // 064: istore 3
   // 065: nop
   // 066: aconst_null
   // 067: astore 4
   // 069: nop
   // 06a: aload 2
   // 06b: astore 5
   // 06d: bipush 0
   // 06e: istore 6
   // 070: aload 5
   // 072: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 077: astore 7
   // 079: aload 7
   // 07b: aload 11
   // 07d: aload 11
   // 07f: aload 2
   // 080: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$0 Ljava/lang/Object;
   // 083: aload 11
   // 085: aload 7
   // 087: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$1 Ljava/lang/Object;
   // 08a: aload 11
   // 08c: bipush 1
   // 08d: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.label I
   // 090: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 095: dup
   // 096: aload 12
   // 098: if_acmpne 0c1
   // 09b: aload 12
   // 09d: areturn
   // 09e: bipush 0
   // 09f: istore 3
   // 0a0: bipush 0
   // 0a1: istore 6
   // 0a3: aload 11
   // 0a5: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$1 Ljava/lang/Object;
   // 0a8: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ab: astore 7
   // 0ad: aconst_null
   // 0ae: astore 4
   // 0b0: aload 11
   // 0b2: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$0 Ljava/lang/Object;
   // 0b5: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0b8: astore 2
   // 0b9: nop
   // 0ba: aload 10
   // 0bc: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0bf: aload 10
   // 0c1: checkcast java/lang/Boolean
   // 0c4: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0c7: ifne 0d5
   // 0ca: new java/util/NoSuchElementException
   // 0cd: dup
   // 0ce: ldc_w "ReceiveChannel is empty."
   // 0d1: invokespecial java/util/NoSuchElementException.<init> (Ljava/lang/String;)V
   // 0d4: athrow
   // 0d5: aload 7
   // 0d7: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0dc: astore 8
   // 0de: aload 7
   // 0e0: aload 11
   // 0e2: aload 11
   // 0e4: aload 2
   // 0e5: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$0 Ljava/lang/Object;
   // 0e8: aload 11
   // 0ea: aload 7
   // 0ec: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$1 Ljava/lang/Object;
   // 0ef: aload 11
   // 0f1: aload 8
   // 0f3: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$2 Ljava/lang/Object;
   // 0f6: aload 11
   // 0f8: bipush 2
   // 0f9: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.label I
   // 0fc: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 101: dup
   // 102: aload 12
   // 104: if_acmpne 134
   // 107: aload 12
   // 109: areturn
   // 10a: bipush 0
   // 10b: istore 3
   // 10c: bipush 0
   // 10d: istore 6
   // 10f: aload 11
   // 111: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$2 Ljava/lang/Object;
   // 114: astore 8
   // 116: aload 11
   // 118: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$1 Ljava/lang/Object;
   // 11b: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 11e: astore 7
   // 120: aconst_null
   // 121: astore 4
   // 123: aload 11
   // 125: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$last$1.L$0 Ljava/lang/Object;
   // 128: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 12b: astore 2
   // 12c: nop
   // 12d: aload 10
   // 12f: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 132: aload 10
   // 134: checkcast java/lang/Boolean
   // 137: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 13a: ifeq 149
   // 13d: aload 7
   // 13f: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 144: astore 8
   // 146: goto 0de
   // 149: aload 8
   // 14b: astore 9
   // 14d: aload 2
   // 14e: aload 4
   // 150: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 153: aload 9
   // 155: areturn
   // 156: astore 5
   // 158: aload 5
   // 15a: astore 4
   // 15c: aload 5
   // 15e: athrow
   // 15f: astore 5
   // 161: aload 2
   // 162: aload 4
   // 164: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 167: aload 5
   // 169: athrow
   // 16a: new java/lang/IllegalStateException
   // 16d: dup
   // 16e: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 171: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 174: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.lastIndexOf(element: Any): Int {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1
   // 00b: astore 18
   // 00d: aload 18
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 18
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 18
   // 031: aload 18
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.result Ljava/lang/Object;
   // 036: astore 17
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 19
   // 03d: aload 18
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.label I
   // 042: tableswitch 314 0 1 22 140
   // 058: aload 17
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: new kotlin/jvm/internal/Ref$IntRef
   // 060: dup
   // 061: invokespecial kotlin/jvm/internal/Ref$IntRef.<init> ()V
   // 064: astore 3
   // 065: aload 3
   // 066: bipush -1
   // 067: putfield kotlin/jvm/internal/Ref$IntRef.element I
   // 06a: new kotlin/jvm/internal/Ref$IntRef
   // 06d: dup
   // 06e: invokespecial kotlin/jvm/internal/Ref$IntRef.<init> ()V
   // 071: astore 4
   // 073: aload 0
   // 074: astore 5
   // 076: bipush 0
   // 077: istore 6
   // 079: aload 5
   // 07b: astore 7
   // 07d: bipush 0
   // 07e: istore 8
   // 080: nop
   // 081: aconst_null
   // 082: astore 9
   // 084: nop
   // 085: aload 7
   // 087: astore 10
   // 089: bipush 0
   // 08a: istore 11
   // 08c: aload 10
   // 08e: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 093: astore 12
   // 095: aload 12
   // 097: aload 18
   // 099: aload 18
   // 09b: aload 1
   // 09c: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$0 Ljava/lang/Object;
   // 09f: aload 18
   // 0a1: aload 3
   // 0a2: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$1 Ljava/lang/Object;
   // 0a5: aload 18
   // 0a7: aload 4
   // 0a9: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$2 Ljava/lang/Object;
   // 0ac: aload 18
   // 0ae: aload 7
   // 0b0: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$3 Ljava/lang/Object;
   // 0b3: aload 18
   // 0b5: aload 12
   // 0b7: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$4 Ljava/lang/Object;
   // 0ba: aload 18
   // 0bc: bipush 1
   // 0bd: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.label I
   // 0c0: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0c5: dup
   // 0c6: aload 19
   // 0c8: if_acmpne 10f
   // 0cb: aload 19
   // 0cd: areturn
   // 0ce: bipush 0
   // 0cf: istore 6
   // 0d1: bipush 0
   // 0d2: istore 8
   // 0d4: bipush 0
   // 0d5: istore 11
   // 0d7: aload 18
   // 0d9: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$4 Ljava/lang/Object;
   // 0dc: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0df: astore 12
   // 0e1: aconst_null
   // 0e2: astore 9
   // 0e4: aload 18
   // 0e6: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$3 Ljava/lang/Object;
   // 0e9: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0ec: astore 7
   // 0ee: aload 18
   // 0f0: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$2 Ljava/lang/Object;
   // 0f3: checkcast kotlin/jvm/internal/Ref$IntRef
   // 0f6: astore 4
   // 0f8: aload 18
   // 0fa: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$1 Ljava/lang/Object;
   // 0fd: checkcast kotlin/jvm/internal/Ref$IntRef
   // 100: astore 3
   // 101: aload 18
   // 103: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastIndexOf$1.L$0 Ljava/lang/Object;
   // 106: astore 1
   // 107: nop
   // 108: aload 17
   // 10a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 10d: aload 17
   // 10f: checkcast java/lang/Boolean
   // 112: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 115: ifeq 14e
   // 118: aload 12
   // 11a: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 11f: astore 13
   // 121: aload 13
   // 123: astore 14
   // 125: bipush 0
   // 126: istore 15
   // 128: aload 1
   // 129: aload 14
   // 12b: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
   // 12e: ifeq 13a
   // 131: aload 3
   // 132: aload 4
   // 134: getfield kotlin/jvm/internal/Ref$IntRef.element I
   // 137: putfield kotlin/jvm/internal/Ref$IntRef.element I
   // 13a: aload 4
   // 13c: getfield kotlin/jvm/internal/Ref$IntRef.element I
   // 13f: istore 16
   // 141: aload 4
   // 143: iload 16
   // 145: bipush 1
   // 146: iadd
   // 147: putfield kotlin/jvm/internal/Ref$IntRef.element I
   // 14a: nop
   // 14b: goto 095
   // 14e: nop
   // 14f: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 152: astore 10
   // 154: aload 7
   // 156: aload 9
   // 158: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 15b: goto 173
   // 15e: astore 10
   // 160: aload 10
   // 162: astore 9
   // 164: aload 10
   // 166: athrow
   // 167: astore 10
   // 169: aload 7
   // 16b: aload 9
   // 16d: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 170: aload 10
   // 172: athrow
   // 173: nop
   // 174: aload 3
   // 175: getfield kotlin/jvm/internal/Ref$IntRef.element I
   // 178: invokestatic kotlin/coroutines/jvm/internal/Boxing.boxInt (I)Ljava/lang/Integer;
   // 17b: areturn
   // 17c: new java/lang/IllegalStateException
   // 17f: dup
   // 180: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 183: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 186: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.lastOrNull(): Any? {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 1
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1
   // 004: ifeq 027
   // 007: aload 1
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1
   // 00b: astore 12
   // 00d: aload 12
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 12
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1
   // 02a: dup
   // 02b: aload 1
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 12
   // 031: aload 12
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.result Ljava/lang/Object;
   // 036: astore 11
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 13
   // 03d: aload 12
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.label I
   // 042: tableswitch 297 0 2 26 92 201
   // 05c: aload 11
   // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 061: aload 0
   // 062: astore 2
   // 063: bipush 0
   // 064: istore 3
   // 065: nop
   // 066: aconst_null
   // 067: astore 4
   // 069: nop
   // 06a: aload 2
   // 06b: astore 5
   // 06d: bipush 0
   // 06e: istore 6
   // 070: aload 5
   // 072: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 077: astore 7
   // 079: aload 7
   // 07b: aload 12
   // 07d: aload 12
   // 07f: aload 2
   // 080: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$0 Ljava/lang/Object;
   // 083: aload 12
   // 085: aload 7
   // 087: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$1 Ljava/lang/Object;
   // 08a: aload 12
   // 08c: bipush 1
   // 08d: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.label I
   // 090: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 095: dup
   // 096: aload 13
   // 098: if_acmpne 0c1
   // 09b: aload 13
   // 09d: areturn
   // 09e: bipush 0
   // 09f: istore 3
   // 0a0: bipush 0
   // 0a1: istore 6
   // 0a3: aload 12
   // 0a5: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$1 Ljava/lang/Object;
   // 0a8: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ab: astore 7
   // 0ad: aconst_null
   // 0ae: astore 4
   // 0b0: aload 12
   // 0b2: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$0 Ljava/lang/Object;
   // 0b5: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0b8: astore 2
   // 0b9: nop
   // 0ba: aload 11
   // 0bc: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0bf: aload 11
   // 0c1: checkcast java/lang/Boolean
   // 0c4: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0c7: ifne 0d6
   // 0ca: aconst_null
   // 0cb: astore 10
   // 0cd: aload 2
   // 0ce: aload 4
   // 0d0: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 0d3: aload 10
   // 0d5: areturn
   // 0d6: aload 7
   // 0d8: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0dd: astore 8
   // 0df: aload 7
   // 0e1: aload 12
   // 0e3: aload 12
   // 0e5: aload 2
   // 0e6: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$0 Ljava/lang/Object;
   // 0e9: aload 12
   // 0eb: aload 7
   // 0ed: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$1 Ljava/lang/Object;
   // 0f0: aload 12
   // 0f2: aload 8
   // 0f4: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$2 Ljava/lang/Object;
   // 0f7: aload 12
   // 0f9: bipush 2
   // 0fa: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.label I
   // 0fd: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 102: dup
   // 103: aload 13
   // 105: if_acmpne 135
   // 108: aload 13
   // 10a: areturn
   // 10b: bipush 0
   // 10c: istore 6
   // 10e: bipush 0
   // 10f: istore 3
   // 110: aload 12
   // 112: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$2 Ljava/lang/Object;
   // 115: astore 8
   // 117: aload 12
   // 119: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$1 Ljava/lang/Object;
   // 11c: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 11f: astore 7
   // 121: aconst_null
   // 122: astore 4
   // 124: aload 12
   // 126: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$lastOrNull$1.L$0 Ljava/lang/Object;
   // 129: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 12c: astore 2
   // 12d: nop
   // 12e: aload 11
   // 130: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 133: aload 11
   // 135: checkcast java/lang/Boolean
   // 138: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 13b: ifeq 14a
   // 13e: aload 7
   // 140: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 145: astore 8
   // 147: goto 0df
   // 14a: aload 8
   // 14c: astore 9
   // 14e: aload 2
   // 14f: aload 4
   // 151: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 154: aload 9
   // 156: areturn
   // 157: astore 5
   // 159: aload 5
   // 15b: astore 4
   // 15d: aload 5
   // 15f: athrow
   // 160: astore 5
   // 162: aload 2
   // 163: aload 4
   // 165: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 168: aload 5
   // 16a: athrow
   // 16b: new java/lang/IllegalStateException
   // 16e: dup
   // 16f: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 172: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 175: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.single(): Any {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 1
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1
   // 004: ifeq 027
   // 007: aload 1
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1
   // 00b: astore 11
   // 00d: aload 11
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 11
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1
   // 02a: dup
   // 02b: aload 1
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 11
   // 031: aload 11
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.result Ljava/lang/Object;
   // 036: astore 10
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 12
   // 03d: aload 11
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.label I
   // 042: tableswitch 278 0 2 26 92 193
   // 05c: aload 10
   // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 061: aload 0
   // 062: astore 2
   // 063: bipush 0
   // 064: istore 3
   // 065: nop
   // 066: aconst_null
   // 067: astore 4
   // 069: nop
   // 06a: aload 2
   // 06b: astore 5
   // 06d: bipush 0
   // 06e: istore 6
   // 070: aload 5
   // 072: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 077: astore 7
   // 079: aload 7
   // 07b: aload 11
   // 07d: aload 11
   // 07f: aload 2
   // 080: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.L$0 Ljava/lang/Object;
   // 083: aload 11
   // 085: aload 7
   // 087: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.L$1 Ljava/lang/Object;
   // 08a: aload 11
   // 08c: bipush 1
   // 08d: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.label I
   // 090: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 095: dup
   // 096: aload 12
   // 098: if_acmpne 0c1
   // 09b: aload 12
   // 09d: areturn
   // 09e: bipush 0
   // 09f: istore 3
   // 0a0: bipush 0
   // 0a1: istore 6
   // 0a3: aload 11
   // 0a5: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.L$1 Ljava/lang/Object;
   // 0a8: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ab: astore 7
   // 0ad: aconst_null
   // 0ae: astore 4
   // 0b0: aload 11
   // 0b2: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.L$0 Ljava/lang/Object;
   // 0b5: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0b8: astore 2
   // 0b9: nop
   // 0ba: aload 10
   // 0bc: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0bf: aload 10
   // 0c1: checkcast java/lang/Boolean
   // 0c4: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0c7: ifne 0d5
   // 0ca: new java/util/NoSuchElementException
   // 0cd: dup
   // 0ce: ldc_w "ReceiveChannel is empty."
   // 0d1: invokespecial java/util/NoSuchElementException.<init> (Ljava/lang/String;)V
   // 0d4: athrow
   // 0d5: aload 7
   // 0d7: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0dc: astore 8
   // 0de: aload 7
   // 0e0: aload 11
   // 0e2: aload 11
   // 0e4: aload 2
   // 0e5: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.L$0 Ljava/lang/Object;
   // 0e8: aload 11
   // 0ea: aload 8
   // 0ec: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.L$1 Ljava/lang/Object;
   // 0ef: aload 11
   // 0f1: bipush 2
   // 0f2: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.label I
   // 0f5: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0fa: dup
   // 0fb: aload 12
   // 0fd: if_acmpne 123
   // 100: aload 12
   // 102: areturn
   // 103: bipush 0
   // 104: istore 3
   // 105: bipush 0
   // 106: istore 6
   // 108: aload 11
   // 10a: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.L$1 Ljava/lang/Object;
   // 10d: astore 8
   // 10f: aconst_null
   // 110: astore 4
   // 112: aload 11
   // 114: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$single$1.L$0 Ljava/lang/Object;
   // 117: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 11a: astore 2
   // 11b: nop
   // 11c: aload 10
   // 11e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 121: aload 10
   // 123: checkcast java/lang/Boolean
   // 126: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 129: ifeq 137
   // 12c: new java/lang/IllegalArgumentException
   // 12f: dup
   // 130: ldc_w "ReceiveChannel has more than one element."
   // 133: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
   // 136: athrow
   // 137: aload 8
   // 139: astore 9
   // 13b: aload 2
   // 13c: aload 4
   // 13e: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 141: aload 9
   // 143: areturn
   // 144: astore 5
   // 146: aload 5
   // 148: astore 4
   // 14a: aload 5
   // 14c: athrow
   // 14d: astore 5
   // 14f: aload 2
   // 150: aload 4
   // 152: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 155: aload 5
   // 157: athrow
   // 158: new java/lang/IllegalStateException
   // 15b: dup
   // 15c: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 15f: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 162: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.singleOrNull(): Any? {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 1
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1
   // 004: ifeq 027
   // 007: aload 1
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1
   // 00b: astore 13
   // 00d: aload 13
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 13
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1
   // 02a: dup
   // 02b: aload 1
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 13
   // 031: aload 13
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.result Ljava/lang/Object;
   // 036: astore 12
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 14
   // 03d: aload 13
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.label I
   // 042: tableswitch 280 0 2 26 92 194
   // 05c: aload 12
   // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 061: aload 0
   // 062: astore 2
   // 063: bipush 0
   // 064: istore 3
   // 065: nop
   // 066: aconst_null
   // 067: astore 4
   // 069: nop
   // 06a: aload 2
   // 06b: astore 5
   // 06d: bipush 0
   // 06e: istore 6
   // 070: aload 5
   // 072: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 077: astore 7
   // 079: aload 7
   // 07b: aload 13
   // 07d: aload 13
   // 07f: aload 2
   // 080: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.L$0 Ljava/lang/Object;
   // 083: aload 13
   // 085: aload 7
   // 087: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.L$1 Ljava/lang/Object;
   // 08a: aload 13
   // 08c: bipush 1
   // 08d: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.label I
   // 090: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 095: dup
   // 096: aload 14
   // 098: if_acmpne 0c1
   // 09b: aload 14
   // 09d: areturn
   // 09e: bipush 0
   // 09f: istore 3
   // 0a0: bipush 0
   // 0a1: istore 6
   // 0a3: aload 13
   // 0a5: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.L$1 Ljava/lang/Object;
   // 0a8: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ab: astore 7
   // 0ad: aconst_null
   // 0ae: astore 4
   // 0b0: aload 13
   // 0b2: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.L$0 Ljava/lang/Object;
   // 0b5: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0b8: astore 2
   // 0b9: nop
   // 0ba: aload 12
   // 0bc: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0bf: aload 12
   // 0c1: checkcast java/lang/Boolean
   // 0c4: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0c7: ifne 0d6
   // 0ca: aconst_null
   // 0cb: astore 11
   // 0cd: aload 2
   // 0ce: aload 4
   // 0d0: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 0d3: aload 11
   // 0d5: areturn
   // 0d6: aload 7
   // 0d8: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0dd: astore 8
   // 0df: aload 7
   // 0e1: aload 13
   // 0e3: aload 13
   // 0e5: aload 2
   // 0e6: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.L$0 Ljava/lang/Object;
   // 0e9: aload 13
   // 0eb: aload 8
   // 0ed: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.L$1 Ljava/lang/Object;
   // 0f0: aload 13
   // 0f2: bipush 2
   // 0f3: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.label I
   // 0f6: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0fb: dup
   // 0fc: aload 14
   // 0fe: if_acmpne 124
   // 101: aload 14
   // 103: areturn
   // 104: bipush 0
   // 105: istore 6
   // 107: bipush 0
   // 108: istore 3
   // 109: aload 13
   // 10b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.L$1 Ljava/lang/Object;
   // 10e: astore 8
   // 110: aconst_null
   // 111: astore 4
   // 113: aload 13
   // 115: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$singleOrNull$1.L$0 Ljava/lang/Object;
   // 118: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 11b: astore 2
   // 11c: nop
   // 11d: aload 12
   // 11f: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 122: aload 12
   // 124: checkcast java/lang/Boolean
   // 127: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 12a: ifeq 139
   // 12d: aconst_null
   // 12e: astore 10
   // 130: aload 2
   // 131: aload 4
   // 133: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 136: aload 10
   // 138: areturn
   // 139: aload 8
   // 13b: astore 9
   // 13d: aload 2
   // 13e: aload 4
   // 140: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 143: aload 9
   // 145: areturn
   // 146: astore 5
   // 148: aload 5
   // 14a: astore 4
   // 14c: aload 5
   // 14e: athrow
   // 14f: astore 5
   // 151: aload 2
   // 152: aload 4
   // 154: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 157: aload 5
   // 159: athrow
   // 15a: new java/lang/IllegalStateException
   // 15d: dup
   // 15e: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 161: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 164: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> ReceiveChannel<Any>.drop(n: Int, context: CoroutineContext = Unconfined as CoroutineContext): ReceiveChannel<Any> {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$drop`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> ReceiveChannel<Any>.dropWhile(context: CoroutineContext = Unconfined as CoroutineContext, predicate: (Any, Continuation<Boolean>) -> Any?): ReceiveChannel<
      Any
   > {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$dropWhile`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@PublishedApi
internal fun <E> ReceiveChannel<Any>.filter(context: CoroutineContext = Unconfined as CoroutineContext, predicate: (Any, Continuation<Boolean>) -> Any?): ReceiveChannel<
      Any
   > {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$filter`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> ReceiveChannel<Any>.filterIndexed(
   context: CoroutineContext = Unconfined as CoroutineContext,
   predicate: (Int, Any, Continuation<Boolean>) -> Any?
): ReceiveChannel<Any> {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$filterIndexed`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> ReceiveChannel<Any>.filterNot(context: CoroutineContext = Unconfined as CoroutineContext, predicate: (Any, Continuation<Boolean>) -> Any?): ReceiveChannel<
      Any
   > {
   return ChannelsKt.filter(`$this$filterNot`, context, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2)
}

@PublishedApi
internal fun <E : Any> ReceiveChannel<Any?>.filterNotNull(): ReceiveChannel<Any> {
   val var10000: ReceiveChannel = ChannelsKt.filter$default(`$this$filterNotNull`, null, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 1, null)
   return var10000
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E : Any, C : MutableCollection<in Any>> ReceiveChannel<Any?>.filterNotNullTo(destination: Any): Any {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1
   // 00b: astore 15
   // 00d: aload 15
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 15
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 15
   // 031: aload 15
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.result Ljava/lang/Object;
   // 036: astore 14
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 16
   // 03d: aload 15
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.label I
   // 042: tableswitch 235 0 1 22 103
   // 058: aload 14
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: aload 0
   // 05e: astore 3
   // 05f: bipush 0
   // 060: istore 4
   // 062: aload 3
   // 063: astore 5
   // 065: bipush 0
   // 066: istore 6
   // 068: nop
   // 069: aconst_null
   // 06a: astore 7
   // 06c: nop
   // 06d: aload 5
   // 06f: astore 8
   // 071: bipush 0
   // 072: istore 9
   // 074: aload 8
   // 076: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 07b: astore 10
   // 07d: aload 10
   // 07f: aload 15
   // 081: aload 15
   // 083: aload 1
   // 084: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.L$0 Ljava/lang/Object;
   // 087: aload 15
   // 089: aload 5
   // 08b: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.L$1 Ljava/lang/Object;
   // 08e: aload 15
   // 090: aload 10
   // 092: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.L$2 Ljava/lang/Object;
   // 095: aload 15
   // 097: bipush 1
   // 098: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.label I
   // 09b: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0a0: dup
   // 0a1: aload 16
   // 0a3: if_acmpne 0da
   // 0a6: aload 16
   // 0a8: areturn
   // 0a9: bipush 0
   // 0aa: istore 4
   // 0ac: bipush 0
   // 0ad: istore 6
   // 0af: bipush 0
   // 0b0: istore 9
   // 0b2: aload 15
   // 0b4: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.L$2 Ljava/lang/Object;
   // 0b7: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ba: astore 10
   // 0bc: aconst_null
   // 0bd: astore 7
   // 0bf: aload 15
   // 0c1: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.L$1 Ljava/lang/Object;
   // 0c4: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0c7: astore 5
   // 0c9: aload 15
   // 0cb: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$1.L$0 Ljava/lang/Object;
   // 0ce: checkcast java/util/Collection
   // 0d1: astore 1
   // 0d2: nop
   // 0d3: aload 14
   // 0d5: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0d8: aload 14
   // 0da: checkcast java/lang/Boolean
   // 0dd: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0e0: ifeq 105
   // 0e3: aload 10
   // 0e5: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0ea: astore 11
   // 0ec: aload 11
   // 0ee: astore 12
   // 0f0: bipush 0
   // 0f1: istore 13
   // 0f3: aload 12
   // 0f5: ifnull 101
   // 0f8: aload 1
   // 0f9: aload 12
   // 0fb: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
   // 100: pop
   // 101: nop
   // 102: goto 07d
   // 105: nop
   // 106: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 109: astore 8
   // 10b: aload 5
   // 10d: aload 7
   // 10f: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 112: goto 12a
   // 115: astore 8
   // 117: aload 8
   // 119: astore 7
   // 11b: aload 8
   // 11d: athrow
   // 11e: astore 8
   // 120: aload 5
   // 122: aload 7
   // 124: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 127: aload 8
   // 129: athrow
   // 12a: nop
   // 12b: aload 1
   // 12c: areturn
   // 12d: new java/lang/IllegalStateException
   // 130: dup
   // 131: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 134: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 137: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E : Any, C : SendChannel<Any>> ReceiveChannel<Any?>.filterNotNullTo(destination: Any): Any {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3
   // 00b: astore 15
   // 00d: aload 15
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 15
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 15
   // 031: aload 15
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.result Ljava/lang/Object;
   // 036: astore 14
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 16
   // 03d: aload 15
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.label I
   // 042: tableswitch 327 0 2 26 107 231
   // 05c: aload 14
   // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 061: aload 0
   // 062: astore 3
   // 063: bipush 0
   // 064: istore 4
   // 066: aload 3
   // 067: astore 5
   // 069: bipush 0
   // 06a: istore 6
   // 06c: nop
   // 06d: aconst_null
   // 06e: astore 7
   // 070: nop
   // 071: aload 5
   // 073: astore 8
   // 075: bipush 0
   // 076: istore 9
   // 078: aload 8
   // 07a: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 07f: astore 10
   // 081: aload 10
   // 083: aload 15
   // 085: aload 15
   // 087: aload 1
   // 088: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$0 Ljava/lang/Object;
   // 08b: aload 15
   // 08d: aload 5
   // 08f: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$1 Ljava/lang/Object;
   // 092: aload 15
   // 094: aload 10
   // 096: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$2 Ljava/lang/Object;
   // 099: aload 15
   // 09b: bipush 1
   // 09c: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.label I
   // 09f: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0a4: dup
   // 0a5: aload 16
   // 0a7: if_acmpne 0de
   // 0aa: aload 16
   // 0ac: areturn
   // 0ad: bipush 0
   // 0ae: istore 4
   // 0b0: bipush 0
   // 0b1: istore 6
   // 0b3: bipush 0
   // 0b4: istore 9
   // 0b6: aload 15
   // 0b8: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$2 Ljava/lang/Object;
   // 0bb: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0be: astore 10
   // 0c0: aconst_null
   // 0c1: astore 7
   // 0c3: aload 15
   // 0c5: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$1 Ljava/lang/Object;
   // 0c8: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0cb: astore 5
   // 0cd: aload 15
   // 0cf: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$0 Ljava/lang/Object;
   // 0d2: checkcast kotlinx/coroutines/channels/SendChannel
   // 0d5: astore 1
   // 0d6: nop
   // 0d7: aload 14
   // 0d9: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0dc: aload 14
   // 0de: checkcast java/lang/Boolean
   // 0e1: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0e4: ifeq 161
   // 0e7: aload 10
   // 0e9: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0ee: astore 11
   // 0f0: aload 11
   // 0f2: astore 12
   // 0f4: bipush 0
   // 0f5: istore 13
   // 0f7: aload 12
   // 0f9: ifnull 15e
   // 0fc: aload 1
   // 0fd: aload 12
   // 0ff: aload 15
   // 101: aload 15
   // 103: aload 1
   // 104: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$0 Ljava/lang/Object;
   // 107: aload 15
   // 109: aload 5
   // 10b: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$1 Ljava/lang/Object;
   // 10e: aload 15
   // 110: aload 10
   // 112: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$2 Ljava/lang/Object;
   // 115: aload 15
   // 117: bipush 2
   // 118: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.label I
   // 11b: invokeinterface kotlinx/coroutines/channels/SendChannel.send (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 120: dup
   // 121: aload 16
   // 123: if_acmpne 15d
   // 126: aload 16
   // 128: areturn
   // 129: bipush 0
   // 12a: istore 4
   // 12c: bipush 0
   // 12d: istore 6
   // 12f: bipush 0
   // 130: istore 9
   // 132: bipush 0
   // 133: istore 13
   // 135: aload 15
   // 137: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$2 Ljava/lang/Object;
   // 13a: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 13d: astore 10
   // 13f: aconst_null
   // 140: astore 7
   // 142: aload 15
   // 144: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$1 Ljava/lang/Object;
   // 147: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 14a: astore 5
   // 14c: aload 15
   // 14e: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$filterNotNullTo$3.L$0 Ljava/lang/Object;
   // 151: checkcast kotlinx/coroutines/channels/SendChannel
   // 154: astore 1
   // 155: nop
   // 156: aload 14
   // 158: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 15b: aload 14
   // 15d: pop
   // 15e: goto 081
   // 161: nop
   // 162: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 165: astore 8
   // 167: aload 5
   // 169: aload 7
   // 16b: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 16e: goto 186
   // 171: astore 8
   // 173: aload 8
   // 175: astore 7
   // 177: aload 8
   // 179: athrow
   // 17a: astore 8
   // 17c: aload 5
   // 17e: aload 7
   // 180: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 183: aload 8
   // 185: athrow
   // 186: nop
   // 187: aload 1
   // 188: areturn
   // 189: new java/lang/IllegalStateException
   // 18c: dup
   // 18d: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 190: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 193: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> ReceiveChannel<Any>.take(n: Int, context: CoroutineContext = Unconfined as CoroutineContext): ReceiveChannel<Any> {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$take`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> ReceiveChannel<Any>.takeWhile(context: CoroutineContext = Unconfined as CoroutineContext, predicate: (Any, Continuation<Boolean>) -> Any?): ReceiveChannel<
      Any
   > {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$takeWhile`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@PublishedApi
internal suspend fun <E, C : SendChannel<Any>> ReceiveChannel<Any>.toChannel(destination: Any): Any {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1
   // 00b: astore 15
   // 00d: aload 15
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 15
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 15
   // 031: aload 15
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.result Ljava/lang/Object;
   // 036: astore 14
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 16
   // 03d: aload 15
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.label I
   // 042: tableswitch 322 0 2 26 107 226
   // 05c: aload 14
   // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 061: aload 0
   // 062: astore 3
   // 063: bipush 0
   // 064: istore 4
   // 066: aload 3
   // 067: astore 5
   // 069: bipush 0
   // 06a: istore 6
   // 06c: nop
   // 06d: aconst_null
   // 06e: astore 7
   // 070: nop
   // 071: aload 5
   // 073: astore 8
   // 075: bipush 0
   // 076: istore 9
   // 078: aload 8
   // 07a: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 07f: astore 10
   // 081: aload 10
   // 083: aload 15
   // 085: aload 15
   // 087: aload 1
   // 088: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$0 Ljava/lang/Object;
   // 08b: aload 15
   // 08d: aload 5
   // 08f: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$1 Ljava/lang/Object;
   // 092: aload 15
   // 094: aload 10
   // 096: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$2 Ljava/lang/Object;
   // 099: aload 15
   // 09b: bipush 1
   // 09c: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.label I
   // 09f: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0a4: dup
   // 0a5: aload 16
   // 0a7: if_acmpne 0de
   // 0aa: aload 16
   // 0ac: areturn
   // 0ad: bipush 0
   // 0ae: istore 4
   // 0b0: bipush 0
   // 0b1: istore 6
   // 0b3: bipush 0
   // 0b4: istore 9
   // 0b6: aload 15
   // 0b8: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$2 Ljava/lang/Object;
   // 0bb: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0be: astore 10
   // 0c0: aconst_null
   // 0c1: astore 7
   // 0c3: aload 15
   // 0c5: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$1 Ljava/lang/Object;
   // 0c8: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0cb: astore 5
   // 0cd: aload 15
   // 0cf: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$0 Ljava/lang/Object;
   // 0d2: checkcast kotlinx/coroutines/channels/SendChannel
   // 0d5: astore 1
   // 0d6: nop
   // 0d7: aload 14
   // 0d9: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0dc: aload 14
   // 0de: checkcast java/lang/Boolean
   // 0e1: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0e4: ifeq 15c
   // 0e7: aload 10
   // 0e9: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0ee: astore 11
   // 0f0: aload 11
   // 0f2: astore 12
   // 0f4: bipush 0
   // 0f5: istore 13
   // 0f7: aload 1
   // 0f8: aload 12
   // 0fa: aload 15
   // 0fc: aload 15
   // 0fe: aload 1
   // 0ff: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$0 Ljava/lang/Object;
   // 102: aload 15
   // 104: aload 5
   // 106: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$1 Ljava/lang/Object;
   // 109: aload 15
   // 10b: aload 10
   // 10d: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$2 Ljava/lang/Object;
   // 110: aload 15
   // 112: bipush 2
   // 113: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.label I
   // 116: invokeinterface kotlinx/coroutines/channels/SendChannel.send (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
   // 11b: dup
   // 11c: aload 16
   // 11e: if_acmpne 158
   // 121: aload 16
   // 123: areturn
   // 124: bipush 0
   // 125: istore 4
   // 127: bipush 0
   // 128: istore 6
   // 12a: bipush 0
   // 12b: istore 9
   // 12d: bipush 0
   // 12e: istore 13
   // 130: aload 15
   // 132: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$2 Ljava/lang/Object;
   // 135: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 138: astore 10
   // 13a: aconst_null
   // 13b: astore 7
   // 13d: aload 15
   // 13f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$1 Ljava/lang/Object;
   // 142: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 145: astore 5
   // 147: aload 15
   // 149: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toChannel$1.L$0 Ljava/lang/Object;
   // 14c: checkcast kotlinx/coroutines/channels/SendChannel
   // 14f: astore 1
   // 150: nop
   // 151: aload 14
   // 153: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 156: aload 14
   // 158: pop
   // 159: goto 081
   // 15c: nop
   // 15d: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 160: astore 8
   // 162: aload 5
   // 164: aload 7
   // 166: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 169: goto 181
   // 16c: astore 8
   // 16e: aload 8
   // 170: astore 7
   // 172: aload 8
   // 174: athrow
   // 175: astore 8
   // 177: aload 5
   // 179: aload 7
   // 17b: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 17e: aload 8
   // 180: athrow
   // 181: nop
   // 182: aload 1
   // 183: areturn
   // 184: new java/lang/IllegalStateException
   // 187: dup
   // 188: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 18b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 18e: athrow
}

@PublishedApi
internal suspend fun <E, C : MutableCollection<in Any>> ReceiveChannel<Any>.toCollection(destination: Any): Any {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1
   // 00b: astore 15
   // 00d: aload 15
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 15
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 15
   // 031: aload 15
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.result Ljava/lang/Object;
   // 036: astore 14
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 16
   // 03d: aload 15
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.label I
   // 042: tableswitch 230 0 1 22 103
   // 058: aload 14
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: aload 0
   // 05e: astore 3
   // 05f: bipush 0
   // 060: istore 4
   // 062: aload 3
   // 063: astore 5
   // 065: bipush 0
   // 066: istore 6
   // 068: nop
   // 069: aconst_null
   // 06a: astore 7
   // 06c: nop
   // 06d: aload 5
   // 06f: astore 8
   // 071: bipush 0
   // 072: istore 9
   // 074: aload 8
   // 076: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 07b: astore 10
   // 07d: aload 10
   // 07f: aload 15
   // 081: aload 15
   // 083: aload 1
   // 084: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.L$0 Ljava/lang/Object;
   // 087: aload 15
   // 089: aload 5
   // 08b: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.L$1 Ljava/lang/Object;
   // 08e: aload 15
   // 090: aload 10
   // 092: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.L$2 Ljava/lang/Object;
   // 095: aload 15
   // 097: bipush 1
   // 098: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.label I
   // 09b: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0a0: dup
   // 0a1: aload 16
   // 0a3: if_acmpne 0da
   // 0a6: aload 16
   // 0a8: areturn
   // 0a9: bipush 0
   // 0aa: istore 4
   // 0ac: bipush 0
   // 0ad: istore 6
   // 0af: bipush 0
   // 0b0: istore 9
   // 0b2: aload 15
   // 0b4: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.L$2 Ljava/lang/Object;
   // 0b7: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ba: astore 10
   // 0bc: aconst_null
   // 0bd: astore 7
   // 0bf: aload 15
   // 0c1: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.L$1 Ljava/lang/Object;
   // 0c4: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0c7: astore 5
   // 0c9: aload 15
   // 0cb: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toCollection$1.L$0 Ljava/lang/Object;
   // 0ce: checkcast java/util/Collection
   // 0d1: astore 1
   // 0d2: nop
   // 0d3: aload 14
   // 0d5: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0d8: aload 14
   // 0da: checkcast java/lang/Boolean
   // 0dd: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0e0: ifeq 100
   // 0e3: aload 10
   // 0e5: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0ea: astore 11
   // 0ec: aload 11
   // 0ee: astore 12
   // 0f0: bipush 0
   // 0f1: istore 13
   // 0f3: aload 1
   // 0f4: aload 12
   // 0f6: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
   // 0fb: pop
   // 0fc: nop
   // 0fd: goto 07d
   // 100: nop
   // 101: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 104: astore 8
   // 106: aload 5
   // 108: aload 7
   // 10a: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 10d: goto 125
   // 110: astore 8
   // 112: aload 8
   // 114: astore 7
   // 116: aload 8
   // 118: athrow
   // 119: astore 8
   // 11b: aload 5
   // 11d: aload 7
   // 11f: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 122: aload 8
   // 124: athrow
   // 125: nop
   // 126: aload 1
   // 127: areturn
   // 128: new java/lang/IllegalStateException
   // 12b: dup
   // 12c: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 12f: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 132: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <K, V> ReceiveChannel<Pair<Any, Any>>.toMap(): Map<Any, Any> {
   return ChannelsKt.toMap(`$this$toMap`, LinkedHashMap(), `$completion`)
}

@PublishedApi
internal suspend fun <K, V, M : MutableMap<in Any, in Any>> ReceiveChannel<Pair<Any, Any>>.toMap(destination: Any): Any {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2
   // 00b: astore 15
   // 00d: aload 15
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 15
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 15
   // 031: aload 15
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.result Ljava/lang/Object;
   // 036: astore 14
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 16
   // 03d: aload 15
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.label I
   // 042: tableswitch 241 0 1 22 103
   // 058: aload 14
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: aload 0
   // 05e: astore 3
   // 05f: bipush 0
   // 060: istore 4
   // 062: aload 3
   // 063: astore 5
   // 065: bipush 0
   // 066: istore 6
   // 068: nop
   // 069: aconst_null
   // 06a: astore 7
   // 06c: nop
   // 06d: aload 5
   // 06f: astore 8
   // 071: bipush 0
   // 072: istore 9
   // 074: aload 8
   // 076: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 07b: astore 10
   // 07d: aload 10
   // 07f: aload 15
   // 081: aload 15
   // 083: aload 1
   // 084: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.L$0 Ljava/lang/Object;
   // 087: aload 15
   // 089: aload 5
   // 08b: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.L$1 Ljava/lang/Object;
   // 08e: aload 15
   // 090: aload 10
   // 092: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.L$2 Ljava/lang/Object;
   // 095: aload 15
   // 097: bipush 1
   // 098: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.label I
   // 09b: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0a0: dup
   // 0a1: aload 16
   // 0a3: if_acmpne 0da
   // 0a6: aload 16
   // 0a8: areturn
   // 0a9: bipush 0
   // 0aa: istore 4
   // 0ac: bipush 0
   // 0ad: istore 6
   // 0af: bipush 0
   // 0b0: istore 9
   // 0b2: aload 15
   // 0b4: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.L$2 Ljava/lang/Object;
   // 0b7: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0ba: astore 10
   // 0bc: aconst_null
   // 0bd: astore 7
   // 0bf: aload 15
   // 0c1: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.L$1 Ljava/lang/Object;
   // 0c4: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0c7: astore 5
   // 0c9: aload 15
   // 0cb: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$toMap$2.L$0 Ljava/lang/Object;
   // 0ce: checkcast java/util/Map
   // 0d1: astore 1
   // 0d2: nop
   // 0d3: aload 14
   // 0d5: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0d8: aload 14
   // 0da: checkcast java/lang/Boolean
   // 0dd: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0e0: ifeq 10b
   // 0e3: aload 10
   // 0e5: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0ea: astore 11
   // 0ec: aload 11
   // 0ee: checkcast kotlin/Pair
   // 0f1: astore 12
   // 0f3: bipush 0
   // 0f4: istore 13
   // 0f6: aload 1
   // 0f7: aload 12
   // 0f9: invokevirtual kotlin/Pair.getFirst ()Ljava/lang/Object;
   // 0fc: aload 12
   // 0fe: invokevirtual kotlin/Pair.getSecond ()Ljava/lang/Object;
   // 101: invokeinterface java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
   // 106: pop
   // 107: nop
   // 108: goto 07d
   // 10b: nop
   // 10c: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 10f: astore 8
   // 111: aload 5
   // 113: aload 7
   // 115: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 118: goto 130
   // 11b: astore 8
   // 11d: aload 8
   // 11f: astore 7
   // 121: aload 8
   // 123: athrow
   // 124: astore 8
   // 126: aload 5
   // 128: aload 7
   // 12a: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 12d: aload 8
   // 12f: athrow
   // 130: nop
   // 131: aload 1
   // 132: areturn
   // 133: new java/lang/IllegalStateException
   // 136: dup
   // 137: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 13a: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 13d: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.toMutableList(): MutableList<Any> {
   return ChannelsKt.toCollection(`$this$toMutableList`, ArrayList(), `$completion`)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.toSet(): Set<Any> {
   return ChannelsKt.toMutableSet(`$this$toSet`, `$completion`)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E, R> ReceiveChannel<Any>.flatMap(
   context: CoroutineContext = Unconfined as CoroutineContext,
   transform: (Any, Continuation<ReceiveChannel<Any>>) -> Any?
): ReceiveChannel<Any> {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$flatMap`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@PublishedApi
internal fun <E, R> ReceiveChannel<Any>.map(context: CoroutineContext = Unconfined as CoroutineContext, transform: (Any, Continuation<Any>) -> Any?): ReceiveChannel<
      Any
   > {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$map`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@PublishedApi
internal fun <E, R> ReceiveChannel<Any>.mapIndexed(context: CoroutineContext = Unconfined as CoroutineContext, transform: (Int, Any, Continuation<Any>) -> Any?): ReceiveChannel<
      Any
   > {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$mapIndexed`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E, R : Any> ReceiveChannel<Any>.mapIndexedNotNull(
   context: CoroutineContext = Unconfined as CoroutineContext,
   transform: (Int, Any, Continuation<Any?>) -> Any?
): ReceiveChannel<Any> {
   return ChannelsKt.filterNotNull(ChannelsKt.mapIndexed(`$this$mapIndexedNotNull`, context, transform))
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E, R : Any> ReceiveChannel<Any>.mapNotNull(context: CoroutineContext = Unconfined as CoroutineContext, transform: (Any, Continuation<Any?>) -> Any?): ReceiveChannel<
      Any
   > {
   return ChannelsKt.filterNotNull(ChannelsKt.map(`$this$mapNotNull`, context, transform))
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> ReceiveChannel<Any>.withIndex(context: CoroutineContext = Unconfined as CoroutineContext): ReceiveChannel<IndexedValue<Any>> {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$withIndex`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> ReceiveChannel<Any>.distinct(): ReceiveChannel<Any> {
   return ChannelsKt.distinctBy$default(`$this$distinct`, null, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 1, null)
}

@PublishedApi
internal fun <E, K> ReceiveChannel<Any>.distinctBy(context: CoroutineContext = Unconfined as CoroutineContext, selector: (Any, Continuation<Any>) -> Any?): ReceiveChannel<
      Any
   > {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(`$this$distinctBy`), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@PublishedApi
internal suspend fun <E> ReceiveChannel<Any>.toMutableSet(): MutableSet<Any> {
   return ChannelsKt.toCollection(`$this$toMutableSet`, LinkedHashSet(), `$completion`)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.any(): Boolean {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 00: aload 1
   // 01: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1
   // 0b: astore 9
   // 0d: aload 9
   // 0f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 9
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 9
   // 31: aload 9
   // 33: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1.result Ljava/lang/Object;
   // 36: astore 8
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 10
   // 3d: aload 9
   // 3f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1.label I
   // 42: tableswitch 133 0 1 22 77
   // 58: aload 8
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: aload 0
   // 5e: astore 2
   // 5f: bipush 0
   // 60: istore 3
   // 61: nop
   // 62: aconst_null
   // 63: astore 4
   // 65: nop
   // 66: aload 2
   // 67: astore 5
   // 69: bipush 0
   // 6a: istore 6
   // 6c: aload 5
   // 6e: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 73: aload 9
   // 75: aload 9
   // 77: aload 2
   // 78: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1.L$0 Ljava/lang/Object;
   // 7b: aload 9
   // 7d: bipush 1
   // 7e: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1.label I
   // 81: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 86: dup
   // 87: aload 10
   // 89: if_acmpne a8
   // 8c: aload 10
   // 8e: areturn
   // 8f: bipush 0
   // 90: istore 3
   // 91: bipush 0
   // 92: istore 6
   // 94: aconst_null
   // 95: astore 4
   // 97: aload 9
   // 99: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$any$1.L$0 Ljava/lang/Object;
   // 9c: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 9f: astore 2
   // a0: nop
   // a1: aload 8
   // a3: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // a6: aload 8
   // a8: astore 7
   // aa: aload 2
   // ab: aload 4
   // ad: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // b0: aload 7
   // b2: areturn
   // b3: astore 5
   // b5: aload 5
   // b7: astore 4
   // b9: aload 5
   // bb: athrow
   // bc: astore 5
   // be: aload 2
   // bf: aload 4
   // c1: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // c4: aload 5
   // c6: athrow
   // c7: new java/lang/IllegalStateException
   // ca: dup
   // cb: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // ce: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // d1: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.count(): Int {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 1
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1
   // 004: ifeq 027
   // 007: aload 1
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1
   // 00b: astore 15
   // 00d: aload 15
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 15
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1
   // 02a: dup
   // 02b: aload 1
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 15
   // 031: aload 15
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.result Ljava/lang/Object;
   // 036: astore 14
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 16
   // 03d: aload 15
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.label I
   // 042: tableswitch 244 0 1 22 111
   // 058: aload 14
   // 05a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 05d: new kotlin/jvm/internal/Ref$IntRef
   // 060: dup
   // 061: invokespecial kotlin/jvm/internal/Ref$IntRef.<init> ()V
   // 064: astore 2
   // 065: aload 0
   // 066: astore 3
   // 067: bipush 0
   // 068: istore 4
   // 06a: aload 3
   // 06b: astore 5
   // 06d: bipush 0
   // 06e: istore 6
   // 070: nop
   // 071: aconst_null
   // 072: astore 7
   // 074: nop
   // 075: aload 5
   // 077: astore 8
   // 079: bipush 0
   // 07a: istore 9
   // 07c: aload 8
   // 07e: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 083: astore 10
   // 085: aload 10
   // 087: aload 15
   // 089: aload 15
   // 08b: aload 2
   // 08c: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.L$0 Ljava/lang/Object;
   // 08f: aload 15
   // 091: aload 5
   // 093: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.L$1 Ljava/lang/Object;
   // 096: aload 15
   // 098: aload 10
   // 09a: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.L$2 Ljava/lang/Object;
   // 09d: aload 15
   // 09f: bipush 1
   // 0a0: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.label I
   // 0a3: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 0a8: dup
   // 0a9: aload 16
   // 0ab: if_acmpne 0e2
   // 0ae: aload 16
   // 0b0: areturn
   // 0b1: bipush 0
   // 0b2: istore 4
   // 0b4: bipush 0
   // 0b5: istore 6
   // 0b7: bipush 0
   // 0b8: istore 9
   // 0ba: aload 15
   // 0bc: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.L$2 Ljava/lang/Object;
   // 0bf: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0c2: astore 10
   // 0c4: aconst_null
   // 0c5: astore 7
   // 0c7: aload 15
   // 0c9: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.L$1 Ljava/lang/Object;
   // 0cc: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0cf: astore 5
   // 0d1: aload 15
   // 0d3: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$count$1.L$0 Ljava/lang/Object;
   // 0d6: checkcast kotlin/jvm/internal/Ref$IntRef
   // 0d9: astore 2
   // 0da: nop
   // 0db: aload 14
   // 0dd: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0e0: aload 14
   // 0e2: checkcast java/lang/Boolean
   // 0e5: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0e8: ifeq 108
   // 0eb: aload 10
   // 0ed: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0f2: astore 11
   // 0f4: bipush 0
   // 0f5: istore 12
   // 0f7: aload 2
   // 0f8: getfield kotlin/jvm/internal/Ref$IntRef.element I
   // 0fb: istore 13
   // 0fd: aload 2
   // 0fe: iload 13
   // 100: bipush 1
   // 101: iadd
   // 102: putfield kotlin/jvm/internal/Ref$IntRef.element I
   // 105: goto 085
   // 108: nop
   // 109: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 10c: astore 8
   // 10e: aload 5
   // 110: aload 7
   // 112: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 115: goto 12d
   // 118: astore 8
   // 11a: aload 8
   // 11c: astore 7
   // 11e: aload 8
   // 120: athrow
   // 121: astore 8
   // 123: aload 5
   // 125: aload 7
   // 127: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 12a: aload 8
   // 12c: athrow
   // 12d: nop
   // 12e: aload 2
   // 12f: getfield kotlin/jvm/internal/Ref$IntRef.element I
   // 132: invokestatic kotlin/coroutines/jvm/internal/Boxing.boxInt (I)Ljava/lang/Integer;
   // 135: areturn
   // 136: new java/lang/IllegalStateException
   // 139: dup
   // 13a: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 13d: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 140: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.maxWith(comparator: Comparator<in Any>): Any? {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1
   // 00b: astore 14
   // 00d: aload 14
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 14
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 14
   // 031: aload 14
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.result Ljava/lang/Object;
   // 036: astore 13
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 15
   // 03d: aload 14
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.label I
   // 042: tableswitch 347 0 2 26 99 224
   // 05c: aload 13
   // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 061: aload 0
   // 062: astore 3
   // 063: bipush 0
   // 064: istore 4
   // 066: nop
   // 067: aconst_null
   // 068: astore 5
   // 06a: nop
   // 06b: aload 3
   // 06c: astore 6
   // 06e: bipush 0
   // 06f: istore 7
   // 071: aload 6
   // 073: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 078: astore 8
   // 07a: aload 8
   // 07c: aload 14
   // 07e: aload 14
   // 080: aload 1
   // 081: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$0 Ljava/lang/Object;
   // 084: aload 14
   // 086: aload 3
   // 087: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$1 Ljava/lang/Object;
   // 08a: aload 14
   // 08c: aload 8
   // 08e: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$2 Ljava/lang/Object;
   // 091: aload 14
   // 093: bipush 1
   // 094: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.label I
   // 097: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 09c: dup
   // 09d: aload 15
   // 09f: if_acmpne 0d2
   // 0a2: aload 15
   // 0a4: areturn
   // 0a5: bipush 0
   // 0a6: istore 4
   // 0a8: bipush 0
   // 0a9: istore 7
   // 0ab: aload 14
   // 0ad: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$2 Ljava/lang/Object;
   // 0b0: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0b3: astore 8
   // 0b5: aconst_null
   // 0b6: astore 5
   // 0b8: aload 14
   // 0ba: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$1 Ljava/lang/Object;
   // 0bd: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0c0: astore 3
   // 0c1: aload 14
   // 0c3: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$0 Ljava/lang/Object;
   // 0c6: checkcast java/util/Comparator
   // 0c9: astore 1
   // 0ca: nop
   // 0cb: aload 13
   // 0cd: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0d0: aload 13
   // 0d2: checkcast java/lang/Boolean
   // 0d5: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0d8: ifne 0e7
   // 0db: aconst_null
   // 0dc: astore 12
   // 0de: aload 3
   // 0df: aload 5
   // 0e1: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 0e4: aload 12
   // 0e6: areturn
   // 0e7: aload 8
   // 0e9: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0ee: astore 9
   // 0f0: aload 8
   // 0f2: aload 14
   // 0f4: aload 14
   // 0f6: aload 1
   // 0f7: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$0 Ljava/lang/Object;
   // 0fa: aload 14
   // 0fc: aload 3
   // 0fd: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$1 Ljava/lang/Object;
   // 100: aload 14
   // 102: aload 8
   // 104: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$2 Ljava/lang/Object;
   // 107: aload 14
   // 109: aload 9
   // 10b: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$3 Ljava/lang/Object;
   // 10e: aload 14
   // 110: bipush 2
   // 111: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.label I
   // 114: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 119: dup
   // 11a: aload 15
   // 11c: if_acmpne 156
   // 11f: aload 15
   // 121: areturn
   // 122: bipush 0
   // 123: istore 7
   // 125: bipush 0
   // 126: istore 4
   // 128: aload 14
   // 12a: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$3 Ljava/lang/Object;
   // 12d: astore 9
   // 12f: aload 14
   // 131: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$2 Ljava/lang/Object;
   // 134: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 137: astore 8
   // 139: aconst_null
   // 13a: astore 5
   // 13c: aload 14
   // 13e: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$1 Ljava/lang/Object;
   // 141: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 144: astore 3
   // 145: aload 14
   // 147: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$maxWith$1.L$0 Ljava/lang/Object;
   // 14a: checkcast java/util/Comparator
   // 14d: astore 1
   // 14e: nop
   // 14f: aload 13
   // 151: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 154: aload 13
   // 156: checkcast java/lang/Boolean
   // 159: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 15c: ifeq 17c
   // 15f: aload 8
   // 161: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 166: astore 10
   // 168: aload 1
   // 169: aload 9
   // 16b: aload 10
   // 16d: invokeinterface java/util/Comparator.compare (Ljava/lang/Object;Ljava/lang/Object;)I 3
   // 172: ifge 0f0
   // 175: aload 10
   // 177: astore 9
   // 179: goto 0f0
   // 17c: aload 9
   // 17e: astore 11
   // 180: aload 3
   // 181: aload 5
   // 183: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 186: aload 11
   // 188: areturn
   // 189: astore 6
   // 18b: aload 6
   // 18d: astore 5
   // 18f: aload 6
   // 191: athrow
   // 192: astore 6
   // 194: aload 3
   // 195: aload 5
   // 197: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 19a: aload 6
   // 19c: athrow
   // 19d: new java/lang/IllegalStateException
   // 1a0: dup
   // 1a1: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 1a4: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 1a7: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.minWith(comparator: Comparator<in Any>): Any? {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1
   // 00b: astore 14
   // 00d: aload 14
   // 00f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 14
   // 01a: dup
   // 01b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.label I
   // 024: goto 031
   // 027: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 14
   // 031: aload 14
   // 033: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.result Ljava/lang/Object;
   // 036: astore 13
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 15
   // 03d: aload 14
   // 03f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.label I
   // 042: tableswitch 347 0 2 26 99 224
   // 05c: aload 13
   // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 061: aload 0
   // 062: astore 3
   // 063: bipush 0
   // 064: istore 4
   // 066: nop
   // 067: aconst_null
   // 068: astore 5
   // 06a: nop
   // 06b: aload 3
   // 06c: astore 6
   // 06e: bipush 0
   // 06f: istore 7
   // 071: aload 6
   // 073: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 078: astore 8
   // 07a: aload 8
   // 07c: aload 14
   // 07e: aload 14
   // 080: aload 1
   // 081: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$0 Ljava/lang/Object;
   // 084: aload 14
   // 086: aload 3
   // 087: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$1 Ljava/lang/Object;
   // 08a: aload 14
   // 08c: aload 8
   // 08e: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$2 Ljava/lang/Object;
   // 091: aload 14
   // 093: bipush 1
   // 094: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.label I
   // 097: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 09c: dup
   // 09d: aload 15
   // 09f: if_acmpne 0d2
   // 0a2: aload 15
   // 0a4: areturn
   // 0a5: bipush 0
   // 0a6: istore 4
   // 0a8: bipush 0
   // 0a9: istore 7
   // 0ab: aload 14
   // 0ad: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$2 Ljava/lang/Object;
   // 0b0: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 0b3: astore 8
   // 0b5: aconst_null
   // 0b6: astore 5
   // 0b8: aload 14
   // 0ba: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$1 Ljava/lang/Object;
   // 0bd: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 0c0: astore 3
   // 0c1: aload 14
   // 0c3: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$0 Ljava/lang/Object;
   // 0c6: checkcast java/util/Comparator
   // 0c9: astore 1
   // 0ca: nop
   // 0cb: aload 13
   // 0cd: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0d0: aload 13
   // 0d2: checkcast java/lang/Boolean
   // 0d5: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 0d8: ifne 0e7
   // 0db: aconst_null
   // 0dc: astore 12
   // 0de: aload 3
   // 0df: aload 5
   // 0e1: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 0e4: aload 12
   // 0e6: areturn
   // 0e7: aload 8
   // 0e9: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 0ee: astore 9
   // 0f0: aload 8
   // 0f2: aload 14
   // 0f4: aload 14
   // 0f6: aload 1
   // 0f7: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$0 Ljava/lang/Object;
   // 0fa: aload 14
   // 0fc: aload 3
   // 0fd: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$1 Ljava/lang/Object;
   // 100: aload 14
   // 102: aload 8
   // 104: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$2 Ljava/lang/Object;
   // 107: aload 14
   // 109: aload 9
   // 10b: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$3 Ljava/lang/Object;
   // 10e: aload 14
   // 110: bipush 2
   // 111: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.label I
   // 114: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 119: dup
   // 11a: aload 15
   // 11c: if_acmpne 156
   // 11f: aload 15
   // 121: areturn
   // 122: bipush 0
   // 123: istore 7
   // 125: bipush 0
   // 126: istore 4
   // 128: aload 14
   // 12a: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$3 Ljava/lang/Object;
   // 12d: astore 9
   // 12f: aload 14
   // 131: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$2 Ljava/lang/Object;
   // 134: checkcast kotlinx/coroutines/channels/ChannelIterator
   // 137: astore 8
   // 139: aconst_null
   // 13a: astore 5
   // 13c: aload 14
   // 13e: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$1 Ljava/lang/Object;
   // 141: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 144: astore 3
   // 145: aload 14
   // 147: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$minWith$1.L$0 Ljava/lang/Object;
   // 14a: checkcast java/util/Comparator
   // 14d: astore 1
   // 14e: nop
   // 14f: aload 13
   // 151: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 154: aload 13
   // 156: checkcast java/lang/Boolean
   // 159: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 15c: ifeq 17c
   // 15f: aload 8
   // 161: invokeinterface kotlinx/coroutines/channels/ChannelIterator.next ()Ljava/lang/Object; 1
   // 166: astore 10
   // 168: aload 1
   // 169: aload 9
   // 16b: aload 10
   // 16d: invokeinterface java/util/Comparator.compare (Ljava/lang/Object;Ljava/lang/Object;)I 3
   // 172: ifle 0f0
   // 175: aload 10
   // 177: astore 9
   // 179: goto 0f0
   // 17c: aload 9
   // 17e: astore 11
   // 180: aload 3
   // 181: aload 5
   // 183: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 186: aload 11
   // 188: areturn
   // 189: astore 6
   // 18b: aload 6
   // 18d: astore 5
   // 18f: aload 6
   // 191: athrow
   // 192: astore 6
   // 194: aload 3
   // 195: aload 5
   // 197: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // 19a: aload 6
   // 19c: athrow
   // 19d: new java/lang/IllegalStateException
   // 1a0: dup
   // 1a1: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 1a4: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 1a7: athrow
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend fun <E> ReceiveChannel<Any>.none(): Boolean {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.RuntimeException: parsing failure!
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
   //
   // Bytecode:
   // 00: aload 1
   // 01: instanceof kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1
   // 04: ifeq 27
   // 07: aload 1
   // 08: checkcast kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1
   // 0b: astore 9
   // 0d: aload 9
   // 0f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1.label I
   // 12: ldc -2147483648
   // 14: iand
   // 15: ifeq 27
   // 18: aload 9
   // 1a: dup
   // 1b: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1.label I
   // 1e: ldc -2147483648
   // 20: isub
   // 21: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1.label I
   // 24: goto 31
   // 27: new kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1
   // 2a: dup
   // 2b: aload 1
   // 2c: invokespecial kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 2f: astore 9
   // 31: aload 9
   // 33: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1.result Ljava/lang/Object;
   // 36: astore 8
   // 38: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 3b: astore 10
   // 3d: aload 9
   // 3f: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1.label I
   // 42: tableswitch 150 0 1 22 77
   // 58: aload 8
   // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 5d: aload 0
   // 5e: astore 2
   // 5f: bipush 0
   // 60: istore 3
   // 61: nop
   // 62: aconst_null
   // 63: astore 4
   // 65: nop
   // 66: aload 2
   // 67: astore 5
   // 69: bipush 0
   // 6a: istore 6
   // 6c: aload 5
   // 6e: invokeinterface kotlinx/coroutines/channels/ReceiveChannel.iterator ()Lkotlinx/coroutines/channels/ChannelIterator; 1
   // 73: aload 9
   // 75: aload 9
   // 77: aload 2
   // 78: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1.L$0 Ljava/lang/Object;
   // 7b: aload 9
   // 7d: bipush 1
   // 7e: putfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1.label I
   // 81: invokeinterface kotlinx/coroutines/channels/ChannelIterator.hasNext (Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 2
   // 86: dup
   // 87: aload 10
   // 89: if_acmpne a8
   // 8c: aload 10
   // 8e: areturn
   // 8f: bipush 0
   // 90: istore 3
   // 91: bipush 0
   // 92: istore 6
   // 94: aconst_null
   // 95: astore 4
   // 97: aload 9
   // 99: getfield kotlinx/coroutines/channels/ChannelsKt__DeprecatedKt$none$1.L$0 Ljava/lang/Object;
   // 9c: checkcast kotlinx/coroutines/channels/ReceiveChannel
   // 9f: astore 2
   // a0: nop
   // a1: aload 8
   // a3: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // a6: aload 8
   // a8: checkcast java/lang/Boolean
   // ab: invokevirtual java/lang/Boolean.booleanValue ()Z
   // ae: ifne b5
   // b1: bipush 1
   // b2: goto b6
   // b5: bipush 0
   // b6: invokestatic kotlin/coroutines/jvm/internal/Boxing.boxBoolean (Z)Ljava/lang/Boolean;
   // b9: astore 7
   // bb: aload 2
   // bc: aload 4
   // be: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // c1: aload 7
   // c3: areturn
   // c4: astore 5
   // c6: aload 5
   // c8: astore 4
   // ca: aload 5
   // cc: athrow
   // cd: astore 5
   // cf: aload 2
   // d0: aload 4
   // d2: invokestatic kotlinx/coroutines/channels/ChannelsKt.cancelConsumed (Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Throwable;)V
   // d5: aload 5
   // d7: athrow
   // d8: new java/lang/IllegalStateException
   // db: dup
   // dc: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // df: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // e2: athrow
}

@Deprecated(message = "Left for binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E : Any> ReceiveChannel<Any?>.requireNoNulls(): ReceiveChannel<Any> {
   return ChannelsKt.map$default(`$this$requireNoNulls`, null, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 1, null)
}

@Deprecated(message = "Binary compatibility", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public infix fun <E, R> ReceiveChannel<Any>.zip(other: ReceiveChannel<Any>): ReceiveChannel<Pair<Any, Any>> {
   return ChannelsKt.zip$default(`$this$zip`, other, null, { t1, t2 ->
      TuplesKt.to(t1, t2)
   }, 2, null)
}

@PublishedApi
internal fun <E, R, V> ReceiveChannel<Any>.zip(
   other: ReceiveChannel<Any>,
   context: CoroutineContext = Unconfined as CoroutineContext,
   transform: (Any, Any) -> Any
): ReceiveChannel<Any> {
   return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumesAll(`$this$zip`, other), {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 6, null)
}

@PublishedApi
internal fun ReceiveChannel<*>.consumes(): (Throwable?) -> Unit {
   return { cause: Throwable? ->
      ChannelsKt.cancelConsumed($this$consumes, cause)
   } as (java.lang.Throwable?) -> Unit
}
