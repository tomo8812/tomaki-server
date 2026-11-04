package org.valkyrienskies.core.api.ships

import org.joml.Matrix4dc

public interface ContraptionWingProvider {
   public var wingGroupId: Int

   public abstract fun computeContraptionWingTransform(): Matrix4dc {
   }
}
