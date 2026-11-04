package dev.emi.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class EmiSmithingRecipe implements EmiRecipe {
   protected final ResourceLocation id;
   protected final EmiIngredient template;
   protected final EmiIngredient input;
   protected final EmiIngredient addition;
   protected final EmiStack output;

   public EmiSmithingRecipe(EmiIngredient template, EmiIngredient input, EmiIngredient addition, EmiStack output, ResourceLocation id) {
      this.id = id;
      this.template = template;
      this.input = input;
      this.addition = addition;
      this.output = output;
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return VanillaEmiRecipeCategories.SMITHING;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<EmiIngredient> getInputs() {
      return List.of(this.template, this.input, this.addition);
   }

   @Override
   public List<EmiStack> getOutputs() {
      return List.of(this.output);
   }

   @Override
   public int getDisplayWidth() {
      return 112;
   }

   @Override
   public int getDisplayHeight() {
      return 18;
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);
      widgets.addSlot(this.template, 0, 0);
      widgets.addSlot(this.input, 18, 0);
      widgets.addSlot(this.addition, 36, 0);
      widgets.addSlot(this.output, 94, 0).recipeContext(this);
   }
}
