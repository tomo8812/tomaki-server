package fuzs.puzzleslib.api.client.gui.v2;

import fuzs.puzzleslib.impl.client.gui.SingleTextureAtlasSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice.Border;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class GuiGraphicsHelper {
   private GuiGraphicsHelper() {
   }

   public static void prepare8xTextOutline(GuiGraphics guiGraphics, Font font, Component component, int posX, int posY, int color, int backgroundColor) {
      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            if (i != 0 || j != 0) {
               guiGraphics.drawString(font, component, posX + i, posY + j, backgroundColor, false);
            }
         }
      }

      guiGraphics.drawString(font, component, posX, posY, color, false);
   }

   public static void fillFrame(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int borderSize, int color) {
      fillFrameArea(guiGraphics, posX, posY, posX + width, posY + height, borderSize, color);
   }

   public static void fillFrameArea(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY, int borderSize, int color) {
      guiGraphics.fill(minX, minY, maxX, minY + borderSize, color);
      guiGraphics.fill(minX, maxY - borderSize, maxX, maxY, color);
      guiGraphics.fill(minX, minY + borderSize, minX + borderSize, maxY - borderSize, color);
      guiGraphics.fill(maxX - borderSize, minY + borderSize, maxX, maxY - borderSize, color);
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation identifier,
      int x,
      int y,
      int width,
      int height,
      int borderSize,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset
   ) {
      blitNineSliced(guiGraphics, identifier, x, y, width, height, borderSize, borderSize, spriteWidth, spriteHeight, uOffset, vOffset);
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation identifier,
      int x,
      int y,
      int width,
      int height,
      int borderWidth,
      int borderHeight,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset
   ) {
      blitNineSliced(
         guiGraphics, identifier, x, y, width, height, borderWidth, borderHeight, borderWidth, borderHeight, spriteWidth, spriteHeight, uOffset, vOffset
      );
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation identifier,
      int x,
      int y,
      int width,
      int height,
      int borderLeft,
      int borderTop,
      int borderRight,
      int borderBottom,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset
   ) {
      blitNineSliced(
         guiGraphics, identifier, x, y, width, height, borderLeft, borderTop, borderRight, borderBottom, spriteWidth, spriteHeight, uOffset, vOffset, -1
      );
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation identifier,
      int x,
      int y,
      int width,
      int height,
      int borderLeft,
      int borderTop,
      int borderRight,
      int borderBottom,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset,
      int color
   ) {
      blitNineSliced(
         guiGraphics,
         identifier,
         x,
         y,
         width,
         height,
         borderLeft,
         borderTop,
         borderRight,
         borderBottom,
         spriteWidth,
         spriteHeight,
         uOffset,
         vOffset,
         256,
         256,
         color
      );
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation identifier,
      int x,
      int y,
      int width,
      int height,
      int borderLeft,
      int borderTop,
      int borderRight,
      int borderBottom,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset,
      int textureWidth,
      int textureHeight
   ) {
      blitNineSliced(
         guiGraphics,
         identifier,
         x,
         y,
         width,
         height,
         borderLeft,
         borderTop,
         borderRight,
         borderBottom,
         spriteWidth,
         spriteHeight,
         uOffset,
         vOffset,
         textureWidth,
         textureHeight,
         -1
      );
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation identifier,
      int x,
      int y,
      int width,
      int height,
      int borderLeft,
      int borderTop,
      int borderRight,
      int borderBottom,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset,
      int textureWidth,
      int textureHeight,
      int color
   ) {
      SingleTextureAtlasSprite textureAtlasSprite = new SingleTextureAtlasSprite(
         identifier, spriteWidth, spriteHeight, uOffset, vOffset, textureWidth, textureHeight
      );
      NineSlice nineSlice = new NineSlice(spriteWidth, spriteHeight, new Border(borderLeft, borderTop, borderRight, borderBottom));
      guiGraphics.blitNineSlicedSprite(textureAtlasSprite, nineSlice, x, y, 0, width, height);
   }

   public static void blitNineSlicedSprite(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height, int borderSize) {
      blitNineSlicedSprite(guiGraphics, sprite, x, y, width, height, borderSize, borderSize, borderSize, borderSize, -1);
   }

   public static void blitNineSlicedSprite(
      GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom
   ) {
      blitNineSlicedSprite(guiGraphics, sprite, x, y, width, height, borderLeft, borderTop, borderRight, borderBottom, -1);
   }

   public static void blitNineSlicedSprite(
      GuiGraphics guiGraphics,
      ResourceLocation sprite,
      int x,
      int y,
      int width,
      int height,
      int borderLeft,
      int borderTop,
      int borderRight,
      int borderBottom,
      int color
   ) {
      GuiSpriteManager guiSprites = Minecraft.getInstance().getGuiSprites();
      TextureAtlasSprite textureAtlasSprite = guiSprites.getSprite(sprite);
      NineSlice nineSlice = new NineSlice(
         textureAtlasSprite.contents().width(), textureAtlasSprite.contents().height(), new Border(borderLeft, borderTop, borderRight, borderBottom)
      );
      guiGraphics.blitNineSlicedSprite(textureAtlasSprite, nineSlice, x, y, 0, width, height);
   }

   public static void blitTiledSprite(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height, int spriteWidth, int spriteHeight) {
      blitTiledSprite(guiGraphics, sprite, x, y, width, height, spriteWidth, spriteHeight, -1);
   }

   public static void blitTiledSprite(
      GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height, int spriteWidth, int spriteHeight, int color
   ) {
      blitTiledSprite(guiGraphics, sprite, x, y, width, height, spriteWidth, spriteHeight, 0, 0, color);
   }

   public static void blitTiledSprite(
      GuiGraphics guiGraphics,
      ResourceLocation sprite,
      int x,
      int y,
      int width,
      int height,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset,
      int color
   ) {
      GuiSpriteManager guiSprites = Minecraft.getInstance().getGuiSprites();
      TextureAtlasSprite textureAtlasSprite = guiSprites.getSprite(sprite);
      guiGraphics.blitTiledSprite(textureAtlasSprite, x, y, 0, width, height, uOffset, vOffset, spriteWidth, spriteHeight, spriteWidth, spriteHeight);
   }
}
