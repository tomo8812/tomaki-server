package org.sinytra.connector.transformer;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.ITransformerLoader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.impl.metadata.DependencyOverrides;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;
import net.fabricmc.loader.impl.metadata.VersionOverrides;
import org.jetbrains.annotations.Nullable;
import org.sinytra.adapter.util.provider.ClassLookup;
import org.sinytra.connector.transformer.transform.TransformProgressMeter;
import reloc.net.minecraftforge.fart.api.ClassProvider;

public interface TransformerEnvironment {
   Path getAuditReportPath();

   Path getGeneratedJarPath();

   ClassLookup getCleanClassLookup();

   ClassProvider getRuntimeClassProvider(List<Path> var1);

   LoaderModMetadata wrapModMetadata(LoaderModMetadata var1);

   TransformProgressMeter createProgressMeter(String var1, int var2);

   Path createCachedJarPath(String var1) throws IOException;

   EnvType getEnvType();

   VersionOverrides getVersionOverrides();

   Supplier<DependencyOverrides> getDependencyOverrides();

   void setGlobalBytecodeLoader(@Nullable ITransformerLoader var1);

   RuntimeException onTransformationError(String var1, Throwable var2);

   int getFabricMixinCompatibility(LoaderModMetadata var1);

   String getJarCacheVersion();
}
