package dev.emi.emi.jemi.impl.extras;

import java.util.List;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.ITextWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;

public class JemiTextWidget extends JemiPlaceable<ITextWidget> implements ITextWidget {
   public int color = -1;
   public boolean shadow = true;
   public int spacing = 0;
   public HorizontalAlignment horizontal = HorizontalAlignment.LEFT;
   public VerticalAlignment vertical = VerticalAlignment.TOP;
   public List<FormattedText> text;

   public JemiTextWidget(List<FormattedText> text, int width, int height) {
      super(width, height);
      this.text = text;
   }

   public ITextWidget setFont(Font font) {
      return this;
   }

   public ITextWidget setColor(int color) {
      this.color = color;
      return this;
   }

   public ITextWidget setLineSpacing(int spacing) {
      this.spacing = spacing;
      return this;
   }

   public ITextWidget setShadow(boolean shadow) {
      this.shadow = shadow;
      return this;
   }

   public ITextWidget setTextAlignment(HorizontalAlignment horizontalAlignment) {
      this.horizontal = horizontalAlignment;
      return this;
   }

   public ITextWidget setTextAlignment(VerticalAlignment verticalAlignment) {
      this.vertical = verticalAlignment;
      return this;
   }
}
