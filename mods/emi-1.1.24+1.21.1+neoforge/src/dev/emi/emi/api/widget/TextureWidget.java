package dev.emi.emi.api.widget;

import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;

public class TextureWidget extends Widget implements WidgetTooltipHolder<TextureWidget> {
   protected final ResourceLocation texture;
   protected final int x;
   protected final int y;
   protected final int width;
   protected final int height;
   protected final int u;
   protected final int v;
   protected final int regionWidth;
   protected final int regionHeight;
   protected final int textureWidth;
   protected final int textureHeight;
   private BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier = (mouseX, mouseY) -> List.of();

   public TextureWidget(
      ResourceLocation texture, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
   ) {
      this.texture = texture;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.u = u;
      this.v = v;
      this.regionWidth = regionWidth;
      this.regionHeight = regionHeight;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
   }

   public TextureWidget(ResourceLocation texture, int x, int y, int width, int height, int u, int v) {
      this(texture, x, y, width, height, u, v, width, height, 256, 256);
   }

   public TextureWidget tooltip(BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier) {
      this.tooltipSupplier = tooltipSupplier;
      return this;
   }

   @Override
   public Bounds getBounds() {
      return new Bounds(this.x, this.y, this.width, this.height);
   }

   @Override
   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      return this.tooltipSupplier.apply(mouseX, mouseY);
   }

   @Override
   public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      context.resetColor();
      context.drawTexture(
         this.texture, this.x, this.y, this.width, this.height, this.u, this.v, this.regionWidth, this.regionHeight, this.textureWidth, this.textureHeight
      );
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object tooltip(BiFunction param1) {
      return this.tooltip(var1);
   }
}
