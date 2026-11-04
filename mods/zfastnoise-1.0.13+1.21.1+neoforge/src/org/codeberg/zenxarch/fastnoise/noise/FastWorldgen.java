package org.codeberg.zenxarch.fastnoise.noise;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.SingleValuePalette;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class FastWorldgen {
   public static final BlockState AIR = Blocks.AIR.defaultBlockState();

   public static boolean isEmpty(ChunkAccess chunk) {
      LevelChunkSection[] sections = chunk.getSections();

      for (int i = 0; i < sections.length; i++) {
         if (!(sections[i].states.data.palette() instanceof SingleValuePalette)) {
            return false;
         }
      }

      return true;
   }

   public static void populateNoise(
      NoiseChunk chunkNoiseSampler,
      BlockState defaultBlockState,
      ChunkAccess chunk,
      int minimumCellY,
      int cellHeight,
      FastChunkSection[] fastSections,
      int minimumY
   ) {
      ChunkPos chunkPos = chunk.getPos();
      int chunkStartX = chunkPos.getMinBlockX();
      int chunkStartZ = chunkPos.getMinBlockZ();
      Aquifer aquiferSampler = chunkNoiseSampler.aquifer();
      chunkNoiseSampler.initializeForFirstCellX();
      MutableBlockPos mutable = new MutableBlockPos();
      int horizontalCellBlockCount = chunkNoiseSampler.cellWidth();
      int verticalCellBlockCount = chunkNoiseSampler.cellHeight();
      int cellWidth = 16 / horizontalCellBlockCount;
      LevelChunkSection[] sections = chunk.getSections();

      for (int cellX = 0; cellX < cellWidth; cellX++) {
         chunkNoiseSampler.advanceCellX(cellX);

         for (int cellZ = 0; cellZ < cellWidth; cellZ++) {
            for (int cellY = cellHeight - 1; cellY >= 0; cellY--) {
               chunkNoiseSampler.selectCellYZ(cellY, cellZ);

               for (int verticalCellBlock = verticalCellBlockCount - 1; verticalCellBlock >= 0; verticalCellBlock--) {
                  int blockY = (minimumCellY + cellY) * verticalCellBlockCount + verticalCellBlock;
                  int blockYInSection = blockY & 15;
                  double verticalCellProgress = (double)verticalCellBlock / verticalCellBlockCount;
                  chunkNoiseSampler.updateForY(blockY, verticalCellProgress);
                  int sectionIdx = blockY - minimumY >> 4;

                  for (int cellBlockX = 0; cellBlockX < horizontalCellBlockCount; cellBlockX++) {
                     int blockX = chunkStartX + cellX * horizontalCellBlockCount + cellBlockX;
                     int blockXInSection = blockX & 15;
                     double cellXProgress = (double)cellBlockX / horizontalCellBlockCount;
                     chunkNoiseSampler.updateForX(blockX, cellXProgress);

                     for (int cellBlockZ = 0; cellBlockZ < horizontalCellBlockCount; cellBlockZ++) {
                        int blockZ = chunkStartZ + cellZ * horizontalCellBlockCount + cellBlockZ;
                        int blockZInSection = blockZ & 15;
                        double cellZProgress = (double)cellBlockZ / horizontalCellBlockCount;
                        chunkNoiseSampler.updateForZ(blockZ, cellZProgress);
                        BlockState state = chunkNoiseSampler.getInterpolatedState();
                        if (state != AIR) {
                           FastChunkSection fastSection = fastSections[sectionIdx] != null
                              ? fastSections[sectionIdx]
                              : (fastSections[sectionIdx] = new FastChunkSection(sections[sectionIdx]));
                           if (state == null) {
                              fastSection.setDefaultBlockState(blockXInSection, blockYInSection, blockZInSection, defaultBlockState);
                           } else {
                              fastSection.setBlockState(blockXInSection, blockYInSection, blockZInSection, state);
                              if (aquiferSampler.shouldScheduleFluidUpdate() && !state.getFluidState().isEmpty()) {
                                 mutable.set(blockX, blockY, blockZ);
                                 chunk.markPosForPostprocessing(mutable);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         chunkNoiseSampler.swapSlices();
      }

      chunkNoiseSampler.stopInterpolation();

      for (int i = 0; i < fastSections.length; i++) {
         if (fastSections[i] != null) {
            fastSections[i].recalculateCounts();
         }
      }

      Heightmap.primeHeightmaps(chunk, ObjectArraySet.of(new Types[]{Types.OCEAN_FLOOR_WG, Types.WORLD_SURFACE_WG}));
   }

   public static void populateBiomes(ChunkAccess chunk, BiomeResolver supplier, Sampler sampler) {
      ChunkPos chunkPos = chunk.getPos();
      LevelHeightAccessor world = chunk.getHeightAccessorForGeneration();
      int x = chunkPos.x * 4;
      int y = world.getMinBuildHeight() >> 2;
      int z = chunkPos.z * 4;
      int maxIdx = world.getHeight() >> 4;
      LevelChunkSection[] sections = chunk.getSections();
      Holder<Biome>[] biomes = new Holder[64];
      byte[] storage = new byte[64];

      for (int i = 0; i < maxIdx; i++) {
         LevelChunkSection section = sections[i];
         FastBiomeGen.populateBiomes(section, supplier, sampler, x, y, z, biomes, storage);
         y += 4;
      }
   }
}
