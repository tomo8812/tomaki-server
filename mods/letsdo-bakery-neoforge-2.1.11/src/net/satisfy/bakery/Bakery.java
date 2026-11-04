package net.satisfy.bakery;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.satisfy.bakery.core.event.CommonEvents;
import net.satisfy.bakery.core.network.PacketHandler;
import net.satisfy.bakery.core.registry.CompostableRegistry;
import net.satisfy.bakery.core.registry.DataComponentRegistry;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;
import net.satisfy.bakery.core.registry.FlammableBlockRegistry;
import net.satisfy.bakery.core.registry.MobEffectRegistry;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;
import net.satisfy.bakery.core.registry.SoundEventRegistry;
import net.satisfy.bakery.core.registry.TabRegistry;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;

public class Bakery {
   public static final String MOD_ID = "bakery";

   public static ResourceLocation identifier(String name) {
      return ResourceLocation.fromNamespaceAndPath("bakery", name);
   }

   public static void init() {
      MobEffectRegistry.init();
      ObjectRegistry.init();
      CompostableRegistry.registerCompostable();
      FlammableBlockRegistry.init();
      EntityTypeRegistry.init();
      RecipeTypeRegistry.init();
      DataComponentRegistry.init();
      PacketHandler.init();
      CommonEvents.init();
      TabRegistry.init();
      SoundEventRegistry.init();
      LifecycleEvent.SETUP.register(Bakery::registerRarities);
   }

   private static void registerRarities() {
      FoundationRarities.register((ItemLike)ObjectRegistry.BAKERY_BANNER.get(), FoundationRarity.LEGENDARY);
   }
}
