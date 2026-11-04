package net.satisfy.farm_and_charm.core.compat.rei.silo;

import java.util.Collections;
import java.util.stream.Collectors;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;

public class SiloDisplay extends BasicDisplay {
   public static final CategoryIdentifier<SiloDisplay> SILO_DISPLAY = CategoryIdentifier.of("farm_and_charm", "silo_display");

   public SiloDisplay(SiloRecipe recipe) {
      super(
         recipe.getIngredients().stream().map(EntryIngredients::ofIngredient).collect(Collectors.toList()),
         Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess())))
      );
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return SiloCategory.SILO_DISPLAY;
   }
}
