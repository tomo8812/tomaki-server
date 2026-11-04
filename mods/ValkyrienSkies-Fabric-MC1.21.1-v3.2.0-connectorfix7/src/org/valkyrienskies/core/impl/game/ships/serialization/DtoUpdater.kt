package org.valkyrienskies.core.impl.game.ships.serialization

public interface DtoUpdater<OLD, NEW> {
   public abstract fun update(data: Any): Any {
   }
}
