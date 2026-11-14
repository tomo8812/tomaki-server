package dev.diorama.bake;

import dev.diorama.data.ChunkSnapshot;
import java.util.Arrays;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

public final class BiomeTints {
   private static final ColorResolver[] RESOLVERS = new ColorResolver[]{
      Biome::getGrassColor, (biome, x, z) -> biome.getFoliageColor(), (biome, x, z) -> biome.getFoliageColor(), (biome, x, z) -> biome.getWaterColor()
   };
   private static final int WATER = 3;

   private BiomeTints() {
   }

   public static ColorResolver water() {
      return RESOLVERS[3];
   }

   static void setWater(ColorResolver resolver) {
      RESOLVERS[3] = resolver;
   }

   public static ChunkSnapshot.Tints of(ChunkSnapshot snapshot) {
      ChunkSnapshot.Tints tints = snapshot.tints();
      if (tints == null) {
         ChunkSnapshot chunk = snapshot.chunk();
         tints = blend(chunk);
         chunk.setTints(tints);
         if (chunk != snapshot) {
            tints = snapshot.tints();
         }
      }

      return tints;
   }

   public static ChunkSnapshot.Tints blend(ChunkSnapshot snapshot) {
      BiomeManager biomes = new BiomeManager(snapshot::knownNoiseBiome, snapshot.biomeZoomSeed);
      int[][] colors = new int[RESOLVERS.length][256];
      int radius = snapshot.blendRadius;
      int[] red = new int[RESOLVERS.length];
      int[] green = new int[RESOLVERS.length];
      int[] blue = new int[RESOLVERS.length];
      MutableBlockPos pos = new MutableBlockPos();
      int size = 16 + radius * 2;
      int[] heights = new int[size * size];
      Arrays.fill(heights, Integer.MIN_VALUE);
      int[][] sampled = new int[RESOLVERS.length][size * size];
      boolean[] known = new boolean[size * size];

      for (int z = 0; z < 16; z++) {
         for (int x = 0; x < 16; x++) {
            int bx = (snapshot.chunkX << 4) + x;
            int bz = (snapshot.chunkZ << 4) + z;
            int y = snapshot.surface(x, z);
            Arrays.fill(red, 0);
            Arrays.fill(green, 0);
            Arrays.fill(blue, 0);
            int count = 0;

            for (int dz = -radius; dz <= radius; dz++) {
               for (int dx = -radius; dx <= radius; dx++) {
                  int cell = (z + dz + radius) * size + x + dx + radius;
                  if (heights[cell] != y) {
                     heights[cell] = y;
                     Holder<Biome> biome = biomes.getBiome(pos.set(bx + dx, y, bz + dz));
                     known[cell] = biome != null;

                     for (int r = 0; biome != null && r < RESOLVERS.length; r++) {
                        sampled[r][cell] = RESOLVERS[r].getColor((Biome)biome.value(), bx + dx, bz + dz);
                     }
                  }

                  if (known[cell]) {
                     count++;

                     for (int r = 0; r < RESOLVERS.length; r++) {
                        int color = sampled[r][cell];
                        red[r] += ARGB32.red(color);
                        green[r] += ARGB32.green(color);
                        blue[r] += ARGB32.blue(color);
                     }
                  }
               }
            }

            if (count == 0) {
               Biome own = (Biome)snapshot.noiseBiome(bx >> 2, y >> 2, bz >> 2).value();
               count = 1;

               for (int r = 0; r < RESOLVERS.length; r++) {
                  int color = RESOLVERS[r].getColor(own, bx, bz);
                  red[r] = ARGB32.red(color);
                  green[r] = ARGB32.green(color);
                  blue[r] = ARGB32.blue(color);
               }
            }

            int i = z << 4 | x;

            for (int r = 0; r < RESOLVERS.length; r++) {
               colors[r][i] = ARGB32.color(red[r] / count, green[r] / count, blue[r] / count) & 16777215;
            }
         }
      }

      return new ChunkSnapshot.Tints(colors[0], colors[1], colors[2], colors[3]);
   }
}
