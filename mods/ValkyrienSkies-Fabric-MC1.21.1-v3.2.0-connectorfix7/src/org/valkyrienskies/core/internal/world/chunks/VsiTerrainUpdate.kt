package org.valkyrienskies.core.internal.world.chunks

public interface VsiTerrainUpdate {
   public val chunkX: Int

   public val chunkY: Int

   public val chunkZ: Int

   public interface Builder {
      public abstract fun addBlock(x: Int, y: Int, z: Int, block: VsiBlockType) {
      }

      public abstract fun build(): VsiTerrainUpdate {
      }
   }
}
