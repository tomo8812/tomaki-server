package net.satisfy.farm_and_charm;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.core.event.CleaverEvents;
import net.satisfy.farm_and_charm.core.event.VanillaItemPlacements;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.registry.CompostableRegistry;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.FeatureRegistry;
import net.satisfy.farm_and_charm.core.registry.FlammableBlockRegistry;
import net.satisfy.farm_and_charm.core.registry.MobEffectRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.farm_and_charm.core.registry.TabRegistry;
import net.satisfy.farm_and_charm.core.registry.VillagerTradeRegistryHandler;
import net.satisfy.farm_and_charm.core.util.CartInteractionHooks;
import net.satisfy.foundation.entity.equipment.ZombieEquipment;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;

public class FarmAndCharm {
   public static final String MOD_ID = "farm_and_charm";

   public static ResourceLocation identifier(String name) {
      return ResourceLocation.fromNamespaceAndPath("farm_and_charm", name);
   }

   public static void init() {
      MobEffectRegistry.init();
      ObjectRegistry.init();
      ZombieEquipment.add(0.03).slot(EquipmentSlot.MAINHAND, ObjectRegistry.PITCHFORK);
      CompostableRegistry.registerCompostable();
      FlammableBlockRegistry.init();
      FeatureRegistry.init();
      VanillaItemPlacements.init();
      EntityTypeRegistry.init();
      TabRegistry.init();
      ScreenhandlerTypeRegistry.init();
      SoundEventRegistry.init();
      RecipeTypeRegistry.init();
      VillagerTradeRegistryHandler.init();
      PacketHandler.init();
      CartInteractionHooks.init();
      CleaverEvents.init();
      LifecycleEvent.SETUP.register(FarmAndCharm::registerRarities);
   }

   private static void registerRarities() {
      FoundationRarities.register((ItemLike)ObjectRegistry.SCARECROW.get(), FoundationRarity.LEGENDARY);
      FoundationRarities.register((ItemLike)ObjectRegistry.DUNGAREES.get(), FoundationRarity.RARE);
   }
}
