package net.satisfy.farm_and_charm.core.compat.rei.cutting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;

public class CuttingBoardDisplay extends BasicDisplay {
   public static final CategoryIdentifier<CuttingBoardDisplay> ID = CategoryIdentifier.of("farm_and_charm", "cutting_board");

   public CuttingBoardDisplay(CuttingBoardRecipe recipe) {
      super(List.of(EntryIngredients.ofIngredient(recipe.getIngredient())), outputs(recipe), Optional.empty());
   }

   private static List<EntryIngredient> outputs(CuttingBoardRecipe recipe) {
      List<EntryIngredient> outputs = new ArrayList<>();
      outputs.add(EntryIngredients.of(recipe.getResult()));

      for (ItemStack byproduct : recipe.getByproducts()) {
         outputs.add(EntryIngredients.of(byproduct));
      }

      return outputs;
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return ID;
   }
}
