package net.satisfy.brewery;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.satisfy.brewery.core.event.CommonEvents;
import net.satisfy.brewery.core.event.PartyStarterEvent;
import net.satisfy.brewery.core.event.brew_event.BrewEvents;
import net.satisfy.brewery.core.network.BreweryNetworking;
import net.satisfy.brewery.core.registry.ArmorSetRegistry;
import net.satisfy.brewery.core.registry.CompostablesRegistry;
import net.satisfy.brewery.core.registry.EntityTypeRegistry;
import net.satisfy.brewery.core.registry.FlammableBlockRegistry;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.core.registry.RecipeTypeRegistry;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import net.satisfy.brewery.core.registry.TabRegistry;
import net.satisfy.foundation.entity.equipment.ZombieEquipment;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;

public class Brewery {
   public static final String MOD_ID = "brewery";

   public static ResourceLocation identifier(String name) {
      return ResourceLocation.fromNamespaceAndPath("brewery", name);
   }

   public static void init() {
      ZombieEquipment.add(0.03)
         .adultsOnly()
         .slot(EquipmentSlot.HEAD, ObjectRegistry.BREWFEST_HAT_RED)
         .slot(EquipmentSlot.CHEST, ObjectRegistry.BREWFEST_REGALIA)
         .slot(EquipmentSlot.MAINHAND, ObjectRegistry.WHISKEY_AK);
      CompostablesRegistry.init();
      MobEffectRegistry.init();
      ObjectRegistry.init();
      FlammableBlockRegistry.init();
      ArmorSetRegistry.init();
      EntityTypeRegistry.init();
      SoundEventRegistry.init();
      RecipeTypeRegistry.init();
      TabRegistry.init();
      LifecycleEvent.SETUP.register(Brewery::registerFuels);
      LifecycleEvent.SETUP.register(Brewery::registerRarities);
      BrewEvents.loadClass();
      CommonEvents.init();
      BreweryNetworking.init();
      registerEvents();
   }

   private static void registerEvents() {
      PartyStarterEvent partyStarterEvent = new PartyStarterEvent();
      PlayerEvent.ATTACK_ENTITY.register(partyStarterEvent);
   }

   private static void registerRarities() {
      FoundationRarities.register((ItemLike)ObjectRegistry.BREWERY_BANNER.get(), FoundationRarity.LEGENDARY);

      for (RegistrySupplier<Item> piece : List.of(
         ObjectRegistry.BREWFEST_HAT,
         ObjectRegistry.BREWFEST_HAT_RED,
         ObjectRegistry.BREWFEST_REGALIA,
         ObjectRegistry.BREWFEST_TROUSERS,
         ObjectRegistry.BREWFEST_BOOTS,
         ObjectRegistry.BREWFEST_DRESS,
         ObjectRegistry.BREWFEST_BLOUSE,
         ObjectRegistry.BREWFEST_SHOES
      )) {
         FoundationRarities.register((ItemLike)piece.get(), FoundationRarity.RARE);
      }
   }

   public static void registerFuels() {
      FuelRegistry.register(
         300,
         new ItemLike[]{
            (ItemLike)ObjectRegistry.BEER_MUG.get(),
            (ItemLike)ObjectRegistry.BENCH.get(),
            (ItemLike)ObjectRegistry.TABLE.get(),
            (ItemLike)ObjectRegistry.BAR_COUNTER.get(),
            (ItemLike)ObjectRegistry.WOODEN_BREWINGSTATION.get()
         }
      );
      FuelRegistry.register(100, new ItemLike[]{(ItemLike)ObjectRegistry.HOPS.get()});
      FuelRegistry.register(75, new ItemLike[]{(ItemLike)ObjectRegistry.PATTERNED_WOOL.get(), (ItemLike)ObjectRegistry.PATTERNED_CARPET.get()});
      FuelRegistry.register(
         50,
         new ItemLike[]{
            (ItemLike)ObjectRegistry.BREWFEST_BOOTS.get(),
            (ItemLike)ObjectRegistry.BREWFEST_HAT.get(),
            (ItemLike)ObjectRegistry.BREWFEST_DRESS.get(),
            (ItemLike)ObjectRegistry.BREWFEST_REGALIA.get(),
            (ItemLike)ObjectRegistry.BREWFEST_TROUSERS.get()
         }
      );
   }
}
