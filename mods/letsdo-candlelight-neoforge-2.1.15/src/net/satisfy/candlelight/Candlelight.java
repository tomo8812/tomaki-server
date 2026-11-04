package net.satisfy.candlelight;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.satisfy.candlelight.core.compat.AccessoriesCompat;
import net.satisfy.candlelight.core.event.CommonEvents;
import net.satisfy.candlelight.core.networking.CandlelightMessages;
import net.satisfy.candlelight.core.registry.ArmorSetRegistry;
import net.satisfy.candlelight.core.registry.CompostableRegistry;
import net.satisfy.candlelight.core.registry.DataComponentRegistry;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import net.satisfy.candlelight.core.registry.FlammableBlockRegistry;
import net.satisfy.candlelight.core.registry.MobEffectRegistry;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.candlelight.core.registry.ScreenHandlerTypeRegistry;
import net.satisfy.candlelight.core.registry.SoundEventRegistry;
import net.satisfy.candlelight.core.registry.TabRegistry;
import net.satisfy.candlelight.platform.PlatformHelper;
import net.satisfy.foundation.entity.equipment.ZombieEquipment;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;

public class Candlelight {
   public static final String MOD_ID = "candlelight";

   public static ResourceLocation identifier(String name) {
      return ResourceLocation.fromNamespaceAndPath("candlelight", name);
   }

   public static void init() {
      ZombieEquipment.add(() -> PlatformHelper.zombieCookingHatChance()).adultsOnly().slot(EquipmentSlot.HEAD, ObjectRegistry.COOKING_HAT);
      CompostableRegistry.init();
      DataComponentRegistry.init();
      ObjectRegistry.init();
      FlammableBlockRegistry.init();
      ArmorSetRegistry.init();
      ScreenHandlerTypeRegistry.init();
      MobEffectRegistry.init();
      SoundEventRegistry.init();
      EntityTypeRegistry.init();
      CommonEvents.init();
      TabRegistry.init();
      CandlelightMessages.init();
      LifecycleEvent.SETUP.register(Candlelight::registerRarities);
      if (Platform.isModLoaded("accessories")) {
         AccessoriesCompat.init();
      }
   }

   private static void registerRarities() {
      FoundationRarities.register((ItemLike)ObjectRegistry.CANDLELIGHT_BANNER.get(), FoundationRarity.LEGENDARY);

      for (RegistrySupplier<Item> piece : List.of(
         ObjectRegistry.COOKING_HAT,
         ObjectRegistry.CHEFS_JACKET,
         ObjectRegistry.CHEFS_PANTS,
         ObjectRegistry.CHEFS_BOOTS,
         ObjectRegistry.FLOWER_CROWN,
         ObjectRegistry.DRESS,
         ObjectRegistry.SHIRT,
         ObjectRegistry.FORMAL_SHIRT,
         ObjectRegistry.TROUSERS_AND_VEST,
         ObjectRegistry.NECKTIE
      )) {
         FoundationRarities.register((ItemLike)piece.get(), FoundationRarity.RARE);
      }
   }
}
