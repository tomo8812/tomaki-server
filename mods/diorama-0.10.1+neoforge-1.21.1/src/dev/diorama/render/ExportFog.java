package dev.diorama.render;

import dev.diorama.MapStyle;
import dev.diorama.data.ImageExport;
import dev.diorama.data.TileStore;

public final class ExportFog implements ImageExport.Finish {
   private static final int NOISE = 256;
   private static final float NOISE_PERIOD = 512.0F;
   private static final float COS_PITCH = 0.643F;
   private static final int LAYERS = 8;
   private static final float NO_FOG_KNOWN = 0.95F;
   private static final float FOG_SHADOW = 0.7F;
   private static final int MAX_MASK = 2048;
   private static float[] noiseR;
   private static float[] noiseG;
   private static float[] noiseB;
   private final float fogHeight;
   private final float blurUnits;
   private final boolean cave;
   private final int lod;
   private final double unitsPerPixel;
   private final float[] known;
   private final byte[] flags;
   private final int maskW;
   private final int maskH;
   private final double originU;
   private final double originV;
   private final float texel;
   private final float fogScale;
   private final int scaleSteps;
   private final float scaleFraction;
   private final float[] lit;
   private final float[] shade;
   private final int background;
   private final float[] moonlit;

   private ExportFog(
      int lod, float[] known, int maskW, int maskH, double originU, double originV, float texel, int steps, float fraction, ExportFog.Look look, float night
   ) {
      boolean smoke = look == ExportFog.Look.SMOKE;
      this.cave = look == ExportFog.Look.CAVE;
      this.fogHeight = this.cave ? 4.0F : 10.0F;
      this.blurUnits = this.cave ? 2.5F : 7.0F;
      this.lod = lod;
      this.unitsPerPixel = (1 << lod) / 4.0;
      this.known = known;
      this.flags = new byte[known.length];
      this.maskW = maskW;
      this.maskH = maskH;
      this.originU = originU;
      this.originV = originV;
      this.texel = texel;
      this.scaleSteps = steps;
      this.scaleFraction = fraction;
      this.fogScale = (float)Math.pow(2.0, steps + fraction);
      this.lit = this.cave ? new float[]{0.24F, 0.26F, 0.33F} : (smoke ? new float[]{0.26F, 0.22F, 0.21F} : new float[]{0.97F, 0.97F, 0.99F});
      this.shade = this.cave ? new float[]{0.035F, 0.04F, 0.055F} : (smoke ? new float[]{0.07F, 0.055F, 0.055F} : new float[]{0.48F, 0.55F, 0.67F});
      this.background = NightLook.darken(this.cave ? -15987182 : (smoke ? -14081761 : -7562837), night) & 16777215;
      this.moonlit = NightLook.darken(new float[]{1.0F, 1.0F, 1.0F}, night);
   }

   public static ExportFog build(TileStore store, ImageExport.Area area, boolean smoke, float night) {
      return build(ImageExport.Tiles.of(store), area, smoke ? ExportFog.Look.SMOKE : ExportFog.Look.FOG, night);
   }

   public static ExportFog build(ImageExport.Tiles tiles, ImageExport.Area area, ExportFog.Look look, float night) {
      loadNoise();
      int lod = area.lod();
      double pixelsPerUnit = 4.0 / (1 << lod);
      double zoom = pixelsPerUnit / 2.0;
      double scaleLog = Math.min(7.99, Math.log(Math.max(1.0, 0.5 / zoom)) / Math.log(2.0));
      int steps = (int)Math.floor(scaleLog);
      float fraction = (float)Math.floor((scaleLog - steps) * 16.0) / 16.0F;
      float texel = Math.max(2, 1 << lod);
      double margin = margin(lod);
      double span = Math.max(area.width(), area.height()) / pixelsPerUnit + 2.0 * margin;

      while (span / texel > 2048.0) {
         texel *= 2.0F;
      }

      double u0 = area.x() / pixelsPerUnit - margin;
      double v0 = area.y() / pixelsPerUnit - margin;
      double u1 = (area.x() + area.width()) / pixelsPerUnit + margin;
      double v1 = (area.y() + area.height()) / pixelsPerUnit + margin;
      double originU = Math.floor(u0 / texel) * texel;
      double originV = Math.floor(v0 / texel) * texel;
      int maskW = (int)Math.ceil((u1 - originU) / texel);
      int maskH = (int)Math.ceil((v1 - originV) / texel);
      float[] sum = new float[maskW * maskH];
      int texelPixels = (int)Math.round(texel * pixelsPerUnit);
      long px0 = Math.round(originU * pixelsPerUnit);
      long py0 = Math.round(originV * pixelsPerUnit);
      long px1 = px0 + (long)maskW * texelPixels;
      long py1 = py0 + (long)maskH * texelPixels;
      int tile = 512;

      for (long tv = Math.floorDiv(py0, tile); tv <= Math.floorDiv(py1 - 1L, tile); tv++) {
         for (long tu = Math.floorDiv(px0, tile); tu <= Math.floorDiv(px1 - 1L, tile); tu++) {
            TileStore.Saved saved = tiles.tile((int)tu, (int)tv, lod);
            if (saved != null) {
               int[] pixels = saved.pixels();

               for (int y = 0; y < tile; y++) {
                  long gy = tv * tile + y;
                  if (gy >= py0 && gy < py1) {
                     int my = (int)((gy - py0) / texelPixels);

                     for (int x = 0; x < tile; x++) {
                        long gx = tu * tile + x;
                        if (gx >= px0 && gx < px1) {
                           sum[my * maskW + (int)((gx - px0) / texelPixels)] = sum[my * maskW + (int)((gx - px0) / texelPixels)]
                              + (pixels[y * tile + x] >>> 24) / 255.0F;
                        }
                     }
                  }
               }
            }
         }
      }

      float perTexel = texelPixels * texelPixels;

      for (int i = 0; i < sum.length; i++) {
         sum[i] = Math.round(Math.min(1.0F, sum[i] / perTexel) * 255.0F) / 255.0F;
      }

      ExportFog fog = new ExportFog(lod, sum, maskW, maskH, originU, originV, texel, steps, fraction, look, night);
      if (look != ExportFog.Look.CAVE && MapStyle.get().roundedFog() && texel < 16.0F) {
         fog.roundOff(Math.max(1, Math.round(10.0F / texel)));
      }

      fog.markReach();
      return fog;
   }

   public static double margin(int lod) {
      double zoom = 4.0 / (1 << lod) / 2.0;
      int steps = (int)Math.floor(Math.min(7.99, Math.log(Math.max(1.0, 0.5 / zoom)) / Math.log(2.0)));
      return 64.0 + 24.0 * Math.pow(2.0, steps + 1);
   }

   private static synchronized void loadNoise() {
      if (noiseR == null) {
         int[] pixels = FogTextures.noisePixels();
         float[] r = new float[pixels.length];
         float[] g = new float[pixels.length];
         float[] b = new float[pixels.length];

         for (int i = 0; i < pixels.length; i++) {
            r[i] = (pixels[i] & 0xFF) / 255.0F;
            g[i] = (pixels[i] >> 8 & 0xFF) / 255.0F;
            b[i] = (pixels[i] >> 16 & 0xFF) / 255.0F;
         }

         noiseR = r;
         noiseG = g;
         noiseB = b;
      }
   }

   private void roundOff(int radius) {
      float[] a = (float[])this.known.clone();
      float[] b = new float[a.length];
      this.boxBlur(a, b, radius, true);
      this.boxBlur(b, a, radius, false);
      this.boxBlur(a, b, radius, true);
      this.boxBlur(b, a, radius, false);

      for (int i = 0; i < a.length; i++) {
         float t = Math.max(0.0F, Math.min(1.0F, (a[i] - 0.5F) / 0.35F));
         float rounded = t * t * (3.0F - 2.0F * t);
         this.known[i] = Math.min(this.known[i], Math.round(rounded * 255.0F) / 255.0F);
      }
   }

   private void boxBlur(float[] from, float[] to, int radius, boolean horizontal) {
      int lines = horizontal ? this.maskH : this.maskW;
      int length = horizontal ? this.maskW : this.maskH;
      float scale = 1.0F / (2 * radius + 1);

      for (int line = 0; line < lines; line++) {
         float sum = 0.0F;

         for (int k = -radius; k <= radius; k++) {
            sum += from[this.index(line, Math.max(0, Math.min(length - 1, k)), horizontal)];
         }

         for (int k = 0; k < length; k++) {
            to[this.index(line, k, horizontal)] = sum * scale;
            int add = Math.min(length - 1, k + radius + 1);
            int drop = Math.max(0, k - radius);
            sum += from[this.index(line, add, horizontal)] - from[this.index(line, drop, horizontal)];
         }
      }
   }

   private int index(int line, int k, boolean horizontal) {
      return horizontal ? line * this.maskW + k : k * this.maskW + line;
   }

   private void markReach() {
      double blur = 1.9 * Math.max(this.blurUnits * (this.cave ? 1.0 : 2.4) / this.texel, 1.0);
      double layerShift = this.fogHeight * 0.643F / this.texel;
      double sun = 9.0 * (1 << this.scaleSteps + 1) / this.texel;
      double slack = 1.5;
      int west = (int)Math.ceil(Math.max(blur, sun) + slack);
      int east = (int)Math.ceil(blur + slack);
      int north = (int)Math.ceil(Math.max(blur, sun * 0.4) + slack);
      int south = (int)Math.ceil(blur + layerShift + slack);
      int w = this.maskW;
      int h = this.maskH;
      int row = w + 1;
      int[] notKnown = new int[(w + 1) * (h + 1)];
      int[] notUnknown = new int[(w + 1) * (h + 1)];

      for (int j = 0; j < h; j++) {
         int lineNotKnown = 0;
         int lineNotUnknown = 0;

         for (int i = 0; i < w; i++) {
            float k = this.known[j * w + i];
            lineNotKnown += k >= 1.0F ? 0 : 1;
            lineNotUnknown += k <= 0.0F ? 0 : 1;
            notKnown[(j + 1) * row + i + 1] = notKnown[j * row + i + 1] + lineNotKnown;
            notUnknown[(j + 1) * row + i + 1] = notUnknown[j * row + i + 1] + lineNotUnknown;
         }
      }

      for (int j = 0; j < h; j++) {
         int top = Math.max(0, j - north);
         int bottom = Math.min(h - 1, j + south) + 1;

         for (int i = 0; i < w; i++) {
            int left = Math.max(0, i - west);
            int right = Math.min(w - 1, i + east) + 1;
            int a = bottom * row + right;
            int b = top * row + right;
            int c = bottom * row + left;
            int d = top * row + left;
            if (notKnown[a] - notKnown[b] - notKnown[c] + notKnown[d] == 0) {
               this.flags[j * w + i] = 1;
            } else if (notUnknown[a] - notUnknown[b] - notUnknown[c] + notUnknown[d] == 0) {
               this.flags[j * w + i] = 2;
            }
         }
      }
   }

   @Override
   public void row(int[] abgr, long y, long x0) {
      ExportFog.Pixel p = new ExportFog.Pixel();

      for (int i = 0; i < abgr.length; i++) {
         abgr[i] = this.paint(abgr[i], x0 + i, y, p);
      }
   }

   private int paint(int abgr, long x, long y, ExportFog.Pixel p) {
      double u = (x + 0.5) * this.unitsPerPixel;
      double v = (y + 0.5) * this.unitsPerPixel;
      int ti = (int)Math.floor((u - this.originU) / this.texel);
      int tj = (int)Math.floor((v - this.originV) / this.texel);
      byte flag = ti >= 0 && tj >= 0 && ti < this.maskW && tj < this.maskH ? this.flags[tj * this.maskW + ti] : 0;
      p.clear = flag == 1;
      p.deep = flag == 2;
      float jitter = hash(x, y) - 0.5F;
      float r = 0.0F;
      float g = 0.0F;
      float b = 0.0F;
      float alpha = 0.0F;
      float toSunU = -9.0F * this.fogScale;
      float toSunV = -3.6F * this.fogScale;

      for (int i = 0; i < 8; i++) {
         float h = this.fogHeight * (1.0F - (i + 0.5F + jitter * 0.6F) / 8.0F);
         double gv = v + h * 0.643F;
         float th = this.thickness(u, gv, p);
         float a = smoothstep(h - 1.4F, h + 1.4F, th);
         if (!(a <= 0.002F)) {
            double probeU = u + toSunU;
            double probeV = gv + toSunV;
            float toward = this.thicknessAt(probeU, probeV, p.clear ? 1.0F : this.knownAt(probeU, probeV, p), p);
            float occlusion = clamp((toward - th) / this.fogHeight * 2.2F);
            float light = clamp(0.2F + 0.7F * (h / this.fogHeight) + 0.3F - occlusion * 0.7F);
            float layerAlpha = a * 0.45F;
            r += (1.0F - alpha) * layerAlpha * mix(this.shade[0], this.lit[0], light);
            g += (1.0F - alpha) * layerAlpha * mix(this.shade[1], this.lit[1], light);
            b += (1.0F - alpha) * layerAlpha * mix(this.shade[2], this.lit[2], light);
            alpha += (1.0F - alpha) * layerAlpha;
            if (alpha > 0.985F) {
               break;
            }
         }
      }

      float soft = this.knownSoft(u, v, p);
      float floorFog = smoothstep(0.5F, 2.5F, this.thicknessAt(u, v, soft, p));
      r += (1.0F - alpha) * floorFog * this.shade[0];
      g += (1.0F - alpha) * floorFog * this.shade[1];
      b += (1.0F - alpha) * floorFog * this.shade[2];
      alpha += (1.0F - alpha) * floorFog;
      float knownHere = this.knownAt(u, v, p);
      alpha *= 1.0F - 0.85F * smoothstep(0.45F, 0.95F, this.cave ? knownHere : soft);
      float empty = 1.0F - smoothstep(0.3F, 0.85F, knownHere);
      float coverR;
      float coverG;
      float coverB;
      if (alpha > 0.02F) {
         coverR = r / alpha;
         coverG = g / alpha;
         coverB = b / alpha;
      } else {
         coverR = mix(this.shade[0], this.lit[0], 0.7F);
         coverG = mix(this.shade[1], this.lit[1], 0.7F);
         coverB = mix(this.shade[2], this.lit[2], 0.7F);
      }

      r += (1.0F - alpha) * empty * coverR;
      g += (1.0F - alpha) * empty * coverG;
      b += (1.0F - alpha) * empty * coverB;
      alpha += (1.0F - alpha) * empty;
      r *= this.moonlit[0];
      g *= this.moonlit[1];
      b *= this.moonlit[2];
      float ta = (abgr >>> 24) / 255.0F;
      float gr = (abgr & 0xFF) / 255.0F * ta + (this.background >> 16 & 0xFF) / 255.0F * (1.0F - ta);
      float gg = (abgr >> 8 & 0xFF) / 255.0F * ta + (this.background >> 8 & 0xFF) / 255.0F * (1.0F - ta);
      float gb = (abgr >> 16 & 0xFF) / 255.0F * ta + (this.background & 0xFF) / 255.0F * (1.0F - ta);
      if (alpha <= 0.002F) {
         return 0xFF000000 | channel(gb) << 16 | channel(gg) << 8 | channel(gr);
      }

      float grain = (hash(y, x) - 0.5F) / 255.0F;
      float or = clamp((r + grain) / alpha) * alpha + gr * (1.0F - alpha);
      float og = clamp((g + grain) / alpha) * alpha + gg * (1.0F - alpha);
      float ob = clamp((b + grain) / alpha) * alpha + gb * (1.0F - alpha);
      return 0xFF000000 | channel(ob) << 16 | channel(og) << 8 | channel(or);
   }

   private float knownAt(double u, double v, ExportFog.Pixel p) {
      return p.deep ? 0.0F : this.sample(u, v);
   }

   private float sample(double u, double v) {
      double fx = (u - this.originU) / this.texel - 0.5;
      double fy = (v - this.originV) / this.texel - 0.5;
      int x0 = (int)Math.floor(fx);
      int y0 = (int)Math.floor(fy);
      float tx = (float)(fx - x0);
      float ty = (float)(fy - y0);
      int xa = Math.max(0, Math.min(this.maskW - 1, x0));
      int xb = Math.max(0, Math.min(this.maskW - 1, x0 + 1));
      int ya = Math.max(0, Math.min(this.maskH - 1, y0));
      int yb = Math.max(0, Math.min(this.maskH - 1, y0 + 1));
      float top = mix(this.known[ya * this.maskW + xa], this.known[ya * this.maskW + xb], tx);
      float bottom = mix(this.known[yb * this.maskW + xa], this.known[yb * this.maskW + xb], tx);
      return mix(top, bottom, ty);
   }

   private float knownBlur(double u, double v, float units) {
      float r = Math.max(units, this.texel);
      float k = this.sample(u, v) * 0.2F;
      k += this.sample(u + r, v) * 0.1F;
      k += this.sample(u - r, v) * 0.1F;
      k += this.sample(u, v + r) * 0.1F;
      k += this.sample(u, v - r) * 0.1F;
      float d = r * 1.9F;
      k += this.sample(u + d, v + d) * 0.1F;
      k += this.sample(u - d, v + d) * 0.1F;
      k += this.sample(u + d, v - d) * 0.1F;
      return k + this.sample(u - d, v - d) * 0.1F;
   }

   private float knownSoft(double u, double v, ExportFog.Pixel p) {
      if (p.clear) {
         return 1.0F;
      } else if (p.deep) {
         return 0.0F;
      } else {
         return this.cave
            ? this.knownBlur(u, v, this.blurUnits)
            : 0.5F * this.knownBlur(u, v, this.blurUnits) + 0.5F * this.knownBlur(u, v, this.blurUnits * 2.4F);
      }
   }

   private static float noiseLayer(double nu, double nv) {
      return noise(noiseR, nu, nv) * 0.55F + noise(noiseG, nu, nv) * 0.3F + noise(noiseB, nu, nv) * 0.15F;
   }

   private static float noise(float[] channel, double nu, double nv) {
      double fx = nu * 256.0 - 0.5;
      double fy = nv * 256.0 - 0.5;
      long x0 = (long)Math.floor(fx);
      long y0 = (long)Math.floor(fy);
      float tx = (float)(fx - x0);
      float ty = (float)(fy - y0);
      int xa = Math.floorMod(x0, 256);
      int xb = Math.floorMod(x0 + 1L, 256);
      int ya = Math.floorMod(y0, 256);
      int yb = Math.floorMod(y0 + 1L, 256);
      float top = mix(channel[ya * 256 + xa], channel[ya * 256 + xb], tx);
      float bottom = mix(channel[yb * 256 + xa], channel[yb * 256 + xb], tx);
      return mix(top, bottom, ty);
   }

   private float noiseAt(double u, double v) {
      double coarsePeriod = 512.0 * Math.pow(2.0, this.scaleSteps + 1);
      double cu = u / coarsePeriod;
      double cv = v / coarsePeriod;
      float fine = noiseLayer(cu * 2.0, cv * 2.0);
      return this.scaleFraction == 0.0F ? fine : mix(fine, noiseLayer(cu, cv), this.scaleFraction);
   }

   private float thicknessFrom(float known, float n) {
      float unknown = 1.0F - known;
      float edge = smoothstep(0.0F, 0.35F, unknown);
      float amount = smoothstep(0.12F, 0.75F, unknown + (n - 0.5F) * 1.5F * edge);
      return this.fogHeight * amount * (0.45F + 0.75F * n);
   }

   private float thicknessAt(double u, double v, float known, ExportFog.Pixel p) {
      return known >= 0.95F ? 0.0F : this.thicknessFrom(known, this.noiseAt(u, v));
   }

   private float thickness(double u, double v, ExportFog.Pixel p) {
      return this.thicknessAt(u, v, this.knownSoft(u, v, p), p);
   }

   private static float hash(long x, long y) {
      double px = Math.floorMod(x, 4096) + 0.5;
      double py = Math.floorMod(y, 4096) + 0.5;
      double s = Math.sin(px * 12.9898 + py * 78.233) * 43758.5453;
      return (float)(s - Math.floor(s));
   }

   private static float smoothstep(float e0, float e1, float x) {
      float t = clamp((x - e0) / (e1 - e0));
      return t * t * (3.0F - 2.0F * t);
   }

   private static float clamp(float x) {
      return Math.max(0.0F, Math.min(1.0F, x));
   }

   private static float mix(float a, float b, float t) {
      return a + (b - a) * t;
   }

   private static int channel(float c) {
      return Math.max(0, Math.min(255, Math.round(c * 255.0F)));
   }

   public enum Look {
      FOG,
      SMOKE,
      CAVE;

      // $VF: synthetic method
      private static ExportFog.Look[] $values() {
         return new ExportFog.Look[]{FOG, SMOKE, CAVE};
      }
   }

   private static final class Pixel {
      boolean clear;
      boolean deep;
   }
}
