package org.valkyrienskies.eureka.gui.shiphelm

import net.minecraft.class_2561
import net.minecraft.class_327
import net.minecraft.class_332
import net.minecraft.class_4185
import net.minecraft.class_4587
import net.minecraft.class_4185.class_4241

public class ShipHelmCheckbox(x: Int, y: Int, width: Int, text: class_2561, font: class_327, state: () -> Boolean, onPress: class_4241) : class_4185(
      x, y, width, 9, text, onPress, class_4185.field_40754
   ) {
   private final val font: class_327
   private final val state: () -> Boolean

   init {
      this.font = font
      this.state = state
   }

   fun method_48579(guiGraphics: class_332, mouseX: Int, mouseY: Int, partialTicks: Float) {
      val border: Int = if (super.field_22763) -12566464 else -9803158
      val fill: Int = if (super.field_22763) (if (super.field_22762) -2039584 else -3750202) else -6645094
      val mark: Int = if (super.field_22763) -12566464 else -9803158
      val textColor: Int = if (super.field_22763) -12566464 else -8355712
      guiGraphics.method_25294(this.method_46426(), this.method_46427(), this.method_46426() + 9, this.method_46427() + 9, border)
      guiGraphics.method_25294(this.method_46426() + 1, this.method_46427() + 1, this.method_46426() + 9 - 1, this.method_46427() + 9 - 1, fill)
      if (this.state() as java.lang.Boolean) {
         guiGraphics.method_25294(this.method_46426() + 3, this.method_46427() + 3, this.method_46426() + 9 - 3, this.method_46427() + 9 - 3, mark)
      }

      val pose: class_4587 = guiGraphics.method_51448()
      pose.method_22903()
      pose.method_22905(0.85F, 0.85F, 1.0F)
      val tx: Float = (this.method_46426() + 9 + 4) / 0.85F
      val ty: Float = (this.method_46427() + (9 - this.font.field_2000 * 0.85F) / 2.0F) / 0.85F
      guiGraphics.method_51439(this.font, this.method_25369(), Math.round(tx), Math.round(ty), textColor, false)
      pose.method_22909()
   }

   public companion object {
      public const val BOX: Int = 9
      public const val GAP: Int = 4
      public const val LABEL_SCALE: Float = 0.85F
      private const val BORDER: Int = -12566464
      private const val BG: Int = -3750202
      private const val BG_HOVER: Int = -2039584
      private const val MARK: Int = -12566464
      private const val TEXT: Int = -12566464
      private const val DIM_BORDER: Int = -9803158
      private const val DIM_BG: Int = -6645094
      private const val DIM_MARK: Int = -9803158
      private const val DIM_TEXT: Int = -8355712
   }
}
