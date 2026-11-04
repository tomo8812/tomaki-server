package com.kishku7.chunksmith.iterator;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.util.ChunkCoordinate;
import java.util.NoSuchElementException;

public class Loop2ChunkIterator implements ChunkIterator {
   private final int x1;
   private final int x2;
   private final int z1;
   private final int z2;
   private final long diameterChunksZ;
   private final long total;
   private int x;
   private int z;
   private boolean hasNext = true;

   public Loop2ChunkIterator(Selection selection, long count) {
      this(selection);
      if (count > 0L) {
         this.x = this.x1 + (int)(count / this.diameterChunksZ);
         this.z = this.z1 + (int)(count % this.diameterChunksZ);
         if (this.x > this.x2) {
            this.hasNext = false;
         }
      }
   }

   public Loop2ChunkIterator(Selection selection) {
      int radiusChunksX = selection.radiusChunksX();
      int radiusChunksZ = selection.radiusChunksZ();
      int centerChunkX = selection.centerChunkX();
      int centerChunkZ = selection.centerChunkZ();
      this.x1 = centerChunkX - radiusChunksX;
      this.x2 = centerChunkX + radiusChunksX;
      this.z1 = centerChunkZ - radiusChunksZ;
      this.z2 = centerChunkZ + radiusChunksZ;
      this.x = this.x1;
      this.z = this.z1;
      int diameterChunksX = selection.diameterChunksX();
      this.diameterChunksZ = selection.diameterChunksZ();
      this.total = diameterChunksX * this.diameterChunksZ;
   }

   @Override
   public boolean hasNext() {
      return this.hasNext;
   }

   public ChunkCoordinate next() {
      if (!this.hasNext) {
         throw new NoSuchElementException();
      }

      ChunkCoordinate chunkCoord = new ChunkCoordinate(this.x, this.z);
      if (++this.z > this.z2) {
         this.z = this.z1;
         if (++this.x > this.x2) {
            this.hasNext = false;
         }
      }

      return chunkCoord;
   }

   @Override
   public long total() {
      return this.total;
   }

   @Override
   public String name() {
      return "loop";
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object next() {
      return this.next();
   }
}
