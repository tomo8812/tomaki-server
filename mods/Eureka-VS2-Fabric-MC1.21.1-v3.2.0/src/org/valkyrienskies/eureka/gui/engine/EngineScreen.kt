package org.valkyrienskies.eureka.gui.engine

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.class_1661
import net.minecraft.class_2561
import net.minecraft.class_2960
import net.minecraft.class_332
import net.minecraft.class_465
import net.minecraft.class_757

public class EngineScreen(handler: EngineScreenMenu, playerInventory: class_1661, text: class_2561) : class_465(handler, playerInventory, text) {
   fun method_2389(guiGraphics: class_332, partialTicks: Float, mouseX: Int, mouseY: Int) {
      RenderSystem.setShader({ 
         class_757.method_34542()
      })
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
      RenderSystem.setShaderTexture(0, TEXTURE)
      val xP: Int = (super.field_22789 - super.field_2792) / 2
      val yP: Int = (super.field_22790 - super.field_2779) / 2
      guiGraphics.method_51448().method_22903()
      guiGraphics.method_51448().method_22904((double)xP, (double)yP, 0.0)
      guiGraphics.method_51448().method_22905(2.0F, 2.0F, 2.0F)
      val var7: Pair = if ((super.field_2797 as EngineScreenMenu).heatLevel > 1) Pair(5, 195) else Pair(5, 159)
      guiGraphics.method_25302(TEXTURE, 5, 4, (var7.component1() as java.lang.Number).intValue(), (var7.component2() as java.lang.Number).intValue(), 78, 34)
      if ((super.field_2797 as EngineScreenMenu).fuelLeft != 0) {
         val t: Float = 13.0F - (float)(super.field_2797 as EngineScreenMenu).fuelLeft / (super.field_2797 as EngineScreenMenu).fuelTotal * 13.0F
         renderBg$coal(t, guiGraphics, 92, 9, 30, 2.3076923F)
         renderBg$coal(t, guiGraphics, 92, 40, 22, 1.6923077F)
         renderBg$coal(t, guiGraphics, 92, 64, 17, 1.3076923F)
         renderBg$coal(t, guiGraphics, 92, 83, 13, 1.0F)
      }

      val var13: Pair = if ((super.field_2797 as EngineScreenMenu).heatLevel > 3) Pair(5, 86) else Pair(5, 122)
      guiGraphics.method_25302(TEXTURE, 5, 4, (var13.component1() as java.lang.Number).intValue(), (var13.component2() as java.lang.Number).intValue(), 78, 34)
      guiGraphics.method_25302(TEXTURE, 0, 0, 0, 0, super.field_2792 / 2, super.field_2779 / 2)
      guiGraphics.method_51448().method_22909()
   }

   fun method_2388(guiGraphics: class_332, mouseX: Int, mouseY: Int) {
   }

   @JvmStatic
   fun `renderBg$coal`(t: Float, `$guiGraphics`: class_332, xC: Int, yC: Int, heightC: Int, mult: Float) {
      `$guiGraphics`.method_25302(TEXTURE, 5, 4 + 34 - heightC + (int)(t * mult), xC, yC, 79, heightC)
   }

   public companion object {
      internal final val TEXTURE: class_2960
      private const val FIRE_HOLE_X: Int = 5
      private const val FIRE_HOLE_Y: Int = 4
      private const val FIRE_HOLE_WIDTH: Int = 78
      private const val FIRE_HOLE_HEIGHT: Int = 34
      private const val HEATED_GLASS_X: Int = 5
      private const val HEATED_GLASS_Y: Int = 86
      private const val GLASS_X: Int = 5
      private const val GLASS_Y: Int = 122
      private const val HEATED_CONTAINER_X: Int = 5
      private const val HEATED_CONTAINER_Y: Int = 195
      private const val CONTAINER_X: Int = 5
      private const val CONTAINER_Y: Int = 159
      private const val COAL_4_X: Int = 92
      private const val COAL_4_Y: Int = 9
      private const val COAL_3_X: Int = 92
      private const val COAL_3_Y: Int = 40
      private const val COAL_2_X: Int = 92
      private const val COAL_2_Y: Int = 64
      private const val COAL_1_X: Int = 92
      private const val COAL_1_Y: Int = 83
      private const val COAL_WIDTH: Int = 79
      private const val COAL_4_HEIGHT: Int = 30
      private const val COAL_3_HEIGHT: Int = 22
      private const val COAL_2_HEIGHT: Int = 17
      private const val COAL_1_HEIGHT: Int = 13
      private const val COAL_MULTI_MAX: Float = 13.0F
      private const val COAL_4_MULT: Float = 2.3076923F
      private const val COAL_3_MULT: Float = 1.6923077F
      private const val COAL_2_MULT: Float = 1.3076923F
      private const val COAL_1_MULT: Float = 1.0F
   }
}
