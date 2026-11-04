package dev.emi.emi.api;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeDecorator;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.ApiStatus.Experimental;

public interface EmiRegistry {
   @Experimental
   <T extends Screen> void addScreenBoundsProvider(Class<T> var1, EmiScreenBoundsProvider<T> var2);

   @Experimental
   void addGenericScreenBoundsProvider(EmiScreenBoundsProvider<Screen> var1);

   @Experimental
   boolean isStackDisabled(EmiIngredient var1);

   RecipeManager getRecipeManager();

   void addCategory(EmiRecipeCategory var1);

   void addWorkstation(EmiRecipeCategory var1, EmiIngredient var2);

   void addRecipe(EmiRecipe var1);

   void removeRecipes(Predicate<EmiRecipe> var1);

   default void removeRecipes(ResourceLocation id) {
      this.removeRecipes(r -> id.equals(r.getId()));
   }

   void addDeferredRecipes(Consumer<Consumer<EmiRecipe>> var1);

   void addEmiStack(EmiStack var1);

   void addEmiStackAfter(EmiStack var1, Predicate<EmiStack> var2);

   default void addEmiStackAfter(EmiStack stack, EmiStack other) {
      this.addEmiStackAfter(stack, s -> s.equals(other));
   }

   void removeEmiStacks(Predicate<EmiStack> var1);

   default void removeEmiStacks(EmiStack stack) {
      this.removeEmiStacks(s -> s.equals(stack));
   }

   @Deprecated
   <T extends EmiIngredient> void addIngredientSerializer(Class<T> var1, EmiIngredientSerializer<T> var2);

   <T extends Screen> void addExclusionArea(Class<T> var1, EmiExclusionArea<T> var2);

   void addGenericExclusionArea(EmiExclusionArea<Screen> var1);

   <T extends Screen> void addDragDropHandler(Class<T> var1, EmiDragDropHandler<T> var2);

   void addGenericDragDropHandler(EmiDragDropHandler<Screen> var1);

   <T extends Screen> void addStackProvider(Class<T> var1, EmiStackProvider<T> var2);

   void addGenericStackProvider(EmiStackProvider<Screen> var1);

   void setDefaultComparison(Object var1, Function<Comparison, Comparison> var2);

   default void setDefaultComparison(Object key, Comparison comparison) {
      this.setDefaultComparison(key, old -> comparison);
   }

   default void setDefaultComparison(EmiStack stack, Function<Comparison, Comparison> comparison) {
      this.setDefaultComparison(stack.getKey(), comparison);
   }

   default void setDefaultComparison(EmiStack stack, Comparison comparison) {
      this.setDefaultComparison(stack.getKey(), old -> comparison);
   }

   @Experimental
   void addAlias(EmiIngredient var1, Component var2);

   <T extends AbstractContainerMenu> void addRecipeHandler(MenuType<T> var1, EmiRecipeHandler<T> var2);

   @Experimental
   void addRecipeDecorator(EmiRecipeDecorator var1);

   @Experimental
   default void addRecipeDecorator(EmiRecipeCategory category, EmiRecipeDecorator decorator) {
      this.addRecipeDecorator((recipe, widgets) -> {
         if (recipe.getCategory() == category) {
            decorator.decorateRecipe(recipe, widgets);
         }
      });
   }
}
