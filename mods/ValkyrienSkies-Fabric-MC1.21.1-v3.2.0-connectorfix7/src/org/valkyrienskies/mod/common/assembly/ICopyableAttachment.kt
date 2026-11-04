package org.valkyrienskies.mod.common.assembly

import java.util.function.Supplier
import net.minecraft.class_3218
import org.jetbrains.annotations.ApiStatus
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.ServerShip

@ApiStatus.Experimental
public interface ICopyableAttachment {
   public abstract fun onCopy(level: Supplier<class_3218>, shipOn: LoadedServerShip, shipsToBeSaved: List<ServerShip>, centerPositions: Map<Long, Vector3dc>) {
   }

   public open fun onAfterCopy(level: Supplier<class_3218>, shipOn: LoadedServerShip, shipsToBeSaved: List<ServerShip>, centerPositions: Map<Long, Vector3dc>) {
   }

   public abstract fun onPaste(
      level: Supplier<class_3218>,
      shipOn: LoadedServerShip,
      loadedShips: Map<Long, ServerShip>,
      centerPositions: Map<Long, Pair<Vector3dc, Vector3dc>>
   ) {
   }
}
