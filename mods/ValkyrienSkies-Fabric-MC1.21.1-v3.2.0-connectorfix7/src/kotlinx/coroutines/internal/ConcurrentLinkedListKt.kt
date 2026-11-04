@file:SourceDebugExtension(["SMAP\nConcurrentLinkedList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/ConcurrentLinkedListKt\n+ 2 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/ConcurrentLinkedListNode\n*L\n1#1,269:1\n46#1,8:284\n107#2,7:270\n107#2,7:277\n*S KotlinDebug\n*F\n+ 1 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/ConcurrentLinkedListKt\n*L\n74#1:284,8\n27#1:270,7\n85#1:277,7\n*E\n"])

package kotlinx.coroutines.internal

import java.util.concurrent.atomic.AtomicIntegerArray
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceArray
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.jvm.internal.SourceDebugExtension

private const val POINTERS_SHIFT: Int = 16
private final val CLOSED: Symbol = Symbol("CLOSED")

internal fun <S : Segment<Any>> Any.findSegmentInternal(id: Long, createNewSegment: (Long, Any) -> Any): SegmentOrClosed<Any> {
   var cur: Segment = `$this$findSegmentInternal`

   while (cur.id < id || cur.isRemoved) {
      val `it$iv`: Any = ConcurrentLinkedListNode.access$getNextOrClosed(cur)
      if (`it$iv` === access$getCLOSED$p()) {
         return SegmentOrClosed.constructor_impl/* $VF was: constructor-impl */(CLOSED)
      }

      val next: Segment = (`it$iv` as ConcurrentLinkedListNode) as Segment
      if ((`it$iv` as ConcurrentLinkedListNode) as Segment != null) {
         cur = next
      } else {
         val var11: Segment = createNewSegment(cur.id + 1L, cur) as Segment
         if (cur.trySetNext(var11)) {
            if (cur.isRemoved) {
               cur.remove()
            }

            cur = var11
         }
      }
   }

   return SegmentOrClosed.constructor_impl/* $VF was: constructor-impl */(cur)
}

internal fun <N : ConcurrentLinkedListNode<Any>> Any.close(): Any {
   var var8: ConcurrentLinkedListNode = `$this$close`

   while (true) {
      val `it$iv`: Any = ConcurrentLinkedListNode.access$getNextOrClosed(var8)
      if (`it$iv` === access$getCLOSED$p()) {
         return (N)var8
      }

      val next: ConcurrentLinkedListNode = `it$iv` as ConcurrentLinkedListNode
      if (`it$iv` as ConcurrentLinkedListNode == null) {
         if (var8.markAsClosed()) {
            return (N)var8
         }
      } else {
         var8 = next
      }
   }
}

fun `moveForward$atomicfu`(`atomicfu$dispatchReceiver`: Any?, `atomicfu$handler`: AtomicReferenceFieldUpdater, to: Segment): Boolean {
   while (true) {
      val cur: Segment = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`) as Segment
      if (cur.id >= to.id) {
         true
      }

      if (!to.tryIncPointers$kotlinx_coroutines_core()) {
         false
      }

      if (`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, cur, to)) {
         if (cur.decPointers$kotlinx_coroutines_core()) {
            cur.remove()
         }

         true
      }

      if (to.decPointers$kotlinx_coroutines_core()) {
         to.remove()
      }
   }
}

fun `moveForward$atomicfu$array`(`atomicfu$dispatchReceiver`: Any?, `atomicfu$handler`: AtomicReferenceArray, `atomicfu$index`: Int, to: Segment): Boolean {
   while (true) {
      val cur: Segment = `atomicfu$handler`.get(`atomicfu$index`) as Segment
      if (cur.id >= to.id) {
         true
      }

      if (!to.tryIncPointers$kotlinx_coroutines_core()) {
         false
      }

      if (`atomicfu$handler`.compareAndSet(`atomicfu$index`, cur, to)) {
         if (cur.decPointers$kotlinx_coroutines_core()) {
            cur.remove()
         }

         true
      }

      if (to.decPointers$kotlinx_coroutines_core()) {
         to.remove()
      }
   }
}

fun `findSegmentAndMoveForward$atomicfu`(
   `atomicfu$dispatchReceiver`: Any?,
   `atomicfu$handler`: AtomicReferenceFieldUpdater,
   id: Long,
   startFrom: Segment,
   createNewSegment: (java.lang.Long?, S?) -> S
) {
   label55@ while (true) {
      val s: Any = findSegmentInternal(startFrom, id, createNewSegment)
      if (isClosed) {
         s
      }

      val `to$iv`: Segment = segment

      while (true) {
         val `cur$iv`: Segment = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`) as Segment
         val var10000: Boolean
         if (`cur$iv`.id >= `to$iv`.id) {
            var10000 = true
         } else if (!`to$iv`.tryIncPointers$kotlinx_coroutines_core()) {
            var10000 = false
         } else {
            if (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, `cur$iv`, `to$iv`)) {
               if (`to$iv`.decPointers$kotlinx_coroutines_core()) {
                  `to$iv`.remove()
               }
               continue
            }

            if (`cur$iv`.decPointers$kotlinx_coroutines_core()) {
               `cur$iv`.remove()
            }

            var10000 = true
         }

         if (var10000) {
            s
         }
         continue@label55
      }
   }
}

fun `findSegmentAndMoveForward$atomicfu$array`(
   `atomicfu$dispatchReceiver`: Any?,
   `atomicfu$handler`: AtomicReferenceArray,
   `atomicfu$index`: Int,
   id: Long,
   startFrom: Segment,
   createNewSegment: (java.lang.Long?, S?) -> S
) {
   label55@ while (true) {
      val s: Any = findSegmentInternal(startFrom, id, createNewSegment)
      if (isClosed) {
         s
      }

      val `to$iv`: Segment = segment

      while (true) {
         val `cur$iv`: Segment = `atomicfu$handler`.get(`atomicfu$index`) as Segment
         val var10000: Boolean
         if (`cur$iv`.id >= `to$iv`.id) {
            var10000 = true
         } else if (!`to$iv`.tryIncPointers$kotlinx_coroutines_core()) {
            var10000 = false
         } else {
            if (!`atomicfu$handler`.compareAndSet(`atomicfu$index`, `cur$iv`, `to$iv`)) {
               if (`to$iv`.decPointers$kotlinx_coroutines_core()) {
                  `to$iv`.remove()
               }
               continue
            }

            if (`cur$iv`.decPointers$kotlinx_coroutines_core()) {
               `cur$iv`.remove()
            }

            var10000 = true
         }

         if (var10000) {
            s
         }
         continue@label55
      }
   }
}

fun `addConditionally$atomicfu`(
   `atomicfu$dispatchReceiver`: Any, `atomicfu$handler`: AtomicIntegerFieldUpdater, delta: Int, condition: (Int?) -> java.lang.Boolean
): Boolean {
   val cur: Int
   do {
      cur = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`)
      if (!condition(cur) as java.lang.Boolean) {
         false
      }
   } while (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, cur, cur + delta))

   true
}

fun `addConditionally$atomicfu$array`(
   `atomicfu$dispatchReceiver`: Any, `atomicfu$handler`: AtomicIntegerArray, `atomicfu$index`: Int, delta: Int, condition: (Int?) -> java.lang.Boolean
): Boolean {
   val cur: Int
   do {
      cur = `atomicfu$handler`.get(`atomicfu$index`)
      if (!condition(cur) as java.lang.Boolean) {
         false
      }
   } while (!`atomicfu$handler`.compareAndSet(`atomicfu$index`, cur, cur + delta))

   true
}

fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
   while (true) {
      `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
   }
}

fun `loop$atomicfu$array`(`atomicfu$handler`: AtomicReferenceArray, `atomicfu$index`: Int, `atomicfu$action`: (Any?) -> Unit) {
   while (true) {
      `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$index`))
   }
}

@JvmSynthetic
fun `access$getCLOSED$p`(): Symbol {
   CLOSED
}
