@file:SourceDebugExtension(["SMAP\nUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Util.kt\norg/valkyrienskies/core/util/UtilKt\n*L\n1#1,77:1\n10#1,5:78\n66#1,5:83\n*S KotlinDebug\n*F\n+ 1 Util.kt\norg/valkyrienskies/core/util/UtilKt\n*L\n37#1:78,5\n60#1:83,5\n*E\n"])

package org.valkyrienskies.core.util

import java.nio.ByteBuffer
import java.util.Queue
import kotlin.jvm.internal.SourceDebugExtension
import org.joml.Vector3i
import org.joml.Vector3ic

public inline fun Byte.iterateBits(func: (Boolean, Int) -> Unit) {
   for (i in 7 downTo 0) {
      func((`$this$iterateBits` and 1 shl i) != 0, i)
   }
}

public inline fun Int.iterateBits(func: (Boolean, Int) -> Unit) {
   for (i in 31 downTo 0) {
      func((`$this$iterateBits` and 1 shl i) != 0, i)
   }
}

public inline fun Long.iterateBits(func: (Boolean, Int) -> Unit) {
   for (i in 63 downTo 0) {
      func((`$this$iterateBits` and 1L shl i) != 0L, i)
   }
}

public inline fun ByteBuffer.iterateBits(func: (Boolean, Int) -> Unit) {
   var i: Int = 0
   val var4: Int = `$this$iterateBits`.capacity()
   if (0 <= var4) {
      while (true) {
         val `$this$iterateBits$iv`: Byte = `$this$iterateBits`.get(i)

         for (`i$iv` in 7 downTo 0) {
            func((`$this$iterateBits$iv` and 1 shl `i$iv`) != 0, i * 8 + `i$iv`)
         }

         if (i == var4) {
            break
         }

         i++
      }
   }
}

public inline fun <T> Queue<Any>.pollUntilEmpty(func: (Any) -> Unit) {
   while (true) {
      val var10001: Any = `$this$pollUntilEmpty`.poll()
      if (var10001 == null) {
         return
      }

      func(var10001)
   }
}

public inline fun <T> Queue<Any>.pollUntilEmpty(func: (Any) -> Unit, predicate: (Any) -> Boolean) {
   while (true) {
      val var10000: Any = `$this$pollUntilEmpty`.poll()
      if (var10000 == null) {
         return
      }

      if (predicate(var10000) as java.lang.Boolean) {
         func(var10000)
      }
   }
}

public fun unwrapIndex(index: Int, dimensions: Vector3ic, v: Vector3i): Vector3i {
   val `z$iv`: Int = index / (dimensions.x() * dimensions.y())
   val var10000: Vector3i = v.set(
      (index - `z$iv` * dimensions.x() * dimensions.y()) % dimensions.x(), (index - `z$iv` * dimensions.x() * dimensions.y()) / dimensions.x(), `z$iv`
   )
   return var10000
}

public inline fun <R> unwrapIndex(index: Int, dimensions: Vector3ic, out: (Int, Int, Int) -> Any): Any {
   val z: Int = index / (dimensions.x() * dimensions.y())
   return (R)out((index - z * dimensions.x() * dimensions.y()) % dimensions.x(), (index - z * dimensions.x() * dimensions.y()) / dimensions.x(), z)
}

public fun wrapIndex(x: Int, y: Int, z: Int, dimensions: Vector3ic): Int {
   return x + y * x + z * x * y
}

public fun wrapIndex(point: Vector3ic, dimensions: Vector3ic): Int {
   return wrapIndex(x, y, z, dimensions)
}
