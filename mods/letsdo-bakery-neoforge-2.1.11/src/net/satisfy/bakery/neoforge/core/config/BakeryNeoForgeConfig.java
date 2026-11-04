package net.satisfy.bakery.neoforge.core.config;

import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class BakeryNeoForgeConfig {
   public static final Builder COMMON_BUILDER = new Builder();
   public static final BooleanValue GIVE_EFFECT = COMMON_BUILDER.comment("The Completionist Banner grants Resistance to players nearby.")
      .define("bannerGiveEffect", true);
   public static final BooleanValue SHOW_TOOLTIP = COMMON_BUILDER.comment("Shows the thank-you tooltip on the Completionist Banner.")
      .define("bannerShowTooltip", true);
   public static final BooleanValue SHOW_BAKER_STATION_INFO = COMMON_BUILDER.comment(
         "Shows an info tooltip when looking at the Baker Station or the dough on it."
      )
      .define("showBakerStationInfo", true);
   public static final BooleanValue SHOW_JAM_POT_INFO = COMMON_BUILDER.comment("Shows an info tooltip when looking at the Jam Pot.")
      .define("showJamPotInfo", true);
   public static final BooleanValue SHOW_DISPLAY_INFO = COMMON_BUILDER.comment(
         "Shows what lies on the Wall, Cake and Cupcake Display, the Tray and the Cake Stand and what is in the Bread Box."
      )
      .define("showDisplayInfo", true);
   public static final BooleanValue INFO_TOOLTIPS_NEED_DUNGAREES = COMMON_BUILDER.comment("Info tooltips only show while wearing Dungarees from Farm & Charm.")
      .define("needDungarees", false);
   public static final IntValue VITALITY_INTERVAL = COMMON_BUILDER.comment("Ticks between two exhaustion reductions.")
      .defineInRange("vitalityEffectInterval", 10, 1, 200);
   public static final DoubleValue VITALITY_EXHAUSTION_REDUCTION = COMMON_BUILDER.comment("Exhaustion removed each interval. Grows with the effect level.")
      .defineInRange("vitalityEffectExhaustionReduction", 0.08, 0.0, 10.0);
   public static final IntValue CAKE_DOUGH_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Cake Dough. 2 points = 1 drumstick.")
      .defineInRange("cakeDoughNutrition", 2, 0, 20);
   public static final DoubleValue CAKE_DOUGH_SATURATION = COMMON_BUILDER.comment("Saturation given by Cake Dough, multiplied with its hunger points.")
      .defineInRange("cakeDoughSaturation", 0.1, 0.0, 2.0);
   public static final IntValue SWEET_DOUGH_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Sweet Dough. 2 points = 1 drumstick.")
      .defineInRange("sweetDoughNutrition", 2, 0, 20);
   public static final DoubleValue SWEET_DOUGH_SATURATION = COMMON_BUILDER.comment("Saturation given by Sweet Dough, multiplied with its hunger points.")
      .defineInRange("sweetDoughSaturation", 0.1, 0.0, 2.0);
   public static final IntValue CROISSANT_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Croissant. 2 points = 1 drumstick.")
      .defineInRange("croissantNutrition", 4, 0, 20);
   public static final DoubleValue CROISSANT_SATURATION = COMMON_BUILDER.comment("Saturation given by Croissant, multiplied with its hunger points.")
      .defineInRange("croissantSaturation", 0.5, 0.0, 2.0);
   public static final IntValue CRUSTY_BREAD_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Crusty Bread. 2 points = 1 drumstick.")
      .defineInRange("crustyBreadNutrition", 5, 0, 20);
   public static final DoubleValue CRUSTY_BREAD_SATURATION = COMMON_BUILDER.comment("Saturation given by Crusty Bread, multiplied with its hunger points.")
      .defineInRange("crustyBreadSaturation", 0.6, 0.0, 2.0);
   public static final IntValue BREAD_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Bread. 2 points = 1 drumstick.")
      .defineInRange("breadNutrition", 5, 0, 20);
   public static final DoubleValue BREAD_SATURATION = COMMON_BUILDER.comment("Saturation given by Bread, multiplied with its hunger points.")
      .defineInRange("breadSaturation", 0.6, 0.0, 2.0);
   public static final IntValue BAGUETTE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Baguette. 2 points = 1 drumstick.")
      .defineInRange("baguetteNutrition", 6, 0, 20);
   public static final DoubleValue BAGUETTE_SATURATION = COMMON_BUILDER.comment("Saturation given by Baguette, multiplied with its hunger points.")
      .defineInRange("baguetteSaturation", 0.6, 0.0, 2.0);
   public static final IntValue TOAST_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Toast. 2 points = 1 drumstick.")
      .defineInRange("toastNutrition", 3, 0, 20);
   public static final DoubleValue TOAST_SATURATION = COMMON_BUILDER.comment("Saturation given by Toast, multiplied with its hunger points.")
      .defineInRange("toastSaturation", 0.5, 0.0, 2.0);
   public static final IntValue BRAIDED_BREAD_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Braided Bread. 2 points = 1 drumstick.")
      .defineInRange("braidedBreadNutrition", 6, 0, 20);
   public static final DoubleValue BRAIDED_BREAD_SATURATION = COMMON_BUILDER.comment("Saturation given by Braided Bread, multiplied with its hunger points.")
      .defineInRange("braidedBreadSaturation", 0.6, 0.0, 2.0);
   public static final IntValue SANDWICH_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Sandwich. 2 points = 1 drumstick.")
      .defineInRange("sandwichNutrition", 9, 0, 20);
   public static final DoubleValue SANDWICH_SATURATION = COMMON_BUILDER.comment("Saturation given by Sandwich, multiplied with its hunger points.")
      .defineInRange("sandwichSaturation", 0.8, 0.0, 2.0);
   public static final IntValue VEGETABLE_SANDWICH_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Vegetable Sandwich. 2 points = 1 drumstick.")
      .defineInRange("vegetableSandwichNutrition", 7, 0, 20);
   public static final DoubleValue VEGETABLE_SANDWICH_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Vegetable Sandwich, multiplied with its hunger points."
      )
      .defineInRange("vegetableSandwichSaturation", 0.6, 0.0, 2.0);
   public static final IntValue GRILLED_SALMON_SANDWICH_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Grilled Salmon Sandwich. 2 points = 1 drumstick."
      )
      .defineInRange("grilledSalmonSandwichNutrition", 8, 0, 20);
   public static final DoubleValue GRILLED_SALMON_SANDWICH_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Grilled Salmon Sandwich, multiplied with its hunger points."
      )
      .defineInRange("grilledSalmonSandwichSaturation", 0.8, 0.0, 2.0);
   public static final IntValue GRILLED_BACON_SANDWICH_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Grilled Bacon Sandwich. 2 points = 1 drumstick."
      )
      .defineInRange("grilledBaconSandwichNutrition", 8, 0, 20);
   public static final DoubleValue GRILLED_BACON_SANDWICH_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Grilled Bacon Sandwich, multiplied with its hunger points."
      )
      .defineInRange("grilledBaconSandwichSaturation", 0.8, 0.0, 2.0);
   public static final IntValue BREAD_WITH_JAM_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Bread With Jam. 2 points = 1 drumstick.")
      .defineInRange("breadWithJamNutrition", 3, 0, 20);
   public static final DoubleValue BREAD_WITH_JAM_SATURATION = COMMON_BUILDER.comment("Saturation given by Bread With Jam, multiplied with its hunger points.")
      .defineInRange("breadWithJamSaturation", 0.5, 0.0, 2.0);
   public static final IntValue STRAWBERRY_CAKE_SLICE_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Strawberry Cake Slice. 2 points = 1 drumstick."
      )
      .defineInRange("strawberryCakeSliceNutrition", 4, 0, 20);
   public static final DoubleValue STRAWBERRY_CAKE_SLICE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Strawberry Cake Slice, multiplied with its hunger points."
      )
      .defineInRange("strawberryCakeSliceSaturation", 0.6, 0.0, 2.0);
   public static final IntValue SWEETBERRY_CAKE_SLICE_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Sweet Berry Cake Slice. 2 points = 1 drumstick."
      )
      .defineInRange("sweetberryCakeSliceNutrition", 4, 0, 20);
   public static final DoubleValue SWEETBERRY_CAKE_SLICE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Sweet Berry Cake Slice, multiplied with its hunger points."
      )
      .defineInRange("sweetberryCakeSliceSaturation", 0.6, 0.0, 2.0);
   public static final IntValue CHOCOLATE_CAKE_SLICE_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Chocolate Cake Slice. 2 points = 1 drumstick."
      )
      .defineInRange("chocolateCakeSliceNutrition", 4, 0, 20);
   public static final DoubleValue CHOCOLATE_CAKE_SLICE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Chocolate Cake Slice, multiplied with its hunger points."
      )
      .defineInRange("chocolateCakeSliceSaturation", 0.7, 0.0, 2.0);
   public static final IntValue CHOCOLATE_GATEAU_SLICE_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Chocolate Gateau Slice. 2 points = 1 drumstick."
      )
      .defineInRange("chocolateGateauSliceNutrition", 4, 0, 20);
   public static final DoubleValue CHOCOLATE_GATEAU_SLICE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Chocolate Gateau Slice, multiplied with its hunger points."
      )
      .defineInRange("chocolateGateauSliceSaturation", 0.7, 0.0, 2.0);
   public static final IntValue BUNDT_CAKE_SLICE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Bundt Cake Slice. 2 points = 1 drumstick.")
      .defineInRange("bundtCakeSliceNutrition", 3, 0, 20);
   public static final DoubleValue BUNDT_CAKE_SLICE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Bundt Cake Slice, multiplied with its hunger points."
      )
      .defineInRange("bundtCakeSliceSaturation", 0.6, 0.0, 2.0);
   public static final IntValue LINZER_TART_SLICE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Linzer Tart Slice. 2 points = 1 drumstick.")
      .defineInRange("linzerTartSliceNutrition", 4, 0, 20);
   public static final DoubleValue LINZER_TART_SLICE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Linzer Tart Slice, multiplied with its hunger points."
      )
      .defineInRange("linzerTartSliceSaturation", 0.6, 0.0, 2.0);
   public static final IntValue APPLE_PIE_SLICE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Apple Pie Slice. 2 points = 1 drumstick.")
      .defineInRange("applePieSliceNutrition", 4, 0, 20);
   public static final DoubleValue APPLE_PIE_SLICE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Apple Pie Slice, multiplied with its hunger points."
      )
      .defineInRange("applePieSliceSaturation", 0.6, 0.0, 2.0);
   public static final IntValue GLOWBERRY_PIE_SLICE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Glowberry Pie Slice. 2 points = 1 drumstick.")
      .defineInRange("glowberryPieSliceNutrition", 4, 0, 20);
   public static final DoubleValue GLOWBERRY_PIE_SLICE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Glowberry Pie Slice, multiplied with its hunger points."
      )
      .defineInRange("glowberryPieSliceSaturation", 0.6, 0.0, 2.0);
   public static final IntValue CHOCOLATE_TART_SLICE_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Chocolate Tart Slice. 2 points = 1 drumstick."
      )
      .defineInRange("chocolateTartSliceNutrition", 4, 0, 20);
   public static final DoubleValue CHOCOLATE_TART_SLICE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Chocolate Tart Slice, multiplied with its hunger points."
      )
      .defineInRange("chocolateTartSliceSaturation", 0.7, 0.0, 2.0);
   public static final IntValue PUDDING_SLICE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Pudding Slice. 2 points = 1 drumstick.")
      .defineInRange("puddingSliceNutrition", 3, 0, 20);
   public static final DoubleValue PUDDING_SLICE_SATURATION = COMMON_BUILDER.comment("Saturation given by Pudding Slice, multiplied with its hunger points.")
      .defineInRange("puddingSliceSaturation", 0.6, 0.0, 2.0);
   public static final IntValue STRAWBERRY_GLAZED_COOKIE_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Strawberry Glazed Cookie. 2 points = 1 drumstick."
      )
      .defineInRange("strawberryGlazedCookieNutrition", 3, 0, 20);
   public static final DoubleValue STRAWBERRY_GLAZED_COOKIE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Strawberry Glazed Cookie, multiplied with its hunger points."
      )
      .defineInRange("strawberryGlazedCookieSaturation", 0.4, 0.0, 2.0);
   public static final IntValue SWEETBERRY_GLAZED_COOKIE_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Sweet Berry Glazed Cookie. 2 points = 1 drumstick."
      )
      .defineInRange("sweetberryGlazedCookieNutrition", 3, 0, 20);
   public static final DoubleValue SWEETBERRY_GLAZED_COOKIE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Sweet Berry Glazed Cookie, multiplied with its hunger points."
      )
      .defineInRange("sweetberryGlazedCookieSaturation", 0.4, 0.0, 2.0);
   public static final IntValue CHOCOLATE_GLAZED_COOKIE_NUTRITION = COMMON_BUILDER.comment(
         "Hunger points restored by Chocolate Glazed Cookie. 2 points = 1 drumstick."
      )
      .defineInRange("chocolateGlazedCookieNutrition", 3, 0, 20);
   public static final DoubleValue CHOCOLATE_GLAZED_COOKIE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Chocolate Glazed Cookie, multiplied with its hunger points."
      )
      .defineInRange("chocolateGlazedCookieSaturation", 0.4, 0.0, 2.0);
   public static final IntValue STRAWBERRY_CUPCAKE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Strawberry Cupcake. 2 points = 1 drumstick.")
      .defineInRange("strawberryCupcakeNutrition", 3, 0, 20);
   public static final DoubleValue STRAWBERRY_CUPCAKE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Strawberry Cupcake, multiplied with its hunger points."
      )
      .defineInRange("strawberryCupcakeSaturation", 0.5, 0.0, 2.0);
   public static final IntValue SWEETBERRY_CUPCAKE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Sweet Berry Cupcake. 2 points = 1 drumstick.")
      .defineInRange("sweetberryCupcakeNutrition", 3, 0, 20);
   public static final DoubleValue SWEETBERRY_CUPCAKE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Sweet Berry Cupcake, multiplied with its hunger points."
      )
      .defineInRange("sweetberryCupcakeSaturation", 0.5, 0.0, 2.0);
   public static final IntValue APPLE_CUPCAKE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Apple Cupcake. 2 points = 1 drumstick.")
      .defineInRange("appleCupcakeNutrition", 3, 0, 20);
   public static final DoubleValue APPLE_CUPCAKE_SATURATION = COMMON_BUILDER.comment("Saturation given by Apple Cupcake, multiplied with its hunger points.")
      .defineInRange("appleCupcakeSaturation", 0.5, 0.0, 2.0);
   public static final IntValue CORNET_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Cornet. 2 points = 1 drumstick.")
      .defineInRange("cornetNutrition", 3, 0, 20);
   public static final DoubleValue CORNET_SATURATION = COMMON_BUILDER.comment("Saturation given by Cornet, multiplied with its hunger points.")
      .defineInRange("cornetSaturation", 0.5, 0.0, 2.0);
   public static final IntValue JAM_ROLL_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Jam Roll. 2 points = 1 drumstick.")
      .defineInRange("jamRollNutrition", 3, 0, 20);
   public static final DoubleValue JAM_ROLL_SATURATION = COMMON_BUILDER.comment("Saturation given by Jam Roll, multiplied with its hunger points.")
      .defineInRange("jamRollSaturation", 0.4, 0.0, 2.0);
   public static final IntValue BUN_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Bun. 2 points = 1 drumstick.")
      .defineInRange("bunNutrition", 4, 0, 20);
   public static final DoubleValue BUN_SATURATION = COMMON_BUILDER.comment("Saturation given by Bun, multiplied with its hunger points.")
      .defineInRange("bunSaturation", 0.5, 0.0, 2.0);
   public static final IntValue WAFFLE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Waffle. 2 points = 1 drumstick.")
      .defineInRange("waffleNutrition", 4, 0, 20);
   public static final DoubleValue WAFFLE_SATURATION = COMMON_BUILDER.comment("Saturation given by Waffle, multiplied with its hunger points.")
      .defineInRange("waffleSaturation", 0.5, 0.0, 2.0);
   public static final IntValue CHOCOLATE_TRUFFLE_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by Chocolate Truffle. 2 points = 1 drumstick.")
      .defineInRange("chocolateTruffleNutrition", 2, 0, 20);
   public static final DoubleValue CHOCOLATE_TRUFFLE_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by Chocolate Truffle, multiplied with its hunger points."
      )
      .defineInRange("chocolateTruffleSaturation", 0.4, 0.0, 2.0);
   public static final IntValue MISSLILITU_BISCUIT_NUTRITION = COMMON_BUILDER.comment("Hunger points restored by MissLilitu Biscuit. 2 points = 1 drumstick.")
      .defineInRange("misslilituBiscuitNutrition", 3, 0, 20);
   public static final DoubleValue MISSLILITU_BISCUIT_SATURATION = COMMON_BUILDER.comment(
         "Saturation given by MissLilitu Biscuit, multiplied with its hunger points."
      )
      .defineInRange("misslilituBiscuitSaturation", 0.5, 0.0, 2.0);
   public static final ModConfigSpec COMMON_CONFIG = COMMON_BUILDER.build();
   public static final IntValue SUGAR_RUSH_MAX_STACKS = COMMON_BUILDER.comment("How many times Sugar Rush can stack.")
      .defineInRange("sugarRushMaxStacks", 10, 1, 50);
   public static final DoubleValue SUGAR_RUSH_BONUS_PER_STACK = COMMON_BUILDER.comment("Speed bonus per stack. 0.02 = 2%.")
      .defineInRange("sugarRushBonusPerStack", 0.02, 0.0, 1.0);
   public static final IntValue SUGAR_RUSH_ATTACK_SPEED_STACKS = COMMON_BUILDER.comment(
         "Stacks up to this number add movement speed. Every stack above adds attack speed."
      )
      .defineInRange("sugarRushAttackSpeedStacks", 5, 1, 50);
   public static final BooleanValue SUGAR_RUSH_ATTACK_SPEED = COMMON_BUILDER.comment("High Sugar Rush stacks also increase attack speed.")
      .define("sugarRushAttackSpeed", true);
   public static final IntValue BANNER_EFFECT_RADIUS = COMMON_BUILDER.comment("Radius in blocks in which the Completionist Banner grants Resistance.")
      .defineInRange("bannerEffectRadius", 8, 1, 32);
   public static final IntValue BANNER_EFFECT_AMPLIFIER = COMMON_BUILDER.comment("Resistance level of the Completionist Banner. 0 = Resistance I.")
      .defineInRange("bannerEffectAmplifier", 1, 0, 4);
   public static final BooleanValue ANIMATIONS = COMMON_BUILDER.comment("Blocks of this mod are animated. Without animations every step finishes instantly.")
      .define("animations", true);
   public static final BooleanValue ENABLE_KNEADING = COMMON_BUILDER.comment("Dough can be kneaded flat with empty hands.").define("enableKneading", true);
   public static final BooleanValue KNEADING_ANIMATION = COMMON_BUILDER.comment(
         "Kneading dough plays an animation. Without it the dough is flattened instantly."
      )
      .define("kneadingAnimation", true);
   public static final IntValue KNEAD_PRESSES = COMMON_BUILDER.comment("Clicks it takes to knead dough flat with empty hands.")
      .defineInRange("kneadPresses", 8, 1, 50);
   public static final IntValue ROLLING_PIN_PRESSES = COMMON_BUILDER.comment("Clicks it takes to flatten dough with a Rolling Pin.")
      .defineInRange("rollingPinPresses", 5, 1, 50);
   public static final IntValue KNIFE_DURATION = COMMON_BUILDER.comment("Ticks it takes to cut dough with a knife.").defineInRange("knifeDuration", 10, 1, 400);
   public static final IntValue JAM_DURATION = COMMON_BUILDER.comment("Ticks it takes jam to spread over cakes, cupcakes and cookies.")
      .defineInRange("jamDuration", 16, 1, 400);
   public static final DoubleValue KNIFE_ATTACK_SPEED = COMMON_BUILDER.comment("Attacks per second of the Bread Knife. Changes need a game restart.")
      .defineInRange("knifeAttackSpeed", 2.0, 0.1, 4.0);
   public static final DoubleValue ROLLING_PIN_ATTACK_SPEED = COMMON_BUILDER.comment("Attacks per second of the Rolling Pin. Changes need a game restart.")
      .defineInRange("rollingPinAttackSpeed", 1.2, 0.1, 4.0);
   public static final IntValue CROISSANT_EFFECT_DURATION = COMMON_BUILDER.comment("How long the effect of Croissant lasts in ticks. 20 ticks = 1 second.")
      .defineInRange("croissantEffectDuration", 900, 0, 72000);
   public static final IntValue CRUSTY_BREAD_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Crusty Bread lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("crustyBreadEffectDuration", 4800, 0, 72000);
   public static final IntValue BREAD_EFFECT_DURATION = COMMON_BUILDER.comment("How long the effect of Bread lasts in ticks. 20 ticks = 1 second.")
      .defineInRange("breadEffectDuration", 4200, 0, 72000);
   public static final IntValue BAGUETTE_EFFECT_DURATION = COMMON_BUILDER.comment("How long the effect of Baguette lasts in ticks. 20 ticks = 1 second.")
      .defineInRange("baguetteEffectDuration", 4200, 0, 72000);
   public static final IntValue TOAST_EFFECT_DURATION = COMMON_BUILDER.comment("How long the effect of Toast lasts in ticks. 20 ticks = 1 second.")
      .defineInRange("toastEffectDuration", 5400, 0, 72000);
   public static final IntValue BRAIDED_BREAD_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Braided Bread lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("braidedBreadEffectDuration", 4200, 0, 72000);
   public static final IntValue SANDWICH_EFFECT_DURATION = COMMON_BUILDER.comment("How long the effect of Sandwich lasts in ticks. 20 ticks = 1 second.")
      .defineInRange("sandwichEffectDuration", 1500, 0, 72000);
   public static final IntValue VEGETABLE_SANDWICH_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Vegetable Sandwich lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("vegetableSandwichEffectDuration", 1800, 0, 72000);
   public static final IntValue GRILLED_SALMON_SANDWICH_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Grilled Salmon Sandwich lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("grilledSalmonSandwichEffectDuration", 1200, 0, 72000);
   public static final IntValue GRILLED_BACON_SANDWICH_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Grilled Bacon Sandwich lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("grilledBaconSandwichEffectDuration", 1200, 0, 72000);
   public static final IntValue BREAD_WITH_JAM_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Bread With Jam lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("breadWithJamEffectDuration", 400, 0, 72000);
   public static final IntValue STRAWBERRY_CAKE_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Strawberry Cake Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("strawberryCakeSliceEffectDuration", 600, 0, 72000);
   public static final IntValue SWEETBERRY_CAKE_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Sweet Berry Cake Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("sweetberryCakeSliceEffectDuration", 600, 0, 72000);
   public static final IntValue CHOCOLATE_CAKE_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Chocolate Cake Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("chocolateCakeSliceEffectDuration", 600, 0, 72000);
   public static final IntValue CHOCOLATE_GATEAU_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Chocolate Gateau Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("chocolateGateauSliceEffectDuration", 600, 0, 72000);
   public static final IntValue BUNDT_CAKE_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Bundt Cake Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("bundtCakeSliceEffectDuration", 600, 0, 72000);
   public static final IntValue LINZER_TART_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Linzer Tart Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("linzerTartSliceEffectDuration", 600, 0, 72000);
   public static final IntValue APPLE_PIE_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Apple Pie Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("applePieSliceEffectDuration", 600, 0, 72000);
   public static final IntValue GLOWBERRY_PIE_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Glowberry Pie Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("glowberryPieSliceEffectDuration", 600, 0, 72000);
   public static final IntValue CHOCOLATE_TART_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Chocolate Tart Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("chocolateTartSliceEffectDuration", 600, 0, 72000);
   public static final IntValue PUDDING_SLICE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Pudding Slice lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("puddingSliceEffectDuration", 600, 0, 72000);
   public static final IntValue STRAWBERRY_GLAZED_COOKIE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Strawberry Glazed Cookie lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("strawberryGlazedCookieEffectDuration", 400, 0, 72000);
   public static final IntValue SWEETBERRY_GLAZED_COOKIE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Sweet Berry Glazed Cookie lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("sweetberryGlazedCookieEffectDuration", 400, 0, 72000);
   public static final IntValue CHOCOLATE_GLAZED_COOKIE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Chocolate Glazed Cookie lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("chocolateGlazedCookieEffectDuration", 400, 0, 72000);
   public static final IntValue STRAWBERRY_CUPCAKE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Strawberry Cupcake lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("strawberryCupcakeEffectDuration", 400, 0, 72000);
   public static final IntValue SWEETBERRY_CUPCAKE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Sweet Berry Cupcake lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("sweetberryCupcakeEffectDuration", 400, 0, 72000);
   public static final IntValue APPLE_CUPCAKE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Apple Cupcake lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("appleCupcakeEffectDuration", 400, 0, 72000);
   public static final IntValue CORNET_EFFECT_DURATION = COMMON_BUILDER.comment("How long the effect of Cornet lasts in ticks. 20 ticks = 1 second.")
      .defineInRange("cornetEffectDuration", 400, 0, 72000);
   public static final IntValue JAM_ROLL_EFFECT_DURATION = COMMON_BUILDER.comment("How long the effect of Jam Roll lasts in ticks. 20 ticks = 1 second.")
      .defineInRange("jamRollEffectDuration", 400, 0, 72000);
   public static final IntValue CHOCOLATE_TRUFFLE_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of Chocolate Truffle lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("chocolateTruffleEffectDuration", 200, 0, 72000);
   public static final IntValue MISSLILITU_BISCUIT_EFFECT_DURATION = COMMON_BUILDER.comment(
         "How long the effect of MissLilitu Biscuit lasts in ticks. 20 ticks = 1 second."
      )
      .defineInRange("misslilituBiscuitEffectDuration", 900, 0, 72000);
   public static final IntValue WAFFLE_EFFECT_DURATION = COMMON_BUILDER.comment("How long the effect of Waffle lasts in ticks. 20 ticks = 1 second.")
      .defineInRange("waffleEffectDuration", 800, 0, 72000);
   public static final IntValue BUN_EFFECT_DURATION = COMMON_BUILDER.comment("How long the effect of Bun lasts in ticks. 20 ticks = 1 second.")
      .defineInRange("bunEffectDuration", 2800, 0, 72000);
   public static boolean give_effect = true;
   public static boolean show_tooltip = true;
   public static boolean show_baker_station_info = true;
   public static boolean show_jam_pot_info = true;
   public static boolean show_display_info = true;
   public static boolean info_tooltips_need_dungarees = false;
   public static int sugar_rush_max_stacks = 10;
   public static double sugar_rush_bonus_per_stack = 0.02;
   public static int sugar_rush_attack_speed_stacks = 5;
   public static boolean sugar_rush_attack_speed = true;
   public static int banner_effect_radius = 8;
   public static int banner_effect_amplifier = 1;
   public static boolean animations = true;
   public static boolean enable_kneading = true;
   public static boolean kneading_animation = true;
   public static int knead_presses = 8;
   public static int rolling_pin_presses = 5;
   public static int knife_duration = 10;
   public static int jam_duration = 16;
   public static double knife_attack_speed = 2.0;
   public static double rolling_pin_attack_speed = 1.2;
   public static int croissant_effect_duration = 900;
   public static int crusty_bread_effect_duration = 4800;
   public static int bread_effect_duration = 4200;
   public static int baguette_effect_duration = 4200;
   public static int toast_effect_duration = 5400;
   public static int braided_bread_effect_duration = 4200;
   public static int sandwich_effect_duration = 1500;
   public static int vegetable_sandwich_effect_duration = 1800;
   public static int grilled_salmon_sandwich_effect_duration = 1200;
   public static int grilled_bacon_sandwich_effect_duration = 1200;
   public static int bread_with_jam_effect_duration = 400;
   public static int strawberry_cake_slice_effect_duration = 600;
   public static int sweetberry_cake_slice_effect_duration = 600;
   public static int chocolate_cake_slice_effect_duration = 600;
   public static int chocolate_gateau_slice_effect_duration = 600;
   public static int bundt_cake_slice_effect_duration = 600;
   public static int linzer_tart_slice_effect_duration = 600;
   public static int apple_pie_slice_effect_duration = 600;
   public static int glowberry_pie_slice_effect_duration = 600;
   public static int chocolate_tart_slice_effect_duration = 600;
   public static int pudding_slice_effect_duration = 600;
   public static int strawberry_glazed_cookie_effect_duration = 400;
   public static int sweetberry_glazed_cookie_effect_duration = 400;
   public static int chocolate_glazed_cookie_effect_duration = 400;
   public static int strawberry_cupcake_effect_duration = 400;
   public static int sweetberry_cupcake_effect_duration = 400;
   public static int apple_cupcake_effect_duration = 400;
   public static int cornet_effect_duration = 400;
   public static int jam_roll_effect_duration = 400;
   public static int chocolate_truffle_effect_duration = 200;
   public static int misslilitu_biscuit_effect_duration = 900;
   public static int waffle_effect_duration = 800;
   public static int bun_effect_duration = 2800;
   public static int vitality_interval = 10;
   public static double vitality_exhaustion_reduction = 0.08;
   public static int cake_dough_nutrition = 5;
   public static double cake_dough_saturation = 0.6;
   public static int sweet_dough_nutrition = 5;
   public static double sweet_dough_saturation = 0.6;
   public static int croissant_nutrition = 5;
   public static double croissant_saturation = 0.6;
   public static int crusty_bread_nutrition = 5;
   public static double crusty_bread_saturation = 1.2;
   public static int bread_nutrition = 5;
   public static double bread_saturation = 1.2;
   public static int baguette_nutrition = 5;
   public static double baguette_saturation = 1.2;
   public static int toast_nutrition = 3;
   public static double toast_saturation = 0.8;
   public static int braided_bread_nutrition = 5;
   public static double braided_bread_saturation = 1.2;
   public static int sandwich_nutrition = 7;
   public static double sandwich_saturation = 0.7;
   public static int vegetable_sandwich_nutrition = 8;
   public static double vegetable_sandwich_saturation = 0.6;
   public static int grilled_salmon_sandwich_nutrition = 6;
   public static double grilled_salmon_sandwich_saturation = 0.8;
   public static int grilled_bacon_sandwich_nutrition = 7;
   public static double grilled_bacon_sandwich_saturation = 0.7;
   public static int bread_with_jam_nutrition = 5;
   public static double bread_with_jam_saturation = 0.5;
   public static int strawberry_cake_slice_nutrition = 5;
   public static double strawberry_cake_slice_saturation = 0.7;
   public static int sweetberry_cake_slice_nutrition = 5;
   public static double sweetberry_cake_slice_saturation = 0.7;
   public static int chocolate_cake_slice_nutrition = 5;
   public static double chocolate_cake_slice_saturation = 0.7;
   public static int chocolate_gateau_slice_nutrition = 5;
   public static double chocolate_gateau_slice_saturation = 0.7;
   public static int bundt_cake_slice_nutrition = 5;
   public static double bundt_cake_slice_saturation = 0.7;
   public static int linzer_tart_slice_nutrition = 5;
   public static double linzer_tart_slice_saturation = 0.7;
   public static int apple_pie_slice_nutrition = 5;
   public static double apple_pie_slice_saturation = 0.7;
   public static int glowberry_pie_slice_nutrition = 5;
   public static double glowberry_pie_slice_saturation = 0.7;
   public static int chocolate_tart_slice_nutrition = 5;
   public static double chocolate_tart_slice_saturation = 0.7;
   public static int pudding_slice_nutrition = 5;
   public static double pudding_slice_saturation = 0.7;
   public static int strawberry_glazed_cookie_nutrition = 3;
   public static double strawberry_glazed_cookie_saturation = 0.5;
   public static int sweetberry_glazed_cookie_nutrition = 3;
   public static double sweetberry_glazed_cookie_saturation = 0.5;
   public static int chocolate_glazed_cookie_nutrition = 3;
   public static double chocolate_glazed_cookie_saturation = 0.5;
   public static int strawberry_cupcake_nutrition = 3;
   public static double strawberry_cupcake_saturation = 0.5;
   public static int sweetberry_cupcake_nutrition = 3;
   public static double sweetberry_cupcake_saturation = 0.5;
   public static int apple_cupcake_nutrition = 3;
   public static double apple_cupcake_saturation = 0.5;
   public static int cornet_nutrition = 3;
   public static double cornet_saturation = 0.5;
   public static int jam_roll_nutrition = 3;
   public static double jam_roll_saturation = 0.5;
   public static int bun_nutrition = 5;
   public static double bun_saturation = 1.2;
   public static int waffle_nutrition = 5;
   public static double waffle_saturation = 0.5;
   public static int chocolate_truffle_nutrition = 2;
   public static double chocolate_truffle_saturation = 0.4;
   public static int misslilitu_biscuit_nutrition = 6;
   public static double misslilitu_biscuit_saturation = 0.6;

   public static void onLoad(Loading event) {
      sync();
   }

   public static void onReload(Reloading event) {
      sync();
   }

   public static void sync() {
      give_effect = (Boolean)GIVE_EFFECT.get();
      show_tooltip = (Boolean)SHOW_TOOLTIP.get();
      show_baker_station_info = (Boolean)SHOW_BAKER_STATION_INFO.get();
      show_jam_pot_info = (Boolean)SHOW_JAM_POT_INFO.get();
      show_display_info = (Boolean)SHOW_DISPLAY_INFO.get();
      info_tooltips_need_dungarees = (Boolean)INFO_TOOLTIPS_NEED_DUNGAREES.get();
      sugar_rush_max_stacks = (Integer)SUGAR_RUSH_MAX_STACKS.get();
      sugar_rush_bonus_per_stack = (Double)SUGAR_RUSH_BONUS_PER_STACK.get();
      sugar_rush_attack_speed_stacks = (Integer)SUGAR_RUSH_ATTACK_SPEED_STACKS.get();
      sugar_rush_attack_speed = (Boolean)SUGAR_RUSH_ATTACK_SPEED.get();
      banner_effect_radius = (Integer)BANNER_EFFECT_RADIUS.get();
      banner_effect_amplifier = (Integer)BANNER_EFFECT_AMPLIFIER.get();
      animations = (Boolean)ANIMATIONS.get();
      enable_kneading = (Boolean)ENABLE_KNEADING.get();
      kneading_animation = (Boolean)KNEADING_ANIMATION.get();
      knead_presses = (Integer)KNEAD_PRESSES.get();
      rolling_pin_presses = (Integer)ROLLING_PIN_PRESSES.get();
      knife_duration = (Integer)KNIFE_DURATION.get();
      jam_duration = (Integer)JAM_DURATION.get();
      knife_attack_speed = (Double)KNIFE_ATTACK_SPEED.get();
      rolling_pin_attack_speed = (Double)ROLLING_PIN_ATTACK_SPEED.get();
      croissant_effect_duration = (Integer)CROISSANT_EFFECT_DURATION.get();
      crusty_bread_effect_duration = (Integer)CRUSTY_BREAD_EFFECT_DURATION.get();
      bread_effect_duration = (Integer)BREAD_EFFECT_DURATION.get();
      baguette_effect_duration = (Integer)BAGUETTE_EFFECT_DURATION.get();
      toast_effect_duration = (Integer)TOAST_EFFECT_DURATION.get();
      braided_bread_effect_duration = (Integer)BRAIDED_BREAD_EFFECT_DURATION.get();
      sandwich_effect_duration = (Integer)SANDWICH_EFFECT_DURATION.get();
      vegetable_sandwich_effect_duration = (Integer)VEGETABLE_SANDWICH_EFFECT_DURATION.get();
      grilled_salmon_sandwich_effect_duration = (Integer)GRILLED_SALMON_SANDWICH_EFFECT_DURATION.get();
      grilled_bacon_sandwich_effect_duration = (Integer)GRILLED_BACON_SANDWICH_EFFECT_DURATION.get();
      bread_with_jam_effect_duration = (Integer)BREAD_WITH_JAM_EFFECT_DURATION.get();
      strawberry_cake_slice_effect_duration = (Integer)STRAWBERRY_CAKE_SLICE_EFFECT_DURATION.get();
      sweetberry_cake_slice_effect_duration = (Integer)SWEETBERRY_CAKE_SLICE_EFFECT_DURATION.get();
      chocolate_cake_slice_effect_duration = (Integer)CHOCOLATE_CAKE_SLICE_EFFECT_DURATION.get();
      chocolate_gateau_slice_effect_duration = (Integer)CHOCOLATE_GATEAU_SLICE_EFFECT_DURATION.get();
      bundt_cake_slice_effect_duration = (Integer)BUNDT_CAKE_SLICE_EFFECT_DURATION.get();
      linzer_tart_slice_effect_duration = (Integer)LINZER_TART_SLICE_EFFECT_DURATION.get();
      apple_pie_slice_effect_duration = (Integer)APPLE_PIE_SLICE_EFFECT_DURATION.get();
      glowberry_pie_slice_effect_duration = (Integer)GLOWBERRY_PIE_SLICE_EFFECT_DURATION.get();
      chocolate_tart_slice_effect_duration = (Integer)CHOCOLATE_TART_SLICE_EFFECT_DURATION.get();
      pudding_slice_effect_duration = (Integer)PUDDING_SLICE_EFFECT_DURATION.get();
      strawberry_glazed_cookie_effect_duration = (Integer)STRAWBERRY_GLAZED_COOKIE_EFFECT_DURATION.get();
      sweetberry_glazed_cookie_effect_duration = (Integer)SWEETBERRY_GLAZED_COOKIE_EFFECT_DURATION.get();
      chocolate_glazed_cookie_effect_duration = (Integer)CHOCOLATE_GLAZED_COOKIE_EFFECT_DURATION.get();
      strawberry_cupcake_effect_duration = (Integer)STRAWBERRY_CUPCAKE_EFFECT_DURATION.get();
      sweetberry_cupcake_effect_duration = (Integer)SWEETBERRY_CUPCAKE_EFFECT_DURATION.get();
      apple_cupcake_effect_duration = (Integer)APPLE_CUPCAKE_EFFECT_DURATION.get();
      cornet_effect_duration = (Integer)CORNET_EFFECT_DURATION.get();
      jam_roll_effect_duration = (Integer)JAM_ROLL_EFFECT_DURATION.get();
      chocolate_truffle_effect_duration = (Integer)CHOCOLATE_TRUFFLE_EFFECT_DURATION.get();
      misslilitu_biscuit_effect_duration = (Integer)MISSLILITU_BISCUIT_EFFECT_DURATION.get();
      waffle_effect_duration = (Integer)WAFFLE_EFFECT_DURATION.get();
      bun_effect_duration = (Integer)BUN_EFFECT_DURATION.get();
      vitality_interval = (Integer)VITALITY_INTERVAL.get();
      vitality_exhaustion_reduction = (Double)VITALITY_EXHAUSTION_REDUCTION.get();
      cake_dough_nutrition = (Integer)CAKE_DOUGH_NUTRITION.get();
      cake_dough_saturation = (Double)CAKE_DOUGH_SATURATION.get();
      sweet_dough_nutrition = (Integer)SWEET_DOUGH_NUTRITION.get();
      sweet_dough_saturation = (Double)SWEET_DOUGH_SATURATION.get();
      croissant_nutrition = (Integer)CROISSANT_NUTRITION.get();
      croissant_saturation = (Double)CROISSANT_SATURATION.get();
      crusty_bread_nutrition = (Integer)CRUSTY_BREAD_NUTRITION.get();
      crusty_bread_saturation = (Double)CRUSTY_BREAD_SATURATION.get();
      bread_nutrition = (Integer)BREAD_NUTRITION.get();
      bread_saturation = (Double)BREAD_SATURATION.get();
      baguette_nutrition = (Integer)BAGUETTE_NUTRITION.get();
      baguette_saturation = (Double)BAGUETTE_SATURATION.get();
      toast_nutrition = (Integer)TOAST_NUTRITION.get();
      toast_saturation = (Double)TOAST_SATURATION.get();
      braided_bread_nutrition = (Integer)BRAIDED_BREAD_NUTRITION.get();
      braided_bread_saturation = (Double)BRAIDED_BREAD_SATURATION.get();
      sandwich_nutrition = (Integer)SANDWICH_NUTRITION.get();
      sandwich_saturation = (Double)SANDWICH_SATURATION.get();
      vegetable_sandwich_nutrition = (Integer)VEGETABLE_SANDWICH_NUTRITION.get();
      vegetable_sandwich_saturation = (Double)VEGETABLE_SANDWICH_SATURATION.get();
      grilled_salmon_sandwich_nutrition = (Integer)GRILLED_SALMON_SANDWICH_NUTRITION.get();
      grilled_salmon_sandwich_saturation = (Double)GRILLED_SALMON_SANDWICH_SATURATION.get();
      grilled_bacon_sandwich_nutrition = (Integer)GRILLED_BACON_SANDWICH_NUTRITION.get();
      grilled_bacon_sandwich_saturation = (Double)GRILLED_BACON_SANDWICH_SATURATION.get();
      bread_with_jam_nutrition = (Integer)BREAD_WITH_JAM_NUTRITION.get();
      bread_with_jam_saturation = (Double)BREAD_WITH_JAM_SATURATION.get();
      strawberry_cake_slice_nutrition = (Integer)STRAWBERRY_CAKE_SLICE_NUTRITION.get();
      strawberry_cake_slice_saturation = (Double)STRAWBERRY_CAKE_SLICE_SATURATION.get();
      sweetberry_cake_slice_nutrition = (Integer)SWEETBERRY_CAKE_SLICE_NUTRITION.get();
      sweetberry_cake_slice_saturation = (Double)SWEETBERRY_CAKE_SLICE_SATURATION.get();
      chocolate_cake_slice_nutrition = (Integer)CHOCOLATE_CAKE_SLICE_NUTRITION.get();
      chocolate_cake_slice_saturation = (Double)CHOCOLATE_CAKE_SLICE_SATURATION.get();
      chocolate_gateau_slice_nutrition = (Integer)CHOCOLATE_GATEAU_SLICE_NUTRITION.get();
      chocolate_gateau_slice_saturation = (Double)CHOCOLATE_GATEAU_SLICE_SATURATION.get();
      bundt_cake_slice_nutrition = (Integer)BUNDT_CAKE_SLICE_NUTRITION.get();
      bundt_cake_slice_saturation = (Double)BUNDT_CAKE_SLICE_SATURATION.get();
      linzer_tart_slice_nutrition = (Integer)LINZER_TART_SLICE_NUTRITION.get();
      linzer_tart_slice_saturation = (Double)LINZER_TART_SLICE_SATURATION.get();
      apple_pie_slice_nutrition = (Integer)APPLE_PIE_SLICE_NUTRITION.get();
      apple_pie_slice_saturation = (Double)APPLE_PIE_SLICE_SATURATION.get();
      glowberry_pie_slice_nutrition = (Integer)GLOWBERRY_PIE_SLICE_NUTRITION.get();
      glowberry_pie_slice_saturation = (Double)GLOWBERRY_PIE_SLICE_SATURATION.get();
      chocolate_tart_slice_nutrition = (Integer)CHOCOLATE_TART_SLICE_NUTRITION.get();
      chocolate_tart_slice_saturation = (Double)CHOCOLATE_TART_SLICE_SATURATION.get();
      pudding_slice_nutrition = (Integer)PUDDING_SLICE_NUTRITION.get();
      pudding_slice_saturation = (Double)PUDDING_SLICE_SATURATION.get();
      strawberry_glazed_cookie_nutrition = (Integer)STRAWBERRY_GLAZED_COOKIE_NUTRITION.get();
      strawberry_glazed_cookie_saturation = (Double)STRAWBERRY_GLAZED_COOKIE_SATURATION.get();
      sweetberry_glazed_cookie_nutrition = (Integer)SWEETBERRY_GLAZED_COOKIE_NUTRITION.get();
      sweetberry_glazed_cookie_saturation = (Double)SWEETBERRY_GLAZED_COOKIE_SATURATION.get();
      chocolate_glazed_cookie_nutrition = (Integer)CHOCOLATE_GLAZED_COOKIE_NUTRITION.get();
      chocolate_glazed_cookie_saturation = (Double)CHOCOLATE_GLAZED_COOKIE_SATURATION.get();
      strawberry_cupcake_nutrition = (Integer)STRAWBERRY_CUPCAKE_NUTRITION.get();
      strawberry_cupcake_saturation = (Double)STRAWBERRY_CUPCAKE_SATURATION.get();
      sweetberry_cupcake_nutrition = (Integer)SWEETBERRY_CUPCAKE_NUTRITION.get();
      sweetberry_cupcake_saturation = (Double)SWEETBERRY_CUPCAKE_SATURATION.get();
      apple_cupcake_nutrition = (Integer)APPLE_CUPCAKE_NUTRITION.get();
      apple_cupcake_saturation = (Double)APPLE_CUPCAKE_SATURATION.get();
      cornet_nutrition = (Integer)CORNET_NUTRITION.get();
      cornet_saturation = (Double)CORNET_SATURATION.get();
      jam_roll_nutrition = (Integer)JAM_ROLL_NUTRITION.get();
      jam_roll_saturation = (Double)JAM_ROLL_SATURATION.get();
      bun_nutrition = (Integer)BUN_NUTRITION.get();
      bun_saturation = (Double)BUN_SATURATION.get();
      waffle_nutrition = (Integer)WAFFLE_NUTRITION.get();
      waffle_saturation = (Double)WAFFLE_SATURATION.get();
      chocolate_truffle_nutrition = (Integer)CHOCOLATE_TRUFFLE_NUTRITION.get();
      chocolate_truffle_saturation = (Double)CHOCOLATE_TRUFFLE_SATURATION.get();
      misslilitu_biscuit_nutrition = (Integer)MISSLILITU_BISCUIT_NUTRITION.get();
      misslilitu_biscuit_saturation = (Double)MISSLILITU_BISCUIT_SATURATION.get();
   }

   static {
      COMMON_BUILDER.comment("Kneading, rolling, cutting and the tools of the Baker Station.").push("Baking");
      COMMON_BUILDER.pop();
      COMMON_BUILDER.comment("Info tooltips that appear when looking at a block. Only affects your own screen.").push("Info Tooltips");
      COMMON_BUILDER.pop();
      COMMON_BUILDER.comment("Fine-tuning of the effects.").push("Effects");
      COMMON_BUILDER.push("Vitality Effect");
      COMMON_BUILDER.pop();
      COMMON_BUILDER.push("Sugar Rush Effect");
      COMMON_BUILDER.pop();
      COMMON_BUILDER.push("Completionist Banner Effect");
      COMMON_BUILDER.pop();
      COMMON_BUILDER.pop();
      COMMON_BUILDER.comment("Block animations.").push("Misc");
      COMMON_BUILDER.pop();
      COMMON_BUILDER.comment("Hunger, saturation and effect duration in ticks of the food items. Changes need a game restart.").push("Food");
      COMMON_BUILDER.pop();
   }
}
