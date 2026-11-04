package org.embeddedt.modernfix.resources;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.resources.IoSupplier;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.util.PackTypeHelper;
import org.jetbrains.annotations.Nullable;

public class PackResourcesCacheEngine {
   private static final Joiner SLASH_JOINER = Joiner.on('/');
   private static final ConcurrentHashMap<String, String> PATH_COMPONENT_INTERNER = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<String, String[]> CACHED_SPLIT_PATHS = new ConcurrentHashMap<>();
   private static final PackResourcesCacheEngine.Node EMPTY = new PackResourcesCacheEngine.Node();
   private final PackResourcesCacheEngine.Node root = new PackResourcesCacheEngine.Node();
   private final Map<PackType, Path> rootPathsByType = new Object2ObjectOpenHashMap();
   private volatile boolean cacheGenerationFlag = false;
   private List<Runnable> cacheGenerationTasks = new ArrayList<>();
   private Path debugPath;

   public PackResourcesCacheEngine(Function<PackType, Path> basePathRetriever) {
      this.debugPath = basePathRetriever.apply(PackType.CLIENT_RESOURCES).toAbsolutePath();
      this.root.children = new Object2ObjectOpenHashMap();

      for (PackType type : PackType.values()) {
         PackResourcesCacheEngine.Node typeRoot = new PackResourcesCacheEngine.Node();
         this.root.children.put(type.getDirectory(), typeRoot);
         Path root = basePathRetriever.apply(type);
         this.rootPathsByType.put(type, root);
         this.cacheGenerationTasks.add(() -> {
            try (Stream<Path> stream = Files.find(root, Integer.MAX_VALUE, (p, a) -> a.isRegularFile())) {
               stream.<Path>map(path -> root.relativize(path.toAbsolutePath())).filter(PackResourcesCacheEngine::isValidCachedResourcePath).forEach(path -> {
                  PackResourcesCacheEngine.Node node = typeRoot;
                  int nameCount = path.getNameCount();

                  for (int i = 0; i < nameCount; i++) {
                     String key = path.getName(i).toString();
                     if (i < nameCount - 1) {
                        key = PATH_COMPONENT_INTERNER.computeIfAbsent(key, Function.identity());
                     }

                     if (node.children == null) {
                        node.children = new Object2ObjectOpenHashMap();
                     }

                     node = node.children.computeIfAbsent(key, $ -> new PackResourcesCacheEngine.Node());
                  }
               });
            } catch (IOException var7x) {
            }
         });
      }

      this.cacheGenerationTasks.add(this.root::optimize);
   }

   private static boolean isValidCachedResourcePath(Path path) {
      if (path.getFileName() != null && path.getNameCount() != 0) {
         String str = SLASH_JOINER.join(path);
         if (str.length() == 0) {
            return false;
         }

         for (int i = 0; i < str.length(); i++) {
            if (!ResourceLocation.validPathChar(str.charAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Set<String> getNamespaces(PackType type) {
      this.awaitLoad();
      if (PackTypeHelper.isVanillaPackType(type)) {
         Map<String, PackResourcesCacheEngine.Node> namespaceToNodeMap = this.root.getChild(type.getDirectory()).children;
         ObjectOpenHashSet<String> results = new ObjectOpenHashSet();

         for (Entry<String, PackResourcesCacheEngine.Node> entry : namespaceToNodeMap.entrySet()) {
            if (!entry.getValue().children.isEmpty()) {
               results.add(entry.getKey());
            }
         }

         return results;
      } else {
         return null;
      }
   }

   private void doGenerateCache() {
      Stopwatch watch = Stopwatch.createStarted();

      for (Runnable r : this.cacheGenerationTasks) {
         r.run();
      }

      watch.stop();
      ModernFix.LOGGER.debug("Generated cache for {} in {}", this.debugPath, watch);
      this.debugPath = null;
      this.cacheGenerationTasks = ImmutableList.of();
   }

   private void awaitLoad() {
      if (!this.cacheGenerationFlag) {
         synchronized (this) {
            if (!this.cacheGenerationFlag) {
               this.doGenerateCache();
               this.cacheGenerationFlag = true;
            }
         }
      }
   }

   public boolean hasResource(String[] paths) {
      this.awaitLoad();
      PackResourcesCacheEngine.Node node = this.root;

      for (String path : paths) {
         if (!path.isEmpty()) {
            node = node.children.get(path);
            if (node == null) {
               return false;
            }
         }
      }

      return true;
   }

   public void collectResources(PackType type, String resourceNamespace, String[] components, int maxDepth, ResourceOutput output) {
      if (!PackTypeHelper.isVanillaPackType(type)) {
         throw new IllegalArgumentException("Only vanilla PackTypes are supported");
      }

      this.awaitLoad();
      PackResourcesCacheEngine.Node node = this.root.getChild(type.getDirectory());
      if (node != null) {
         node = node.getChild(resourceNamespace);
         if (node != null) {
            node.collectResources(resourceNamespace, this.rootPathsByType.get(type).resolve(resourceNamespace), components, 0, maxDepth, output);
         }
      }
   }

   private static String[] decompose(String path) {
      String[] components = path.split("/");

      for (int i = 0; i < components.length; i++) {
         components[i] = PATH_COMPONENT_INTERNER.computeIfAbsent(components[i], Function.identity());
      }

      return components;
   }

   public static String[] decomposeCached(String path) {
      return CACHED_SPLIT_PATHS.computeIfAbsent(path, PackResourcesCacheEngine::decompose);
   }

   static class Node {
      Map<String, PackResourcesCacheEngine.Node> children;

      void optimize() {
         if (this.children != null) {
            for (Entry<String, PackResourcesCacheEngine.Node> entry : this.children.entrySet()) {
               PackResourcesCacheEngine.Node oldNode = entry.getValue();
               oldNode.optimize();
               if (oldNode.children == null) {
                  entry.setValue(PackResourcesCacheEngine.EMPTY);
               }
            }

            this.children = Map.copyOf(this.children);
         } else {
            this.children = Map.of();
         }
      }

      void collectResources(String namespace, Path baseNioPath, String[] pathComponents, int curIndex, int maxDepth, ResourceOutput output) {
         if (curIndex <= maxDepth) {
            if (curIndex >= pathComponents.length) {
               this.outputResources(namespace, baseNioPath, String.join("/", pathComponents), output);
            } else {
               while (true) {
                  String component = pathComponents[curIndex];
                  if (!component.isEmpty()) {
                     PackResourcesCacheEngine.Node n = this.getChild(component);
                     if (n != null) {
                        n.collectResources(namespace, baseNioPath, pathComponents, curIndex + 1, maxDepth, output);
                     }
                     break;
                  }

                  curIndex++;
                  maxDepth++;
               }
            }
         }
      }

      void outputResources(String namespace, Path baseNioPath, String path, ResourceOutput output) {
         if (this.children.isEmpty()) {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, path);
            output.accept(location, (IoSupplier)() -> Files.newInputStream(baseNioPath.resolve(path)));
         } else {
            for (Entry<String, PackResourcesCacheEngine.Node> entry : this.children.entrySet()) {
               entry.getValue().outputResources(namespace, baseNioPath, path + "/" + entry.getKey(), output);
            }
         }
      }

      @Nullable
      PackResourcesCacheEngine.Node getChild(String name) {
         return this.children.get(name);
      }
   }
}
