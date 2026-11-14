package dev.diorama.bake;

import java.util.List;
import org.jspecify.annotations.Nullable;

public record TileImage(
   TileKey key,
   int @Nullable [][] mips,
   byte @Nullable [] coverage,
   byte @Nullable [] info,
   List<TileImage.Patch> patches,
   int generation,
   int @Nullable [] spots,
   byte @Nullable [] blockLight
) {
   public static final int BLOCK = 32;

   public static TileImage full(TileKey key, int[][] mips, byte[] coverage) {
      return full(key, mips, coverage, null);
   }

   public static TileImage full(TileKey key, int[][] mips, byte[] coverage, byte @Nullable [] info) {
      return new TileImage(key, mips, coverage, info, List.of(), 0, null, null);
   }

   public static TileImage partial(TileKey key, List<TileImage.Patch> patches) {
      return new TileImage(key, null, null, null, patches, 0, null, null);
   }

   public TileImage withGeneration(int generation) {
      return new TileImage(this.key, this.mips, this.coverage, this.info, this.patches, generation, this.spots, this.blockLight);
   }

   public TileImage withSpots(int @Nullable [] spots) {
      return new TileImage(this.key, this.mips, this.coverage, this.info, this.patches, this.generation, spots, this.blockLight);
   }

   public TileImage withBlockLight(byte @Nullable [] blockLight) {
      return new TileImage(this.key, this.mips, this.coverage, this.info, this.patches, this.generation, this.spots, blockLight);
   }

   public boolean isFull() {
      return this.mips != null;
   }

   public record Patch(int blockX, int blockY, int[][] mips, byte[] coverage, byte @Nullable [] info, byte @Nullable [] blockLight) {
   }
}
