package org.sinytra.connector.locator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.JarContents;
import cpw.mods.jarhandling.JarContentsBuilder;
import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import cpw.mods.modlauncher.api.IModuleLayerManager.Layer;
import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;
import net.fabricmc.loader.impl.metadata.NestedJarEntry;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LogMarkers;
import net.neoforged.fml.loading.moddiscovery.ModJarMetadata;
import net.neoforged.fml.loading.moddiscovery.locators.JarInJarDependencyLocator;
import net.neoforged.fml.loading.moddiscovery.readers.JarModsDotTomlModFileReader;
import net.neoforged.fml.loading.progress.StartupNotificationManager;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IDependencyLocator;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFile;
import net.neoforged.neoforgespi.locating.IncompatibleFileReporting;
import net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes;
import net.neoforged.neoforgespi.locating.IModFile.Type;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.Nullable;
import org.sinytra.connector.ConnectorEarlyLoader;
import org.sinytra.connector.locator.filter.ForgeModPackageFilter;
import org.sinytra.connector.locator.filter.SplitPackageMerger;
import org.sinytra.connector.locator.transform.ConnectorTransformerEnvironment;
import org.sinytra.connector.transformer.TransformerEnvironment;
import org.sinytra.connector.transformer.jar.JarTransformer;
import org.sinytra.connector.util.ConnectorUtil;
import org.sinytra.connector.util.PriorityModLoadingException;
import org.slf4j.Logger;

public class ConnectorLocator implements IDependencyLocator {
   public static final String PLACEHOLDER_PROPERTY = "connector:placeholder";
   private static final Logger LOGGER = LogUtils.getLogger();

   public int getPriority() {
      return -1000;
   }

   public void scanMods(List<IModFile> loadedMods, IDiscoveryPipeline pipeline) {
      if (ConnectorEarlyLoader.hasEncounteredException()) {
         LOGGER.error("Skipping mod scan due to previously encountered error");
      } else {
         try {
            ConnectorLocator.LocationResult results = this.locateFabricMods(loadedMods);
            if (results != null) {
               results.mods().forEach(pipeline::addModFile);
               results.originalPaths().forEach(ConnectorEarlyLoader::addConnectorModPath);
               Path generatedAdapterJar = results.generatedJarPath();
               if (Files.exists(generatedAdapterJar)) {
                  pipeline.addPath(generatedAdapterJar, ModFileDiscoveryAttributes.DEFAULT, IncompatibleFileReporting.ERROR);
               }
            }
         } catch (PriorityModLoadingException e) {
            throw e;
         } catch (ModLoadingException e) {
            ConnectorEarlyLoader.addGenericLoadingException(e.getIssues());
         } catch (Throwable t) {
            StartupNotificationManager.addModMessage("CONNECTOR LOCATOR ERROR");
            LOGGER.error("Connector locator error", t);
            ConnectorEarlyLoader.addGenericLoadingException(ConnectorEarlyLoader.createGenericLoadingIssue(t, "Fabric mod discovery failed"));
         } finally {
            ForgeModPackageFilter.filterPackages(loadedMods);

            try {
               loadEmbeddedJars(pipeline);
            } catch (Throwable t) {
               StartupNotificationManager.addModMessage("CONNECTOR EMBEDDED LOCATOR ERROR");
               LOGGER.error("Connector embedded locator error", t);
               ConnectorEarlyLoader.addGenericLoadingException(ConnectorEarlyLoader.createGenericLoadingIssue(t, "Connector embedded mod discovery failed"));
            }
         }
      }
   }

   @Nullable
   private ConnectorLocator.LocationResult locateFabricMods(List<IModFile> discoveredMods) {
      LOGGER.debug(LogMarkers.SCAN, "Scanning mods dir {} for mods", FMLPaths.MODSDIR.get());
      Path tempDir = ConnectorUtil.CONNECTOR_FOLDER.resolve("temp");
      Collection<ConnectorLocator.SimpleModInfo> loadedModInfos = getPreviouslyDiscoveredMods(discoveredMods);
      Collection<IModFile> loadedModFiles = loadedModInfos.stream().map(ConnectorLocator.SimpleModInfo::origin).toList();
      Collection<String> loadedModIds = loadedModInfos.stream()
         .filter(mod -> !mod.library())
         .map(ConnectorLocator.SimpleModInfo::modid)
         .collect(Collectors.toUnmodifiableSet());
      Collection<String> loadedModuleNames = Stream.concat(
            Launcher.INSTANCE.findLayerManager().flatMap(m -> m.getLayer(Layer.BOOT)).stream().flatMap(m -> m.modules().stream()).map(Module::getName),
            loadedModInfos.stream().filter(ConnectorLocator.SimpleModInfo::library).map(ConnectorLocator.SimpleModInfo::moduleName).filter(Objects::nonNull)
         )
         .collect(Collectors.toUnmodifiableSet());
      TransformerEnvironment environment = new ConnectorTransformerEnvironment(loadedModFiles);
      JarTransformer transformer = new JarTransformer(environment);
      List<JarTransformer.TransformableJar> discoveredJars = FabricModsDiscoverer.scanFabricMods()
         .<JarTransformer.TransformableJar>map(LambdaExceptionUtils.rethrowFunction(p -> transformer.cacheTransformableJar(p.toFile())))
         .filter(jar -> !shouldIgnoreMod(jar, loadedModIds, loadedModuleNames))
         .toList();
      Multimap<JarTransformer.TransformableJar, JarTransformer.TransformableJar> parentToChildren = HashMultimap.create();
      List<JarTransformer.TransformableJar> discoveredNestedJars = discoveredJars.stream()
         .flatMap(
            jar -> {
               LoaderModMetadata metadata = jar.modPath().metadata().modMetadata();
               return shouldIgnoreMod(jar, loadedModIds, loadedModuleNames)
                  ? Stream.empty()
                  : discoverNestedJarsRecursive(transformer, tempDir, jar, metadata.getJars(), parentToChildren, loadedModIds, loadedModuleNames);
            }
         )
         .toList();
      Collection<? super IModFile> ignoredModFiles = new ArrayList<>();
      List<JarTransformer.TransformableJar> uniqueJars = handleDuplicateMods(discoveredJars, discoveredNestedJars, loadedModInfos, ignoredModFiles);
      List<JarTransformer.TransformableJar> candidates = DependencyResolver.resolveDependencies(uniqueJars, parentToChildren, loadedModFiles);
      List<Path> renameLibs = loadedModFiles.stream().map(modFile -> modFile.getSecureJar().getRootPath()).toList();
      List<JarTransformer.TransformedFabricModPath> transformed = transformer.transform(candidates, renameLibs);
      List<JarTransformer.TransformedFabricModPath> failing = transformed.stream()
         .filter(j -> j.auditTrail() != null && j.auditTrail().hasFailingMixins())
         .toList();
      if (!failing.isEmpty()) {
         MixinTransformSafeguard.trigger(failing);
      }

      if (ConnectorEarlyLoader.hasEncounteredException()) {
         StartupNotificationManager.addModMessage("JAR TRANSFORMATION ERROR");
         LOGGER.error("Cancelling jar discovery due to previous error");
         return null;
      } else {
         List<SplitPackageMerger.FilteredModPath> moduleSafeJars = SplitPackageMerger.mergeSplitPackages(
            transformed.stream().map(JarTransformer.TransformedFabricModPath::output).toList(), loadedModFiles, ignoredModFiles
         );
         List<IModFile> loadedMods = moduleSafeJars.stream().map(ConnectorLocator::createConnectorModFile).toList();
         List<Path> originalPaths = transformed.stream().map(JarTransformer.TransformedFabricModPath::input).toList();
         return new ConnectorLocator.LocationResult(loadedMods, originalPaths, environment.getGeneratedJarPath());
      }
   }

   private static IModFile createConnectorModFile(SplitPackageMerger.FilteredModPath modPath) {
      JarContents jarContents = new JarContentsBuilder().paths(modPath.paths()).pathFilter(modPath.filter()).build();
      if (modPath.metadata().generated()) {
         return IModFile.create(SecureJar.from(jarContents), JarModsDotTomlModFileReader::manifestParser, Type.GAMELIBRARY, ModFileDiscoveryAttributes.DEFAULT);
      }

      ModJarMetadata modJarMetadata = new ModJarMetadata(jarContents);
      SecureJar secureJar = SecureJar.from(jarContents, modJarMetadata);
      IModFile modFile = IModFile.create(
         secureJar,
         f -> FabricModMetadataParser.createForgeMetadata(
            f, (ConnectorFabricModMetadata)modPath.metadata().modMetadata(), modPath.metadata().visibleMixinConfigs(), modPath.metadata().generated()
         )
      );
      modJarMetadata.setModFile(modFile);
      return modFile;
   }

   private static Stream<JarTransformer.TransformableJar> discoverNestedJarsRecursive(
      JarTransformer transformer,
      Path tempDir,
      JarTransformer.TransformableJar parent,
      Collection<NestedJarEntry> jars,
      Multimap<JarTransformer.TransformableJar, JarTransformer.TransformableJar> parentToChildren,
      Collection<String> loadedModIds,
      Collection<String> loadedModuleNames
   ) {
      SecureJar secureJar = SecureJar.from(new Path[]{parent.input().toPath()});
      return jars.stream()
         .map(entry -> secureJar.getPath(entry.getFile(), new String[0]))
         .filter(x$0 -> Files.exists(x$0))
         .flatMap(
            path -> {
               JarTransformer.TransformableJar jar = (JarTransformer.TransformableJar)LambdaExceptionUtils.uncheck(
                  () -> prepareNestedJar(transformer, tempDir, secureJar.getPrimaryPath().getFileName().toString(), path)
               );
               if (shouldIgnoreMod(jar, loadedModIds, loadedModuleNames)) {
                  return Stream.empty();
               }

               parentToChildren.put(parent, jar);
               LoaderModMetadata metadata = jar.modPath().metadata().modMetadata();
               return Stream.concat(
                  Stream.of(jar), discoverNestedJarsRecursive(transformer, tempDir, jar, metadata.getJars(), parentToChildren, loadedModIds, loadedModuleNames)
               );
            }
         );
   }

   private static JarTransformer.TransformableJar prepareNestedJar(JarTransformer transformer, Path tempDir, String parentName, Path path) throws IOException {
      Files.createDirectories(tempDir);
      String parentNameWithoutExt = parentName.split("\\.(?!.*\\.)")[0];
      Path extracted = tempDir.resolve(parentNameWithoutExt + "$" + path.getFileName().toString());
      ConnectorUtil.cache(path, extracted, () -> Files.copy(path, extracted));
      return (JarTransformer.TransformableJar)LambdaExceptionUtils.uncheck(() -> transformer.cacheTransformableJar(extracted.toFile()));
   }

   private static List<JarTransformer.TransformableJar> handleDuplicateMods(
      List<JarTransformer.TransformableJar> rootMods,
      List<JarTransformer.TransformableJar> nestedMods,
      Collection<ConnectorLocator.SimpleModInfo> loadedMods,
      Collection<? super IModFile> ignoredModFiles
   ) {
      return Stream.concat(rootMods.stream(), nestedMods.stream())
         .filter(
            jar -> {
               String id = jar.modPath().metadata().modMetadata().getId();
               List<ConnectorLocator.SimpleModInfo> forgeMods = loadedMods.stream().filter(mod -> mod.modid().equals(id)).toList();
               if (forgeMods.stream().anyMatch(ConnectorLocator.SimpleModInfo::library)) {
                  ArtifactVersion artifactVersion = new DefaultArtifactVersion(jar.modPath().metadata().modMetadata().getVersion().getFriendlyString());
                  ConnectorLocator.SimpleModInfo fabricModInfo = new ConnectorLocator.SimpleModInfo(id, artifactVersion, false, null, null);
                  List<ConnectorLocator.SimpleModInfo> modsByVersion = Stream.concat(Stream.of(fabricModInfo), forgeMods.stream())
                     .sorted(Comparator.comparing(ConnectorLocator.SimpleModInfo::version).reversed())
                     .toList();
                  if (modsByVersion.getFirst() == fabricModInfo) {
                     modsByVersion.subList(1, modsByVersion.size()).forEach(mod -> {
                        IModFile modFile = Objects.requireNonNull(mod.origin(), "Missing mod origin for mod " + mod.modid());
                        ignoredModFiles.add(modFile);
                     });
                     return true;
                  }
               }

               if (loadedMods.stream().anyMatch(mod -> mod.modid().equals(id))) {
                  LOGGER.info(LogMarkers.SCAN, "Removing duplicate mod {} in file {}", id, jar.modPath().path().toAbsolutePath());
                  return false;
               } else {
                  return true;
               }
            }
         )
         .toList();
   }

   private static boolean shouldIgnoreMod(JarTransformer.TransformableJar jar, Collection<String> loadedModIds, Collection<String> loadedModuleNames) {
      LoaderModMetadata metadata = jar.modPath().metadata().modMetadata();
      String id = metadata.getId();
      return ConnectorUtil.DISABLED_MODS.contains(id)
         || loadedModIds.contains(id)
         || jar.modPath().metadata().generated() && loadedModuleNames.contains(jar.moduleName());
   }

   private static Collection<ConnectorLocator.SimpleModInfo> getPreviouslyDiscoveredMods(List<IModFile> discoveredMods) {
      return discoveredMods.stream()
         .flatMap(modFile -> Optional.ofNullable(modFile.getModFileInfo()).stream())
         .flatMap(
            modFileInfo -> {
               IModFile modFile = modFileInfo.getFile();
               List<IModInfo> modInfos = modFileInfo.getMods();
               if (modFileInfo.getFileProperties().containsKey("connector:placeholder")) {
                  modInfos.forEach(mod -> mod.getVersion().parseVersion("0.0"));
                  return Stream.empty();
               }

               if (!modInfos.isEmpty()) {
                  return modInfos.stream()
                     .map(modInfo -> new ConnectorLocator.SimpleModInfo(modInfo.getModId(), modInfo.getVersion(), false, modFile, modFileInfo.moduleName()));
               }

               String version = modFileInfo.getFile().getSecureJar().moduleDataProvider().descriptor().version().map(Version::toString).orElse("0.0");
               return Stream.of(
                  new ConnectorLocator.SimpleModInfo(modFileInfo.moduleName(), new DefaultArtifactVersion(version), true, modFile, modFileInfo.moduleName())
               );
            }
         )
         .toList();
   }

   private static void loadEmbeddedJars(IDiscoveryPipeline pipeline) throws Exception {
      SecureJar secureJar = SecureJar.from(new Path[]{Path.of(ConnectorLocator.class.getProtectionDomain().getCodeSource().getLocation().toURI())});
      IModFile modFile = IModFile.create(secureJar, JarModsDotTomlModFileReader::manifestParser);
      new JarInJarDependencyLocator().scanMods(List.of(modFile), pipeline);
   }

   private record LocationResult(List<IModFile> mods, List<Path> originalPaths, Path generatedJarPath) {
   }

   private record SimpleModInfo(String modid, ArtifactVersion version, boolean library, @Nullable IModFile origin, @Nullable String moduleName) {
   }
}
