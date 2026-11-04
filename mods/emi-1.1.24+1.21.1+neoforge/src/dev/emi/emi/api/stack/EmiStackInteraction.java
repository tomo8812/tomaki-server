package dev.emi.emi.api.stack;

import dev.emi.emi.api.recipe.EmiRecipe;
import org.jetbrains.annotations.Nullable;

public class EmiStackInteraction {
   public static final EmiStackInteraction EMPTY = new EmiStackInteraction(EmiStack.EMPTY, null, false);
   private final EmiIngredient stack;
   private final EmiRecipe recipe;
   private final boolean clickable;

   public EmiStackInteraction(EmiIngredient stack) {
      this(stack, null, true);
   }

   public EmiStackInteraction(EmiIngredient stack, @Nullable EmiRecipe recipe, boolean clickable) {
      this.stack = stack;
      this.recipe = recipe;
      this.clickable = clickable;
   }

   public EmiIngredient getStack() {
      return this.stack;
   }

   @Nullable
   public EmiRecipe getRecipeContext() {
      return this.recipe;
   }

   public boolean isClickable() {
      return this.clickable;
   }

   public boolean isEmpty() {
      return this.stack.isEmpty();
   }
}
