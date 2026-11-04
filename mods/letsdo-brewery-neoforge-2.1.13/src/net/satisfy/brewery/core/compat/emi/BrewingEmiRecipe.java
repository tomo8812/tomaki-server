package net.satisfy.brewery.core.compat.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.satisfy.brewery.core.recipe.BrewingRecipe;

public class BrewingEmiRecipe extends BasicEmiRecipe {
   public BrewingEmiRecipe(RecipeHolder<BrewingRecipe> holder) {
      super(BreweryEMIPlugin.BREWING, holder.id(), 84, 58);
      ((BrewingRecipe)holder.value()).getIngredients().forEach(ingredient -> this.inputs.add(EmiIngredient.of(ingredient)));
      this.outputs.add(EmiStack.of(((BrewingRecipe)holder.value()).getResultItem(null)));
   }

   public void addWidgets(WidgetHolder widgets) {
      for (int i = 0; i < 3; i++) {
         widgets.addSlot((EmiIngredient)(i < this.inputs.size() ? (EmiIngredient)this.inputs.get(i) : EmiStack.EMPTY), 0, i * 20);
      }

      widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 20);
      widgets.addAnimatedTexture(EmiTexture.FULL_ARROW, 26, 20, 2500, true, false, false);
      widgets.addSlot((EmiIngredient)this.outputs.get(0), 58, 16).large(true).recipeContext(this);
   }
}
