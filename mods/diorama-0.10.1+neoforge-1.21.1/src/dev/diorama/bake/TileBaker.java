package dev.diorama.bake;

import dev.diorama.Diorama;
import dev.diorama.Facing;
import dev.diorama.MapStyle;
import dev.diorama.Projection;
import dev.diorama.data.ChunkSnapshot;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jspecify.annotations.Nullable;

public final class TileBaker {
   static final double[][] SAMPLES = new double[][]{{0.375, 0.125}, {0.875, 0.375}, {0.125, 0.625}, {0.625, 0.875}};
   static final double[][] FAST_SAMPLES = new double[][]{{0.5, 0.5}};
   private static volatile boolean fast;
   public static final float LID_AMBIENT = 0.26F;
   public static final float LID_KEY = 0.52F;
   public static final float[] LID_KEY_TINT = new float[]{1.1F, 1.0F, 0.88F};
   public static final float[] LID_FILL_TINT = new float[]{1.0F, 0.86F, 0.84F};
   public static final float LID_GLOW = 0.7F;
   public static final int HAZE_START = 30;
   public static final double HAZE_DEPTH = 55.0;
   public static final float HAZE_MAX = 0.72F;
   public static final float HAZE_LIGHT = 1.6F;
   public static final float HAZE_MAX_LUMINANCE = 0.025F;
   public static final double SUN_AZIMUTH_X = -0.878;
   public static final double SUN_AZIMUTH_Z = 0.479;
   public static final double[][] SUN_JITTER = new double[][]{{0.03, 0.0, -0.02}, {-0.02, 0.0, 0.03}, {0.02, 0.0, 0.02}, {-0.03, 0.0, -0.03}};
   public static final double[][] SKY_RAYS;
   public static final double[] SKY_WEIGHTS;
   public static final int SKY_REACH = 12;
   private static final float[] AO_LEVELS;
   static final float SIDE = 0.82F;
   private static final float OUTLINE_WATER_FADE = 6.0F;
   private static final float OUTLINE_DEPTH = 1.5F;
   private static final float CAVE_OUTLINE_DEPTH = 3.0F;
   public static final int CAVE_DEPTH = 48;
   static final int CAVE_ROCK = 10;
   static final float CAVE_FADE = 12.0F;
   static final int CAVE_FADE_START = 6;
   static final float CAVE_WALL_FADE = 4.0F;
   private static final int CAVE_WALL_REACH = 16;
   private static final int SHADOW_STEPS = 96;
   public static final double SHADOW_DISTANCE = 40.0;
   public static final int SHADOW_REACH_X;
   public static final int SHADOW_REACH_Z;
   public static final int SHADOW_REACH_Y;
   static final float[] SRGB_TO_LINEAR;
   private static final int[] LINEAR_TO_SRGB;
   private final TileKey key;
   private final Looks looks;
   final VoxelReader reader;
   final boolean cave;
   final boolean roofless;
   final boolean lit;
   final int startY;
   final int base;
   final @Nullable CaveCut caveCut;
   final int rockLimit;
   final boolean depthLook;
   final int limitY;
   final int topY;
   final int rayTop;
   private final @Nullable TallChunks tall;
   final boolean fogOfWar;
   private final @Nullable Overlays overlays;
   private final Map<Long, Boolean> slimeChunks = new HashMap<>();
   private final ChunkSource source;
   private final MapStyle style;
   private final Lighting lighting;
   private float[] light = new float[3];
   private final float[] unlit = new float[3];
   private final double sin;
   private final double cos;
   private final double invSin;
   private final double invCos;
   private final double sunX;
   private final double sunY;
   private final double sunZ;
   final boolean lifted;
   private final Long2IntOpenHashMap lidAt = new Long2IntOpenHashMap();
   private final Long2IntOpenHashMap fogAt = new Long2IntOpenHashMap();
   private final Long2FloatOpenHashMap skyAtCorner = new Long2FloatOpenHashMap();
   private float outR;
   private float outG;
   private float outB;
   private float outDepth;
   private float outWater;
   private int outBlockLight;
   private boolean hitSolid;
   private boolean hitTop;
   private int hitId;
   private int hitX;
   private int hitY;
   private int hitZ;
   private double hitFx;
   private double hitFz;
   private final double depthOrigin;
   private boolean fourSamples;
   private int @Nullable [] savedSpots;
   private boolean surfaceOnly;
   private float surfaceY;
   private boolean surfaceTop;
   private int surfaceLight;
   private int surfaceShaded;
   private boolean surfaceDepthOnly;
   float hazeR;
   float hazeG;
   float hazeB;
   private static final int NETHER_WASTES_FOG = 3344392;
   float albedoR;
   float albedoG;
   float albedoB;
   private double shapeT;
   private boolean shapeTop;

   private TileBaker(TileKey key, ChunkSource source, Looks looks, boolean fogOfWar, @Nullable Overlays overlays) {
      this.key = key;
      this.overlays = overlays;
      this.looks = looks;
      this.reader = new VoxelReader(source, looks, true);
      this.cave = key.isCave();
      this.roofless = !this.cave && source.roof() != Integer.MIN_VALUE;
      this.lit = this.cave || this.roofless;
      this.source = source;
      this.fogOfWar = this.lit && fogOfWar;
      this.style = MapStyle.get();
      this.lighting = new Lighting(this.style);
      double elevation = Math.toRadians(this.style.sunElevation());
      this.sunX = Math.cos(elevation) * -0.878;
      this.sunY = Math.sin(elevation);
      this.sunZ = Math.cos(elevation) * 0.479;
      this.sin = Projection.sin();
      this.cos = Projection.cos();
      this.invSin = 1.0 / this.sin;
      this.invCos = 1.0 / this.cos;
      this.topY = localTop(key, source, 0);
      this.rayTop = localTop(key, source, 12);
      this.lifted = this.lit && source.roof() != Integer.MIN_VALUE;
      this.depthLook = this.cave && !this.lifted && source.depthCaves();
      this.base = this.cave ? Math.min(key.layer(), source.maxY()) : Integer.MIN_VALUE;
      this.limitY = this.cave ? Math.max(source.minY(), this.base - 48) : source.minY();
      CaveCut cut = this.depthLook && key.lod() == 0
         ? new CaveCut(
            this.reader,
            this.base,
            (int)Math.floor(key.minU()) - 1,
            (int)Math.floor((key.minV() + this.limitY * this.cos) / this.sin) - 2,
            (int)Math.ceil(key.minU() + key.units()) + 1,
            (int)Math.ceil((key.minV() + key.units() + (this.base + 8) * this.cos) / this.sin) + 2
         )
         : null;
      this.caveCut = cut != null && cut.raises() ? cut : null;
      this.startY = this.cave
         ? Math.min(this.base + (this.caveCut != null ? 8 : 0), source.maxY())
         : (this.roofless ? Math.min(source.roof(), source.maxY()) : this.topY);
      this.tall = this.lit ? null : TallChunks.around(key, source, this.startY);
      double originZ = (key.minV() + this.startY * this.cos) / this.sin;
      this.depthOrigin = -(this.sin * this.startY + this.cos * originZ);
      this.rockLimit = this.depthLook ? this.base - 10 : Integer.MIN_VALUE;
      this.lidAt.defaultReturnValue(Integer.MIN_VALUE);
      this.fogAt.defaultReturnValue(-1);
   }

   int cutAt(int x, int z) {
      return this.caveCut == null ? this.startY : this.caveCut.at(x, z);
   }

   int lid(int x, int z) {
      long key = (long)x << 32 | z & 4294967295L;
      int lid = this.lidAt.get(key);
      if (lid == Integer.MIN_VALUE) {
         lid = this.reader.lid(x, z, this.cutAt(x, z), this.limitY);
         this.lidAt.put(key, lid);
      }

      return lid;
   }

   boolean passesMissing(int y) {
      return this.tall != null && y >= this.tall.missingTop;
   }

   private void hazeColor(double x, double y, double z) {
      this.fogBlend(x, y, z);
      float scale = 1.6F * Math.min(1.0F, 0.025F / Math.max(1.0E-6F, luminance(this.hazeR, this.hazeG, this.hazeB)));
      this.hazeR *= scale;
      this.hazeG *= scale;
      this.hazeB *= scale;
   }

   void fogBlend(double x, double y, double z) {
      double qx = x / 4.0 - 0.5;
      double qz = z / 4.0 - 0.5;
      int x0 = (int)Math.floor(qx);
      int z0 = (int)Math.floor(qz);
      int qy = (int)Math.floor(y) >> 2;
      double fx = qx - x0;
      double fz = qz - z0;
      float r = 0.0F;
      float g = 0.0F;
      float b = 0.0F;

      for (int i = 0; i < 4; i++) {
         int cx = x0 + (i & 1);
         int cz = z0 + (i >> 1);
         double w = ((i & 1) == 0 ? 1.0 - fx : fx) * (i >> 1 == 0 ? 1.0 - fz : fz);
         int c = this.fogCell(cx, qy, cz);
         r += (float)(w * SRGB_TO_LINEAR[c >> 16 & 0xFF]);
         g += (float)(w * SRGB_TO_LINEAR[c >> 8 & 0xFF]);
         b += (float)(w * SRGB_TO_LINEAR[c & 0xFF]);
      }

      this.hazeR = r;
      this.hazeG = g;
      this.hazeB = b;
   }

   private int fogCell(int qx, int qy, int qz) {
      long key = (long)(qx & 67108863) << 38 | (long)(qz & 67108863) << 12 | qy + 2048 & 4095;
      int color = this.fogAt.get(key);
      if (color < 0) {
         ChunkSnapshot snapshot = this.reader.snapshot(qx << 2, qz << 2);
         color = snapshot == null ? 3344392 : fogColor(snapshot.noiseBiome(qx, qy, qz));
         this.fogAt.put(key, color);
      }

      return color;
   }

   private static int fogColor(Holder<Biome> biome) {
      if (biome.is(Biomes.CRIMSON_FOREST)) {
         return 3343107;
      } else if (biome.is(Biomes.WARPED_FOREST)) {
         return 1705242;
      } else if (biome.is(Biomes.SOUL_SAND_VALLEY)) {
         return 1787717;
      } else {
         return biome.is(Biomes.BASALT_DELTAS) ? 6840176 : 3344392;
      }
   }

   static TileBaker forMeshing(TileKey key, ChunkSource source, Looks looks, boolean fogOfWar, int @Nullable [] savedSpots) {
      TileBaker baker = new TileBaker(key, source, looks, fogOfWar, null);
      baker.useSavedSpots(savedSpots);
      return baker;
   }

   private void useSavedSpots(int @Nullable [] spots) {
      this.savedSpots = !this.lit && this.key.lod() == 0 ? spots : null;
   }

   boolean passedWhenSaved(int x, int y, int z) {
      return TileSpots.passedWhenSaved(this.key, this.savedSpots, x, y, z);
   }

   public static TileSurface surface(TileKey key, int revision, ChunkSource source, Looks looks, boolean fogOfWar, int x0, int y0, int width, int height) {
      TileBaker baker = new TileBaker(key, source, looks, fogOfWar, null);
      int count = width * height;
      TileSurface surface = new TileSurface(
         revision,
         x0,
         y0,
         width,
         height,
         new float[count],
         new float[count],
         new boolean[count],
         new byte[count],
         new int[count],
         new int[count],
         new float[count]
      );
      Arrays.fill(surface.y(), Float.NaN);
      Arrays.fill(surface.near(), Float.NaN);
      if (baker.startY > baker.limitY && !baker.lifted) {
         if (!baker.cave) {
            baker.surfaceOnly = true;
            baker.light = new float[4];
            double unit = 0.25;

            for (int py = 0; py < height; py++) {
               for (int px = 0; px < width; px++) {
                  baker.surfaceY = Float.NaN;
                  if (baker.trace(key.minU() + (x0 + px + 0.5) * unit, key.minV() + (y0 + py + 0.5) * unit, 0) && !Float.isNaN(baker.surfaceY)) {
                     int i = py * width + px;
                     surface.y()[i] = baker.surfaceY;
                     surface.top()[i] = baker.surfaceTop;
                     surface.sun()[i] = (byte)Math.round(Math.max(0.0F, Math.min(1.0F, baker.light[3])) * 255.0F);
                     surface.albedo()[i] = baker.surfaceShaded;
                  }

                  int i = py * width + px;
                  float near = Float.NaN;
                  baker.surfaceDepthOnly = true;

                  for (int s = 0; s < SAMPLES.length; s++) {
                     baker.surfaceY = Float.NaN;
                     double v = key.minV() + (y0 + py + SAMPLES[s][1]) * unit;
                     if (baker.trace(key.minU() + (x0 + px + SAMPLES[s][0]) * unit, v, s) && !Float.isNaN(baker.surfaceY)) {
                        double z = (v + baker.surfaceY * baker.cos) / baker.sin;
                        float depth = (float)(-(baker.sin * baker.surfaceY + baker.cos * z));
                        near = Float.isNaN(near) ? depth : Math.min(near, depth);
                     }
                  }

                  baker.surfaceDepthOnly = false;
                  surface.near()[i] = near;
               }
            }

            return surface;
         } else {
            int[] pixels = new int[count];
            float[] linear = new float[count * 4];
            baker.bakePixels(x0, y0, width, height, pixels, null, linear, null);
            double unitsPerPixel = 0.25;
            baker.surfaceOnly = true;

            for (int py = 0; py < height; py++) {
               for (int px = 0; px < width; px++) {
                  int i = py * width + px;
                  baker.surfaceY = Float.NaN;
                  baker.surfaceLight = 0;
                  if (pixels[i] != 0) {
                     double u = key.minU() + (x0 + px + 0.5) * unitsPerPixel;
                     double v = key.minV() + (y0 + py + 0.5) * unitsPerPixel;
                     boolean hit = baker.trace(u, v, 0) && !Float.isNaN(baker.surfaceY);

                     for (int s = 0; s < SAMPLES.length && !hit; s++) {
                        baker.surfaceY = Float.NaN;
                        baker.surfaceLight = 0;
                        hit = baker.trace(key.minU() + (x0 + px + SAMPLES[s][0]) * unitsPerPixel, key.minV() + (y0 + py + SAMPLES[s][1]) * unitsPerPixel, s)
                           && !Float.isNaN(baker.surfaceY);
                     }

                     if (hit) {
                        surface.y()[i] = baker.surfaceY;
                        surface.near()[i] = (float)(-(baker.sin * baker.surfaceY + baker.cos * (v + baker.surfaceY * baker.cos) / baker.sin));
                        surface.top()[i] = baker.surfaceTop;
                        surface.light()[i] = baker.surfaceLight;
                        surface.outline()[i] = linear[i * 4 + 3];
                        surface.albedo()[i] = pixels[i] & 0xFF000000 | srgb(linear[i * 4], linear[i * 4 + 1], linear[i * 4 + 2]);
                     }
                  }
               }
            }

            return surface;
         }
      } else {
         return surface;
      }
   }

   private void landed(int id, int x, int by, int bz, double fx, double hitY, double hitZ, boolean top, boolean partial) {
      this.surfaceY = (float)hitY;
      if (!this.surfaceDepthOnly) {
         if (partial && !this.cave && this.smallFootprint(id)) {
            this.surfaceY = by;
         }

         this.shade(id, x, by, bz, fx, hitY, hitZ, top, partial, 0);
         this.surfaceShaded = this.cave ? 0 : this.withoutSun();
         this.surfaceTop = top;
      }
   }

   private boolean smallFootprint(int id) {
      float[] boxes = this.looks.shapes[this.looks.shape[id]];
      float area = 0.0F;

      for (int i = 0; i < boxes.length; i += 6) {
         area += (boxes[i + 3] - boxes[i]) * (boxes[i + 5] - boxes[i + 2]);
      }

      return area < 0.5F;
   }

   public static TileBaker forSunChecks(ChunkSource source, Looks looks, int pu0, int pv0, int pu1, int pv1) {
      int size = 512;
      TileKey tallest = null;
      int top = Integer.MIN_VALUE;

      for (int tv = Math.floorDiv(pv0, size); tv <= Math.floorDiv(pv1 - 1, size); tv++) {
         for (int tu = Math.floorDiv(pu0, size); tu <= Math.floorDiv(pu1 - 1, size); tu++) {
            TileKey key = new TileKey(Integer.MIN_VALUE, tu, tv, 0);
            int reach = localTop(key, source, 12);
            if (reach > top) {
               top = reach;
               tallest = key;
            }
         }
      }

      return new TileBaker(tallest, source, looks, false, null);
   }

   public boolean sunBlocked(double x, double y, double z) {
      return this.inShadow(x + this.sunX * 0.06, y + this.sunY * 0.06, z + this.sunZ * 0.06, this.sunX, this.sunY, this.sunZ);
   }

   private int withoutSun() {
      float[] tint = this.style.sunTint();
      float direct = this.light[3] * luminance(this.light[0], this.light[1], this.light[2]) / Math.max(1.0E-6F, luminance(tint[0], tint[1], tint[2]));
      return this.abgr(
         this.albedoR * Math.max(0.0F, this.light[0] - direct * tint[0]),
         this.albedoG * Math.max(0.0F, this.light[1] - direct * tint[1]),
         this.albedoB * Math.max(0.0F, this.light[2] - direct * tint[2]),
         255
      );
   }

   private void landedOnWater(int waterId, int x, int by, int bz, double fx, double y, double z) {
      if (this.surfaceDepthOnly) {
         this.surfaceY = (float)y;
      } else {
         this.albedo(waterId, x, bz, true);
         this.surfaceShaded = 0;
         if (this.cave) {
            this.surfaceLight = this.waterLight(x, by, bz, y);
         } else {
            boolean shadowed = this.inShadow(x + fx, y + 1.0E-4, z, this.sunX, this.sunY, this.sunZ);
            this.lighting.surface(1.0, shadowed, 1.0F, 1.0F, 0.0F, 1.0F, y, this.light);
         }

         this.surfaceY = (float)y;
         this.surfaceTop = true;
      }
   }

   private int waterLight(int x, int by, int bz, double y) {
      float fade = this.depthLook ? this.caveFade(y) : 1.0F - Math.min(0.5F, (float)(this.startY - y) / 60.0F);
      return TileSurface.caveLight(this.reader.blockLight(x, by + 1, bz), this.reader.skyLight(x, by + 1, bz), 1.0F, fade, 0) | 268435456;
   }

   private float caveFade(double y) {
      return (float)Math.exp(Math.min(0.0, y - this.base + 6.0) / 12.0);
   }

   int wallDepth(int x, int y, int z) {
      int d = 0;

      while (d < 16 && y + 1 + d < this.cutAt(x, z) && this.reader.solid(x, y + 1 + d, z) > 0) {
         d++;
      }

      return d;
   }

   private static int srgb(float r, float g, float b) {
      return LINEAR_TO_SRGB[Math.min(4095, Math.max(0, (int)(r * 4095.0F)))] << 16
         | LINEAR_TO_SRGB[Math.min(4095, Math.max(0, (int)(g * 4095.0F)))] << 8
         | LINEAR_TO_SRGB[Math.min(4095, Math.max(0, (int)(b * 4095.0F)))];
   }

   static int localTop(TileKey key, ChunkSource source, int moreNorth) {
      return localTop(key, source, moreNorth, Math.min(source.maxY(), source.topY()));
   }

   static int localTop(TileKey key, ChunkSource source, int moreNorth, int ceiling) {
      double minU = key.minU();
      double minV = key.minV();
      int x0 = Math.floorDiv((int)Math.floor(minU) - 1, 16);
      int x1 = Math.floorDiv((int)Math.ceil(minU + 128.0) + SHADOW_REACH_X + 1, 16);
      int z0 = Math.floorDiv((int)Math.floor(Projection.zAt(minV, source.minY())) - 1 - moreNorth, 16);
      int z1 = Math.floorDiv((int)Math.ceil(Projection.zAt(minV + 128.0, ceiling)) + SHADOW_REACH_Z + 1, 16);
      int top = Integer.MIN_VALUE;

      for (int cx = x0 - 2; cx <= x1; cx++) {
         for (int cz = z0; cz <= z1 + 1; cz++) {
            top = Math.max(top, source.topBelow(cx, cz, ceiling));
         }
      }

      return top == Integer.MIN_VALUE ? ceiling : Math.min(ceiling, top);
   }

   public static boolean fast() {
      return fast;
   }

   public static void setFast(boolean value) {
      fast = value;
   }

   public static TileImage bake(TileKey key, ChunkSource source, Looks looks, @Nullable BitSet blocks, boolean fogOfWar) {
      return bake(key, source, looks, blocks, fogOfWar, null);
   }

   public static TileImage bake(TileKey key, ChunkSource source, Looks looks, @Nullable BitSet blocks, boolean fogOfWar, int @Nullable [] saved) {
      return bake(key, source, looks, blocks, fogOfWar, saved, null, null, null);
   }

   public static TileImage bake(
      TileKey key, ChunkSource source, Looks looks, @Nullable BitSet blocks, boolean fogOfWar, int @Nullable [] saved, @Nullable Overlays overlays
   ) {
      return bake(key, source, looks, blocks, fogOfWar, saved, null, null, overlays);
   }

   public static TileImage bake(
      TileKey key,
      ChunkSource source,
      Looks looks,
      @Nullable BitSet blocks,
      boolean fogOfWar,
      int @Nullable [] saved,
      byte @Nullable [] savedLight,
      int @Nullable [] savedSpots,
      @Nullable Overlays overlays
   ) {
      TileBaker baker = new TileBaker(key, source, looks, fogOfWar, overlays);
      baker.useSavedSpots(savedSpots);
      int size = 512;
      if (blocks == null) {
         int[] pixels = new int[size * size];
         byte[] info = overlays == null ? null : new byte[size * size];
         byte[] pixelLight = baker.lit ? null : new byte[size * size];
         baker.bakePixels(0, 0, size, size, pixels, info, null, pixelLight);
         byte[] blockLight = pixelLight == null ? null : BlockLight.shrink(pixelLight, pixels, saved, savedLight, 0, 0, size);
         merge(pixels, saved, 0, 0, size);
         return TileImage.full(key, Mipmaps.build(pixels, size), coverage(pixels, size, size), info).withBlockLight(blockLight);
      }

      int perRow = size / 32;
      List<TileImage.Patch> patches = new ArrayList<>();

      for (int i = blocks.nextSetBit(0); i >= 0; i = blocks.nextSetBit(i + 1)) {
         int bx = i % perRow;
         int by = i / perRow;
         int[] pixels = new int[1024];
         byte[] info = overlays == null ? null : new byte[1024];
         byte[] pixelLight = baker.lit ? null : new byte[1024];
         baker.bakePixels(bx * 32, by * 32, 32, 32, pixels, info, null, pixelLight);
         byte[] blockLight = pixelLight == null ? null : BlockLight.shrink(pixelLight, pixels, saved, savedLight, bx * 32, by * 32, 32);
         merge(pixels, saved, bx * 32, by * 32, 32);
         patches.add(new TileImage.Patch(bx, by, Mipmaps.build(pixels, 32), coverage(pixels, 32, 32), info, blockLight));
      }

      if (VoxelReader.DEBUG && !baker.reader.missing.isEmpty()) {
         StringBuilder sb = new StringBuilder();

         for (long m : baker.reader.missing) {
            int cx = (int)(m >> 32);
            int cz = (int)m;
            sb.append(' ').append(cx).append(',').append(cz).append(source.chunk(cx, cz) == null ? "(still missing)" : "(present now)");
         }

         Diorama.LOGGER.info("Partial bake of {} ({} blocks) hit unknown chunks:{}", new Object[]{key, blocks.cardinality(), sb});
      }

      return TileImage.partial(key, patches);
   }

   public static int[] reference(TileKey key, ChunkSource source, Looks looks, boolean fogOfWar, @Nullable Overlays overlays, byte @Nullable [] info) {
      TileBaker baker = new TileBaker(key, source, looks, fogOfWar, overlays);
      baker.fourSamples = true;
      int size = 512;
      int[] pixels = new int[size * size];
      baker.bakePixels(0, 0, size, size, pixels, info, null, null);
      return pixels;
   }

   public static TileImage gpuImage(
      TileKey key, int[] pixels, int @Nullable [] saved, byte @Nullable [] info, byte @Nullable [] pixelLight, byte @Nullable [] savedLight
   ) {
      int size = 512;
      byte[] blockLight = pixelLight == null ? null : BlockLight.shrink(pixelLight, pixels, saved, savedLight, 0, 0, size);
      merge(pixels, saved, 0, 0, size);
      return TileImage.full(key, Mipmaps.build(pixels, size), coverage(pixels, size, size), info).withBlockLight(blockLight);
   }

   public static TileImage image(TileKey key, int[] pixels) {
      int size = 512;
      return TileImage.full(key, Mipmaps.build(pixels, size), coverage(pixels, size, size));
   }

   private static void merge(int[] pixels, int @Nullable [] saved, int px0, int py0, int edge) {
      if (saved != null) {
         int size = 512;

         for (int y = 0; y < edge; y++) {
            for (int x = 0; x < edge; x++) {
               int i = y * edge + x;
               int old = saved[(py0 + y) * size + px0 + x];
               if (old >>> 24 > pixels[i] >>> 24) {
                  pixels[i] = old;
               }
            }
         }
      }
   }

   private void bakePixels(int px0, int py0, int width, int height, int[] out, byte @Nullable [] info, float @Nullable [] linear, byte @Nullable [] blockLight) {
      if (this.startY > this.limitY) {
         int border = this.style.outline() > 0.0F ? 1 : 0;
         float edge = outlineDepth(this.depthLook);
         int w = width + 2 * border;
         int h = height + 2 * border;
         int[] pixels = border == 0 ? out : new int[w * h];
         float[] depth = border == 0 ? null : new float[w * h];
         float[] water = border == 0 ? null : new float[w * h];
         double minU = this.key.minU();
         double minV = this.key.minV();
         double unitsPerPixel = 0.25;
         double[][] samples = fast && !this.fourSamples ? FAST_SAMPLES : SAMPLES;

         for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
               float r = 0.0F;
               float g = 0.0F;
               float b = 0.0F;
               float d = 0.0F;
               float wd = 0.0F;
               float ur = 0.0F;
               float ug = 0.0F;
               float ub = 0.0F;
               int lightSum = 0;
               int known = 0;

               for (int s = 0; s < samples.length; s++) {
                  double u = minU + (px0 + x - border + samples[s][0]) * unitsPerPixel;
                  double v = minV + (py0 + y - border + samples[s][1]) * unitsPerPixel;
                  this.surfaceLight = -1;
                  this.outWater = 0.0F;
                  if (this.trace(u, v, s)) {
                     if (linear != null) {
                        float[] lit = this.unlit;
                        float fog = 0.0F;
                        if (this.surfaceLight == -1) {
                           Arrays.fill(lit, 1.0F);
                        } else {
                           TileSurface.caveLightOf(this.surfaceLight, this.surfaceLight & 15, lit);
                           fog = TileSurface.fog(this.surfaceLight);
                        }

                        ur += (this.outR - Lighting.CAVE_FOG[0] * fog) / Math.max(0.001F, lit[0]);
                        ug += (this.outG - Lighting.CAVE_FOG[1] * fog) / Math.max(0.001F, lit[1]);
                        ub += (this.outB - Lighting.CAVE_FOG[2] * fog) / Math.max(0.001F, lit[2]);
                     }

                     if (s == 0 && info != null && this.hitSolid) {
                        int ix = x - border;
                        int iy = y - border;
                        if (ix >= 0 && iy >= 0 && ix < width && iy < height) {
                           info[iy * width + ix] = (byte)this.overlayBits();
                        }
                     }

                     r += this.outR;
                     g += this.outG;
                     b += this.outB;
                     d += this.outDepth;
                     wd += this.outWater;
                     lightSum += this.outBlockLight;
                     known++;
                  }
               }

               if (known > 0) {
                  pixels[y * w + x] = this.abgr(r / known, g / known, b / known, known * 255 / samples.length);
                  int ix = x - border;
                  int iy = y - border;
                  if (blockLight != null && ix >= 0 && iy >= 0 && ix < width && iy < height) {
                     blockLight[iy * width + ix] = (byte)BlockLight.encode(lightSum, known);
                  }

                  if (linear != null && ix >= 0 && iy >= 0 && ix < width && iy < height) {
                     int i = (iy * width + ix) * 4;
                     linear[i] = ur / known;
                     linear[i + 1] = ug / known;
                     linear[i + 2] = ub / known;
                  }
               }

               if (depth != null) {
                  depth[y * w + x] = known > 0 ? d / known : Float.NaN;
                  water[y * w + x] = known > 0 ? wd / known : 0.0F;
               }
            }
         }

         if (border != 0) {
            for (int y = 0; y < height; y++) {
               for (int x = 0; x < width; x++) {
                  int i = (y + 1) * w + x + 1;
                  int pixel = pixels[i];
                  float here = depth[i];
                  if (pixel != 0
                     && (
                        nearer(depth[i - 1], here, edge)
                           || nearer(depth[i + 1], here, edge)
                           || nearer(depth[i - w], here, edge)
                           || nearer(depth[i + w], here, edge)
                     )) {
                     float outline = (float)Math.exp(-water[i] / 6.0F);
                     pixel = darken(pixel, 1.0F - this.style.outline() * outline);
                     if (linear != null) {
                        linear[(y * width + x) * 4 + 3] = outline;
                     }
                  }

                  out[y * width + x] = pixel;
               }
            }
         }
      }
   }

   private int overlayBits() {
      int bits = 0;
      if ((this.hitX & 15) == 0 && this.hitFx < 0.25) {
         bits |= 1;
      }

      if (this.hitTop && (this.hitZ & 15) == 0 && this.hitFz < 1.0 / (4.0 * this.sin)) {
         bits |= 2;
      }

      Long seed = this.overlays.slimeSeed();
      if (seed != null) {
         int cx = this.hitX >> 4;
         int cz = this.hitZ >> 4;
         Facing facing = Projection.facing();
         if (this.slimeChunks
            .computeIfAbsent((long)cx << 32 | cz & 4294967295L, k -> Overlays.slimeChunk(seed, facing.worldBlockX(cx, cz), facing.worldBlockZ(cx, cz)))) {
            bits |= 4;
         }
      }

      if (this.overlays.spawnSpots()
         && this.hitTop
         && this.looks.spawnOn[this.hitId]
         && this.hitFx >= 0.3
         && this.hitFx < 0.7
         && this.hitFz >= 0.3
         && this.hitFz < 0.7) {
         int feet = this.reader.state(this.hitX, this.hitY + 1, this.hitZ);
         int head = this.reader.state(this.hitX, this.hitY + 2, this.hitZ);
         if (feet != -1
            && head != -1
            && this.looks.spawnIn[feet]
            && this.looks.spawnIn[head]
            && this.reader.blockLight(this.hitX, this.hitY + 1, this.hitZ) == 0) {
            int sky = this.reader.skyLight(this.hitX, this.hitY + 1, this.hitZ);
            bits |= sky <= 7 ? 16 : 8;
         }
      }

      return bits;
   }

   static boolean nearer(float neighbour, float here) {
      return nearer(neighbour, here, 1.5F);
   }

   private static boolean nearer(float neighbour, float here, float edge) {
      return here - neighbour > edge;
   }

   public static float outlineDepth(boolean depthLook) {
      return depthLook ? 3.0F : 1.5F;
   }

   static int darken(int abgr, float keep) {
      int r = Math.round((abgr & 0xFF) * keep);
      int g = Math.round((abgr >> 8 & 0xFF) * keep);
      int b = Math.round((abgr >> 16 & 0xFF) * keep);
      return abgr & 0xFF000000 | b << 16 | g << 8 | r;
   }

   private boolean trace(double u, double v, int sample) {
      double sin = this.sin;
      double cos = this.cos;
      int x = (int)Math.floor(u);
      double fx = u - x;
      int top = this.tall == null ? this.startY : this.tall.rayStart(x, v, sin, cos);
      double y0 = top;
      double z0 = (v + y0 * cos) / sin;
      int by = top - 1;
      int bz = (int)Math.floor(z0);
      double tMaxY = (y0 - by) / sin;
      double tMaxZ = (z0 - bz) / cos;
      double tDeltaY = 1.0 / sin;
      double tDeltaZ = 1.0 / cos;
      double t = 0.0;
      boolean enteredTop = true;
      boolean skippingRock = this.lit;
      this.hitSolid = false;
      this.outBlockLight = 0;
      float overR = 0.0F;
      float overG = 0.0F;
      float overB = 0.0F;
      float through = 1.0F;
      int waterId = -1;
      double waterT = 0.0;
      int waterBy = 0;
      int waterBz = 0;

      while (by >= this.limitY && (!skippingRock || by >= this.rockLimit)) {
         boolean cutAway = this.caveCut != null && by >= this.caveCut.at(x, bz);
         skippingRock |= cutAway;
         if (skippingRock) {
            int solid = cutAway ? 1 : this.reader.solid(x, by, bz);
            if (solid == -1) {
               return false;
            }

            if (solid > 0 || this.lifted && by + 1 < this.startY && this.reader.pocket(x, by, bz)) {
               if (tMaxY < tMaxZ) {
                  by--;
                  t = tMaxY;
                  tMaxY += tDeltaY;
                  enteredTop = true;
               } else {
                  bz--;
                  t = tMaxZ;
                  tMaxZ += tDeltaZ;
                  enteredTop = false;
               }
               continue;
            }

            skippingRock = false;
         }

         int id = this.reader.state(x, by, bz);
         if (id == -1) {
            if (!this.passesMissing(by) && !TileSpots.passedWhenSaved(this.key, this.savedSpots, x, by, bz)) {
               return false;
            }

            id = VoxelReader.AIR_ID;
         }

         byte kind = this.looks.kind[id];
         int shape = kind != 1 && kind != 4 ? 0 : this.looks.shape[id];
         boolean partial = shape != 0;
         if (partial) {
            double tIn = t;
            boolean hit = this.enterShape(this.looks.shapes[shape], fx, y0, z0, by, bz, t, Math.min(tMaxY, tMaxZ), enteredTop);
            int wet = this.looks.wetShape[id];
            if (wet >= 0 && waterId < 0 && (!hit || this.shapeT > tIn)) {
               waterId = wet;
               waterT = tIn;
               waterBy = by;
               waterBz = bz;
            }

            if (hit) {
               t = this.shapeT;
               enteredTop = this.shapeTop;
            } else {
               kind = 0;
            }
         }

         switch (kind) {
            case 1:
            case 3:
               if (!this.lifted && !this.depthLook || kind != 1 || !this.reader.ceilingUnderside(x, by, bz, this.lid(x, bz), this.limitY)) {
                  if (!this.fogOfWar || this.source.isSeen(x, by, bz) || (enteredTop ? this.source.isSeen(x, by + 1, bz) : this.source.isSeen(x, by, bz + 1))) {
                     if (this.surfaceOnly) {
                        if (waterId >= 0) {
                           this.landedOnWater(waterId, x, waterBy, waterBz, fx, y0 - waterT * sin, z0 - waterT * cos);
                        } else {
                           this.landed(id, x, by, bz, fx, y0 - t * sin, z0 - t * cos, enteredTop, partial);
                        }

                        return true;
                     }

                     double hitY = y0 - t * sin;
                     double hitZ = z0 - t * cos;
                     this.hitSolid = kind == 1;
                     this.hitId = id;
                     this.hitTop = enteredTop;
                     this.hitX = x;
                     this.hitY = by;
                     this.hitZ = bz;
                     this.hitFx = fx;
                     this.hitFz = hitZ - bz;
                     this.outDepth = (float)(-(sin * hitY + cos * hitZ) - this.depthOrigin);
                     this.shade(id, x, by, bz, fx, hitY, hitZ, enteredTop, partial, sample);
                     if (through < 1.0F) {
                        float light = luminance(this.outR, this.outG, this.outB) / Math.max(0.05F, luminance(this.albedoR, this.albedoG, this.albedoB));
                        this.outR = this.outR * through + overR * light;
                        this.outG = this.outG * through + overG * light;
                        this.outB = this.outB * through + overB * light;
                     }

                     if (waterId >= 0) {
                        this.mixWater(waterId, x, waterBy, waterBz, u, y0, z0, waterT, t, sample);
                        if (this.cave) {
                           this.surfaceLight = this.waterLight(x, waterBy, waterBz, y0 - waterT * sin);
                        }
                     }

                     return true;
                  }

                  return false;
               }
               break;
            case 2:
               if (waterId < 0) {
                  waterId = id;
                  waterT = t;
                  waterBy = by;
                  waterBz = bz;
               }
               break;
            case 4:
            case 5:
               if (!this.surfaceOnly) {
                  float w = kind == 5 ? 0.4F : 0.25F;
                  this.albedo(id, x, bz, true);
                  overR += through * w * this.albedoR;
                  overG += through * w * this.albedoG;
                  overB += through * w * this.albedoB;
                  through *= 1.0F - w;
               }
         }

         if (tMaxY < tMaxZ) {
            by--;
            t = tMaxY;
            tMaxY += tDeltaY;
            enteredTop = true;
         } else {
            bz--;
            t = tMaxZ;
            tMaxZ += tDeltaZ;
            enteredTop = false;
         }
      }

      if (!this.fogOfWar || !skippingRock && waterId >= 0 && this.source.isSeen(x, waterBy, waterBz)) {
         if (this.surfaceOnly) {
            if (waterId >= 0) {
               this.landedOnWater(waterId, x, waterBy, waterBz, fx, y0 - waterT * sin, z0 - waterT * cos);
            }

            return true;
         } else {
            this.outDepth = (float)(-(sin * (y0 - t * sin) + cos * (z0 - t * cos)) - this.depthOrigin);
            if (skippingRock) {
               float[] cut = this.depthLook ? Lighting.CAVE_CUT : Lighting.CAVE_CUT_DARK;
               this.outR = cut[0];
               this.outG = cut[1];
               this.outB = cut[2];
            } else if (waterId >= 0) {
               this.outR = 0.0F;
               this.outG = 0.0F;
               this.outB = 0.0F;
               this.mixWater(waterId, x, waterBy, waterBz, u, y0, z0, waterT, waterT + 64.0, sample);
               if (this.cave) {
                  this.surfaceLight = this.waterLight(x, waterBy, waterBz, y0 - waterT * sin);
               }
            } else {
               this.outR = 0.01F;
               this.outG = 0.01F;
               this.outB = 0.015F;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   void albedo(int id, int x, int z, boolean top) {
      Looks l = this.looks;
      int base = top ? l.top[id] : l.side[id];
      int tinted = top ? l.topTinted[id] : l.sideTinted[id];
      float w = top ? l.topTintWeight[id] : l.sideTintWeight[id];
      int tint = this.tint(id, x, z);
      float bw = 1.0F - w;
      this.albedoR = SRGB_TO_LINEAR[base >> 16 & 0xFF] * bw + SRGB_TO_LINEAR[tinted >> 16 & 0xFF] * SRGB_TO_LINEAR[tint >> 16 & 0xFF] * w;
      this.albedoG = SRGB_TO_LINEAR[base >> 8 & 0xFF] * bw + SRGB_TO_LINEAR[tinted >> 8 & 0xFF] * SRGB_TO_LINEAR[tint >> 8 & 0xFF] * w;
      this.albedoB = SRGB_TO_LINEAR[base & 0xFF] * bw + SRGB_TO_LINEAR[tinted & 0xFF] * SRGB_TO_LINEAR[tint & 0xFF] * w;
   }

   private int tint(int id, int x, int z) {
      byte kind = this.looks.tint[id];
      if (kind == 0) {
         int constant = this.looks.constantTint[id];
         return constant < 0 ? 16777215 : constant;
      }

      ChunkSnapshot snapshot = this.reader.snapshot(x, z);
      if (snapshot == null) {
         return 16777215;
      }

      int i = (z & 15) << 4 | x & 15;
      ChunkSnapshot.Tints tints = BiomeTints.of(snapshot);

      return switch (kind) {
         case 1 -> tints.grass()[i];
         case 2 -> tints.foliage()[i];
         case 3 -> tints.dryFoliage()[i];
         case 4 -> tints.water()[i];
         default -> 16777215;
      };
   }

   private void shade(int id, int x, int by, int bz, double fx, double hitY, double hitZ, boolean top, boolean partial, int sample) {
      this.albedo(id, x, bz, top);
      float variation = this.style.blockVariation();
      if (variation > 0.0F) {
         float nudge = 1.0F + variation * blockNoise(x, by, bz);
         this.albedoR *= nudge;
         this.albedoG *= nudge;
         this.albedoB *= nudge;
      }

      int frontY = top ? by + 1 : by;
      int frontZ = top ? bz : bz + 1;
      float ao = top ? this.ambientOcclusionTop(x, by + 1, bz, fx, hitZ - bz) : this.ambientOcclusionSouth(x, by, bz + 1, fx, hitY - by);
      float emission = this.looks.emission[id] / 15.0F;
      if (this.looks.kind[id] == 3) {
         float crust = 1.0F + (this.lifted ? 0.1F : 0.3F) * blockNoise(x, by, bz);
         this.albedoR *= crust;
         this.albedoG *= crust * crust;
         this.albedoB *= crust;
      }

      float aoSun = Math.max(0.0F, 1.0F - this.style.cornerOcclusion() * (1.0F - ao));
      float aoSky = Math.min(ao, aoSun);
      if (this.lifted) {
         double[] j = SUN_JITTER[sample];
         double sx = this.sunX + j[0];
         double sy = this.sunY + j[1];
         double sz = this.sunZ + j[2];
         double lambert = top ? 1.0 : Math.max(0.0, sz) / sy;
         float direct = 0.0F;
         if (lambert > 0.0) {
            double ox = x + fx;
            double oy = top ? by + 1 + 1.0E-4 : hitY;
            double oz = top ? hitZ : bz + 1 + 1.0E-4;
            direct = this.inShadow(ox, oy, oz, sx, sy, sz) ? 0.0F : (float)(0.52F * lambert) * aoSun;
         }

         float sky = top ? this.skyTop(x, by + 1, bz, fx, hitZ - bz) : this.skySouth(x, by, bz + 1, fx, hitY - by);
         float ambient = 0.26F * aoSky * (1.0F - this.style.skyOcclusion() * (1.0F - (0.15F + 0.85F * sky)));
         float warm = (float)Math.pow(this.reader.blockLight(x, frontY, frontZ) / 15.0F, 1.5) * 0.7F;
         float lr = ambient * LID_FILL_TINT[0] + direct * LID_KEY_TINT[0] + warm * 0.9F * ao;
         float lg = ambient * LID_FILL_TINT[1] + direct * LID_KEY_TINT[1] + warm * 0.75F * ao;
         float lb = ambient * LID_FILL_TINT[2] + direct * LID_KEY_TINT[2] + warm * 0.5F * ao;
         if (emission > 0.0F) {
            float e = this.lifted && this.looks.kind[id] != 3 ? 0.3F + emission * 0.45F : 0.35F + emission * 0.85F;
            lr = Math.max(lr, e);
            lg = Math.max(lg, e);
            lb = Math.max(lb, e);
         }

         if (!top) {
            lr *= 0.82F;
            lg *= 0.82F;
            lb *= 0.82F;
         }

         this.outR = this.albedoR * lr;
         this.outG = this.albedoG * lg;
         this.outB = this.albedoB * lb;
         if (this.lifted) {
            double below = this.source.roof() - 30 - hitY;
            float f = below <= 0.0 ? 0.0F : (float)Math.min(0.72F, 1.0 - Math.exp(-below / 55.0));
            this.hazeColor(x + fx, hitY, hitZ);
            this.outR = this.outR + (this.hazeR - this.outR) * f;
            this.outG = this.outG + (this.hazeG - this.outG) * f;
            this.outB = this.outB + (this.hazeB - this.outB) * f;
         }
      } else if (this.lit) {
         int blockLight = this.reader.blockLight(x, frontY, frontZ);
         int skyLight = this.reader.skyLight(x, frontY, frontZ);
         float fade = !this.depthLook
            ? 1.0F - Math.min(0.5F, (float)(this.startY - hitY) / 60.0F)
            : this.caveFade(hitY) * (top ? 1.0F : (float)Math.exp(-this.wallDepth(x, by, bz) / 4.0F));
         float slate = this.depthLook ? 1.0F - fade : 0.0F;
         Lighting.cave(blockLight, skyLight, ao, fade, emission, top ? 1.0F : 0.82F, this.light);
         this.outR = this.albedoR * this.light[0] + Lighting.CAVE_FOG[0] * slate;
         this.outG = this.albedoG * this.light[1] + Lighting.CAVE_FOG[1] * slate;
         this.outB = this.albedoB * this.light[2] + Lighting.CAVE_FOG[2] * slate;
         this.surfaceLight = TileSurface.caveLight(blockLight, skyLight, ao, fade, this.looks.emission[id]) | (this.depthLook ? 536870912 : 0);
      } else {
         double[] j = SUN_JITTER[sample];
         double sx = this.sunX + j[0];
         double sy = this.sunY + j[1];
         double sz = this.sunZ + j[2];
         double lambert = top ? 1.0 : Math.max(0.0, sz) / sy;
         boolean shadowed = lambert > 0.0
            && this.inShadow(x + fx, top ? (partial ? hitY : by + 1) + 1.0E-4 : hitY, top ? hitZ : (partial ? hitZ : bz + 1) + 1.0E-4, sx, sy, sz);
         float sky = this.style.skyOcclusion() > 0.0F ? (top ? this.skyTop(x, by + 1, bz, fx, hitZ - bz) : this.skySouth(x, by, bz + 1, fx, hitY - by)) : 1.0F;
         this.lighting.surface(lambert, shadowed, ao, sky, emission, top ? 1.0F : 0.82F, hitY, this.light);
         this.outBlockLight = Math.max(this.reader.blockLight(x, frontY, frontZ), this.looks.emission[id]);
         this.outR = this.albedoR * this.light[0];
         this.outG = this.albedoG * this.light[1];
         this.outB = this.albedoB * this.light[2];
      }
   }

   private void mixWater(int waterId, int x, int by, int bz, double u, double y0, double z0, double tEntry, double tHit, int sample) {
      this.albedo(waterId, x, bz, true);
      float wr = this.albedoR;
      float wg = this.albedoG;
      float wb = this.albedoB;
      float light;
      if (this.lit) {
         light = Lighting.caveWater(this.reader.blockLight(x, by + 1, bz), this.reader.skyLight(x, by + 1, bz))
            * (this.depthLook ? this.caveFade(y0 - tEntry * this.sin) : 1.0F);
      } else {
         double sy = this.sunY + SUN_JITTER[sample][1];
         double ox = u;
         double oy = y0 - tEntry * this.sin + 1.0E-4;
         double oz = z0 - tEntry * this.cos;
         boolean shadow = this.inShadow(ox, oy, oz, this.sunX, sy, this.sunZ);
         float ambient = this.style.ambient();
         float occlusion = this.style.skyOcclusion();
         if (occlusion > 0.0F) {
            float sky = this.skyTop(x, by + 1, bz, u - x, oz - bz);
            ambient *= 1.0F - occlusion * (1.0F - (0.15F + 0.85F * sky));
         }

         light = ambient + (shadow ? 0.0F : this.style.sunStrength());
         float heightShade = this.style.heightShade();
         if (heightShade != 0.0F) {
            light *= 1.0F + heightShade * (float)Math.max(-1.0, Math.min(1.0, (by + 1 - 64) / 64.0));
         }
      }

      double depth = Math.max(0.0, tHit - tEntry);
      this.outWater = (float)depth;
      if (this.style.clearWater()) {
         float peak = Math.max(1.0E-4F, Math.max(wr, Math.max(wg, wb)));
         float tr = (float)Math.exp(-depth * (0.45 * (1.0F - wr / peak) + 0.16));
         float tg = (float)Math.exp(-depth * (0.45 * (1.0F - wg / peak) + 0.16));
         float tb = (float)Math.exp(-depth * (0.45 * (1.0F - wb / peak) + 0.16));
         float deep = light * (float)(0.3 + 0.7 * Math.exp(-depth / 18.0));
         float l = luminance(wr, wg, wb);
         float deepR = Math.max(0.0F, l + (wr - l) * 1.2F) * deep;
         float deepG = Math.max(0.0F, l + (wg - l) * 1.2F) * deep;
         float deepB = Math.max(0.0F, l + (wb - l) * 1.2F) * deep;
         this.outR = this.outR * tr + deepR * (1.0F - tr);
         this.outG = this.outG * tg + deepG * (1.0F - tg);
         this.outB = this.outB * tb + deepB * (1.0F - tb);
      } else {
         float a = (float)Math.max(0.5, Math.min(0.94, 1.0 - Math.exp(-0.2 * depth)));
         this.outR = this.outR * (1.0F - a) + wr * light * a;
         this.outG = this.outG * (1.0F - a) + wg * light * a;
         this.outB = this.outB * (1.0F - a) + wb * light * a;
      }
   }

   private boolean inShadow(double ox, double oy, double oz, double dx, double dy, double dz) {
      return this.blocked(ox, oy, oz, dx, dy, dz, 40.0, 96, true);
   }

   private float skyTop(int x, int y, int z, double fx, double fz) {
      return bilinear(this.skyAt(x, y, z), this.skyAt(x + 1, y, z), this.skyAt(x, y, z + 1), this.skyAt(x + 1, y, z + 1), fx, fz);
   }

   private float skySouth(int x, int y, int z, double fx, double fy) {
      return bilinear(this.skyAt(x, y + 1, z), this.skyAt(x + 1, y + 1, z), this.skyAt(x, y, z), this.skyAt(x + 1, y, z), fx, 1.0 - fy);
   }

   float skyAt(int x, int y, int z) {
      long key = (long)(x & 67108863) << 38 | (long)(z & 67108863) << 12 | y + 2048 & 4095;
      float cached = this.skyAtCorner.getOrDefault(key, -1.0F);
      if (cached >= 0.0F) {
         return cached;
      }

      double open = 0.0;
      double total = 0.0;

      for (int i = 0; i < SKY_RAYS.length; i++) {
         double[] d = SKY_RAYS[i];
         if (!this.blocked(x + d[0] * 0.001, y + d[1] * 0.001, z + d[2] * 0.001, d[0], d[1], d[2], 12.0, 36, false)) {
            open += SKY_WEIGHTS[i];
         }

         total += SKY_WEIGHTS[i];
      }

      float sky = (float)(open / total);
      this.skyAtCorner.put(key, sky);
      return sky;
   }

   private boolean blocked(double ox, double oy, double oz, double dx, double dy, double dz, double distance, int steps, boolean shapes) {
      double ix = 1.0 / dx;
      double iy = 1.0 / dy;
      double iz = 1.0 / dz;
      int cx = (int)Math.floor(ox);
      int cy = (int)Math.floor(oy);
      int cz = (int)Math.floor(oz);
      int start = cy >= this.rayTop || this.lifted && cy >= this.lid(cx, cz) ? 0 : this.reader.solid(cx, cy, cz);
      if (start != 1 && (start != 2 || shapes && !this.meetsShape(cx, cy, cz, ox, oy, oz, dx, dy, dz, ix, iy, iz))) {
         int top = this.lifted ? Math.min(this.rayTop, this.startY) : this.rayTop;
         if (dy > 0.0) {
            double ex = ox + dx * distance;
            double ez = oz + dz * distance;
            top = Math.min(
               top,
               this.reader
                  .solidTop(
                     (int)Math.floor(Math.min(ox, ex)) - 1 >> 4,
                     (int)Math.floor(Math.min(oz, ez)) - 1 >> 4,
                     (int)Math.floor(Math.max(ox, ex)) + 1 >> 4,
                     (int)Math.floor(Math.max(oz, ez)) + 1 >> 4
                  )
            );
         }

         int stepX = dx > 0.0 ? 1 : -1;
         int stepY = dy > 0.0 ? 1 : -1;
         int stepZ = dz > 0.0 ? 1 : -1;
         double tDeltaX = dx == 0.0 ? Double.MAX_VALUE : Math.abs(ix);
         double tDeltaY = dy == 0.0 ? Double.MAX_VALUE : Math.abs(iy);
         double tDeltaZ = dz == 0.0 ? Double.MAX_VALUE : Math.abs(iz);
         double tMaxX = dx == 0.0 ? Double.MAX_VALUE : (dx > 0.0 ? cx + 1 - ox : ox - cx) * tDeltaX;
         double tMaxY = dy == 0.0 ? Double.MAX_VALUE : (dy > 0.0 ? cy + 1 - oy : oy - cy) * tDeltaY;
         double tMaxZ = dz == 0.0 ? Double.MAX_VALUE : (dz > 0.0 ? cz + 1 - oz : oz - cz) * tDeltaZ;

         for (int i = 0; i < steps; i++) {
            if (Math.min(tMaxX, Math.min(tMaxY, tMaxZ)) > distance) {
               return false;
            }

            if (tMaxX < tMaxY && tMaxX < tMaxZ) {
               cx += stepX;
               tMaxX += tDeltaX;
            } else if (tMaxY < tMaxZ) {
               cy += stepY;
               tMaxY += tDeltaY;
            } else {
               cz += stepZ;
               tMaxZ += tDeltaZ;
            }

            if (cy >= top) {
               return false;
            }

            int solid = this.reader.solid(cx, cy, cz);
            if (solid == -1) {
               return false;
            }

            if ((!this.lifted || solid <= 0 || cy < this.lid(cx, cz))
               && (solid == 1 || solid == 2 && (!shapes || this.meetsShape(cx, cy, cz, ox, oy, oz, dx, dy, dz, ix, iy, iz)))) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private boolean meetsShape(int cx, int cy, int cz, double ox, double oy, double oz, double dx, double dy, double dz, double ix, double iy, double iz) {
      int id = this.reader.state(cx, cy, cz);
      int shape = id < 0 ? 0 : this.looks.shape[id];
      if (shape == 0) {
         return true;
      }

      float[] boxes = this.looks.shapes[shape];
      double px = ox - cx;
      double py = oy - cy;
      double pz = oz - cz;

      for (int i = 0; i < boxes.length; i += 6) {
         double near = 0.0;
         double far = Double.MAX_VALUE;

         for (int axis = 0; axis < 3 && near <= far; axis++) {
            double p = axis == 0 ? px : (axis == 1 ? py : pz);
            double d = axis == 0 ? dx : (axis == 1 ? dy : dz);
            double inv = axis == 0 ? ix : (axis == 1 ? iy : iz);
            double min = boxes[i + axis];
            double max = boxes[i + 3 + axis];
            if (d == 0.0) {
               if (p < min || p > max) {
                  far = -1.0;
               }
            } else {
               double t0 = (min - p) * inv;
               double t1 = (max - p) * inv;
               near = Math.max(near, Math.min(t0, t1));
               far = Math.min(far, Math.max(t0, t1));
            }
         }

         if (near <= far) {
            return true;
         }
      }

      return false;
   }

   private boolean enterShape(float[] boxes, double fx, double y0, double z0, int by, int bz, double tIn, double tOut, boolean inTop) {
      double best = Double.MAX_VALUE;

      for (int i = 0; i < boxes.length; i += 6) {
         if (!(fx < boxes[i]) && !(fx >= boxes[i + 3])) {
            double tTop = (y0 - by - boxes[i + 4]) * this.invSin;
            double tSouth = (z0 - bz - boxes[i + 5]) * this.invCos;
            double enter = Math.max(tIn, Math.max(tTop, tSouth));
            double exit = Math.min(tOut, Math.min((y0 - by - boxes[i + 1]) * this.invSin, (z0 - bz - boxes[i + 2]) * this.invCos));
            if (enter < exit && enter < best) {
               best = enter;
               this.shapeTop = enter == tIn ? inTop : tTop >= tSouth;
            }
         }
      }

      this.shapeT = best;
      return best != Double.MAX_VALUE;
   }

   boolean occludes(int x, int y, int z) {
      return this.reader.solid(x, y, z) > 0;
   }

   private float ambientOcclusionTop(int x, int y, int z, double fx, double fz) {
      boolean n = this.occludes(x, y, z - 1);
      boolean s = this.occludes(x, y, z + 1);
      boolean w = this.occludes(x - 1, y, z);
      boolean e = this.occludes(x + 1, y, z);
      float nw = corner(w, n, this.occludes(x - 1, y, z - 1));
      float ne = corner(e, n, this.occludes(x + 1, y, z - 1));
      float sw = corner(w, s, this.occludes(x - 1, y, z + 1));
      float se = corner(e, s, this.occludes(x + 1, y, z + 1));
      return bilinear(nw, ne, sw, se, fx, fz);
   }

   private float ambientOcclusionSouth(int x, int y, int z, double fx, double fy) {
      boolean up = this.occludes(x, y + 1, z);
      boolean down = this.occludes(x, y - 1, z);
      boolean w = this.occludes(x - 1, y, z);
      boolean e = this.occludes(x + 1, y, z);
      float tw = corner(w, up, this.occludes(x - 1, y + 1, z));
      float te = corner(e, up, this.occludes(x + 1, y + 1, z));
      float bw = corner(w, down, this.occludes(x - 1, y - 1, z));
      float be = corner(e, down, this.occludes(x + 1, y - 1, z));
      return bilinear(tw, te, bw, be, fx, 1.0 - fy);
   }

   static float corner(boolean side1, boolean side2, boolean diagonal) {
      return side1 && side2 ? AO_LEVELS[0] : AO_LEVELS[3 - (side1 ? 1 : 0) - (side2 ? 1 : 0) - (diagonal ? 1 : 0)];
   }

   private static float bilinear(float a, float b, float c, float d, double fx, double fy) {
      float top = (float)(a + (b - a) * fx);
      float bottom = (float)(c + (d - c) * fx);
      return (float)(top + (bottom - top) * fy);
   }

   private static float luminance(float r, float g, float b) {
      return 0.2126F * r + 0.7152F * g + 0.0722F * b;
   }

   static float blockNoise(int x, int y, int z) {
      int h = x * 73856093 ^ y * 19349663 ^ z * 83492791;
      h ^= h >>> 13;
      h *= 1540483477;
      h ^= h >>> 15;
      return (h & 65535) / 32767.5F - 1.0F;
   }

   private int abgr(float r, float g, float b, int alpha) {
      return abgr(this.style, r, g, b, alpha);
   }

   static int abgr(MapStyle style, float r, float g, float b, int alpha) {
      float saturation = style.saturation();
      float exposure = style.exposure();
      float l = luminance(r, g, b);
      r = (l + (r - l) * saturation) * exposure;
      g = (l + (g - l) * saturation) * exposure;
      b = (l + (b - l) * saturation) * exposure;
      int ri = LINEAR_TO_SRGB[Math.min(4095, Math.max(0, (int)(r * 4095.0F)))];
      int gi = LINEAR_TO_SRGB[Math.min(4095, Math.max(0, (int)(g * 4095.0F)))];
      int bi = LINEAR_TO_SRGB[Math.min(4095, Math.max(0, (int)(b * 4095.0F)))];
      return alpha << 24 | bi << 16 | gi << 8 | ri;
   }

   private static byte[] coverage(int[] pixels, int width, int height) {
      int cellPixels = 8;
      int cellsX = width / cellPixels;
      int cellsY = height / cellPixels;
      byte[] out = new byte[cellsX * cellsY];

      for (int cy = 0; cy < cellsY; cy++) {
         for (int cx = 0; cx < cellsX; cx++) {
            int sum = 0;

            for (int y = 0; y < cellPixels; y++) {
               int row = (cy * cellPixels + y) * width + cx * cellPixels;

               for (int x = 0; x < cellPixels; x++) {
                  sum += pixels[row + x] >>> 24;
               }
            }

            out[cy * cellsX + cx] = (byte)(sum / (cellPixels * cellPixels));
         }
      }

      return out;
   }

   static {
      int ring = 6;
      SKY_RAYS = new double[ring * 2 + 1][];
      SKY_WEIGHTS = new double[ring * 2 + 1];
      double[] elevations = new double[]{Math.toRadians(25.0), Math.toRadians(55.0)};

      for (int r = 0; r < 2; r++) {
         for (int i = 0; i < ring; i++) {
            double azimuth = (i + r * 0.5) * 2.0 * Math.PI / ring;
            double e = elevations[r];
            SKY_RAYS[r * ring + i] = new double[]{Math.cos(e) * Math.cos(azimuth), Math.sin(e), Math.cos(e) * Math.sin(azimuth)};
            SKY_WEIGHTS[r * ring + i] = Math.sin(e);
         }
      }

      SKY_RAYS[ring * 2] = new double[]{0.0, 1.0, 0.0};
      SKY_WEIGHTS[ring * 2] = 1.5;
      AO_LEVELS = new float[]{0.6F, 0.74F, 0.87F, 1.0F};
      SHADOW_REACH_X = (int)Math.ceil(40.0 * Math.cos(Math.toRadians(25.0)) * 0.878) + 1;
      SHADOW_REACH_Z = (int)Math.ceil(40.0 * Math.cos(Math.toRadians(25.0)) * 0.479) + 1;
      SHADOW_REACH_Y = (int)Math.ceil(40.0 * Math.sin(Math.toRadians(60.0))) + 1;
      SRGB_TO_LINEAR = new float[256];
      LINEAR_TO_SRGB = new int[4096];

      for (int i = 0; i < 256; i++) {
         double v = i / 255.0;
         SRGB_TO_LINEAR[i] = (float)(v <= 0.04045 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4));
      }

      for (int i = 0; i < 4096; i++) {
         double v = i / 4095.0;
         double s = v <= 0.0031308 ? v * 12.92 : 1.055 * Math.pow(v, 0.4166666666666667) - 0.055;
         LINEAR_TO_SRGB[i] = (int)Math.round(Math.max(0.0, Math.min(1.0, s)) * 255.0);
      }
   }
}
