package dev.emi.emi.jemi.impl;

import com.google.common.collect.Lists;
import java.util.List;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.widgets.ISlottedWidgetFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;

public class JemiRecipeLayoutBuilder implements IRecipeLayoutBuilder {
   public final List<JemiIngredientAcceptor> ingredients = Lists.newArrayList();
   public final List<JemiRecipeSlotBuilder> slots = Lists.newArrayList();
   public boolean shapeless = false;

   public IRecipeSlotBuilder addSlot(RecipeIngredientRole role) {
      return this.addSlot(role, 0, 0);
   }

   public IRecipeSlotBuilder addSlot(RecipeIngredientRole recipeIngredientRole, int x, int y) {
      JemiRecipeSlotBuilder builder = new JemiRecipeSlotBuilder(recipeIngredientRole, x, y);
      this.ingredients.add(builder.acceptor);
      this.slots.add(builder);
      return builder;
   }

   public IIngredientAcceptor<?> addInvisibleIngredients(RecipeIngredientRole recipeIngredientRole) {
      JemiIngredientAcceptor acceptor = new JemiIngredientAcceptor(recipeIngredientRole);
      this.ingredients.add(acceptor);
      return acceptor;
   }

   public void moveRecipeTransferButton(int posX, int posY) {
   }

   public void setShapeless() {
      this.shapeless = true;
   }

   public void setShapeless(int posX, int posY) {
      this.shapeless = true;
   }

   public void createFocusLink(IIngredientAcceptor<?>... slots) {
   }

   public IRecipeSlotBuilder addSlotToWidget(RecipeIngredientRole role, ISlottedWidgetFactory<?> widgetFactory) {
      return this.addSlot(role, 0, 0);
   }
}
