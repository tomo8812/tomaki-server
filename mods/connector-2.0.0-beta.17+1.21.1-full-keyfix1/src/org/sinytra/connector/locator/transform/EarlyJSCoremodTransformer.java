package org.sinytra.connector.locator.transform;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.ClassTransformer;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformationServiceDecorator;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.loading.ImmediateWindowHandler;
import net.neoforged.fml.loading.LogMarkers;
import net.neoforged.fml.loading.moddiscovery.CoreModFile;
import net.neoforged.fml.loading.moddiscovery.ModFile;
import net.neoforged.fml.util.ServiceLoaderUtil;
import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.coremod.ICoreMod;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.NotNull;
import org.sinytra.connector.locator.ConnectorEarlyLocatorBootstrap;
import org.sinytra.connector.transformer.transform.TransformerUtil;
import org.slf4j.Logger;
import reloc.net.minecraftforge.fart.api.ClassProvider;

public class EarlyJSCoremodTransformer implements ClassProvider {
   private static final Class<?> COREMOD_SCRIPT_LOADER = (Class<?>)LambdaExceptionUtils.uncheck(
      () -> Class.forName("net.neoforged.fml.loading.CoreModScriptLoader")
   );
   private static final MethodHandle LOAD_COREMOD_SCRIPTS = (MethodHandle)LambdaExceptionUtils.uncheck(
      () -> MethodHandles.privateLookupIn(COREMOD_SCRIPT_LOADER, MethodHandles.lookup())
         .findStatic(COREMOD_SCRIPT_LOADER, "loadCoreModScripts", MethodType.methodType(List.class, List.class))
   );
   private static final MethodHandle TRANSFORM = (MethodHandle)LambdaExceptionUtils.uncheck(
      () -> MethodHandles.privateLookupIn(ClassTransformer.class, MethodHandles.lookup())
         .findVirtual(ClassTransformer.class, "transform", MethodType.methodType(byte[].class, byte[].class, String.class, String.class))
   );
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ClassProvider provider;
   private final ClassTransformer transformer;

   public static EarlyJSCoremodTransformer create(ClassProvider classProvider, Collection<IModFile> loadedMods) {
      try {
         List<? extends ITransformer<?>> transformers = transformers(loadedMods);
         TransformStore transformStore = new TransformStore();
         ITransformationService service = new EarlyJSCoremodTransformer.DummyService(transformers);
         Constructor<TransformationServiceDecorator> cst = TransformationServiceDecorator.class.getDeclaredConstructor(ITransformationService.class);
         cst.setAccessible(true);
         TransformationServiceDecorator decorator = cst.newInstance(service);
         decorator.gatherTransformers(transformStore);
         LaunchPluginHandler plugins = TransformerUtil.allocateInstance(LaunchPluginHandler.class);
         Field pluginsField = LaunchPluginHandler.class.getDeclaredField("plugins");
         pluginsField.setAccessible(true);
         pluginsField.set(plugins, new HashMap());
         Constructor<ClassTransformer> xformCst = ClassTransformer.class
            .getDeclaredConstructor(TransformStore.class, LaunchPluginHandler.class, TransformingClassLoader.class);
         xformCst.setAccessible(true);
         ClassTransformer classTransformer = xformCst.newInstance(transformStore, plugins, null);
         return new EarlyJSCoremodTransformer(classProvider, classTransformer);
      } catch (Throwable t) {
         throw new RuntimeException(t);
      }
   }

   public EarlyJSCoremodTransformer(ClassProvider provider, ClassTransformer transformer) {
      this.provider = provider;
      this.transformer = transformer;
   }

   @Override
   public Optional<? extends ClassProvider.IClassInfo> getClass(String s) {
      return this.provider.getClass(s);
   }

   @Override
   public Optional<byte[]> getClassBytes(String s) {
      return this.provider.getClassBytes(s).map(bytes -> {
         try {
            return (byte[])TRANSFORM.invoke((ClassTransformer)this.transformer, (byte[])bytes, (String)s, (String)"computing_frames");
         } catch (Throwable t) {
            LOGGER.error("Error transforming class {}", s, t);
            return (byte[])bytes;
         }
      });
   }

   @Override
   public void close() throws IOException {
      this.provider.close();
   }

   private static List<? extends ITransformer<?>> transformers(Collection<IModFile> modFiles) {
      LOGGER.debug(LogMarkers.LOADING, "Loading coremod transformers");
      ArrayList<ITransformer<?>> result = new ArrayList<>(loadCoreModScripts(modFiles));
      ILaunchContext launchContext = ConnectorEarlyLocatorBootstrap.getLaunchContext();

      for (ICoreMod coreMod : ServiceLoaderUtil.loadServices(launchContext, ICoreMod.class)) {
         String sourceFile = ServiceLoaderUtil.identifySourcePath(launchContext, coreMod);

         try {
            for (ITransformer<?> transformer : coreMod.getTransformers()) {
               LOGGER.debug(LogMarkers.CORE, "Adding {} transformer from core-mod {} in {}", new Object[]{transformer.targets(), coreMod, sourceFile});
               result.add(transformer);
            }
         } catch (Exception e) {
            ModLoader.addLoadingIssue(
               ModLoadingIssue.error("fml.modloadingissue.coremod_error", new Object[]{coreMod.getClass().getName(), sourceFile}).withCause(e)
            );
         }
      }

      return result;
   }

   private static List<ITransformer<?>> loadCoreModScripts(Collection<IModFile> modFiles) {
      List<IModFile> filesWithCoreModScripts = modFiles.stream().filter(mf -> {
         List<CoreModFile> coremods = ((ModFile)mf).getCoreMods();
         return coremods != null && !coremods.isEmpty();
      }).toList();
      if (filesWithCoreModScripts.isEmpty()) {
         LOGGER.debug(LogMarkers.CORE, "Not loading coremod script-engine since no mod requested it");
         return List.of();
      }

      LOGGER.info(LogMarkers.CORE, "Loading coremod script-engine for {}", filesWithCoreModScripts);

      try {
         return (List)LOAD_COREMOD_SCRIPTS.invoke((List)filesWithCoreModScripts);
      } catch (Throwable e) {
         String message = "Could not find the coremod script-engine, but the following mods require it: " + filesWithCoreModScripts;
         ImmediateWindowHandler.crash(message);
         throw new IllegalStateException(message, e);
      }
   }

   private record DummyService(List transformers) implements ITransformationService {
      @NotNull
      public String name() {
         return "connector_early_js_coremods";
      }

      public void initialize(IEnvironment environment) {
      }

      public void onLoad(IEnvironment env, Set<String> otherServices) {
      }
   }
}
