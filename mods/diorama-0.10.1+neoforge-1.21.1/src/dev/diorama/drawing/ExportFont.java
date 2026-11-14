package dev.diorama.drawing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.NativeImage;
import dev.diorama.Diorama;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

final class ExportFont {
   private static final ExportFont.Glyph MISSING = missing();
   private final Map<Integer, ExportFont.Glyph> glyphs = new HashMap<>();
   private final Map<Integer, Float> spaces = new HashMap<>();
   private final Set<Integer> wanted;
   private final Set<String> options;

   private ExportFont(Set<Integer> wanted, Set<String> options) {
      this.wanted = wanted;
      this.options = options;
   }

   static ExportFont load(ResourceManager manager, List<String> texts, Set<String> options) {
      Set<Integer> wanted = new HashSet<>();

      for (String text : texts) {
         text.codePoints().forEach(wanted::add);
         shaped(text).codePoints().forEach(wanted::add);
      }

      ExportFont font = new ExportFont(wanted, options);
      font.read(manager, ResourceLocation.withDefaultNamespace("default"), new HashSet<>());
      return font;
   }

   private boolean passes(JsonObject provider) {
      if (!provider.has("filter")) {
         return true;
      }

      for (Entry<String, JsonElement> entry : provider.getAsJsonObject("filter").entrySet()) {
         if (this.options.contains(entry.getKey()) != entry.getValue().getAsBoolean()) {
            return false;
         }
      }

      return true;
   }

   private boolean has(int c) {
      return this.glyphs.containsKey(c) || this.spaces.containsKey(c);
   }

   private void read(ResourceManager manager, ResourceLocation id, Set<ResourceLocation> seen) {
      if (seen.add(id)) {
         ResourceLocation file = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "font/" + id.getPath() + ".json");
         List<Resource> stack = manager.getResourceStack(file);

         for (int i = stack.size() - 1; i >= 0; i--) {
            try (InputStream in = stack.get(i).open()) {
               JsonObject json = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();

               for (JsonElement element : json.getAsJsonArray("providers")) {
                  this.provider(manager, element, seen);
               }
            } catch (IOException | RuntimeException e) {
               Diorama.LOGGER.warn("Couldn't read font {} for an export", file, e);
            }
         }
      }
   }

   private void provider(ResourceManager manager, JsonElement element, Set<ResourceLocation> seen) {
      try {
         JsonObject provider = element.getAsJsonObject();
         if (!this.passes(provider)) {
            return;
         }

         switch (provider.get("type").getAsString()) {
            case "reference":
               ResourceLocation ref = ResourceLocation.tryParse(provider.get("id").getAsString());
               if (ref != null) {
                  this.read(manager, ref, seen);
               }
               break;
            case "space":
               Map<Integer, Float> advances = new HashMap<>();

               for (Entry<String, JsonElement> advance : provider.getAsJsonObject("advances").entrySet()) {
                  advances.put(advance.getKey().codePointAt(0), advance.getValue().getAsFloat());
               }

               advances.forEach((c, advancex) -> {
                  if (!this.has(c)) {
                     this.spaces.put(c, advancex);
                  }
               });
               break;
            case "bitmap":
               this.bitmap(manager, provider);
               break;
            case "unihex":
               this.unihex(manager, provider);
         }
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read a font provider for an export", e);
      }
   }

   private void bitmap(ResourceManager manager, JsonObject provider) throws IOException {
      ResourceLocation sheet = ResourceLocation.tryParse(provider.get("file").getAsString());
      if (sheet != null) {
         int height = provider.has("height") ? provider.get("height").getAsInt() : 8;
         int ascent = provider.get("ascent").getAsInt();
         JsonArray rows = provider.getAsJsonArray("chars");
         Optional<Resource> resource = manager.getResource(ResourceLocation.fromNamespaceAndPath(sheet.getNamespace(), "textures/" + sheet.getPath()));
         if (!resource.isEmpty() && !rows.isEmpty()) {
            Map<Integer, ExportFont.Glyph> read = new HashMap<>();

            try (InputStream in = resource.get().open()) {
               NativeImage image = NativeImage.read(in);

               label153: {
                  try {
                     String first = rows.get(0).getAsString();
                     int columns = first.codePointCount(0, first.length());
                     if (columns != 0) {
                        int cellW = image.getWidth() / columns;
                        int cellH = image.getHeight() / rows.size();
                        float scale = (float)height / cellH;
                        int row = 0;

                        while (true) {
                           if (row >= rows.size()) {
                              break label153;
                           }

                           int[] points = rows.get(row).getAsString().codePoints().toArray();

                           for (int col = 0; col < points.length; col++) {
                              int c = points[col];
                              if (c != 0 && !this.has(c) && !read.containsKey(c)) {
                                 byte[] alpha = new byte[cellW * cellH];
                                 int width = 0;

                                 for (int y = 0; y < cellH; y++) {
                                    for (int x = 0; x < cellW; x++) {
                                       int a = alpha(image, col * cellW + x, row * cellH + y);
                                       alpha[y * cellW + x] = (byte)a;
                                       if (a != 0) {
                                          width = Math.max(width, x + 1);
                                       }
                                    }
                                 }

                                 read.put(c, new ExportFont.Glyph(alpha, cellW, cellH, scale, 7 - ascent, (int)(0.5 + width * scale) + 1, 1.0F));
                              }
                           }

                           row++;
                        }
                     }
                  } catch (Throwable var27) {
                     if (image != null) {
                        try {
                           image.close();
                        } catch (Throwable var26) {
                           var27.addSuppressed(var26);
                        }
                     }

                     throw var27;
                  }

                  if (image != null) {
                     image.close();
                  }

                  return;
               }

               if (image != null) {
                  image.close();
               }
            }

            this.glyphs.putAll(read);
         }
      }
   }

   private void unihex(ResourceManager manager, JsonObject provider) throws IOException {
      Set<Integer> missing = new HashSet<>();

      for (int c : this.wanted) {
         if (!this.has(c)) {
            missing.add(c);
         }
      }

      ResourceLocation hex = ResourceLocation.tryParse(provider.get("hex_file").getAsString());
      Optional<Resource> resource = hex == null ? Optional.empty() : manager.getResource(hex);
      if (!missing.isEmpty() && !resource.isEmpty()) {
         Map<Integer, int[]> sizes = new HashMap<>();
         if (provider.has("size_overrides")) {
            for (JsonElement element : provider.getAsJsonArray("size_overrides")) {
               JsonObject range = element.getAsJsonObject();
               int from = range.get("from").getAsString().codePointAt(0);
               int to = range.get("to").getAsString().codePointAt(0);

               for (int c : missing) {
                  if (c >= from && c <= to) {
                     sizes.putIfAbsent(c, new int[]{range.get("left").getAsInt(), range.get("right").getAsInt()});
                  }
               }
            }
         }

         Map<Integer, String> found = new HashMap<>();

         ZipEntry entry;
         try (
            InputStream in = resource.get().open();
            ZipInputStream zip = new ZipInputStream(in);
         ) {
            while ((entry = zip.getNextEntry()) != null) {
               if (entry.getName().endsWith(".hex")) {
                  BufferedReader lines = new BufferedReader(new InputStreamReader(zip, StandardCharsets.US_ASCII));

                  String line;
                  while ((line = lines.readLine()) != null) {
                     int colon = line.indexOf(58);
                     if (colon >= 4 && colon <= 6) {
                        int c = Integer.parseInt(line.substring(0, colon), 16);
                        if (missing.contains(c)) {
                           found.put(c, line.substring(colon + 1).trim());
                        }
                     }
                  }
               }
            }
         }

         for (Entry<Integer, String> glyph : found.entrySet()) {
            this.hexGlyph(glyph.getKey(), glyph.getValue(), sizes.get(glyph.getKey()));
         }
      }
   }

   private void hexGlyph(int c, String digits, int[] size) {
      int perRow = digits.length() / 16;
      if (perRow >= 2 && perRow <= 8 && digits.length() % 16 == 0) {
         int bits = perRow * 4;
         int[] rows = new int[16];
         int mask = 0;

         for (int y = 0; y < 16; y++) {
            rows[y] = (int)(Long.parseLong(digits.substring(y * perRow, (y + 1) * perRow), 16) << 32 - bits);
            mask |= rows[y];
         }

         int right;
         int left;
         if (size != null) {
            left = size[0];
            right = size[1];
         } else if (mask == 0) {
            left = 0;
            right = bits;
         } else {
            left = Integer.numberOfLeadingZeros(mask);
            right = 32 - Integer.numberOfTrailingZeros(mask) - 1;
         }

         int width = right - left + 1;
         if (width > 0) {
            byte[] alpha = new byte[width * 16];

            for (int y = 0; y < 16; y++) {
               for (int x = 0; x < width; x++) {
                  int bit = 31 - (left + x);
                  if (bit >= 0 && bit < 32 && (rows[y] >> bit & 1) != 0) {
                     alpha[y * width + x] = -1;
                  }
               }
            }

            this.glyphs.put(c, new ExportFont.Glyph(alpha, width, 16, 0.5F, 0.0F, width / 2 + 1, 0.5F));
         }
      }
   }

   private static ExportFont.Glyph missing() {
      byte[] alpha = new byte[40];

      for (int y = 0; y < 8; y++) {
         for (int x = 0; x < 5; x++) {
            if (x == 0 || x == 4 || y == 0 || y == 7) {
               alpha[y * 5 + x] = -1;
            }
         }
      }

      return new ExportFont.Glyph(alpha, 5, 8, 1.0F, 0.0F, 6.0F, 1.0F);
   }

   private static int alpha(NativeImage image, int x, int y) {
      return image.getPixelRGBA(x, y) >>> 24;
   }

   private ExportFont.Glyph glyph(int c) {
      ExportFont.Glyph glyph = this.glyphs.get(c);
      return glyph != null ? glyph : MISSING;
   }

   static String shaped(String text) {
      if (!Language.getInstance().isDefaultRightToLeft()) {
         return text;
      }

      try {
         Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException e) {
         return text;
      }
   }

   float width(String text) {
      float width = 0.0F;

      for (int c : text.codePoints().toArray()) {
         Float space = this.spaces.get(c);
         width += space != null ? space : this.glyph(c).advance();
      }

      return width;
   }

   float[] span(String text) {
      float pen = 0.0F;
      float left = 0.0F;
      float right = 0.0F;

      for (int c : text.codePoints().toArray()) {
         Float space = this.spaces.get(c);
         if (space != null) {
            pen += space;
         } else {
            ExportFont.Glyph glyph = this.glyph(c);
            left = Math.min(left, pen);
            right = Math.max(right, pen + glyph.width() * glyph.size() + glyph.shadow());
            pen += glyph.advance();
         }

         left = Math.min(left, pen);
         right = Math.max(right, pen);
      }

      return new float[]{left, right};
   }

   float[] extent(String text) {
      float top = 0.0F;
      float bottom = 8.0F;

      for (int c : text.codePoints().toArray()) {
         if (!this.spaces.containsKey(c)) {
            ExportFont.Glyph glyph = this.glyph(c);
            top = Math.min(top, glyph.top());
            bottom = Math.max(bottom, glyph.top() + glyph.height() * glyph.size() + glyph.shadow());
         }
      }

      return new float[]{top, bottom};
   }

   void draw(int[] pixels, int stride, int rows, String text, float x, float y, int scale, int rgb) {
      text = shaped(text);
      this.drawPass(pixels, stride, rows, text, x, y, scale, (rgb & 16579836) >> 2, true);
      this.drawPass(pixels, stride, rows, text, x, y, scale, rgb, false);
   }

   private void drawPass(int[] pixels, int stride, int rows, String text, float x, float y, int scale, int rgb, boolean shadow) {
      float pen = x;

      for (int c : text.codePoints().toArray()) {
         Float space = this.spaces.get(c);
         if (space != null) {
            pen += space * scale;
         } else {
            ExportFont.Glyph glyph = this.glyph(c);
            float offset = shadow ? glyph.shadow() * scale : 0.0F;
            float texel = glyph.size() * scale;
            int x0 = Math.round(pen + offset);
            int y0 = Math.round(y + glyph.top() * scale + offset);
            int w = Math.round(glyph.width() * texel);
            int h = Math.round(glyph.height() * texel);

            for (int py = 0; py < h; py++) {
               int oy = y0 + py;
               if (oy >= 0 && oy < rows) {
                  int ty = Math.min(glyph.height() - 1, (int)(py / texel));

                  for (int px = 0; px < w; px++) {
                     int ox = x0 + px;
                     int a = glyph.alpha()[ty * glyph.width() + Math.min(glyph.width() - 1, (int)(px / texel))] & 255;
                     if (a != 0 && ox >= 0 && ox < stride) {
                        pixels[oy * stride + ox] = over(pixels[oy * stride + ox], a << 24 | rgb);
                     }
                  }
               }
            }

            pen += glyph.advance() * scale;
         }
      }
   }

   static int over(int below, int argb) {
      int a = argb >>> 24;
      if (a == 255) {
         return argb;
      }

      int ba = below >>> 24;
      int outA = a + ba * (255 - a) / 255;
      if (outA == 0) {
         return 0;
      }

      int r = ((argb >> 16 & 0xFF) * a + (below >> 16 & 0xFF) * ba * (255 - a) / 255) / outA;
      int g = ((argb >> 8 & 0xFF) * a + (below >> 8 & 0xFF) * ba * (255 - a) / 255) / outA;
      int b = ((argb & 0xFF) * a + (below & 0xFF) * ba * (255 - a) / 255) / outA;
      return outA << 24 | r << 16 | g << 8 | b;
   }

   private record Glyph(byte[] alpha, int width, int height, float size, float top, float advance, float shadow) {
   }
}
