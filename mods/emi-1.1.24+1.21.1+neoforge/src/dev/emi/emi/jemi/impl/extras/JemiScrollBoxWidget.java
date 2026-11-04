package dev.emi.emi.jemi.impl.extras;

import java.util.List;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IScrollBoxWidget;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.FormattedText;

public class JemiScrollBoxWidget implements IScrollBoxWidget {
   private static final int SCROLL_BAR_WIDTH = 18;
   public int x;
   public int y;
   public int width;
   public int height;

   public JemiScrollBoxWidget(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width + 18;
      this.height = height;
   }

   public ScreenPosition getPosition() {
      return new ScreenPosition(this.x, this.y);
   }

   public ScreenRectangle getArea() {
      return new ScreenRectangle(this.getPosition(), this.width, this.height);
   }

   public int getContentAreaWidth() {
      return this.width - 18;
   }

   public int getContentAreaHeight() {
      return this.height;
   }

   public IScrollBoxWidget setContents(IDrawable contents) {
      return this;
   }

   public IScrollBoxWidget setContents(List<FormattedText> text) {
      return this;
   }
}
