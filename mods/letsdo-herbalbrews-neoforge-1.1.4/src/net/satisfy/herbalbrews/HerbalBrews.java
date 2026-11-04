package net.satisfy.herbalbrews;

import net.satisfy.herbalbrews.core.event.CommonEvents;
import net.satisfy.herbalbrews.core.registry.EffectRegistry;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;
import net.satisfy.herbalbrews.core.registry.MenuTypeRegistry;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;
import net.satisfy.herbalbrews.core.registry.SoundEventRegistry;
import net.satisfy.herbalbrews.core.registry.TabRegistry;

public class HerbalBrews {
   public static final String MOD_ID = "herbalbrews";

   public static void init() {
      ObjectRegistry.init();
      EntityTypeRegistry.init();
      EffectRegistry.init();
      RecipeTypeRegistry.init();
      SoundEventRegistry.init();
      MenuTypeRegistry.init();
      TabRegistry.init();
      CommonEvents.init();
   }
}
