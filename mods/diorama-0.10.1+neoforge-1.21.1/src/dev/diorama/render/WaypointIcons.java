package dev.diorama.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

public final class WaypointIcons {
   public static final int TILE = 11;
   public static final List<String> OFFERED = List.of(
      "diorama:home",
      "diorama:bed",
      "diorama:farm",
      "diorama:animals",
      "diorama:villagers",
      "diorama:mine",
      "diorama:storage",
      "diorama:portal",
      "diorama:danger",
      "diorama:treasure"
   );
   public static final String BED = "diorama:bed";
   public static final String PORTAL = "diorama:portal";
   private static final String FOLDER = "textures/waypoint_icons/";
   private static final Map<String, Optional<PinArt.Image>> IMAGES = new HashMap<>();

   private WaypointIcons() {
   }

   public static PinArt.@Nullable Image image(String id) {
      Optional<PinArt.Image> known = IMAGES.get(id);
      if (known == null) {
         PinArt.Image loaded = load(id, Minecraft.getInstance().getResourceManager());
         known = Optional.ofNullable(loaded == null ? null : tiled(loaded));
         IMAGES.put(id, known);
      }

      return known.orElse(null);
   }

   public static void reload() {
      IMAGES.clear();
      Pins.forgetIcons();
   }

   public static List<String> all() {
      List<String> ids = new ArrayList<>(OFFERED);
      List<String> added = new ArrayList<>();

      for (ResourceLocation file : Minecraft.getInstance()
         .getResourceManager()
         .listResources("textures/waypoint_icons", filex -> filex.getPath().endsWith(".png"))
         .keySet()) {
         String id = file.getNamespace() + ":" + file.getPath().substring("textures/waypoint_icons/".length(), file.getPath().length() - ".png".length());
         if (!ids.contains(id)) {
            added.add(id);
         }
      }

      added.sort(null);
      ids.addAll(added);
      return ids;
   }

   private static PinArt.@Nullable Image load(String id, ResourceManager manager) {
      ResourceLocation name = ResourceLocation.tryParse(id);
      if (name == null) {
         return null;
      }

      ResourceLocation file = ResourceLocation.fromNamespaceAndPath(name.getNamespace(), "textures/waypoint_icons/" + name.getPath() + ".png");
      Optional<Resource> resource = manager.getResource(file);
      if (resource.isEmpty()) {
         return null;
      }

      try (InputStream in = resource.get().open()) {
         NativeImage image = NativeImage.read(in);

         PinArt.Image var7;
         try {
            var7 = cropped(image);
         } catch (Throwable var11) {
            if (image != null) {
               try {
                  image.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (image != null) {
            image.close();
         }

         return var7;
      } catch (IOException | RuntimeException e) {
         return null;
      }
   }

   private static PinArt.@Nullable Image cropped(NativeImage image) {
      int width = image.getWidth();
      int height = image.getHeight();
      int[] all = new int[width * height];
      int x0 = width;
      int y0 = height;
      int x1 = -1;
      int y1 = -1;

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int argb = argb(image, x, y);
            all[y * width + x] = argb;
            if (argb >>> 24 >= 128) {
               x0 = Math.min(x0, x);
               y0 = Math.min(y0, y);
               x1 = Math.max(x1, x);
               y1 = Math.max(y1, y);
            }
         }
      }

      if (x1 < 0) {
         return null;
      }

      int w = x1 - x0 + 1;
      int h = y1 - y0 + 1;
      int[] argb = new int[w * h];

      for (int y = 0; y < h; y++) {
         System.arraycopy(all, (y0 + y) * width + x0, argb, y * w, w);
      }

      return PinArt.fromArgb(w, h, argb);
   }

   static int argb(NativeImage image, int x, int y) {
      int abgr = image.getPixelRGBA(x, y);
      return abgr & -16711936 | abgr >> 16 & 0xFF | (abgr & 0xFF) << 16;
   }

   private static PinArt.Image tiled(PinArt.Image image) {
      int factor = (Math.max(image.width, image.height) + 11 - 1) / 11;
      int w = image.width / factor;
      int h = image.height / factor;
      int x0 = (11 - w) / 2;
      int y0 = (11 - h) / 2;
      int[] argb = new int[121];

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            argb[(y0 + y) * 11 + x0 + x] = image.argb(x * factor, y * factor);
         }
      }

      return PinArt.fromArgb(11, 11, argb);
   }
}
