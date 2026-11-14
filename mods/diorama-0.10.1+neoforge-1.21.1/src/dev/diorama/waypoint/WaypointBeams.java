package dev.diorama.waypoint;

import dev.diorama.Settings;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public final class WaypointBeams {
   static final int HEIGHT = 256;
   private static final int TIP = 64;
   private static final float CORE = 0.4F;
   private static final float EDGE = 0.8F;
   private static final float CORE_MIN = 2.0F;
   private static final float EDGE_MIN = 5.0F;
   private static final float CORE_ALPHA = 0.9F;
   private static final float EDGE_ALPHA = 0.45F;
   private static final double GONE = 3.0;
   private static final double FULL = 12.0;
   private static final double DIM_FROM = 256.0;
   private static final double DIM_TO = 2048.0;
   private static final float FAR_ALPHA = 0.7F;
   private static final float MARGIN = 96.0F;
   private static final float NEAR = 0.05F;
   private static final int TEXTURE = 8;
   private static final float[] ACROSS = new float[]{0.0F, 0.4F, 1.0F, 1.0F, 1.0F, 1.0F, 0.4F, 0.0F};
   private static final float SOFT = 1.6F;
   private static final float[] DOWN = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F};
   private static final float CLEAR_V = 0.0625F;
   private static final float SOLID_V = 0.1875F;
   private static @Nullable MapTexture texture;
   private static final int BLOCKS = 4000;
   private static final int PER_BLOCK = 4;
   private static final int PARTS = 1024;
   private static final float[] CLEAR = new float[0];
   private static final int ALONG_X = 2;
   private static final int ALONG_Z = 3;
   private static final float COVERS = 0.9F;
   private static final int LOOK_MOST = 12000;
   private static final long AGAIN_MOVING = 50L;
   private static final long AGAIN_STILL = 500L;
   private final Map<UUID, WaypointBeams.Sight> sights = new HashMap<>();
   private final boolean[] hidden = new boolean[1024];
   private final Map<BlockState, float[]> blocking = new IdentityHashMap<>();
   private final MutableBlockPos pos = new MutableBlockPos();
   private @Nullable ClientLevel level;
   private WeakReference<ClientLevel> seenIn = new WeakReference<>(null);
   private Vec3 eye = Vec3.ZERO;
   private float width;
   private float height;
   private float z;
   private long frame;
   private long now;
   private int blocks;
   private double reach;
   private int index;
   private int resume;
   private int next;
   private final Vector4f base = new Vector4f();
   private final Vector4f top = new Vector4f();
   private float[] xyz = new float[384];
   private float[] uvs = new float[256];
   private int[] colors = new int[32];
   private int quads;

   static boolean has(Waypoint waypoint, Settings.Beams beams) {
      return switch (beams) {
         case OFF -> false;
         case TEMPORARY -> waypoint.kind == Waypoint.Kind.TEMPORARY || waypoint.kind == Waypoint.Kind.DEATH;
         case ALL -> true;
      };
   }

   void begin(ClientLevel level, Vec3 eye, float width, float height, float z) {
      this.quads = 0;
      this.level = level;
      if (this.seenIn.get() != level) {
         this.seenIn = new WeakReference<>(level);
         this.sights.clear();
      }

      this.eye = eye;
      this.width = width;
      this.height = height;
      this.z = z;
      this.frame++;
      this.now = System.nanoTime() / 1000000L;
      this.blocks = 4000;
      this.index = 0;
      this.resume = this.next;
      this.next = 0;
      this.reach = (Integer)Minecraft.getInstance().options.renderDistance().get() * 16 + 16;
      if (this.frame % 1200L == 0L) {
         this.sights.values().removeIf(sight -> this.frame - sight.used > 1200L);
      }
   }

   void add(Waypoint waypoint) {
      int index = this.index++;
      Vec3 eye = this.eye;
      double dx = waypoint.x + 0.5 - eye.x;
      double dy = waypoint.y + 1.0 - eye.y;
      double dz = waypoint.z + 0.5 - eye.z;
      double ground = Math.sqrt(dx * dx + dz * dz);
      if (!(ground <= 3.0)) {
         WaypointBeams.Sight sight = this.sights.get(waypoint.id);
         if ((sight == null || index >= this.resume && sight.stale(eye, this.now)) && this.blocks > 0) {
            if (sight == null) {
               sight = new WaypointBeams.Sight();
               this.sights.put(waypoint.id, sight);
            }

            this.look(sight, dx, dy, dz, ground);
            sight.at = eye;
            sight.time = this.now;
            if (this.blocks <= 0) {
               this.next = index + 1;
            }
         }

         if (sight != null) {
            sight.used = this.frame;
            if (sight.count != 0) {
               double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
               float near = (float)Math.min(1.0, (ground - 3.0) / 9.0);
               float far = 1.0F - 0.3F * (float)Math.max(0.0, Math.min(1.0, (distance - 256.0) / 1792.0));
               int saved = waypoint.color >>> 24 == 0 ? 255 : waypoint.color >>> 24;
               float strength = near * (2.0F - near) * far * saved / 255.0F;
               int rgb = waypoint.color & 16777215;
               int edge = alpha(0.45F * strength) << 24 | edgeColor(rgb);
               int core = alpha(0.9F * strength) << 24 | coreColor(rgb);
               int body = 192;

               for (int i = 0; i < sight.count; i++) {
                  float from = sight.spans[i * 2];
                  float to = sight.spans[i * 2 + 1];
                  if (from < body) {
                     this.piece(dx, dy, dz, from, Math.min(to, body), 0.1875F, 0.1875F, this.width, this.height, this.z, edge, core);
                  }

                  if (to > body) {
                     float start = Math.max(from, body);
                     this.piece(dx, dy, dz, start, to, tipV(start), tipV(to), this.width, this.height, this.z, edge, core);
                  }
               }
            }
         }
      }
   }

   private static float tipV(float height) {
      return 0.1875F + -0.125F * (height - 192.0F) / 64.0F;
   }

   private void look(WaypointBeams.Sight sight, double dx, double dy, double dz, double ground) {
      Arrays.fill(this.hidden, false);
      int hiddenParts = 0;
      double limit = Math.min(ground, this.reach);
      double ux = dx / ground;
      double uz = dz / ground;
      double low = dy / ground;
      double high = (dy + 256.0) / ground;
      int x = (int)Math.floor(this.eye.x);
      int z = (int)Math.floor(this.eye.z);
      int stepX = ux > 0.0 ? 1 : -1;
      int stepZ = uz > 0.0 ? 1 : -1;
      double perX = ux == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / ux);
      double perZ = uz == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / uz);
      double nextX = ux == 0.0 ? Double.POSITIVE_INFINITY : (ux > 0.0 ? x + 1 - this.eye.x : this.eye.x - x) * perX;
      double nextZ = uz == 0.0 ? Double.POSITIVE_INFINITY : (uz > 0.0 ? z + 1 - this.eye.z : this.eye.z - z) * perZ;
      int face = Math.abs(ux) >= Math.abs(uz) ? 2 : 3;
      int floor = this.level.getMinBuildHeight();
      int budget = this.blocks - 12000;
      double entered = 0.0;

      while (entered < limit && hiddenParts < 1024 && this.blocks > budget) {
         double near = Math.max(entered, 0.001);
         double far = Math.max(Math.min(Math.min(nextX, nextZ), limit), near);
         int top = Math.min(this.level.getHeight(Types.MOTION_BLOCKING, x, z), (int)Math.floor(this.eye.y + Math.max(near * high, far * high)));
         int bottom = Math.max(floor, (int)Math.floor(this.eye.y + Math.min(near * low, far * low)));

         for (int y = top; y >= bottom && hiddenParts < 1024; y--) {
            this.blocks--;
            BlockState state = this.level.getBlockState(this.pos.set(x, y, z));
            if (!state.isAir()) {
               float[] blocking = this.blocking(state);
               if (blocking != CLEAR && !(blocking[face] < 0.9F)) {
                  double under = y + blocking[0] - this.eye.y;
                  double over = y + blocking[1] - this.eye.y;
                  int from = Math.max(0, (int)Math.floor((Math.min(under / near, under / far) * ground - dy) * 4.0));
                  int to = Math.min(1024, (int)Math.ceil((Math.max(over / near, over / far) * ground - dy) * 4.0));

                  for (int i = from; i < to; i++) {
                     if (!this.hidden[i]) {
                        this.hidden[i] = true;
                        hiddenParts++;
                     }
                  }
               }
            }
         }

         if (nextX < nextZ) {
            x += stepX;
            entered = nextX;
            nextX += perX;
         } else {
            z += stepZ;
            entered = nextZ;
            nextZ += perZ;
         }
      }

      sight.count = 0;
      int start = -1;

      for (int i = 0; i <= 1024; i++) {
         boolean shown = i < 1024 && !this.hidden[i];
         if (shown && start < 0) {
            start = i;
         } else if (!shown && start >= 0) {
            sight.add(start / 4.0F, i / 4.0F);
            start = -1;
         }
      }
   }

   private float[] blocking(BlockState state) {
      boolean fixed = !state.getBlock().hasDynamicShape();
      float[] blocking = fixed ? this.blocking.get(state) : null;
      if (blocking == null) {
         blocking = CLEAR;

         try {
            VoxelShape shape = state.getVisualShape(this.level, this.pos, CollisionContext.empty());
            if (!shape.isEmpty() && !state.is(BlockTags.FENCES) && !state.is(BlockTags.FENCE_GATES)) {
               double from = Math.max(0.0, shape.min(Axis.Y));
               double to = Math.min(1.0, shape.max(Axis.Y));
               double[] covered = new double[2];
               shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
                  double tall = Math.min(to, y2) - Math.max(from, y1);
                  if (tall > 0.0) {
                     covered[0] += (z2 - z1) * tall;
                     covered[1] += (x2 - x1) * tall;
                  }
               });
               if (to > from && Math.max(covered[0], covered[1]) >= 0.9F * (to - from)) {
                  blocking = new float[]{
                     (float)from, (float)to, (float)Math.min(1.0, covered[0] / (to - from)), (float)Math.min(1.0, covered[1] / (to - from))
                  };
               }
            }
         } catch (RuntimeException var10) {
         }

         if (fixed) {
            this.blocking.put(state, blocking);
         }
      }

      return blocking;
   }

   private void piece(double dx, double dy, double dz, double from, double to, float baseV, float topV, float width, float height, float z, int edge, int core) {
      if (WorldProjection.clip(dx, dy + from, dz, this.base) && WorldProjection.clip(dx, dy + to, dz, this.top)) {
         if (!(this.base.w < 0.05F) || !(this.top.w < 0.05F)) {
            if (this.base.w < 0.05F) {
               float t = (0.05F - this.base.w) / (this.top.w - this.base.w);
               this.base.lerp(this.top, t);
               baseV += (topV - baseV) * t;
            } else if (this.top.w < 0.05F) {
               float t = (0.05F - this.top.w) / (this.base.w - this.top.w);
               this.top.lerp(this.base, t);
               topV += (baseV - topV) * t;
            }

            float ax = (this.base.x / this.base.w + 1.0F) / 2.0F * width;
            float ay = (1.0F - this.base.y / this.base.w) / 2.0F * height;
            float bx = (this.top.x / this.top.w + 1.0F) / 2.0F * width;
            float by = (1.0F - this.top.y / this.top.w) / 2.0F * height;
            float s0 = 0.0F;
            float s1 = 1.0F;

            for (int side = 0; side < 4; side++) {
               float p = side == 0 ? bx + 96.0F : (side == 1 ? width + 96.0F - bx : (side == 2 ? by + 96.0F : height + 96.0F - by));
               float d = side == 0 ? ax - bx : (side == 1 ? bx - ax : (side == 2 ? ay - by : by - ay));
               if (d == 0.0F) {
                  if (p < 0.0F) {
                     return;
                  }
               } else if (d > 0.0F) {
                  s0 = Math.max(s0, -p / d);
               } else {
                  s1 = Math.min(s1, -p / d);
               }
            }

            float length = (float)Math.hypot(ax - bx, ay - by);
            if (!(s0 >= s1) && !(length * (s1 - s0) < 0.5F)) {
               float lx = -(ay - by) / length;
               float ly = (ax - bx) / length;
               float perBlock = WorldProjection.focal() * height / 2.0F;
               float topScale = perBlock / this.top.w;
               float baseScale = perBlock / this.base.w;
               float gui = (float)Math.max(1.0, Minecraft.getInstance().getWindow().getGuiScale());
               float first = topScale + (baseScale - topScale) * s0;
               float last = topScale + (baseScale - topScale) * s1;
               float taper = Math.max(first, last) / Math.min(first, last);
               int steps = taper < 1.02F ? 1 : (taper < 2.0F ? 2 : (taper < 6.0F ? 4 : 8));

               for (int layer = 0; layer < 2; layer++) {
                  float blocks = layer == 0 ? 0.8F : 0.4F;
                  float least = (layer == 0 ? 5.0F : 2.0F) / gui;
                  int argb = layer == 0 ? edge : core;

                  for (int i = 0; i < steps; i++) {
                     float sa = s0 + (s1 - s0) * i / steps;
                     float sb = s0 + (s1 - s0) * (i + 1) / steps;
                     float tx = bx + (ax - bx) * sa;
                     float ty = by + (ay - by) * sa;
                     float ux = bx + (ax - bx) * sb;
                     float uy = by + (ay - by) * sb;
                     float th = Math.max(least, blocks * (topScale + (baseScale - topScale) * sa)) / 2.0F * 1.6F;
                     float uh = Math.max(least, blocks * (topScale + (baseScale - topScale) * sb)) / 2.0F * 1.6F;
                     float tv = topV + (baseV - topV) * sa;
                     float uv = topV + (baseV - topV) * sb;
                     if (steps == 1) {
                        this.quad(
                           tx + lx * th,
                           ty + ly * th,
                           ux + lx * uh,
                           uy + ly * uh,
                           ux - lx * uh,
                           uy - ly * uh,
                           tx - lx * th,
                           ty - ly * th,
                           0.0F,
                           1.0F,
                           tv,
                           uv,
                           z,
                           argb
                        );
                     } else {
                        this.quad(tx + lx * th, ty + ly * th, ux + lx * uh, uy + ly * uh, ux, uy, tx, ty, 0.0F, 0.5F, tv, uv, z, argb);
                        this.quad(tx, ty, ux, uy, ux - lx * uh, uy - ly * uh, tx - lx * th, ty - ly * th, 0.5F, 1.0F, tv, uv, z, argb);
                     }
                  }
               }
            }
         }
      }
   }

   void end() {
      this.level = null;
   }

   void draw(GuiGraphics graphics) {
      if (this.quads != 0) {
         if (texture == null) {
            texture = MapTexture.create("diorama waypoint beam", 8, 1);
            int[] abgr = new int[64];

            for (int y = 0; y < 8; y++) {
               for (int x = 0; x < 8; x++) {
                  abgr[y * 8 + x] = Math.round(ACROSS[x] * DOWN[y] * 255.0F) << 24 | 16777215;
               }
            }

            texture.write(0, 0, 0, 8, abgr);
         }

         MapCanvas.of(graphics).rects(texture, Sampling.CLAMP_LINEAR, this.xyz, this.uvs, this.colors, this.quads);
      }
   }

   private static int alpha(float alpha) {
      return Math.max(0, Math.min(255, Math.round(alpha * 255.0F)));
   }

   public static int coreColor(int rgb) {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      return r + (255 - r) / 4 << 16 | g + (255 - g) / 4 << 8 | b + (255 - b) / 4;
   }

   private static int edgeColor(int rgb) {
      return (rgb >> 16 & 0xFF) / 4 << 16 | (rgb >> 8 & 0xFF) / 4 << 8 | (rgb & 0xFF) / 4;
   }

   private void quad(
      float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, float leftU, float rightU, float topV, float baseV, float z, int argb
   ) {
      if (this.quads == this.colors.length) {
         int n = this.quads * 2;
         this.xyz = Arrays.copyOf(this.xyz, n * 12);
         this.uvs = Arrays.copyOf(this.uvs, n * 8);
         this.colors = Arrays.copyOf(this.colors, n);
      }

      int p = this.quads * 12;
      int t = this.quads * 8;
      this.corner(p, t, x0, y0, z, leftU, topV);
      this.corner(p + 3, t + 2, x1, y1, z, leftU, baseV);
      this.corner(p + 6, t + 4, x2, y2, z, rightU, baseV);
      this.corner(p + 9, t + 6, x3, y3, z, rightU, topV);
      this.colors[this.quads++] = argb;
   }

   private void corner(int p, int t, float x, float y, float z, float u, float v) {
      this.xyz[p] = x;
      this.xyz[p + 1] = y;
      this.xyz[p + 2] = z;
      this.uvs[t] = u;
      this.uvs[t + 1] = v;
   }

   private static final class Sight {
      float[] spans = new float[8];
      int count;
      Vec3 at = Vec3.ZERO;
      long time;
      long used;

      boolean stale(Vec3 eye, long now) {
         long age = now - this.time;
         return age >= 500L || age >= 50L && eye.distanceToSqr(this.at) > 0.01;
      }

      void add(float from, float to) {
         if (!(to - from < 0.05F)) {
            if (this.count * 2 == this.spans.length) {
               this.spans = Arrays.copyOf(this.spans, this.count * 4);
            }

            this.spans[this.count * 2] = from;
            this.spans[this.count * 2 + 1] = to;
            this.count++;
         }
      }
   }
}
