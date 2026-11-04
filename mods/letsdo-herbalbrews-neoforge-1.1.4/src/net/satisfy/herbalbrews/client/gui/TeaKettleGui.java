package net.satisfy.herbalbrews.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.satisfy.herbalbrews.client.gui.handler.TeaKettleGuiHandler;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import net.satisfy.herbalbrews.core.util.HerbalBrewsIdentifier;
import org.joml.Vector2i;

public class TeaKettleGui extends AbstractContainerScreen<TeaKettleGuiHandler> {
   private static final ResourceLocation BACKGROUND = HerbalBrewsIdentifier.identifier("textures/gui/tea_kettle.png");
   private static final int ARROW_X = 54;
   private static final int ARROW_Y = 22;
   private static final int ARROW_WIDTH = 24;
   private static final int ARROW_HEIGHT = 17;
   private static final int WATER_X = 141;
   private static final int WATER_Y = 16;
   private static final int WATER_WIDTH = 8;
   private static final int WATER_HEIGHT = 43;
   private static final int WATER_TEXTURE_X = 183;
   private static final int WATER_TEXTURE_Y = 31;
   private static final int HEAT_X = 156;
   private static final int HEAT_Y = 16;
   private static final int HEAT_WIDTH = 5;
   private static final int HEAT_HEIGHT = 43;
   private static final int HEAT_TEXTURE_X = 176;
   private static final int HEAT_TEXTURE_Y = 31;
   private static final int HEATING_SLOT_X = 151;
   private static final int HEATING_SLOT_Y = 62;
   private static final int HEATING_SLOT_WIDTH = 15;
   private static final int HEATING_SLOT_HEIGHT = 15;
   private final Vector2i screenPos = new Vector2i();

   public TeaKettleGui(TeaKettleGuiHandler handler, Inventory inventory, Component title) {
      super(handler, inventory, ((Block)ObjectRegistry.TEA_KETTLE.get()).getName());
   }

   protected void init() {
      super.init();
      this.screenPos.set(this.leftPos, this.topPos);
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
      guiGraphics.blit(BACKGROUND, this.screenPos.x(), this.screenPos.y(), 0, 0, this.imageWidth, this.imageHeight);
      int progress = ((TeaKettleGuiHandler)this.menu).getScaledProgress(24);
      guiGraphics.blit(BACKGROUND, this.screenPos.x() + 54, this.screenPos.y() + 22, 176, 14, progress, 17);
      if (((TeaKettleGuiHandler)this.menu).isBeingBurned()) {
         guiGraphics.blit(BACKGROUND, this.screenPos.x() + 152, this.screenPos.y() + 63, 176, 0, 14, 14);
      }

      int waterLevel = ((TeaKettleGuiHandler)this.menu).getWaterLevel();
      int filledHeight = waterLevel * 43 / 100;
      guiGraphics.blit(BACKGROUND, this.screenPos.x() + 141, this.screenPos.y() + 16 + 43 - filledHeight, 183, 74 - filledHeight, 8, filledHeight);
      int heatLevel = ((TeaKettleGuiHandler)this.menu).getHeatLevel();
      int filledHeatHeight = heatLevel * 43 / 100;
      guiGraphics.blit(BACKGROUND, this.screenPos.x() + 156, this.screenPos.y() + 16 + 43 - filledHeatHeight, 176, 74 - filledHeatHeight, 5, filledHeatHeight);
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      super.render(guiGraphics, mouseX, mouseY, delta);
      if (this.isMouseOverWaterArea(mouseX, mouseY)) {
         int waterLevel = ((TeaKettleGuiHandler)this.menu).getWaterLevel();
         Component tooltip = Component.translatable("tooltip.herbalbrews.tea_kettle.water_level", new Object[]{waterLevel});
         guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
      }

      if (this.isMouseOverWaterSlot(mouseX, mouseY)) {
         Component tooltip = Component.translatable("tooltip.herbalbrews.tea_kettle.water_slot");
         guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
      }

      if (this.isMouseOverHeatArea(mouseX, mouseY)) {
         int heatLevel = ((TeaKettleGuiHandler)this.menu).getHeatLevel();
         Component tooltip = Component.translatable("tooltip.herbalbrews.tea_kettle.heat_level", new Object[]{heatLevel});
         guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
      }

      if (this.isMouseOverProgressArrow(mouseX, mouseY)) {
         int remainingTicks = ((TeaKettleGuiHandler)this.menu).getRequiredDuration() - ((TeaKettleGuiHandler)this.menu).getCookingTime();
         String formattedTime = this.formatTicks(remainingTicks);
         Component tooltip = Component.translatable("tooltip.herbalbrews.tea_kettle.remaining_time", new Object[]{formattedTime});
         guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
      }

      if (this.isMouseOverHeatingSlot(mouseX, mouseY)) {
         int heatIncrease = 35;
         Component tooltip = Component.translatable("tooltip.herbalbrews.tea_kettle.heat_increase", new Object[]{heatIncrease});
         guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
      }

      super.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   private boolean isMouseOverWaterArea(int mouseX, int mouseY) {
      int left = this.screenPos.x() + 141;
      int top = this.screenPos.y() + 16;
      return mouseX >= left && mouseX < left + 8 && mouseY >= top && mouseY < top + 43;
   }

   private boolean isMouseOverHeatArea(int mouseX, int mouseY) {
      int left = this.screenPos.x() + 156;
      int top = this.screenPos.y() + 16;
      return mouseX >= left && mouseX < left + 5 && mouseY >= top && mouseY < top + 43;
   }

   private boolean isMouseOverProgressArrow(int mouseX, int mouseY) {
      int left = this.screenPos.x() + 54;
      int top = this.screenPos.y() + 22;
      return mouseX >= left && mouseX < left + 24 && mouseY >= top && mouseY < top + 17;
   }

   private boolean isMouseOverHeatingSlot(int mouseX, int mouseY) {
      int left = this.screenPos.x() + 151;
      int top = this.screenPos.y() + 62;
      return mouseX >= left && mouseX < left + 15 && mouseY >= top && mouseY < top + 15;
   }

   private boolean isMouseOverWaterSlot(int mouseX, int mouseY) {
      int left = this.screenPos.x() + 118;
      int top = this.screenPos.y() + 25;
      return mouseX >= left && mouseX < left + 18 && mouseY >= top && mouseY < top + 18;
   }

   private String formatTicks(int ticks) {
      int seconds = ticks / 20;
      int minutes = seconds / 60;
      seconds %= 60;
      return String.format("%d:%02d", minutes, seconds);
   }
}
