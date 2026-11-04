package org.embeddedt.modernfix.world.gen;

import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeManager.NoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ChunkBiomeLookup implements Function<BlockPos, Holder<Biome>> {
   private Holder<Biome>[] biomes = new Holder[0];
   private double[] biasX = new double[0];
   private double[] biasY = new double[0];
   private double[] biasZ = new double[0];
   private boolean[] uniform = new boolean[0];
   private int qMinX;
   private int qMinY;
   private int qMinZ;
   private int sizeX;
   private int sizeY;
   private int sizeZ;
   private BiomeManager fallbackManager;

   public void prepare(NoiseBiomeSource source, long biomeZoomSeed, ChunkAccess chunk, BiomeManager fallback) {
      int chunkMinX = chunk.getPos().getMinBlockX();
      int chunkMinZ = chunk.getPos().getMinBlockZ();
      int minBuildHeight = chunk.getMinBuildHeight();
      int maxBuildHeight = minBuildHeight + chunk.getHeight();
      int biomeOffset = 2;
      int minBlockX = chunkMinX - biomeOffset;
      int maxBlockX = chunkMinX + 15 - biomeOffset;
      int minBlockZ = chunkMinZ - biomeOffset;
      int maxBlockZ = chunkMinZ + 15 - biomeOffset;
      int minBlockY = minBuildHeight - biomeOffset;
      int maxBlockY = maxBuildHeight - 1 - biomeOffset;
      this.qMinX = QuartPos.fromBlock(minBlockX);
      int qMaxX = QuartPos.fromBlock(maxBlockX) + 1;
      this.qMinZ = QuartPos.fromBlock(minBlockZ);
      int qMaxZ = QuartPos.fromBlock(maxBlockZ) + 1;
      this.qMinY = QuartPos.fromBlock(minBlockY);
      int qMaxY = QuartPos.fromBlock(maxBlockY) + 1;
      this.sizeX = qMaxX - this.qMinX + 1;
      this.sizeY = qMaxY - this.qMinY + 1;
      this.sizeZ = qMaxZ - this.qMinZ + 1;
      int totalCells = this.sizeX * this.sizeY * this.sizeZ;
      if (this.biomes.length < totalCells) {
         this.biomes = new Holder[totalCells];
         this.biasX = new double[totalCells];
         this.biasY = new double[totalCells];
         this.biasZ = new double[totalCells];
         this.uniform = new boolean[totalCells];
      }

      boolean isSingleBiome = !this.fetchBiomes(source);
      if (isSingleBiome) {
         Arrays.fill(this.uniform, 0, totalCells, true);
      } else {
         this.computeUniformity();
         this.computeBiases(biomeZoomSeed);
      }

      this.fallbackManager = fallback;
   }

   public void dispose() {
      if (this.fallbackManager != null) {
         Arrays.fill(this.biomes, null);
         this.fallbackManager = null;
      }
   }

   private boolean fetchBiomes(NoiseBiomeSource source) {
      Holder<Biome>[] biomes = this.biomes;
      Holder<Biome> firstSeen = null;
      boolean seenMultiple = false;

      for (int rx = 0; rx < this.sizeX; rx++) {
         int wx = this.qMinX + rx;

         for (int rz = 0; rz < this.sizeZ; rz++) {
            int wz = this.qMinZ + rz;

            for (int ry = 0; ry < this.sizeY; ry++) {
               int wy = this.qMinY + ry;
               Holder<Biome> biome = source.getNoiseBiome(wx, wy, wz);
               biomes[this.index(rx, ry, rz)] = biome;
               if (biome != firstSeen) {
                  if (firstSeen == null) {
                     firstSeen = biome;
                  } else {
                     seenMultiple = true;
                  }
               }
            }
         }
      }

      return seenMultiple;
   }

   private void computeUniformity() {
      boolean[] uniform = this.uniform;
      int sizeX = this.sizeX;
      int sizeY = this.sizeY;
      int sizeZ = this.sizeZ;

      for (int rx = 0; rx < sizeX - 1; rx++) {
         for (int rz = 0; rz < sizeZ - 1; rz++) {
            for (int ry = 0; ry < sizeY - 1; ry++) {
               uniform[this.index(rx, ry, rz)] = this.isUniform(rx, ry, rz);
            }
         }
      }
   }

   private void computeBiases(long biomeZoomSeed) {
      int sizeX = this.sizeX;
      int sizeY = this.sizeY;
      int sizeZ = this.sizeZ;
      int qMinX = this.qMinX;
      int qMinY = this.qMinY;
      int qMinZ = this.qMinZ;

      for (int rx = 0; rx < sizeX; rx++) {
         int wx = qMinX + rx;

         for (int rz = 0; rz < sizeZ; rz++) {
            int wz = qMinZ + rz;

            for (int ry = 0; ry < sizeY; ry++) {
               this.computeBias(this.index(rx, ry, rz), biomeZoomSeed, wx, qMinY + ry, wz);
            }
         }
      }
   }

   private void computeBias(int idx, long seed, int x, int y, int z) {
      long s = LinearCongruentialGenerator.next(seed, x);
      s = LinearCongruentialGenerator.next(s, y);
      s = LinearCongruentialGenerator.next(s, z);
      s = LinearCongruentialGenerator.next(s, x);
      s = LinearCongruentialGenerator.next(s, y);
      s = LinearCongruentialGenerator.next(s, z);
      this.biasX[idx] = getFiddle(s);
      s = LinearCongruentialGenerator.next(s, seed);
      this.biasY[idx] = getFiddle(s);
      s = LinearCongruentialGenerator.next(s, seed);
      this.biasZ[idx] = getFiddle(s);
   }

   private static double getFiddle(long seed) {
      double d = Math.floorMod(seed >> 24, 1024) / 1024.0;
      return (d - 0.5) * 0.9;
   }

   private boolean isUniform(int rx, int ry, int rz) {
      Holder<Biome>[] biomes = this.biomes;
      Holder<Biome> ref = biomes[this.index(rx, ry, rz)];

      for (int dx = 0; dx <= 1; dx++) {
         for (int dy = 0; dy <= 1; dy++) {
            for (int dz = 0; dz <= 1; dz++) {
               if (biomes[this.index(rx + dx, ry + dy, rz + dz)] != ref) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int index(int rx, int ry, int rz) {
      return (rx * this.sizeY + ry) * this.sizeZ + rz;
   }

   public Holder<Biome> apply(BlockPos pos) {
      return this.getBiome(pos);
   }

   public Holder<Biome> getBiome(BlockPos pos) {
      int i = pos.getX() - 2;
      int j = pos.getY() - 2;
      int k = pos.getZ() - 2;
      int rx = QuartPos.fromBlock(i) - this.qMinX;
      int ry = QuartPos.fromBlock(j) - this.qMinY;
      int rz = QuartPos.fromBlock(k) - this.qMinZ;
      if (rx >= 0 && rx < this.sizeX - 1 && ry >= 0 && ry < this.sizeY - 1 && rz >= 0 && rz < this.sizeZ - 1) {
         int baseIdx = this.index(rx, ry, rz);
         return this.uniform[baseIdx] ? this.biomes[baseIdx] : this.getBiomeWithVoronoi(i, j, k, rx, ry, rz);
      } else {
         return this.fallbackManager.getBiome(pos);
      }
   }

   private Holder<Biome> getBiomeWithVoronoi(int i, int j, int k, int rx, int ry, int rz) {
      double[] biasX = this.biasX;
      double[] biasY = this.biasY;
      double[] biasZ = this.biasZ;
      double d0 = QuartPos.quartLocal(i) / 4.0;
      double d1 = QuartPos.quartLocal(j) / 4.0;
      double d2 = QuartPos.quartLocal(k) / 4.0;
      int closestIdx = 0;
      double closestDist = Double.POSITIVE_INFINITY;

      for (int c = 0; c < 8; c++) {
         boolean fx = (c & 4) == 0;
         boolean fy = (c & 2) == 0;
         boolean fz = (c & 1) == 0;
         int idx = this.index(rx + (fx ? 0 : 1), ry + (fy ? 0 : 1), rz + (fz ? 0 : 1));
         double dx = (fx ? d0 : d0 - 1.0) + biasX[idx];
         double dy = (fy ? d1 : d1 - 1.0) + biasY[idx];
         double dz = (fz ? d2 : d2 - 1.0) + biasZ[idx];
         double dist = Mth.square(dx) + Mth.square(dy) + Mth.square(dz);
         if (dist < closestDist) {
            closestDist = dist;
            closestIdx = idx;
         }
      }

      return this.biomes[closestIdx];
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object apply(Object param1) {
      return this.apply((BlockPos)var1);
   }
}
