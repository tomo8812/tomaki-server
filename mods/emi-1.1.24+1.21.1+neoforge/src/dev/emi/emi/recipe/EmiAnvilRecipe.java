package dev.emi.emi.recipe;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class EmiAnvilRecipe implements EmiRecipe {
   private final EmiStack tool;
   private final EmiIngredient resource;
   private final ResourceLocation id;
   private final int uniq = EmiUtil.RANDOM.nextInt();

   public EmiAnvilRecipe(EmiStack tool, EmiIngredient resource, ResourceLocation id) {
      this.tool = tool;
      this.resource = resource;
      this.id = id;
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return VanillaEmiRecipeCategories.ANVIL_REPAIRING;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<EmiIngredient> getInputs() {
      return List.of(this.tool, this.resource);
   }

   @Override
   public List<EmiStack> getOutputs() {
      return List.of(this.tool);
   }

   @Override
   public boolean supportsRecipeTree() {
      return false;
   }

   @Override
   public int getDisplayWidth() {
      return 125;
   }

   @Override
   public int getDisplayHeight() {
      return 18;
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(EmiTexture.PLUS, 27, 3);
      widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
      widgets.addGeneratedSlot(r -> this.getTool(r, false), this.uniq, 0, 0);
      widgets.addSlot(this.resource, 49, 0);
      widgets.addGeneratedSlot(r -> this.getTool(r, true), this.uniq, 107, 0).recipeContext(this);
   }

   private EmiStack getTool(Random r, boolean repaired) {
      ItemStack stack = this.tool.getItemStack().copy();
      if (stack.getMaxDamage() <= 0) {
         return this.tool;
      }

      int d = r.nextInt(stack.getMaxDamage());
      if (repaired) {
         d -= stack.getMaxDamage() / 4;
         if (d <= 0) {
            return this.tool;
         }
      }

      stack.setDamageValue(d);
      return EmiStack.of(stack);
   }
}
