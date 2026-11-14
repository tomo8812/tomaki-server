package dev.diorama.render;

import dev.diorama.Diorama;
import dev.diorama.MapStyle;
import dev.diorama.bake.TileBaker;
import dev.diorama.bake.TileKey;
import dev.diorama.render.gpu.FogLayer;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.Nullable;

public final class MapViewport {
   private static final int MASK = 256;
   private static final double MASK_MARGIN = 24.0;
   public static final double DETAILED_ZOOM = 0.5;
   private @Nullable MapTexture maskTexture;
   private final int[] maskPixels = new int[65536];
   private @Nullable MapTexture detailTexture;
   private final int[] detailPixels = new int[65536];
   private final byte[] softBytes = new byte[65536];
   private MapViewport.@Nullable MaskInputs maskInputs;
   private WeakReference<MapLevel> maskLevel = new WeakReference<>(null);
   private static final ExecutorService MASK_WORKER = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "diorama-fog-mask");
      thread.setDaemon(true);
      thread.setPriority(4);
      return thread;
   });
   private @Nullable CompletableFuture<Void> finishing;
   private volatile boolean finishFailed;
   private static boolean finishFailureLogged;
   private static final double CRISP_FROM = 1.5;
   private static final double OVERLAY_ZOOM = 1.0;
   private static final int NEAR_TILES = 2;
   private static final int NEAR_TILE_COUNT = 25;
   private static final int MAX_FOG_STEPS = 11;
   private static final double COARSE_TEXEL = 512.0;
   private static final int COARSE_GROW = 2;
   private static final double[] COARSE_SPOTS = new double[]{0.25, 0.25, 0.75, 0.25, 0.25, 0.75, 0.75, 0.75};
   private static final double[] MIDDLE = new double[]{0.5, 0.5};
   private static final int OPACITY_CODES = 31;
   private float opacity = 1.0F;
   private float night = -1.0F;
   private static final int FINER_STAND_INS = 2;
   private static volatile boolean fogHiddenForTests;
   private static final int LAYER_CODE = 2;
   private @Nullable FogLayer fogLayer;
   private MapViewport.@Nullable LayerKey layerKey;
   private long fogSerial;
   private final float[] remembered = new float[65536];
   private final int[] notKnownCounts = new int[66049];
   private final int[] notUnknownCounts = new int[66049];
   private final float[] blurA = new float[65536];
   private final float[] blurB = new float[65536];
   private final int[] grownLine = new int[256];
   private final float[] knownShare = new float[65536];
   private final int[] notFullCounts = new int[66049];
   private final int[] notEmptyCounts = new int[66049];

   public void night(float night) {
      this.night = night;
   }

   public void opacity(float opacity) {
      this.opacity = Math.max(0.0F, Math.min(1.0F, opacity));
   }

   public void draw(GuiGraphics graphics, MapLevel level, MapViewport.View view) {
      this.draw(graphics, level, view, 0, false, null);
   }

   public void draw(GuiGraphics graphics, MapLevel level, MapViewport.View view, int overlays, boolean claims, @Nullable Runnable lit) {
      graphics.enableScissor(view.x(), view.y(), view.x() + view.width(), view.y() + view.height());
      boolean cave = view.layer() != Integer.MIN_VALUE;
      int background = cave ? -15987182 : (level.roofless() ? -14081761 : -7562837);
      float night = !(this.night < 0.0F) && !cave && !level.roofless() ? this.night : NightLook.forView(level, view.layer());
      if (night > 0.0F) {
         background = NightLook.darken(background, night);
      }

      if (!(this.opacity < 1.0F)) {
         if (view.round()) {
            MapCanvas.of(graphics).solid(Circle.in(view).disc(), background);
         } else {
            graphics.fill(view.x(), view.y(), view.x() + view.width(), view.y() + view.height(), background);
         }
      }

      int alpha = Math.round(this.opacity * 255.0F);
      MapMemory memory = cave ? null : level.memory();
      if (memory != null) {
         memory.draw(MapCanvas.of(graphics), view, NightLook.dim(night, alpha), lodFor(level, view), tileSampling(sampling(view)));
      }

      this.drawTiles(graphics, level, view, view.zoom() >= 1.0 ? overlays : 0, claims, lit, night);
      this.drawFog(graphics, level, view, night);
      graphics.disableScissor();
   }

   public static int lodFor(MapLevel level, MapViewport.View view) {
      if (view.layer() == Integer.MIN_VALUE && level.hasOverviews() && !(view.zoom() >= 0.5)) {
         double screenPixelsPerUnit = view.zoom() * Minecraft.getInstance().getWindow().getGuiScale();
         int lod = 0;

         while (lod < 11 && 4.0 / (2 << lod) >= screenPixelsPerUnit) {
            lod++;
         }

         boolean finer = lod > 1 || lod == 1 && !view.follows();
         return finer && mostTilesFor(view, lod - 1) <= 167.0 ? lod - 1 : lod;
      } else {
         return 0;
      }
   }

   private static double mostTilesFor(MapViewport.View view, int lod) {
      double units = 128.0 * (1 << lod);
      return (Math.ceil(view.width() / view.zoom() / units) + 3.0) * (Math.ceil(view.height() / view.zoom() / units) + 3.0);
   }

   public static Sampling sampling(MapViewport.View view) {
      double screenPixelsPerPixel = view.zoom() * Minecraft.getInstance().getWindow().getGuiScale() / 4.0;
      return !(view.zoom() >= 4.0) && !(screenPixelsPerPixel >= 1.5) ? Sampling.TILE_SMOOTH : Sampling.TILE_CRISP;
   }

   static Sampling tileSampling(Sampling sampling) {
      return sampling == Sampling.TILE_SMOOTH ? Sampling.TILE_AREA : sampling;
   }

   public static boolean ready(MapLevel level, MapViewport.View view) {
      boolean ready = true;

      for (TileKey key : tilesIn(level, view)) {
         ready &= level.want(key, distance(key, view)) != null;
      }

      return ready;
   }

   public static boolean showsAnything(MapLevel level, MapViewport.View view) {
      for (TileKey key : tilesIn(level, view)) {
         MapTile tile = level.peek(key);
         if (tile != null && !tile.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private static List<TileKey> tilesIn(MapLevel level, MapViewport.View view) {
      int lod = lodFor(level, view);
      double units = 128.0 * (1 << lod);
      double u0 = view.minU();
      double v0 = view.minV();
      int tu1 = (int)Math.floor((u0 + view.width() / view.zoom()) / units);
      int tv1 = (int)Math.floor((v0 + view.height() / view.zoom()) / units);
      List<TileKey> keys = new ArrayList<>();

      for (int tu = (int)Math.floor(u0 / units); tu <= tu1; tu++) {
         for (int tv = (int)Math.floor(v0 / units); tv <= tv1; tv++) {
            keys.add(new TileKey(view.layer(), tu, tv, lod));
         }
      }

      return keys;
   }

   private static double distance(TileKey key, MapViewport.View view) {
      double units = 128.0 * (1 << key.lod());
      double du = (key.tileU() + 0.5) * units - view.centerU();
      double dv = (key.tileV() + 0.5) * units - view.centerV();
      return Math.sqrt(du * du + dv * dv);
   }

   private void drawTiles(GuiGraphics graphics, MapLevel level, MapViewport.View view, int overlays, boolean claims, @Nullable Runnable lit, float night) {
      int lod = lodFor(level, view);
      double units = 128.0 * (1 << lod);
      double u0 = view.minU();
      double v0 = view.minV();
      double u1 = u0 + view.width() / view.zoom();
      double v1 = v0 + view.height() / view.zoom();
      int tu0 = (int)Math.floor(u0 / units);
      int tv0 = (int)Math.floor(v0 / units);
      int tu1 = (int)Math.floor(u1 / units);
      int tv1 = (int)Math.floor(v1 / units);
      Sampling sampling = sampling(view);
      Sampling tiles = tileSampling(sampling);
      MapCanvas canvas = MapCanvas.of(graphics);
      float size = (float)(units * view.zoom());
      Circle circle = view.round() ? Circle.in(view) : null;
      int tint = Math.round(this.opacity * 255.0F) << 24 | 16777215;
      List<Runnable> above = new ArrayList<>();

      for (int tu = tu0 - 1; tu <= tu1 + 1; tu++) {
         for (int tv = tv0 - 1; tv <= tv1 + 1; tv++) {
            TileKey key = new TileKey(view.layer(), tu, tv, lod);
            double du = (tu + 0.5) * units - view.centerU();
            double dv = (tv + 0.5) * units - view.centerV();
            boolean visible = tu >= tu0 && tu <= tu1 && tv >= tv0 && tv <= tv1;
            MapTile tile = level.want(key, Math.sqrt(du * du + dv * dv) + (visible ? 0.0 : 100000.0));
            float sx = view.screenX(tu * units);
            float sy = view.screenY(tv * units);
            if (!visible || tile != null && tile.texture() != null) {
               if (visible && tile != null && tile.texture() != null) {
                  MapTexture overlay = overlays != 0 && lod <= 0 ? tile.overlay(overlays) : null;
                  MapTexture claimed = claims ? level.claims(key, tile) : null;
                  if (circle != null) {
                     Circle.Quads inside = circle.clip(sx, sy, sx + size, sy + size, 0.0F, 0.0F, 1.0F, 1.0F);
                     if (inside != null) {
                        drawTile(canvas, tile, tiles, inside.corners(), inside.uvs(), tint, night);
                        if (claimed != null) {
                           above.add(() -> canvas.quad(claimed, sampling, inside.corners(), inside.uvs(), tint));
                        }

                        if (overlay != null) {
                           above.add(() -> canvas.quad(overlay, sampling, inside.corners(), inside.uvs(), tint));
                        }
                     }
                  } else {
                     drawTile(canvas, tile, tiles, sx, sy, size, 0.0F, 0.0F, 1.0F, 1.0F, tint, night);
                     if (claimed != null) {
                        above.add(() -> canvas.rect(claimed, sampling, sx, sy, sx + size, sy + size, 0.0F, 0.0F, 1.0F, 1.0F, tint));
                     }

                     if (overlay != null) {
                        above.add(() -> canvas.rect(overlay, sampling, sx, sy, sx + size, sy + size, 0.0F, 0.0F, 1.0F, 1.0F, tint));
                     }
                  }
               }
            } else {
               drawStandIn(level, key, canvas, tiles, circle, sx, sy, size, tint, night);
            }
         }
      }

      if (this.drawsNear(view, lod)) {
         drawNear(level, view, canvas, tiles, circle, tint, night);
      }

      if (lit != null) {
         lit.run();
      }

      above.forEach(Runnable::run);
   }

   private static void drawStandIn(
      MapLevel level, TileKey key, MapCanvas canvas, Sampling sampling, @Nullable Circle circle, float sx, float sy, float size, int tint, float night
   ) {
      TileKey above = key;

      for (int up = 1; key.lod() + up <= 11; up++) {
         above = above.parent();
         MapTile tile = level.standIn(above);
         if (tile != null && tile.texture() != null) {
            int span = 1 << up;
            float u0 = (float)Math.floorMod(key.tileU(), span) / span;
            float v0 = (float)Math.floorMod(key.tileV(), span) / span;
            drawPart(canvas, tile, sampling, circle, sx, sy, size, u0, v0, u0 + 1.0F / span, v0 + 1.0F / span, tint, night);
            return;
         }
      }

      drawFiner(level, key, canvas, sampling, circle, sx, sy, size, tint, night, 2);
   }

   private static void drawFiner(
      MapLevel level,
      TileKey key,
      MapCanvas canvas,
      Sampling sampling,
      @Nullable Circle circle,
      float sx,
      float sy,
      float size,
      int tint,
      float night,
      int levels
   ) {
      if (key.lod() != 0 && levels != 0) {
         float half = size / 2.0F;

         for (int dv = 0; dv < 2; dv++) {
            for (int du = 0; du < 2; du++) {
               TileKey child = new TileKey(key.layer(), key.tileU() * 2 + du, key.tileV() * 2 + dv, key.lod() - 1);
               MapTile tile = level.standIn(child);
               if (tile != null && tile.texture() != null) {
                  drawPart(canvas, tile, sampling, circle, sx + du * half, sy + dv * half, half, 0.0F, 0.0F, 1.0F, 1.0F, tint, night);
               } else if (tile == null) {
                  drawFiner(level, child, canvas, sampling, circle, sx + du * half, sy + dv * half, half, tint, night, levels - 1);
               }
            }
         }
      }
   }

   private static void drawPart(
      MapCanvas canvas,
      MapTile tile,
      Sampling sampling,
      @Nullable Circle circle,
      float sx,
      float sy,
      float size,
      float u0,
      float v0,
      float u1,
      float v1,
      int tint,
      float night
   ) {
      if (circle != null) {
         Circle.Quads inside = circle.clip(sx, sy, sx + size, sy + size, u0, v0, u1, v1);
         if (inside != null) {
            drawTile(canvas, tile, sampling, inside.corners(), inside.uvs(), tint, night);
         }
      } else {
         drawTile(canvas, tile, sampling, sx, sy, size, u0, v0, u1, v1, tint, night);
      }
   }

   private static void drawTile(
      MapCanvas canvas, MapTile tile, Sampling sampling, float sx, float sy, float size, float u0, float v0, float u1, float v1, int tint, float night
   ) {
      drawTile(
         canvas,
         tile,
         sampling,
         new float[]{sx, sy, sx, sy + size, sx + size, sy + size, sx + size, sy},
         new float[]{u0, v0, u0, v1, u1, v1, u1, v0},
         tint,
         night
      );
   }

   private static void drawTile(MapCanvas canvas, MapTile tile, Sampling sampling, float[] corners, float[] uvs, int tint, float night) {
      if (night <= 0.0F) {
         canvas.quad(tile.texture(), sampling, corners, uvs, tint);
      } else {
         int alpha = tint >>> 24;
         MapTexture light = tile.light();
         if (light == null) {
            canvas.quad(tile.texture(), sampling, corners, uvs, NightLook.dim(night, alpha));
         } else {
            canvas.nightTile(tile.texture(), sampling, light, corners, uvs, NightLook.params(night, sampling == Sampling.TILE_CRISP, alpha));
         }
      }
   }

   private boolean drawsNear(MapViewport.View view, int lod) {
      return lod > 0 && view.follows() && this.opacity >= 1.0F;
   }

   private static void drawNear(MapLevel level, MapViewport.View view, MapCanvas canvas, Sampling sampling, @Nullable Circle circle, int tint, float night) {
      int units = 128;
      float size = (float)(units * view.zoom());
      int nearU = (int)Math.floor(view.centerU() / units);
      int nearV = (int)Math.floor(view.centerV() / units);

      for (int tu = nearU - 2; tu <= nearU + 2; tu++) {
         for (int tv = nearV - 2; tv <= nearV + 2; tv++) {
            TileKey key = new TileKey(view.layer(), tu, tv, 0);
            float sx = view.screenX((double)tu * units);
            float sy = view.screenY((double)tv * units);
            boolean visible = sx < view.x() + view.width() && sx + size > view.x() && sy < view.y() + view.height() && sy + size > view.y();
            MapTile tile = level.want(key, distance(key, view) + (visible ? 0 : 100000));
            if (visible && tile != null && tile.texture() != null) {
               drawPart(canvas, tile, sampling, circle, sx, sy, size, 0.0F, 0.0F, 1.0F, 1.0F, tint, night);
            }
         }
      }
   }

   public static void hideFogForTests(boolean hide) {
      fogHiddenForTests = hide;
   }

   private void drawFog(GuiGraphics graphics, MapLevel level, MapViewport.View view, float night) {
      double viewW = view.width() / view.zoom();
      double viewH = view.height() / view.zoom();
      double scaleLog = Math.min(11.99, Math.log(Math.max(1.0, 0.5 / view.zoom())) / Math.log(2.0));
      int steps = (int)Math.floor(scaleLog);
      int fraction = (int)Math.floor((scaleLog - steps) * 16.0);
      double snap = 64.0 * (1 << steps + 1);
      double need = Math.max(viewW, viewH) + 48.0 + snap;
      int scaleCode = 0;

      double texel;
      for (texel = 2.0; 256.0 * texel < need; scaleCode++) {
         texel *= 2.0;
      }

      double originU = Math.floor((view.minU() - 24.0) / snap) * snap;
      double originV = Math.floor((view.minV() - 24.0) / snap) * snap;
      boolean fast = TileBaker.fast();
      this.buildMask(level, view, originU, originV, texel, steps, fraction, fast);
      if (!fogHiddenForTests) {
         double span = 256.0 * texel;
         float mu0 = (float)((view.minU() - originU) / span);
         float mv0 = (float)((view.minV() - originV) / span);
         float mu1 = (float)((view.minU() + viewW - originU) / span);
         float mv1 = (float)((view.minV() + viewH - originV) / span);
         int noiseU = Math.floorMod((long)(originU / snap), 8);
         int noiseV = Math.floorMod((long)(originV / snap), 8);
         boolean cave = view.layer() != Integer.MIN_VALUE;
         int fogCode = (cave ? 128 : 0) | (steps & 7) << 4 | fraction;
         int quality = (steps >= 8 ? 8 : 0) | (fast ? 4 : 0) | (level.roofless() ? 1 : 0);
         int veil = Math.round((1.0F - this.opacity) * 31.0F);
         int dark = Math.round(night * 31.0F);
         int params = (scaleCode * 16 | quality) << 24 | (noiseU * 32 | veil) << 16 | (noiseV * 32 | dark) << 8 | fogCode;
         MapCanvas canvas = MapCanvas.of(graphics);
         boolean fullSize = fast && !cave && !level.roofless();
         if (fullSize || !this.drawFogLayer(canvas, view, originU, originV, span, params)) {
            if (view.round()) {
               Circle.Quads disc = Circle.in(view).clip(view.x(), view.y(), view.x() + view.width(), view.y() + view.height(), mu0, mv0, mu1, mv1);
               if (disc != null) {
                  canvas.fog(
                     this.maskTexture,
                     FogTextures.noise(),
                     this.detailTexture,
                     disc.corners(),
                     disc.uvs(),
                     view.x(),
                     view.y(),
                     view.width(),
                     view.height(),
                     params
                  );
               }
            } else {
               canvas.fog(
                  this.maskTexture, FogTextures.noise(), this.detailTexture, view.x(), view.y(), view.width(), view.height(), mu0, mv0, mu1, mv1, params
               );
            }
         }
      }
   }

   private boolean drawFogLayer(MapCanvas canvas, MapViewport.View view, double originU, double originV, double span, int params) {
      double scale = Minecraft.getInstance().getWindow().getGuiScale() / 2.0;
      double texelsW = view.width() * scale;
      double texelsH = view.height() * scale;
      double texel = 1.0 / (view.zoom() * scale);
      double startU = (Math.floor(view.minU() / texel) - 1.0) * texel;
      double startV = (Math.floor(view.minV() / texel) - 1.0) * texel;
      double shiftU = (view.minU() - startU) / texel;
      double shiftV = (view.minV() - startV) / texel;
      int width = (int)Math.ceil(shiftU + texelsW) + 1;
      int height = (int)Math.ceil(shiftV + texelsH) + 1;
      if (this.fogLayer == null) {
         this.fogLayer = new FogLayer();
      }

      float u0 = (float)((startU - originU) / span);
      float v0 = (float)((startV - originV) / span);
      float u1 = (float)((startU + width * texel - originU) / span);
      float v1 = (float)((startV + height * texel - originV) / span);
      ClientLevel world = Minecraft.getInstance().level;
      MapViewport.LayerKey key = new MapViewport.LayerKey(this.fogSerial, params, width, height, u0, v0, u1, v1, world == null ? 0L : world.getGameTime());
      if (!key.equals(this.layerKey) || this.fogLayer.texture() == null) {
         if (!this.fogLayer.render(canvas, this.maskTexture, FogTextures.noise(), this.detailTexture, width, height, u0, v0, u1, v1, params | 33554432)) {
            return false;
         }

         this.layerKey = key;
      }

      MapTexture layer = this.fogLayer.texture();
      float size = this.fogLayer.size();
      float su0 = (float)(shiftU / size);
      float sv0 = (float)(shiftV / size);
      float su1 = (float)((shiftU + texelsW) / size);
      float sv1 = (float)((shiftV + texelsH) / size);
      float x1 = view.x() + view.width();
      float y1 = view.y() + view.height();
      if (view.round()) {
         Circle.Quads disc = Circle.in(view).clip(view.x(), view.y(), x1, y1, su0, sv0, su1, sv1);
         if (disc != null) {
            canvas.quad(layer, Sampling.CLAMP_LINEAR, disc.corners(), disc.uvs(), -1);
         }
      } else {
         canvas.rect(layer, Sampling.CLAMP_LINEAR, view.x(), view.y(), x1, y1, su0, sv0, su1, sv1, -1);
      }

      return true;
   }

   private void buildMask(MapLevel level, MapViewport.View view, double originU, double originV, double texel, int scaleSteps, int fraction, boolean fast) {
      if (this.maskTexture == null) {
         this.maskTexture = MapTexture.create("diorama fog mask", 256, 1);
         this.detailTexture = MapTexture.create("diorama fog details", 256, 1);
      }

      int lod = lodFor(level, view);
      boolean rounded = MapStyle.get().roundedFog() && view.layer() == Integer.MIN_VALUE && texel < 16.0;
      MapMemory memory = view.layer() == Integer.MIN_VALUE ? level.memory() : null;
      boolean drawsNear = this.drawsNear(view, lod);
      int nearU = drawsNear ? (int)Math.floor(view.centerU() / 128.0) : Integer.MIN_VALUE;
      int nearV = drawsNear ? (int)Math.floor(view.centerV() / 128.0) : Integer.MIN_VALUE;
      int memoryLod = memory == null ? -1 : memory.lod(view, lod);
      MapViewport.MaskInputs inputs = new MapViewport.MaskInputs(
         level.coverageVersion(),
         memory == null ? 0L : memory.version(),
         view.layer(),
         lod,
         memoryLod,
         originU,
         originV,
         texel,
         rounded,
         scaleSteps,
         fraction,
         fast,
         nearU,
         nearV
      );
      boolean sameLevel = this.maskLevel.get() == level;
      this.takeFinished();
      if (!sameLevel || !inputs.equals(this.maskInputs)) {
         boolean later = sameLevel && this.maskInputs != null && inputs.sameGeometry(this.maskInputs);
         if (this.finishing != null) {
            if (later) {
               return;
            }

            this.finishing.join();
            this.finishing = null;
         }

         this.maskInputs = inputs;
         this.maskLevel = new WeakReference<>(level);
         double units = 128.0 * (1 << lod);
         double cellUnits = units / 64.0;
         int lastTu = Integer.MIN_VALUE;
         int lastTv = Integer.MIN_VALUE;
         MapTile tile = null;
         MapTile above = null;
         TileKey aboveKey = null;
         MapTile[] finer = new MapTile[2];
         int[] lastFinerTu = new int[2];
         int[] lastFinerTv = new int[2];
         Arrays.fill(lastFinerTu, Integer.MIN_VALUE);
         Arrays.fill(lastFinerTv, Integer.MIN_VALUE);
         int lastNearTu = Integer.MIN_VALUE;
         int lastNearTv = Integer.MIN_VALUE;
         MapTile nearTile = null;
         boolean coarse = view.layer() == Integer.MIN_VALUE && texel >= 512.0;
         double[] spots = coarse ? COARSE_SPOTS : MIDDLE;

         for (int j = 0; j < 256; j++) {
            for (int i = 0; i < 256; i++) {
               float most = 0.0F;

               for (int spot = 0; spot < spots.length && most < 1.0F; spot += 2) {
                  double u = originU + (i + spots[spot]) * texel;
                  double v = originV + (j + spots[spot + 1]) * texel;
                  int tv = (int)Math.floor(v / units);
                  int cellY = Math.min(63, (int)((v - tv * units) / cellUnits));
                  int tu = (int)Math.floor(u / units);
                  if (tu != lastTu || tv != lastTv) {
                     tile = level.peek(new TileKey(view.layer(), tu, tv, lod));
                     lastTu = tu;
                     lastTv = tv;
                     above = null;
                     aboveKey = null;
                     if (tile == null || !tile.hasCoverage()) {
                        TileKey up = new TileKey(view.layer(), tu, tv, lod);

                        for (int l = lod + 1; l <= 11 && above == null; l++) {
                           up = up.parent();
                           MapTile candidate = level.peek(up);
                           if (candidate != null && candidate.hasCoverage()) {
                              above = candidate;
                              aboveKey = up;
                           }
                        }
                     }
                  }

                  float known = 0.0F;
                  if (tile != null && tile.hasCoverage()) {
                     int cellX = Math.min(63, (int)((u - tu * units) / cellUnits));
                     known = tile.coverage(cellX, cellY);
                  } else if (above != null) {
                     double aboveSpan = aboveKey.units();
                     double aboveCell = aboveSpan / 64.0;
                     int cellX = Math.max(0, Math.min(63, (int)((u - aboveKey.tileU() * aboveSpan) / aboveCell)));
                     int aboveY = Math.max(0, Math.min(63, (int)((v - aboveKey.tileV() * aboveSpan) / aboveCell)));
                     known = above.coverage(cellX, aboveY);
                  } else {
                     for (int down = 1; down <= 2 && lod - down >= 0; down++) {
                        double span = units / (1 << down);
                        int finerTu = (int)Math.floor(u / span);
                        int finerTv = (int)Math.floor(v / span);
                        if (finerTu != lastFinerTu[down - 1] || finerTv != lastFinerTv[down - 1]) {
                           finer[down - 1] = level.peek(new TileKey(view.layer(), finerTu, finerTv, lod - down));
                           lastFinerTu[down - 1] = finerTu;
                           lastFinerTv[down - 1] = finerTv;
                        }

                        MapTile under = finer[down - 1];
                        if (under != null) {
                           if (under.hasCoverage()) {
                              double finerCell = span / 64.0;
                              int cellX = Math.max(0, Math.min(63, (int)((u - finerTu * span) / finerCell)));
                              int finerY = Math.max(0, Math.min(63, (int)((v - finerTv * span) / finerCell)));
                              known = under.coverage(cellX, finerY);
                           }
                           break;
                        }
                     }
                  }

                  if (drawsNear && known < 1.0F) {
                     int nearTu = (int)Math.floor(u / 128.0);
                     int nearTv = (int)Math.floor(v / 128.0);
                     if (Math.abs(nearTu - nearU) <= 2 && Math.abs(nearTv - nearV) <= 2) {
                        if (nearTu != lastNearTu || nearTv != lastNearTv) {
                           nearTile = level.peek(new TileKey(view.layer(), nearTu, nearTv, 0));
                           lastNearTu = nearTu;
                           lastNearTv = nearTv;
                        }

                        if (nearTile != null && nearTile.hasCoverage()) {
                           double cell = 2.0;
                           int nearX = Math.min(63, (int)((u - nearTu * 128.0) / cell));
                           int nearY = Math.min(63, (int)((v - nearTv * 128.0) / cell));
                           known = Math.max(known, nearTile.coverage(nearX, nearY));
                        }
                     }
                  }

                  most = Math.max(most, known);
               }

               if (coarse && most > 0.0F) {
                  most = 1.0F;
               }

               this.maskPixels[j * 256 + i] = 0xFF000000 | Math.round(most * 255.0F);
            }
         }

         boolean surface = view.layer() == Integer.MIN_VALUE;
         if (surface) {
            if (memory != null) {
               memory.ask(memoryLod, originU, originV, originU + 256.0 * texel, originV + 256.0 * texel);

               for (int j = 0; j < 256; j++) {
                  double v = originV + (j + 0.5) * texel;

                  for (int i = 0; i < 256; i++) {
                     float known = memory.known(memoryLod, originU + (i + 0.5) * texel, v);
                     this.remembered[j * 256 + i] = Float.isNaN(known) ? 1.0F : known;
                  }
               }
            } else {
               Arrays.fill(this.remembered, 0.0F);
            }
         }

         this.finish(() -> this.finishBuild(inputs, rounded, surface), later);
      }
   }

   private void finishBuild(MapViewport.MaskInputs inputs, boolean rounded, boolean surface) {
      double texel = inputs.texel();
      boolean fast = inputs.fast();
      if (rounded) {
         this.roundOff(Math.max(1, (int)Math.round(10.0 / texel)));
      }

      if (surface && texel >= 512.0) {
         this.keepOffMapped(2);
      }

      this.writeSoft(texel, !surface, fast);
      this.markReach(fogReach(texel, !surface, fast, inputs.scaleSteps()));
      if (surface) {
         for (int k = 0; k < 65536; k++) {
            this.maskPixels[k] = this.maskPixels[k] & 16777215 | 255 - Math.round(this.remembered[k] * 255.0F) << 24;
         }
      }

      this.writeDetail();
   }

   private void finish(Runnable work, boolean later) {
      if (!later) {
         work.run();
         this.uploadMask();
      } else {
         this.finishFailed = false;
         this.finishing = CompletableFuture.runAsync(() -> {
            try {
               work.run();
            } catch (Throwable t) {
               this.finishFailed = true;
               if (!finishFailureLogged) {
                  finishFailureLogged = true;
                  Diorama.LOGGER.error("Building the fog mask failed", t);
               }
            }
         }, MASK_WORKER);
      }
   }

   private void takeFinished() {
      if (this.finishing != null && this.finishing.isDone()) {
         this.finishing = null;
         if (this.finishFailed) {
            this.maskInputs = null;
         } else {
            this.uploadMask();
         }
      }
   }

   private void uploadMask() {
      if (this.maskTexture != null) {
         this.maskTexture.write(0, 0, 0, 256, this.maskPixels);
      }

      if (this.detailTexture != null) {
         this.detailTexture.write(0, 0, 0, 256, this.detailPixels);
      }

      this.fogSerial++;
   }

   private static int[] fogReach(double texel, boolean cave, boolean fast, int scaleSteps) {
      double fogHeight = cave ? 4.0 : 10.0;
      double blurUnits = cave ? 2.5 : 7.0;
      if (blurUnits >= 5.0 && !fast) {
         blurUnits *= 2.4;
      }

      double blur = 1.9 * Math.max(blurUnits / texel, 1.0);
      double layerShift = fogHeight * 0.643 / texel;
      double sun = fast ? 0.0 : 9.0 * (1 << scaleSteps + 1) / texel;
      double slack = 1.5;
      return new int[]{
         (int)Math.ceil(Math.max(blur, sun) + slack),
         (int)Math.ceil(blur + slack),
         (int)Math.ceil(Math.max(blur, sun * 0.4) + slack),
         (int)Math.ceil(blur + layerShift + slack)
      };
   }

   private void markReach(int[] reach) {
      int row = 257;
      int[] notKnown = this.notKnownCounts;
      int[] notUnknown = this.notUnknownCounts;

      for (int j = 0; j < 256; j++) {
         int lineNotKnown = 0;
         int lineNotUnknown = 0;

         for (int i = 0; i < 256; i++) {
            int k = j * 256 + i;
            int known = this.maskPixels[k] & 0xFF;
            lineNotKnown += known == 255 ? 0 : 1;
            lineNotUnknown += known == 0 ? 0 : 1;
            notKnown[(j + 1) * row + i + 1] = notKnown[j * row + i + 1] + lineNotKnown;
            notUnknown[(j + 1) * row + i + 1] = notUnknown[j * row + i + 1] + lineNotUnknown;
         }
      }

      for (int j = 0; j < 256; j++) {
         int top = Math.max(0, j - reach[2]);
         int bottom = Math.min(255, j + reach[3]) + 1;

         for (int i = 0; i < 256; i++) {
            int left = Math.max(0, i - reach[0]);
            int right = Math.min(255, i + reach[1]) + 1;
            int a = bottom * row + right;
            int b = top * row + right;
            int c = bottom * row + left;
            int d = top * row + left;
            if (notKnown[a] - notKnown[b] - notKnown[c] + notKnown[d] == 0) {
               this.maskPixels[j * 256 + i] = this.maskPixels[j * 256 + i] | 0xFF00;
            } else if (notUnknown[a] - notUnknown[b] - notUnknown[c] + notUnknown[d] == 0) {
               this.maskPixels[j * 256 + i] = this.maskPixels[j * 256 + i] | 0xFF0000;
            }
         }
      }
   }

   private void roundOff(int radius) {
      for (int i = 0; i < this.maskPixels.length; i++) {
         this.blurA[i] = (this.maskPixels[i] & 0xFF) / 255.0F;
      }

      boxBlur(this.blurA, this.blurB, radius, true);
      boxBlur(this.blurB, this.blurA, radius, false);
      boxBlur(this.blurA, this.blurB, radius, true);
      boxBlur(this.blurB, this.blurA, radius, false);

      for (int i = 0; i < this.maskPixels.length; i++) {
         float t = Math.max(0.0F, Math.min(1.0F, (this.blurA[i] - 0.5F) / 0.35F));
         float rounded = t * t * (3.0F - 2.0F * t);
         int known = Math.min(this.maskPixels[i] & 0xFF, Math.round(rounded * 255.0F));
         this.maskPixels[i] = 0xFF000000 | known;
      }
   }

   private void keepOffMapped(int radius) {
      int[] known = this.grownLine;

      for (int pass = 0; pass < 2; pass++) {
         boolean horizontal = pass == 0;

         for (int line = 0; line < 256; line++) {
            for (int k = 0; k < 256; k++) {
               int most = 0;

               for (int d = Math.max(0, k - radius); d <= Math.min(255, k + radius); d++) {
                  most = Math.max(most, this.maskPixels[index(line, d, horizontal)] & 0xFF);
               }

               known[k] = most;
            }

            for (int k = 0; k < 256; k++) {
               int at = index(line, k, horizontal);
               this.maskPixels[at] = this.maskPixels[at] & -256 | known[k];
            }
         }
      }
   }

   private static void boxBlur(float[] from, float[] to, int radius, boolean horizontal) {
      float scale = 1.0F / (2 * radius + 1);

      for (int line = 0; line < 256; line++) {
         float sum = 0.0F;

         for (int k = -radius; k <= radius; k++) {
            sum += from[index(line, Math.max(0, Math.min(255, k)), horizontal)];
         }

         for (int k = 0; k < 256; k++) {
            to[index(line, k, horizontal)] = sum * scale;
            int add = Math.min(255, k + radius + 1);
            int drop = Math.max(0, k - radius);
            sum += from[index(line, add, horizontal)] - from[index(line, drop, horizontal)];
         }
      }
   }

   private void writeDetail() {
      for (int k = 0; k < 65536; k++) {
         this.detailPixels[k] = (this.softBytes[k] & 255) << 16;
      }
   }

   private void writeSoft(double texel, boolean cave, boolean fast) {
      double units = cave ? 2.5 : 7.0;
      boolean wide = units >= 5.0 && !fast;
      double near = Math.max(units / texel, 1.0);
      double far = Math.max(units * 2.4 / texel, 1.0);
      int reach = (int)Math.ceil(1.9 * (wide ? far : near)) + 2;
      int row = 257;

      for (int j = 0; j < 256; j++) {
         int lineNotFull = 0;
         int lineNotEmpty = 0;

         for (int i = 0; i < 256; i++) {
            int k = j * 256 + i;
            int known = this.maskPixels[k] & 0xFF;
            this.knownShare[k] = known / 255.0F;
            lineNotFull += known == 255 ? 0 : 1;
            lineNotEmpty += known == 0 ? 0 : 1;
            this.notFullCounts[(j + 1) * row + i + 1] = this.notFullCounts[j * row + i + 1] + lineNotFull;
            this.notEmptyCounts[(j + 1) * row + i + 1] = this.notEmptyCounts[j * row + i + 1] + lineNotEmpty;
         }
      }

      for (int j = 0; j < 256; j++) {
         int t = Math.max(0, j - reach);
         int b = Math.min(255, j + reach) + 1;

         for (int i = 0; i < 256; i++) {
            int l = Math.max(0, i - reach);
            int r = Math.min(255, i + reach) + 1;
            float soft;
            if (this.notFullCounts[b * row + r] - this.notFullCounts[t * row + r] - this.notFullCounts[b * row + l] + this.notFullCounts[t * row + l] == 0) {
               soft = 1.0F;
            } else if (this.notEmptyCounts[b * row + r]
                  - this.notEmptyCounts[t * row + r]
                  - this.notEmptyCounts[b * row + l]
                  + this.notEmptyCounts[t * row + l]
               == 0) {
               soft = 0.0F;
            } else {
               soft = this.blurAt(i + 0.5, j + 0.5, near);
               if (wide) {
                  soft = 0.5F * soft + 0.5F * this.blurAt(i + 0.5, j + 0.5, far);
               }
            }

            this.softBytes[j * 256 + i] = (byte)Math.round(soft * 255.0F);
         }
      }
   }

   private float blurAt(double x, double y, double r) {
      double d = r * 1.9;
      return this.knownAt(x, y) * 0.2F
         + (this.knownAt(x + r, y) + this.knownAt(x - r, y) + this.knownAt(x, y + r) + this.knownAt(x, y - r)) * 0.1F
         + (this.knownAt(x + d, y + d) + this.knownAt(x - d, y + d) + this.knownAt(x + d, y - d) + this.knownAt(x - d, y - d)) * 0.1F;
   }

   private float knownAt(double x, double y) {
      double fx = x - 0.5;
      double fy = y - 0.5;
      int i0 = (int)Math.floor(fx);
      int j0 = (int)Math.floor(fy);
      float tx = (float)(fx - i0);
      float ty = (float)(fy - j0);
      int i1 = Math.min(255, Math.max(0, i0 + 1));
      int j1 = Math.min(255, Math.max(0, j0 + 1));
      i0 = Math.min(255, Math.max(0, i0));
      j0 = Math.min(255, Math.max(0, j0));
      float top = this.knownShare[j0 * 256 + i0] + (this.knownShare[j0 * 256 + i1] - this.knownShare[j0 * 256 + i0]) * tx;
      float bottom = this.knownShare[j1 * 256 + i0] + (this.knownShare[j1 * 256 + i1] - this.knownShare[j1 * 256 + i0]) * tx;
      return top + (bottom - top) * ty;
   }

   private static int index(int line, int k, boolean horizontal) {
      return horizontal ? line * 256 + k : k * 256 + line;
   }

   public void close() {
      if (this.finishing != null) {
         this.finishing.join();
         this.finishing = null;
      }

      if (this.maskTexture != null) {
         this.maskTexture.close();
         this.maskTexture = null;
      }

      if (this.detailTexture != null) {
         this.detailTexture.close();
         this.detailTexture = null;
      }

      if (this.fogLayer != null) {
         this.fogLayer.close();
         this.fogLayer = null;
      }

      this.layerKey = null;
      this.maskInputs = null;
   }

   private record LayerKey(long serial, int params, int width, int height, float u0, float v0, float u1, float v1, long tick) {
   }

   private record MaskInputs(
      long coverageVersion,
      long memoryVersion,
      int layer,
      int lod,
      int memoryLod,
      double originU,
      double originV,
      double texel,
      boolean rounded,
      int scaleSteps,
      int fraction,
      boolean fast,
      int nearU,
      int nearV
   ) {
      boolean sameGeometry(MapViewport.MaskInputs other) {
         return this.layer == other.layer
            && this.originU == other.originU
            && this.originV == other.originV
            && this.texel == other.texel
            && this.rounded == other.rounded
            && this.scaleSteps == other.scaleSteps
            && this.fraction == other.fraction
            && this.fast == other.fast;
      }
   }

   public record View(int x, int y, int width, int height, double centerU, double centerV, double zoom, int layer, boolean round, boolean follows) {
      public View(int x, int y, int width, int height, double centerU, double centerV, double zoom, int layer) {
         this(x, y, width, height, centerU, centerV, zoom, layer, false, false);
      }

      public double minU() {
         return this.centerU - this.width / 2.0 / this.zoom;
      }

      public double minV() {
         return this.centerV - this.height / 2.0 / this.zoom;
      }

      public float screenX(double u) {
         return (float)(this.x + this.width / 2.0 + (u - this.centerU) * this.zoom);
      }

      public float screenY(double v) {
         return (float)(this.y + this.height / 2.0 + (v - this.centerV) * this.zoom);
      }
   }
}
