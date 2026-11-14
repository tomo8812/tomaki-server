package com.chaosthedude.naturescompass.registry;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.chaosthedude.naturescompass.items.NaturesCompassItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = "naturescompass", bus = Bus.MOD)
public class NaturesCompassRegistry {
   @SubscribeEvent
   public static void register(RegisterEvent event) {
      event.register(BuiltInRegistries.ITEM.key(), registry -> {
         NaturesCompass.naturesCompass = new NaturesCompassItem();
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "naturescompass"), NaturesCompass.naturesCompass);
      });
      event.register(BuiltInRegistries.DATA_COMPONENT_TYPE.key(), registry -> {
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "biome_id"), NaturesCompass.BIOME_ID);
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "compass_state"), NaturesCompass.COMPASS_STATE);
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "found_x"), NaturesCompass.FOUND_X);
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "found_z"), NaturesCompass.FOUND_Z);
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "search_radius"), NaturesCompass.SEARCH_RADIUS);
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "samples"), NaturesCompass.SAMPLES);
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "display_coords"), NaturesCompass.DISPLAY_COORDS);
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "prev_pos"), NaturesCompass.PREV_POS);
         registry.register(ResourceLocation.fromNamespaceAndPath("naturescompass", "damage"), NaturesCompass.DAMAGE);
      });
   }
}
