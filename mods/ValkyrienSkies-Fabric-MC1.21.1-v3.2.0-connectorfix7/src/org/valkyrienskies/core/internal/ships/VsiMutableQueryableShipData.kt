package org.valkyrienskies.core.internal.ships

import kotlin.jvm.internal.markers.KMutableIterable
import org.valkyrienskies.core.api.ships.Ship

public interface VsiMutableQueryableShipData<ShipType extends Ship> : VsiQueryableShipData<ShipType>, java.lang.Iterable<ShipType>, KMutableIterable {
   public abstract fun add(ship: Any) {
   }

   public abstract fun remove(ship: Any) {
   }

   public abstract fun remove(id: Long): Any {
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "add(ship)", imports = []))
   public open fun addShipData(ship: Any) {
      this.add((ShipType)ship)
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "remove(ship)", imports = []))
   public open fun removeShipData(ship: Any) {
      this.remove((ShipType)ship)
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "remove(id)", imports = []))
   public open fun removeShipData(id: Long): Any {
      return this.remove(id)
   }
}
