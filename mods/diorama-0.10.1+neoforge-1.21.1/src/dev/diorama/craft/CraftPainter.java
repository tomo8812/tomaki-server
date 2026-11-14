package dev.diorama.craft;

import dev.diorama.Diorama;
import dev.diorama.Facing;
import dev.diorama.MapSession;
import dev.diorama.MapStyle;
import dev.diorama.Projection;
import dev.diorama.bake.BlockLooks;
import dev.diorama.bake.CraftBaker;
import dev.diorama.bake.CraftBlocks;
import dev.diorama.bake.CraftShadow;
import dev.diorama.bake.Mipmaps;
import dev.diorama.bake.SurfaceArea;
import dev.diorama.bake.TileBaker;
import dev.diorama.data.ChunkSnapshot;
import dev.diorama.render.Circle;
import dev.diorama.render.MapLevel;
import dev.diorama.render.MapViewport;
import dev.diorama.render.NightLook;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import org.jspecify.annotations.Nullable;

final class CraftPainter {
   private static final double REBAKE_MISS = 0.125;
   private static final double REBAKE_CLIMB = 4.0;
   private static final double SHADOW_STEP = 0.125;
   private static final long SHADOW_GAP = 50000000L;
   private static final long IDLE_PER_WORK = 4L;
   private static final long AREA_KEPT = 1000000000L;
   private static final double AREA_MOVE = 1.0;
   private static final long UPLOAD_NANOS = 1500000L;
   private static final long CUT_KEPT = 250000000L;
   private static final float HIDES = 0.25F;
   private static final float GROUND_HIDES = 0.3F;
   static final ExecutorService BAKER = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "diorama-craft-baker");
      thread.setDaemon(true);
      thread.setPriority(2);
      return thread;
   });
   private final MapLevel map;
   private long uploadsUntil;

   CraftPainter(MapLevel map) {
      this.map = map;
   }

   void draw(GuiGraphics graphics, MapViewport.View view, Collection<Craft> crafts, float partialTick, float opacity) {
      BlockLooks looks = BlockLooks.get();
      if (looks != null && !crafts.isEmpty()) {
         this.uploadsUntil = System.nanoTime() + 1500000L;
         MapStyle style = MapStyle.get();
         double sin = Projection.sin();
         double cos = Projection.cos();
         int ppu = 4;
         boolean surface = view.layer() == Integer.MIN_VALUE;
         if (surface && this.map.roof() == Integer.MIN_VALUE || !this.map.fogOfWar()) {
            List<CraftPainter.Drawn> drawn = new ArrayList<>();
            List<CraftPainter.Shade> shades = new ArrayList<>();

            for (Craft craft : crafts) {
               Body.Blocks blocks = craft.blocks;
               if (blocks != null && craft.posed) {
                  double[] pose = craft.framePose;
                  if (craft.body == null || !craft.body.pose(blocks.min(), partialTick, pose)) {
                     System.arraycopy(craft.pose, 0, pose, 0, 12);
                  }

                  turn(pose, Projection.facing());
                  CraftBlocks b = blocks.blocks();
                  double[] box = b.box(pose);
                  if (surface || box[1] <= view.layer() && box[4] >= view.layer() - 48) {
                     Craft.Inputs inputs = new Craft.Inputs(b, style, looks, TileBaker.fast());
                     if (surface) {
                        CraftPainter.Shade shade = this.shadow(craft, inputs, pose, view);
                        if (shade != null) {
                           shades.add(shade);
                        }
                     }

                     if (inView(view, box[0], box[2] * sin - box[4] * cos, box[3], box[5] * sin - box[1] * cos)) {
                        double[] c = craft.center(pose);
                        double cu = c[0];
                        double cv = Projection.v(c[1], c[2]);
                        this.bake(craft, inputs, pose);
                        CraftBaker.Image image = craft.image;
                        if (image != null && craft.texture != null) {
                           int pu0 = (int)Math.round((cu + image.offsetU()) * ppu);
                           int pv0 = (int)Math.round((cv + image.offsetV()) * ppu);
                           double[] place = new double[]{(double)pu0 / ppu, (double)pv0 / ppu, 1.0 / ppu, 0.0, 0.0, 1.0 / ppu};
                           drawn.add(new CraftPainter.Drawn(craft, image, pu0, pv0, -(sin * c[1] + cos * c[2]), place));
                        }
                     }
                  }
               }
            }

            if (!drawn.isEmpty() || !shades.isEmpty()) {
               this.paint(graphics, view, drawn, shades, surface, opacity);
            }
         }
      }
   }

   static void turn(double[] pose, Facing facing) {
      for (int i = 0; i < 12; i += 3) {
         double x = pose[i];
         double z = pose[i + 2];
         pose[i] = facing.x(x, z);
         pose[i + 2] = facing.z(x, z);
      }
   }

   private static boolean inView(MapViewport.View view, double u0, double v0, double u1, double v1) {
      return u1 > view.minU() && u0 < view.minU() + view.width() / view.zoom() && v1 > view.minV() && v0 < view.minV() + view.height() / view.zoom();
   }

   private void paint(
      GuiGraphics graphics, MapViewport.View view, List<CraftPainter.Drawn> drawn, List<CraftPainter.Shade> shades, boolean surface, float opacity
   ) {
      int layer = view.layer();
      graphics.enableScissor(view.x(), view.y(), view.x() + view.width(), view.y() + view.height());
      MapCanvas canvas = MapCanvas.of(graphics);
      Circle circle = view.round() ? Circle.in(view) : null;
      int alpha = Math.round(Math.max(0.0F, Math.min(1.0F, opacity)) * 255.0F);
      MapSession session = MapSession.get();
      float night = session == null ? 0.0F : NightLook.forView(session.map(), layer);
      int tint = NightLook.dim(night, alpha);
      Sampling sampling = MapViewport.sampling(view);

      for (CraftPainter.Shade s : shades) {
         CraftShadow.Image shadow = s.craft().shadow;
         double[] place = new double[]{s.u0(), s.v0(), 0.25, 0.0, 0.0, 0.25};
         quad(canvas, circle, view, s.craft().shadowTexture, sampling, place, shadow.width(), shadow.height(), shadow.edge(), tint);
      }

      drawn.sort((a, b) -> a.area() != b.area() ? Long.compare(b.area(), a.area()) : Double.compare(b.depth(), a.depth()));

      for (int i = 0; i < drawn.size(); i++) {
         CraftPainter.Drawn d = drawn.get(i);
         List<CraftPainter.Drawn> under = new ArrayList<>();

         for (int j = 0; j < i; j++) {
            if (drawn.get(j).overlaps(d)) {
               under.add(drawn.get(j));
            }
         }

         boolean ground = !surface || this.mayBeCovered(d.craft());
         CraftPainter.SunMask sun = surface ? this.sunMask(d) : null;
         MapTexture texture = under.isEmpty() && !ground && sun == null ? d.craft().texture : this.cut(d, under, ground, sun, layer);
         quad(canvas, circle, view, texture, sampling, d.place(), d.image().width(), d.image().height(), d.image().edge(), tint);
      }

      graphics.disableScissor();
   }

   private static void quad(
      MapCanvas canvas,
      @Nullable Circle circle,
      MapViewport.View view,
      MapTexture texture,
      Sampling sampling,
      double[] place,
      int width,
      int height,
      int edge,
      int tint
   ) {
      float[] corners = new float[8];
      float[] uvs = new float[8];
      int[][] at = new int[][]{{0, 0}, {0, height}, {width, height}, {width, 0}};

      for (int i = 0; i < 4; i++) {
         corners[i * 2] = view.screenX(place[0] + place[2] * at[i][0] + place[3] * at[i][1]);
         corners[i * 2 + 1] = view.screenY(place[1] + place[4] * at[i][0] + place[5] * at[i][1]);
         uvs[i * 2] = (float)at[i][0] / edge;
         uvs[i * 2 + 1] = (float)at[i][1] / edge;
      }

      if (circle == null) {
         canvas.quad(texture, sampling, corners, uvs, tint);
      } else {
         float[] inside = circle.clip(corners, 4);
         if (inside != null) {
            float ax = corners[6] - corners[0];
            float ay = corners[7] - corners[1];
            float bx = corners[2] - corners[0];
            float by = corners[3] - corners[1];
            float det = ax * by - ay * bx;
            if (det != 0.0F) {
               float[] clippedUvs = new float[inside.length];

               for (int i = 0; i < inside.length; i += 2) {
                  float dx = inside[i] - corners[0];
                  float dy = inside[i + 1] - corners[1];
                  clippedUvs[i] = (dx * by - dy * bx) / det * width / edge;
                  clippedUvs[i + 1] = (ax * dy - ay * dx) / det * height / edge;
               }

               canvas.quad(texture, sampling, inside, clippedUvs, tint);
            }
         }
      }
   }

   private MapTexture cut(CraftPainter.Drawn d, List<CraftPainter.Drawn> under, boolean ground, CraftPainter.@Nullable SunMask sun, int layer) {
      List<Object> key = new ArrayList<>();
      key.add(d.image());
      key.add(sun);

      for (CraftPainter.Drawn u : under) {
         key.add(u.image());
         key.add(d.pu0() - u.pu0());
         key.add(d.pv0() - u.pv0());
         key.add(Math.round((u.depth() - d.depth()) * 16.0));
      }

      SurfaceArea surfaces = null;
      if (ground) {
         surfaces = this.map.surfaces(layer, d.pu0(), d.pv0(), d.pu1(), d.pv1());
         key.add(layer);
         key.add(d.pu0());
         key.add(d.pv0());
         key.add(Math.round(d.depth() * 16.0));
         key.add(Arrays.asList(surfaces.tiles()));
      }

      Craft craft = d.craft();
      if (key.equals(craft.cutFor)) {
         return craft.cut != null ? craft.cut : craft.texture;
      }

      long now = System.nanoTime();
      if (now >= this.uploadsUntil && now - craft.cutAt < 250000000L && craft.cutFor instanceof List<?> old && old.get(0) == d.image()) {
         return craft.cut != null ? craft.cut : craft.texture;
      }

      craft.cutAt = now;
      craft.cutFor = key;
      CraftBaker.Image image = d.image();
      int edge = image.edge();
      int width = image.width();
      int[] pixels = (int[])image.mips()[0].clone();
      int hidden = 0;
      if (sun != null) {
         for (int i = sun.shaded().nextSetBit(0); i >= 0; i = sun.shaded().nextSetBit(i + 1)) {
            int at = i / width * edge + i % width;
            if (pixels[at] != 0) {
               pixels[at] = pixels[at] & 0xFF000000 | image.sunless()[i] & 16777215;
               hidden++;
            }
         }
      }

      if (layer != Integer.MIN_VALUE) {
         hidden += underground(d, pixels, layer);
      }

      for (CraftPainter.Drawn u : under) {
         CraftBaker.Image other = u.image();
         int dx = d.pu0() - u.pu0();
         int dy = d.pv0() - u.pv0();
         float shift = (float)(u.depth() - d.depth());

         for (int y = Math.max(0, -dy); y < Math.min(image.height(), other.height() - dy); y++) {
            for (int x = Math.max(0, -dx); x < Math.min(width, other.width() - dx); x++) {
               int i = y * edge + x;
               float there = other.depth()[(y + dy) * other.width() + x + dx];
               if (pixels[i] != 0 && !Float.isNaN(there)) {
                  float here = image.depth()[y * width + x];
                  if (Float.isNaN(here) || there + shift < here - 0.25F) {
                     pixels[i] = 0;
                     hidden++;
                  }
               }
            }
         }
      }

      if (surfaces != null) {
         hidden += hideBehindGround(d, pixels, surfaces);
      }

      if (hidden == 0) {
         if (craft.cut != null) {
            craft.cut.close();
            craft.cut = null;
         }

         return craft.texture;
      } else {
         craft.cut = Craft.put(craft.cut, craft.cutEdge == edge, "diorama craft cut " + craft.id, edge, Mipmaps.build(pixels, edge));
         craft.cutEdge = edge;
         return craft.cut;
      }
   }

   private static int underground(CraftPainter.Drawn d, int[] pixels, int layer) {
      CraftBaker.Image image = d.image();
      double sin = Projection.sin();
      double cos = Projection.cos();
      int changed = 0;

      for (int y = 0; y < image.height(); y++) {
         double v = (d.pv0() + y + 0.5) / 4.0;

         for (int x = 0; x < image.width(); x++) {
            int i = y * image.edge() + x;
            int at = y * image.width() + x;
            float depth = image.depth()[at];
            if (pixels[i] != 0 && !Float.isNaN(depth)) {
               double worldY = -v * cos - (d.depth() + depth) * sin;
               pixels[i] = worldY > layer ? 0 : pixels[i] & 0xFF000000 | image.sunless()[at] & 16777215;
               changed++;
            }
         }
      }

      return changed;
   }

   private CraftPainter.@Nullable SunMask sunMask(CraftPainter.Drawn d) {
      Craft craft = d.craft();
      if (craft.sunPending != null && craft.sunPending.isDone()) {
         craft.sunMask = craft.sunPending.getNow(null);
         craft.sunPending = null;
      }

      CraftBaker.Image image = d.image();
      List<Object> key = new ArrayList<>(List.of(image, d.pu0(), d.pv0(), Math.round(d.depth() * 16.0)));
      int[] box = craft.box(craft.framePose);

      for (int cx = box[0] - TileBaker.SHADOW_REACH_X >> 4; cx <= box[3] + TileBaker.SHADOW_REACH_X >> 4; cx++) {
         for (int cz = box[2] - TileBaker.SHADOW_REACH_Z >> 4; cz <= box[5] + TileBaker.SHADOW_REACH_Z >> 4; cz++) {
            key.add(System.identityHashCode(this.map.chunk(cx, cz)));
         }
      }

      long nanos = System.nanoTime();
      BlockLooks looks = BlockLooks.get();
      if (craft.sunPending == null && looks != null && !key.equals(craft.sunFor) && nanos - craft.sunAt >= Math.max(50000000L, craft.sunCost * 4L)) {
         craft.sunFor = key;
         craft.sunAt = nanos;
         int pu0 = d.pu0();
         int pv0 = d.pv0();
         double depth0 = d.depth();
         craft.sunPending = later(() -> {
            long start = System.nanoTime();
            TileBaker baker = TileBaker.forSunChecks(this.map, looks, pu0, pv0, pu0 + image.width(), pv0 + image.height());
            int ppu = 4;
            double sin = Projection.sin();
            double cos = Projection.cos();
            BitSet shaded = new BitSet();

            for (int y = 0; y < image.height(); y++) {
               double v = (pv0 + y + 0.5) / ppu;

               for (int x = 0; x < image.width(); x++) {
                  int i = y * image.width() + x;
                  float depth = image.depth()[i];
                  if (!Float.isNaN(depth)) {
                     double world = depth0 + depth;
                     if (baker.sunBlocked((pu0 + x + 0.5) / ppu, -v * cos - world * sin, v * sin - world * cos)) {
                        shaded.set(i);
                     }
                  }
               }
            }

            craft.sunCost = System.nanoTime() - start;
            return shaded.isEmpty() ? null : new CraftPainter.SunMask(image.width(), image.height(), shaded);
         }, "work out what shades a craft");
      }

      CraftPainter.SunMask mask = craft.sunMask;
      return mask != null && mask.width() == image.width() && mask.height() == image.height() ? mask : null;
   }

   private boolean mayBeCovered(Craft craft) {
      int[] box = craft.box(craft.framePose);
      double run = Math.max(0, this.map.topY() - box[1]) * Projection.cos() / Projection.sin();

      for (int cx = box[0] >> 4; cx <= box[3] >> 4; cx++) {
         for (int cz = box[2] >> 4; cz <= (int)Math.floor(box[5] + run) >> 4; cz++) {
            ChunkSnapshot chunk = this.map.chunk(cx, cz);
            if (chunk != null && chunk.maxY >= box[1]) {
               return true;
            }
         }
      }

      return false;
   }

   private static int hideBehindGround(CraftPainter.Drawn d, int[] pixels, SurfaceArea ground) {
      CraftBaker.Image image = d.image();
      int hidden = 0;

      for (int y = 0; y < image.height(); y++) {
         for (int x = 0; x < image.width(); x++) {
            int i = y * image.edge() + x;
            float depth = image.depth()[y * image.width() + x];
            float near = ground.near(d.pu0() + x, d.pv0() + y);
            if (pixels[i] != 0 && !Float.isNaN(depth) && !Float.isNaN(near) && near < d.depth() + depth - 0.3F) {
               pixels[i] = 0;
               hidden++;
            }
         }
      }

      return hidden;
   }

   private static <T> CompletableFuture<@Nullable T> later(Supplier<@Nullable T> job, String what) {
      return CompletableFuture.supplyAsync(job, BAKER).exceptionally(e -> {
         Diorama.LOGGER.warn("Couldn't {} for the map", what, e);
         return null;
      });
   }

   private void bake(Craft craft, Craft.Inputs inputs, double[] pose) {
      if (craft.pending != null && craft.pending.isDone() && System.nanoTime() < this.uploadsUntil) {
         CraftBaker.Image image = craft.pending.getNow(null);
         craft.pending = null;
         if (image != null) {
            craft.texture = Craft.put(
               craft.texture, craft.image != null && craft.image.edge() == image.edge(), "diorama craft " + craft.id, image.edge(), image.mips()
            );
            craft.image = image;
         }
      }

      if (craft.pending == null) {
         CraftBaker.Image image = craft.image;
         CraftBlocks b = inputs.blocks();
         double[] linear = Arrays.copyOfRange(pose, 3, 12);
         double y = pose[1];
         boolean turned = image != null
            ? image.pitchSin() != Projection.sin() || Math.abs(craft.imageY - y) > 4.0 || stretched(image.linear(), linear, b)
            : craft.triedLinear != null && stretched(craft.triedLinear, linear, b);
         if (!inputs.equals(craft.imageFrom) || turned && System.nanoTime() - craft.imageAt >= craft.imageCost * 4L) {
            craft.imageAt = System.nanoTime();
            craft.imageFrom = inputs;
            craft.imageY = y;
            craft.triedLinear = linear;
            craft.pending = later(() -> {
               long start = System.nanoTime();
               CraftBaker.Image baked = CraftBaker.bake(b, linear, y, inputs.looks(), inputs.style(), inputs.fast());
               craft.imageCost = System.nanoTime() - start;
               return baked;
            }, "draw a craft");
         }
      }
   }

   private static boolean stretched(double[] from, double[] to, CraftBlocks b) {
      double[] w = new double[4];
      double missed = CraftBaker.warp(from, to, b.sizeX, b.sizeY, b.sizeZ, w);
      double radius = 0.5 * Math.sqrt(b.sizeX * b.sizeX + b.sizeY * b.sizeY + b.sizeZ * b.sizeZ);
      return missed + radius * (Math.abs(w[0] - 1.0) + Math.abs(w[1]) + Math.abs(w[2]) + Math.abs(w[3] - 1.0)) > 0.125;
   }

   private CraftPainter.@Nullable Shade shadow(Craft craft, Craft.Inputs inputs, double[] pose, MapViewport.View view) {
      if (craft.shadowPending != null && craft.shadowPending.isDone() && System.nanoTime() < this.uploadsUntil) {
         CraftShadow.Image made = craft.shadowPending.getNow(null);
         craft.shadowPending = null;
         if (made != null) {
            if (made.mips() != null) {
               craft.shadowTexture = Craft.put(
                  craft.shadowTexture, craft.shadowTextureEdge == made.edge(), "diorama craft shadow " + craft.id, made.edge(), made.mips()
               );
               craft.shadowTextureEdge = made.edge();
            }

            craft.shadow = made;
         }
      }

      long nanos = System.nanoTime();
      double[] middle = Craft.center(pose, inputs.blocks());
      double[] was = craft.shadowAreaMiddle;
      if (nanos - craft.shadowAreaAt > 1000000000L
         || was == null
         || Math.abs(was[0] - middle[0]) + Math.abs(was[1] - middle[1]) + Math.abs(was[2] - middle[2]) > 1.0) {
         craft.shadowArea = this.shadowArea(inputs.blocks(), pose, inputs.style());
         craft.shadowAreaMiddle = middle;
         craft.shadowAreaAt = nanos;
      }

      int[] area = craft.shadowArea;
      int ppu = 4;
      if (area != null && inView(view, (double)area[0] / ppu, (double)area[1] / ppu, (double)area[2] / ppu, (double)area[3] / ppu)) {
         SurfaceArea ground = this.map.surfaces(Integer.MIN_VALUE, area[0], area[1], area[2], area[3]);
         CraftShadow.Image shadow = craft.shadow;
         CraftBlocks b = inputs.blocks();
         double[] linear = Arrays.copyOfRange(pose, 3, 12);
         boolean turned = shadow == null
            || !inputs.equals(craft.shadowFrom)
            || shadow.pitchSin() != Projection.sin()
            || shadow.silhouette() == null
            || CraftShadow.stale(shadow.silhouette(), linear, b);
         boolean moved = turned
            || !shadow.ground().same(ground)
            || Math.abs(shadow.pose()[0] - pose[0]) + Math.abs(shadow.pose()[1] - pose[1]) + Math.abs(shadow.pose()[2] - pose[2]) > 0.125;
         if (moved && craft.shadowPending == null && nanos - craft.shadowAt >= Math.max(50000000L, craft.shadowCost * 4L)) {
            craft.shadowAt = nanos;
            CraftShadow.Silhouette kept = turned ? null : shadow.silhouette();
            double[] at = (double[])pose.clone();
            craft.shadowFrom = inputs;
            craft.shadowPending = later(() -> {
               long start = System.nanoTime();
               CraftShadow.Silhouette silhouette = kept != null ? kept : CraftShadow.silhouette(b, linear, inputs.looks(), inputs.style());
               CraftShadow.Image cast = CraftShadow.cast(silhouette, at, area[0], area[1], area[2] - area[0], area[3] - area[1], ground);
               craft.shadowCost = System.nanoTime() - start;
               return cast;
            }, "make a craft's shadow");
         }

         if (shadow != null && shadow.mips() != null && craft.shadowTexture != null) {
            double[] now = craft.center(pose);
            double[] then = craft.center(shadow.pose());
            double run = (now[1] - then[1]) / Math.tan(Math.toRadians(inputs.style().sunElevation()));
            double dx = now[0] - then[0] - -0.878 * run;
            double dz = now[2] - then[2] - 0.479 * run;
            return new CraftPainter.Shade(craft, (double)shadow.pu0() / ppu + dx, (double)shadow.pv0() / ppu + dz * Projection.sin());
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private int @Nullable [] shadowArea(CraftBlocks blocks, double[] pose, MapStyle style) {
      double[] c = Craft.center(pose, blocks);
      int middle = this.surface((int)Math.floor(c[0]), (int)Math.floor(c[2]));
      if (middle == Integer.MIN_VALUE) {
         return null;
      }

      int[] reach = CraftShadow.reach(blocks, pose, middle, style);
      int lowest = middle;

      for (int z = reach[1]; z <= reach[3]; z += 2) {
         for (int x = reach[0]; x <= reach[2]; x += 2) {
            int top = this.surface(x, z);
            if (top != Integer.MIN_VALUE) {
               lowest = Math.min(lowest, top);
            }
         }
      }

      if (lowest < middle) {
         reach = CraftShadow.reach(blocks, pose, lowest, style);
      }

      int ppu = 4;
      double sin = Projection.sin();
      double cos = Projection.cos();
      double top = blocks.box(pose)[4];
      int[] area = new int[]{
         reach[0] * ppu, (int)Math.floor((reach[1] * sin - top * cos) * ppu), (reach[2] + 1) * ppu, (int)Math.ceil(((reach[3] + 1) * sin - lowest * cos) * ppu)
      };
      return area[2] - area[0] <= 2048 && area[3] - area[1] <= 2048 ? area : null;
   }

   private int surface(int x, int z) {
      ChunkSnapshot chunk = this.map.chunk(x >> 4, z >> 4);
      return chunk == null ? Integer.MIN_VALUE : chunk.surface(x, z);
   }

   private record Drawn(Craft craft, CraftBaker.Image image, int pu0, int pv0, double depth, double[] place) {
      int pu1() {
         return this.pu0 + this.image.width();
      }

      int pv1() {
         return this.pv0 + this.image.height();
      }

      long area() {
         return (long)this.image.width() * this.image.height();
      }

      boolean overlaps(CraftPainter.Drawn other) {
         return this.pu0 < other.pu1() && other.pu0 < this.pu1() && this.pv0 < other.pv1() && other.pv0 < this.pv1();
      }
   }

   private record Shade(Craft craft, double u0, double v0) {
   }

   record SunMask(int width, int height, BitSet shaded) {
   }
}
