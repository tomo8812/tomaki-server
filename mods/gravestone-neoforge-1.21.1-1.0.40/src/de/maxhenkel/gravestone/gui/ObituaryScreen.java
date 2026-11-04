package de.maxhenkel.gravestone.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.corelib.death.Death;
import de.maxhenkel.gravestone.entity.DummyPlayer;
import java.util.Collections;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class ObituaryScreen extends Screen {
   private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("gravestone", "textures/gui/info.png");
   protected static final int TEXTURE_X = 163;
   protected static final int TEXTURE_Y = 165;
   protected static final int ITEM_OFFSET_LEFT = 40;
   protected static final int OFFSET_LEFT = 7;
   protected static final int OFFSET_RIGHT = 14;
   protected static final int ITEM_SIZE_OFFSET_LEFT = 15;
   protected static final int PLAYER_X = 50;
   protected static final int PLAYER_Y = 70;
   protected static final int PLAYER_SCALE = 30;
   protected static final int TITLE_OFFSET_TOP = 10;
   protected static final int CONTENT_OFFSET_TOP = 30;
   protected static final int BUTTON_WIDTH = 75;
   protected static final int BUTTON_HEIGHT = 20;
   protected static final int BUTTON_SPACING = 5;
   private DummyPlayer player;
   private Death death;
   private Button buttonPrev;
   private Button buttonNext;
   private int page;
   private PageList pageList;
   private int guiLeft;
   private int guiTop;

   public ObituaryScreen(Death death) {
      super(Component.translatable("gui.obituary.title"));
      this.death = death;
      this.page = 0;
      this.pageList = new PageList(death.getAllItems(), this);
   }

   protected void init() {
      super.init();
      this.guiLeft = (this.width - 163) / 2;
      this.guiTop = (this.height - 190) / 2;
      int buttonTop = this.guiTop + 165 + 5;
      this.buttonPrev = (Button)this.addRenderableWidget(Button.builder(Component.translatable("button.gravestone.prev"), button -> {
         this.page--;
         if (this.page < 0) {
            this.page = 0;
         }

         this.checkButtons();
      }).bounds(this.guiLeft, buttonTop, 75, 20).build());
      this.buttonNext = (Button)this.addRenderableWidget(Button.builder(Component.translatable("button.gravestone.next"), button -> {
         this.page++;
         if (this.page > this.pageList.getPages()) {
            this.page = this.pageList.getPages();
         }

         this.checkButtons();
      }).bounds(this.guiLeft + 163 - 75, buttonTop, 75, 20).build());
      this.buttonPrev.active = false;
      if (this.pageList.getPages() <= 0) {
         this.buttonNext.active = false;
      }
   }

   protected void checkButtons() {
      if (this.page <= 0) {
         this.buttonPrev.active = false;
      } else {
         this.buttonPrev.active = true;
      }

      if (this.page >= this.pageList.getPages()) {
         this.buttonNext.active = false;
      } else {
         this.buttonNext.active = true;
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.blit(GUI_TEXTURE, this.guiLeft, this.guiTop, 0, 0, 163, 165);
      if (this.page == 0) {
         this.drawFirstPage(guiGraphics, mouseX, mouseY);
      } else if (this.page > 0 && this.pageList.getPages() >= this.page - 1) {
         this.pageList.drawPage(guiGraphics, this.page - 1, mouseX, mouseY);
      }
   }

   public void drawFirstPage(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      this.drawCentered(
         guiGraphics,
         this.font,
         Component.translatable("gui.obituary.title").withStyle(ChatFormatting.UNDERLINE),
         this.width / 2,
         this.guiTop + 10,
         ChatFormatting.BLACK.getColor()
      );
      int height = this.guiTop + 30;
      if (this.minecraft.options.advancedItemTooltips) {
         this.drawLeft(guiGraphics, Component.translatable("gui.obituary.id").append(":").withStyle(ChatFormatting.BLACK), height);
         this.drawRight(guiGraphics, Component.literal(this.death.getId().toString()).withStyle(ChatFormatting.DARK_GRAY), height, 0.5F);
         height += 13;
      }

      this.drawLeft(guiGraphics, Component.translatable("gui.obituary.name").append(":").withStyle(ChatFormatting.BLACK), height);
      this.drawRight(guiGraphics, Component.literal(this.death.getPlayerName()).withStyle(ChatFormatting.DARK_GRAY), height);
      height += 13;
      this.drawLeft(guiGraphics, Component.translatable("gui.obituary.dimension").append(":").withStyle(ChatFormatting.BLACK), height);
      this.drawRight(guiGraphics, Component.literal(this.death.getDimension().split(":")[1]).withStyle(ChatFormatting.DARK_GRAY), height);
      height += 13;
      this.drawLeft(guiGraphics, Component.translatable("gui.obituary.time").append(":").withStyle(ChatFormatting.BLACK), height);
      MutableComponent date = GraveUtils.getDate(this.death.getTimestamp());
      if (date != null) {
         this.drawRight(guiGraphics, date.withStyle(ChatFormatting.DARK_GRAY), height);
      } else {
         this.drawRight(guiGraphics, Component.literal("N/A").withStyle(ChatFormatting.DARK_GRAY), height);
      }

      height += 13;
      this.drawLeft(guiGraphics, Component.translatable("gui.obituary.location").append(":").withStyle(ChatFormatting.BLACK), height);
      BlockPos pos = this.death.getBlockPos();
      this.drawRight(guiGraphics, Component.literal("X: " + pos.getX()).withStyle(ChatFormatting.DARK_GRAY), height);
      height += 13;
      this.drawRight(guiGraphics, Component.literal("Y: " + pos.getY()).withStyle(ChatFormatting.DARK_GRAY), height);
      height += 13;
      this.drawRight(guiGraphics, Component.literal("Z: " + pos.getZ()).withStyle(ChatFormatting.DARK_GRAY), height);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.player == null) {
         this.player = new DummyPlayer(
            this.minecraft.level, new GameProfile(this.death.getPlayerUUID(), this.death.getPlayerName()), this.death.getEquipment(), this.death.getModel()
         );
      }

      int playerLeft = this.guiLeft + 56;
      int playerTop = this.guiTop + 165 - 70;
      InventoryScreen.renderEntityInInventoryFollowsMouse(
         guiGraphics, playerLeft, playerTop, playerLeft + 50, playerTop + 70, 30, 0.0625F, mouseX, mouseY, this.player
      );
      if (this.minecraft.options.advancedItemTooltips
         && mouseX >= this.guiLeft + 7
         && mouseX <= this.guiLeft + 163 - 7
         && mouseY >= this.guiTop + 30
         && mouseY <= this.guiTop + 30 + 9) {
         guiGraphics.renderTooltip(this.font, Collections.singletonList(Component.translatable("gui.obituary.copy_id").getVisualOrderText()), mouseX, mouseY);
      }
   }

   public boolean mouseClicked(double x, double y, int clickType) {
      if (this.minecraft.options.advancedItemTooltips
         && this.page == 0
         && x >= this.guiLeft + 7
         && x <= this.guiLeft + 163 - 7
         && y >= this.guiTop + 30
         && y <= this.guiTop + 30 + 9) {
         this.minecraft.keyboardHandler.setClipboard(this.death.getId().toString());
         Component deathID = ComponentUtils.wrapInSquareBrackets(Component.translatable("message.gravestone.death_id"))
            .withStyle(
               style -> style.applyFormat(ChatFormatting.GREEN)
                  .withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/restore @s " + this.death.getId().toString() + " replace"))
                  .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal(this.death.getId().toString())))
            );
         this.minecraft.player.sendSystemMessage(Component.translatable("message.gravestone.copied", new Object[]{deathID}));
         this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.setScreen(null);
      }

      return super.mouseClicked(x, y, clickType);
   }

   public void drawCentered(GuiGraphics guiGraphics, Font fontRenderer, MutableComponent text, int x, int y, int color) {
      guiGraphics.drawString(fontRenderer, text.getVisualOrderText(), x - fontRenderer.width(text) / 2, y, color, false);
   }

   public void drawItem(GuiGraphics guiGraphics, MutableComponent string, int height) {
      guiGraphics.drawString(this.font, string.getVisualOrderText(), this.guiLeft + 40, height, ChatFormatting.BLACK.getColor(), false);
   }

   public void drawItemSize(GuiGraphics guiGraphics, MutableComponent string, int height) {
      guiGraphics.drawString(this.font, string.getVisualOrderText(), this.guiLeft + 15, height, ChatFormatting.BLACK.getColor(), false);
   }

   public void drawLeft(GuiGraphics guiGraphics, MutableComponent string, int height) {
      guiGraphics.drawString(this.font, string.getVisualOrderText(), this.guiLeft + 7, height, ChatFormatting.BLACK.getColor(), false);
   }

   public void drawRight(GuiGraphics guiGraphics, MutableComponent string, int height) {
      this.drawRight(guiGraphics, string, height, 1.0F);
   }

   public void drawRight(GuiGraphics guiGraphics, MutableComponent string, int height, float scale) {
      guiGraphics.pose().pushPose();
      guiGraphics.pose().scale(scale, scale, 1.0F);
      float f = 1.0F / scale;
      int strWidth = this.font.width(string);
      float spacing = (9.0F * f - 9.0F) / 2.0F;
      guiGraphics.drawString(
         this.font,
         string.getVisualOrderText(),
         (this.guiLeft + 163 - strWidth * scale - 14.0F) * f,
         height * f + spacing,
         ChatFormatting.BLACK.getColor(),
         false
      );
      guiGraphics.pose().popPose();
   }

   public Font getFontRenderer() {
      return this.font;
   }

   public int getGuiLeft() {
      return this.guiLeft;
   }

   public int getGuiTop() {
      return this.guiTop;
   }
}
