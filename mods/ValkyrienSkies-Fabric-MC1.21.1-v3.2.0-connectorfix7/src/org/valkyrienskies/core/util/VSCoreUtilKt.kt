@file:SourceDebugExtension(["SMAP\nVSCoreUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSCoreUtil.kt\norg/valkyrienskies/core/util/VSCoreUtilKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,173:1\n144#1:177\n145#1,16:181\n1#2:174\n1317#3,2:175\n12727#4,3:178\n12727#4,3:197\n*S KotlinDebug\n*F\n+ 1 VSCoreUtil.kt\norg/valkyrienskies/core/util/VSCoreUtilKt\n*L\n138#1:177\n138#1:181,16\n29#1:175,2\n138#1:178,3\n144#1:197,3\n*E\n"])

package org.valkyrienskies.core.util

import com.google.common.collect.ImmutableSet
import com.google.common.collect.ImmutableSet.Builder
import io.netty.buffer.ByteBuf
import java.util.ConcurrentModificationException
import java.util.function.Consumer
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic

public fun Int.squared(): Int {
   return `$this$squared` * `$this$squared`
}

public fun Double.squared(): Double {
   return `$this$squared` * `$this$squared`
}

public fun <A, B : Any> Consumer<Any>.variance(): Consumer<Any> {
   return `$this$variance`
}

public fun <T> Sequence<Any>.toImmutableSet(): ImmutableSet<Any> {
   val var1: Builder = ImmutableSet.builder()
   val builder: Builder = var1

   for (`element$iv` in `$this$toImmutableSet`) {
      builder.add(`element$iv`)
   }

   val var10000: ImmutableSet = var1.build()
   return var10000
}

public fun <T> Iterable<Any>.toImmutableSet(): ImmutableSet<Any> {
   val var10000: ImmutableSet = ImmutableSet.copyOf(`$this$toImmutableSet`)
   return var10000
}

public fun ByteBuf.readVec3fAsDouble(): Vector3d {
   return Vector3d(`$this$readVec3fAsDouble`.readFloat(), `$this$readVec3fAsDouble`.readFloat(), `$this$readVec3fAsDouble`.readFloat())
}

public fun ByteBuf.writeVec3AsFloat(v: Vector3dc) {
   `$this$writeVec3AsFloat`.writeFloat((float)v.x())
   `$this$writeVec3AsFloat`.writeFloat((float)v.y())
   `$this$writeVec3AsFloat`.writeFloat((float)v.z())
}

public fun ByteBuf.readQuatfAsDouble(): Quaterniond {
   return Quaterniond(
      `$this$readQuatfAsDouble`.readFloat(),
      `$this$readQuatfAsDouble`.readFloat(),
      `$this$readQuatfAsDouble`.readFloat(),
      `$this$readQuatfAsDouble`.readFloat()
   )
}

public fun ByteBuf.writeQuatfAsFloat(q: Quaterniondc) {
   `$this$writeQuatfAsFloat`.writeFloat((float)q.x())
   `$this$writeQuatfAsFloat`.writeFloat((float)q.y())
   `$this$writeQuatfAsFloat`.writeFloat((float)q.z())
   `$this$writeQuatfAsFloat`.writeFloat((float)q.w())
}

public fun ByteBuf.writeNormQuatdAs3F(q: Quaterniondc) {
   if (Math.abs(q.lengthSquared() - 1.0) > 1.0E-12) {
      throw IllegalArgumentException("The input quaternion $q is not normalized!")
   } else {
      if (q.w() > 0.0) {
         `$this$writeNormQuatdAs3F`.writeFloat((float)q.x())
         `$this$writeNormQuatdAs3F`.writeFloat((float)q.y())
         `$this$writeNormQuatdAs3F`.writeFloat((float)q.z())
      } else {
         `$this$writeNormQuatdAs3F`.writeFloat(-((float)q.x()))
         `$this$writeNormQuatdAs3F`.writeFloat(-((float)q.y()))
         `$this$writeNormQuatdAs3F`.writeFloat(-((float)q.z()))
      }
   }
}

public fun ByteBuf.read3FAsNormQuatd(): Quaterniond {
   val x: Double = `$this$read3FAsNormQuatd`.readFloat()
   val y: Double = `$this$read3FAsNormQuatd`.readFloat()
   val z: Double = `$this$read3FAsNormQuatd`.readFloat()
   var wSquared: Double = 1.0 - (x * x + y * y + z * z)
   if (1.0 - (x * x + y * y + z * z) < 0.0) {
      if (!(wSquared > -1.0E-7)) {
         throw IllegalStateException(
            "The computed value for wSquared is $wSquared; which is negative! Did we send a non-normalized quaternion? Or was packet corrupted?"
         )
      }

      wSquared = 0.0
   }

   val var10000: Quaterniond = Quaterniond(x, y, z, Math.sqrt(wSquared)).normalize()
   return var10000
}

public fun ByteBuf.writeQuatd(q: Quaterniondc) {
   `$this$writeQuatd`.writeDouble(q.x())
   `$this$writeQuatd`.writeDouble(q.y())
   `$this$writeQuatd`.writeDouble(q.z())
   `$this$writeQuatd`.writeDouble(q.w())
}

public fun ByteBuf.readQuatd(): Quaterniond {
   return Quaterniond(`$this$readQuatd`.readDouble(), `$this$readQuatd`.readDouble(), `$this$readQuatd`.readDouble(), `$this$readQuatd`.readDouble())
}

public fun ByteBuf.writeVec3d(v: Vector3dc) {
   `$this$writeVec3d`.writeDouble(v.x())
   `$this$writeVec3d`.writeDouble(v.y())
   `$this$writeVec3d`.writeDouble(v.z())
}

public fun ByteBuf.readVec3d(): Vector3d {
   return Vector3d(`$this$readVec3d`.readDouble(), `$this$readVec3d`.readDouble(), `$this$readVec3d`.readDouble())
}

public fun ByteBuf.writeAABBi(a: AABBic) {
   `$this$writeAABBi`.writeInt(a.minX())
   `$this$writeAABBi`.writeInt(a.minY())
   `$this$writeAABBi`.writeInt(a.minZ())
   `$this$writeAABBi`.writeInt(a.maxX())
   `$this$writeAABBi`.writeInt(a.maxY())
   `$this$writeAABBi`.writeInt(a.maxZ())
}

public fun ByteBuf.readAABBi(): AABBi {
   return AABBi(
      `$this$readAABBi`.readInt(),
      `$this$readAABBi`.readInt(),
      `$this$readAABBi`.readInt(),
      `$this$readAABBi`.readInt(),
      `$this$readAABBi`.readInt(),
      `$this$readAABBi`.readInt()
   )
}

@JvmSynthetic
public inline fun <T, reified R> Array<Any>.mapToArray(transform: (Any) -> Any): Array<Any> {
   var var3: Int = 0
   val var4: Int = `$this$mapToArray`.length
   Intrinsics.reifiedOperationMarker(0, "R")
   val var5: Array<Any> = arrayOfNulls(var4)

   while (var3 < var4) {
      var5[var3] = transform(`$this$mapToArray`[var3])
      var3++
   }

   return (R[])var5
}

@JvmSynthetic
public inline fun <reified T> Array<Any?>.filterNotNullToArray(): Array<Any> {
   val `$this$filterToArray$iv`: Array<Any> = `$this$filterNotNullToArray`
   var `count$iv$iv`: Int = 0

   for (`element$iv$iv` in `$this$filterNotNullToArray`) {
      if (`element$iv$iv` != null) {
         `count$iv$iv`++
      }
   }

   Intrinsics.reifiedOperationMarker(0, "T?")
   val `newArray$iv`: Array<Any> = arrayOfNulls(`count$iv$iv`)
   var var14: Int = 0

   try {
      for (var17 in `$this$filterToArray$iv`) {
         if (var17 != null) {
            `newArray$iv`[var14++] = var17
         }
      }
   } catch (var13: IndexOutOfBoundsException) {
      throw ConcurrentModificationException("Array was modified while filtering", var13)
   }

   if (var14 != `newArray$iv`.length) {
      throw ConcurrentModificationException("Array was modified while filtering")
   } else {
      return (T[])`newArray$iv`
   }
}

@JvmSynthetic
public inline fun <reified T> Array<Any>.filterToArray(predicate: (Any) -> Boolean): Array<Any> {
   var `count$iv`: Int = 0

   for (`element$iv` in `$this$filterToArray`) {
      if (predicate(`element$iv`) as java.lang.Boolean) {
         `count$iv`++
      }
   }

   Intrinsics.reifiedOperationMarker(0, "T?")
   val newArray: Array<Any> = arrayOfNulls(`count$iv`)
   var var11: Int = 0

   try {
      for (var14 in `$this$filterToArray`) {
         if (predicate(var14) as java.lang.Boolean) {
            newArray[var11++] = var14
         }
      }
   } catch (var10: IndexOutOfBoundsException) {
      throw ConcurrentModificationException("Array was modified while filtering", var10)
   }

   if (var11 != newArray.length) {
      throw ConcurrentModificationException("Array was modified while filtering")
   } else {
      return (T[])newArray
   }
}

public fun asLong(x: Int, y: Int): Long {
   return x or (long)y shl 32
}

public fun asInts(x: Long): Pair<Int, Int> {
   return Pair((int)x, (int)(x shr 32))
}

public inline fun tryAndPrint(func: () -> Unit) {
   try {
      func()
   } catch (var3: Exception) {
      var3.printStackTrace()
   }
}
