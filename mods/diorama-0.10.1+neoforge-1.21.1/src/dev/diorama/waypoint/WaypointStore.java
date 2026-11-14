package dev.diorama.waypoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.diorama.Diorama;
import dev.diorama.Maps;
import dev.diorama.Settings;
import dev.diorama.WorldKey;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.DimensionType;
import org.jspecify.annotations.Nullable;

public final class WaypointStore {
   public static final int DEATHS_KEPT = 3;
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(UUID.class, new TypeAdapter<UUID>() {
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
   private static @Nullable WaypointStore current;
   private final String worldKey;
   private final Path file;
   private final List<Waypoint> waypoints = new ArrayList<>();
   private final List<Waypoint> elsewhere = new ArrayList<>();
   private final Path foldersFile;
   private WaypointStore.Folders folders = new WaypointStore.Folders();
   private final Set<WaypointStore.ShownIn> shownIn = new LinkedHashSet<>();
   private final Map<String, Double> scales = new HashMap<>();
   private final Map<String, Map<Waypoint, Waypoint>> copies = new HashMap<>();
   public static final String WAYSTONES_KEY = "waystones";

   private WaypointStore(String worldKey) {
      this.worldKey = worldKey;
      this.file = WorldKey.folder(worldKey).resolve("waypoints.json");
      this.foldersFile = WorldKey.folder(worldKey).resolve("folders.json");
      this.load();
      this.loadFolders();
   }

   public static @Nullable WaypointStore get() {
      String key = WorldKey.current();
      if (key == null) {
         return null;
      }

      if (current == null || !current.worldKey.equals(key)) {
         current = new WaypointStore(key);
      }

      return current;
   }

   public static void onLeave() {
      current = null;
   }

   public static WaypointStore open(String worldKey) {
      return new WaypointStore(worldKey);
   }

   public static void mapChanged(String dimension) {
      WaypointStore store = current;
      if (store != null) {
         String active = Maps.of(store.worldKey).active(dimension);
         store.waypoints.removeIf(w -> w.dimension.equals(dimension) && w.kind == Waypoint.Kind.TEMPORARY);
         List<Waypoint> leaving = store.waypoints.stream().filter(w -> w.dimension.equals(dimension) && !Maps.id(w.map).equals(active)).toList();
         List<Waypoint> coming = store.elsewhere.stream().filter(w -> w.dimension.equals(dimension) && Maps.id(w.map).equals(active)).toList();
         store.waypoints.removeAll(leaving);
         store.elsewhere.removeAll(coming);
         store.elsewhere.addAll(leaving);
         store.waypoints.addAll(coming);
      }
   }

   public static void mapDeleted(String worldKey, String dimension, String map) {
      WaypointStore store = current != null && current.worldKey.equals(worldKey) ? current : new WaypointStore(worldKey);
      if (store.elsewhere.removeIf(w -> w.dimension.equals(dimension) && Maps.id(w.map).equals(map))) {
         store.save();
      }
   }

   public int count(String dimension, String map) {
      int count = 0;

      for (List<Waypoint> list : List.of(this.waypoints, this.elsewhere)) {
         for (Waypoint waypoint : list) {
            if (waypoint.dimension.equals(dimension) && Maps.id(waypoint.map).equals(map) && waypoint.kind != Waypoint.Kind.TEMPORARY && !hidden(waypoint)) {
               count++;
            }
         }
      }

      return count;
   }

   private static boolean hidden(Waypoint waypoint) {
      return waypoint.kind.isPortal() && (waypoint.deleted || !Settings.get().portalMarkers);
   }

   public List<Waypoint> all() {
      return this.waypoints.stream().filter(w -> !hidden(w)).toList();
   }

   List<Waypoint> withHidden(String dimension) {
      return this.waypoints.stream().filter(w -> w.dimension.equals(dimension)).toList();
   }

   public List<Waypoint> in(String dimension) {
      List<Waypoint> out = new ArrayList<>();

      for (Waypoint waypoint : this.waypoints) {
         if (waypoint.dimension.equals(dimension) && !hidden(waypoint)) {
            out.add(waypoint);
         }
      }

      for (Waypoint waystone : Waystones.in(dimension)) {
         out.add(this.withVisibility(waystone));
      }

      return out;
   }

   public List<Waypoint> shownIn(String dimension) {
      List<Waypoint> out = this.in(dimension);
      this.addShownIn(dimension, out);
      return out;
   }

   public List<Waypoint> shownIn(String dimension, List<Waypoint> own) {
      List<Waypoint> out = new ArrayList<>(own);
      this.addShownIn(dimension, out);
      return out;
   }

   private void addShownIn(String dimension, List<Waypoint> out) {
      double scale = this.coordinateScale(dimension);
      Map<Waypoint, Waypoint> previous = this.copies.getOrDefault(dimension, Map.of());
      Map<Waypoint, Waypoint> made = new IdentityHashMap<>();

      for (WaypointStore.ShownIn pair : this.shownIn) {
         if (pair.in().equals(dimension)) {
            double divider = scale / this.coordinateScale(pair.from());

            for (Waypoint waypoint : this.in(pair.from())) {
               Waypoint copy = waypoint.scaled(divider, previous.get(waypoint));
               made.put(waypoint, copy);
               out.add(copy);
            }
         }
      }

      if (made.isEmpty()) {
         this.copies.remove(dimension);
      } else {
         this.copies.put(dimension, made);
      }
   }

   public boolean showsIn(String in, String from) {
      return this.shownIn.contains(new WaypointStore.ShownIn(in, from));
   }

   public void setShownIn(String in, String from, boolean shown) {
      if (shown) {
         this.shownIn.add(new WaypointStore.ShownIn(in, from));
      } else {
         this.shownIn.remove(new WaypointStore.ShownIn(in, from));
      }
   }

   public double coordinateScale(String dimension) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && mc.level.dimension().location().toString().equals(dimension)) {
         double scale = mc.level.dimensionType().coordinateScale();
         this.scales.put(dimension, scale);
         return scale;
      }

      Double known = this.scales.get(dimension);
      if (known == null) {
         known = lookUpScale(mc, dimension);
         this.scales.put(dimension, known);
      }

      return known;
   }

   private static double lookUpScale(Minecraft mc, String dimension) {
      IntegratedServer server = mc.getSingleplayerServer();
      if (server != null) {
         for (ServerLevel level : server.getAllLevels()) {
            if (level.dimension().location().toString().equals(dimension)) {
               return level.dimensionType().coordinateScale();
            }
         }
      }

      if (mc.level != null) {
         Registry<DimensionType> types = mc.level.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);

         for (Entry<ResourceKey<DimensionType>, DimensionType> type : types.entrySet()) {
            if (type.getKey().location().toString().equals(dimension)) {
               return type.getValue().coordinateScale();
            }
         }
      }

      return 1.0;
   }

   public List<Waypoint> waystones() {
      List<Waypoint> out = new ArrayList<>();

      for (Waypoint waystone : Waystones.all()) {
         out.add(this.withVisibility(waystone));
      }

      return out;
   }

   private Waypoint withVisibility(Waypoint waystone) {
      waystone.visible = !this.folders.hidden.contains(waystoneKey(waystone.id));
      return waystone;
   }

   public void setVisible(Waypoint waypoint, boolean visible) {
      waypoint.visible = visible;
      if (waypoint.kind == Waypoint.Kind.WAYSTONE) {
         this.setFolderHidden(waystoneKey(waypoint.id), !visible);
      } else {
         this.save();
      }
   }

   public static String dimensionKey(String dimension) {
      return "dim:" + dimension;
   }

   private static String waystoneKey(UUID id) {
      return "waystone:" + id;
   }

   public static String customKey(String name) {
      return "folder:" + name;
   }

   public String folderKey(Waypoint waypoint) {
      if (waypoint.kind == Waypoint.Kind.WAYSTONE) {
         return "waystones";
      }

      String folder = this.folderName(waypoint.folder);
      return folder != null ? customKey(folder) : dimensionKey(waypoint.dimension);
   }

   public List<String> customFolders() {
      return List.copyOf(this.folders.custom);
   }

   public @Nullable String folderName(@Nullable String name) {
      if (name != null && !name.isBlank()) {
         for (String folder : this.folders.custom) {
            if (folder.equalsIgnoreCase(name.trim())) {
               return folder;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public boolean addFolder(String name) {
      String trimmed = name.trim();
      if (!trimmed.isEmpty() && this.folderName(trimmed) == null) {
         this.folders.custom.add(trimmed);
         this.saveFolders();
         return true;
      } else {
         return false;
      }
   }

   public boolean renameFolder(String old, String name) {
      String trimmed = name.trim();
      int index = this.folders.custom.indexOf(old);
      String clash = this.folderName(trimmed);
      if (index >= 0 && !trimmed.isEmpty() && (clash == null || clash.equals(old))) {
         for (Waypoint waypoint : this.everyWaypoint()) {
            if (old.equals(this.folderName(waypoint.folder))) {
               waypoint.folder = trimmed;
            }
         }

         this.folders.custom.set(index, trimmed);
         if (this.folders.hidden.remove(customKey(old))) {
            this.folders.hidden.add(customKey(trimmed));
         }

         this.saveFolders();
         this.save();
         return true;
      } else {
         return false;
      }
   }

   public void deleteFolder(String name) {
      if (this.folders.custom.contains(name)) {
         for (Waypoint waypoint : this.everyWaypoint()) {
            if (name.equals(this.folderName(waypoint.folder))) {
               waypoint.folder = "";
            }
         }

         this.folders.custom.remove(name);
         this.folders.hidden.remove(customKey(name));
         this.saveFolders();
         this.save();
      }
   }

   public boolean folderHidden(String key) {
      return this.folders.hidden.contains(key);
   }

   public void setFolderHidden(String key, boolean hidden) {
      if (hidden ? this.folders.hidden.add(key) : this.folders.hidden.remove(key)) {
         this.saveFolders();
      }
   }

   public WaypointSort sort() {
      return WaypointSort.named(this.folders.sort);
   }

   public boolean sortReversed() {
      return this.folders.sortReversed;
   }

   public void setSort(WaypointSort sort, boolean reversed) {
      if (sort != this.sort() || reversed != this.folders.sortReversed) {
         this.folders.sort = sort.name();
         this.folders.sortReversed = reversed;
         this.saveFolders();
      }
   }

   public boolean shown(Waypoint waypoint) {
      return waypoint.visible && !this.folderHidden(this.folderKey(waypoint));
   }

   public static boolean isShown(Waypoint waypoint) {
      WaypointStore store = get();
      return store == null ? waypoint.visible : store.shown(waypoint);
   }

   public static boolean isNear(Waypoint waypoint, Entity player) {
      int limit = Settings.get().waypointDistance;
      return limit > 0 && waypoint.kind != Waypoint.Kind.DEATH && waypoint.kind != Waypoint.Kind.TEMPORARY && waypoint.kind != Waypoint.Kind.BED
         ? waypoint.distanceTo(player.getX(), player.getY(), player.getZ()) <= limit
         : true;
   }

   public void add(Waypoint waypoint) {
      this.onMapInUse(waypoint);
      this.waypoints.add(waypoint);
      if (waypoint.kind == Waypoint.Kind.DEATH) {
         List<Waypoint> deaths = new ArrayList<>(this.waypoints.stream().filter(w -> w.kind == Waypoint.Kind.DEATH).toList());
         deaths.sort(Comparator.comparingLong(w -> w.created));

         for (int i = 0; i < deaths.size() - 3; i++) {
            this.waypoints.remove(deaths.get(i));
         }
      }

      this.save();
   }

   public void addAll(List<Waypoint> added) {
      if (!added.isEmpty()) {
         added.forEach(this::onMapInUse);
         this.waypoints.addAll(added);
         this.save();
      }
   }

   private void onMapInUse(Waypoint waypoint) {
      waypoint.map = Maps.noted(Maps.of(this.worldKey).active(waypoint.dimension));
   }

   private List<Waypoint> everyWaypoint() {
      List<Waypoint> all = new ArrayList<>(this.waypoints);
      all.addAll(this.elsewhere);
      return all;
   }

   public void remove(Waypoint waypoint) {
      if (waypoint.kind.isPortal()) {
         waypoint.deleted = true;
         this.save();
      } else {
         this.forget(waypoint);
      }
   }

   void forget(Waypoint waypoint) {
      this.waypoints.remove(waypoint);
      this.save();
   }

   public void changed() {
      this.save();
   }

   private void load() {
      if (Files.isRegularFile(this.file)) {
         boolean damaged = false;
         Maps maps = Maps.of(this.worldKey);

         try (Reader reader = Files.newBufferedReader(this.file, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
               damaged = true;
            } else {
               for (JsonElement element : root.getAsJsonArray()) {
                  try {
                     Waypoint waypoint = normalise((Waypoint)GSON.fromJson(element, Waypoint.class));
                     if (waypoint != null) {
                        (Maps.id(waypoint.map).equals(maps.active(waypoint.dimension)) ? this.waypoints : this.elsewhere).add(waypoint);
                     } else {
                        damaged = true;
                     }
                  } catch (RuntimeException e) {
                     damaged = true;
                     Diorama.LOGGER.warn("Skipping a waypoint in {} that couldn't be read: {}", new Object[]{this.file, element, e});
                  }
               }
            }
         } catch (Exception e) {
            damaged = true;
            Diorama.LOGGER.error("Couldn't read waypoints from {}", this.file, e);
         }

         if (damaged) {
            Path backup = this.file.resolveSibling("waypoints.json.unreadable-" + System.currentTimeMillis());

            try {
               Files.copy(this.file, backup);
               Diorama.LOGGER.warn("Kept a copy of the unreadable waypoints file at {}", backup);
            } catch (IOException e) {
               Diorama.LOGGER.error("Couldn't back up {}", this.file, e);
            }
         }
      }
   }

   private static @Nullable Waypoint normalise(@Nullable Waypoint waypoint) {
      if (waypoint != null && waypoint.dimension != null) {
         if (waypoint.id == null) {
            waypoint.id = UUID.randomUUID();
         }

         if (waypoint.name == null || waypoint.name.isBlank()) {
            waypoint.name = "Waypoint";
         }

         if (waypoint.initials == null) {
            waypoint.initials = "";
         }

         if (waypoint.icon == null) {
            waypoint.icon = "";
         }

         if (waypoint.kind == null) {
            waypoint.kind = Waypoint.Kind.MANUAL;
         }

         if (waypoint.folder == null) {
            waypoint.folder = "";
         }

         if (waypoint.map != null && waypoint.map.isEmpty()) {
            waypoint.map = null;
         }

         return waypoint;
      } else {
         return null;
      }
   }

   private void loadFolders() {
      if (Files.isRegularFile(this.foldersFile)) {
         try (Reader reader = Files.newBufferedReader(this.foldersFile, StandardCharsets.UTF_8)) {
            WaypointStore.Folders read = (WaypointStore.Folders)GSON.fromJson(reader, WaypointStore.Folders.class);
            if (read != null) {
               WaypointStore.Folders clean = new WaypointStore.Folders();
               if (read.custom != null) {
                  for (String name : read.custom) {
                     if (name != null && !name.isBlank() && clean.custom.stream().noneMatch(n -> n.equalsIgnoreCase(name.trim()))) {
                        clean.custom.add(name.trim());
                     }
                  }
               }

               if (read.hidden != null) {
                  clean.hidden.addAll(read.hidden);
               }

               clean.sort = read.sort;
               clean.sortReversed = read.sortReversed;
               this.folders = clean;
            }
         } catch (Exception e) {
            Diorama.LOGGER.error("Couldn't read waypoint folders from {}", this.foldersFile, e);
         }
      }
   }

   private void saveFolders() {
      try {
         Files.createDirectories(this.foldersFile.getParent());
         Path temp = this.foldersFile.resolveSibling("folders.json.tmp");

         try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            GSON.toJson(this.folders, writer);
         }

         Files.move(temp, this.foldersFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.error("Couldn't save waypoint folders to {}", this.foldersFile, e);
      }
   }

   private void save() {
      try {
         Files.createDirectories(this.file.getParent());
         Path temp = this.file.resolveSibling("waypoints.json.tmp");

         try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            GSON.toJson(this.everyWaypoint().stream().filter(w -> w.kind != Waypoint.Kind.TEMPORARY).toList(), writer);
         }

         Files.move(temp, this.file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.error("Couldn't save waypoints to {}", this.file, e);
      }
   }

   private static final class Folders {
      List<String> custom = new ArrayList<>();
      Set<String> hidden = new LinkedHashSet<>();
      @Nullable String sort;
      boolean sortReversed;
   }

   private record ShownIn(String in, String from) {
   }
}
