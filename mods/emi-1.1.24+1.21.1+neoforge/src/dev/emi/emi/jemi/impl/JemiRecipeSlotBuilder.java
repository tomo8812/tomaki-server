package dev.emi.emi.jemi.impl;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.placement.IPlaceable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class JemiRecipeSlotBuilder implements IRecipeSlotBuilder {
   public final JemiIngredientAcceptor acceptor;
   public boolean large = false;
   public boolean defaultBackground = false;
   public int x;
   public int y;
   public Optional<String> name = Optional.empty();
   public IRecipeSlotTooltipCallback tooltipCallback;
   public IRecipeSlotRichTooltipCallback richTooltipCallback;
   public JemiRecipeSlot.OffsetDrawable background;
   public JemiRecipeSlot.OffsetDrawable overlay;
   public Map<IIngredientType<?>, JemiRecipeSlot.IngredientRenderer<?>> renderers;
   public JemiRecipeSlot.TankInfo tankInfo;

   public JemiRecipeSlotBuilder(RecipeIngredientRole role, int x, int y) {
      this.acceptor = new JemiIngredientAcceptor(role);
      this.x = x;
      this.y = y;
   }

   public <I> IRecipeSlotBuilder addIngredients(IIngredientType<I> ingredientType, List<@Nullable I> ingredients) {
      this.acceptor.addIngredients(ingredientType, ingredients);
      return this;
   }

   public <I> IRecipeSlotBuilder addIngredient(IIngredientType<I> ingredientType, I ingredient) {
      this.acceptor.addIngredient(ingredientType, ingredient);
      return this;
   }

   public IRecipeSlotBuilder addIngredientsUnsafe(List<?> ingredients) {
      this.acceptor.addIngredientsUnsafe(ingredients);
      return this;
   }

   public IRecipeSlotBuilder addFluidStack(Fluid fluid, long amount) {
      this.acceptor.addFluidStack(fluid, amount);
      return this;
   }

   public IRecipeSlotBuilder addFluidStack(Fluid fluid, long amount, DataComponentPatch componentChanges) {
      this.acceptor.addFluidStack(fluid, amount, componentChanges);
      return this;
   }

   public IRecipeSlotBuilder addTooltipCallback(IRecipeSlotTooltipCallback tooltipCallback) {
      this.tooltipCallback = tooltipCallback;
      return this;
   }

   public IRecipeSlotBuilder setSlotName(String slotName) {
      this.name = Optional.ofNullable(slotName);
      return this;
   }

   public IRecipeSlotBuilder setBackground(IDrawable background, int xOffset, int yOffset) {
      this.background = new JemiRecipeSlot.OffsetDrawable(background, xOffset, yOffset);
      return this;
   }

   public IRecipeSlotBuilder setOverlay(IDrawable overlay, int xOffset, int yOffset) {
      this.overlay = new JemiRecipeSlot.OffsetDrawable(overlay, xOffset, yOffset);
      return this;
   }

   public IRecipeSlotBuilder setFluidRenderer(long capacity, boolean showCapacity, int width, int height) {
      this.tankInfo = new JemiRecipeSlot.TankInfo(width, height, capacity, showCapacity);
      return this;
   }

   public <T> IRecipeSlotBuilder setCustomRenderer(IIngredientType<T> ingredientType, IIngredientRenderer<T> ingredientRenderer) {
      if (this.renderers == null) {
         this.renderers = Maps.newHashMap();
      }

      this.renderers.put(ingredientType, new JemiRecipeSlot.IngredientRenderer(ingredientType, ingredientRenderer));
      return this;
   }

   public IRecipeSlotBuilder addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
      this.acceptor.addTypedIngredients(ingredients);
      return this;
   }

   public IRecipeSlotBuilder addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
      this.acceptor.addOptionalTypedIngredients(ingredients);
      return this;
   }

   public IRecipeSlotBuilder addRichTooltipCallback(IRecipeSlotRichTooltipCallback tooltipCallback) {
      this.richTooltipCallback = tooltipCallback;
      return this;
   }

   public IRecipeSlotBuilder addFluidStack(Fluid fluid) {
      this.acceptor.addFluidStack(fluid);
      return this;
   }

   public IRecipeSlotBuilder setPosition(int xPos, int yPos) {
      this.x = xPos;
      this.y = yPos;
      return this;
   }

   public int getWidth() {
      return this.large ? 26 : 18;
   }

   public int getHeight() {
      return this.large ? 26 : 18;
   }

   public IRecipeSlotBuilder setStandardSlotBackground() {
      this.defaultBackground = true;
      return this;
   }

   public IRecipeSlotBuilder setOutputSlotBackground() {
      this.defaultBackground = true;
      this.large = true;
      return this;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addFluidStack(Fluid param1, long nullx, DataComponentPatch nullxx) {
      return this.addFluidStack(var1, nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addFluidStack(Fluid param1, long nullx) {
      return this.addFluidStack(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addFluidStack(Fluid param1) {
      return this.addFluidStack(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addOptionalTypedIngredients(List param1) {
      return this.addOptionalTypedIngredients(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addTypedIngredients(List param1) {
      return this.addTypedIngredients(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addIngredientsUnsafe(List param1) {
      return this.addIngredientsUnsafe(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addIngredient(IIngredientType param1, Object nullx) {
      return this.addIngredient(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addIngredients(IIngredientType param1, List/* @Nullable */ nullx) {
      return this.addIngredients(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addFluidStack(Fluid param1, long nullx, DataComponentPatch nullxx) {
      return this.addFluidStack(var1, nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addFluidStack(Fluid param1, long nullx) {
      return this.addFluidStack(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addFluidStack(Fluid param1) {
      return this.addFluidStack(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addOptionalTypedIngredients(List param1) {
      return this.addOptionalTypedIngredients(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addTypedIngredients(List param1) {
      return this.addTypedIngredients(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addIngredientsUnsafe(List param1) {
      return this.addIngredientsUnsafe(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addIngredient(IIngredientType param1, Object nullx) {
      return this.addIngredient(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addIngredients(IIngredientType param1, List/* @Nullable */ nullx) {
      return this.addIngredients(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IPlaceable setPosition(int param1, int nullx) {
      return this.setPosition(var1, nullx);
   }
}
