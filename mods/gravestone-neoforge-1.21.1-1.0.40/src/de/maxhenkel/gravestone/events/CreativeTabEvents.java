package de.maxhenkel.gravestone.events;

import de.maxhenkel.gravestone.Main;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class CreativeTabEvents {
   @SubscribeEvent
   public static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
         event.accept(new ItemStack((ItemLike)Main.GRAVESTONE.get()));
      }
   }
}
