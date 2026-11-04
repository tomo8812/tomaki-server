package net.satisfy.farm_and_charm.neoforge.core.config;

import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class FarmAndCharmNeoForgeConfig {
   public static final ModConfigSpec COMMON_CONFIG;
   public static final BooleanValue ENABLE_RAIN_GROWTH_EFFECT;
   public static final BooleanValue ENABLE_FIREFLIES;
   public static final DoubleValue RAIN_GROWTH_MULTIPLIER;
   public static final IntValue BIG_CROP_CHANCE;
   public static final BooleanValue ENABLE_BONEMEAL_EFFECT;
   public static final BooleanValue ENABLE_FERTILIZER;
   public static final IntValue FERTILIZED_SOIL_RANGE;
   public static final IntValue SCARECROW_GROWTH_INTERVAL;
   public static final IntValue SCARECROW_RANGE;
   public static final IntValue WILD_CROP_DROP_CHANCE;
   public static final IntValue WATER_SPRINKLER_RANGE;
   public static final IntValue WELL_GROUNDWATER_DEPTH;
   public static final IntValue WELL_RAIN_FILL_CHANCE;
   public static final IntValue FEEDING_TROUGH_RANGE;
   public static final BooleanValue ENABLE_DOG_FOOD_TAMING;
   public static final BooleanValue ENABLE_CAT_TAMING_CHANCE;
   public static final BooleanValue ENABLE_HORSE_TAMING;
   public static final BooleanValue ENABLE_HORSE_EFFECTS;
   public static final BooleanValue ENABLE_CHICKEN_EFFECTS;
   public static final IntValue CHICKEN_COOP_MAX_CHICKENS;
   public static final IntValue CHICKEN_COOP_MAX_EGGS;
   public static final BooleanValue ENABLE_DOG_BEGGING;
   public static final BooleanValue ENABLE_CAT_BEGGING;
   public static final IntValue PET_BOWL_SEARCH_RANGE;
   public static final IntValue FEEDING_TIME_MIDDAY_START;
   public static final IntValue FEEDING_TIME_MIDDAY_END;
   public static final IntValue FEEDING_TIME_EVENING_START;
   public static final IntValue FEEDING_TIME_EVENING_END;
   public static final IntValue IRON_CLEAVER_CHOPS;
   public static final IntValue DIAMOND_CLEAVER_CHOPS;
   public static final IntValue NETHERITE_CLEAVER_CHOPS;
   public static final BooleanValue CLEAVER_HEAD_DROPS;
   public static final BooleanValue COOKING_EXPERIENCE;
   public static final DoubleValue COOKING_EXPERIENCE_MULTIPLIER;
   public static final BooleanValue ANIMATIONS;
   public static final BooleanValue SHOW_CUTTING_BOARD_INFO;
   public static final BooleanValue SHOW_MINCER_INFO;
   public static final BooleanValue SHOW_CRAFTING_BOWL_INFO;
   public static final BooleanValue SHOW_SILO_INFO;
   public static final BooleanValue SHOW_WATER_SPRINKLER_INFO;
   public static final BooleanValue SHOW_SCARECROW_INFO;
   public static final BooleanValue INFO_TOOLTIPS_NEED_DUNGAREES;
   public static final IntValue CHICKEN_EFFECT_TICK_INTERVAL;
   public static final IntValue CHICKEN_EFFECT_EGG_CHANCE;
   public static final IntValue CHICKEN_EFFECT_FEATHER_CHANCE;
   public static final IntValue FEAST_EFFECT_SATIATION_INTERVAL;
   public static final IntValue FEAST_EFFECT_SUSTENANCE_INTERVAL;
   public static final IntValue FEAST_EFFECT_HEAL_AMOUNT;
   public static final IntValue SUSTENANCE_EFFECT_INTERVAL;
   public static final IntValue SUSTENANCE_EFFECT_HEAL_AMOUNT;
   public static final IntValue SUSTENANCE_EFFECT_FOOD_INCREMENT;
   public static final IntValue SATIATION_EFFECT_INTERVAL;
   public static final IntValue SATIATION_EFFECT_HEAL_AMOUNT;
   public static final IntValue OAT_PANCAKE_NUTRITION;
   public static final DoubleValue OAT_PANCAKE_SATURATION;
   public static final IntValue ROASTED_CORN_NUTRITION;
   public static final DoubleValue ROASTED_CORN_SATURATION;
   public static final IntValue POTATO_WITH_ROAST_MEAT_NUTRITION;
   public static final DoubleValue POTATO_WITH_ROAST_MEAT_SATURATION;
   public static final IntValue BAKED_LAMB_HAM_NUTRITION;
   public static final DoubleValue BAKED_LAMB_HAM_SATURATION;
   public static final IntValue FARMERS_BREAKFAST_NUTRITION;
   public static final DoubleValue FARMERS_BREAKFAST_SATURATION;
   public static final IntValue STUFFED_CHICKEN_NUTRITION;
   public static final DoubleValue STUFFED_CHICKEN_SATURATION;
   public static final IntValue STUFFED_RABBIT_NUTRITION;
   public static final DoubleValue STUFFED_RABBIT_SATURATION;
   public static final IntValue GRANDMOTHERS_STRAWBERRY_CAKE_NUTRITION;
   public static final DoubleValue GRANDMOTHERS_STRAWBERRY_CAKE_SATURATION;
   public static final IntValue FARMERS_BREAD_NUTRITION;
   public static final DoubleValue FARMERS_BREAD_SATURATION;
   public static final IntValue FARMER_SALAD_NUTRITION;
   public static final DoubleValue FARMER_SALAD_SATURATION;
   public static final IntValue GOULASH_NUTRITION;
   public static final DoubleValue GOULASH_SATURATION;
   public static final IntValue SIMPLE_TOMATO_SOUP_NUTRITION;
   public static final DoubleValue SIMPLE_TOMATO_SOUP_SATURATION;
   public static final IntValue BARLEY_SOUP_NUTRITION;
   public static final DoubleValue BARLEY_SOUP_SATURATION;
   public static final IntValue ONION_SOUP_NUTRITION;
   public static final DoubleValue ONION_SOUP_SATURATION;
   public static final IntValue POTATO_SOUP_NUTRITION;
   public static final DoubleValue POTATO_SOUP_SATURATION;
   public static final IntValue PASTA_WITH_ONION_SAUCE_NUTRITION;
   public static final DoubleValue PASTA_WITH_ONION_SAUCE_SATURATION;
   public static final IntValue CORN_GRITS_NUTRITION;
   public static final DoubleValue CORN_GRITS_SATURATION;
   public static final IntValue OATMEAL_WITH_STRAWBERRIES_NUTRITION;
   public static final DoubleValue OATMEAL_WITH_STRAWBERRIES_SATURATION;
   public static final IntValue SAUSAGE_WITH_OAT_PATTY_NUTRITION;
   public static final DoubleValue SAUSAGE_WITH_OAT_PATTY_SATURATION;
   public static final IntValue LAMB_WITH_CORN_NUTRITION;
   public static final DoubleValue LAMB_WITH_CORN_SATURATION;
   public static final IntValue BEEF_PATTY_WITH_VEGETABLES_NUTRITION;
   public static final DoubleValue BEEF_PATTY_WITH_VEGETABLES_SATURATION;
   public static final IntValue BARLEY_PATTIES_WITH_POTATOES_NUTRITION;
   public static final DoubleValue BARLEY_PATTIES_WITH_POTATOES_SATURATION;
   public static final IntValue BACON_WITH_EGGS_NUTRITION;
   public static final DoubleValue BACON_WITH_EGGS_SATURATION;
   public static final IntValue CHICKEN_WRAPPED_IN_BACON_NUTRITION;
   public static final DoubleValue CHICKEN_WRAPPED_IN_BACON_SATURATION;
   public static final IntValue COOKED_SALMON_NUTRITION;
   public static final DoubleValue COOKED_SALMON_SATURATION;
   public static final IntValue COOKED_COD_NUTRITION;
   public static final DoubleValue COOKED_COD_SATURATION;
   public static final IntValue ROASTED_CHICKEN_NUTRITION;
   public static final DoubleValue ROASTED_CHICKEN_SATURATION;
   public static boolean enableRainGrowthEffect = true;
   public static boolean enableFireflies = true;
   public static double rainGrowthMultiplier = 0.5;
   public static int bigCropChance = 3;
   public static boolean enableBonemealEffect = true;
   public static boolean enableFertilizer = true;
   public static int fertilizedSoilRange = 5;
   public static int scarecrowGrowthInterval = 25;
   public static int scarecrowRange = 8;
   public static int wildCropDropChance = 60;
   public static int waterSprinklerRange = 8;
   public static int wellGroundwaterDepth = 6;
   public static int wellRainFillChance = 17;
   public static int feedingTroughRange = 8;
   public static boolean enableDogFoodTaming = true;
   public static boolean enableCatTamingChance = true;
   public static boolean enableHorseTaming = true;
   public static boolean enableHorseEffects = true;
   public static boolean enableChickenEffects = true;
   public static int chickenCoopMaxChickens = 3;
   public static int chickenCoopMaxEggs = 9;
   public static boolean enableDogBegging = true;
   public static boolean enableCatBegging = true;
   public static int petBowlSearchRange = 16;
   public static int feedingTimeMiddayStart = 5800;
   public static int feedingTimeMiddayEnd = 6200;
   public static int feedingTimeEveningStart = 11500;
   public static int feedingTimeEveningEnd = 12500;
   public static int ironCleaverChops = 5;
   public static int diamondCleaverChops = 3;
   public static int netheriteCleaverChops = 1;
   public static boolean cleaverHeadDrops = true;
   public static boolean cookingExperience = true;
   public static float cookingExperienceMultiplier = 1.0F;
   public static boolean animations = true;
   public static boolean showCuttingBoardInfo = true;
   public static boolean showMincerInfo = true;
   public static boolean showCraftingBowlInfo = true;
   public static boolean showSiloInfo = true;
   public static boolean showWaterSprinklerInfo = true;
   public static boolean showScarecrowInfo = true;
   public static boolean infoTooltipsNeedDungarees = false;
   public static int chickenEffectTickInterval = 120;
   public static int chickenEffectEggChance = 20;
   public static int chickenEffectFeatherChance = 20;
   public static int feastEffectSatiationInterval = 40;
   public static int feastEffectSustenanceInterval = 200;
   public static int feastEffectHealAmount = 1;
   public static int sustenanceEffectInterval = 200;
   public static int sustenanceEffectHealAmount = 1;
   public static int sustenanceEffectFoodIncrement = 1;
   public static int satiationEffectInterval = 40;
   public static int satiationEffectHealAmount = 1;
   public static int oatPancakeNutrition = 5;
   public static double oatPancakeSaturation = 0.6;
   public static int roastedCornNutrition = 6;
   public static double roastedCornSaturation = 0.5;
   public static int potatoWithRoastMeatNutrition = 4;
   public static double potatoWithRoastMeatSaturation = 0.6;
   public static int bakedLambHamNutrition = 5;
   public static double bakedLambHamSaturation = 0.8;
   public static int farmersBreakfastNutrition = 7;
   public static double farmersBreakfastSaturation = 0.8;
   public static int stuffedChickenNutrition = 5;
   public static double stuffedChickenSaturation = 0.7;
   public static int stuffedRabbitNutrition = 5;
   public static double stuffedRabbitSaturation = 0.7;
   public static int grandmothersStrawberryCakeNutrition = 4;
   public static double grandmothersStrawberryCakeSaturation = 0.5;
   public static int farmersBreadNutrition = 3;
   public static double farmersBreadSaturation = 0.6;
   public static int farmerSaladNutrition = 6;
   public static double farmerSaladSaturation = 0.5;
   public static int goulashNutrition = 14;
   public static double goulashSaturation = 0.8;
   public static int simpleTomatoSoupNutrition = 7;
   public static double simpleTomatoSoupSaturation = 0.6;
   public static int barleySoupNutrition = 7;
   public static double barleySoupSaturation = 0.65;
   public static int onionSoupNutrition = 9;
   public static double onionSoupSaturation = 0.5;
   public static int potatoSoupNutrition = 8;
   public static double potatoSoupSaturation = 0.55;
   public static int pastaWithOnionSauceNutrition = 5;
   public static double pastaWithOnionSauceSaturation = 0.6;
   public static int cornGritsNutrition = 6;
   public static double cornGritsSaturation = 0.5;
   public static int oatmealWithStrawberriesNutrition = 5;
   public static double oatmealWithStrawberriesSaturation = 0.5;
   public static int sausageWithOatPattyNutrition = 13;
   public static double sausageWithOatPattySaturation = 0.9;
   public static int lambWithCornNutrition = 12;
   public static double lambWithCornSaturation = 0.8;
   public static int beefPattyWithVegetablesNutrition = 11;
   public static double beefPattyWithVegetablesSaturation = 0.75;
   public static int barleyPattiesWithPotatoesNutrition = 10;
   public static double barleyPattiesWithPotatoesSaturation = 0.65;
   public static int baconWithEggsNutrition = 6;
   public static double baconWithEggsSaturation = 0.75;
   public static int chickenWrappedInBaconNutrition = 12;
   public static double chickenWrappedInBaconSaturation = 0.7;
   public static int cookedSalmonNutrition = 12;
   public static double cookedSalmonSaturation = 0.8;
   public static int cookedCodNutrition = 11;
   public static double cookedCodSaturation = 0.75;
   public static int roastedChickenNutrition = 5;
   public static double roastedChickenSaturation = 0.6;

   public static void onLoad(Loading event) {
      sync();
   }

   public static void onReload(Reloading event) {
      sync();
   }

   public static void sync() {
      enableRainGrowthEffect = (Boolean)ENABLE_RAIN_GROWTH_EFFECT.get();
      enableFireflies = (Boolean)ENABLE_FIREFLIES.get();
      rainGrowthMultiplier = (Double)RAIN_GROWTH_MULTIPLIER.get();
      bigCropChance = (Integer)BIG_CROP_CHANCE.get();
      enableBonemealEffect = (Boolean)ENABLE_BONEMEAL_EFFECT.get();
      enableFertilizer = (Boolean)ENABLE_FERTILIZER.get();
      fertilizedSoilRange = (Integer)FERTILIZED_SOIL_RANGE.get();
      scarecrowGrowthInterval = (Integer)SCARECROW_GROWTH_INTERVAL.get();
      scarecrowRange = (Integer)SCARECROW_RANGE.get();
      wildCropDropChance = (Integer)WILD_CROP_DROP_CHANCE.get();
      waterSprinklerRange = (Integer)WATER_SPRINKLER_RANGE.get();
      wellGroundwaterDepth = (Integer)WELL_GROUNDWATER_DEPTH.get();
      wellRainFillChance = (Integer)WELL_RAIN_FILL_CHANCE.get();
      feedingTroughRange = (Integer)FEEDING_TROUGH_RANGE.get();
      enableDogFoodTaming = (Boolean)ENABLE_DOG_FOOD_TAMING.get();
      enableCatTamingChance = (Boolean)ENABLE_CAT_TAMING_CHANCE.get();
      enableHorseTaming = (Boolean)ENABLE_HORSE_TAMING.get();
      enableHorseEffects = (Boolean)ENABLE_HORSE_EFFECTS.get();
      enableChickenEffects = (Boolean)ENABLE_CHICKEN_EFFECTS.get();
      chickenCoopMaxChickens = (Integer)CHICKEN_COOP_MAX_CHICKENS.get();
      chickenCoopMaxEggs = (Integer)CHICKEN_COOP_MAX_EGGS.get();
      enableDogBegging = (Boolean)ENABLE_DOG_BEGGING.get();
      enableCatBegging = (Boolean)ENABLE_CAT_BEGGING.get();
      petBowlSearchRange = (Integer)PET_BOWL_SEARCH_RANGE.get();
      feedingTimeMiddayStart = (Integer)FEEDING_TIME_MIDDAY_START.get();
      feedingTimeMiddayEnd = (Integer)FEEDING_TIME_MIDDAY_END.get();
      feedingTimeEveningStart = (Integer)FEEDING_TIME_EVENING_START.get();
      feedingTimeEveningEnd = (Integer)FEEDING_TIME_EVENING_END.get();
      ironCleaverChops = (Integer)IRON_CLEAVER_CHOPS.get();
      diamondCleaverChops = (Integer)DIAMOND_CLEAVER_CHOPS.get();
      netheriteCleaverChops = (Integer)NETHERITE_CLEAVER_CHOPS.get();
      cleaverHeadDrops = (Boolean)CLEAVER_HEAD_DROPS.get();
      cookingExperience = (Boolean)COOKING_EXPERIENCE.get();
      cookingExperienceMultiplier = ((Double)COOKING_EXPERIENCE_MULTIPLIER.get()).floatValue();
      animations = (Boolean)ANIMATIONS.get();
      showCuttingBoardInfo = (Boolean)SHOW_CUTTING_BOARD_INFO.get();
      showMincerInfo = (Boolean)SHOW_MINCER_INFO.get();
      showCraftingBowlInfo = (Boolean)SHOW_CRAFTING_BOWL_INFO.get();
      showSiloInfo = (Boolean)SHOW_SILO_INFO.get();
      showWaterSprinklerInfo = (Boolean)SHOW_WATER_SPRINKLER_INFO.get();
      showScarecrowInfo = (Boolean)SHOW_SCARECROW_INFO.get();
      infoTooltipsNeedDungarees = (Boolean)INFO_TOOLTIPS_NEED_DUNGAREES.get();
      chickenEffectTickInterval = (Integer)CHICKEN_EFFECT_TICK_INTERVAL.get();
      chickenEffectEggChance = (Integer)CHICKEN_EFFECT_EGG_CHANCE.get();
      chickenEffectFeatherChance = (Integer)CHICKEN_EFFECT_FEATHER_CHANCE.get();
      feastEffectSatiationInterval = (Integer)FEAST_EFFECT_SATIATION_INTERVAL.get();
      feastEffectSustenanceInterval = (Integer)FEAST_EFFECT_SUSTENANCE_INTERVAL.get();
      feastEffectHealAmount = (Integer)FEAST_EFFECT_HEAL_AMOUNT.get();
      sustenanceEffectInterval = (Integer)SUSTENANCE_EFFECT_INTERVAL.get();
      sustenanceEffectHealAmount = (Integer)SUSTENANCE_EFFECT_HEAL_AMOUNT.get();
      sustenanceEffectFoodIncrement = (Integer)SUSTENANCE_EFFECT_FOOD_INCREMENT.get();
      satiationEffectInterval = (Integer)SATIATION_EFFECT_INTERVAL.get();
      satiationEffectHealAmount = (Integer)SATIATION_EFFECT_HEAL_AMOUNT.get();
      oatPancakeNutrition = (Integer)OAT_PANCAKE_NUTRITION.get();
      oatPancakeSaturation = (Double)OAT_PANCAKE_SATURATION.get();
      roastedCornNutrition = (Integer)ROASTED_CORN_NUTRITION.get();
      roastedCornSaturation = (Double)ROASTED_CORN_SATURATION.get();
      potatoWithRoastMeatNutrition = (Integer)POTATO_WITH_ROAST_MEAT_NUTRITION.get();
      potatoWithRoastMeatSaturation = (Double)POTATO_WITH_ROAST_MEAT_SATURATION.get();
      bakedLambHamNutrition = (Integer)BAKED_LAMB_HAM_NUTRITION.get();
      bakedLambHamSaturation = (Double)BAKED_LAMB_HAM_SATURATION.get();
      farmersBreakfastNutrition = (Integer)FARMERS_BREAKFAST_NUTRITION.get();
      farmersBreakfastSaturation = (Double)FARMERS_BREAKFAST_SATURATION.get();
      stuffedChickenNutrition = (Integer)STUFFED_CHICKEN_NUTRITION.get();
      stuffedChickenSaturation = (Double)STUFFED_CHICKEN_SATURATION.get();
      stuffedRabbitNutrition = (Integer)STUFFED_RABBIT_NUTRITION.get();
      stuffedRabbitSaturation = (Double)STUFFED_RABBIT_SATURATION.get();
      grandmothersStrawberryCakeNutrition = (Integer)GRANDMOTHERS_STRAWBERRY_CAKE_NUTRITION.get();
      grandmothersStrawberryCakeSaturation = (Double)GRANDMOTHERS_STRAWBERRY_CAKE_SATURATION.get();
      farmersBreadNutrition = (Integer)FARMERS_BREAD_NUTRITION.get();
      farmersBreadSaturation = (Double)FARMERS_BREAD_SATURATION.get();
      farmerSaladNutrition = (Integer)FARMER_SALAD_NUTRITION.get();
      farmerSaladSaturation = (Double)FARMER_SALAD_SATURATION.get();
      goulashNutrition = (Integer)GOULASH_NUTRITION.get();
      goulashSaturation = (Double)GOULASH_SATURATION.get();
      simpleTomatoSoupNutrition = (Integer)SIMPLE_TOMATO_SOUP_NUTRITION.get();
      simpleTomatoSoupSaturation = (Double)SIMPLE_TOMATO_SOUP_SATURATION.get();
      barleySoupNutrition = (Integer)BARLEY_SOUP_NUTRITION.get();
      barleySoupSaturation = (Double)BARLEY_SOUP_SATURATION.get();
      onionSoupNutrition = (Integer)ONION_SOUP_NUTRITION.get();
      onionSoupSaturation = (Double)ONION_SOUP_SATURATION.get();
      potatoSoupNutrition = (Integer)POTATO_SOUP_NUTRITION.get();
      potatoSoupSaturation = (Double)POTATO_SOUP_SATURATION.get();
      pastaWithOnionSauceNutrition = (Integer)PASTA_WITH_ONION_SAUCE_NUTRITION.get();
      pastaWithOnionSauceSaturation = (Double)PASTA_WITH_ONION_SAUCE_SATURATION.get();
      cornGritsNutrition = (Integer)CORN_GRITS_NUTRITION.get();
      cornGritsSaturation = (Double)CORN_GRITS_SATURATION.get();
      oatmealWithStrawberriesNutrition = (Integer)OATMEAL_WITH_STRAWBERRIES_NUTRITION.get();
      oatmealWithStrawberriesSaturation = (Double)OATMEAL_WITH_STRAWBERRIES_SATURATION.get();
      sausageWithOatPattyNutrition = (Integer)SAUSAGE_WITH_OAT_PATTY_NUTRITION.get();
      sausageWithOatPattySaturation = (Double)SAUSAGE_WITH_OAT_PATTY_SATURATION.get();
      lambWithCornNutrition = (Integer)LAMB_WITH_CORN_NUTRITION.get();
      lambWithCornSaturation = (Double)LAMB_WITH_CORN_SATURATION.get();
      beefPattyWithVegetablesNutrition = (Integer)BEEF_PATTY_WITH_VEGETABLES_NUTRITION.get();
      beefPattyWithVegetablesSaturation = (Double)BEEF_PATTY_WITH_VEGETABLES_SATURATION.get();
      barleyPattiesWithPotatoesNutrition = (Integer)BARLEY_PATTIES_WITH_POTATOES_NUTRITION.get();
      barleyPattiesWithPotatoesSaturation = (Double)BARLEY_PATTIES_WITH_POTATOES_SATURATION.get();
      baconWithEggsNutrition = (Integer)BACON_WITH_EGGS_NUTRITION.get();
      baconWithEggsSaturation = (Double)BACON_WITH_EGGS_SATURATION.get();
      chickenWrappedInBaconNutrition = (Integer)CHICKEN_WRAPPED_IN_BACON_NUTRITION.get();
      chickenWrappedInBaconSaturation = (Double)CHICKEN_WRAPPED_IN_BACON_SATURATION.get();
      cookedSalmonNutrition = (Integer)COOKED_SALMON_NUTRITION.get();
      cookedSalmonSaturation = (Double)COOKED_SALMON_SATURATION.get();
      cookedCodNutrition = (Integer)COOKED_COD_NUTRITION.get();
      cookedCodSaturation = (Double)COOKED_COD_SATURATION.get();
      roastedChickenNutrition = (Integer)ROASTED_CHICKEN_NUTRITION.get();
      roastedChickenSaturation = (Double)ROASTED_CHICKEN_SATURATION.get();
   }

   static {
      Builder builder = new Builder();
      builder.comment("Everything about growing crops.").push("Farming");
      ENABLE_RAIN_GROWTH_EFFECT = builder.comment("Crops grow faster while it is raining on them.").define("enableRainGrowthEffect", true);
      ENABLE_FIREFLIES = builder.comment("Shows fireflies around lit lanterns outdoors at night when it is not raining.").define("enableFireflies", true);
      RAIN_GROWTH_MULTIPLIER = builder.comment("How much faster crops grow in rain. 0.5 = 50% faster, 1.0 = twice as fast.")
         .defineInRange("rainGrowthMultiplier", 0.5, 0.0, 2.0);
      BIG_CROP_CHANCE = builder.comment(
            "Chance in percent that a fully grown lettuce or onion grows into a big crop while rain falls on it in bright light. Only some crops can grow big."
         )
         .defineInRange("bigCropChance", 3, 0, 100);
      ENABLE_BONEMEAL_EFFECT = builder.comment("Fertilized farmland occasionally acts like bone meal on the crop above it.")
         .define("enableBonemealEffect", true);
      ENABLE_FERTILIZER = builder.comment("Fertilizer makes a few crops in a 5×5 area grow like bone meal does.").define("enableFertilizer", true);
      FERTILIZED_SOIL_RANGE = builder.comment("Radius in blocks around fertilized soil spread with a pitchfork in which plants can grow a stage.")
         .defineInRange("fertilizedSoilRange", 5, 1, 16);
      SCARECROW_GROWTH_INTERVAL = builder.comment("Every how many seconds a scarecrow helps the crops around it grow.")
         .defineInRange("scarecrowGrowthInterval", 25, 1, 600);
      SCARECROW_RANGE = builder.comment("Radius in blocks around the scarecrow.").defineInRange("scarecrowRange", 8, 1, 16);
      WILD_CROP_DROP_CHANCE = builder.comment("Chance in percent that bone meal on a wild crop drops another wild crop.")
         .defineInRange("wildCropDropChance", 60, 0, 100);
      builder.pop();
      builder.comment("Sprinklers and the timber well.").push("Water");
      WATER_SPRINKLER_RANGE = builder.comment("Radius in blocks that a water sprinkler keeps watered.").defineInRange("waterSprinklerRange", 8, 1, 16);
      WELL_GROUNDWATER_DEPTH = builder.comment("How many blocks below the well it searches for a water source.")
         .defineInRange("wellGroundwaterDepth", 6, 1, 32);
      WELL_RAIN_FILL_CHANCE = builder.comment("Chance in percent per random tick that rain fills the well a bit.")
         .defineInRange("wellRainFillChance", 17, 0, 100);
      builder.pop();
      builder.comment("Feeding, taming and animal behaviour.").push("Animals");
      FEEDING_TROUGH_RANGE = builder.comment("How far animals walk to eat from a feeding trough or drink from a water trough.")
         .defineInRange("feedingTroughRange", 8, 1, 16);
      ENABLE_DOG_FOOD_TAMING = builder.comment("Wolves can be tamed with dog food.").define("enableDogFoodTaming", true);
      ENABLE_CAT_TAMING_CHANCE = builder.comment("Cats can be tamed with cat food.").define("enableCatTamingChance", true);
      ENABLE_HORSE_TAMING = builder.comment("Horses can be tamed with horse fodder.").define("enableHorseTaming", true);
      ENABLE_HORSE_EFFECTS = builder.comment("Horse fodder heals horses and gives them a temporary boost.").define("enableHorseEffects", true);
      ENABLE_CHICKEN_EFFECTS = builder.comment("Chicken feed makes chickens lay eggs and drop feathers more often.").define("enableChickenEffects", true);
      CHICKEN_COOP_MAX_CHICKENS = builder.comment("How many chickens fit into one chicken coop.").defineInRange("chickenCoopMaxChickens", 3, 1, 16);
      CHICKEN_COOP_MAX_EGGS = builder.comment("How many eggs a chicken coop can hold before you collect them.").defineInRange("chickenCoopMaxEggs", 9, 1, 64);
      ENABLE_DOG_BEGGING = builder.comment("Tamed dogs sit at their empty pet bowl at feeding time and whine.").define("enableDogBegging", true);
      ENABLE_CAT_BEGGING = builder.comment("Tamed cats sit at their empty pet bowl at feeding time and meow.").define("enableCatBegging", true);
      PET_BOWL_SEARCH_RANGE = builder.comment("How far pets look for their bowl at feeding time. Higher values cost more performance.")
         .defineInRange("petBowlSearchRange", 16, 4, 32);
      FEEDING_TIME_MIDDAY_START = builder.comment(
            "When tamed pets start begging at their pet bowl around noon. Time of day in ticks. 0 = sunrise, 6000 = noon, 12000 = sunset, 18000 = midnight."
         )
         .defineInRange("feedingTimeMiddayStart", 5800, 0, 23999);
      FEEDING_TIME_MIDDAY_END = builder.comment(
            "When tamed pets stop begging at their pet bowl around noon. Time of day in ticks. 0 = sunrise, 6000 = noon, 12000 = sunset, 18000 = midnight."
         )
         .defineInRange("feedingTimeMiddayEnd", 6200, 0, 23999);
      FEEDING_TIME_EVENING_START = builder.comment(
            "When tamed pets start begging at their pet bowl in the evening. Time of day in ticks. 0 = sunrise, 6000 = noon, 12000 = sunset, 18000 = midnight."
         )
         .defineInRange("feedingTimeEveningStart", 11500, 0, 23999);
      FEEDING_TIME_EVENING_END = builder.comment(
            "When tamed pets stop begging at their pet bowl in the evening. Time of day in ticks. 0 = sunrise, 6000 = noon, 12000 = sunset, 18000 = midnight."
         )
         .defineInRange("feedingTimeEveningEnd", 12500, 0, 23999);
      builder.pop();
      builder.comment("Cutting board and cleavers.").push("Kitchen");
      IRON_CLEAVER_CHOPS = builder.comment("How many chops an iron cleaver needs per ingredient.").defineInRange("ironCleaverChops", 5, 1, 20);
      DIAMOND_CLEAVER_CHOPS = builder.comment("How many chops a diamond cleaver needs per ingredient.").defineInRange("diamondCleaverChops", 3, 1, 20);
      NETHERITE_CLEAVER_CHOPS = builder.comment("How many chops a netherite cleaver needs per ingredient.").defineInRange("netheriteCleaverChops", 1, 1, 20);
      CLEAVER_HEAD_DROPS = builder.comment("Mobs killed with a cleaver can drop their head.").define("cleaverHeadDrops", true);
      COOKING_EXPERIENCE = builder.comment(
            "Cooking, mincing, cutting, stripping and whisking give experience. The amount per recipe is set in the recipe files."
         )
         .define("cookingExperience", true);
      COOKING_EXPERIENCE_MULTIPLIER = builder.comment("Multiplies all experience from cooking and food processing. 1.0 = as set in the recipes.")
         .defineInRange("cookingExperienceMultiplier", 1.0, 0.0, 10.0);
      builder.pop();
      builder.comment("Info tooltips that appear when looking at a block. Only affects your own screen.").push("Info Tooltips");
      SHOW_CUTTING_BOARD_INFO = builder.comment("Shows an info tooltip when looking at the cutting board.").define("showCuttingBoardInfo", true);
      SHOW_MINCER_INFO = builder.comment("Shows an info tooltip when looking at the mincer.").define("showMincerInfo", true);
      SHOW_CRAFTING_BOWL_INFO = builder.comment("Shows an info tooltip when looking at the crafting bowl.").define("showCraftingBowlInfo", true);
      SHOW_SILO_INFO = builder.comment("Shows an info tooltip when looking at the silo.").define("showSiloInfo", true);
      SHOW_WATER_SPRINKLER_INFO = builder.comment("Shows an info tooltip when looking at the water sprinkler.").define("showWaterSprinklerInfo", true);
      SHOW_SCARECROW_INFO = builder.comment("Shows an info tooltip when looking at the scarecrow.").define("showScarecrowInfo", true);
      INFO_TOOLTIPS_NEED_DUNGAREES = builder.comment("Info tooltips only show while wearing Dungarees.").define("needDungarees", false);
      builder.pop();
      builder.comment("Fine-tuning of the food effects.").push("Effects");
      builder.push("Chicken Effect");
      CHICKEN_EFFECT_TICK_INTERVAL = builder.comment("Every how many ticks the effect tries to drop an egg or feather. 20 ticks = 1 second.")
         .defineInRange("chickenEffectTickInterval", 120, 10, 600);
      CHICKEN_EFFECT_EGG_CHANCE = builder.comment("Chance in percent to drop an egg.").defineInRange("chickenEffectEggChance", 20, 0, 100);
      CHICKEN_EFFECT_FEATHER_CHANCE = builder.comment("Chance in percent to drop a feather.").defineInRange("chickenEffectFeatherChance", 20, 0, 100);
      builder.pop();
      builder.push("Feast Effect");
      FEAST_EFFECT_SATIATION_INTERVAL = builder.comment("Every how many ticks you heal while full. 20 ticks = 1 second.")
         .defineInRange("feastEffectSatiationInterval", 40, 10, 200);
      FEAST_EFFECT_SUSTENANCE_INTERVAL = builder.comment("Every how many ticks one hunger point is restored. 20 ticks = 1 second.")
         .defineInRange("feastEffectSustenanceInterval", 200, 50, 600);
      FEAST_EFFECT_HEAL_AMOUNT = builder.comment("Health points healed each time. 2 points = 1 heart.").defineInRange("feastEffectHealAmount", 1, 1, 5);
      builder.pop();
      builder.push("Sustenance Effect");
      SUSTENANCE_EFFECT_INTERVAL = builder.comment("Every how many ticks the effect kicks in. 20 ticks = 1 second.")
         .defineInRange("sustenanceEffectInterval", 200, 50, 600);
      SUSTENANCE_EFFECT_HEAL_AMOUNT = builder.comment("Health points healed when your hunger bar is full. 2 points = 1 heart.")
         .defineInRange("sustenanceEffectHealAmount", 1, 1, 5);
      SUSTENANCE_EFFECT_FOOD_INCREMENT = builder.comment("Hunger points restored when you are not full.")
         .defineInRange("sustenanceEffectFoodIncrement", 1, 1, 20);
      builder.pop();
      builder.push("Satiation Effect");
      SATIATION_EFFECT_INTERVAL = builder.comment("Every how many ticks you heal while full. 20 ticks = 1 second.")
         .defineInRange("satiationEffectInterval", 40, 10, 200);
      SATIATION_EFFECT_HEAL_AMOUNT = builder.comment("Health points healed each time. 2 points = 1 heart.").defineInRange("satiationEffectHealAmount", 1, 1, 5);
      builder.pop();
      builder.pop();
      builder.comment("Block animations.").push("Misc");
      ANIMATIONS = builder.comment("Blocks of this mod are animated.").define("animations", true);
      builder.pop();
      builder.comment("Hunger and saturation of the food items. Changes need a game restart.").push("Food");
      OAT_PANCAKE_NUTRITION = builder.comment("Hunger points restored by Oat Pancake. 2 points = 1 drumstick.").defineInRange("oatPancakeNutrition", 5, 0, 20);
      OAT_PANCAKE_SATURATION = builder.comment("Saturation given by Oat Pancake, multiplied with its hunger points.")
         .defineInRange("oatPancakeSaturation", 0.6, 0.0, 2.0);
      ROASTED_CORN_NUTRITION = builder.comment("Hunger points restored by Roasted Corn. 2 points = 1 drumstick.")
         .defineInRange("roastedCornNutrition", 6, 0, 20);
      ROASTED_CORN_SATURATION = builder.comment("Saturation given by Roasted Corn, multiplied with its hunger points.")
         .defineInRange("roastedCornSaturation", 0.5, 0.0, 2.0);
      POTATO_WITH_ROAST_MEAT_NUTRITION = builder.comment("Hunger points restored by Potato with Roast Meat. 2 points = 1 drumstick.")
         .defineInRange("potatoWithRoastMeatNutrition", 4, 0, 20);
      POTATO_WITH_ROAST_MEAT_SATURATION = builder.comment("Saturation given by Potato with Roast Meat, multiplied with its hunger points.")
         .defineInRange("potatoWithRoastMeatSaturation", 0.6, 0.0, 2.0);
      BAKED_LAMB_HAM_NUTRITION = builder.comment("Hunger points restored by Baked Lamb Ham. 2 points = 1 drumstick.")
         .defineInRange("bakedLambHamNutrition", 5, 0, 20);
      BAKED_LAMB_HAM_SATURATION = builder.comment("Saturation given by Baked Lamb Ham, multiplied with its hunger points.")
         .defineInRange("bakedLambHamSaturation", 0.8, 0.0, 2.0);
      FARMERS_BREAKFAST_NUTRITION = builder.comment("Hunger points restored by Farmer's Breakfast. 2 points = 1 drumstick.")
         .defineInRange("farmersBreakfastNutrition", 7, 0, 20);
      FARMERS_BREAKFAST_SATURATION = builder.comment("Saturation given by Farmer's Breakfast, multiplied with its hunger points.")
         .defineInRange("farmersBreakfastSaturation", 0.8, 0.0, 2.0);
      STUFFED_CHICKEN_NUTRITION = builder.comment("Hunger points restored by Stuffed Chicken. 2 points = 1 drumstick.")
         .defineInRange("stuffedChickenNutrition", 5, 0, 20);
      STUFFED_CHICKEN_SATURATION = builder.comment("Saturation given by Stuffed Chicken, multiplied with its hunger points.")
         .defineInRange("stuffedChickenSaturation", 0.7, 0.0, 2.0);
      STUFFED_RABBIT_NUTRITION = builder.comment("Hunger points restored by Stuffed Rabbit. 2 points = 1 drumstick.")
         .defineInRange("stuffedRabbitNutrition", 5, 0, 20);
      STUFFED_RABBIT_SATURATION = builder.comment("Saturation given by Stuffed Rabbit, multiplied with its hunger points.")
         .defineInRange("stuffedRabbitSaturation", 0.7, 0.0, 2.0);
      GRANDMOTHERS_STRAWBERRY_CAKE_NUTRITION = builder.comment("Hunger points restored by Grandmother's Strawberry Cake. 2 points = 1 drumstick.")
         .defineInRange("grandmothersStrawberryCakeNutrition", 4, 0, 20);
      GRANDMOTHERS_STRAWBERRY_CAKE_SATURATION = builder.comment("Saturation given by Grandmother's Strawberry Cake, multiplied with its hunger points.")
         .defineInRange("grandmothersStrawberryCakeSaturation", 0.5, 0.0, 2.0);
      FARMERS_BREAD_NUTRITION = builder.comment("Hunger points restored by Farmer's Bread. 2 points = 1 drumstick.")
         .defineInRange("farmersBreadNutrition", 3, 0, 20);
      FARMERS_BREAD_SATURATION = builder.comment("Saturation given by Farmer's Bread, multiplied with its hunger points.")
         .defineInRange("farmersBreadSaturation", 0.6, 0.0, 2.0);
      FARMER_SALAD_NUTRITION = builder.comment("Hunger points restored by Farmer Salad. 2 points = 1 drumstick.")
         .defineInRange("farmerSaladNutrition", 6, 0, 20);
      FARMER_SALAD_SATURATION = builder.comment("Saturation given by Farmer Salad, multiplied with its hunger points.")
         .defineInRange("farmerSaladSaturation", 0.5, 0.0, 2.0);
      GOULASH_NUTRITION = builder.comment("Hunger points restored by Goulash. 2 points = 1 drumstick.").defineInRange("goulashNutrition", 14, 0, 20);
      GOULASH_SATURATION = builder.comment("Saturation given by Goulash, multiplied with its hunger points.").defineInRange("goulashSaturation", 0.8, 0.0, 2.0);
      SIMPLE_TOMATO_SOUP_NUTRITION = builder.comment("Hunger points restored by Simple Tomato Soup. 2 points = 1 drumstick.")
         .defineInRange("simpleTomatoSoupNutrition", 7, 0, 20);
      SIMPLE_TOMATO_SOUP_SATURATION = builder.comment("Saturation given by Simple Tomato Soup, multiplied with its hunger points.")
         .defineInRange("simpleTomatoSoupSaturation", 0.6, 0.0, 2.0);
      BARLEY_SOUP_NUTRITION = builder.comment("Hunger points restored by Barley Soup. 2 points = 1 drumstick.").defineInRange("barleySoupNutrition", 7, 0, 20);
      BARLEY_SOUP_SATURATION = builder.comment("Saturation given by Barley Soup, multiplied with its hunger points.")
         .defineInRange("barleySoupSaturation", 0.65, 0.0, 2.0);
      ONION_SOUP_NUTRITION = builder.comment("Hunger points restored by Onion Soup. 2 points = 1 drumstick.").defineInRange("onionSoupNutrition", 9, 0, 20);
      ONION_SOUP_SATURATION = builder.comment("Saturation given by Onion Soup, multiplied with its hunger points.")
         .defineInRange("onionSoupSaturation", 0.5, 0.0, 2.0);
      POTATO_SOUP_NUTRITION = builder.comment("Hunger points restored by Potato Soup. 2 points = 1 drumstick.").defineInRange("potatoSoupNutrition", 8, 0, 20);
      POTATO_SOUP_SATURATION = builder.comment("Saturation given by Potato Soup, multiplied with its hunger points.")
         .defineInRange("potatoSoupSaturation", 0.55, 0.0, 2.0);
      PASTA_WITH_ONION_SAUCE_NUTRITION = builder.comment("Hunger points restored by Pasta with Onion Sauce. 2 points = 1 drumstick.")
         .defineInRange("pastaWithOnionSauceNutrition", 5, 0, 20);
      PASTA_WITH_ONION_SAUCE_SATURATION = builder.comment("Saturation given by Pasta with Onion Sauce, multiplied with its hunger points.")
         .defineInRange("pastaWithOnionSauceSaturation", 0.6, 0.0, 2.0);
      CORN_GRITS_NUTRITION = builder.comment("Hunger points restored by Corn Grits. 2 points = 1 drumstick.").defineInRange("cornGritsNutrition", 6, 0, 20);
      CORN_GRITS_SATURATION = builder.comment("Saturation given by Corn Grits, multiplied with its hunger points.")
         .defineInRange("cornGritsSaturation", 0.5, 0.0, 2.0);
      OATMEAL_WITH_STRAWBERRIES_NUTRITION = builder.comment("Hunger points restored by Oatmeal with Strawberries. 2 points = 1 drumstick.")
         .defineInRange("oatmealWithStrawberriesNutrition", 5, 0, 20);
      OATMEAL_WITH_STRAWBERRIES_SATURATION = builder.comment("Saturation given by Oatmeal with Strawberries, multiplied with its hunger points.")
         .defineInRange("oatmealWithStrawberriesSaturation", 0.5, 0.0, 2.0);
      SAUSAGE_WITH_OAT_PATTY_NUTRITION = builder.comment("Hunger points restored by Sausage with Oat Patty. 2 points = 1 drumstick.")
         .defineInRange("sausageWithOatPattyNutrition", 13, 0, 20);
      SAUSAGE_WITH_OAT_PATTY_SATURATION = builder.comment("Saturation given by Sausage with Oat Patty, multiplied with its hunger points.")
         .defineInRange("sausageWithOatPattySaturation", 0.9, 0.0, 2.0);
      LAMB_WITH_CORN_NUTRITION = builder.comment("Hunger points restored by Lamb with Corn. 2 points = 1 drumstick.")
         .defineInRange("lambWithCornNutrition", 12, 0, 20);
      LAMB_WITH_CORN_SATURATION = builder.comment("Saturation given by Lamb with Corn, multiplied with its hunger points.")
         .defineInRange("lambWithCornSaturation", 0.8, 0.0, 2.0);
      BEEF_PATTY_WITH_VEGETABLES_NUTRITION = builder.comment("Hunger points restored by Beef Patty with Vegetables. 2 points = 1 drumstick.")
         .defineInRange("beefPattyWithVegetablesNutrition", 11, 0, 20);
      BEEF_PATTY_WITH_VEGETABLES_SATURATION = builder.comment("Saturation given by Beef Patty with Vegetables, multiplied with its hunger points.")
         .defineInRange("beefPattyWithVegetablesSaturation", 0.75, 0.0, 2.0);
      BARLEY_PATTIES_WITH_POTATOES_NUTRITION = builder.comment("Hunger points restored by Barley Patties with Potatoes. 2 points = 1 drumstick.")
         .defineInRange("barleyPattiesWithPotatoesNutrition", 10, 0, 20);
      BARLEY_PATTIES_WITH_POTATOES_SATURATION = builder.comment("Saturation given by Barley Patties with Potatoes, multiplied with its hunger points.")
         .defineInRange("barleyPattiesWithPotatoesSaturation", 0.65, 0.0, 2.0);
      BACON_WITH_EGGS_NUTRITION = builder.comment("Hunger points restored by Bacon with Eggs. 2 points = 1 drumstick.")
         .defineInRange("baconWithEggsNutrition", 6, 0, 20);
      BACON_WITH_EGGS_SATURATION = builder.comment("Saturation given by Bacon with Eggs, multiplied with its hunger points.")
         .defineInRange("baconWithEggsSaturation", 0.75, 0.0, 2.0);
      CHICKEN_WRAPPED_IN_BACON_NUTRITION = builder.comment("Hunger points restored by Chicken Wrapped in Bacon. 2 points = 1 drumstick.")
         .defineInRange("chickenWrappedInBaconNutrition", 12, 0, 20);
      CHICKEN_WRAPPED_IN_BACON_SATURATION = builder.comment("Saturation given by Chicken Wrapped in Bacon, multiplied with its hunger points.")
         .defineInRange("chickenWrappedInBaconSaturation", 0.7, 0.0, 2.0);
      COOKED_SALMON_NUTRITION = builder.comment("Hunger points restored by Cooked Salmon. 2 points = 1 drumstick.")
         .defineInRange("cookedSalmonNutrition", 12, 0, 20);
      COOKED_SALMON_SATURATION = builder.comment("Saturation given by Cooked Salmon, multiplied with its hunger points.")
         .defineInRange("cookedSalmonSaturation", 0.8, 0.0, 2.0);
      COOKED_COD_NUTRITION = builder.comment("Hunger points restored by Cooked Cod. 2 points = 1 drumstick.").defineInRange("cookedCodNutrition", 11, 0, 20);
      COOKED_COD_SATURATION = builder.comment("Saturation given by Cooked Cod, multiplied with its hunger points.")
         .defineInRange("cookedCodSaturation", 0.75, 0.0, 2.0);
      ROASTED_CHICKEN_NUTRITION = builder.comment("Hunger points restored by Roasted Chicken. 2 points = 1 drumstick.")
         .defineInRange("roastedChickenNutrition", 5, 0, 20);
      ROASTED_CHICKEN_SATURATION = builder.comment("Saturation given by Roasted Chicken, multiplied with its hunger points.")
         .defineInRange("roastedChickenSaturation", 0.6, 0.0, 2.0);
      builder.pop();
      COMMON_CONFIG = builder.build();
   }
}
