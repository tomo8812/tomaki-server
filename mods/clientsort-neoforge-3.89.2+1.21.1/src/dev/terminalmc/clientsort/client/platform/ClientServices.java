package dev.terminalmc.clientsort.client.platform;

import java.util.ServiceLoader;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientServices {
   private static final Logger LOGGER = LoggerFactory.getLogger("ClientSort (Client Service)");

   private ClientServices() {
      throw new UnsupportedOperationException("This class cannot be instantiated.");
   }

   public static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz, clazz.getClassLoader())
         .findFirst()
         .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }

   public static <T> T loadOr(Class<T> clazz, Supplier<T> supplier) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElse(supplier.get());
      LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }
}
