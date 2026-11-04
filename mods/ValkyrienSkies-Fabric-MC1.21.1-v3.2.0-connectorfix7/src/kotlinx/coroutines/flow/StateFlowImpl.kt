package kotlinx.coroutines.flow

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.internal.AbstractSharedFlow
import kotlinx.coroutines.flow.internal.AbstractSharedFlowSlot
import kotlinx.coroutines.flow.internal.FusibleFlow
import kotlinx.coroutines.flow.internal.NullSurrogateKt
import kotlinx.coroutines.internal.Symbol

@SourceDebugExtension(["SMAP\nStateFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowImpl\n+ 2 Symbol.kt\nkotlinx/coroutines/internal/Symbol\n+ 3 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 4 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n+ 5 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 6 CoroutineScope.kt\nkotlinx/coroutines/CoroutineScopeKt\n*L\n1#1,428:1\n18#2:429\n18#2:443\n28#3,4:430\n28#3,4:437\n20#4:434\n20#4:441\n13579#5,2:435\n329#6:442\n*S KotlinDebug\n*F\n+ 1 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowImpl\n*L\n315#1:429\n396#1:443\n324#1:430,4\n352#1:437,4\n324#1:434\n352#1:441\n348#1:435,2\n385#1:442\n*E\n"])
private class StateFlowImpl<T>(initialState: Any) : AbstractSharedFlow<StateFlowSlot>, MutableStateFlow<T>, CancellableFlow<T>, FusibleFlow<T> {
   @Volatile
   @Volatile
   private Object _state;
   private final var sequence: Int
   @JvmStatic
   private AtomicReferenceFieldUpdater _state$FU = AtomicReferenceFieldUpdater.newUpdater(StateFlowImpl.class, Object.class, "_state");

   init {
      this._state = initialState
   }

   public open var value: Any
      public open get() {
         val `this_$iv`: Symbol = NullSurrogateKt.NULL
         val `value$iv`: Any = _state$FU.get(this)
         return (T)(if (`value$iv` === `this_$iv`) null else `value$iv`)
      }

      public open set(value) {
         var var10002: Any = value
         if (value == null) {
            var10002 = NullSurrogateKt.NULL
         }

         this.updateState(null, var10002)
      }


   public override fun compareAndSet(expect: Any, update: Any): Boolean {
      var var10001: Any = expect
      if (expect == null) {
         var10001 = NullSurrogateKt.NULL
      }

      var var10002: Any = update
      if (update == null) {
         var10002 = NullSurrogateKt.NULL
      }

      return this.updateState(var10001, var10002)
   }

   private fun updateState(expectedState: Any?, newState: Any): Boolean {
      var var21: Int
      var var23: Array<AbstractSharedFlowSlot>
      synchronized (this) {
         val oldState: Any = _state$FU.get(this)
         if (expectedState != null && !(oldState == expectedState)) {
            return false
         }

         if (oldState == newState) {
            return true
         }

         _state$FU.set(this, newState)
         var21 = this.sequence
         if ((this.sequence and 1) != 0) {
            this.sequence += 2
            return true
         }

         this.sequence = ++var21
         var23 = this.getSlots()
      }

      while (true) {
         val var10000: Array<Any> = var23 as Array<StateFlowSlot>
         if (var23 as Array<StateFlowSlot> != null) {
            for (`element$iv` in var10000) {
               if (`element$iv` != null) {
                  ((StateFlowSlot)`element$iv`).makePending()
               }
            }
         }

         synchronized (this) {
            if (this.sequence == var21) {
               this.sequence = var21 + 1
               return true
            }

            var21 = this.sequence
            var23 = this.getSlots()
         }
      }
   }

   public open val replayCache: List<Any>
      public open get() {
         return CollectionsKt.listOf(this.value)
      }


   public override fun tryEmit(value: Any): Boolean {
      this.value = value
      return true
   }

   public override suspend fun emit(value: Any) {
      this.value = value
      return Unit.INSTANCE
   }

   public override fun resetReplayCache() {
      throw UnsupportedOperationException("MutableStateFlow.resetReplayCache is not supported")
   }

   public override suspend fun collect(collector: FlowCollector<Any>): Nothing {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:122)
      //
      // Bytecode:
      // 000: aload 2
      // 001: instanceof kotlinx/coroutines/flow/StateFlowImpl$collect$1
      // 004: ifeq 027
      // 007: aload 2
      // 008: checkcast kotlinx/coroutines/flow/StateFlowImpl$collect$1
      // 00b: astore 10
      // 00d: aload 10
      // 00f: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.label I
      // 012: ldc -2147483648
      // 014: iand
      // 015: ifeq 027
      // 018: aload 10
      // 01a: dup
      // 01b: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.label I
      // 01e: ldc -2147483648
      // 020: isub
      // 021: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.label I
      // 024: goto 032
      // 027: new kotlinx/coroutines/flow/StateFlowImpl$collect$1
      // 02a: dup
      // 02b: aload 0
      // 02c: aload 2
      // 02d: invokespecial kotlinx/coroutines/flow/StateFlowImpl$collect$1.<init> (Lkotlinx/coroutines/flow/StateFlowImpl;Lkotlin/coroutines/Continuation;)V
      // 030: astore 10
      // 032: aload 10
      // 034: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.result Ljava/lang/Object;
      // 037: astore 9
      // 039: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 03c: astore 11
      // 03e: aload 10
      // 040: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.label I
      // 043: tableswitch 457 0 3 29 92 271 388
      // 060: aload 9
      // 062: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 065: aload 0
      // 066: invokevirtual kotlinx/coroutines/flow/StateFlowImpl.allocateSlot ()Lkotlinx/coroutines/flow/internal/AbstractSharedFlowSlot;
      // 069: checkcast kotlinx/coroutines/flow/StateFlowSlot
      // 06c: astore 3
      // 06d: nop
      // 06e: aload 1
      // 06f: instanceof kotlinx/coroutines/flow/SubscribedFlowCollector
      // 072: ifeq 0c3
      // 075: aload 1
      // 076: checkcast kotlinx/coroutines/flow/SubscribedFlowCollector
      // 079: aload 10
      // 07b: aload 10
      // 07d: aload 0
      // 07e: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$0 Ljava/lang/Object;
      // 081: aload 10
      // 083: aload 1
      // 084: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$1 Ljava/lang/Object;
      // 087: aload 10
      // 089: aload 3
      // 08a: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$2 Ljava/lang/Object;
      // 08d: aload 10
      // 08f: bipush 1
      // 090: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.label I
      // 093: invokevirtual kotlinx/coroutines/flow/SubscribedFlowCollector.onSubscription (Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 096: dup
      // 097: aload 11
      // 099: if_acmpne 0c2
      // 09c: aload 11
      // 09e: areturn
      // 09f: aload 10
      // 0a1: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$2 Ljava/lang/Object;
      // 0a4: checkcast kotlinx/coroutines/flow/StateFlowSlot
      // 0a7: astore 3
      // 0a8: aload 10
      // 0aa: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$1 Ljava/lang/Object;
      // 0ad: checkcast kotlinx/coroutines/flow/FlowCollector
      // 0b0: astore 1
      // 0b1: aload 10
      // 0b3: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$0 Ljava/lang/Object;
      // 0b6: checkcast kotlinx/coroutines/flow/StateFlowImpl
      // 0b9: astore 0
      // 0ba: nop
      // 0bb: aload 9
      // 0bd: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 0c0: aload 9
      // 0c2: pop
      // 0c3: bipush 0
      // 0c4: istore 5
      // 0c6: aload 10
      // 0c8: invokeinterface kotlin/coroutines/Continuation.getContext ()Lkotlin/coroutines/CoroutineContext; 1
      // 0cd: nop
      // 0ce: getstatic kotlinx/coroutines/Job.Key Lkotlinx/coroutines/Job$Key;
      // 0d1: checkcast kotlin/coroutines/CoroutineContext$Key
      // 0d4: invokeinterface kotlin/coroutines/CoroutineContext.get (Lkotlin/coroutines/CoroutineContext$Key;)Lkotlin/coroutines/CoroutineContext$Element; 2
      // 0d9: checkcast kotlinx/coroutines/Job
      // 0dc: astore 4
      // 0de: aconst_null
      // 0df: astore 5
      // 0e1: getstatic kotlinx/coroutines/flow/StateFlowImpl._state$FU Ljava/util/concurrent/atomic/AtomicReferenceFieldUpdater;
      // 0e4: aload 0
      // 0e5: invokevirtual java/util/concurrent/atomic/AtomicReferenceFieldUpdater.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 0e8: astore 6
      // 0ea: aload 4
      // 0ec: dup
      // 0ed: ifnull 0f6
      // 0f0: invokestatic kotlinx/coroutines/JobKt.ensureActive (Lkotlinx/coroutines/Job;)V
      // 0f3: goto 0f7
      // 0f6: pop
      // 0f7: aload 5
      // 0f9: ifnull 106
      // 0fc: aload 5
      // 0fe: aload 6
      // 100: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 103: ifne 18b
      // 106: aload 1
      // 107: getstatic kotlinx/coroutines/flow/internal/NullSurrogateKt.NULL Lkotlinx/coroutines/internal/Symbol;
      // 10a: astore 7
      // 10c: bipush 0
      // 10d: istore 8
      // 10f: aload 6
      // 111: aload 7
      // 113: if_acmpne 11a
      // 116: aconst_null
      // 117: goto 11c
      // 11a: aload 6
      // 11c: aload 10
      // 11e: aload 10
      // 120: aload 0
      // 121: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$0 Ljava/lang/Object;
      // 124: aload 10
      // 126: aload 1
      // 127: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$1 Ljava/lang/Object;
      // 12a: aload 10
      // 12c: aload 3
      // 12d: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$2 Ljava/lang/Object;
      // 130: aload 10
      // 132: aload 4
      // 134: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$3 Ljava/lang/Object;
      // 137: aload 10
      // 139: aload 6
      // 13b: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$4 Ljava/lang/Object;
      // 13e: aload 10
      // 140: bipush 2
      // 141: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.label I
      // 144: invokeinterface kotlinx/coroutines/flow/FlowCollector.emit (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
      // 149: dup
      // 14a: aload 11
      // 14c: if_acmpne 186
      // 14f: aload 11
      // 151: areturn
      // 152: aload 10
      // 154: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$4 Ljava/lang/Object;
      // 157: astore 6
      // 159: aload 10
      // 15b: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$3 Ljava/lang/Object;
      // 15e: checkcast kotlinx/coroutines/Job
      // 161: astore 4
      // 163: aload 10
      // 165: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$2 Ljava/lang/Object;
      // 168: checkcast kotlinx/coroutines/flow/StateFlowSlot
      // 16b: astore 3
      // 16c: aload 10
      // 16e: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$1 Ljava/lang/Object;
      // 171: checkcast kotlinx/coroutines/flow/FlowCollector
      // 174: astore 1
      // 175: aload 10
      // 177: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$0 Ljava/lang/Object;
      // 17a: checkcast kotlinx/coroutines/flow/StateFlowImpl
      // 17d: astore 0
      // 17e: nop
      // 17f: aload 9
      // 181: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 184: aload 9
      // 186: pop
      // 187: aload 6
      // 189: astore 5
      // 18b: aload 3
      // 18c: invokevirtual kotlinx/coroutines/flow/StateFlowSlot.takePending ()Z
      // 18f: ifne 0e1
      // 192: aload 3
      // 193: aload 10
      // 195: aload 10
      // 197: aload 0
      // 198: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$0 Ljava/lang/Object;
      // 19b: aload 10
      // 19d: aload 1
      // 19e: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$1 Ljava/lang/Object;
      // 1a1: aload 10
      // 1a3: aload 3
      // 1a4: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$2 Ljava/lang/Object;
      // 1a7: aload 10
      // 1a9: aload 4
      // 1ab: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$3 Ljava/lang/Object;
      // 1ae: aload 10
      // 1b0: aload 5
      // 1b2: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$4 Ljava/lang/Object;
      // 1b5: aload 10
      // 1b7: bipush 3
      // 1b8: putfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.label I
      // 1bb: invokevirtual kotlinx/coroutines/flow/StateFlowSlot.awaitPending (Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 1be: dup
      // 1bf: aload 11
      // 1c1: if_acmpne 1fb
      // 1c4: aload 11
      // 1c6: areturn
      // 1c7: aload 10
      // 1c9: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$4 Ljava/lang/Object;
      // 1cc: astore 5
      // 1ce: aload 10
      // 1d0: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$3 Ljava/lang/Object;
      // 1d3: checkcast kotlinx/coroutines/Job
      // 1d6: astore 4
      // 1d8: aload 10
      // 1da: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$2 Ljava/lang/Object;
      // 1dd: checkcast kotlinx/coroutines/flow/StateFlowSlot
      // 1e0: astore 3
      // 1e1: aload 10
      // 1e3: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$1 Ljava/lang/Object;
      // 1e6: checkcast kotlinx/coroutines/flow/FlowCollector
      // 1e9: astore 1
      // 1ea: aload 10
      // 1ec: getfield kotlinx/coroutines/flow/StateFlowImpl$collect$1.L$0 Ljava/lang/Object;
      // 1ef: checkcast kotlinx/coroutines/flow/StateFlowImpl
      // 1f2: astore 0
      // 1f3: nop
      // 1f4: aload 9
      // 1f6: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 1f9: aload 9
      // 1fb: pop
      // 1fc: goto 0e1
      // 1ff: astore 4
      // 201: aload 0
      // 202: aload 3
      // 203: checkcast kotlinx/coroutines/flow/internal/AbstractSharedFlowSlot
      // 206: invokevirtual kotlinx/coroutines/flow/StateFlowImpl.freeSlot (Lkotlinx/coroutines/flow/internal/AbstractSharedFlowSlot;)V
      // 209: aload 4
      // 20b: athrow
      // 20c: new java/lang/IllegalStateException
      // 20f: dup
      // 210: ldc_w "call to 'resume' before 'invoke' with coroutine"
      // 213: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 216: athrow
   }

   protected open fun createSlot(): StateFlowSlot {
      return StateFlowSlot()
   }

   protected open fun createSlotArray(size: Int): Array<StateFlowSlot?> {
      return arrayOfNulls(size)
   }

   public override fun fuse(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): Flow<Any> {
      return StateFlowKt.fuseStateFlow(this, context, capacity, onBufferOverflow)
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
}
