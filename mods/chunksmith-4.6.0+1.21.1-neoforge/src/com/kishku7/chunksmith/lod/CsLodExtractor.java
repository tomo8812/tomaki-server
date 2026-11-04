package com.kishku7.chunksmith.lod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;

public final class CsLodExtractor {
   private CsLodExtractor() {
   }

   public static CsLodChunk extract(LevelChunk chunk) {
      LevelChunkSection[] sections = chunk.getSections();
      if (sections.length == 0) {
         return null;
      }

      String dimension = chunk.getLevel().dimension().location().toString();
      int chunkX = chunk.getPos().x;
      int chunkZ = chunk.getPos().z;
      int minSectionY = chunk.getMinSection();
      LevelLightEngine light = chunk.getLevel().getLightEngine();
      CsLodExtractor.Palette blocks = new CsLodExtractor.Palette();
      CsLodExtractor.Palette biomes = new CsLodExtractor.Palette();
      List<CsLodChunk.Section> out = new ArrayList<>(sections.length);

      for (int i = 0; i < sections.length; i++) {
         LevelChunkSection section = sections[i];
         SectionPos pos = SectionPos.of(chunkX, minSectionY + i, chunkZ);
         out.add(extractSection(section, pos, light, blocks, biomes));
      }

      return new CsLodChunk(dimension, chunkX, chunkZ, minSectionY, blocks.entries(), biomes.entries(), out);
   }

   private static CsLodChunk.Section extractSection(
      LevelChunkSection section, SectionPos pos, LevelLightEngine light, CsLodExtractor.Palette blocks, CsLodExtractor.Palette biomes
   ) {
      int uniformBlock = -1;
      int[] blockIndices = null;
      if (section.hasOnlyAir()) {
         uniformBlock = blocks.id(BlockStateParser.serialize(section.getBlockState(0, 0, 0)));
      } else {
         blockIndices = new int[4096];
         int first = -1;
         boolean uniform = true;
         int n = 0;

         for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
               for (int x = 0; x < 16; x++) {
                  BlockState state = section.getBlockState(x, y, z);
                  int id = blocks.id(BlockStateParser.serialize(state));
                  blockIndices[n++] = id;
                  if (first < 0) {
                     first = id;
                  } else if (id != first) {
                     uniform = false;
                  }
               }
            }
         }

         if (uniform) {
            uniformBlock = first;
            blockIndices = null;
         }
      }

      int uniformBiome = -1;
      int[] biomeIndices = new int[64];
      int firstBiome = -1;
      boolean uniformB = true;
      int b = 0;

      for (int y = 0; y < 4; y++) {
         for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 4; x++) {
               Holder<Biome> holder = section.getNoiseBiome(x, y, z);
               int id = biomes.id(holder.unwrapKey().map(key -> key.location().toString()).orElse("minecraft:plains"));
               biomeIndices[b++] = id;
               if (firstBiome < 0) {
                  firstBiome = id;
               } else if (id != firstBiome) {
                  uniformB = false;
               }
            }
         }
      }

      if (uniformB) {
         uniformBiome = firstBiome;
         biomeIndices = null;
      }

      CsLodExtractor.Light sky = extractLight(light, LightLayer.SKY, pos);
      CsLodExtractor.Light block = extractLight(light, LightLayer.BLOCK, pos);
      return new CsLodChunk.Section(blockIndices, uniformBlock, biomeIndices, uniformBiome, sky.packed, sky.uniform, block.packed, block.uniform);
   }

   private static CsLodExtractor.Light extractLight(LevelLightEngine engine, LightLayer layer, SectionPos pos) {
      DataLayer data = engine.getLayerListener(layer).getDataLayerData(pos);
      if (data != null && !data.isEmpty()) {
         byte[] packed = new byte[2048];
         int first = -1;
         boolean uniform = true;
         int n = 0;

         for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
               for (int x = 0; x < 16; x++) {
                  int value = Math.max(0, Math.min(15, data.get(x, y, z)));
                  if (first < 0) {
                     first = value;
                  } else if (value != first) {
                     uniform = false;
                  }

                  if ((n & 1) == 0) {
                     packed[n >> 1] = (byte)value;
                  } else {
                     packed[n >> 1] = (byte)(packed[n >> 1] | (byte)(value << 4));
                  }

                  n++;
               }
            }
         }

         return uniform ? new CsLodExtractor.Light(null, first) : new CsLodExtractor.Light(packed, -1);
      } else {
         int value = engine.getLayerListener(layer).getLightValue(pos.origin().offset(0, 0, 0));
         return new CsLodExtractor.Light(null, Math.max(0, Math.min(15, value)));
      }
   }

   static BlockPos origin(SectionPos pos) {
      return pos.origin();
   }

   private static final class Light {
      private final byte[] packed;
      private final int uniform;

      private Light(byte[] packed, int uniform) {
         this.packed = packed;
         this.uniform = uniform;
      }
   }

   private static final class Palette {
      private final Map<String, Integer> ids = new HashMap<>();
      private final List<String> order = new ArrayList<>();

      private int id(String value) {
         Integer existing = this.ids.get(value);
         if (existing != null) {
            return existing;
         }

         int id = this.order.size();
         this.ids.put(value, id);
         this.order.add(value);
         return id;
      }

      private List<String> entries() {
         return this.order;
      }
   }
}
