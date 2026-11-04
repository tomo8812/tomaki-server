package dev.emi.emi.jemi.impl;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.jemi.JemiUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class JemiRecipeSlot implements IRecipeSlotView {
   public final RecipeIngredientRole role;
   public final boolean large;
   public final boolean defaultBackground;
   public final int x;
   public final int y;
   public final Optional<String> name;
   public final IRecipeSlotTooltipCallback tooltipCallback;
   public final IRecipeSlotRichTooltipCallback richTooltipCallback;
   public final JemiRecipeSlot.OffsetDrawable background;
   public final JemiRecipeSlot.OffsetDrawable overlay;
   public final Map<IIngredientType<?>, JemiRecipeSlot.IngredientRenderer<?>> renderers;
   public final JemiRecipeSlot.TankInfo tankInfo;
   public final EmiIngredient stack;
   public SlotWidget widget;
   public int highlight = 0;

   public JemiRecipeSlot(JemiRecipeSlotBuilder builder) {
      this.role = builder.acceptor.role;
      this.large = builder.large;
      this.defaultBackground = builder.defaultBackground;
      this.x = builder.x;
      this.y = builder.y;
      this.name = builder.name;
      this.tooltipCallback = builder.tooltipCallback;
      this.richTooltipCallback = builder.richTooltipCallback;
      this.background = builder.background;
      this.overlay = builder.overlay;
      this.renderers = builder.renderers;
      this.tankInfo = builder.tankInfo;
      this.stack = builder.acceptor.build();
   }

   public JemiRecipeSlot(RecipeIngredientRole role, EmiStack stack) {
      this.role = role;
      this.large = false;
      this.defaultBackground = false;
      this.x = 0;
      this.y = 0;
      this.name = Optional.empty();
      this.tooltipCallback = null;
      this.richTooltipCallback = null;
      this.background = null;
      this.overlay = null;
      this.renderers = null;
      this.tankInfo = null;
      this.stack = stack;
   }

   public <T> Stream<T> getIngredients(IIngredientType<T> ingredientType) {
      return this.getAllIngredients().filter(t -> t.getType() == ingredientType).map(t -> (T)t.getIngredient());
   }

   public @Unmodifiable List<@Nullable ITypedIngredient<?>> getAllIngredientsList() {
      return this.getAllIngredients().toList();
   }

   public Stream<ITypedIngredient<?>> getAllIngredients() {
      return this.stack.getEmiStacks().stream().map(JemiUtil::getTyped).filter(Optional::isPresent).map(Optional::get);
   }

   public boolean isEmpty() {
      return this.stack.isEmpty();
   }

   public <T> Optional<T> getDisplayedIngredient(IIngredientType<T> ingredientType) {
      Optional<ITypedIngredient<?>> ing = this.getDisplayedIngredient();
      return ing.isPresent() && ing.get().getType() == ingredientType ? Optional.of((T)ing.get().getIngredient()) : Optional.empty();
   }

   public Optional<ITypedIngredient<?>> getDisplayedIngredient() {
      return JemiUtil.getTyped(this.stack.getEmiStacks().get(0));
   }

   public Optional<String> getSlotName() {
      return this.name;
   }

   public RecipeIngredientRole getRole() {
      return this.role;
   }

   public void drawHighlight(GuiGraphics raw, int color) {
      this.highlight = color;
   }

   public record IngredientRenderer<T>(IIngredientType<T> type, IIngredientRenderer<T> renderer) {
   }

   public record OffsetDrawable(IDrawable drawable, int xOff, int yOff) {
   }

   public record TankInfo(int width, int height, long capacity, boolean showCapacity) {
   }
}
