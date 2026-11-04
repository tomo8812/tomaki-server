package dev.emi.emi.screen.widget.config;

import dev.emi.emi.EmiPort;
import dev.emi.emi.input.EmiInput;
import dev.emi.emi.screen.widget.SizedButtonWidget;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;

public class IntEdit {
   private static final Pattern NUMBER = Pattern.compile("^-?[0-9]*$");
   public final EditBox text;
   public final Button up;
   public final Button down;

   public IntEdit(int width, IntSupplier getter, IntConsumer setter) {
      Minecraft client = Minecraft.getInstance();
      this.text = new EditBox(client.font, 0, 0, width - 14, 18, EmiPort.literal(""));
      this.text.setValue(getter.getAsInt() + "");
      this.text.setResponder(string -> {
         try {
            if (string.isBlank()) {
               setter.accept(0);
            } else {
               setter.accept(Integer.parseInt(string));
            }
         } catch (Exception var3) {
         }
      });
      this.text.setFilter(s -> NUMBER.matcher(s).matches());
      this.up = new SizedButtonWidget(150, 0, 12, 10, 232, 48, () -> true, button -> {
         setter.accept(getter.getAsInt() + this.getInc());
         this.text.setValue(getter.getAsInt() + "");
      });
      this.down = new SizedButtonWidget(150, 10, 12, 10, 244, 48, () -> true, button -> {
         setter.accept(getter.getAsInt() - this.getInc());
         this.text.setValue(getter.getAsInt() + "");
      });
   }

   public boolean contains(int x, int y) {
      return x > this.text.x && x < this.up.x + this.up.getWidth() && y > this.text.y && y < this.text.y + this.text.getHeight();
   }

   public int getInc() {
      if (EmiInput.isShiftDown()) {
         return 10;
      } else {
         return EmiInput.isControlDown() ? 5 : 1;
      }
   }

   public void setPosition(int x, int y) {
      this.text.x = x + 1;
      this.text.y = y + 1;
      this.up.x = x + this.text.getWidth() + 2;
      this.up.y = y;
      this.down.x = this.up.x;
      this.down.y = y + 10;
   }
}
