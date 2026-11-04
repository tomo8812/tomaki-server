package kotlinx.coroutines.channels

import java.util.concurrent.atomic.AtomicReferenceArray
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.Waiter
import kotlinx.coroutines.internal.OnUndeliveredElementKt
import kotlinx.coroutines.internal.Segment

@SourceDebugExtension(["SMAP\nBufferedChannel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BufferedChannel.kt\nkotlinx/coroutines/channels/ChannelSegment\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,3055:1\n1#2:3056\n*E\n"])
internal class ChannelSegment<E>(id: Long, prev: ChannelSegment<Any>?, channel: BufferedChannel<Any>?, pointers: Int) : Segment(id, prev, pointers) {
   private final val _channel: BufferedChannel<Any>?
   private AtomicReferenceArray data;

   init {
      this._channel = channel
      this.data = AtomicReferenceArray(BufferedChannelKt.SEGMENT_SIZE * 2)
   }

   public final val channel: BufferedChannel<Any>
      public final get() {
         val var10000: BufferedChannel = this._channel
         return var10000
      }


   public open val numberOfSlots: Int
      public open get() {
         return BufferedChannelKt.SEGMENT_SIZE
      }


   internal fun storeElement(index: Int, element: Any) {
      this.setElementLazy(index, element)
   }

   internal fun getElement(index: Int): Any {
      return (E)this.data.get(index * 2)
   }

   internal fun retrieveElement(index: Int): Any {
      val var2: Any = this.getElement$kotlinx_coroutines_core(index)
      this.cleanElement$kotlinx_coroutines_core(index)
      return (E)var2
   }

   internal fun cleanElement(index: Int) {
      this.setElementLazy(index, null)
   }

   private fun setElementLazy(index: Int, value: Any?) {
      this.data.lazySet(index * 2, value)
   }

   internal fun getState(index: Int): Any? {
      return this.data.get(index * 2 + 1)
   }

   internal fun setState(index: Int, value: Any?) {
      this.data.set(index * 2 + 1, value)
   }

   internal fun casState(index: Int, from: Any?, to: Any?): Boolean {
      return this.data.compareAndSet(index * 2 + 1, from, to)
   }

   internal fun getAndSetState(index: Int, update: Any?): Any? {
      return this.data.getAndSet(index * 2 + 1, update)
   }

   public override fun onCancellation(index: Int, cause: Throwable?, context: CoroutineContext) {
      val isSender: Boolean = index >= BufferedChannelKt.SEGMENT_SIZE
      val indexx: Int = if (index >= BufferedChannelKt.SEGMENT_SIZE) index - BufferedChannelKt.SEGMENT_SIZE else index
      val element: Any = this.getElement$kotlinx_coroutines_core(if (index >= BufferedChannelKt.SEGMENT_SIZE) index - BufferedChannelKt.SEGMENT_SIZE else index)

      while (true) {
         val cur: Any = this.getState$kotlinx_coroutines_core(indexx)
         if (cur !is Waiter && cur !is WaiterEB) {
            if (cur === BufferedChannelKt.access$getINTERRUPTED_SEND$p() || cur === BufferedChannelKt.access$getINTERRUPTED_RCV$p()) {
               this.cleanElement$kotlinx_coroutines_core(indexx)
               if (isSender) {
                  val var9: Function1 = this.channel.onUndeliveredElement
                  if (var9 != null) {
                     OnUndeliveredElementKt.callUndeliveredElement(var9, element, context)
                  }
               }

               return
            }

            if (cur != BufferedChannelKt.access$getRESUMING_BY_EB$p() && cur != BufferedChannelKt.access$getRESUMING_BY_RCV$p()) {
               if (cur != BufferedChannelKt.access$getDONE_RCV$p() && cur != BufferedChannelKt.BUFFERED) {
                  if (cur === CHANNEL_CLOSED) {
                     return
                  }

                  throw IllegalStateException(("unexpected state: $cur").toString())
               }

               return
            }
         } else if (this.casState$kotlinx_coroutines_core(
            indexx, cur, if (isSender) BufferedChannelKt.access$getINTERRUPTED_SEND$p() else BufferedChannelKt.access$getINTERRUPTED_RCV$p()
         )) {
            this.cleanElement$kotlinx_coroutines_core(indexx)
            this.onCancelledRequest(indexx, !isSender)
            if (isSender) {
               val var10000: Function1 = this.channel.onUndeliveredElement
               if (var10000 != null) {
                  OnUndeliveredElementKt.callUndeliveredElement(var10000, element, context)
               }
            }

            return
         }
      }
   }

   public fun onCancelledRequest(index: Int, receiver: Boolean) {
      if (receiver) {
         this.channel.waitExpandBufferCompletion$kotlinx_coroutines_core(this.id * (long)BufferedChannelKt.SEGMENT_SIZE + (long)index)
      }

      this.onSlotCleaned()
   }
}
