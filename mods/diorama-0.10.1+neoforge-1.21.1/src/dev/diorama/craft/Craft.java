package dev.diorama.craft;

import dev.diorama.MapStyle;
import dev.diorama.bake.BlockLooks;
import dev.diorama.bake.CraftBaker;
import dev.diorama.bake.CraftBlocks;
import dev.diorama.bake.CraftShadow;
import dev.diorama.render.gpu.MapTexture;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.Nullable;

final class Craft {
   final UUID id;
   @Nullable Body body;
   Body.@Nullable Blocks blocks;
   long version;
   long readAt;
   final double[] pose = new double[12];
   boolean posed;
   boolean announced;
   boolean parked;
   long movedAt;
   boolean settled;
   final double[] anchor = new double[3];
   long nearSince = -1L;
   final double[] framePose = new double[12];
   CraftBaker.@Nullable Image image;
   @Nullable MapTexture texture;
   @Nullable CompletableFuture<CraftBaker.@Nullable Image> pending;
   Craft.@Nullable Inputs imageFrom;
   double @Nullable [] triedLinear;
   double imageY;
   CraftShadow.@Nullable Image shadow;
   @Nullable MapTexture shadowTexture;
   volatile long shadowCost;
   volatile long imageCost;
   long imageAt;
   int shadowTextureEdge;
   @Nullable CompletableFuture<CraftShadow.@Nullable Image> shadowPending;
   Craft.@Nullable Inputs shadowFrom;
   long shadowAt;
   int @Nullable [] shadowArea;
   double @Nullable [] shadowAreaMiddle;
   long shadowAreaAt;
   @Nullable MapTexture cut;
   @Nullable Object cutFor;
   CraftPainter.@Nullable SunMask sunMask;
   @Nullable CompletableFuture<CraftPainter.@Nullable SunMask> sunPending;
   @Nullable Object sunFor;
   long sunAt;
   volatile long sunCost;
   long cutAt;
   int cutEdge;

   Craft(UUID id) {
      this.id = id;
   }

   double[] center(double[] pose) {
      return center(pose, this.blocks.blocks());
   }

   static double[] center(double[] p, CraftBlocks b) {
      double hx = b.sizeX / 2.0;
      double hy = b.sizeY / 2.0;
      double hz = b.sizeZ / 2.0;
      return new double[]{p[0] + p[3] * hx + p[6] * hy + p[9] * hz, p[1] + p[4] * hx + p[7] * hy + p[10] * hz, p[2] + p[5] * hx + p[8] * hy + p[11] * hz};
   }

   int[] box(double[] pose) {
      double[] box = this.blocks.blocks().box(pose);
      int[] blocks = new int[6];

      for (int i = 0; i < 6; i++) {
         blocks[i] = (int)Math.floor(box[i]);
      }

      return blocks;
   }

   static MapTexture put(@Nullable MapTexture old, boolean fits, String name, int edge, int[][] mips) {
      MapTexture texture = old;
      if (texture != null && !fits) {
         texture.close();
         texture = null;
      }

      if (texture == null) {
         texture = MapTexture.create(name, edge, 6);
      }

      for (int level = 0; level < 6; level++) {
         texture.write(level, 0, 0, edge >> level, mips[level]);
      }

      return texture;
   }

   void close() {
      for (MapTexture texture : new MapTexture[]{this.texture, this.shadowTexture, this.cut}) {
         if (texture != null) {
            texture.close();
         }
      }

      this.texture = null;
      this.shadowTexture = null;
      this.cut = null;
      this.image = null;
      this.shadow = null;
      this.cutFor = null;
   }

   record Inputs(CraftBlocks blocks, MapStyle style, BlockLooks looks, boolean fast) {
   }
}
