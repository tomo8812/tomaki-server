package dev.emi.emi.jemi;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.jemi.impl.JemiIngredientAcceptor;
import dev.emi.emi.jemi.impl.JemiRecipeLayoutBuilder;
import dev.emi.emi.jemi.impl.JemiRecipeSlot;
import dev.emi.emi.jemi.impl.JemiRecipeSlotBuilder;
import dev.emi.emi.jemi.impl.JemiTooltipBuilder;
import dev.emi.emi.jemi.impl.extras.JemiRecipeExtrasBuilder;
import dev.emi.emi.jemi.impl.extras.JemiWidgetBuilder;
import dev.emi.emi.jemi.widget.JemiSlotWidget;
import dev.emi.emi.jemi.widget.JemiTankWidget;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.screen.EmiScreenManager;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.library.focus.FocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class JemiRecipe<T> implements EmiRecipe {
   public List<EmiIngredient> inputs = Lists.newArrayList();
   public List<EmiIngredient> catalysts = Lists.newArrayList();
   public List<EmiStack> outputs = Lists.newArrayList();
   public EmiRecipeCategory recipeCategory;
   public ResourceLocation originalId;
   public ResourceLocation id;
   public IRecipeCategory<T> category;
   public T recipe;
   public boolean allowTree = true;

   public JemiRecipe(EmiRecipeCategory recipeCategory, IRecipeCategory<T> category, T recipe) {
      this.recipeCategory = recipeCategory;
      this.category = category;
      this.recipe = recipe;
      this.originalId = category.getRegistryName(recipe);
      if (this.originalId != null) {
         this.id = EmiPort.id("jei", "/" + EmiUtil.subId(this.originalId));
      }

      JemiRecipeLayoutBuilder builder = new JemiRecipeLayoutBuilder();
      category.setRecipe(builder, recipe, JemiPlugin.runtime.getJeiHelpers().getFocusFactory().getEmptyFocusGroup());

      for (JemiRecipeSlotBuilder jrsb : builder.slots) {
         jrsb.acceptor.coerceStacks(jrsb.tooltipCallback, jrsb.renderers);
      }

      for (JemiIngredientAcceptor acceptor : builder.ingredients) {
         EmiIngredient stack = acceptor.build();
         if (acceptor.role == RecipeIngredientRole.INPUT) {
            this.inputs.add(stack);
         } else if (acceptor.role == RecipeIngredientRole.CATALYST) {
            this.catalysts.add(stack);
         } else if (acceptor.role == RecipeIngredientRole.OUTPUT) {
            if (stack.getEmiStacks().size() > 1) {
               this.allowTree = false;
            }

            this.outputs.addAll(stack.getEmiStacks());
         }
      }
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return this.recipeCategory;
   }

   @Nullable
   @Override
   public RecipeHolder<?> getBackingRecipe() {
      return EmiPort.getRecipe(this.originalId);
   }

   @Nullable
   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<EmiIngredient> getInputs() {
      return this.inputs;
   }

   @Override
   public List<EmiIngredient> getCatalysts() {
      return this.catalysts;
   }

   @Override
   public List<EmiStack> getOutputs() {
      return this.outputs;
   }

   @Override
   public int getDisplayWidth() {
      return this.category.getWidth();
   }

   @Override
   public int getDisplayHeight() {
      return this.category.getHeight();
   }

   @Override
   public boolean supportsRecipeTree() {
      return this.allowTree && EmiRecipe.super.supportsRecipeTree();
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      Optional<IRecipeLayoutDrawable<T>> opt = JemiPlugin.runtime.getRecipeManager().createRecipeLayoutDrawable(this.category, this.recipe, FocusGroup.EMPTY);
      JemiRecipeLayoutBuilder builder = new JemiRecipeLayoutBuilder();
      this.category.setRecipe(builder, this.recipe, JemiPlugin.runtime.getJeiHelpers().getFocusFactory().getEmptyFocusGroup());

      for (JemiRecipeSlotBuilder jrsb : builder.slots) {
         jrsb.acceptor.coerceStacks(jrsb.tooltipCallback, jrsb.renderers);
      }

      if (opt.isPresent()) {
         widgets.add(new JemiRecipe.JemiWidget(0, 0, this.getDisplayWidth(), this.getDisplayHeight(), opt.get()));

         for (JemiRecipeSlotBuilder sb : builder.slots) {
            JemiRecipeSlot slot = new JemiRecipeSlot(sb);
            if (slot.tankInfo != null && !slot.getIngredients(JemiUtil.getFluidType()).toList().isEmpty()) {
               widgets.add(new JemiTankWidget(slot, this));
            } else {
               widgets.add(new JemiSlotWidget(slot, this));
            }
         }
      }

      try {
         JemiRecipeExtrasBuilder extras = new JemiRecipeExtrasBuilder(null);
         this.category.createRecipeExtras(extras, this.recipe, JemiPlugin.runtime.getJeiHelpers().getFocusFactory().getEmptyFocusGroup());

         for (JemiWidgetBuilder b : extras.widgets) {
            b.addWidgets(widgets);
         }
      } catch (Throwable t) {
         EmiLog.error("Exception adding JEMI extras", t);
      }
   }

   public class JemiWidget extends Widget {
      private final IRecipeLayoutDrawable<T> recipeLayoutDrawable;
      private final Bounds bounds;
      private final int x;
      private final int y;

      public JemiWidget(int x, int y, int w, int h, IRecipeLayoutDrawable<T> recipeLayoutDrawable) {
         this.recipeLayoutDrawable = recipeLayoutDrawable;
         this.bounds = new Bounds(x, y, w, h);
         this.x = x;
         this.y = y;
      }

      @Override
      public Bounds getBounds() {
         return this.bounds;
      }

      @Override
      public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
         EmiDrawContext context = EmiDrawContext.wrap(draw);
         context.push();
         context.matrices().translate(this.x, this.y, 0.0F);
         IDrawable background = JemiRecipe.this.category.getBackground();
         if (background != null) {
            background.draw(context.raw());
         }

         JemiRecipe.this.category.draw(JemiRecipe.this.recipe, this.recipeLayoutDrawable.getRecipeSlotsView(), context.raw(), mouseX, mouseY);
         context.resetColor();
         context.pop();
      }

      @Override
      public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
         JemiTooltipBuilder builder = new JemiTooltipBuilder();
         JemiRecipe.this.category.getTooltip(builder, JemiRecipe.this.recipe, this.recipeLayoutDrawable.getRecipeSlotsView(), mouseX, mouseY);
         return builder.tooltip;
      }

      @Override
      public boolean mouseClicked(int mouseX, int mouseY, int button) {
         return JemiRecipe.this.category.handleInput(JemiRecipe.this.recipe, mouseX, mouseY, Type.MOUSE.getOrCreate(button));
      }

      @Override
      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         return JemiRecipe.this.category
            .handleInput(JemiRecipe.this.recipe, EmiScreenManager.lastMouseX, EmiScreenManager.lastMouseY, InputConstants.getKey(keyCode, scanCode));
      }
   }
}
