package dev.emi.emi.api.widget;

import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class DrawableWidget extends Widget implements WidgetTooltipHolder<DrawableWidget> {
   protected final DrawableWidget.DrawableWidgetConsumer consumer;
   protected final Bounds bounds;
   protected final int x;
   protected final int y;
   protected BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier = (mouseX, mouseY) -> List.of();

   public DrawableWidget(int x, int y, int w, int h, DrawableWidget.DrawableWidgetConsumer consumer) {
      this.x = x;
      this.y = y;
      this.bounds = new Bounds(x, y, w, h);
      this.consumer = consumer;
   }

   public DrawableWidget tooltip(BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier) {
      this.tooltipSupplier = tooltipSupplier;
      return this;
   }

   @Override
   public Bounds getBounds() {
      return this.bounds;
   }

   @Override
   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      return this.tooltipSupplier.apply(mouseX, mouseY);
   }

   @Override
   public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      context.push();
      context.matrices().translate(this.x, this.y, 0.0F);
      this.consumer.render(context.raw(), mouseX, mouseY, delta);
      context.pop();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object tooltip(BiFunction param1) {
      return this.tooltip(var1);
   }

   public interface DrawableWidgetConsumer {
      void render(GuiGraphics var1, int var2, int var3, float var4);
   }
}
