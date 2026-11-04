package net.satisfy.bakery.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import net.satisfy.bakery.platform.neoforge.PlatformHelperImpl;

public class PlatformHelper {
   @ExpectPlatform
   @Transformed
   public static boolean animationsEnabled() {
      return PlatformHelperImpl.animationsEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean shouldGiveEffect() {
      return PlatformHelperImpl.shouldGiveEffect();
   }

   @ExpectPlatform
   @Transformed
   public static boolean shouldShowTooltip() {
      return PlatformHelperImpl.shouldShowTooltip();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showBakerStationInfo() {
      return PlatformHelperImpl.showBakerStationInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showJamPotInfo() {
      return PlatformHelperImpl.showJamPotInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showDisplayInfo() {
      return PlatformHelperImpl.showDisplayInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean infoTooltipsNeedDungarees() {
      return PlatformHelperImpl.infoTooltipsNeedDungarees();
   }

   @ExpectPlatform
   @Transformed
   public static int getCroissantNutrition() {
      return PlatformHelperImpl.getCroissantNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getCroissantSaturation() {
      return PlatformHelperImpl.getCroissantSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getCrustyBreadNutrition() {
      return PlatformHelperImpl.getCrustyBreadNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getCrustyBreadSaturation() {
      return PlatformHelperImpl.getCrustyBreadSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getBreadNutrition() {
      return PlatformHelperImpl.getBreadNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getBreadSaturation() {
      return PlatformHelperImpl.getBreadSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getBaguetteNutrition() {
      return PlatformHelperImpl.getBaguetteNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getBaguetteSaturation() {
      return PlatformHelperImpl.getBaguetteSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getToastNutrition() {
      return PlatformHelperImpl.getToastNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getToastSaturation() {
      return PlatformHelperImpl.getToastSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getBraidedBreadNutrition() {
      return PlatformHelperImpl.getBraidedBreadNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getBraidedBreadSaturation() {
      return PlatformHelperImpl.getBraidedBreadSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getSandwichNutrition() {
      return PlatformHelperImpl.getSandwichNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getSandwichSaturation() {
      return PlatformHelperImpl.getSandwichSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getVegetableSandwichNutrition() {
      return PlatformHelperImpl.getVegetableSandwichNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getVegetableSandwichSaturation() {
      return PlatformHelperImpl.getVegetableSandwichSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getGrilledSalmonSandwichNutrition() {
      return PlatformHelperImpl.getGrilledSalmonSandwichNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getGrilledSalmonSandwichSaturation() {
      return PlatformHelperImpl.getGrilledSalmonSandwichSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getGrilledBaconSandwichNutrition() {
      return PlatformHelperImpl.getGrilledBaconSandwichNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getGrilledBaconSandwichSaturation() {
      return PlatformHelperImpl.getGrilledBaconSandwichSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getBreadWithJamNutrition() {
      return PlatformHelperImpl.getBreadWithJamNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getBreadWithJamSaturation() {
      return PlatformHelperImpl.getBreadWithJamSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getStrawberryCakeSliceNutrition() {
      return PlatformHelperImpl.getStrawberryCakeSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getStrawberryCakeSliceSaturation() {
      return PlatformHelperImpl.getStrawberryCakeSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getSweetberryCakeSliceNutrition() {
      return PlatformHelperImpl.getSweetberryCakeSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getSweetberryCakeSliceSaturation() {
      return PlatformHelperImpl.getSweetberryCakeSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateCakeSliceNutrition() {
      return PlatformHelperImpl.getChocolateCakeSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getChocolateCakeSliceSaturation() {
      return PlatformHelperImpl.getChocolateCakeSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateGateauSliceNutrition() {
      return PlatformHelperImpl.getChocolateGateauSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getChocolateGateauSliceSaturation() {
      return PlatformHelperImpl.getChocolateGateauSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getBundtCakeSliceNutrition() {
      return PlatformHelperImpl.getBundtCakeSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getBundtCakeSliceSaturation() {
      return PlatformHelperImpl.getBundtCakeSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getLinzerTartSliceNutrition() {
      return PlatformHelperImpl.getLinzerTartSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getLinzerTartSliceSaturation() {
      return PlatformHelperImpl.getLinzerTartSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getApplePieSliceNutrition() {
      return PlatformHelperImpl.getApplePieSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getApplePieSliceSaturation() {
      return PlatformHelperImpl.getApplePieSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getGlowberryPieSliceNutrition() {
      return PlatformHelperImpl.getGlowberryPieSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getGlowberryPieSliceSaturation() {
      return PlatformHelperImpl.getGlowberryPieSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateTartSliceNutrition() {
      return PlatformHelperImpl.getChocolateTartSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getChocolateTartSliceSaturation() {
      return PlatformHelperImpl.getChocolateTartSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getPuddingSliceNutrition() {
      return PlatformHelperImpl.getPuddingSliceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getPuddingSliceSaturation() {
      return PlatformHelperImpl.getPuddingSliceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getStrawberryGlazedCookieNutrition() {
      return PlatformHelperImpl.getStrawberryGlazedCookieNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getStrawberryGlazedCookieSaturation() {
      return PlatformHelperImpl.getStrawberryGlazedCookieSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getSweetberryGlazedCookieNutrition() {
      return PlatformHelperImpl.getSweetberryGlazedCookieNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getSweetberryGlazedCookieSaturation() {
      return PlatformHelperImpl.getSweetberryGlazedCookieSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateGlazedCookieNutrition() {
      return PlatformHelperImpl.getChocolateGlazedCookieNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getChocolateGlazedCookieSaturation() {
      return PlatformHelperImpl.getChocolateGlazedCookieSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getStrawberryCupcakeNutrition() {
      return PlatformHelperImpl.getStrawberryCupcakeNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getStrawberryCupcakeSaturation() {
      return PlatformHelperImpl.getStrawberryCupcakeSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getSweetberryCupcakeNutrition() {
      return PlatformHelperImpl.getSweetberryCupcakeNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getSweetberryCupcakeSaturation() {
      return PlatformHelperImpl.getSweetberryCupcakeSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getAppleCupcakeNutrition() {
      return PlatformHelperImpl.getAppleCupcakeNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getAppleCupcakeSaturation() {
      return PlatformHelperImpl.getAppleCupcakeSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getCornetNutrition() {
      return PlatformHelperImpl.getCornetNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getCornetSaturation() {
      return PlatformHelperImpl.getCornetSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getJamRollNutrition() {
      return PlatformHelperImpl.getJamRollNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getJamRollSaturation() {
      return PlatformHelperImpl.getJamRollSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getWaffleNutrition() {
      return PlatformHelperImpl.getWaffleNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getWaffleSaturation() {
      return PlatformHelperImpl.getWaffleSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateTruffleNutrition() {
      return PlatformHelperImpl.getChocolateTruffleNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getChocolateTruffleSaturation() {
      return PlatformHelperImpl.getChocolateTruffleSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getMisslilituBiscuitNutrition() {
      return PlatformHelperImpl.getMisslilituBiscuitNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getMisslilituBiscuitSaturation() {
      return PlatformHelperImpl.getMisslilituBiscuitSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getBunNutrition() {
      return PlatformHelperImpl.getBunNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static float getBunSaturation() {
      return PlatformHelperImpl.getBunSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int getVitalityEffectInterval() {
      return PlatformHelperImpl.getVitalityEffectInterval();
   }

   @ExpectPlatform
   @Transformed
   public static float getVitalityEffectExhaustionReduction() {
      return PlatformHelperImpl.getVitalityEffectExhaustionReduction();
   }

   @ExpectPlatform
   @Transformed
   public static int getSugarRushMaxStacks() {
      return PlatformHelperImpl.getSugarRushMaxStacks();
   }

   @ExpectPlatform
   @Transformed
   public static double getSugarRushBonusPerStack() {
      return PlatformHelperImpl.getSugarRushBonusPerStack();
   }

   @ExpectPlatform
   @Transformed
   public static int getSugarRushAttackSpeedStacks() {
      return PlatformHelperImpl.getSugarRushAttackSpeedStacks();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isSugarRushAttackSpeedEnabled() {
      return PlatformHelperImpl.isSugarRushAttackSpeedEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static int getBannerEffectRadius() {
      return PlatformHelperImpl.getBannerEffectRadius();
   }

   @ExpectPlatform
   @Transformed
   public static int getBannerEffectAmplifier() {
      return PlatformHelperImpl.getBannerEffectAmplifier();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isBakerStationAnimationEnabled() {
      return PlatformHelperImpl.isBakerStationAnimationEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isKneadingEnabled() {
      return PlatformHelperImpl.isKneadingEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static int getKneadPresses() {
      return PlatformHelperImpl.getKneadPresses();
   }

   @ExpectPlatform
   @Transformed
   public static int getRollingPinPresses() {
      return PlatformHelperImpl.getRollingPinPresses();
   }

   @ExpectPlatform
   @Transformed
   public static int getKnifeDuration() {
      return PlatformHelperImpl.getKnifeDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getJamDuration() {
      return PlatformHelperImpl.getJamDuration();
   }

   @ExpectPlatform
   @Transformed
   public static float getKnifeAttackSpeed() {
      return PlatformHelperImpl.getKnifeAttackSpeed();
   }

   @ExpectPlatform
   @Transformed
   public static float getRollingPinAttackSpeed() {
      return PlatformHelperImpl.getRollingPinAttackSpeed();
   }

   @ExpectPlatform
   @Transformed
   public static int getCroissantEffectDuration() {
      return PlatformHelperImpl.getCroissantEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getCrustyBreadEffectDuration() {
      return PlatformHelperImpl.getCrustyBreadEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getBreadEffectDuration() {
      return PlatformHelperImpl.getBreadEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getBaguetteEffectDuration() {
      return PlatformHelperImpl.getBaguetteEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getToastEffectDuration() {
      return PlatformHelperImpl.getToastEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getBraidedBreadEffectDuration() {
      return PlatformHelperImpl.getBraidedBreadEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getSandwichEffectDuration() {
      return PlatformHelperImpl.getSandwichEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getVegetableSandwichEffectDuration() {
      return PlatformHelperImpl.getVegetableSandwichEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getGrilledSalmonSandwichEffectDuration() {
      return PlatformHelperImpl.getGrilledSalmonSandwichEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getGrilledBaconSandwichEffectDuration() {
      return PlatformHelperImpl.getGrilledBaconSandwichEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getBreadWithJamEffectDuration() {
      return PlatformHelperImpl.getBreadWithJamEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getStrawberryCakeSliceEffectDuration() {
      return PlatformHelperImpl.getStrawberryCakeSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getSweetberryCakeSliceEffectDuration() {
      return PlatformHelperImpl.getSweetberryCakeSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateCakeSliceEffectDuration() {
      return PlatformHelperImpl.getChocolateCakeSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateGateauSliceEffectDuration() {
      return PlatformHelperImpl.getChocolateGateauSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getBundtCakeSliceEffectDuration() {
      return PlatformHelperImpl.getBundtCakeSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getLinzerTartSliceEffectDuration() {
      return PlatformHelperImpl.getLinzerTartSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getApplePieSliceEffectDuration() {
      return PlatformHelperImpl.getApplePieSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getGlowberryPieSliceEffectDuration() {
      return PlatformHelperImpl.getGlowberryPieSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateTartSliceEffectDuration() {
      return PlatformHelperImpl.getChocolateTartSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getPuddingSliceEffectDuration() {
      return PlatformHelperImpl.getPuddingSliceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getStrawberryGlazedCookieEffectDuration() {
      return PlatformHelperImpl.getStrawberryGlazedCookieEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getSweetberryGlazedCookieEffectDuration() {
      return PlatformHelperImpl.getSweetberryGlazedCookieEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateGlazedCookieEffectDuration() {
      return PlatformHelperImpl.getChocolateGlazedCookieEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getStrawberryCupcakeEffectDuration() {
      return PlatformHelperImpl.getStrawberryCupcakeEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getSweetberryCupcakeEffectDuration() {
      return PlatformHelperImpl.getSweetberryCupcakeEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getAppleCupcakeEffectDuration() {
      return PlatformHelperImpl.getAppleCupcakeEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getCornetEffectDuration() {
      return PlatformHelperImpl.getCornetEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getJamRollEffectDuration() {
      return PlatformHelperImpl.getJamRollEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getChocolateTruffleEffectDuration() {
      return PlatformHelperImpl.getChocolateTruffleEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getMisslilituBiscuitEffectDuration() {
      return PlatformHelperImpl.getMisslilituBiscuitEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getWaffleEffectDuration() {
      return PlatformHelperImpl.getWaffleEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getBunEffectDuration() {
      return PlatformHelperImpl.getBunEffectDuration();
   }
}
