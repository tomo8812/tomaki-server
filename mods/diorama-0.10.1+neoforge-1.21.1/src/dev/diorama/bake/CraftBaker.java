package dev.diorama.bake;

import dev.diorama.MapStyle;
import dev.diorama.Projection;
import org.jspecify.annotations.Nullable;

public final class CraftBaker {
   public static final int MAX_PIXELS = 2048;
   private static final int GRASS = 9551193;
   private static final int FOLIAGE = 7842607;
   private static final int DRY_FOLIAGE = 10847821;
   private static final int WATER = 4159204;
   private final CraftBlocks blocks;
   private final BlockLooks looks;
   private final MapStyle style;
   private final double[] l;
   private final double[] inv;
   private final double sin;
   private final double cos;
   private final Lighting lighting;
   private final float[] light = new float[4];
   private final double originY;
   private float outR;
   private float outG;
   private float outB;
   private float outA;
   private float outDepth;
   private float albedoR;
   private float albedoG;
   private float albedoB;
   private final GridRay ray;
   private final GridRay sunRay;
   private final double[] o = new double[3];
   private final double[] r = new double[3];
   private final float[] corners = new float[4];
   private float shadedR;
   private float shadedG;
   private float shadedB;
   private float sunlessR;
   private float sunlessG;
   private float sunlessB;
   private float outSunlessR;
   private float outSunlessG;
   private float outSunlessB;

   private CraftBaker(CraftBlocks blocks, double[] linear, double[] inv, double originY, BlockLooks looks, MapStyle style) {
      this.blocks = blocks;
      this.looks = looks;
      this.style = style;
      this.l = linear;
      this.inv = inv;
      this.ray = new GridRay(blocks);
      this.sunRay = new GridRay(blocks);
      this.sin = Projection.sin();
      this.cos = Projection.cos();
      this.lighting = new Lighting(style);
      this.originY = originY;
   }

   public static CraftBaker.@Nullable Image bake(CraftBlocks blocks, double[] linear, double originY, BlockLooks looks, MapStyle style, boolean fast) {
      double[] inv = GridRay.invert(linear);
      return inv == null ? null : new CraftBaker(blocks, (double[])linear.clone(), inv, originY, looks, style).image(fast);
   }

   public static double warp(double[] from, double[] to, int sizeX, int sizeY, int sizeZ, double[] out) {
      double sin = Projection.sin();
      double cos = Projection.cos();
      double uu = 0.0;
      double uv = 0.0;
      double vv = 0.0;
      double xu = 0.0;
      double xv = 0.0;
      double yu = 0.0;
      double yv = 0.0;
      double[][] corners = new double[8][4];

      for (int c = 0; c < 8; c++) {
         double x = ((c & 1) - 0.5) * sizeX;
         double y = ((c >> 1 & 1) - 0.5) * sizeY;
         double z = ((c >> 2 & 1) - 0.5) * sizeZ;
         double u = from[0] * x + from[3] * y + from[6] * z;
         double v = (from[2] * x + from[5] * y + from[8] * z) * sin - (from[1] * x + from[4] * y + from[7] * z) * cos;
         double tu = to[0] * x + to[3] * y + to[6] * z;
         double tv = (to[2] * x + to[5] * y + to[8] * z) * sin - (to[1] * x + to[4] * y + to[7] * z) * cos;
         corners[c] = new double[]{u, v, tu, tv};
         uu += u * u;
         uv += u * v;
         vv += v * v;
         xu += tu * u;
         xv += tu * v;
         yu += tv * u;
         yv += tv * v;
      }

      double det = uu * vv - uv * uv;
      if (Math.abs(det) < 1.0E-6) {
         out[0] = 1.0;
         out[1] = 0.0;
         out[2] = 0.0;
         out[3] = 1.0;
         return Double.MAX_VALUE;
      }

      out[0] = (xu * vv - xv * uv) / det;
      out[1] = (xv * uu - xu * uv) / det;
      out[2] = (yu * vv - yv * uv) / det;
      out[3] = (yv * uu - yu * uv) / det;
      double most = 0.0;

      for (double[] c : corners) {
         most = Math.max(most, Math.max(Math.abs(out[0] * c[0] + out[1] * c[1] - c[2]), Math.abs(out[2] * c[0] + out[3] * c[1] - c[3])));
      }

      return most;
   }

   private CraftBaker.@Nullable Image image(boolean fast) {
      CraftBlocks b = this.blocks;
      double minU = Double.MAX_VALUE;
      double maxU = -Double.MAX_VALUE;
      double minV = Double.MAX_VALUE;
      double maxV = -Double.MAX_VALUE;

      for (int c = 0; c < 8; c++) {
         double x = (c & 1) * b.sizeX;
         double y = (c >> 1 & 1) * b.sizeY;
         double z = (c >> 2 & 1) * b.sizeZ;
         double wx = this.l[0] * x + this.l[3] * y + this.l[6] * z;
         double wy = this.l[1] * x + this.l[4] * y + this.l[7] * z;
         double wz = this.l[2] * x + this.l[5] * y + this.l[8] * z;
         double v = wz * this.sin - wy * this.cos;
         minU = Math.min(minU, wx);
         maxU = Math.max(maxU, wx);
         minV = Math.min(minV, v);
         maxV = Math.max(maxV, v);
      }

      double hx = b.sizeX / 2.0;
      double hy = b.sizeY / 2.0;
      double hz = b.sizeZ / 2.0;
      double middleU = this.l[0] * hx + this.l[3] * hy + this.l[6] * hz;
      double middleY = this.l[1] * hx + this.l[4] * hy + this.l[7] * hz;
      double middleZ = this.l[2] * hx + this.l[5] * hy + this.l[8] * hz;
      double middleV = middleZ * this.sin - middleY * this.cos;
      int ppu = 4;
      double offsetU = middleU + (Math.floor((minU - middleU) * ppu) - 1.0) / ppu;
      double offsetV = middleV + (Math.floor((minV - middleV) * ppu) - 1.0) / ppu;
      int width = (int)Math.ceil((maxU - offsetU) * ppu) + 1;
      int height = (int)Math.ceil((maxV - offsetV) * ppu) + 1;
      if (width <= 2048 && height <= 2048) {
         int step = 32;
         int edge = (Math.max(width, height) + step - 1) / step * step;
         int[] pixels = new int[edge * edge];
         float[] depth = new float[width * height];
         int[] sunless = new int[width * height];
         double[][] samples = fast ? TileBaker.FAST_SAMPLES : TileBaker.SAMPLES;
         double unitsPerPixel = 1.0 / ppu;

         for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
               float r = 0.0F;
               float g = 0.0F;
               float bl = 0.0F;
               float a = 0.0F;
               float d = 0.0F;
               float nr = 0.0F;
               float ng = 0.0F;
               float nb = 0.0F;
               int opaque = 0;

               for (int s = 0; s < samples.length; s++) {
                  double u = offsetU + (px + samples[s][0]) * unitsPerPixel;
                  double v = offsetV + (py + samples[s][1]) * unitsPerPixel;
                  this.trace(u, v, s);
                  r += this.outR;
                  g += this.outG;
                  bl += this.outB;
                  a += this.outA;
                  nr += this.outSunlessR;
                  ng += this.outSunlessG;
                  nb += this.outSunlessB;
                  if (!Float.isNaN(this.outDepth)) {
                     d += this.outDepth;
                     opaque++;
                  }
               }

               depth[py * width + px] = opaque > 0 ? d / opaque : Float.NaN;
               if (a > 0.0F) {
                  int alpha = Math.min(255, Math.round(a / samples.length * 255.0F));
                  pixels[py * edge + px] = TileBaker.abgr(this.style, r / a, g / a, bl / a, alpha);
                  sunless[py * width + px] = TileBaker.abgr(this.style, nr / a, ng / a, nb / a, alpha);
               }
            }
         }

         this.outline(pixels, sunless, depth, width, height, edge);
         float middleDepth = (float)(-(this.sin * middleY + this.cos * middleZ));

         for (int i = 0; i < depth.length; i++) {
            depth[i] -= middleDepth;
         }

         return new CraftBaker.Image(edge, width, height, offsetU - middleU, offsetV - middleV, Mipmaps.build(pixels, edge), depth, this.l, this.sin, sunless);
      } else {
         return null;
      }
   }

   private void outline(int[] pixels, int[] sunless, float[] depth, int width, int height, int edge) {
      float outline = this.style.outline();
      if (!(outline <= 0.0F)) {
         float keep = 1.0F - outline;
         boolean[] behind = new boolean[width * height];

         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
               int i = y * width + x;
               float here = depth[i];
               behind[i] = !Float.isNaN(here)
                  && (
                     x > 0 && TileBaker.nearer(depth[i - 1], here)
                        || x + 1 < width && TileBaker.nearer(depth[i + 1], here)
                        || y > 0 && TileBaker.nearer(depth[i - width], here)
                        || y + 1 < height && TileBaker.nearer(depth[i + width], here)
                  );
            }
         }

         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
               if (behind[y * width + x]) {
                  pixels[y * edge + x] = TileBaker.darken(pixels[y * edge + x], keep);
                  sunless[y * width + x] = TileBaker.darken(sunless[y * width + x], keep);
               }
            }
         }
      }
   }

   private void trace(double u, double v, int sample) {
      this.outR = 0.0F;
      this.outG = 0.0F;
      this.outB = 0.0F;
      this.outA = 0.0F;
      this.outDepth = Float.NaN;
      this.outSunlessR = 0.0F;
      this.outSunlessG = 0.0F;
      this.outSunlessB = 0.0F;
      double px = u;
      double py = -v * this.cos;
      double pz = v * this.sin;
      double dx = 0.0;
      double dy = -this.sin;
      double dz = -this.cos;
      double[] m = this.inv;
      double ox = m[0] * px + m[1] * py + m[2] * pz;
      double oy = m[3] * px + m[4] * py + m[5] * pz;
      double oz = m[6] * px + m[7] * py + m[8] * pz;
      double rx = m[0] * dx + m[1] * dy + m[2] * dz;
      double ry = m[3] * dx + m[4] * dy + m[5] * dz;
      double rz = m[6] * dx + m[7] * dy + m[8] * dz;
      CraftBlocks b = this.blocks;
      GridRay ray = this.ray;
      if (ray.start(ox, oy, oz, rx, ry, rz, -Double.MAX_VALUE)) {
         double[] o = this.o;
         double[] r = this.r;
         o[0] = ox;
         o[1] = oy;
         o[2] = oz;
         r[0] = rx;
         r[1] = ry;
         r[2] = rz;
         int[] cell = ray.cell;
         float transR = 1.0F;
         float transG = 1.0F;
         float transB = 1.0F;
         float left = 1.0F;

         do {
            int id = b.look(this.looks, CraftBlocks.index(b.sizeX, b.sizeZ, cell[0], cell[1], cell[2]));
            double tEnter = ray.enter;
            double tExit = ray.exit();
            int enteredAxis = ray.axis;
            if (id != 0 && id < this.looks.kind.length && this.looks.kind[id] != 0) {
               byte kind = this.looks.kind[id];
               double tHit = tEnter;
               int hitAxis = enteredAxis;
               int shape = kind == 1 ? this.looks.shape[id] : 0;
               boolean hit = true;
               if (shape != 0) {
                  hit = false;
                  float[] boxes = this.looks.shapes[shape];
                  double best = Double.MAX_VALUE;

                  for (int i = 0; i < boxes.length; i += 6) {
                     double enter = tEnter;
                     double exit = tExit;
                     int enterAxis = enteredAxis;

                     for (int a = 0; a < 3 && enter < exit; a++) {
                        double lo = cell[a] + boxes[i + a];
                        double hi = cell[a] + boxes[i + 3 + a];
                        if (Math.abs(r[a]) < 1.0E-12) {
                           if (o[a] < lo || o[a] > hi) {
                              enter = exit;
                           }
                        } else {
                           double ta = (lo - o[a]) / r[a];
                           double tb = (hi - o[a]) / r[a];
                           double near = Math.min(ta, tb);
                           if (near > enter) {
                              enter = near;
                              enterAxis = a;
                           }

                           exit = Math.min(exit, Math.max(ta, tb));
                        }
                     }

                     if (enter < exit && enter < best) {
                        best = enter;
                        hitAxis = enterAxis;
                     }
                  }

                  if (best != Double.MAX_VALUE) {
                     hit = true;
                     tHit = best;
                  }
               }

               if (hit) {
                  double hx = o[0] + r[0] * tHit;
                  double hy = o[1] + r[1] * tHit;
                  double hz = o[2] + r[2] * tHit;
                  int sign = r[hitAxis] > 0.0 ? -1 : 1;
                  this.shade(id, cell, hitAxis, sign, hx, hy, hz, sample);
                  if (kind == 4) {
                     transR *= 0.5F + 0.5F * this.albedoR;
                     transG *= 0.5F + 0.5F * this.albedoG;
                     transB *= 0.5F + 0.5F * this.albedoB;
                  } else {
                     if (kind != 5) {
                        this.outR = this.outR + this.shadedR * transR * left;
                        this.outG = this.outG + this.shadedG * transG * left;
                        this.outB = this.outB + this.shadedB * transB * left;
                        this.outSunlessR = this.outSunlessR + this.sunlessR * transR * left;
                        this.outSunlessG = this.outSunlessG + this.sunlessG * transG * left;
                        this.outSunlessB = this.outSunlessB + this.sunlessB * transB * left;
                        this.outA += left;
                        if (Float.isNaN(this.outDepth)) {
                           this.outDepth = (float)tHit;
                        }

                        return;
                     }

                     float w = 0.5F * left;
                     this.outR = this.outR + this.shadedR * transR * w;
                     this.outG = this.outG + this.shadedG * transG * w;
                     this.outB = this.outB + this.shadedB * transB * w;
                     this.outSunlessR = this.outSunlessR + this.sunlessR * transR * w;
                     this.outSunlessG = this.outSunlessG + this.sunlessG * transG * w;
                     this.outSunlessB = this.outSunlessB + this.sunlessB * transB * w;
                     this.outA += w;
                     left -= w;
                     if (Float.isNaN(this.outDepth)) {
                        this.outDepth = (float)tHit;
                     }
                  }
               }
            }
         } while (ray.next());
      }
   }

   private boolean solid(int x, int y, int z) {
      CraftBlocks b = this.blocks;
      if (x >= 0 && y >= 0 && z >= 0 && x < b.sizeX && y < b.sizeY && z < b.sizeZ) {
         int id = b.look(this.looks, CraftBlocks.index(b.sizeX, b.sizeZ, x, y, z));
         return id != 0 && id < this.looks.kind.length && this.looks.kind[id] == 1;
      } else {
         return false;
      }
   }

   private void shade(int id, int[] cell, int axis, int sign, double hx, double hy, double hz, int sample) {
      boolean top = axis == 1;
      this.albedo(id, top);
      float variation = this.style.blockVariation();
      if (variation > 0.0F) {
         float nudge = 1.0F + variation * TileBaker.blockNoise(cell[0], cell[1], cell[2]);
         this.albedoR *= nudge;
         this.albedoG *= nudge;
         this.albedoB *= nudge;
      }

      double nx = this.inv[axis * 3] * sign;
      double ny = this.inv[axis * 3 + 1] * sign;
      double nz = this.inv[axis * 3 + 2] * sign;
      double length = Math.sqrt(nx * nx + ny * ny + nz * nz);
      nx /= length;
      ny /= length;
      nz /= length;
      float ao = this.ambientOcclusion(cell, axis, sign, hx, hy, hz);
      double[] j = TileBaker.SUN_JITTER[sample];
      double sx = this.lighting.sunX + j[0];
      double sy = this.lighting.sunY + j[1];
      double sz = this.lighting.sunZ + j[2];
      double lambert = Math.min(1.5, Math.max(0.0, nx * sx + ny * sy + nz * sz) / sy);
      double e = 1.0E-4 * sign;
      boolean shadowed = lambert > 0.0 && this.inShadow(hx + (axis == 0 ? e : 0.0), hy + (axis == 1 ? e : 0.0), hz + (axis == 2 ? e : 0.0), sx, sy, sz);
      float sky = (float)Math.max(0.0, Math.min(1.0, 0.5 + 0.5 * ny)) * (ao - 0.6F) / 0.4F;
      float facing = (float)(0.82F + 0.18F * Math.max(0.0, ny));
      double y = this.originY + this.l[1] * hx + this.l[4] * hy + this.l[7] * hz;
      this.lighting.surface(lambert, shadowed, ao, sky, this.looks.emission[id] / 15.0F, facing, y, this.light);
      this.shadedR = this.albedoR * this.light[0];
      this.shadedG = this.albedoG * this.light[1];
      this.shadedB = this.albedoB * this.light[2];
      float[] tint = this.style.sunTint();
      float direct = this.light[3]
         * Lighting.luminance(this.light[0], this.light[1], this.light[2])
         / Math.max(1.0E-6F, Lighting.luminance(tint[0], tint[1], tint[2]));
      this.sunlessR = this.albedoR * Math.max(0.0F, this.light[0] - direct * tint[0]);
      this.sunlessG = this.albedoG * Math.max(0.0F, this.light[1] - direct * tint[1]);
      this.sunlessB = this.albedoB * Math.max(0.0F, this.light[2] - direct * tint[2]);
   }

   private void albedo(int id, boolean top) {
      BlockLooks l = this.looks;
      int base = top ? l.top[id] : l.side[id];
      int tinted = top ? l.topTinted[id] : l.sideTinted[id];
      float w = top ? l.topTintWeight[id] : l.sideTintWeight[id];

      int tint = switch (l.tint[id]) {
         case 1 -> 9551193;
         case 2 -> 7842607;
         case 3 -> 10847821;
         case 4 -> 4159204;
         default -> l.constantTint[id] < 0 ? 16777215 : l.constantTint[id];
      };
      float[] lin = TileBaker.SRGB_TO_LINEAR;
      float bw = 1.0F - w;
      this.albedoR = lin[base >> 16 & 0xFF] * bw + lin[tinted >> 16 & 0xFF] * lin[tint >> 16 & 0xFF] * w;
      this.albedoG = lin[base >> 8 & 0xFF] * bw + lin[tinted >> 8 & 0xFF] * lin[tint >> 8 & 0xFF] * w;
      this.albedoB = lin[base & 0xFF] * bw + lin[tinted & 0xFF] * lin[tint & 0xFF] * w;
   }

   private float ambientOcclusion(int[] cell, int axis, int sign, double hx, double hy, double hz) {
      int a1 = (axis + 1) % 3;
      int a2 = (axis + 2) % 3;
      double h1 = a1 == 0 ? hx : (a1 == 1 ? hy : hz);
      double h2 = a2 == 0 ? hx : (a2 == 1 ? hy : hz);
      double f1 = Math.max(0.0, Math.min(1.0, h1 - cell[a1]));
      double f2 = Math.max(0.0, Math.min(1.0, h2 - cell[a2]));
      int fx = cell[0] + (axis == 0 ? sign : 0);
      int fy = cell[1] + (axis == 1 ? sign : 0);
      int fz = cell[2] + (axis == 2 ? sign : 0);
      float[] corners = this.corners;

      for (int c = 0; c < 4; c++) {
         int s1 = (c & 1) == 0 ? -1 : 1;
         int s2 = (c & 2) == 0 ? -1 : 1;
         int x1 = fx + (a1 == 0 ? s1 : 0);
         int y1 = fy + (a1 == 1 ? s1 : 0);
         int z1 = fz + (a1 == 2 ? s1 : 0);
         int x2 = fx + (a2 == 0 ? s2 : 0);
         int y2 = fy + (a2 == 1 ? s2 : 0);
         int z2 = fz + (a2 == 2 ? s2 : 0);
         corners[c] = TileBaker.corner(this.solid(x1, y1, z1), this.solid(x2, y2, z2), this.solid(x1 + x2 - fx, y1 + y2 - fy, z1 + z2 - fz));
      }

      float near = (float)(corners[0] + (corners[1] - corners[0]) * f1);
      float far = (float)(corners[2] + (corners[3] - corners[2]) * f1);
      return (float)(near + (far - near) * f2);
   }

   private boolean meetsShape(int[] cell, double x, double y, double z, double rx, double ry, double rz) {
      CraftBlocks b = this.blocks;
      int id = b.look(this.looks, CraftBlocks.index(b.sizeX, b.sizeZ, cell[0], cell[1], cell[2]));
      int shape = this.looks.shape[id];
      if (shape == 0) {
         return true;
      }

      float[] boxes = this.looks.shapes[shape];
      double[] o = new double[]{x, y, z};
      double[] r = new double[]{rx, ry, rz};

      for (int i = 0; i < boxes.length; i += 6) {
         double enter = 0.0;
         double exit = Double.MAX_VALUE;

         for (int a = 0; a < 3 && enter < exit; a++) {
            double lo = cell[a] + boxes[i + a];
            double hi = cell[a] + boxes[i + 3 + a];
            if (Math.abs(r[a]) < 1.0E-12) {
               if (o[a] < lo || o[a] > hi) {
                  exit = 0.0;
               }
            } else {
               double ta = (lo - o[a]) / r[a];
               double tb = (hi - o[a]) / r[a];
               enter = Math.max(enter, Math.min(ta, tb));
               exit = Math.min(exit, Math.max(ta, tb));
            }
         }

         if (enter < exit) {
            return true;
         }
      }

      return false;
   }

   private boolean inShadow(double x, double y, double z, double sx, double sy, double sz) {
      double[] m = this.inv;
      GridRay ray = this.sunRay;
      double rx = m[0] * sx + m[1] * sy + m[2] * sz;
      double ry = m[3] * sx + m[4] * sy + m[5] * sz;
      double rz = m[6] * sx + m[7] * sy + m[8] * sz;
      if (!ray.start(x, y, z, rx, ry, rz, 0.0)) {
         return false;
      }

      while (ray.next()) {
         if (this.solid(ray.cell[0], ray.cell[1], ray.cell[2]) && this.meetsShape(ray.cell, x, y, z, rx, ry, rz)) {
            return true;
         }
      }

      return false;
   }

   public record Image(
      int edge, int width, int height, double offsetU, double offsetV, int[][] mips, float[] depth, double[] linear, double pitchSin, int[] sunless
   ) {
   }
}
