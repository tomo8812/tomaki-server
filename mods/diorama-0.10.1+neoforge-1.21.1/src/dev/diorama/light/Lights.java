package dev.diorama.light;

import dev.diorama.Diorama;
import dev.diorama.Facing;
import dev.diorama.MapSession;
import dev.diorama.MapStyle;
import dev.diorama.Projection;
import dev.diorama.Settings;
import dev.diorama.bake.BlockLooks;
import dev.diorama.bake.Mipmaps;
import dev.diorama.bake.SurfaceArea;
import dev.diorama.bake.VoxelReader;
import dev.diorama.render.Circle;
import dev.diorama.render.MapLevel;
import dev.diorama.render.MapViewport;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class Lights {
   private final ClientLevel level;
   private final MapLevel map;
   private final Map<UUID, Lights.Glow> glows = new HashMap<>();
   private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "diorama-held-light");
      thread.setDaemon(true);
      return thread;
   });
   private static final int MAX_SPAN = 512;
   private static final int[][] STEPS = steps();
   private static final float[] STEP_LENGTHS = new float[]{0.0F, 1.0F, (float)Math.sqrt(2.0), (float)Math.sqrt(3.0)};

   public Lights(ClientLevel level, MapLevel map) {
      this.level = level;
      this.map = map;
   }

   static int carried(Player player) {
      return Math.max(emission(player.getMainHandItem()), emission(player.getOffhandItem()));
   }

   private static int emission(ItemStack stack) {
      if (stack.is(Items.LAVA_BUCKET)) {
         return 15;
      } else {
         return stack.getItem() instanceof BlockItem item ? item.getBlock().defaultBlockState().getLightEmission() : 0;
      }
   }

   public void draw(GuiGraphics graphics, MapViewport.View view, float partialTick, float opacity, boolean radar) {
      Player self = Minecraft.getInstance().player;
      Entity viewer = MapSession.viewer();
      if (view.layer() != Integer.MIN_VALUE && !this.map.roofless() && self != null && viewer != null) {
         double ppu = 4.0;
         double vu0 = view.minU() * ppu;
         double vv0 = view.minV() * ppu;
         double vu1 = vu0 + view.width() / view.zoom() * ppu;
         double vv1 = vv0 + view.height() / view.zoom() * ppu;
         List<Lights.Held> held = new ArrayList<>();

         for (Player player : this.level.players()) {
            int carried = carried(player);
            if (carried > 0 && (player == viewer || player != self && radar && onRadar(player, self, viewer, partialTick) && this.simulated(player, viewer))) {
               Vec3 eye = player.getEyePosition(partialTick);
               int sy = (int)Math.floor(eye.y - 0.5);
               if (sy <= view.layer()) {
                  Facing facing = Projection.facing();
                  int eyeX = (int)Math.floor(eye.x);
                  int eyeZ = (int)Math.floor(eye.z);
                  Lights.Held light = Lights.Held.of(player.getUUID(), facing.blockX(eyeX, eyeZ), sy, facing.blockZ(eyeX, eyeZ), carried);
                  if (light.pu1() > vu0 && light.pu0() < vu1 && light.pv1() > vv0 && light.pv0() < vv1) {
                     held.add(light);
                  }
               }
            }
         }

         MapStyle style = MapStyle.get();
         Map<UUID, Lights.Glow> kept = new HashMap<>();
         List<Lights.Shown> drawn = new ArrayList<>();
         held.sort(Comparator.comparing(Lights.Held::id));
         List<Lights.Bounds> before = new ArrayList<>();

         for (List<Lights.Held> group : groups(held)) {
            Lights.Glow glow = null;

            for (Lights.Held light : group) {
               Lights.Glow old = this.glows.remove(light.id());
               if (glow == null) {
                  glow = old;
               } else {
                  drop(old);
               }
            }

            if (glow == null) {
               glow = new Lights.Glow();
            }

            kept.put(group.get(0).id(), glow);
            int pu0 = group.stream().mapToInt(Lights.Held::pu0).min().getAsInt();
            int pv0 = group.stream().mapToInt(Lights.Held::pv0).min().getAsInt();
            int pu1 = group.stream().mapToInt(Lights.Held::pu1).max().getAsInt();
            int pv1 = group.stream().mapToInt(Lights.Held::pv1).max().getAsInt();
            List<Lights.Held> lights = held.stream().filter(light -> light.pu0() < pu1 && pu0 < light.pu1() && light.pv0() < pv1 && pv0 < light.pv1()).toList();
            List<Lights.Bounds> taken = before.stream().filter(b -> b.touches(pu0, pv0, pu1, pv1)).toList();
            before.add(new Lights.Bounds(pu0, pv0, pu1, pv1));
            this.make(glow, lights, taken, pu0, pv0, pu1, pv1, view.layer(), style);
            if (glow.lit && glow.texture != null && glow.layer == view.layer() && lights.stream().map(Lights.Held::id).toList().containsAll(glow.lights)) {
               List<Lights.Bounds> pieces = List.of(glow.bounds());

               for (Lights.Shown other : drawn) {
                  if (!glow.apart(other.glow())) {
                     for (Lights.Bounds hole : other.pieces()) {
                        pieces = cut(pieces, hole);
                     }
                  }
               }

               drawn.add(new Lights.Shown(glow, pieces));
            }
         }

         this.glows.values().forEach(Lights::drop);
         this.glows.clear();
         this.glows.putAll(kept);
         MapCanvas canvas = MapCanvas.of(graphics);
         Circle circle = view.round() ? Circle.in(view) : null;
         int tint = Math.round(Math.max(0.0F, Math.min(1.0F, opacity)) * 255.0F) << 24 | 16777215;

         for (Lights.Shown shown : drawn) {
            Lights.Glow glow = shown.glow();

            for (Lights.Bounds piece : shown.pieces()) {
               float x0 = view.screenX(piece.pu0() / ppu);
               float y0 = view.screenY(piece.pv0() / ppu);
               float x1 = view.screenX(piece.pu1() / ppu);
               float y1 = view.screenY(piece.pv1() / ppu);
               float tu0 = (float)(piece.pu0() - glow.pu0) / glow.edge;
               float tv0 = (float)(piece.pv0() - glow.pv0) / glow.edge;
               float tu1 = (float)(piece.pu1() - glow.pu0) / glow.edge;
               float tv1 = (float)(piece.pv1() - glow.pv0) / glow.edge;
               if (circle != null) {
                  Circle.Quads inside = circle.clip(x0, y0, x1, y1, tu0, tv0, tu1, tv1);
                  if (inside != null) {
                     canvas.quad(glow.texture, MapViewport.sampling(view), inside.corners(), inside.uvs(), tint);
                  }
               } else {
                  canvas.rect(glow.texture, MapViewport.sampling(view), x0, y0, x1, y1, tu0, tv0, tu1, tv1, tint);
               }
            }
         }
      } else {
         this.close();
      }
   }

   private static List<Lights.Bounds> cut(List<Lights.Bounds> pieces, Lights.Bounds hole) {
      List<Lights.Bounds> left = new ArrayList<>();

      for (Lights.Bounds p : pieces) {
         if (!p.touches(hole.pu0(), hole.pv0(), hole.pu1(), hole.pv1())) {
            left.add(p);
         } else {
            int pv0 = Math.max(p.pv0(), hole.pv0());
            int pv1 = Math.min(p.pv1(), hole.pv1());
            if (p.pv0() < hole.pv0()) {
               left.add(new Lights.Bounds(p.pu0(), p.pv0(), p.pu1(), hole.pv0()));
            }

            if (hole.pv1() < p.pv1()) {
               left.add(new Lights.Bounds(p.pu0(), hole.pv1(), p.pu1(), p.pv1()));
            }

            if (p.pu0() < hole.pu0()) {
               left.add(new Lights.Bounds(p.pu0(), pv0, hole.pu0(), pv1));
            }

            if (hole.pu1() < p.pu1()) {
               left.add(new Lights.Bounds(hole.pu1(), pv0, p.pu1(), pv1));
            }
         }
      }

      return left;
   }

   private boolean simulated(Player player, Entity viewer) {
      int reach = this.level.getServerSimulationDistance();
      return Math.abs((player.getBlockX() >> 4) - (viewer.getBlockX() >> 4)) <= reach
         && Math.abs((player.getBlockZ() >> 4) - (viewer.getBlockZ() >> 4)) <= reach;
   }

   private static boolean onRadar(Player player, Player self, Entity viewer, float partialTick) {
      Settings settings = Settings.get();
      return settings.radarPlayers
         && player.isAlive()
         && !player.isInvisibleTo(self)
         && (!player.isSpectator() || self.isSpectator())
         && Math.abs(player.getPosition(partialTick).y - viewer.getPosition(partialTick).y) <= settings.radarRange;
   }

   private static List<List<Lights.Held>> groups(List<Lights.Held> held) {
      List<List<Lights.Held>> groups = new ArrayList<>();

      for (Lights.Held light : held) {
         List<Lights.Held> joined = new ArrayList<>(List.of(light));
         Iterator<List<Lights.Held>> it = groups.iterator();

         while (it.hasNext()) {
            List<Lights.Held> group = it.next();
            if (group.stream().anyMatch(light::touches) && span(joined, group) <= 512) {
               joined.addAll(group);
               it.remove();
            }
         }

         joined.sort(Comparator.comparing(Lights.Held::id));
         groups.add(joined);
      }

      return groups;
   }

   private static int span(List<Lights.Held> a, List<Lights.Held> b) {
      int pu0 = Integer.MAX_VALUE;
      int pv0 = Integer.MAX_VALUE;
      int pu1 = Integer.MIN_VALUE;
      int pv1 = Integer.MIN_VALUE;

      for (List<Lights.Held> group : List.of(a, b)) {
         for (Lights.Held light : group) {
            pu0 = Math.min(pu0, light.pu0());
            pv0 = Math.min(pv0, light.pv0());
            pu1 = Math.max(pu1, light.pu1());
            pv1 = Math.max(pv1, light.pv1());
         }
      }

      return Math.max(pu1 - pu0, pv1 - pv0);
   }

   private void make(Lights.Glow glow, List<Lights.Held> lights, List<Lights.Bounds> taken, int pu0, int pv0, int pu1, int pv1, int layer, MapStyle style) {
      if (glow.pending != null) {
         if (!glow.pending.isDone()) {
            return;
         }

         CompletableFuture<Lights.Made> pending = glow.pending;
         glow.pending = null;

         try {
            show(glow, pending.join());
         } catch (CompletionException | CancellationException e) {
            Diorama.LOGGER.warn("Couldn't light the map around a held light", e);
         }
      }

      SurfaceArea area = this.map.surfaces(layer, pu0, pv0, pu1, pv1);
      double sin = Projection.sin();
      double cos = Projection.cos();
      List<Object> key = List.of(lights, taken, layer, style, sin, cos, Arrays.asList(area.tiles()));
      if (!key.equals(glow.madeFor)) {
         glow.madeFor = key;
         BlockLooks looks = BlockLooks.get();
         VoxelReader reader = new VoxelReader(this.map, looks, true);
         glow.pending = CompletableFuture.supplyAsync(
            () -> build(reader, looks, area, lights, taken, layer, pu0, pv0, pu1 - pu0, pv1 - pv0, style, sin, cos), WORKER
         );
      }
   }

   private static Lights.Made build(
      VoxelReader reader,
      BlockLooks looks,
      SurfaceArea area,
      List<Lights.Held> lights,
      List<Lights.Bounds> taken,
      int layer,
      int pu0,
      int pv0,
      int width,
      int height,
      MapStyle style,
      double sin,
      double cos
   ) {
      int ppu = 4;
      float[][] levels = new float[lights.size()][];

      for (int i = 0; i < levels.length; i++) {
         Lights.Held light = lights.get(i);
         levels[i] = spread(reader, looks, light.sx(), light.sy(), light.sz(), light.carried(), light.carried() - 1, 2 * light.carried() - 1);
      }

      int step = 32;
      int edge = (Math.max(width, height) + step - 1) / step * step;
      int[] pixels = new int[edge * edge];
      float[] scratch = new float[4];
      boolean lit = false;

      for (int py = 0; py < height; py++) {
         for (int px = 0; px < width; px++) {
            int pu = pu0 + px;
            int pv = pv0 + py;
            float y = area.y(pu, pv);
            if (!Float.isNaN(y) && !taken(taken, pu, pv)) {
               boolean top = area.top(pu, pv);
               double wx = (pu + 0.5) / ppu;
               double wy = y + (top ? 0.5 : 0.0);
               double wz = ((pv + 0.5) / ppu + y * cos) / sin + (top ? 0.0 : 0.5);
               float light = 0.0F;

               for (int i = 0; i < levels.length; i++) {
                  Lights.Held held = lights.get(i);
                  if (pu >= held.pu0() && pu < held.pu1() && pv >= held.pv0() && pv < held.pv1()) {
                     int reach = held.carried() - 1;
                     int x0 = held.sx() - reach;
                     int y0 = held.sy() - reach;
                     int z0 = held.sz() - reach;
                     light = Math.max(light, smooth(levels[i], 2 * reach + 1, reader, x0, y0, z0, wx - x0, wy - y0, wz - z0));
                  }
               }

               int own = area.blockLight(pu, pv);
               if (light > own) {
                  int alpha = Math.round(Math.min(1.0F, light - own) * 255.0F);
                  pixels[py * edge + px] = area.relit(pu, pv, light, alpha, style, scratch);
                  lit = true;
               }
            }
         }
      }

      return new Lights.Made(
         lights.stream().map(Lights.Held::id).toList(), taken, layer, pu0, pv0, width, height, edge, lit ? Mipmaps.build(pixels, edge) : null
      );
   }

   private static boolean taken(List<Lights.Bounds> taken, int pu, int pv) {
      for (Lights.Bounds bounds : taken) {
         if (bounds.contains(pu, pv)) {
            return true;
         }
      }

      return false;
   }

   private static void show(Lights.Glow glow, Lights.Made made) {
      glow.lit = made.mips() != null;
      if (glow.lit) {
         if (glow.texture != null && glow.edge != made.edge()) {
            glow.texture.close();
            glow.texture = null;
         }

         if (glow.texture == null) {
            glow.texture = MapTexture.create("diorama held light", made.edge(), 6);
         }

         for (int level = 0; level < 6; level++) {
            glow.texture.write(level, 0, 0, made.edge() >> level, made.mips()[level]);
         }

         glow.edge = made.edge();
         glow.layer = made.layer();
         glow.lights = made.lights();
         glow.taken = made.taken();
         glow.pu0 = made.pu0();
         glow.pv0 = made.pv0();
         glow.width = made.width();
         glow.height = made.height();
      }
   }

   private static float[] spread(VoxelReader reader, BlockLooks looks, int sx, int sy, int sz, int carried, int reach, int side) {
      float[] levels = new float[side * side * side];
      byte[] open = new byte[levels.length];
      byte[] faces = new byte[levels.length];
      PriorityQueue<Long> queue = new PriorityQueue<>();
      int start = (reach * side + reach) * side + reach;
      levels[start] = carried;
      queue.add((long)start);

      while (!queue.isEmpty()) {
         long entry = queue.poll();
         int cell = (int)entry;
         float distance = Float.intBitsToFloat((int)(entry >>> 32));
         if (!(carried - distance < levels[cell])) {
            int x = cell % side;
            int z = cell / side % side;
            int y = cell / (side * side);

            for (int[] step : STEPS) {
               int nx = x + step[0];
               int ny = y + step[1];
               int nz = z + step[2];
               if (nx >= 0 && ny >= 0 && nz >= 0 && nx < side && ny < side && nz < side) {
                  int next = (ny * side + nz) * side + nx;
                  float further = distance + STEP_LENGTHS[Math.abs(step[0]) + Math.abs(step[1]) + Math.abs(step[2])];
                  if (!(carried - further <= levels[next]) && passes(reader, looks, open, faces, side, sx - reach, sy - reach, sz - reach, x, y, z, step)) {
                     levels[next] = carried - further;
                     queue.add((long)Float.floatToIntBits(further) << 32 | next);
                  }
               }
            }
         }
      }

      return levels;
   }

   private static boolean passes(
      VoxelReader reader, BlockLooks looks, byte[] open, byte[] faces, int side, int x0, int y0, int z0, int x, int y, int z, int[] step
   ) {
      int here = (y * side + z) * side + x;
      look(reader, looks, open, faces, here, x0 + x, y0 + y, z0 + z);

      for (int axis = 0; axis < 3; axis++) {
         if (step[axis] != 0 && (faces[here] & side(axis, step[axis])) != 0) {
            return false;
         }
      }

      for (int c = 1; c < 8; c++) {
         int dx = (c & 1) == 0 ? 0 : step[0];
         int dy = (c & 2) == 0 ? 0 : step[1];
         int dz = (c & 4) == 0 ? 0 : step[2];
         if (c == ((dx != 0 ? 1 : 0) | (dy != 0 ? 2 : 0) | (dz != 0 ? 4 : 0))) {
            int cell = ((y + dy) * side + z + dz) * side + x + dx;
            look(reader, looks, open, faces, cell, x0 + x + dx, y0 + y + dy, z0 + z + dz);
            if (open[cell] == 2
               || dx != 0 && (faces[cell] & side(0, -dx)) != 0
               || dy != 0 && (faces[cell] & side(1, -dy)) != 0
               || dz != 0 && (faces[cell] & side(2, -dz)) != 0) {
               return false;
            }
         }
      }

      return true;
   }

   private static void look(VoxelReader reader, BlockLooks looks, byte[] open, byte[] faces, int cell, int wx, int wy, int wz) {
      if (open[cell] == 0) {
         int solid = reader.solid(wx, wy, wz);
         open[cell] = (byte)(solid != 1 && solid != -1 ? 1 : 2);
         if (solid == 2) {
            int state = reader.state(wx, wy, wz);
            faces[cell] = state < 0 ? 0 : filledSides(looks.shapes[looks.shape[state]]);
         }
      }
   }

   private static int side(int axis, int sign) {
      return 1 << axis * 2 + (sign > 0 ? 1 : 0);
   }

   private static byte filledSides(float[] boxes) {
      int sides = 0;

      for (int i = 0; i < boxes.length; i += 6) {
         for (int axis = 0; axis < 3; axis++) {
            int a = (axis + 1) % 3;
            int b = (axis + 2) % 3;
            if (boxes[i + a] <= 0.001F && boxes[i + a + 3] >= 0.999F && boxes[i + b] <= 0.001F && boxes[i + b + 3] >= 0.999F) {
               if (boxes[i + axis] <= 0.001F) {
                  sides |= side(axis, -1);
               }

               if (boxes[i + axis + 3] >= 0.999F) {
                  sides |= side(axis, 1);
               }
            }
         }
      }

      return (byte)sides;
   }

   private static float smooth(float[] levels, int side, VoxelReader reader, int x0, int y0, int z0, double qx, double qy, double qz) {
      int bx = (int)Math.floor(qx - 0.5);
      int by = (int)Math.floor(qy - 0.5);
      int bz = (int)Math.floor(qz - 0.5);
      double fx = qx - 0.5 - bx;
      double fy = qy - 0.5 - by;
      double fz = qz - 0.5 - bz;
      double sum = 0.0;
      double weights = 0.0;

      for (int c = 0; c < 8; c++) {
         int x = bx + (c & 1);
         int y = by + (c >> 1 & 1);
         int z = bz + (c >> 2 & 1);
         if (x >= 0 && y >= 0 && z >= 0 && x < side && y < side && z < side) {
            int solid = reader.solid(x0 + x, y0 + y, z0 + z);
            if (solid != 1 && solid != -1) {
               double w = ((c & 1) == 0 ? 1.0 - fx : fx) * ((c >> 1 & 1) == 0 ? 1.0 - fy : fy) * ((c >> 2 & 1) == 0 ? 1.0 - fz : fz);
               sum += w * levels[(y * side + z) * side + x];
               weights += w;
            }
         }
      }

      return weights > 0.0 ? (float)(sum / weights) : 0.0F;
   }

   private static int[][] steps() {
      List<int[]> steps = new ArrayList<>();

      for (int dy = -1; dy <= 1; dy++) {
         for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
               if (dx != 0 || dy != 0 || dz != 0) {
                  steps.add(new int[]{dx, dy, dz});
               }
            }
         }
      }

      return steps.toArray(int[][]::new);
   }

   private static void drop(Lights.@Nullable Glow glow) {
      if (glow != null) {
         if (glow.pending != null) {
            glow.pending.cancel(false);
         }

         if (glow.texture != null) {
            glow.texture.close();
         }
      }
   }

   public void close() {
      this.glows.values().forEach(Lights::drop);
      this.glows.clear();
   }

   private record Bounds(int pu0, int pv0, int pu1, int pv1) {
      boolean touches(int pu0, int pv0, int pu1, int pv1) {
         return this.pu0 < pu1 && pu0 < this.pu1 && this.pv0 < pv1 && pv0 < this.pv1;
      }

      boolean contains(int pu, int pv) {
         return pu >= this.pu0 && pu < this.pu1 && pv >= this.pv0 && pv < this.pv1;
      }
   }

   private static final class Glow {
      @Nullable Object madeFor;
      @Nullable MapTexture texture;
      int edge;
      int layer;
      List<UUID> lights = List.of();
      List<Lights.Bounds> taken = List.of();
      int pu0;
      int pv0;
      int width;
      int height;
      boolean lit;
      @Nullable CompletableFuture<Lights.Made> pending;

      Lights.Bounds bounds() {
         return new Lights.Bounds(this.pu0, this.pv0, this.pu0 + this.width, this.pv0 + this.height);
      }

      boolean apart(Lights.Glow other) {
         Lights.Bounds a = this.bounds();
         Lights.Bounds b = other.bounds();
         return !a.touches(b.pu0(), b.pv0(), b.pu1(), b.pv1()) || this.taken.contains(b) || other.taken.contains(a);
      }
   }

   private record Held(UUID id, int sx, int sy, int sz, int carried, int pu0, int pv0, int pu1, int pv1) {
      static Lights.Held of(UUID id, int sx, int sy, int sz, int carried) {
         double sin = Projection.sin();
         double cos = Projection.cos();
         int ppu = 4;
         return new Lights.Held(
            id,
            sx,
            sy,
            sz,
            carried,
            (sx - carried) * ppu,
            (int)Math.floor(((sz - carried) * sin - (sy + carried + 1) * cos) * ppu),
            (sx + carried + 1) * ppu,
            (int)Math.ceil(((sz + carried + 1) * sin - (sy - carried) * cos) * ppu)
         );
      }

      boolean touches(Lights.Held other) {
         return this.pu0 < other.pu1 && other.pu0 < this.pu1 && this.pv0 < other.pv1 && other.pv0 < this.pv1;
      }
   }

   private record Made(List<UUID> lights, List<Lights.Bounds> taken, int layer, int pu0, int pv0, int width, int height, int edge, int @Nullable [][] mips) {
   }

   private record Shown(Lights.Glow glow, List<Lights.Bounds> pieces) {
   }
}
