package org.millenaire.world.sitescore;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.millenaire.world.SiteValidator;
import org.millenaire.world.TerrainPreparer;

public record ChunkTerrainSummary(int chunkX, int chunkZ, int[] heights, int waterMask, int lavaMask) {
   public static final int SIDE = 4;
   public static final int STEP = 4;
   public static final int OFFSET = 2;

   public int height(int i, int j) {
      return this.heights[j * 4 + i];
   }

   public boolean isWater(int i, int j) {
      return (this.waterMask & 1 << j * 4 + i) != 0;
   }

   public boolean isLava(int i, int j) {
      return (this.lavaMask & 1 << j * 4 + i) != 0;
   }

   public static ChunkTerrainSummary filler(int chunkX, int chunkZ) {
      return new ChunkTerrainSummary(chunkX, chunkZ, new int[16], 0, 0);
   }

   public static ChunkTerrainSummary compute(ServerLevel level, int chunkX, int chunkZ) {
      SiteMeasures.seaLevel = SiteValidator.seaLevel(level);
      int[] heights = new int[16];
      int water = 0;
      int lava = 0;

      for (int j = 0; j < 4; j++) {
         for (int i = 0; i < 4; i++) {
            int x = (chunkX << 4) + 2 + 4 * i;
            int z = (chunkZ << 4) + 2 + 4 * j;
            int y = TerrainPreparer.getSurfaceOrWaterHeight(level, x, z);
            int idx = j * 4 + i;
            heights[idx] = y;
            BlockPos below = new BlockPos(x, y - 1, z);
            BlockPos at = new BlockPos(x, y, z);
            BlockState surface = y > level.getMinBuildHeight() ? level.getBlockState(below) : level.getBlockState(at);
            if (surface.is(Blocks.LAVA) || level.getBlockState(at).is(Blocks.LAVA)) {
               lava |= 1 << idx;
            } else if (!level.getFluidState(below).isEmpty() || !level.getFluidState(at).isEmpty() || SiteValidator.isIceCappedWater(level, x, y, z)) {
               water |= 1 << idx;
            }
         }
      }

      return new ChunkTerrainSummary(chunkX, chunkZ, heights, water, lava);
   }
}
