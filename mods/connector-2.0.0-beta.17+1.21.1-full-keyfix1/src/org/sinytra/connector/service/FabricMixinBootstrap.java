package org.sinytra.connector.service;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.version.VersionInterval;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.fml.loading.moddiscovery.ModFileParser.MixinConfig;
import net.neoforged.neoforgespi.language.IModInfo;
import org.sinytra.connector.ConnectorEarlyLoader;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.transformer.Config;

public final class FabricMixinBootstrap {
   private static final Logger LOGGER = LogUtils.getLogger();

   private FabricMixinBootstrap() {
   }

   public static void init() {
      Map<String, ModFileInfo> configToModMap = new HashMap<>();

      for (ModFileInfo modFile : LoadingModList.get().getModFiles()) {
         List<MixinConfig> mixinConfigs = modFile.getFile().getMixinConfigs();
         if (mixinConfigs != null) {
            for (MixinConfig config : mixinConfigs) {
               if (Files.exists(modFile.getFile().findResource(new String[]{config.config()}))) {
                  ModFileInfo prev = configToModMap.putIfAbsent(config.config(), modFile);
                  if (prev != null) {
                     LOGGER.debug(
                        "Non-unique Mixin config name {} used by the mods {} and {}", new Object[]{config.config(), prev.moduleName(), modFile.moduleName()}
                     );
                  }
               }
            }
         }
      }

      try {
         IMixinConfig.class.getMethod("decorate", String.class, Object.class);
         FabricMixinBootstrap.MixinConfigDecorator.apply(configToModMap);
      } catch (NoSuchMethodException e) {
         LOGGER.info("Detected old Mixin version without config decoration support");
      }
   }

   public static final class MixinConfigDecorator {
      private static final List<FabricMixinBootstrap.MixinConfigDecorator.LoaderMixinVersionEntry> VERSIONS = List.of(
         FabricMixinBootstrap.MixinConfigDecorator.LoaderMixinVersionEntry.create("0.12.0-", 10000)
      );

      static void apply(Map<String, ModFileInfo> configToModMap) {
         for (Config rawConfig : Mixins.getConfigs()) {
            ModFileInfo mod = configToModMap.get(rawConfig.getName());
            if (mod != null) {
               IMixinConfig config = rawConfig.getConfig();
               if (!mod.getMods().isEmpty()) {
                  String modid = ((IModInfo)mod.getMods().getFirst()).getModId();
                  int compat;
                  if (ConnectorEarlyLoader.isConnectorMod(modid)) {
                     compat = FabricLoaderImpl.INSTANCE.getModContainer(modid).map(m -> getMixinCompat(m.getMetadata())).orElse(10000);
                  } else {
                     compat = 10000;
                  }

                  config.decorate("fabric-compat", compat);
               }
            }
         }
      }

      public static int getMixinCompat(ModMetadata metadata) {
         boolean found = false;
         List<VersionInterval> reqIntervals = List.of(VersionInterval.INFINITE);

         for (ModDependency dep : metadata.getDependencies()) {
            if (dep.getModId().equals("fabricloader") || dep.getModId().equals("fabric-loader")) {
               if (dep.getKind() == ModDependency.Kind.DEPENDS) {
                  found = true;
                  reqIntervals = VersionInterval.and(reqIntervals, dep.getVersionIntervals());
               } else if (dep.getKind() == ModDependency.Kind.BREAKS) {
                  found = true;
                  reqIntervals = VersionInterval.and(reqIntervals, VersionInterval.not(dep.getVersionIntervals()));
               }
            }
         }

         if (!found) {
            return 10000;
         }

         if (reqIntervals.isEmpty()) {
            throw new IllegalStateException("mod " + metadata.getId() + " is incompatible with every loader version?");
         }

         Version minLoaderVersion = reqIntervals.getFirst().getMin();
         if (minLoaderVersion != null) {
            Iterator var7 = VERSIONS.iterator();
            if (var7.hasNext()) {
               FabricMixinBootstrap.MixinConfigDecorator.LoaderMixinVersionEntry version = (FabricMixinBootstrap.MixinConfigDecorator.LoaderMixinVersionEntry)var7.next();
               if (minLoaderVersion.compareTo(version.loaderVersion) >= 0) {
                  return version.mixinVersion;
               }
            }
         }

         return 9002;
      }

      private record LoaderMixinVersionEntry(SemanticVersion loaderVersion, int mixinVersion) {
         public static FabricMixinBootstrap.MixinConfigDecorator.LoaderMixinVersionEntry create(String loaderVersion, int mixinVersion) {
            return new FabricMixinBootstrap.MixinConfigDecorator.LoaderMixinVersionEntry(
               (SemanticVersion)LambdaExceptionUtils.uncheck(() -> SemanticVersion.parse(loaderVersion)), mixinVersion
            );
         }
      }
   }
}
