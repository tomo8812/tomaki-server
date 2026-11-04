package net.satisfy.farm_and_charm.core.compat.rei.roasting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;

public class RoasterDisplay extends BasicDisplay {
   public static final CategoryIdentifier<RoasterDisplay> ROASTER_DISPLAY = CategoryIdentifier.of("farm_and_charm", "roaster_display");

   public RoasterDisplay(RoasterRecipe recipe) {
      super(createInputs(recipe), List.of(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))), Optional.empty());
   }

   private static List<EntryIngredient> createInputs(RoasterRecipe recipe) {
      List<EntryIngredient> inputs = new ArrayList<>();
      ItemStack container = recipe.getContainer();
      inputs.add(EntryIngredients.of(container.isEmpty() ? ItemStack.EMPTY : container));
      int max = Math.min(6, recipe.getIngredients().size());

      for (int i = 0; i < max; i++) {
         inputs.add(EntryIngredients.ofIngredient((Ingredient)recipe.getIngredients().get(i)));
      }

      return inputs;
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return ROASTER_DISPLAY;
   }
}
