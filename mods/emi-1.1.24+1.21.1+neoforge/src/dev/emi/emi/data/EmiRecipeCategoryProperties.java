package dev.emi.emi.data;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import java.util.Comparator;
import java.util.function.Supplier;

public class EmiRecipeCategoryProperties {
   public int order;
   public Supplier<EmiRenderable> icon;
   public Supplier<EmiRenderable> simplified;
   public Comparator<EmiRecipe> sort;

   public static int getOrder(EmiRecipeCategory category) {
      EmiRecipeCategoryProperties props = EmiData.categoryPriorities.get(category.getId().toString());
      return props != null ? props.order : 0;
   }

   public static Comparator<EmiRecipe> getSort(EmiRecipeCategory category) {
      EmiRecipeCategoryProperties props = EmiData.categoryPriorities.get(category.getId().toString());
      return props != null && props.sort != null ? props.sort : category.getSort();
   }

   public static EmiRenderable getIcon(EmiRecipeCategory category) {
      EmiRecipeCategoryProperties props = EmiData.categoryPriorities.get(category.getId().toString());
      return props != null && props.icon != null ? props.icon.get() : category.icon;
   }

   public static EmiRenderable getSimplifiedIcon(EmiRecipeCategory category) {
      EmiRecipeCategoryProperties props = EmiData.categoryPriorities.get(category.getId().toString());
      return props != null && props.simplified != null ? props.simplified.get() : category.simplified;
   }
}
