package dev.diorama.render;

import dev.diorama.Claims;
import dev.diorama.bake.BlockLight;
import dev.diorama.bake.TileImage;
import dev.diorama.bake.TileSurface;
import dev.diorama.render.gpu.MapTexture;
import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.Nullable;

public final class MapTile {
   private @Nullable MapTexture texture;
   private byte @Nullable [] coverage;
   private byte @Nullable [] info;
   private @Nullable MapTexture overlay;
   private byte @Nullable [] blockLight;
   private @Nullable MapTexture light;
   private int overlayShown = -1;
   private boolean empty = true;
   long lastDrawn;
   private @Nullable MapTexture claims;
   Claims.@Nullable Snapshot claimsPainted;
   int pixelsRevision;
   long pixelsChanged;
   int claimsRevision;
   @Nullable TileSurface surface;
   @Nullable CompletableFuture<@Nullable TileSurface> surfacePending;
   int @Nullable [] surfacePendingArea;
   int surfaceChanges;
   long surfaceStarted;
   long surfaceAsked;
   int @Nullable [] surfaceWanted;
   long surfaceWantedSince;

   private void surfaceChanged(TileImage image) {
      if (image.isFull()) {
         this.surfaceChanges++;
      } else {
         int edge = 32;

         for (TileImage.Patch patch : image.patches()) {
            int x0 = patch.blockX() * edge;
            int y0 = patch.blockY() * edge;
            int[] area = this.surfacePendingArea;
            if (this.surface != null && this.surface.touches(x0, y0, x0 + edge, y0 + edge)
               || this.surfacePending != null && x0 < area[2] && area[0] < x0 + edge && y0 < area[3] && area[1] < y0 + edge) {
               this.surfaceChanges++;
               return;
            }
         }
      }
   }

   void upload(TileImage image) {
      this.surfaceChanged(image);
      if (image.isFull()) {
         this.coverage = image.coverage();
         this.info = image.info();
         this.overlayShown = -1;
         this.blockLight = image.blockLight() == null ? null : (byte[])image.blockLight().clone();
         this.empty = isEmpty(this.coverage);
         if (this.empty) {
            this.close();
         } else {
            this.ensureTexture(image);
            int size = 512;

            for (int level = 0; level < 6; level++) {
               this.texture.write(level, 0, 0, size >> level, image.mips()[level]);
            }

            this.uploadLight();
         }
      } else if (this.coverage != null) {
         int cellsPerBlock = 4;

         for (TileImage.Patch patch : image.patches()) {
            for (int y = 0; y < cellsPerBlock; y++) {
               System.arraycopy(
                  patch.coverage(), y * cellsPerBlock, this.coverage, (patch.blockY() * cellsPerBlock + y) * 64 + patch.blockX() * cellsPerBlock, cellsPerBlock
               );
            }
         }

         this.empty = isEmpty(this.coverage);
         if (this.empty) {
            this.close();
         } else {
            if (this.ensureTexture(image)) {
               int size = 512;

               for (int level = 0; level < 6; level++) {
                  this.texture.write(level, 0, 0, size >> level, new int[(size >> level) * (size >> level)]);
               }
            }

            for (TileImage.Patch patch : image.patches()) {
               for (int level = 0; level < 6; level++) {
                  int edge = 32 >> level;
                  this.texture.write(level, patch.blockX() * edge, patch.blockY() * edge, edge, patch.mips()[level]);
               }

               if (patch.info() != null) {
                  this.patchInfo(patch);
               }
            }

            this.patchLight(image);
         }
      }
   }

   private void patchLight(TileImage image) {
      boolean changed = false;
      int edge = 8;

      for (TileImage.Patch patch : image.patches()) {
         byte[] light = patch.blockLight();
         if (light != null) {
            if (this.blockLight == null) {
               this.blockLight = new byte[16384];
            }

            for (int y = 0; y < edge; y++) {
               System.arraycopy(light, y * edge, this.blockLight, (patch.blockY() * edge + y) * 128 + patch.blockX() * edge, edge);
            }

            changed = true;
         }
      }

      if (changed) {
         this.uploadLight();
      }
   }

   private void uploadLight() {
      if (!BlockLight.any(this.blockLight)) {
         if (this.light != null) {
            this.light.close();
            this.light = null;
         }
      } else {
         if (this.light == null) {
            this.light = MapTexture.create("diorama light", 128, 6);
         }

         byte[][] mips = BlockLight.mips(this.blockLight, 6);

         for (int level = 0; level < 6; level++) {
            byte[] values = mips[level];
            int[] pixels = new int[values.length];

            for (int i = 0; i < values.length; i++) {
               pixels[i] = 0xFF000000 | values[i] & 255;
            }

            this.light.write(level, 0, 0, 128 >> level, pixels);
         }
      }
   }

   public @Nullable MapTexture light() {
      return this.texture == null ? null : this.light;
   }

   private void patchInfo(TileImage.Patch patch) {
      int size = 512;
      int edge = 32;
      if (this.info == null) {
         this.info = new byte[size * size];
      }

      int x0 = patch.blockX() * edge;
      int y0 = patch.blockY() * edge;

      for (int y = 0; y < edge; y++) {
         System.arraycopy(patch.info(), y * edge, this.info, (y0 + y) * size + x0, edge);
      }

      if (this.overlay != null && this.overlayShown >= 0) {
         this.drawOverlay(x0, y0, edge);
      }
   }

   public @Nullable MapTexture overlay(int shown) {
      if (this.info != null && shown != 0 && this.texture != null) {
         if (this.overlay == null) {
            this.overlay = MapTexture.create("diorama overlay", 512, 1);
            this.overlayShown = -1;
         }

         if (this.overlayShown != shown) {
            this.overlayShown = shown;
            this.drawOverlay(0, 0, 512);
         }

         return this.overlay;
      } else {
         return null;
      }
   }

   private void drawOverlay(int x0, int y0, int edge) {
      int size = 512;
      int[] pixels = new int[edge * edge];

      for (int y = 0; y < edge; y++) {
         for (int x = 0; x < edge; x++) {
            pixels[y * edge + x] = overlayColor(this.info[(y0 + y) * size + x0 + x], this.overlayShown);
         }
      }

      this.overlay.write(0, x0, y0, edge, pixels);
   }

   static int overlayColor(int bits, int shown) {
      bits &= shown;
      if ((bits & 3) != 0) {
         return -1593835521;
      } else if ((bits & 16) != 0) {
         return -534238977;
      } else if ((bits & 8) != 0) {
         return -1340026625;
      } else {
         return (bits & 4) != 0 ? 1883304016 : 0;
      }
   }

   void paintClaims(int @Nullable [] pixels, Claims.Snapshot painted, int revision) {
      this.claimsPainted = painted;
      this.claimsRevision = revision;
      if (pixels == null) {
         if (this.claims != null) {
            this.claims.close();
            this.claims = null;
         }
      } else {
         if (this.claims == null) {
            this.claims = MapTexture.create("diorama claims", 512, 1);
         }

         this.claims.write(0, 0, 0, 512, pixels);
      }
   }

   public @Nullable MapTexture claims() {
      return this.texture == null ? null : this.claims;
   }

   private boolean ensureTexture(TileImage image) {
      if (this.texture != null) {
         return false;
      }

      this.texture = MapTexture.create("diorama tile " + image.key(), 512, 6);
      return true;
   }

   public @Nullable MapTexture texture() {
      return this.texture;
   }

   public boolean isEmpty() {
      return this.empty;
   }

   public float coverage(int cellX, int cellY) {
      byte[] c = this.coverage;
      return c == null ? 0.0F : (c[cellY * 64 + cellX] & 0xFF) / 255.0F;
   }

   public int blockLightAt(int lx, int ly) {
      return this.blockLight == null ? -1 : this.blockLight[ly * 128 + lx] & 0xFF;
   }

   public int overlayBits(int px, int py) {
      return this.info == null ? -1 : this.info[py * 512 + px] & 0xFF;
   }

   public boolean hasCoverage() {
      return this.coverage != null;
   }

   void close() {
      if (this.surfacePending != null) {
         this.surfacePending.cancel(false);
         this.surfacePending = null;
      }

      if (this.claims != null) {
         this.claims.close();
         this.claims = null;
      }

      this.claimsPainted = null;
      if (this.overlay != null) {
         this.overlay.close();
         this.overlay = null;
      }

      if (this.light != null) {
         this.light.close();
         this.light = null;
      }

      if (this.texture != null) {
         this.texture.close();
         this.texture = null;
      }
   }

   private static boolean isEmpty(byte[] coverage) {
      for (byte b : coverage) {
         if (b != 0) {
            return false;
         }
      }

      return true;
   }
}
