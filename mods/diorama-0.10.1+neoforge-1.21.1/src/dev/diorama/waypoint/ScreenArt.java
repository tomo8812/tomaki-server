package dev.diorama.waypoint;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public final class ScreenArt {
   private static final ResourceLocation LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
   static final ResourceLocation SEARCH = ResourceLocation.withDefaultNamespace("icon/search");
   public static final ResourceLocation TEXT_FIELD = ResourceLocation.withDefaultNamespace("widget/text_field");
   static final ResourceLocation TEXT_FIELD_FOCUSED = ResourceLocation.withDefaultNamespace("widget/text_field_highlighted");
   static final ResourceLocation SCROLLER = ResourceLocation.withDefaultNamespace("widget/scroller");
   static final ResourceLocation SCROLLER_BACKGROUND = ResourceLocation.withDefaultNamespace("widget/scroller_background");
   static final int SCROLLBAR_WIDTH = 6;
   public static final int SCROLLER_MIN = 32;

   private ScreenArt() {
   }

   public static void sprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height) {
      RenderSystem.enableBlend();
      graphics.blitSprite(sprite, x, y, width, height);
      RenderSystem.disableBlend();
   }

   public static void list(GuiGraphics graphics, int x0, int y0, int x1, int y1) {
      int w = x1 - x0;
      int h = y1 - y0;
      RenderSystem.enableBlend();
      graphics.blit(LIST_BACKGROUND, x0, y0, x1, y1, w, h, 32, 32);
      graphics.blit(Screen.INWORLD_HEADER_SEPARATOR, x0, y0 - 2, 0.0F, 0.0F, w, 2, 32, 2);
      graphics.blit(Screen.INWORLD_FOOTER_SEPARATOR, x0, y1, 0.0F, 0.0F, w, 2, 32, 2);
      RenderSystem.disableBlend();
   }

   public static void selection(GuiGraphics graphics, int x0, int y0, int x1, int y1) {
      graphics.fill(x0, y0, x1, y1, -1);
      graphics.fill(x0 + 1, y0 + 1, x1 - 1, y1 - 1, -16777216);
   }

   public static void scrollbar(GuiGraphics graphics, int x, int y0, int y1, int handle, int top) {
      sprite(graphics, SCROLLER_BACKGROUND, x, y0, 6, y1 - y0);
      sprite(graphics, SCROLLER, x, y0 + top, 6, handle);
   }
}
