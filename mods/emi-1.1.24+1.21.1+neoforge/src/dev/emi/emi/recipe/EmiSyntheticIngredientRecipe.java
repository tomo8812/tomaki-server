package dev.emi.emi.recipe;

import dev.emi.emi.VanillaPlugin;
import dev.emi.emi.api.recipe.EmiIngredientRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiResolutionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class EmiSyntheticIngredientRecipe extends EmiIngredientRecipe {
   private final EmiIngredient ingredient;

   public EmiSyntheticIngredientRecipe(EmiIngredient ingredient) {
      this.ingredient = ingredient;
   }

   @Override
   protected EmiIngredient getIngredient() {
      return this.ingredient;
   }

   @Override
   protected List<EmiStack> getStacks() {
      return this.ingredient.getEmiStacks();
   }

   @Override
   protected EmiRecipe getRecipeContext(EmiStack stack, int offset) {
      return new EmiResolutionRecipe(this.ingredient, stack);
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return VanillaPlugin.INGREDIENT;
   }

   @Override
   public ResourceLocation getId() {
      return null;
   }
}
