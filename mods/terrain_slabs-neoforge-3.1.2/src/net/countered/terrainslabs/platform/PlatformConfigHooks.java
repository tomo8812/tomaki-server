package net.countered.terrainslabs.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import net.countered.terrainslabs.platform.neoforge.PlatformConfigHooksImpl;

public class PlatformConfigHooks {
   @ExpectPlatform
   @Transformed
   public static boolean isSlabGenerationEnabled() {
      return PlatformConfigHooksImpl.isSlabGenerationEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isVegetationOnSlabsEnabled() {
      return PlatformConfigHooksImpl.isVegetationOnSlabsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isSnowOnSlabsEnabled() {
      return PlatformConfigHooksImpl.isSnowOnSlabsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static float getAoStrength() {
      return PlatformConfigHooksImpl.getAoStrength();
   }

   @ExpectPlatform
   @Transformed
   public static int getSlabRunLength() {
      return PlatformConfigHooksImpl.getSlabRunLength();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isCornerSlabsEnabled() {
      return PlatformConfigHooksImpl.isCornerSlabsEnabled();
   }
}
