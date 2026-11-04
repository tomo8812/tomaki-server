package dev.emi.emi.widget;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public class RecipeButtonWidget extends Widget {
   protected final EmiRecipe recipe;
   protected final int x;
   protected final int y;
   protected final int u;
   protected final int v;

   public RecipeButtonWidget(int x, int y, int u, int v, EmiRecipe recipe) {
      this.x = x;
      this.y = y;
      this.u = u;
      this.v = v;
      this.recipe = recipe;
   }

   public int getTextureOffset(int mouseX, int mouseY) {
      return this.getBounds().contains(mouseX, mouseY) ? 12 : 0;
   }

   public void playButtonSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   @Override
   public Bounds getBounds() {
      return new Bounds(this.x, this.y, 12, 12);
   }

   @Override
   public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      context.resetColor();
      context.drawTexture(EmiRenderHelper.BUTTONS, this.x, this.y, 12, 12, this.u, this.v + this.getTextureOffset(mouseX, mouseY), 12, 12, 256, 256);
   }
}
