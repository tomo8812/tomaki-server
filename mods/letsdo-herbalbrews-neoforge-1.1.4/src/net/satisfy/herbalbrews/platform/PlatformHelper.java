package net.satisfy.herbalbrews.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import net.satisfy.herbalbrews.platform.neoforge.PlatformHelperImpl;

public class PlatformHelper {
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
   public static int getDryingDuration() {
      return PlatformHelperImpl.getDryingDuration();
   }

   @ExpectPlatform
   @Transformed
   public static int getBrewingDuration() {
      return PlatformHelperImpl.getBrewingDuration();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isHatDamageReductionEnabled() {
      return PlatformHelperImpl.isHatDamageReductionEnabled();
   }

   @ExpectPlatform
   @Transformed
   public static int getHatDamageReductionAmount() {
      return PlatformHelperImpl.getHatDamageReductionAmount();
   }

   @ExpectPlatform
   @Transformed
   public static int getJugEffectDuration() {
      return PlatformHelperImpl.getJugEffectDuration();
   }
}
