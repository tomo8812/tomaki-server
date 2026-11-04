package dev.emi.emi.api.widget;

import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;

public class TextWidget extends Widget {
   private static final Minecraft CLIENT = Minecraft.getInstance();
   protected final FormattedCharSequence text;
   protected final int x;
   protected final int y;
   protected final int color;
   protected final boolean shadow;
   protected TextWidget.Alignment horizontalAlignment = TextWidget.Alignment.START;
   protected TextWidget.Alignment verticalAlignment = TextWidget.Alignment.START;

   public TextWidget(FormattedCharSequence text, int x, int y, int color, boolean shadow) {
      this.text = text;
      this.x = x;
      this.y = y;
      this.color = color;
      this.shadow = shadow;
   }

   public TextWidget horizontalAlign(TextWidget.Alignment alignment) {
      this.horizontalAlignment = alignment;
      return this;
   }

   public TextWidget verticalAlign(TextWidget.Alignment alignment) {
      this.verticalAlignment = alignment;
      return this;
   }

   @Override
   public Bounds getBounds() {
      int width = CLIENT.font.width(this.text);
      int xOff = this.horizontalAlignment.offset(width);
      int yOff = this.verticalAlignment.offset(9);
      return new Bounds(this.x + xOff, this.y + yOff, width, 9);
   }

   @Override
   public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      context.push();
      int xOff = this.horizontalAlignment.offset(CLIENT.font.width(this.text));
      int yOff = this.verticalAlignment.offset(9);
      context.matrices().translate(xOff, yOff, 300.0F);
      if (this.shadow) {
         context.drawTextWithShadow(this.text, this.x, this.y, this.color);
      } else {
         context.drawText(this.text, this.x, this.y, this.color);
      }

      context.pop();
   }

   public enum Alignment {
      START,
      CENTER,
      END;

      public int offset(int length) {
         return switch (this) {
            case START -> 0;
            case CENTER -> -(length / 2);
            case END -> -length;
         };
      }

      // $VF: synthetic method
      private static TextWidget.Alignment[] $values() {
         return new TextWidget.Alignment[]{START, CENTER, END};
      }
   }
}
