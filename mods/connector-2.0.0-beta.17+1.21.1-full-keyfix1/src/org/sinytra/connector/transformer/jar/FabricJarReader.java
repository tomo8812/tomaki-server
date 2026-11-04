package org.sinytra.connector.transformer.jar;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.impl.metadata.DependencyOverrides;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;
import net.fabricmc.loader.impl.metadata.ModMetadataParser;
import net.fabricmc.loader.impl.metadata.ParseMetadataException;
import net.fabricmc.loader.impl.metadata.VersionOverrides;
import org.sinytra.connector.transformer.TransformerEnvironment;
import org.slf4j.Logger;

public class FabricJarReader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String LOOM_GENERATED_PROPERTY = "fabric-loom:generated";
   private static final String LOOM_REMAP_ATTRIBUTE = "Fabric-Loom-Remap";

   public static FabricModFileMetadata readModMetadata(File input, TransformerEnvironment environment) throws IOException {
      try (JarFile jarFile = new JarFile(input)) {
         LoaderModMetadata metadata;
         Set<String> allConfigs;
         Set<String> configs;
         try (InputStream ins = jarFile.getInputStream(jarFile.getEntry("fabric.mod.json"))) {
            VersionOverrides versionOverrides = environment.getVersionOverrides();
            DependencyOverrides dependencyOverrides = environment.getDependencyOverrides().get();
            LoaderModMetadata rawMetadata = ModMetadataParser.parseMetadata(ins, "", Collections.emptyList(), versionOverrides, dependencyOverrides, false);
            metadata = environment.wrapModMetadata(rawMetadata);
            Map<EnvType, Collection<String>> envMixinConfigs = Map.of(
               EnvType.CLIENT, metadata.getMixinConfigs(EnvType.CLIENT), EnvType.SERVER, metadata.getMixinConfigs(EnvType.SERVER)
            );
            allConfigs = envMixinConfigs.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            configs = new HashSet<>(envMixinConfigs.get(environment.getEnvType()));
         } catch (ParseMetadataException e) {
            throw new RuntimeException(e);
         }

         boolean containsAT = jarFile.getEntry("META-INF/accesstransformer.cfg") != null;
         Set<String> refmaps = new HashSet<>();
         Set<String> mixinPackages = new HashSet<>();
         Set<String> mixinClasses = new HashSet<>();

         for (String configName : configs) {
            ZipEntry entry = jarFile.getEntry(configName);
            if (entry != null) {
               readMixinConfigPackages(input, jarFile, entry, refmaps, mixinPackages, mixinClasses);
            }
         }

         jarFile.stream().forEach(entryx -> {
            String name = entryx.getName();
            if (!allConfigs.contains(name)) {
               if ((name.endsWith(".mixins.json") || name.startsWith("mixins.") && name.endsWith(".json")) && configs.add(name)) {
                  readMixinConfigPackages(input, jarFile, entryx, refmaps, mixinPackages, mixinClasses);
               }
            }
         });
         Attributes manifestAttributes = Optional.ofNullable(jarFile.getManifest()).map(Manifest::getMainAttributes).orElseGet(Attributes::new);
         boolean generated = isGeneratedLibraryJarMetadata(manifestAttributes, metadata);
         return new FabricModFileMetadata(
            metadata, Set.copyOf(configs), configs, refmaps, mixinPackages, mixinClasses, manifestAttributes, containsAT, generated
         );
      }
   }

   private static void readMixinConfigPackages(File input, JarFile jarFile, ZipEntry entry, Set<String> refmaps, Set<String> packages, Set<String> mixinClasses) {
      try (Reader reader = new InputStreamReader(jarFile.getInputStream(entry))) {
         JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
         if (json.has("refmap")) {
            String refmap = json.get("refmap").getAsString();
            refmaps.add(refmap);
         }

         if (json.has("package")) {
            String pkg = json.get("package").getAsString();
            if (!pkg.isEmpty()) {
               String pkgPath = pkg.replace('.', '/') + "/";
               packages.add(pkgPath);
            }

            for (String type : List.of("mixins", "client", "server")) {
               if (json.has(type)) {
                  for (JsonElement mixin : json.getAsJsonArray(type)) {
                     if (mixin.isJsonPrimitive()) {
                        String className = pkg + "." + mixin.getAsString();
                        mixinClasses.add(className.replace('.', '/'));
                     }
                  }
               }
            }
         }
      } catch (Throwable t) {
         LOGGER.error("Error reading mixin config entry {} in file {}", entry.getName(), input.getAbsolutePath());
         throw new RuntimeException(t);
      }
   }

   private static boolean isGeneratedLibraryJarMetadata(Attributes manifestAttributes, LoaderModMetadata metadata) {
      CustomValue generatedValue = metadata.getCustomValue("fabric-loom:generated");
      if (generatedValue != null && generatedValue.getType() == CustomValue.CvType.BOOLEAN && generatedValue.getAsBoolean()) {
         String loomRemapAttribute = manifestAttributes.getValue("Fabric-Loom-Remap");
         return loomRemapAttribute == null || !loomRemapAttribute.equals("true");
      } else {
         return false;
      }
   }
}
