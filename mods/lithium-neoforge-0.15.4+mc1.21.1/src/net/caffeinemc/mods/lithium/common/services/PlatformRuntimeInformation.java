package net.caffeinemc.mods.lithium.common.services;

import java.nio.file.Path;

public interface PlatformRuntimeInformation {
   PlatformRuntimeInformation INSTANCE = Services.load(PlatformRuntimeInformation.class);

   static PlatformRuntimeInformation getInstance() {
      return INSTANCE;
   }

   boolean isDevelopmentEnvironment();

   Path getGameDirectory();

   Path getConfigDirectory();

   boolean platformHasEarlyLoadingScreen();

   boolean platformUsesRefmap();

   boolean isModInLoadingList(String var1);

   boolean usesAlphaMultiplication();
}
