package dev.terminalmc.clientsort.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.terminalmc.clientsort.mixin.client.accessor.KeyMappingAccessor;
import dev.terminalmc.clientsort.util.Localization;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
   public static final KeyMapping EDIT_KEY = new KeyMapping(
      Localization.translationKey("key", "edit"), Type.KEYSYM, InputConstants.UNKNOWN.getValue(), Localization.translationKey("name")
   );
   public static final KeyMapping CANCEL_AUTO_KEY = new KeyMapping(
      Localization.translationKey("key", "cancelAuto"), Type.KEYSYM, InputConstants.UNKNOWN.getValue(), Localization.translationKey("name")
   );
   public static final KeyMapping SORT_KEY = new KeyMapping(Localization.translationKey("key", "op.sort"), Type.MOUSE, 2, Localization.translationKey("name"));
   public static final KeyMapping STACK_FILL_KEY = new KeyMapping(
      Localization.translationKey("key", "op.stackFill"), Type.KEYSYM, InputConstants.UNKNOWN.getValue(), Localization.translationKey("name")
   );
   public static final KeyMapping MATCH_TRANSFER_KEY = new KeyMapping(
      Localization.translationKey("key", "op.matchTransfer"), Type.KEYSYM, InputConstants.UNKNOWN.getValue(), Localization.translationKey("name")
   );
   public static final KeyMapping TRANSFER_KEY = new KeyMapping(
      Localization.translationKey("key", "op.transfer"), Type.KEYSYM, InputConstants.UNKNOWN.getValue(), Localization.translationKey("name")
   );
   public static final List<KeyMapping> KEYBINDS = List.of(EDIT_KEY, CANCEL_AUTO_KEY, SORT_KEY, STACK_FILL_KEY, MATCH_TRANSFER_KEY, TRANSFER_KEY);

   public static void isolateKeybinds() {
      KEYBINDS.forEach(kb -> KeyMappingAccessor.clientsort$getAll().remove(kb.getName()));
      KeyMapping.resetMapping();
   }

   public static void bindKey(KeyMapping keybind, Key key) {
      keybind.setKey(key);
      KeyMapping.resetMapping();
      Minecraft.getInstance().options.save();
   }

   public static boolean isDown(KeyMapping keybind) {
      return isKeyDown(((KeyMappingAccessor)keybind).clientsort$getKey());
   }

   public static boolean isKeyDown(Key key) {
      long window = Minecraft.getInstance().getWindow().getWindow();
      if (key.equals(InputConstants.UNKNOWN)) {
         return false;
      } else {
         return key.getType().equals(Type.MOUSE) ? GLFW.glfwGetMouseButton(window, key.getValue()) == 1 : GLFW.glfwGetKey(window, key.getValue()) == 1;
      }
   }
}
