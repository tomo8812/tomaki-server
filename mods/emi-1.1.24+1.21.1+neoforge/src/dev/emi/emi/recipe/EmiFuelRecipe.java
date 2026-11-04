package dev.emi.emi.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class EmiFuelRecipe implements EmiRecipe {
   private final EmiIngredient stack;
   private final int time;
   private final ResourceLocation id;

   public EmiFuelRecipe(EmiIngredient stack, int time, ResourceLocation id) {
      this.stack = stack;
      this.time = time;
      this.id = id;
      if (stack.getEmiStacks().get(0).getItemStack().getItem().equals(Items.LAVA_BUCKET)) {
         stack.getEmiStacks().get(0).setRemainder(EmiStack.of(Items.BUCKET));
      }
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return VanillaEmiRecipeCategories.FUEL;
   }

   @Nullable
   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<EmiIngredient> getInputs() {
      return List.of(this.stack);
   }

   @Override
   public List<EmiStack> getOutputs() {
      return List.of();
   }

   @Override
   public int getDisplayWidth() {
      return 144;
   }

   @Override
   public int getDisplayHeight() {
      return 18;
   }

   @Override
   public boolean supportsRecipeTree() {
      return false;
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(EmiTexture.EMPTY_FLAME, 1, 1);
      widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 1, 1, 1000 * this.time / 20, false, true, true);
      widgets.addSlot(this.stack, 18, 0).recipeContext(this);
      widgets.addText(EmiPort.translatable("emi.fuel_time.items", EmiRenderHelper.TEXT_FORMAT.format(this.time / 200.0F)), 38, 5, -1, true);
   }
}
