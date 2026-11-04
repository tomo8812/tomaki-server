package net.satisfy.farm_and_charm.core.compat.rei.doughing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;

public class CraftingBowlDisplay extends BasicDisplay {
   public CraftingBowlDisplay(CraftingBowlRecipe recipe) {
      super(
         createInputs(recipe), Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))), Optional.of(recipe.getId())
      );
   }

   private static List<EntryIngredient> createInputs(CraftingBowlRecipe recipe) {
      List<EntryIngredient> inputs = new ArrayList<>();
      int max = Math.min(4, recipe.getIngredients().size());

      for (int i = 0; i < max; i++) {
         inputs.add(EntryIngredients.ofIngredient((Ingredient)recipe.getIngredients().get(i)));
      }

      return inputs;
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return CraftingBowlCategory.CRAFTING_BOWL_DISPLAY;
   }
}
