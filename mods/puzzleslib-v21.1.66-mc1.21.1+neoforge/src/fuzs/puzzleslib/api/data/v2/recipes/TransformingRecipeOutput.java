package fuzs.puzzleslib.api.data.v2.recipes;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.function.UnaryOperator;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public interface TransformingRecipeOutput extends RecipeOutput {
   static RecipeOutput transformed(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator) {
      return ProxyImpl.get().getTransformingRecipeOutput(recipeOutput, operator);
   }

   RecipeOutput recipeOutput();

   UnaryOperator<Recipe<?>> operator();

   default void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
      this.recipeOutput().accept(id, this.operator().apply(recipe), advancement);
   }

   default Builder advancement() {
      return this.recipeOutput().advancement();
   }
}
