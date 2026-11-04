package net.satisfy.bakery.core.compat.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.satisfy.bakery.core.recipe.JamRecipe;

public class JamPotEmiRecipe extends BasicEmiRecipe {
   private static final int INPUTS = 3;
   private final int cookingTime;

   public JamPotEmiRecipe(RecipeHolder<JamRecipe> holder) {
      super(BakeryEMIPlugin.JAM_COOKING, holder.id(), 118, 26);
      JamRecipe recipe = (JamRecipe)holder.value();
      this.cookingTime = recipe.getCookingTime();
      recipe.getIngredients().stream().limit(3L).map(EmiIngredient::of).forEach(this.inputs::add);
      this.outputs.add(EmiStack.of(recipe.getResultItem(Objects.requireNonNull(Minecraft.getInstance().level).registryAccess())));
   }

   public void addWidgets(WidgetHolder widgets) {
      for (int i = 0; i < 3; i++) {
         widgets.addSlot((EmiIngredient)(i < this.inputs.size() ? (EmiIngredient)this.inputs.get(i) : EmiStack.EMPTY), i * 18, 4);
      }

      widgets.addFillingArrow(60, 5, Math.max(this.cookingTime, 1) * 50);
      widgets.addSlot((EmiIngredient)this.outputs.get(0), 92, 0).large(true).recipeContext(this);
   }
}
