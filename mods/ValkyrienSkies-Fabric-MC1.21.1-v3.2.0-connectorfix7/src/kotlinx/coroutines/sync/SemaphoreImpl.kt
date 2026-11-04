package kotlinx.coroutines.sync

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicLongFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KFunction
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.CancellableContinuationKt
import kotlinx.coroutines.Waiter
import kotlinx.coroutines.internal.ConcurrentLinkedListKt
import kotlinx.coroutines.internal.Segment
import kotlinx.coroutines.selects.SelectInstance

@SourceDebugExtension(["SMAP\nSemaphore.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Semaphore.kt\nkotlinx/coroutines/sync/SemaphoreImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n+ 4 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/ConcurrentLinkedListKt\n+ 5 Semaphore.kt\nkotlinx/coroutines/sync/SemaphoreSegment\n*L\n1#1,397:1\n205#1,10:411\n205#1,10:421\n1#2:398\n332#3,12:399\n72#4,3:431\n46#4,8:434\n72#4,3:445\n46#4,8:448\n375#5:442\n375#5:443\n367#5:444\n378#5:456\n367#5:457\n375#5:458\n*S KotlinDebug\n*F\n+ 1 Semaphore.kt\nkotlinx/coroutines/sync/SemaphoreImpl\n*L\n197#1:411,10\n221#1:421,10\n187#1:399,12\n289#1:431,3\n289#1:434,8\n322#1:445,3\n322#1:448,8\n293#1:442\n299#1:443\n313#1:444\n328#1:456\n334#1:457\n337#1:458\n*E\n"])
internal open class SemaphoreImpl(permits: Int, acquiredPermits: Int) : Semaphore {
   private final val permits: Int
   @Volatile
   @Volatile
   private Object head;
   @Volatile
   @Volatile
   private long deqIdx;
   @Volatile
   @Volatile
   private Object tail;
   @Volatile
   @Volatile
   private long enqIdx;
   @Volatile
   @Volatile
   private int _availablePermits;
   private final val onCancellationRelease: (Throwable) -> Unit
   @JvmStatic
   private AtomicReferenceFieldUpdater head$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "head");
   @JvmStatic
   private AtomicLongFieldUpdater deqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "deqIdx");
   @JvmStatic
   private AtomicReferenceFieldUpdater tail$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "tail");
   @JvmStatic
   private AtomicLongFieldUpdater enqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "enqIdx");
   @JvmStatic
   private AtomicIntegerFieldUpdater _availablePermits$FU = AtomicIntegerFieldUpdater.newUpdater(SemaphoreImpl.class, "_availablePermits");

   init {
      this.permits = permits
      if (this.permits <= 0) {
         throw IllegalArgumentException(("Semaphore should have at least 1 permit, but had ${this.permits}").toString())
      } else if (0 > acquiredPermits || acquiredPermits > this.permits) {
         throw IllegalArgumentException(("The number of acquired permits should be in 0..${this.permits}").toString())
      } else {
         val s: SemaphoreSegment = SemaphoreSegment(0L, null, 2)
         this.head = s
         this.tail = s
         this._availablePermits = this.permits - acquiredPermits
         this.onCancellationRelease = { <anonymous parameter 0>: Throwable ->
            SemaphoreImpl.this.release()
         } as (java.lang.Throwable?) -> Unit
      }
   }

   public open val availablePermits: Int
      public open get() {
         return Math.max(_availablePermits$FU.get(this), 0)
      }


   public override fun tryAcquire(): Boolean {
      while (true) {
         val p: Int = _availablePermits$FU.get(this)
         if (p > this.permits) {
            this.coerceAvailablePermitsAtMaximum()
         } else {
            if (p <= 0) {
               return false
            }

            if (_availablePermits$FU.compareAndSet(this, p, p - 1)) {
               return true
            }
         }
      }
   }

   public override suspend fun acquire() {
      return acquire$suspendImpl(this, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `acquire$suspendImpl`(`$this`: SemaphoreImpl, `$completion`: Continuation<in Unit>) {
      if (`$this`.decPermits() > 0) {
         Unit.INSTANCE
      } else {
         val var10000: Any = `$this`.acquireSlowPath(`$completion`)
         if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }
   }

   private suspend fun acquireSlowPath() {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(`$completion`))

      try {
         if (!access$addAcquireToQueue(this, `cancellable$iv`)) {
            this.acquire(`cancellable$iv`)
         }
      } catch (var8: java.lang.Throwable) {
         `cancellable$iv`.releaseClaimedReusableContinuation$kotlinx_coroutines_core()
         throw var8
      }

      val var10000: Any = `cancellable$iv`.getResult()
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   protected fun acquire(waiter: CancellableContinuation<Unit>) {
      val `this_$iv`: SemaphoreImpl = this

      do {
         if (`this_$iv`.decPermits() > 0) {
            waiter.resume(Unit.INSTANCE, this.onCancellationRelease)
            break
         }
      } while (!this.addAcquireToQueue(waiter as Waiter))
   }

   private inline fun <W> acquire(waiter: Any, suspend: (Any) -> Boolean, onAcquired: (Any) -> Unit) {
      do {
         if (this.decPermits() > 0) {
            onAcquired(waiter)
            return
         }
      } while (!suspend(waiter))
   }

   protected fun onAcquireRegFunction(select: SelectInstance<*>, ignoredParam: Any?) {
      val `this_$iv`: SemaphoreImpl = this

      do {
         if (`this_$iv`.decPermits() > 0) {
            select.selectInRegistrationPhase(Unit.INSTANCE)
            break
         }
      } while (!this.addAcquireToQueue(select as Waiter))
   }

   private fun decPermits(): Int {
      val p: Int
      do {
         p = _availablePermits$FU.getAndDecrement(this)
      } while (p > this.permits)

      return p
   }

   public override fun release() {
      do {
         val p: Int = _availablePermits$FU.getAndIncrement(this)
         if (p >= this.permits) {
            this.coerceAvailablePermitsAtMaximum()
            throw IllegalStateException(("The number of released permits cannot be greater than ${this.permits}").toString())
         }

         if (p >= 0) {
            return
         }
      } while (!this.tryResumeNextFromQueue())
   }

   private fun coerceAvailablePermitsAtMaximum() {
      val cur: Int
      do {
         cur = _availablePermits$FU.get(this)
      } while (cur > this.permits && !_availablePermits$FU.compareAndSet(this, cur, this.permits))
   }

   private fun addAcquireToQueue(waiter: Waiter): Boolean {
      val curTail: SemaphoreSegment = tail$FU.get(this) as SemaphoreSegment
      val enqIdx: Long = enqIdx$FU.getAndIncrement(this)
      val createNewSegment: KFunction = <unrepresentable>.INSTANCE as KFunction
      val i: AtomicReferenceFieldUpdater = tail$FU
      val var8: Long = enqIdx / SemaphoreKt.access$getSEGMENT_SIZE$p()

      val `$i$f$cas`: Any
      label85@ while (true) {
         `$i$f$cas` = ConcurrentLinkedListKt.findSegmentInternal(curTail, var8, createNewSegment as (java.lang.Long?, SemaphoreSegment?) -> SemaphoreSegment)
         if (isClosed) {
            break
         }

         val `to$iv$iv`: Segment = segment

         while (true) {
            val `cur$iv$iv`: Segment = i.get(this) as Segment
            val var10000: Boolean
            if (`cur$iv$iv`.id >= `to$iv$iv`.id) {
               var10000 = true
            } else if (!`to$iv$iv`.tryIncPointers$kotlinx_coroutines_core()) {
               var10000 = false
            } else {
               if (!i.compareAndSet(this, `cur$iv$iv`, `to$iv$iv`)) {
                  if (`to$iv$iv`.decPointers$kotlinx_coroutines_core()) {
                     `to$iv$iv`.remove()
                  }
                  continue
               }

               if (`cur$iv$iv`.decPointers$kotlinx_coroutines_core()) {
                  `cur$iv$iv`.remove()
               }

               var10000 = true
            }

            if (var10000) {
               break@label85
            }
            continue@label85
         }
      }

      val segment: SemaphoreSegment = segment as SemaphoreSegment
      val var17: Int = (int)(enqIdx % SemaphoreKt.access$getSEGMENT_SIZE$p())
      if (segment.getAcquirers().compareAndSet(var17, null, waiter)) {
         waiter.invokeOnCancellation(segment, var17)
         return true
      } else if (segment.getAcquirers().compareAndSet(var17, SemaphoreKt.access$getPERMIT$p(), SemaphoreKt.access$getTAKEN$p())) {
         if (waiter is CancellableContinuation) {
            (waiter as CancellableContinuation).resume(Unit.INSTANCE, this.onCancellationRelease)
         } else {
            if (waiter !is SelectInstance) {
               throw IllegalStateException(("unexpected: $waiter").toString())
            }

            (waiter as SelectInstance).selectInRegistrationPhase(Unit.INSTANCE)
         }

         return true
      } else if (ASSERTIONS_ENABLED && segment.getAcquirers().get(var17) != SemaphoreKt.access$getBROKEN$p()) {
         throw AssertionError()
      } else {
         return false
      }
   }

   private fun tryResumeNextFromQueue(): Boolean {
      val curHead: SemaphoreSegment = head$FU.get(this) as SemaphoreSegment
      val deqIdx: Long = deqIdx$FU.getAndIncrement(this)
      val id: Long = deqIdx / SemaphoreKt.access$getSEGMENT_SIZE$p()
      val createNewSegment: KFunction = <unrepresentable>.INSTANCE as KFunction
      val i: AtomicReferenceFieldUpdater = head$FU

      var `this_$iv`: Any
      label89@ while (true) {
         `this_$iv` = ConcurrentLinkedListKt.findSegmentInternal(curHead, id, createNewSegment as (java.lang.Long?, SemaphoreSegment?) -> SemaphoreSegment)
         if (isClosed) {
            break
         }

         val `expected$iv`: Segment = segment

         while (true) {
            val `this_$ivx`: Segment = i.get(this) as Segment
            val var10000: Boolean
            if (`this_$ivx`.id >= `expected$iv`.id) {
               var10000 = true
            } else if (!`expected$iv`.tryIncPointers$kotlinx_coroutines_core()) {
               var10000 = false
            } else {
               if (!i.compareAndSet(this, `this_$ivx`, `expected$iv`)) {
                  if (`expected$iv`.decPointers$kotlinx_coroutines_core()) {
                     `expected$iv`.remove()
                  }
                  continue
               }

               if (`this_$ivx`.decPointers$kotlinx_coroutines_core()) {
                  `this_$ivx`.remove()
               }

               var10000 = true
            }

            if (var10000) {
               break@label89
            }
            continue@label89
         }
      }

      val segment: SemaphoreSegment = segment as SemaphoreSegment
      segment.cleanPrev()
      if (segment.id > id) {
         return false
      } else {
         val var16: Int = (int)(deqIdx % SemaphoreKt.access$getSEGMENT_SIZE$p())
         val var17: Any = segment.getAcquirers().getAndSet(var16, SemaphoreKt.access$getPERMIT$p())
         if (var17 == null) {
            `this_$iv` = SemaphoreKt.access$getMAX_SPIN_CYCLES$p()

            repeat(`this_$iv`) { var22 ->
               if (segment.getAcquirers().get(var16) === SemaphoreKt.access$getTAKEN$p()) {
                  return true
               }
            }

            return !segment.getAcquirers().compareAndSet(var16, SemaphoreKt.access$getPERMIT$p(), SemaphoreKt.access$getBROKEN$p())
         } else {
            return var17 != SemaphoreKt.access$getCANCELLED$p() && this.tryResumeAcquire(var17)
         }
      }
   }

   private fun Any.tryResumeAcquire(): Boolean {
      val var10000: Boolean
      if (`$this$tryResumeAcquire` is CancellableContinuation) {
         val token: Any = (`$this$tryResumeAcquire` as CancellableContinuation).tryResume(Unit.INSTANCE, null, this.onCancellationRelease)
         if (token != null) {
            (`$this$tryResumeAcquire` as CancellableContinuation).completeResume(token)
            var10000 = true
         } else {
            var10000 = false
         }
      } else {
         if (`$this$tryResumeAcquire` !is SelectInstance) {
            throw IllegalStateException(("unexpected: $`$this$tryResumeAcquire`").toString())
         }

         var10000 = (`$this$tryResumeAcquire` as SelectInstance).trySelect(this, Unit.INSTANCE)
      }

      return var10000
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$acquireSlowPath`(`$this`: SemaphoreImpl, `$completion`: Continuation) {
      `$this`.acquireSlowPath(`$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$addAcquireToQueue`(`$this`: SemaphoreImpl, waiter: Waiter): Boolean {
      `$this`.addAcquireToQueue(waiter)
   }
}
