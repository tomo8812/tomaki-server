package dev.emi.emi.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.config.FluidUnit;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;

public class EmiCookingRecipe implements EmiRecipe {
   private final ResourceLocation id;
   private final EmiRecipeCategory category;
   private final EmiIngredient input;
   private final EmiStack output;
   private final AbstractCookingRecipe recipe;
   private final int fuelMultiplier;
   private final boolean infiniBurn;

   public EmiCookingRecipe(AbstractCookingRecipe recipe, EmiRecipeCategory category, int fuelMultiplier, boolean infiniBurn) {
      this.id = EmiPort.getId(recipe);
      this.category = category;
      this.input = EmiIngredient.of((Ingredient)recipe.getIngredients().get(0));
      this.output = EmiStack.of(EmiPort.getOutput(recipe));
      if (this.input.getEmiStacks().get(0).getItemStack().is(Items.WET_SPONGE)) {
         this.input.getEmiStacks().get(0).setRemainder(EmiStack.of(Fluids.WATER, FluidUnit.BUCKET));
      }

      this.recipe = recipe;
      this.fuelMultiplier = fuelMultiplier;
      this.infiniBurn = infiniBurn;
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return this.category;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<EmiIngredient> getInputs() {
      return List.of(this.input);
   }

   @Override
   public List<EmiStack> getOutputs() {
      return List.of(this.output);
   }

   @Override
   public int getDisplayWidth() {
      return 82;
   }

   @Override
   public int getDisplayHeight() {
      return 38;
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      widgets.addFillingArrow(24, 5, 50 * this.recipe.getCookingTime())
         .tooltip(
            (mx, my) -> List.of(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.cooking.time", this.recipe.getCookingTime() / 20.0F))))
         );
      if (this.infiniBurn) {
         widgets.addTexture(EmiTexture.FULL_FLAME, 1, 24);
      } else {
         widgets.addTexture(EmiTexture.EMPTY_FLAME, 1, 24);
         widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 1, 24, 4000 / this.fuelMultiplier, false, true, true);
      }

      widgets.addText(EmiPort.ordered(EmiPort.translatable("emi.cooking.experience", this.recipe.getExperience())), 26, 28, -1, true);
      widgets.addSlot(this.input, 0, 4);
      widgets.addSlot(this.output, 56, 0).large(true).recipeContext(this);
   }
}
