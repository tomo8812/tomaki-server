package dev.diorama.bake;

public record TileKey(int layer, int tileU, int tileV, int lod) {
   public static final int SURFACE = Integer.MIN_VALUE;
   public static final int MAX_LOD = 11;

   public TileKey(int layer, int tileU, int tileV) {
      this(layer, tileU, tileV, 0);
   }

   public boolean isCave() {
      return this.layer != Integer.MIN_VALUE;
   }

   public double units() {
      return 128.0 * (1 << this.lod);
   }

   public double minU() {
      return this.tileU * this.units();
   }

   public double minV() {
      return this.tileV * this.units();
   }

   public TileKey parent() {
      return new TileKey(this.layer, Math.floorDiv(this.tileU, 2), Math.floorDiv(this.tileV, 2), this.lod + 1);
   }
}
