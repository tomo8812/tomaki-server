package net.caffeinemc.mods.lithium.common.config;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import net.caffeinemc.mods.lithium.common.services.PlatformMixinOverrides;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LithiumConfig {
   private static final Logger LOGGER = LogManager.getLogger("LithiumConfig");
   private static final String JSON_KEY_LITHIUM_OPTIONS = "lithium:options";
   private final Map<String, Option> options = new HashMap<>();
   private final Set<Option> optionsWithDependencies = new ObjectLinkedOpenHashSet();

   private LithiumConfig() {
      InputStream defaultPropertiesStream = LithiumConfig.class.getResourceAsStream("/assets/lithium/lithium-mixin-config-default.properties");
      if (defaultPropertiesStream == null) {
         throw new IllegalStateException("Lithium mixin config default properties could not be read!");
      }

      try (BufferedReader propertiesReader = new BufferedReader(new InputStreamReader(defaultPropertiesStream))) {
         Properties properties = new Properties();
         properties.load(propertiesReader);
         properties.forEach((ruleName, enabled) -> this.addMixinRule((String)ruleName, Boolean.parseBoolean((String)enabled)));
      } catch (IOException e) {
         e.printStackTrace();
         throw new IllegalStateException("Lithium mixin config default properties could not be read!");
      }

      InputStream dependenciesStream = LithiumConfig.class.getResourceAsStream("/assets/lithium/lithium-mixin-config-dependencies.properties");
      if (dependenciesStream == null) {
         throw new IllegalStateException("Lithium mixin config dependencies could not be read!");
      }

      try (BufferedReader propertiesReader = new BufferedReader(new InputStreamReader(dependenciesStream))) {
         Properties properties = new Properties();
         properties.load(propertiesReader);
         properties.forEach((o1, o2) -> {
            String rulename = (String)o1;
            String dependencies = (String)o2;
            String[] dependenciesSplit = dependencies.split(",");

            for (String dependency : dependenciesSplit) {
               String[] split = dependency.split(":");
               if (split.length != 2) {
                  return;
               }

               String dependencyName = split[0];
               String requiredState = split[1];
               this.addRuleDependency(rulename, dependencyName, Boolean.parseBoolean(requiredState));
            }
         });
      } catch (IOException e) {
         e.printStackTrace();
         throw new IllegalStateException("Lithium mixin config dependencies could not be read!");
      }
   }

   public static LithiumConfig load(File file) {
      LithiumConfig config = new LithiumConfig();
      if (file.exists()) {
         Properties props = new Properties();

         try (FileInputStream fin = new FileInputStream(file)) {
            props.load(fin);
         } catch (IOException e) {
            throw new RuntimeException("Could not load config file", e);
         }

         config.readProperties(props);
      } else {
         try {
            writeDefaultConfig(file);
         } catch (IOException e) {
            LOGGER.warn("Could not write default configuration file", e);
         }
      }

      PlatformMixinOverrides.getInstance().applyModOverrides().forEach(config::applyModOverride);
      PlatformMixinOverrides.getInstance().applyLithiumCompat(config.options);
      config.applyDependencies();
      return config;
   }

   private void addRuleDependency(String rule, String dependency, boolean requiredValue) {
      Option option = this.options.get(rule);
      if (option == null) {
         LOGGER.error("Option {} for dependency '{} depends on {}={}' not found. Skipping.", rule, rule, dependency, requiredValue);
      } else {
         Option dependencyOption = this.options.get(dependency);
         if (dependencyOption == null) {
            LOGGER.error("Option {} for dependency '{} depends on {}={}' not found. Skipping.", dependency, rule, dependency, requiredValue);
         } else {
            option.addDependency(dependencyOption, requiredValue);
            this.optionsWithDependencies.add(option);
         }
      }
   }

   private void addMixinRule(String mixin, boolean enabled) {
      if (this.options.putIfAbsent(mixin, new Option(mixin, enabled, false)) != null) {
         throw new IllegalStateException("Mixin rule already defined: " + mixin);
      }
   }

   private void readProperties(Properties props) {
      for (Entry<Object, Object> entry : props.entrySet()) {
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         Option option = this.options.get(key);
         if (option == null) {
            LOGGER.warn("No configuration key exists with name '{}', ignoring", key);
         } else {
            boolean enabled;
            if (value.equalsIgnoreCase("true")) {
               enabled = true;
            } else {
               if (!value.equalsIgnoreCase("false")) {
                  LOGGER.warn("Invalid value '{}' encountered for configuration key '{}', ignoring", value, key);
                  continue;
               }

               enabled = false;
            }

            option.setEnabled(enabled, true);
         }
      }
   }

   protected void applyModOverride(PlatformMixinOverrides.MixinOverride override) {
      Option option = this.options.get(override.option());
      if (option == null && !override.option().startsWith("mixin.")) {
         option = this.options.get("mixin." + override.option());
      }

      if (option == null) {
         LOGGER.warn("Mod '{}' attempted to override option '{}', which doesn't exist, ignoring", override.modId(), override.option());
      } else {
         if (!override.enabled() && option.isEnabled()) {
            option.clearModsDefiningValue();
         }

         if (!override.enabled() || option.isEnabled() || option.getDefiningMods().isEmpty()) {
            option.addModOverride(override.enabled(), override.modId());
         }
      }
   }

   public Option getEffectiveOptionForMixin(String mixinClassName) {
      int lastSplit = 0;
      Option rule = null;

      int nextSplit;
      while ((nextSplit = mixinClassName.indexOf(46, lastSplit)) != -1) {
         String key = getMixinRuleName(mixinClassName.substring(0, nextSplit));
         Option candidate = this.options.get(key);
         if (candidate != null) {
            rule = candidate;
            if (!rule.isEnabled()) {
               return rule;
            }
         }

         lastSplit = nextSplit + 1;
      }

      return rule;
   }

   private void applyDependencies() {
      while (this.applyDependenciesOnce()) {
      }
   }

   private boolean applyDependenciesOnce() {
      boolean changed = false;

      for (Option optionWithDependency : this.optionsWithDependencies) {
         changed |= optionWithDependency.disableIfDependenciesNotMet(LOGGER, this);
      }

      return changed;
   }

   private static void writeDefaultConfig(File file) throws IOException {
      File dir = file.getParentFile();
      if (!dir.exists()) {
         if (!dir.mkdirs()) {
            throw new IOException("Could not create parent directories");
         }
      } else if (!dir.isDirectory()) {
         throw new IOException("The parent file is not a directory");
      }

      try (Writer writer = new FileWriter(file)) {
         writer.write("# This is the configuration file for Lithium.\n");
         writer.write("#\n");
         writer.write("# You can find information on editing this file and all the available options here:\n");
         writer.write("# https://github.com/CaffeineMC/lithium-fabric/wiki/Configuration-File\n");
         writer.write("#\n");
         writer.write("# By default, this file will be empty except for this notice.\n");
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

   public Option getParent(Option option) {
      String optionName = option.getName();
      int split;
      if ((split = optionName.lastIndexOf(46)) != -1) {
         String key = optionName.substring(0, split);
         return this.options.get(key);
      } else {
         return null;
      }
   }
}
