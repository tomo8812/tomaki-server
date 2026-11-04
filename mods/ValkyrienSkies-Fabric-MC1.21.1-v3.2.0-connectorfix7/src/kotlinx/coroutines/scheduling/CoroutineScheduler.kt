package kotlinx.coroutines.scheduling

import java.io.Closeable
import java.util.ArrayList
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicLongFieldUpdater
import java.util.concurrent.locks.LockSupport
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlin.random.Random
import kotlinx.coroutines.AbstractTimeSource
import kotlinx.coroutines.internal.ResizableAtomicArray
import kotlinx.coroutines.internal.Symbol
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nCoroutineScheduler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineScheduler.kt\nkotlinx/coroutines/scheduling/CoroutineScheduler\n+ 2 Tasks.kt\nkotlinx/coroutines/scheduling/TasksKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 5 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n+ 6 Tasks.kt\nkotlinx/coroutines/scheduling/Task\n+ 7 CoroutineScheduler.kt\nkotlinx/coroutines/scheduling/CoroutineScheduler$Worker\n*L\n1#1,1033:1\n285#1:1036\n283#1:1037\n283#1:1038\n285#1:1039\n280#1:1045\n281#1,5:1046\n291#1:1052\n283#1:1053\n284#1:1054\n283#1:1060\n284#1:1061\n280#1:1062\n288#1:1063\n283#1:1064\n283#1:1067\n284#1:1068\n285#1:1069\n93#2:1034\n93#2:1051\n1#3:1035\n28#4,4:1040\n28#4,4:1055\n20#5:1044\n20#5:1059\n90#6:1065\n610#7:1066\n*S KotlinDebug\n*F\n+ 1 CoroutineScheduler.kt\nkotlinx/coroutines/scheduling/CoroutineScheduler\n*L\n281#1:1036\n288#1:1037\n289#1:1038\n298#1:1039\n347#1:1045\n375#1:1046,5\n398#1:1052\n445#1:1053\n446#1:1054\n482#1:1060\n483#1:1061\n489#1:1062\n498#1:1063\n498#1:1064\n576#1:1067\n577#1:1068\n578#1:1069\n119#1:1034\n395#1:1051\n347#1:1040,4\n478#1:1055,4\n347#1:1044\n478#1:1059\n515#1:1065\n522#1:1066\n*E\n"])
internal class CoroutineScheduler(corePoolSize: Int,
      maxPoolSize: Int,
      idleWorkerKeepAliveNs: Long = TasksKt.IDLE_WORKER_KEEP_ALIVE_NS,
      schedulerName: String = TasksKt.DEFAULT_SCHEDULER_NAME
   ) :
   Executor,
   Closeable {
   @JvmField
   public final val corePoolSize: Int

   @JvmField
   public final val maxPoolSize: Int

   @JvmField
   public final val idleWorkerKeepAliveNs: Long

   @JvmField
   @NotNull
   public final val schedulerName: String

   @JvmField
   @NotNull
   public final val globalCpuQueue: GlobalQueue

   @JvmField
   @NotNull
   public final val globalBlockingQueue: GlobalQueue

   @Volatile
   @Volatile
   private long parkedWorkersStack;

   @JvmField
   @NotNull
   public final val workers: ResizableAtomicArray<kotlinx.coroutines.scheduling.CoroutineScheduler.Worker>

   @Volatile
   @Volatile
   private long controlState;
   @Volatile
   @Volatile
   private int _isTerminated;
   @JvmStatic
   private AtomicLongFieldUpdater parkedWorkersStack$FU = AtomicLongFieldUpdater.newUpdater(CoroutineScheduler.class, "parkedWorkersStack");
   @JvmStatic
   private AtomicLongFieldUpdater controlState$FU = AtomicLongFieldUpdater.newUpdater(CoroutineScheduler.class, "controlState");
   @JvmStatic
   private AtomicIntegerFieldUpdater _isTerminated$FU = AtomicIntegerFieldUpdater.newUpdater(CoroutineScheduler.class, "_isTerminated");

   init {
      this.corePoolSize = corePoolSize
      this.maxPoolSize = maxPoolSize
      this.idleWorkerKeepAliveNs = idleWorkerKeepAliveNs
      this.schedulerName = schedulerName
      if (this.corePoolSize < 1) {
         throw IllegalArgumentException(("Core pool size ${this.corePoolSize} should be at least 1").toString())
      } else if (this.maxPoolSize < this.corePoolSize) {
         throw IllegalArgumentException(
            ("Max pool size ${this.maxPoolSize} should be greater than or equals to core pool size ${this.corePoolSize}").toString()
         )
      } else if (this.maxPoolSize > 2097150) {
         throw IllegalArgumentException(("Max pool size ${this.maxPoolSize} should not exceed maximal supported number of threads 2097150").toString())
      } else if (this.idleWorkerKeepAliveNs <= 0L) {
         throw IllegalArgumentException(("Idle worker keep alive time ${this.idleWorkerKeepAliveNs} must be positive").toString())
      } else {
         this.globalCpuQueue = GlobalQueue()
         this.globalBlockingQueue = GlobalQueue()
         this.workers = ResizableAtomicArray<>((this.corePoolSize + 1) * 2)
         this.controlState = (long)this.corePoolSize shl 42
         this._isTerminated = 0
      }
   }

   private fun addToGlobalQueue(task: Task): Boolean {
      return if (task.taskContext.taskMode == 1) this.globalBlockingQueue.addLast(task) else this.globalCpuQueue.addLast(task)
   }

   public fun parkedWorkersStackTopUpdate(worker: kotlinx.coroutines.scheduling.CoroutineScheduler.Worker, oldIndex: Int, newIndex: Int) {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = parkedWorkersStack$FU

      val top: Long
      val updVersion: Long
      val updIndex: Int
      do {
         top = `atomicfu$handler$iv`.get(this)
         val index: Int = (int)(top and 2097151L)
         updVersion = top + 2097152L and -2097152L
         updIndex = if (index == oldIndex) (if (newIndex == 0) this.parkedWorkersStackNextIndex(worker) else newIndex) else index
      } while (updIndex < 0 || !parkedWorkersStack$FU.compareAndSet(this, top, updVersion or (long)updIndex))
   }

   public fun parkedWorkersStackPush(worker: kotlinx.coroutines.scheduling.CoroutineScheduler.Worker): Boolean {
      if (worker.nextParkedWorker != NOT_IN_STACK) {
         return false
      } else {
         val `atomicfu$handler$iv`: AtomicLongFieldUpdater = parkedWorkersStack$FU

         val top: Long
         val updVersion: Long
         val updIndex: Int
         do {
            top = `atomicfu$handler$iv`.get(this)
            val index: Int = (int)(top and 2097151L)
            updVersion = top + 2097152L and -2097152L
            updIndex = worker.indexInArray
            if (ASSERTIONS_ENABLED && updIndex == 0) {
               throw AssertionError()
            }

            worker.nextParkedWorker = this.workers.get(index)
         } while (!parkedWorkersStack$FU.compareAndSet(this, top, updVersion or (long)updIndex))

         return true
      }
   }

   private fun parkedWorkersStackPop(): kotlinx.coroutines.scheduling.CoroutineScheduler.Worker? {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = parkedWorkersStack$FU

      val top: Long
      val updVersion: Long
      val updIndex: Int
      val var10000: CoroutineScheduler.Worker
      do {
         top = `atomicfu$handler$iv`.get(this)
         var10000 = this.workers.get((int)(top and 2097151L))
         if (var10000 == null) {
            return null
         }

         updVersion = top + 2097152L and -2097152L
         updIndex = this.parkedWorkersStackNextIndex(var10000)
      } while (updIndex < 0 || !parkedWorkersStack$FU.compareAndSet(this, top, updVersion or (long)updIndex))

      var10000.nextParkedWorker = NOT_IN_STACK
      return var10000
   }

   private fun parkedWorkersStackNextIndex(worker: kotlinx.coroutines.scheduling.CoroutineScheduler.Worker): Int {
      var next: Any = worker.nextParkedWorker

      while (next != NOT_IN_STACK) {
         if (next == null) {
            return 0
         }

         val nextWorker: CoroutineScheduler.Worker = next as CoroutineScheduler.Worker
         val updIndex: Int = (next as CoroutineScheduler.Worker).indexInArray
         if (updIndex != 0) {
            return updIndex
         }

         next = nextWorker.nextParkedWorker
      }

      return -1
   }

   private final val createdWorkers: Int
      private final inline get() {
         return (int)(access$getControlState$FU$p().get(this) and 2097151L)
      }


   private final val availableCpuPermits: Int
      private final inline get() {
         return (int)((controlState$FU.get(this) and 9223367638808264704L) shr 42)
      }


   private inline fun createdWorkers(state: Long): Int {
      return (int)(state and 2097151L)
   }

   private inline fun blockingTasks(state: Long): Int {
      return (int)((state and 4398044413952L) shr 21)
   }

   public inline fun availableCpuPermits(state: Long): Int {
      return (int)((state and 9223367638808264704L) shr 42)
   }

   private inline fun incrementCreatedWorkers(): Int {
      return (int)(controlState$FU.incrementAndGet(this) and 2097151L)
   }

   private inline fun decrementCreatedWorkers(): Int {
      return (int)(access$getControlState$FU$p().getAndDecrement(this) and 2097151L)
   }

   private inline fun incrementBlockingTasks(): Long {
      return controlState$FU.addAndGet(this, 2097152L)
   }

   private inline fun decrementBlockingTasks() {
      access$getControlState$FU$p().addAndGet(this, -2097152L)
   }

   private inline fun tryAcquireCpuPermit(): Boolean {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = access$getControlState$FU$p()

      val state: Long
      do {
         state = `atomicfu$handler$iv`.get(this)
         if ((int)((state and 9223367638808264704L) shr 42) == 0) {
            return false
         }
      } while (!access$getControlState$FU$p().compareAndSet(this, state, state - 4398046511104L))

      return true
   }

   private inline fun releaseCpuPermit(): Long {
      return access$getControlState$FU$p().addAndGet(this, 4398046511104L)
   }

   public final val isTerminated: Boolean
      public final get() {
         return _isTerminated$FU.get(this) != 0
      }


   public override fun execute(command: Runnable) {
      dispatch$default(this, command, null, false, 6, null)
   }

   public override fun close() {
      this.shutdown(10000L)
   }

   public fun shutdown(timeout: Long) {
      if (_isTerminated$FU.compareAndSet(this, 0, 1)) {
         val currentWorker: CoroutineScheduler.Worker = this.currentWorker()
         val var10000: Int
         synchronized (this.workers) {
            var10000 = (int)(access$getControlState$FU$p().get(this) and 2097151L)
         }

         val created: Int = var10000
         var var13: Int = 1
         if (1 <= var10000) {
            while (true) {
               val var24: Any = this.workers.get(var13)
               val var16: CoroutineScheduler.Worker = var24 as CoroutineScheduler.Worker
               if (var24 as CoroutineScheduler.Worker != currentWorker) {
                  while (var16.isAlive()) {
                     LockSupport.unpark(var16)
                     var16.join(timeout)
                  }

                  val var18: CoroutineScheduler.WorkerState = var16.state
                  if (ASSERTIONS_ENABLED && var18 != CoroutineScheduler.WorkerState.TERMINATED) {
                     throw AssertionError()
                  }

                  var16.localQueue.offloadAllWorkTo(this.globalBlockingQueue)
               }

               if (var13 == created) {
                  break
               }

               var13++
            }
         }

         this.globalBlockingQueue.close()
         this.globalCpuQueue.close()

         while (true) {
            run label111@{
               if (currentWorker != null) {
                  var25 = currentWorker.findTask(true)
                  if (var25 != null) {
                     return@label111
                  }
               }

               var25 = this.globalCpuQueue.removeFirstOrNull()
               if (var25 == null) {
                  var25 = this.globalBlockingQueue.removeFirstOrNull()
                  if (var25 == null) {
                     if (currentWorker != null) {
                        currentWorker.tryReleaseCpu(CoroutineScheduler.WorkerState.TERMINATED)
                     }

                     if (ASSERTIONS_ENABLED && (int)((controlState$FU.get(this) and 9223367638808264704L) shr 42) != this.corePoolSize) {
                        throw AssertionError()
                     }

                     parkedWorkersStack$FU.set(this, 0L)
                     controlState$FU.set(this, 0L)
                     return
                  }
               }
            }

            this.runSafely(var25)
         }
      }
   }

   public fun dispatch(block: Runnable, taskContext: TaskContext = TasksKt.NonBlockingContext, tailDispatch: Boolean = false) {
      val var10000: AbstractTimeSource = timeSource
      if (var10000 != null) {
         var10000.trackTask()
      }

      val task: Task = this.createTask(block, taskContext)
      val isBlockingTask: Boolean = task.taskContext.taskMode == 1
      val var14: Long = if (isBlockingTask) controlState$FU.addAndGet(this, 2097152L) else 0L
      val var12: CoroutineScheduler.Worker = this.currentWorker()
      val var13: Task = this.submitToLocalQueue(var12, task, tailDispatch)
      if (var13 != null && !this.addToGlobalQueue(var13)) {
         throw RejectedExecutionException("${this.schedulerName} was terminated")
      } else {
         val skipUnpark: Boolean = tailDispatch && var12 != null
         if (isBlockingTask) {
            this.signalBlockingWork(var14, skipUnpark)
         } else {
            if (skipUnpark) {
               return
            }

            this.signalCpuWork()
         }
      }
   }

   public fun createTask(block: Runnable, taskContext: TaskContext): Task {
      val nanoTime: Long = TasksKt.schedulerTimeSource.nanoTime()
      if (block is Task) {
         (block as Task).submissionTime = nanoTime
         (block as Task).taskContext = taskContext
         return block as Task
      } else {
         return TaskImpl(block, nanoTime, taskContext)
      }
   }

   private fun signalBlockingWork(stateSnapshot: Long, skipUnpark: Boolean) {
      if (!skipUnpark) {
         if (!this.tryUnpark()) {
            if (!this.tryCreateWorker(stateSnapshot)) {
               this.tryUnpark()
            }
         }
      }
   }

   public fun signalCpuWork() {
      if (!this.tryUnpark()) {
         if (!tryCreateWorker$default(this, 0L, 1, null)) {
            this.tryUnpark()
         }
      }
   }

   private fun tryCreateWorker(state: Long = controlState$FU.get(this)): Boolean {
      if (RangesKt.coerceAtLeast((int)(state and 2097151L) - (int)((state and 4398044413952L) shr 21), 0) < this.corePoolSize) {
         val var8: Int = this.createNewWorker()
         if (var8 == 1 && this.corePoolSize > 1) {
            this.createNewWorker()
         }

         if (var8 > 0) {
            return true
         }
      }

      return false
   }

   private fun tryUnpark(): Boolean {
      val var10000: CoroutineScheduler.Worker
      do {
         var10000 = this.parkedWorkersStackPop()
         if (var10000 == null) {
            return false
         }
      } while (!CoroutineScheduler.Worker.getWorkerCtl$FU().compareAndSet(var10000, -1, 0))

      LockSupport.unpark(var10000)
      return true
   }

   private fun createNewWorker(): Int {
      var var5: Any
      var var22: Byte
      run label83@{
         var var21: Byte
         run label82@{
            var5 = this.workers
            val var19: Int
            val var24: CoroutineScheduler.Worker
            synchronized (this.workers) {
               if (this.isTerminated) {
                  var22 = -1
                  return@label83
               }

               val state: Long = controlState$FU.get(this)
               val created: Int = (int)(state and 2097151L)
               val var27: Int = RangesKt.coerceAtLeast((int)(state and 2097151L) - (int)((state and 4398044413952L) shr 21), 0)
               if (var27 >= this.corePoolSize) {
                  var21 = 0
                  return@label82
               }

               if (created >= this.maxPoolSize) {
                  return 0
               }

               val var28: Int = (int)(access$getControlState$FU$p().get(this) and 2097151L) + 1
               if (var28 <= 0 || this.workers.get(var28) != null) {
                  throw IllegalArgumentException("Failed requirement.".toString())
               }

               var24 = CoroutineScheduler.Worker(var28)
               this.workers.setSynchronized(var28, var24)
               if (var28 != (int)(controlState$FU.incrementAndGet(this) and 2097151L)) {
                  throw IllegalArgumentException("Failed requirement.".toString())
               }

               var19 = var27 + 1
            }

            var24.start()
            return var19
         }

         // $VF: monitorexit
         return var21
      }

      // $VF: monitorexit
      return var22
   }

   private fun kotlinx.coroutines.scheduling.CoroutineScheduler.Worker?.submitToLocalQueue(task: Task, tailDispatch: Boolean): Task? {
      if (`$this$submitToLocalQueue` == null) {
         return task
      } else if (`$this$submitToLocalQueue`.state === CoroutineScheduler.WorkerState.TERMINATED) {
         return task
      } else if (task.taskContext.taskMode == 0 && `$this$submitToLocalQueue`.state === CoroutineScheduler.WorkerState.BLOCKING) {
         return task
      } else {
         `$this$submitToLocalQueue`.mayHaveLocalTasks = true
         return `$this$submitToLocalQueue`.localQueue.add(task, tailDispatch)
      }
   }

   private fun currentWorker(): kotlinx.coroutines.scheduling.CoroutineScheduler.Worker? {
      val var1: Thread = Thread.currentThread()
      return if ((var1 as? CoroutineScheduler.Worker) != null)
         (if (var8/* $VF was: CoroutineScheduler.this */ == this) (var1 as? CoroutineScheduler.Worker) else null)
         else
         null
      }

   public override fun toString(): String {
      var parkedWorkers: Int = 0
      var blockingWorkers: Int = 0
      var cpuWorkers: Int = 0
      var dormant: Int = 0
      var terminated: Int = 0
      val queueSizes: ArrayList = ArrayList()
      var state: Int = 1

      for (var8 in this.workers.currentLength()..state) {
         val var10000: CoroutineScheduler.Worker = this.workers.get(state)
         if (var10000 != null) {
            val `this_$iv`: Int = var10000.localQueue.size
            when (CoroutineScheduler.WhenMappings.$EnumSwitchMapping$0[var10000.state.ordinal()]) {
               1 -> {
                  parkedWorkers++
                  continue
               }
               2 -> {
                  blockingWorkers++
                  queueSizes.add("$`this_$iv`b")
                  continue
               }
               3 -> {
                  cpuWorkers++
                  queueSizes.add("$`this_$iv`c")
                  continue
               }
               4 -> {
                  dormant++
                  if (`this_$iv` > 0) {
                     queueSizes.add("$`this_$iv`d")
                  }
                  continue
               }
               5 -> terminated++
               else -> {}
            }
         }
      }

      val var12: Long = controlState$FU.get(this)
      val var13: StringBuilder = StringBuilder()
      var13.append(this.schedulerName)
         .append('@')
         .append(hexAddress)
         .append("[Pool Size {core = ")
         .append(this.corePoolSize)
         .append(", max = ")
         .append(this.maxPoolSize)
         .append("}, Worker States {CPU = ")
         .append(cpuWorkers)
         .append(", blocking = ")
         .append(blockingWorkers)
         .append(", parked = ")
         .append(parkedWorkers)
         .append(", dormant = ")
         .append(dormant)
         .append(", terminated = ")
         .append(terminated)
         .append("}, running workers queues = ")
         .append(queueSizes)
         .append(", global CPU queue size = ")
         .append(this.globalCpuQueue.getSize())
         .append(", global blocking queue size = ")
         .append(this.globalBlockingQueue.getSize())
         var13.append(", Control State {created workers= ")
         .append((int)(var12 and 2097151L))
         .append(", blocking tasks = ")
         .append((int)((var12 and 4398044413952L) shr 21))
         .append(", CPUs acquired = ")
         .append(this.corePoolSize - (int)((var12 and 9223367638808264704L) shr 42))
         .append("}]")
         return var13.toString()
   }

   public fun runSafely(task: Task) {
      try {
         task.run()
      } catch (var6: java.lang.Throwable) {
         val thread: Thread = Thread.currentThread()
         thread.getUncaughtExceptionHandler().uncaughtException(thread, var6)
      } finally {
         val var10000: AbstractTimeSource = timeSource
         if (var10000 != null) {
            var10000.unTrackTask()
         }
      }
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicLongFieldUpdater, `atomicfu$action`: (java.lang.Long?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   public companion object {
      private const val BLOCKING_MASK: Long = 4398044413952L
      private const val BLOCKING_SHIFT: Int = 21
      private const val CLAIMED: Int = 0
      private const val CPU_PERMITS_MASK: Long = 9223367638808264704L
      private const val CPU_PERMITS_SHIFT: Int = 42
      private const val CREATED_MASK: Long = 2097151L
      internal const val MAX_SUPPORTED_POOL_SIZE: Int = 2097150
      internal const val MIN_SUPPORTED_POOL_SIZE: Int = 1

      @JvmField
      @NotNull
      public final val NOT_IN_STACK: Symbol

      private const val PARKED: Int = -1
      private const val PARKED_INDEX_MASK: Long = 2097151L
      private const val PARKED_VERSION_INC: Long = 2097152L
      private const val PARKED_VERSION_MASK: Long = -2097152L
      private const val TERMINATED: Int = 1
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class WhenMappings {
      @JvmStatic
      @JvmSynthetic
      public int[] $EnumSwitchMapping$0;

      @JvmStatic
      fun {
         val var0: IntArray = IntArray(CoroutineScheduler.WorkerState.values().length)

         try {
            var0[CoroutineScheduler.WorkerState.PARKING.ordinal()] = 1
         } catch (var6: NoSuchFieldError) {
         }

         try {
            var0[CoroutineScheduler.WorkerState.BLOCKING.ordinal()] = 2
         } catch (var5: NoSuchFieldError) {
         }

         try {
            var0[CoroutineScheduler.WorkerState.CPU_ACQUIRED.ordinal()] = 3
         } catch (var4: NoSuchFieldError) {
         }

         try {
            var0[CoroutineScheduler.WorkerState.DORMANT.ordinal()] = 4
         } catch (var3: NoSuchFieldError) {
         }

         try {
            var0[CoroutineScheduler.WorkerState.TERMINATED.ordinal()] = 5
         } catch (var2: NoSuchFieldError) {
         }

         $EnumSwitchMapping$0 = var0
      }
   }

   @SourceDebugExtension(["SMAP\nCoroutineScheduler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineScheduler.kt\nkotlinx/coroutines/scheduling/CoroutineScheduler$Worker\n+ 2 CoroutineScheduler.kt\nkotlinx/coroutines/scheduling/CoroutineScheduler\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Tasks.kt\nkotlinx/coroutines/scheduling/Task\n+ 5 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 6 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n*L\n1#1,1033:1\n298#2:1034\n285#2:1035\n299#2,4:1036\n304#2:1040\n294#2,2:1041\n294#2,2:1045\n280#2:1052\n289#2:1053\n283#2:1054\n280#2:1055\n1#3:1043\n90#4:1044\n28#5,4:1047\n20#6:1051\n*S KotlinDebug\n*F\n+ 1 CoroutineScheduler.kt\nkotlinx/coroutines/scheduling/CoroutineScheduler$Worker\n*L\n665#1:1034\n665#1:1035\n665#1:1036,4\n679#1:1040\n753#1:1041,2\n807#1:1045,2\n855#1:1052\n881#1:1053\n881#1:1054\n963#1:1055\n790#1:1044\n851#1:1047,4\n851#1:1051\n*E\n"])
   internal inner class Worker private constructor() : Thread {
      public final var indexInArray: Int
         public final set(index) {
            this.setName("${CoroutineScheduler.this.schedulerName}-worker-${if (index == 0) "TERMINATED" else java.lang.String.valueOf(index)}")
            this.indexInArray = index
         }


      @JvmField
      @NotNull
      public final val localQueue: WorkQueue

      private final val stolenTask: ObjectRef<Task?>

      @JvmField
      @NotNull
      public final var state: kotlinx.coroutines.scheduling.CoroutineScheduler.WorkerState
         private set

      @Volatile
      @Volatile
      private int workerCtl;
      private final var terminationDeadline: Long
      public final var nextParkedWorker: Any?
      private final var minDelayUntilStealableTaskNs: Long
      private final var rngState: Int

      @JvmField
      public final var mayHaveLocalTasks: Boolean
         private set

      @JvmStatic
      private AtomicIntegerFieldUpdater workerCtl$FU = AtomicIntegerFieldUpdater.newUpdater(CoroutineScheduler.Worker.class, "workerCtl");

      init {
         this.setDaemon(true)
         this.localQueue = WorkQueue()
         this.stolenTask = ObjectRef()
         this.state = CoroutineScheduler.WorkerState.DORMANT
         this.nextParkedWorker = CoroutineScheduler.NOT_IN_STACK
         this.rngState = Random.Default.nextInt()
      }

      public constructor(index: Int) : this() {
         this.indexInArray = index
      }

      public final val scheduler: CoroutineScheduler
         public final inline get() {
            return access$getThis$0$p(this)
         }


      fun getWorkerCtl(): Int {
         this.workerCtl
      }

      private fun tryAcquireCpuPermit(): Boolean {
         var var10000: Boolean
         if (this.state === CoroutineScheduler.WorkerState.CPU_ACQUIRED) {
            var10000 = true
         } else {
            val `this_$iv`: CoroutineScheduler = CoroutineScheduler.this
            val `atomicfu$handler$iv$iv`: AtomicLongFieldUpdater = CoroutineScheduler.controlState$FU

            while (true) {
               val `state$iv`: Long = `atomicfu$handler$iv$iv`.get(`this_$iv`)
               if ((int)((`state$iv` and 9223367638808264704L) shr 42) == 0) {
                  var10000 = false
                  break
               }

               if (CoroutineScheduler.controlState$FU.compareAndSet(`this_$iv`, `state$iv`, `state$iv` - 4398046511104L)) {
                  var10000 = true
                  break
               }
            }

            if (var10000) {
               this.state = CoroutineScheduler.WorkerState.CPU_ACQUIRED
               var10000 = true
            } else {
               var10000 = false
            }
         }

         return var10000
      }

      public fun tryReleaseCpu(newState: kotlinx.coroutines.scheduling.CoroutineScheduler.WorkerState): Boolean {
         val hadCpu: Boolean = this.state === CoroutineScheduler.WorkerState.CPU_ACQUIRED
         if (this.state === CoroutineScheduler.WorkerState.CPU_ACQUIRED) {
            val `this_$iv`: CoroutineScheduler = CoroutineScheduler.this
            CoroutineScheduler.controlState$FU.addAndGet(`this_$iv`, 4398046511104L)
         }

         if (this.state != newState) {
            this.state = newState
         }

         return hadCpu
      }

      public override fun run() {
         this.runWorker()
      }

      private fun runWorker() {
         var rescanned: Boolean = false

         while (!CoroutineScheduler.this.isTerminated && this.state != CoroutineScheduler.WorkerState.TERMINATED) {
            val task: Task = this.findTask(this.mayHaveLocalTasks)
            if (task != null) {
               rescanned = false
               this.minDelayUntilStealableTaskNs = 0L
               this.executeTask(task)
            } else {
               this.mayHaveLocalTasks = false
               if (this.minDelayUntilStealableTaskNs != 0L) {
                  if (!rescanned) {
                     rescanned = true
                  } else {
                     rescanned = false
                     this.tryReleaseCpu(CoroutineScheduler.WorkerState.PARKING)
                     Thread.interrupted()
                     LockSupport.parkNanos(this.minDelayUntilStealableTaskNs)
                     this.minDelayUntilStealableTaskNs = 0L
                  }
               } else {
                  this.tryPark()
               }
            }
         }

         this.tryReleaseCpu(CoroutineScheduler.WorkerState.TERMINATED)
      }

      public fun runSingleTask(): Long {
         val stateSnapshot: CoroutineScheduler.WorkerState = this.state
         val isCpuThread: Boolean = this.state === CoroutineScheduler.WorkerState.CPU_ACQUIRED
         val task: Task = if (this.state === CoroutineScheduler.WorkerState.CPU_ACQUIRED) this.findCpuTask() else this.findBlockingTask()
         if (task == null) {
            return if (this.minDelayUntilStealableTaskNs == 0L) -1L else this.minDelayUntilStealableTaskNs
         } else {
            CoroutineScheduler.this.runSafely(task)
            if (!isCpuThread) {
               val var4: CoroutineScheduler = CoroutineScheduler.this
               CoroutineScheduler.controlState$FU.addAndGet(var4, -2097152L)
            }

            if (ASSERTIONS_ENABLED && this.state != stateSnapshot) {
               throw AssertionError()
            } else {
               return 0L
            }
         }
      }

      public fun isIo(): Boolean {
         return this.state === CoroutineScheduler.WorkerState.BLOCKING
      }

      private fun tryPark() {
         if (!this.inStack()) {
            CoroutineScheduler.this.parkedWorkersStackPush(this)
         } else {
            workerCtl$FU.set(this, -1)

            while (
               this.inStack()
                  && workerCtl$FU.get(this) == -1
                  && !CoroutineScheduler.this.isTerminated
                  && this.state != CoroutineScheduler.WorkerState.TERMINATED
            ) {
               this.tryReleaseCpu(CoroutineScheduler.WorkerState.PARKING)
               Thread.interrupted()
               this.park()
            }
         }
      }

      private fun inStack(): Boolean {
         return this.nextParkedWorker != CoroutineScheduler.NOT_IN_STACK
      }

      private fun executeTask(task: Task) {
         val taskMode: Int = task.taskContext.taskMode
         this.idleReset(taskMode)
         this.beforeTask(taskMode)
         CoroutineScheduler.this.runSafely(task)
         this.afterTask(taskMode)
      }

      private fun beforeTask(taskMode: Int) {
         if (taskMode != 0) {
            if (this.tryReleaseCpu(CoroutineScheduler.WorkerState.BLOCKING)) {
               CoroutineScheduler.this.signalCpuWork()
            }
         }
      }

      private fun afterTask(taskMode: Int) {
         if (taskMode != 0) {
            val currentState: CoroutineScheduler = CoroutineScheduler.this
            CoroutineScheduler.controlState$FU.addAndGet(currentState, -2097152L)
            val var4: CoroutineScheduler.WorkerState = this.state
            if (this.state != CoroutineScheduler.WorkerState.TERMINATED) {
               if (ASSERTIONS_ENABLED && var4 != CoroutineScheduler.WorkerState.BLOCKING) {
                  throw AssertionError()
               }

               this.state = CoroutineScheduler.WorkerState.DORMANT
            }
         }
      }

      public fun nextInt(upperBound: Int): Int {
         val var6: Int = this.rngState xor this.rngState shl 13 xor (this.rngState xor this.rngState shl 13) shr 17 xor (
            this.rngState xor this.rngState shl 13 xor (this.rngState xor this.rngState shl 13) shr 17
         ) shl 5
         this.rngState = this.rngState xor this.rngState shl 13 xor (this.rngState xor this.rngState shl 13) shr 17 xor (
            this.rngState xor this.rngState shl 13 xor (this.rngState xor this.rngState shl 13) shr 17
         ) shl 5
         return if ((upperBound - 1 and upperBound) == 0) var6 and upperBound - 1 else (var6 and Integer.MAX_VALUE) % upperBound
      }

      private fun park() {
         if (this.terminationDeadline == 0L) {
            this.terminationDeadline = System.nanoTime() + CoroutineScheduler.this.idleWorkerKeepAliveNs
         }

         LockSupport.parkNanos(CoroutineScheduler.this.idleWorkerKeepAliveNs)
         if (System.nanoTime() - this.terminationDeadline >= 0L) {
            this.terminationDeadline = 0L
            this.tryTerminateWorker()
         }
      }

      private fun tryTerminateWorker() {
         run label83@{
            val var2: CoroutineScheduler = CoroutineScheduler.this
            synchronized (CoroutineScheduler.this.workers) {
               if (var2.isTerminated) {
                  return
               }

               if ((int)(CoroutineScheduler.controlState$FU.get(var2) and 2097151L) <= var2.corePoolSize) {
                  return
               }

               if (workerCtl$FU.compareAndSet(this, -1, 1)) {
                  val var18: Int = this.indexInArray
                  this.indexInArray = 0
                  var2.parkedWorkersStackTopUpdate(this, var18, 0)
                  val var19: Int = (int)(CoroutineScheduler.controlState$FU.getAndDecrement(var2) and 2097151L)
                  if (var19 != var18) {
                     val var10000: Any = var2.workers.get(var19)
                     val var20: CoroutineScheduler.Worker = var10000 as CoroutineScheduler.Worker
                     var2.workers.setSynchronized(var18, var10000 as CoroutineScheduler.Worker)
                     var20.indexInArray = var18
                     var2.parkedWorkersStackTopUpdate(var20, var19, var18)
                  }

                  var2.workers.setSynchronized(var19, null)
                  return@label83
               }
            }

            return
         }

         this.state = CoroutineScheduler.WorkerState.TERMINATED
      }

      private fun idleReset(mode: Int) {
         this.terminationDeadline = 0L
         if (this.state === CoroutineScheduler.WorkerState.PARKING) {
            if (ASSERTIONS_ENABLED && mode != 1) {
               throw AssertionError()
            }

            this.state = CoroutineScheduler.WorkerState.BLOCKING
         }
      }

      public fun findTask(mayHaveLocalTasks: Boolean): Task? {
         return if (this.tryAcquireCpuPermit()) this.findAnyTask(mayHaveLocalTasks) else this.findBlockingTask()
      }

      private fun findBlockingTask(): Task? {
         var var10000: Task = this.localQueue.pollBlocking()
         if (var10000 == null) {
            var10000 = CoroutineScheduler.this.globalBlockingQueue.removeFirstOrNull()
            if (var10000 == null) {
               var10000 = this.trySteal(1)
            }
         }

         return var10000
      }

      private fun findCpuTask(): Task? {
         var var10000: Task = this.localQueue.pollCpu()
         if (var10000 == null) {
            var10000 = CoroutineScheduler.this.globalBlockingQueue.removeFirstOrNull()
            if (var10000 == null) {
               var10000 = this.trySteal(2)
            }
         }

         return var10000
      }

      private fun findAnyTask(scanLocalQueue: Boolean): Task? {
         if (scanLocalQueue) {
            val globalFirst: Boolean = this.nextInt(2 * CoroutineScheduler.this.corePoolSize) == 0
            if (globalFirst) {
               val var3: Task = this.pollGlobalQueues()
               if (var3 != null) {
                  return var3
               }
            }

            var var8: Task = this.localQueue.poll()
            if (var8 != null) {
               return var8
            }

            if (!globalFirst) {
               var8 = this.pollGlobalQueues()
               if (var8 != null) {
                  return var8
               }
            }
         } else {
            val var7: Task = this.pollGlobalQueues()
            if (var7 != null) {
               return var7
            }
         }

         return this.trySteal(3)
      }

      private fun pollGlobalQueues(): Task? {
         label21@
         if (this.nextInt(2) == 0) {
            val var4: Task = CoroutineScheduler.this.globalCpuQueue.removeFirstOrNull()
            return var4 ?: CoroutineScheduler.this.globalBlockingQueue.removeFirstOrNull()
         } else {
            val var1: Task = CoroutineScheduler.this.globalBlockingQueue.removeFirstOrNull()
            return var1 ?: CoroutineScheduler.this.globalCpuQueue.removeFirstOrNull()
         }
      }

      private fun trySteal(stealingMode: Int): Task? {
         val currentIndex: CoroutineScheduler = CoroutineScheduler.this
         val created: Int = (int)(CoroutineScheduler.controlState$FU.get(currentIndex) and 2097151L)
         if (created < 2) {
            return null
         } else {
            var var16: Int = this.nextInt(created)
            var minDelay: Long = 0L
            minDelay = java.lang.Long.MAX_VALUE
            val var5: CoroutineScheduler = CoroutineScheduler.this

            repeat(created) { var6 ->
               if (++var16 > created) {
                  var16 = 1
               }

               val worker: CoroutineScheduler.Worker = var5.workers.get(var16)
               if (worker != null && worker != this) {
                  val stealResult: Long = worker.localQueue.trySteal(stealingMode, this.stolenTask)
                  if (stealResult == -1L) {
                     val result: Task = this.stolenTask.element as Task
                     this.stolenTask.element = null
                     return result
                  }

                  if (stealResult > 0L) {
                     minDelay = Math.min(minDelay, stealResult)
                  }
               }
            }

            this.minDelayUntilStealableTaskNs = if (minDelay != java.lang.Long.MAX_VALUE) minDelay else 0L
            return null
         }
      }

      @JvmStatic
      fun `getWorkerCtl$FU`(): AtomicIntegerFieldUpdater {
         workerCtl$FU
      }
   }

   public enum class WorkerState {
      CPU_ACQUIRED,
      BLOCKING,
      PARKING,
      DORMANT,
      TERMINATED;

      @JvmStatic
      @JvmSynthetic
      fun `$values`(): Array<CoroutineScheduler.WorkerState> {
         arrayOf(CPU_ACQUIRED, BLOCKING, PARKING, DORMANT, TERMINATED)
      }
   }
}
