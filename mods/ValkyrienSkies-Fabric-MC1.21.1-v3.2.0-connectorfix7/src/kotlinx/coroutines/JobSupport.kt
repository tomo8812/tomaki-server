package kotlinx.coroutines

import java.util.ArrayList
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.internal.LockFreeLinkedListHead
import kotlinx.coroutines.internal.LockFreeLinkedListNode
import kotlinx.coroutines.internal.OpDescriptor
import kotlinx.coroutines.internal.StackTraceRecoveryKt
import kotlinx.coroutines.internal.Symbol
import kotlinx.coroutines.selects.SelectClause0
import kotlinx.coroutines.selects.SelectClause0Impl
import kotlinx.coroutines.selects.SelectClause1
import kotlinx.coroutines.selects.SelectClause1Impl
import kotlinx.coroutines.selects.SelectInstance

/** @deprecated */
@Deprecated(message = "This is internal API and may be removed in the future releases", level = DeprecationLevel.ERROR)
@SourceDebugExtension(["SMAP\nJobSupport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JobSupport.kt\nkotlinx/coroutines/JobSupport\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 4 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 6 Concurrent.kt\nkotlinx/coroutines/internal/ConcurrentKt\n+ 7 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n+ 8 Exceptions.kt\nkotlinx/coroutines/ExceptionsKt\n+ 9 LockFreeLinkedList.kt\nkotlinx/coroutines/internal/LockFreeLinkedListHead\n+ 10 CompletionHandler.common.kt\nkotlinx/coroutines/CompletionHandler_commonKt\n+ 11 CompletionHandler.kt\nkotlinx/coroutines/CompletionHandlerKt\n+ 12 LockFreeLinkedList.kt\nkotlinx/coroutines/internal/LockFreeLinkedListNode\n+ 13 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n*L\n1#1,1454:1\n706#1,2:1461\n367#1,2:1471\n369#1,4:1476\n373#1,4:1482\n377#1,2:1489\n367#1,2:1491\n369#1,4:1496\n373#1,4:1502\n377#1,2:1509\n178#1,2:1518\n707#1:1520\n178#1,2:1521\n178#1,2:1540\n178#1,2:1555\n706#1,2:1557\n706#1,2:1559\n178#1,2:1561\n706#1,2:1563\n178#1,2:1565\n178#1,2:1572\n178#1,2:1574\n1#2:1455\n1#2:1480\n1#2:1500\n28#3,4:1456\n28#3,4:1523\n28#3,4:1567\n28#3,4:1576\n20#4:1460\n20#4:1527\n20#4:1571\n20#4:1580\n288#5,2:1463\n288#5,2:1465\n19#6:1467\n163#7:1468\n163#7:1469\n153#7,4:1583\n75#8:1470\n75#8:1481\n75#8:1501\n75#8:1514\n341#9,3:1473\n344#9,3:1486\n341#9,3:1493\n344#9,3:1506\n341#9,3:1511\n344#9,3:1515\n47#10:1528\n22#11:1529\n22#11:1530\n13#11:1551\n13#11:1554\n13#11:1581\n13#11:1582\n13#11:1587\n13#11:1588\n134#12:1531\n73#12,3:1532\n135#12,5:1535\n314#13,9:1542\n323#13,2:1552\n*S KotlinDebug\n*F\n+ 1 JobSupport.kt\nkotlinx/coroutines/JobSupport\n*L\n250#1:1461,2\n332#1:1471,2\n332#1:1476,4\n332#1:1482,4\n332#1:1489,2\n364#1:1491,2\n364#1:1496,4\n364#1:1502,4\n364#1:1509,2\n381#1:1518,2\n426#1:1520\n461#1:1521,2\n553#1:1540,2\n594#1:1555,2\n621#1:1557,2\n630#1:1559,2\n694#1:1561,2\n723#1:1563,2\n736#1:1565,2\n809#1:1572,2\n831#1:1574,2\n332#1:1480\n364#1:1500\n213#1:1456,4\n478#1:1523,4\n739#1:1567,4\n884#1:1576,4\n213#1:1460\n478#1:1527\n739#1:1571\n884#1:1580\n261#1:1463,2\n265#1:1465,2\n273#1:1467\n279#1:1468\n281#1:1469\n1218#1:1583,4\n284#1:1470\n332#1:1481\n364#1:1501\n372#1:1514\n332#1:1473,3\n332#1:1486,3\n364#1:1493,3\n364#1:1506,3\n368#1:1511,3\n368#1:1515,3\n483#1:1528\n495#1:1529\n505#1:1530\n561#1:1551\n577#1:1554\n924#1:1581\n974#1:1582\n1237#1:1587\n1259#1:1588\n526#1:1531\n526#1:1532,3\n526#1:1535,5\n559#1:1542,9\n559#1:1552,2\n*E\n"])
public open class JobSupport(active: Boolean) : Job, ChildJob, ParentJob {
   @Volatile
   @Volatile
   private Object _state;
   @Volatile
   @Volatile
   private Object _parentHandle;
   @JvmStatic
   private AtomicReferenceFieldUpdater _state$FU = AtomicReferenceFieldUpdater.newUpdater(JobSupport.class, Object.class, "_state");
   @JvmStatic
   private AtomicReferenceFieldUpdater _parentHandle$FU = AtomicReferenceFieldUpdater.newUpdater(JobSupport.class, Object.class, "_parentHandle");

   init {
      this._state = if (active) JobSupportKt.access$getEMPTY_ACTIVE$p() else JobSupportKt.access$getEMPTY_NEW$p()
   }

   public final val key: kotlin.coroutines.CoroutineContext.Key<*>
      public final get() {
         return Job.Key
      }


   internal final var parentHandle: ChildHandle?
      internal final get() {
         return _parentHandle$FU.get(this) as ChildHandle
      }

      internal final set(value) {
         _parentHandle$FU.set(this, value)
      }


   public open val parent: Job?
      public open get() {
         val var10000: ChildHandle = this.parentHandle
         return if (var10000 != null) var10000.parent else null
      }


   protected fun initParentJob(parent: Job?) {
      if (ASSERTIONS_ENABLED && this.parentHandle != null) {
         throw AssertionError()
      } else if (parent == null) {
         this.parentHandle = NonDisposableHandle.INSTANCE as ChildHandle
      } else {
         parent.start()
         val var3: ChildHandle = parent.attachChild(this)
         this.parentHandle = var3
         if (this.isCompleted) {
            var3.dispose()
            this.parentHandle = NonDisposableHandle.INSTANCE as ChildHandle
         }
      }
   }

   internal final val state: Any?
      internal final get() {
         val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _state$FU

         while (true) {
            val state: Any = `atomicfu$handler$iv`.get(this)
            if (state !is OpDescriptor) {
               return state
            }

            (state as OpDescriptor).perform(this)
         }
      }


   private inline fun loopOnState(block: (Any?) -> Unit): Nothing {
      while (true) {
         block(this.state)
      }
   }

   public open val isActive: Boolean
      public open get() {
         val state: Any = this.state
         return state is Incomplete && (state as Incomplete).isActive
      }


   public final val isCompleted: Boolean
      public final get() {
         return this.state !is Incomplete
      }


   public final val isCancelled: Boolean
      public final get() {
         val state: Any = this.state
         return state is CompletedExceptionally || state is JobSupport.Finishing && (state as JobSupport.Finishing).isCancelling
      }


   private fun finalizeFinishingState(state: kotlinx.coroutines.JobSupport.Finishing, proposedUpdate: Any?): Any? {
      if (ASSERTIONS_ENABLED && this.state != state) {
         throw AssertionError()
      } else if (ASSERTIONS_ENABLED && state.isSealed) {
         throw AssertionError()
      } else if (ASSERTIONS_ENABLED && !state.isCompleting) {
         throw AssertionError()
      } else {
         val var16: java.lang.Throwable = if ((proposedUpdate as? CompletedExceptionally) != null) (proposedUpdate as? CompletedExceptionally).cause else null
         val var17: Boolean
         val var10000: java.lang.Throwable
         synchronized (state) {
            var17 = state.isCancelling
            val exceptions: java.util.List = state.sealLocked(var16)
            val finalCause: java.lang.Throwable = this.getFinalRootCause(state, exceptions)
            if (finalCause != null) {
               this.addSuppressedExceptions(finalCause, exceptions)
            }

            var10000 = finalCause
         }

         val var18: Any = if (var10000 == null)
            proposedUpdate
            else
            (if (var10000 === var16) proposedUpdate else CompletedExceptionally(var10000, false, 2, null))
            if (var10000 != null && (this.cancelParent(var10000) || this.handleJobException(var10000))) {
            (var18 as CompletedExceptionally).makeHandled()
         }

         if (!var17) {
            this.onCancelling(var10000)
         }

         this.onCompletionInternal(var18)
         if (ASSERTIONS_ENABLED && !_state$FU.compareAndSet(this, state, (JobSupport.Finishing)JobSupportKt.boxIncomplete(var18))) {
            throw AssertionError()
         } else {
            this.completeStateFinalization(state, var18)
            return var18
         }
      }
   }

   private fun getFinalRootCause(state: kotlinx.coroutines.JobSupport.Finishing, exceptions: List<Throwable>): Throwable? {
      label88@
      if (exceptions.isEmpty()) {
         return if (state.isCancelling) JobCancellationException(access$cancellationExceptionMessage(this), null, this) else null
      } else {
         val `$this$firstOrNull$iv`: java.util.Iterator = exceptions.iterator()

         var var10000: Any
         while (true) {
            if (`$this$firstOrNull$iv`.hasNext()) {
               val `$i$f$firstOrNull`: Any = `$this$firstOrNull$iv`.next()
               if (`$i$f$firstOrNull` as java.lang.Throwable is CancellationException) {
                  continue
               }

               var10000 = `$i$f$firstOrNull`
               break
            }

            var10000 = null
            break
         }

         val firstNonCancellation: java.lang.Throwable = var10000 as java.lang.Throwable
         if (var10000 as java.lang.Throwable != null) {
            return firstNonCancellation
         } else {
            val var13: java.lang.Throwable = exceptions.get(0) as java.lang.Throwable
            if (var13 is TimeoutCancellationException) {
               val var19: java.util.Iterator = exceptions.iterator()

               while (true) {
                  if (!var19.hasNext()) {
                     var10000 = null
                     break
                  }

                  val var20: Any = var19.next()
                  if (var20 as java.lang.Throwable != var13 && var20 as java.lang.Throwable is TimeoutCancellationException) {
                     var10000 = var20
                     break
                  }
               }

               val var15: java.lang.Throwable = var10000 as java.lang.Throwable
               if (var10000 as java.lang.Throwable != null) {
                  return var15
               }
            }

            return var13
         }
      }
   }

   private fun addSuppressedExceptions(rootCause: Throwable, exceptions: List<Throwable>) {
      if (exceptions.size() > 1) {
         val seenExceptions: java.util.Set = Collections.newSetFromMap(IdentityHashMap(exceptions.size()))
         val var10: java.lang.Throwable = if (!RECOVER_STACK_TRACES) rootCause else StackTraceRecoveryKt.unwrapImpl(rootCause)

         for (exception in exceptions) {
            val unwrapped: java.lang.Throwable = if (!RECOVER_STACK_TRACES) exception else StackTraceRecoveryKt.unwrapImpl(exception)
            if (unwrapped != rootCause && unwrapped != var10 && unwrapped !is CancellationException && seenExceptions.add(unwrapped)) {
               kotlin.ExceptionsKt.addSuppressed(rootCause, unwrapped)
            }
         }
      }
   }

   private fun tryFinalizeSimpleState(state: Incomplete, update: Any?): Boolean {
      if (ASSERTIONS_ENABLED && state !is Empty && state !is JobNode) {
         throw AssertionError()
      } else if (ASSERTIONS_ENABLED && update is CompletedExceptionally) {
         throw AssertionError()
      } else if (!_state$FU.compareAndSet(this, state, (Incomplete)JobSupportKt.boxIncomplete(update))) {
         return false
      } else {
         this.onCancelling(null)
         this.onCompletionInternal(update)
         this.completeStateFinalization(state, update)
         return true
      }
   }

   private fun completeStateFinalization(state: Incomplete, update: Any?) {
      val var10000: ChildHandle = this.parentHandle
      if (var10000 != null) {
         var10000.dispose()
         this.parentHandle = NonDisposableHandle.INSTANCE as ChildHandle
      }

      val cause: java.lang.Throwable = if ((update as? CompletedExceptionally) != null) (update as? CompletedExceptionally).cause else null
      if (state is JobNode) {
         try {
            (state as JobNode).invoke(cause)
         } catch (var7: java.lang.Throwable) {
            this.handleOnCompletionException$kotlinx_coroutines_core(CompletionHandlerException("Exception in completion handler $state for $this", var7))
         }
      } else {
         val var8: NodeList = state.list
         if (var8 != null) {
            this.notifyCompletion(var8, cause)
         }
      }
   }

   private fun notifyCancelling(list: NodeList, cause: Throwable) {
      this.onCancelling(cause)
      val `this_$iv`: JobSupport = this
      var `exception$iv`: Any = null
      val `this_$iv$iv`: LockFreeLinkedListHead = list
      val var10000: Any = list.getNext()

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var `it$iv`: LockFreeLinkedListNode = var10000 as LockFreeLinkedListNode
      while (true) {
         if (!(`it$iv` == `this_$iv$iv`)) break
         if (`it$iv` is JobCancellingNode) {
            val var9: JobNode = `it$iv` as JobNode

            try {
               var9.invoke(cause)
            } catch (var17: java.lang.Throwable) {
               val var22: java.lang.Throwable = `exception$iv` as java.lang.Throwable
               if (`exception$iv` as java.lang.Throwable != null) {
                  kotlin.ExceptionsKt.addSuppressed(var22, var17)
                  if (var22 != null) {
                     continue
                  }
               }

               `exception$iv` = CompletionHandlerException("Exception in completion handler ${`it$iv` as JobNode} for $`this_$iv`", var17)
            }
         }

         `it$iv` = `it$iv`.nextNode
      }

      val var23: java.lang.Throwable = `exception$iv` as java.lang.Throwable
      if (`exception$iv` as java.lang.Throwable != null) {
         `this_$iv`.handleOnCompletionException$kotlinx_coroutines_core(var23)
      }

      this.cancelParent(cause)
   }

   private fun cancelParent(cause: Throwable): Boolean {
      if (this.isScopedCoroutine) {
         return true
      } else {
         val isCancellation: Boolean = cause is CancellationException
         val parent: ChildHandle = this.parentHandle
         return if (parent != null && parent != NonDisposableHandle.INSTANCE) parent.childCancelled(cause) || isCancellation else isCancellation
      }
   }

   private fun NodeList.notifyCompletion(cause: Throwable?) {
      val `this_$iv`: JobSupport = this
      var `exception$iv`: Any = null
      val `this_$iv$iv`: LockFreeLinkedListHead = `$this$notifyCompletion`
      val var10000: Any = `$this$notifyCompletion`.getNext()

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var `it$iv`: LockFreeLinkedListNode = var10000 as LockFreeLinkedListNode
      while (true) {
         if (!(`it$iv` == `this_$iv$iv`)) break
         if (`it$iv` is JobNode) {
            val var9: JobNode = `it$iv` as JobNode

            try {
               var9.invoke(cause)
            } catch (var17: java.lang.Throwable) {
               val var22: java.lang.Throwable = `exception$iv` as java.lang.Throwable
               if (`exception$iv` as java.lang.Throwable != null) {
                  kotlin.ExceptionsKt.addSuppressed(var22, var17)
                  if (var22 != null) {
                     continue
                  }
               }

               `exception$iv` = CompletionHandlerException("Exception in completion handler ${`it$iv` as JobNode} for $`this_$iv`", var17)
            }
         }

         `it$iv` = `it$iv`.nextNode
      }

      val var23: java.lang.Throwable = `exception$iv` as java.lang.Throwable
      if (`exception$iv` as java.lang.Throwable != null) {
         `this_$iv`.handleOnCompletionException$kotlinx_coroutines_core(var23)
      }
   }

   @JvmSynthetic
   private inline fun <reified T : JobNode> notifyHandlers(list: NodeList, cause: Throwable?) {
      var exception: Any = null
      val `this_$iv`: LockFreeLinkedListHead = list
      val var10000: Any = list.getNext()

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var it: LockFreeLinkedListNode = var10000 as LockFreeLinkedListNode
      while (true) {
         if (!(it == `this_$iv`)) break
         Intrinsics.reifiedOperationMarker(3, "T")
         if (it is LockFreeLinkedListNode) {
            val var8: JobNode = it as JobNode

            try {
               var8.invoke(cause)
            } catch (var16: java.lang.Throwable) {
               val var21: java.lang.Throwable = exception as java.lang.Throwable
               if (exception as java.lang.Throwable != null) {
                  kotlin.ExceptionsKt.addSuppressed(var21, var16)
                  if (var21 != null) {
                     continue
                  }
               }

               exception = CompletionHandlerException("Exception in completion handler ${it as JobNode} for ${this}", var16)
            }
         }

         it = it.nextNode
      }

      val var22: java.lang.Throwable = exception as java.lang.Throwable
      if (exception as java.lang.Throwable != null) {
         this.handleOnCompletionException$kotlinx_coroutines_core(var22)
      }
   }

   public override fun start(): Boolean {
      val `this_$iv`: JobSupport = this

      while (true) {
         when (this.startInternal(`this_$iv`.state)) {
            0 -> return false
            1 -> return true
            else -> {}
         }
      }
   }

   private fun startInternal(state: Any?): Int {
      if (state is Empty) {
         if ((state as Empty).isActive) {
            return 0
         } else if (!_state$FU.compareAndSet(this, state, JobSupportKt.access$getEMPTY_ACTIVE$p())) {
            return -1
         } else {
            this.onStart()
            return 1
         }
      } else if (state is InactiveNodeList) {
         if (!_state$FU.compareAndSet(this, state, (state as InactiveNodeList).list)) {
            return -1
         } else {
            this.onStart()
            return 1
         }
      } else {
         return 0
      }
   }

   protected open fun onStart() {
   }

   public override fun getCancellationException(): CancellationException {
      val state: Any = this.state
      val var2: CancellationException
      if (state is JobSupport.Finishing) {
         val var10000: java.lang.Throwable = (state as JobSupport.Finishing).rootCause
         if (var10000 == null) {
            throw IllegalStateException(("Job is still new or active: $this").toString())
         }

         var2 = this.toCancellationException(var10000, "${classSimpleName} is cancelling")
         if (var2 == null) {
            throw IllegalStateException(("Job is still new or active: $this").toString())
         }
      } else {
         if (state is Incomplete) {
            throw IllegalStateException(("Job is still new or active: $this").toString())
         }

         var2 = if (state is CompletedExceptionally)
            toCancellationException$default(this, (state as CompletedExceptionally).cause, null, 1, null)
            else
            JobCancellationException("${classSimpleName} has completed normally", null, this)
         }

      return var2
   }

   protected fun Throwable.toCancellationException(message: String? = null): CancellationException {
      var var10000: JobCancellationException = `$this$toCancellationException` as? CancellationException
      if ((`$this$toCancellationException` as? CancellationException) == null) {
         var10000 = JobCancellationException
         var var10002: java.lang.String = message
         if (message == null) {
            var10002 = access$cancellationExceptionMessage(this)
         }

         var10000./* $VF: Unable to resugar constructor */<init>(var10002, `$this$toCancellationException`, this)
         var10000 = var10000
      }

      return var10000
   }

   protected final val completionCause: Throwable?
      protected final get() {
         val state: Any = this.state
         val var10000: java.lang.Throwable
         if (state is JobSupport.Finishing) {
            var10000 = (state as JobSupport.Finishing).rootCause
            if (var10000 == null) {
               throw IllegalStateException(("Job is still new or active: $this").toString())
            }
         } else {
            if (state is Incomplete) {
               throw IllegalStateException(("Job is still new or active: $this").toString())
            }

            var10000 = if (state is CompletedExceptionally) (state as CompletedExceptionally).cause else null
         }

         return var10000
      }


   protected final val completionCauseHandled: Boolean
      protected final get() {
         val it: Any = this.state
         return it is CompletedExceptionally && (it as CompletedExceptionally).handled
      }


   public override fun invokeOnCompletion(handler: (Throwable?) -> Unit): DisposableHandle {
      return this.invokeOnCompletion(false, true, handler)
   }

   public override fun invokeOnCompletion(onCancelling: Boolean, invokeImmediately: Boolean, handler: (Throwable?) -> Unit): DisposableHandle {
      val node: JobNode = this.makeNode(handler, onCancelling)
      val `this_$iv`: JobSupport = this

      val state: Any
      val var19: DisposableHandle
      while (true) {
         state = `this_$iv`.state
         if (state is Empty) {
            if ((state as Empty).isActive) {
               if (_state$FU.compareAndSet(this, state, node)) {
                  return node
               }
            } else {
               this.promoteEmptyToNodeList(state as Empty)
            }
         } else {
            if (state !is Incomplete) {
               if (invokeImmediately) {
                  handler(if ((state as? CompletedExceptionally) != null) (state as? CompletedExceptionally).cause else null)
               }

               return NonDisposableHandle.INSTANCE
            }

            val `$this$invokeIt$iv`: NodeList = (state as Incomplete).list
            if (`$this$invokeIt$iv` == null) {
               this.promoteSingleToNodeList(state as JobNode)
            } else {
               var `cause$iv`: Any = null
               var var24: DisposableHandle = NonDisposableHandle.INSTANCE
               if (onCancelling && state is JobSupport.Finishing) {
                  synchronized (state) {
                     `cause$iv` = (state as JobSupport.Finishing).rootCause
                     if (`cause$iv` == null || handler is ChildHandleNode && !(state as JobSupport.Finishing).isCompleting) {
                        if (!this.addLastAtomic(state, `$this$invokeIt$iv`, node)) {
                           continue
                        }

                        if (`cause$iv` == null) {
                           var19 = node
                           break
                        }

                        var24 = node
                     }
                  }
               }

               if (`cause$iv` != null) {
                  if (invokeImmediately) {
                     handler(`cause$iv`)
                  }

                  return var24
               }

               if (this.addLastAtomic(state, `$this$invokeIt$iv`, node)) {
                  return node
               }
            }
         }
      }

      // $VF: monitorexit
      return var19
   }

   private fun makeNode(handler: (Throwable?) -> Unit, onCancelling: Boolean): JobNode {
      var var9: JobNode
      if (onCancelling) {
         var9 = handler as? JobCancellingNode
         if ((handler as? JobCancellingNode) == null) {
            var9 = InvokeOnCancelling(handler)
         }

         var9 = var9
      } else {
         val var4: JobNode = handler as? JobNode
         if ((handler as? JobNode) != null) {
            if (ASSERTIONS_ENABLED && var4 is JobCancellingNode) {
               throw AssertionError()
            }

            var9 = var4
         } else {
            var9 = InvokeOnCompletion(handler)
         }
      }

      var9.job = this
      return var9
   }

   private fun addLastAtomic(expect: Any, list: NodeList, node: JobNode): Boolean {
      val `this_$iv`: LockFreeLinkedListNode = list
      val `condAdd$iv`: LockFreeLinkedListNode.CondAddOp = JobSupport$addLastAtomic$$inlined$addLastIf$1(node, this, expect)

      while (true) {
         when (`this_$iv`.prevNode.tryCondAddNext(node, `this_$iv`, `condAdd$iv`)) {
            1 -> return true
            2 -> return false
            else -> {}
         }
      }
   }

   private fun promoteEmptyToNodeList(state: Empty) {
      val list: NodeList = NodeList()
      _state$FU.compareAndSet(this, state, if (state.isActive) list else InactiveNodeList(list))
   }

   private fun promoteSingleToNodeList(state: JobNode) {
      state.addOneIfEmpty(NodeList())
      _state$FU.compareAndSet(this, state, state.getNextNode())
   }

   public override suspend fun join() {
      if (!this.joinInternal()) {
         JobKt.ensureActive(`$completion`.getContext())
         return Unit.INSTANCE
      } else {
         val var10000: Any = this.joinSuspend(`$completion`)
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }
   }

   private fun joinInternal(): Boolean {
      val `this_$iv`: JobSupport = this

      val state: Any
      do {
         state = `this_$iv`.state
         if (state !is Incomplete) {
            return false
         }
      } while (this.startInternal(state) < 0)

      return true
   }

   private suspend fun joinSuspend() {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
      `cancellable$iv`.initCancellability()
      CancellableContinuationKt.disposeOnCancellation(`cancellable$iv`, this.invokeOnCompletion(ResumeOnCompletion(`cancellable$iv`)))
      val var10000: Any = `cancellable$iv`.getResult()
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   public final val onJoin: SelectClause0
      public final get() {
         val var10003: <unrepresentable> = <unrepresentable>.INSTANCE
         return SelectClause0Impl(this, TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10003, 3) as Function3, null, 4, null)
      }


   private fun registerSelectForOnJoin(select: SelectInstance<*>, ignoredParam: Any?) {
      if (!this.joinInternal()) {
         select.selectInRegistrationPhase(Unit.INSTANCE)
      } else {
         select.disposeOnCompletion(this.invokeOnCompletion(JobSupport.SelectOnJoinCompletionHandler(select)))
      }
   }

   internal fun removeNode(node: JobNode) {
      val `this_$iv`: JobSupport = this

      val state: Any
      do {
         state = `this_$iv`.state
         if (state !is JobNode) {
            if (state is Incomplete) {
               if ((state as Incomplete).list != null) {
                  node.remove()
               }

               return
            }

            return
         }

         if (state != node) {
            return
         }
      } while (!_state$FU.compareAndSet(this, state, JobSupportKt.access$getEMPTY_ACTIVE$p()))
   }

   internal open val onCancelComplete: Boolean
      internal open get() {
         return false
      }


   public override fun cancel(cause: CancellationException?) {
      var var10001: CancellationException = cause
      if (cause == null) {
         var10001 = JobCancellationException(access$cancellationExceptionMessage(this), null, this)
      }

      this.cancelInternal(var10001)
   }

   protected open fun cancellationExceptionMessage(): String {
      return "Job was cancelled"
   }

   @Deprecated(message = "Added since 1.2.0 for binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public override fun cancel(cause: Throwable?): Boolean {
      var var10001: CancellationException
      run label15@{
         if (cause != null) {
            var10001 = toCancellationException$default(this, cause, null, 1, null)
            if (var10001 != null) {
               return@label15
            }
         }

         var10001 = JobCancellationException(access$cancellationExceptionMessage(this), null, this)
      }

      this.cancelInternal(var10001)
      return true
   }

   public open fun cancelInternal(cause: Throwable) {
      this.cancelImpl$kotlinx_coroutines_core(cause)
   }

   public override fun parentCancelled(parentJob: ParentJob) {
      this.cancelImpl$kotlinx_coroutines_core(parentJob)
   }

   public open fun childCancelled(cause: Throwable): Boolean {
      return cause is CancellationException || this.cancelImpl$kotlinx_coroutines_core(cause) && this.handlesException
   }

   public fun cancelCoroutine(cause: Throwable?): Boolean {
      return this.cancelImpl$kotlinx_coroutines_core(cause)
   }

   internal fun cancelImpl(cause: Any?): Boolean {
      var finalState: Any = JobSupportKt.access$getCOMPLETING_ALREADY$p()
      if (this.onCancelComplete) {
         finalState = this.cancelMakeCompleting(cause)
         if (finalState === JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            return true
         }
      }

      if (finalState === JobSupportKt.access$getCOMPLETING_ALREADY$p()) {
         finalState = this.makeCancelling(cause)
      }

      val var10000: Boolean
      if (finalState === JobSupportKt.access$getCOMPLETING_ALREADY$p()) {
         var10000 = true
      } else if (finalState === JobSupportKt.COMPLETING_WAITING_CHILDREN) {
         var10000 = true
      } else if (finalState === JobSupportKt.access$getTOO_LATE_TO_CANCEL$p()) {
         var10000 = false
      } else {
         this.afterCompletion(finalState)
         var10000 = true
      }

      return var10000
   }

   private fun cancelMakeCompleting(cause: Any?): Any? {
      val `this_$iv`: JobSupport = this

      val finalState: Any
      do {
         val state: Any = `this_$iv`.state
         if (state !is Incomplete || state is JobSupport.Finishing && (state as JobSupport.Finishing).isCompleting) {
            return JobSupportKt.access$getCOMPLETING_ALREADY$p()
         }

         finalState = this.tryMakeCompleting(state, CompletedExceptionally(this.createCauseException(cause), false, 2, null))
      } while (finalState === JobSupportKt.access$getCOMPLETING_RETRY$p())

      return finalState
   }

   internal inline fun defaultCancellationException(message: String? = ..., cause: Throwable? = ...): JobCancellationException {
      val var10000: JobCancellationException = JobCancellationException
      var var10002: java.lang.String = message
      if (message == null) {
         var10002 = access$cancellationExceptionMessage(this)
      }

      var10000./* $VF: Unable to resugar constructor */<init>(var10002, cause, this)
      return var10000
   }

   @JvmStatic
   @JvmSynthetic
   fun `defaultCancellationException$kotlinx_coroutines_core$default`(
      `$this`: JobSupport, message: java.lang.String, cause: java.lang.Throwable, `$i$f$defaultCancellationException$kotlinx_coroutines_core`: Int, var4: Any
   ): JobCancellationException {
      if (var4 != null) {
         throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: defaultCancellationException")
      } else {
         if ((`$i$f$defaultCancellationException$kotlinx_coroutines_core` and 1) != 0) {
            message = null
         }

         if ((`$i$f$defaultCancellationException$kotlinx_coroutines_core` and 2) != 0) {
            cause = null
         }

         val var10000: JobCancellationException = JobCancellationException
         var var10002: java.lang.String = message
         if (message == null) {
            var10002 = access$cancellationExceptionMessage(`$this`)
         }

         var10000./* $VF: Unable to resugar constructor */<init>(var10002, cause, `$this`)
         var10000
      }
   }

   public override fun getChildJobCancellationCause(): CancellationException {
      val state: Any = this.state
      var var10000: java.lang.Throwable
      if (state is JobSupport.Finishing) {
         var10000 = (state as JobSupport.Finishing).rootCause
      } else if (state is CompletedExceptionally) {
         var10000 = (state as CompletedExceptionally).cause
      } else {
         if (state is Incomplete) {
            throw IllegalStateException(("Cannot be cancelling child in this state: $state").toString())
         }

         var10000 = null
      }

      var10000 = var10000 as? CancellationException
      if ((var10000 as? CancellationException) == null) {
         var10000 = JobCancellationException("Parent job is ${this.stateString(state)}", var10000, this)
      }

      return var10000
   }

   private fun createCauseException(cause: Any?): Throwable {
      var var10000: java.lang.Throwable
      if (cause == null || cause is java.lang.Throwable) {
         var10000 = cause as java.lang.Throwable
         if (cause as java.lang.Throwable == null) {
            var10000 = JobCancellationException(access$cancellationExceptionMessage(this), null, this)
         }
      } else {
         var10000 = (cause as ParentJob).getChildJobCancellationCause()
      }

      return var10000
   }

   private fun makeCancelling(cause: Any?): Any? {
      var causeExceptionCache: Any = null
      val `this_$iv`: JobSupport = this

      while (true) {
         val state: Any = `this_$iv`.state
         if (state is JobSupport.Finishing) {
            val var19: java.lang.Throwable
            synchronized (state) {
               if ((state as JobSupport.Finishing).isSealed) {
                  return JobSupportKt.access$getTOO_LATE_TO_CANCEL$p()
               }

               val wasCancelling: Boolean = (state as JobSupport.Finishing).isCancelling
               if (cause != null || !wasCancelling) {
                  var var32: java.lang.Throwable = (java.lang.Throwable)causeExceptionCache
                  if (causeExceptionCache == null) {
                     var32 = this.createCauseException(cause)
                  }

                  (state as JobSupport.Finishing).addExceptionLocked(var32)
               }

               var19 = if (!wasCancelling) (state as JobSupport.Finishing).rootCause else null
            }

            if (var19 != null) {
               this.notifyCancelling((state as JobSupport.Finishing).list, var19)
            }

            return JobSupportKt.access$getCOMPLETING_ALREADY$p()
         }

         if (state !is Incomplete) {
            return JobSupportKt.access$getTOO_LATE_TO_CANCEL$p()
         }

         var var10000: java.lang.Throwable = (java.lang.Throwable)causeExceptionCache
         if (causeExceptionCache == null) {
            val it: java.lang.Throwable = this.createCauseException(cause)
            causeExceptionCache = it
            var10000 = it
         }

         if ((state as Incomplete).isActive) {
            if (this.tryMakeCancelling(state as Incomplete, var10000)) {
               return JobSupportKt.access$getCOMPLETING_ALREADY$p()
            }
         } else {
            val finalState: Any = this.tryMakeCompleting(state, CompletedExceptionally(var10000, false, 2, null))
            if (finalState === JobSupportKt.access$getCOMPLETING_ALREADY$p()) {
               throw IllegalStateException(("Cannot happen in $state").toString())
            }

            if (finalState != JobSupportKt.access$getCOMPLETING_RETRY$p()) {
               return finalState
            }
         }
      }
   }

   private fun getOrPromoteCancellingList(state: Incomplete): NodeList? {
      var var10000: NodeList = state.list
      if (var10000 == null) {
         if (state is Empty) {
            var10000 = NodeList()
         } else {
            if (state !is JobNode) {
               throw IllegalStateException(("State should have list: $state").toString())
            }

            this.promoteSingleToNodeList(state as JobNode)
            var10000 = null
         }
      }

      return var10000
   }

   private fun tryMakeCancelling(state: Incomplete, rootCause: Throwable): Boolean {
      if (ASSERTIONS_ENABLED && state is JobSupport.Finishing) {
         throw AssertionError()
      } else if (ASSERTIONS_ENABLED && !state.isActive) {
         throw AssertionError()
      } else {
         val var10000: NodeList = this.getOrPromoteCancellingList(state)
         if (var10000 == null) {
            return false
         } else if (!_state$FU.compareAndSet(this, state, JobSupport.Finishing(var10000, false, rootCause))) {
            return false
         } else {
            this.notifyCancelling(var10000, rootCause)
            return true
         }
      }
   }

   internal fun makeCompleting(proposedUpdate: Any?): Boolean {
      val `this_$iv`: JobSupport = this

      val finalState: Any
      do {
         finalState = this.tryMakeCompleting(`this_$iv`.state, proposedUpdate)
         if (finalState === JobSupportKt.access$getCOMPLETING_ALREADY$p()) {
            return false
         }

         if (finalState === JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            return true
         }
      } while (finalState === JobSupportKt.access$getCOMPLETING_RETRY$p())

      this.afterCompletion(finalState)
      return true
   }

   internal fun makeCompletingOnce(proposedUpdate: Any?): Any? {
      val `this_$iv`: JobSupport = this

      val finalState: Any
      do {
         finalState = this.tryMakeCompleting(`this_$iv`.state, proposedUpdate)
         if (finalState === JobSupportKt.access$getCOMPLETING_ALREADY$p()) {
            throw IllegalStateException("Job $this is already complete or completing, but is being completed with $proposedUpdate", this.exceptionOrNull)
         }
      } while (finalState === JobSupportKt.access$getCOMPLETING_RETRY$p())

      return finalState
   }

   private fun tryMakeCompleting(state: Any?, proposedUpdate: Any?): Any? {
      if (state !is Incomplete) {
         return JobSupportKt.access$getCOMPLETING_ALREADY$p()
      } else if ((state is Empty || state is JobNode) && state !is ChildHandleNode && proposedUpdate !is CompletedExceptionally) {
         return if (this.tryFinalizeSimpleState(state as Incomplete, proposedUpdate)) proposedUpdate else JobSupportKt.access$getCOMPLETING_RETRY$p()
      } else {
         return this.tryMakeCompletingSlowPath(state as Incomplete, proposedUpdate)
      }
   }

   private fun tryMakeCompletingSlowPath(state: Incomplete, proposedUpdate: Any?): Any? {
      val var10000: NodeList = this.getOrPromoteCancellingList(state)
      if (var10000 == null) {
         return JobSupportKt.access$getCOMPLETING_RETRY$p()
      } else {
         var var25: JobSupport.Finishing = state as? JobSupport.Finishing
         if ((state as? JobSupport.Finishing) == null) {
            var25 = JobSupport.Finishing(var10000, false, null)
         }

         var var18: Symbol
         run label117@{
            val finishing: JobSupport.Finishing = var25
            val notifyRootCause: ObjectRef = ObjectRef()
            synchronized (var25) {
               if (finishing.isCompleting) {
                  var18 = JobSupportKt.access$getCOMPLETING_ALREADY$p()
                  return@label117
               }

               finishing.isCompleting = true
               if (finishing != state && !_state$FU.compareAndSet(this, state, finishing)) {
                  return JobSupportKt.access$getCOMPLETING_RETRY$p()
               }

               if (ASSERTIONS_ENABLED && finishing.isSealed) {
                  throw AssertionError()
               }

               val var23: Boolean = finishing.isCancelling
               val var26: CompletedExceptionally = proposedUpdate as? CompletedExceptionally
               if ((proposedUpdate as? CompletedExceptionally) != null) {
                  finishing.addExceptionLocked(var26.cause)
               }

               notifyRootCause.element = if (java.lang.Boolean.valueOf(!var23)) finishing.rootCause else null
            }

            val var27: java.lang.Throwable = notifyRootCause.element as java.lang.Throwable
            if (notifyRootCause.element as java.lang.Throwable != null) {
               this.notifyCancelling(var10000, var27)
            }

            val var20: ChildHandleNode = this.firstChild(state)
            if (var20 != null && this.tryWaitForChild(var25, var20, proposedUpdate)) {
               return JobSupportKt.COMPLETING_WAITING_CHILDREN
            }

            return this.finalizeFinishingState(var25, proposedUpdate)
         }

         // $VF: monitorexit
         return var18
      }
   }

   private final val exceptionOrNull: Throwable?
      private final get() {
         return if ((`$this$exceptionOrNull` as? CompletedExceptionally) != null) (`$this$exceptionOrNull` as? CompletedExceptionally).cause else null
      }


   private fun firstChild(state: Incomplete): ChildHandleNode? {
      var var10000: ChildHandleNode = state as? ChildHandleNode
      if ((state as? ChildHandleNode) == null) {
         val var2: NodeList = state.list
         var10000 = if (var2 != null) this.nextChild(var2) else null
      }

      return var10000
   }

   private tailrec fun tryWaitForChild(state: kotlinx.coroutines.JobSupport.Finishing, child: ChildHandleNode, proposedUpdate: Any?): Boolean {
      while (true) {
         if (Job.DefaultImpls.invokeOnCompletion$default(child.childJob, false, false, JobSupport.ChildCompletion(this, state, child, proposedUpdate), 1, null)
            != NonDisposableHandle.INSTANCE) {
            return true
         }

         val var11: ChildHandleNode = this.nextChild(child)
         if (var11 == null) {
            return false
         }

         this = this
         state = state
         child = var11
         proposedUpdate = proposedUpdate
      }
   }

   private fun continueCompleting(state: kotlinx.coroutines.JobSupport.Finishing, lastChild: ChildHandleNode, proposedUpdate: Any?) {
      if (ASSERTIONS_ENABLED && this.state != state) {
         throw AssertionError()
      } else {
         val var6: ChildHandleNode = this.nextChild(lastChild)
         if (var6 == null || !this.tryWaitForChild(state, var6, proposedUpdate)) {
            this.afterCompletion(this.finalizeFinishingState(state, proposedUpdate))
         }
      }
   }

   private fun LockFreeLinkedListNode.nextChild(): ChildHandleNode? {
      var cur: LockFreeLinkedListNode = `$this$nextChild`

      while (cur.isRemoved) {
         cur = cur.prevNode
      }

      while (true) {
         cur = cur.nextNode
         if (!cur.isRemoved) {
            if (cur is ChildHandleNode) {
               return cur as ChildHandleNode
            }

            if (cur is NodeList) {
               return null
            }
         }
      }
   }

   public final val children: Sequence<Job>
      public final get() {
         return SequencesKt.sequence({
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as Function2)
      }


   public override fun attachChild(child: ChildJob): ChildHandle {
      val var4: DisposableHandle = Job.DefaultImpls.invokeOnCompletion$default(this, true, false, ChildHandleNode(child), 2, null)
      return var4 as ChildHandle
   }

   internal open fun handleOnCompletionException(exception: Throwable) {
      throw exception
   }

   protected open fun onCancelling(cause: Throwable?) {
   }

   protected open val isScopedCoroutine: Boolean
      protected open get() {
         return false
      }


   internal open val handlesException: Boolean
      internal open get() {
         return true
      }


   protected open fun handleJobException(exception: Throwable): Boolean {
      return false
   }

   protected open fun onCompletionInternal(state: Any?) {
   }

   protected open fun afterCompletion(state: Any?) {
   }

   public override fun toString(): String {
      return "${this.toDebugString()}@${hexAddress}"
   }

   @InternalCoroutinesApi
   public fun toDebugString(): String {
      return "${this.nameString$kotlinx_coroutines_core()}{${this.stateString(this.state)}}"
   }

   internal open fun nameString(): String {
      return classSimpleName
   }

   private fun stateString(state: Any?): String {
      return if (state is JobSupport.Finishing)
         (if ((state as JobSupport.Finishing).isCancelling) "Cancelling" else (if ((state as JobSupport.Finishing).isCompleting) "Completing" else "Active"))
         else
         (
            if (state is Incomplete)
               (if ((state as Incomplete).isActive) "Active" else "New")
               else
               (if (state is CompletedExceptionally) "Cancelled" else "Completed")
         )
      }

   private final val isCancelling: Boolean
      private final get() {
         return `$this$isCancelling` is JobSupport.Finishing && (`$this$isCancelling` as JobSupport.Finishing).isCancelling
      }


   public final val isCompletedExceptionally: Boolean
      public final get() {
         return this.state is CompletedExceptionally
      }


   public fun getCompletionExceptionOrNull(): Throwable? {
      val state: Any = this.state
      if (state is Incomplete) {
         throw IllegalStateException("This job has not completed yet".toString())
      } else {
         return this.exceptionOrNull
      }
   }

   internal fun getCompletedInternal(): Any? {
      val state: Any = this.state
      if (state is Incomplete) {
         throw IllegalStateException("This job has not completed yet".toString())
      } else if (state is CompletedExceptionally) {
         throw (state as CompletedExceptionally).cause
      } else {
         return JobSupportKt.unboxState(state)
      }
   }

   protected suspend fun awaitInternal(): Any? {
      val state: Any
      do {
         state = this.state
         if (state !is Incomplete) {
            if (state is CompletedExceptionally) {
               val `exception$iv`: java.lang.Throwable = (state as CompletedExceptionally).cause
               if (!RECOVER_STACK_TRACES) {
                  throw `exception$iv`
               }

               if (`$completion` !is CoroutineStackFrame) {
                  throw `exception$iv`
               }

               throw StackTraceRecoveryKt.access$recoverFromStackFrame(`exception$iv`, `$completion` as CoroutineStackFrame)
            }

            return JobSupportKt.unboxState(state)
         }
      } while (this.startInternal(state) < 0)

      return this.awaitSuspend(`$completion`)
   }

   private suspend fun awaitSuspend(): Any? {
      val cont: JobSupport.AwaitContinuation = JobSupport.AwaitContinuation(IntrinsicsKt.intercepted(`$completion`), this)
      cont.initCancellability()
      CancellableContinuationKt.disposeOnCancellation(cont, this.invokeOnCompletion(ResumeAwaitOnCompletion(cont)))
      val var7: Any = cont.getResult()
      if (var7 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return var7
   }

   protected final val onAwaitInternal: SelectClause1<*>
      protected final get() {
         var var10003: Function3 = <unrepresentable>.INSTANCE
         var10003 = TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10003, 3) as Function3
         val var10004: <unrepresentable> = <unrepresentable>.INSTANCE
         return SelectClause1Impl(this, var10003, TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10004, 3) as Function3, null, 8, null)
      }


   private fun onAwaitInternalRegFunc(select: SelectInstance<*>, ignoredParam: Any?) {
      val disposableHandle: Any
      do {
         disposableHandle = this.state
         if (disposableHandle !is Incomplete) {
            select.selectInRegistrationPhase(if (disposableHandle is CompletedExceptionally) disposableHandle else JobSupportKt.unboxState(disposableHandle))
            return
         }
      } while (this.startInternal(disposableHandle) < 0)

      select.disposeOnCompletion(this.invokeOnCompletion(JobSupport.SelectOnAwaitCompletionHandler(select)))
   }

   private fun onAwaitInternalProcessResFunc(ignoredParam: Any?, result: Any?): Any? {
      if (result is CompletedExceptionally) {
         throw (result as CompletedExceptionally).cause
      } else {
         return result
      }
   }

   /** @deprecated */
   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   override fun cancel() {
      Job.DefaultImpls.cancel(this)
   }

   /** @deprecated */
   @Deprecated(message = "Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.", level = DeprecationLevel.ERROR)
   override fun plus(other: Job): Job {
      Job.DefaultImpls.plus(this, other)
   }

   open fun plus(context: CoroutineContext): CoroutineContext {
      Job.DefaultImpls.plus(this, context)
   }

   open fun <R> fold(initial: R, operation: (R?, Element?) -> R): R {
      Job.DefaultImpls.fold(this, initial, operation)
   }

   open fun <E extends Element> get(key: kotlin.coroutines.CoroutineContext.Key<E>): E? {
      Job.DefaultImpls.get(this, key)
   }

   open fun minusKey(key: kotlin.coroutines.CoroutineContext.Key<*>): CoroutineContext {
      Job.DefaultImpls.minusKey(this, key)
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$joinSuspend`(`$this`: JobSupport, `$completion`: Continuation) {
      `$this`.joinSuspend(`$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$cancellationExceptionMessage`(`$this`: JobSupport): java.lang.String {
      `$this`.cancellationExceptionMessage()
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$awaitSuspend`(`$this`: JobSupport, `$completion`: Continuation) {
      `$this`.awaitSuspend(`$completion`)
   }

   @SourceDebugExtension(["SMAP\nJobSupport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JobSupport.kt\nkotlinx/coroutines/JobSupport$AwaitContinuation\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1454:1\n1#2:1455\n*E\n"])
   private class AwaitContinuation<T>(delegate: Continuation<Any>, job: JobSupport) : CancellableContinuationImpl(delegate, 1) {
      private final val job: JobSupport

      init {
         this.job = job
      }

      public override fun getContinuationCancellationCause(parent: Job): Throwable {
         val state: Any = this.job.state
         if (state is JobSupport.Finishing) {
            val var3: java.lang.Throwable = (state as JobSupport.Finishing).rootCause
            if (var3 != null) {
               return var3
            }
         }

         return if (state is CompletedExceptionally) (state as CompletedExceptionally).cause else parent.getCancellationException()
      }

      protected override fun nameString(): String {
         return "AwaitContinuation"
      }
   }

   private class ChildCompletion(parent: JobSupport, state: kotlinx.coroutines.JobSupport.Finishing, child: ChildHandleNode, proposedUpdate: Any?) : JobNode {
      private final val parent: JobSupport
      private final val state: kotlinx.coroutines.JobSupport.Finishing
      private final val child: ChildHandleNode
      private final val proposedUpdate: Any?

      init {
         this.parent = parent
         this.state = state
         this.child = child
         this.proposedUpdate = proposedUpdate
      }

      public override operator fun invoke(cause: Throwable?) {
         this.parent.continueCompleting(this.state, this.child, this.proposedUpdate)
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      fun invoke(p1: Any) {
         this.invoke(p1 as java.lang.Throwable)
         Unit.INSTANCE
      }
   }

   @SourceDebugExtension(["SMAP\nJobSupport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JobSupport.kt\nkotlinx/coroutines/JobSupport$Finishing\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1454:1\n1#2:1455\n*E\n"])
   private class Finishing(list: NodeList, isCompleting: Boolean, rootCause: Throwable?) : Incomplete {
      public open val list: NodeList
      @Volatile
      @Volatile
      private int _isCompleting;
      @Volatile
      @Volatile
      private Object _rootCause;
      @Volatile
      @Volatile
      private Object _exceptionsHolder;
      @JvmStatic
      private AtomicIntegerFieldUpdater _isCompleting$FU = AtomicIntegerFieldUpdater.newUpdater(JobSupport.Finishing.class, "_isCompleting");
      @JvmStatic
      private AtomicReferenceFieldUpdater _rootCause$FU = AtomicReferenceFieldUpdater.newUpdater(JobSupport.Finishing.class, Object.class, "_rootCause");
      @JvmStatic
      private AtomicReferenceFieldUpdater _exceptionsHolder$FU = AtomicReferenceFieldUpdater.newUpdater(
         JobSupport.Finishing.class, Object.class, "_exceptionsHolder"
      );

      init {
         this.list = list
         this._isCompleting = isCompleting
         this._rootCause = rootCause
      }

      public final var isCompleting: Boolean
         public final get() {
            return _isCompleting$FU.get(this) != 0
         }

         public final set(value) {
            _isCompleting$FU.set(this, value)
         }


      public final var rootCause: Throwable?
         public final get() {
            return _rootCause$FU.get(this) as java.lang.Throwable
         }

         public final set(value) {
            _rootCause$FU.set(this, value)
         }


      private final var exceptionsHolder: Any?
         private final get() {
            return _exceptionsHolder$FU.get(this)
         }

         private final set(value) {
            _exceptionsHolder$FU.set(this, value)
         }


      public final val isSealed: Boolean
         public final get() {
            return this.exceptionsHolder === JobSupportKt.access$getSEALED$p()
         }


      public final val isCancelling: Boolean
         public final get() {
            return this.rootCause != null
         }


      public open val isActive: Boolean
         public open get() {
            return this.rootCause == null
         }


      public fun sealLocked(proposedException: Throwable?): List<Throwable> {
         val rootCause: Any = this.exceptionsHolder
         val var10000: ArrayList
         if (rootCause == null) {
            var10000 = this.allocateList()
         } else if (rootCause is java.lang.Throwable) {
            val var4: ArrayList = this.allocateList()
            var4.add(rootCause)
            var10000 = var4
         } else {
            if (rootCause !is ArrayList) {
               throw IllegalStateException(("State is $rootCause").toString())
            }

            var10000 = rootCause as ArrayList
         }

         val var7: java.lang.Throwable = this.rootCause
         if (var7 != null) {
            var10000.add(0, var7)
         }

         if (proposedException != null && !(proposedException == var7)) {
            var10000.add(proposedException)
         }

         this.exceptionsHolder = JobSupportKt.access$getSEALED$p()
         return var10000
      }

      public fun addExceptionLocked(exception: Throwable) {
         val rootCause: java.lang.Throwable = this.rootCause
         if (rootCause == null) {
            this.rootCause = exception
         } else if (exception != rootCause) {
            val eh: Any = this.exceptionsHolder
            if (eh == null) {
               this.exceptionsHolder = exception
            } else if (eh is java.lang.Throwable) {
               if (exception === eh) {
                  return
               }

               val var4: ArrayList = this.allocateList()
               var4.add(eh)
               var4.add(exception)
               this.exceptionsHolder = var4
            } else {
               if (eh !is ArrayList) {
                  throw IllegalStateException(("State is $eh").toString())
               }

               (eh as ArrayList).add(exception)
            }
         }
      }

      private fun allocateList(): ArrayList<Throwable> {
         return ArrayList<>(4)
      }

      public override fun toString(): String {
         return "Finishing[cancelling=${this.isCancelling}, completing=${this.isCompleting}, rootCause=${this.rootCause}, exceptions=${this.exceptionsHolder}, list=${this.list}]"
      }
   }

   private inner class SelectOnAwaitCompletionHandler(select: SelectInstance<*>) : JobNode {
      private final val select: SelectInstance<*>

      init {
         this.select = select
      }

      public override operator fun invoke(cause: Throwable?) {
         val state: Any = JobSupport.this.state
         this.select.trySelect(JobSupport.this, if (state is CompletedExceptionally) state else JobSupportKt.unboxState(state))
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      fun invoke(p1: Any) {
         this.invoke(p1 as java.lang.Throwable)
         Unit.INSTANCE
      }
   }

   private inner class SelectOnJoinCompletionHandler(select: SelectInstance<*>) : JobNode {
      private final val select: SelectInstance<*>

      init {
         this.select = select
      }

      public override operator fun invoke(cause: Throwable?) {
         this.select.trySelect(JobSupport.this, Unit.INSTANCE)
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      fun invoke(p1: Any) {
         this.invoke(p1 as java.lang.Throwable)
         Unit.INSTANCE
      }
   }
}
