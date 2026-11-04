package net.satisfy.farm_and_charm.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import net.satisfy.farm_and_charm.platform.neoforge.PlatformHelperImpl;

public abstract class PlatformHelper {
   @ExpectPlatform
   @Transformed
   public static boolean animationsEnabled() {
      return PlatformHelperImpl.animationsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isRainGrowthEffectEnabled() {
      return PlatformHelperImpl.isRainGrowthEffectEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static float getRainGrowthMultiplier() {
      return PlatformHelperImpl.getRainGrowthMultiplier();
   }

   @ExpectPlatform
   @Transformed
   public static int getBigCropChance() {
      return PlatformHelperImpl.getBigCropChance();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isBonemealEffectEnabled() {
      return PlatformHelperImpl.isBonemealEffectEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isFertilizerEnabled() {
      return PlatformHelperImpl.isFertilizerEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static int getFertilizedSoilRange() {
      return PlatformHelperImpl.getFertilizedSoilRange();
   }

   @ExpectPlatform
   @Transformed
   public static int getScarecrowGrowthInterval() {
      return PlatformHelperImpl.getScarecrowGrowthInterval();
   }

   @ExpectPlatform
   @Transformed
   public static int getScarecrowRange() {
      return PlatformHelperImpl.getScarecrowRange();
   }

   @ExpectPlatform
   @Transformed
   public static int getWildCropDropChance() {
      return PlatformHelperImpl.getWildCropDropChance();
   }

   @ExpectPlatform
   @Transformed
   public static int getWaterSprinklerRange() {
      return PlatformHelperImpl.getWaterSprinklerRange();
   }

   @ExpectPlatform
   @Transformed
   public static int getWellGroundwaterDepth() {
      return PlatformHelperImpl.getWellGroundwaterDepth();
   }

   @ExpectPlatform
   @Transformed
   public static int getWellRainFillChance() {
      return PlatformHelperImpl.getWellRainFillChance();
   }

   @ExpectPlatform
   @Transformed
   public static int getFeedingTroughRange() {
      return PlatformHelperImpl.getFeedingTroughRange();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isTamingEnabled() {
      return PlatformHelperImpl.isTamingEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableCatTamingChance() {
      return PlatformHelperImpl.enableCatTamingChance();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isHorseTamingEnabled() {
      return PlatformHelperImpl.isHorseTamingEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isHorseEffectsEnabled() {
      return PlatformHelperImpl.isHorseEffectsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isChickenEffectsEnabled() {
      return PlatformHelperImpl.isChickenEffectsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static int getChickenCoopMaxChickens() {
      return PlatformHelperImpl.getChickenCoopMaxChickens();
   }

   @ExpectPlatform
   @Transformed
   public static int getChickenCoopMaxEggs() {
      return PlatformHelperImpl.getChickenCoopMaxEggs();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isDogBeggingEnabled() {
      return PlatformHelperImpl.isDogBeggingEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isCatBeggingEnabled() {
      return PlatformHelperImpl.isCatBeggingEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static int getPetBowlSearchRange() {
      return PlatformHelperImpl.getPetBowlSearchRange();
   }

   @ExpectPlatform
   @Transformed
   public static int getFeedingTimeMiddayStart() {
      return PlatformHelperImpl.getFeedingTimeMiddayStart();
   }

   @ExpectPlatform
   @Transformed
   public static int getFeedingTimeMiddayEnd() {
      return PlatformHelperImpl.getFeedingTimeMiddayEnd();
   }

   @ExpectPlatform
   @Transformed
   public static int getFeedingTimeEveningStart() {
      return PlatformHelperImpl.getFeedingTimeEveningStart();
   }

   @ExpectPlatform
   @Transformed
   public static int getFeedingTimeEveningEnd() {
      return PlatformHelperImpl.getFeedingTimeEveningEnd();
   }

   @ExpectPlatform
   @Transformed
   public static boolean infoTooltipsNeedDungarees() {
      return PlatformHelperImpl.infoTooltipsNeedDungarees();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showCuttingBoardInfo() {
      return PlatformHelperImpl.showCuttingBoardInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showMincerInfo() {
      return PlatformHelperImpl.showMincerInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showCraftingBowlInfo() {
      return PlatformHelperImpl.showCraftingBowlInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showSiloInfo() {
      return PlatformHelperImpl.showSiloInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showWaterSprinklerInfo() {
      return PlatformHelperImpl.showWaterSprinklerInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showScarecrowInfo() {
      return PlatformHelperImpl.showScarecrowInfo();
   }

   @ExpectPlatform
   @Transformed
   public static int getIronCleaverChops() {
      return PlatformHelperImpl.getIronCleaverChops();
   }

   @ExpectPlatform
   @Transformed
   public static int getDiamondCleaverChops() {
      return PlatformHelperImpl.getDiamondCleaverChops();
   }

   @ExpectPlatform
   @Transformed
   public static int getNetheriteCleaverChops() {
      return PlatformHelperImpl.getNetheriteCleaverChops();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isCleaverHeadDropsEnabled() {
      return PlatformHelperImpl.isCleaverHeadDropsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isCookingExperienceEnabled() {
      return PlatformHelperImpl.isCookingExperienceEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static float getCookingExperienceMultiplier() {
      return PlatformHelperImpl.getCookingExperienceMultiplier();
   }

   @ExpectPlatform
   @Transformed
   public static int getChickenEffectTickInterval() {
      return PlatformHelperImpl.getChickenEffectTickInterval();
   }

   @ExpectPlatform
   @Transformed
   public static int getChickenEffectEggChance() {
      return PlatformHelperImpl.getChickenEffectEggChance();
   }

   @ExpectPlatform
   @Transformed
   public static int getChickenEffectFeatherChance() {
      return PlatformHelperImpl.getChickenEffectFeatherChance();
   }

   @ExpectPlatform
   @Transformed
   public static int getFeastEffectSatiationInterval() {
      return PlatformHelperImpl.getFeastEffectSatiationInterval();
   }

   @ExpectPlatform
   @Transformed
   public static int getFeastEffectSustenanceInterval() {
      return PlatformHelperImpl.getFeastEffectSustenanceInterval();
   }

   @ExpectPlatform
   @Transformed
   public static int getFeastEffectHealAmount() {
      return PlatformHelperImpl.getFeastEffectHealAmount();
   }

   @ExpectPlatform
   @Transformed
   public static int getSustenanceEffectInterval() {
      return PlatformHelperImpl.getSustenanceEffectInterval();
   }

   @ExpectPlatform
   @Transformed
   public static int getSustenanceEffectHealAmount() {
      return PlatformHelperImpl.getSustenanceEffectHealAmount();
   }

   @ExpectPlatform
   @Transformed
   public static int getSustenanceEffectFoodIncrement() {
      return PlatformHelperImpl.getSustenanceEffectFoodIncrement();
   }

   @ExpectPlatform
   @Transformed
   public static int getSatiationEffectInterval() {
      return PlatformHelperImpl.getSatiationEffectInterval();
   }

   @ExpectPlatform
   @Transformed
   public static int getSatiationEffectHealAmount() {
      return PlatformHelperImpl.getSatiationEffectHealAmount();
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

   @ExpectPlatform
   @Transformed
   public static boolean isFirefliesEnabled() {
      return PlatformHelperImpl.isFirefliesEnabled();
   }
}
