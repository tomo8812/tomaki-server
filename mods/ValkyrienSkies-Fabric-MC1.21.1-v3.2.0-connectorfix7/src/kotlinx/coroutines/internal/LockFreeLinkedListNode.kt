package kotlinx.coroutines.internal

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.InternalCoroutinesApi
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@InternalCoroutinesApi
@SourceDebugExtension(["SMAP\nLockFreeLinkedList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LockFreeLinkedList.kt\nkotlinx/coroutines/internal/LockFreeLinkedListNode\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,367:1\n73#1,3:369\n1#2:368\n*S KotlinDebug\n*F\n+ 1 LockFreeLinkedList.kt\nkotlinx/coroutines/internal/LockFreeLinkedListNode\n*L\n134#1:369,3\n*E\n"])
public open class LockFreeLinkedListNode {
   @Volatile
   @Volatile
   private Object _next = this;
   @Volatile
   @Volatile
   private Object _prev = this;
   @Volatile
   @Volatile
   private Object _removedRef;
   @JvmStatic
   private AtomicReferenceFieldUpdater _next$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListNode.class, Object.class, "_next");
   @JvmStatic
   private AtomicReferenceFieldUpdater _prev$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListNode.class, Object.class, "_prev");
   @JvmStatic
   private AtomicReferenceFieldUpdater _removedRef$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListNode.class, Object.class, "_removedRef");

   private fun removed(): Removed {
      var var10000: Removed = _removedRef$FU.get(this) as Removed
      if (var10000 == null) {
         val var1: Removed = Removed(this)
         _removedRef$FU.lazySet(this, var1)
         var10000 = var1
      }

      return var10000
   }

   @PublishedApi
   internal inline fun makeCondAddOp(node: LockFreeLinkedListNode, crossinline condition: () -> Boolean): kotlinx.coroutines.internal.LockFreeLinkedListNode.CondAddOp {
      return object : kotlinx.coroutines.internal.LockFreeLinkedListNode.CondAddOp {
         public open fun prepare(affected: LockFreeLinkedListNode): Any? {
            return if (condition()) null else CONDITION_FALSE
         }

         // $VF: bridge methodSupplier
         @JvmSynthetic
         override fun prepare(affected: Any) {
            this.prepare(affected as LockFreeLinkedListNode)
         }
      }
   }

   public open val isRemoved: Boolean
      public open get() {
         return this.next is Removed
      }


   public final val next: Any
      public final get() {
         val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _next$FU

         while (true) {
            val next: Any = `atomicfu$handler$iv`.get(this)
            if (next !is OpDescriptor) {
               return next
            }

            (next as OpDescriptor).perform(this)
         }
      }


   public final val nextNode: LockFreeLinkedListNode
      public final get() {
         return LockFreeLinkedListKt.unwrap(this.next)
      }


   public final val prevNode: LockFreeLinkedListNode
      public final get() {
         var var10000: LockFreeLinkedListNode = this.correctPrev(null)
         if (var10000 == null) {
            var10000 = this.findPrevNonRemoved(_prev$FU.get(this) as LockFreeLinkedListNode)
         }

         return var10000
      }


   private tailrec fun findPrevNonRemoved(current: LockFreeLinkedListNode): LockFreeLinkedListNode {
      while (current.isRemoved) {
         val var3: Any = _prev$FU.get(current)
         this = this
         current = var3 as LockFreeLinkedListNode
      }

      return current
   }

   public fun addOneIfEmpty(node: LockFreeLinkedListNode): Boolean {
      _prev$FU.lazySet(node, this)
      _next$FU.lazySet(node, this)

      do {
         if (this.next != this) {
            return false
         }
      } while (!_next$FU.compareAndSet(this, this, node))

      node.finishAdd(this)
      return true
   }

   public fun addLast(node: LockFreeLinkedListNode) {
      while (!this.prevNode.addNext(node, this)) {
      }
   }

   public inline fun addLastIf(node: LockFreeLinkedListNode, crossinline condition: () -> Boolean): Boolean {
      val condAdd: LockFreeLinkedListNode.CondAddOp = object : kotlinx.coroutines.internal.LockFreeLinkedListNode.CondAddOp {
         public open fun prepare(affected: LockFreeLinkedListNode): Any? {
            return if (condition()) null else CONDITION_FALSE
         }

         // $VF: bridge methodSupplier
         @JvmSynthetic
         override fun prepare(affected: Any) {
            this.prepare(affected as LockFreeLinkedListNode)
         }
      }

      while (true) {
         when (this.prevNode.tryCondAddNext(node, this, condAdd)) {
            1 -> return true
            2 -> return false
            else -> {}
         }
      }
   }

   @PublishedApi
   internal fun addNext(node: LockFreeLinkedListNode, next: LockFreeLinkedListNode): Boolean {
      _prev$FU.lazySet(node, this)
      _next$FU.lazySet(node, next)
      if (!_next$FU.compareAndSet(this, next, node)) {
         return false
      } else {
         node.finishAdd(next)
         return true
      }
   }

   @PublishedApi
   internal fun tryCondAddNext(
      node: LockFreeLinkedListNode,
      next: LockFreeLinkedListNode,
      condAdd: kotlinx.coroutines.internal.LockFreeLinkedListNode.CondAddOp
   ): Int {
      _prev$FU.lazySet(node, this)
      _next$FU.lazySet(node, next)
      condAdd.oldNext = next
      if (!_next$FU.compareAndSet(this, next, condAdd)) {
         return 0
      } else {
         return if (condAdd.perform(this) == null) 1 else 2
      }
   }

   public open fun remove(): Boolean {
      return this.removeOrNext() == null
   }

   @PublishedApi
   internal fun removeOrNext(): LockFreeLinkedListNode? {
      val next: Any
      do {
         next = this.next
         if (next is Removed) {
            return (next as Removed).ref
         }

         if (next === this) {
            return next as LockFreeLinkedListNode
         }
      } while (!_next$FU.compareAndSet(this, next, (next as LockFreeLinkedListNode).removed()))

      (next as LockFreeLinkedListNode).correctPrev(null)
      return null
   }

   private fun finishAdd(next: LockFreeLinkedListNode) {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _prev$FU

      val nextPrev: LockFreeLinkedListNode
      do {
         nextPrev = `atomicfu$handler$iv`.get(next) as LockFreeLinkedListNode
         if (this.next != next) {
            return
         }
      } while (!_prev$FU.compareAndSet(next, nextPrev, this))

      if (this.isRemoved) {
         next.correctPrev(null)
      }
   }

   protected open fun nextIfRemoved(): LockFreeLinkedListNode? {
      val var1: Any = this.next
      return if ((var1 as? Removed) != null) (var1 as? Removed).ref else null
   }

   private tailrec fun correctPrev(op: OpDescriptor?): LockFreeLinkedListNode? {
      label62@ while (true) {
         val oldPrev: LockFreeLinkedListNode = _prev$FU.get(this) as LockFreeLinkedListNode
         var prev: LockFreeLinkedListNode = oldPrev
         var last: LockFreeLinkedListNode = null

         while (true) {
            val prevNext: Any = _next$FU.get(prev)
            if (prevNext === this) {
               if (oldPrev === prev) {
                  return prev
               }

               if (_prev$FU.compareAndSet(this, oldPrev, prev)) {
                  return prev
               }

               this = this
               op = op
               continue@label62
            }

            if (this.isRemoved) {
               return null
            }

            if (prevNext === op) {
               return prev
            }

            if (prevNext is OpDescriptor) {
               (prevNext as OpDescriptor).perform(prev)
               this = this
               op = op
               continue@label62
            }

            if (prevNext is Removed) {
               if (last != null) {
                  if (!_next$FU.compareAndSet(last, prev, (prevNext as Removed).ref)) {
                     this = this
                     op = op
                     continue@label62
                  }

                  prev = last
                  last = null
               } else {
                  prev = _prev$FU.get(prev) as LockFreeLinkedListNode
               }
            } else {
               last = prev
               prev = prevNext as LockFreeLinkedListNode
            }
         }
      }
   }

   internal fun validateNode(prev: LockFreeLinkedListNode, next: LockFreeLinkedListNode) {
      if (ASSERTIONS_ENABLED && prev != _prev$FU.get(this)) {
         throw AssertionError()
      } else if (ASSERTIONS_ENABLED && next != _next$FU.get(this)) {
         throw AssertionError()
      }
   }

   public override fun toString(): String {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
      //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
      //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
      //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
      //   at org.vineflower.kotlin.expr.KFunctionExprent.toJava(KFunctionExprent.java:134)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExitExprent.toJava(ExitExprent.java:85)
      //
      // Bytecode:
      // 00: new java/lang/StringBuilder
      // 03: dup
      // 04: invokespecial java/lang/StringBuilder.<init> ()V
      // 07: new kotlinx/coroutines/internal/LockFreeLinkedListNode$toString$1
      // 0a: dup
      // 0b: aload 0
      // 0c: invokespecial kotlinx/coroutines/internal/LockFreeLinkedListNode$toString$1.<init> (Ljava/lang/Object;)V
      // 0f: invokevirtual java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
      // 12: bipush 64
      // 14: invokevirtual java/lang/StringBuilder.append (C)Ljava/lang/StringBuilder;
      // 17: aload 0
      // 18: invokestatic kotlinx/coroutines/DebugStringsKt.getHexAddress (Ljava/lang/Object;)Ljava/lang/String;
      // 1b: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 1e: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 21: areturn
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   @PublishedApi
   internal abstract class CondAddOp : AtomicOp<LockFreeLinkedListNode> {
      @JvmField
      @NotNull
      public final val newNode: LockFreeLinkedListNode

      @JvmField
      @Nullable
      public final var oldNext: LockFreeLinkedListNode?
         private set

      open fun CondAddOp(newNode: LockFreeLinkedListNode) {
         this.newNode = newNode
      }

      public open fun complete(affected: LockFreeLinkedListNode, failure: Any?) {
         val success: Boolean = failure == null
         val update: LockFreeLinkedListNode = if (failure == null) this.newNode else this.oldNext
         if ((if (failure == null) this.newNode else this.oldNext) != null && LockFreeLinkedListNode._next$FU.compareAndSet(affected, this, update) && success) {
            val var10000: LockFreeLinkedListNode = this.newNode
            val var10001: LockFreeLinkedListNode = this.oldNext
            var10000.finishAdd(var10001)
         }
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override fun complete(affected: Any, failure: Any) {
         this.complete(affected as LockFreeLinkedListNode, failure)
      }
   }
}
