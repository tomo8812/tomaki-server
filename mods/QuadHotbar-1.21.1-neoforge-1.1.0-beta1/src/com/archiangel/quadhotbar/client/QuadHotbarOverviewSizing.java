package com.archiangel.quadhotbar.client;

import com.archiangel.quadhotbar.QuadHotbarConfig;
import com.archiangel.quadhotbar.QuadHotbarOverviewLayout;
import net.minecraft.client.Minecraft;

public final class QuadHotbarOverviewSizing {
   private QuadHotbarOverviewSizing() {
   }

   public static int maxWidth() {
      Minecraft mc = Minecraft.getInstance();
      int limit = QuadHotbarOverviewLayout.maxWidthForScale(mc.getWindow().getGuiScale());
      if (QuadHotbarClientEvents.hasModdedServer()) {
         limit = Math.min(limit, QuadHotbarOverviewLayout.normalizeWidth(QuadHotbarClientEvents.serverPolicy().maxOverviewWidth()));
      }

      int fits = Math.max(1, Math.min(3, (mc.getWindow().getGuiScaledWidth() - 24) / 170));
      return Math.min(limit, fits * 9);
   }

   public static int effectiveWidth() {
      return Math.min(QuadHotbarOverviewLayout.normalizeWidth(QuadHotbarConfig.overviewWidth), maxWidth());
   }
}
