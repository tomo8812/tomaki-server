package org.sinytra.connector;

import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.ModLoadingIssue.Severity;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.fml.loading.progress.StartupNotificationManager;
import net.neoforged.neoforgespi.language.IModInfo;
import org.sinytra.connector.util.ConnectorConfig;
import org.slf4j.Logger;

public class ConnectorEarlyLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<String> CONNECTOR_MODIDS = new HashSet<>();
   private static final List<IModInfo> CONNECTOR_MODS = new ArrayList<>();
   private static final List<Path> CONNECTOR_MOD_PATHS = new ArrayList<>();
   private static final List<ModLoadingIssue> LOADING_EXCEPTIONS = new ArrayList<>();

   public static boolean isConnectorMod(String modid) {
      return CONNECTOR_MODIDS.contains(modid);
   }

   public static boolean isConnectorModClass(Class<?> cls) {
      return cls.getModule().isNamed() && isConnectorMod(cls.getModule().getName());
   }

   public static List<IModInfo> getConnectorMods() {
      return CONNECTOR_MODS;
   }

   public static List<ModLoadingIssue> getLoadingExceptions() {
      return LOADING_EXCEPTIONS;
   }

   public static boolean hasEncounteredException() {
      return !LOADING_EXCEPTIONS.isEmpty() || LoadingModList.get() != null && LoadingModList.get().hasErrors();
   }

   public static void addGenericLoadingException(Throwable t, String message) {
      addGenericLoadingException(createGenericLoadingIssue(t, message));
   }

   public static void addGenericLoadingException(ModLoadingIssue issue) {
      addGenericLoadingException(List.of(issue));
   }

   public static void addGenericLoadingException(List<ModLoadingIssue> issues) {
      if (LoadingModList.get() != null) {
         LoadingModList.get().getModLoadingIssues().addAll(issues);
      } else {
         LOADING_EXCEPTIONS.addAll(issues);
      }
   }

   public static ModLoadingIssue createGenericLoadingIssue(Throwable original, String message) {
      return createLoadingIssue(original, "§e[Connector]§r {0}\n§c{1}§7: {2}§r", true, message, original.getClass().getName(), original.getMessage());
   }

   public static ModLoadingIssue createLoadingIssue(Throwable original, String message, boolean keepOriginal, Object... args) {
      return new ModLoadingIssue(Severity.ERROR, message, Arrays.asList(args), keepOriginal ? original : null, null, null, null);
   }

   public static void addConnectorModPath(Path path) {
      CONNECTOR_MOD_PATHS.add(path);
   }

   public static boolean isConnectorMod(Path path) {
      return CONNECTOR_MOD_PATHS.contains(path);
   }

   public static void init() {
      if (hasEncounteredException()) {
         LOGGER.error("Skipping early mod setup due to previous error");
      } else {
         LOGGER.debug("Starting early connector loader setup");
         ProgressMeter progress = StartupNotificationManager.prependProgressBar("[Connector] Early Setup", 0);

         try {
            List<String> hiddenMods = ConnectorConfig.INSTANCE.get().hiddenMods();
            List<ModInfo> mods = LoadingModList.get().getMods().stream().filter(mod -> {
               if (mod.getOwningFile().getFileProperties().containsKey("connector_transformed")) {
                  CONNECTOR_MODIDS.add(mod.getModId());
                  CONNECTOR_MODS.add(mod);
                  return true;
               } else {
                  return !hiddenMods.contains(mod.getModId());
               }
            }).toList();
            FabricLoaderImpl.INSTANCE.addFmlMods(mods);
         } catch (Throwable t) {
            LOGGER.error("Encountered error during early mod setup", t);
            addGenericLoadingException(t, "Encountered an error during early mod setup");
         }

         progress.complete();
      }
   }

   public static void setup() {
      try {
         FabricLoaderImpl.INSTANCE.setup();
      } catch (Throwable t) {
         LOGGER.error("Encountered an error during fabric loader setup", t);
         addGenericLoadingException(t, "Encountered an error during fabric loader setup");
      }
   }

   public static void preLaunch() {
      LOGGER.debug("Running prelaunch entrypoint");
      ProgressMeter progress = StartupNotificationManager.prependProgressBar("[Connector] PreLaunch", 0);

      try {
         FabricLoader.getInstance().invokeEntrypoints("preLaunch", PreLaunchEntrypoint.class, PreLaunchEntrypoint::onPreLaunch);
      } catch (Throwable t) {
         LOGGER.error("Encountered an error in prelaunch entrypoint", t);
         addGenericLoadingException(t, "Encountered an error in prelaunch entrypoint");
      }

      progress.complete();
   }
}
