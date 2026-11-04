package dev.emi.emi.api.recipe;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public abstract class EmiPatternCraftingRecipe extends EmiCraftingRecipe {
   protected final int unique = EmiUtil.RANDOM.nextInt();

   public EmiPatternCraftingRecipe(List<EmiIngredient> input, EmiStack output, ResourceLocation id) {
      super(input, output, id);
   }

   public EmiPatternCraftingRecipe(List<EmiIngredient> input, EmiStack output, ResourceLocation id, boolean shapeless) {
      super(input, output, id, shapeless);
   }

   public abstract SlotWidget getInputWidget(int var1, int var2, int var3);

   public abstract SlotWidget getOutputWidget(int var1, int var2);

   @Override
   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
      if (this.shapeless) {
         widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);
      }

      for (int i = 0; i < 9; i++) {
         widgets.add(this.getInputWidget(i, i % 3 * 18, i / 3 * 18));
      }

      widgets.add(this.getOutputWidget(92, 14).large(true).recipeContext(this));
   }

   @Override
   public boolean supportsRecipeTree() {
      return false;
   }
}
