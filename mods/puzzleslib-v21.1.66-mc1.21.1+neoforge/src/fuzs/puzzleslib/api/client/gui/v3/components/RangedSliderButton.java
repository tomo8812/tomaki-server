package fuzs.puzzleslib.api.client.gui.v3.components;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class RangedSliderButton extends AbstractSliderButton {
   protected final double minValue;
   protected final double maxValue;

   public RangedSliderButton(int x, int y, int width, int height, double value, double minValue, double maxValue) {
      super(x, y, width, height, CommonComponents.EMPTY, 0.0);
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.setAbsoluteValue(value);
   }

   public double getAbsoluteValue() {
      return this.getRelativeValue() * (this.maxValue - this.minValue) + this.minValue;
   }

   public void setAbsoluteValue(double value) {
      this.setRelativeValue((value - this.minValue) / (this.maxValue - this.minValue));
   }

   public double getRelativeValue() {
      return this.value;
   }

   public void setRelativeValue(double value) {
      double oldValue = this.value;
      this.value = Mth.clamp(value, 0.0, 1.0);
      if (oldValue != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   protected void updateMessage() {
      this.setMessage(this.getMessageFromValue(this.getAbsoluteValue()));
   }

   protected void applyValue() {
      this.applyValue(this.getAbsoluteValue());
   }

   protected abstract Component getMessageFromValue(double var1);

   protected abstract void applyValue(double var1);
}
