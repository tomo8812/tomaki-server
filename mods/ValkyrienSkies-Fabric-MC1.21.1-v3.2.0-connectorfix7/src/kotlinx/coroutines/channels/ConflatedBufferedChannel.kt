package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.Waiter
import kotlinx.coroutines.internal.OnUndeliveredElementKt
import kotlinx.coroutines.internal.Symbol
import kotlinx.coroutines.internal.UndeliveredElementException
import kotlinx.coroutines.selects.SelectInstance

@SourceDebugExtension(["SMAP\nConflatedBufferedChannel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConflatedBufferedChannel.kt\nkotlinx/coroutines/channels/ConflatedBufferedChannel\n+ 2 Channel.kt\nkotlinx/coroutines/channels/ChannelKt\n+ 3 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel\n+ 4 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannelKt\n+ 5 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel$sendImpl$1\n*L\n1#1,119:1\n548#2,5:120\n514#2,6:125\n514#2,6:212\n548#2,5:218\n244#3:131\n269#3,10:132\n280#3,68:143\n3038#4:142\n269#5:211\n*S KotlinDebug\n*F\n+ 1 ConflatedBufferedChannel.kt\nkotlinx/coroutines/channels/ConflatedBufferedChannel\n*L\n41#1:120,5\n53#1:125,6\n106#1:212,6\n109#1:218,5\n80#1:131\n80#1:132,10\n80#1:143,68\n80#1:142\n80#1:211\n*E\n"])
internal open class ConflatedBufferedChannel<E>(capacity: Int, onBufferOverflow: BufferOverflow, onUndeliveredElement: ((Any) -> Unit)? = null) : BufferedChannel(
      capacity, onUndeliveredElement
   ) {
   private final val capacity: Int
   private final val onBufferOverflow: BufferOverflow

   init {
      this.capacity = capacity
      this.onBufferOverflow = onBufferOverflow
      if (this.onBufferOverflow === BufferOverflow.SUSPEND) {
         throw IllegalArgumentException(
            ("This implementation does not support suspension for senders, use ${(BufferedChannel::class).getSimpleName()} instead").toString()
         )
      } else if (this.capacity < 1) {
         throw IllegalArgumentException(("Buffered channel capacity must be at least 1, but ${this.capacity} was specified").toString())
      }
   }

   protected open val isConflatedDropOldest: Boolean
      protected open get() {
         return this.onBufferOverflow === BufferOverflow.DROP_OLDEST
      }


   public override suspend fun send(element: Any) {
      return send$suspendImpl(this, (E)element, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <E> `send$suspendImpl`(`$this`: ConflatedBufferedChannel<E>, element: E, `$completion`: Continuation<in Unit>) {
      val `$this$onClosed_u2dWpGqRn0$iv`: Any = `$this`.trySendImpl_Mj0NB7M/* $VF was: trySendImpl-Mj0NB7M */(element, true)
      if (`$this$onClosed_u2dWpGqRn0$iv` is ChannelResult.Closed) {
         val it: java.lang.Throwable = ChannelResult.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$onClosed_u2dWpGqRn0$iv`)
         if (`$this`.onUndeliveredElement != null) {
            val itx: UndeliveredElementException = OnUndeliveredElementKt.callUndeliveredElementCatchingException$default(
               `$this`.onUndeliveredElement, element, null, 2, null
            )
            if (itx != null) {
               ExceptionsKt.addSuppressed(itx, `$this`.getSendException())
               throw itx
            }
         }

         throw `$this`.getSendException()
      } else {
         Unit.INSTANCE
      }
   }

   internal override suspend fun sendBroadcast(element: Any): Boolean {
      return sendBroadcast$suspendImpl(this, (E)element, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <E> `sendBroadcast$suspendImpl`(`$this`: ConflatedBufferedChannel<E>, element: E, `$completion`: Continuation<? super java.lang.Boolean>) {
      val `$this$onSuccess_u2dWpGqRn0$iv`: Any = `$this`.trySendImpl_Mj0NB7M/* $VF was: trySendImpl-Mj0NB7M */(element, true)
      if (`$this$onSuccess_u2dWpGqRn0$iv` !is ChannelResult.Failed) {
         val it: Unit = `$this$onSuccess_u2dWpGqRn0$iv` as Unit
         Boxing.boxBoolean(true)
      } else {
         Boxing.boxBoolean(false)
      }
   }

   public override fun trySend(element: Any): ChannelResult<Unit> {
      return this.trySendImpl_Mj0NB7M/* $VF was: trySendImpl-Mj0NB7M */((E)element, false)
   }

   private fun trySendImpl(element: Any, isSendOp: Boolean): ChannelResult<Unit> {
      return if (this.onBufferOverflow === BufferOverflow.DROP_LATEST)
         this.trySendDropLatest_Mj0NB7M/* $VF was: trySendDropLatest-Mj0NB7M */((E)element, isSendOp)
         else
         this.trySendDropOldest_JP2dKIU/* $VF was: trySendDropOldest-JP2dKIU */((E)element)
      }

   private fun trySendDropLatest(element: Any, isSendOp: Boolean): ChannelResult<Unit> {
      val result: Any = super.trySend_JP2dKIU/* $VF was: trySend-JP2dKIU */((E)element)
      if (!isSuccess && !isClosed) {
         if (isSendOp && this.onUndeliveredElement != null) {
            val var5: UndeliveredElementException = OnUndeliveredElementKt.callUndeliveredElementCatchingException$default(
               this.onUndeliveredElement, element, null, 2, null
            )
            if (var5 != null) {
               throw var5
            }
         }

         return ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(Unit.INSTANCE)
      } else {
         return result
      }
   }

   private fun trySendDropOldest(element: Any): ChannelResult<Unit> {
      val `waiter$iv`: Symbol = BufferedChannelKt.BUFFERED
      var `segment$iv`: ChannelSegment = BufferedChannel.access$getSendSegment$FU$p().get(this) as ChannelSegment

      while (true) {
         val `sendersAndCloseStatusCur$iv`: Long = BufferedChannel.access$getSendersAndCloseStatus$FU$p().getAndIncrement(this)
         val `s$iv`: Long = `sendersAndCloseStatusCur$iv` and 1152921504606846975L
         val `closed$iv`: Boolean = BufferedChannel.access$isClosedForSend0(this, `sendersAndCloseStatusCur$iv`)
         val `id$iv`: Long = `s$iv` / BufferedChannelKt.SEGMENT_SIZE
         val `i$iv`: Int = (int)(`s$iv` % BufferedChannelKt.SEGMENT_SIZE)
         if (`segment$iv`.id != `id$iv`) {
            val var10000: ChannelSegment = BufferedChannel.access$findSegmentSend(this, `id$iv`, `segment$iv`)
            if (var10000 == null) {
               if (`closed$iv`) {
                  return ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.getSendException())
               }
               continue
            }

            `segment$iv` = var10000
         }

         when (BufferedChannel.access$updateCellSend(this, `segment$iv`, `i$iv`, element, `s$iv`, `waiter$iv`, `closed$iv`)) {
            0 -> {
               `segment$iv`.cleanPrev()
               return ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(Unit.INSTANCE)
            }
            1 -> return ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(Unit.INSTANCE)
            2 -> {
               if (`closed$iv`) {
                  `segment$iv`.onSlotCleaned()
                  return ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.getSendException())
               }

               val var25: Waiter = `waiter$iv` as? Waiter
               if ((`waiter$iv` as? Waiter) != null) {
                  BufferedChannel.access$prepareSenderForSuspension(this, var25, `segment$iv`, `i$iv`)
               }

               this.dropFirstElementUntilTheSpecifiedCellIsInTheBuffer(`segment$iv`.id * (long)BufferedChannelKt.SEGMENT_SIZE + (long)`i$iv`)
               return ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(Unit.INSTANCE)
            }
            3 -> throw IllegalStateException("unexpected".toString())
            4 -> {
               if (`s$iv` < (this as BufferedChannel).receiversCounter) {
                  `segment$iv`.cleanPrev()
               }

               return ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.getSendException())
            }
            5 -> `segment$iv`.cleanPrev()
            else -> {}
         }
      }
   }

   protected override fun registerSelectForSend(select: SelectInstance<*>, element: Any?) {
      val `$this$onClosed_u2dWpGqRn0$iv`: Any = this.trySend_JP2dKIU/* $VF was: trySend-JP2dKIU */((E)element)
      if (`$this$onClosed_u2dWpGqRn0$iv` !is ChannelResult.Failed) {
         val var11: Unit = `$this$onClosed_u2dWpGqRn0$iv` as Unit
         select.selectInRegistrationPhase(Unit.INSTANCE)
      } else if (`$this$onClosed_u2dWpGqRn0$iv` is ChannelResult.Closed) {
         val it: java.lang.Throwable = ChannelResult.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$onClosed_u2dWpGqRn0$iv`)
         select.selectInRegistrationPhase(CHANNEL_CLOSED)
      } else {
         throw IllegalStateException("unreachable".toString())
      }
   }

   internal override fun shouldSendSuspend(): Boolean {
      return false
   }
}
