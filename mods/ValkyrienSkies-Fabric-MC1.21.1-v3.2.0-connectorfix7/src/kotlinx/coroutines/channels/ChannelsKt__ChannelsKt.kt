@file:JvmMultifileClass
@file:JvmName("ChannelsKt")
@file:SourceDebugExtension(["SMAP\nChannels.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Channels.kt\nkotlinx/coroutines/channels/ChannelsKt__ChannelsKt\n+ 2 Channel.kt\nkotlinx/coroutines/channels/ChannelKt\n*L\n1#1,61:1\n514#2,6:62\n*S KotlinDebug\n*F\n+ 1 Channels.kt\nkotlinx/coroutines/channels/ChannelsKt__ChannelsKt\n*L\n37#1:62,6\n*E\n"])

package kotlinx.coroutines.channels

import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt

public fun <E> SendChannel<Any>.trySendBlocking(element: Any): ChannelResult<Unit> {
   val `$this$onSuccess_u2dWpGqRn0$iv`: Any = `$this$trySendBlocking`.trySend_JP2dKIU/* $VF was: trySend-JP2dKIU */(element)
   if (`$this$onSuccess_u2dWpGqRn0$iv` !is ChannelResult.Failed) {
      val it: Unit = `$this$onSuccess_u2dWpGqRn0$iv` as Unit
      return ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(Unit.INSTANCE)
   } else {
      return (BuildersKt.runBlocking$default(null, {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function2, 1, null) as ChannelResult).unbox_impl/* $VF was: unbox-impl */()
   }
}

@Deprecated(message = "Deprecated in the favour of 'trySendBlocking'. Consider handling the result of 'trySendBlocking' explicitly and rethrow exception if necessary", replaceWith = @ReplaceWith(expression = "trySendBlocking(element)", imports = []), level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <E> SendChannel<Any>.sendBlocking(element: Any) {
   if (!isSuccess) {
      BuildersKt.runBlocking$default(null, {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function2, 1, null)
   }
}
