package dev.emi.emi.jemi.widget;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.jemi.JemiStack;
import dev.emi.emi.jemi.JemiUtil;
import dev.emi.emi.jemi.impl.JemiIngredientAcceptor;
import dev.emi.emi.jemi.impl.JemiRecipeSlot;
import dev.emi.emi.jemi.impl.JemiTooltipBuilder;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiLog;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class JemiSlotWidget extends SlotWidget {
   public final JemiRecipeSlot slot;

   public JemiSlotWidget(JemiRecipeSlot slot, EmiRecipe recipe) {
      super(slot.stack, slot.x - (slot.large ? 6 : 1), slot.y - (slot.large ? 6 : 1));
      this.slot = slot;
      slot.widget = this;
      if (slot.getRole() == RecipeIngredientRole.OUTPUT) {
         this.recipeContext(recipe);
      }

      this.drawBack(slot.defaultBackground);
      IIngredientRenderer<?> renderer = this.getRenderer();
      if (renderer != null) {
         this.customBackground(null, 0, 0, renderer.getWidth() + 2, renderer.getHeight() + 2);
      } else if (slot.large) {
         this.large(true);
      }
   }

   private ITypedIngredient<?> getIngredient() {
      if (this.slot.renderers != null) {
         Optional<ITypedIngredient<?>> opt = JemiUtil.getTyped(this.getStack().getEmiStacks().get(0));
         if (opt.isPresent()) {
            return opt.get();
         }
      }

      return null;
   }

   private IIngredientRenderer<?> getRenderer() {
      ITypedIngredient<?> typed = this.getIngredient();
      return typed != null && this.slot.renderers.containsKey(typed.getType()) ? this.slot.renderers.get(typed.getType()).renderer() : null;
   }

   @Override
   public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      if (this.slot.background != null) {
         this.slot.background.drawable().draw(raw, this.x + 1 + this.slot.background.xOff(), this.y + 1 + this.slot.background.yOff());
      }

      super.render(raw, mouseX, mouseY, delta);
   }

   @Override
   public void drawStack(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      IIngredientRenderer renderer = this.getRenderer();
      if (renderer != null) {
         ITypedIngredient<?> typed = this.getIngredient();
         Bounds bounds = this.getBounds();
         int xOff = bounds.x() + (bounds.width() - 16) / 2 + (16 - renderer.getWidth()) / 2;
         int yOff = bounds.y() + (bounds.height() - 16) / 2 + (16 - renderer.getHeight()) / 2;
         context.enableBlend();
         context.push();
         context.matrices().translate(xOff, yOff, 0.0F);
         renderer.render(context.raw(), typed.getIngredient());
         context.pop();
      } else {
         super.drawStack(context.raw(), mouseX, mouseY, delta);
      }
   }

   @Override
   public void drawOverlay(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      if (this.slot.overlay != null) {
         context.enableBlend();
         context.push();
         context.matrices().translate(0.0F, 0.0F, 200.0F);
         this.slot.overlay.drawable().draw(context.raw(), this.x + 1 + this.slot.overlay.xOff(), this.y + 1 + this.slot.overlay.yOff());
         context.pop();
      }

      super.drawOverlay(context.raw(), mouseX, mouseY, delta);
   }

   public static void addTooltip(List<ClientTooltipComponent> list, JemiRecipeSlot slot, EmiIngredient stack, IIngredientRenderer<?> renderer) {
      if (renderer != null && stack.getEmiStacks().size() == 1 && stack.getEmiStacks().get(0) instanceof JemiStack js) {
         JemiStack var10 = js.copy();
         var10.renderer = (IIngredientRenderer<T>)renderer;
         stack = var10;
      }

      list.addAll(stack.getTooltip());
      if (slot.richTooltipCallback != null) {
         try {
            JemiTooltipBuilder tooltipBuilder = new JemiTooltipBuilder();
            slot.richTooltipCallback.onRichTooltip(slot, tooltipBuilder);
            list.addAll(tooltipBuilder.tooltip);
         } catch (Exception e) {
            EmiLog.error("Error initializing JEI TooltipBuilder", e);
         }
      } else if (slot.tooltipCallback != null) {
         try {
            List<Component> event = Lists.newArrayList();
            List<Component> original = stack.getEmiStacks().get(0).getTooltipText();
            Set<Component> toRemove = original.stream().collect(Collectors.toSet());
            event.addAll(stack.getEmiStacks().get(0).getTooltipText());
            slot.tooltipCallback.onTooltip(slot, event);
            int index = Math.min(list.size(), 1);
            if (!event.isEmpty()) {
               list.addAll(
                  index,
                  event.stream()
                     .filter(t -> !toRemove.contains(t) && !JemiIngredientAcceptor.FLUID_END.matcher(t.getString()).find())
                     .map(t -> ClientTooltipComponent.create(t.getVisualOrderText()))
                     .toList()
               );
            }
         } catch (Exception e) {
            EmiLog.error("Error initializing JEI tooltip", e);
         }
      }
   }

   @Override
   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      List<ClientTooltipComponent> list = Lists.newArrayList();
      if (this.getStack().isEmpty()) {
         return List.of();
      }

      addTooltip(list, this.slot, this.getStack(), this.getRenderer());
      this.addSlotTooltip(list);
      return list;
   }
}
