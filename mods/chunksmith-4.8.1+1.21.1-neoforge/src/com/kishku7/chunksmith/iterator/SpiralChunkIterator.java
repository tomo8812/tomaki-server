package com.kishku7.chunksmith.iterator;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.util.ChunkCoordinate;
import java.util.NoSuchElementException;

public class SpiralChunkIterator implements ChunkIterator {
   private static final int RIGHT = 0;
   private static final int DOWN = 1;
   private static final int LEFT = 2;
   private static final int UP = 3;
   private final int stopX;
   private final int stopZ;
   private final long total;
   private int x;
   private int z;
   private int span = 1;
   private int spanCount;
   private int spanProgress;
   private int direction;
   private boolean hasNext = true;

   public SpiralChunkIterator(Selection selection, long count) {
      this(selection);
      if (count > 0L) {
         int diameterFinished = (int)Math.floor(Math.sqrt(count));
         if (diameterFinished % 2 == 0) {
            diameterFinished--;
         }

         this.span = diameterFinished;
         this.spanCount = 1;
         this.direction = 1;
         int radiusFinished = diameterFinished / 2;
         this.x += radiusFinished + 1;
         this.z += radiusFinished;
         long perimeterCount = count - (long)diameterFinished * diameterFinished;
         int spanned = (int)Math.min(this.span, perimeterCount);
         this.z -= spanned;
         if (spanned == this.span) {
            this.span++;
            this.spanCount = 0;
            this.direction = 2;
         } else {
            this.spanProgress += spanned;
         }

         perimeterCount -= spanned;
         spanned = (int)Math.min(this.span, perimeterCount);
         this.x -= spanned;
         if (spanned == this.span) {
            this.spanCount = 1;
            this.direction = 3;
         } else {
            this.spanProgress += spanned;
         }

         perimeterCount -= spanned;
         spanned = (int)Math.min(this.span, perimeterCount);
         this.z += spanned;
         if (spanned == this.span) {
            this.span++;
            this.spanCount = 0;
            this.direction = 0;
         } else {
            this.spanProgress += spanned;
         }

         perimeterCount -= spanned;
         spanned = (int)Math.min(this.span, perimeterCount);
         this.x += spanned;
         if (spanned == this.span) {
            this.spanCount = 1;
            this.direction = 1;
         } else {
            this.spanProgress += spanned;
         }

         if (this.x > this.stopX) {
            this.hasNext = false;
         }
      }
   }

   public SpiralChunkIterator(Selection selection) {
      int radiusChunks = selection.radiusChunksX();
      this.x = selection.centerChunkX();
      this.z = selection.centerChunkZ();
      this.stopX = this.x + radiusChunks;
      this.stopZ = this.z + radiusChunks;
      long diameter = selection.diameterChunksX();
      this.total = diameter * diameter;
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
      if (this.x == this.stopX && this.z == this.stopZ) {
         this.hasNext = false;
      }

      if (this.spanCount == 2) {
         this.spanCount = 0;
         this.span++;
      }

      if (this.direction == 0) {
         this.x++;
      } else if (this.direction == 1) {
         this.z--;
      } else if (this.direction == 2) {
         this.x--;
      } else {
         if (this.direction != 3) {
            throw new IllegalStateException("Invalid direction");
         }

         this.z++;
      }

      this.spanProgress++;
      if (this.spanProgress == this.span) {
         this.spanProgress = 0;
         this.spanCount++;
         this.direction = this.direction == 3 ? 0 : ++this.direction;
      }

      return chunkCoord;
   }

   @Override
   public long total() {
      return this.total;
   }

   @Override
   public String name() {
      return "spiral";
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object next() {
      return this.next();
   }
}
