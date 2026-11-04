package net.satisfy.brewery.core.compat.rei.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.core.compat.rei.category.BrewingStationCategory;
import net.satisfy.brewery.core.recipe.BrewingRecipe;

public class BrewingStationDisplay extends BasicDisplay {
   public BrewingStationDisplay(BrewingRecipe recipe) {
      this(
         EntryIngredients.ofIngredients(new ArrayList(recipe.getIngredients())),
         Collections.singletonList(EntryIngredients.of(recipe.getResultItem(null))),
         Optional.ofNullable(recipe.getId())
      );
   }

   public BrewingStationDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location) {
      super(inputs, outputs, location);
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return BrewingStationCategory.BREWING_STATION_DISPLAY;
   }
}
