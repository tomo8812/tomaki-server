package org.codeberg.zenxarch.fastnoise.ocl;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.nio.ByteBuffer;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.codeberg.zenxarch.fastnoise.heightmap.HeightmapUtil;
import org.codeberg.zenxarch.fastnoise.noise.FastChunkSection;

public final class FastCopyBufferDataIntoChunks {
   private static final BlockState AIR = Blocks.AIR.defaultBlockState();
   private static final Types[] heightmaps = HeightmapUtil.calculateHeightmaps(ChunkStatus.NOISE);

   public static void copyData(
      StaticCache2D<ProtoChunk> chunks,
      BlockState[] clBlockStateMappings,
      int verticalSize,
      NoiseGeneratorSettings settings,
      int horizontalSize,
      ByteBuffer blockOutBufferData,
      ChunkPos startingPos,
      int batchSize
   ) {
      for (int chunkOffZ = 0; chunkOffZ < batchSize; chunkOffZ++) {
         for (int chunkOffX = 0; chunkOffX < batchSize; chunkOffX++) {
            ProtoChunk chunk = (ProtoChunk)chunks.get(startingPos.x + chunkOffX, startingPos.z + chunkOffZ);
            if (!chunk.getPersistedStatus().isOrAfter(ChunkStatus.NOISE)) {
               copyDataIntoChunk(chunkOffX, chunkOffZ, chunk, clBlockStateMappings, verticalSize, blockOutBufferData, horizontalSize);
               chunk.setPersistedStatus(ChunkStatus.NOISE);
            }
         }
      }
   }

   private static void copyDataIntoChunk(
      int chunkOffX, int chunkOffZ, ProtoChunk chunk, BlockState[] mappings, int verticalSize, ByteBuffer rawData, int horizontalSize
   ) {
      LevelChunkSection[] rawSections = chunk.getSections();
      FastChunkSection[] sections = new FastChunkSection[rawSections.length];

      for (int i = 0; i < sections.length; i++) {
         sections[i] = new FastChunkSection(rawSections[i]);
      }

      ShortList[] postProcessingLists = chunk.getPostProcessing();
      BlockState defaultState = mappings[2];

      for (int y = 0; y < verticalSize; y++) {
         int sy = y >> 4;
         FastChunkSection section = sections[sy];

         for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
               byte value = getData(rawData, chunkOffX, chunkOffZ, x, y, z, horizontalSize);
               if (putBlock(x, y & 15, z, value, section, defaultState, mappings)) {
                  ChunkAccess.getOrCreateOffsetList(postProcessingLists, sy).add((short)(x | (y & 15) << 4 | z << 8));
               }
            }
         }
      }

      finalizeChunks(sections, chunk, defaultState);
   }

   private static byte getData(ByteBuffer rawData, int chunkOffX, int chunkOffZ, int x, int y, int z, int horizontalSize) {
      int idx = y * horizontalSize * horizontalSize + (chunkOffZ * 16 + z) * horizontalSize + chunkOffX * 16 + x;
      return rawData.get(idx);
   }

   private static boolean putBlock(int x, int y, int z, byte value, FastChunkSection section, BlockState defaultState, BlockState[] mappings) {
      int blockIdx = value & 127;
      if (blockIdx == 1) {
         return false;
      } else if (blockIdx == 2) {
         section.setDefaultBlockState(x, y, z, defaultState);
         return false;
      } else {
         BlockState state = mappings[blockIdx];
         section.setBlockState(x, y, z, state);
         return (value & -128) != 0;
      }
   }

   private static void finalizeChunks(FastChunkSection[] fastSections, ChunkAccess chunk, BlockState defaultBlockState) {
      for (int i = 0; i < fastSections.length; i++) {
         if (fastSections[i] != null) {
            fastSections[i].recalculateCounts();
         }
      }

      for (int i = 0; i < heightmaps.length; i++) {
         HeightmapUtil.populateHeightmapPostNoise(chunk, heightmaps[i], defaultBlockState, AIR);
      }
   }
}
