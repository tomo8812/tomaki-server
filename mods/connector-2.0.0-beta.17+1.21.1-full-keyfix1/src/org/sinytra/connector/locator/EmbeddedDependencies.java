package org.sinytra.connector.locator;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class EmbeddedDependencies {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String FABRIC_LOADER_VERSION = "Fabric-Loader-Version";
   private static final Path SELF_PATH = (Path)LambdaExceptionUtils.uncheck(() -> {
      URL jarLocation = ConnectorLocator.class.getProtectionDomain().getCodeSource().getLocation();
      return Path.of(jarLocation.toURI());
   });
   private static final Attributes ATTRIBUTES = readManifestAttributes();
   private static final Supplier<String> JAR_CACHE_VERSION = Suppliers.memoize(() -> {
      String ver = EmbeddedDependencies.class.getPackage().getImplementationVersion();
      if (ver == null) {
         LOGGER.error("Missing Connector jar version, disabling transformer caching");
         return RandomStringUtils.randomAlphabetic(5);
      } else {
         return ver + "," + FMLEnvironment.dist.name().toLowerCase();
      }
   });

   @Nullable
   public static String getJarCacheVersion() {
      return JAR_CACHE_VERSION.get();
   }

   public static String getFabricLoaderVersion() {
      return ATTRIBUTES.getValue("Fabric-Loader-Version");
   }

   private static Attributes readManifestAttributes() {
      Path manifestPath = SELF_PATH.resolve("META-INF/MANIFEST.MF");

      Manifest manifest;
      try (InputStream is = Files.newInputStream(manifestPath)) {
         manifest = new Manifest(is);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      return manifest.getMainAttributes();
   }

   private EmbeddedDependencies() {
   }
}
