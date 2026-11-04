package dev.emi.emi.jemi.impl;

import java.util.List;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import org.jetbrains.annotations.Unmodifiable;

public class JemiRecipeSlotDrawablesView implements IRecipeSlotDrawablesView {
   public @Unmodifiable List<IRecipeSlotDrawable> getSlots() {
      return List.of();
   }
}
