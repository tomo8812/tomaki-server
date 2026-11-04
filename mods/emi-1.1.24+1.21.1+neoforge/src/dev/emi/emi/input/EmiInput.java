package dev.emi.emi.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class EmiInput {
   public static final int CONTROL_MASK = 1;
   public static final int ALT_MASK = 2;
   public static final int SHIFT_MASK = 4;

   public static boolean isControlDown() {
      return Screen.hasControlDown();
   }

   public static boolean isAltDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342)
         || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
   }

   public static boolean isShiftDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)
         || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
   }

   public static int maskFromCode(int keyCode) {
      if (!Minecraft.ON_OSX || keyCode != 343 && keyCode != 347) {
         if (keyCode == 341 || keyCode == 345) {
            return 1;
         } else if (keyCode == 342 || keyCode == 346) {
            return 2;
         } else {
            return keyCode != 340 && keyCode != 344 ? 0 : 4;
         }
      } else {
         return 1;
      }
   }

   public static int getCurrentModifiers() {
      int ret = 0;
      if (isControlDown()) {
         ret |= 1;
      }

      if (isAltDown()) {
         ret |= 2;
      }

      if (isShiftDown()) {
         ret |= 4;
      }

      return ret;
   }
}
