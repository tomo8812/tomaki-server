package dev.emi.emi.jemi.widget;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.TankWidget;
import dev.emi.emi.jemi.impl.JemiRecipeSlot;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class JemiTankWidget extends TankWidget {
   private final JemiRecipeSlot slot;
   private final JemiSlotWidget jsw;

   public JemiTankWidget(JemiRecipeSlot slot, EmiRecipe recipe) {
      super(slot.stack, slot.x - 1, slot.y - 1, slot.tankInfo.width() + 2, slot.tankInfo.height() + 2, slot.tankInfo.capacity());
      this.slot = slot;
      slot.widget = this;
      if (slot.getRole() == RecipeIngredientRole.OUTPUT) {
         this.recipeContext(recipe);
      }

      this.drawBack(false);
      this.jsw = new JemiSlotWidget(slot, recipe);
   }

   @Override
   public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      if (this.slot.background != null) {
         this.slot.background.drawable().draw(context.raw(), this.x + 1 + this.slot.background.xOff(), this.y + 1 + this.slot.background.yOff());
      }

      super.render(context.raw(), mouseX, mouseY, delta);
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

   @Override
   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      return this.jsw.getTooltip(mouseX, mouseY);
   }
}
