package dev.emi.emi.api.recipe;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class EmiCraftingRecipe implements EmiRecipe {
   protected final ResourceLocation id;
   protected final List<EmiIngredient> input;
   protected final EmiStack output;
   public final boolean shapeless;

   public EmiCraftingRecipe(List<EmiIngredient> input, EmiStack output, ResourceLocation id) {
      this(input, output, id, true);
   }

   public EmiCraftingRecipe(List<EmiIngredient> input, EmiStack output, ResourceLocation id, boolean shapeless) {
      this.input = input;
      this.output = output;
      this.id = id;
      this.shapeless = shapeless;
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return VanillaEmiRecipeCategories.CRAFTING;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<EmiIngredient> getInputs() {
      return this.input;
   }

   @Override
   public List<EmiStack> getOutputs() {
      return List.of(this.output);
   }

   @Override
   public int getDisplayWidth() {
      return 118;
   }

   @Override
   public int getDisplayHeight() {
      return 54;
   }

   public boolean canFit(int width, int height) {
      if (this.input.size() > 9) {
         return false;
      }

      for (int i = 0; i < this.input.size(); i++) {
         int x = i % 3;
         int y = i / 3;
         if (!this.input.get(i).isEmpty() && (x >= width || y >= height)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
      if (this.shapeless) {
         widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);
      }

      int sOff = 0;
      if (!this.shapeless) {
         if (this.canFit(1, 3)) {
            sOff--;
         }

         if (this.canFit(3, 1)) {
            sOff -= 3;
         }
      }

      for (int i = 0; i < 9; i++) {
         int s = i + sOff;
         if (s >= 0 && s < this.input.size()) {
            widgets.addSlot(this.input.get(s), i % 3 * 18, i / 3 * 18);
         } else {
            widgets.addSlot(EmiStack.of(ItemStack.EMPTY), i % 3 * 18, i / 3 * 18);
         }
      }

      widgets.addSlot(this.output, 92, 14).large(true).recipeContext(this);
   }
}
