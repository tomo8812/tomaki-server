package org.sinytra.connector.locator;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import java.lang.module.ModuleDescriptor.Version;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.FMLModMetadata;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.discovery.BuiltinMetadataWrapper;
import net.fabricmc.loader.impl.discovery.ModCandidateImpl;
import net.fabricmc.loader.impl.discovery.ModResolutionException;
import net.fabricmc.loader.impl.discovery.ModResolver;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.metadata.DependencyOverrides;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;
import net.fabricmc.loader.impl.metadata.ModDependencyImpl;
import net.fabricmc.loader.impl.metadata.VersionOverrides;
import net.fabricmc.loader.impl.util.version.VersionParser;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.Nullable;
import org.sinytra.connector.ConnectorEarlyLoader;
import org.sinytra.connector.transformer.jar.JarTransformer;
import org.sinytra.connector.util.ConnectorConfig;
import org.slf4j.Logger;

public final class DependencyResolver {
   private static final String MIXINEXTRAS_MODID = "mixinextras";
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final VersionOverrides VERSION_OVERRIDES = new VersionOverrides();
   public static final Supplier<DependencyOverrides> DEPENDENCY_OVERRIDES = Suppliers.memoize(
      () -> loadConfigFile("fabric_loader_dependencies.json", () -> new DependencyOverrides(FMLPaths.CONFIGDIR.get()))
   );

   public static List<JarTransformer.TransformableJar> resolveDependencies(
      Collection<JarTransformer.TransformableJar> keys,
      Multimap<JarTransformer.TransformableJar, JarTransformer.TransformableJar> jars,
      Collection<IModFile> loadedMods
   ) {
      FabricLoaderImpl.INSTANCE.aliasMods(ConnectorConfig.INSTANCE.get().globalModAliases());
      BiMap<JarTransformer.TransformableJar, ModCandidateImpl> jarToCandidate = HashBiMap.create();
      List<ModCandidateImpl> candidates = createCandidatesRecursive(keys, keys, jars, jarToCandidate);
      Stream<ModCandidateImpl> forgeCandidates = loadedMods.stream()
         .flatMap(modFile -> modFile.getModFileInfo() != null ? modFile.getModInfos().stream() : Stream.empty())
         .map(
            modInfo -> ModCandidateImpl.createPlain(
               List.of(modInfo.getOwningFile().getFile().getFilePath()), new BuiltinMetadataWrapper(new FMLModMetadata(modInfo)), false, List.of()
            )
         );
      Stream<ModCandidateImpl> builtinCandidates = Stream.of(createJavaMod(), createFabricLoaderMod(), createMixinExtrasMod(loadedMods))
         .filter(Objects::nonNull);
      List<ModCandidateImpl> allCandidates = Stream.of(candidates.stream(), forgeCandidates, builtinCandidates).flatMap(Function.identity()).toList();
      EnvType envType = FabricLoader.getInstance().getEnvironmentType();

      try {
         List<ModCandidateImpl> resolved = ModResolver.resolve(allCandidates, envType, Map.of());
         List<JarTransformer.TransformableJar> candidateJars = resolved.stream()
            .map(jarToCandidate.inverse()::get)
            .filter(Objects::nonNull)
            .filter(jar -> jar.modPath().metadata().modMetadata().loadsInEnvironment(envType))
            .toList();
         LOGGER.info("Dependency resolution found {} candidates to load", candidateJars.size());
         return candidateJars;
      } catch (ModResolutionException e) {
         throw new ModLoadingException(ConnectorEarlyLoader.createLoadingIssue(e, e.getMessage().replaceAll("\t", "  "), false));
      }
   }

   public static void removeAliasedModDependencyConstraints(LoaderModMetadata metadata) {
      Multimap<String, String> aliases = ConnectorConfig.INSTANCE.get().globalModAliases();
      Collection<ModDependency> mapped = metadata.getDependencies()
         .stream()
         .map(
            dep -> {
               if (aliases.keys().contains(dep.getModId()) || aliases.values().contains(dep.getModId())) {
                  return dep.getKind() == ModDependency.Kind.BREAKS
                     ? null
                     : (ModDependency)LambdaExceptionUtils.uncheck(() -> new ModDependencyImpl(dep.getKind(), dep.getModId(), List.of("*")));
               } else {
                  return (ModDependency)(dep.getModId().equals("fabricloader") ? null : dep);
               }
            }
         )
         .filter(Objects::nonNull)
         .toList();
      metadata.setDependencies(mapped);
   }

   private static List<ModCandidateImpl> createCandidatesRecursive(
      Collection<JarTransformer.TransformableJar> candidateJars,
      Collection<JarTransformer.TransformableJar> jarsToLoad,
      Multimap<JarTransformer.TransformableJar, JarTransformer.TransformableJar> parentsToChildren,
      Map<JarTransformer.TransformableJar, ModCandidateImpl> jarToCandidate
   ) {
      List<ModCandidateImpl> candidates = new ArrayList<>();

      for (JarTransformer.TransformableJar candidateJar : candidateJars) {
         if (jarsToLoad.contains(candidateJar)) {
            ModCandidateImpl candidate = jarToCandidate.computeIfAbsent(
               candidateJar,
               j -> {
                  Collection<JarTransformer.TransformableJar> children = parentsToChildren.containsKey(candidateJar)
                     ? parentsToChildren.get(candidateJar)
                     : List.of();
                  List<ModCandidateImpl> childCandidates = createCandidatesRecursive(children, jarsToLoad, parentsToChildren, jarToCandidate);
                  List<Path> paths = parentsToChildren.containsValue(candidateJar) ? null : List.of(candidateJar.modPath().path());
                  ModCandidateImpl parent = ModCandidateImpl.createPlain(paths, candidateJar.modPath().metadata().modMetadata(), false, childCandidates);

                  for (ModCandidateImpl childCandidate : childCandidates) {
                     childCandidate.addParent(parent);
                  }

                  return parent;
               }
            );
            candidates.add(candidate);
         }
      }

      return candidates;
   }

   private static ModCandidateImpl createJavaMod() {
      ModMetadata metadata = new BuiltinModMetadata.Builder("java", System.getProperty("java.specification.version").replaceFirst("^1\\.", ""))
         .setName(System.getProperty("java.vm.name"))
         .build();
      GameProvider.BuiltinMod builtinMod = new GameProvider.BuiltinMod(Collections.singletonList(Paths.get(System.getProperty("java.home"))), metadata);
      return ModCandidateImpl.createBuiltin(builtinMod, VERSION_OVERRIDES, DEPENDENCY_OVERRIDES.get());
   }

   private static ModCandidateImpl createFabricLoaderMod() {
      String version = EmbeddedDependencies.getFabricLoaderVersion();
      if (version == null) {
         version = "0.0NONE";
      } else {
         String[] components = version.split("\\.");
         version = components[0] + "." + components[1] + ".*";
      }

      ModMetadata metadata = new BuiltinModMetadata.Builder("fabricloader", parseVersionUnchecked(version)).setName("Fabric Loader").build();
      GameProvider.BuiltinMod builtinMod = new GameProvider.BuiltinMod(
         Collections.singletonList(
            Path.of((URI)LambdaExceptionUtils.uncheck(() -> FabricLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()))
         ),
         metadata
      );
      return ModCandidateImpl.createBuiltin(builtinMod, VERSION_OVERRIDES, DEPENDENCY_OVERRIDES.get());
   }

   @Nullable
   private static ModCandidateImpl createMixinExtrasMod(Collection<IModFile> loadedMods) {
      SecureJar mixinExJar = loadedMods.stream().<SecureJar>map(IModFile::getSecureJar).filter(j -> j.name().contains("mixinextras")).findFirst().orElse(null);
      if (mixinExJar == null) {
         return null;
      }

      String version = mixinExJar.moduleDataProvider().descriptor().version().map(Version::toString).orElse(null);
      ModMetadata metadata = new BuiltinModMetadata.Builder("mixinextras", parseVersionUnchecked(version)).setName(mixinExJar.name()).build();
      GameProvider.BuiltinMod builtinMod = new GameProvider.BuiltinMod(List.of(mixinExJar.getPrimaryPath()), metadata);
      return ModCandidateImpl.createBuiltin(builtinMod, VERSION_OVERRIDES, DEPENDENCY_OVERRIDES.get());
   }

   private static <T> T loadConfigFile(String name, Supplier<T> supplier) {
      try {
         return supplier.get();
      } catch (Throwable t) {
         throw new ModLoadingException(ConnectorEarlyLoader.createGenericLoadingIssue(t, "Invalid config file " + name));
      }
   }

   private static net.fabricmc.loader.api.Version parseVersionUnchecked(String str) {
      try {
         return VersionParser.parse(str, true);
      } catch (VersionParsingException e) {
         throw new RuntimeException(e);
      }
   }
}
