package kotlinx.coroutines.channels

import java.util.ArrayList
import java.util.NoSuchElementException
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicLongFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.internal.LowPriorityInOverloadResolution
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.functions.Function4
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.CancellableContinuationKt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Waiter
import kotlinx.coroutines.internal.ConcurrentLinkedListKt
import kotlinx.coroutines.internal.ConcurrentLinkedListNode
import kotlinx.coroutines.internal.InlineList
import kotlinx.coroutines.internal.OnUndeliveredElementKt
import kotlinx.coroutines.internal.Segment
import kotlinx.coroutines.internal.StackTraceRecoveryKt
import kotlinx.coroutines.internal.Symbol
import kotlinx.coroutines.internal.UndeliveredElementException
import kotlinx.coroutines.selects.SelectClause1
import kotlinx.coroutines.selects.SelectClause1Impl
import kotlinx.coroutines.selects.SelectClause2
import kotlinx.coroutines.selects.SelectClause2Impl
import kotlinx.coroutines.selects.SelectImplementation
import kotlinx.coroutines.selects.SelectInstance
import kotlinx.coroutines.selects.TrySelectDetailedResult
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nBufferedChannel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannelKt\n+ 4 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n+ 5 DispatchedTask.kt\nkotlinx/coroutines/DispatchedTaskKt\n+ 6 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n+ 7 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel$sendImpl$1\n+ 8 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel$receiveImpl$1\n+ 9 InlineList.kt\nkotlinx/coroutines/internal/InlineList\n+ 10 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/ConcurrentLinkedListKt\n+ 11 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,3055:1\n273#1,6:3058\n280#1,68:3065\n374#1,18:3156\n244#1:3174\n269#1,10:3175\n280#1,48:3186\n395#1:3234\n334#1,14:3235\n399#1,3:3250\n244#1:3263\n269#1,10:3264\n280#1,68:3275\n244#1:3353\n269#1,10:3354\n280#1,68:3365\n244#1:3437\n269#1,10:3438\n280#1,68:3449\n886#1,52:3519\n964#1,8:3575\n858#1:3583\n882#1,33:3584\n974#1:3617\n916#1,14:3618\n935#1,3:3633\n979#1,6:3636\n886#1,52:3650\n964#1,8:3706\n858#1:3714\n882#1,33:3715\n974#1:3748\n916#1,14:3749\n935#1,3:3764\n979#1,6:3767\n858#1:3782\n882#1,48:3783\n935#1,3:3832\n858#1:3835\n882#1,48:3836\n935#1,3:3885\n244#1:3897\n269#1,10:3898\n280#1,68:3909\n858#1:3978\n882#1,48:3979\n935#1,3:4028\n1#2:3056\n3038#3:3057\n3038#3:3064\n3038#3:3185\n3038#3:3274\n3038#3:3364\n3038#3:3436\n3038#3:3448\n3038#3:3518\n3038#3:3781\n3038#3:3888\n3038#3:3889\n3052#3:3890\n3052#3:3891\n3051#3:3892\n3051#3:3893\n3051#3:3894\n3052#3:3895\n3051#3:3896\n3038#3:3908\n3039#3:4031\n3038#3:4032\n3038#3:4033\n3038#3:4034\n3039#3:4035\n3038#3:4036\n3039#3:4059\n3038#3:4060\n3038#3:4061\n3039#3:4062\n3038#3:4112\n3039#3:4113\n3039#3:4114\n3039#3:4132\n3039#3:4133\n314#4,9:3133\n323#4,2:3150\n332#4,4:3152\n336#4,8:3253\n314#4,9:3344\n323#4,2:3434\n332#4,4:3571\n336#4,8:3642\n332#4,4:3702\n336#4,8:3773\n220#5:3142\n221#5:3145\n220#5:3146\n221#5:3149\n61#6,2:3143\n61#6,2:3147\n61#6,2:3261\n269#7:3249\n269#7:3343\n269#7:3433\n269#7:3517\n269#7:3977\n882#8:3632\n882#8:3763\n882#8:3831\n882#8:3884\n882#8:4027\n37#9,11:4037\n37#9,11:4048\n72#10,3:4063\n46#10,8:4066\n72#10,3:4074\n46#10,8:4077\n46#10,8:4085\n72#10,3:4093\n46#10,8:4096\n46#10,8:4104\n766#11:4115\n857#11,2:4116\n2310#11,14:4118\n766#11:4134\n857#11,2:4135\n2310#11,14:4137\n766#11:4151\n857#11,2:4152\n2310#11,14:4154\n*S KotlinDebug\n*F\n+ 1 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel\n*L\n113#1:3058,6\n113#1:3065,68\n154#1:3156,18\n154#1:3174\n154#1:3175,10\n154#1:3186,48\n154#1:3234\n154#1:3235,14\n154#1:3250,3\n194#1:3263\n194#1:3264,10\n194#1:3275,68\n225#1:3353\n225#1:3354,10\n225#1:3365,68\n391#1:3437\n391#1:3438,10\n391#1:3449,68\n667#1:3519,52\n696#1:3575,8\n696#1:3583\n696#1:3584,33\n696#1:3617\n696#1:3618,14\n696#1:3633,3\n696#1:3636,6\n732#1:3650,52\n748#1:3706,8\n748#1:3714\n748#1:3715,33\n748#1:3748\n748#1:3749,14\n748#1:3764,3\n748#1:3767,6\n781#1:3782\n781#1:3783,48\n781#1:3832,3\n971#1:3835\n971#1:3836,48\n971#1:3885,3\n1464#1:3897\n1464#1:3898,10\n1464#1:3909,68\n1512#1:3978\n1512#1:3979,48\n1512#1:4028,3\n70#1:3057\n113#1:3064\n154#1:3185\n194#1:3274\n225#1:3364\n278#1:3436\n391#1:3448\n606#1:3518\n771#1:3781\n1007#1:3888\n1056#1:3889\n1374#1:3890\n1376#1:3891\n1406#1:3892\n1416#1:3893\n1425#1:3894\n1426#1:3895\n1433#1:3896\n1464#1:3908\n1865#1:4031\n1867#1:4032\n1869#1:4033\n1882#1:4034\n1893#1:4035\n1894#1:4036\n2196#1:4059\n2209#1:4060\n2219#1:4061\n2222#1:4062\n2539#1:4112\n2541#1:4113\n2566#1:4114\n2628#1:4132\n2629#1:4133\n134#1:3133,9\n134#1:3150,2\n153#1:3152,4\n153#1:3253,8\n221#1:3344,9\n221#1:3434,2\n695#1:3571,4\n695#1:3642,8\n746#1:3702,4\n746#1:3773,8\n138#1:3142\n138#1:3145\n141#1:3146\n141#1:3149\n138#1:3143,2\n141#1:3147,2\n183#1:3261,2\n154#1:3249\n194#1:3343\n225#1:3433\n391#1:3517\n1464#1:3977\n696#1:3632\n748#1:3763\n781#1:3831\n971#1:3884\n1512#1:4027\n2098#1:4037,11\n2153#1:4048,11\n2361#1:4063,3\n2361#1:4066,8\n2416#1:4074,3\n2416#1:4077,8\n2435#1:4085,8\n2465#1:4093,3\n2465#1:4096,8\n2526#1:4104,8\n2575#1:4115\n2575#1:4116,2\n2576#1:4118,14\n2640#1:4134\n2640#1:4135,2\n2641#1:4137,14\n2681#1:4151\n2681#1:4152,2\n2682#1:4154,14\n*E\n"])
internal open class BufferedChannel<E>(capacity: Int, onUndeliveredElement: ((Any) -> Unit)? = null) : Channel<E> {
   private final val capacity: Int

   @JvmField
   @Nullable
   internal final val onUndeliveredElement: ((Any) -> Unit)?

   @Volatile
   @Volatile
   private long sendersAndCloseStatus;
   @Volatile
   @Volatile
   private long receivers;
   @Volatile
   @Volatile
   private long bufferEnd;
   @Volatile
   @Volatile
   private long completedExpandBuffersAndPauseFlag;
   @Volatile
   @Volatile
   private Object sendSegment;
   @Volatile
   @Volatile
   private Object receiveSegment;
   @Volatile
   @Volatile
   private Object bufferEndSegment;
   private final val onUndeliveredElementReceiveCancellationConstructor: ((SelectInstance<*>, Any?, Any?) -> (Throwable) -> Unit)?
   @Volatile
   @Volatile
   private Object _closeCause;
   @Volatile
   @Volatile
   private Object closeHandler;
   @JvmStatic
   private AtomicLongFieldUpdater sendersAndCloseStatus$FU = AtomicLongFieldUpdater.newUpdater(BufferedChannel.class, "sendersAndCloseStatus");
   @JvmStatic
   private AtomicLongFieldUpdater receivers$FU = AtomicLongFieldUpdater.newUpdater(BufferedChannel.class, "receivers");
   @JvmStatic
   private AtomicLongFieldUpdater bufferEnd$FU = AtomicLongFieldUpdater.newUpdater(BufferedChannel.class, "bufferEnd");
   @JvmStatic
   private AtomicLongFieldUpdater completedExpandBuffersAndPauseFlag$FU = AtomicLongFieldUpdater.newUpdater(
      BufferedChannel.class, "completedExpandBuffersAndPauseFlag"
   );
   @JvmStatic
   private AtomicReferenceFieldUpdater sendSegment$FU = AtomicReferenceFieldUpdater.newUpdater(BufferedChannel.class, Object.class, "sendSegment");
   @JvmStatic
   private AtomicReferenceFieldUpdater receiveSegment$FU = AtomicReferenceFieldUpdater.newUpdater(BufferedChannel.class, Object.class, "receiveSegment");
   @JvmStatic
   private AtomicReferenceFieldUpdater bufferEndSegment$FU = AtomicReferenceFieldUpdater.newUpdater(BufferedChannel.class, Object.class, "bufferEndSegment");
   @JvmStatic
   private AtomicReferenceFieldUpdater _closeCause$FU = AtomicReferenceFieldUpdater.newUpdater(BufferedChannel.class, Object.class, "_closeCause");
   @JvmStatic
   private AtomicReferenceFieldUpdater closeHandler$FU = AtomicReferenceFieldUpdater.newUpdater(BufferedChannel.class, Object.class, "closeHandler");

   init {
      this.capacity = capacity
      this.onUndeliveredElement = onUndeliveredElement
      if (this.capacity < 0) {
         throw IllegalArgumentException(("Invalid channel capacity: ${this.capacity}, should be >=0").toString())
      } else {
         this.bufferEnd = BufferedChannelKt.access$initialBufferEnd(this.capacity)
         this.completedExpandBuffersAndPauseFlag = this.bufferEndCounter
         val firstSegment: ChannelSegment = ChannelSegment<>(0L, null, this, 3)
         this.sendSegment = firstSegment
         this.receiveSegment = firstSegment
         val var10001: ChannelSegment
         if (this.isRendezvousOrUnlimited) {
            var10001 = BufferedChannelKt.access$getNULL_SEGMENT$p()
         } else {
            var10001 = firstSegment
         }

         this.bufferEndSegment = var10001
         var var10000: BufferedChannel = this
         val var9: Function3
         if (this.onUndeliveredElement != null) {
            val it: Function1 = this.onUndeliveredElement
            var9 = { select: SelectInstance<*>, <anonymous parameter 1>: Any?, element: Any? ->
               return { it: Throwable ->
                  if (element != CHANNEL_CLOSED) {
                     OnUndeliveredElementKt.callUndeliveredElement(var4/* $VF was: BufferedChannel.this */.onUndeliveredElement, element, select.context)
                  }
               } as (java.lang.Throwable?) -> Unit
            } as Function3
            var10000 = this
         } else {
            var9 = null
         }

         var10000.onUndeliveredElementReceiveCancellationConstructor = var9
         this._closeCause = BufferedChannelKt.access$getNO_CLOSE_CAUSE$p()
      }
   }

   internal final val sendersCounter: Long
      internal final get() {
         return sendersAndCloseStatus$FU.get(this) and 1152921504606846975L
      }


   internal final val receiversCounter: Long
      internal final get() {
         return receivers$FU.get(this)
      }


   private final val bufferEndCounter: Long
      private final get() {
         return bufferEnd$FU.get(this)
      }


   private final val isRendezvousOrUnlimited: Boolean
      private final get() {
         val it: Long = this.bufferEndCounter
         return it == 0L || it == java.lang.Long.MAX_VALUE
      }


   public override suspend fun send(element: Any) {
      return send$suspendImpl(this, (E)element, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <E> `send$suspendImpl`(`$this`: BufferedChannel<E>, element: E, `$completion`: Continuation<in Unit>) {
      val `this_$iv`: BufferedChannel = `$this`
      val `waiter$iv`: Any = null
      var `segment$iv`: ChannelSegment = access$getSendSegment$FU$p().get(`$this`) as ChannelSegment

      while (true) {
         val `sendersAndCloseStatusCur$iv`: Long = access$getSendersAndCloseStatus$FU$p().getAndIncrement(`this_$iv`)
         val `s$iv`: Long = `sendersAndCloseStatusCur$iv` and 1152921504606846975L
         val `closed$iv`: Boolean = access$isClosedForSend0(`this_$iv`, `sendersAndCloseStatusCur$iv`)
         val `id$iv`: Long = `s$iv` / BufferedChannelKt.SEGMENT_SIZE
         val `i$iv`: Int = (int)(`s$iv` % BufferedChannelKt.SEGMENT_SIZE)
         if (`segment$iv`.id != `id$iv`) {
            val var10000: ChannelSegment = access$findSegmentSend(`this_$iv`, `id$iv`, `segment$iv`)
            if (var10000 == null) {
               if (!`closed$iv`) {
                  Unit.INSTANCE
               }

               val var34: Any = `$this`.onClosedSend(element, `$completion`)
               if (var34 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                  var34
               }

               Unit.INSTANCE
            }

            `segment$iv` = var10000
         }

         when (access$updateCellSend(`this_$iv`, `segment$iv`, `i$iv`, element, `s$iv`, `waiter$iv`, `closed$iv`)) {
            0 -> {
               `segment$iv`.cleanPrev()
               Unit.INSTANCE
            }
            1 -> Unit.INSTANCE
            2 -> {
               if (`closed$iv`) {
                  `segment$iv`.onSlotCleaned()
                  val var33: Any = `$this`.onClosedSend(element, `$completion`)
                  if (var33 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                     var33
                  }
               } else if (ASSERTIONS_ENABLED) {
                  if (true) {
                     throw AssertionError()
                  }

                  Unit.INSTANCE
               }

               Unit.INSTANCE
            }
            3 -> {
               val var32: Any = `$this`.sendOnNoWaiterSuspend(`segment$iv`, `i$iv`, element, `s$iv`, `$completion`)
               if (var32 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                  var32
               }

               Unit.INSTANCE
            }
            4 -> {
               if (`s$iv` < `this_$iv`.receiversCounter) {
                  `segment$iv`.cleanPrev()
               }

               val var31: Any = `$this`.onClosedSend(element, `$completion`)
               if (var31 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                  var31
               }

               Unit.INSTANCE
            }
            5 -> `segment$iv`.cleanPrev()
            else -> {}
         }
      }
   }

   private suspend fun onClosedSend(element: Any) {
      var `cancellable$iv`: CancellableContinuationImpl
      run label49@{
         `cancellable$iv` = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
         `cancellable$iv`.initCancellability()
         val continuation: CancellableContinuation = `cancellable$iv`
         if (this.onUndeliveredElement != null) {
            val var10000: UndeliveredElementException = OnUndeliveredElementKt.callUndeliveredElementCatchingException$default(
               this.onUndeliveredElement, element, null, 2, null
            )
            if (var10000 != null) {
               ExceptionsKt.addSuppressed(var10000, this.sendException)
               continuation.resumeWith(
                  Result.constructor_impl/* $VF was: constructor-impl */(
                     ResultKt.createFailure(
                        if (RECOVER_STACK_TRACES && continuation as Continuation is CoroutineStackFrame)
                           StackTraceRecoveryKt.access$recoverFromStackFrame(var10000, continuation as CoroutineStackFrame)
                           else
                           var10000
                     )
                  )
               )
               return@label49
            }
         }

         val `$this$resumeWithStackTrace$iv`: Continuation = continuation
         val `exception$iv`: java.lang.Throwable = this.sendException
         `$this$resumeWithStackTrace$iv`.resumeWith(
            Result.constructor_impl/* $VF was: constructor-impl */(
               ResultKt.createFailure(
                  if (RECOVER_STACK_TRACES && `$this$resumeWithStackTrace$iv` is CoroutineStackFrame)
                     StackTraceRecoveryKt.access$recoverFromStackFrame(`exception$iv`, `$this$resumeWithStackTrace$iv` as CoroutineStackFrame)
                     else
                     `exception$iv`
               )
            )
         )
      }

      val var18: Any = `cancellable$iv`.getResult()
      if (var18 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return if (var18 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var18 else Unit.INSTANCE
   }

   private suspend fun sendOnNoWaiterSuspend(segment: ChannelSegment<Any>, index: Int, element: Any, s: Long) {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(`$completion`))

      try {
         run label99@{
            val cont: CancellableContinuationImpl = `cancellable$iv`
val `this_$iv`: BufferedChannel = this
var `segment$iv$iv`: ChannelSegment
            when (access$updateCellSend(this, segment, index, element, s, `cancellable$iv`, false)) {
               0 -> {
                  segment.cleanPrev()
                  `cancellable$iv`.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
                  return@label99
               }
               1 -> {
                  `cancellable$iv`.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
                  return@label99
               }
               2 -> {
                  access$prepareSenderForSuspension(this, `cancellable$iv`, segment, index)
                  return@label99
               }
               3 -> throw IllegalStateException("unexpected".toString())
               4 -> {
                  if (s < this.receiversCounter) {
                     segment.cleanPrev()
                  }

                  access$onClosedSendOnNoWaiterSuspend(this, element, `cancellable$iv`)
                  return@label99
               }
               5 -> {
                  segment.cleanPrev()
                  `segment$iv$iv` = access$getSendSegment$FU$p().get(this) as ChannelSegment
               }
               else -> throw IllegalStateException("unexpected".toString())
            }

            label98@ while (true) {
               val `sendersAndCloseStatusCur$iv$iv`: Long = access$getSendersAndCloseStatus$FU$p().getAndIncrement(`this_$iv`)
               val `s$iv$iv`: Long = `sendersAndCloseStatusCur$iv$iv` and 1152921504606846975L
               val `closed$iv$iv`: Boolean = access$isClosedForSend0(`this_$iv`, `sendersAndCloseStatusCur$iv$iv`)
               val `id$iv$iv`: Long = `s$iv$iv` / BufferedChannelKt.SEGMENT_SIZE
               val `i$iv$iv`: Int = (int)(`s$iv$iv` % BufferedChannelKt.SEGMENT_SIZE)
               if (`segment$iv$iv`.id != `id$iv$iv`) {
                  val var10000: ChannelSegment = access$findSegmentSend(`this_$iv`, `id$iv$iv`, `segment$iv$iv`)
                  if (var10000 == null) {
                     if (`closed$iv$iv`) {
                        access$onClosedSendOnNoWaiterSuspend(this, element, cont)
                        break
                     }
                     continue
                  }

                  `segment$iv$iv` = var10000
               }

               when (access$updateCellSend(`this_$iv`, `segment$iv$iv`, `i$iv$iv`, element, `s$iv$iv`, cont, `closed$iv$iv`)) {
                  0 -> {
                     `segment$iv$iv`.cleanPrev()
                     cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
                     break@label98
                  }
                  1 -> {
                     cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
                     break@label98
                  }
                  2 -> {
                     if (`closed$iv$iv`) {
                        `segment$iv$iv`.onSlotCleaned()
                        access$onClosedSendOnNoWaiterSuspend(this, element, cont)
                     } else {
                        val var42: Waiter = cont as? Waiter
                        if ((cont as? Waiter) != null) {
                           access$prepareSenderForSuspension(`this_$iv`, var42, `segment$iv$iv`, `i$iv$iv`)
                        }
                     }
                     break@label98
                  }
                  3 -> throw IllegalStateException("unexpected".toString())
                  4 -> {
                     if (`s$iv$iv` < `this_$iv`.receiversCounter) {
                        `segment$iv$iv`.cleanPrev()
                     }

                     access$onClosedSendOnNoWaiterSuspend(this, element, cont)
                     break@label98
                  }
                  5 -> `segment$iv$iv`.cleanPrev()
                  else -> {}
               }
            }
         }
      } catch (var34: java.lang.Throwable) {
         `cancellable$iv`.releaseClaimedReusableContinuation$kotlinx_coroutines_core()
         throw var34
      }

      val var43: Any = `cancellable$iv`.getResult()
      if (var43 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return if (var43 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var43 else Unit.INSTANCE
   }

   private fun Waiter.prepareSenderForSuspension(segment: ChannelSegment<Any>, index: Int) {
      `$this$prepareSenderForSuspension`.invokeOnCancellation(segment, index + BufferedChannelKt.SEGMENT_SIZE)
   }

   private fun onClosedSendOnNoWaiterSuspend(element: Any, cont: CancellableContinuation<Unit>) {
      if (this.onUndeliveredElement != null) {
         OnUndeliveredElementKt.callUndeliveredElement(this.onUndeliveredElement, element, cont.getContext())
      }

      val var3: Continuation = cont
      val `exception$iv`: java.lang.Throwable = this.sendException
      var3.resumeWith(
         Result.constructor_impl/* $VF was: constructor-impl */(
            ResultKt.createFailure(
               if (RECOVER_STACK_TRACES && cont as Continuation is CoroutineStackFrame)
                  StackTraceRecoveryKt.access$recoverFromStackFrame(`exception$iv`, cont as CoroutineStackFrame)
                  else
                  `exception$iv`
            )
         )
      )
   }

   public override fun trySend(element: Any): ChannelResult<Unit> {
      if (this.shouldSendSuspend(sendersAndCloseStatus$FU.get(this))) {
         return ChannelResult.Companion.failure_PtdJZtk/* $VF was: failure-PtdJZtk */()
      } else {
         val `waiter$iv`: Symbol = BufferedChannelKt.access$getINTERRUPTED_SEND$p()
         var `segment$iv`: ChannelSegment = access$getSendSegment$FU$p().get(this) as ChannelSegment

         while (true) {
            val `sendersAndCloseStatusCur$iv`: Long = access$getSendersAndCloseStatus$FU$p().getAndIncrement(this)
            val `s$iv`: Long = `sendersAndCloseStatusCur$iv` and 1152921504606846975L
            val `closed$iv`: Boolean = access$isClosedForSend0(this, `sendersAndCloseStatusCur$iv`)
            val `id$iv`: Long = `s$iv` / BufferedChannelKt.SEGMENT_SIZE
            val `i$iv`: Int = (int)(`s$iv` % BufferedChannelKt.SEGMENT_SIZE)
            if (`segment$iv`.id != `id$iv`) {
               val var10000: ChannelSegment = access$findSegmentSend(this, `id$iv`, `segment$iv`)
               if (var10000 == null) {
                  if (!`closed$iv`) {
                     return ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.sendException)
                  }

                  return ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.sendException)
               }

               `segment$iv` = var10000
            }

            when (access$updateCellSend(this, `segment$iv`, `i$iv`, element, `s$iv`, `waiter$iv`, `closed$iv`)) {
               0 -> {
                  `segment$iv`.cleanPrev()
                  return ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(Unit.INSTANCE)
               }
               1 -> return ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(Unit.INSTANCE)
               2 -> {
                  var var25: Any
                  if (`closed$iv`) {
                     `segment$iv`.onSlotCleaned()
                     var25 = (Waiter)ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.sendException)
                  } else {
                     var25 = `waiter$iv` as? Waiter
                     if ((`waiter$iv` as? Waiter) != null) {
                        access$prepareSenderForSuspension(this, var25, `segment$iv`, `i$iv`)
                     }

                     `segment$iv`.onSlotCleaned()
                     var25 = (Waiter)ChannelResult.Companion.failure_PtdJZtk/* $VF was: failure-PtdJZtk */()
                  }

                  return var25
               }
               3 -> throw IllegalStateException("unexpected".toString())
               4 -> {
                  if (`s$iv` < this.receiversCounter) {
                     `segment$iv`.cleanPrev()
                  }

                  return ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.sendException)
               }
               5 -> `segment$iv`.cleanPrev()
               else -> {}
            }
         }
      }
   }

   internal open suspend fun sendBroadcast(element: Any): Boolean {
      return sendBroadcast$suspendImpl(this, (E)element, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <E> `sendBroadcast$suspendImpl`(`$this`: BufferedChannel<E>, element: E, `$completion`: Continuation<? super java.lang.Boolean>) {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
      `cancellable$iv`.initCancellability()
      val cont: CancellableContinuation = `cancellable$iv`
      if (`$this`.onUndeliveredElement != null) {
         throw IllegalStateException("the `onUndeliveredElement` feature is unsupported for `sendBroadcast(e)`".toString())
      } else {
         val `waiter$iv`: BufferedChannel.SendBroadcast = BufferedChannel.SendBroadcast(cont)
         var `segment$iv`: ChannelSegment = access$getSendSegment$FU$p().get(`$this`) as ChannelSegment

         label79@ while (true) {
            val `sendersAndCloseStatusCur$iv`: Long = access$getSendersAndCloseStatus$FU$p().getAndIncrement(`$this`)
            val `s$iv`: Long = `sendersAndCloseStatusCur$iv` and 1152921504606846975L
            val `closed$iv`: Boolean = access$isClosedForSend0(`$this`, `sendersAndCloseStatusCur$iv`)
            val `id$iv`: Long = `s$iv` / BufferedChannelKt.SEGMENT_SIZE
            val `i$iv`: Int = (int)(`s$iv` % BufferedChannelKt.SEGMENT_SIZE)
            if (`segment$iv`.id != `id$iv`) {
               val var10000: ChannelSegment = access$findSegmentSend(`$this`, `id$iv`, `segment$iv`)
               if (var10000 == null) {
                  if (!`closed$iv`) {
                     continue
                  }

                  cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Boxing.boxBoolean(false)))
                  break
               }

               `segment$iv` = var10000
            }

            when (access$updateCellSend(`$this`, `segment$iv`, `i$iv`, element, `s$iv`, `waiter$iv`, `closed$iv`)) {
               0 -> {
                  `segment$iv`.cleanPrev()
                  cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Boxing.boxBoolean(true)))
                  break@label79
               }
               1 -> {
                  cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Boxing.boxBoolean(true)))
                  break@label79
               }
               2 -> {
                  if (`closed$iv`) {
                     `segment$iv`.onSlotCleaned()
                     cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Boxing.boxBoolean(false)))
                  } else {
                     val var33: Waiter = `waiter$iv` as? Waiter
                     if ((`waiter$iv` as? Waiter) != null) {
                        access$prepareSenderForSuspension(`$this`, var33, `segment$iv`, `i$iv`)
                     }
                  }
                  break@label79
               }
               3 -> throw IllegalStateException("unexpected".toString())
               4 -> {
                  if (`s$iv` < `$this`.receiversCounter) {
                     `segment$iv`.cleanPrev()
                  }

                  cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Boxing.boxBoolean(false)))
                  break@label79
               }
               5 -> `segment$iv`.cleanPrev()
               else -> {}
            }
         }

         val var34: Any = `cancellable$iv`.getResult()
         if (var34 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(`$completion`)
         }

         var34
      }
   }

   protected inline fun <R> sendImpl(
      element: Any,
      waiter: Any?,
      onRendezvousOrBuffered: () -> Any,
      onSuspend: (ChannelSegment<Any>, Int) -> Any,
      onClosed: () -> Any,
      onNoWaiterSuspend: (ChannelSegment<Any>, Int, Any, Long) -> Any = { <anonymous parameter 0>, <anonymous parameter 1>, <anonymous parameter 2>, <anonymous parameter 3> ->
            throw IllegalStateException("unexpected".toString())
         } as Function4
   ): Any {
      var segment: ChannelSegment = access$getSendSegment$FU$p().get(this) as ChannelSegment

      while (true) {
         val sendersAndCloseStatusCur: Long = access$getSendersAndCloseStatus$FU$p().getAndIncrement(this)
         val s: Long = sendersAndCloseStatusCur and 1152921504606846975L
         val var17: Boolean = access$isClosedForSend0(this, sendersAndCloseStatusCur)
         val id: Long = s / BufferedChannelKt.SEGMENT_SIZE
         val i: Int = (int)(s % BufferedChannelKt.SEGMENT_SIZE)
         if (segment.id != id) {
            val var10000: ChannelSegment = access$findSegmentSend(this, id, segment)
            if (var10000 == null) {
               if (var17) {
                  return (R)onClosed()
               }
               continue
            }

            segment = var10000
         }

         when (access$updateCellSend(this, segment, i, element, s, waiter, var17)) {
            0 -> {
               segment.cleanPrev()
               return (R)onRendezvousOrBuffered()
            }
            1 -> return (R)onRendezvousOrBuffered()
            2 -> {
               if (var17) {
                  segment.onSlotCleaned()
                  return (R)onClosed()
               }

               val var18: Waiter = waiter as? Waiter
               if ((waiter as? Waiter) != null) {
                  access$prepareSenderForSuspension(this, var18, segment, i)
               }

               return (R)onSuspend(segment, i)
            }
            3 -> return (R)onNoWaiterSuspend(segment, i, element, s)
            4 -> {
               if (s < this.receiversCounter) {
                  segment.cleanPrev()
               }

               return (R)onClosed()
            }
            5 -> segment.cleanPrev()
            else -> {}
         }
      }
   }

   private inline fun sendImplOnNoWaiter(
      segment: ChannelSegment<Any>,
      index: Int,
      element: Any,
      s: Long,
      waiter: Waiter,
      onRendezvousOrBuffered: () -> Unit,
      onClosed: () -> Unit
   ) {
      when (access$updateCellSend(this, segment, index, element, s, waiter, false)) {
         0 -> {
            segment.cleanPrev()
            onRendezvousOrBuffered()
            return
         }
         1 -> {
            onRendezvousOrBuffered()
            return
         }
         2 -> {
            access$prepareSenderForSuspension(this, waiter, segment, index)
            return
         }
         3 -> throw IllegalStateException("unexpected".toString())
         4 -> {
            if (s < this.receiversCounter) {
               segment.cleanPrev()
            }

            onClosed()
            return
         }
         5 -> {
            segment.cleanPrev()
            var `segment$iv`: ChannelSegment = access$getSendSegment$FU$p().get(this) as ChannelSegment

            while (true) {
               val `sendersAndCloseStatusCur$iv`: Long = access$getSendersAndCloseStatus$FU$p().getAndIncrement(this)
               val `s$iv`: Long = `sendersAndCloseStatusCur$iv` and 1152921504606846975L
               val `closed$iv`: Boolean = access$isClosedForSend0(this, `sendersAndCloseStatusCur$iv`)
               val `id$iv`: Long = `s$iv` / BufferedChannelKt.SEGMENT_SIZE
               val `i$iv`: Int = (int)(`s$iv` % BufferedChannelKt.SEGMENT_SIZE)
               if (`segment$iv`.id != `id$iv`) {
                  val var10000: ChannelSegment = access$findSegmentSend(this, `id$iv`, `segment$iv`)
                  if (var10000 == null) {
                     if (!`closed$iv`) {
                        onClosed()
                        return
                     }

                     onClosed()
                     return
                  }

                  `segment$iv` = var10000
               }

               when (access$updateCellSend(this, `segment$iv`, `i$iv`, element, `s$iv`, waiter, `closed$iv`)) {
                  0 -> {
                     `segment$iv`.cleanPrev()
                     onRendezvousOrBuffered()
                     return
                  }
                  1 -> {
                     onRendezvousOrBuffered()
                     return
                  }
                  2 -> {
                     if (`closed$iv`) {
                        `segment$iv`.onSlotCleaned()
                        onClosed()
                        return
                     } else {
                        val var27: Waiter = if (waiter is Waiter) waiter else null
                        if ((if (waiter is Waiter) waiter else null) != null) {
                           access$prepareSenderForSuspension(this, var27, `segment$iv`, `i$iv`)
                        }

                        return
                     }
                  }
                  3 -> throw IllegalStateException("unexpected".toString())
                  4 -> {
                     if (`s$iv` < this.receiversCounter) {
                        `segment$iv`.cleanPrev()
                     }

                     onClosed()
                     return
                  }
                  5 -> `segment$iv`.cleanPrev()
                  else -> {}
               }
            }
         }
         else -> throw IllegalStateException("unexpected".toString())
      }
   }

   private fun updateCellSend(segment: ChannelSegment<Any>, index: Int, element: Any, s: Long, waiter: Any?, closed: Boolean): Int {
      segment.storeElement$kotlinx_coroutines_core(index, element)
      if (closed) {
         return this.updateCellSendSlow(segment, index, (E)element, s, waiter, closed)
      } else {
         val state: Any = segment.getState$kotlinx_coroutines_core(index)
         if (state == null) {
            if (this.bufferOrRendezvousSend(s)) {
               if (segment.casState$kotlinx_coroutines_core(index, null, BufferedChannelKt.BUFFERED)) {
                  return 1
               }
            } else {
               if (waiter == null) {
                  return 3
               }

               if (segment.casState$kotlinx_coroutines_core(index, null, waiter)) {
                  return 2
               }
            }
         } else if (state is Waiter) {
            segment.cleanElement$kotlinx_coroutines_core(index)
            val var10000: Byte
            if (this.tryResumeReceiver(state, (E)element)) {
               segment.setState$kotlinx_coroutines_core(index, BufferedChannelKt.access$getDONE_RCV$p())
               this.onReceiveDequeued()
               var10000 = 0
            } else {
               if (segment.getAndSetState$kotlinx_coroutines_core(index, BufferedChannelKt.access$getINTERRUPTED_RCV$p())
                  != BufferedChannelKt.access$getINTERRUPTED_RCV$p()) {
                  segment.onCancelledRequest(index, true)
               }

               var10000 = 5
            }

            return var10000
         }

         return this.updateCellSendSlow(segment, index, (E)element, s, waiter, closed)
      }
   }

   private fun updateCellSendSlow(segment: ChannelSegment<Any>, index: Int, element: Any, s: Long, waiter: Any?, closed: Boolean): Int {
      while (true) {
         val state: Any = segment.getState$kotlinx_coroutines_core(index)
         if (state == null) {
            if (this.bufferOrRendezvousSend(s) && !closed) {
               if (segment.casState$kotlinx_coroutines_core(index, null, BufferedChannelKt.BUFFERED)) {
                  return 1
               }
            } else if (closed) {
               if (segment.casState$kotlinx_coroutines_core(index, null, BufferedChannelKt.access$getINTERRUPTED_SEND$p())) {
                  segment.onCancelledRequest(index, false)
                  return 4
               }
            } else {
               if (waiter == null) {
                  return 3
               }

               if (segment.casState$kotlinx_coroutines_core(index, null, waiter)) {
                  return 2
               }
            }
         } else {
            if (state === BufferedChannelKt.access$getIN_BUFFER$p()) {
               if (!segment.casState$kotlinx_coroutines_core(index, state, BufferedChannelKt.BUFFERED)) {
                  continue
               }

               return 1
            }

            if (state === BufferedChannelKt.access$getINTERRUPTED_RCV$p()) {
               segment.cleanElement$kotlinx_coroutines_core(index)
               return 5
            }

            if (state === BufferedChannelKt.access$getPOISONED$p()) {
               segment.cleanElement$kotlinx_coroutines_core(index)
               return 5
            }

            if (state === CHANNEL_CLOSED) {
               segment.cleanElement$kotlinx_coroutines_core(index)
               this.completeCloseOrCancel()
               return 4
            }

            if (ASSERTIONS_ENABLED && state !is Waiter && state !is WaiterEB) {
               throw AssertionError()
            }

            segment.cleanElement$kotlinx_coroutines_core(index)
            val var10000: Byte
            if (this.tryResumeReceiver(if (state is WaiterEB) (state as WaiterEB).waiter else state, (E)element)) {
               segment.setState$kotlinx_coroutines_core(index, BufferedChannelKt.access$getDONE_RCV$p())
               this.onReceiveDequeued()
               var10000 = 0
            } else {
               if (segment.getAndSetState$kotlinx_coroutines_core(index, BufferedChannelKt.access$getINTERRUPTED_RCV$p())
                  != BufferedChannelKt.access$getINTERRUPTED_RCV$p()) {
                  segment.onCancelledRequest(index, true)
               }

               var10000 = 5
            }

            return var10000
         }
      }
   }

   private fun shouldSendSuspend(curSendersAndCloseStatus: Long): Boolean {
      return !this.isClosedForSend0 && !this.bufferOrRendezvousSend(curSendersAndCloseStatus and 1152921504606846975L)
   }

   private fun bufferOrRendezvousSend(curSenders: Long): Boolean {
      return curSenders < this.bufferEndCounter || curSenders < this.receiversCounter + this.capacity
   }

   internal open fun shouldSendSuspend(): Boolean {
      return this.shouldSendSuspend(sendersAndCloseStatus$FU.get(this))
   }

   private fun Any.tryResumeReceiver(element: Any): Boolean {
      val var10000: Boolean
      if (`$this$tryResumeReceiver` is SelectInstance) {
         var10000 = (`$this$tryResumeReceiver` as SelectInstance).trySelect(this, element)
      } else if (`$this$tryResumeReceiver` is ReceiveCatching) {
         var10000 = BufferedChannelKt.access$tryResume0(
            (`$this$tryResumeReceiver` as ReceiveCatching).cont,
            ChannelResult.box_impl/* $VF was: box-impl */(ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(element)),
            if (this.onUndeliveredElement != null)
               OnUndeliveredElementKt.bindCancellationFun(this.onUndeliveredElement, element, (`$this$tryResumeReceiver` as ReceiveCatching).cont.context)
               else
               null
         )
      } else if (`$this$tryResumeReceiver` is BufferedChannel.BufferedChannelIterator) {
         var10000 = (`$this$tryResumeReceiver` as BufferedChannel.BufferedChannelIterator).tryResumeHasNext((E)element)
      } else {
         if (`$this$tryResumeReceiver` !is CancellableContinuation) {
            throw IllegalStateException(("Unexpected receiver type: $`$this$tryResumeReceiver`").toString())
         }

         var10000 = BufferedChannelKt.access$tryResume0(
            `$this$tryResumeReceiver` as CancellableContinuation,
            element,
            if (this.onUndeliveredElement != null)
               OnUndeliveredElementKt.bindCancellationFun(
                  this.onUndeliveredElement, element, (`$this$tryResumeReceiver` as CancellableContinuation).getContext()
               )
               else
               null
         )
      }

      return var10000
   }

   protected open fun onReceiveEnqueued() {
   }

   protected open fun onReceiveDequeued() {
   }

   public override suspend fun receive(): Any {
      return receive$suspendImpl(this, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <E> `receive$suspendImpl`(`$this`: BufferedChannel<E>, `$completion`: Continuation<in E>) {
      val `this_$iv`: BufferedChannel = `$this`
      val `waiter$iv`: Any = null
      var `segment$iv`: ChannelSegment = access$getReceiveSegment$FU$p().get(`$this`) as ChannelSegment

      while (!`this_$iv`.isClosedForReceive) {
         val `r$iv`: Long = access$getReceivers$FU$p().getAndIncrement(`this_$iv`)
         val `id$iv`: Long = `r$iv` / BufferedChannelKt.SEGMENT_SIZE
         val `i$iv`: Int = (int)(`r$iv` % BufferedChannelKt.SEGMENT_SIZE)
         if (`segment$iv`.id != `id$iv`) {
            val var10000: ChannelSegment = access$findSegmentReceive(`this_$iv`, `id$iv`, `segment$iv`)
            if (var10000 == null) {
               continue
            }

            `segment$iv` = var10000
         }

         val `updCellResult$iv`: Any = access$updateCellReceive(`this_$iv`, `segment$iv`, `i$iv`, `r$iv`, `waiter$iv`)
         if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND$p()) {
            throw IllegalStateException("unexpected".toString())
         }

         if (`updCellResult$iv` != BufferedChannelKt.access$getFAILED$p()) {
            if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()) {
               `$this`.receiveOnNoWaiterSuspend(`segment$iv`, `i$iv`, `r$iv`, `$completion`)
            }

            `segment$iv`.cleanPrev()
            `updCellResult$iv`
         }

         if (`r$iv` < `this_$iv`.sendersCounter) {
            `segment$iv`.cleanPrev()
         }
      }

      throw StackTraceRecoveryKt.recoverStackTrace(`$this`.receiveException)
   }

   private suspend fun receiveOnNoWaiterSuspend(segment: ChannelSegment<Any>, index: Int, r: Long): Any {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(`$completion`))

      try {
         val cont: CancellableContinuationImpl = `cancellable$iv`
         val `this_$iv`: BufferedChannel = this
         val `updCellResult$iv`: Any = access$updateCellReceive(this, segment, index, r, `cancellable$iv`)
         if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND$p()) {
            access$prepareReceiverForSuspension(this, `cancellable$iv`, segment, index)
         } else if (`updCellResult$iv` != BufferedChannelKt.access$getFAILED$p()) {
            segment.cleanPrev()
            `cancellable$iv`.resume(
               `updCellResult$iv`,
               if (this.onUndeliveredElement != null)
                  OnUndeliveredElementKt.bindCancellationFun(this.onUndeliveredElement, `updCellResult$iv`, `cancellable$iv`.context)
                  else
                  null
            )
         } else {
            if (r < this.sendersCounter) {
               segment.cleanPrev()
            }

            var `segment$iv$iv`: ChannelSegment = access$getReceiveSegment$FU$p().get(this) as ChannelSegment

            label113@ while (true) {
               val `r$iv$iv`: Long
               val `i$iv$iv`: Int
               while (true) {
                  if (`this_$iv`.isClosedForReceive) {
                     access$onClosedReceiveOnNoWaiterSuspend(this, cont)
                     break@label113
                  }

                  `r$iv$iv` = access$getReceivers$FU$p().getAndIncrement(`this_$iv`)
                  val `id$iv$iv`: Long = `r$iv$iv` / BufferedChannelKt.SEGMENT_SIZE
                  `i$iv$iv` = (int)(`r$iv$iv` % BufferedChannelKt.SEGMENT_SIZE)
                  if (`segment$iv$iv`.id == `id$iv$iv`) {
                     break
                  }

                  val var10000: ChannelSegment = access$findSegmentReceive(`this_$iv`, `id$iv$iv`, `segment$iv$iv`)
                  if (var10000 != null) {
                     `segment$iv$iv` = var10000
                     break
                  }
               }

               val `updCellResult$iv$iv`: Any = access$updateCellReceive(`this_$iv`, `segment$iv$iv`, `i$iv$iv`, `r$iv$iv`, cont)
               if (`updCellResult$iv$iv` === BufferedChannelKt.access$getSUSPEND$p()) {
                  val var36: Waiter = cont as? Waiter
                  if ((cont as? Waiter) != null) {
                     access$prepareReceiverForSuspension(`this_$iv`, var36, `segment$iv$iv`, `i$iv$iv`)
                  }
               }

               if (`updCellResult$iv$iv` != BufferedChannelKt.access$getFAILED$p()) {
                  if (`updCellResult$iv$iv` === BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()) {
                     throw IllegalStateException("unexpected".toString())
                  }

                  `segment$iv$iv`.cleanPrev()
                  cont.resume(
                     `updCellResult$iv$iv`,
                     if (this.onUndeliveredElement != null)
                        OnUndeliveredElementKt.bindCancellationFun(this.onUndeliveredElement, `updCellResult$iv$iv`, cont.context)
                        else
                        null
                  )
                  break
               }

               if (`r$iv$iv` < `this_$iv`.sendersCounter) {
                  `segment$iv$iv`.cleanPrev()
               }
            }
         }
      } catch (var31: java.lang.Throwable) {
         `cancellable$iv`.releaseClaimedReusableContinuation$kotlinx_coroutines_core()
         throw var31
      }

      val var37: Any = `cancellable$iv`.getResult()
      if (var37 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return var37
   }

   private fun Waiter.prepareReceiverForSuspension(segment: ChannelSegment<Any>, index: Int) {
      this.onReceiveEnqueued()
      `$this$prepareReceiverForSuspension`.invokeOnCancellation(segment, index)
   }

   private fun onClosedReceiveOnNoWaiterSuspend(cont: CancellableContinuation<Any>) {
      cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(this.receiveException)))
   }

   public override suspend fun receiveCatching(): ChannelResult<Any> {
      return receiveCatching_JP2dKIU$suspendImpl/* $VF was: receiveCatching-JP2dKIU$suspendImpl */(this, `$completion`)
   }

   private suspend fun receiveCatchingOnNoWaiterSuspend(segment: ChannelSegment<Any>, index: Int, r: Long): ChannelResult<Any> {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
      //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
      //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
      //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.AssignmentExprent.toJava(AssignmentExprent.java:161)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
      //
      // Bytecode:
      // 000: aload 5
      // 002: instanceof kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1
      // 005: ifeq 02b
      // 008: aload 5
      // 00a: checkcast kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1
      // 00d: astore 32
      // 00f: aload 32
      // 011: getfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.label I
      // 014: ldc_w -2147483648
      // 017: iand
      // 018: ifeq 02b
      // 01b: aload 32
      // 01d: dup
      // 01e: getfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.label I
      // 021: ldc_w -2147483648
      // 024: isub
      // 025: putfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.label I
      // 028: goto 037
      // 02b: new kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1
      // 02e: dup
      // 02f: aload 0
      // 030: aload 5
      // 032: invokespecial kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.<init> (Lkotlinx/coroutines/channels/BufferedChannel;Lkotlin/coroutines/Continuation;)V
      // 035: astore 32
      // 037: aload 32
      // 039: getfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.result Ljava/lang/Object;
      // 03c: astore 31
      // 03e: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 041: astore 33
      // 043: aload 32
      // 045: getfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.label I
      // 048: tableswitch 608 0 1 24 560
      // 060: aload 31
      // 062: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 065: bipush 0
      // 066: istore 6
      // 068: aload 32
      // 06a: aload 0
      // 06b: putfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.L$0 Ljava/lang/Object;
      // 06e: aload 32
      // 070: aload 1
      // 071: putfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.L$1 Ljava/lang/Object;
      // 074: aload 32
      // 076: iload 2
      // 077: putfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.I$0 I
      // 07a: aload 32
      // 07c: lload 3
      // 07d: putfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.J$0 J
      // 080: aload 32
      // 082: bipush 1
      // 083: putfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.label I
      // 086: aload 32
      // 088: checkcast kotlin/coroutines/Continuation
      // 08b: astore 7
      // 08d: bipush 0
      // 08e: istore 8
      // 090: aload 7
      // 092: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.intercepted (Lkotlin/coroutines/Continuation;)Lkotlin/coroutines/Continuation;
      // 095: invokestatic kotlinx/coroutines/CancellableContinuationKt.getOrCreateCancellableContinuation (Lkotlin/coroutines/Continuation;)Lkotlinx/coroutines/CancellableContinuationImpl;
      // 098: astore 9
      // 09a: nop
      // 09b: aload 9
      // 09d: astore 10
      // 09f: bipush 0
      // 0a0: istore 11
      // 0a2: new kotlinx/coroutines/channels/ReceiveCatching
      // 0a5: dup
      // 0a6: aload 10
      // 0a8: ldc_w "null cannot be cast to non-null type kotlinx.coroutines.CancellableContinuationImpl<kotlinx.coroutines.channels.ChannelResult<E of kotlinx.coroutines.channels.BufferedChannel.receiveCatchingOnNoWaiterSuspend_GKJJFZk$lambda$35>>"
      // 0ab: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;Ljava/lang/String;)V
      // 0ae: aload 10
      // 0b0: invokespecial kotlinx/coroutines/channels/ReceiveCatching.<init> (Lkotlinx/coroutines/CancellableContinuationImpl;)V
      // 0b3: astore 12
      // 0b5: aload 0
      // 0b6: astore 13
      // 0b8: bipush 0
      // 0b9: istore 14
      // 0bb: aload 13
      // 0bd: aload 1
      // 0be: iload 2
      // 0bf: lload 3
      // 0c0: aload 12
      // 0c2: checkcast kotlinx/coroutines/Waiter
      // 0c5: invokestatic kotlinx/coroutines/channels/BufferedChannel.access$updateCellReceive (Lkotlinx/coroutines/channels/BufferedChannel;Lkotlinx/coroutines/channels/ChannelSegment;IJLjava/lang/Object;)Ljava/lang/Object;
      // 0c8: astore 15
      // 0ca: nop
      // 0cb: aload 15
      // 0cd: invokestatic kotlinx/coroutines/channels/BufferedChannelKt.access$getSUSPEND$p ()Lkotlinx/coroutines/internal/Symbol;
      // 0d0: if_acmpne 0e2
      // 0d3: aload 13
      // 0d5: aload 12
      // 0d7: checkcast kotlinx/coroutines/Waiter
      // 0da: aload 1
      // 0db: iload 2
      // 0dc: invokestatic kotlinx/coroutines/channels/BufferedChannel.access$prepareReceiverForSuspension (Lkotlinx/coroutines/channels/BufferedChannel;Lkotlinx/coroutines/Waiter;Lkotlinx/coroutines/channels/ChannelSegment;I)V
      // 0df: goto 24c
      // 0e2: aload 15
      // 0e4: invokestatic kotlinx/coroutines/channels/BufferedChannelKt.access$getFAILED$p ()Lkotlinx/coroutines/internal/Symbol;
      // 0e7: if_acmpne 218
      // 0ea: lload 3
      // 0eb: aload 13
      // 0ed: invokevirtual kotlinx/coroutines/channels/BufferedChannel.getSendersCounter$kotlinx_coroutines_core ()J
      // 0f0: lcmp
      // 0f1: ifge 0f8
      // 0f4: aload 1
      // 0f5: invokevirtual kotlinx/coroutines/channels/ChannelSegment.cleanPrev ()V
      // 0f8: nop
      // 0f9: bipush 0
      // 0fa: istore 16
      // 0fc: invokestatic kotlinx/coroutines/channels/BufferedChannel.access$getReceiveSegment$FU$p ()Ljava/util/concurrent/atomic/AtomicReferenceFieldUpdater;
      // 0ff: aload 13
      // 101: invokevirtual java/util/concurrent/atomic/AtomicReferenceFieldUpdater.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 104: checkcast kotlinx/coroutines/channels/ChannelSegment
      // 107: astore 17
      // 109: nop
      // 10a: aload 13
      // 10c: invokevirtual kotlinx/coroutines/channels/BufferedChannel.isClosedForReceive ()Z
      // 10f: ifeq 121
      // 112: bipush 0
      // 113: istore 18
      // 115: aload 0
      // 116: aload 10
      // 118: checkcast kotlinx/coroutines/CancellableContinuation
      // 11b: invokestatic kotlinx/coroutines/channels/BufferedChannel.access$onClosedReceiveCatchingOnNoWaiterSuspend (Lkotlinx/coroutines/channels/BufferedChannel;Lkotlinx/coroutines/CancellableContinuation;)V
      // 11e: goto 24c
      // 121: invokestatic kotlinx/coroutines/channels/BufferedChannel.access$getReceivers$FU$p ()Ljava/util/concurrent/atomic/AtomicLongFieldUpdater;
      // 124: aload 13
      // 126: invokevirtual java/util/concurrent/atomic/AtomicLongFieldUpdater.getAndIncrement (Ljava/lang/Object;)J
      // 129: lstore 19
      // 12b: lload 19
      // 12d: getstatic kotlinx/coroutines/channels/BufferedChannelKt.SEGMENT_SIZE I
      // 130: i2l
      // 131: ldiv
      // 132: lstore 21
      // 134: lload 19
      // 136: getstatic kotlinx/coroutines/channels/BufferedChannelKt.SEGMENT_SIZE I
      // 139: i2l
      // 13a: lrem
      // 13b: l2i
      // 13c: istore 23
      // 13e: aload 17
      // 140: getfield kotlinx/coroutines/channels/ChannelSegment.id J
      // 143: lload 21
      // 145: lcmp
      // 146: ifeq 15c
      // 149: aload 13
      // 14b: lload 21
      // 14d: aload 17
      // 14f: invokestatic kotlinx/coroutines/channels/BufferedChannel.access$findSegmentReceive (Lkotlinx/coroutines/channels/BufferedChannel;JLkotlinx/coroutines/channels/ChannelSegment;)Lkotlinx/coroutines/channels/ChannelSegment;
      // 152: dup
      // 153: ifnonnull 15a
      // 156: pop
      // 157: goto 109
      // 15a: astore 17
      // 15c: aload 13
      // 15e: aload 17
      // 160: iload 23
      // 162: lload 19
      // 164: aload 12
      // 166: checkcast kotlinx/coroutines/Waiter
      // 169: invokestatic kotlinx/coroutines/channels/BufferedChannel.access$updateCellReceive (Lkotlinx/coroutines/channels/BufferedChannel;Lkotlinx/coroutines/channels/ChannelSegment;IJLjava/lang/Object;)Ljava/lang/Object;
      // 16c: astore 24
      // 16e: nop
      // 16f: aload 24
      // 171: invokestatic kotlinx/coroutines/channels/BufferedChannelKt.access$getSUSPEND$p ()Lkotlinx/coroutines/internal/Symbol;
      // 174: if_acmpne 1a4
      // 177: aload 12
      // 179: checkcast kotlinx/coroutines/Waiter
      // 17c: instanceof kotlinx/coroutines/Waiter
      // 17f: ifeq 18a
      // 182: aload 12
      // 184: checkcast kotlinx/coroutines/Waiter
      // 187: goto 18b
      // 18a: aconst_null
      // 18b: dup
      // 18c: ifnull 19c
      // 18f: aload 13
      // 191: swap
      // 192: aload 17
      // 194: iload 23
      // 196: invokestatic kotlinx/coroutines/channels/BufferedChannel.access$prepareReceiverForSuspension (Lkotlinx/coroutines/channels/BufferedChannel;Lkotlinx/coroutines/Waiter;Lkotlinx/coroutines/channels/ChannelSegment;I)V
      // 199: goto 19d
      // 19c: pop
      // 19d: bipush 0
      // 19e: istore 25
      // 1a0: nop
      // 1a1: goto 214
      // 1a4: aload 24
      // 1a6: invokestatic kotlinx/coroutines/channels/BufferedChannelKt.access$getFAILED$p ()Lkotlinx/coroutines/internal/Symbol;
      // 1a9: if_acmpne 1bf
      // 1ac: lload 19
      // 1ae: aload 13
      // 1b0: invokevirtual kotlinx/coroutines/channels/BufferedChannel.getSendersCounter$kotlinx_coroutines_core ()J
      // 1b3: lcmp
      // 1b4: ifge 1bc
      // 1b7: aload 17
      // 1b9: invokevirtual kotlinx/coroutines/channels/ChannelSegment.cleanPrev ()V
      // 1bc: goto 109
      // 1bf: aload 24
      // 1c1: invokestatic kotlinx/coroutines/channels/BufferedChannelKt.access$getSUSPEND_NO_WAITER$p ()Lkotlinx/coroutines/internal/Symbol;
      // 1c4: if_acmpne 1e0
      // 1c7: aload 17
      // 1c9: iload 23
      // 1cb: istore 26
      // 1cd: astore 27
      // 1cf: bipush 0
      // 1d0: istore 28
      // 1d2: new java/lang/IllegalStateException
      // 1d5: dup
      // 1d6: ldc_w "unexpected"
      // 1d9: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 1dc: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 1df: athrow
      // 1e0: aload 17
      // 1e2: invokevirtual kotlinx/coroutines/channels/ChannelSegment.cleanPrev ()V
      // 1e5: aload 24
      // 1e7: astore 18
      // 1e9: bipush 0
      // 1ea: istore 29
      // 1ec: aload 10
      // 1ee: getstatic kotlinx/coroutines/channels/ChannelResult.Companion Lkotlinx/coroutines/channels/ChannelResult$Companion;
      // 1f1: aload 18
      // 1f3: invokevirtual kotlinx/coroutines/channels/ChannelResult$Companion.success-JP2dKIU (Ljava/lang/Object;)Ljava/lang/Object;
      // 1f6: invokestatic kotlinx/coroutines/channels/ChannelResult.box-impl (Ljava/lang/Object;)Lkotlinx/coroutines/channels/ChannelResult;
      // 1f9: aload 0
      // 1fa: getfield kotlinx/coroutines/channels/BufferedChannel.onUndeliveredElement Lkotlin/jvm/functions/Function1;
      // 1fd: dup
      // 1fe: ifnull 20e
      // 201: aload 18
      // 203: aload 10
      // 205: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.getContext ()Lkotlin/coroutines/CoroutineContext;
      // 208: invokestatic kotlinx/coroutines/internal/OnUndeliveredElementKt.bindCancellationFun (Lkotlin/jvm/functions/Function1;Ljava/lang/Object;Lkotlin/coroutines/CoroutineContext;)Lkotlin/jvm/functions/Function1;
      // 20b: goto 210
      // 20e: pop
      // 20f: aconst_null
      // 210: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.resume (Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)V
      // 213: nop
      // 214: nop
      // 215: goto 24c
      // 218: aload 1
      // 219: invokevirtual kotlinx/coroutines/channels/ChannelSegment.cleanPrev ()V
      // 21c: aload 15
      // 21e: astore 18
      // 220: bipush 0
      // 221: istore 29
      // 223: aload 10
      // 225: getstatic kotlinx/coroutines/channels/ChannelResult.Companion Lkotlinx/coroutines/channels/ChannelResult$Companion;
      // 228: aload 18
      // 22a: invokevirtual kotlinx/coroutines/channels/ChannelResult$Companion.success-JP2dKIU (Ljava/lang/Object;)Ljava/lang/Object;
      // 22d: invokestatic kotlinx/coroutines/channels/ChannelResult.box-impl (Ljava/lang/Object;)Lkotlinx/coroutines/channels/ChannelResult;
      // 230: aload 0
      // 231: getfield kotlinx/coroutines/channels/BufferedChannel.onUndeliveredElement Lkotlin/jvm/functions/Function1;
      // 234: dup
      // 235: ifnull 245
      // 238: aload 18
      // 23a: aload 10
      // 23c: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.getContext ()Lkotlin/coroutines/CoroutineContext;
      // 23f: invokestatic kotlinx/coroutines/internal/OnUndeliveredElementKt.bindCancellationFun (Lkotlin/jvm/functions/Function1;Ljava/lang/Object;Lkotlin/coroutines/CoroutineContext;)Lkotlin/jvm/functions/Function1;
      // 242: goto 247
      // 245: pop
      // 246: aconst_null
      // 247: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.resume (Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)V
      // 24a: nop
      // 24b: nop
      // 24c: nop
      // 24d: nop
      // 24e: goto 25b
      // 251: astore 30
      // 253: aload 9
      // 255: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.releaseClaimedReusableContinuation$kotlinx_coroutines_core ()V
      // 258: aload 30
      // 25a: athrow
      // 25b: aload 9
      // 25d: invokevirtual kotlinx/coroutines/CancellableContinuationImpl.getResult ()Ljava/lang/Object;
      // 260: dup
      // 261: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 264: if_acmpne 26f
      // 267: aload 32
      // 269: checkcast kotlin/coroutines/Continuation
      // 26c: invokestatic kotlin/coroutines/jvm/internal/DebugProbesKt.probeCoroutineSuspended (Lkotlin/coroutines/Continuation;)V
      // 26f: dup
      // 270: aload 33
      // 272: if_acmpne 2a0
      // 275: aload 33
      // 277: areturn
      // 278: bipush 0
      // 279: istore 6
      // 27b: aload 32
      // 27d: getfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.J$0 J
      // 280: pop2
      // 281: aload 32
      // 283: getfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.I$0 I
      // 286: pop
      // 287: aload 32
      // 289: getfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.L$1 Ljava/lang/Object;
      // 28c: checkcast kotlinx/coroutines/channels/ChannelSegment
      // 28f: pop
      // 290: aload 32
      // 292: getfield kotlinx/coroutines/channels/BufferedChannel$receiveCatchingOnNoWaiterSuspend$1.L$0 Ljava/lang/Object;
      // 295: checkcast kotlinx/coroutines/channels/BufferedChannel
      // 298: pop
      // 299: aload 31
      // 29b: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 29e: aload 31
      // 2a0: nop
      // 2a1: checkcast kotlinx/coroutines/channels/ChannelResult
      // 2a4: invokevirtual kotlinx/coroutines/channels/ChannelResult.unbox-impl ()Ljava/lang/Object;
      // 2a7: areturn
      // 2a8: new java/lang/IllegalStateException
      // 2ab: dup
      // 2ac: ldc_w "call to 'resume' before 'invoke' with coroutine"
      // 2af: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 2b2: athrow
   }

   private fun onClosedReceiveCatchingOnNoWaiterSuspend(cont: CancellableContinuation<ChannelResult<Any>>) {
      cont.resumeWith(
         Result.constructor_impl/* $VF was: constructor-impl */(
            ChannelResult.box_impl/* $VF was: box-impl */(ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.closeCause))
         )
      )
   }

   public override fun tryReceive(): ChannelResult<Any> {
      val r: Long = receivers$FU.get(this)
      val sendersAndCloseStatusCur: Long = sendersAndCloseStatus$FU.get(this)
      if (this.isClosedForReceive0) {
         return ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.closeCause)
      } else if (r >= (sendersAndCloseStatusCur and 1152921504606846975L)) {
         return ChannelResult.Companion.failure_PtdJZtk/* $VF was: failure-PtdJZtk */()
      } else {
         val var24: Symbol = BufferedChannelKt.access$getINTERRUPTED_RCV$p()
         var `segment$iv`: ChannelSegment = access$getReceiveSegment$FU$p().get(this) as ChannelSegment

         var var28: ChannelSegment
         while (true) {
            if (this.isClosedForReceive) {
               var28 = (ChannelSegment)ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.closeCause)
               break
            }

            val `r$iv`: Long = access$getReceivers$FU$p().getAndIncrement(this)
            val `id$iv`: Long = `r$iv` / BufferedChannelKt.SEGMENT_SIZE
            val `i$iv`: Int = (int)(`r$iv` % BufferedChannelKt.SEGMENT_SIZE)
            if (`segment$iv`.id != `id$iv`) {
               var28 = access$findSegmentReceive(this, `id$iv`, `segment$iv`)
               if (var28 == null) {
                  continue
               }

               `segment$iv` = var28
            }

            val `updCellResult$iv`: Any = access$updateCellReceive(this, `segment$iv`, `i$iv`, `r$iv`, var24)
            if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND$p()) {
               val var27: Waiter = var24 as? Waiter
               if ((var24 as? Waiter) != null) {
                  access$prepareReceiverForSuspension(this, var27, `segment$iv`, `i$iv`)
               }

               this.waitExpandBufferCompletion$kotlinx_coroutines_core(`r$iv`)
               `segment$iv`.onSlotCleaned()
               var28 = (ChannelSegment)ChannelResult.Companion.failure_PtdJZtk/* $VF was: failure-PtdJZtk */()
               break
            } else if (`updCellResult$iv` === BufferedChannelKt.access$getFAILED$p()) {
               if (`r$iv` < this.sendersCounter) {
                  `segment$iv`.cleanPrev()
               }
            } else {
               if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()) {
                  throw IllegalStateException("unexpected".toString())
               }

               `segment$iv`.cleanPrev()
               var28 = (ChannelSegment)ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(`updCellResult$iv`)
               break
            }
         }

         return var28
      }
   }

   protected fun dropFirstElementUntilTheSpecifiedCellIsInTheBuffer(globalCellIndex: Long) {
      if (ASSERTIONS_ENABLED && !this.isConflatedDropOldest) {
         throw AssertionError()
      } else {
         var var14: ChannelSegment = receiveSegment$FU.get(this) as ChannelSegment

         while (true) {
            val r: Long = receivers$FU.get(this)
            if (globalCellIndex < Math.max(r + (long)this.capacity, this.bufferEndCounter)) {
               return
            }

            if (receivers$FU.compareAndSet(this, r, r + 1L)) {
               val id: Long = r / BufferedChannelKt.SEGMENT_SIZE
               val i: Int = (int)(r % BufferedChannelKt.SEGMENT_SIZE)
               if (var14.id != id) {
                  val var10000: ChannelSegment = this.findSegmentReceive(id, var14)
                  if (var10000 == null) {
                     continue
                  }

                  var14 = var10000
               }

               val updCellResult: Any = this.updateCellReceive(var14, i, r, null)
               if (updCellResult === BufferedChannelKt.access$getFAILED$p()) {
                  if (r < this.sendersCounter) {
                     var14.cleanPrev()
                  }
               } else {
                  var14.cleanPrev()
                  if (this.onUndeliveredElement != null) {
                     val var11: UndeliveredElementException = OnUndeliveredElementKt.callUndeliveredElementCatchingException$default(
                        this.onUndeliveredElement, updCellResult, null, 2, null
                     )
                     if (var11 != null) {
                        throw var11
                     }
                  }
               }
            }
         }
      }
   }

   private inline fun <R> receiveImpl(
      waiter: Any?,
      onElementRetrieved: (Any) -> Any,
      onSuspend: (ChannelSegment<Any>, Int, Long) -> Any,
      onClosed: () -> Any,
      onNoWaiterSuspend: (ChannelSegment<Any>, Int, Long) -> Any = { <anonymous parameter 0>, <anonymous parameter 1>, <anonymous parameter 2> ->
            throw IllegalStateException("unexpected".toString())
         } as Function3
   ): Any {
      var segment: ChannelSegment = access$getReceiveSegment$FU$p().get(this) as ChannelSegment

      var var14: ChannelSegment
      while (true) {
         val r: Long
         val i: Int
         while (true) {
            if (this.isClosedForReceive) {
               return (R)onClosed()
            }

            r = access$getReceivers$FU$p().getAndIncrement(this)
            val id: Long = r / BufferedChannelKt.SEGMENT_SIZE
            i = (int)(r % BufferedChannelKt.SEGMENT_SIZE)
            if (segment.id == id) {
               break
            }

            var14 = access$findSegmentReceive(this, id, segment)
            if (var14 != null) {
               segment = var14
               break
            }
         }

         val updCellResult: Any = access$updateCellReceive(this, segment, i, r, waiter)
         if (updCellResult === BufferedChannelKt.access$getSUSPEND$p()) {
            val var15: Waiter = waiter as? Waiter
            if ((waiter as? Waiter) != null) {
               access$prepareReceiverForSuspension(this, var15, segment, i)
            }

            var14 = (ChannelSegment)onSuspend(segment, i, r)
            break
         }

         if (updCellResult != BufferedChannelKt.access$getFAILED$p()) {
            if (updCellResult === BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()) {
               var14 = (ChannelSegment)onNoWaiterSuspend(segment, i, r)
            } else {
               segment.cleanPrev()
               var14 = (ChannelSegment)onElementRetrieved(updCellResult)
            }
            break
         }

         if (r < this.sendersCounter) {
            segment.cleanPrev()
         }
      }

      return (R)var14
   }

   private inline fun receiveImplOnNoWaiter(
      segment: ChannelSegment<Any>,
      index: Int,
      r: Long,
      waiter: Waiter,
      onElementRetrieved: (Any) -> Unit,
      onClosed: () -> Unit
   ) {
      val updCellResult: Any = access$updateCellReceive(this, segment, index, r, waiter)
      if (updCellResult === BufferedChannelKt.access$getSUSPEND$p()) {
         access$prepareReceiverForSuspension(this, waiter, segment, index)
      } else if (updCellResult === BufferedChannelKt.access$getFAILED$p()) {
         if (r < this.sendersCounter) {
            segment.cleanPrev()
         }

         var `segment$iv`: ChannelSegment = access$getReceiveSegment$FU$p().get(this) as ChannelSegment

         while (true) {
            val `r$iv`: Long
            val `i$iv`: Int
            while (true) {
               if (this.isClosedForReceive) {
                  onClosed()
                  return
               }

               `r$iv` = access$getReceivers$FU$p().getAndIncrement(this)
               val `id$iv`: Long = `r$iv` / BufferedChannelKt.SEGMENT_SIZE
               `i$iv` = (int)(`r$iv` % BufferedChannelKt.SEGMENT_SIZE)
               if (`segment$iv`.id == `id$iv`) {
                  break
               }

               val var10000: ChannelSegment = access$findSegmentReceive(this, `id$iv`, `segment$iv`)
               if (var10000 != null) {
                  `segment$iv` = var10000
                  break
               }
            }

            val `updCellResult$iv`: Any = access$updateCellReceive(this, `segment$iv`, `i$iv`, `r$iv`, waiter)
            if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND$p()) {
               val var22: Waiter = if (waiter is Waiter) waiter else null
               if ((if (waiter is Waiter) waiter else null) != null) {
                  access$prepareReceiverForSuspension(this, var22, `segment$iv`, `i$iv`)
               }
               break
            }

            if (`updCellResult$iv` != BufferedChannelKt.access$getFAILED$p()) {
               if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()) {
                  throw IllegalStateException("unexpected".toString())
               }

               `segment$iv`.cleanPrev()
               onElementRetrieved(`updCellResult$iv`)
               break
            }

            if (`r$iv` < this.sendersCounter) {
               `segment$iv`.cleanPrev()
            }
         }
      } else {
         segment.cleanPrev()
         onElementRetrieved(updCellResult)
      }
   }

   private fun updateCellReceive(segment: ChannelSegment<Any>, index: Int, r: Long, waiter: Any?): Any? {
      val state: Any = segment.getState$kotlinx_coroutines_core(index)
      if (state == null) {
         if (r >= (sendersAndCloseStatus$FU.get(this) and 1152921504606846975L)) {
            if (waiter == null) {
               return BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()
            }

            if (segment.casState$kotlinx_coroutines_core(index, state, waiter)) {
               this.expandBuffer()
               return BufferedChannelKt.access$getSUSPEND$p()
            }
         }
      } else if (state === BufferedChannelKt.BUFFERED && segment.casState$kotlinx_coroutines_core(index, state, BufferedChannelKt.access$getDONE_RCV$p())) {
         this.expandBuffer()
         return segment.retrieveElement$kotlinx_coroutines_core(index)
      }

      return this.updateCellReceiveSlow(segment, index, r, waiter)
   }

   private fun updateCellReceiveSlow(segment: ChannelSegment<Any>, index: Int, r: Long, waiter: Any?): Any? {
      while (true) {
         val state: Any = segment.getState$kotlinx_coroutines_core(index)
         if (state != null && state != BufferedChannelKt.access$getIN_BUFFER$p()) {
            if (state === BufferedChannelKt.BUFFERED) {
               if (segment.casState$kotlinx_coroutines_core(index, state, BufferedChannelKt.access$getDONE_RCV$p())) {
                  this.expandBuffer()
                  return segment.retrieveElement$kotlinx_coroutines_core(index)
               }
            } else {
               if (state === BufferedChannelKt.access$getINTERRUPTED_SEND$p()) {
                  return BufferedChannelKt.access$getFAILED$p()
               }

               if (state === BufferedChannelKt.access$getPOISONED$p()) {
                  return BufferedChannelKt.access$getFAILED$p()
               }

               if (state === CHANNEL_CLOSED) {
                  this.expandBuffer()
                  return BufferedChannelKt.access$getFAILED$p()
               }

               if (state != BufferedChannelKt.access$getRESUMING_BY_EB$p()
                  && segment.casState$kotlinx_coroutines_core(index, state, BufferedChannelKt.access$getRESUMING_BY_RCV$p())) {
                  val var12: Boolean = state is WaiterEB
                  val var10000: Any
                  if (this.tryResumeSender(if (state is WaiterEB) (state as WaiterEB).waiter else state, segment, index)) {
                     segment.setState$kotlinx_coroutines_core(index, BufferedChannelKt.access$getDONE_RCV$p())
                     this.expandBuffer()
                     var10000 = segment.retrieveElement$kotlinx_coroutines_core(index)
                  } else {
                     segment.setState$kotlinx_coroutines_core(index, BufferedChannelKt.access$getINTERRUPTED_SEND$p())
                     segment.onCancelledRequest(index, false)
                     if (var12) {
                        this.expandBuffer()
                     }

                     var10000 = BufferedChannelKt.access$getFAILED$p()
                  }

                  return var10000
               }
            }
         } else if (r < (sendersAndCloseStatus$FU.get(this) and 1152921504606846975L)) {
            if (segment.casState$kotlinx_coroutines_core(index, state, BufferedChannelKt.access$getPOISONED$p())) {
               this.expandBuffer()
               return BufferedChannelKt.access$getFAILED$p()
            }
         } else {
            if (waiter == null) {
               return BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()
            }

            if (segment.casState$kotlinx_coroutines_core(index, state, waiter)) {
               this.expandBuffer()
               return BufferedChannelKt.access$getSUSPEND$p()
            }
         }
      }
   }

   private fun Any.tryResumeSender(segment: ChannelSegment<Any>, index: Int): Boolean {
      val var10000: Boolean
      if (`$this$tryResumeSender` is CancellableContinuation) {
         var10000 = BufferedChannelKt.tryResume0$default(`$this$tryResumeSender` as CancellableContinuation, Unit.INSTANCE, null, 2, null)
      } else if (`$this$tryResumeSender` is SelectInstance) {
         val trySelectResult: TrySelectDetailedResult = (`$this$tryResumeSender` as SelectImplementation).trySelectDetailed(this, Unit.INSTANCE)
         if (trySelectResult === TrySelectDetailedResult.REREGISTER) {
            segment.cleanElement$kotlinx_coroutines_core(index)
         }

         var10000 = trySelectResult === TrySelectDetailedResult.SUCCESSFUL
      } else {
         if (`$this$tryResumeSender` !is BufferedChannel.SendBroadcast) {
            throw IllegalStateException(("Unexpected waiter: $`$this$tryResumeSender`").toString())
         }

         var10000 = BufferedChannelKt.tryResume0$default((`$this$tryResumeSender` as BufferedChannel.SendBroadcast).cont, true, null, 2, null)
      }

      return var10000
   }

   private fun expandBuffer() {
      if (!this.isRendezvousOrUnlimited) {
         var segment: ChannelSegment = bufferEndSegment$FU.get(this) as ChannelSegment

         while (true) {
            val b: Long = bufferEnd$FU.getAndIncrement(this)
            val id: Long = b / BufferedChannelKt.SEGMENT_SIZE
            if (this.sendersCounter <= b) {
               if (segment.id < id && segment.getNext() != null) {
                  this.moveSegmentBufferEndToSpecifiedOrLast(id, segment)
               }

               incCompletedExpandBufferAttempts$default(this, 0L, 1, null)
               return
            }

            if (segment.id != id) {
               val var10000: ChannelSegment = this.findSegmentBufferEnd(id, segment, b)
               if (var10000 == null) {
                  continue
               }

               segment = var10000
            }

            if (this.updateCellExpandBuffer(segment, (int)(b % (long)BufferedChannelKt.SEGMENT_SIZE), b)) {
               incCompletedExpandBufferAttempts$default(this, 0L, 1, null)
               return
            }

            incCompletedExpandBufferAttempts$default(this, 0L, 1, null)
         }
      }
   }

   private fun updateCellExpandBuffer(segment: ChannelSegment<Any>, index: Int, b: Long): Boolean {
      val state: Any = segment.getState$kotlinx_coroutines_core(index)
      if (state is Waiter
         && b >= receivers$FU.get(this)
         && segment.casState$kotlinx_coroutines_core(index, state, BufferedChannelKt.access$getRESUMING_BY_EB$p())) {
         val var10000: Boolean
         if (this.tryResumeSender(state, segment, index)) {
            segment.setState$kotlinx_coroutines_core(index, BufferedChannelKt.BUFFERED)
            var10000 = true
         } else {
            segment.setState$kotlinx_coroutines_core(index, BufferedChannelKt.access$getINTERRUPTED_SEND$p())
            segment.onCancelledRequest(index, false)
            var10000 = false
         }

         return var10000
      } else {
         return this.updateCellExpandBufferSlow(segment, index, b)
      }
   }

   private fun updateCellExpandBufferSlow(segment: ChannelSegment<Any>, index: Int, b: Long): Boolean {
      while (true) {
         val state: Any = segment.getState$kotlinx_coroutines_core(index)
         if (state is Waiter) {
            if (b < receivers$FU.get(this)) {
               if (segment.casState$kotlinx_coroutines_core(index, state, WaiterEB(state as Waiter))) {
                  return true
               }
            } else if (segment.casState$kotlinx_coroutines_core(index, state, BufferedChannelKt.access$getRESUMING_BY_EB$p())) {
               val var10000: Boolean
               if (this.tryResumeSender(state, segment, index)) {
                  segment.setState$kotlinx_coroutines_core(index, BufferedChannelKt.BUFFERED)
                  var10000 = true
               } else {
                  segment.setState$kotlinx_coroutines_core(index, BufferedChannelKt.access$getINTERRUPTED_SEND$p())
                  segment.onCancelledRequest(index, false)
                  var10000 = false
               }

               return var10000
            }
         } else {
            if (state === BufferedChannelKt.access$getINTERRUPTED_SEND$p()) {
               return false
            }

            if (state == null) {
               if (segment.casState$kotlinx_coroutines_core(index, state, BufferedChannelKt.access$getIN_BUFFER$p())) {
                  return true
               }
            } else {
               if (state === BufferedChannelKt.BUFFERED) {
                  return true
               }

               if (state != BufferedChannelKt.access$getPOISONED$p()
                  && state != BufferedChannelKt.access$getDONE_RCV$p()
                  && state != BufferedChannelKt.access$getINTERRUPTED_RCV$p()) {
                  if (state === CHANNEL_CLOSED) {
                     return true
                  }

                  if (state === BufferedChannelKt.access$getRESUMING_BY_RCV$p()) {
                     continue
                  }

                  throw IllegalStateException(("Unexpected cell state: $state").toString())
               }

               return true
            }
         }
      }
   }

   private fun incCompletedExpandBufferAttempts(nAttempts: Long = 1L) {
      if ((completedExpandBuffersAndPauseFlag$FU.addAndGet(this, nAttempts) and 4611686018427387904L) != 0L) {
         do {
         } while ((completedExpandBuffersAndPauseFlag$FU.get(this) and 4611686018427387904L) != 0L)
      }
   }

   internal fun waitExpandBufferCompletion(globalIndex: Long) {
      if (!this.isRendezvousOrUnlimited) {
         while (this.bufferEndCounter <= globalIndex) {
         }

         val b: Int = BufferedChannelKt.access$getEXPAND_BUFFER_COMPLETION_WAIT_ITERATIONS$p()

         repeat(b) { `atomicfu$handler$iv` ->
            val ebCompleted: Long = this.bufferEndCounter
            if (ebCompleted == (completedExpandBuffersAndPauseFlag$FU.get(this) and 4611686018427387903L) && ebCompleted == this.bufferEndCounter) {
               return
            }
         }

         val var25: AtomicLongFieldUpdater = completedExpandBuffersAndPauseFlag$FU

         val var27: Long
         do {
            var27 = var25.get(this)
         } while (!var25.compareAndSet(this, var27, BufferedChannelKt.access$constructEBCompletedAndPauseFlag(var27 and 4611686018427387903L, true)))

         while (true) {
            val var24: Long = this.bufferEndCounter
            val var26: Long = completedExpandBuffersAndPauseFlag$FU.get(this)
            val var28: Long = var26 and 4611686018427387903L
            val var30: Boolean = (var26 and 4611686018427387904L) != 0L
            if (var24 == var28 && var24 == this.bufferEndCounter) {
               val `atomicfu$handler$ivx`: AtomicLongFieldUpdater = completedExpandBuffersAndPauseFlag$FU

               val `$this$ebCompletedCounter$ivx`: Long
               do {
                  `$this$ebCompletedCounter$ivx` = `atomicfu$handler$ivx`.get(this)
               } while (
                  !`atomicfu$handler$ivx`.compareAndSet(
                     this,
                     `$this$ebCompletedCounter$ivx`,
                     BufferedChannelKt.access$constructEBCompletedAndPauseFlag(`$this$ebCompletedCounter$ivx` and 4611686018427387903L, false)
                  )
               )

               return
            }

            if (!var30) {
               completedExpandBuffersAndPauseFlag$FU.compareAndSet(this, var26, BufferedChannelKt.access$constructEBCompletedAndPauseFlag(var28, true))
            }
         }
      }
   }

   public open val onSend: SelectClause2<Any, BufferedChannel<Any>>
      public open get() {
         var var10003: Function3 = <unrepresentable>.INSTANCE
         var10003 = TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10003, 3) as Function3
         val var10004: <unrepresentable> = <unrepresentable>.INSTANCE
         return SelectClause2Impl<>(this, var10003, TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10004, 3) as Function3, null, 8, null)
      }


   protected open fun registerSelectForSend(select: SelectInstance<*>, element: Any?) {
      val `element$iv`: Any = element
      var `segment$iv`: ChannelSegment = access$getSendSegment$FU$p().get(this) as ChannelSegment

      while (true) {
         val `sendersAndCloseStatusCur$iv`: Long = access$getSendersAndCloseStatus$FU$p().getAndIncrement(this)
         val `s$iv`: Long = `sendersAndCloseStatusCur$iv` and 1152921504606846975L
         val `closed$iv`: Boolean = access$isClosedForSend0(this, `sendersAndCloseStatusCur$iv`)
         val `id$iv`: Long = `s$iv` / BufferedChannelKt.SEGMENT_SIZE
         val `i$iv`: Int = (int)(`s$iv` % BufferedChannelKt.SEGMENT_SIZE)
         if (`segment$iv`.id != `id$iv`) {
            val var10000: ChannelSegment = access$findSegmentSend(this, `id$iv`, `segment$iv`)
            if (var10000 == null) {
               if (!`closed$iv`) {
                  this.onClosedSelectOnSend((E)element, select)
                  return
               }

               this.onClosedSelectOnSend((E)element, select)
               return
            }

            `segment$iv` = var10000
         }

         when (access$updateCellSend(this, `segment$iv`, `i$iv`, `element$iv`, `s$iv`, select, `closed$iv`)) {
            0 -> {
               `segment$iv`.cleanPrev()
               select.selectInRegistrationPhase(Unit.INSTANCE)
               return
            }
            1 -> {
               select.selectInRegistrationPhase(Unit.INSTANCE)
               return
            }
            2 -> {
               if (`closed$iv`) {
                  `segment$iv`.onSlotCleaned()
                  this.onClosedSelectOnSend((E)element, select)
               } else {
                  val var25: Waiter = select as? Waiter
                  if ((select as? Waiter) != null) {
                     access$prepareSenderForSuspension(this, var25, `segment$iv`, `i$iv`)
                  }
               }

               return
            }
            3 -> throw IllegalStateException("unexpected".toString())
            4 -> {
               if (`s$iv` < this.receiversCounter) {
                  `segment$iv`.cleanPrev()
               }

               this.onClosedSelectOnSend((E)element, select)
               return
            }
            5 -> `segment$iv`.cleanPrev()
            else -> {}
         }
      }
   }

   private fun onClosedSelectOnSend(element: Any, select: SelectInstance<*>) {
      if (this.onUndeliveredElement != null) {
         OnUndeliveredElementKt.callUndeliveredElement(this.onUndeliveredElement, element, select.context)
      }

      select.selectInRegistrationPhase(CHANNEL_CLOSED)
   }

   private fun processResultSelectSend(ignoredParam: Any?, selectResult: Any?): Any? {
      if (selectResult === CHANNEL_CLOSED) {
         throw this.sendException
      } else {
         return this
      }
   }

   public open val onReceive: SelectClause1<Any>
      public open get() {
         var var10003: Function3 = <unrepresentable>.INSTANCE
         var10003 = TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10003, 3) as Function3
         val var10004: <unrepresentable> = <unrepresentable>.INSTANCE
         return SelectClause1Impl<>(
            this,
            var10003,
            TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10004, 3) as (Any?, Any?, Any?) -> Any,
            this.onUndeliveredElementReceiveCancellationConstructor
         )
      }


   public open val onReceiveCatching: SelectClause1<ChannelResult<Any>>
      public open get() {
         var var10003: Function3 = <unrepresentable>.INSTANCE
         var10003 = TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10003, 3) as Function3
         val var10004: <unrepresentable> = <unrepresentable>.INSTANCE
         return SelectClause1Impl<>(
            this,
            var10003,
            TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10004, 3) as (Any?, Any?, Any?) -> Any,
            this.onUndeliveredElementReceiveCancellationConstructor
         )
      }


   public open val onReceiveOrNull: SelectClause1<Any?>
      public open get() {
         var var10003: Function3 = <unrepresentable>.INSTANCE
         var10003 = TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10003, 3) as Function3
         val var10004: <unrepresentable> = <unrepresentable>.INSTANCE
         return SelectClause1Impl<>(
            this,
            var10003,
            TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10004, 3) as (Any?, Any?, Any?) -> Any,
            this.onUndeliveredElementReceiveCancellationConstructor
         )
      }


   private fun registerSelectForReceive(select: SelectInstance<*>, ignoredParam: Any?) {
      var `segment$iv`: ChannelSegment = access$getReceiveSegment$FU$p().get(this) as ChannelSegment

      while (true) {
         if (this.isClosedForReceive) {
            this.onClosedSelectOnReceive(select)
            break
         }

         val `r$iv`: Long = access$getReceivers$FU$p().getAndIncrement(this)
         val `id$iv`: Long = `r$iv` / BufferedChannelKt.SEGMENT_SIZE
         val `i$iv`: Int = (int)(`r$iv` % BufferedChannelKt.SEGMENT_SIZE)
         if (`segment$iv`.id != `id$iv`) {
            val var10000: ChannelSegment = access$findSegmentReceive(this, `id$iv`, `segment$iv`)
            if (var10000 == null) {
               continue
            }

            `segment$iv` = var10000
         }

         val `updCellResult$iv`: Any = access$updateCellReceive(this, `segment$iv`, `i$iv`, `r$iv`, select)
         if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND$p()) {
            val var16: Waiter = select as? Waiter
            if ((select as? Waiter) != null) {
               access$prepareReceiverForSuspension(this, var16, `segment$iv`, `i$iv`)
            }
         } else if (`updCellResult$iv` === BufferedChannelKt.access$getFAILED$p()) {
            if (`r$iv` < this.sendersCounter) {
               `segment$iv`.cleanPrev()
            }
         } else {
            if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()) {
               throw IllegalStateException("unexpected".toString())
            }

            `segment$iv`.cleanPrev()
            select.selectInRegistrationPhase(`updCellResult$iv`)
            break
         }
      }
   }

   private fun onClosedSelectOnReceive(select: SelectInstance<*>) {
      select.selectInRegistrationPhase(CHANNEL_CLOSED)
   }

   private fun processResultSelectReceive(ignoredParam: Any?, selectResult: Any?): Any? {
      if (selectResult === CHANNEL_CLOSED) {
         throw this.receiveException
      } else {
         return selectResult
      }
   }

   private fun processResultSelectReceiveOrNull(ignoredParam: Any?, selectResult: Any?): Any? {
      val var10000: Any
      if (selectResult === CHANNEL_CLOSED) {
         if (this.closeCause != null) {
            throw this.receiveException
         }

         var10000 = null
      } else {
         var10000 = selectResult
      }

      return var10000
   }

   private fun processResultSelectReceiveCatching(ignoredParam: Any?, selectResult: Any?): Any? {
      return ChannelResult.box_impl/* $VF was: box-impl */(
         if (selectResult === CHANNEL_CLOSED)
            ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.closeCause)
            else
            ChannelResult.Companion.success_JP2dKIU/* $VF was: success-JP2dKIU */(selectResult)
      )
   }

   public override operator fun iterator(): ChannelIterator<Any> {
      return BufferedChannel.BufferedChannelIterator()
   }

   protected final val closeCause: Throwable?
      protected final get() {
         return _closeCause$FU.get(this) as java.lang.Throwable
      }


   protected final val sendException: Throwable
      protected final get() {
         var var10000: java.lang.Throwable = this.closeCause
         if (var10000 == null) {
            var10000 = ClosedSendChannelException("Channel was closed")
         }

         return var10000
      }


   private final val receiveException: Throwable
      private final get() {
         var var10000: java.lang.Throwable = this.closeCause
         if (var10000 == null) {
            var10000 = ClosedReceiveChannelException("Channel was closed")
         }

         return var10000
      }


   protected open fun onClosedIdempotent() {
   }

   public override fun close(cause: Throwable?): Boolean {
      return this.closeOrCancelImpl(cause, false)
   }

   public override fun cancel(cause: Throwable?): Boolean {
      return this.cancelImpl$kotlinx_coroutines_core(cause)
   }

   public override fun cancel() {
      this.cancelImpl$kotlinx_coroutines_core(null)
   }

   public override fun cancel(cause: CancellationException?) {
      this.cancelImpl$kotlinx_coroutines_core(cause)
   }

   internal open fun cancelImpl(cause: Throwable?): Boolean {
      var var10001: java.lang.Throwable = cause
      if (cause == null) {
         var10001 = CancellationException("Channel was cancelled")
      }

      return this.closeOrCancelImpl(var10001, true)
   }

   protected open fun closeOrCancelImpl(cause: Throwable?, cancel: Boolean): Boolean {
      if (cancel) {
         this.markCancellationStarted()
      }

      val closedByThisOperation: Boolean = _closeCause$FU.compareAndSet(this, BufferedChannelKt.access$getNO_CLOSE_CAUSE$p(), cause)
      if (cancel) {
         this.markCancelled()
      } else {
         this.markClosed()
      }

      this.completeCloseOrCancel()
      this.onClosedIdempotent()
      if (closedByThisOperation) {
         this.invokeCloseHandler()
      }

      return closedByThisOperation
   }

   private fun invokeCloseHandler() {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = closeHandler$FU

      val var5: Any
      do {
         var5 = `atomicfu$handler$iv`.get(this)
      } while (
         !`atomicfu$handler$iv`.compareAndSet(
            this, var5, if (var5 == null) BufferedChannelKt.access$getCLOSE_HANDLER_CLOSED$p() else BufferedChannelKt.access$getCLOSE_HANDLER_INVOKED$p()
         )
      )

      if (var5 != null) {
         val var10000: Function1 = TypeIntrinsics.beforeCheckcastToFunctionOfArity(var5, 1) as Function1
         (var5 as Function1)(this.closeCause)
      }
   }

   public override fun invokeOnClose(handler: (Throwable?) -> Unit) {
      if (!closeHandler$FU.compareAndSet(this, null, handler)) {
         val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = closeHandler$FU

         do {
            val cur: Any = `atomicfu$handler$iv`.get(this)
            if (cur != BufferedChannelKt.access$getCLOSE_HANDLER_CLOSED$p()) {
               if (cur === BufferedChannelKt.access$getCLOSE_HANDLER_INVOKED$p()) {
                  throw IllegalStateException("Another handler was already registered and successfully invoked".toString())
               }

               throw IllegalStateException(("Another handler is already registered: $cur").toString())
            }
         } while (
            !closeHandler$FU.compareAndSet(this, BufferedChannelKt.access$getCLOSE_HANDLER_CLOSED$p(), BufferedChannelKt.access$getCLOSE_HANDLER_INVOKED$p())
         )

         handler(this.closeCause)
      }
   }

   private fun markClosed() {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = sendersAndCloseStatus$FU

      val var4: Long
      var var10000: Long
      do {
         var4 = `atomicfu$handler$iv`.get(this)
         when ((int)(var4 shr 60)) {
            0 -> {
               var10000 = BufferedChannelKt.access$constructSendersAndCloseStatus(var4 and 1152921504606846975L, 2)
               continue
            }
            1 -> {
               var10000 = BufferedChannelKt.access$constructSendersAndCloseStatus(var4 and 1152921504606846975L, 3)
               continue
            }
            else -> return
         }
      } while (!`atomicfu$handler$iv`.compareAndSet(this, var4, var10000))
   }

   private fun markCancelled() {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = sendersAndCloseStatus$FU

      val var4: Long
      do {
         var4 = `atomicfu$handler$iv`.get(this)
      } while (!`atomicfu$handler$iv`.compareAndSet(this, var4, BufferedChannelKt.access$constructSendersAndCloseStatus(var4 and 1152921504606846975L, 3)))
   }

   private fun markCancellationStarted() {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = sendersAndCloseStatus$FU

      val var4: Long
      do {
         var4 = `atomicfu$handler$iv`.get(this)
         if ((int)(var4 shr 60) != 0) {
            return
         }
      } while (!`atomicfu$handler$iv`.compareAndSet(this, var4, BufferedChannelKt.access$constructSendersAndCloseStatus(var4 and 1152921504606846975L, 1)))
   }

   private fun completeCloseOrCancel() {
      this.isClosedForSend
   }

   protected open val isConflatedDropOldest: Boolean
      protected open get() {
         return false
      }


   private fun completeClose(sendersCur: Long): ChannelSegment<Any> {
      val lastSegment: ChannelSegment = this.closeLinkedList()
      if (this.isConflatedDropOldest) {
         val lastBufferedCellGlobalIndex: Long = this.markAllEmptyCellsAsClosed(lastSegment)
         if (lastBufferedCellGlobalIndex != -1L) {
            this.dropFirstElementUntilTheSpecifiedCellIsInTheBuffer(lastBufferedCellGlobalIndex)
         }
      }

      this.cancelSuspendedReceiveRequests(lastSegment, sendersCur)
      return lastSegment
   }

   private fun completeCancel(sendersCur: Long) {
      this.removeUnprocessedElements(this.completeClose(sendersCur))
   }

   private fun closeLinkedList(): ChannelSegment<Any> {
      var var4: Any = bufferEndSegment$FU.get(this)
      var it: ChannelSegment = sendSegment$FU.get(this) as ChannelSegment
      if (it.id > (var4 as ChannelSegment).id) {
         var4 = it
      }

      it = receiveSegment$FU.get(this) as ChannelSegment
      if (it.id > (var4 as ChannelSegment).id) {
         var4 = it
      }

      return ConcurrentLinkedListKt.close(var4 as ConcurrentLinkedListNode)
   }

   private fun markAllEmptyCellsAsClosed(lastSegment: ChannelSegment<Any>): Long {
      var segment: ChannelSegment = lastSegment

      while (true) {
         label52@ for (index in BufferedChannelKt.SEGMENT_SIZE - 1 downTo 0) {
            val globalIndex: Long = segment.id * BufferedChannelKt.SEGMENT_SIZE + index
            if (segment.id * BufferedChannelKt.SEGMENT_SIZE + index < this.receiversCounter) {
               return -1L
            }

            val state: Any
            do {
               state = segment.getState$kotlinx_coroutines_core(index)
               if (state != null && state != BufferedChannelKt.access$getIN_BUFFER$p()) {
                  if (state === BufferedChannelKt.BUFFERED) {
                     return globalIndex
                  }
                  continue@label52
               }
            } while (!segment.casState$kotlinx_coroutines_core(index, state, CHANNEL_CLOSED))

            segment.onSlotCleaned()
         }

         val var10000: ChannelSegment = segment.getPrev()
         if (var10000 == null) {
            return -1L
         }

         segment = var10000
      }
   }

   private fun removeUnprocessedElements(lastSegment: ChannelSegment<Any>) {
      val onUndeliveredElement: Function1 = this.onUndeliveredElement
      var undeliveredElementException: UndeliveredElementException = null
      var suspendedSenders: Any = InlineList.constructor_impl$default/* $VF was: constructor-impl$default */(null, 1, null)
      var segment: ChannelSegment = lastSegment

      label139@ while (true) {
         label136@ for (var6 in BufferedChannelKt.SEGMENT_SIZE - 1 downTo 0) {
            val globalIndex: Long = segment.id * BufferedChannelKt.SEGMENT_SIZE + var6

            while (true) {
               val var9: Any = segment.getState$kotlinx_coroutines_core(var6)
               if (var9 === BufferedChannelKt.access$getDONE_RCV$p()) {
                  break@label139
               }

               if (var9 === BufferedChannelKt.BUFFERED) {
                  if (globalIndex < this.receiversCounter) {
                     break@label139
                  }

                  if (segment.casState$kotlinx_coroutines_core(var6, var9, CHANNEL_CLOSED)) {
                     if (onUndeliveredElement != null) {
                        undeliveredElementException = OnUndeliveredElementKt.callUndeliveredElementCatchingException(
                           onUndeliveredElement, segment.getElement$kotlinx_coroutines_core(var6), undeliveredElementException
                        )
                     }

                     segment.cleanElement$kotlinx_coroutines_core(var6)
                     segment.onSlotCleaned()
                     continue@label136
                  }
               } else if (var9 != BufferedChannelKt.access$getIN_BUFFER$p() && var9 != null) {
                  if (var9 !is Waiter && var9 !is WaiterEB) {
                     if (var9 === BufferedChannelKt.access$getRESUMING_BY_EB$p() || var9 === BufferedChannelKt.access$getRESUMING_BY_RCV$p()) {
                        break@label139
                     }

                     if (var9 != BufferedChannelKt.access$getRESUMING_BY_EB$p()) {
                        continue@label136
                     }
                  } else {
                     if (globalIndex < this.receiversCounter) {
                        break@label139
                     }

                     val it: Waiter = if (var9 is WaiterEB) (var9 as WaiterEB).waiter else var9 as Waiter
                     if (segment.casState$kotlinx_coroutines_core(var6, var9, CHANNEL_CLOSED)) {
                        if (onUndeliveredElement != null) {
                           undeliveredElementException = OnUndeliveredElementKt.callUndeliveredElementCatchingException(
                              onUndeliveredElement, segment.getElement$kotlinx_coroutines_core(var6), undeliveredElementException
                           )
                        }

                        suspendedSenders = InlineList.plus_FjFbRPM/* $VF was: plus-FjFbRPM */(suspendedSenders, it)
                        segment.cleanElement$kotlinx_coroutines_core(var6)
                        segment.onSlotCleaned()
                        continue@label136
                     }
                  }
               } else if (segment.casState$kotlinx_coroutines_core(var6, var9, CHANNEL_CLOSED)) {
                  segment.onSlotCleaned()
                  continue@label136
               }
            }
         }

         val var10000: ChannelSegment = segment.getPrev()
         if (var10000 == null) {
            break
         }

         segment = var10000
      }

      if (suspendedSenders != null) {
         if (suspendedSenders !is ArrayList) {
            this.resumeSenderOnCancelledChannel(suspendedSenders as Waiter)
         } else {
            val itx: ArrayList = suspendedSenders as ArrayList

            for (var17 in (suspendedSenders as ArrayList).size() - 1 downTo 0) {
               this.resumeSenderOnCancelledChannel(itx.get(var17) as Waiter)
            }
         }
      }

      if (undeliveredElementException != null) {
         throw undeliveredElementException
      }
   }

   private fun cancelSuspendedReceiveRequests(lastSegment: ChannelSegment<Any>, sendersCounter: Long) {
      var suspendedReceivers: Any = InlineList.constructor_impl$default/* $VF was: constructor-impl$default */(null, 1, null)

      // $VF: Unable to resugar Kotlin loop from Java for loop
      run label86@{
         var segment: ChannelSegment = lastSegment
         while (true) {
            if (segment != null) break
            label85@ for (var6 in BufferedChannelKt.SEGMENT_SIZE - 1 downTo 0) {
               if (segment.id * BufferedChannelKt.SEGMENT_SIZE + var6 < sendersCounter) {
                  break@label86
               }

               while (true) {
                  val state: Any = segment.getState$kotlinx_coroutines_core(var6)
                  if (state != null && state != BufferedChannelKt.access$getIN_BUFFER$p()) {
                     if (state is WaiterEB) {
                        if (segment.casState$kotlinx_coroutines_core(var6, state, CHANNEL_CLOSED)) {
                           suspendedReceivers = InlineList.plus_FjFbRPM/* $VF was: plus-FjFbRPM */(suspendedReceivers, (state as WaiterEB).waiter)
                           segment.onCancelledRequest(var6, true)
                           continue@label85
                        }
                     } else {
                        if (state !is Waiter) {
                           continue@label85
                        }

                        if (segment.casState$kotlinx_coroutines_core(var6, state, CHANNEL_CLOSED)) {
                           suspendedReceivers = InlineList.plus_FjFbRPM/* $VF was: plus-FjFbRPM */(suspendedReceivers, state)
                           segment.onCancelledRequest(var6, true)
                           continue@label85
                        }
                     }
                  } else if (segment.casState$kotlinx_coroutines_core(var6, state, CHANNEL_CLOSED)) {
                     segment.onSlotCleaned()
                     continue@label85
                  }
               }
            }

            segment = segment.getPrev()
         }
      }

      if (suspendedReceivers != null) {
         if (suspendedReceivers !is ArrayList) {
            this.resumeReceiverOnClosedChannel(suspendedReceivers as Waiter)
         } else {
            val `list$iv`: ArrayList = suspendedReceivers as ArrayList

            for (`i$iv` in (suspendedReceivers as ArrayList).size() - 1 downTo 0) {
               this.resumeReceiverOnClosedChannel(`list$iv`.get(`i$iv`) as Waiter)
            }
         }
      }
   }

   private fun Waiter.resumeReceiverOnClosedChannel() {
      this.resumeWaiterOnClosedChannel(`$this$resumeReceiverOnClosedChannel`, true)
   }

   private fun Waiter.resumeSenderOnCancelledChannel() {
      this.resumeWaiterOnClosedChannel(`$this$resumeSenderOnCancelledChannel`, false)
   }

   private fun Waiter.resumeWaiterOnClosedChannel(receiver: Boolean) {
      if (`$this$resumeWaiterOnClosedChannel` is BufferedChannel.SendBroadcast) {
         (`$this$resumeWaiterOnClosedChannel` as BufferedChannel.SendBroadcast).cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(false))
      } else if (`$this$resumeWaiterOnClosedChannel` is CancellableContinuation) {
         (`$this$resumeWaiterOnClosedChannel` as Continuation)
            .resumeWith(
               Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(if (receiver) this.receiveException else this.sendException))
            )
         } else if (`$this$resumeWaiterOnClosedChannel` is ReceiveCatching) {
         (`$this$resumeWaiterOnClosedChannel` as ReceiveCatching)
            .cont
            .resumeWith(
               Result.constructor_impl/* $VF was: constructor-impl */(
                  ChannelResult.box_impl/* $VF was: box-impl */(ChannelResult.Companion.closed_JP2dKIU/* $VF was: closed-JP2dKIU */(this.closeCause))
               )
            )
         } else if (`$this$resumeWaiterOnClosedChannel` is BufferedChannel.BufferedChannelIterator) {
         (`$this$resumeWaiterOnClosedChannel` as BufferedChannel.BufferedChannelIterator).tryResumeHasNextOnClosedChannel()
      } else {
         if (`$this$resumeWaiterOnClosedChannel` !is SelectInstance) {
            throw IllegalStateException(("Unexpected waiter: $`$this$resumeWaiterOnClosedChannel`").toString())
         }

         (`$this$resumeWaiterOnClosedChannel` as SelectInstance).trySelect(this, CHANNEL_CLOSED)
      }
   }

   @ExperimentalCoroutinesApi
   public open val isClosedForSend: Boolean
      public open get() {
         return this.isClosedForSend0
      }


   private final val isClosedForSend0: Boolean
      private final get() {
         return this.isClosed(`$this$isClosedForSend0`, false)
      }


   @ExperimentalCoroutinesApi
   public open val isClosedForReceive: Boolean
      public open get() {
         return this.isClosedForReceive0
      }


   private final val isClosedForReceive0: Boolean
      private final get() {
         return this.isClosed(`$this$isClosedForReceive0`, true)
      }


   private fun isClosed(sendersAndCloseStatusCur: Long, isClosedForReceive: Boolean): Boolean {
      var var14: Boolean
      when ((int)(sendersAndCloseStatusCur shr 60)) {
         0 -> var14 = false
         1 -> var14 = false
         2 -> {
            this.completeClose(sendersAndCloseStatusCur and 1152921504606846975L)
            var14 = !isClosedForReceive || !this.hasElements$kotlinx_coroutines_core()
         }
         3 -> {
            this.completeCancel(sendersAndCloseStatusCur and 1152921504606846975L)
            var14 = true
         }
         else -> throw IllegalStateException(("unexpected close status: ${(int)(sendersAndCloseStatusCur shr 60)}").toString())
      }

      return var14
   }

   @ExperimentalCoroutinesApi
   public open val isEmpty: Boolean
      public open get() {
         return !this.isClosedForReceive && !this.hasElements$kotlinx_coroutines_core() && !this.isClosedForReceive
      }


   internal fun hasElements(): Boolean {
      while (true) {
         var segment: ChannelSegment = receiveSegment$FU.get(this) as ChannelSegment
         val r: Long = this.receiversCounter
         if (this.sendersCounter <= r) {
            return false
         }

         val id: Long = r / BufferedChannelKt.SEGMENT_SIZE
         if (segment.id != r / BufferedChannelKt.SEGMENT_SIZE) {
            val var10000: ChannelSegment = this.findSegmentReceive(id, segment)
            if (var10000 == null) {
               if ((receiveSegment$FU.get(this) as ChannelSegment).id >= id) {
                  continue
               }

               return false
            }

            segment = var10000
         }

         segment.cleanPrev()
         if (this.isCellNonEmpty(segment, (int)(r % (long)BufferedChannelKt.SEGMENT_SIZE), r)) {
            return true
         }

         receivers$FU.compareAndSet(this, r, r + 1L)
      }
   }

   private fun isCellNonEmpty(segment: ChannelSegment<Any>, index: Int, globalIndex: Long): Boolean {
      val state: Any
      do {
         state = segment.getState$kotlinx_coroutines_core(index)
         if (state != null && state != BufferedChannelKt.access$getIN_BUFFER$p()) {
            if (state === BufferedChannelKt.BUFFERED) {
               return true
            }

            if (state === BufferedChannelKt.access$getINTERRUPTED_SEND$p()) {
               return false
            }

            if (state === CHANNEL_CLOSED) {
               return false
            }

            if (state === BufferedChannelKt.access$getDONE_RCV$p()) {
               return false
            }

            if (state === BufferedChannelKt.access$getPOISONED$p()) {
               return false
            }

            if (state === BufferedChannelKt.access$getRESUMING_BY_EB$p()) {
               return true
            }

            if (state === BufferedChannelKt.access$getRESUMING_BY_RCV$p()) {
               return false
            }

            return globalIndex == this.receiversCounter
         }
      } while (!segment.casState$kotlinx_coroutines_core(index, state, BufferedChannelKt.access$getPOISONED$p()))

      this.expandBuffer()
      return false
   }

   private fun findSegmentSend(id: Long, startFrom: ChannelSegment<Any>): ChannelSegment<Any>? {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = sendSegment$FU
      val it: Function2 = BufferedChannelKt.createSegmentFunction() as Function2

      var segment: Any
      label82@ while (true) {
         segment = ConcurrentLinkedListKt.findSegmentInternal(startFrom, id, it)
         if (isClosed) {
            break
         }

         val var8: Segment = segment

         while (true) {
            val `cur$iv$iv`: Segment = `atomicfu$handler$iv`.get(this) as Segment
            val var10000: Boolean
            if (`cur$iv$iv`.id >= var8.id) {
               var10000 = true
            } else if (!var8.tryIncPointers$kotlinx_coroutines_core()) {
               var10000 = false
            } else {
               if (!`atomicfu$handler$iv`.compareAndSet(this, `cur$iv$iv`, var8)) {
                  if (var8.decPointers$kotlinx_coroutines_core()) {
                     var8.remove()
                  }
                  continue
               }

               if (`cur$iv$iv`.decPointers$kotlinx_coroutines_core()) {
                  `cur$iv$iv`.remove()
               }

               var10000 = true
            }

            if (var10000) {
               break@label82
            }
            continue@label82
         }
      }

      val var17: ChannelSegment
      if (isClosed) {
         this.completeCloseOrCancel()
         if (startFrom.id * BufferedChannelKt.SEGMENT_SIZE < this.receiversCounter) {
            startFrom.cleanPrev()
         }

         var17 = null
      } else {
         segment = segment as ChannelSegment
         if (((ChannelSegment)segment).id > id) {
            this.updateSendersCounterIfLower(((ChannelSegment)segment).id * (long)BufferedChannelKt.SEGMENT_SIZE)
            if (((ChannelSegment)segment).id * BufferedChannelKt.SEGMENT_SIZE < this.receiversCounter) {
               ((ChannelSegment)segment).cleanPrev()
            }

            var17 = null
         } else {
            if (ASSERTIONS_ENABLED && ((ChannelSegment)segment).id != id) {
               throw AssertionError()
            }

            var17 = (ChannelSegment)segment
         }
      }

      return var17
   }

   private fun findSegmentReceive(id: Long, startFrom: ChannelSegment<Any>): ChannelSegment<Any>? {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = receiveSegment$FU
      val it: Function2 = BufferedChannelKt.createSegmentFunction() as Function2

      var segment: Any
      label116@ while (true) {
         segment = ConcurrentLinkedListKt.findSegmentInternal(startFrom, id, it)
         if (isClosed) {
            break
         }

         val var8: Segment = segment

         while (true) {
            val `cur$iv`: Segment = `atomicfu$handler$iv`.get(this) as Segment
            val var10000: Boolean
            if (`cur$iv`.id >= var8.id) {
               var10000 = true
            } else if (!var8.tryIncPointers$kotlinx_coroutines_core()) {
               var10000 = false
            } else {
               if (!`atomicfu$handler$iv`.compareAndSet(this, `cur$iv`, var8)) {
                  if (var8.decPointers$kotlinx_coroutines_core()) {
                     var8.remove()
                  }
                  continue
               }

               if (`cur$iv`.decPointers$kotlinx_coroutines_core()) {
                  `cur$iv`.remove()
               }

               var10000 = true
            }

            if (var10000) {
               break@label116
            }
            continue@label116
         }
      }

      val var22: ChannelSegment
      if (isClosed) {
         this.completeCloseOrCancel()
         if (startFrom.id * BufferedChannelKt.SEGMENT_SIZE < this.sendersCounter) {
            startFrom.cleanPrev()
         }

         var22 = null
      } else {
         segment = segment as ChannelSegment
         if (!this.isRendezvousOrUnlimited && id <= this.bufferEndCounter / BufferedChannelKt.SEGMENT_SIZE) {
            val var16: AtomicReferenceFieldUpdater = bufferEndSegment$FU

            while (true) {
               val var20: Segment = var16.get(this) as Segment
               if (var20.id >= (segment as Segment).id || !(segment as Segment).tryIncPointers$kotlinx_coroutines_core()) {
                  break
               }

               if (var16.compareAndSet(this, var20, segment as Segment)) {
                  if (var20.decPointers$kotlinx_coroutines_core()) {
                     var20.remove()
                  }
                  break
               }

               if ((segment as Segment).decPointers$kotlinx_coroutines_core()) {
                  (segment as Segment).remove()
               }
            }
         }

         if (((ChannelSegment)segment).id > id) {
            this.updateReceiversCounterIfLower(((ChannelSegment)segment).id * (long)BufferedChannelKt.SEGMENT_SIZE)
            if (((ChannelSegment)segment).id * BufferedChannelKt.SEGMENT_SIZE < this.sendersCounter) {
               ((ChannelSegment)segment).cleanPrev()
            }

            var22 = null
         } else {
            if (ASSERTIONS_ENABLED && ((ChannelSegment)segment).id != id) {
               throw AssertionError()
            }

            var22 = (ChannelSegment)segment
         }
      }

      return var22
   }

   private fun findSegmentBufferEnd(id: Long, startFrom: ChannelSegment<Any>, currentBufferEndCounter: Long): ChannelSegment<Any>? {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = bufferEndSegment$FU
      val it: Function2 = BufferedChannelKt.createSegmentFunction() as Function2

      var segment: Any
      label79@ while (true) {
         segment = ConcurrentLinkedListKt.findSegmentInternal(startFrom, id, it)
         if (isClosed) {
            break
         }

         val var10: Segment = segment

         while (true) {
            val `cur$iv$iv`: Segment = `atomicfu$handler$iv`.get(this) as Segment
            val var10000: Boolean
            if (`cur$iv$iv`.id >= var10.id) {
               var10000 = true
            } else if (!var10.tryIncPointers$kotlinx_coroutines_core()) {
               var10000 = false
            } else {
               if (!`atomicfu$handler$iv`.compareAndSet(this, `cur$iv$iv`, var10)) {
                  if (var10.decPointers$kotlinx_coroutines_core()) {
                     var10.remove()
                  }
                  continue
               }

               if (`cur$iv$iv`.decPointers$kotlinx_coroutines_core()) {
                  `cur$iv$iv`.remove()
               }

               var10000 = true
            }

            if (var10000) {
               break@label79
            }
            continue@label79
         }
      }

      val var19: ChannelSegment
      if (isClosed) {
         this.completeCloseOrCancel()
         this.moveSegmentBufferEndToSpecifiedOrLast(id, startFrom)
         incCompletedExpandBufferAttempts$default(this, 0L, 1, null)
         var19 = null
      } else {
         segment = segment as ChannelSegment
         if (((ChannelSegment)segment).id > id) {
            if (bufferEnd$FU.compareAndSet(this, currentBufferEndCounter + 1L, ((ChannelSegment)segment).id * (long)BufferedChannelKt.SEGMENT_SIZE)) {
               this.incCompletedExpandBufferAttempts(((ChannelSegment)segment).id * (long)BufferedChannelKt.SEGMENT_SIZE - currentBufferEndCounter)
            } else {
               incCompletedExpandBufferAttempts$default(this, 0L, 1, null)
            }

            var19 = null
         } else {
            if (ASSERTIONS_ENABLED && ((ChannelSegment)segment).id != id) {
               throw AssertionError()
            }

            var19 = (ChannelSegment)segment
         }
      }

      return var19
   }

   private fun moveSegmentBufferEndToSpecifiedOrLast(id: Long, startFrom: ChannelSegment<Any>) {
      var segment: ChannelSegment = startFrom

      while (segment.id < id) {
         val var10000: ChannelSegment = segment.getNext()
         if (var10000 == null) {
            break
         }

         segment = var10000
      }

      var var11: Boolean
      label82@ do {
         while (segment.isRemoved()) {
            val var10: ChannelSegment = segment.getNext()
            if (var10 == null) {
               break
            }

            segment = var10
         }

         val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = bufferEndSegment$FU

         while (true) {
            val `cur$iv`: Segment = `atomicfu$handler$iv`.get(this) as Segment
            if (`cur$iv`.id >= segment.id) {
               var11 = true
               continue@label82
            }

            if (!segment.tryIncPointers$kotlinx_coroutines_core()) {
               var11 = false
               continue@label82
            }

            if (`atomicfu$handler$iv`.compareAndSet(this, `cur$iv`, segment)) {
               if (`cur$iv`.decPointers$kotlinx_coroutines_core()) {
                  `cur$iv`.remove()
               }

               var11 = true
               continue@label82
            }

            if (segment.decPointers$kotlinx_coroutines_core()) {
               segment.remove()
            }
         }
      } while (!var11)
   }

   private fun updateSendersCounterIfLower(value: Long) {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = sendersAndCloseStatus$FU

      val cur: Long
      val curCounter: Long
      do {
         cur = `atomicfu$handler$iv`.get(this)
         curCounter = cur and 1152921504606846975L
         if ((cur and 1152921504606846975L) >= value) {
            return
         }
      } while (!sendersAndCloseStatus$FU.compareAndSet(this, cur, BufferedChannelKt.access$constructSendersAndCloseStatus(curCounter, (int)(cur shr 60))))
   }

   private fun updateReceiversCounterIfLower(value: Long) {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = receivers$FU

      val cur: Long
      do {
         cur = `atomicfu$handler$iv`.get(this)
         if (cur >= value) {
            return
         }
      } while (!receivers$FU.compareAndSet(this, cur, value))
   }

   public override fun toString(): String {
      val sb: StringBuilder = StringBuilder()
      when ((int)(sendersAndCloseStatus$FU.get(this) shr 60)) {
         2 -> sb.append("closed,")
         3 -> sb.append("cancelled,")
         else -> {}
      }

      sb.append("capacity=${this.capacity},")
      sb.append("data=[")
      val var23: java.lang.Iterable = CollectionsKt.listOf(arrayOf(receiveSegment$FU.get(this), sendSegment$FU.get(this), bufferEndSegment$FU.get(this)))
      val `minElem$iv`: java.util.Collection = ArrayList()

      for (`v$iv` in var23) {
         if (`v$iv` as ChannelSegment != BufferedChannelKt.access$getNULL_SEGMENT$p()) {
            `minElem$iv`.add(`v$iv`)
         }
      }

      val var24: java.util.Iterator = (`minElem$iv` as java.util.List).iterator()
      if (!var24.hasNext()) {
         throw NoSuchElementException()
      } else {
         var var26: Any = var24.next()
         var var10000: Any
         if (!var24.hasNext()) {
            var10000 = (ChannelSegment)var26
         } else {
            var var28: Long = (var26 as ChannelSegment).id

            do {
               val var31: Any = var24.next()
               if (var28 > (var31 as ChannelSegment).id) {
                  var26 = var31
                  var28 = (var31 as ChannelSegment).id
               }
            } while (var24.hasNext())

            var10000 = (ChannelSegment)var26
         }

         val firstSegment: ChannelSegment = var10000
         val var21: Long = this.receiversCounter
         val var25: Long = this.sendersCounter
         var var29: ChannelSegment = firstSegment

         label215@ while (true) {
            var var32: Int = 0

            for (var35 in BufferedChannelKt.SEGMENT_SIZE..var32) {
               val var37: Long = var29.id * BufferedChannelKt.SEGMENT_SIZE + var32
               if (var29.id * BufferedChannelKt.SEGMENT_SIZE + var32 >= var25 && var29.id * BufferedChannelKt.SEGMENT_SIZE + var32 >= var21) {
                  break@label215
               }

               val cellState: Any = var29.getState$kotlinx_coroutines_core(var32)
               val element: Any = var29.getElement$kotlinx_coroutines_core(var32)
               val var38: java.lang.String
               if (cellState is CancellableContinuation) {
                  var38 = if (var37 < var21 && var37 >= var25) "receive" else (if (var37 < var25 && var37 >= var21) "send" else "cont")
               } else if (cellState is SelectInstance) {
                  var38 = if (var37 < var21 && var37 >= var25) "onReceive" else (if (var37 < var25 && var37 >= var21) "onSend" else "select")
               } else if (cellState is ReceiveCatching) {
                  var38 = "receiveCatching"
               } else if (cellState is BufferedChannel.SendBroadcast) {
                  var38 = "sendBroadcast"
               } else if (cellState is WaiterEB) {
                  var38 = "EB($cellState)"
               } else if (cellState == BufferedChannelKt.access$getRESUMING_BY_RCV$p() || cellState == BufferedChannelKt.access$getRESUMING_BY_EB$p()) {
                  var38 = "resuming_sender"
               } else {
                  if (cellState == null
                     || cellState == BufferedChannelKt.access$getIN_BUFFER$p()
                     || cellState == BufferedChannelKt.access$getDONE_RCV$p()
                     || cellState == BufferedChannelKt.access$getPOISONED$p()
                     || cellState == BufferedChannelKt.access$getINTERRUPTED_RCV$p()
                     || cellState == BufferedChannelKt.access$getINTERRUPTED_SEND$p()
                     || cellState == CHANNEL_CLOSED) {
                     continue
                  }

                  var38 = cellState.toString()
               }

               if (element != null) {
                  sb.append("($var38,$element),")
               } else {
                  sb.append("$var38,")
               }
            }

            var10000 = var29.getNext()
            if (var10000 == null) {
               break
            }

            var29 = var10000
         }

         if (StringsKt.last(sb) == ',') {
         }

         sb.append("]")
         return sb.toString()
      }
   }

   internal fun toStringDebug(): String {
      val sb: StringBuilder = StringBuilder()
sb.append(
   "S=${this.sendersCounter},R=${this.receiversCounter},B=${this.bufferEndCounter},B'=${completedExpandBuffersAndPauseFlag$FU.get(this)},C=${(int)(
      sendersAndCloseStatus$FU.get(this) shr 60
   )},"
)
      when ((int)(sendersAndCloseStatus$FU.get(this) shr 60)) {
         1 -> sb.append("CANCELLATION_STARTED,")
         2 -> sb.append("CLOSED,")
         3 -> sb.append("CANCELLED,")
         else -> {}
      }

      sb.append("SEND_SEGM=${hexAddress},RCV_SEGM=${hexAddress}")
      if (!this.isRendezvousOrUnlimited) {
         sb.append(",EB_SEGM=${hexAddress}")
      }

      sb.append("  ")
      val var23: java.lang.Iterable = CollectionsKt.listOf(arrayOf(receiveSegment$FU.get(this), sendSegment$FU.get(this), bufferEndSegment$FU.get(this)))
      val i: java.util.Collection = ArrayList()

      for (element in var23) {
         if (element as ChannelSegment != BufferedChannelKt.access$getNULL_SEGMENT$p()) {
            i.add(element)
         }
      }

      val var24: java.util.Iterator = (i as java.util.List).iterator()
      if (!var24.hasNext()) {
         throw NoSuchElementException()
      } else {
         var var26: Any = var24.next()
         var var10000: Any
         if (!var24.hasNext()) {
            var10000 = (ChannelSegment)var26
         } else {
            var var29: Long = (var26 as ChannelSegment).id

            do {
               val var32: Any = var24.next()
               if (var29 > (var32 as ChannelSegment).id) {
                  var26 = var32
                  var29 = (var32 as ChannelSegment).id
               }
            } while (var24.hasNext())

            var10000 = (ChannelSegment)var26
         }

         var var19: ChannelSegment = var10000

         while (true) {
            var var41: StringBuilder = StringBuilder()
               .append(hexAddress)
               .append("=[")
               .append(if (var19.isRemoved()) "*" else "")
               .append(var19.id)
               .append(",prev=")
               var var10002: ChannelSegment = var19.getPrev()
            sb.append(var41.append(if (var10002 != null) hexAddress else null).append(',').toString())
            val var22: Int = BufferedChannelKt.SEGMENT_SIZE

            repeat(var22) { var25 ->
               val var33: Any = var19.getState$kotlinx_coroutines_core(var25)
               sb.append(
                  "[$var25]=(${if (var33 is CancellableContinuation)
                     "cont"
                     else
                     (
                        if (var33 is SelectInstance)
                           "select"
                           else
                           (
                              if (var33 is ReceiveCatching)
                                 "receiveCatching"
                                 else
                                 (
                                    if (var33 is BufferedChannel.SendBroadcast)
                                       "send(broadcast)"
                                       else
                                       (if (var33 is WaiterEB) "EB($var33)" else java.lang.String.valueOf(var33))
                                 )
                           )
                     )},${var19.getElement$kotlinx_coroutines_core(var25)}),"
               )
            }

            var41 = StringBuilder().append("next=")
            var10002 = var19.getNext()
            sb.append(var41.append(if (var10002 != null) hexAddress else null).append("]  ").toString())
            var10000 = var19.getNext()
            if (var10000 == null) {
               return sb.toString()
            }

            var19 = var10000
         }
      }
   }

   public fun checkSegmentStructureInvariants() {
      if (this.isRendezvousOrUnlimited) {
         if (bufferEndSegment$FU.get(this) != BufferedChannelKt.access$getNULL_SEGMENT$p()) {
            throw IllegalStateException(
               ("bufferEndSegment must be NULL_SEGMENT for rendezvous and unlimited channels; they do not manipulate it.\nChannel state: $this").toString()
            )
         }
      } else if ((receiveSegment$FU.get(this) as ChannelSegment).id > (bufferEndSegment$FU.get(this) as ChannelSegment).id) {
         throw IllegalStateException(("bufferEndSegment should not have lower id than receiveSegment.\nChannel state: $this").toString())
      }

      val i: java.lang.Iterable = CollectionsKt.listOf(arrayOf(receiveSegment$FU.get(this), sendSegment$FU.get(this), bufferEndSegment$FU.get(this)))
      val var5: java.util.Collection = ArrayList()

      for (`v$iv` in i) {
         if (`v$iv` as ChannelSegment != BufferedChannelKt.access$getNULL_SEGMENT$p()) {
            var5.add(`v$iv`)
         }
      }

      val var24: java.util.Iterator = (var5 as java.util.List).iterator()
      if (!var24.hasNext()) {
         throw NoSuchElementException()
      } else {
         var var28: Any = var24.next()
         var var10000: Any
         if (!var24.hasNext()) {
            var10000 = (ChannelSegment)var28
         } else {
            var var33: Long = (var28 as ChannelSegment).id

            do {
               val var36: Any = var24.next()
               if (var33 > (var36 as ChannelSegment).id) {
                  var28 = var36
                  var33 = (var36 as ChannelSegment).id
               }
            } while (var24.hasNext())

            var10000 = (ChannelSegment)var28
         }

         val firstSegment: ChannelSegment = var10000
         if (var10000.getPrev() != null) {
            throw IllegalStateException(
               ("All processed segments should be unreachable from the data structure, but the `prev` link of the leftmost segment is non-null.\nChannel state: $this")
                  .toString()
            )
         } else {
            var var17: ChannelSegment = firstSegment

            while (var17.getNext() != null) {
               run label197@{
                  var10000 = var17.getNext()
                  if (var10000.getPrev() != null) {
                     var10000 = var17.getNext()
                     if (var10000.getPrev() != var17) {
                        var44 = false
                        return@label197
                     }
                  }

                  var44 = true
               }

               if (!var44) {
                  throw IllegalStateException(("The `segment.next.prev === segment` invariant is violated.\nChannel state: $this").toString())
               }

               var var21: Int = 0
               var var25: Int = 0

               for (var29 in BufferedChannelKt.SEGMENT_SIZE..var25) {
                  val var34: Any = var17.getState$kotlinx_coroutines_core(var25)
                  if (!(var34 == BufferedChannelKt.BUFFERED) && var34 !is Waiter) {
                     if (var34 == BufferedChannelKt.access$getINTERRUPTED_RCV$p()
                        || var34 == BufferedChannelKt.access$getINTERRUPTED_SEND$p()
                        || var34 == CHANNEL_CLOSED) {
                        if (var17.getElement$kotlinx_coroutines_core(var25) != null) {
                           throw IllegalStateException("Check failed.".toString())
                        }

                        var21++
                     } else {
                        if (!(var34 == BufferedChannelKt.access$getPOISONED$p()) && !(var34 == BufferedChannelKt.access$getDONE_RCV$p())) {
                           throw IllegalStateException(("Unexpected segment cell state: $var34.\nChannel state: $this").toString())
                        }

                        if (var17.getElement$kotlinx_coroutines_core(var25) != null) {
                           throw IllegalStateException("Check failed.".toString())
                        }
                     }
                  }
               }

               if (var21 == BufferedChannelKt.SEGMENT_SIZE
                  && var17 != receiveSegment$FU.get(this)
                  && var17 != sendSegment$FU.get(this)
                  && var17 != bufferEndSegment$FU.get(this)) {
                  throw IllegalStateException(("Logically removed segment is reachable.\nChannel state: $this").toString())
               }

               var10000 = var17.getNext()
               var17 = var10000
            }
         }
      }
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated in the favour of 'trySend' method", replaceWith = @ReplaceWith(expression = "trySend(element).isSuccess", imports = []), level = DeprecationLevel.ERROR)
   override fun offer(element: E): Boolean {
      Channel.DefaultImpls.offer(this, (E)element)
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated in the favour of 'tryReceive'. Please note that the provided replacement does not rethrow channel's close cause as 'poll' did, for the precise replacement please refer to the 'poll' documentation", replaceWith = @ReplaceWith(expression = "tryReceive().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
   override fun poll(): E {
      Channel.DefaultImpls.poll(this)
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated in favor of 'receiveCatching'. Please note that the provided replacement does not rethrow channel's close cause as 'receiveOrNull' did, for the detailed replacement please refer to the 'receiveOrNull' documentation", replaceWith = @ReplaceWith(expression = "receiveCatching().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
   @LowPriorityInOverloadResolution
   override fun receiveOrNull(`$completion`: Continuation<in E>) {
      Channel.DefaultImpls.receiveOrNull(this, `$completion`)
   }

   fun `update$atomicfu`(`atomicfu$handler`: AtomicLongFieldUpdater, `atomicfu$action`: (java.lang.Long?) -> java.lang.Long, `atomicfu$dispatchReceiver`: Any) {
      val var5: Long
      do {
         var5 = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`)
      } while (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, var5, (`atomicfu$action`(var5) as java.lang.Number).longValue()))
   }

   fun `getAndUpdate$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Any, `atomicfu$dispatchReceiver`: Any) {
      val var5: Any
      do {
         var5 = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`)
      } while (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, var5, `atomicfu$action`(var5)))

      var5
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicLongFieldUpdater, `atomicfu$action`: (java.lang.Long?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$onClosedSend`(`$this`: BufferedChannel, element: Any, `$completion`: Continuation) {
      `$this`.onClosedSend(element, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$sendOnNoWaiterSuspend`(`$this`: BufferedChannel, segment: ChannelSegment, index: Int, element: Any, s: Long, `$completion`: Continuation) {
      `$this`.sendOnNoWaiterSuspend(segment, index, element, s, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getSendSegment$FU$p`(): AtomicReferenceFieldUpdater {
      sendSegment$FU
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getSendersAndCloseStatus$FU$p`(): AtomicLongFieldUpdater {
      sendersAndCloseStatus$FU
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$isClosedForSend0`(`$this`: BufferedChannel, `$receiver`: Long): Boolean {
      `$this`.isClosedForSend0
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$findSegmentSend`(`$this`: BufferedChannel, id: Long, startFrom: ChannelSegment): ChannelSegment {
      `$this`.findSegmentSend(id, startFrom)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$updateCellSend`(`$this`: BufferedChannel, segment: ChannelSegment, index: Int, element: Any, s: Long, waiter: Any, closed: Boolean): Int {
      `$this`.updateCellSend(segment, index, element, s, waiter, closed)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$prepareSenderForSuspension`(`$this`: BufferedChannel, `$receiver`: Waiter, segment: ChannelSegment, index: Int) {
      `$this`.prepareSenderForSuspension(`$receiver`, segment, index)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$receiveOnNoWaiterSuspend`(`$this`: BufferedChannel, segment: ChannelSegment, index: Int, r: Long, `$completion`: Continuation) {
      `$this`.receiveOnNoWaiterSuspend(segment, index, r, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$onClosedSendOnNoWaiterSuspend`(`$this`: BufferedChannel, element: Any, cont: CancellableContinuation) {
      `$this`.onClosedSendOnNoWaiterSuspend(element, cont)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$onClosedReceiveOnNoWaiterSuspend`(`$this`: BufferedChannel, cont: CancellableContinuation) {
      `$this`.onClosedReceiveOnNoWaiterSuspend(cont)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$onClosedReceiveCatchingOnNoWaiterSuspend`(`$this`: BufferedChannel, cont: CancellableContinuation) {
      `$this`.onClosedReceiveCatchingOnNoWaiterSuspend(cont)
   }

   @SourceDebugExtension(["SMAP\nBufferedChannel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel$BufferedChannelIterator\n+ 2 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel\n+ 3 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n+ 4 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel$receiveImpl$1\n+ 5 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n+ 6 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,3055:1\n886#2,52:3056\n964#2,8:3112\n858#2:3120\n882#2,33:3121\n974#2:3154\n916#2,14:3155\n935#2,3:3170\n979#2,6:3173\n332#3,4:3108\n336#3,8:3179\n882#4:3169\n61#5,2:3187\n61#5,2:3190\n1#6:3189\n*S KotlinDebug\n*F\n+ 1 BufferedChannel.kt\nkotlinx/coroutines/channels/BufferedChannel$BufferedChannelIterator\n*L\n1590#1:3056,52\n1627#1:3112,8\n1627#1:3120\n1627#1:3121,33\n1627#1:3154\n1627#1:3155,14\n1627#1:3170,3\n1627#1:3173,6\n1625#1:3108,4\n1625#1:3179,8\n1627#1:3169\n1663#1:3187,2\n1708#1:3190,2\n*E\n"])
   private inner class BufferedChannelIterator : ChannelIterator<E>, Waiter {
      private final var receiveResult: Any? = BufferedChannelKt.access$getNO_RECEIVE_RESULT$p()
      private final var continuation: CancellableContinuationImpl<Boolean>?

      public override suspend operator fun hasNext(): Boolean {
         val `this_$iv`: BufferedChannel = BufferedChannel.this
         val `waiter$iv`: Any = null
         var `segment$iv`: ChannelSegment = BufferedChannel.receiveSegment$FU.get(`this_$iv`) as ChannelSegment

         var var21: java.lang.Boolean
         while (true) {
            if (`this_$iv`.isClosedForReceive) {
               var21 = Boxing.boxBoolean(this.onClosedHasNext())
               break
            }

            val `r$iv`: Long = BufferedChannel.receivers$FU.getAndIncrement(`this_$iv`)
            val `id$iv`: Long = `r$iv` / BufferedChannelKt.SEGMENT_SIZE
            val `i$iv`: Int = (int)(`r$iv` % BufferedChannelKt.SEGMENT_SIZE)
            if (`segment$iv`.id != `id$iv`) {
               val var10000: ChannelSegment = `this_$iv`.findSegmentReceive(`id$iv`, `segment$iv`)
               if (var10000 == null) {
                  continue
               }

               `segment$iv` = var10000
            }

            val `updCellResult$iv`: Any = `this_$iv`.updateCellReceive(`segment$iv`, `i$iv`, `r$iv`, `waiter$iv`)
            if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND$p()) {
               throw IllegalStateException("unreachable".toString())
            }

            if (`updCellResult$iv` != BufferedChannelKt.access$getFAILED$p()) {
               if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()) {
                  return this.hasNextOnNoWaiterSuspend(`segment$iv`, `i$iv`, `r$iv`, `$completion`)
               }

               `segment$iv`.cleanPrev()
               this.receiveResult = `updCellResult$iv`
               var21 = Boxing.boxBoolean(true)
               break
            }

            if (`r$iv` < `this_$iv`.sendersCounter) {
               `segment$iv`.cleanPrev()
            }
         }

         return var21
      }

      private fun onClosedHasNext(): Boolean {
         this.receiveResult = CHANNEL_CLOSED
         val var10000: java.lang.Throwable = BufferedChannel.this.closeCause
         if (var10000 == null) {
            return false
         } else {
            throw StackTraceRecoveryKt.recoverStackTrace(var10000)
         }
      }

      private suspend fun hasNextOnNoWaiterSuspend(segment: ChannelSegment<Any>, index: Int, r: Long): Boolean {
         val var6: BufferedChannel = BufferedChannel.this
         val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationKt.getOrCreateCancellableContinuation(
            IntrinsicsKt.intercepted(`$completion`)
         )

         try {
            access$setContinuation$p(this, `cancellable$iv`)
            val `this_$iv`: BufferedChannel = var6
            val `updCellResult$iv`: Any = var6.updateCellReceive(segment, index, r, this)
            if (`updCellResult$iv` === BufferedChannelKt.access$getSUSPEND$p()) {
               var6.prepareReceiverForSuspension(this, segment, index)
            } else if (`updCellResult$iv` != BufferedChannelKt.access$getFAILED$p()) {
               segment.cleanPrev()
               access$setReceiveResult$p(this, `updCellResult$iv`)
               access$setContinuation$p(this, null)
               `cancellable$iv`.resume(
                  Boxing.boxBoolean(true),
                  if (var6.onUndeliveredElement != null)
                     OnUndeliveredElementKt.bindCancellationFun(var6.onUndeliveredElement, `updCellResult$iv`, `cancellable$iv`.context)
                     else
                     null
               )
            } else {
               if (r < var6.sendersCounter) {
                  segment.cleanPrev()
               }

               var `segment$iv$iv`: ChannelSegment = BufferedChannel.receiveSegment$FU.get(var6) as ChannelSegment

               label113@ while (true) {
                  val `r$iv$iv`: Long
                  val `i$iv$iv`: Int
                  while (true) {
                     if (`this_$iv`.isClosedForReceive) {
                        access$onClosedHasNextNoWaiterSuspend(this)
                        break@label113
                     }

                     `r$iv$iv` = BufferedChannel.receivers$FU.getAndIncrement(`this_$iv`)
                     val `id$iv$iv`: Long = `r$iv$iv` / BufferedChannelKt.SEGMENT_SIZE
                     `i$iv$iv` = (int)(`r$iv$iv` % BufferedChannelKt.SEGMENT_SIZE)
                     if (`segment$iv$iv`.id == `id$iv$iv`) {
                        break
                     }

                     val var10000: ChannelSegment = `this_$iv`.findSegmentReceive(`id$iv$iv`, `segment$iv$iv`)
                     if (var10000 != null) {
                        `segment$iv$iv` = var10000
                        break
                     }
                  }

                  val `updCellResult$iv$iv`: Any = `this_$iv`.updateCellReceive(`segment$iv$iv`, `i$iv$iv`, `r$iv$iv`, this)
                  if (`updCellResult$iv$iv` === BufferedChannelKt.access$getSUSPEND$p()) {
                     val var35: Waiter = this as? Waiter
                     if ((this as? Waiter) != null) {
                        `this_$iv`.prepareReceiverForSuspension(var35, `segment$iv$iv`, `i$iv$iv`)
                     }
                  }

                  if (`updCellResult$iv$iv` != BufferedChannelKt.access$getFAILED$p()) {
                     if (`updCellResult$iv$iv` === BufferedChannelKt.access$getSUSPEND_NO_WAITER$p()) {
                        throw IllegalStateException("unexpected".toString())
                     }

                     `segment$iv$iv`.cleanPrev()
                     access$setReceiveResult$p(this, `updCellResult$iv$iv`)
                     access$setContinuation$p(this, null)
                     `cancellable$iv`.resume(
                        Boxing.boxBoolean(true),
                        if (var6.onUndeliveredElement != null)
                           OnUndeliveredElementKt.bindCancellationFun(var6.onUndeliveredElement, `updCellResult$iv$iv`, `cancellable$iv`.context)
                           else
                           null
                     )
                     break
                  }

                  if (`r$iv$iv` < `this_$iv`.sendersCounter) {
                     `segment$iv$iv`.cleanPrev()
                  }
               }
            }
         } catch (var31: java.lang.Throwable) {
            `cancellable$iv`.releaseClaimedReusableContinuation$kotlinx_coroutines_core()
            throw var31
         }

         val var36: Any = `cancellable$iv`.getResult()
         if (var36 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(`$completion`)
         }

         return var36
      }

      public override fun invokeOnCancellation(segment: Segment<*>, index: Int) {
         if (this.continuation != null) {
            this.continuation.invokeOnCancellation(segment, index)
         }
      }

      private fun onClosedHasNextNoWaiterSuspend() {
         val var10000: CancellableContinuationImpl = this.continuation
         this.continuation = null
         this.receiveResult = CHANNEL_CLOSED
         val cause: java.lang.Throwable = BufferedChannel.this.closeCause
         if (cause == null) {
            var10000.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(false))
         } else {
            var10000.resumeWith(
               Result.constructor_impl/* $VF was: constructor-impl */(
                  ResultKt.createFailure(
                     if (RECOVER_STACK_TRACES && var10000 as Continuation is CoroutineStackFrame)
                        StackTraceRecoveryKt.access$recoverFromStackFrame(cause, var10000 as CoroutineStackFrame)
                        else
                        cause
                  )
               )
            )
         }
      }

      public override operator fun next(): Any {
         val result: Any = this.receiveResult
         if (this.receiveResult === BufferedChannelKt.access$getNO_RECEIVE_RESULT$p()) {
            throw IllegalStateException("`hasNext()` has not been invoked".toString())
         } else {
            this.receiveResult = BufferedChannelKt.access$getNO_RECEIVE_RESULT$p()
            if (result === CHANNEL_CLOSED) {
               throw StackTraceRecoveryKt.recoverStackTrace(BufferedChannel.this.receiveException)
            } else {
               return (E)result
            }
         }
      }

      public fun tryResumeHasNext(element: Any): Boolean {
         val var10000: CancellableContinuationImpl = this.continuation
         this.continuation = null
         this.receiveResult = element
         return BufferedChannelKt.access$tryResume0(
            var10000,
            true,
            if (BufferedChannel.this.onUndeliveredElement != null)
               OnUndeliveredElementKt.bindCancellationFun(BufferedChannel.this.onUndeliveredElement, element, var10000.context)
               else
               null
         )
      }

      public fun tryResumeHasNextOnClosedChannel() {
         val var10000: CancellableContinuationImpl = this.continuation
         this.continuation = null
         this.receiveResult = CHANNEL_CLOSED
         val cause: java.lang.Throwable = BufferedChannel.this.closeCause
         if (cause == null) {
            var10000.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(false))
         } else {
            var10000.resumeWith(
               Result.constructor_impl/* $VF was: constructor-impl */(
                  ResultKt.createFailure(
                     if (RECOVER_STACK_TRACES && var10000 as Continuation is CoroutineStackFrame)
                        StackTraceRecoveryKt.access$recoverFromStackFrame(cause, var10000 as CoroutineStackFrame)
                        else
                        cause
                  )
               )
            )
         }
      }

      /** @deprecated */
      @Deprecated(message = "Since 1.3.0, binary compatibility with versions <= 1.2.x", level = DeprecationLevel.HIDDEN)
      @JvmName(name = "next")
      @JvmSynthetic
      override fun next(`$completion`: Continuation) {
         ChannelIterator.DefaultImpls.next(this, `$completion`)
      }

      @JvmStatic
      @JvmSynthetic
      fun `access$hasNextOnNoWaiterSuspend`(
         `$this`: BufferedChannel.BufferedChannelIterator, segment: ChannelSegment, index: Int, r: Long, `$completion`: Continuation
      ) {
         `$this`.hasNextOnNoWaiterSuspend(segment, index, r, `$completion`)
      }

      @JvmStatic
      @JvmSynthetic
      fun `access$setReceiveResult$p`(`$this`: BufferedChannel.BufferedChannelIterator, `<set-?>`: Any) {
         `$this`.receiveResult = `<set-?>`
      }

      @JvmStatic
      @JvmSynthetic
      fun `access$setContinuation$p`(`$this`: BufferedChannel.BufferedChannelIterator, `<set-?>`: CancellableContinuationImpl) {
         `$this`.continuation = `<set-?>`
      }

      @JvmStatic
      @JvmSynthetic
      fun `access$onClosedHasNextNoWaiterSuspend`(`$this`: BufferedChannel.BufferedChannelIterator) {
         `$this`.onClosedHasNextNoWaiterSuspend()
      }
   }

   private class SendBroadcast(cont: CancellableContinuation<Boolean>) : Waiter {
      public final val cont: CancellableContinuation<Boolean>
      @JvmSynthetic
      private CancellableContinuationImpl<java.lang.Boolean> $$delegate_0;

      init {
         this.cont = cont
         this.$$delegate_0 = cont as CancellableContinuationImpl<java.lang.Boolean>
      }

      public override fun invokeOnCancellation(segment: Segment<*>, index: Int) {
         this.$$delegate_0.invokeOnCancellation(segment, index)
      }
   }
}
