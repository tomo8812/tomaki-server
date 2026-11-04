package org.valkyrienskies.core.api.world

import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.util.GameTickOnly

@GameTickOnly
public interface ClientShipWorld : ShipWorld {
   public val allShips: QueryableShipData<ClientShip>

   public val loadedShips: QueryableShipData<ClientShip>

   @Deprecated(message = "redundant", replaceWith = @ReplaceWith(expression = "loadedShips.getShipDataIntersecting(aabb)", imports = []))
   public abstract override fun getShipObjectsIntersecting(aabb: AABBdc): List<ClientShip> {
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "allShips", imports = {}))
   public open val queryableShipData: QueryableShipData<ClientShip>
      public open get() {
         return this.allShips
      }

}
