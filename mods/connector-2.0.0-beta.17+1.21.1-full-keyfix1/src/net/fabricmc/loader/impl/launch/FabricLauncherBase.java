package net.fabricmc.loader.impl.launch;

import java.util.Map;

public abstract class FabricLauncherBase implements FabricLauncher {
   private static FabricLauncherBase instance;
   private static Map<String, Object> properties;
   private static MappingConfiguration mappingConfiguration = new MappingConfiguration();

   public static Class<?> getClass(String className) throws ClassNotFoundException {
      return Class.forName(className, true, getLauncher().getTargetClassLoader());
   }

   @Override
   public MappingConfiguration getMappingConfiguration() {
      return mappingConfiguration;
   }

   protected static void setProperties(Map<String, Object> propertiesA) {
      if (properties != null && properties != propertiesA) {
         throw new RuntimeException("Duplicate setProperties call!");
      }

      properties = propertiesA;
   }

   public static FabricLauncher getLauncher() {
      if (instance == null) {
         instance = new FabricLauncherFML();
      }

      return instance;
   }

   public static Map<String, Object> getProperties() {
      return properties;
   }

   public static boolean isMixinReady() {
      return true;
   }
}
