package org.valkyrienskies.core.internal.collision

import org.joml.Matrix4dc
import org.joml.Vector3dc
import org.joml.primitives.AABBdc

public interface VsiEntityPolygonCollider {
   public abstract fun adjustEntityMovementForPolygonCollisions(
      movement: Vector3dc,
      entityBoundingBox: AABBdc,
      entityStepHeight: Double,
      collidingPolygons: List<VsiConvexPolygonc>
   ): Pair<Vector3dc, Long?> {
   }

   public abstract fun createPolygonFromAABB(aabb: AABBdc, transform: Matrix4dc? = null, shipFrom: Long? = null): VsiConvexPolygonc {
   }
}
