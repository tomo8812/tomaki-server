package org.codeberg.zenxarch.fastnoise;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

public class FastNoiseConfig {
   private static final String configFileName = "zfastnoise.mixin.properties";
   private static final String overridesKey = "zfastnoise:overrides";
   private static final String[] keys = new String[]{"mixin.perf.noise", "mixin.perf.biome"};
   private static final Object2BooleanMap<String> defaults = new Object2BooleanArrayMap(Map.of(keys[0], true, keys[1], true));

   private static Path getConfigPath() {
      Path configPath = FMLPaths.CONFIGDIR.get().resolve("zfastnoise.mixin.properties");
      if (!Files.exists(configPath)) {
         try {
            Files.createFile(configPath);
         } catch (Exception e) {
            FastNoiseConstants.LOGGER.error("Cannot create file " + configPath.toString(), e);
            return null;
         }
      }

      if (!Files.isRegularFile(configPath)) {
         FastNoiseConstants.LOGGER.error("Config file " + configPath.toString() + " is not a file");
         return null;
      } else {
         return configPath;
      }
   }

   private static Properties loadProperties() {
      Properties result = new Properties();
      Path configPath = getConfigPath();
      if (configPath == null) {
         return result;
      }

      try {
         result.load(Files.newInputStream(configPath));
         return result;
      } catch (Exception e) {
         FastNoiseConstants.LOGGER.error("Cannot read file " + configPath.toString(), e);
         return result;
      }
   }

   private static void saveProperties(Properties props) {
      Path configPath = getConfigPath();
      if (configPath != null) {
         try {
            props.store(Files.newOutputStream(configPath), "");
         } catch (Exception e) {
            FastNoiseConstants.LOGGER.error("Cannot write file " + configPath.toString(), e);
         }
      }
   }

   private static void collectOverrides(Object2BooleanArrayMap<String> map, ModInfo info, String key, boolean value) {
      if (!map.containsKey(key)) {
         FastNoiseConstants.LOGGER.error("Mod {} tried to override unknown key {}", info.getModId(), key);
      } else {
         FastNoiseConstants.LOGGER.info("Mod {} override key {} with value {}", new Object[]{info.getModId(), key, value});
         map.put(key, value);
      }
   }

   private static void collectOverrides(Object2BooleanArrayMap<String> map, ModInfo info, List<?> array) {
      for (Object value : array) {
         if (value instanceof String string) {
            collectOverrides(map, info, string, false);
         } else {
            FastNoiseConstants.LOGGER.error("Mod {} has array of not strings as overrides", info.getModId());
         }
      }
   }

   private static void collectOverrides(Object2BooleanArrayMap<String> map, ModInfo info, Map<?, ?> object) {
      for (Object value : object.keySet()) {
         if (value instanceof String key) {
            if (object.get(value) instanceof Boolean bl) {
               collectOverrides(map, info, key, bl);
            } else {
               FastNoiseConstants.LOGGER.error("Mod {} has object of not booleans as overrides", info.getModId());
            }
         }
      }
   }

   private static void collectOverrides(Object2BooleanArrayMap<String> map) {
      for (ModInfo container : FMLLoader.getLoadingModList().getMods()) {
         Optional<Object> meta = container.getConfigElement(new String[]{"zfastnoise:overrides"});
         if (!meta.isEmpty()) {
            Object value = meta.get();
            switch (value) {
               case Map<?, ?> otherMap:
                  collectOverrides(map, container, otherMap);
                  break;
               case List<?> list:
                  collectOverrides(map, container, list);
                  break;
               case String string:
                  collectOverrides(map, container, string, false);
                  break;
               default:
                  FastNoiseConstants.LOGGER.error("Mod {} has unsupported overrides of type {}", container.getModId(), value.getClass().getSimpleName());
            }
         }
      }
   }

   public static Object2BooleanMap<String> loadConfig() {
      Properties props = loadProperties();
      Object2BooleanArrayMap<String> result = new Object2BooleanArrayMap();

      for (String key : keys) {
         boolean r = defaults.getOrDefault(key, true);

         try {
            r = Boolean.parseBoolean(props.getProperty(key, Boolean.toString(r)));
         } catch (Exception var8) {
         }

         props.setProperty(key, Boolean.toString(r));
         result.put(key, r);
      }

      saveProperties(props);
      collectOverrides(result);
      return Object2BooleanMaps.unmodifiable(result);
   }
}
