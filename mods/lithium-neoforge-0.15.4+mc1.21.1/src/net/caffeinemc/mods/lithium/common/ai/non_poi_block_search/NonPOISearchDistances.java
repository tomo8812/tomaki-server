package net.caffeinemc.mods.lithium.common.ai.non_poi_block_search;

import net.caffeinemc.mods.lithium.common.util.Distances;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class NonPOISearchDistances {
   public static class MoveToBlockGoalDistances {
      public static int getMinimumSortOrderOfChunk(BlockPos center, long chunkPos) {
         return getMinimumSortOrderOfChunk(center, ChunkPos.getX(chunkPos), ChunkPos.getZ(chunkPos));
      }

      public static int getMinimumSortOrderOfChunk(BlockPos center, int chunkX, int chunkZ) {
         long closest = Distances.getClosestPositionWithinChunk(center, chunkX, chunkZ);
         int dX = BlockPos.getX(closest) - center.getX();
         int dZ = BlockPos.getZ(closest) - center.getZ();
         return getVanillaSortOrderInt(getRing(dX, dZ), dX, dZ);
      }

      public static int getRing(int dX, int dZ) {
         return Math.max(Math.abs(dX), Math.abs(dZ));
      }

      public static int getVanillaSortOrderInt(int ring, int dX, int dZ) {
         return (ring << 16 | Math.abs(dX) << 9 | Math.abs(dZ) << 1) - ((dX > 0 ? 1 : 0) << 8 | (dZ > 0 ? 1 : 0));
      }
   }
}
