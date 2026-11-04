package de.maxhenkel.gravestone.corelib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class DynamicConfig {
   protected CommentedFileConfig config;
   private ObjectConverter converter = new ObjectConverter();

   public void init(Path configFile, Path defaultConfigFile) {
      boolean createDefaults = !configFile.toFile().exists() && !defaultConfigFile.toFile().exists();
      GenericBuilder<CommentedConfig, CommentedFileConfig> builder = CommentedFileConfig.builder(configFile)
         .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
         .autosave();
      if (defaultConfigFile != null && defaultConfigFile.toFile().exists()) {
         builder.defaultData(defaultConfigFile);
      }

      this.config = (CommentedFileConfig)builder.build();
      this.config.load();
      if (createDefaults) {
         this.setDefaults();
      }

      this.onLoad();
   }

   public void init(Path configFile) {
      this.init(configFile, null);
   }

   protected void setDefaults() {
   }

   protected void onLoad() {
   }

   public <T> T get(String path, T defaultValue) {
      this.checkLoaded();
      return (T)this.config.getOrElse(path, defaultValue);
   }

   public List<String> getSubValues(String path) {
      this.checkLoaded();
      Config cfg;
      if (path != null && !path.isEmpty()) {
         cfg = (Config)this.config.getOrElse(path, null);
      } else {
         cfg = this.config;
      }

      return cfg != null ? cfg.entrySet().stream().<String>map(Entry::getKey).collect(Collectors.toList()) : Collections.emptyList();
   }

   public List<String> getSubValues() {
      this.checkLoaded();
      return this.getSubValues(null);
   }

   @Nullable
   private Config getSubConfig(String path) {
      this.checkLoaded();
      return (Config)this.config.getOrElse(path, null);
   }

   public <T> T getObject(String path, Supplier<T> object, Supplier<T> defaultValue) {
      this.checkLoaded();
      Config subConfig = this.getSubConfig(path);
      return (T)(subConfig == null ? defaultValue.get() : this.converter.toObject(subConfig, object));
   }

   @Nullable
   public <T> T getObject(String path, Supplier<T> object) {
      return this.getObject(path, object, () -> null);
   }

   public <T> T setObject(String path, T object) {
      this.checkLoaded();
      Config config = this.converter.toConfig(object, Config::inMemoryUniversal);
      this.set(path, config);
      return object;
   }

   public <T> T set(String path, T value) {
      this.checkLoaded();
      return (T)this.config.set(path, value);
   }

   private void checkLoaded() {
      if (!this.isLoaded()) {
         throw new IllegalStateException("Config not loaded");
      }
   }

   public boolean isLoaded() {
      return this.config != null;
   }

   public enum DynamicConfigType {
      SERVER,
      COMMON;

      // $VF: synthetic method
      private static DynamicConfig.DynamicConfigType[] $values() {
         return new DynamicConfig.DynamicConfigType[]{SERVER, COMMON};
      }
   }
}
