package com.kishku7.chunksmith.iterator;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.util.ChunkCoordinate;
import com.kishku7.chunksmith.util.Hilbert;
import java.util.NoSuchElementException;

public class RegionChunkIterator implements ChunkIterator {
   private final int centerRegionX;
   private final int centerRegionZ;
   private final int radiusRegions;
   private final int minChunkX;
   private final int minChunkZ;
   private final int maxChunkX;
   private final int maxChunkZ;
   private final long totalChunks;
   private RegionChunkIterator.RegionChunkProgress currentRegionProgress;
   private int regionX;
   private int regionZ;
   private int annulusRegions;
   private int spanRegions;
   private int downRegions;
   private int leftRegions;
   private int upRegions;
   private int rightRegions;
   private boolean hasNextRegion = true;
   private boolean hasNext = true;

   public RegionChunkIterator(Selection selection, long count) {
      this(selection);
      if (count > 0L) {
         this.currentRegionProgress = null;
         this.regionX = this.centerRegionX;
         this.regionZ = this.centerRegionZ;
         this.annulusRegions = 0;
         this.spanRegions = 0;
         this.downRegions = this.leftRegions = this.upRegions = this.rightRegions = 0;
         this.hasNextRegion = true;
         this.hasNext = true;
         long countRemainingChunks = count;
         long estimatedRegionCount = count / 1024L;
         int estimatedDiameterRegions = (int)Math.floor(Math.sqrt(estimatedRegionCount));
         if (estimatedDiameterRegions % 2 == 0) {
            estimatedDiameterRegions--;
         }

         if (estimatedDiameterRegions > 2) {
            int skipDiameterRegions = estimatedDiameterRegions - 2;
            this.annulusRegions = skipDiameterRegions / 2 + 1;
            this.regionX = this.regionX + this.annulusRegions;
            this.regionZ = this.regionZ + (this.annulusRegions - 1);
            this.spanRegions = 2 * this.annulusRegions;
            this.downRegions++;
            long skipChunks = (long)skipDiameterRegions * skipDiameterRegions * 1024L;
            countRemainingChunks -= skipChunks;
         }

         this.currentRegionProgress = this.nextRegionChunkProgress(countRemainingChunks);

         for (long var12 = countRemainingChunks - Math.min(countRemainingChunks, this.currentRegionProgress == null ? 0L : this.currentRegionProgress.total());
            this.currentRegionProgress != null && !this.currentRegionProgress.hasNext();
            var12 -= Math.min(var12, this.currentRegionProgress == null ? 0L : this.currentRegionProgress.total())
         ) {
            this.currentRegionProgress = this.nextRegionChunkProgress(var12);
         }

         if (this.currentRegionProgress == null) {
            this.hasNext = false;
         }
      }
   }

   public RegionChunkIterator(Selection selection) {
      this.centerRegionX = selection.centerRegionX();
      this.centerRegionZ = selection.centerRegionZ();
      this.radiusRegions = selection.radiusRegionsX();
      int centerChunkX = selection.centerChunkX();
      int centerChunkZ = selection.centerChunkZ();
      int radiusChunks = selection.radiusChunksX();
      this.minChunkX = centerChunkX - radiusChunks;
      this.minChunkZ = centerChunkZ - radiusChunks;
      this.maxChunkX = centerChunkX + radiusChunks;
      this.maxChunkZ = centerChunkZ + radiusChunks;
      this.regionX = this.centerRegionX;
      this.regionZ = this.centerRegionZ;
      long diameterChunks = selection.diameterChunksX();
      this.totalChunks = diameterChunks * diameterChunks;
      this.currentRegionProgress = this.nextRegionChunkProgress();
   }

   @Override
   public boolean hasNext() {
      return this.hasNext;
   }

   public ChunkCoordinate next() {
      if (!this.hasNext) {
         throw new NoSuchElementException();
      }

      ChunkCoordinate chunkCoord = this.currentRegionProgress.next();

      while (this.currentRegionProgress != null && !this.currentRegionProgress.hasNext()) {
         this.currentRegionProgress = this.nextRegionChunkProgress();
      }

      if (this.currentRegionProgress == null) {
         this.hasNext = false;
      }

      return chunkCoord;
   }

   private RegionChunkIterator.RegionChunkProgress nextRegionChunkProgress() {
      return this.nextRegionChunkProgress(0L);
   }

   private RegionChunkIterator.RegionChunkProgress nextRegionChunkProgress(long count) {
      if (!this.hasNextRegion) {
         return null;
      }

      RegionChunkIterator.RegionChunkProgress regionChunkProgress = new RegionChunkIterator.RegionChunkProgress(this.regionX, this.regionZ, count);
      if (this.regionX == this.centerRegionX + this.annulusRegions && this.regionZ == this.centerRegionZ + this.annulusRegions) {
         this.annulusRegions++;
         this.regionX++;
         this.regionZ++;
         if (this.annulusRegions > this.radiusRegions) {
            this.hasNextRegion = false;
         }

         this.spanRegions = 2 * this.annulusRegions;
         this.downRegions = this.leftRegions = this.upRegions = this.rightRegions = 0;
      }

      if (this.downRegions < this.spanRegions) {
         this.regionZ--;
         this.downRegions++;
      } else if (this.leftRegions < this.spanRegions) {
         this.regionX--;
         this.leftRegions++;
      } else if (this.upRegions < this.spanRegions) {
         this.regionZ++;
         this.upRegions++;
      } else if (this.rightRegions < this.spanRegions) {
         this.regionX++;
         this.rightRegions++;
      }

      return regionChunkProgress;
   }

   @Override
   public long total() {
      return this.totalChunks;
   }

   @Override
   public String name() {
      return "region";
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object next() {
      return this.next();
   }

   public final class RegionChunkProgress implements ChunkIterator {
      private final int minX;
      private final int minZ;
      private final int sizeZ;
      private final int total;
      private final boolean full;
      private final String name;
      private int current;
      private int offsetX;
      private int offsetZ;
      private boolean hasNext = true;

      public RegionChunkProgress(int x, int z, long count) {
         this(x, z);
         if (count > 0L) {
            this.current = (int)Math.min(count, 1024L);
            if (!this.full && this.sizeZ > 0) {
               this.offsetX = this.current / this.sizeZ;
               this.offsetZ = this.current % this.sizeZ;
            }

            if (this.current >= this.total) {
               this.hasNext = false;
            }
         }
      }

      public RegionChunkProgress(int x, int z) {
         int lowEdgeX = x << 5;
         int lowEdgeZ = z << 5;
         int highEdgeX = lowEdgeX + 31;
         int highEdgeZ = lowEdgeZ + 31;
         this.minX = Math.max(lowEdgeX, RegionChunkIterator.this.minChunkX);
         this.minZ = Math.max(lowEdgeZ, RegionChunkIterator.this.minChunkZ);
         int maxX = Math.min(highEdgeX, RegionChunkIterator.this.maxChunkX);
         int maxZ = Math.min(highEdgeZ, RegionChunkIterator.this.maxChunkZ);
         int sizeX = maxX - this.minX + 1;
         if (this.minX <= highEdgeX && this.minZ <= highEdgeZ && maxX >= lowEdgeX && maxZ >= lowEdgeZ) {
            this.sizeZ = maxZ - this.minZ + 1;
            this.total = sizeX * this.sizeZ;
            this.full = this.total == 1024;
         } else {
            this.sizeZ = 0;
            this.total = 0;
            this.full = false;
            this.hasNext = false;
         }

         this.name = "region_chunk_progress_%d_%d".formatted(x, z);
      }

      @Override
      public boolean hasNext() {
         return this.hasNext;
      }

      public ChunkCoordinate next() {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         }

         ChunkCoordinate chunkCoord;
         if (this.full) {
            ChunkCoordinate offset = Hilbert.regionDistanceToChunkCoordinateOffset(this.current);
            chunkCoord = new ChunkCoordinate(this.minX + offset.x(), this.minZ + offset.z());
         } else {
            chunkCoord = new ChunkCoordinate(this.minX + this.offsetX, this.minZ + this.offsetZ);
            if (++this.offsetZ >= this.sizeZ) {
               this.offsetZ = 0;
               this.offsetX++;
            }
         }

         this.current++;
         if (this.current >= this.total) {
            this.hasNext = false;
         }

         return chunkCoord;
      }

      @Override
      public long total() {
         return this.total;
      }

      @Override
      public String name() {
         return this.name;
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object next() {
         return this.next();
      }
   }
}
