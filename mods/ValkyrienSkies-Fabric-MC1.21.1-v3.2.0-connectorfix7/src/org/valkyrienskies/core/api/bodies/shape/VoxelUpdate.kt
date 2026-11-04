package org.valkyrienskies.core.api.bodies.shape

public interface VoxelUpdate {
   public val chunkX: Int

   public val chunkY: Int

   public val chunkZ: Int

   public interface Builder {
      public abstract fun addBlock(x: Int, y: Int, z: Int, block: VoxelType) {
      }

      public abstract fun build(): VoxelUpdate {
      }
   }
}
