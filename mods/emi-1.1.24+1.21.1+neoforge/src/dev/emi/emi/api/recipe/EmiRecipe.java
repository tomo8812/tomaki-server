package dev.emi.emi.api.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public interface EmiRecipe {
   EmiRecipeCategory getCategory();

   @Nullable
   ResourceLocation getId();

   List<EmiIngredient> getInputs();

   default List<EmiIngredient> getCatalysts() {
      return List.of();
   }

   List<EmiStack> getOutputs();

   int getDisplayWidth();

   int getDisplayHeight();

   void addWidgets(WidgetHolder var1);

   default boolean supportsRecipeTree() {
      return !this.getInputs().isEmpty() && !this.getOutputs().isEmpty();
   }

   default boolean hideCraftable() {
      return false;
   }

   @Nullable
   default RecipeHolder<?> getBackingRecipe() {
      return EmiPort.getRecipe(this.getId());
   }
}
