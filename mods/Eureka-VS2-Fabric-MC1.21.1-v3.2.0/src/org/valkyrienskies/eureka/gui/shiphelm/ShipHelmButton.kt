package org.valkyrienskies.eureka.gui.shiphelm

import net.minecraft.class_2561
import net.minecraft.class_327
import net.minecraft.class_332
import net.minecraft.class_4185
import net.minecraft.class_5481
import net.minecraft.class_4185.class_4241

public class ShipHelmButton(x: Int, y: Int, width: Int, height: Int, text: class_2561, font: class_327, onPress: class_4241) : class_4185(
      x, y, width, height, text, onPress, class_4185.field_40754
   ) {
   private final val font: class_327
   public final var isPressed: Boolean

   init {
      this.font = font
      this.field_22763 = true
   }

   fun method_48579(guiGraphics: class_332, mouseX: Int, mouseY: Int, partialTicks: Float) {
      if (!super.field_22762) {
         this.isPressed = false
      }

      val fill: Int = if (!super.field_22763) -6645094 else (if (this.isPressed) -5723992 else (if (super.field_22762) -2039584 else -3750202))
      val border: Int = if (super.field_22763) -12566464 else -9803158
      val textColor: Int = if (super.field_22763) -12566464 else -8355712
      guiGraphics.method_25294(
         this.method_46426(), this.method_46427(), this.method_46426() + super.field_22758, this.method_46427() + super.field_22759, border
      )
      guiGraphics.method_25294(
         this.method_46426() + 1, this.method_46427() + 1, this.method_46426() + super.field_22758 - 1, this.method_46427() + super.field_22759 - 1, fill
      )
      val var10000: class_5481 = this.method_25369().method_30937()
      guiGraphics.method_51430(
         this.font,
         var10000,
         this.method_46426() + super.field_22758 / 2 - this.font.method_30880(var10000) / 2,
         this.method_46427() + (super.field_22759 - 8) / 2,
         textColor,
         false
      )
   }

   fun method_25348(mouseX: Double, mouseY: Double) {
      this.isPressed = true
      super.method_25348(mouseX, mouseY)
   }

   fun method_25357(mouseX: Double, mouseY: Double) {
      this.isPressed = false
   }

   public companion object {
      private const val BORDER: Int = -12566464
      private const val BG: Int = -3750202
      private const val BG_HOVER: Int = -2039584
      private const val BG_PRESSED: Int = -5723992
      private const val TEXT: Int = -12566464
      private const val DIM_BORDER: Int = -9803158
      private const val DIM_BG: Int = -6645094
      private const val DIM_TEXT: Int = -8355712
   }
}
