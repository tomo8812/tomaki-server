package org.valkyrienskies.core.internal.world

import java.util.UUID
import org.joml.Vector3d

public interface VsiPlayer {
   public abstract fun getPosition(dest: Vector3d): Vector3d {
   }

   public abstract fun getPlayerState(): VsiPlayerState {
   }

   public val dimension: String

   public val uuid: UUID

   public val isAdmin: Boolean

   public open val canModifyServerConfig: Boolean
      public open get() {
         return this.isAdmin
      }


   public val forceWatchingShips: Set<Long>
}
