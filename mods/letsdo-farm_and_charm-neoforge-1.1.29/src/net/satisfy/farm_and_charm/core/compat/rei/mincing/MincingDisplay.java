package net.satisfy.farm_and_charm.core.compat.rei.mincing;

import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;

public class MincingDisplay extends BasicDisplay {
   public static final CategoryIdentifier<MincingDisplay> MINCING_DISPLAY = CategoryIdentifier.of("farm_and_charm", "mincing_display");

   public MincingDisplay(MincerRecipe recipe) {
      super(List.of(EntryIngredients.ofIngredient(recipe.getInput())), List.of(EntryIngredients.of(recipe.getOutput())), Optional.empty());
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return MINCING_DISPLAY;
   }
}
