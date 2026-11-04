package dev.emi.emi.jemi.impl;

import com.google.common.collect.Lists;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.jemi.widget.JemiSlotWidget;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class JemiRecipeSlotDrawable implements IRecipeSlotDrawable {
   public JemiSlotWidget widget;
   public List<IIngredientConsumer> overrides = Lists.newArrayList();

   public Stream<ITypedIngredient<?>> getAllIngredients() {
      return this.widget.slot.getAllIngredients();
   }

   public @Unmodifiable List<@Nullable ITypedIngredient<?>> getAllIngredientsList() {
      return this.widget.slot.getAllIngredientsList();
   }

   public Optional<ITypedIngredient<?>> getDisplayedIngredient() {
      return this.widget.slot.getDisplayedIngredient();
   }

   public RecipeIngredientRole getRole() {
      return this.widget.slot.getRole();
   }

   public void drawHighlight(GuiGraphics raw, int color) {
      this.widget.slot.drawHighlight(raw, color);
   }

   public Optional<String> getSlotName() {
      return this.widget.slot.getSlotName();
   }

   public void draw(GuiGraphics raw) {
   }

   public void drawHoverOverlays(GuiGraphics raw) {
   }

   public List<Component> getTooltip() {
      return Lists.newArrayList();
   }

   public void getTooltip(ITooltipBuilder tooltipBuilder) {
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.widget.getBounds().contains((int)mouseX, (int)mouseY);
   }

   public void setPosition(int x, int y) {
   }

   public IIngredientConsumer createDisplayOverrides() {
      JemiIngredientConsumer consumer = new JemiIngredientConsumer();
      this.overrides.add(consumer);
      return consumer;
   }

   public void clearDisplayOverrides() {
      this.overrides.clear();
   }

   public Rect2i getRect() {
      Bounds bounds = this.widget.getBounds();
      return new Rect2i(bounds.x(), bounds.y(), bounds.width(), bounds.height());
   }

   public Rect2i getAreaIncludingBackground() {
      return this.getRect();
   }
}
