package org.valkyrienskies.eureka.gui.shiphelm

import net.minecraft.class_2561
import net.minecraft.class_327
import net.minecraft.class_332
import net.minecraft.class_4185
import net.minecraft.class_4587
import net.minecraft.class_5348
import net.minecraft.class_4185.class_4241

public class ShipHelmIconButton(x: Int, y: Int, width: Int, height: Int, text: class_2561, font: class_327, onPress: class_4241) : class_4185(
      x, y, width, height, text, onPress, class_4185.field_40754
   ) {
   private final val font: class_327

   init {
      this.font = font
   }

   fun method_48579(guiGraphics: class_332, mouseX: Int, mouseY: Int, partialTicks: Float) {
      guiGraphics.method_25294(
         this.method_46426(), this.method_46427(), this.method_46426() + super.field_22758, this.method_46427() + super.field_22759, -12566464
      )
      guiGraphics.method_25294(
         this.method_46426() + 1,
         this.method_46427() + 1,
         this.method_46426() + super.field_22758 - 1,
         this.method_46427() + super.field_22759 - 1,
         if (super.field_22762) -2039584 else -3750202
      )
      val pose: class_4587 = guiGraphics.method_51448()
      pose.method_22903()
      pose.method_22905(0.65F, 0.65F, 1.0F)
      val tx: Float = (this.method_46426() + (super.field_22758 - this.font.method_27525(this.method_25369() as class_5348) * 0.65F) / 2.0F) / 0.65F
      val ty: Float = (this.method_46427() + (super.field_22759 - this.font.field_2000 * 0.65F) / 2.0F) / 0.65F
      guiGraphics.method_51439(this.font, this.method_25369(), Math.round(tx), Math.round(ty), -12566464, false)
      pose.method_22909()
   }

   public companion object {
      private const val LABEL_SCALE: Float = 0.65F
      private const val BORDER: Int = -12566464
      private const val BG: Int = -3750202
      private const val BG_HOVER: Int = -2039584
      private const val TEXT: Int = -12566464
   }
}
