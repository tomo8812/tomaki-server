package kotlinx.coroutines.sync

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.CancellableContinuationKt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Waiter
import kotlinx.coroutines.internal.Segment
import kotlinx.coroutines.selects.SelectClause2
import kotlinx.coroutines.selects.SelectClause2Impl
import kotlinx.coroutines.selects.SelectInstance
import kotlinx.coroutines.selects.SelectInstanceInternal
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nMutex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Mutex.kt\nkotlinx/coroutines/sync/MutexImpl\n+ 2 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n332#2,12:311\n1#3:323\n*S KotlinDebug\n*F\n+ 1 Mutex.kt\nkotlinx/coroutines/sync/MutexImpl\n*L\n175#1:311,12\n*E\n"])
internal open class MutexImpl(locked: Boolean) : SemaphoreImpl(1, if (locked) 1 else 0), Mutex {
   @Volatile
   @Volatile
   private Object owner;
   private final val onSelectCancellationUnlockConstructor: (SelectInstance<*>, Any?, Any?) -> (Throwable) -> Unit
   @JvmStatic
   private AtomicReferenceFieldUpdater owner$FU = AtomicReferenceFieldUpdater.newUpdater(MutexImpl.class, Object.class, "owner");

   init {
      this.owner = if (locked) null else MutexKt.access$getNO_OWNER$p()
      this.onSelectCancellationUnlockConstructor = { <anonymous parameter 0>: SelectInstance<*>, owner: Any?, <anonymous parameter 2>: Any? ->
         return { it: Throwable ->
            var3/* $VF was: MutexImpl.this */.unlock(owner)
         } as (java.lang.Throwable?) -> Unit
      } as (SelectInstance<*>?, Any?, Any?) -> ((java.lang.Throwable?) -> Unit)
   }

   public open val isLocked: Boolean
      public open get() {
         return this.getAvailablePermits() == 0
      }


   public override fun holdsLock(owner: Any): Boolean {
      return this.holdsLockImpl(owner) == 1
   }

   private fun holdsLockImpl(owner: Any?): Int {
      while (this.isLocked) {
         val curOwner: Any = owner$FU.get(this)
         if (curOwner != MutexKt.access$getNO_OWNER$p()) {
            return if (curOwner === owner) 1 else 2
         }
      }

      return 0
   }

   public override suspend fun lock(owner: Any?) {
      return lock$suspendImpl(this, owner, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `lock$suspendImpl`(`$this`: MutexImpl, owner: Any, `$completion`: Continuation<in Unit>) {
      if (`$this`.tryLock(owner)) {
         Unit.INSTANCE
      } else {
         val var10000: Any = `$this`.lockSuspend(owner, `$completion`)
         if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }
   }

   private suspend fun lockSuspend(owner: Any?) {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(`$completion`))

      try {
         this.acquire(MutexImpl.CancellableContinuationWithOwner(`cancellable$iv`, owner))
      } catch (var11: java.lang.Throwable) {
         `cancellable$iv`.releaseClaimedReusableContinuation$kotlinx_coroutines_core()
         throw var11
      }

      val var10000: Any = `cancellable$iv`.getResult()
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   public override fun tryLock(owner: Any?): Boolean {
      var var10000: Boolean
      when (this.tryLockImpl(owner)) {
         0 -> var10000 = true
         1 -> var10000 = false
         2 -> throw IllegalStateException(("This mutex is already locked by the specified owner: $owner").toString())
         else -> throw IllegalStateException("unexpected".toString())
      }

      return var10000
   }

   private fun tryLockImpl(owner: Any?): Int {
      while (!this.tryAcquire()) {
         if (owner == null) {
            return 1
         }

         when (this.holdsLockImpl(owner)) {
            0 -> continue
            1 -> return 2
            2 -> return 1
            else -> continue
         }
      }

      if (ASSERTIONS_ENABLED && owner$FU.get(this) != MutexKt.access$getNO_OWNER$p()) {
         throw AssertionError()
      } else {
         owner$FU.set(this, owner)
         return 0
      }
   }

   public override fun unlock(owner: Any?) {
      while (this.isLocked) {
         val curOwner: Any = owner$FU.get(this)
         if (curOwner != MutexKt.access$getNO_OWNER$p()) {
            if (curOwner != owner && owner != null) {
               throw IllegalStateException(("This mutex is locked by $curOwner, but $owner is expected").toString())
            }

            if (owner$FU.compareAndSet(this, curOwner, MutexKt.access$getNO_OWNER$p())) {
               this.release()
               return
            }
         }
      }

      throw IllegalStateException("This mutex is not locked".toString())
   }

   public open val onLock: SelectClause2<Any?, Mutex>
      public open get() {
         var var10003: Function3 = <unrepresentable>.INSTANCE
         var10003 = TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10003, 3) as Function3
         val var10004: <unrepresentable> = <unrepresentable>.INSTANCE
         return SelectClause2Impl<>(
            this,
            var10003,
            TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10004, 3) as (Any?, Any?, Any?) -> Any,
            this.onSelectCancellationUnlockConstructor
         )
      }


   protected open fun onLockRegFunction(select: SelectInstance<*>, owner: Any?) {
      if (owner != null && this.holdsLock(owner)) {
         select.selectInRegistrationPhase(MutexKt.access$getON_LOCK_ALREADY_LOCKED_BY_OWNER$p())
      } else {
         this.onAcquireRegFunction(MutexImpl.SelectInstanceWithOwner(select as SelectInstanceInternal, owner), owner)
      }
   }

   protected open fun onLockProcessResult(owner: Any?, result: Any?): Any? {
      if (result == MutexKt.access$getON_LOCK_ALREADY_LOCKED_BY_OWNER$p()) {
         throw IllegalStateException(("This mutex is already locked by the specified owner: $owner").toString())
      } else {
         return this
      }
   }

   public override fun toString(): String {
      return "Mutex@${hexAddress}[isLocked=${this.isLocked},owner=${owner$FU.get(this)}]"
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$lockSuspend`(`$this`: MutexImpl, owner: Any, `$completion`: Continuation) {
      `$this`.lockSuspend(owner, `$completion`)
   }

   @SourceDebugExtension(["SMAP\nMutex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Mutex.kt\nkotlinx/coroutines/sync/MutexImpl$CancellableContinuationWithOwner\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n1#2:311\n*E\n"])
   private inner class CancellableContinuationWithOwner(cont: CancellableContinuationImpl<Unit>, owner: Any?) : CancellableContinuation<Unit>, Waiter {
      @JvmField
      @NotNull
      public final val cont: CancellableContinuationImpl<Unit>

      @JvmField
      @Nullable
      public final val owner: Any?

      init {
         this.cont = cont
         this.owner = owner
      }

      public open val context: CoroutineContext

      public open val isActive: Boolean

      public open val isCancelled: Boolean

      public open val isCompleted: Boolean

      public override fun cancel(cause: Throwable? = ...): Boolean {
         return this.cont.cancel(cause)
      }

      @InternalCoroutinesApi
      public override fun completeResume(token: Any) {
         this.cont.completeResume(token)
      }

      @InternalCoroutinesApi
      public override fun initCancellability() {
         this.cont.initCancellability()
      }

      public override fun invokeOnCancellation(handler: (Throwable?) -> Unit) {
         this.cont.invokeOnCancellation(handler)
      }

      public open fun resumeWith(result: Result<Unit>) {
         this.cont.resumeWith(result)
      }

      @InternalCoroutinesApi
      public open fun tryResume(value: Unit, idempotent: Any? = ...): Any? {
         return this.cont.tryResume(value, idempotent)
      }

      @InternalCoroutinesApi
      public override fun tryResumeWithException(exception: Throwable): Any? {
         return this.cont.tryResumeWithException(exception)
      }

      @ExperimentalCoroutinesApi
      public open fun CoroutineDispatcher.resumeUndispatched(value: Unit) {
         this.cont.resumeUndispatched(`$this$resumeUndispatched`, value)
      }

      @ExperimentalCoroutinesApi
      public override fun CoroutineDispatcher.resumeUndispatchedWithException(exception: Throwable) {
         this.cont.resumeUndispatchedWithException(`$this$resumeUndispatchedWithException`, exception)
      }

      public override fun invokeOnCancellation(segment: Segment<*>, index: Int) {
         this.cont.invokeOnCancellation(segment, index)
      }

      public open fun tryResume(value: Unit, idempotent: Any?, onCancellation: ((Throwable) -> Unit)?): Any? {
         var token: Any = MutexImpl.this
         if (ASSERTIONS_ENABLED && MutexImpl.owner$FU.get(token) != MutexKt.access$getNO_OWNER$p()) {
            throw AssertionError()
         } else {
            token = this.cont.tryResume(value, idempotent, { it: Throwable ->
               val var2: MutexImpl = var8/* $VF was: MutexImpl.this */
               val var3: MutexImpl.CancellableContinuationWithOwner = CancellableContinuationWithOwner.this
               if (ASSERTIONS_ENABLED) {
                  val itx: Any = MutexImpl.owner$FU.get(var2)
                  if (itx != MutexKt.access$getNO_OWNER$p() && itx != var3.owner) {
                     throw AssertionError()
                  }
               }

               MutexImpl.owner$FU.set(var8/* $VF was: MutexImpl.this */, CancellableContinuationWithOwner.this.owner)
               var8/* $VF was: MutexImpl.this */.unlock(CancellableContinuationWithOwner.this.owner)
            } as (java.lang.Throwable?) -> Unit)
            if (token != null) {
               val var8: MutexImpl = MutexImpl.this
               if (ASSERTIONS_ENABLED && MutexImpl.owner$FU.get(var8) != MutexKt.access$getNO_OWNER$p()) {
                  throw AssertionError()
               }

               MutexImpl.owner$FU.set(MutexImpl.this, this.owner)
            }

            return token
         }
      }

      public open fun resume(value: Unit, onCancellation: ((Throwable) -> Unit)?) {
         val var3: MutexImpl = MutexImpl.this
         if (ASSERTIONS_ENABLED && MutexImpl.owner$FU.get(var3) != MutexKt.access$getNO_OWNER$p()) {
            throw AssertionError()
         } else {
            MutexImpl.owner$FU.set(MutexImpl.this, this.owner)
            this.cont.resume(value, { it: Throwable ->
               var3/* $VF was: MutexImpl.this */.unlock(CancellableContinuationWithOwner.this.owner)
            } as (java.lang.Throwable?) -> Unit)
         }
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override fun tryResume(value: Any, idempotent: Any) {
         this.tryResume(value as Unit, idempotent)
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override fun CoroutineDispatcher.resumeUndispatched(value: Any) {
         this.resumeUndispatched(`$this$resumeUndispatched`, value as Unit)
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override fun tryResume(value: Any, idempotent: Any, onCancellation: Function1) {
         this.tryResume(value as Unit, idempotent, onCancellation)
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override fun resume(value: Any, onCancellation: Function1) {
         this.resume(value as Unit, onCancellation)
      }
   }

   @SourceDebugExtension(["SMAP\nMutex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Mutex.kt\nkotlinx/coroutines/sync/MutexImpl$SelectInstanceWithOwner\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n1#2:311\n*E\n"])
   private inner class SelectInstanceWithOwner<Q>(select: SelectInstanceInternal<Any>, owner: Any?) : SelectInstanceInternal<Q> {
      @JvmField
      @NotNull
      public final val select: SelectInstanceInternal<Any>

      @JvmField
      @Nullable
      public final val owner: Any?

      init {
         this.select = select
         this.owner = owner
      }

      public open val context: CoroutineContext

      public override fun disposeOnCompletion(disposableHandle: DisposableHandle) {
         this.select.disposeOnCompletion(disposableHandle)
      }

      public override fun invokeOnCancellation(segment: Segment<*>, index: Int) {
         this.select.invokeOnCancellation(segment, index)
      }

      public override fun trySelect(clauseObject: Any, result: Any?): Boolean {
         val var3: MutexImpl = MutexImpl.this
         if (ASSERTIONS_ENABLED && MutexImpl.owner$FU.get(var3) != MutexKt.access$getNO_OWNER$p()) {
            throw AssertionError()
         } else {
            val var7: Boolean = this.select.trySelect(clauseObject, result)
            val var8: MutexImpl = MutexImpl.this
            if (var7) {
               MutexImpl.owner$FU.set(var8, this.owner)
            }

            return var7
         }
      }

      public override fun selectInRegistrationPhase(internalResult: Any?) {
         val var2: MutexImpl = MutexImpl.this
         if (ASSERTIONS_ENABLED && MutexImpl.owner$FU.get(var2) != MutexKt.access$getNO_OWNER$p()) {
            throw AssertionError()
         } else {
            MutexImpl.owner$FU.set(MutexImpl.this, this.owner)
            this.select.selectInRegistrationPhase(internalResult)
         }
      }
   }
}
