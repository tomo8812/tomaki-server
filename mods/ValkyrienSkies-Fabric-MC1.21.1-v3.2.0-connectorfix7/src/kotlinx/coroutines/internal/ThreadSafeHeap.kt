package kotlinx.coroutines.internal

import java.util.Arrays
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@SourceDebugExtension(["SMAP\nThreadSafeHeap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ThreadSafeHeap.kt\nkotlinx/coroutines/internal/ThreadSafeHeap\n+ 2 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 3 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,168:1\n28#2,4:169\n28#2,4:174\n28#2,4:179\n28#2,4:184\n28#2,4:189\n28#2,4:194\n28#2,4:199\n28#2,4:204\n20#3:173\n20#3:178\n20#3:183\n20#3:188\n20#3:193\n20#3:198\n20#3:203\n20#3:208\n1#4:209\n*S KotlinDebug\n*F\n+ 1 ThreadSafeHeap.kt\nkotlinx/coroutines/internal/ThreadSafeHeap\n*L\n35#1:169,4\n42#1:174,4\n50#1:179,4\n52#1:184,4\n60#1:189,4\n69#1:194,4\n72#1:199,4\n81#1:204,4\n35#1:173\n42#1:178\n50#1:183\n52#1:188\n60#1:193\n69#1:198\n72#1:203\n81#1:208\n*E\n"])
public open class ThreadSafeHeap<T extends ThreadSafeHeapNode & java.lang.Comparable<? super T>> {
   private final var a: Array<Any?>?
   @Volatile
   @Volatile
   private int _size;
   @JvmStatic
   private AtomicIntegerFieldUpdater _size$FU = AtomicIntegerFieldUpdater.newUpdater(ThreadSafeHeap.class, "_size");

   public final var size: Int
      public final get() {
         return _size$FU.get(this)
      }

      private final set(value) {
         _size$FU.set(this, value)
      }


   public final val isEmpty: Boolean
      public final get() {
         return this.size == 0
      }


   public fun clear() {
      synchronized (this) {
         if (this.a != null) {
            ArraysKt.fill$default(this.a, null, 0, 0, 6, null)
         }

         _size$FU.set(this, 0)
      }
   }

   public fun find(predicate: (Any) -> Boolean): Any? {
      var var12: ThreadSafeHeapNode
      synchronized (this) {
         var i: Int = 0
         val var7: Int = this.size

         while (true) {
            if (i >= var7) {
               var12 = null
               break
            }

            var12 = if (this.a != null) this.a[i] else null
            if (predicate(var12) as java.lang.Boolean) {
               var12 = var12
               break
            }

            i++
         }

         var12 = var12
      }

      return (T)var12
   }

   public fun peek(): Any? {
      val var10000: ThreadSafeHeapNode
      synchronized (this) {
         var10000 = this.firstImpl()
      }

      return (T)var10000
   }

   public fun removeFirstOrNull(): Any? {
      val var10000: ThreadSafeHeapNode
      synchronized (this) {
         var10000 = if (this.size > 0) this.removeAtImpl(0) else null
      }

      return (T)var10000
   }

   public inline fun removeFirstIf(predicate: (Any) -> Boolean): Any? {
      synchronized (this){} // $VF: monitorenter 

      var var9: Any
      run label51@{
         var var8: ThreadSafeHeapNode
         try {
            val var13: Boolean = true
            val var10000: ThreadSafeHeapNode = this.firstImpl()
            if (var10000 == null) {
               var9 = null
               return@label51
            }

            var8 = if (predicate(var10000)) this.removeAtImpl(0) else null
         } finally {
            if (var11) {
               InlineMarker.finallyStart(1)
               // $VF: monitorexit
               InlineMarker.finallyEnd(1)
            }
         }

         InlineMarker.finallyStart(1)
         // $VF: monitorexit
         InlineMarker.finallyEnd(1)
         return (T)var8
         val var11: Boolean
      }

      InlineMarker.finallyStart(2)
      // $VF: monitorexit
      InlineMarker.finallyEnd(2)
      return (T)var9
   }

   public fun addLast(node: Any) {
      synchronized (this) {
         this.addImpl((T)node)
      }
   }

   public inline fun addLastIf(node: Any, cond: (Any?) -> Boolean): Boolean {
      var var10: Boolean
      synchronized (this) {
         if (cond(this.firstImpl()) as java.lang.Boolean) {
            this.addImpl((T)node)
            var10 = true
         } else {
            var10 = false
         }

         InlineMarker.finallyStart(1)
         InlineMarker.finallyEnd(1)
         var10 = var10
      }

      return var10
   }

   public fun remove(node: Any): Boolean {
      val var9: Boolean
      synchronized (this) {
         val var10000: Boolean
         if (node.heap == null) {
            var10000 = false
         } else {
            val index: Int = node.index
            if (ASSERTIONS_ENABLED && index < 0) {
               throw AssertionError()
            }

            this.removeAtImpl(index)
            var10000 = true
         }

         var9 = var10000
      }

      return var9
   }

   @PublishedApi
   internal fun firstImpl(): Any? {
      return if (this.a != null) this.a[0] else null
   }

   @PublishedApi
   internal fun removeAtImpl(index: Int): Any {
      if (ASSERTIONS_ENABLED && this.size <= 0) {
         throw AssertionError()
      } else {
         var var10000: Array<ThreadSafeHeapNode>
         var10000 = this.a
         this.size = this.size + -1
         label45@
         if (index < this.size) {
            this.swap(index, this.size)
            val var6: Int = (index - 1) / 2
            if (index > 0) {
               val var8: ThreadSafeHeapNode = var10000[index]
               val var9: java.lang.Comparable = var8 as java.lang.Comparable
               val var10001: ThreadSafeHeapNode = var10000[var6]
               if (var9.compareTo(var10001) < 0) {
                  this.swap(index, var6)
                  this.siftUpFrom(var6)
                  break@label45
               }
            }

            this.siftDownFrom(index)
         }

         val var10: ThreadSafeHeapNode = var10000[this.size]
         if (ASSERTIONS_ENABLED && var10.heap != this) {
            throw AssertionError()
         } else {
            var10.heap = null
            var10.index = -1
            var10000[this.size] = null
            return (T)var10
         }
      }
   }

   @PublishedApi
   internal fun addImpl(node: Any) {
      if (ASSERTIONS_ENABLED && node.heap != null) {
         throw AssertionError()
      } else {
         node.heap = this
         val var5: Array<ThreadSafeHeapNode> = this.realloc()
         val var4: Int = this.size
         this.size = var4 + 1
         var5[var4] = node
         node.index = var4
         this.siftUpFrom(var4)
      }
   }

   private tailrec fun siftUpFrom(i: Int) {
      while (i > 0) {
         val var10000: Array<ThreadSafeHeapNode> = this.a
         val j: Int = (i - 1) / 2
         val var4: ThreadSafeHeapNode = var10000[(i - 1) / 2]
         val var5: java.lang.Comparable = var4 as java.lang.Comparable
         val var10001: ThreadSafeHeapNode = var10000[i]
         if (var5.compareTo(var10001) <= 0) {
            return
         }

         this.swap(i, j)
         this = this
         i = j
      }
   }

   private tailrec fun siftDownFrom(i: Int) {
      while (true) {
         var j: Int = 2 * i + 1
         if (2 * i + 1 >= this.size) {
            return
         }

         val var10000: Array<ThreadSafeHeapNode> = this.a
         if (j + 1 < this.size) {
            val var6: ThreadSafeHeapNode = var10000[j + 1]
            val var7: java.lang.Comparable = var6 as java.lang.Comparable
            val var10001: ThreadSafeHeapNode = var10000[j]
            if (var7.compareTo(var10001) < 0) {
               j++
            }
         }

         val var8: ThreadSafeHeapNode = var10000[i]
         val var9: java.lang.Comparable = var8 as java.lang.Comparable
         val var10: ThreadSafeHeapNode = var10000[j]
         if (var9.compareTo(var10) <= 0) {
            return
         }

         this.swap(i, j)
         this = this
         i = j
      }
   }

   private fun realloc(): Array<Any?> {
      val a: Array<ThreadSafeHeapNode> = this.a
      val var10000: Array<ThreadSafeHeapNode>
      if (this.a == null) {
         val var2: Array<ThreadSafeHeapNode> = arrayOfNulls(4)
         this.a = (T[])var2
         var10000 = var2
      } else if (this.size >= a.length) {
         val var8: Array<Any> = Arrays.copyOf(a, this.size * 2)
         this.a = (T[])var8
         var10000 = var8 as Array<ThreadSafeHeapNode>
      } else {
         var10000 = a
      }

      return (T[])var10000
   }

   private fun swap(i: Int, j: Int) {
      val var10000: Array<ThreadSafeHeapNode> = this.a
      val var6: ThreadSafeHeapNode = var10000[j]
      val var7: ThreadSafeHeapNode = var10000[i]
      var10000[i] = var6
      var10000[j] = var7
      var6.index = i
      var7.index = j
   }
}
