package dev.emi.emi.screen.tooltip;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

public class TagTooltipComponent implements EmiTooltipComponent {
   private static final ResourceLocation TEXTURE = EmiPort.id("emi", "textures/gui/widgets.png");
   private static final int MAX_DISPLAYED = 63;
   private final List<EmiStack> stacks;

   public TagTooltipComponent(List<EmiStack> stacks) {
      this.stacks = stacks;
   }

   public int getStackWidth() {
      if (this.stacks.size() < 4) {
         return this.stacks.size();
      } else {
         return this.stacks.size() > 16 ? 8 : 4;
      }
   }

   public int getHeight() {
      int s = this.stacks.size();
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

      for (int i = 0; i < this.stacks.size() && i < 63; i++) {
         context.drawStack(this.stacks.get(i), i % sw * 18, i / sw * 18, 1);
      }

      if (this.stacks.size() > 63) {
         context.resetColor();
         context.drawTexture(TEXTURE, this.getWidth(render.text) - 14, this.getHeight() - 8, 0, 192, 9, 3);
      }
   }
}
