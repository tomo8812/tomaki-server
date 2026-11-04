package kotlinx.coroutines.debug.internal

import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.util.NoSuchElementException
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceArray
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.collections.MutableMap.MutableEntry
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMutableIterator

@SourceDebugExtension(["SMAP\nConcurrentWeakMap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConcurrentWeakMap.kt\nkotlinx/coroutines/debug/internal/ConcurrentWeakMap\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,284:1\n1#2:285\n*E\n"])
internal class ConcurrentWeakMap<K, V>(weakRefQueue: Boolean = false) : AbstractMutableMap<K, V> {
   @Volatile
   @Volatile
   private int _size;
   @Volatile
   @Volatile
   private Object core = ConcurrentWeakMap.Core(16);
   private final val weakRefQueue: ReferenceQueue<Any>?
   @JvmStatic
   private AtomicIntegerFieldUpdater _size$FU = AtomicIntegerFieldUpdater.newUpdater(ConcurrentWeakMap.class, "_size");
   @JvmStatic
   private AtomicReferenceFieldUpdater core$FU = AtomicReferenceFieldUpdater.newUpdater(ConcurrentWeakMap.class, Object.class, "core");

   init {
      this.weakRefQueue = if (weakRefQueue) ReferenceQueue<>() else null
   }

   public open val size: Int
      public open get() {
         return _size$FU.get(this)
      }


   private fun decrementSize() {
      _size$FU.decrementAndGet(this)
   }

   public open operator fun get(key: Any): Any? {
      return (V)(if (key == null) null else (core$FU.get(this) as ConcurrentWeakMap.Core).getImpl((K)key))
   }

   public open fun put(key: Any, value: Any): Any? {
      var oldValue: Any = ConcurrentWeakMap.Core.putImpl$default(core$FU.get(this) as ConcurrentWeakMap.Core, key, value, null, 4, null)
      if (oldValue === ConcurrentWeakMapKt.access$getREHASH$p()) {
         oldValue = this.putSynchronized((K)key, (V)value)
      }

      if (oldValue == null) {
         _size$FU.incrementAndGet(this)
      }

      return (V)oldValue
   }

   public open fun remove(key: Any): Any? {
      if (key == null) {
         return null
      } else {
         var oldValue: Any = ConcurrentWeakMap.Core.putImpl$default(core$FU.get(this) as ConcurrentWeakMap.Core, key, null, null, 4, null)
         if (oldValue === ConcurrentWeakMapKt.access$getREHASH$p()) {
            oldValue = this.putSynchronized((K)key, null)
         }

         if (oldValue != null) {
            _size$FU.decrementAndGet(this)
         }

         return (V)oldValue
      }
   }

   @Synchronized
   private fun putSynchronized(key: Any, value: Any?): Any? {
      var curCore: ConcurrentWeakMap.Core = core$FU.get(this) as ConcurrentWeakMap.Core

      while (true) {
         val oldValue: Any = ConcurrentWeakMap.Core.putImpl$default(curCore, key, value, null, 4, null)
         if (oldValue != ConcurrentWeakMapKt.access$getREHASH$p()) {
            return (V)oldValue
         }

         curCore = curCore.rehash()
         core$FU.set(this, curCore)
      }
   }

   public open val keys: MutableSet<Any>
      public open get() {
         return ConcurrentWeakMap.KeyValueSet<K>({ k, <anonymous parameter 1> ->
            k
         }) as MutableSet<K>
      }


   public open val entries: MutableSet<MutableEntry<Any, Any>>
      public open get() {
         return ConcurrentWeakMap.KeyValueSet<java.util.Map.Entry<K, V>>({ k, v ->
            ConcurrentWeakMap.Entry(k, v) as java.util.Map.Entry
         }) as MutableSet<MutableMap.MutableEntry<K, V>>
      }


   public open fun clear() {
      for (k in this.keySet()) {
         this.remove(k)
      }
   }

   public fun runWeakRefQueueCleaningLoopUntilInterrupted() {
      if (this.weakRefQueue == null) {
         throw IllegalStateException("Must be created with weakRefQueue = true".toString())
      } else {
         try {
            while (true) {
               val var10001: Reference = this.weakRefQueue.remove()
               this.cleanWeakRef(var10001 as HashedWeakRef<*>)
            }
         } catch (var3: InterruptedException) {
            Thread.currentThread().interrupt()
         }
      }
   }

   private fun cleanWeakRef(w: HashedWeakRef<*>) {
      (core$FU.get(this) as ConcurrentWeakMap.Core).cleanWeakRef(w)
   }

   fun ConcurrentWeakMap() {
      this(false, 1, null)
   }

   private inner class Core(allocated: Int) {
      private final val allocated: Int
      private final val shift: Int
      private final val threshold: Int
      @Volatile
      @Volatile
      private int load;
      private AtomicReferenceArray keys;
      private AtomicReferenceArray values;
      @JvmStatic
      private AtomicIntegerFieldUpdater load$FU = AtomicIntegerFieldUpdater.newUpdater(ConcurrentWeakMap.Core.class, "load");

      init {
         this.allocated = allocated
         this.shift = Integer.numberOfLeadingZeros(this.allocated) + 1
         this.threshold = 2 * this.allocated / 3
         this.keys = AtomicReferenceArray(this.allocated)
         this.values = AtomicReferenceArray(this.allocated)
      }

      private fun index(hash: Int): Int {
         return hash * -1640531527 ushr this.shift
      }

      public fun getImpl(key: Any): Any? {
         var index: Int = this.index(key.hashCode())

         while (true) {
            val var10000: HashedWeakRef = this.keys.get(index) as HashedWeakRef
            if (var10000 == null) {
               return null
            }

            val k: Any = var10000.get()
            if (key == k) {
               val value: Any = this.values.get(index)
               return (V)(if (value is Marked) (value as Marked).ref else value)
            }

            if (k == null) {
               this.removeCleanedAt(index)
            }

            if (index == 0) {
               index = this.allocated
            }

            index--
         }
      }

      private fun removeCleanedAt(index: Int) {
         val var10000: Any
         do {
            var10000 = this.values.get(index)
            if (var10000 == null) {
               return
            }

            if (var10000 is Marked) {
               return
            }
         } while (!this.values.compareAndSet(index, var10000, null))

         ConcurrentWeakMap.this.decrementSize()
      }

      public fun putImpl(key: Any, value: Any?, weakKey0: HashedWeakRef<Any>? = null): Any? {
         var index: Int = this.index(key.hashCode())
         var loadIncremented: Boolean = false
         var weakKey: HashedWeakRef = weakKey0

         while (true) {
            val oldValue: HashedWeakRef = this.keys.get(index) as HashedWeakRef
            if (oldValue == null) {
               if (value == null) {
                  return null
               }

               if (!loadIncremented) {
                  val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = load$FU

                  val var11: Int
                  val n: Int
                  do {
                     var11 = `atomicfu$handler$iv`.get(this)
                     n = var11
                     if (var11 >= this.threshold) {
                        return ConcurrentWeakMapKt.access$getREHASH$p()
                     }
                  } while (!`atomicfu$handler$iv`.compareAndSet(this, var11, ++n))

                  loadIncremented = true
               }

               if (weakKey == null) {
                  weakKey = HashedWeakRef<>(key, ConcurrentWeakMap.this.weakRefQueue)
               }

               if (this.keys.compareAndSet(index, null, weakKey)) {
                  break
               }
            } else {
               val k: Any = oldValue.get()
               if (key == k) {
                  if (loadIncremented) {
                     load$FU.decrementAndGet(this)
                  }
                  break
               }

               if (k == null) {
                  this.removeCleanedAt(index)
               }

               if (index == 0) {
                  index = this.allocated
               }

               index--
            }
         }

         val var15: Any
         do {
            var15 = this.values.get(index)
            if (var15 is Marked) {
               return ConcurrentWeakMapKt.access$getREHASH$p()
            }
         } while (!this.values.compareAndSet(index, var15, value))

         return var15
      }

      public fun rehash(): kotlinx.coroutines.debug.internal.ConcurrentWeakMap.Core {
         label70@ while (true) {
            val newCore: ConcurrentWeakMap.Core = ConcurrentWeakMap.this.Core(
               Integer.highestOneBit(RangesKt.coerceAtLeast(ConcurrentWeakMap.this.size(), 4)) * 4
            )
            var index: Int = 0

            for (var4 in this.allocated..index) {
               val w: HashedWeakRef = this.keys.get(index) as HashedWeakRef
               val k: Any = if (w != null) w.get() else null
               if (w != null && k == null) {
                  this.removeCleanedAt(index)
               }

               var var11: Any
               do {
                  var11 = this.values.get(index)
                  if (var11 is Marked) {
                     var11 = (var11 as Marked).ref
                     break
                  }
               } while (!this.values.compareAndSet(index, var11, ConcurrentWeakMapKt.access$mark(var11)))

               if (k != null && var11 != null) {
                  val oldValue: Any = newCore.putImpl(k, (HashedWeakRef)var11, w)
                  if (oldValue === ConcurrentWeakMapKt.access$getREHASH$p()) {
                     continue@label70
                  }

                  if (_Assertions.ENABLED && oldValue != null) {
                     throw AssertionError("Assertion failed")
                  }
               }
            }

            return newCore
         }
      }

      public fun cleanWeakRef(weakRef: HashedWeakRef<*>) {
         var index: Int = this.index(weakRef.hash)

         while (true) {
            val var10000: HashedWeakRef = this.keys.get(index) as HashedWeakRef
            if (var10000 == null) {
               return
            }

            if (var10000 === weakRef) {
               this.removeCleanedAt(index)
               return
            }

            if (index == 0) {
               index = this.allocated
            }

            index--
         }
      }

      public fun <E> keyValueIterator(factory: (Any, Any) -> Any): MutableIterator<Any> {
         return ConcurrentWeakMap.Core.KeyValueIterator(factory)
      }

      fun `update$atomicfu`(`atomicfu$handler`: AtomicIntegerFieldUpdater, `atomicfu$action`: (Int?) -> Int, `atomicfu$dispatchReceiver`: Any) {
         val var5: Int
         do {
            var5 = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`)
         } while (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, var5, (`atomicfu$action`(var5) as java.lang.Number).intValue()))
      }

      @SourceDebugExtension(["SMAP\nConcurrentWeakMap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConcurrentWeakMap.kt\nkotlinx/coroutines/debug/internal/ConcurrentWeakMap$Core$KeyValueIterator\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,284:1\n1#2:285\n*E\n"])
      private inner class KeyValueIterator<E>(factory: (Any, Any) -> Any) : java.util.Iterator<E>, KMutableIterator {
         private final val factory: (Any, Any) -> Any
         private final var index: Int
         private final lateinit var key: Any
         private final lateinit var value: Any

         init {
            this.factory = factory
            this.index = -1
            this.findNext()
         }

         private fun findNext() {
            while (true) {
               this.index++
               if (this.index >= Core.this.allocated) {
                  return
               }

               val var10001: HashedWeakRef = Core.this.keys.get(this.index) as HashedWeakRef
               if (var10001 != null) {
                  val var2: Any = var10001.get()
                  if (var2 != null) {
                     this.key = var2
                     var value: Any = Core.this.values.get(this.index)
                     if (value is Marked) {
                        value = (value as Marked).ref
                     }

                     if (value != null) {
                        this.value = value
                        return
                     }
                  }
               }
            }
         }

         public override operator fun hasNext(): Boolean {
            return this.index < Core.this.allocated
         }

         public override operator fun next(): Any {
            if (this.index >= Core.this.allocated) {
               throw NoSuchElementException()
            } else {
               var var10001: Any = this.key
               if (this.key == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("key")
                  var10001 = Unit.INSTANCE
               }

               var var10002: Any = this.value
               if (this.value == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("value")
                  var10002 = Unit.INSTANCE
               }

               val var1: Any = this.factory(var10001, var10002)
               this.findNext()
               return (E)var1
            }
         }

         public open fun remove(): Nothing {
            ConcurrentWeakMapKt.access$noImpl()
            throw KotlinNothingValueException()
         }

         // $VF: bridge methodSupplier
         @JvmSynthetic
         override fun remove() {
            this.remove()
         }
      }
   }

   private class Entry<K, V>(key: Any, value: Any) : java.util.Map.Entry<K, V>, kotlin.jvm.internal.markers.KMutableMap.Entry {
      public open val key: Any
      public open val value: Any

      init {
         this.key = (K)key
         this.value = (V)value
      }

      public override fun setValue(newValue: Any): Any {
         ConcurrentWeakMapKt.access$noImpl()
         throw KotlinNothingValueException()
      }
   }

   private inner class KeyValueSet<E>(factory: (Any, Any) -> Any) : AbstractMutableSet<E> {
      private final val factory: (Any, Any) -> Any

      init {
         this.factory = factory
      }

      public open val size: Int
         public open get() {
            return ConcurrentWeakMap.this.size()
         }


      public open fun add(element: Any): Boolean {
         ConcurrentWeakMapKt.access$noImpl()
         throw KotlinNothingValueException()
      }

      public open operator fun iterator(): MutableIterator<Any> {
         return (ConcurrentWeakMap.core$FU.get(ConcurrentWeakMap.this) as ConcurrentWeakMap.Core).keyValueIterator(this.factory)
      }
   }
}
