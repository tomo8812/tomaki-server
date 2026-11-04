package dev.emi.emi.api.recipe;

import dev.emi.emi.api.widget.WidgetHolder;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface EmiRecipeDecorator {
   void decorateRecipe(EmiRecipe var1, WidgetHolder var2);
}
