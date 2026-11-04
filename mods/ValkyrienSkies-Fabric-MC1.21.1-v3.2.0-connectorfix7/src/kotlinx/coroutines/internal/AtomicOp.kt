package kotlinx.coroutines.internal

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@SourceDebugExtension(["SMAP\nAtomic.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Atomic.kt\nkotlinx/coroutines/internal/AtomicOp\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,79:1\n1#2:80\n*E\n"])
public abstract class AtomicOp<T> : OpDescriptor {
   @Volatile
   @Volatile
   private Object _consensus = AtomicKt.NO_DECISION;
   @JvmStatic
   private AtomicReferenceFieldUpdater _consensus$FU = AtomicReferenceFieldUpdater.newUpdater(AtomicOp.class, Object.class, "_consensus");

   public open val atomicOp: AtomicOp<*>
      public open get() {
         return this
      }


   private fun decide(decision: Any?): Any? {
      if (ASSERTIONS_ENABLED && decision === AtomicKt.NO_DECISION) {
         throw AssertionError()
      } else {
         val var3: Any = _consensus$FU.get(this)
         if (var3 != AtomicKt.NO_DECISION) {
            return var3
         } else {
            return if (_consensus$FU.compareAndSet(this, AtomicKt.NO_DECISION, decision)) decision else _consensus$FU.get(this)
         }
      }
   }

   public abstract fun prepare(affected: Any): Any? {
   }

   public abstract fun complete(affected: Any, failure: Any?) {
   }

   public override fun perform(affected: Any?): Any? {
      var decision: Any = _consensus$FU.get(this)
      if (decision === AtomicKt.NO_DECISION) {
         decision = this.decide(this.prepare((T)affected))
      }

      this.complete((T)affected, decision)
      return decision
   }
}
