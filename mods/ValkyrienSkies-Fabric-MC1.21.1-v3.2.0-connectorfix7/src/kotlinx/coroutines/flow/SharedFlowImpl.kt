package kotlinx.coroutines.flow

import java.util.ArrayList
import java.util.Arrays
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.CancellableContinuationKt
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.internal.AbstractSharedFlow
import kotlinx.coroutines.flow.internal.AbstractSharedFlowKt
import kotlinx.coroutines.flow.internal.AbstractSharedFlowSlot
import kotlinx.coroutines.flow.internal.FusibleFlow
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nSharedFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SharedFlow.kt\nkotlinx/coroutines/flow/SharedFlowImpl\n+ 2 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 3 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n+ 4 CoroutineScope.kt\nkotlinx/coroutines/CoroutineScopeKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 6 AbstractSharedFlow.kt\nkotlinx/coroutines/flow/internal/AbstractSharedFlow\n+ 7 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 8 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n*L\n1#1,731:1\n28#2,4:732\n28#2,4:738\n28#2,4:760\n28#2,4:767\n28#2,4:779\n28#2,4:793\n28#2,4:807\n20#3:736\n20#3:742\n20#3:764\n20#3:771\n20#3:783\n20#3:797\n20#3:811\n329#4:737\n1#5:743\n94#6,2:744\n96#6,2:747\n98#6:750\n94#6,2:772\n96#6,2:775\n98#6:778\n94#6,2:800\n96#6,2:803\n98#6:806\n13579#7:746\n13580#7:749\n13579#7:774\n13580#7:777\n13579#7:802\n13580#7:805\n314#8,9:751\n323#8,2:765\n314#8,9:784\n323#8,2:798\n*S KotlinDebug\n*F\n+ 1 SharedFlow.kt\nkotlinx/coroutines/flow/SharedFlowImpl\n*L\n351#1:732,4\n391#1:738,4\n485#1:760,4\n506#1:767,4\n626#1:779,4\n661#1:793,4\n689#1:807,4\n351#1:736\n391#1:742\n485#1:764\n506#1:771\n626#1:783\n661#1:797\n689#1:811\n373#1:737\n453#1:744,2\n453#1:747,2\n453#1:750\n529#1:772,2\n529#1:775,2\n529#1:778\n676#1:800,2\n676#1:803,2\n676#1:806\n453#1:746\n453#1:749\n529#1:774\n529#1:777\n676#1:802\n676#1:805\n483#1:751,9\n483#1:765,2\n660#1:784,9\n660#1:798,2\n*E\n"])
internal open class SharedFlowImpl<T>(replay: Int, bufferCapacity: Int, onBufferOverflow: BufferOverflow)
   : AbstractSharedFlow<SharedFlowSlot>,
   MutableSharedFlow<T>,
   CancellableFlow<T>,
   FusibleFlow<T> {
   private final val replay: Int
   private final val bufferCapacity: Int
   private final val onBufferOverflow: BufferOverflow
   private final var buffer: Array<Any?>?
   private final var replayIndex: Long
   private final var minCollectorIndex: Long
   private final var bufferSize: Int
   private final var queueSize: Int

   init {
      this.replay = replay
      this.bufferCapacity = bufferCapacity
      this.onBufferOverflow = onBufferOverflow
   }

   private final val head: Long
      private final get() {
         return Math.min(this.minCollectorIndex, this.replayIndex)
      }


   private final val replaySize: Int
      private final get() {
         return (int)(this.head + this.bufferSize - this.replayIndex)
      }


   private final val totalSize: Int
      private final get() {
         return this.bufferSize + this.queueSize
      }


   private final val bufferEndIndex: Long
      private final get() {
         return this.head + this.bufferSize
      }


   private final val queueEndIndex: Long
      private final get() {
         return this.head + this.bufferSize + this.queueSize
      }


   public open val replayCache: List<Any>
      public open get() {
         val var12: ArrayList
         synchronized (this) {
            val replaySize: Int = this.replaySize
            if (replaySize == 0) {
               return CollectionsKt.emptyList()
            }

            val result: ArrayList = ArrayList(replaySize)
            val var10000: Array<Any> = this.buffer
            val buffer: Array<Any> = var10000

            repeat(replaySize) { i ->
               result.add(SharedFlowKt.access$getBufferAt(buffer, this.replayIndex + (long)i))
            }

            var12 = result
         }

         return var12
      }


   protected final val lastReplayedLocked: Any
      protected final get() {
         val var10000: Array<Any> = this.buffer
         return (T)SharedFlowKt.access$getBufferAt(var10000, this.replayIndex + (long)this.replaySize - 1L)
      }


   public override suspend fun collect(collector: FlowCollector<Any>): Nothing {
      return collect$suspendImpl(this, collector, `$completion`)
   }

   public override fun tryEmit(value: Any): Boolean {
      var var10: Array<Continuation> = AbstractSharedFlowKt.EMPTY_RESUMES
      var var15: Boolean
      synchronized (this) {
         if (this.tryEmitLocked((T)value)) {
            var10 = this.findSlotsToResumeLocked(var10)
            var15 = true
         } else {
            var15 = false
         }

         var15 = var15
      }

      for (var14 in var10) {
         if (var14 != null) {
            var14.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
         }
      }

      return var15
   }

   public override suspend fun emit(value: Any) {
      return emit$suspendImpl(this, (T)value, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <T> `emit$suspendImpl`(`$this`: SharedFlowImpl<T>, value: T, `$completion`: Continuation<in Unit>) {
      if (`$this`.tryEmit(value)) {
         Unit.INSTANCE
      } else {
         val var10000: Any = `$this`.emitSuspend(value, `$completion`)
         if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }
   }

   private fun tryEmitLocked(value: Any): Boolean {
      if (this.getNCollectors() == 0) {
         return this.tryEmitNoCollectorsLocked((T)value)
      } else {
         if (this.bufferSize >= this.bufferCapacity && this.minCollectorIndex <= this.replayIndex) {
            when (SharedFlowImpl.WhenMappings.$EnumSwitchMapping$0[this.onBufferOverflow.ordinal()]) {
               1 -> return false
               2 -> return true
               3 -> {}
               else -> {}
            }
         }

         this.enqueueLocked(value)
         val var2: Int = this.bufferSize++
         if (this.bufferSize > this.bufferCapacity) {
            this.dropOldestLocked()
         }

         if (this.replaySize > this.replay) {
            this.updateBufferLocked(this.replayIndex + 1L, this.minCollectorIndex, this.bufferEndIndex, this.queueEndIndex)
         }

         return true
      }
   }

   private fun tryEmitNoCollectorsLocked(value: Any): Boolean {
      if (ASSERTIONS_ENABLED && this.getNCollectors() != 0) {
         throw AssertionError()
      } else if (this.replay == 0) {
         return true
      } else {
         this.enqueueLocked(value)
         val var3: Int = this.bufferSize++
         if (this.bufferSize > this.replay) {
            this.dropOldestLocked()
         }

         this.minCollectorIndex = this.head + this.bufferSize
         return true
      }
   }

   private fun dropOldestLocked() {
      val var10000: Array<Any> = this.buffer
      SharedFlowKt.access$setBufferAt(var10000, this.head, null)
      this.bufferSize += -1
      val var4: Long = this.head + 1L
      if (this.replayIndex < var4) {
         this.replayIndex = var4
      }

      if (this.minCollectorIndex < var4) {
         this.correctCollectorIndexesOnDropOldest(var4)
      }

      if (ASSERTIONS_ENABLED && this.head != var4) {
         throw AssertionError()
      }
   }

   private fun correctCollectorIndexesOnDropOldest(newHead: Long) {
      val `this_$iv`: AbstractSharedFlow = this
      if (AbstractSharedFlow.access$getNCollectors(this) != 0) {
         val var10000: Array<Any> = AbstractSharedFlow.access$getSlots(`this_$iv`)
         if (var10000 != null) {
            for (`element$iv$iv` in var10000) {
               if (`element$iv$iv` != null) {
                  val slot: SharedFlowSlot = `element$iv$iv` as SharedFlowSlot
                  if ((`element$iv$iv` as SharedFlowSlot).index >= 0L && (`element$iv$iv` as SharedFlowSlot).index < newHead) {
                     slot.index = newHead
                  }
               }
            }
         }
      }

      this.minCollectorIndex = newHead
   }

   private fun enqueueLocked(item: Any?) {
      val curSize: Int = this.totalSize
      SharedFlowKt.access$setBufferAt(
         if (this.buffer == null)
            this.growBuffer(null, 0, 2)
            else
            (if (curSize >= this.buffer.length) this.growBuffer(this.buffer, curSize, this.buffer.length * 2) else this.buffer),
         this.head + (long)curSize,
         item
      )
   }

   private fun growBuffer(curBuffer: Array<Any?>?, curSize: Int, newSize: Int): Array<Any?> {
      if (newSize <= 0) {
         throw IllegalStateException("Buffer size overflow".toString())
      } else {
         val head: Array<Any> = arrayOfNulls(newSize)
         this.buffer = head
         val newBuffer: Array<Any> = head
         if (curBuffer == null) {
            return head
         } else {
            val var8: Long = this.head

            repeat(curSize) { var11 ->
               SharedFlowKt.access$setBufferAt(newBuffer, var8 + (long)var11, SharedFlowKt.access$getBufferAt(curBuffer, var8 + (long)var11))
            }

            return newBuffer
         }
      }
   }

   private suspend fun emitSuspend(value: Any) {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
      `cancellable$iv`.initCancellability()
      val cont: CancellableContinuation = `cancellable$iv`
      var var21: Array<Continuation> = AbstractSharedFlowKt.EMPTY_RESUMES
      var var28: SharedFlowImpl.Emitter
      synchronized (this) {
         if (access$tryEmitLocked(this, value)) {
            cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
            var21 = access$findSlotsToResumeLocked(this, var21)
            var28 = null
         } else {
            val itx: SharedFlowImpl.Emitter = SharedFlowImpl.Emitter(this, access$getHead(this) + access$getTotalSize(this), value, cont)
            access$enqueueLocked(this, itx)
            access$setQueueSize$p(this, access$getQueueSize$p(this) + 1)
            if (access$getBufferCapacity$p(this) == 0) {
               var21 = access$findSlotsToResumeLocked(this, var21)
            }

            var28 = itx
         }

         var28 = var28
      }

      if (var28 != null) {
         CancellableContinuationKt.disposeOnCancellation(cont, var28)
      }

      for (var27 in var21) {
         if (var27 != null) {
            var27.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
         }
      }

      val var29: Any = `cancellable$iv`.getResult()
      if (var29 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return if (var29 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var29 else Unit.INSTANCE
   }

   private fun cancelEmitter(emitter: kotlinx.coroutines.flow.SharedFlowImpl.Emitter) {
      synchronized (this) {
         if (emitter.index < this.head) {
            return
         }

         val var10000: Array<Any> = this.buffer
         if (SharedFlowKt.access$getBufferAt(var10000, emitter.index) === emitter) {
            SharedFlowKt.access$setBufferAt(var10000, emitter.index, SharedFlowKt.NO_VALUE)
            this.cleanupTailLocked()
            return
         }
      }
   }

   internal fun updateNewCollectorIndexLocked(): Long {
      val index: Long = this.replayIndex
      if (this.replayIndex < this.minCollectorIndex) {
         this.minCollectorIndex = this.replayIndex
      }

      return this.replayIndex
   }

   internal fun updateCollectorIndexLocked(oldIndex: Long): Array<Continuation<Unit>?> {
      if (ASSERTIONS_ENABLED && oldIndex < this.minCollectorIndex) {
         throw AssertionError()
      } else if (oldIndex > this.minCollectorIndex) {
         return AbstractSharedFlowKt.EMPTY_RESUMES
      } else {
         val var20: Long = this.head
         var newMinCollectorIndex: Long = 0L
         newMinCollectorIndex = var20 + this.bufferSize
         if (this.bufferCapacity == 0 && this.queueSize > 0) {
            newMinCollectorIndex += 1L
         }

         val var21: AbstractSharedFlow = this
         if (AbstractSharedFlow.access$getNCollectors(this) != 0) {
            val var10000: Array<Any> = AbstractSharedFlow.access$getSlots(var21)
            if (var10000 != null) {
               for (newBufferSize1 in var10000) {
                  if (newBufferSize1 != null) {
                     val slot: SharedFlowSlot = newBufferSize1 as SharedFlowSlot
                     if ((newBufferSize1 as SharedFlowSlot).index >= 0L && (newBufferSize1 as SharedFlowSlot).index < newMinCollectorIndex) {
                        newMinCollectorIndex = slot.index
                     }
                  }
               }
            }
         }

         if (ASSERTIONS_ENABLED && newMinCollectorIndex < this.minCollectorIndex) {
            throw AssertionError()
         } else if (newMinCollectorIndex <= this.minCollectorIndex) {
            return AbstractSharedFlowKt.EMPTY_RESUMES
         } else {
            var var23: Long = this.bufferEndIndex
            val var24: Int
            val var37: Int = var24 = if (this.getNCollectors() > 0)
               Math.min(this.queueSize, this.bufferCapacity - (int)(var23 - newMinCollectorIndex))
               else
               this.queueSize
               var var26: Array<Continuation> = AbstractSharedFlowKt.EMPTY_RESUMES
            val var27: Long = var23 + this.queueSize
            if (var37 > 0) {
               var26 = arrayOfNulls(var37)
               var var28: Int = 0
               val var38: Array<Any> = this.buffer
               val var30: Array<Any> = var38

               for (var32 in var23..var27) {
                  val var35: Any = SharedFlowKt.access$getBufferAt(var30, var32)
                  if (var35 != SharedFlowKt.NO_VALUE) {
                     var26[var28++] = (var35 as SharedFlowImpl.Emitter).cont
                     SharedFlowKt.access$setBufferAt(var30, var32, SharedFlowKt.NO_VALUE)
                     SharedFlowKt.access$setBufferAt(var30, var23, (var35 as SharedFlowImpl.Emitter).value)
                     var23 += 1L
                     if (var28 >= var24) {
                        break
                     }
                  }
               }
            }

            val var29: Int = (int)(var23 - var20)
            if (this.getNCollectors() == 0) {
               newMinCollectorIndex = var23
            }

            var var31: Long = Math.max(this.replayIndex, var23 - (long)Math.min(this.replay, var29))
            if (this.bufferCapacity == 0 && var31 < var27) {
               val var39: Array<Any> = this.buffer
               if (SharedFlowKt.access$getBufferAt(var39, var31) == SharedFlowKt.NO_VALUE) {
                  var23 += 1L
                  var31 += 1L
               }
            }

            this.updateBufferLocked(var31, newMinCollectorIndex, var23, var27)
            this.cleanupTailLocked()
            if (var26.length != 0) {
               var26 = this.findSlotsToResumeLocked(var26)
            }

            return var26
         }
      }
   }

   private fun updateBufferLocked(newReplayIndex: Long, newMinCollectorIndex: Long, newBufferEndIndex: Long, newQueueEndIndex: Long) {
      val newHead: Long = Math.min(newMinCollectorIndex, newReplayIndex)
      if (ASSERTIONS_ENABLED && newHead < this.head) {
         throw AssertionError()
      } else {
         for (var13 in this.head..newHead) {
            val var10000: Array<Any> = this.buffer
            SharedFlowKt.access$setBufferAt(var10000, var13, null)
         }

         this.replayIndex = newReplayIndex
         this.minCollectorIndex = newMinCollectorIndex
         this.bufferSize = (int)(newBufferEndIndex - newHead)
         this.queueSize = (int)(newQueueEndIndex - newBufferEndIndex)
         if (ASSERTIONS_ENABLED && this.bufferSize < 0) {
            throw AssertionError()
         } else if (ASSERTIONS_ENABLED && this.queueSize < 0) {
            throw AssertionError()
         } else if (ASSERTIONS_ENABLED && this.replayIndex > this.head + this.bufferSize) {
            throw AssertionError()
         }
      }
   }

   private fun cleanupTailLocked() {
      if (this.bufferCapacity != 0 || this.queueSize > 1) {
         val var10000: Array<Any> = this.buffer
         val buffer: Array<Any> = var10000

         while (this.queueSize > 0 && SharedFlowKt.access$getBufferAt(buffer, this.head + (long)this.totalSize - 1L) === SharedFlowKt.NO_VALUE) {
            this.queueSize += -1
            SharedFlowKt.access$setBufferAt(buffer, this.head + (long)this.totalSize, null)
         }
      }
   }

   private fun tryTakeValue(slot: SharedFlowSlot): Any? {
      var var15: Array<Continuation> = AbstractSharedFlowKt.EMPTY_RESUMES
      var var20: Any
      synchronized (this) {
         val index: Long = this.tryPeekLocked(slot)
         if (index < 0L) {
            var20 = SharedFlowKt.NO_VALUE
         } else {
            val oldIndex: Long = slot.index
            val newValue: Any = this.getPeekedValueLockedAt(index)
            slot.index = index + 1L
            var15 = this.updateCollectorIndexLocked$kotlinx_coroutines_core(oldIndex)
            var20 = newValue
         }

         var20 = var20
      }

      for (var19 in var15) {
         if (var19 != null) {
            var19.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
         }
      }

      return var20
   }

   private fun tryPeekLocked(slot: SharedFlowSlot): Long {
      val index: Long = slot.index
      if (slot.index < this.bufferEndIndex) {
         return index
      } else if (this.bufferCapacity > 0) {
         return -1L
      } else if (index > this.head) {
         return -1L
      } else {
         return if (this.queueSize == 0) -1L else index
      }
   }

   private fun getPeekedValueLockedAt(index: Long): Any? {
      val var10000: Array<Any> = this.buffer
      val item: Any = SharedFlowKt.access$getBufferAt(var10000, index)
      return if (item is SharedFlowImpl.Emitter) (item as SharedFlowImpl.Emitter).value else item
   }

   private suspend fun awaitValue(slot: SharedFlowSlot) {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
      `cancellable$iv`.initCancellability()
      val cont: CancellableContinuation = `cancellable$iv`
      synchronized (this) {
         if (access$tryPeekLocked(this, slot) < 0L) {
            slot.cont = cont
            slot.cont = cont
         } else {
            cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
         }
      }

      val var10000: Any = `cancellable$iv`.getResult()
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   private fun findSlotsToResumeLocked(resumesIn: Array<Continuation<Unit>?>): Array<Continuation<Unit>?> {
      var var17: Array<Any> = resumesIn
      var var18: Int = resumesIn.length
      val `this_$iv`: AbstractSharedFlow = this
      if (AbstractSharedFlow.access$getNCollectors(this) != 0) {
         val var10000: Array<Any> = AbstractSharedFlow.access$getSlots(`this_$iv`)
         if (var10000 != null) {
            for (`element$iv$iv` in var10000) {
               if (`element$iv$iv` != null) {
                  val slot: SharedFlowSlot = `element$iv$iv` as SharedFlowSlot
                  if ((`element$iv$iv` as SharedFlowSlot).cont != null) {
                     val cont: Continuation = slot.cont
                     if (this.tryPeekLocked(slot) >= 0L) {
                        if (var18 >= var17.length) {
                           val var19: Array<Any> = Arrays.copyOf(var17, Math.max(2, 2 * var17.length))
                           var17 = var19
                        }

                        val var20: Array<Continuation> = var17 as Array<Continuation>
                        val var16: Int = var18
                        var18 += 1
                        var20[var16] = cont
                        slot.cont = null
                     }
                  }
               }
            }
         }
      }

      return var17 as Array<Continuation<Unit>>
   }

   protected open fun createSlot(): SharedFlowSlot {
      return SharedFlowSlot()
   }

   protected open fun createSlotArray(size: Int): Array<SharedFlowSlot?> {
      return arrayOfNulls(size)
   }

   public override fun resetReplayCache() {
      synchronized (this) {
         this.updateBufferLocked(this.bufferEndIndex, this.minCollectorIndex, this.bufferEndIndex, this.queueEndIndex)
      }
   }

   public override fun fuse(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): Flow<Any> {
      return SharedFlowKt.fuseSharedFlow(this, context, capacity, onBufferOverflow)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun createSlot(): AbstractSharedFlowSlot {
      this.createSlot() as AbstractSharedFlowSlot
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun createSlotArray(size: Int): Array<AbstractSharedFlowSlot> {
      this.createSlotArray(size) as Array<AbstractSharedFlowSlot>
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$emitSuspend`(`$this`: SharedFlowImpl, value: Any, `$completion`: Continuation) {
      `$this`.emitSuspend(value, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$awaitValue`(`$this`: SharedFlowImpl, slot: SharedFlowSlot, `$completion`: Continuation) {
      `$this`.awaitValue(slot, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$enqueueLocked`(`$this`: SharedFlowImpl, item: Any) {
      `$this`.enqueueLocked(item)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getQueueSize$p`(`$this`: SharedFlowImpl): Int {
      `$this`.queueSize
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$setQueueSize$p`(`$this`: SharedFlowImpl, `<set-?>`: Int) {
      `$this`.queueSize = `<set-?>`
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getBufferCapacity$p`(`$this`: SharedFlowImpl): Int {
      `$this`.bufferCapacity
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$findSlotsToResumeLocked`(`$this`: SharedFlowImpl, resumesIn: Array<Continuation>): Array<Continuation> {
      `$this`.findSlotsToResumeLocked(resumesIn)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$tryEmitLocked`(`$this`: SharedFlowImpl, value: Any): Boolean {
      `$this`.tryEmitLocked(value)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getHead`(`$this`: SharedFlowImpl): Long {
      `$this`.head
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getTotalSize`(`$this`: SharedFlowImpl): Int {
      `$this`.totalSize
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$tryPeekLocked`(`$this`: SharedFlowImpl, slot: SharedFlowSlot): Long {
      `$this`.tryPeekLocked(slot)
   }

   private class Emitter(flow: SharedFlowImpl<*>, index: Long, value: Any?, cont: Continuation<Unit>) : DisposableHandle {
      @JvmField
      @NotNull
      public final val flow: SharedFlowImpl<*>

      @JvmField
      public final var index: Long
         private set

      @JvmField
      @Nullable
      public final val value: Any?

      @JvmField
      @NotNull
      public final val cont: Continuation<Unit>

      init {
         this.flow = flow
         this.index = index
         this.value = value
         this.cont = cont
      }

      public override fun dispose() {
         this.flow.cancelEmitter(this)
      }
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class WhenMappings {
      @JvmStatic
      @JvmSynthetic
      public int[] $EnumSwitchMapping$0;

      @JvmStatic
      fun {
         val var0: IntArray = IntArray(BufferOverflow.values().length)

         try {
            var0[BufferOverflow.SUSPEND.ordinal()] = 1
         } catch (var4: NoSuchFieldError) {
         }

         try {
            var0[BufferOverflow.DROP_LATEST.ordinal()] = 2
         } catch (var3: NoSuchFieldError) {
         }

         try {
            var0[BufferOverflow.DROP_OLDEST.ordinal()] = 3
         } catch (var2: NoSuchFieldError) {
         }

         $EnumSwitchMapping$0 = var0
      }
   }
}
