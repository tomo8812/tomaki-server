package com.kishku7.chunksmith.util;

import java.util.BitSet;

public final class SettleSweep {
   private final int minChunkX;
   private final int minChunkZ;
   private final int width;
   private final int height;
   private final int radius;
   private final BitSet generated;
   private final BitSet swept;
   private int cursor;
   private int stopsIssued;

   public SettleSweep(int minChunkX, int minChunkZ, int width, int height, int radius) {
      this.minChunkX = minChunkX;
      this.minChunkZ = minChunkZ;
      this.width = Math.max(0, width);
      this.height = Math.max(0, height);
      this.radius = Math.max(1, radius);
      this.generated = new BitSet(Math.max(1, this.width * this.height));
      this.swept = new BitSet(Math.max(1, this.width * this.height));
   }

   public void markGenerated(int chunkX, int chunkZ) {
      int index = this.index(chunkX, chunkZ);
      if (index >= 0) {
         this.generated.set(index);
      }
   }

   public int[] nextStop() {
      int stops = this.stopCount();

      for (int scanned = 0; scanned < stops; scanned++) {
         int slot = this.cursor;
         this.cursor = (this.cursor + 1) % Math.max(1, stops);
         int stopsX = this.stopsPerRow();
         int gx = slot % stopsX;
         int gz = slot / stopsX;
         int chunkX = this.minChunkX + Math.min(gx * this.radius, this.width - 1);
         int chunkZ = this.minChunkZ + Math.min(gz * this.radius, this.height - 1);
         int index = this.index(chunkX, chunkZ);
         if (index >= 0 && !this.swept.get(index) && this.windowGenerated(chunkX, chunkZ)) {
            this.swept.set(index);
            this.stopsIssued++;
            return new int[]{chunkX, chunkZ};
         }
      }

      return null;
   }

   public boolean windowGenerated(int chunkX, int chunkZ) {
      for (int x = chunkX - this.radius; x <= chunkX + this.radius; x++) {
         for (int z = chunkZ - this.radius; z <= chunkZ + this.radius; z++) {
            int index = this.index(x, z);
            if (index >= 0 && !this.generated.get(index)) {
               return false;
            }
         }
      }

      return true;
   }

   public int stopCount() {
      return this.stopsPerRow() * this.stopsPerColumn();
   }

   public int stopsIssued() {
      return this.stopsIssued;
   }

   public boolean isComplete() {
      return this.stopsIssued >= this.stopCount();
   }

   public int radius() {
      return this.radius;
   }

   private int stopsPerRow() {
      return this.width <= 0 ? 0 : (this.width + this.radius - 1) / this.radius;
   }

   private int stopsPerColumn() {
      return this.height <= 0 ? 0 : (this.height + this.radius - 1) / this.radius;
   }

   private int index(int chunkX, int chunkZ) {
      int x = chunkX - this.minChunkX;
      int z = chunkZ - this.minChunkZ;
      return x >= 0 && z >= 0 && x < this.width && z < this.height ? z * this.width + x : -1;
   }
}
