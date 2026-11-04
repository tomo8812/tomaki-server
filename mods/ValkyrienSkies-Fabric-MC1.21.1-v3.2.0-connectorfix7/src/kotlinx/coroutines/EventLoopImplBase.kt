package kotlinx.coroutines

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.LockFreeTaskQueueCore
import kotlinx.coroutines.internal.ThreadSafeHeap
import kotlinx.coroutines.internal.ThreadSafeHeapNode

@SourceDebugExtension(["SMAP\nEventLoop.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventLoop.common.kt\nkotlinx/coroutines/EventLoopImplBase\n+ 2 ThreadSafeHeap.kt\nkotlinx/coroutines/internal/ThreadSafeHeap\n+ 3 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 4 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n+ 5 EventLoop.kt\nkotlinx/coroutines/EventLoopKt\n+ 6 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,543:1\n60#2:544\n61#2,7:550\n28#3,4:545\n20#4:549\n56#5:557\n1#6:558\n*S KotlinDebug\n*F\n+ 1 EventLoop.common.kt\nkotlinx/coroutines/EventLoopImplBase\n*L\n269#1:544\n269#1:550,7\n269#1:545,4\n269#1:549\n280#1:557\n*E\n"])
internal abstract class EventLoopImplBase : EventLoopImplPlatform, Delay {
   @Volatile
   @Volatile
   private Object _queue;
   @Volatile
   @Volatile
   private Object _delayed;
   @Volatile
   @Volatile
   private int _isCompleted = 0;
   @JvmStatic
   private AtomicReferenceFieldUpdater _queue$FU = AtomicReferenceFieldUpdater.newUpdater(EventLoopImplBase.class, Object.class, "_queue");
   @JvmStatic
   private AtomicReferenceFieldUpdater _delayed$FU = AtomicReferenceFieldUpdater.newUpdater(EventLoopImplBase.class, Object.class, "_delayed");
   @JvmStatic
   private AtomicIntegerFieldUpdater _isCompleted$FU = AtomicIntegerFieldUpdater.newUpdater(EventLoopImplBase.class, "_isCompleted");

   private final var isCompleted: Boolean
      private final get() {
         return _isCompleted$FU.get(this) != 0
      }

      private final set(value) {
         _isCompleted$FU.set(this, value)
      }


   protected open val isEmpty: Boolean
      protected open get() {
         if (!this.isUnconfinedQueueEmpty()) {
            return false
         } else {
            val delayed: EventLoopImplBase.DelayedTaskQueue = _delayed$FU.get(this) as EventLoopImplBase.DelayedTaskQueue
            if (delayed != null && !delayed.isEmpty()) {
               return false
            } else {
               val queue: Any = _queue$FU.get(this)
               return queue == null
                  || (if (queue is LockFreeTaskQueueCore) (queue as LockFreeTaskQueueCore).isEmpty else queue === EventLoop_commonKt.access$getCLOSED_EMPTY$p())
               }
         }
      }


   protected open val nextTime: Long
      protected open get() {
         if (super.getNextTime() == 0L) {
            return 0L
         } else {
            val queue: Any = _queue$FU.get(this)
            if (queue != null) {
               if (queue !is LockFreeTaskQueueCore) {
                  if (queue === EventLoop_commonKt.access$getCLOSED_EMPTY$p()) {
                     return java.lang.Long.MAX_VALUE
                  }

                  return 0L
               }

               if (!(queue as LockFreeTaskQueueCore).isEmpty) {
                  return 0L
               }
            }

            val var10000: EventLoopImplBase.DelayedTaskQueue = _delayed$FU.get(this) as EventLoopImplBase.DelayedTaskQueue
            if (var10000 != null) {
               val var3: EventLoopImplBase.DelayedTask = var10000.peek()
               if (var3 != null) {
                  val var4: Long = var3.nanoTime
                  val var10001: AbstractTimeSource = timeSource
                  return RangesKt.coerceAtLeast(var4 - (if (var10001 != null) var10001.nanoTime() else System.nanoTime()), 0L)
               }
            }

            return java.lang.Long.MAX_VALUE
         }
      }


   public override fun shutdown() {
      ThreadLocalEventLoop.INSTANCE.resetEventLoop$kotlinx_coroutines_core()
      this.isCompleted = true
      this.closeQueue()

      while (this.processNextEvent() <= 0L) {
      }

      this.rescheduleAllDelayed()
   }

   public override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
      val timeNanos: Long = EventLoop_commonKt.delayToNanos(timeMillis)
      if (timeNanos < 4611686018427387903L) {
         val var10000: AbstractTimeSource = timeSource
         val now: Long = if (var10000 != null) var10000.nanoTime() else System.nanoTime()
         val task: EventLoopImplBase.DelayedResumeTask = EventLoopImplBase.DelayedResumeTask(now + timeNanos, continuation)
         this.schedule(now, task)
         CancellableContinuationKt.disposeOnCancellation(continuation, task)
      }
   }

   protected fun scheduleInvokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
      val timeNanos: Long = EventLoop_commonKt.delayToNanos(timeMillis)
      val var11: DisposableHandle
      if (timeNanos < 4611686018427387903L) {
         val var10000: AbstractTimeSource = timeSource
         val now: Long = if (var10000 != null) var10000.nanoTime() else System.nanoTime()
         val var8: EventLoopImplBase.DelayedRunnableTask = EventLoopImplBase.DelayedRunnableTask(now + timeNanos, block)
         this.schedule(now, var8)
         var11 = var8
      } else {
         var11 = NonDisposableHandle.INSTANCE
      }

      return var11
   }

   public override fun processNextEvent(): Long {
      if (this.processUnconfinedEvent()) {
         return 0L
      } else {
         val delayed: EventLoopImplBase.DelayedTaskQueue = _delayed$FU.get(this) as EventLoopImplBase.DelayedTaskQueue
         if (delayed != null && !delayed.isEmpty()) {
            val var10000: AbstractTimeSource = timeSource
            val task: Long = if (var10000 != null) var10000.nanoTime() else System.nanoTime()

            do {
               var var15: ThreadSafeHeapNode
               run label118@{
                  val `this_$iv`: ThreadSafeHeap = delayed
                  val var11: Any
                  synchronized (delayed as ThreadSafeHeap) {
                     val var19: ThreadSafeHeapNode = `this_$iv`.firstImpl()
                     if (var19 != null) {
                        var15 = if ((var19 as EventLoopImplBase.DelayedTask).timeToExecute(task) && this.enqueueImpl(var19 as EventLoopImplBase.DelayedTask))
                           `this_$iv`.removeAtImpl(0)
                           else
                           null
                           return@label118
                     }

                     var11 = null
                  }

                  var20 = (ThreadSafeHeapNode)var11
                  continue
               }

               var20 = var15
            } while (var20 as EventLoopImplBase.DelayedTask != null)
         }

         val var18: Runnable = this.dequeue()
         if (var18 != null) {
            var18.run()
            return 0L
         } else {
            return this.nextTime
         }
      }
   }

   public override fun dispatch(context: CoroutineContext, block: Runnable) {
      this.enqueue(block)
   }

   public open fun enqueue(task: Runnable) {
      if (this.enqueueImpl(task)) {
         this.unpark()
      } else {
         DefaultExecutor.INSTANCE.enqueue(task)
      }
   }

   private fun enqueueImpl(task: Runnable): Boolean {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _queue$FU

      while (true) {
         val queue: Any = `atomicfu$handler$iv`.get(this)
         if (this.isCompleted) {
            return false
         }

         if (queue == null) {
            if (_queue$FU.compareAndSet(this, null, task)) {
               return true
            }
         } else if (queue is LockFreeTaskQueueCore) {
            when ((queue as LockFreeTaskQueueCore).addLast(task)) {
               0 -> return true
               1 -> {
                  _queue$FU.compareAndSet(this, queue, (queue as LockFreeTaskQueueCore).next())
                  continue
               }
               2 -> return false
               else -> {}
            }
         } else {
            if (queue === EventLoop_commonKt.access$getCLOSED_EMPTY$p()) {
               return false
            }

            val newQueue: LockFreeTaskQueueCore = LockFreeTaskQueueCore(8, true)
            newQueue.addLast(queue as Runnable)
            newQueue.addLast(task)
            if (_queue$FU.compareAndSet(this, queue, newQueue)) {
               return true
            }
         }
      }
   }

   private fun dequeue(): Runnable? {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _queue$FU

      while (true) {
         val queue: Any = `atomicfu$handler$iv`.get(this)
         if (queue == null) {
            return null
         }

         if (queue is LockFreeTaskQueueCore) {
            val result: Any = (queue as LockFreeTaskQueueCore).removeFirstOrNull()
            if (result != LockFreeTaskQueueCore.REMOVE_FROZEN) {
               return result as Runnable
            }

            _queue$FU.compareAndSet(this, queue, (queue as LockFreeTaskQueueCore).next())
         } else {
            if (queue === EventLoop_commonKt.access$getCLOSED_EMPTY$p()) {
               return null
            }

            if (_queue$FU.compareAndSet(this, queue, null)) {
               return queue as Runnable
            }
         }
      }
   }

   private fun closeQueue() {
      if (ASSERTIONS_ENABLED && !this.isCompleted) {
         throw AssertionError()
      } else {
         val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _queue$FU

         while (true) {
            val queue: Any = `atomicfu$handler$iv`.get(this)
            if (queue == null) {
               if (_queue$FU.compareAndSet(this, null, EventLoop_commonKt.access$getCLOSED_EMPTY$p())) {
                  return
               }
            } else {
               if (queue is LockFreeTaskQueueCore) {
                  (queue as LockFreeTaskQueueCore).close()
                  return
               }

               if (queue === EventLoop_commonKt.access$getCLOSED_EMPTY$p()) {
                  return
               }

               val newQueue: LockFreeTaskQueueCore = LockFreeTaskQueueCore(8, true)
               newQueue.addLast(queue as Runnable)
               if (_queue$FU.compareAndSet(this, queue, newQueue)) {
                  return
               }
            }
         }
      }
   }

   public fun schedule(now: Long, delayedTask: kotlinx.coroutines.EventLoopImplBase.DelayedTask) {
      when (this.scheduleImpl(now, delayedTask)) {
         0 -> {
            if (this.shouldUnpark(delayedTask)) {
               this.unpark()
            }
         }
         1 -> this.reschedule(now, delayedTask)
         2 -> {}
         else -> throw IllegalStateException("unexpected result".toString())
      }
   }

   private fun shouldUnpark(task: kotlinx.coroutines.EventLoopImplBase.DelayedTask): Boolean {
      val var10000: EventLoopImplBase.DelayedTaskQueue = _delayed$FU.get(this) as EventLoopImplBase.DelayedTaskQueue
      return (if (var10000 != null) var10000.peek() else null) === task
   }

   private fun scheduleImpl(now: Long, delayedTask: kotlinx.coroutines.EventLoopImplBase.DelayedTask): Int {
      if (this.isCompleted) {
         return 1
      } else {
         var var10000: EventLoopImplBase.DelayedTaskQueue = _delayed$FU.get(this) as EventLoopImplBase.DelayedTaskQueue
         if (var10000 == null) {
            val `$this$scheduleImpl_u24lambda_u248`: EventLoopImplBase = this
            _delayed$FU.compareAndSet(this, null, EventLoopImplBase.DelayedTaskQueue(now))
            var10000 = (EventLoopImplBase.DelayedTaskQueue)_delayed$FU.get(`$this$scheduleImpl_u24lambda_u248`)
            var10000 = var10000
         }

         return delayedTask.scheduleTask(now, var10000, this)
      }
   }

   protected fun resetAll() {
      _queue$FU.set(this, null)
      _delayed$FU.set(this, null)
   }

   private fun rescheduleAllDelayed() {
      val var10000: AbstractTimeSource = timeSource
      val now: Long = if (var10000 != null) var10000.nanoTime() else System.nanoTime()

      while (true) {
         val var4: EventLoopImplBase.DelayedTaskQueue = _delayed$FU.get(this) as EventLoopImplBase.DelayedTaskQueue
         if (var4 == null) {
            break
         }

         val var5: EventLoopImplBase.DelayedTask = var4.removeFirstOrNull()
         if (var5 == null) {
            break
         }

         this.reschedule(now, var5)
      }
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated without replacement as an internal method never intended for public use", level = DeprecationLevel.ERROR)
   override fun delay(time: Long, `$completion`: Continuation<in Unit>) {
      Delay.DefaultImpls.delay(this, time, `$completion`)
   }

   override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
      Delay.DefaultImpls.invokeOnTimeout(this, timeMillis, block, context)
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   @SourceDebugExtension(["SMAP\nEventLoop.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventLoop.common.kt\nkotlinx/coroutines/EventLoopImplBase$DelayedResumeTask\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,543:1\n1#2:544\n*E\n"])
   private inner class DelayedResumeTask(nanoTime: Long, cont: CancellableContinuation<Unit>) : EventLoopImplBase.DelayedTask(nanoTime) {
      private final val cont: CancellableContinuation<Unit>

      init {
         this.cont = cont
      }

      public override fun run() {
         this.cont.resumeUndispatched(EventLoopImplBase.this, Unit.INSTANCE)
      }

      public override fun toString(): String {
         return "${super.toString()}${this.cont}"
      }
   }

   private class DelayedRunnableTask(nanoTime: Long, block: Runnable) : EventLoopImplBase.DelayedTask(nanoTime) {
      private final val block: Runnable

      init {
         this.block = block
      }

      public override fun run() {
         this.block.run()
      }

      public override fun toString(): String {
         return "${super.toString()}${this.block}"
      }
   }

   @SourceDebugExtension(["SMAP\nEventLoop.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventLoop.common.kt\nkotlinx/coroutines/EventLoopImplBase$DelayedTask\n+ 2 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 3 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n+ 4 ThreadSafeHeap.kt\nkotlinx/coroutines/internal/ThreadSafeHeap\n*L\n1#1,543:1\n28#2,4:544\n28#2,4:550\n28#2,4:562\n20#3:548\n20#3:554\n20#3:566\n72#4:549\n73#4,7:555\n*S KotlinDebug\n*F\n+ 1 EventLoop.common.kt\nkotlinx/coroutines/EventLoopImplBase$DelayedTask\n*L\n437#1:544,4\n439#1:550,4\n479#1:562,4\n437#1:548\n439#1:554\n479#1:566\n439#1:549\n439#1:555,7\n*E\n"])
   internal abstract class DelayedTask : Runnable, java.lang.Comparable<EventLoopImplBase.DelayedTask>, DisposableHandle, ThreadSafeHeapNode {
      @JvmField
      public final var nanoTime: Long
         private set

      private final var _heap: Any?
      public open var index: Int

      open fun DelayedTask(nanoTime: Long) {
         this.nanoTime = nanoTime
         this.index = -1
      }

      public open var heap: ThreadSafeHeap<*>?
         public open get() {
            return this._heap as? ThreadSafeHeap
         }

         public open set(value) {
            if (this._heap === EventLoop_commonKt.access$getDISPOSED_TASK$p()) {
               throw IllegalArgumentException("Failed requirement.".toString())
            } else {
               this._heap = value
            }
         }


      public open operator fun compareTo(other: kotlinx.coroutines.EventLoopImplBase.DelayedTask): Int {
         return if (this.nanoTime - other.nanoTime > 0L) 1 else (if (this.nanoTime - other.nanoTime < 0L) -1 else 0)
      }

      public fun timeToExecute(now: Long): Boolean {
         return now - this.nanoTime >= 0L
      }

      public fun scheduleTask(now: Long, delayed: kotlinx.coroutines.EventLoopImplBase.DelayedTaskQueue, eventLoop: EventLoopImplBase): Int {
         val var26: Byte
         synchronized (this) {
            if (this._heap != EventLoop_commonKt.access$getDISPOSED_TASK$p()) {
               val `this_$iv`: ThreadSafeHeap = delayed
               synchronized (delayed as ThreadSafeHeap) {
                  val firstTask: EventLoopImplBase.DelayedTask = `this_$iv`.firstImpl() as EventLoopImplBase.DelayedTask
                  if (eventLoop.isCompleted) {
                     return 1
                  }

                  if (firstTask == null) {
                     delayed.timeNow = now
                  } else {
                     val minTime: Long = if (firstTask.nanoTime - now >= 0L) now else firstTask.nanoTime
                     if ((if (firstTask.nanoTime - now >= 0L) now else firstTask.nanoTime) - delayed.timeNow > 0L) {
                        delayed.timeNow = minTime
                     }
                  }

                  if (this.nanoTime - delayed.timeNow < 0L) {
                     this.nanoTime = delayed.timeNow
                  }

                  if (true) {
                     `this_$iv`.addImpl(this)
                     val var10000: Boolean = true
                  }
               }

               return 0
            }

            var26 = 2
         }

         // $VF: monitorexit
         return var26
      }

      public override fun dispose() {
         synchronized (this) {
            val heap: Any = this._heap
            if (this._heap != EventLoop_commonKt.access$getDISPOSED_TASK$p()) {
               val var10000: EventLoopImplBase.DelayedTaskQueue = heap as? EventLoopImplBase.DelayedTaskQueue
               if ((heap as? EventLoopImplBase.DelayedTaskQueue) != null) {
                  var10000.remove(this)
               }

               this._heap = EventLoop_commonKt.access$getDISPOSED_TASK$p()
               return
            }
         }
      }

      public override fun toString(): String {
         return "Delayed[nanos=${this.nanoTime}]"
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override fun compareTo(other: Any): Int {
         this.compareTo(other as EventLoopImplBase.DelayedTask)
      }
   }

   internal class DelayedTaskQueue(timeNow: Long) : ThreadSafeHeap<EventLoopImplBase.DelayedTask> {
      @JvmField
      public final var timeNow: Long
         private set

      init {
         this.timeNow = timeNow
      }
   }
}
