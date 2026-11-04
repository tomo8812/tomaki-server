package kotlinx.coroutines.flow.internal

import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ProduceKt
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import org.jetbrains.annotations.NotNull

@InternalCoroutinesApi
@SourceDebugExtension(["SMAP\nChannelFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChannelFlow.kt\nkotlinx/coroutines/flow/internal/ChannelFlow\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,245:1\n1#2:246\n*E\n"])
public abstract class ChannelFlow<T> : FusibleFlow<T> {
   @JvmField
   @NotNull
   public final val context: CoroutineContext

   @JvmField
   public final val capacity: Int

   @JvmField
   @NotNull
   public final val onBufferOverflow: BufferOverflow

   open fun ChannelFlow(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow) {
      this.context = context
      this.capacity = capacity
      this.onBufferOverflow = onBufferOverflow
      if (ASSERTIONS_ENABLED && this.capacity == -1) {
         throw AssertionError()
      }
   }

   internal final val collectToFun: (ProducerScope<Any>, Continuation<Unit>) -> Any?
      internal final get() {
         return {
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as (ProducerScope<in T>?, Continuation<in Unit>?) -> Any
      }


   internal final val produceCapacity: Int
      internal final get() {
         return if (this.capacity == -3) -2 else this.capacity
      }


   public open fun dropChannelOperators(): Flow<Any>? {
      return null
   }

   public override fun fuse(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): Flow<Any> {
      if (ASSERTIONS_ENABLED && capacity == -1) {
         throw AssertionError()
      } else {
         val var8: CoroutineContext = context.plus(this.context)
         val var9: Int
         val var10: BufferOverflow
         if (onBufferOverflow != BufferOverflow.SUSPEND) {
            var9 = capacity
            var10 = onBufferOverflow
         } else {
            val var10000: Int
            if (this.capacity == -3) {
               var10000 = capacity
            } else if (capacity == -3) {
               var10000 = this.capacity
            } else if (this.capacity == -2) {
               var10000 = capacity
            } else if (capacity == -2) {
               var10000 = this.capacity
            } else {
               if (ASSERTIONS_ENABLED && this.capacity < 0) {
                  throw AssertionError()
               }

               if (ASSERTIONS_ENABLED && capacity < 0) {
                  throw AssertionError()
               }

               var10000 = if (this.capacity + capacity >= 0) this.capacity + capacity else Integer.MAX_VALUE
            }

            var9 = var10000
            var10 = this.onBufferOverflow
         }

         return if (var8 == this.context && var9 == this.capacity && var10 === this.onBufferOverflow) this else this.create(var8, var9, var10)
      }
   }

   protected abstract fun create(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): ChannelFlow<Any> {
   }

   protected abstract suspend fun collectTo(scope: ProducerScope<Any>) {
   }

   public open fun produceImpl(scope: CoroutineScope): ReceiveChannel<Any> {
      return ProduceKt.produce$default(
         scope, this.context, this.produceCapacity, this.onBufferOverflow, CoroutineStart.ATOMIC, null, this.collectToFun, 16, null
      )
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      return collect$suspendImpl(this, collector, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <T> `collect$suspendImpl`(`$this`: ChannelFlow<T>, collector: FlowCollector<in T>, `$completion`: Continuation<in Unit>) {
      val var10000: Any = CoroutineScopeKt.coroutineScope({
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function2, `$completion`)
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   protected open fun additionalToStringProps(): String? {
      return null
   }

   public override fun toString(): String {
      val props: ArrayList = ArrayList(4)
      val var10000: java.lang.String = this.additionalToStringProps()
      if (var10000 != null) {
         props.add(var10000)
      }

      if (this.context != EmptyCoroutineContext.INSTANCE) {
         props.add("context=${this.context}")
      }

      if (this.capacity != -3) {
         props.add("capacity=${this.capacity}")
      }

      if (this.onBufferOverflow != BufferOverflow.SUSPEND) {
         props.add("onBufferOverflow=${this.onBufferOverflow}")
      }

      return "${classSimpleName}[${CollectionsKt.joinToString$default(props, ", ", null, null, 0, null, null, 62, null)}]"
   }
}
