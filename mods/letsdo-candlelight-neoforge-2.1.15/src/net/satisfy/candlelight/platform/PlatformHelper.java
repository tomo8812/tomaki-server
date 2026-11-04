package net.satisfy.candlelight.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import net.satisfy.candlelight.platform.neoforge.PlatformHelperImpl;

public class PlatformHelper {
   @ExpectPlatform
   @Transformed
   public static double tableSetFoodBonus() {
      return PlatformHelperImpl.tableSetFoodBonus();
   }

   @ExpectPlatform
   @Transformed
   public static double elegantFoodBonus() {
      return PlatformHelperImpl.elegantFoodBonus();
   }

   @ExpectPlatform
   @Transformed
   public static int wellServedTicksPerCourse() {
      return PlatformHelperImpl.wellServedTicksPerCourse();
   }

   @ExpectPlatform
   @Transformed
   public static int maxWellServedTicks() {
      return PlatformHelperImpl.maxWellServedTicks();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableDinnerMenu() {
      return PlatformHelperImpl.enableDinnerMenu();
   }

   @ExpectPlatform
   @Transformed
   public static int menuBonusTicks() {
      return PlatformHelperImpl.menuBonusTicks();
   }

   @ExpectPlatform
   @Transformed
   public static int maxMenuWellServedTicks() {
      return PlatformHelperImpl.maxMenuWellServedTicks();
   }

   @ExpectPlatform
   @Transformed
   public static int menuMaxTicksBetweenCourses() {
      return PlatformHelperImpl.menuMaxTicksBetweenCourses();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableTableLights() {
      return PlatformHelperImpl.enableTableLights();
   }

   @ExpectPlatform
   @Transformed
   public static int tableLightRadius() {
      return PlatformHelperImpl.tableLightRadius();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableTableDrinks() {
      return PlatformHelperImpl.enableTableDrinks();
   }

   @ExpectPlatform
   @Transformed
   public static int tableSignMaxLines() {
      return PlatformHelperImpl.tableSignMaxLines();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableDinnerGuests() {
      return PlatformHelperImpl.enableDinnerGuests();
   }

   @ExpectPlatform
   @Transformed
   public static double dinnerGuestChance() {
      return PlatformHelperImpl.dinnerGuestChance();
   }

   @ExpectPlatform
   @Transformed
   public static int dinnerGuestRange() {
      return PlatformHelperImpl.dinnerGuestRange();
   }

   @ExpectPlatform
   @Transformed
   public static int dinnerGuestInterval() {
      return PlatformHelperImpl.dinnerGuestInterval();
   }

   @ExpectPlatform
   @Transformed
   public static int dinnerStart() {
      return PlatformHelperImpl.dinnerStart();
   }

   @ExpectPlatform
   @Transformed
   public static int dinnerEnd() {
      return PlatformHelperImpl.dinnerEnd();
   }

   @ExpectPlatform
   @Transformed
   public static boolean nitwitsEat() {
      return PlatformHelperImpl.nitwitsEat();
   }

   @ExpectPlatform
   @Transformed
   public static int dinnerDiscountPercent() {
      return PlatformHelperImpl.dinnerDiscountPercent();
   }

   @ExpectPlatform
   @Transformed
   public static int dinnerDiscountTicks() {
      return PlatformHelperImpl.dinnerDiscountTicks();
   }

   @ExpectPlatform
   @Transformed
   public static boolean discountOnlyHost() {
      return PlatformHelperImpl.discountOnlyHost();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableChefSetBonus() {
      return PlatformHelperImpl.enableChefSetBonus();
   }

   @ExpectPlatform
   @Transformed
   public static double chefSetExtraPortionChance() {
      return PlatformHelperImpl.chefSetExtraPortionChance();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableElegantSetBonus() {
      return PlatformHelperImpl.enableElegantSetBonus();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showSetBonusTooltips() {
      return PlatformHelperImpl.showSetBonusTooltips();
   }

   @ExpectPlatform
   @Transformed
   public static double zombieCookingHatChance() {
      return PlatformHelperImpl.zombieCookingHatChance();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showTableSetInfo() {
      return PlatformHelperImpl.showTableSetInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showSideTableInfo() {
      return PlatformHelperImpl.showSideTableInfo();
   }

   @ExpectPlatform
   @Transformed
   public static boolean showTableSignInfo() {
      return PlatformHelperImpl.showTableSignInfo();
   }

   @ExpectPlatform
   @Transformed
   public static int wellServedFoodFloor() {
      return PlatformHelperImpl.wellServedFoodFloor();
   }

   @ExpectPlatform
   @Transformed
   public static int refreshedMaxCharges() {
      return PlatformHelperImpl.refreshedMaxCharges();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableRingLuck() {
      return PlatformHelperImpl.enableRingLuck();
   }

   @ExpectPlatform
   @Transformed
   public static int ringLuckAmplifier() {
      return PlatformHelperImpl.ringLuckAmplifier();
   }

   @ExpectPlatform
   @Transformed
   public static boolean bannerGiveEffect() {
      return PlatformHelperImpl.bannerGiveEffect();
   }

   @ExpectPlatform
   @Transformed
   public static int bannerEffectRadius() {
      return PlatformHelperImpl.bannerEffectRadius();
   }

   @ExpectPlatform
   @Transformed
   public static int bannerEffectAmplifier() {
      return PlatformHelperImpl.bannerEffectAmplifier();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableNapkinSound() {
      return PlatformHelperImpl.enableNapkinSound();
   }

   @ExpectPlatform
   @Transformed
   public static boolean enableDishSteam() {
      return PlatformHelperImpl.enableDishSteam();
   }

   @ExpectPlatform
   @Transformed
   public static int dishSteamTicks() {
      return PlatformHelperImpl.dishSteamTicks();
   }

   @ExpectPlatform
   @Transformed
   public static int tomatoSoupNutrition() {
      return PlatformHelperImpl.tomatoSoupNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double tomatoSoupSaturation() {
      return PlatformHelperImpl.tomatoSoupSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int mushroomSoupNutrition() {
      return PlatformHelperImpl.mushroomSoupNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double mushroomSoupSaturation() {
      return PlatformHelperImpl.mushroomSoupSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int pastaWithMozzarellaNutrition() {
      return PlatformHelperImpl.pastaWithMozzarellaNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double pastaWithMozzarellaSaturation() {
      return PlatformHelperImpl.pastaWithMozzarellaSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int bologneseNutrition() {
      return PlatformHelperImpl.bologneseNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double bologneseSaturation() {
      return PlatformHelperImpl.bologneseSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int beefWithMushroomInWineAndPotatoesNutrition() {
      return PlatformHelperImpl.beefWithMushroomInWineAndPotatoesNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double beefWithMushroomInWineAndPotatoesSaturation() {
      return PlatformHelperImpl.beefWithMushroomInWineAndPotatoesSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int pastaWithBologneseNutrition() {
      return PlatformHelperImpl.pastaWithBologneseNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double pastaWithBologneseSaturation() {
      return PlatformHelperImpl.pastaWithBologneseSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int roastbeefWithGlazedCarrotsNutrition() {
      return PlatformHelperImpl.roastbeefWithGlazedCarrotsNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double roastbeefWithGlazedCarrotsSaturation() {
      return PlatformHelperImpl.roastbeefWithGlazedCarrotsSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int roastedLambWithLettuceNutrition() {
      return PlatformHelperImpl.roastedLambWithLettuceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double roastedLambWithLettuceSaturation() {
      return PlatformHelperImpl.roastedLambWithLettuceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int filletSteakNutrition() {
      return PlatformHelperImpl.filletSteakNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double filletSteakSaturation() {
      return PlatformHelperImpl.filletSteakSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int tropicalFishSupremeNutrition() {
      return PlatformHelperImpl.tropicalFishSupremeNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double tropicalFishSupremeSaturation() {
      return PlatformHelperImpl.tropicalFishSupremeSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int chickenAlfredoNutrition() {
      return PlatformHelperImpl.chickenAlfredoNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double chickenAlfredoSaturation() {
      return PlatformHelperImpl.chickenAlfredoSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int salmonOnWhiteWineNutrition() {
      return PlatformHelperImpl.salmonOnWhiteWineNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double salmonOnWhiteWineSaturation() {
      return PlatformHelperImpl.salmonOnWhiteWineSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int chickenWithVegetablesNutrition() {
      return PlatformHelperImpl.chickenWithVegetablesNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double chickenWithVegetablesSaturation() {
      return PlatformHelperImpl.chickenWithVegetablesSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int porkRibsNutrition() {
      return PlatformHelperImpl.porkRibsNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double porkRibsSaturation() {
      return PlatformHelperImpl.porkRibsSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int lasagneNutrition() {
      return PlatformHelperImpl.lasagneNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double lasagneSaturation() {
      return PlatformHelperImpl.lasagneSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int beefWellingtonNutrition() {
      return PlatformHelperImpl.beefWellingtonNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double beefWellingtonSaturation() {
      return PlatformHelperImpl.beefWellingtonSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int mozzarellaNutrition() {
      return PlatformHelperImpl.mozzarellaNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double mozzarellaSaturation() {
      return PlatformHelperImpl.mozzarellaSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int potroastNutrition() {
      return PlatformHelperImpl.potroastNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double potroastSaturation() {
      return PlatformHelperImpl.potroastSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int beetrootSaladNutrition() {
      return PlatformHelperImpl.beetrootSaladNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double beetrootSaladSaturation() {
      return PlatformHelperImpl.beetrootSaladSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int beetrootSaladEffectDuration() {
      return PlatformHelperImpl.beetrootSaladEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int chickenTeriyakiNutrition() {
      return PlatformHelperImpl.chickenTeriyakiNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double chickenTeriyakiSaturation() {
      return PlatformHelperImpl.chickenTeriyakiSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int chickenTeriyakiEffectDuration() {
      return PlatformHelperImpl.chickenTeriyakiEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int saladNutrition() {
      return PlatformHelperImpl.saladNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double saladSaturation() {
      return PlatformHelperImpl.saladSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int saladEffectDuration() {
      return PlatformHelperImpl.saladEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int beefTartareNutrition() {
      return PlatformHelperImpl.beefTartareNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double beefTartareSaturation() {
      return PlatformHelperImpl.beefTartareSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int beefTartareEffectDuration() {
      return PlatformHelperImpl.beefTartareEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int pastaWithLettuceNutrition() {
      return PlatformHelperImpl.pastaWithLettuceNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double pastaWithLettuceSaturation() {
      return PlatformHelperImpl.pastaWithLettuceSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int pastaWithLettuceEffectDuration() {
      return PlatformHelperImpl.pastaWithLettuceEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int omeletNutrition() {
      return PlatformHelperImpl.omeletNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double omeletSaturation() {
      return PlatformHelperImpl.omeletSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int omeletEffectDuration() {
      return PlatformHelperImpl.omeletEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int harvestPlateNutrition() {
      return PlatformHelperImpl.harvestPlateNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double harvestPlateSaturation() {
      return PlatformHelperImpl.harvestPlateSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int harvestPlateEffectDuration() {
      return PlatformHelperImpl.harvestPlateEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int chocolateMousseNutrition() {
      return PlatformHelperImpl.chocolateMousseNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double chocolateMousseSaturation() {
      return PlatformHelperImpl.chocolateMousseSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int chocolateMousseEffectDuration() {
      return PlatformHelperImpl.chocolateMousseEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int freshGardenSaladNutrition() {
      return PlatformHelperImpl.freshGardenSaladNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double freshGardenSaladSaturation() {
      return PlatformHelperImpl.freshGardenSaladSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int freshGardenSaladEffectDuration() {
      return PlatformHelperImpl.freshGardenSaladEffectDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int tomatoMozzarellaSaladNutrition() {
      return PlatformHelperImpl.tomatoMozzarellaSaladNutrition();
   }

   @ExpectPlatform
   @Transformed
   public static double tomatoMozzarellaSaladSaturation() {
      return PlatformHelperImpl.tomatoMozzarellaSaladSaturation();
   }

   @ExpectPlatform
   @Transformed
   public static int tomatoMozzarellaSaladEffectDuration() {
      return PlatformHelperImpl.tomatoMozzarellaSaladEffectDuration();
   }
}
