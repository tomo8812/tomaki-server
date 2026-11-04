package org.sinytra.connector.locator.filter;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.JarContents;
import cpw.mods.jarhandling.JarContentsBuilder;
import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.IModuleLayerManager.Layer;
import cpw.mods.niofs.union.UnionPathFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.Nullable;
import org.sinytra.connector.transformer.jar.FabricModFileMetadata;
import org.sinytra.connector.transformer.jar.JarTransformer;
import org.slf4j.Logger;

public class SplitPackageMerger {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final UnionPathFilter BASE_FILTER = (path, basePath) -> !path.startsWith("META-INF/versions");

   public static List<SplitPackageMerger.FilteredModPath> mergeSplitPackages(
      List<JarTransformer.FabricModPath> paths, Iterable<IModFile> existing, Collection<? super IModFile> ignoredModFiles
   ) {
      List<JarTransformer.FabricModPath> plainPaths = new ArrayList<>(paths);
      List<SplitPackageMerger.FilteredModPath> output = new ArrayList<>();
      Map<String, List<Pair<SecureJar, JarTransformer.FabricModPath>>> pkgSources = new HashMap<>();

      for (JarTransformer.FabricModPath modInfo : paths) {
         SecureJar secureJar = SecureJar.from(new Path[]{modInfo.path()});

         for (String pkg : secureJar.moduleDataProvider().descriptor().packages()) {
            pkgSources.computeIfAbsent(pkg, p -> new ArrayList<>()).add(Pair.of(secureJar, modInfo));
         }
      }

      List<SecureJar> jarOrder = new ArrayList<>();
      Map<String, List<Pair<SecureJar, JarTransformer.FabricModPath>>> mergePkgs = new LinkedHashMap<>();
      AtomicInteger totalJars = new AtomicInteger(0);
      pkgSources.forEach(
         (pkg, sources) -> {
            if (sources.size() > 1) {
               LOGGER.debug(
                  "Found split package {} in jars {}",
                  pkg,
                  sources.stream().map(infox -> ((SecureJar)infox.getFirst()).name()).collect(Collectors.joining(","))
               );
               sources.forEach(source -> {
                  if (plainPaths.remove(source.getSecond())) {
                     totalJars.getAndIncrement();
                  }

                  mergePkgs.computeIfAbsent(pkg, p -> new ArrayList<>()).add((Pair<SecureJar, JarTransformer.FabricModPath>)source);
                  jarOrder.add((SecureJar)source.getFirst());
               });
            }
         }
      );
      LOGGER.debug("Found {} split packages across {} jars", mergePkgs.keySet().size(), totalJars.get());
      Map<String, SplitPackageMerger.JarMergeInfo> jarMap = new HashMap<>();
      mergePkgs.forEach(
         (pkg, sources) -> {
            sources.sort(Comparator.comparingInt(p -> jarOrder.indexOf(p.getFirst())));
            Pair<SecureJar, JarTransformer.FabricModPath> pairx = sources.get(0);
            SecureJar candidate = (SecureJar)pairx.getFirst();
            String name = candidate.name();
            SplitPackageMerger.JarMergeInfo owner = jarMap.computeIfAbsent(
               name, str -> new SplitPackageMerger.JarMergeInfo(candidate, ((JarTransformer.FabricModPath)pairx.getSecond()).metadata())
            );
            analyzeJar(jarMap, owner, sources.subList(1, sources.size()), pkg);
         }
      );
      Set<String> existingPackages = new HashSet<>();

      for (IModFile modFile : existing) {
         if (!ignoredModFiles.contains(modFile)) {
            existingPackages.addAll(modFile.getSecureJar().moduleDataProvider().descriptor().packages());
         }
      }

      Collection<Layer> layers = Set.of(Layer.BOOT, Layer.SERVICE);
      IModuleLayerManager manager = (IModuleLayerManager)Launcher.INSTANCE.findLayerManager().orElseThrow();

      for (Layer layer : layers) {
         ((ModuleLayer)manager.getLayer(layer).orElseThrow())
            .modules()
            .stream()
            .flatMap(module -> module.getPackages().stream())
            .forEach(existingPackages::add);
      }

      for (String pkg : existingPackages) {
         List<Pair<SecureJar, JarTransformer.FabricModPath>> list = pkgSources.get(pkg);
         if (list != null) {
            for (Pair<SecureJar, JarTransformer.FabricModPath> pair : list) {
               SecureJar jar = (SecureJar)pair.getFirst();
               JarTransformer.FabricModPath modPath = (JarTransformer.FabricModPath)pair.getSecond();
               plainPaths.remove(modPath);
               SplitPackageMerger.JarMergeInfo info = jarMap.computeIfAbsent(jar.name(), str -> new SplitPackageMerger.JarMergeInfo(jar, modPath.metadata()));
               LOGGER.debug("Excluding existing package {} from jar {}", pkg, jar.name());
               info.excludedPackages().add(pkg);
            }
         }
      }

      jarMap.values().forEach(infox -> {
         Set<Path> additionalPaths = infox.additionalPaths();
         Set<String> excludedPackages = infox.excludedPackages();
         UnionPathFilter filter = !excludedPackages.isEmpty() ? new PackageTracker(Set.copyOf(excludedPackages)) : null;
         Path[] jarPaths = Stream.concat(Stream.of(infox.jar().getPrimaryPath()), additionalPaths.stream()).toArray(Path[]::new);
         output.add(new SplitPackageMerger.FilteredModPath(jarPaths, mergeANDFilter(BASE_FILTER, filter), infox.metadata));
      });

      for (JarTransformer.FabricModPath modPath : plainPaths) {
         output.add(new SplitPackageMerger.FilteredModPath(new Path[]{modPath.path()}, BASE_FILTER, modPath.metadata()));
      }

      if (paths.size() != output.size()) {
         LOGGER.error("Expected {} paths, got {}", paths.size(), plainPaths.size());
         throw new IllegalStateException("Path size disprenancy detected!");
      } else {
         return output;
      }
   }

   private static void analyzeJar(
      Map<String, SplitPackageMerger.JarMergeInfo> swap,
      SplitPackageMerger.JarMergeInfo master,
      List<Pair<SecureJar, JarTransformer.FabricModPath>> others,
      String pkg
   ) {
      List<Path> additionalPaths = others.stream().flatMap(pair -> {
         SecureJar sj = (SecureJar)pair.getFirst();
         FabricModFileMetadata metadata = ((JarTransformer.FabricModPath)pair.getSecond()).metadata();
         JarContents jarContents = new JarContentsBuilder().paths(new Path[]{sj.getPrimaryPath()}).pathFilter(singlePackageFilter(pkg)).build();
         SecureJar singlePackage = SecureJar.from(jarContents);
         SplitPackageMerger.JarMergeInfo jarInfo = swap.computeIfAbsent(sj.name(), name -> new SplitPackageMerger.JarMergeInfo(sj, metadata));
         jarInfo.excludedPackages().add(pkg);
         return Stream.of(singlePackage.getRootPath());
      }).toList();
      master.additionalPaths().addAll(additionalPaths);
   }

   private static UnionPathFilter singlePackageFilter(String pkg) {
      return (path, basePath) -> {
         int idx = path.lastIndexOf(47);
         return path.equals("/") || idx == path.length() - 1 || idx > -1 && pkg.equals(path.substring(0, idx).replace('/', '.'));
      };
   }

   private static UnionPathFilter mergeANDFilter(UnionPathFilter left, @Nullable UnionPathFilter right) {
      return (a, b) -> left.test(a, b) && (right == null || right.test(a, b));
   }

   public record FilteredModPath(Path[] paths, @Nullable UnionPathFilter filter, FabricModFileMetadata metadata) {
   }

   private record JarMergeInfo(SecureJar jar, FabricModFileMetadata metadata, Set<Path> additionalPaths, Set<String> excludedPackages) {
      public JarMergeInfo(SecureJar jar, FabricModFileMetadata metadata) {
         this(jar, metadata, new HashSet<>(), new HashSet<>());
      }
   }
}
