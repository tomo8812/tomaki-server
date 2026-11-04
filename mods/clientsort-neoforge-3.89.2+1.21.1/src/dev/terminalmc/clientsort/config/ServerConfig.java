package dev.terminalmc.clientsort.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.platform.services.PlatformServices;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerConfig {
   private static final Path CONFIG_DIR = PlatformServices.getInstance().getConfigDir();
   public static final String FILE_NAME = "clientsort-server.json";
   private static final String BACKUP_FILE_NAME = "clientsort-server.unreadable.json";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   public final ServerConfig.Options options = new ServerConfig.Options();
   private static ServerConfig instance = null;

   public static ServerConfig.Options serverOptions() {
      return get().options;
   }

   private void validate() {
      this.options.classPolicies = ServerConfig.Options.classPoliciesValidator.validate(this.options.classPolicies);
   }

   private void upgradeLegacy() {
      if (this.options.validateOperationResults != null) {
         this.options.validateOperationResults = null;
         this.options.classPolicies = ServerConfig.Options.classPoliciesDefault.get();
      }
   }

   public static ServerConfig get() {
      if (instance == null) {
         instance = load();
      }

      return instance;
   }

   public static ServerConfig getAndSave() {
      get();
      save();
      return instance;
   }

   public static ServerConfig reloadAndSave() {
      instance = load();
      save();
      return instance;
   }

   public static ServerConfig resetAndSave() {
      instance = new ServerConfig();
      save();
      return instance;
   }

   @NotNull
   public static ServerConfig load() {
      Path file = CONFIG_DIR.resolve("clientsort-server.json");
      ServerConfig serverConfig = null;
      if (Files.exists(file)) {
         serverConfig = load(file, GSON);
         if (serverConfig == null) {
            backup();
            ClientSort.LOG.warn("Resetting config");
         } else {
            serverConfig.upgradeLegacy();
         }
      }

      return serverConfig != null ? serverConfig : new ServerConfig();
   }

   @Nullable
   private static ServerConfig load(Path file, Gson gson) {
      try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8)) {
         return (ServerConfig)gson.fromJson(reader, ServerConfig.class);
      } catch (Exception e) {
         ClientSort.LOG.error("Unable to load config", e);
         return null;
      }
   }

   private static void backup() {
      try {
         ClientSort.LOG.warn("Copying {} to {}", "clientsort-server.json", "clientsort-server.unreadable.json");
         if (!Files.isDirectory(CONFIG_DIR)) {
            Files.createDirectories(CONFIG_DIR);
         }

         Path file = CONFIG_DIR.resolve("clientsort-server.json");
         Path backupFile = file.resolveSibling("clientsort-server.unreadable.json");
         Files.move(file, backupFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
         ClientSort.LOG.error("Unable to copy config file", e);
      }
   }

   public static void save() {
      if (instance != null) {
         instance.validate();

         try {
            if (!Files.isDirectory(CONFIG_DIR)) {
               Files.createDirectories(CONFIG_DIR);
            }

            Path file = CONFIG_DIR.resolve("clientsort-server.json");
            Path tempFile = file.resolveSibling(file.getFileName() + ".tmp");

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile.toFile()), StandardCharsets.UTF_8)) {
               writer.write(GSON.toJson(instance));
            } catch (IOException e) {
               throw new IOException(e);
            }

            Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            ClientSort.onConfigSaved(instance);
         } catch (IOException e) {
            ClientSort.LOG.error("Unable to save config", e);
         }
      }
   }

   public static class Options {
      @Nullable
      public Boolean validateOperationResults;
      @Nullable
      public Boolean validationActiveSingleplayer;
      public static final boolean validationActiveServerDefault = false;
      public boolean validationActiveServer = false;
      public static final boolean validateItemTypeDefault = true;
      public boolean validateItemType = true;
      public static final boolean validateStackSizeDefault = true;
      public boolean validateStackSize = true;
      public static final int validateStackSizeThresholdDefault = 32;
      public int validateStackSizeThreshold = 32;
      public static final boolean alwaysLogUnexpectedResultsDefault = true;
      public boolean alwaysLogUnexpectedResults = true;
      public static final Supplier<List<ServerClassPolicy>> classPoliciesDefaultList = () -> new ArrayList<>(
         List.of(
            new ServerClassPolicy("com.simibubi.create.content.equipment.toolbox.ToolboxMenu", false, false, false),
            new ServerClassPolicy("com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu", false, false, false),
            new ServerClassPolicy("com.tom.storagemod.menu.CraftingTerminalMenu", false, false, false),
            new ServerClassPolicy("com.tom.storagemod.menu.StorageTerminalMenu", false, false, false)
         )
      );
      public static final Supplier<Map<String, ServerClassPolicy>> classPoliciesDefault = () -> {
         Map<String, ServerClassPolicy> map = new LinkedHashMap<>();
         classPoliciesDefaultList.get().forEach(item -> map.put(item.className, item));
         return map;
      };
      public Map<String, ServerClassPolicy> classPolicies = classPoliciesDefault.get();
      public static ServerConfig.Validator<Map<String, ServerClassPolicy>> classPoliciesValidator = val -> {
         Map<String, ServerClassPolicy> validPolicies = new LinkedHashMap<>();
         if (val == null) {
            return validPolicies;
         }

         val.values()
            .forEach(
               cp -> {
                  if (cp != null && cp.className != null && !cp.className.isBlank()) {
                     String className = cp.className;
                     validPolicies.put(
                        className,
                        new ServerClassPolicy(className, cp.sortEnabled, cp.stackFillEnabled, cp.transferEnabled, cp.lastAutoEditTime, cp.lastAutoEditReason)
                     );
                  }
               }
            );
         Map<String, ServerClassPolicy> sortedPolicies = new LinkedHashMap<>();
         validPolicies.keySet().stream().sorted().forEach(k -> sortedPolicies.put(k, validPolicies.get(k)));
         return sortedPolicies;
      };
   }

   @FunctionalInterface
   public interface Validator<T> {
      @NotNull
      T validate(@Nullable T var1);
   }
}
