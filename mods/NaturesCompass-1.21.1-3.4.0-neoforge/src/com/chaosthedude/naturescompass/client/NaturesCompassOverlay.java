package com.chaosthedude.naturescompass.client;

import com.chaosthedude.naturescompass.config.ConfigHandler;
import com.chaosthedude.naturescompass.items.NaturesCompassItem;
import com.chaosthedude.naturescompass.util.BiomeUtils;
import com.chaosthedude.naturescompass.util.CompassState;
import com.chaosthedude.naturescompass.util.ItemUtils;
import com.chaosthedude.naturescompass.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NaturesCompassOverlay implements Layer {
   public static final Minecraft mc = Minecraft.getInstance();

   public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
      if (mc.player != null
         && mc.level != null
         && !mc.options.hideGui
         && !mc.getDebugOverlay().showDebugScreen()
         && (mc.screen == null || (Boolean)ConfigHandler.CLIENT.displayWithChatOpen.get() && mc.screen instanceof ChatScreen)) {
         Player player = mc.player;
         ItemStack stack = ItemUtils.getHeldNatureCompass(player);
         if (stack != null && stack.getItem() instanceof NaturesCompassItem) {
            NaturesCompassItem compass = (NaturesCompassItem)stack.getItem();
            if (compass.getState(stack) == CompassState.SEARCHING) {
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.status", new Object[0]), 5, 5, 16777215, 0);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.searching", new Object[0]), 5, 5, 11184810, 1);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.biome", new Object[0]), 5, 5, 16777215, 3);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, BiomeUtils.getBiomeName(mc.level, compass.getBiomeKey(stack)), 5, 5, 11184810, 4);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.radius", new Object[0]), 5, 5, 16777215, 6);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(compass.getSearchRadius(stack)), 5, 5, 11184810, 7);
            } else if (compass.getState(stack) == CompassState.FOUND) {
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.status", new Object[0]), 5, 5, 16777215, 0);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.found", new Object[0]), 5, 5, 11184810, 1);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.biome", new Object[0]), 5, 5, 16777215, 3);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, BiomeUtils.getBiomeName(mc.level, compass.getBiomeKey(stack)), 5, 5, 11184810, 4);
               if (compass.shouldDisplayCoordinates(stack)) {
                  RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.coordinates", new Object[0]), 5, 5, 16777215, 6);
                  RenderUtils.drawConfiguredStringOnHUD(guiGraphics, compass.getFoundBiomeX(stack) + ", " + compass.getFoundBiomeZ(stack), 5, 5, 11184810, 7);
                  RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.distance", new Object[0]), 5, 5, 16777215, 9);
                  RenderUtils.drawConfiguredStringOnHUD(
                     guiGraphics,
                     String.valueOf(BiomeUtils.getDistanceToBiome(player, compass.getFoundBiomeX(stack), compass.getFoundBiomeZ(stack))),
                     5,
                     5,
                     11184810,
                     10
                  );
               }
            } else if (compass.getState(stack) == CompassState.NOT_FOUND) {
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.status", new Object[0]), 5, 5, 16777215, 0);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.notFound", new Object[0]), 5, 5, 11184810, 1);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.biome", new Object[0]), 5, 5, 16777215, 3);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, BiomeUtils.getBiomeName(mc.level, compass.getBiomeKey(stack)), 5, 5, 11184810, 4);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.radius", new Object[0]), 5, 5, 16777215, 6);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(compass.getSearchRadius(stack)), 5, 5, 11184810, 7);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.naturescompass.samples", new Object[0]), 5, 5, 16777215, 9);
               RenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(compass.getSamples(stack)), 5, 5, 11184810, 10);
            }
         }
      }
   }
}
