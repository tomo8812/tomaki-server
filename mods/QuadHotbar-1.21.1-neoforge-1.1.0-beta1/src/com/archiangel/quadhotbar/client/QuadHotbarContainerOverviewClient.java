package com.archiangel.quadhotbar.client;

import com.archiangel.quadhotbar.QuadHotbarContainerOverview;
import com.archiangel.quadhotbar.QuadHotbarOverviewMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;

public final class QuadHotbarContainerOverviewClient {
   private QuadHotbarContainerOverviewClient() {
   }

   public static void request() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.screen instanceof AbstractContainerScreen<?> screen) {
         QuadHotbarContainerOverview.requestOpen(screen.getMenu().containerId);
      }
   }

   public static void open(QuadHotbarContainerOverview.Header header) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null
         && mc.screen instanceof AbstractContainerScreen<?> base
         && !(base instanceof QuadHotbarOverviewScreen)
         && mc.player.containerMenu == base.getMenu()
         && base.getMenu().containerId == header.parent()
         && header.pages() >= 1
         && header.pages() <= 100) {
         QuadHotbarOverviewMenu menu = QuadHotbarOverviewMenu.external(
            header.parent(),
            mc.player.getInventory(),
            header.pages(),
            header.active(),
            header.height(),
            base.getMenu(),
            new SimpleContainer(header.pages() * 36),
            header.death()
         );
         mc.setScreen(
            new QuadHotbarOverviewScreen(
               menu,
               mc.player.getInventory(),
               Component.translatable(header.death() ? "quadhotbar.overview.death_title" : "quadhotbar.overview.title"),
               base,
               header.token()
            )
         );
      } else {
         QuadHotbarContainerOverview.close(header.parent(), header.token());
      }
   }

   public static void sync(QuadHotbarContainerOverview.Frame frame) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen instanceof QuadHotbarOverviewScreen screen
         && ((QuadHotbarOverviewMenu)screen.getMenu()).external
         && screen.externalToken() == frame.token()
         && ((QuadHotbarOverviewMenu)screen.getMenu()).containerId == frame.parent()) {
         if (frame.page() == -2) {
            screen.closeExternalPanel();
         } else {
            QuadHotbarOverviewMenu menu = (QuadHotbarOverviewMenu)screen.getMenu();
            if (frame.page() >= 0 && frame.page() < menu.pageCount && frame.stacks().size() == 36) {
               for (int i = 0; i < 36; i++) {
                  ((Slot)menu.slots.get(frame.page() * 36 + i)).set(frame.stacks().get(i));
               }
            }

            menu.setCarried(frame.cursor());
         }
      }
   }
}
