package org.valkyrienskies.core.internal.collision

import kotlin.jvm.internal.SourceDebugExtension
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc

@SourceDebugExtension(["SMAP\nVsiConvexPolygonc.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VsiConvexPolygonc.kt\norg/valkyrienskies/core/internal/collision/VsiConvexPolygonc\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,50:1\n1863#2,2:51\n*S KotlinDebug\n*F\n+ 1 VsiConvexPolygonc.kt\norg/valkyrienskies/core/internal/collision/VsiConvexPolygonc\n*L\n42#1:51,2\n*E\n"])
public interface VsiConvexPolygonc {
   public val points: Iterable<Vector3dc>

   public val normals: Iterable<Vector3dc>

   public val shipFrom: Long?

   public val aabb: AABBdc

   public open fun getEnclosingAABB(output: AABBd): AABBd {
      output.minX = java.lang.Double.POSITIVE_INFINITY
      output.minY = java.lang.Double.POSITIVE_INFINITY
      output.minZ = java.lang.Double.POSITIVE_INFINITY
      output.maxX = java.lang.Double.NEGATIVE_INFINITY
      output.maxY = java.lang.Double.NEGATIVE_INFINITY
      output.maxZ = java.lang.Double.NEGATIVE_INFINITY

      for (point in this.points) {
         output.minX = Math.min(output.minX, point.x())
         output.minY = Math.min(output.minY, point.y())
         output.minZ = Math.min(output.minZ, point.z())
         output.maxX = Math.max(output.maxX, point.x())
         output.maxY = Math.max(output.maxY, point.y())
         output.maxZ = Math.max(output.maxZ, point.z())
      }

      return output
   }

   public open fun computeCenterPos(output: Vector3d): Vector3d {
      output.zero()
      var pointsCount: Int = 0

      for (`element$iv` in this.points) {
         output.add(`element$iv` as Vector3dc)
         pointsCount++
      }

      if (pointsCount > 0) {
         output.div((double)pointsCount)
      }

      return output
   }
}
