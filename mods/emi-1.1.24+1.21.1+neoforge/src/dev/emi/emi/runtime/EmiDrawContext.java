package dev.emi.emi.runtime;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class EmiDrawContext {
   private final Minecraft client = Minecraft.getInstance();
   private final GuiGraphics context;

   private EmiDrawContext(GuiGraphics context) {
      this.context = context;
   }

   public static EmiDrawContext wrap(GuiGraphics context) {
      return new EmiDrawContext(context);
   }

   public GuiGraphics raw() {
      return this.context;
   }

   public PoseStack matrices() {
      return this.context.pose();
   }

   public void push() {
      this.matrices().pushPose();
   }

   public void pop() {
      this.matrices().popPose();
   }

   public void drawTexture(ResourceLocation texture, int x, int y, int u, int v, int width, int height) {
      this.drawTexture(texture, x, y, width, height, u, v, width, height, 256, 256);
   }

   public void drawTexture(ResourceLocation texture, int x, int y, int z, float u, float v, int width, int height) {
      this.drawTexture(texture, x, y, z, u, v, width, height, 256, 256);
   }

   public void drawTexture(ResourceLocation texture, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight) {
      EmiPort.setPositionTexShader();
      this.context.blit(texture, x, y, z, u, v, width, height, textureWidth, textureHeight);
   }

   public void drawTexture(
      ResourceLocation texture, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
   ) {
      EmiPort.setPositionTexShader();
      this.context.blit(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
   }

   public void fill(int x, int y, int width, int height, int color) {
      this.context.fill(x, y, x + width, y + height, color);
   }

   public void drawText(Component text, int x, int y) {
      this.drawText(text, x, y, -1);
   }

   public void drawText(Component text, int x, int y, int color) {
      this.context.drawString(this.client.font, text, x, y, color, false);
   }

   public void drawText(FormattedCharSequence text, int x, int y, int color) {
      this.context.drawString(this.client.font, text, x, y, color, false);
   }

   public void drawTextWithShadow(Component text, int x, int y) {
      this.drawTextWithShadow(text, x, y, -1);
   }

   public void drawTextWithShadow(Component text, int x, int y, int color) {
      this.context.drawString(this.client.font, text, x, y, color, true);
   }

   public void drawTextWithShadow(FormattedCharSequence text, int x, int y, int color) {
      this.context.drawString(this.client.font, text, x, y, color, true);
   }

   public void drawCenteredText(Component text, int x, int y) {
      this.drawCenteredText(text, x, y, -1);
   }

   public void drawCenteredText(Component text, int x, int y, int color) {
      this.context.drawString(this.client.font, text, x - this.client.font.width(text) / 2, y, color, false);
   }

   public void drawCenteredTextWithShadow(Component text, int x, int y) {
      this.drawCenteredTextWithShadow(text, x, y, -1);
   }

   public void drawCenteredTextWithShadow(Component text, int x, int y, int color) {
      this.context.drawCenteredString(this.client.font, text.getVisualOrderText(), x, y, color);
   }

   public void enableDepthTest() {
      RenderSystem.enableDepthTest();
   }

   public void disableDepthTest() {
      RenderSystem.disableDepthTest();
   }

   public void enableBlend() {
      RenderSystem.enableBlend();
   }

   public void disableBlend() {
      RenderSystem.disableBlend();
   }

   public void resetColor() {
      this.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void setColor(float r, float g, float b) {
      this.setColor(r, g, b, 1.0F);
   }

   public void setColor(float r, float g, float b, float a) {
      this.raw().setColor(r, g, b, a);
   }

   public void drawStack(EmiIngredient stack, int x, int y) {
      stack.render(this.raw(), x, y, this.client.getTimer().getGameTimeDeltaPartialTick(false));
   }

   public void drawStack(EmiIngredient stack, int x, int y, int flags) {
      this.drawStack(stack, x, y, this.client.getTimer().getGameTimeDeltaPartialTick(false), flags);
   }

   public void drawStack(EmiIngredient stack, int x, int y, float delta, int flags) {
      stack.render(this.raw(), x, y, delta, flags);
   }
}
