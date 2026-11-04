package org.valkyrienskies.mod.common

import org.valkyrienskies.core.internal.world.VsiPipeline
import org.valkyrienskies.core.internal.world.VsiServerShipWorld

public interface IShipObjectWorldServerProvider : IShipObjectWorldProvider {
   public val shipObjectWorld: VsiServerShipWorld?

   public val vsPipeline: VsiPipeline?
}
