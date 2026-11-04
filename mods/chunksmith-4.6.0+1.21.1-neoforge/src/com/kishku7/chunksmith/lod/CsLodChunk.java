package com.kishku7.chunksmith.lod;

import java.util.List;

public final class CsLodChunk {
   public static final int BLOCKS_PER_SECTION = 4096;
   public static final int BIOMES_PER_SECTION = 64;
   public static final int LIGHT_BYTES = 2048;
   private final String dimension;
   private final int chunkX;
   private final int chunkZ;
   private final int minSectionY;
   private final List<String> blockPalette;
   private final List<String> biomePalette;
   private final List<CsLodChunk.Section> sections;

   public CsLodChunk(
      String dimension, int chunkX, int chunkZ, int minSectionY, List<String> blockPalette, List<String> biomePalette, List<CsLodChunk.Section> sections
   ) {
      this.dimension = dimension;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.minSectionY = minSectionY;
      this.blockPalette = blockPalette;
      this.biomePalette = biomePalette;
      this.sections = sections;
   }

   public String getDimension() {
      return this.dimension;
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public int getMinSectionY() {
      return this.minSectionY;
   }

   public List<String> getBlockPalette() {
      return this.blockPalette;
   }

   public List<String> getBiomePalette() {
      return this.biomePalette;
   }

   public List<CsLodChunk.Section> getSections() {
      return this.sections;
   }

   public static final class Section {
      private final int[] blocks;
      private final int uniformBlock;
      private final int[] biomes;
      private final int uniformBiome;
      private final byte[] skyLight;
      private final int uniformSky;
      private final byte[] blockLight;
      private final int uniformBlockLight;

      public Section(int[] blocks, int uniformBlock, int[] biomes, int uniformBiome, byte[] skyLight, int uniformSky, byte[] blockLight, int uniformBlockLight) {
         this.blocks = blocks;
         this.uniformBlock = uniformBlock;
         this.biomes = biomes;
         this.uniformBiome = uniformBiome;
         this.skyLight = skyLight;
         this.uniformSky = uniformSky;
         this.blockLight = blockLight;
         this.uniformBlockLight = uniformBlockLight;
      }

      public int[] getBlocks() {
         return this.blocks;
      }

      public int getUniformBlock() {
         return this.uniformBlock;
      }

      public int[] getBiomes() {
         return this.biomes;
      }

      public int getUniformBiome() {
         return this.uniformBiome;
      }

      public byte[] getSkyLight() {
         return this.skyLight;
      }

      public int getUniformSky() {
         return this.uniformSky;
      }

      public byte[] getBlockLight() {
         return this.blockLight;
      }

      public int getUniformBlockLight() {
         return this.uniformBlockLight;
      }

      public static int nibble(byte[] packed, int index) {
         int b = packed[index >> 1] & 255;
         return (index & 1) == 0 ? b & 15 : b >> 4;
      }
   }
}
