package net.satisfy.brewery.core.compat.rei;

import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.level.ItemLike;
import net.satisfy.brewery.core.compat.rei.category.BrewingStationCategory;
import net.satisfy.brewery.core.compat.rei.display.BrewingStationDisplay;
import net.satisfy.brewery.core.recipe.BrewingRecipe;
import net.satisfy.brewery.core.registry.ObjectRegistry;

public class BreweryREIClientPlugin {
   public static void registerCategories(CategoryRegistry registry) {
      registry.add(new BrewingStationCategory());
      registry.addWorkstations(
         BrewingStationCategory.BREWING_STATION_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.WOODEN_BREWINGSTATION.get())}
      );
      registry.addWorkstations(
         BrewingStationCategory.BREWING_STATION_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.COPPER_BREWINGSTATION.get())}
      );
      registry.addWorkstations(
         BrewingStationCategory.BREWING_STATION_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.NETHERITE_BREWINGSTATION.get())}
      );
   }

   public static void registerDisplays(DisplayRegistry registry) {
      registry.registerFiller(BrewingRecipe.class, BrewingStationDisplay::new);
   }
}
