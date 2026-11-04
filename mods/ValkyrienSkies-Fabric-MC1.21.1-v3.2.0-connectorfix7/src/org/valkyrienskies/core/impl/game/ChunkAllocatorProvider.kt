package org.valkyrienskies.core.impl.game

public interface ChunkAllocatorProvider {
   public abstract fun forDimension(dimensionId: String): ChunkAllocator {
   }
}
