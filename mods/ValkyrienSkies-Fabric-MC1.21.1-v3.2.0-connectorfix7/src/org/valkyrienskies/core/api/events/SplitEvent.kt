package org.valkyrienskies.core.api.events

import org.joml.Vector3ic
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.GameTickOnly

@VsBeta
@GameTickOnly
public interface SplitEvent {
   public val wasPocket: Boolean

   public val oldRoot: Vector3ic

   public val newRootA: Vector3ic

   public val newRootB: Vector3ic

   public val voxelType: Int

   public var dimensionId: String?
}
