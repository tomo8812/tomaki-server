package dev.diorama;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.diorama.drawing.DrawingStore;
import dev.diorama.waypoint.WaypointStore;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class Maps {
   public static final String MAIN = "";
   public static final int MAX_NAME = 32;
   private static final String FILE = "maps.json";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static @Nullable Maps current;
   private final String worldKey;
   private final Path file;
   private Maps.Saved saved = new Maps.Saved();

   private Maps(String worldKey) {
      this.worldKey = worldKey;
      this.file = WorldKey.folder(worldKey).resolve("maps.json");
      this.load();
   }

   public static @Nullable Maps get() {
      String key = WorldKey.current();
      return key == null ? null : of(key);
   }

   public static Maps of(String worldKey) {
      Maps maps = current;
      if (maps == null || !maps.worldKey.equals(worldKey)) {
         maps = new Maps(worldKey);
         current = maps;
      }

      return maps;
   }

   public static void onLeave() {
      current = null;
   }

   public String worldKey() {
      return this.worldKey;
   }

   public Facing facing() {
      return this.saved.facing == null ? Facing.NORTH : Facing.of(this.saved.facing);
   }

   public boolean facing(Facing facing) {
      String before = GSON.toJson(this.saved);
      this.saved.facing = facing == Facing.NORTH ? null : facing.id();
      return this.saveOrUndo(before);
   }

   public List<String> dimensions() {
      return List.copyOf(this.saved.dimensions.keySet());
   }

   public List<String> saved(String here, Collection<String> serverLevels) {
      LinkedHashSet<String> known = new LinkedHashSet<>(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"));
      serverLevels.stream().sorted().forEach(known::add);
      known.addAll(this.dimensions());
      known.add(here);
      return known.stream()
         .filter(dimension -> dimension.equals(here) || this.list(dimension).stream().anyMatch(map -> Files.isDirectory(map.folder())))
         .toList();
   }

   public String active(String dimension) {
      Maps.Dimension saved = this.saved.dimensions.get(dimension);
      return saved == null ? "" : saved.active;
   }

   public static String id(@Nullable String noted) {
      return noted == null ? "" : noted;
   }

   public static @Nullable String noted(String id) {
      return id.isEmpty() ? null : id;
   }

   public Path tileFolder(String dimension, String id) {
      String folder = WorldKey.key("dim", dimension);
      return WorldKey.folder(this.worldKey).resolve(id.isEmpty() ? folder : folder + "-map-" + id);
   }

   public List<Maps.Info> list(String dimension) {
      Maps.Dimension saved = this.saved.dimensions.get(dimension);
      String active = saved == null ? "" : saved.active;
      List<Maps.Info> out = new ArrayList<>();
      if (saved == null || !saved.mainDeleted) {
         out.add(new Maps.Info("", this.name(dimension, ""), mainCreated(this.tileFolder(dimension, "")), active.isEmpty(), this.tileFolder(dimension, "")));
      }

      if (saved != null) {
         for (Maps.Entry entry : saved.maps) {
            out.add(new Maps.Info(entry.id, entry.name, entry.created, entry.id.equals(active), this.tileFolder(dimension, entry.id)));
         }
      }

      return out;
   }

   public boolean several(String dimension) {
      Maps.Dimension saved = this.saved.dimensions.get(dimension);
      return saved != null && saved.maps.size() + (saved.mainDeleted ? 0 : 1) > 1;
   }

   public String name(String dimension, String id) {
      Maps.Dimension saved = this.saved.dimensions.get(dimension);
      if (id.isEmpty()) {
         return saved != null && saved.mainName != null ? saved.mainName : Component.translatable("diorama.maps.main").getString();
      }

      if (saved != null) {
         for (Maps.Entry entry : saved.maps) {
            if (entry.id.equals(id)) {
               return entry.name;
            }
         }
      }

      return id;
   }

   public boolean taken(String dimension, String name, @Nullable String except) {
      String wanted = name.trim();

      for (Maps.Info info : this.list(dimension)) {
         if (!info.id().equals(except) && info.name().equalsIgnoreCase(wanted)) {
            return true;
         }
      }

      return false;
   }

   public String suggestedName(String dimension) {
      int n = this.list(dimension).size() + 1;

      while (true) {
         String name = Component.translatable("diorama.maps.numbered", new Object[]{n}).getString();
         if (!this.taken(dimension, name, null)) {
            return name;
         }

         n++;
      }
   }

   public @Nullable String create(String dimension, String name) {
      String trimmed = name.trim();
      if (!trimmed.isEmpty() && trimmed.length() <= 32 && !this.taken(dimension, trimmed, null)) {
         String before = GSON.toJson(this.saved);
         Maps.Dimension saved = this.saved.dimensions.computeIfAbsent(dimension, d -> new Maps.Dimension());
         Maps.Entry entry = new Maps.Entry();

         do {
            entry.id = HexFormat.of().toHexDigits(ThreadLocalRandom.current().nextInt());
         } while (Files.exists(this.tileFolder(dimension, entry.id)) || saved.maps.stream().anyMatch(e -> e.id.equals(entry.id)));

         entry.name = trimmed;
         entry.created = System.currentTimeMillis();
         saved.maps.add(entry);
         saved.active = entry.id;
         if (!this.saveOrUndo(before)) {
            return null;
         }

         this.changed(dimension);
         return entry.id;
      } else {
         return null;
      }
   }

   public boolean use(String dimension, String id) {
      if (!this.exists(dimension, id)) {
         return false;
      }

      String before = GSON.toJson(this.saved);
      Maps.Dimension saved = this.saved.dimensions.computeIfAbsent(dimension, d -> new Maps.Dimension());
      boolean changed = !saved.active.equals(id);
      saved.active = id;
      if (!this.saveOrUndo(before)) {
         return false;
      }

      if (changed) {
         Diorama.LOGGER.info("{} of {} is now on map {}", new Object[]{dimension, this.worldKey, id.isEmpty() ? "main" : id});
         this.changed(dimension);
      }

      return true;
   }

   public boolean rename(String dimension, String id, String name) {
      String trimmed = name.trim();
      if (!trimmed.isEmpty() && trimmed.length() <= 32 && !this.taken(dimension, trimmed, id) && this.exists(dimension, id)) {
         String before = GSON.toJson(this.saved);
         Maps.Dimension saved = this.saved.dimensions.computeIfAbsent(dimension, d -> new Maps.Dimension());
         if (id.isEmpty()) {
            saved.mainName = trimmed;
         } else {
            saved.maps.stream().filter(e -> e.id.equals(id)).forEach(e -> e.name = trimmed);
         }

         return this.saveOrUndo(before);
      } else {
         return false;
      }
   }

   public boolean delete(String dimension, String id) {
      Maps.Dimension saved = this.saved.dimensions.get(dimension);
      if (saved != null && !saved.active.equals(id) && this.exists(dimension, id)) {
         String before = GSON.toJson(this.saved);
         if (id.isEmpty()) {
            saved.mainDeleted = true;
         } else {
            saved.maps.removeIf(e -> e.id.equals(id));
         }

         if (!this.saveOrUndo(before)) {
            return false;
         }

         WaypointStore.mapDeleted(this.worldKey, dimension, id);
         DrawingStore.mapDeleted(this.worldKey, dimension, id);
         Path folder = this.tileFolder(dimension, id);

         try {
            deleteTree(folder);
         } catch (IOException e) {
            Diorama.LOGGER.warn("Couldn't delete all of the map in {}", folder, e);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean exists(String dimension, String id) {
      return this.list(dimension).stream().anyMatch(info -> info.id().equals(id));
   }

   private void changed(String dimension) {
      if (this.worldKey.equals(WorldKey.current())) {
         MapSession.mapChanged(dimension);
         WaypointStore.mapChanged(dimension);
         DrawingStore.mapChanged(dimension);
      }
   }

   private static long mainCreated(Path folder) {
      try {
         return Files.isDirectory(folder) ? Files.readAttributes(folder, BasicFileAttributes.class).creationTime().toMillis() : 0L;
      } catch (IOException e) {
         return 0L;
      }
   }

   private static void deleteTree(Path folder) throws IOException {
      if (Files.exists(folder)) {
         try (Stream<Path> paths = Files.walk(folder)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
               Files.delete(path);
            }
         }
      }
   }

   public static long size(Path folder) {
      if (!Files.isDirectory(folder)) {
         return 0L;
      }

      try (Stream<Path> paths = Files.walk(folder)) {
         return paths.filter(x$0 -> Files.isRegularFile(x$0)).mapToLong(path -> {
            try {
               return Files.size(path);
            } catch (IOException e) {
               return 0L;
            }
         }).sum();
      } catch (IOException | UncheckedIOException e) {
         return 0L;
      }
   }

   public static String readableSize(long bytes) {
      if (bytes < 1048576L) {
         return Math.max(1L, Math.round(bytes / 1024.0)) + " KB";
      }

      double mb = bytes / 1048576.0;
      return mb < 1024.0 ? String.format(Locale.ROOT, mb < 100.0 ? "%.1f MB" : "%.0f MB", mb) : String.format(Locale.ROOT, "%.1f GB", mb / 1024.0);
   }

   private void load() {
      if (Files.isRegularFile(this.file)) {
         boolean damaged = false;

         try (Reader reader = Files.newBufferedReader(this.file, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            JsonElement dimensions = root.isJsonObject() ? root.getAsJsonObject().get("dimensions") : null;
            if (dimensions == null || !dimensions.isJsonObject()) {
               damaged = true;
               dimensions = new JsonObject();
            }

            JsonElement facing = root.isJsonObject() ? root.getAsJsonObject().get("facing") : null;
            if (facing != null && facing.isJsonPrimitive()) {
               this.saved.facing = facing.getAsString();
            }

            Iterator var6 = dimensions.getAsJsonObject().entrySet().iterator();

            label149:
            while (true) {
               Map.Entry<String, JsonElement> dimension;
               Maps.Dimension in;
               while (true) {
                  if (!var6.hasNext()) {
                     break label149;
                  }

                  dimension = (Map.Entry<String, JsonElement>)var6.next();

                  try {
                     in = (Maps.Dimension)GSON.fromJson(dimension.getValue(), Maps.Dimension.class);
                     break;
                  } catch (RuntimeException e) {
                     damaged = true;
                     Diorama.LOGGER.warn("Skipping the maps of {} in {}, which couldn't be read", new Object[]{dimension.getKey(), this.file, e});
                  }
               }

               if (in != null) {
                  Maps.Dimension clean = new Maps.Dimension();
                  clean.mainName = in.mainName != null && !in.mainName.isBlank() ? in.mainName.trim() : null;
                  clean.mainDeleted = in.mainDeleted;
                  if (in.maps != null) {
                     for (Maps.Entry entry : in.maps) {
                        if (entry != null
                           && entry.id != null
                           && !entry.id.isEmpty()
                           && entry.id.matches("[0-9a-f]+")
                           && entry.name != null
                           && !entry.name.isBlank()
                           && clean.maps.stream().noneMatch(ex -> ex.id.equals(entry.id))) {
                           entry.name = entry.name.trim();
                           clean.maps.add(entry);
                        }
                     }
                  }

                  String noted = in.active == null ? "" : in.active;
                  boolean known = noted.isEmpty() ? !clean.mainDeleted : clean.maps.stream().anyMatch(ex -> ex.id.equals(noted));
                  String active = noted;
                  if (!known) {
                     active = clean.mainDeleted && !clean.maps.isEmpty() ? clean.maps.get(0).id : "";
                     if (active.isEmpty()) {
                        clean.mainDeleted = false;
                     }
                  }

                  clean.active = active;
                  this.saved.dimensions.put(dimension.getKey(), clean);
               }
            }
         } catch (Exception e) {
            damaged = true;
            Diorama.LOGGER.error("Couldn't read this world's maps from {}; using each dimension's main map", this.file, e);
         }

         if (damaged) {
            Path backup = this.file.resolveSibling("maps.json.unreadable-" + System.currentTimeMillis());

            try {
               Files.copy(this.file, backup);
               Diorama.LOGGER.warn("Kept a copy of the unreadable maps file at {}", backup);
            } catch (IOException e) {
               Diorama.LOGGER.error("Couldn't back up {}", this.file, e);
            }
         }
      }
   }

   private boolean saveOrUndo(String before) {
      if (this.save()) {
         return true;
      }

      this.saved = (Maps.Saved)GSON.fromJson(before, Maps.Saved.class);
      return false;
   }

   private boolean save() {
      try {
         Files.createDirectories(this.file.getParent());
         Path temp = this.file.resolveSibling("maps.json.tmp");

         try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            GSON.toJson(this.saved, writer);
         }

         Files.move(temp, this.file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         return true;
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.error("Couldn't save this world's maps to {}", this.file, e);
         return false;
      }
   }

   private static final class Dimension {
      String active = "";
      @Nullable String mainName;
      boolean mainDeleted;
      List<Maps.Entry> maps = new ArrayList<>();
   }

   private static final class Entry {
      String id = "";
      String name = "";
      long created;
   }

   public record Info(String id, String name, long created, boolean active, Path folder) {
   }

   private static final class Saved {
      Map<String, Maps.Dimension> dimensions = new LinkedHashMap<>();
      @Nullable String facing;
   }
}
