package org.sinytra.connector.service;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import cpw.mods.modlauncher.api.IModuleLayerManager.Layer;
import cpw.mods.modlauncher.api.ITransformationService.Resource;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.neoforged.fml.loading.ImmediateWindowHandler;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforgespi.earlywindow.ImmediateWindowProvider;
import org.sinytra.connector.ConnectorEarlyLoader;
import org.sinytra.connector.service.hacks.ConnectorForkJoinThreadFactory;
import org.sinytra.connector.service.hacks.FabricASMFixer;
import org.sinytra.connector.service.hacks.LenientRuntimeEnumExtender;
import org.sinytra.connector.service.hacks.ModuleLayerMigrator;
import org.sinytra.connector.util.ConnectorUtil;
import org.slf4j.Logger;

public class ConnectorLoaderService implements ITransformationService {
   private static final String NAME = "connector_loader";
   private static final String AUTHLIB_MODULE = "authlib";
   private static final String BRIGADIER_MODULE = "brigadier";
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final VarHandle PLUGINS = (VarHandle)LambdaExceptionUtils.uncheck(
      () -> ConnectorUtil.TRUSTED_LOOKUP.findVarHandle(LaunchPluginHandler.class, "plugins", Map.class)
   );

   public String name() {
      return "connector_loader";
   }

   public void initialize(IEnvironment environment) {
      VarHandle provider = (VarHandle)LambdaExceptionUtils.uncheck(
         () -> ConnectorUtil.TRUSTED_LOOKUP.findStaticVarHandle(ImmediateWindowHandler.class, "provider", ImmediateWindowProvider.class)
      );
      final ImmediateWindowProvider original = (ImmediateWindowProvider)provider.get();
      ImmediateWindowProvider newProvider = new ImmediateWindowProvider() {
         public void updateModuleReads(ModuleLayer layer) {
            if (!ConnectorEarlyLoader.hasEncounteredException()) {
               ConnectorEarlyLoader.setup();
               LambdaExceptionUtils.uncheck(() -> Class.forName("org.sinytra.connector.mod.DummyTarget", false, Thread.currentThread().getContextClassLoader()));
               ConnectorEarlyLoader.preLaunch();
            }

            original.updateModuleReads(layer);
         }

         public String name() {
            return original.name();
         }

         public Runnable initialize(String[] arguments) {
            return original.initialize(arguments);
         }

         public void updateFramebufferSize(IntConsumer width, IntConsumer height) {
            original.updateFramebufferSize(width, height);
         }

         public long setupMinecraftWindow(IntSupplier width, IntSupplier height, Supplier<String> title, LongSupplier monitor) {
            return original.setupMinecraftWindow(width, height, title, monitor);
         }

         public boolean positionWindow(Optional<Object> monitor, IntConsumer widthSetter, IntConsumer heightSetter, IntConsumer xSetter, IntConsumer ySetter) {
            return original.positionWindow(monitor, widthSetter, heightSetter, xSetter, ySetter);
         }

         public <T> Supplier<T> loadingOverlay(Supplier<?> mc, Supplier<?> ri, Consumer<Optional<Throwable>> ex, boolean fade) {
            return original.loadingOverlay(mc, ri, ex, fade);
         }

         public void periodicTick() {
            original.periodicTick();
         }

         public String getGLVersion() {
            return original.getGLVersion();
         }

         public void crash(String message) {
            original.crash(message);
         }
      };
      provider.set((ImmediateWindowProvider)newProvider);
      ConnectorForkJoinThreadFactory.install();
   }

   public void onLoad(IEnvironment env, Set<String> otherServices) {
      List<ILaunchPluginService> injectPlugins = List.of(new ConnectorPreLaunchPlugin());

      try {
         Field launchPluginsField = Launcher.class.getDeclaredField("launchPlugins");
         launchPluginsField.setAccessible(true);
         LaunchPluginHandler launchPluginHandler = (LaunchPluginHandler)launchPluginsField.get(Launcher.INSTANCE);
         Map<String, ILaunchPluginService> plugins = (Map)PLUGINS.get((LaunchPluginHandler)launchPluginHandler);
         LinkedHashMap<String, ILaunchPluginService> sortedPlugins = new LinkedHashMap<>();
         sortedPlugins.put("mixin", plugins.remove("mixin"));
         plugins.remove("runtime_enum_extender");
         sortedPlugins.put("runtime_enum_extender", new LenientRuntimeEnumExtender());
         injectPlugins.forEach(plugin -> sortedPlugins.put(plugin.name(), plugin));
         sortedPlugins.putAll(plugins);
         PLUGINS.set((LaunchPluginHandler)launchPluginHandler, (LinkedHashMap)sortedPlugins);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public List<Resource> completeScan(IModuleLayerManager layerManager) {
      LoadingModList.get().getModLoadingIssues().removeIf(issue -> {
         Path path = issue.affectedPath();
         return ConnectorEarlyLoader.isConnectorMod(path) && issue.translationKey().startsWith("fml.modloadingissue.brokenfile.");
      });
      LoadingModList.get().getModLoadingIssues().addAll(ConnectorEarlyLoader.getLoadingExceptions());
      layerManager.getLayer(Layer.PLUGIN).map(ModuleLayer::modules).ifPresent(modules -> {
         LOGGER.debug("Making PLUGIN modules read connector");
         ModuleLayerMigrator.addReads((Set<Module>)modules);
      });
      return List.of(
         new Resource(
            Layer.GAME,
            Stream.of(FabricASMFixer.provideGeneratedClassesJar(), ModuleLayerMigrator.moveModule("authlib"), ModuleLayerMigrator.moveModule("brigadier"))
               .filter(Objects::nonNull)
               .toList()
         )
      );
   }

   public List<? extends ITransformer<?>> transformers() {
      return List.of();
   }
}
