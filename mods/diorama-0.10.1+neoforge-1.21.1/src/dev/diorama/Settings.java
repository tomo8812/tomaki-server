package dev.diorama;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dev.diorama.bake.TileBaker;
import dev.diorama.share.ServerLocks;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;

public final class Settings {
   public static final double[] ZOOMS = new double[]{0.1875, 0.25, 0.375, 0.5, 0.75, 1.0, 1.5, 2.0, 3.0, 4.0};
   private static final int STEPS_BEFORE_OLD_ZOOMS = 4;
   private static final int OLD_ZOOMS = 6;
   public static final int[] RANGES = new int[]{16, 32, 64, 128};
   public static final int[] WAYPOINT_DISTANCES = new int[]{0, 64, 128, 256, 512, 1024, 2048};
   public static final int MIN_SIZE = 64;
   public static final int MAX_SIZE = 224;
   public static final int SIZE_STEP = 8;
   public static final int MIN_ENLARGED = 125;
   public static final int MAX_ENLARGED = 400;
   public static final int ENLARGED_STEP = 25;
   public static final int MIN_OPACITY = 20;
   public static final int MAX_OPACITY = 100;
   public static final int OPACITY_STEP = 5;
   public static final String DEFAULT_TELEPORT_COMMAND = "tp @s {x} {y} {z}";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static Settings current;
   public boolean minimap = true;
   @JsonAdapter(Settings.SizeAdapter.class)
   public int minimapSize = 96;
   public Settings.Corner minimapCorner = Settings.Corner.TOP_RIGHT;
   public int minimapOffsetX = 6;
   public int minimapOffsetY = 6;
   public boolean minimapCentered = false;
   public int minimapEnlarged = 200;
   public boolean minimapEnlargeToggle = false;
   public double minimapPixelsPerBlock = 2.0;
   private @Nullable Integer minimapZoom;
   public int minimapOpacity = 100;
   public boolean compass = true;
   public boolean coordinates = true;
   public boolean biome = true;
   public Settings.GameTime gameTime = Settings.GameTime.OFF;
   public int minimapTextSize = 100;
   public Settings.DayNight dayNight = Settings.DayNight.FOLLOW;
   public boolean autoCaves = true;
   public boolean preferSurface = true;
   public boolean legibleCaves = false;
   public Settings.Frame minimapFrame = Settings.Frame.CLASSIC;
   public Settings.Shape minimapShape = Settings.Shape.SQUARE;
   public boolean radarOnMinimap = true;
   public boolean radarOnMap = true;
   public boolean radarPlayers = true;
   public boolean radarHostile = true;
   public boolean radarNeutral = true;
   public boolean radarPassive = true;
   public boolean radarItems = true;
   public int radarRange = 32;
   public Settings.RadarStyle radarStyle = Settings.RadarStyle.HEADS;
   public int radarIconScale = 100;
   public boolean radarTypeOutline = true;
   public boolean radarHeightArrows = true;
   public Settings.NameTags playerNames = Settings.NameTags.FULLSCREEN;
   public List<RadarCategories.Category> radarCategories = new ArrayList<>();
   public Map<String, RadarCategories.KindLook> radarKinds = new TreeMap<>();
   public boolean radarPokemonAdded = false;
   public int arrowColor = 0;
   public boolean waypointsOnMap = true;
   public boolean waypointsInWorld = true;
   public boolean deathMarkers = true;
   public boolean removeReachedDeaths = true;
   public boolean bedMarkers = true;
   public boolean portalMarkers = false;
   public Settings.WaypointLabels waypointLabels = Settings.WaypointLabels.AUTO;
   public Settings.Beams waypointBeams = Settings.Beams.TEMPORARY;
   public int waypointDistance = 0;
   public int waypointMinimapSize = 60;
   public int waypointMapSize = 60;
   public int waypointWorldSize = 60;
   public boolean mapWaypointsButton = true;
   public boolean mapSettingsButton = true;
   public boolean mapMapsButton = true;
   public boolean mapDimensionButton = true;
   public boolean mapAddWaypoints = true;
   public String teleportCommand = "tp @s {x} {y} {z}";
   public boolean outlines = true;
   public boolean roundedFog = true;
   public boolean alwaysFogOfWar = false;
   public boolean mapSavedChunks = true;
   public Set<String> mapOffDimensions = new TreeSet<>();
   public Settings.Quality quality = Settings.Quality.FANCY;
   public boolean gpuBaking = true;
   public int defaultsMoved = 0;
   public boolean chunkGrid = false;
   public boolean slimeChunks = false;
   public Settings.SpawnSpots spawnSpots = Settings.SpawnSpots.OFF;
   public Settings.ClaimsShown claims = Settings.ClaimsShown.ALWAYS;
   public Settings.WebMap webMap = Settings.WebMap.OFF;
   public int webMapPort = 8100;
   public static final int DEFAULT_WEB_MAP_PORT = 8100;
   public Settings.DrawingsShown drawings = Settings.DrawingsShown.ALWAYS;
   public Settings.WorldBorderShown worldBorder = Settings.WorldBorderShown.ALWAYS;
   public static final int MIN_SCALE = 50;
   public static final int MAX_SCALE = 200;
   public static final int SCALE_STEP = 25;
   public static final int MIN_WAYPOINT_SIZE = 25;
   public static final int DEFAULT_WAYPOINT_SIZE = 60;
   public static final int MIN_TEXT_SIZE = 25;
   public static final int MAX_TEXT_SIZE = 400;
   public static final int[] ARROW_COLORS = new int[]{4172277, 15682379, 6478187, 16762173, 16214805, 9326278, 14041247, 15921906};
   public static final String[] ARROW_COLOR_NAMES = new String[]{"blue", "red", "green", "yellow", "orange", "purple", "pink", "white"};

   private static int clampTextSize(int size) {
      return Math.max(25, Math.min(400, size));
   }

   private static int clampWaypointSize(int size) {
      return Math.max(25, Math.min(200, size));
   }

   public static int snapScale(int scale) {
      int clamped = Math.max(50, Math.min(200, scale));
      return 50 + Math.round((clamped - 50) / 25.0F) * 25;
   }

   public int arrowRgb() {
      return ARROW_COLORS[Math.floorMod(this.arrowColor, ARROW_COLORS.length)];
   }

   public static Settings get() {
      if (current == null) {
         current = load();
      }

      return current;
   }

   public static Settings reload() {
      current = load();
      return current;
   }

   public double zoom() {
      return ZOOMS[this.zoomStep()];
   }

   public int zoomStep() {
      int nearest = 0;

      for (int i = 1; i < ZOOMS.length; i++) {
         if (Math.abs(ZOOMS[i] - this.minimapPixelsPerBlock) < Math.abs(ZOOMS[nearest] - this.minimapPixelsPerBlock)) {
            nearest = i;
         }
      }

      return nearest;
   }

   public void zoomStep(int step) {
      this.minimapPixelsPerBlock = ZOOMS[Math.max(0, Math.min(ZOOMS.length - 1, step))];
   }

   public float opacity() {
      return snapOpacity(this.minimapOpacity) / 100.0F;
   }

   public MapStyle style() {
      return MapStyle.DEFAULT.withName("Settings").withOutline(this.outlines ? MapStyle.DEFAULT.outline() : 0.0F).withRoundedFog(this.roundedFog);
   }

   public void save() {
      Path file = file();

      try {
         Files.createDirectories(file.getParent());
         Path temp = file.resolveSibling("config.json.tmp");

         try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
         }

         Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (Exception e) {
         Diorama.LOGGER.error("Couldn't save settings to {}", file, e);
      }

      this.apply();
   }

   public void apply() {
      MapStyle.set(this.style());
      boolean fast = this.quality == Settings.Quality.FAST;
      if (fast != TileBaker.fast()) {
         TileBaker.setFast(fast);
         MapSession session = MapSession.get();
         if (session != null) {
            session.map().colorsChanged();
         }
      }

      ServerRules.setAlwaysFogOfWar(this.alwaysFogOfWar);
   }

   private static Settings load() {
      Path file = file();
      Settings settings = null;
      if (Files.isRegularFile(file)) {
         try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            settings = (Settings)GSON.fromJson(reader, Settings.class);
         } catch (Exception e) {
            Diorama.LOGGER.error("Couldn't read settings from {}; using defaults", file, e);
         }
      }

      boolean read = settings != null;
      if (settings == null) {
         settings = new Settings();
      }

      boolean moved = settings.defaultsMoved < 1;
      if (moved) {
         settings.gpuBaking = true;
         settings.defaultsMoved = 1;
      }

      if (settings.spawnSpots == null) {
         settings.spawnSpots = Settings.SpawnSpots.OFF;
      }

      if (settings.minimapCorner == null) {
         settings.minimapCorner = Settings.Corner.TOP_RIGHT;
      }

      settings.minimapOffsetX = Math.max(6, settings.minimapOffsetX);
      settings.minimapOffsetY = Math.max(6, settings.minimapOffsetY);
      if (settings.dayNight == null) {
         settings.dayNight = Settings.DayNight.FOLLOW;
      }

      if (settings.gameTime == null) {
         settings.gameTime = Settings.GameTime.OFF;
      }

      if (settings.quality == null) {
         settings.quality = Settings.Quality.FANCY;
      }

      if (settings.minimapFrame == null) {
         settings.minimapFrame = Settings.Frame.CLASSIC;
      }

      if (settings.minimapShape == null) {
         settings.minimapShape = Settings.Shape.SQUARE;
      }

      if (settings.radarStyle == null) {
         settings.radarStyle = Settings.RadarStyle.HEADS;
      }

      settings.radarIconScale = snapScale(settings.radarIconScale);
      if (settings.playerNames == null) {
         settings.playerNames = Settings.NameTags.FULLSCREEN;
      }

      if (settings.webMap == null) {
         settings.webMap = Settings.WebMap.OFF;
      }

      if (settings.webMapPort < 1024 || settings.webMapPort > 65500) {
         settings.webMapPort = 8100;
      }

      if (settings.claims == null) {
         settings.claims = Settings.ClaimsShown.ALWAYS;
      }

      if (settings.drawings == null) {
         settings.drawings = Settings.DrawingsShown.ALWAYS;
      }

      if (settings.worldBorder == null) {
         settings.worldBorder = Settings.WorldBorderShown.ALWAYS;
      }

      if (settings.waypointLabels == null) {
         settings.waypointLabels = Settings.WaypointLabels.AUTO;
      }

      if (settings.waypointBeams == null) {
         settings.waypointBeams = Settings.Beams.TEMPORARY;
      }

      RadarCategories.loaded(settings);
      settings.teleportCommand = cleanCommand(settings.teleportCommand);
      Set<String> off = new TreeSet<>();
      if (settings.mapOffDimensions != null) {
         for (String dimension : settings.mapOffDimensions) {
            if (dimension != null) {
               off.addAll(ServerLocks.dimensions(dimension));
            }
         }
      }

      settings.mapOffDimensions = off;
      settings.arrowColor = Math.floorMod(settings.arrowColor, ARROW_COLORS.length);
      if (settings.minimapZoom != null) {
         settings.zoomStep(4 + Math.max(0, Math.min(5, settings.minimapZoom)));
         settings.minimapZoom = null;
      }

      settings.zoomStep(settings.zoomStep());
      settings.minimapSize = snapSize(settings.minimapSize);
      settings.minimapOpacity = snapOpacity(settings.minimapOpacity);
      settings.minimapEnlarged = snapEnlarged(settings.minimapEnlarged);
      settings.radarRange = nearest(RANGES, settings.radarRange);
      settings.waypointDistance = nearest(WAYPOINT_DISTANCES, settings.waypointDistance);
      settings.minimapTextSize = clampTextSize(settings.minimapTextSize);
      settings.waypointMinimapSize = clampWaypointSize(settings.waypointMinimapSize);
      settings.waypointMapSize = clampWaypointSize(settings.waypointMapSize);
      settings.waypointWorldSize = clampWaypointSize(settings.waypointWorldSize);
      settings.apply();
      if (moved && read) {
         settings.save();
      }

      return settings;
   }

   public static String cleanCommand(@Nullable String command) {
      String trimmed = command == null ? "" : command.trim().replaceAll("\\s+", " ");

      while (trimmed.startsWith("/")) {
         trimmed = trimmed.substring(1).trim();
      }

      return trimmed.isEmpty() ? "tp @s {x} {y} {z}" : trimmed;
   }

   private static int nearest(int[] steps, int value) {
      int nearest = steps[0];

      for (int step : steps) {
         if (Math.abs((long)step - value) < Math.abs((long)nearest - value)) {
            nearest = step;
         }
      }

      return nearest;
   }

   public static int snapSize(int size) {
      int clamped = Math.max(64, Math.min(224, size));
      return 64 + Math.round((clamped - 64) / 8.0F) * 8;
   }

   public static int snapEnlarged(int percent) {
      int clamped = Math.max(125, Math.min(400, percent));
      return 125 + Math.round((clamped - 125) / 25.0F) * 25;
   }

   public static int snapOpacity(int opacity) {
      int clamped = Math.max(20, Math.min(100, opacity));
      return 20 + Math.round((clamped - 20) / 5.0F) * 5;
   }

   private static Path file() {
      return Minecraft.getInstance().gameDirectory.toPath().resolve("diorama").resolve("config.json");
   }

   public enum Beams {
      OFF,
      TEMPORARY,
      ALL;

      // $VF: synthetic method
      private static Settings.Beams[] $values() {
         return new Settings.Beams[]{OFF, TEMPORARY, ALL};
      }
   }

   public enum ClaimsShown {
      OFF,
      FULLSCREEN,
      ALWAYS;

      // $VF: synthetic method
      private static Settings.ClaimsShown[] $values() {
         return new Settings.ClaimsShown[]{OFF, FULLSCREEN, ALWAYS};
      }
   }

   public enum Corner {
      TOP_RIGHT,
      TOP_LEFT,
      BOTTOM_RIGHT,
      BOTTOM_LEFT;

      // $VF: synthetic method
      private static Settings.Corner[] $values() {
         return new Settings.Corner[]{TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT};
      }
   }

   public enum DayNight {
      FOLLOW,
      DAY,
      NIGHT;

      // $VF: synthetic method
      private static Settings.DayNight[] $values() {
         return new Settings.DayNight[]{FOLLOW, DAY, NIGHT};
      }
   }

   public enum DrawingsShown {
      OFF,
      FULLSCREEN,
      ALWAYS;

      // $VF: synthetic method
      private static Settings.DrawingsShown[] $values() {
         return new Settings.DrawingsShown[]{OFF, FULLSCREEN, ALWAYS};
      }
   }

   public enum Frame {
      CLASSIC,
      MINECRAFT,
      NONE;

      // $VF: synthetic method
      private static Settings.Frame[] $values() {
         return new Settings.Frame[]{CLASSIC, MINECRAFT, NONE};
      }
   }

   public enum GameTime {
      OFF,
      DAY_24H,
      DAY_12H,
      ONLY_24H,
      ONLY_12H;

      // $VF: synthetic method
      private static Settings.GameTime[] $values() {
         return new Settings.GameTime[]{OFF, DAY_24H, DAY_12H, ONLY_24H, ONLY_12H};
      }
   }

   public enum NameTags {
      NEVER,
      FULLSCREEN,
      ALWAYS;

      // $VF: synthetic method
      private static Settings.NameTags[] $values() {
         return new Settings.NameTags[]{NEVER, FULLSCREEN, ALWAYS};
      }
   }

   public enum Quality {
      FANCY,
      FAST;

      // $VF: synthetic method
      private static Settings.Quality[] $values() {
         return new Settings.Quality[]{FANCY, FAST};
      }
   }

   public enum RadarStyle {
      HEADS,
      FRAMED,
      DOTS;

      // $VF: synthetic method
      private static Settings.RadarStyle[] $values() {
         return new Settings.RadarStyle[]{HEADS, FRAMED, DOTS};
      }
   }

   public enum Shape {
      SQUARE,
      ROUND;

      // $VF: synthetic method
      private static Settings.Shape[] $values() {
         return new Settings.Shape[]{SQUARE, ROUND};
      }
   }

   public static final class SizeAdapter extends TypeAdapter<Integer> {
      public void write(JsonWriter out, Integer value) throws IOException {
         out.value(value);
      }

      public Integer read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NUMBER) {
            return in.nextInt();
         }

         String name = in.nextString();

         return switch (name) {
            case "SMALL" -> 96;
            case "LARGE" -> 176;
            default -> 128;
         };
      }

      // $VF: synthetic method
      // $VF: bridge method
      public Object read(JsonReader param1) throws IOException {
         return this.read(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public void write(JsonWriter param1, Object nullx) throws IOException {
         this.write(var1, (Integer)nullx);
      }
   }

   public enum SpawnSpots {
      OFF,
      ALWAYS_DARK,
      AT_NIGHT_TOO;

      // $VF: synthetic method
      private static Settings.SpawnSpots[] $values() {
         return new Settings.SpawnSpots[]{OFF, ALWAYS_DARK, AT_NIGHT_TOO};
      }
   }

   public enum WaypointLabels {
      AUTO,
      ALWAYS,
      DISTANCE,
      NONE;

      // $VF: synthetic method
      private static Settings.WaypointLabels[] $values() {
         return new Settings.WaypointLabels[]{AUTO, ALWAYS, DISTANCE, NONE};
      }
   }

   public enum WebMap {
      OFF,
      THIS_COMPUTER,
      NETWORK;

      // $VF: synthetic method
      private static Settings.WebMap[] $values() {
         return new Settings.WebMap[]{OFF, THIS_COMPUTER, NETWORK};
      }
   }

   public enum WorldBorderShown {
      OFF,
      FULLSCREEN,
      ALWAYS;

      // $VF: synthetic method
      private static Settings.WorldBorderShown[] $values() {
         return new Settings.WorldBorderShown[]{OFF, FULLSCREEN, ALWAYS};
      }
   }
}
