package net.caffeinemc.mods.lithium.common.services;

import java.util.List;
import java.util.Map;
import net.caffeinemc.mods.lithium.common.config.Option;

public interface PlatformMixinOverrides {
   PlatformMixinOverrides INSTANCE = Services.load(PlatformMixinOverrides.class);

   static PlatformMixinOverrides getInstance() {
      return INSTANCE;
   }

   void applyLithiumCompat(Map<String, Option> var1);

   List<PlatformMixinOverrides.MixinOverride> applyModOverrides();

   record MixinOverride(String modId, String option, boolean enabled) {
   }
}
