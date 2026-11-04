package org.valkyrienskies.core.api.events

import org.joml.Vector3ic
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.GameTickOnly

@VsBeta
@GameTickOnly
public interface MergeEvent {
   public val stillPocket: Boolean

   public val oldRootA: Vector3ic

   public val oldRootB: Vector3ic

   public val newRoot: Vector3ic

   public val voxelType: Int

   public var dimensionId: String?
}
