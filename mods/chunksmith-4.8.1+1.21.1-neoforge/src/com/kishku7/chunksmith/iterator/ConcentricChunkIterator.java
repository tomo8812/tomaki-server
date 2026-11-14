package com.kishku7.chunksmith.iterator;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.util.ChunkCoordinate;
import java.util.NoSuchElementException;

public class ConcentricChunkIterator implements ChunkIterator {
   private final int xCenter;
   private final int zCenter;
   private final int radiusChunks;
   private final long total;
   private int x;
   private int z;
   private int annulus;
   private int span;
   private int down;
   private int left;
   private int up;
   private int right;
   private boolean hasNext = true;

   public ConcentricChunkIterator(Selection selection, long count) {
      this(selection);
      if (count > 0L) {
         int diameterFinished = (int)Math.floor(Math.sqrt(count));
         if (diameterFinished % 2 == 0) {
            diameterFinished--;
         }

         this.annulus = diameterFinished / 2 + 1;
         this.x = this.x + this.annulus;
         this.z = this.z + this.annulus;
         this.span = 2 * this.annulus;
         long perimeterCount = count - (long)diameterFinished * diameterFinished + 1L;
         this.down = this.down + (int)Math.min(this.span, perimeterCount);
         perimeterCount -= Math.min(this.span, perimeterCount);
         this.left = this.left + (int)Math.min(this.span, perimeterCount);
         perimeterCount -= Math.min(this.span, perimeterCount);
         this.up = this.up + (int)Math.min(this.span, perimeterCount);
         perimeterCount -= Math.min(this.span, perimeterCount);
         this.right = this.right + (int)Math.min(this.span, perimeterCount);
         this.x = this.x + (this.right - this.left);
         this.z = this.z + (this.up - this.down);
         if (this.annulus > this.radiusChunks) {
            this.hasNext = false;
         }
      }
   }

   public ConcentricChunkIterator(Selection selection) {
      this.radiusChunks = selection.radiusChunksX();
      this.x = selection.centerChunkX();
      this.z = selection.centerChunkZ();
      this.xCenter = this.x;
      this.zCenter = this.z;
      long diameterChunks = selection.diameterChunksX();
      this.total = diameterChunks * diameterChunks;
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
      if (this.x == this.xCenter + this.annulus && this.z == this.zCenter + this.annulus) {
         this.annulus++;
         this.x++;
         this.z++;
         if (this.annulus > this.radiusChunks) {
            this.hasNext = false;
         }

         this.span = 2 * this.annulus;
         this.down = this.left = this.up = this.right = 0;
      }

      if (this.down < this.span) {
         this.z--;
         this.down++;
      } else if (this.left < this.span) {
         this.x--;
         this.left++;
      } else if (this.up < this.span) {
         this.z++;
         this.up++;
      } else if (this.right < this.span) {
         this.x++;
         this.right++;
      }

      return chunkCoord;
   }

   @Override
   public long total() {
      return this.total;
   }

   @Override
   public String name() {
      return "concentric";
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object next() {
      return this.next();
   }
}
