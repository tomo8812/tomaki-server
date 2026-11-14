package dev.diorama.bake;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.diorama.Diorama;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public final class JarModels {
   private static final int MAX_PARENTS = 32;
   private final Map<String, ZipFile> assets = new HashMap<>();
   private final List<ZipFile> jars = new ArrayList<>();
   private final Map<String, @Nullable JsonObject> json = new HashMap<>();
   private final Map<String, float @Nullable []> textures = new HashMap<>();
   private boolean warned;

   private JarModels() {
   }

   public static JarModels of(Path folder) {
      JarModels models = new JarModels();
      if (Files.isDirectory(folder)) {
         try (DirectoryStream<Path> files = Files.newDirectoryStream(folder, "*.jar")) {
            for (Path file : files) {
               models.add(file);
            }
         } catch (IOException e) {
            Diorama.LOGGER.warn("Couldn't list the mods in {} for their block models", folder, e);
         }
      }

      return models;
   }

   private void add(Path file) {
      try {
         ZipFile zip = new ZipFile(file.toFile());
         boolean any = false;
         Enumeration<? extends ZipEntry> entries = zip.entries();

         while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith("assets/")
               && (name.contains("/blockstates/") || name.contains("/models/") || name.contains("/textures/"))
               && !name.startsWith("assets/minecraft/")) {
               this.assets.putIfAbsent(name, zip);
               any = true;
            }
         }

         if (any) {
            this.jars.add(zip);
         } else {
            zip.close();
         }
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't read {} for its block models", file, e);
      }
   }

   public int jarCount() {
      return this.jars.size();
   }

   public synchronized void close() {
      for (ZipFile zip : this.jars) {
         try {
            zip.close();
         } catch (IOException var4) {
         }
      }

      this.jars.clear();
      this.assets.clear();
   }

   synchronized FaceColor @Nullable [] faces(BlockState state) {
      String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
      int colon = id.indexOf(58);
      String namespace = id.substring(0, colon);
      String path = id.substring(colon + 1);

      try {
         JsonObject blockstate = this.json("assets/" + namespace + "/blockstates/" + path + ".json");
         if (blockstate == null) {
            return null;
         }

         Map<String, String> values = values(state);
         List<JsonObject> chosen = new ArrayList<>();
         if (blockstate.has("variants")) {
            for (Entry<String, JsonElement> variant : blockstate.getAsJsonObject("variants").entrySet()) {
               if (matchesVariant(variant.getKey(), values)) {
                  chosen.add(first(variant.getValue()));
                  break;
               }
            }
         } else if (blockstate.has("multipart")) {
            for (JsonElement part : blockstate.getAsJsonArray("multipart")) {
               JsonObject object = part.getAsJsonObject();
               if (!object.has("when") || matchesWhen(object.get("when"), values)) {
                  chosen.add(first(object.get("apply")));
               }
            }
         }

         FaceColor top = new FaceColor();
         FaceColor side = new FaceColor();
         String particle = null;

         for (JsonObject variant : chosen) {
            if (variant != null && variant.has("model")) {
               int x = variant.has("x") ? Math.floorMod(variant.get("x").getAsInt(), 360) : 0;
               int y = variant.has("y") ? Math.floorMod(variant.get("y").getAsInt(), 360) : 0;
               boolean onSide = x == 90 || x == 270;
               JarModels.Model model = this.model(variant.get("model").getAsString());
               if (model != null) {
                  particle = particle != null ? particle : model.texture("#particle");
                  this.add(model, top, side, onSide, y / 90);
               }
            }
         }

         if (top.isEmpty() && side.isEmpty() && particle != null) {
            float[] avg = this.texture(particle);
            if (avg != null) {
               top.add(avg, -1);
               side.add(avg, -1);
            }
         }

         return top.isEmpty() && side.isEmpty() ? null : new FaceColor[]{top.isEmpty() ? side : top, side.isEmpty() ? top : side};
      } catch (RuntimeException e) {
         if (!this.warned) {
            this.warned = true;
            Diorama.LOGGER.warn("Couldn't read the block model of {} ({}); such blocks take their map colour", state, e.toString());
         }

         return null;
      }
   }

   static byte tintOf(BlockState state) {
      return (byte)(state.is(BlockTags.LEAVES) ? 2 : 1);
   }

   private void add(JarModels.Model model, FaceColor top, FaceColor side, boolean onSide, int quarterTurns) {
      FaceColor up = onSide ? side : top;
      FaceColor front = onSide ? top : side;
      boolean cullsSouth = false;

      for (JarModels.Face face : model.faces) {
         cullsSouth |= "south".equals(turn(face.cull, quarterTurns));
      }

      for (JarModels.Face face : model.faces) {
         String cull = turn(face.cull, quarterTurns);
         float[] avg = this.texture(model.texture(face.texture));
         if (avg != null) {
            if (cull == null) {
               if (!face.direction.equals("up") && !face.direction.equals("down")) {
                  top.add(avg, face.tint);
                  side.add(avg, face.tint);
               } else {
                  up.add(avg, face.tint);
               }
            } else if (cull.equals("up")) {
               up.add(avg, face.tint);
            } else if (cull.equals("south") || cull.equals("north") && !cullsSouth) {
               front.add(avg, face.tint);
            }
         }
      }
   }

   private static @Nullable String turn(@Nullable String side, int quarterTurns) {
      if (side != null && quarterTurns != 0) {
         String[] around = new String[]{"north", "east", "south", "west"};

         for (int i = 0; i < 4; i++) {
            if (around[i].equals(side)) {
               return around[i + quarterTurns & 3];
            }
         }

         return side;
      } else {
         return side;
      }
   }

   private static @Nullable JsonObject first(JsonElement element) {
      if (element.isJsonArray()) {
         JsonArray array = element.getAsJsonArray();
         return array.isEmpty() ? null : array.get(0).getAsJsonObject();
      } else {
         return element.getAsJsonObject();
      }
   }

   private static Map<String, String> values(BlockState state) {
      Map<String, String> out = new HashMap<>();

      for (Property<?> property : state.getProperties()) {
         out.put(property.getName(), valueName(state, property));
      }

      return out;
   }

   private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
      return property.getName(state.getValue(property));
   }

   private static boolean matchesVariant(String key, Map<String, String> values) {
      if (!key.isEmpty() && !key.equals("normal")) {
         for (String pair : key.split(",")) {
            int eq = pair.indexOf(61);
            if (eq < 0 || !pair.substring(eq + 1).equals(values.get(pair.substring(0, eq)))) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private static boolean matchesWhen(JsonElement when, Map<String, String> values) {
      JsonObject object = when.getAsJsonObject();
      if (object.has("OR")) {
         for (JsonElement any : object.getAsJsonArray("OR")) {
            if (matchesWhen(any, values)) {
               return true;
            }
         }

         return false;
      } else if (object.has("AND")) {
         for (JsonElement all : object.getAsJsonArray("AND")) {
            if (!matchesWhen(all, values)) {
               return false;
            }
         }

         return true;
      } else {
         for (Entry<String, JsonElement> entry : object.entrySet()) {
            String have = values.get(entry.getKey());
            boolean any = false;

            for (String want : entry.getValue().getAsString().split("\\|")) {
               any |= want.equals(have);
            }

            if (!any) {
               return false;
            }
         }

         return true;
      }
   }

   private JarModels.@Nullable Model model(String name) {
      JarModels.Model model = new JarModels.Model();
      boolean haveFaces = false;
      String at = name;

      for (int depth = 0; at != null && depth < 32; depth++) {
         JarModels.ResourceName location = JarModels.ResourceName.of(at);
         JsonObject json = location.namespace.equals("minecraft") ? null : this.json("assets/" + location.namespace + "/models/" + location.path + ".json");
         if (json == null) {
            if (!haveFaces) {
               haveFaces = VanillaParents.faces(
                  location.path, (direction, cullx, texture, tint) -> model.faces.add(new JarModels.Face(direction, cullx, texture, tint))
               );
            }
            break;
         }

         if (json.has("textures")) {
            for (Entry<String, JsonElement> texture : json.getAsJsonObject("textures").entrySet()) {
               if (texture.getValue().isJsonPrimitive()) {
                  model.textures.putIfAbsent(texture.getKey(), texture.getValue().getAsString());
               }
            }
         }

         if (!haveFaces && json.has("elements")) {
            haveFaces = true;

            for (JsonElement element : json.getAsJsonArray("elements")) {
               JsonObject faces = element.getAsJsonObject().getAsJsonObject("faces");
               if (faces != null) {
                  for (Entry<String, JsonElement> face : faces.entrySet()) {
                     JsonObject f = face.getValue().getAsJsonObject();
                     if (f.has("texture")) {
                        String cull = f.has("cullface") ? f.get("cullface").getAsString() : null;
                        model.faces
                           .add(
                              new JarModels.Face(face.getKey(), cull, f.get("texture").getAsString(), f.has("tintindex") ? f.get("tintindex").getAsInt() : -1)
                           );
                     }
                  }
               }
            }
         }

         at = json.has("parent") ? json.get("parent").getAsString() : null;
      }

      return model;
   }

   private @Nullable JsonObject json(String path) {
      if (this.json.containsKey(path)) {
         return this.json.get(path);
      }

      JsonObject parsed = null;
      ZipFile zip = this.assets.get(path);
      if (zip != null) {
         try (Reader reader = new InputStreamReader(zip.getInputStream(zip.getEntry(path)), StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            parsed = element.isJsonObject() ? element.getAsJsonObject() : null;
         } catch (IOException | RuntimeException e) {
            parsed = null;
         }
      }

      this.json.put(path, parsed);
      return parsed;
   }

   private float @Nullable [] texture(@Nullable String name) {
      if (name == null) {
         return null;
      }

      if (this.textures.containsKey(name)) {
         return this.textures.get(name);
      }

      JarModels.ResourceName location = JarModels.ResourceName.of(name);
      String path = "assets/" + location.namespace + "/textures/" + location.path + ".png";
      float[] avg = null;
      ZipFile zip = this.assets.get(path);
      if (zip != null) {
         try (InputStream in = zip.getInputStream(zip.getEntry(path))) {
            BufferedImage image = ImageIO.read(in);
            avg = image == null ? null : average(image);
         } catch (IOException | RuntimeException e) {
            avg = null;
         }
      }

      this.textures.put(name, avg);
      return avg;
   }

   static float[] average(BufferedImage image) {
      int w = image.getWidth();
      int h = image.getHeight() > w && image.getHeight() % w == 0 ? w : image.getHeight();
      double r = 0.0;
      double g = 0.0;
      double b = 0.0;
      double weight = 0.0;
      int visible = 0;

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            int argb = image.getRGB(x, y);
            int a = argb >>> 24;
            if (a >= 26) {
               double wa = a / 255.0;
               r += (argb >> 16 & 0xFF) * wa;
               g += (argb >> 8 & 0xFF) * wa;
               b += (argb & 0xFF) * wa;
               weight += wa;
               visible++;
            }
         }
      }

      return weight == 0.0
         ? new float[]{0.0F, 0.0F, 0.0F, 0.0F}
         : new float[]{(float)(r / weight / 255.0), (float)(g / weight / 255.0), (float)(b / weight / 255.0), (float)visible / (w * h)};
   }

   private record Face(String direction, @Nullable String cull, String texture, int tint) {
   }

   private static final class Model {
      final Map<String, String> textures = new HashMap<>();
      final List<JarModels.Face> faces = new ArrayList<>();

      @Nullable String texture(String name) {
         String value = name;

         for (int i = 0; i < 16 && value != null && value.startsWith("#"); i++) {
            value = this.textures.get(value.substring(1));
         }

         if (value == null && !name.startsWith("#")) {
            value = this.textures.get(name);

            for (int i = 0; i < 16 && value != null && value.startsWith("#"); i++) {
               value = this.textures.get(value.substring(1));
            }
         }

         return value;
      }
   }

   private record ResourceName(String namespace, String path) {
      static JarModels.ResourceName of(String name) {
         int colon = name.indexOf(58);
         return colon < 0 ? new JarModels.ResourceName("minecraft", name) : new JarModels.ResourceName(name.substring(0, colon), name.substring(colon + 1));
      }
   }
}
