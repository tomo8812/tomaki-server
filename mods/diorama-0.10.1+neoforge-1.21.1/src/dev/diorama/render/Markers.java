package dev.diorama.render;

import dev.diorama.Projection;
import dev.diorama.Settings;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.gui.GuiGraphics;
import org.jspecify.annotations.Nullable;

public final class Markers {
   private static final int HEADINGS = 72;
   private static final int UPLOADS_PER_FRAME = 4;
   private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(r -> {
      Thread thread = new Thread(r, "Diorama arrow");
      thread.setDaemon(true);
      return thread;
   });
   private static final Map<Long, MapTexture> ARROWS = new HashMap<>();
   private static final Map<Long, CompletableFuture<int[]>> DRAWING = new HashMap<>();
   private static final Set<Long> STARTED = new HashSet<>();

   private Markers() {
   }

   public static float onMap(float yawDegrees) {
      return yawDegrees - 90 * Projection.facing().turns;
   }

   public static void player(GuiGraphics graphics, float x, float y, float yawDegrees, float size) {
      int heading = Math.floorMod(Math.round(yawDegrees / 5.0F), 72);
      Settings settings = Settings.get();
      int accent = settings.arrowRgb();
      MapTexture texture = arrow(heading, size, accent, settings.arrowColor);
      if (texture == null) {
         flat(graphics, x, y, yawDegrees, size, accent);
      } else {
         float half = 18.0F;
         MapCanvas.of(graphics).rect(texture, Sampling.CLAMP_LINEAR, x - half, y - half, x + half, y + half, 0.0F, 0.0F, 1.0F, 1.0F, -1);
      }
   }

   private static @Nullable MapTexture arrow(int heading, float size, int accent, int colour) {
      double sin = Projection.sin();
      double cos = Projection.cos();
      long style = ((long)(colour & 7) << 12 | Math.round(size * 4.0F) & 4095) << 40 | Math.round(sin * 10000.0) << 20 | Math.round(cos * 10000.0);
      if (STARTED.add(style)) {
         for (int i = 0; i < 72; i++) {
            int h = (heading + i) % 72;
            double yaw = h * 360.0 / 72.0;
            DRAWING.put(style * 72L + h, CompletableFuture.supplyAsync(() -> {
               PinArt.Image image = ArrowArt.voxel(yaw, size, sin, cos, accent);
               return image.abgr(image.width);
            }, WORKER));
         }
      }

      long wanted = style * 72L + heading;
      upload(wanted);
      int budget = 4;
      Iterator<Entry<Long, CompletableFuture<int[]>>> pending = DRAWING.entrySet().iterator();

      while (pending.hasNext() && budget > 0) {
         Entry<Long, CompletableFuture<int[]>> entry = pending.next();
         if (entry.getValue().isDone()) {
            ARROWS.put(entry.getKey(), texture(entry.getValue().join()));
            pending.remove();
            budget--;
         }
      }

      return ARROWS.get(style * 72L + heading);
   }

   private static void upload(long key) {
      CompletableFuture<int[]> drawing = DRAWING.get(key);
      if (drawing != null && drawing.isDone()) {
         ARROWS.put(key, texture(drawing.join()));
         DRAWING.remove(key);
      }
   }

   private static MapTexture texture(int[] pixels) {
      int edge = 144;
      MapTexture texture = MapTexture.create("diorama arrow", edge, 1);
      texture.write(0, 0, 0, edge, pixels);
      return texture;
   }

   private static void flat(GuiGraphics graphics, float x, float y, float yawDegrees, float size, int accent) {
      double yaw = Math.toRadians(yawDegrees);
      double dx = -Math.sin(yaw);
      double dy = Math.cos(yaw) * Projection.sin();
      double len = Math.sqrt(dx * dx + dy * dy);
      dx /= len;
      dy /= len;
      quad(graphics, x, y, dx, dy, size + 1.6F, -535817192);
      quad(graphics, x, y, dx, dy, size, -1);
      quad(graphics, x, y, dx, dy, size * 0.55F, 0xFF000000 | accent);
   }

   private static void quad(GuiGraphics graphics, float x, float y, double dx, double dy, float size, int color) {
      double px = -dy;
      double py = dx;
      float tipX = (float)(x + dx * size);
      float tipY = (float)(y + dy * size);
      float leftX = (float)(x - dx * size * 0.75 + px * size * 0.7);
      float leftY = (float)(y - dy * size * 0.75 + py * size * 0.7);
      float rightX = (float)(x - dx * size * 0.75 - px * size * 0.7);
      float rightY = (float)(y - dy * size * 0.75 - py * size * 0.7);
      float notchX = (float)(x - dx * size * 0.35);
      float notchY = (float)(y - dy * size * 0.35);
      MapCanvas.of(graphics).solid(new float[]{tipX, tipY, rightX, rightY, notchX, notchY, leftX, leftY}, color);
   }
}
