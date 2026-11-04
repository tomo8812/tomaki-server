package kotlinx.coroutines.internal

public abstract class OpDescriptor {
   public abstract fun perform(affected: Any?): Any? {
   }

   public abstract val atomicOp: AtomicOp<*>?

   public override fun toString(): String {
      return "${classSimpleName}@${hexAddress}"
   }
}
