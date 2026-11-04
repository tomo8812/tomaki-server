package org.valkyrienskies.mod.common.util

import org.valkyrienskies.core.api.ships.Ship

public interface IEntityDraggingInformationProvider {
   public val draggingInformation: EntityDraggingInformation

   public abstract fun `vs$shouldDrag`(): Boolean {
   }

   public open fun `vs$isInSealedArea`(): Boolean {
      return false
   }

   public open fun `vs$setInSealedArea`(sealed: Boolean) {
   }

   public open fun `vs$isGliding`(): Boolean {
      return false
   }

   public abstract fun `vs$dragImmediately`(ship: Ship?) {
   }
}
