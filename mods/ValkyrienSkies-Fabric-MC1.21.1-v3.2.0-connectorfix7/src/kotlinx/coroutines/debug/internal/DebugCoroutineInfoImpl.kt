package kotlinx.coroutines.debug.internal

import java.lang.ref.WeakReference
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@PublishedApi
@SourceDebugExtension(["SMAP\nDebugCoroutineInfoImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DebugCoroutineInfoImpl.kt\nkotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,180:1\n1#2:181\n*E\n"])
internal class DebugCoroutineInfoImpl internal constructor(context: CoroutineContext?, creationStackBottom: StackTraceFrame?, sequenceNumber: Long) {
   internal final val creationStackBottom: StackTraceFrame?

   @JvmField
   public final val sequenceNumber: Long

   private final val _context: WeakReference<CoroutineContext?>

   @JvmField
   @NotNull
   public final var _state: String
      private set

   private final var unmatchedResume: Int

   @JvmField
   @Nullable
   public final var lastObservedThread: Thread?
      private set

   @JvmField
   @Nullable
   public final var _lastObservedFrame: WeakReference<CoroutineStackFrame>?
      private set

   init {
      this.creationStackBottom = creationStackBottom
      this.sequenceNumber = sequenceNumber
      this._context = WeakReference<>(context)
      this._state = "CREATED"
   }

   public final val context: CoroutineContext?
      public final get() {
         return this._context.get()
      }


   public final val creationStackTrace: List<StackTraceElement>
      public final get() {
         return this.creationStackTrace()
      }


   internal final val state: String
      internal final get() {
         return this._state
      }


   @Synchronized
   internal fun updateState(state: String, frame: Continuation<*>, shouldBeMatched: Boolean) {
      if (this._state == "RUNNING" && state == "RUNNING" && shouldBeMatched) {
         this.unmatchedResume++
      } else if (this.unmatchedResume > 0 && state == "SUSPENDED") {
         this.unmatchedResume += -1
         return
      }

      if (!(this._state == state) || !(state == "SUSPENDED") || this.lastObservedFrame == null) {
         this._state = state
         this.lastObservedFrame = frame as? CoroutineStackFrame
         this.lastObservedThread = if (state == "RUNNING") Thread.currentThread() else null
      }
   }

   internal final var lastObservedFrame: CoroutineStackFrame?
      internal final get() {
         return if (this._lastObservedFrame != null) this._lastObservedFrame.get() else null
      }

      internal final set(value) {
         var var10000: DebugCoroutineInfoImpl = this
         val var10001: WeakReference
         if (value != null) {
            var10001 = WeakReference<>(value)
            var10000 = this
         } else {
            var10001 = null
         }

         var10000._lastObservedFrame = var10001
      }


   internal fun lastObservedStackTrace(): List<StackTraceElement> {
      val var10000: CoroutineStackFrame = this.lastObservedFrame
      if (var10000 == null) {
         return CollectionsKt.emptyList()
      } else {
         var frame: CoroutineStackFrame = var10000
         val result: ArrayList = ArrayList()

         while (frame != null) {
            val var5: StackTraceElement = frame.getStackTraceElement()
            if (var5 != null) {
               result.add(var5)
            }

            frame = frame.getCallerFrame()
         }

         return result
      }
   }

   private fun creationStackTrace(): List<StackTraceElement> {
      if (this.creationStackBottom == null) {
         return CollectionsKt.emptyList()
      } else {
         val bottom: StackTraceFrame = this.creationStackBottom
         return SequencesKt.toList(SequencesKt.sequence({
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as Function2))
      }
   }

   private tailrec suspend fun SequenceScope<StackTraceElement>.yieldFrames(frame: CoroutineStackFrame?) {
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
      // 01: instanceof kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1
      // 04: ifeq 27
      // 07: aload 3
      // 08: checkcast kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1
      // 0b: astore 9
      // 0d: aload 9
      // 0f: getfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.label I
      // 12: ldc -2147483648
      // 14: iand
      // 15: ifeq 27
      // 18: aload 9
      // 1a: dup
      // 1b: getfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.label I
      // 1e: ldc -2147483648
      // 20: isub
      // 21: putfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.label I
      // 24: goto 32
      // 27: new kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1
      // 2a: dup
      // 2b: aload 0
      // 2c: aload 3
      // 2d: invokespecial kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.<init> (Lkotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl;Lkotlin/coroutines/Continuation;)V
      // 30: astore 9
      // 32: aload 9
      // 34: getfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.result Ljava/lang/Object;
      // 37: astore 8
      // 39: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 3c: astore 10
      // 3e: aload 9
      // 40: getfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.label I
      // 43: tableswitch 172 0 1 21 90
      // 58: aload 8
      // 5a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 5d: aload 2
      // 5e: ifnonnull 65
      // 61: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 64: areturn
      // 65: aload 2
      // 66: invokeinterface kotlin/coroutines/jvm/internal/CoroutineStackFrame.getStackTraceElement ()Ljava/lang/StackTraceElement; 1
      // 6b: dup
      // 6c: ifnull c7
      // 6f: astore 6
      // 71: bipush 0
      // 72: istore 7
      // 74: aload 1
      // 75: aload 6
      // 77: aload 9
      // 79: aload 9
      // 7b: aload 0
      // 7c: putfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.L$0 Ljava/lang/Object;
      // 7f: aload 9
      // 81: aload 1
      // 82: putfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.L$1 Ljava/lang/Object;
      // 85: aload 9
      // 87: aload 2
      // 88: putfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.L$2 Ljava/lang/Object;
      // 8b: aload 9
      // 8d: bipush 1
      // 8e: putfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.label I
      // 91: invokevirtual kotlin/sequences/SequenceScope.yield (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 94: dup
      // 95: aload 10
      // 97: if_acmpne c2
      // 9a: aload 10
      // 9c: areturn
      // 9d: bipush 0
      // 9e: istore 7
      // a0: aload 9
      // a2: getfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.L$2 Ljava/lang/Object;
      // a5: checkcast kotlin/coroutines/jvm/internal/CoroutineStackFrame
      // a8: astore 2
      // a9: aload 9
      // ab: getfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.L$1 Ljava/lang/Object;
      // ae: checkcast kotlin/sequences/SequenceScope
      // b1: astore 1
      // b2: aload 9
      // b4: getfield kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl$yieldFrames$1.L$0 Ljava/lang/Object;
      // b7: checkcast kotlinx/coroutines/debug/internal/DebugCoroutineInfoImpl
      // ba: astore 0
      // bb: aload 8
      // bd: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // c0: aload 8
      // c2: pop
      // c3: nop
      // c4: goto c9
      // c7: pop
      // c8: nop
      // c9: aload 2
      // ca: invokeinterface kotlin/coroutines/jvm/internal/CoroutineStackFrame.getCallerFrame ()Lkotlin/coroutines/jvm/internal/CoroutineStackFrame; 1
      // cf: astore 4
      // d1: aload 4
      // d3: ifnull e8
      // d6: aload 0
      // d7: astore 5
      // d9: aload 1
      // da: astore 6
      // dc: aload 5
      // de: astore 0
      // df: aload 6
      // e1: astore 1
      // e2: aload 4
      // e4: astore 2
      // e5: goto 5d
      // e8: goto eb
      // eb: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // ee: areturn
      // ef: new java/lang/IllegalStateException
      // f2: dup
      // f3: ldc "call to 'resume' before 'invoke' with coroutine"
      // f5: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // f8: athrow
   }

   public override fun toString(): String {
      return "DebugCoroutineInfo(state=${this.state},context=${this.context})"
   }
}
