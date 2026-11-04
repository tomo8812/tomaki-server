package net.countered.terrainslabs.platform.neoforge;

import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.lib.config.MidnightConfig.Comment;
import eu.midnightdust.lib.config.MidnightConfig.Condition;
import eu.midnightdust.lib.config.MidnightConfig.Entry;

public class PlatformConfigHooksImpl extends MidnightConfig {
   public static final String GENERATION = "generation";
   public static final String RENDERING = "rendering";
   @Entry(category = "generation")
   public static boolean enableSlabGeneration = true;
   @Entry(category = "generation")
   public static boolean enableVegetationOnSlabs = true;
   @Entry(category = "generation")
   public static boolean enableSnowOnSlabs = true;
   @Entry(category = "generation")
   public static boolean enableExperimentalFeatures = false;
   @Comment(category = "generation", name = "Experimental Features:")
   public static Comment needsExperimentalEnabled;
   @Condition(requiredOption = "enableExperimentalFeatures", visibleButLocked = true)
   @Entry(category = "generation")
   public static boolean enableCornerSlabs = false;
   @Condition(requiredOption = "enableExperimentalFeatures", visibleButLocked = true)
   @Entry(category = "generation", isSlider = true, min = 1.0, max = 8.0)
   public static int slabRunLength = 1;
   @Entry(category = "rendering", isSlider = true, min = 0.0, max = 1.0)
   public static float adjustSlabAo = 0.5F;

   public static boolean isSlabGenerationEnabled() {
      return enableSlabGeneration;
   }

   public static boolean isVegetationOnSlabsEnabled() {
      return enableVegetationOnSlabs;
   }

   public static boolean isSnowOnSlabsEnabled() {
      return enableSnowOnSlabs;
   }

   public static boolean isCornerSlabsEnabled() {
      return enableCornerSlabs;
   }

   public static int getSlabRunLength() {
      return slabRunLength;
   }

   public static float getAoStrength() {
      return adjustSlabAo;
   }
}
