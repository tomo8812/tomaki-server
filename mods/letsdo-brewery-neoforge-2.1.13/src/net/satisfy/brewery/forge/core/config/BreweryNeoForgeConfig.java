package net.satisfy.brewery.forge.core.config;

import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class BreweryNeoForgeConfig {
   public static final ModConfigSpec COMMON_CONFIG;
   public static final IntValue BREW_TIME;
   public static final BooleanValue ENABLE_BREW_EVENTS;
   public static final IntValue MIN_BREW_EVENT_INTERVAL;
   public static final IntValue MAX_BREW_EVENT_INTERVAL;
   public static final BooleanValue ENABLE_BEER_ELEMENTALS;
   public static final BooleanValue ANIMATIONS;
   public static final BooleanValue ENABLE_DRUNKENNESS;
   public static final BooleanValue ENABLE_DRUNK_SWAY;
   public static final BooleanValue ENABLE_DRUNK_SLOWNESS;
   public static final BooleanValue ENABLE_BLACKOUT;
   public static final DoubleValue BLACKOUT_CHANCE;
   public static final BooleanValue ENABLE_BLACKOUT_TELEPORT;
   public static final IntValue BLACKOUT_TELEPORT_RANGE;
   public static final IntValue COMBUSTION_EFFECT_RADIUS;
   public static final IntValue COMBUSTION_EFFECT_IGNITE_CHANCE;
   public static final IntValue REPULSION_EFFECT_RADIUS;
   public static final DoubleValue REPULSION_EFFECT_STRENGTH;
   public static final IntValue STOUT_HEART_EFFECT_HEALTH_CAP;
   public static final DoubleValue STOUT_HEART_EFFECT_HEAL_AMOUNT;
   public static final IntValue MINING_EFFECT_HASTE_ONE_HEIGHT;
   public static final IntValue MINING_EFFECT_HASTE_TWO_HEIGHT;
   public static final IntValue MINING_EFFECT_HASTE_THREE_HEIGHT;
   public static final IntValue MINING_EFFECT_HASTE_FOUR_HEIGHT;
   public static final IntValue PACIFY_EFFECT_RANGE;
   public static final BooleanValue HALEY_EFFECT_FLIGHT;
   public static final BooleanValue SHOW_BREWINGSTATION_INFO;
   public static final BooleanValue SHOW_BIG_BARREL_INFO;
   public static final BooleanValue INFO_TOOLTIPS_NEED_DUNGAREES;
   public static final IntValue SAUSAGE_NUTRITION;
   public static final DoubleValue SAUSAGE_SATURATION;
   public static final IntValue PRETZEL_NUTRITION;
   public static final DoubleValue PRETZEL_SATURATION;
   public static final IntValue PORK_KNUCKLE_NUTRITION;
   public static final DoubleValue PORK_KNUCKLE_SATURATION;
   public static final IntValue FRIED_CHICKEN_NUTRITION;
   public static final DoubleValue FRIED_CHICKEN_SATURATION;
   public static final IntValue HALF_CHICKEN_NUTRITION;
   public static final DoubleValue HALF_CHICKEN_SATURATION;
   public static final IntValue MASHED_POTATOES_NUTRITION;
   public static final DoubleValue MASHED_POTATOES_SATURATION;
   public static final IntValue POTATO_SALAD_NUTRITION;
   public static final DoubleValue POTATO_SALAD_SATURATION;
   public static final IntValue DUMPLINGS_NUTRITION;
   public static final DoubleValue DUMPLINGS_SATURATION;
   public static int brewTime = 60;
   public static boolean enableBrewEvents = true;
   public static int minBrewEventInterval = 5;
   public static int maxBrewEventInterval = 15;
   public static boolean enableBeerElementals = true;
   public static boolean animations = true;
   public static boolean enableDrunkenness = true;
   public static boolean enableDrunkSway = true;
   public static boolean enableDrunkSlowness = true;
   public static boolean enableBlackout = true;
   public static double blackoutChance = 0.15;
   public static boolean enableBlackoutTeleport = true;
   public static int blackoutTeleportRange = 30;
   public static int combustionEffectRadius = 4;
   public static int combustionEffectIgniteChance = 2;
   public static int repulsionEffectRadius = 4;
   public static double repulsionEffectStrength = 0.2;
   public static int stoutHeartEffectHealthCap = 75;
   public static double stoutHeartEffectHealAmount = 0.2;
   public static int miningEffectHasteOneHeight = 50;
   public static int miningEffectHasteTwoHeight = 30;
   public static int miningEffectHasteThreeHeight = 0;
   public static int miningEffectHasteFourHeight = -20;
   public static int pacifyEffectRange = 32;
   public static boolean haleyEffectFlight = true;
   public static boolean showBrewingstationInfo = true;
   public static boolean showBigBarrelInfo = true;
   public static boolean infoTooltipsNeedDungarees = false;
   public static int sausageNutrition = 12;
   public static double sausageSaturation = 0.9;
   public static int pretzelNutrition = 3;
   public static double pretzelSaturation = 0.4;
   public static int porkKnuckleNutrition = 4;
   public static double porkKnuckleSaturation = 0.7;
   public static int friedChickenNutrition = 3;
   public static double friedChickenSaturation = 0.5;
   public static int halfChickenNutrition = 2;
   public static double halfChickenSaturation = 0.6;
   public static int mashedPotatoesNutrition = 2;
   public static double mashedPotatoesSaturation = 0.5;
   public static int potatoSaladNutrition = 2;
   public static double potatoSaladSaturation = 0.5;
   public static int dumplingsNutrition = 3;
   public static double dumplingsSaturation = 0.5;

   public static void onLoad(Loading event) {
      bake();
   }

   public static void onReload(Reloading event) {
      bake();
   }

   private static void bake() {
      brewTime = (Integer)BREW_TIME.get();
      enableBrewEvents = (Boolean)ENABLE_BREW_EVENTS.get();
      minBrewEventInterval = (Integer)MIN_BREW_EVENT_INTERVAL.get();
      maxBrewEventInterval = (Integer)MAX_BREW_EVENT_INTERVAL.get();
      enableBeerElementals = (Boolean)ENABLE_BEER_ELEMENTALS.get();
      animations = (Boolean)ANIMATIONS.get();
      enableDrunkenness = (Boolean)ENABLE_DRUNKENNESS.get();
      enableDrunkSway = (Boolean)ENABLE_DRUNK_SWAY.get();
      enableDrunkSlowness = (Boolean)ENABLE_DRUNK_SLOWNESS.get();
      enableBlackout = (Boolean)ENABLE_BLACKOUT.get();
      blackoutChance = (Double)BLACKOUT_CHANCE.get();
      enableBlackoutTeleport = (Boolean)ENABLE_BLACKOUT_TELEPORT.get();
      blackoutTeleportRange = (Integer)BLACKOUT_TELEPORT_RANGE.get();
      combustionEffectRadius = (Integer)COMBUSTION_EFFECT_RADIUS.get();
      combustionEffectIgniteChance = (Integer)COMBUSTION_EFFECT_IGNITE_CHANCE.get();
      repulsionEffectRadius = (Integer)REPULSION_EFFECT_RADIUS.get();
      repulsionEffectStrength = (Double)REPULSION_EFFECT_STRENGTH.get();
      stoutHeartEffectHealthCap = (Integer)STOUT_HEART_EFFECT_HEALTH_CAP.get();
      stoutHeartEffectHealAmount = (Double)STOUT_HEART_EFFECT_HEAL_AMOUNT.get();
      miningEffectHasteOneHeight = (Integer)MINING_EFFECT_HASTE_ONE_HEIGHT.get();
      miningEffectHasteTwoHeight = (Integer)MINING_EFFECT_HASTE_TWO_HEIGHT.get();
      miningEffectHasteThreeHeight = (Integer)MINING_EFFECT_HASTE_THREE_HEIGHT.get();
      miningEffectHasteFourHeight = (Integer)MINING_EFFECT_HASTE_FOUR_HEIGHT.get();
      pacifyEffectRange = (Integer)PACIFY_EFFECT_RANGE.get();
      haleyEffectFlight = (Boolean)HALEY_EFFECT_FLIGHT.get();
      showBrewingstationInfo = (Boolean)SHOW_BREWINGSTATION_INFO.get();
      showBigBarrelInfo = (Boolean)SHOW_BIG_BARREL_INFO.get();
      infoTooltipsNeedDungarees = (Boolean)INFO_TOOLTIPS_NEED_DUNGAREES.get();
      sausageNutrition = (Integer)SAUSAGE_NUTRITION.get();
      sausageSaturation = (Double)SAUSAGE_SATURATION.get();
      pretzelNutrition = (Integer)PRETZEL_NUTRITION.get();
      pretzelSaturation = (Double)PRETZEL_SATURATION.get();
      porkKnuckleNutrition = (Integer)PORK_KNUCKLE_NUTRITION.get();
      porkKnuckleSaturation = (Double)PORK_KNUCKLE_SATURATION.get();
      friedChickenNutrition = (Integer)FRIED_CHICKEN_NUTRITION.get();
      friedChickenSaturation = (Double)FRIED_CHICKEN_SATURATION.get();
      halfChickenNutrition = (Integer)HALF_CHICKEN_NUTRITION.get();
      halfChickenSaturation = (Double)HALF_CHICKEN_SATURATION.get();
      mashedPotatoesNutrition = (Integer)MASHED_POTATOES_NUTRITION.get();
      mashedPotatoesSaturation = (Double)MASHED_POTATOES_SATURATION.get();
      potatoSaladNutrition = (Integer)POTATO_SALAD_NUTRITION.get();
      potatoSaladSaturation = (Double)POTATO_SALAD_SATURATION.get();
      dumplingsNutrition = (Integer)DUMPLINGS_NUTRITION.get();
      dumplingsSaturation = (Double)DUMPLINGS_SATURATION.get();
   }

   static {
      Builder builder = new Builder();
      builder.comment("Fine-tuning of the drink effects.").push("Effects");
      builder.push("Combustion Effect");
      COMBUSTION_EFFECT_RADIUS = builder.comment("Radius in blocks in which monsters can catch fire.").defineInRange("combustionEffectRadius", 4, 1, 16);
      COMBUSTION_EFFECT_IGNITE_CHANCE = builder.comment("Chance in percent per tick that a monster nearby catches fire.")
         .defineInRange("combustionEffectIgniteChance", 2, 0, 100);
      builder.pop();
      builder.push("Repulsion Effect");
      REPULSION_EFFECT_RADIUS = builder.comment("Radius in blocks in which entities are pushed away.").defineInRange("repulsionEffectRadius", 4, 1, 16);
      REPULSION_EFFECT_STRENGTH = builder.comment("How hard entities are pushed away. Grows with the effect level.")
         .defineInRange("repulsionEffectStrength", 0.2, 0.0, 2.0);
      builder.pop();
      builder.push("Stoutheart Effect");
      STOUT_HEART_EFFECT_HEALTH_CAP = builder.comment("Up to how many percent of your max health the effect heals.")
         .defineInRange("stoutHeartEffectHealthCap", 75, 1, 100);
      STOUT_HEART_EFFECT_HEAL_AMOUNT = builder.comment("Health points healed every half second. Grows with the effect level. 2 points = 1 heart.")
         .defineInRange("stoutHeartEffectHealAmount", 0.2, 0.0, 5.0);
      builder.pop();
      builder.push("Mining Effect");
      MINING_EFFECT_HASTE_ONE_HEIGHT = builder.comment("From this height up you get Haste I.").defineInRange("miningEffectHasteOneHeight", 50, -64, 320);
      MINING_EFFECT_HASTE_TWO_HEIGHT = builder.comment("From this height up you get Haste II.").defineInRange("miningEffectHasteTwoHeight", 30, -64, 320);
      MINING_EFFECT_HASTE_THREE_HEIGHT = builder.comment("From this height up you get Haste III.").defineInRange("miningEffectHasteThreeHeight", 0, -64, 320);
      MINING_EFFECT_HASTE_FOUR_HEIGHT = builder.comment("From this height up you get Haste IV. Below it you get Haste V.")
         .defineInRange("miningEffectHasteFourHeight", -20, -64, 320);
      builder.pop();
      builder.push("Pacify Effect");
      PACIFY_EFFECT_RANGE = builder.comment("Radius in blocks in which endermen stop attacking you.").defineInRange("pacifyEffectRange", 32, 1, 64);
      builder.pop();
      builder.push("Haley Effect");
      HALEY_EFFECT_FLIGHT = builder.comment("Amorous Glide lets you fly.").define("haleyEffectFlight", true);
      builder.pop();
      builder.pop();
      builder.comment("Brewing Station, brew events and Beer Elementals.").push("Brewing");
      BREW_TIME = builder.comment("How many seconds brewing takes.").defineInRange("brewTime", 60, 5, 600);
      ENABLE_BREW_EVENTS = builder.comment("The kettle, oven, timer and whistle need your attention while brewing. Without events, beer has no quality.")
         .define("enableBrewEvents", true);
      MIN_BREW_EVENT_INTERVAL = builder.comment("Shortest time in seconds between two brew events.").defineInRange("minBrewEventInterval", 5, 1, 120);
      MAX_BREW_EVENT_INTERVAL = builder.comment("Longest time in seconds between two brew events.").defineInRange("maxBrewEventInterval", 15, 1, 120);
      ENABLE_BEER_ELEMENTALS = builder.comment("Badly brewed beer can spawn Beer Elementals.").define("enableBeerElementals", true);
      ANIMATIONS = builder.comment("Blocks of this mod are animated.").define("animations", true);
      builder.pop();
      builder.comment("Drunk effects of beer and whiskey.").push("Drunkenness");
      ENABLE_DRUNKENNESS = builder.comment("Beer and whiskey make you drunk. Turns off all drunk effects below.").define("enableDrunkenness", true);
      ENABLE_DRUNK_SWAY = builder.comment("The camera sways and you stagger while drunk.").define("enableDrunkSway", true);
      ENABLE_DRUNK_SLOWNESS = builder.comment("You walk slower the more drunk you are.").define("enableDrunkSlowness", true);
      ENABLE_BLACKOUT = builder.comment("Very drunk players can black out and wake up somewhere else.").define("enableBlackout", true);
      BLACKOUT_CHANCE = builder.comment("Chance in percent per tick that a very drunk player blacks out.").defineInRange("blackoutChance", 0.15, 0.0, 5.0);
      ENABLE_BLACKOUT_TELEPORT = builder.comment("After a blackout you wake up a few blocks away.").define("enableBlackoutTeleport", true);
      BLACKOUT_TELEPORT_RANGE = builder.comment("How many blocks away you can wake up after a blackout.").defineInRange("blackoutTeleportRange", 30, 1, 128);
      builder.pop();
      builder.comment("Info tooltips that appear when looking at a block. Only affects your own screen.").push("Info Tooltips");
      SHOW_BREWINGSTATION_INFO = builder.comment("Shows an info tooltip when looking at the kettle of a Brewing Station.")
         .define("showBrewingstationInfo", true);
      SHOW_BIG_BARREL_INFO = builder.comment("Shows an info tooltip when looking at a Big Barrel.").define("showBigBarrelInfo", true);
      INFO_TOOLTIPS_NEED_DUNGAREES = builder.comment("Info tooltips only show while wearing Dungarees from Farm & Charm.").define("needDungarees", false);
      builder.pop();
      builder.comment("Hunger and saturation of the food items. Changes need a game restart.").push("Food");
      SAUSAGE_NUTRITION = builder.comment("Hunger points restored by eating Sausage. 2 points = 1 drumstick.").defineInRange("sausageNutrition", 12, 0, 20);
      SAUSAGE_SATURATION = builder.comment("Saturation factor of Sausage. Saturation gained = hunger points Ã\u0097 factor Ã\u0097 2.")
         .defineInRange("sausageSaturation", 0.9, 0.0, 2.0);
      PRETZEL_NUTRITION = builder.comment("Hunger points restored by eating Pretzel. 2 points = 1 drumstick.").defineInRange("pretzelNutrition", 3, 0, 20);
      PRETZEL_SATURATION = builder.comment("Saturation factor of Pretzel. Saturation gained = hunger points Ã\u0097 factor Ã\u0097 2.")
         .defineInRange("pretzelSaturation", 0.4, 0.0, 2.0);
      PORK_KNUCKLE_NUTRITION = builder.comment("Hunger points restored by eating Pork Knuckle or by one bite of a placed Pork Knuckle. 2 points = 1 drumstick.")
         .defineInRange("porkKnuckleNutrition", 4, 0, 20);
      PORK_KNUCKLE_SATURATION = builder.comment("Saturation factor of Pork Knuckle. Saturation gained = hunger points Ã\u0097 factor Ã\u0097 2.")
         .defineInRange("porkKnuckleSaturation", 0.7, 0.0, 2.0);
      FRIED_CHICKEN_NUTRITION = builder.comment(
            "Hunger points restored by eating Fried Chicken or by one bite of a placed Fried Chicken. 2 points = 1 drumstick."
         )
         .defineInRange("friedChickenNutrition", 3, 0, 20);
      FRIED_CHICKEN_SATURATION = builder.comment("Saturation factor of Fried Chicken. Saturation gained = hunger points Ã\u0097 factor Ã\u0097 2.")
         .defineInRange("friedChickenSaturation", 0.5, 0.0, 2.0);
      HALF_CHICKEN_NUTRITION = builder.comment("Hunger points restored by eating Half Chicken or by one bite of a placed Half Chicken. 2 points = 1 drumstick.")
         .defineInRange("halfChickenNutrition", 2, 0, 20);
      HALF_CHICKEN_SATURATION = builder.comment("Saturation factor of Half Chicken. Saturation gained = hunger points Ã\u0097 factor Ã\u0097 2.")
         .defineInRange("halfChickenSaturation", 0.6, 0.0, 2.0);
      MASHED_POTATOES_NUTRITION = builder.comment(
            "Hunger points restored by eating Mashed Potatoes or by one bite of a placed Mashed Potatoes. 2 points = 1 drumstick."
         )
         .defineInRange("mashedPotatoesNutrition", 2, 0, 20);
      MASHED_POTATOES_SATURATION = builder.comment("Saturation factor of Mashed Potatoes. Saturation gained = hunger points Ã\u0097 factor Ã\u0097 2.")
         .defineInRange("mashedPotatoesSaturation", 0.5, 0.0, 2.0);
      POTATO_SALAD_NUTRITION = builder.comment("Hunger points restored by eating Potato Salad or by one bite of a placed Potato Salad. 2 points = 1 drumstick.")
         .defineInRange("potatoSaladNutrition", 2, 0, 20);
      POTATO_SALAD_SATURATION = builder.comment("Saturation factor of Potato Salad. Saturation gained = hunger points Ã\u0097 factor Ã\u0097 2.")
         .defineInRange("potatoSaladSaturation", 0.5, 0.0, 2.0);
      DUMPLINGS_NUTRITION = builder.comment("Hunger points restored by eating Dumplings or by one bite of a placed Dumplings. 2 points = 1 drumstick.")
         .defineInRange("dumplingsNutrition", 3, 0, 20);
      DUMPLINGS_SATURATION = builder.comment("Saturation factor of Dumplings. Saturation gained = hunger points Ã\u0097 factor Ã\u0097 2.")
         .defineInRange("dumplingsSaturation", 0.5, 0.0, 2.0);
      builder.pop();
      COMMON_CONFIG = builder.build();
   }
}
