package dev.emi.emi.screen.tooltip;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

public class IngredientTooltipComponent implements EmiTooltipComponent {
   private static final ResourceLocation TEXTURE = EmiPort.id("emi", "textures/gui/widgets.png");
   private static final int MAX_DISPLAYED = 63;
   private final List<? extends EmiIngredient> ingredients;

   public IngredientTooltipComponent(List<? extends EmiIngredient> ingredients) {
      this.ingredients = ingredients;
   }

   public int getStackWidth() {
      if (this.ingredients.size() < 4) {
         return this.ingredients.size();
      } else {
         return this.ingredients.size() > 16 ? 8 : 4;
      }
   }

   public int getHeight() {
      int s = this.ingredients.size();
      if (s > 63) {
         s = 63;
      }

      return ((s - 1) / this.getStackWidth() + 1) * 18;
   }

   public int getWidth(Font textRenderer) {
      return 18 * this.getStackWidth();
   }

   @Override
   public void drawTooltip(EmiDrawContext context, EmiTooltipComponent.TooltipRenderData render) {
      int sw = this.getStackWidth();

      for (int i = 0; i < this.ingredients.size() && i < 63; i++) {
         context.drawStack(this.ingredients.get(i), i % sw * 18, i / sw * 18);
      }

      if (this.ingredients.size() > 63) {
         context.resetColor();
         context.drawTexture(TEXTURE, this.getWidth(render.text) - 14, this.getHeight() - 8, 0, 192, 9, 3);
      }
   }
}
