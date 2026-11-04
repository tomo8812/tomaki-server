package org.valkyrienskies.core.api.bodies.shape

import org.joml.primitives.AABBic

public interface Voxel : BodyShape {
   public val definedArea: AABBic

   public abstract fun applyUpdateAsync(update: VoxelUpdate) {
   }

   public abstract fun applyUpdateSync(update: VoxelUpdate) {
   }
}
