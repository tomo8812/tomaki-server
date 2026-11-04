package org.sinytra.connector.locator;

import cpw.mods.jarhandling.SecureJar;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import net.neoforged.fml.loading.moddiscovery.readers.JarModsDotTomlModFileReader;
import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFile;
import net.neoforged.neoforgespi.locating.IModFileCandidateLocator;
import net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes;
import net.neoforged.neoforgespi.locating.IModFile.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.filter.MarkerFilter;
import org.sinytra.connector.service.DummyVirtualJar;

public class ConnectorEarlyLocatorBootstrap implements IModFileCandidateLocator {
   private static ILaunchContext launchContext;

   public ConnectorEarlyLocatorBootstrap() {
      injectLogMarkers();
   }

   public static ILaunchContext getLaunchContext() {
      return launchContext;
   }

   private static void injectLogMarkers() {
      String markerselection = System.getProperty("connector.logging.markers", "");
      Arrays.stream(markerselection.split(",")).forEach(marker -> System.setProperty("connector.logging.marker." + marker.toLowerCase(Locale.ROOT), "ACCEPT"));
      Logger logger = (Logger)LogManager.getLogger(ConnectorEarlyLocatorBootstrap.class);
      Configuration config = logger.getContext().getConfiguration();
      config.addFilter(MarkerFilter.createFilter("MIXINPATCH", parseLogMarker("connector.logging.marker.mixinpatch"), Result.NEUTRAL));
      logger.getContext().updateLoggers();
   }

   private static Result parseLogMarker(String propertyName) {
      String value = System.getProperty(propertyName, "DENY");
      return Result.valueOf(value);
   }

   public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
      launchContext = context;
      pipeline.addModFile(overrideFabricLoaderMod());
   }

   private static IModFile overrideFabricLoaderMod() {
      SecureJar secureJar = new DummyVirtualJar("net.fabricmc.loader", "dummy_forgified_fabric_loader", Set.of(), () -> {
         Manifest manifest = new Manifest();
         manifest.getMainAttributes().put(Name.IMPLEMENTATION_VERSION, "999.999.999");
         return manifest;
      }, s -> Optional.empty());
      return IModFile.create(secureJar, JarModsDotTomlModFileReader::manifestParser, Type.LIBRARY, ModFileDiscoveryAttributes.DEFAULT);
   }
}
