package com.kishku7.chunksmith.lod;

import com.seibel.distanthorizons.api.DhApi.Delayed;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGeneratorReturnType;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.AbstractDhApiChunkWorldGenerator;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.objects.data.DhApiChunk;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CsLodDhGenerator extends AbstractDhApiChunkWorldGenerator {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private final IDhApiLevelWrapper level;
   private final CsLodRegionStore store;
   private final AtomicLong served = new AtomicLong();
   private final AtomicLong missed = new AtomicLong();
   private final Map<String, IDhApiBlockStateWrapper> blockWrappers = new ConcurrentHashMap<>();
   private final Map<String, IDhApiBiomeWrapper> biomeWrappers = new ConcurrentHashMap<>();

   public CsLodDhGenerator(IDhApiLevelWrapper level, Path storeRoot) {
      this.level = level;
      this.store = new CsLodRegionStore(storeRoot);
   }

   public EDhApiWorldGeneratorReturnType getReturnType() {
      return EDhApiWorldGeneratorReturnType.API_CHUNKS;
   }

   public void preGeneratorTaskStart() {
   }

   public Object[] generateChunk(int chunkX, int chunkZ, EDhApiDistantGeneratorMode mode) {
      throw new UnsupportedOperationException("ChunkSmith serves API_CHUNKS, not vanilla chunks");
   }

   public DhApiChunk generateApiChunk(int chunkX, int chunkZ, EDhApiDistantGeneratorMode mode) {
      CsLodChunk record;
      try {
         record = this.store.read(chunkX, chunkZ);
      } catch (IOException e) {
         this.missed.incrementAndGet();
         return this.emptyChunk(chunkX, chunkZ);
      }

      if (record == null) {
         this.missed.incrementAndGet();
         return this.emptyChunk(chunkX, chunkZ);
      }

      int bottomY = record.getMinSectionY() * 16;
      int topY = bottomY + record.getSections().size() * 16;
      DhApiChunk chunk = DhApiChunk.create(chunkX, chunkZ, bottomY, topY);

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            chunk.setDataPoints(x, z, this.column(record, x, z, bottomY));
         }
      }

      this.served.incrementAndGet();
      return chunk;
   }

   private List<DhApiTerrainDataPoint> column(CsLodChunk record, int x, int z, int bottomY) {
      List<CsLodChunk.Section> sections = record.getSections();
      int height = sections.size() * 16;
      String[] states = new String[height];
      String[] biomes = new String[height];
      int[] skyLight = new int[height];
      int[] blockLight = new int[height];

      for (int s = 0; s < sections.size(); s++) {
         CsLodChunk.Section section = sections.get(s);

         for (int y = 0; y < 16; y++) {
            int index = s * 16 + y;
            int voxel = y * 256 + z * 16 + x;
            int blockId = section.getUniformBlock() >= 0 ? section.getUniformBlock() : section.getBlocks()[voxel];
            states[index] = record.getBlockPalette().get(blockId);
            int biomeCell = (y >> 2) * 16 + (z >> 2) * 4 + (x >> 2);
            int biomeId = section.getUniformBiome() >= 0 ? section.getUniformBiome() : section.getBiomes()[biomeCell];
            biomes[index] = record.getBiomePalette().get(biomeId);
            skyLight[index] = section.getUniformSky() >= 0 ? section.getUniformSky() : CsLodChunk.Section.nibble(section.getSkyLight(), voxel);
            blockLight[index] = section.getUniformBlockLight() >= 0
               ? section.getUniformBlockLight()
               : CsLodChunk.Section.nibble(section.getBlockLight(), voxel);
         }
      }

      List<DhApiTerrainDataPoint> points = new ArrayList<>();
      int runStart = 0;

      for (int y = 1; y <= height; y++) {
         boolean end = y == height;
         boolean same = !end
            && states[y].equals(states[runStart])
            && biomes[y].equals(biomes[runStart])
            && skyOf(skyLight, y, height) == skyOf(skyLight, runStart, height)
            && blockOf(blockLight, y, height) == blockOf(blockLight, runStart, height);
         if (!same) {
            points.add(
               DhApiTerrainDataPoint.create(
                  (byte)0,
                  blockOf(blockLight, runStart, height),
                  skyOf(skyLight, runStart, height),
                  bottomY + runStart,
                  bottomY + y,
                  this.blockWrapper(states[runStart]),
                  this.biomeWrapper(biomes[runStart])
               )
            );
            runStart = y;
         }
      }

      return points;
   }

   private DhApiChunk emptyChunk(int chunkX, int chunkZ) {
      DhApiChunk chunk = DhApiChunk.create(chunkX, chunkZ, this.level.getMinHeight(), this.level.getMaxHeight());

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            chunk.setDataPoints(x, z, List.of());
         }
      }

      return chunk;
   }

   private static int skyOf(int[] skyLight, int y, int height) {
      return y + 1 < height ? skyLight[y + 1] : skyLight[height - 1];
   }

   private static int blockOf(int[] blockLight, int y, int height) {
      return y + 1 < height ? blockLight[y + 1] : blockLight[height - 1];
   }

   private IDhApiBlockStateWrapper blockWrapper(String state) {
      return this.blockWrappers.computeIfAbsent(state, key -> {
         try {
            return Delayed.wrapperFactory.getDefaultBlockStateWrapper(key, this.level);
         } catch (IOException e) {
            return Delayed.wrapperFactory.getAirBlockStateWrapper();
         }
      });
   }

   private IDhApiBiomeWrapper biomeWrapper(String biome) {
      return this.biomeWrappers.computeIfAbsent(biome, key -> {
         try {
            return Delayed.wrapperFactory.getBiomeWrapper(key, this.level);
         } catch (IOException e) {
            throw new IllegalStateException("ChunkSmith: DH rejected biome id " + key, e);
         }
      });
   }

   public long getServedCount() {
      return this.served.get();
   }

   public long getMissedCount() {
      return this.missed.get();
   }

   public void close() {
      try {
         this.store.close();
      } catch (IOException e) {
         LOGGER.warn("Chunksmith: failed to close the LOD store for DH: {}", e.toString());
      }
   }
}
