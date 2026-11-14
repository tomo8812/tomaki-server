package dev.diorama.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import dev.diorama.Diorama;
import dev.diorama.compat.Pixelmon;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

public final class XaeroRadarIcons {
   public static final ResourceLocation ID = Diorama.id("xaero_radar_icons");
   public static final ResourceManagerReloadListener LISTENER = XaeroRadarIcons::reload;
   private static final String NAMESPACE = "xaerominimap";
   private static final String DEFINITIONS = "entity/icon/definition/";
   private static final String SPRITES = "entity/icon/sprite/";
   private static final int SOLID = 13;
   private static @Nullable Map<EntityType<?>, XaeroRadarIcons.Definition> definitions;
   private static final Map<XaeroRadarIcons.SpriteKey, XaeroRadarIcons.Sprite> sprites = new HashMap<>();
   private static final Set<ResourceLocation> unreadable = new HashSet<>();

   private XaeroRadarIcons() {
   }

   static XaeroRadarIcons.@Nullable Icon of(Entity entity) {
      Map<EntityType<?>, XaeroRadarIcons.Definition> loaded = definitions;
      if (loaded == null) {
         load(Minecraft.getInstance().getResourceManager());
         loaded = definitions;
      }

      XaeroRadarIcons.Definition definition = loaded.get(entity.getType());
      XaeroRadarIcons.Icon icon = null;
      if (definition != null) {
         if (!definition.byTexture().isEmpty()) {
            ResourceLocation texture = MobFaces.texture(entity);
            icon = texture == null ? null : definition.byTexture().get(texture.toString());
         }

         if (icon == null) {
            icon = definition.fallback();
         }
      }

      return icon != null && icon != XaeroRadarIcons.Basic.OWN_FACE ? icon : pokemon(entity);
   }

   private static XaeroRadarIcons.@Nullable Icon pokemon(Entity entity) {
      ResourceLocation location = Pixelmon.sprite(entity);
      if (location == null) {
         return CobblemonPortraits.of(entity);
      }

      if (unreadable.contains(location)) {
         return null;
      }

      ResourceManager manager = Minecraft.getInstance().getResourceManager();
      List<String> problems = new ArrayList<>();
      XaeroRadarIcons.Sprite sprite = sprite(location, true, false, manager, problems);
      if (sprite == null) {
         unreadable.add(location);
         Diorama.LOGGER.warn("Radar icon for {}: {}", entity.getType(), String.join("; ", problems));
      }

      return sprite;
   }

   public static @Nullable String describe(Entity entity) {
      XaeroRadarIcons.Icon icon = of(entity);
      if (icon instanceof XaeroRadarIcons.Basic basic) {
         return basic.name().toLowerCase(Locale.ROOT);
      } else if (icon instanceof XaeroRadarIcons.ItemIcon item) {
         return "item " + BuiltInRegistries.ITEM.getKey(item.stack().getItem());
      } else {
         return icon instanceof XaeroRadarIcons.Sprite sprite
            ? (sprite.outlined ? "outlined_sprite " : (sprite.flipped ? "sprite " : "normal_sprite ")) + sprite.location + " " + sprite.w + "x" + sprite.h
            : null;
      }
   }

   public static void reload(ResourceManager manager) {
      load(manager);
      WaypointIcons.reload();
      MinimapFrames.reload();
   }

   private static void load(ResourceManager manager) {
      for (XaeroRadarIcons.Sprite sprite : sprites.values()) {
         sprite.close();
      }

      sprites.clear();
      unreadable.clear();
      CobblemonPortraits.clear();
      Map<EntityType<?>, XaeroRadarIcons.Definition> loaded = new HashMap<>();

      for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
         ResourceLocation mob = BuiltInRegistries.ENTITY_TYPE.getKey(type);
         ResourceLocation file = ResourceLocation.fromNamespaceAndPath(
            "xaerominimap", "entity/icon/definition/" + mob.getNamespace() + "/" + mob.getPath() + ".json"
         );
         Optional<Resource> resource = manager.getResource(file);
         if (!resource.isEmpty()) {
            List<String> problems = new ArrayList<>();

            try (Reader reader = resource.get().openAsReader()) {
               XaeroRadarIcons.Definition definition = parse(JsonParser.parseReader(reader), manager, problems);
               if (definition != null) {
                  loaded.put(type, definition);
               }
            } catch (IOException | RuntimeException e) {
               problems.add(String.valueOf(e.getMessage()));
            }

            if (!problems.isEmpty()) {
               Diorama.LOGGER.warn("Radar icon file {} (from {}): {}", new Object[]{file, resource.get().sourcePackId(), String.join("; ", problems)});
            }
         }
      }

      definitions = loaded;
      if (!loaded.isEmpty()) {
         Diorama.LOGGER.info("Radar icons from Xaero's Minimap icon files: {} loaded", loaded.size());
      }
   }

   private static XaeroRadarIcons.@Nullable Definition parse(JsonElement json, ResourceManager manager, List<String> problems) {
      if (!json.isJsonObject()) {
         problems.add("not a JSON object");
         return null;
      }

      JsonElement variants = json.getAsJsonObject().get("variants");
      if (variants != null && !variants.isJsonNull()) {
         if (!variants.isJsonObject()) {
            problems.add("\"variants\" isn't an object");
            return null;
         }

         Map<String, XaeroRadarIcons.Icon> byTexture = new HashMap<>();
         XaeroRadarIcons.Icon fallback = XaeroRadarIcons.Basic.OWN_FACE;

         for (Entry<String, JsonElement> entry : ((JsonObject)variants).entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            XaeroRadarIcons.Icon icon = value.isJsonPrimitive() ? icon(value.getAsString(), manager, problems) : null;
            if (icon == null) {
               if (!value.isJsonPrimitive()) {
                  problems.add("\"" + key + "\" isn't a string");
               }
            } else if (key.equals("default")) {
               fallback = icon;
            } else {
               byTexture.put(key, icon);
            }
         }

         return fallback == XaeroRadarIcons.Basic.OWN_FACE && byTexture.values().stream().allMatch(iconx -> iconx == XaeroRadarIcons.Basic.OWN_FACE)
            ? null
            : new XaeroRadarIcons.Definition(Map.copyOf(byTexture), fallback);
      } else {
         return null;
      }
   }

   private static XaeroRadarIcons.@Nullable Icon icon(String value, ResourceManager manager, List<String> problems) {
      String[] parts = value.split(":");
      if (parts.length == 0) {
         problems.add("\"" + value + "\" isn't a form Xaero's reads");
         return null;
      }

      switch (parts[0]) {
         case "dot":
            return XaeroRadarIcons.Basic.DOT;
         case "model":
            if (parts.length > 2) {
               problems.add("\"" + value + "\" isn't a form Xaero's reads");
               return null;
            }

            return XaeroRadarIcons.Basic.OWN_FACE;
         case "item":
            if (parts.length == 1) {
               return XaeroRadarIcons.Basic.OWN_FACE;
            } else if (parts.length > 3) {
               problems.add("\"" + value + "\" isn't a form Xaero's reads");
               return null;
            } else {
               ResourceLocation id = parts.length == 2 ? ResourceLocation.tryParse(parts[1]) : ResourceLocation.tryBuild(parts[1], parts[2]);
               Optional<Item> item = id == null ? Optional.empty() : BuiltInRegistries.ITEM.getOptional(id);
               if (item.isEmpty()) {
                  problems.add("no item " + value.substring("item:".length()));
                  return XaeroRadarIcons.Basic.DOT;
               }

               ItemStack stack = new ItemStack((ItemLike)item.get());
               return stack.isEmpty() ? XaeroRadarIcons.Basic.DOT : new XaeroRadarIcons.ItemIcon(stack);
            }
         case "normal_sprite":
         case "outlined_sprite":
         case "sprite":
            if (parts.length != 2) {
               problems.add("\"" + value + "\" isn't a form Xaero's reads");
               return null;
            } else {
               ResourceLocation location = ResourceLocation.tryBuild("xaerominimap", "entity/icon/sprite/" + parts[1]);
               if (location == null) {
                  problems.add("\"" + parts[1] + "\" isn't a valid file name");
                  return XaeroRadarIcons.Basic.OWN_FACE;
               }

               XaeroRadarIcons.Sprite sprite = sprite(location, parts[0].equals("outlined_sprite"), parts[0].equals("sprite"), manager, problems);
               return sprite == null ? XaeroRadarIcons.Basic.OWN_FACE : sprite;
            }
         default:
            problems.add("\"" + value + "\" isn't a form Xaero's reads");
            return null;
      }
   }

   private static XaeroRadarIcons.@Nullable Sprite sprite(
      ResourceLocation location, boolean outlined, boolean flipped, ResourceManager manager, List<String> problems
   ) {
      XaeroRadarIcons.SpriteKey key = new XaeroRadarIcons.SpriteKey(location, outlined, flipped);
      XaeroRadarIcons.Sprite cached = sprites.get(key);
      if (cached != null) {
         return cached;
      }

      Optional<Resource> resource = manager.getResource(location);
      if (resource.isEmpty()) {
         problems.add("no image " + location);
         return null;
      }

      try (InputStream in = resource.get().open()) {
         NativeImage image = NativeImage.read(in);

         XaeroRadarIcons.Sprite var17;
         label100: {
            try {
               XaeroRadarIcons.Sprite sprite = XaeroRadarIcons.Sprite.cropped(location, image, outlined, flipped);
               if (sprite != null) {
                  sprites.put(key, sprite);
                  var17 = sprite;
                  break label100;
               }

               problems.add("image " + location + " is see-through");
               var17 = null;
            } catch (Throwable var14) {
               if (image != null) {
                  try {
                     image.close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }
               }

               throw var14;
            }

            if (image != null) {
               image.close();
            }

            return var17;
         }

         if (image != null) {
            image.close();
         }

         return var17;
      } catch (IOException | RuntimeException e) {
         problems.add("can't read image " + location + ": " + e.getMessage());
         return null;
      }
   }

   enum Basic implements XaeroRadarIcons.Icon {
      OWN_FACE,
      DOT;

      // $VF: synthetic method
      private static XaeroRadarIcons.Basic[] $values() {
         return new XaeroRadarIcons.Basic[]{OWN_FACE, DOT};
      }
   }

   private record Definition(Map<String, XaeroRadarIcons.Icon> byTexture, XaeroRadarIcons.Icon fallback) {
   }

   sealed interface Icon permits XaeroRadarIcons.Basic, XaeroRadarIcons.Sprite, XaeroRadarIcons.ItemIcon {
   }

   record ItemIcon(ItemStack stack) implements XaeroRadarIcons.Icon {
   }

   static final class Sprite implements XaeroRadarIcons.Icon {
      final ResourceLocation location;
      final boolean outlined;
      final boolean flipped;
      final int w;
      final int h;
      private final int[] argb;
      private final Map<Integer, XaeroRadarIcons.Sprite.Baked> baked = new HashMap<>();

      private Sprite(ResourceLocation location, boolean outlined, boolean flipped, int w, int h, int[] argb) {
         this.location = location;
         this.outlined = outlined;
         this.flipped = flipped;
         this.w = w;
         this.h = h;
         this.argb = argb;
      }

      static XaeroRadarIcons.@Nullable Sprite cropped(ResourceLocation location, NativeImage image, boolean outlined, boolean flipped) {
         int width = image.getWidth();
         int height = image.getHeight();
         int[] all = new int[width * height];
         int x0 = width;
         int y0 = height;
         int x1 = -1;
         int y1 = -1;

         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
               int abgr = image.getPixelRGBA(x, y);
               int argb = abgr & -16711936 | abgr >> 16 & 0xFF | (abgr & 0xFF) << 16;
               all[(flipped ? height - 1 - y : y) * width + x] = argb;
               if (argb >>> 24 >= 13) {
                  int row = flipped ? height - 1 - y : y;
                  x0 = Math.min(x0, x);
                  y0 = Math.min(y0, row);
                  x1 = Math.max(x1, x);
                  y1 = Math.max(y1, row);
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

         return new XaeroRadarIcons.Sprite(location, outlined, flipped, w, h, argb);
      }

      void drawPixel(IconBatch icons, float x, float y, float size, int color, int outline, int alpha) {
         int factor = this.factor(size);
         XaeroRadarIcons.Sprite.Baked b = this.baked(factor);
         float texel = Radar.texel(size, Math.max(b.w(), b.h()));
         float x0 = x - b.w() * texel / 2.0F;
         float y0 = y - b.h() * texel / 2.0F;
         GuiGraphics graphics = icons.graphics();
         graphics.pose().pushPose();
         graphics.pose().translate(x0, y0, 0.0F);
         graphics.pose().scale(texel, texel, 1.0F);
         int shadow = Radar.withAlpha(1610612736, alpha);
         if (this.outlined) {
            this.mask(icons, b, 0.0F, 0.0F, shadow);
            this.mask(icons, b, -1.0F, -1.0F, outline);
         } else {
            this.image(icons, b, 0.0F, 0.0F, b.w() + 2, b.h() + 2, shadow);
         }

         this.image(icons, b, -1.0F, -1.0F, b.w() + 1, b.h() + 1, color);
         graphics.pose().popPose();
      }

      void drawFitted(IconBatch icons, float x, float y, float size, int color) {
         XaeroRadarIcons.Sprite.Baked b = this.baked(this.factor(size));
         float scale = size / Math.max(b.w(), b.h());
         float w = b.w() * scale;
         float h = b.h() * scale;
         GuiGraphics graphics = icons.graphics();
         graphics.pose().pushPose();
         graphics.pose().translate(x - w / 2.0F, y - h / 2.0F, 0.0F);
         graphics.pose().scale(scale, scale, 1.0F);
         this.image(icons, b, -1.0F, -1.0F, b.w() + 1, b.h() + 1, color);
         graphics.pose().popPose();
      }

      private void image(IconBatch icons, XaeroRadarIcons.Sprite.Baked b, float x0, float y0, float x1, float y1, int color) {
         float edge = b.edge();
         icons.rect(b.texture(), Sampling.CLAMP_CRISP, x0, y0, x1, y1, 0.0F, 0.0F, (b.w() + 2) / edge, (b.h() + 2) / edge, color);
      }

      private void mask(IconBatch icons, XaeroRadarIcons.Sprite.Baked b, float dx, float dy, int color) {
         float edge = b.edge();
         float u0 = b.edge() / 2 / edge;
         icons.rect(
            b.texture(), Sampling.CLAMP_CRISP, dx, dy, dx + b.w() + 2.0F, dy + b.h() + 2.0F, u0, 0.0F, u0 + (b.w() + 2) / edge, (b.h() + 2) / edge, color
         );
      }

      private int factor(float size) {
         float most = Radar.texel(size, 8) * 8.0F * 1.5F;
         int longest = Math.max(this.w, this.h);
         int factor = 1;

         while (true) {
            int texels = (longest + factor - 1) / factor;
            if (texels == 1 || texels * Radar.texel(size, texels) <= most) {
               return factor;
            }

            factor++;
         }
      }

      private XaeroRadarIcons.Sprite.Baked baked(int factor) {
         XaeroRadarIcons.Sprite.Baked b = this.baked.get(factor);
         if (b == null) {
            b = this.bake(factor);
            this.baked.put(factor, b);
         }

         return b;
      }

      private XaeroRadarIcons.Sprite.Baked bake(int factor) {
         XaeroRadarIcons.Sprite.Baked b = this.bake(factor, false);
         if (b != null) {
            return b;
         }

         b = this.bake(factor, true);
         return Objects.requireNonNull(b);
      }

      private XaeroRadarIcons.Sprite.@Nullable Baked bake(int factor, boolean anyCover) {
         int sw = (this.w + factor - 1) / factor;
         int sh = (this.h + factor - 1) / factor;
         int pw = sw + 2;
         int ph = sh + 2;
         int edge = 2 * Math.max(pw, ph);
         int[] abgr = new int[edge * edge];
         boolean[] solid = new boolean[pw * ph];
         boolean any = false;

         for (int sy = 0; sy < sh; sy++) {
            for (int sx = 0; sx < sw; sx++) {
               int pixel = factor == 1 ? this.argb[sy * this.w + sx] : this.average(sx * factor, sy * factor, factor, anyCover);
               int a = pixel >>> 24;
               if (a >= 13) {
                  abgr[(sy + 1) * edge + sx + 1] = pixel & -16711936 | pixel >> 16 & 0xFF | (pixel & 0xFF) << 16;
                  solid[(sy + 1) * pw + sx + 1] = true;
                  any = true;
               }
            }
         }

         if (!any) {
            return null;
         }

         int maskX = edge / 2;

         for (int y = 0; y < ph; y++) {
            for (int x = 0; x < pw; x++) {
               boolean near = false;

               for (int dy = -1; dy <= 1 && !near; dy++) {
                  for (int dx = -1; dx <= 1 && !near; dx++) {
                     int nx = x + dx;
                     int ny = y + dy;
                     near = nx >= 0 && ny >= 0 && nx < pw && ny < ph && solid[ny * pw + nx];
                  }
               }

               if (near) {
                  abgr[y * edge + maskX + x] = -1;
               }
            }
         }

         MapTexture texture = MapTexture.create("diorama radar icon " + this.location, edge, 1);
         texture.write(0, 0, 0, edge, abgr);
         return new XaeroRadarIcons.Sprite.Baked(texture, edge, sw, sh);
      }

      private int average(int x0, int y0, int factor, boolean anyCover) {
         double a = 0.0;
         double r = 0.0;
         double g = 0.0;
         double b = 0.0;
         int count = 0;

         for (int y = y0; y < Math.min(this.h, y0 + factor); y++) {
            for (int x = x0; x < Math.min(this.w, x0 + factor); x++) {
               int pixel = this.argb[y * this.w + x];
               double pa = (pixel >>> 24) / 255.0;
               a += pa;
               r += (pixel >> 16 & 0xFF) * pa;
               g += (pixel >> 8 & 0xFF) * pa;
               b += (pixel & 0xFF) * pa;
               count++;
            }
         }

         return (anyCover ? !(a * 255.0 < 13.0) : !(a < count / 2.0))
            ? 0xFF000000 | (int)Math.round(r / a) << 16 | (int)Math.round(g / a) << 8 | (int)Math.round(b / a)
            : 0;
      }

      void close() {
         for (XaeroRadarIcons.Sprite.Baked b : this.baked.values()) {
            b.texture().close();
         }

         this.baked.clear();
      }

      private record Baked(MapTexture texture, int edge, int w, int h) {
      }
   }

   private record SpriteKey(ResourceLocation location, boolean outlined, boolean flipped) {
   }
}
