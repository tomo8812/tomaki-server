package dev.diorama.render.gpu;

public enum Sampling {
   TILE_SMOOTH,
   TILE_AREA,
   TILE_CRISP,
   CLAMP_LINEAR,
   CLAMP_CRISP,
   REPEAT_LINEAR;

   public boolean mipmapped() {
      return this == TILE_SMOOTH || this == TILE_AREA || this == TILE_CRISP;
   }

   public boolean crisp() {
      return this == TILE_CRISP || this == CLAMP_CRISP;
   }

   public boolean repeats() {
      return this == REPEAT_LINEAR;
   }

   // $VF: synthetic method
   private static Sampling[] $values() {
      return new Sampling[]{TILE_SMOOTH, TILE_AREA, TILE_CRISP, CLAMP_LINEAR, CLAMP_CRISP, REPEAT_LINEAR};
   }
}
