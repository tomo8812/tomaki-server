package net.satisfy.herbalbrews.platform.neoforge;

import net.satisfy.herbalbrews.neoforge.config.HerbalBrewsNeoForgeConfig;
import net.satisfy.herbalbrews.platform.PlatformHelper;

public class PlatformHelperImpl extends PlatformHelper {
   public static boolean shouldGiveEffect() {
      return (Boolean)HerbalBrewsNeoForgeConfig.ITEMS_BANNER_GIVE_EFFECT.get();
   }

   public static boolean shouldShowTooltip() {
      return (Boolean)HerbalBrewsNeoForgeConfig.ITEMS_BANNER_SHOW_TOOLTIP.get();
   }

   public static int getDryingDuration() {
      return (Integer)HerbalBrewsNeoForgeConfig.BLOCKS_DRYING_DURATION.get();
   }

   public static int getBrewingDuration() {
      return (Integer)HerbalBrewsNeoForgeConfig.BLOCKS_BREWING_DURATION.get();
   }

   public static boolean isHatDamageReductionEnabled() {
      return (Boolean)HerbalBrewsNeoForgeConfig.ITEMS_HAT_DAMAGE_REDUCTION_ENABLED.get();
   }

   public static int getHatDamageReductionAmount() {
      return (Integer)HerbalBrewsNeoForgeConfig.ITEMS_HAT_DAMAGE_REDUCTION_AMOUNT.get();
   }

   public static int getJugEffectDuration() {
      return (Integer)HerbalBrewsNeoForgeConfig.BLOCKS_JUG_EFFECT_DURATION.get();
   }
}
