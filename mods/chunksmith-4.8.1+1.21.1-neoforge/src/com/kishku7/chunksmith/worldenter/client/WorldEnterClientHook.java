package com.kishku7.chunksmith.worldenter.client;

import com.kishku7.chunksmith.worldenter.WorldEnterPregen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class WorldEnterClientHook {
   private WorldEnterClientHook() {
   }

   public static void tick(Minecraft client) {
      if (client != null && client.level != null) {
         Screen current = currentScreen(client);
         boolean active = WorldEnterPregen.isActive();
         if (active) {
            if (current == null) {
               showScreen(client, new WorldEnterScreen());
            }
         } else {
            if (current instanceof WorldEnterScreen) {
               showScreen(client, null);
            }
         }
      }
   }

   private static Screen currentScreen(Minecraft client) {
      return client.screen;
   }

   private static void showScreen(Minecraft client, Screen screen) {
      client.setScreen(screen);
   }
}
