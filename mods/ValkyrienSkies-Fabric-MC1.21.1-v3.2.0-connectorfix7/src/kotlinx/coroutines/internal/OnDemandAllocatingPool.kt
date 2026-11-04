package kotlinx.coroutines.internal

import java.util.ArrayList
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceArray
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOnDemandAllocatingPool.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OnDemandAllocatingPool.kt\nkotlinx/coroutines/internal/OnDemandAllocatingPool\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 OnDemandAllocatingPool.kt\nkotlinx/coroutines/internal/OnDemandAllocatingPoolKt\n*L\n1#1,107:1\n41#1:108\n41#1:109\n35#1,7:110\n41#1:127\n1549#2:117\n1620#2,2:118\n1622#2:122\n1549#2:123\n1620#2,3:124\n101#3,2:120\n*S KotlinDebug\n*F\n+ 1 OnDemandAllocatingPool.kt\nkotlinx/coroutines/internal/OnDemandAllocatingPool\n*L\n35#1:108\n54#1:109\n76#1:110,7\n92#1:127\n77#1:117\n77#1:118,2\n77#1:122\n91#1:123\n91#1:124,3\n79#1:120,2\n*E\n"])
internal class OnDemandAllocatingPool<T>(maxCapacity: Int, create: (Int) -> Any) {
   private final val maxCapacity: Int
   private final val create: (Int) -> Any
   @Volatile
   @Volatile
   private int controlState;
   private AtomicReferenceArray elements;
   @JvmStatic
   private AtomicIntegerFieldUpdater controlState$FU = AtomicIntegerFieldUpdater.newUpdater(OnDemandAllocatingPool.class, "controlState");

   init {
      this.maxCapacity = maxCapacity
      this.create = create
      this.elements = AtomicReferenceArray(this.maxCapacity)
   }

   private inline fun tryForbidNewElements(): Int {
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = controlState$FU

      val it: Int
      do {
         it = `atomicfu$handler$iv`.get(this)
         if ((it and Integer.MIN_VALUE) != 0) {
            return 0
         }
      } while (!controlState$FU.compareAndSet(this, it, it or Integer.MIN_VALUE))

      return it
   }

   private inline fun Int.isClosed(): Boolean {
      return (`$this$isClosed` and Integer.MIN_VALUE) != 0
   }

   public fun allocate(): Boolean {
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = controlState$FU

      val ctl: Int
      do {
         ctl = `atomicfu$handler$iv`.get(this)
         if ((ctl and Integer.MIN_VALUE) != 0) {
            return false
         }

         if (ctl >= this.maxCapacity) {
            return true
         }
      } while (!controlState$FU.compareAndSet(this, ctl, ctl + 1))

      this.elements.set(ctl, this.create(ctl))
      return true
   }

   public fun close(): List<Any> {
      val `$this$map$iv`: OnDemandAllocatingPool = this
      val `destination$iv$iv`: AtomicIntegerFieldUpdater = controlState$FU

      var var10000: Int
      while (true) {
         val `it$iv`: Int = `destination$iv$iv`.get(`$this$map$iv`)
         if ((`it$iv` and Integer.MIN_VALUE) != 0) {
            var10000 = 0
            break
         }

         if (controlState$FU.compareAndSet(`$this$map$iv`, `it$iv`, `it$iv` or Integer.MIN_VALUE)) {
            var10000 = `it$iv`
            break
         }
      }

      val var15: java.lang.Iterable = RangesKt.until(0, var10000) as java.lang.Iterable
      val var17: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var15, 10))
      val var19: java.util.Iterator = var15.iterator()

      while (var19.hasNext()) {
         val i: Int = (var19 as IntIterator).nextInt()

         val element: Any
         do {
            element = this.elements.getAndSet(i, null)
         } while (element == null)

         var17.add(element)
      }

      return var17 as MutableList<T>
   }

   internal fun stateRepresentation(): String {
      val ctl: Int = controlState$FU.get(this)
      val closedStr: java.lang.Iterable = RangesKt.until(0, ctl and Integer.MAX_VALUE) as java.lang.Iterable
      val `$i$f$isClosed`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(closedStr, 10))
      val var8: java.util.Iterator = closedStr.iterator()

      while (var8.hasNext()) {
         `$i$f$isClosed`.add(this.elements.get((var8 as IntIterator).nextInt()))
      }

      return "${(`$i$f$isClosed` as java.util.List).toString()}${if ((ctl and Integer.MIN_VALUE) != 0) "[closed]" else ""}"
   }

   public override fun toString(): String {
      return "OnDemandAllocatingPool(${this.stateRepresentation$kotlinx_coroutines_core()})"
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicIntegerFieldUpdater, `atomicfu$action`: (Int?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }
}
