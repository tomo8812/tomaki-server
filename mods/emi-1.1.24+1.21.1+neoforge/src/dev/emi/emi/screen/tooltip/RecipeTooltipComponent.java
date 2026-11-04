package dev.emi.emi.screen.tooltip;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.Font;

public class RecipeTooltipComponent implements EmiTooltipComponent {
   private final EmiRecipe recipe;
   private final boolean showMissing;
   private int overlayColor = -1;
   private int width = 0;
   private int height = 0;

   public RecipeTooltipComponent(EmiRecipe recipe) {
      this(recipe, false);
   }

   public RecipeTooltipComponent(EmiRecipe recipe, int overlayColor) {
      this(recipe, false);
      this.overlayColor = overlayColor;
   }

   public RecipeTooltipComponent(EmiRecipe recipe, boolean showMissing) {
      this.recipe = recipe;
      this.showMissing = showMissing;

      try {
         this.width = recipe.getDisplayWidth() + 8;
         this.height = recipe.getDisplayHeight() + 8 + 2;
      } catch (Exception var4) {
      }
   }

   public EmiRecipe getRecipe() {
      return this.recipe;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth(Font textRenderer) {
      return this.width;
   }

   @Override
   public void drawTooltip(EmiDrawContext context, EmiTooltipComponent.TooltipRenderData render) {
      EmiRenderHelper.renderRecipe(this.recipe, context, 0, 0, this.showMissing, this.overlayColor);
   }
}
