package dev.emi.emi.bom;

import com.google.common.collect.Maps;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import java.util.Map;

public class MaterialTree {
   public MaterialNode goal;
   public TreeCost cost = new TreeCost();
   public Map<EmiIngredient, EmiRecipe> resolutions = Maps.newHashMap();
   public long batches = 1L;

   public MaterialTree(EmiRecipe recipe) {
      EmiStack output = recipe.getOutputs().get(0);
      this.goal = new MaterialNode(output);
      this.goal.defineRecipe(recipe);
      this.recalculate();
   }

   public EmiRecipe getRecipe(EmiIngredient stack) {
      EmiRecipe recipe = this.resolutions.get(stack);
      if (recipe == null && !this.resolutions.containsKey(stack)) {
         recipe = BoM.getRecipe(stack);
      }

      return recipe;
   }

   public void addResolution(EmiIngredient ingredient, EmiRecipe recipe) {
      this.resolutions.put(ingredient, recipe);
      if (ingredient.equals(this.goal.ingredient)) {
         this.goal.defineRecipe(recipe);
         this.goal.amount = ingredient.getAmount();
      }

      this.recalculate();
   }

   public void recalculate() {
      this.goal.recalculate(this);
   }

   public void calculateProgress(EmiPlayerInventory inventory) {
      this.cost.calculateProgress(this.goal, this.batches, inventory);
   }

   public void calculateCost() {
      this.cost.calculate(this.goal, this.batches);
   }
}
