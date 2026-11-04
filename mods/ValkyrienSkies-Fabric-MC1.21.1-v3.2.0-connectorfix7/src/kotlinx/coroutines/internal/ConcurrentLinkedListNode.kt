package kotlinx.coroutines.internal

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nConcurrentLinkedList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/ConcurrentLinkedListNode\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,269:1\n107#1,7:270\n1#2:277\n*S KotlinDebug\n*F\n+ 1 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/ConcurrentLinkedListNode\n*L\n115#1:270,7\n*E\n"])
internal abstract class ConcurrentLinkedListNode<N extends ConcurrentLinkedListNode<N>> {
   @Volatile
   @Volatile
   private Object _next;
   @Volatile
   @Volatile
   private Object _prev;
   @JvmStatic
   private AtomicReferenceFieldUpdater _next$FU = AtomicReferenceFieldUpdater.newUpdater(ConcurrentLinkedListNode.class, Object.class, "_next");
   @JvmStatic
   private AtomicReferenceFieldUpdater _prev$FU = AtomicReferenceFieldUpdater.newUpdater(ConcurrentLinkedListNode.class, Object.class, "_prev");

   open fun ConcurrentLinkedListNode(prev: N?) {
      this._prev = prev
   }

   private final val nextOrClosed: Any?
      private final get() {
         return _next$FU.get(this)
      }


   public inline fun nextOrIfClosed(onClosedAction: () -> Nothing): Any? {
      val it: Any = access$getNextOrClosed(this)
      if (it === ConcurrentLinkedListKt.access$getCLOSED$p()) {
         onClosedAction()
         throw KotlinNothingValueException()
      } else {
         return (N)it
      }
   }

   public final val next: Any?
      public final get() {
         val `it$iv`: Any = access$getNextOrClosed(this)
         return (N)(if (`it$iv` === ConcurrentLinkedListKt.access$getCLOSED$p()) null else `it$iv`)
      }


   public fun trySetNext(value: Any): Boolean {
      return _next$FU.compareAndSet(this, null, value)
   }

   public final val isTail: Boolean
      public final get() {
         return this.next == null
      }


   public final val prev: Any?
      public final get() {
         return (N)_prev$FU.get(this)
      }


   public fun cleanPrev() {
      _prev$FU.lazySet(this, null)
   }

   public fun markAsClosed(): Boolean {
      return _next$FU.compareAndSet(this, null, ConcurrentLinkedListKt.access$getCLOSED$p())
   }

   public abstract val isRemoved: Boolean

   public fun remove() {
      if (ASSERTIONS_ENABLED && !this.isRemoved && !this.isTail) {
         throw AssertionError()
      } else if (!this.isTail) {
         val next: ConcurrentLinkedListNode
         val var9: ConcurrentLinkedListNode
         do {
            var9 = this.aliveSegmentLeft
            next = this.aliveSegmentRight
            val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _prev$FU

            val var6: Any
            do {
               var6 = `atomicfu$handler$iv`.get(next)
            } while (!`atomicfu$handler$iv`.compareAndSet(next, var6, if (var6 as ConcurrentLinkedListNode == null) null else var9))

            if (var9 != null) {
               _next$FU.set(var9, next)
            }
         } while (next.isRemoved && !next.isTail || var9 != null && var9.isRemoved)
      }
   }

   private final val aliveSegmentLeft: Any?
      private final get() {
         var cur: ConcurrentLinkedListNode = this.prev

         while (cur != null && cur.isRemoved) {
            cur = _prev$FU.get(cur) as ConcurrentLinkedListNode
         }

         return (N)cur
      }


   private final val aliveSegmentRight: Any
      private final get() {
         if (ASSERTIONS_ENABLED && this.isTail) {
            throw AssertionError()
         } else {
            var var10000: ConcurrentLinkedListNode = this.next
            var var2: ConcurrentLinkedListNode = var10000

            while (var2.isRemoved) {
               var10000 = var2.next
               if (var10000 == null) {
                  return (N)var2
               }

               var2 = var10000
            }

            return (N)var2
         }
      }


   fun `update$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Any, `atomicfu$dispatchReceiver`: Any) {
      val var5: Any
      do {
         var5 = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`)
      } while (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, var5, `atomicfu$action`(var5)))
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getNextOrClosed`(`$this`: ConcurrentLinkedListNode) {
      `$this`.nextOrClosed
   }
}
