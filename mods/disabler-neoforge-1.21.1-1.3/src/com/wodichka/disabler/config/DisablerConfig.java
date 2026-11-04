package com.wodichka.disabler.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public final class DisablerConfig {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private static final List<String> DEFAULT_BIOME_EXCEPTIONS = List.of("minecraft:mushroom_fields");
   private static final boolean DEFAULT_STORAGE_SCAN_ENABLED = true;
   private static final int DEFAULT_STORAGE_SCAN_INTERVAL_TICKS = 300;
   private static final int DEFAULT_STORAGE_SCAN_BLOCK_ENTITIES_PER_TICK = 512;
   private static final List<String> DEFAULT_STORAGE_SCAN_SKIPPED_NAMESPACES = List.of();
   private static final String CONFIG_FILE_NAME = "disabler-server.json";
   private static final String GUIDE_FILE_NAME = "DisablerGuide.md";
   private static final String LEGACY_CONFIG_FILE_NAME = "disabler-server.toml";
   private static final Pattern LEGACY_TOML_STRING_PATTERN = Pattern.compile("\"([^\"]+)\"");
   private static volatile DisablerConfig.Snapshot snapshot = DisablerConfig.Snapshot.defaults();

   private DisablerConfig() {
   }

   public static void load(Path configDirectory) {
      Path configPath = configDirectory.resolve("disabler-server.json");

      try {
         ensureConfigExists(configPath);

         try {
            writeGuide(configDirectory.resolve("DisablerGuide.md"));
         } catch (IOException exception) {
            LOGGER.warn("Failed to write the Disabler config guide", exception);
         }

         try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (parsed.isJsonObject()) {
               JsonObject root = parsed.getAsJsonObject();
               JsonObject normalized = normalizeConfig(root);
               if (!GSON.toJson(root).equals(GSON.toJson(normalized))) {
                  writeConfig(configPath, normalized);
               }

               snapshot = parseSnapshot(normalized);
               LOGGER.info("Loaded Disabler JSON config from {}", configPath);
               return;
            }

            LOGGER.warn("Disabler config '{}' must contain a JSON object. Keeping previous config.", configPath);
         }
      } catch (Exception exception) {
         LOGGER.error("Failed to load Disabler JSON config '{}'. Keeping previous config.", configPath, exception);
      }
   }

   public static boolean hasBlockedMobs() {
      return !snapshot.blockedMobIds().isEmpty();
   }

   public static boolean hasBlockedStructures() {
      return !snapshot.blockedStructureIds().isEmpty();
   }

   public static boolean hasBlockedBiomes() {
      return !snapshot.blockedBiomeIds().isEmpty();
   }

   public static boolean hasBlockedItems() {
      return !snapshot.blockedItemIds().isEmpty();
   }

   public static boolean hasBlockedDimensions() {
      return !snapshot.blockedDimensionIds().isEmpty();
   }

   public static boolean hasBlockedBiomeRules() {
      return hasBlockedBiomes();
   }

   public static boolean isBlockedMob(EntityType<?> entityType) {
      return snapshot.blockedMobIds().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
   }

   public static boolean isBlockedStructure(ResourceLocation structureId) {
      return snapshot.blockedStructureIds().contains(structureId);
   }

   public static boolean isBlockedBiome(ResourceLocation biomeId) {
      return snapshot.blockedBiomeIds().contains(biomeId);
   }

   public static boolean isBlockedItem(Item item) {
      return snapshot.blockedItemIds().contains(BuiltInRegistries.ITEM.getKey(item));
   }

   public static boolean isBlockedDimension(ResourceKey<Level> dimensionKey) {
      return isBlockedDimension(dimensionKey.location());
   }

   public static boolean isBlockedDimension(ResourceLocation dimensionId) {
      return snapshot.blockedDimensionIds().contains(dimensionId);
   }

   public static Set<ResourceLocation> getBlockedBiomeIds() {
      return snapshot.blockedBiomeIds();
   }

   public static Set<ResourceLocation> getBiomeExceptionIds() {
      return snapshot.biomeExceptionIds();
   }

   public static boolean shouldScanStorageInventories() {
      return snapshot.storageScanEnabled();
   }

   public static int getStorageScanIntervalTicks() {
      return snapshot.storageScanIntervalTicks();
   }

   public static int getStorageScanBlockEntitiesPerTick() {
      return snapshot.storageScanBlockEntitiesPerTick();
   }

   public static Set<String> getStorageScanSkippedNamespaces() {
      return snapshot.storageScanSkippedNamespaces();
   }

   private static void ensureConfigExists(Path configPath) throws IOException {
      if (!Files.exists(configPath)) {
         Files.createDirectories(configPath.getParent());
         JsonObject config = createInitialConfig(configPath);
         writeConfig(configPath, config);
         LOGGER.info("Created Disabler JSON config at {}", configPath);
      }
   }

   private static void writeConfig(Path configPath, JsonObject config) throws IOException {
      try (Writer writer = Files.newBufferedWriter(configPath)) {
         GSON.toJson(config, writer);
      }
   }

   private static void writeGuide(Path guidePath) throws IOException {
      String guide = configGuide();
      if (!Files.exists(guidePath) || !guide.equals(Files.readString(guidePath, StandardCharsets.UTF_8))) {
         Files.createDirectories(guidePath.getParent());
         Files.writeString(guidePath, guide, StandardCharsets.UTF_8);
         LOGGER.info("Wrote Disabler config guide to {}", guidePath);
      }
   }

   private static JsonObject createInitialConfig(Path configPath) throws IOException {
      Path legacyConfigPath = configPath.resolveSibling("disabler-server.toml");
      if (!Files.exists(legacyConfigPath)) {
         return defaultConfigJson();
      }

      JsonObject migrated = migrateLegacyToml(legacyConfigPath);
      LOGGER.info("Migrated Disabler config from '{}' to JSON format", legacyConfigPath);
      return migrated;
   }

   private static JsonObject defaultConfigJson() {
      JsonObject root = new JsonObject();
      root.add("blocked_mobs", new JsonArray());
      root.add("blocked_biomes", new JsonArray());
      JsonArray exceptions = new JsonArray();

      for (String exception : DEFAULT_BIOME_EXCEPTIONS) {
         exceptions.add(exception);
      }

      root.add("biome_exceptions", exceptions);
      root.add("blocked_structures", new JsonArray());
      root.add("blocked_dimensions", new JsonArray());
      root.add("blocked_items", new JsonArray());
      JsonObject storageScan = new JsonObject();
      storageScan.addProperty("enabled", true);
      storageScan.addProperty("interval_ticks", 300);
      storageScan.addProperty("block_entities_per_tick", 512);
      storageScan.add("skipped_namespaces", toJsonArray(DEFAULT_STORAGE_SCAN_SKIPPED_NAMESPACES));
      root.add("storage_scan", storageScan);
      return root;
   }

   private static JsonObject normalizeConfig(JsonObject root) {
      JsonObject defaults = defaultConfigJson();
      JsonObject normalized = new JsonObject();
      normalized.add("blocked_mobs", readKnownValue(root, "blocked_mobs", defaults.get("blocked_mobs")));
      normalized.add("blocked_biomes", readKnownValue(root, "blocked_biomes", defaults.get("blocked_biomes")));
      normalized.add("biome_exceptions", readKnownValue(root, "biome_exceptions", defaults.get("biome_exceptions")));
      normalized.add("blocked_structures", readKnownValue(root, "blocked_structures", defaults.get("blocked_structures")));
      normalized.add("blocked_dimensions", readKnownValue(root, "blocked_dimensions", defaults.get("blocked_dimensions")));
      normalized.add("blocked_items", readKnownValue(root, "blocked_items", defaults.get("blocked_items")));
      normalized.add("storage_scan", normalizeStorageScan(root, defaults.getAsJsonObject("storage_scan")));
      return normalized;
   }

   private static JsonObject normalizeStorageScan(JsonObject root, JsonObject defaults) {
      JsonObject source = root.has("storage_scan") && root.get("storage_scan").isJsonObject() ? root.getAsJsonObject("storage_scan") : new JsonObject();
      JsonObject normalized = new JsonObject();
      normalized.add("enabled", readStorageValue(source, "enabled", defaults));
      normalized.add("interval_ticks", readStorageValue(source, "interval_ticks", defaults));
      normalized.add("block_entities_per_tick", readStorageValue(source, "block_entities_per_tick", defaults));
      normalized.add("skipped_namespaces", readStorageValue(source, "skipped_namespaces", defaults));
      return normalized;
   }

   private static JsonElement readKnownValue(JsonObject root, String key, JsonElement defaultValue) {
      return root.has(key) ? root.get(key).deepCopy() : defaultValue.deepCopy();
   }

   private static JsonElement readStorageValue(JsonObject source, String key, JsonObject defaults) {
      return source.has(key) ? source.get(key).deepCopy() : defaults.get(key).deepCopy();
   }

   private static String configGuide() {
      return "# Disabler configuration guide\n\nThe server config is `config/disabler-server.json`. Resource ids use the `namespace:path` format. An empty list disables that blocking category.\n\n## `blocked_mobs`\nEntity type ids that cannot spawn or join a server level. Example: `minecraft:zombie`.\n\n## `blocked_biomes`\nBiome ids replaced during new chunk generation. Replacements are climate-compatible and keep land biomes on land and water biomes in water.\n\n## `biome_exceptions`\nBiome ids that cannot be selected as replacements. `minecraft:mushroom_fields` is excluded by default because it has special terrain requirements.\n\n## `blocked_structures`\nStructure ids prevented from starting in newly generated chunks. Example: `minecraft:village_plains`.\n\n## `blocked_dimensions`\nVanilla or modded dimension ids that entities cannot enter. Example: `minecraft:the_nether`.\n\n## `blocked_items`\nItem ids removed from generated loot, item entities, player inventories, open menus, and supported loaded storages.\n\n## `storage_scan`\nControls periodic removal of blocked items from compatible loaded block-entity inventories.\n\n- `enabled`: enables or disables the periodic storage scan.\n- `interval_ticks`: delay between complete scan cycles. 20 ticks are approximately one second; the default is 300 ticks.\n- `block_entities_per_tick`: maximum block entities processed per tick during a scan cycle.\n- `skipped_namespaces`: mod namespaces whose physical storages must not be scanned. Lootr is protected internally and does not need to be listed.\n\nLootr personal inventories are filtered through dedicated compatibility. Its physical chests, barrels, frames, and other containers are not converted or scanned as ordinary inventories.\n";
   }

   private static JsonObject migrateLegacyToml(Path legacyConfigPath) throws IOException {
      List<String> lines = Files.readAllLines(legacyConfigPath);
      JsonObject root = defaultConfigJson();
      copyLegacyArray(root, lines, "blocked_mobs", "blocked_mobs");
      copyLegacyArray(root, lines, "blocked_structures", "blocked_structures");
      copyLegacyArray(root, lines, "blocked_biomes", "blocked_biomes");
      copyLegacyArray(root, lines, "blocked_items", "blocked_items");
      copyLegacyArray(root, lines, "blocked_dimensions", "blocked_dimensions");
      copyLegacyArray(root, lines, "exceptions", "biome_exceptions");
      return root;
   }

   private static void copyLegacyArray(JsonObject root, List<String> lines, String legacyKey, String jsonKey) {
      List<String> values = readLegacyTomlArray(lines, legacyKey);
      if (values != null) {
         root.add(jsonKey, toJsonArray(values));
      }
   }

   private static List<String> readLegacyTomlArray(List<String> lines, String key) {
      for (String rawLine : lines) {
         String line = rawLine.split("#", 2)[0].trim();
         if (line.startsWith(key) && line.indexOf(61) >= 0) {
            int start = line.indexOf(91);
            int end = line.lastIndexOf(93);
            if (start >= 0 && end >= start) {
               List<String> values = new ArrayList<>();
               Matcher matcher = LEGACY_TOML_STRING_PATTERN.matcher(line.substring(start + 1, end));

               while (matcher.find()) {
                  values.add(matcher.group(1));
               }

               return List.copyOf(values);
            }

            return List.of();
         }
      }

      return null;
   }

   private static JsonArray toJsonArray(List<String> values) {
      JsonArray array = new JsonArray();

      for (String value : values) {
         array.add(value);
      }

      return array;
   }

   private static DisablerConfig.Snapshot parseSnapshot(JsonObject root) {
      return new DisablerConfig.Snapshot(
         parseLocations(readStringList(root, "blocked_mobs", List.of()), "mob"),
         parseLocations(readStringList(root, "blocked_structures", List.of()), "structure"),
         parseLocations(readStringList(root, "blocked_biomes", List.of()), "biome"),
         parseLocations(readStringList(root, "blocked_items", List.of()), "item"),
         parseLocations(readStringList(root, "blocked_dimensions", List.of()), "dimension"),
         parseLocations(readStringList(root, "biome_exceptions", DEFAULT_BIOME_EXCEPTIONS), "biome exception"),
         readStorageScanEnabled(root),
         readStorageScanIntervalTicks(root),
         readStorageScanBlockEntitiesPerTick(root),
         readStorageScanSkippedNamespaces(root)
      );
   }

   private static boolean readStorageScanEnabled(JsonObject root) {
      JsonObject storageScan = readObject(root, "storage_scan");
      return storageScan == null ? true : readBoolean(storageScan.get("enabled"), "storage_scan.enabled", true);
   }

   private static int readStorageScanIntervalTicks(JsonObject root) {
      JsonObject storageScan = readObject(root, "storage_scan");
      int value = storageScan == null ? 300 : readInt(storageScan.get("interval_ticks"), "storage_scan.interval_ticks", 300);
      return Math.max(20, value);
   }

   private static int readStorageScanBlockEntitiesPerTick(JsonObject root) {
      JsonObject storageScan = readObject(root, "storage_scan");
      int value = storageScan == null ? 512 : readInt(storageScan.get("block_entities_per_tick"), "storage_scan.block_entities_per_tick", 512);
      return Math.max(1, value);
   }

   private static Set<String> readStorageScanSkippedNamespaces(JsonObject root) {
      JsonObject storageScan = readObject(root, "storage_scan");
      List<String> rawNamespaces = storageScan == null
         ? DEFAULT_STORAGE_SCAN_SKIPPED_NAMESPACES
         : readArray(storageScan.get("skipped_namespaces"), "storage_scan.skipped_namespaces", DEFAULT_STORAGE_SCAN_SKIPPED_NAMESPACES);
      Set<String> namespaces = new LinkedHashSet<>();

      for (String rawNamespace : rawNamespaces) {
         String namespace = rawNamespace.trim().toLowerCase(Locale.ROOT);
         if (!namespace.isEmpty()) {
            namespaces.add(namespace);
         }
      }

      return Set.copyOf(namespaces);
   }

   private static JsonObject readObject(JsonObject root, String key) {
      JsonElement element = root.get(key);
      if (element == null || element.isJsonNull()) {
         return null;
      } else if (!element.isJsonObject()) {
         LOGGER.warn("Ignoring Disabler config key '{}' because it is not a JSON object", key);
         return null;
      } else {
         return element.getAsJsonObject();
      }
   }

   private static List<String> readStringList(JsonObject root, String key, List<String> defaultValue) {
      return readArray(root.get(key), key, defaultValue);
   }

   private static boolean readBoolean(JsonElement element, String name, boolean defaultValue) {
      if (element == null || element.isJsonNull()) {
         return defaultValue;
      }

      if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
         return element.getAsBoolean();
      }

      LOGGER.warn("Ignoring Disabler config key '{}' because it is not a boolean", name);
      return defaultValue;
   }

   private static int readInt(JsonElement element, String name, int defaultValue) {
      if (element == null || element.isJsonNull()) {
         return defaultValue;
      }

      if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
         return element.getAsInt();
      }

      LOGGER.warn("Ignoring Disabler config key '{}' because it is not a number", name);
      return defaultValue;
   }

   private static List<String> readArray(JsonElement element, String name, List<String> defaultValue) {
      if (element != null && !element.isJsonNull()) {
         if (!element.isJsonArray()) {
            LOGGER.warn("Ignoring Disabler config key '{}' because it is not a JSON array", name);
            return defaultValue;
         }

         List<String> values = new ArrayList<>();

         for (JsonElement entry : element.getAsJsonArray()) {
            if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) {
               values.add(entry.getAsString());
            } else {
               LOGGER.warn("Ignoring non-string value '{}' in Disabler config key '{}'", entry, name);
            }
         }

         return List.copyOf(values);
      } else {
         return defaultValue;
      }
   }

   private static Set<ResourceLocation> parseLocations(List<String> rawIds, String kind) {
      Set<ResourceLocation> parsedIds = new LinkedHashSet<>();

      for (String rawId : rawIds) {
         ResourceLocation id = ResourceLocation.tryParse(rawId);
         if (id == null) {
            LOGGER.warn("Ignoring invalid {} id '{}' in Disabler config", kind, rawId);
         } else {
            parsedIds.add(id);
         }
      }

      return Set.copyOf(parsedIds);
   }

   private record Snapshot(
      Set<ResourceLocation> blockedMobIds,
      Set<ResourceLocation> blockedStructureIds,
      Set<ResourceLocation> blockedBiomeIds,
      Set<ResourceLocation> blockedItemIds,
      Set<ResourceLocation> blockedDimensionIds,
      Set<ResourceLocation> biomeExceptionIds,
      boolean storageScanEnabled,
      int storageScanIntervalTicks,
      int storageScanBlockEntitiesPerTick,
      Set<String> storageScanSkippedNamespaces
   ) {
      private static DisablerConfig.Snapshot defaults() {
         return new DisablerConfig.Snapshot(
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of(),
            DisablerConfig.parseLocations(DisablerConfig.DEFAULT_BIOME_EXCEPTIONS, "biome exception"),
            true,
            300,
            512,
            Set.copyOf(DisablerConfig.DEFAULT_STORAGE_SCAN_SKIPPED_NAMESPACES)
         );
      }
   }
}
