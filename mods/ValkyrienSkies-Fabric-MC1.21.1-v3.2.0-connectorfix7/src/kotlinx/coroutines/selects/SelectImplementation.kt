package kotlinx.coroutines.selects

import java.util.ArrayList
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.internal.LowPriorityInOverloadResolution
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CancelHandler
import kotlinx.coroutines.CancelHandlerBase
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.internal.Segment
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@PublishedApi
@SourceDebugExtension(["SMAP\nSelect.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Select.kt\nkotlinx/coroutines/selects/SelectImplementation\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n+ 5 CompletionHandler.kt\nkotlinx/coroutines/CompletionHandlerKt\n+ 6 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n*L\n1#1,873:1\n1#2:874\n2624#3,3:875\n1855#3,2:888\n1855#3,2:896\n1855#3,2:898\n314#4,9:878\n323#4,2:890\n19#5:887\n153#6,4:892\n*S KotlinDebug\n*F\n+ 1 Select.kt\nkotlinx/coroutines/selects/SelectImplementation\n*L\n505#1:875,3\n569#1:888,2\n726#1:896,2\n751#1:898,2\n545#1:878,9\n545#1:890,2\n561#1:887\n711#1:892,4\n*E\n"])
internal open class SelectImplementation<R>(context: CoroutineContext) : CancelHandler, SelectBuilder<R>, SelectInstanceInternal<R> {
   public open val context: CoroutineContext
   @Volatile
   @Volatile
   private Object state;
   private final var clauses: MutableList<kotlinx.coroutines.selects.SelectImplementation.ClauseData>?
   private final var disposableHandleOrSegment: Any?
   private final var indexInSegment: Int
   private final var internalResult: Any?
   @JvmStatic
   private AtomicReferenceFieldUpdater state$FU = AtomicReferenceFieldUpdater.newUpdater(SelectImplementation.class, Object.class, "state");

   init {
      this.context = context
      this.state = SelectKt.access$getSTATE_REG$p()
      this.clauses = ArrayList<>(2)
      this.indexInSegment = -1
      this.internalResult = SelectKt.access$getNO_RESULT$p()
   }

   private final val inRegistrationPhase: Boolean
      private final get() {
         val it: Any = state$FU.get(this)
         return it === SelectKt.access$getSTATE_REG$p() || it is java.util.List
      }


   private final val isSelected: Boolean
      private final get() {
         return state$FU.get(this) is SelectImplementation.ClauseData
      }


   private final val isCancelled: Boolean
      private final get() {
         return state$FU.get(this) === SelectKt.access$getSTATE_CANCELLED$p()
      }


   @PublishedApi
   internal open suspend fun doSelect(): Any {
      return doSelect$suspendImpl(this, `$completion`)
   }

   @PublishedApi
   @JvmStatic
   @JvmSynthetic
   fun <R> `doSelect$suspendImpl`(`$this`: SelectImplementation<R>, `$completion`: Continuation<in R>) {
      if (`$this`.isSelected) `$this`.complete(`$completion`) else `$this`.doSelectSuspend(`$completion`)
   }

   private suspend fun doSelectSuspend(): Any {
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
      // 00: aload 1
      // 01: instanceof kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1
      // 04: ifeq 24
      // 07: aload 1
      // 08: checkcast kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1
      // 0b: astore 3
      // 0c: aload 3
      // 0d: getfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.label I
      // 10: ldc -2147483648
      // 12: iand
      // 13: ifeq 24
      // 16: aload 3
      // 17: dup
      // 18: getfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.label I
      // 1b: ldc -2147483648
      // 1d: isub
      // 1e: putfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.label I
      // 21: goto 2e
      // 24: new kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1
      // 27: dup
      // 28: aload 0
      // 29: aload 1
      // 2a: invokespecial kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.<init> (Lkotlinx/coroutines/selects/SelectImplementation;Lkotlin/coroutines/Continuation;)V
      // 2d: astore 3
      // 2e: aload 3
      // 2f: getfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.result Ljava/lang/Object;
      // 32: astore 2
      // 33: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 36: astore 4
      // 38: aload 3
      // 39: getfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.label I
      // 3c: tableswitch 100 0 2 28 56 94
      // 58: aload 2
      // 59: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 5c: aload 0
      // 5d: aload 3
      // 5e: aload 3
      // 5f: aload 0
      // 60: putfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.L$0 Ljava/lang/Object;
      // 63: aload 3
      // 64: bipush 1
      // 65: putfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.label I
      // 68: invokespecial kotlinx/coroutines/selects/SelectImplementation.waitUntilSelected (Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 6b: dup
      // 6c: aload 4
      // 6e: if_acmpne 81
      // 71: aload 4
      // 73: areturn
      // 74: aload 3
      // 75: getfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.L$0 Ljava/lang/Object;
      // 78: checkcast kotlinx/coroutines/selects/SelectImplementation
      // 7b: astore 0
      // 7c: aload 2
      // 7d: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 80: aload 2
      // 81: pop
      // 82: aload 0
      // 83: aload 3
      // 84: aload 3
      // 85: aconst_null
      // 86: putfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.L$0 Ljava/lang/Object;
      // 89: aload 3
      // 8a: bipush 2
      // 8b: putfield kotlinx/coroutines/selects/SelectImplementation$doSelectSuspend$1.label I
      // 8e: invokespecial kotlinx/coroutines/selects/SelectImplementation.complete (Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 91: dup
      // 92: aload 4
      // 94: if_acmpne 9f
      // 97: aload 4
      // 99: areturn
      // 9a: aload 2
      // 9b: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 9e: aload 2
      // 9f: areturn
      // a0: new java/lang/IllegalStateException
      // a3: dup
      // a4: ldc "call to 'resume' before 'invoke' with coroutine"
      // a6: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // a9: athrow
   }

   public override operator fun SelectClause0.invoke(block: (Continuation<Any>) -> Any?) {
      register$default(
         this,
         SelectImplementation.ClauseData(
            `$this$invoke`.getClauseObject(),
            `$this$invoke`.getRegFunc(),
            `$this$invoke`.getProcessResFunc(),
            PARAM_CLAUSE_0,
            block,
            `$this$invoke`.getOnCancellationConstructor()
         ),
         false,
         1,
         null
      )
   }

   public override operator fun <Q> SelectClause1<Any>.invoke(block: (Any, Continuation<Any>) -> Any?) {
      register$default(
         this,
         SelectImplementation.ClauseData(
            `$this$invoke`.getClauseObject(),
            `$this$invoke`.getRegFunc(),
            `$this$invoke`.getProcessResFunc(),
            null,
            block,
            `$this$invoke`.getOnCancellationConstructor()
         ),
         false,
         1,
         null
      )
   }

   public override operator fun <P, Q> SelectClause2<Any, Any>.invoke(param: Any, block: (Any, Continuation<Any>) -> Any?) {
      register$default(
         this,
         SelectImplementation.ClauseData(
            `$this$invoke`.getClauseObject(),
            `$this$invoke`.getRegFunc(),
            `$this$invoke`.getProcessResFunc(),
            param,
            block,
            `$this$invoke`.getOnCancellationConstructor()
         ),
         false,
         1,
         null
      )
   }

   @JvmName(name = "register")
   internal fun kotlinx.coroutines.selects.SelectImplementation.ClauseData.register(reregister: Boolean = false) {
      if (ASSERTIONS_ENABLED && state$FU.get(this) === SelectKt.access$getSTATE_CANCELLED$p()) {
         throw AssertionError()
      } else if (state$FU.get(this) !is SelectImplementation.ClauseData) {
         if (!reregister) {
            this.checkClauseObject(`$this$register`.clauseObject)
         }

         if (`$this$register`.tryRegisterAsWaiter(this)) {
            if (!reregister) {
               val var10000: java.util.List = this.clauses
               var10000.add(`$this$register`)
            }

            `$this$register`.disposableHandleOrSegment = this.disposableHandleOrSegment
            `$this$register`.indexInSegment = this.indexInSegment
            this.disposableHandleOrSegment = null
            this.indexInSegment = -1
         } else {
            state$FU.set(this, `$this$register`)
         }
      }
   }

   private fun checkClauseObject(clauseObject: Any) {
      val var10000: java.util.List = this.clauses
      val `$this$none$iv`: java.lang.Iterable = var10000
      var var11: Boolean
      if (var10000 is java.util.Collection && (var10000 as java.util.Collection).isEmpty()) {
         var11 = true
      } else {
         run label43@{
            for (`element$iv` in `$this$none$iv`) {
               if ((`element$iv` as SelectImplementation.ClauseData).clauseObject === clauseObject) {
                  var11 = false
                  return@label43
               }
            }

            var11 = true
         }
      }

      if (!var11) {
         throw IllegalStateException(("Cannot use select clauses on the same object: $clauseObject").toString())
      }
   }

   public override fun disposeOnCompletion(disposableHandle: DisposableHandle) {
      this.disposableHandleOrSegment = disposableHandle
   }

   public override fun invokeOnCancellation(segment: Segment<*>, index: Int) {
      this.disposableHandleOrSegment = segment
      this.indexInSegment = index
   }

   public override fun selectInRegistrationPhase(internalResult: Any?) {
      this.internalResult = internalResult
   }

   private suspend fun waitUntilSelected() {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
      `cancellable$iv`.initCancellability()
      val cont: CancellableContinuation = `cancellable$iv`
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = access$getState$FU$p()

      while (true) {
         val curState: Any = `atomicfu$handler$iv`.get(this)
         if (curState === SelectKt.access$getSTATE_REG$p()) {
            if (access$getState$FU$p().compareAndSet(this, curState, cont)) {
               cont.invokeOnCancellation(this as CancelHandlerBase)
               break
            }
         } else {
            if (curState !is java.util.List) {
               if (curState !is SelectImplementation.ClauseData) {
                  throw IllegalStateException(("unexpected state: $curState").toString())
               }

               cont.resume(Unit.INSTANCE, (curState as SelectImplementation.ClauseData).createOnCancellationAction(this, access$getInternalResult$p(this)))
               break
            }

            if (access$getState$FU$p().compareAndSet(this, curState, SelectKt.access$getSTATE_REG$p())) {
               for (`element$iv` in curState as java.lang.Iterable) {
                  access$reregisterClause(this, `element$iv`)
               }
            }
         }
      }

      val var10000: Any = `cancellable$iv`.getResult()
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`)
      }

      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   private fun reregisterClause(clauseObject: Any) {
      val var10000: SelectImplementation.ClauseData = this.findClause(clauseObject)
      var10000.disposableHandleOrSegment = null
      var10000.indexInSegment = -1
      this.register(var10000, true)
   }

   public override fun trySelect(clauseObject: Any, result: Any?): Boolean {
      return this.trySelectInternal(clauseObject, result) == 0
   }

   public fun trySelectDetailed(clauseObject: Any, result: Any?): TrySelectDetailedResult {
      return SelectKt.access$TrySelectDetailedResult(this.trySelectInternal(clauseObject, result))
   }

   private fun trySelectInternal(clauseObject: Any, internalResult: Any?): Int {
      while (true) {
         val curState: Any = state$FU.get(this)
         if (curState is CancellableContinuation) {
            val var10000: SelectImplementation.ClauseData = this.findClause(clauseObject)
            if (var10000 != null) {
               val onCancellation: Function1 = var10000.createOnCancellationAction(this, internalResult)
               if (state$FU.compareAndSet(this, curState, var10000)) {
                  val cont: CancellableContinuation = curState as CancellableContinuation
                  this.internalResult = internalResult
                  if (SelectKt.access$tryResume(cont, onCancellation)) {
                     return 0
                  }

                  this.internalResult = null
                  return 2
               }
            }
         } else {
            if (curState == SelectKt.access$getSTATE_COMPLETED$p() || curState is SelectImplementation.ClauseData) {
               return 3
            }

            if (curState == SelectKt.access$getSTATE_CANCELLED$p()) {
               return 2
            }

            if (curState == SelectKt.access$getSTATE_REG$p()) {
               if (state$FU.compareAndSet(this, curState, CollectionsKt.listOf(clauseObject))) {
                  return 1
               }
            } else {
               if (curState is java.util.List) {
                  if (!state$FU.compareAndSet(this, curState, CollectionsKt.plus(curState as java.util.Collection, clauseObject))) {
                     continue
                  }

                  return 1
               }

               throw IllegalStateException(("Unexpected state: $curState").toString())
            }
         }
      }
   }

   private fun findClause(clauseObject: Any): kotlinx.coroutines.selects.SelectImplementation.ClauseData? {
      if (this.clauses == null) {
         return null
      } else {
         val var4: java.util.Iterator = this.clauses.iterator()

         var var10000: Any
         while (true) {
            if (var4.hasNext()) {
               val var5: Any = var4.next()
               if ((var5 as SelectImplementation.ClauseData).clauseObject != clauseObject) {
                  continue
               }

               var10000 = (SelectImplementation.ClauseData)var5
               break
            }

            var10000 = null
            break
         }

         var10000 = var10000
         if (var10000 == null) {
            throw IllegalStateException(("Clause with object $clauseObject is not found").toString())
         } else {
            return var10000
         }
      }
   }

   private suspend fun complete(): Any {
      label17@
      if (ASSERTIONS_ENABLED && !this.isSelected) {
         throw AssertionError()
      } else {
         val var10000: Any = state$FU.get(this)
         val var5: SelectImplementation.ClauseData = var10000 as SelectImplementation.ClauseData
         val internalResult: Any = this.internalResult
         this.cleanup(var5)
         return if (!RECOVER_STACK_TRACES)
            var5.invokeBlock(var5.processResult(internalResult), `$completion`)
            else
            this.processResultAndInvokeBlockRecoveringException(var5, internalResult, `$completion`)
         }
   }

   private suspend fun processResultAndInvokeBlockRecoveringException(clause: kotlinx.coroutines.selects.SelectImplementation.ClauseData, internalResult: Any?): Any {
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
      // 00: aload 3
      // 01: instanceof kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1
      // 04: ifeq 27
      // 07: aload 3
      // 08: checkcast kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1
      // 0b: astore 10
      // 0d: aload 10
      // 0f: getfield kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1.label I
      // 12: ldc -2147483648
      // 14: iand
      // 15: ifeq 27
      // 18: aload 10
      // 1a: dup
      // 1b: getfield kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1.label I
      // 1e: ldc -2147483648
      // 20: isub
      // 21: putfield kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1.label I
      // 24: goto 32
      // 27: new kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1
      // 2a: dup
      // 2b: aload 0
      // 2c: aload 3
      // 2d: invokespecial kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1.<init> (Lkotlinx/coroutines/selects/SelectImplementation;Lkotlin/coroutines/Continuation;)V
      // 30: astore 10
      // 32: aload 10
      // 34: getfield kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1.result Ljava/lang/Object;
      // 37: astore 9
      // 39: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 3c: astore 11
      // 3e: aload 10
      // 40: getfield kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1.label I
      // 43: tableswitch 116 0 1 21 57
      // 58: aload 9
      // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 5d: nop
      // 5e: aload 1
      // 5f: aload 2
      // 60: invokevirtual kotlinx/coroutines/selects/SelectImplementation$ClauseData.processResult (Ljava/lang/Object;)Ljava/lang/Object;
      // 63: astore 4
      // 65: aload 1
      // 66: aload 4
      // 68: aload 10
      // 6a: aload 10
      // 6c: bipush 1
      // 6d: putfield kotlinx/coroutines/selects/SelectImplementation$processResultAndInvokeBlockRecoveringException$1.label I
      // 70: invokevirtual kotlinx/coroutines/selects/SelectImplementation$ClauseData.invokeBlock (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 73: dup
      // 74: aload 11
      // 76: if_acmpne 84
      // 79: aload 11
      // 7b: areturn
      // 7c: nop
      // 7d: aload 9
      // 7f: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 82: aload 9
      // 84: astore 4
      // 86: goto b4
      // 89: astore 5
      // 8b: bipush 0
      // 8c: istore 6
      // 8e: invokestatic kotlinx/coroutines/DebugKt.getRECOVER_STACK_TRACES ()Z
      // 91: ifne 97
      // 94: aload 5
      // 96: athrow
      // 97: aload 10
      // 99: astore 7
      // 9b: bipush 0
      // 9c: istore 8
      // 9e: aload 7
      // a0: instanceof kotlin/coroutines/jvm/internal/CoroutineStackFrame
      // a3: ifne a9
      // a6: aload 5
      // a8: athrow
      // a9: aload 5
      // ab: aload 7
      // ad: checkcast kotlin/coroutines/jvm/internal/CoroutineStackFrame
      // b0: invokestatic kotlinx/coroutines/internal/StackTraceRecoveryKt.access$recoverFromStackFrame (Ljava/lang/Throwable;Lkotlin/coroutines/jvm/internal/CoroutineStackFrame;)Ljava/lang/Throwable;
      // b3: athrow
      // b4: aload 4
      // b6: areturn
      // b7: new java/lang/IllegalStateException
      // ba: dup
      // bb: ldc "call to 'resume' before 'invoke' with coroutine"
      // bd: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // c0: athrow
   }

   private fun cleanup(selectedClause: kotlinx.coroutines.selects.SelectImplementation.ClauseData) {
      if (ASSERTIONS_ENABLED && !(state$FU.get(this) == selectedClause)) {
         throw AssertionError()
      } else if (this.clauses != null) {
         for (`element$iv` in this.clauses) {
            val clause: SelectImplementation.ClauseData = `element$iv` as SelectImplementation.ClauseData
            if (`element$iv` as SelectImplementation.ClauseData != selectedClause) {
               clause.dispose()
            }
         }

         state$FU.set(this, SelectKt.access$getSTATE_COMPLETED$p())
         this.internalResult = SelectKt.access$getNO_RESULT$p()
         this.clauses = null
      }
   }

   public override operator fun invoke(cause: Throwable?) {
      val `$this$forEach$iv`: AtomicReferenceFieldUpdater = state$FU

      val var5: Any
      do {
         var5 = `$this$forEach$iv`.get(this)
         if (var5 === SelectKt.access$getSTATE_COMPLETED$p()) {
            return
         }
      } while (!`$this$forEach$iv`.compareAndSet(this, var5, SelectKt.access$getSTATE_CANCELLED$p()))

      if (this.clauses != null) {
         for (var13 in this.clauses) {
            (var13 as SelectImplementation.ClauseData).dispose()
         }

         this.internalResult = SelectKt.access$getNO_RESULT$p()
         this.clauses = null
      }
   }

   override fun <P, Q> SelectClause2<in P, out Q>.invoke(block: (Q?, Continuation<in R>?) -> Any) {
      SelectBuilder.DefaultImpls.invoke(this, `$this$invoke`, block)
   }

   /** @deprecated */
   @Deprecated(message = "Replaced with the same extension function", replaceWith = @ReplaceWith(expression = "onTimeout", imports = ["kotlinx.coroutines.selects.onTimeout"]), level = DeprecationLevel.ERROR)
   @ExperimentalCoroutinesApi
   @LowPriorityInOverloadResolution
   override fun onTimeout(timeMillis: Long, block: (Continuation<in R>?) -> Any) {
      SelectBuilder.DefaultImpls.onTimeout(this, timeMillis, block)
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   fun `update$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Any, `atomicfu$dispatchReceiver`: Any) {
      val var5: Any
      do {
         var5 = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`)
      } while (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, var5, `atomicfu$action`(var5)))
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   open fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$waitUntilSelected`(`$this`: SelectImplementation, `$completion`: Continuation) {
      `$this`.waitUntilSelected(`$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$complete`(`$this`: SelectImplementation, `$completion`: Continuation) {
      `$this`.complete(`$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$reregisterClause`(`$this`: SelectImplementation, clauseObject: Any) {
      `$this`.reregisterClause(clauseObject)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getState$FU$p`(): AtomicReferenceFieldUpdater {
      state$FU
   }

   @SourceDebugExtension(["SMAP\nSelect.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Select.kt\nkotlinx/coroutines/selects/SelectImplementation$ClauseData\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,873:1\n1#2:874\n*E\n"])
   internal inner class ClauseData(clauseObject: Any,
      regFunc: (Any, SelectInstance<*>, Any?) -> Unit,
      processResFunc: (Any, Any?, Any?) -> Any?,
      param: Any?,
      block: Any,
      onCancellationConstructor: ((SelectInstance<*>, Any?, Any?) -> (Throwable) -> Unit)?
   ) {
      @JvmField
      @NotNull
      public final val clauseObject: Any

      private final val regFunc: (Any, SelectInstance<*>, Any?) -> Unit
      private final val processResFunc: (Any, Any?, Any?) -> Any?
      private final val param: Any?
      private final val block: Any

      @JvmField
      @Nullable
      public final val onCancellationConstructor: ((SelectInstance<*>, Any?, Any?) -> (Throwable) -> Unit)?

      @JvmField
      @Nullable
      public final var disposableHandleOrSegment: Any?
         private set

      @JvmField
      public final var indexInSegment: Int
         private set

      init {
         this.clauseObject = clauseObject
         this.regFunc = regFunc
         this.processResFunc = processResFunc
         this.param = param
         this.block = block
         this.onCancellationConstructor = onCancellationConstructor
         this.indexInSegment = -1
      }

      public fun tryRegisterAsWaiter(select: SelectImplementation<Any>): Boolean {
         if (ASSERTIONS_ENABLED && !select.inRegistrationPhase && !select.isCancelled) {
            throw AssertionError()
         } else if (ASSERTIONS_ENABLED && select.internalResult != SelectKt.access$getNO_RESULT$p()) {
            throw AssertionError()
         } else {
            this.regFunc(this.clauseObject, select, this.param)
            return select.internalResult === SelectKt.access$getNO_RESULT$p()
         }
      }

      public fun processResult(result: Any?): Any? {
         return this.processResFunc(this.clauseObject, this.param, result)
      }

      public suspend fun invokeBlock(argument: Any?): Any {
         val block: Any = this.block
         if (this.param === PARAM_CLAUSE_0) {
            return (block as Function1)(`$completion`)
         } else {
            return (block as Function2)(argument, `$completion`)
         }
      }

      public fun dispose() {
         val `$this$dispose_u24lambda_u242`: Any = this.disposableHandleOrSegment
         if (this.disposableHandleOrSegment is Segment) {
            (this.disposableHandleOrSegment as Segment).onCancellation(this.indexInSegment, null, SelectImplementation.this.context)
         } else {
            val var10000: DisposableHandle = this.disposableHandleOrSegment as? DisposableHandle
            if ((this.disposableHandleOrSegment as? DisposableHandle) != null) {
               var10000.dispose()
            }
         }
      }

      public fun createOnCancellationAction(select: SelectInstance<*>, internalResult: Any?): ((Throwable) -> Unit)? {
         return if (this.onCancellationConstructor != null) this.onCancellationConstructor(select, this.param, internalResult) as Function1 else null
      }
   }
}
