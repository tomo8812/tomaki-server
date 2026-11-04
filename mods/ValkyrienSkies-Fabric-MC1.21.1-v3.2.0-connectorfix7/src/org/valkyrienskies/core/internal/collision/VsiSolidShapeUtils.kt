package org.valkyrienskies.core.internal.collision

import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape
import org.valkyrienskies.core.api.physics.blockstates.CollisionPoint

public interface VsiSolidShapeUtils {
   public abstract fun mergeBoxes(boxes: MutableList<AABBic>): MutableList<AABBic> {
   }

   public abstract fun cutBoxes(boxes: MutableList<AABBic>, cut: AABBic): MutableList<AABBic> {
   }

   public abstract fun generateCollisionPointsForBoxes(boxes: List<AABBic>): List<CollisionPoint> {
   }

   public abstract fun generateShapeFromBoxes(posBoxes: MutableList<AABBic>): BoxesBlockShape? {
   }
}
