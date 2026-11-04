package org.embeddedt.modernfix.core.config;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.embeddedt.modernfix.annotation.FeatureLevel;
import org.embeddedt.modernfix.annotation.IgnoreOutsideDev;
import org.embeddedt.modernfix.annotation.RequiresFeatureLevel;
import org.embeddedt.modernfix.annotation.RequiresMod;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;
import org.embeddedt.modernfix.platform.ModernFixPlatformHooks;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;

public class ModernFixEarlyConfig {
   private static final Logger LOGGER = LogManager.getLogger("ModernFixConfig");
   private final Map<String, Option<?>> options = new HashMap<>();
   private final Multimap<String, Option<?>> optionsByCategory = HashMultimap.create();
   private static final boolean ALLOW_OVERRIDE_OVERRIDES = Boolean.getBoolean("modernfix.unsupported.allowOverriding");
   public static final boolean OPTIFINE_PRESENT;
   private File configFile;
   private static final String MIXIN_DESC;
   private static final String MIXIN_CLIENT_ONLY_DESC;
   private static final String MIXIN_REQUIRES_MOD_DESC;
   private static final String MIXIN_DEV_ONLY_DESC;
   private static final String FEATURE_LEVEL_ANNOTATION_DESC;
   private static final Pattern PLATFORM_PREFIX;
   private final Set<String> mixinOptions = new ObjectOpenHashSet();
   private final Map<String, String> mixinsMissingMods = new Object2ObjectOpenHashMap();
   private final Map<String, FeatureLevel> mixinsRequiringLowerStability = new Object2ObjectOpenHashMap();
   private final Map<String, ModernFixEarlyConfig.PackageMetadata> packageMetadataCache = new HashMap<>();
   public static boolean isFabric;
   private static final boolean isDevEnv;
   private static final ImmutableMap<String, Boolean> DEFAULT_SETTING_OVERRIDES;

   private static boolean modPresent(String modId) {
      return modId.equals("optifine") ? OPTIFINE_PRESENT : ModernFixPlatformHooks.INSTANCE.modPresent(modId);
   }

   public static String sanitize(String mixinClassName) {
      return PLATFORM_PREFIX.matcher(mixinClassName).replaceFirst("");
   }

   public Map<String, String> getPermanentlyDisabledMixins() {
      return this.mixinsMissingMods;
   }

   private static <T> T getAnnotationValue(AnnotationNode ann, String key) {
      if (ann.values == null) {
         return null;
      }

      for (int i = 0; i < ann.values.size(); i += 2) {
         if (ann.values.get(i).equals(key)) {
            return (T)ann.values.get(i + 1);
         }
      }

      return null;
   }

   private ModernFixEarlyConfig.PackageMetadata loadPackageMetadata(String packageResourcePath) {
      String classPath = packageResourcePath + "/package-info.class";

      try (InputStream stream = ModernFixEarlyConfig.class.getClassLoader().getResourceAsStream(classPath)) {
         if (stream == null) {
            return new ModernFixEarlyConfig.PackageMetadata();
         }

         ClassReader reader = new ClassReader(stream);
         ClassNode node = new ClassNode();
         reader.accept(node, 7);
         ModernFixEarlyConfig.PackageMetadata meta = new ModernFixEarlyConfig.PackageMetadata();
         List<AnnotationNode> annotations = new ArrayList<>();
         if (node.invisibleAnnotations != null) {
            annotations.addAll(node.invisibleAnnotations);
         }

         if (node.visibleAnnotations != null) {
            annotations.addAll(node.visibleAnnotations);
         }

         for (AnnotationNode annotation : annotations) {
            if (Objects.equals(annotation.desc, MIXIN_REQUIRES_MOD_DESC)) {
               meta.requiredModId = getAnnotationValue(annotation, "value");
            } else if (Objects.equals(annotation.desc, FEATURE_LEVEL_ANNOTATION_DESC)) {
               String[] enumVal = getAnnotationValue(annotation, "value");
               meta.requiredLevel = FeatureLevel.valueOf(enumVal[1]);
            }
         }

         return meta;
      } catch (IOException e) {
         LOGGER.error("Error scanning package-info " + classPath, e);
         return new ModernFixEarlyConfig.PackageMetadata();
      }
   }

   private ModernFixEarlyConfig.PackageMetadata getOrLoadPackageMetadata(String packageResourcePath) {
      return this.packageMetadataCache.computeIfAbsent(packageResourcePath, this::loadPackageMetadata);
   }

   private void scanForAndBuildMixinOptions() {
      List<String> configFiles = ImmutableList.of("modernfix-modernfix.mixins.json");
      List<String> mixinPaths = new ArrayList<>();

      for (String configFile : configFiles) {
         InputStream stream = ModernFixEarlyConfig.class.getClassLoader().getResourceAsStream(configFile);
         if (stream != null) {
            try (Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
               JsonObject configObject = (JsonObject)new JsonParser().parse(reader);
               List<JsonElement> mixinList = Stream.of("mixins", "client")
                  .map(key -> Optional.ofNullable(configObject.getAsJsonArray(key)))
                  .flatMap(
                     arr -> arr.<Stream<? extends JsonElement>>map(jsonElements -> StreamSupport.stream(jsonElements.spliterator(), false))
                        .orElseGet(() -> Stream.of())
                  )
                  .collect(Collectors.toList());
               String packageName = configObject.get("package").getAsString().replace('.', '/');

               for (JsonElement mixin : mixinList) {
                  mixinPaths.add(packageName + "/" + mixin.getAsString().replace('.', '/') + ".class");
               }
            } catch (IOException | JsonParseException e) {
               LOGGER.error("Error loading config " + configFile, e);
            }
         }
      }

      Splitter dotSplitter = Splitter.on('.');

      for (String mixinPath : mixinPaths) {
         try {
            try (InputStream stream = ModernFixEarlyConfig.class.getClassLoader().getResourceAsStream(mixinPath)) {
               ClassReader reader = new ClassReader(stream);
               ClassNode node = new ClassNode();
               reader.accept(node, 7);
               if (node.invisibleAnnotations != null) {
                  boolean isMixin = false;
                  boolean isClientOnly = false;
                  boolean requiredModPresent = true;
                  boolean isDevOnly = false;
                  String requiredModId = "";
                  FeatureLevel requiredLevel = FeatureLevel.GA;

                  for (AnnotationNode annotation : node.invisibleAnnotations) {
                     if (Objects.equals(annotation.desc, MIXIN_DESC)) {
                        isMixin = true;
                     } else if (Objects.equals(annotation.desc, MIXIN_CLIENT_ONLY_DESC)) {
                        isClientOnly = true;
                     } else if (Objects.equals(annotation.desc, MIXIN_REQUIRES_MOD_DESC)) {
                        String modId = getAnnotationValue(annotation, "value");
                        if (modId != null) {
                           requiredModPresent = modId.startsWith("!") ? !modPresent(modId.substring(1)) : modPresent(modId);
                           requiredModId = modId;
                        }
                     } else if (Objects.equals(annotation.desc, MIXIN_DEV_ONLY_DESC)) {
                        isDevOnly = true;
                     } else if (Objects.equals(annotation.desc, FEATURE_LEVEL_ANNOTATION_DESC)) {
                        String[] enumVal = getAnnotationValue(annotation, "value");
                        requiredLevel = FeatureLevel.valueOf(enumVal[1]);
                     }
                  }

                  String classPackagePath = mixinPath.substring(0, mixinPath.lastIndexOf(47));
                  int mixinRootEnd = classPackagePath.indexOf("/mixin");
                  if (mixinRootEnd >= 0) {
                     String mixinRoot = classPackagePath.substring(0, mixinRootEnd + "/mixin".length());
                     String walkPkg = mixinRoot;

                     while (walkPkg.length() < classPackagePath.length()) {
                        int nextSlash = classPackagePath.indexOf(47, walkPkg.length() + 1);
                        walkPkg = nextSlash == -1 ? classPackagePath : classPackagePath.substring(0, nextSlash);
                        ModernFixEarlyConfig.PackageMetadata pkgMeta = this.getOrLoadPackageMetadata(walkPkg);
                        if (requiredModPresent && pkgMeta.requiredModId != null) {
                           boolean present = pkgMeta.requiredModId.startsWith("!")
                              ? !modPresent(pkgMeta.requiredModId.substring(1))
                              : modPresent(pkgMeta.requiredModId);
                           if (!present) {
                              requiredModPresent = false;
                              requiredModId = pkgMeta.requiredModId;
                           }
                        }

                        if (pkgMeta.requiredLevel != null && pkgMeta.requiredLevel.ordinal() > requiredLevel.ordinal()) {
                           requiredLevel = pkgMeta.requiredLevel;
                        }
                     }
                  }

                  if (isMixin && (!isDevOnly || ModernFixPlatformHooks.INSTANCE.isDevEnv())) {
                     String mixinClassName = sanitize(node.name.replace('/', '.')).replace("org.embeddedt.modernfix.mixin.", "");
                     if (!requiredModPresent) {
                        this.mixinsMissingMods.put(mixinClassName, requiredModId);
                     } else if (isClientOnly && !ModernFixPlatformHooks.INSTANCE.isClient()) {
                        this.mixinsMissingMods.put(mixinClassName, "[not client]");
                     }

                     if (requiredLevel != FeatureLevel.GA) {
                        this.mixinsRequiringLowerStability.put(mixinClassName, requiredLevel);
                     }

                     String mixinCategoryName = "mixin." + mixinClassName.substring(0, mixinClassName.lastIndexOf(46));
                     this.mixinOptions.add(mixinCategoryName);
                  }
                  continue;
               }
            }

            return;
         } catch (IOException e) {
            LOGGER.error("Error scanning file " + mixinPath, e);
         }
      }
   }

   private ModernFixEarlyConfig(File file) {
      this.configFile = file;
      OptionCategories.load();
      this.scanForAndBuildMixinOptions();
      this.mixinOptions.addAll(DEFAULT_SETTING_OVERRIDES.keySet());

      for (String optionName : this.mixinOptions) {
         boolean defaultEnabled = (Boolean)DEFAULT_SETTING_OVERRIDES.getOrDefault(optionName, true);
         Option<Boolean> option = new Option<>(optionName, OptionType.BOOLEAN, defaultEnabled, false);
         this.options.putIfAbsent(optionName, option);
         this.optionsByCategory.put(OptionCategories.getCategoryForOption(optionName), option);
      }

      this.addBuiltInOptions();

      for (Entry<String, Option<?>> entry : this.options.entrySet()) {
         int idx = entry.getKey().lastIndexOf(46);
         if (idx > 0) {
            String potentialParentKey = entry.getKey().substring(0, idx);
            Option<?> potentialParent = this.options.get(potentialParentKey);
            if (potentialParent != null) {
               entry.getValue().setParent(potentialParent);
            }
         }
      }

      this.addMixinRule("launch.class_search_cache", true);
      this.disableIfModPresent("mixin.perf.thread_priorities", "smoothboot", "threadtweak");
      this.disableIfModPresent("mixin.perf.boost_worker_count", "smoothboot", "threadtweak");
      this.disableIfModPresent("mixin.perf.compress_biome_container", "chocolate", "betterendforge", "skyblockbuilder", "modern_beta", "worldedit");
      this.disableIfModPresent("mixin.bugfix.mc218112", "performant");
      this.disableIfModPresent("mixin.bugfix.remove_block_chunkloading", "performant");
      this.disableIfModPresent("mixin.bugfix.paper_chunk_patches", "c2me");
      this.disableIfModPresent("mixin.bugfix.preserve_early_window_pos", "better_loading_screen");
      this.disableIfModPresent("mixin.perf.dynamic_dfu", "litematica");
      this.disableIfModPresent("mixin.perf.cache_strongholds", "littletiles");
      this.disableIfModPresent("mixin.perf.deduplicate_wall_shapes", "dashloader");
      this.disableIfModPresent("mixin.perf.nbt_memory_usage", "c2me");
      this.disableIfModPresent("mixin.bugfix.item_cache_flag", "lithium", "canary", "radium");
      this.disableIfModPresent("mixin.bugfix.chunk_deadlock", "c2me", "dimthread");
      this.disableIfModPresent("mixin.perf.release_protochunks", "c2me", "moonrise");
      this.disableIfModPresent("mixin.launch.class_search_cache", "optifine");
      this.disableIfModPresent("mixin.perf.faster_texture_stitching", "optifine");
      this.disableIfModPresent("mixin.bugfix.entity_pose_stack", "optifine");
      this.disableIfModPresent("mixin.perf.datapack_reload_exceptions", "cyanide");
      this.disableIfModPresent("mixin.bugfix.buffer_builder_leak", "isometric-renders", "witherstormmod");
      this.disableIfModPresent("mixin.feature.remove_chat_signing", "nochatreports");
      this.disableIfModPresent("mixin.perf.faster_texture_loading", "stitch", "optifine", "changed");
      this.disableIfModPresent("mixin.perf.faster_ingredients", "vmp", "prefab");
      this.disableIfModPresent("mixin.perf.smart_ingredient_sync", "crafttweaker");
      if (isFabric) {
         this.disableIfModPresent("mixin.bugfix.packet_leak", "memoryleakfix");
      }

      this.checkBlockstateCacheRebuilds();
      this.checkModelDataManager();
   }

   private void checkBlockstateCacheRebuilds() {
      if (ModernFixPlatformHooks.INSTANCE.isDevEnv()) {
         try {
            URL deobfClass = isFabric
               ? ModernFixEarlyConfig.class.getResource("/net/minecraft/world/level/Level.class")
               : ModernFixEarlyConfig.class.getClassLoader().getResource("/net/minecraft/world/level/Level.class");
            if (deobfClass == null) {
               LOGGER.warn("We are in a non-Mojmap dev environment. Disabling blockstate cache patch");
               this.options.get("mixin.perf.reduce_blockstate_cache_rebuilds").asBoolean().addModOverride(false, "[not mojmap]");
            }
         } catch (Throwable e) {
            e.printStackTrace();
         }
      }
   }

   private void checkModelDataManager() {
      if (!isFabric && modPresent("rubidium") && !modPresent("embeddium")) {
         Option<?> option = this.options.get("mixin.bugfix.model_data_manager_cme");
         if (option != null) {
            LOGGER.warn("ModelDataManager bugfixes have been disabled to prevent broken rendering with Rubidium installed. Please migrate to Embeddium.");
            option.asBoolean().addModOverride(false, "rubidium");
         }
      }
   }

   private void disableIfModPresent(String configName, String... ids) {
      for (String id : ids) {
         if (!ModernFixPlatformHooks.INSTANCE.isEarlyLoadingNormally() || modPresent(id)) {
            Option<?> option = this.options.get(configName);
            if (option != null) {
               option.asBoolean().addModOverride(false, id);
            }
         }
      }
   }

   private <T> void addBuiltInOption(String name, OptionType<T> type, T initialValue) {
      this.options.putIfAbsent(name, new Option<>(name, type, initialValue, false));
   }

   private void addBuiltInOptions() {
      this.addBuiltInOption("stability_level", OptionType.enumType(FeatureLevel.class), FeatureLevel.GA);
   }

   private void addMixinRule(String mixin, boolean enabled) {
      String name = getMixinRuleName(mixin);
      if (this.options.putIfAbsent(name, new Option<>(name, OptionType.BOOLEAN, enabled, false)) != null) {
         throw new IllegalStateException("Mixin rule already defined: " + mixin);
      }
   }

   private void readJVMProperties() {
      for (String optionKey : this.options.keySet()) {
         String value = System.getProperty("modernfix.config." + optionKey);
         if (value != null && value.length() != 0) {
            try {
               this.options.get(optionKey).setFromString(value, true);
               ModernFixMixinPlugin.instance.logger.info("Configured {} to '{}' via JVM property.", optionKey, value);
            } catch (RuntimeException e) {
               ModernFixMixinPlugin.instance.logger.warn("Invalid value '{}' for JVM property '{}', ignoring", value, optionKey);
            }
         }
      }
   }

   private void readGlobalProperties() {
      try {
         Path minecraftFolder;
         if (SystemUtils.IS_OS_MAC) {
            minecraftFolder = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "minecraft");
         } else if (SystemUtils.IS_OS_WINDOWS) {
            minecraftFolder = Paths.get(System.getenv("APPDATA"), ".minecraft");
         } else {
            minecraftFolder = Paths.get(System.getProperty("user.home"), ".minecraft");
         }

         Path globalPropsFile = minecraftFolder.resolve("global").resolve("modernfix-global-mixins.properties");
         if (Files.exists(globalPropsFile)) {
            Properties properties = new Properties();

            try (InputStream is = Files.newInputStream(globalPropsFile)) {
               properties.load(is);
            }

            if (!properties.isEmpty()) {
               LOGGER.info(
                  "Global properties specified: [{}]",
                  properties.entrySet().stream().map(ex -> ex.getKey() + "=" + ex.getValue()).collect(Collectors.joining(", "))
               );
               this.readProperties(properties);
            }
         }
      } catch (Exception e) {
         LOGGER.error("Error reading global properties file", e);
      }
   }

   private void readProperties(Properties props) {
      if (ALLOW_OVERRIDE_OVERRIDES) {
         LOGGER.fatal(
            "JVM argument given to override mod overrides. Issues opened with this option present will be ignored unless they can be reproduced without."
         );
      }

      for (Entry<Object, Object> entry : props.entrySet()) {
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         Option<?> option = this.options.get(key);
         if (option == null) {
            LOGGER.warn("No configuration key exists with name '{}', ignoring", key);
         } else if (!ALLOW_OVERRIDE_OVERRIDES && option.isModDefined()) {
            LOGGER.warn("Option '{}' already disabled by a mod. Ignoring user configuration", key);
         } else {
            try {
               option.setFromString(value, true);
            } catch (RuntimeException e) {
               LOGGER.warn("Invalid value '{}' encountered for configuration key '{}', ignoring", value, key);
            }
         }
      }
   }

   public Option<?> getEffectiveOptionForMixin(String mixinClassName) {
      int lastSplit = 0;
      Option<?> rule = null;

      int nextSplit;
      while ((nextSplit = mixinClassName.indexOf(46, lastSplit)) != -1) {
         String key = getMixinRuleName(mixinClassName.substring(0, nextSplit));
         Option<?> candidate = this.options.get(key);
         if (candidate != null) {
            rule = candidate;
            if (!rule.asBoolean().getValue()) {
               return rule;
            }
         }

         lastSplit = nextSplit + 1;
      }

      return rule;
   }

   public static ModernFixEarlyConfig load(File file) {
      ModernFixEarlyConfig config = new ModernFixEarlyConfig(file);
      Properties props = new Properties();
      if (!Boolean.getBoolean("modernfix.ignoreConfigForTesting")) {
         if (file.exists()) {
            try (FileInputStream fin = new FileInputStream(file)) {
               props.load(fin);
            } catch (IOException e) {
               throw new RuntimeException("Could not load config file", e);
            }

            config.readProperties(props);
         }

         try {
            config.save();
         } catch (IOException e) {
            LOGGER.warn("Could not write configuration file", e);
         }

         config.readGlobalProperties();
         config.readJVMProperties();
         config.finalizeLoad();
      }

      return config;
   }

   public void finalizeLoad() {
      FeatureLevel stabilityLevel = this.getOptionValue("stability_level", FeatureLevel.class);

      for (Entry<String, FeatureLevel> entry : this.mixinsRequiringLowerStability.entrySet()) {
         if (!stabilityLevel.isAtLeast(entry.getValue())) {
            this.mixinsMissingMods.put(entry.getKey(), "[feature level: requires " + entry.getValue() + "]");
         }
      }
   }

   public void save() throws IOException {
      File dir = this.configFile.getParentFile();
      if (!dir.exists()) {
         if (!dir.mkdirs()) {
            throw new IOException("Could not create parent directories");
         }
      } else if (!dir.isDirectory()) {
         throw new IOException("The parent file is not a directory");
      }

      try (Writer writer = new FileWriter(this.configFile)) {
         writer.write("# This is the configuration file for ModernFix.\n");
         writer.write("# In general, prefer using the config screen to editing this file. It can be accessed\n");
         writer.write("# via the standard mod menu on your respective mod loader. Changes will, however,\n");
         writer.write("# require restarting the game to take effect.\n");
         writer.write("#\n");
         writer.write("# The following options can be enabled or disabled if there is a compatibility issue.\n");
         writer.write("# Add a line with your option name and =true or =false at the bottom of the file to enable\n");
         writer.write("# or disable a rule. For example:\n");
         writer.write("#   mixin.perf.dynamic_resources=true\n");
         writer.write("# Do not include the #. You may reset to defaults by deleting this file.\n");
         writer.write("#\n");
         writer.write("# To enable features that are still in testing, add a line at the bottom setting the stability level:\n");
         writer.write("#   stability_level=BETA\n");
         writer.write("#\n");
         writer.write("# Available options:\n");
         List<String> keys = this.options.keySet().stream().filter(keyx -> !keyx.equals("mixin.core")).sorted().collect(Collectors.toList());

         for (String line : keys) {
            if (!line.equals("mixin.core")) {
               Option<?> option = this.options.get(line);
               String extraContext = "";
               if (option != null) {
                  if (!option.isUserDefined()) {
                     extraContext = "=" + option.getSerializedValue() + " # " + (option.isModDefined() ? "(overridden for mod compat)" : "(default)");
                  } else {
                     boolean defaultEnabled = (Boolean)DEFAULT_SETTING_OVERRIDES.getOrDefault(line, true);
                     extraContext = "=" + defaultEnabled + " # (default)";
                  }
               }

               writer.write("#  " + line + extraContext + "\n");
            }
         }

         writer.write("#\n");
         writer.write("# User overrides go here.\n");

         for (String key : keys) {
            Option<?> option = this.options.get(key);
            if (option.isUserDefined()) {
               writer.write(key + "=" + option.getSerializedValue() + "\n");
            }
         }
      }
   }

   private static String getMixinRuleName(String name) {
      return "mixin." + name;
   }

   public int getOptionCount() {
      return this.options.size();
   }

   public int getOptionOverrideCount() {
      return (int)this.options.values().stream().filter(Option::isOverridden).count();
   }

   public Map<String, Option<?>> getOptionMap() {
      return Collections.unmodifiableMap(this.options);
   }

   public Multimap<String, Option<?>> getOptionCategoryMap() {
      return Multimaps.unmodifiableMultimap(this.optionsByCategory);
   }

   public <T> T getOptionValue(String optionName, Class<T> type) {
      Option<?> option = this.options.get(optionName);
      if (option == null) {
         throw new IllegalStateException("Attempting to read option '" + optionName + "' that is not registered!");
      } else {
         return option.asType(type).getValue();
      }
   }

   static {
      boolean hasOfClass = false;

      try {
         Class.forName("optifine.OptiFineTransformationService");
         hasOfClass = true;
      } catch (Throwable var2) {
      }

      OPTIFINE_PRESENT = hasOfClass;
      MIXIN_DESC = Type.getDescriptor(Mixin.class);
      MIXIN_CLIENT_ONLY_DESC = Type.getDescriptor(ClientOnlyMixin.class);
      MIXIN_REQUIRES_MOD_DESC = Type.getDescriptor(RequiresMod.class);
      MIXIN_DEV_ONLY_DESC = Type.getDescriptor(IgnoreOutsideDev.class);
      FEATURE_LEVEL_ANNOTATION_DESC = Type.getDescriptor(RequiresFeatureLevel.class);
      PLATFORM_PREFIX = Pattern.compile("(neoforge|fabric|common)\\.");
      isFabric = ModernFixEarlyConfig.class.getClassLoader().getResourceAsStream("modernfix-fabric.mixins.json") != null;
      isDevEnv = ModernFixPlatformHooks.INSTANCE.isDevEnv();
      DEFAULT_SETTING_OVERRIDES = new ModernFixEarlyConfig.DefaultSettingMapBuilder()
         .put("mixin.perf.dynamic_resources", false)
         .put("mixin.feature.direct_stack_trace", false)
         .put("mixin.feature.stalled_chunk_load_detection", false)
         .put("mixin.bugfix.restore_old_dragon_movement", false)
         .put("mixin.feature.cause_lag_by_disabling_threads", false)
         .put("mixin.bugfix.missing_block_entities", false)
         .put("mixin.feature.blockentity_incorrect_thread", false)
         .put("mixin.perf.clear_mixin_classinfo", false)
         .put("mixin.perf.deduplicate_climate_parameters", false)
         .put("mixin.bugfix.packet_leak", false)
         .put("mixin.perf.deduplicate_location", false)
         .put("mixin.perf.dynamic_entity_renderers", false)
         .put("mixin.feature.integrated_server_watchdog", true)
         .put("mixin.perf.faster_item_rendering", false)
         .put("mixin.feature.spam_thread_dump", false)
         .put("mixin.feature.remove_chat_signing", false)
         .put("mixin.bugfix.skip_redundant_saves", false)
         .put("mixin.feature.snapshot_easter_egg", true)
         .put("mixin.feature.warn_missing_perf_mods", true)
         .put("mixin.feature.spark_profile_launch", false)
         .put("mixin.feature.spark_profile_world_join", false)
         .put("mixin.feature.log_stdout_in_log_files", true)
         .put("mixin.devenv", isDevEnv)
         .putConditionally(() -> !isFabric, "mixin.bugfix.fix_config_crashes", true)
         .putConditionally(() -> !isFabric, "mixin.feature.registry_event_progress", true)
         .build();
   }

   private static class DefaultSettingMapBuilder extends Builder<String, Boolean> {
      public ModernFixEarlyConfig.DefaultSettingMapBuilder putConditionally(BooleanSupplier condition, String k, Boolean v) {
         if (condition.getAsBoolean()) {
            this.put(k, v);
         }

         return this;
      }

      public ModernFixEarlyConfig.DefaultSettingMapBuilder put(String key, Boolean value) {
         super.put(key, value);
         return this;
      }

      // $VF: synthetic method
      // $VF: bridge method
      public Builder put(Object param1, Object nullx) {
         return this.put((String)var1, (Boolean)nullx);
      }
   }

   private static class PackageMetadata {
      String requiredModId;
      FeatureLevel requiredLevel;
   }
}
