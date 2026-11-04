package kotlinx.coroutines.debug.internal

import _COROUTINE.ArtificialStackFrames
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicLongFieldUpdater
import kotlin.concurrent.ThreadsKt
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.CoroutineContext.Key
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineId
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.JobKt
import kotlinx.coroutines.JobSupport
import kotlinx.coroutines.internal.ScopeCoroutine
import org.jetbrains.annotations.NotNull

@PublishedApi
@SourceDebugExtension(["SMAP\nDebugProbesImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DebugProbesImpl.kt\nkotlinx/coroutines/debug/internal/DebugProbesImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 5 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 6 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,620:1\n150#1:638\n151#1,4:640\n156#1,5:645\n150#1:650\n151#1,4:652\n156#1,5:657\n1#2:621\n1#2:639\n1#2:651\n766#3:622\n857#3,2:623\n1208#3,2:625\n1238#3,4:627\n1855#3,2:665\n350#3,7:673\n1819#3,8:680\n603#4:631\n603#4:644\n603#4:656\n603#4:662\n1295#4,2:663\n37#5,2:632\n37#5,2:634\n37#5,2:636\n1627#6,6:667\n1735#6,6:688\n*S KotlinDebug\n*F\n+ 1 DebugProbesImpl.kt\nkotlinx/coroutines/debug/internal/DebugProbesImpl\n*L\n245#1:638\n245#1:640,4\n245#1:645,5\n252#1:650\n252#1:652,4\n252#1:657,5\n245#1:639\n252#1:651\n110#1:622\n110#1:623,2\n111#1:625,2\n111#1:627,4\n307#1:665,2\n416#1:673,7\n506#1:680,8\n154#1:631\n245#1:644\n252#1:656\n287#1:662\n288#1:663,2\n211#1:632,2\n212#1:634,2\n213#1:636,2\n355#1:667,6\n558#1:688,6\n*E\n"])
internal object DebugProbesImpl {
   private final val ARTIFICIAL_FRAME: StackTraceElement = ArtificialStackFrames().coroutineCreation()
   private final val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
   private final var weakRefCleanerThread: Thread?
   private final val capturedCoroutinesMap: ConcurrentWeakMap<kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner<*>, Boolean> =
      ConcurrentWeakMap(false, 1, null)
      internal final var sanitizeStackTraces: Boolean = true
   internal final var enableCreationStackTraces: Boolean = true
   public final var ignoreCoroutinesWithEmptyContext: Boolean = true
   private final val dynamicAttach: ((Boolean) -> Unit)? = INSTANCE.getDynamicAttach()
   private final val callerInfoCache: ConcurrentWeakMap<CoroutineStackFrame, DebugCoroutineInfoImpl> = ConcurrentWeakMap(true)
   @JvmStatic
   private DebugProbesImpl.Installations$kotlinx$VolatileWrapper installations$kotlinx$VolatileWrapper = DebugProbesImpl.Installations$kotlinx$VolatileWrapper(
      null
   );
   @JvmStatic
   private DebugProbesImpl.SequenceNumber$kotlinx$VolatileWrapper sequenceNumber$kotlinx$VolatileWrapper = DebugProbesImpl.SequenceNumber$kotlinx$VolatileWrapper(
      null
   );

   private final val capturedCoroutines: Set<kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner<*>>
      private final get() {
         return capturedCoroutinesMap.keySet()
      }


   public final val isInstalled: Boolean
      public final get() {
         return DebugProbesImpl.Installations$kotlinx$VolatileWrapper.installations$FU.get(installations$kotlinx$VolatileWrapper) > 0
      }


   private fun getDynamicAttach(): ((Boolean) -> Unit)? {
      val var1: DebugProbesImpl = this

      var `$this$getDynamicAttach_u24lambda_u240`: DebugProbesImpl
      try {
         `$this$getDynamicAttach_u24lambda_u240` = var1
         val var10000: Any = Class.forName("kotlinx.coroutines.debug.internal.ByteBuddyDynamicAttach").getConstructors()[0].newInstance()
         `$this$getDynamicAttach_u24lambda_u240` = (DebugProbesImpl)Result.constructor_impl/* $VF was: constructor-impl */(
            TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10000, 1) as Function1
         )
      } catch (var6: java.lang.Throwable) {
         `$this$getDynamicAttach_u24lambda_u240` = (DebugProbesImpl)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var6))
      }

      return (
         if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$getDynamicAttach_u24lambda_u240`)) null else `$this$getDynamicAttach_u24lambda_u240`
      ) as (java.lang.Boolean?) -> Unit
   }

   internal fun install() {
      if (DebugProbesImpl.Installations$kotlinx$VolatileWrapper.installations$FU.incrementAndGet(installations$kotlinx$VolatileWrapper) <= 1) {
         this.startWeakRefCleanerThread()
         if (!AgentInstallationType.INSTANCE.isInstalledStatically) {
            if (dynamicAttach != null) {
               dynamicAttach(true)
            }
         }
      }
   }

   internal fun uninstall() {
      if (!this.isInstalled) {
         throw IllegalStateException("Agent was not installed".toString())
      } else if (DebugProbesImpl.Installations$kotlinx$VolatileWrapper.installations$FU.decrementAndGet(installations$kotlinx$VolatileWrapper) == 0) {
         this.stopWeakRefCleanerThread()
         capturedCoroutinesMap.clear()
         callerInfoCache.clear()
         if (!AgentInstallationType.INSTANCE.isInstalledStatically) {
            if (dynamicAttach != null) {
               dynamicAttach(false)
            }
         }
      }
   }

   private fun startWeakRefCleanerThread() {
      weakRefCleanerThread = ThreadsKt.thread$default(false, true, null, "Coroutines Debugger Cleaner", 0, {
         callerInfoCache.runWeakRefQueueCleaningLoopUntilInterrupted()
      }, 21, null)
   }

   private fun stopWeakRefCleanerThread() {
      if (weakRefCleanerThread != null) {
         val thread: Thread = weakRefCleanerThread
         weakRefCleanerThread = null
         thread.interrupt()
         thread.join()
      }
   }

   internal fun hierarchyToString(job: Job): String {
      if (!this.isInstalled) {
         throw IllegalStateException("Debug probes are not installed".toString())
      } else {
         val var5: java.lang.Iterable = this.capturedCoroutines
         val `$this$associateByTo$iv$iv`: java.util.Collection = ArrayList()

         for (`element$iv$iv` in var5) {
            if ((`element$iv$iv` as DebugProbesImpl.CoroutineOwner).delegate.getContext().get(Job.Key) != null) {
               `$this$associateByTo$iv$iv`.add(`element$iv$iv`)
            }
         }

         val var16: java.lang.Iterable = `$this$associateByTo$iv$iv` as java.util.List
         val `destination$iv$ivx`: java.util.Map = LinkedHashMap(
            RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associateByTo$iv$iv` as java.util.List, 10)), 16)
         )

         for (var28 in var16) {
            `destination$iv$ivx`.put(
               JobKt.getJob((var28 as DebugProbesImpl.CoroutineOwner).delegate.getContext()), (var28 as DebugProbesImpl.CoroutineOwner).info
            )
         }

         val var17: StringBuilder = StringBuilder()
         INSTANCE.build(job, `destination$iv$ivx`, var17, "")
         val var10000: java.lang.String = var17.toString()
         return var10000
      }
   }

   private fun Job.build(map: Map<Job, DebugCoroutineInfoImpl>, builder: StringBuilder, indent: String) {
      val info: DebugCoroutineInfoImpl = map.get(`$this$build`) as DebugCoroutineInfoImpl
      val var9: java.lang.String
      if (info == null) {
         if (`$this$build` !is ScopeCoroutine) {
            builder.append("$indent${this.debugString}
")
            var9 = "$indent	"
         } else {
            var9 = indent
         }
      } else {
         builder.append(
            "$indent${this.debugString}, continuation is ${info.state} at line ${CollectionsKt.firstOrNull(
               info.lastObservedStackTrace$kotlinx_coroutines_core()
            ) as StackTraceElement}
   "
         )
         var9 = "$indent	"
      }

      for (var11 in `$this$build`.children) {
         this.build(var11, map, builder, var9)
      }
   }

   private final val debugString: String
      private final get() {
         return if (`$this$debugString` is JobSupport) (`$this$debugString` as JobSupport).toDebugString() else `$this$debugString`.toString()
      }


   private inline fun <R : Any> dumpCoroutinesInfoImpl(
      crossinline create: (kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner<*>, CoroutineContext) -> Any
   ): List<Any> {
      if (!this.isInstalled) {
         throw IllegalStateException("Debug probes are not installed".toString())
      } else {
         return SequencesKt.toList(
            SequencesKt.mapNotNull(
               SequencesKt.sortedWith(CollectionsKt.asSequence(this.capturedCoroutines), DebugProbesImpl$dumpCoroutinesInfoImpl$$inlined$sortedBy$1()),
               { owner: kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner<*> ->
                  var var10000: CoroutineContext
                  if (DebugProbesImpl.INSTANCE.isFinished(owner)) {
                     var10000 = null
                  } else {
                     var10000 = owner.info.context
                     var10000 = (CoroutineContext)(if (var10000 != null) create(owner, var10000) else null)
                  }

                  return (R)var10000
               } as Function1
            )
         )
      }
   }

   public fun dumpCoroutinesInfoAsJsonAndReferences(): Array<Any> {
      val coroutinesInfo: java.util.List = this.dumpCoroutinesInfo()
      val size: Int = coroutinesInfo.size()
      val lastObservedThreads: ArrayList = ArrayList(size)
      val lastObservedFrames: ArrayList = ArrayList(size)
      val coroutinesInfoAsJson: ArrayList = ArrayList(size)

      for (`$this$toTypedArray$iv` in coroutinesInfo) {
         var `$i$f$toTypedArray`: CoroutineContext
         var var22: java.lang.String
         run label39@{
            `$i$f$toTypedArray` = `$this$toTypedArray$iv`.context
            val var10000: CoroutineName = `$i$f$toTypedArray`.get(CoroutineName.Key) as CoroutineName
            if (var10000 != null) {
               var22 = var10000.name
               if (var22 != null) {
                  var22 = this.toStringRepr(var22)
                  return@label39
               }
            }

            var22 = null
         }

         val var23: CoroutineDispatcher = `$i$f$toTypedArray`.get(CoroutineDispatcher.Key as Key) as CoroutineDispatcher
         val dispatcher: java.lang.String = if (var23 != null) this.toStringRepr(var23) else null
         val var10001: StringBuilder = StringBuilder()
            .append("\n                {\n                    \"name\": ")
            .append(var22)
            .append(",\n                    \"id\": ")
            val var10002: CoroutineId = `$i$f$toTypedArray`.get(CoroutineId.Key) as CoroutineId
         coroutinesInfoAsJson.add(
            StringsKt.trimIndent(
               var10001.append(if (var10002 != null) var10002.id else null)
                  .append(",\n                    \"dispatcher\": ")
                  .append(dispatcher)
                  .append(",\n                    \"sequenceNumber\": ")
                  .append(`$this$toTypedArray$iv`.sequenceNumber)
                  .append(",\n                    \"state\": \"")
                  .append(`$this$toTypedArray$iv`.state)
                  .append("\"\n                } \n                ")
                  .toString()
            )
         )
         lastObservedFrames.add(`$this$toTypedArray$iv`.lastObservedFrame)
         lastObservedThreads.add(`$this$toTypedArray$iv`.lastObservedThread)
      }

      return arrayOf(
         "[${CollectionsKt.joinToString$default(coroutinesInfoAsJson, null, null, null, 0, null, null, 63, null)}]",
         lastObservedThreads.toArray(arrayOfNulls(0)),
         lastObservedFrames.toArray(arrayOfNulls(0)),
         coroutinesInfo.toArray(arrayOfNulls(0))
      )
   }

   public fun enhanceStackTraceWithThreadDumpAsJson(info: DebugCoroutineInfo): String {
      val stackTraceElements: java.util.List = this.enhanceStackTraceWithThreadDump(info, info.lastObservedStackTrace)
      val stackTraceElementsInfoAsJson: java.util.List = ArrayList()

      for (element in stackTraceElements) {
         val var10001: StringBuilder = StringBuilder()
            .append("\n                {\n                    \"declaringClass\": \"")
            .append(element.getClassName())
            .append("\",\n                    \"methodName\": \"")
            .append(element.getMethodName())
            .append("\",\n                    \"fileName\": ")
            val var10002: java.lang.String = element.getFileName()
         stackTraceElementsInfoAsJson.add(
            StringsKt.trimIndent(
               var10001.append(if (var10002 != null) this.toStringRepr(var10002) else null)
                  .append(",\n                    \"lineNumber\": ")
                  .append(element.getLineNumber())
                  .append("\n                }\n                ")
                  .toString()
            )
         )
      }

      return "[${CollectionsKt.joinToString$default(stackTraceElementsInfoAsJson, null, null, null, 0, null, null, 63, null)}]"
   }

   private fun Any.toStringRepr(): String {
      return DebugProbesImplKt.access$repr(`$this$toStringRepr`.toString())
   }

   public fun dumpCoroutinesInfo(): List<DebugCoroutineInfo> {
      if (!this.isInstalled) {
         throw IllegalStateException("Debug probes are not installed".toString())
      } else {
         return SequencesKt.toList(
            SequencesKt.mapNotNull(
               SequencesKt.sortedWith(CollectionsKt.asSequence(this.capturedCoroutines), DebugProbesImpl$dumpCoroutinesInfoImpl$$inlined$sortedBy$1()),
               DebugProbesImpl$dumpCoroutinesInfo$$inlined$dumpCoroutinesInfoImpl$1()
            )
         )
      }
   }

   public fun dumpDebuggerInfo(): List<DebuggerInfo> {
      if (!this.isInstalled) {
         throw IllegalStateException("Debug probes are not installed".toString())
      } else {
         return SequencesKt.toList(
            SequencesKt.mapNotNull(
               SequencesKt.sortedWith(CollectionsKt.asSequence(this.capturedCoroutines), DebugProbesImpl$dumpCoroutinesInfoImpl$$inlined$sortedBy$1()),
               DebugProbesImpl$dumpDebuggerInfo$$inlined$dumpCoroutinesInfoImpl$1()
            )
         )
      }
   }

   @JvmName(name = "dumpCoroutines")
   internal fun dumpCoroutines(out: PrintStream) {
      synchronized (out) {
         INSTANCE.dumpCoroutinesSynchronized(out)
      }
   }

   private fun kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner<*>.isFinished(): Boolean {
      val var10000: CoroutineContext = `$this$isFinished`.info.context
      if (var10000 != null) {
         val var3: Job = var10000.get(Job.Key) as Job
         if (var3 != null) {
            if (!var3.isCompleted) {
               return false
            }

            capturedCoroutinesMap.remove(`$this$isFinished`)
            return true
         }
      }

      return false
   }

   private fun dumpCoroutinesSynchronized(out: PrintStream) {
      if (!this.isInstalled) {
         throw IllegalStateException("Debug probes are not installed".toString())
      } else {
         out.print("Coroutines dump ${dateFormat.format(System.currentTimeMillis())}")

         for (`element$iv` in SequencesKt.sortedWith(SequencesKt.filter(CollectionsKt.asSequence(this.capturedCoroutines), { it ->
            !INSTANCE.isFinished(it)
         }), DebugProbesImpl$dumpCoroutinesSynchronized$$inlined$sortedBy$1())) {
            val owner: DebugProbesImpl.CoroutineOwner = `element$iv` as DebugProbesImpl.CoroutineOwner
            val info: DebugCoroutineInfoImpl = (`element$iv` as DebugProbesImpl.CoroutineOwner).info
            val observedStackTrace: java.util.List = (`element$iv` as DebugProbesImpl.CoroutineOwner).info.lastObservedStackTrace$kotlinx_coroutines_core()
            val enhancedStackTrace: java.util.List = INSTANCE.enhanceStackTraceWithThreadDumpImpl(info.state, info.lastObservedThread, observedStackTrace)
            out.print(
               "\n\nCoroutine ${owner.delegate}, state: ${if (info.state == "RUNNING" && enhancedStackTrace === observedStackTrace)
                  "${info.state} (Last suspension stacktrace, not an actual stacktrace)"
                  else
                  info.state}"
            )
            if (observedStackTrace.isEmpty()) {
               out.print("\n\tat ${ARTIFICIAL_FRAME}")
               INSTANCE.printStackTrace(out, info.creationStackTrace)
            } else {
               INSTANCE.printStackTrace(out, enhancedStackTrace)
            }
         }
      }
   }

   private fun printStackTrace(out: PrintStream, frames: List<StackTraceElement>) {
      for (`element$iv` in frames) {
         out.print("\n\tat ${`element$iv` as StackTraceElement}")
      }
   }

   public fun enhanceStackTraceWithThreadDump(info: DebugCoroutineInfo, coroutineTrace: List<StackTraceElement>): List<StackTraceElement> {
      return this.enhanceStackTraceWithThreadDumpImpl(info.state, info.lastObservedThread, coroutineTrace)
   }

   private fun enhanceStackTraceWithThreadDumpImpl(state: String, thread: Thread?, coroutineTrace: List<StackTraceElement>): List<StackTraceElement> {
      if (state == "RUNNING" && thread != null) {
         val `$this$indexOfFirst$iv`: DebugProbesImpl = this

         var continuationStartFrame: DebugProbesImpl
         try {
            continuationStartFrame = `$this$indexOfFirst$iv`
            continuationStartFrame = (DebugProbesImpl)Result.constructor_impl/* $VF was: constructor-impl */(thread.getStackTrace())
         } catch (var13: java.lang.Throwable) {
            continuationStartFrame = (DebugProbesImpl)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var13))
         }

         val var10000: Array<StackTraceElement> = (
            if (Result.isFailure_impl/* $VF was: isFailure-impl */(continuationStartFrame)) null else continuationStartFrame
         ) as Array<StackTraceElement>
         if (var10000 == null) {
            return coroutineTrace
         } else {
            val actualTrace: Array<StackTraceElement> = var10000
            val var15: Array<Any> = var10000
            var var20: Int = 0
            val expectedSize: Int = var10000.length

            while (true) {
               if (var20 >= expectedSize) {
                  var27 = -1
                  break
               }

               if (var15[var20].getClassName() == "kotlin.coroutines.jvm.internal.BaseContinuationImpl"
                  && var15[var20].getMethodName() == "resumeWith"
                  && var15[var20].getFileName() == "ContinuationImpl.kt") {
                  var27 = var20
                  break
               }

               var20++
            }

            val var16: Pair = this.findContinuationStartIndex(var27, var10000, coroutineTrace)
            continuationStartFrame = (var16.component1() as java.lang.Number).intValue()
            var20 = (var16.component2() as java.lang.Number).intValue()
            if (continuationStartFrame == -1) {
               return coroutineTrace
            } else {
               val var23: ArrayList = ArrayList(var27 + coroutineTrace.size() - continuationStartFrame - 1 - var20)
               var var24: Int = 0

               for (var12 in var27 - var20..var24) {
                  var23.add(actualTrace[var24])
               }

               var24 = continuationStartFrame + 1

               for (var26 in coroutineTrace.size()..var24) {
                  var23.add(coroutineTrace.get(var24))
               }

               return var23
            }
         }
      } else {
         return coroutineTrace
      }
   }

   private fun findContinuationStartIndex(indexOfResumeWith: Int, actualTrace: Array<StackTraceElement>, coroutineTrace: List<StackTraceElement>): Pair<
         Int,
         Int
      > {
      val var4: Byte = 3

      repeat(var4) { var5 ->
         val result: Int = INSTANCE.findIndexOfFrame(indexOfResumeWith - 1 - var5, actualTrace, coroutineTrace)
         if (result != -1) {
            return TuplesKt.to(result, var5)
         }
      }

      return TuplesKt.to(-1, 0)
   }

   private fun findIndexOfFrame(frameIndex: Int, actualTrace: Array<StackTraceElement>, coroutineTrace: List<StackTraceElement>): Int {
      val var10000: StackTraceElement = ArraysKt.getOrNull(actualTrace, frameIndex) as StackTraceElement
      if (var10000 == null) {
         return -1
      } else {
         val continuationFrame: StackTraceElement = var10000
         var `index$iv`: Int = 0
         val var8: java.util.Iterator = coroutineTrace.iterator()

         while (true) {
            if (!var8.hasNext()) {
               var12 = -1
               break
            }

            val it: StackTraceElement = var8.next() as StackTraceElement
            if (it.getFileName() == continuationFrame.getFileName()
               && it.getClassName() == continuationFrame.getClassName()
               && it.getMethodName() == continuationFrame.getMethodName()) {
               var12 = `index$iv`
               break
            }

            `index$iv`++
         }

         return var12
      }
   }

   internal fun probeCoroutineResumed(frame: Continuation<*>) {
      this.updateState(frame, "RUNNING")
   }

   internal fun probeCoroutineSuspended(frame: Continuation<*>) {
      this.updateState(frame, "SUSPENDED")
   }

   private fun updateState(frame: Continuation<*>, state: String) {
      if (this.isInstalled) {
         if (!ignoreCoroutinesWithEmptyContext || frame.getContext() != EmptyCoroutineContext.INSTANCE) {
            if (state == "RUNNING") {
               val var5: CoroutineStackFrame = frame as? CoroutineStackFrame
               if ((frame as? CoroutineStackFrame) != null) {
                  this.updateRunningState(var5, state)
               }
            } else {
               val var10000: DebugProbesImpl.CoroutineOwner = this.owner(frame)
               if (var10000 != null) {
                  this.updateState(var10000, frame, state)
               }
            }
         }
      }
   }

   private fun updateRunningState(frame: CoroutineStackFrame, state: String) {
      if (this.isInstalled) {
         val cached: DebugCoroutineInfoImpl = callerInfoCache.remove(frame)
         val var7: DebugCoroutineInfoImpl
         val var8: Boolean
         if (cached != null) {
            var7 = cached
            var8 = false
         } else {
            val var10000: DebugProbesImpl.CoroutineOwner = this.owner(frame)
            if (var10000 == null || var10000.info == null) {
               return
            }

            var7 = var10000.info
            var8 = true
            val var11: CoroutineStackFrame = var10000.info.lastObservedFrame
            val caller: CoroutineStackFrame = if (var11 != null) this.realCaller(var11) else null
            if (caller != null) {
               callerInfoCache.remove(caller)
            }
         }

         var7.updateState$kotlinx_coroutines_core(state, frame as Continuation<*>, var8)
         val var12: CoroutineStackFrame = this.realCaller(frame)
         if (var12 != null) {
            (callerInfoCache as java.util.Map).put(var12, var7)
         }
      }
   }

   private tailrec fun CoroutineStackFrame.realCaller(): CoroutineStackFrame? {
      while (true) {
         val var10000: CoroutineStackFrame = `$this$realCaller`.getCallerFrame()
         if (var10000 == null) {
            return null
         }

         if (var10000.getStackTraceElement() != null) {
            return var10000
         }

         this = this
         `$this$realCaller` = var10000
      }
   }

   private fun updateState(owner: kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner<*>, frame: Continuation<*>, state: String) {
      if (this.isInstalled) {
         owner.info.updateState$kotlinx_coroutines_core(state, frame, true)
      }
   }

   private fun Continuation<*>.owner(): kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner<*>? {
      return if ((`$this$owner` as? CoroutineStackFrame) != null) this.owner(`$this$owner` as? CoroutineStackFrame) else null
   }

   private tailrec fun CoroutineStackFrame.owner(): kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner<*>? {
      while (true) {
         val var10000: DebugProbesImpl.CoroutineOwner
         if (`$this$owner` is DebugProbesImpl.CoroutineOwner) {
            var10000 = `$this$owner` as DebugProbesImpl.CoroutineOwner
         } else {
            val var2: CoroutineStackFrame = `$this$owner`.getCallerFrame()
            if (var2 != null) {
               this = this
               `$this$owner` = var2
               continue
            }

            var10000 = null
         }

         return var10000
      }
   }

   internal fun <T> probeCoroutineCreated(completion: Continuation<Any>): Continuation<Any> {
      if (!this.isInstalled) {
         return completion
      } else {
         label29@
         if (ignoreCoroutinesWithEmptyContext && completion.getContext() === EmptyCoroutineContext.INSTANCE) {
            return completion
         } else {
            return if (this.owner(completion) != null)
               completion
               else
               this.createOwner(completion, if (enableCreationStackTraces) this.toStackTraceFrame(this.sanitizeStackTrace(Exception())) else null)
            }
      }
   }

   private fun List<StackTraceElement>.toStackTraceFrame(): StackTraceFrame {
      var `accumulator$iv`: Any = null
      if (!`$this$toStackTraceFrame`.isEmpty()) {
         val `iterator$iv`: java.util.ListIterator = `$this$toStackTraceFrame`.listIterator(`$this$toStackTraceFrame`.size())

         while (`iterator$iv`.hasPrevious()) {
            `accumulator$iv` = StackTraceFrame(`accumulator$iv` as CoroutineStackFrame, `iterator$iv`.previous() as StackTraceElement)
         }
      }

      return StackTraceFrame(`accumulator$iv` as CoroutineStackFrame, ARTIFICIAL_FRAME)
   }

   private fun <T> createOwner(completion: Continuation<Any>, frame: StackTraceFrame?): Continuation<Any> {
      if (!this.isInstalled) {
         return completion
      } else {
         val owner: DebugProbesImpl.CoroutineOwner = DebugProbesImpl.CoroutineOwner(
            completion,
            DebugCoroutineInfoImpl(
               completion.getContext(),
               frame,
               DebugProbesImpl.SequenceNumber$kotlinx$VolatileWrapper.sequenceNumber$FU.incrementAndGet(sequenceNumber$kotlinx$VolatileWrapper)
            )
         )
         (capturedCoroutinesMap as java.util.Map).put(owner, true)
         if (!this.isInstalled) {
            capturedCoroutinesMap.clear()
         }

         return owner
      }
   }

   private fun probeCoroutineCompleted(owner: kotlinx.coroutines.debug.internal.DebugProbesImpl.CoroutineOwner<*>) {
      capturedCoroutinesMap.remove(owner)
      var var10000: CoroutineStackFrame = owner.info.lastObservedFrame
      if (var10000 != null) {
         var10000 = this.realCaller(var10000)
         if (var10000 != null) {
            callerInfoCache.remove(var10000)
            return
         }
      }
   }

   private fun <T : Throwable> sanitizeStackTrace(throwable: Any): List<StackTraceElement> {
      var stackTrace: Array<StackTraceElement>
      var size: Int
      var var10000: Int
      run label89@{
         stackTrace = throwable.getStackTrace()
         size = stackTrace.length
         val result: Array<StackTraceElement> = stackTrace
         var j: Int = stackTrace.length + -1
         if (0 <= stackTrace.length + -1) {
            do {
               val k: Int = j--
               if (result[k].getClassName() == "kotlin.coroutines.jvm.internal.DebugProbesKt") {
                  var10000 = k
                  return@label89
               }
            } while (0 <= j)
         }

         var10000 = -1
      }

      val traceStart: Int = 1 + var10000
      if (!sanitizeStackTraces) {
         val var14: Int = size - traceStart
         val var16: ArrayList = ArrayList(size - traceStart)

         repeat(var14) { var18 ->
            var16.add(stackTrace[var18 + traceStart])
         }

         return var16
      } else {
         val var13: ArrayList = ArrayList(size - traceStart + 1)
         var var15: Int = traceStart

         while (var15 < size) {
            if (this.isInternalMethod) {
               var13.add(stackTrace[var15])
               var var17: Int = var15 + 1

               while (var17 < size && this.isInternalMethod) {
                  var17++
               }

               var var19: Int = var17 - 1

               while (var19 > var15 && stackTrace[var19].getFileName() == null) {
                  var19--
               }

               if (var19 > var15 && var19 < var17 - 1) {
                  var13.add(stackTrace[var19])
               }

               var13.add(stackTrace[var17 - 1])
               var15 = var17
            } else {
               var13.add(stackTrace[var15])
               var15++
            }
         }

         return var13
      }
   }

   private final val isInternalMethod: Boolean
      private final get() {
         return StringsKt.startsWith$default(`$this$isInternalMethod`.getClassName(), "kotlinx.coroutines", false, 2, null)
      }


   public class CoroutineOwner<T> internal constructor(delegate: Continuation<Any>, info: DebugCoroutineInfoImpl) : Continuation<T>, CoroutineStackFrame {
      @JvmField
      @NotNull
      internal final val delegate: Continuation<Any>

      @JvmField
      @NotNull
      public final val info: DebugCoroutineInfoImpl

      init {
         this.delegate = delegate
         this.info = info
      }

      public open val context: CoroutineContext

      private final val frame: StackTraceFrame?
         private final get() {
            return this.info.creationStackBottom
         }


      public open val callerFrame: CoroutineStackFrame?
         public open get() {
            val var10000: StackTraceFrame = this.frame
            return if (var10000 != null) var10000.callerFrame else null
         }


      public open fun getStackTraceElement(): StackTraceElement? {
         val var10000: StackTraceFrame = this.frame
         return if (var10000 != null) var10000.getStackTraceElement() else null
      }

      public open fun resumeWith(result: Result<Any>) {
         DebugProbesImpl.INSTANCE.probeCoroutineCompleted(this)
         this.delegate.resumeWith(result)
      }

      public override fun toString(): String {
         return this.delegate.toString()
      }
   }

   // $VF: Class flags could not be determined
   internal class `Installations$kotlinx$VolatileWrapper` {
      @JvmStatic
      private AtomicIntegerFieldUpdater installations$FU = AtomicIntegerFieldUpdater.newUpdater(
         DebugProbesImpl.Installations$kotlinx$VolatileWrapper.class, "installations"
      );
      @Volatile
      @Volatile
      private int installations;

      fun `Installations$kotlinx$VolatileWrapper`() {
      }
   }

   // $VF: Class flags could not be determined
   internal class `SequenceNumber$kotlinx$VolatileWrapper` {
      @JvmStatic
      private AtomicLongFieldUpdater sequenceNumber$FU = AtomicLongFieldUpdater.newUpdater(
         DebugProbesImpl.SequenceNumber$kotlinx$VolatileWrapper.class, "sequenceNumber"
      );
      @Volatile
      @Volatile
      private long sequenceNumber;

      fun `SequenceNumber$kotlinx$VolatileWrapper`() {
      }
   }
}
