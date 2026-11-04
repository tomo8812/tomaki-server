package org.valkyrienskies.core.impl.game.ships.serialization

public interface ChainUpdater<T> {
   public abstract fun updateToLatest(value: Any): Any {
   }
}
