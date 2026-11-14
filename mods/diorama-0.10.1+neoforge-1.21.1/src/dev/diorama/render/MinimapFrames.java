package dev.diorama.render;

import com.mojang.blaze3d.platform.NativeImage;
import dev.diorama.Diorama;
import dev.diorama.Settings;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jspecify.annotations.Nullable;

public final class MinimapFrames {
   private static final float PICTURE = 128.0F;
   private static final int OUTSIDE = 4;
   private static final int BAND = 16;
   private static final int LARGEST_FILE = 2048;
   private static final String FOLDER = "textures/gui/minimap_frame/";
   private static final Map<String, Optional<MinimapFrames.Picture>> PICTURES = new HashMap<>();

   private MinimapFrames() {
   }

   public static void draw(GuiGraphics graphics, Settings.Frame frame, boolean round, int x, int y, int size) {
      if (frame != Settings.Frame.NONE) {
         MinimapFrames.Picture picture = picture(frame.name().toLowerCase(Locale.ROOT) + (round ? "_round" : "_square"));
         if (picture != null) {
            Circle.Quads quads = round ? new Circle(x + size / 2.0F, y + size / 2.0F, size / 2.0F + 4.0F).ring(16.0F, 0.125F) : square(x - 4, y - 4, size + 8);
            float[] uvs = quads.uvs();

            for (int i = 0; i < uvs.length; i += 2) {
               uvs[i] *= picture.u;
               uvs[i + 1] = uvs[i + 1] * picture.v;
            }

            MapCanvas.of(graphics).quad(picture.texture, round ? Sampling.CLAMP_LINEAR : Sampling.CLAMP_CRISP, quads.corners(), uvs, -1);
         }
      }
   }

   public static void reload() {
      PICTURES.values().forEach(known -> known.ifPresent(picture -> picture.texture.close()));
      PICTURES.clear();
   }

   private static Circle.Quads square(int x, int y, int width) {
      float length = width - 32;
      float tile = 96.0F;
      int tiles = (int)Math.ceil(length / tile);
      tiles += 1 - tiles % 2;
      float start = (length - tiles * tile) / 2.0F;
      float[] corners = new float[(4 + 4 * tiles) * 8];
      float[] uvs = new float[corners.length];
      int quad = 0;
      float far = 112.0F;
      int x1 = x + width - 16;
      int y1 = y + width - 16;
      quad = rect(corners, uvs, quad, x, y, 16.0F, 16.0F, 0.0F, 0.0F);
      quad = rect(corners, uvs, quad, x1, y, 16.0F, 16.0F, far, 0.0F);
      quad = rect(corners, uvs, quad, x, y1, 16.0F, 16.0F, 0.0F, far);
      quad = rect(corners, uvs, quad, x1, y1, 16.0F, 16.0F, far, far);

      for (int i = 0; i < tiles; i++) {
         float tileStart = start + i * tile;
         float from = Math.max(0.0F, tileStart);
         float to = Math.min(length, tileStart + tile);
         float along = 16.0F + from - tileStart;
         quad = rect(corners, uvs, quad, x + 16 + from, y, to - from, 16.0F, along, 0.0F);
         quad = rect(corners, uvs, quad, x + 16 + from, y1, to - from, 16.0F, along, far);
         quad = rect(corners, uvs, quad, x, y + 16 + from, 16.0F, to - from, 0.0F, along);
         quad = rect(corners, uvs, quad, x1, y + 16 + from, 16.0F, to - from, far, along);
      }

      return new Circle.Quads(corners, uvs);
   }

   private static int rect(float[] corners, float[] uvs, int quad, float x, float y, float w, float h, float u, float v) {
      int at = quad * 8;
      float[] xy = new float[]{x, y, x, y + h, x + w, y + h, x + w, y};
      float[] uv = new float[]{u, v, u, v + h, u + w, v + h, u + w, v};
      System.arraycopy(xy, 0, corners, at, 8);

      for (int i = 0; i < 8; i++) {
         uvs[at + i] = uv[i] / 128.0F;
      }

      return quad + 1;
   }

   private static MinimapFrames.@Nullable Picture picture(String name) {
      Optional<MinimapFrames.Picture> known = PICTURES.get(name);
      if (known == null) {
         known = Optional.ofNullable(load(name));
         PICTURES.put(name, known);
      }

      return known.orElse(null);
   }

   private static MinimapFrames.@Nullable Picture load(String name) {
      ResourceLocation file = ResourceLocation.fromNamespaceAndPath("diorama", "textures/gui/minimap_frame/" + name + ".png");
      Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(file);
      if (resource.isEmpty()) {
         return null;
      }

      try (InputStream in = resource.get().open()) {
         NativeImage image = NativeImage.read(in);

         MinimapFrames.Picture var20;
         label125: {
            Object abgr;
            try {
               int width = image.getWidth();
               int height = image.getHeight();
               int edge = Math.max(width, height);
               if (edge <= 2048) {
                  int[] abgrx = new int[edge * edge];

                  for (int y = 0; y < height; y++) {
                     for (int x = 0; x < width; x++) {
                        int argb = WaypointIcons.argb(image, x, y);
                        abgrx[y * edge + x] = argb & -16711936 | argb >> 16 & 0xFF | (argb & 0xFF) << 16;
                     }
                  }

                  MapTexture texture = MapTexture.create("diorama minimap frame", edge, 1);

                  try {
                     texture.write(0, 0, 0, edge, abgrx);
                  } catch (RuntimeException e) {
                     texture.close();
                     throw e;
                  }

                  var20 = new MinimapFrames.Picture(texture, (float)width / edge, (float)height / edge);
                  break label125;
               }

               Diorama.LOGGER.warn("The minimap frame {} is {} x {}; the most is {} x {}", new Object[]{file, width, height, 2048, 2048});
               abgr = null;
            } catch (Throwable var15) {
               if (image != null) {
                  try {
                     image.close();
                  } catch (Throwable var13) {
                     var15.addSuppressed(var13);
                  }
               }

               throw var15;
            }

            if (image != null) {
               image.close();
            }

            return (MinimapFrames.Picture)abgr;
         }

         if (image != null) {
            image.close();
         }

         return var20;
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.warn("Can't read the minimap frame {}", file, e);
         return null;
      }
   }

   private record Picture(MapTexture texture, float u, float v) {
   }
}
