package net.satisfy.brewery.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import net.satisfy.brewery.platform.neoforge.PlatformHelperImpl;

public class PlatformHelper {
   @ExpectPlatform
   @Transformed
   public static int getBrewTime() {
      return PlatformHelperImpl.getBrewTime();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isBrewEventsEnabled() {
      return PlatformHelperImpl.isBrewEventsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static int getMinBrewEventInterval() {
      return PlatformHelperImpl.getMinBrewEventInterval();
   }

   @ExpectPlatform
   @Transformed
   public static int getMaxBrewEventInterval() {
      return PlatformHelperImpl.getMaxBrewEventInterval();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isBeerElementalsEnabled() {
      return PlatformHelperImpl.isBeerElementalsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean animationsEnabled() {
      return PlatformHelperImpl.animationsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isDrunkennessEnabled() {
      return PlatformHelperImpl.isDrunkennessEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isDrunkSwayEnabled() {
      return PlatformHelperImpl.isDrunkSwayEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isDrunkSlownessEnabled() {
      return PlatformHelperImpl.isDrunkSlownessEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isBlackoutEnabled() {
      return PlatformHelperImpl.isBlackoutEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static double getBlackoutChance() {
      return PlatformHelperImpl.getBlackoutChance();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isBlackoutTeleportEnabled() {
      return PlatformHelperImpl.isBlackoutTeleportEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static int getBlackoutTeleportRange() {
      return PlatformHelperImpl.getBlackoutTeleportRange();
   }

   @ExpectPlatform
   @Transformed
   public static int getCombustionEffectRadius() {
      return PlatformHelperImpl.getCombustionEffectRadius();
   }

   @ExpectPlatform
   @Transformed
   public static int getCombustionEffectIgniteChance() {
      return PlatformHelperImpl.getCombustionEffectIgniteChance();
   }

   @ExpectPlatform
   @Transformed
   public static int getRepulsionEffectRadius() {
      return PlatformHelperImpl.getRepulsionEffectRadius();
   }

   @ExpectPlatform
   @Transformed
   public static double getRepulsionEffectStrength() {
      return PlatformHelperImpl.getRepulsionEffectStrength();
   }

   @ExpectPlatform
   @Transformed
   public static int getStoutHeartEffectHealthCap() {
      return PlatformHelperImpl.getStoutHeartEffectHealthCap();
   }

   @ExpectPlatform
   @Transformed
   public static double getStoutHeartEffectHealAmount() {
      return PlatformHelperImpl.getStoutHeartEffectHealAmount();
   }

   @ExpectPlatform
   @Transformed
   public static int getMiningEffectHasteOneHeight() {
      return PlatformHelperImpl.getMiningEffectHasteOneHeight();
   }

   @ExpectPlatform
   @Transformed
   public static int getMiningEffectHasteTwoHeight() {
      return PlatformHelperImpl.getMiningEffectHasteTwoHeight();
   }

   @ExpectPlatform
   @Transformed
   public static int getMiningEffectHasteThreeHeight() {
      return PlatformHelperImpl.getMiningEffectHasteThreeHeight();
   }

   @ExpectPlatform
   @Transformed
   public static int getMiningEffectHasteFourHeight() {
      return PlatformHelperImpl.getMiningEffectHasteFourHeight();
   }

   @ExpectPlatform
   @Transformed
   public static int getPacifyEffectRange() {
      return PlatformHelperImpl.getPacifyEffectRange();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isHaleyEffectFlightEnabled() {
      return PlatformHelperImpl.isHaleyEffectFlightEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showBrewingstationInfo() {
      return PlatformHelperImpl.showBrewingstationInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showBigBarrelInfo() {
      return PlatformHelperImpl.showBigBarrelInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean infoTooltipsNeedDungarees() {
      return PlatformHelperImpl.infoTooltipsNeedDungarees();
   }

   @ExpectPlatform
   @Transformed
   public static int getNutrition(String itemName) {
      return PlatformHelperImpl.getNutrition(itemName);
   }

   @ExpectPlatform
   @Transformed
   public static float getSaturation(String itemName) {
      return PlatformHelperImpl.getSaturation(itemName);
   }
}
