package kotlinx.coroutines.internal

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater

internal open class LockFreeTaskQueue<E>(singleConsumer: Boolean) {
   @Volatile
   @Volatile
   private Object _cur;
   @JvmStatic
   private AtomicReferenceFieldUpdater _cur$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeTaskQueue.class, Object.class, "_cur");

   init {
      this._cur = LockFreeTaskQueueCore(8, singleConsumer)
   }

   public final val isEmpty: Boolean
      public final get() {
         return (_cur$FU.get(this) as LockFreeTaskQueueCore).isEmpty
      }


   public final val size: Int
      public final get() {
         return (_cur$FU.get(this) as LockFreeTaskQueueCore).size
      }


   public fun close() {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _cur$FU

      while (true) {
         val cur: LockFreeTaskQueueCore = `atomicfu$handler$iv`.get(this) as LockFreeTaskQueueCore
         if (cur.close()) {
            return
         }

         _cur$FU.compareAndSet(this, cur, cur.next())
      }
   }

   public fun addLast(element: Any): Boolean {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _cur$FU

      while (true) {
         val cur: LockFreeTaskQueueCore = `atomicfu$handler$iv`.get(this) as LockFreeTaskQueueCore
         when (cur.addLast(element)) {
            0 -> return true
            1 -> _cur$FU.compareAndSet(this, cur, cur.next())
            2 -> return false
            else -> continue
         }
      }
   }

   public fun removeFirstOrNull(): Any? {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _cur$FU

      while (true) {
         val cur: LockFreeTaskQueueCore = `atomicfu$handler$iv`.get(this) as LockFreeTaskQueueCore
         val result: Any = cur.removeFirstOrNull()
         if (result != LockFreeTaskQueueCore.REMOVE_FROZEN) {
            return (E)result
         }

         _cur$FU.compareAndSet(this, cur, cur.next())
      }
   }

   public fun <R> map(transform: (Any) -> Any): List<Any> {
      return (_cur$FU.get(this) as LockFreeTaskQueueCore).map(transform)
   }

   public fun isClosed(): Boolean {
      return (_cur$FU.get(this) as LockFreeTaskQueueCore).isClosed()
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }
}
