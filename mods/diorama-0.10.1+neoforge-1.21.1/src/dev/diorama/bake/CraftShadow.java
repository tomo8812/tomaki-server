package dev.diorama.bake;

import dev.diorama.MapStyle;
import dev.diorama.Projection;
import org.jspecify.annotations.Nullable;

public final class CraftShadow {
   private static final int SILHOUETTE_TEXELS = 8;
   private static final int BIG_SILHOUETTE_TEXELS = 4;
   private static final int MAX_REACH = 192;
   private static final double STALE = 0.125;
   private static final int SAMPLES = 2;

   private CraftShadow() {
   }

   public static double[] sun(MapStyle style) {
      double elevation = Math.toRadians(style.sunElevation());
      return new double[]{Math.cos(elevation) * -0.878, Math.sin(elevation), Math.cos(elevation) * 0.479};
   }

   public static int[] reach(CraftBlocks blocks, double[] pose, int groundY, MapStyle style) {
      double[] box = blocks.box(pose);
      double elevation = Math.toRadians(style.sunElevation());
      double run = Math.min(192.0, Math.max(0.0, box[4] - groundY)) / Math.tan(elevation);
      double dx = 0.878 * run;
      double dz = -0.479 * run;
      return new int[]{
         (int)Math.floor(Math.min(box[0], box[0] + dx)) - 1,
         (int)Math.floor(Math.min(box[2], box[2] + dz)) - 1,
         (int)Math.ceil(Math.max(box[3], box[3] + dx)) + 1,
         (int)Math.ceil(Math.max(box[5], box[5] + dz)) + 1
      };
   }

   public static boolean stale(CraftShadow.Silhouette silhouette, double[] linear, CraftBlocks blocks) {
      double[] l = silhouette.linear;

      for (int c = 1; c < 8; c++) {
         double x = (c & 1) * blocks.sizeX;
         double y = (c >> 1 & 1) * blocks.sizeY;
         double z = (c >> 2 & 1) * blocks.sizeZ;
         double dx = (l[0] - linear[0]) * x + (l[3] - linear[3]) * y + (l[6] - linear[6]) * z;
         double dy = (l[1] - linear[1]) * x + (l[4] - linear[4]) * y + (l[7] - linear[7]) * z;
         double dz = (l[2] - linear[2]) * x + (l[5] - linear[5]) * y + (l[8] - linear[8]) * z;
         if (dx * dx + dy * dy + dz * dz > 0.015625) {
            return true;
         }
      }

      return false;
   }

   public static CraftShadow.@Nullable Silhouette silhouette(CraftBlocks blocks, double[] linear, BlockLooks looks, MapStyle style) {
      double[] inv = GridRay.invert(linear);
      if (inv == null) {
         return null;
      }

      double[] s = sun(style);
      double[] e1 = normalize(new double[]{-s[2], 0.0, s[0]});
      double[] e2 = new double[]{e1[1] * s[2] - e1[2] * s[1], e1[2] * s[0] - e1[0] * s[2], e1[0] * s[1] - e1[1] * s[0]};
      double a0 = Double.MAX_VALUE;
      double a1 = -Double.MAX_VALUE;
      double b0 = Double.MAX_VALUE;
      double b1 = -Double.MAX_VALUE;

      for (int c = 0; c < 8; c++) {
         double x = (c & 1) * blocks.sizeX;
         double y = (c >> 1 & 1) * blocks.sizeY;
         double z = (c >> 2 & 1) * blocks.sizeZ;
         double wx = linear[0] * x + linear[3] * y + linear[6] * z;
         double wy = linear[1] * x + linear[4] * y + linear[7] * z;
         double wz = linear[2] * x + linear[5] * y + linear[8] * z;
         double a = wx * e1[0] + wy * e1[1] + wz * e1[2];
         double b = wx * e2[0] + wy * e2[1] + wz * e2[2];
         a0 = Math.min(a0, a);
         a1 = Math.max(a1, a);
         b0 = Math.min(b0, b);
         b1 = Math.max(b1, b);
      }

      int texels = 8;
      int width = (int)Math.ceil((a1 - a0) * texels) + 1;
      int height = (int)Math.ceil((b1 - b0) * texels) + 1;
      if ((long)width * height > 4194304L) {
         texels = 4;
         width = (int)Math.ceil((a1 - a0) * texels) + 1;
         height = (int)Math.ceil((b1 - b0) * texels) + 1;
         if ((long)width * height > 4194304L) {
            return null;
         }
      }

      float[] reach = new float[width * height];
      CraftShadow.Walker walker = new CraftShadow.Walker(blocks, looks, inv, s);

      for (int j = 0; j < height; j++) {
         for (int i = 0; i < width; i++) {
            double a = a0 + (i + 0.5) / texels;
            double b = b0 + (j + 0.5) / texels;
            reach[j * width + i] = walker.reach(a * e1[0] + b * e2[0], a * e1[1] + b * e2[1], a * e1[2] + b * e2[2]);
         }
      }

      return new CraftShadow.Silhouette((double[])linear.clone(), s, e1, e2, a0, b0, texels, width, height, reach);
   }

   public static CraftShadow.Image cast(CraftShadow.@Nullable Silhouette silhouette, double[] pose, int pu0, int pv0, int width, int height, SurfaceArea ground) {
      int step = 32;
      int edge = (Math.max(width, height) + step - 1) / step * step;
      CraftShadow.Image none = new CraftShadow.Image(edge, width, height, pu0, pv0, null, (double[])pose.clone(), silhouette, ground, Projection.sin());
      if (silhouette != null && edge <= 2048) {
         double sin = Projection.sin();
         double cos = Projection.cos();
         int ppu = 4;
         int[] pixels = new int[edge * edge];
         boolean any = false;
         double[] s = silhouette.s;
         double[] e1 = silhouette.e1;
         double[] e2 = silhouette.e2;

         for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
               int pu = pu0 + px;
               int pv = pv0 + py;
               float y = ground.y(pu, pv);
               float sun = Float.isNaN(y) ? 0.0F : ground.sun(pu, pv);
               if (!(sun <= 0.0F)) {
                  int dark = 0;

                  for (int sy = 0; sy < 2; sy++) {
                     for (int sx = 0; sx < 2; sx++) {
                        double u = (pu + (sx + 0.5) / 2.0) / ppu;
                        double v = (pv + (sy + 0.5) / 2.0) / ppu;
                        double rx = u - pose[0];
                        double ry = y - pose[1];
                        double rz = (v + y * cos) / sin - pose[2];
                        float reach = silhouette.at(rx * e1[0] + ry * e1[1] + rz * e1[2], rx * e2[0] + ry * e2[1] + rz * e2[2]);
                        if (reach > rx * s[0] + ry * s[1] + rz * s[2] + 0.05) {
                           dark++;
                        }
                     }
                  }

                  if (dark > 0) {
                     int coverage = Math.round(255.0F * dark / 4.0F);
                     int shaded = ground.shaded(pu, pv);
                     if (shaded != 0) {
                        pixels[py * edge + px] = coverage << 24 | shaded & 16777215;
                     } else {
                        float alpha = (float)(1.0 - Math.pow(1.0F - sun, 0.45454545454545453)) * dark / 4.0F;
                        pixels[py * edge + px] = Math.round(alpha * 255.0F) << 24;
                     }

                     any = true;
                  }
               }
            }
         }

         return any
            ? new CraftShadow.Image(edge, width, height, pu0, pv0, Mipmaps.build(pixels, edge), none.pose(), silhouette, ground, none.pitchSin())
            : none;
      } else {
         return none;
      }
   }

   private static double[] normalize(double[] v) {
      double length = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
      return new double[]{v[0] / length, v[1] / length, v[2] / length};
   }

   public record Image(
      int edge,
      int width,
      int height,
      int pu0,
      int pv0,
      int @Nullable [][] mips,
      double[] pose,
      CraftShadow.@Nullable Silhouette silhouette,
      SurfaceArea ground,
      double pitchSin
   ) {
   }

   public record Silhouette(double[] linear, double[] s, double[] e1, double[] e2, double a0, double b0, int texels, int width, int height, float[] reach) {
      float at(double a, double b) {
         int i = (int)Math.floor((a - this.a0) * this.texels);
         int j = (int)Math.floor((b - this.b0) * this.texels);
         return i >= 0 && j >= 0 && i < this.width && j < this.height ? this.reach[j * this.width + i] : Float.NaN;
      }
   }

   private static final class Walker {
      private final CraftBlocks blocks;
      private final BlockLooks looks;
      private final double[] inv;
      private final double rx;
      private final double ry;
      private final double rz;
      private final GridRay ray;

      Walker(CraftBlocks blocks, BlockLooks looks, double[] inv, double[] s) {
         this.blocks = blocks;
         this.looks = looks;
         this.inv = inv;
         this.rx = inv[0] * s[0] + inv[1] * s[1] + inv[2] * s[2];
         this.ry = inv[3] * s[0] + inv[4] * s[1] + inv[5] * s[2];
         this.rz = inv[6] * s[0] + inv[7] * s[1] + inv[8] * s[2];
         this.ray = new GridRay(blocks);
      }

      float reach(double x, double y, double z) {
         double[] m = this.inv;
         GridRay ray = this.ray;
         if (!ray.start(
            m[0] * x + m[1] * y + m[2] * z, m[3] * x + m[4] * y + m[5] * z, m[6] * x + m[7] * y + m[8] * z, this.rx, this.ry, this.rz, -Double.MAX_VALUE
         )) {
            return Float.NaN;
         }

         CraftBlocks b = this.blocks;
         float reach = Float.NaN;

         do {
            int id = b.look(this.looks, CraftBlocks.index(b.sizeX, b.sizeZ, ray.cell[0], ray.cell[1], ray.cell[2]));
            if (id != 0 && id < this.looks.kind.length && this.looks.kind[id] != 0 && ray.enter < ray.exit()) {
               reach = (float)ray.exit();
            }
         } while (ray.next());

         return reach;
      }
   }
}
