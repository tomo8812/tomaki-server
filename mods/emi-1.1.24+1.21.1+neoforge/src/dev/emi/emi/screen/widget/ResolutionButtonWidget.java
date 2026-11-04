package dev.emi.emi.screen.widget;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiHistory;
import dev.emi.emi.widget.RecipeDefaultButtonWidget;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;

public class ResolutionButtonWidget extends Button {
   public Supplier<Widget> hoveredWidget;
   public EmiIngredient stack;

   public ResolutionButtonWidget(int x, int y, int width, int height, EmiIngredient stack, Supplier<Widget> hoveredWidget) {
      super(x, y, width, height, EmiPort.literal(""), button -> {
         BoM.tree.addResolution(stack, null);
         EmiHistory.pop();
      }, s -> (MutableComponent)s.get());
      this.stack = stack;
      this.hoveredWidget = hoveredWidget;
   }

   public void renderWidget(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      int u = 0;
      if (this.isHovered()) {
         u = 18;
      } else {
         Widget widget = this.hoveredWidget.get();
         if (widget instanceof SlotWidget slot && slot.getRecipe() != null || widget instanceof RecipeDefaultButtonWidget) {
            u = 36;
         }
      }

      EmiTexture.SLOT.render(context.raw(), this.x, this.y, delta);
      context.drawTexture(EmiRenderHelper.WIDGETS, this.x, this.y, u, 128, this.width, this.height);
      if (this.isHovered()) {
         Minecraft client = Minecraft.getInstance();
         raw.renderComponentTooltip(
            client.font,
            List.of(
               EmiPort.translatable("tooltip.emi.resolution"),
               EmiPort.translatable("tooltip.emi.select_resolution"),
               EmiPort.translatable("tooltip.emi.default_resolution"),
               EmiPort.translatable("tooltip.emi.clear_resolution")
            ),
            mouseX,
            mouseY
         );
      }

      this.stack.render(raw, this.x + 1, this.y + 1, delta);
   }
}
