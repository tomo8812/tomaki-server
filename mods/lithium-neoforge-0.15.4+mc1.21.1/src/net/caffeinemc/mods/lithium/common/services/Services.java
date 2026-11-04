package net.caffeinemc.mods.lithium.common.services;

import java.util.ServiceLoader;
import net.caffeinemc.mods.lithium.common.LithiumMod;

public class Services {
   public static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      LithiumMod.logger().debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }
}
