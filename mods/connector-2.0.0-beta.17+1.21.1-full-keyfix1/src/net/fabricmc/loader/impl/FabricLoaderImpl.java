package net.fabricmc.loader.impl;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.modlauncher.ArgumentHandler;
import cpw.mods.modlauncher.Launcher;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.ObjectShare;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.impl.entrypoint.EntrypointStorage;
import net.fabricmc.loader.impl.metadata.EntrypointMetadata;
import net.fabricmc.loader.impl.util.DefaultLanguageAdapter;
import net.fabricmc.loader.impl.util.ExceptionUtil;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;

public final class FabricLoaderImpl extends FabricLoader {
   public static final FabricLoaderImpl INSTANCE = FabricLoaderImpl.InitHelper.get();
   private final Map<String, ModContainerImpl> modMap = new HashMap<>();
   private final List<ModContainerImpl> mods = new ArrayList<>();
   private final Multimap<String, String> modAliases = HashMultimap.create();
   private final Map<String, Supplier<LanguageAdapter>> adapterMap = new HashMap<>();
   private final EntrypointStorage entrypointStorage = new EntrypointStorage();
   private final ObjectShare objectShare = new ObjectShareImpl();
   private volatile MappingResolverImpl mappingResolver;
   private String[] launchArgs;
   private boolean loadedFMLMods;

   private FabricLoaderImpl() {
   }

   @Override
   public String getRawGameVersion() {
      return FMLLoader.versionInfo().mcVersion();
   }

   @Override
   public Object getGameInstance() {
      throw new UnsupportedOperationException();
   }

   @Override
   public EnvType getEnvironmentType() {
      return FMLEnvironment.dist == Dist.CLIENT ? EnvType.CLIENT : EnvType.SERVER;
   }

   @Override
   public Path getGameDir() {
      return FMLPaths.GAMEDIR.get().toAbsolutePath().normalize();
   }

   @Deprecated
   @Override
   public File getGameDirectory() {
      return this.getGameDir().toFile();
   }

   @Override
   public Path getConfigDir() {
      return FMLPaths.CONFIGDIR.get();
   }

   @Deprecated
   @Override
   public File getConfigDirectory() {
      return this.getConfigDir().toFile();
   }

   @Override
   public <T> List<T> getEntrypoints(String key, Class<T> type) {
      return this.entrypointStorage.getEntrypoints(key, type);
   }

   @Override
   public <T> List<EntrypointContainer<T>> getEntrypointContainers(String key, Class<T> type) {
      return this.entrypointStorage.getEntrypointContainers(key, type);
   }

   @Override
   public <T> void invokeEntrypoints(String key, Class<T> type, Consumer<? super T> invoker) {
      if (!this.hasEntrypoints(key)) {
         Log.debug(LogCategory.ENTRYPOINT, "No subscribers for entrypoint '%s'", key);
      } else {
         RuntimeException exception = null;
         Collection<EntrypointContainer<T>> entrypoints = INSTANCE.getEntrypointContainers(key, type);
         Log.debug(LogCategory.ENTRYPOINT, "Iterating over entrypoint '%s'", key);

         for (EntrypointContainer<T> container : entrypoints) {
            try {
               invoker.accept(container.getEntrypoint());
            } catch (Throwable t) {
               exception = ExceptionUtil.gatherExceptions(
                  t,
                  exception,
                  exc -> new RuntimeException(
                     String.format(
                        "Could not execute entrypoint stage '%s' due to errors, provided by '%s' at '%s'!",
                        key,
                        container.getProvider().getMetadata().getId(),
                        container.getDefinition()
                     ),
                     exc
                  )
               );
            }
         }

         if (exception != null) {
            throw exception;
         }
      }
   }

   public MappingResolverImpl getMappingResolver() {
      if (this.mappingResolver == null) {
         synchronized (this) {
            if (this.mappingResolver == null) {
               this.mappingResolver = new MappingResolverImpl();
            }
         }
      }

      return this.mappingResolver;
   }

   @Override
   public ObjectShare getObjectShare() {
      return this.objectShare;
   }

   @Override
   public Optional<ModContainer> getModContainer(String id) {
      return Optional.ofNullable(this.modMap.get(id));
   }

   @Override
   public Collection<ModContainer> getAllMods() {
      return Collections.unmodifiableList(this.mods);
   }

   @Override
   public boolean isModLoaded(String id) {
      return this.modMap.containsKey(id);
   }

   @Override
   public boolean isDevelopmentEnvironment() {
      return !FMLEnvironment.production;
   }

   @Override
   public String[] getLaunchArguments(boolean sanitize) {
      if (this.launchArgs == null) {
         try {
            Field argumentHandlerField = Launcher.class.getDeclaredField("argumentHandler");
            argumentHandlerField.setAccessible(true);
            ArgumentHandler handler = (ArgumentHandler)argumentHandlerField.get(Launcher.INSTANCE);
            this.launchArgs = handler.buildArgumentList();
         } catch (Throwable t) {
            throw new RuntimeException(t);
         }
      }

      return this.launchArgs;
   }

   public boolean hasEntrypoints(String key) {
      return this.entrypointStorage.hasEntrypoints(key);
   }

   public Collection<Object> getModInstances(String modid) {
      return this.entrypointStorage.getModInstances().get(modid);
   }

   public void addFmlMods(List<? extends IModInfo> fmlMods) {
      if (!this.loadedFMLMods) {
         for (IModInfo mod : fmlMods) {
            ModContainerImpl container = new ModContainerImpl(mod);
            if (this.modMap.put(mod.getModId(), container) != null) {
               throw new IllegalStateException("Duplicate fml mod with metadata: " + mod.getModId());
            }

            this.mods.add(container);

            for (String provides : container.getMetadata().getProvides()) {
               this.modMap.putIfAbsent(provides, container);
            }
         }

         this.loadedFMLMods = true;
      }
   }

   public void aliasMods(Multimap<String, String> aliases) {
      this.modAliases.putAll(aliases);
   }

   public Collection<String> getModAliases(String modid) {
      return this.modAliases.get(modid);
   }

   public void setup() {
      this.setupLanguageAdapters();
      this.setupMods();
   }

   private void setupLanguageAdapters() {
      this.adapterMap.put("default", () -> DefaultLanguageAdapter.INSTANCE);

      for (ModContainerImpl mod : this.mods) {
         for (Entry<String, String> laEntry : mod.getInfo().getLanguageAdapterDefinitions().entrySet()) {
            if (this.adapterMap.containsKey(laEntry.getKey())) {
               throw new RuntimeException(
                  "Duplicate language adapter key: "
                     + laEntry.getKey()
                     + "! ("
                     + laEntry.getValue()
                     + ", "
                     + this.adapterMap.get(laEntry.getKey()).getClass().getName()
                     + ")"
               );
            }

            Supplier<LanguageAdapter> supplier = Suppliers.memoize(
               () -> {
                  try {
                     return (LanguageAdapter)Class.forName(laEntry.getValue(), true, Thread.currentThread().getContextClassLoader())
                        .getDeclaredConstructor()
                        .newInstance();
                  } catch (Exception e) {
                     throw new RuntimeException("Failed to instantiate language adapter: " + laEntry.getKey(), e);
                  }
               }
            );
            this.adapterMap.put(laEntry.getKey(), supplier);
         }
      }
   }

   private void setupMods() {
      for (ModContainerImpl mod : this.mods) {
         try {
            for (String in : mod.getInfo().getOldInitializers()) {
               String adapter = mod.getInfo().getOldStyleLanguageAdapter();
               this.entrypointStorage.addDeprecated(mod, adapter, in);
            }

            for (String key : mod.getInfo().getEntrypointKeys()) {
               for (EntrypointMetadata in : mod.getInfo().getEntrypoints(key)) {
                  this.entrypointStorage.add(mod, key, in, this.adapterMap);
               }
            }
         } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to setup mod %s (%s)", mod.getInfo().getName(), mod.getOrigin()), e);
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public MappingResolver getMappingResolver() {
      return this.getMappingResolver();
   }

   public static class InitHelper {
      private static FabricLoaderImpl instance;

      public static FabricLoaderImpl get() {
         if (instance == null) {
            instance = new FabricLoaderImpl();
         }

         return instance;
      }
   }
}
