package org.codeberg.zenxarch.fastnoise.noise;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ZeroBitStorage;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.SingleValuePalette;
import net.minecraft.world.level.chunk.Palette.Factory;
import net.minecraft.world.level.chunk.PalettedContainer.Configuration;
import net.minecraft.world.level.chunk.PalettedContainer.Data;
import net.minecraft.world.level.chunk.PalettedContainer.Strategy;

public final class FastBiomeGen {
   private static final Factory ARRAY = Strategy.LINEAR_PALETTE_FACTORY;
   private static final Factory SINGULAR = Strategy.SINGLE_VALUE_PALETTE_FACTORY;
   static final Configuration<Holder<Biome>> SINGULAR_TYPE = new Configuration(SINGULAR, 0);
   private static final Configuration<Holder<Biome>> ARRAY_1_TYPE = new Configuration(ARRAY, 1);
   private static final Configuration<Holder<Biome>> ARRAY_2_TYPE = new Configuration(ARRAY, 2);
   private static final Configuration<Holder<Biome>> ARRAY_3_TYPE = new Configuration(ARRAY, 3);
   private static final Configuration<Holder<Biome>> ARRAY_4_TYPE = new Configuration(ARRAY, 4);
   private static final Configuration<Holder<Biome>> ARRAY_5_TYPE = new Configuration(ARRAY, 5);
   private static final Configuration<Holder<Biome>> ARRAY_6_TYPE = new Configuration(ARRAY, 6);
   private static final Configuration<Holder<Biome>>[] types = new Configuration[]{
      ARRAY_1_TYPE, ARRAY_2_TYPE, ARRAY_3_TYPE, ARRAY_4_TYPE, ARRAY_5_TYPE, ARRAY_6_TYPE
   };
   private static final int[] sizes = new int[]{-1, 1, 2, 4, 4, 6, 7};

   public static void populateBiomes(
      LevelChunkSection section, BiomeResolver biomeSupplier, Sampler sampler, int x, int y, int z, Holder<Biome>[] biomes, byte[] storage
   ) {
      int size = 0;

      for (int ix = 0; ix < 4; ix++) {
         for (int iy = 0; iy < 4; iy++) {
            for (int iz = 0; iz < 4; iz++) {
               Holder<Biome> biome = biomeSupplier.getNoiseBiome(x + ix, y + iy, z + iz, sampler);
               int bidx = -1;

               for (int i = 0; i < size; i++) {
                  if (biomes[i] == biome) {
                     bidx = i;
                     break;
                  }
               }

               if (bidx == -1) {
                  biomes[bidx = size++] = biome;
               }

               storage[(iy << 2 | iz) << 2 | ix] = (byte)bidx;
            }
         }
      }

      PalettedContainer<Holder<Biome>> container = (PalettedContainer<Holder<Biome>>)section.biomes;
      IdMap<Holder<Biome>> idList = container.registry;
      if (size == 1) {
         if (container.data.palette() instanceof SingleValuePalette<Holder<Biome>> palette) {
            palette.value = biomes[0];
         } else {
            container.data = new Data(SINGULAR_TYPE, new ZeroBitStorage(64), new SingleValuePalette(idList, container, List.of(biomes[0])));
         }
      } else {
         int bits = Mth.ceillog2(size);
         Holder<Biome>[] downSizedBiomes = new Holder[1 << bits];
         System.arraycopy(biomes, 0, downSizedBiomes, 0, size);
         container.data = new Data(types[bits - 1], toStorage(biomes, bits, storage), new LinearPalette(idList, downSizedBiomes, container, bits, size));
      }
   }

   private static BitStorage toStorage(Holder<Biome>[] palette, int bits, byte[] data) {
      long[] storage = new long[sizes[bits]];
      int idx = 0;

      for (int i = 0; i < bits; i++) {
         for (int j = 0; j + bits < 65; j += bits) {
            storage[i] |= (long)data[idx++] << j;
         }
      }

      for (int j = 0; idx < 64; j += bits) {
         storage[bits] |= (long)data[idx++] << j;
      }

      return new SimpleBitStorage(bits, 64, storage);
   }
}
