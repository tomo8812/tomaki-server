package dev.emi.emi.api.widget;

import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class AnimatedTextureWidget extends TextureWidget {
   protected final int time;
   protected final boolean horizontal;
   protected final boolean endToStart;
   protected final boolean fullToEmpty;

   public AnimatedTextureWidget(
      ResourceLocation texture,
      int x,
      int y,
      int width,
      int height,
      int u,
      int v,
      int regionWidth,
      int regionHeight,
      int textureWidth,
      int textureHeight,
      int time,
      boolean horizontal,
      boolean endToStart,
      boolean fullToEmpty
   ) {
      super(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
      this.time = time;
      this.horizontal = horizontal;
      this.endToStart = endToStart;
      this.fullToEmpty = fullToEmpty;
   }

   public AnimatedTextureWidget(
      ResourceLocation texture, int x, int y, int width, int height, int u, int v, int time, boolean horizontal, boolean endToStart, boolean fullToEmpty
   ) {
      this(texture, x, y, width, height, u, v, width, height, 256, 256, time, horizontal, endToStart, fullToEmpty);
   }

   @Override
   public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      int subTime = (int)(System.currentTimeMillis() % this.time);
      if (this.endToStart ^ this.fullToEmpty) {
         subTime = this.time - subTime;
      }

      int mx = this.x;
      int my = this.y;
      int mw = this.width;
      int mh = this.height;
      int mu = this.u;
      int mv = this.v;
      int mrw = this.regionWidth;
      int mrh = this.regionHeight;
      if (this.horizontal) {
         if (this.endToStart) {
            mx = this.x + this.width * subTime / this.time;
            mu = this.u + this.regionWidth * subTime / this.time;
            mw = this.width - (mx - this.x);
            mrw = this.regionWidth - (mu - this.u);
         } else {
            mw = this.width * subTime / this.time;
            mrw = this.regionWidth * subTime / this.time;
         }
      } else if (this.endToStart) {
         my = this.y + this.height * subTime / this.time;
         mv = this.v + this.regionHeight * subTime / this.time;
         mh = this.height - (my - this.y);
         mrh = this.regionHeight - (mv - this.v);
      } else {
         mh = this.height * subTime / this.time;
         mrh = this.regionHeight * subTime / this.time;
      }

      context.drawTexture(this.texture, mx, my, mw, mh, mu, mv, mrw, mrh, this.textureWidth, this.textureHeight);
   }
}
