package dev.diorama.bake;

import dev.diorama.MapStyle;
import org.jspecify.annotations.Nullable;

public record SurfaceArea(int tileU0, int tileV0, int columns, int rows, @Nullable TileSurface[] tiles) {
   private @Nullable TileSurface at(int pu, int pv) {
      int size = 512;
      int column = Math.floorDiv(pu, size) - this.tileU0;
      int row = Math.floorDiv(pv, size) - this.tileV0;
      return column >= 0 && row >= 0 && column < this.columns && row < this.rows ? this.tiles[row * this.columns + column] : null;
   }

   private static int index(TileSurface surface, int pu, int pv) {
      return surface.index(Math.floorMod(pu, 512), Math.floorMod(pv, 512));
   }

   public float near(int pu, int pv) {
      TileSurface surface = this.at(pu, pv);
      int i = surface == null ? -1 : index(surface, pu, pv);
      return i >= 0 && surface.near().length != 0 ? surface.near()[i] : Float.NaN;
   }

   public float y(int pu, int pv) {
      TileSurface surface = this.at(pu, pv);
      int i = surface == null ? -1 : index(surface, pu, pv);
      return i < 0 ? Float.NaN : surface.y()[i];
   }

   public float sun(int pu, int pv) {
      TileSurface surface = this.at(pu, pv);
      int i = surface == null ? -1 : index(surface, pu, pv);
      return i < 0 ? 0.0F : (surface.sun()[i] & 0xFF) / 255.0F;
   }

   public int shaded(int pu, int pv) {
      TileSurface surface = this.at(pu, pv);
      int i = surface == null ? -1 : index(surface, pu, pv);
      return i < 0 ? 0 : surface.albedo()[i];
   }

   public boolean top(int pu, int pv) {
      TileSurface surface = this.at(pu, pv);
      return surface.top()[index(surface, pu, pv)];
   }

   public int blockLight(int pu, int pv) {
      TileSurface surface = this.at(pu, pv);
      return surface.light()[index(surface, pu, pv)] & 15;
   }

   public int relit(int pu, int pv, float blockLight, int alpha, MapStyle style, float[] scratch) {
      TileSurface surface = this.at(pu, pv);
      int i = index(surface, pu, pv);
      surface.caveLight(i, blockLight, scratch);
      int albedo = surface.albedo()[i];
      float[] lin = TileBaker.SRGB_TO_LINEAR;
      float fog = TileSurface.fog(surface.light()[i]);
      int pixel = TileBaker.abgr(
         style,
         lin[albedo >> 16 & 0xFF] * scratch[0] + Lighting.CAVE_FOG[0] * fog,
         lin[albedo >> 8 & 0xFF] * scratch[1] + Lighting.CAVE_FOG[1] * fog,
         lin[albedo & 0xFF] * scratch[2] + Lighting.CAVE_FOG[2] * fog,
         alpha * (albedo >>> 24) / 255
      );
      float outline = surface.outline()[i];
      return outline > 0.0F ? TileBaker.darken(pixel, 1.0F - style.outline() * outline) : pixel;
   }

   public boolean same(SurfaceArea other) {
      if (this.tileU0 == other.tileU0 && this.tileV0 == other.tileV0 && this.columns == other.columns && this.rows == other.rows) {
         for (int i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i] != other.tiles[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
