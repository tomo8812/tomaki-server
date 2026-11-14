package dev.diorama.drawing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.diorama.Diorama;
import dev.diorama.Maps;
import dev.diorama.WorldKey;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public final class DrawingStore {
   private static final Gson GSON = new GsonBuilder().registerTypeAdapter(UUID.class, new TypeAdapter<UUID>() {
      public void write(JsonWriter out, UUID value) throws IOException {
         out.value(value.toString());
      }

      public UUID read(JsonReader in) throws IOException {
         return UUID.fromString(in.nextString());
      }

      // $VF: synthetic method
      // $VF: bridge method
      public Object read(JsonReader param1) throws IOException {
         return this.read(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public void write(JsonWriter param1, Object nullx) throws IOException {
         this.write(var1, (UUID)nullx);
      }
   }).create();
   private static @Nullable DrawingStore current;
   private final String worldKey;
   private final Path file;
   private final List<Drawing> drawings = new ArrayList<>();
   private final List<Drawing> elsewhere = new ArrayList<>();
   private boolean batching;

   private DrawingStore(String worldKey) {
      this.worldKey = worldKey;
      this.file = WorldKey.folder(worldKey).resolve("drawings.json");
      this.load();
   }

   public static @Nullable DrawingStore get() {
      String key = WorldKey.current();
      if (key == null) {
         return null;
      }

      if (current == null || !current.worldKey.equals(key)) {
         current = new DrawingStore(key);
      }

      return current;
   }

   public static void onLeave() {
      current = null;
   }

   public static DrawingStore open(String worldKey) {
      return new DrawingStore(worldKey);
   }

   public static void mapChanged(String dimension) {
      DrawingStore store = current;
      if (store != null) {
         store.split(dimension);
      }
   }

   public static void mapDeleted(String worldKey, String dimension, String map) {
      DrawingStore store = current != null && current.worldKey.equals(worldKey) ? current : new DrawingStore(worldKey);
      if (store.elsewhere.removeIf(d -> d.dimension.equals(dimension) && Maps.id(d.map).equals(map))) {
         store.save();
      }
   }

   private void split(String dimension) {
      String active = Maps.of(this.worldKey).active(dimension);
      List<Drawing> leaving = this.drawings.stream().filter(d -> d.dimension.equals(dimension) && !Maps.id(d.map).equals(active)).toList();
      List<Drawing> coming = this.elsewhere.stream().filter(d -> d.dimension.equals(dimension) && Maps.id(d.map).equals(active)).toList();
      this.drawings.removeAll(leaving);
      this.elsewhere.removeAll(coming);
      this.elsewhere.addAll(leaving);
      this.drawings.addAll(coming);
   }

   public List<Drawing> in(String dimension) {
      List<Drawing> out = new ArrayList<>();

      for (Drawing drawing : this.drawings) {
         if (drawing.dimension.equals(dimension)) {
            out.add(drawing);
         }
      }

      return out;
   }

   public List<Drawing> all() {
      return this.drawings;
   }

   public void add(Drawing drawing) {
      drawing.map = Maps.noted(Maps.of(this.worldKey).active(drawing.dimension));
      this.drawings.add(drawing);
      this.save();
   }

   public void restore(Drawing drawing, int index) {
      if (!Maps.id(drawing.map).equals(Maps.of(this.worldKey).active(drawing.dimension))) {
         this.elsewhere.add(drawing);
         this.save();
      } else {
         this.drawings.add(Math.max(0, Math.min(index, this.drawings.size())), drawing);
         this.save();
      }
   }

   public int remove(Drawing drawing) {
      int index = this.drawings.indexOf(drawing);
      if (index >= 0) {
         this.drawings.remove(index);
         this.save();
      }

      return index;
   }

   public void batch(Runnable changes) {
      this.batching = true;

      try {
         changes.run();
      } finally {
         this.batching = false;
         this.save();
      }
   }

   private void load() {
      if (Files.isRegularFile(this.file)) {
         boolean damaged = false;

         try (Reader reader = Files.newBufferedReader(this.file, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
               damaged = true;
            } else {
               for (JsonElement element : root.getAsJsonArray()) {
                  try {
                     Drawing drawing = (Drawing)GSON.fromJson(element, Drawing.class);
                     if (valid(drawing)) {
                        if (drawing.map != null && drawing.map.isEmpty()) {
                           drawing.map = null;
                        }

                        (Maps.id(drawing.map).equals(Maps.of(this.worldKey).active(drawing.dimension)) ? this.drawings : this.elsewhere).add(drawing);
                     } else {
                        damaged = true;
                     }
                  } catch (RuntimeException e) {
                     damaged = true;
                     Diorama.LOGGER.warn("Skipping a drawing in {} that couldn't be read: {}", new Object[]{this.file, element, e});
                  }
               }
            }
         } catch (Exception e) {
            damaged = true;
            Diorama.LOGGER.error("Couldn't read drawings from {}", this.file, e);
         }

         if (damaged) {
            Path backup = this.file.resolveSibling("drawings.json.unreadable-" + System.currentTimeMillis());

            try {
               Files.copy(this.file, backup);
               Diorama.LOGGER.warn("Kept a copy of the unreadable drawings file at {}", backup);
            } catch (IOException e) {
               Diorama.LOGGER.error("Couldn't back up {}", this.file, e);
            }
         }
      }
   }

   private static boolean valid(@Nullable Drawing drawing) {
      if (drawing != null
         && drawing.id != null
         && drawing.kind != null
         && drawing.dimension != null
         && drawing.points != null
         && drawing.points.length % 2 == 0) {
         if (drawing.text == null) {
            drawing.text = "";
         }

         for (double value : drawing.points) {
            if (!Double.isFinite(value)) {
               return false;
            }
         }

         return drawing.complete();
      } else {
         return false;
      }
   }

   private void save() {
      if (!this.batching) {
         try {
            Files.createDirectories(this.file.getParent());
            Path temp = this.file.resolveSibling("drawings.json.tmp");

            try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
               List<Drawing> all = new ArrayList<>(this.drawings);
               all.addAll(this.elsewhere);
               GSON.toJson(all, writer);
            }

            Files.move(temp, this.file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (IOException | RuntimeException e) {
            Diorama.LOGGER.error("Couldn't save drawings to {}", this.file, e);
         }
      }
   }
}
