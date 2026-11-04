package com.archiangel.quadhotbar.client;

import com.archiangel.quadhotbar.QuadHotbarConfig;
import com.archiangel.quadhotbar.QuadHotbarContainerOverview;
import com.archiangel.quadhotbar.QuadHotbarOverviewLayout;
import com.archiangel.quadhotbar.QuadHotbarOverviewMenu;
import com.archiangel.quadhotbar.QuadHotbarOverviewNetwork;
import com.archiangel.quadhotbar.mixin.SlotPositionAccessor;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class QuadHotbarOverviewScreen extends AbstractContainerScreen<QuadHotbarOverviewMenu> {
   private static final int CELL = 18;
   private static final int PAGE_HEIGHT = 100;
   private int viewportX;
   private int viewportY;
   private int viewportWidth;
   private int viewportHeight;
   private int pagesAcross;
   private int contentHeight;
   private double scroll;
   private double targetScroll;
   private long lastScrollFrame = System.nanoTime();
   private boolean docked;
   private boolean draggingScrollbar;
   private double scrollbarGrabOffset;
   private boolean safeClick;
   private boolean returning;
   private AbstractContainerScreen<?> vanillaScreen;
   private QuadHotbarControlButton closeButton;
   private boolean delegatingBaseInput;
   private boolean baseGesture;
   private boolean draggingWindow;
   private double dragOffsetX;
   private double dragOffsetY;
   private boolean positionChanged;
   private int externalToken;

   public QuadHotbarOverviewScreen(QuadHotbarOverviewMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   public QuadHotbarOverviewScreen(QuadHotbarOverviewMenu menu, Inventory inventory, Component title, AbstractContainerScreen<?> parent, int token) {
      this(menu, inventory, title);
      this.vanillaScreen = parent;
      this.externalToken = token;
   }

   public int externalToken() {
      return this.externalToken;
   }

   protected void init() {
      this.docked = !((QuadHotbarOverviewMenu)this.menu).standalone;
      if (this.docked) {
         if (this.vanillaScreen == null) {
            this.vanillaScreen = (AbstractContainerScreen<?>)(this.minecraft.player.hasInfiniteMaterials()
               ? new CreativeModeInventoryScreen(
                  this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), (Boolean)this.minecraft.options.operatorItemsTab().get()
               )
               : new InventoryScreen(this.minecraft.player));
         }

         if (!((QuadHotbarOverviewMenu)this.menu).external) {
            this.minecraft.player.containerMenu = this.menu;
         }

         if (this.vanillaScreen.width == 0) {
            this.vanillaScreen.init(this.minecraft, this.width, this.height);
         } else if (!((QuadHotbarOverviewMenu)this.menu).external || this.vanillaScreen.width != this.width || this.vanillaScreen.height != this.height) {
            this.vanillaScreen.resize(this.minecraft, this.width, this.height);
         }

         if (!((QuadHotbarOverviewMenu)this.menu).external) {
            this.minecraft.player.containerMenu = this.menu;
         }
      }

      int requestedWidth = ((QuadHotbarOverviewMenu)this.menu).external
         ? QuadHotbarOverviewSizing.effectiveWidth()
         : ((QuadHotbarOverviewMenu)this.menu).columns;
      this.pagesAcross = Math.min(requestedWidth, QuadHotbarOverviewSizing.maxWidth()) / 9;
      this.viewportWidth = this.pagesAcross * 162 + (this.pagesAcross - 1) * 8;
      this.viewportHeight = Math.max(18, Math.min(((QuadHotbarOverviewMenu)this.menu).viewportRows * 18, this.height - 64));
      this.imageWidth = this.viewportWidth + 32;
      this.imageHeight = this.viewportHeight + 44;
      this.contentHeight = QuadHotbarOverviewLayout.rows(((QuadHotbarOverviewMenu)this.menu).pageCount, this.pagesAcross) * 100;
      this.viewportX = 8;
      this.viewportY = 28;
      super.init();
      if (this.docked) {
         this.leftPos = this.vanillaScreen.getGuiLeft() + this.vanillaScreen.getXSize() + 28;
         this.topPos = this.automaticTop();
      }

      if (QuadHotbarConfig.overviewX >= 0) {
         this.leftPos = QuadHotbarConfig.overviewX;
      }

      if (QuadHotbarConfig.overviewY >= 0) {
         this.topPos = QuadHotbarConfig.overviewY;
      }

      this.clampPosition();
      this.closeButton = (QuadHotbarControlButton)this.addRenderableWidget(
         QuadHotbarControlButton.close(this.leftPos + this.imageWidth - 23, this.topPos + 3, button -> this.returnToInventoryWithCursor())
      );
      this.updateSlots();
      if (!((QuadHotbarOverviewMenu)this.menu).external) {
         QuadHotbarInventoryPageUi.rememberOverviewState(true);
      }
   }

   private int pageColumn(int page) {
      return QuadHotbarOverviewLayout.column(
         page, ((QuadHotbarOverviewMenu)this.menu).pageCount, this.pagesAcross, QuadHotbarConfig.overviewHorizontal, QuadHotbarConfig.overviewFirstPageRight
      );
   }

   private int pageRow(int page) {
      return QuadHotbarOverviewLayout.row(page, ((QuadHotbarOverviewMenu)this.menu).pageCount, this.pagesAcross, QuadHotbarConfig.overviewHorizontal);
   }

   private void updateSlots() {
      this.scroll = Mth.clamp(this.scroll, 0.0, Math.max(0, this.contentHeight - this.viewportHeight));

      for (int index = 0; index < ((QuadHotbarOverviewMenu)this.menu).slots.size(); index++) {
         if (index < ((QuadHotbarOverviewMenu)this.menu).pageSlots) {
            int page = index / 36;
            int slot = index % 36;
            int x = this.viewportX + this.pageColumn(page) * 170 + slot % 9 * 18;
            int y = this.viewportY + this.pageRow(page) * 100 + 18 + displayRow(slot) * 18 - (int)this.scroll;
            boolean visible = y + 18 > this.viewportY && y < this.viewportY + this.viewportHeight;
            Slot target = (Slot)((QuadHotbarOverviewMenu)this.menu).slots.get(index);
            ((QuadHotbarOverviewMenu.OverviewSlot)target).visible = visible;
            ((SlotPositionAccessor)target).quadhotbar$setX(visible ? x + 1 : -10000);
            ((SlotPositionAccessor)target).quadhotbar$setY(visible ? y + 1 : -10000);
         }
      }
   }

   private static int displayRow(int slot) {
      return slot < 9 ? 3 : slot / 9 - 1;
   }

   private int automaticTop() {
      return this.vanillaScreen.getGuiTop() - (this.vanillaScreen instanceof CreativeModeInventoryScreen ? 28 : 0);
   }

   private void advanceScroll() {
      long now = System.nanoTime();
      double elapsed = Math.min(0.05, Math.max(0.0, (now - this.lastScrollFrame) / 1.0E9));
      this.lastScrollFrame = now;
      double maximum = Math.max(0, this.contentHeight - this.viewportHeight);
      this.targetScroll = Mth.clamp(this.targetScroll, 0.0, maximum);
      this.scroll = Mth.clamp(this.scroll, 0.0, maximum);
      this.scroll = this.scroll + (this.targetScroll - this.scroll) * (1.0 - Math.exp(-20.0 * elapsed));
      if (Math.abs(this.targetScroll - this.scroll) < 0.05) {
         this.scroll = this.targetScroll;
      }
   }

   private boolean inViewport(double x, double y) {
      return x >= this.leftPos + this.viewportX
         && x < this.leftPos + this.viewportX + this.viewportWidth
         && y >= this.topPos + this.viewportY
         && y < this.topPos + this.viewportY + this.viewportHeight;
   }

   private void clipViewport(GuiGraphics graphics) {
      graphics.enableScissor(
         this.leftPos + this.viewportX,
         this.topPos + this.viewportY,
         this.leftPos + this.viewportX + this.viewportWidth,
         this.topPos + this.viewportY + this.viewportHeight
      );
   }

   protected boolean isHovering(int x, int y, int slotWidth, int slotHeight, double mouseX, double mouseY) {
      return this.inViewport(mouseX, mouseY) && super.isHovering(x, y, slotWidth, slotHeight, mouseX, mouseY);
   }

   protected void renderSlot(GuiGraphics graphics, Slot slot) {
      this.clipViewport(graphics);
      graphics.pose().pushPose();
      graphics.pose().translate(0.0, (int)this.scroll - this.scroll, 0.0);
      super.renderSlot(graphics, slot);
      graphics.pose().popPose();
      graphics.disableScissor();
   }

   protected void renderSlotHighlight(GuiGraphics graphics, Slot slot, int mouseX, int mouseY, float partialTick) {
      this.clipViewport(graphics);
      graphics.pose().pushPose();
      graphics.pose().translate(0.0, (int)this.scroll - this.scroll, 0.0);
      super.renderSlotHighlight(graphics, slot, mouseX, mouseY, partialTick);
      graphics.pose().popPose();
      graphics.disableScissor();
   }

   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      QuadHotbarVanillaUi.container(graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
      graphics.drawString(
         this.font, this.font.plainSubstrByWidth(this.title.getString(), this.imageWidth - 38), this.leftPos + 8, this.topPos + 9, 4210752, false
      );
      graphics.enableScissor(
         this.leftPos + this.viewportX,
         this.topPos + this.viewportY,
         this.leftPos + this.viewportX + this.viewportWidth,
         this.topPos + this.viewportY + this.viewportHeight
      );
      graphics.fill(
         this.leftPos + this.viewportX,
         this.topPos + this.viewportY,
         this.leftPos + this.viewportX + this.viewportWidth,
         this.topPos + this.viewportY + this.viewportHeight,
         436207616
      );
      graphics.pose().pushPose();
      graphics.pose().translate(0.0, (int)this.scroll - this.scroll, 0.0);

      for (int page = 0; page < ((QuadHotbarOverviewMenu)this.menu).pageCount; page++) {
         int x = this.leftPos + this.viewportX + this.pageColumn(page) * 170;
         int y = this.topPos + this.viewportY + this.pageRow(page) * 100 - (int)this.scroll;
         if (y + 100 >= this.topPos + this.viewportY && y <= this.topPos + this.viewportY + this.viewportHeight) {
            Component pageTitle = Component.translatable("quadhotbar.overview.page", new Object[]{page + 1})
               .withStyle(page == ((QuadHotbarOverviewMenu)this.menu).activePage ? ChatFormatting.BOLD : ChatFormatting.RESET);
            graphics.drawString(this.font, pageTitle, x + (162 - this.font.width(pageTitle)) / 2, y + 4, 4210752, false);
            graphics.hLine(x, x + 161, y + 15, -7631989);
            graphics.hLine(x, x + 161, y + 16, -1);

            for (int slot = 0; slot < 36; slot++) {
               QuadHotbarVanillaUi.slot(graphics, x + slot % 9 * 18, y + 18 + displayRow(slot) * 18, slot < 9);
            }
         }
      }

      graphics.pose().popPose();
      graphics.disableScissor();
      int trackX = this.leftPos + this.viewportX + this.viewportWidth + 4;
      int trackY = this.topPos + this.viewportY;
      int knobY = this.scrollbarTop();
      QuadHotbarVanillaUi.creativeScrollbar(
         graphics, trackX, trackY, this.viewportHeight, knobY, this.scrollbarHeight(), this.contentHeight > this.viewportHeight
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.advanceScroll();
      if (this.docked) {
         if (QuadHotbarConfig.overviewX < 0 && QuadHotbarConfig.overviewY < 0 && !this.draggingWindow) {
            this.leftPos = this.vanillaScreen.getGuiLeft() + this.vanillaScreen.getXSize() + 28;
            this.topPos = this.automaticTop();
            this.clampPosition();
            this.closeButton.setX(this.leftPos + this.imageWidth - 23);
            this.closeButton.setY(this.topPos + 3);
         }

         boolean covered = this.containsPanel(mouseX, mouseY);
         this.vanillaScreen.renderWithTooltip(graphics, covered ? -10000 : mouseX, covered ? -10000 : mouseY, partialTick);
      }

      this.updateSlots();
      graphics.pose().pushPose();
      graphics.pose().translate(0.0F, 0.0F, 400.0F);
      super.render(graphics, mouseX, mouseY, partialTick);
      this.renderTooltip(graphics, mouseX, mouseY);
      graphics.pose().popPose();
   }

   public void renderBackground(GuiGraphics graphics, int x, int y, float partialTick) {
      if (!this.docked) {
         this.renderTransparentBackground(graphics);
      }

      this.renderBg(graphics, partialTick, x, y);
   }

   public boolean isBaseScreen(Object screen) {
      return screen == this.vanillaScreen;
   }

   public boolean isDelegatingBaseInput() {
      return this.delegatingBaseInput;
   }

   public boolean containsPanel(double x, double y) {
      return x >= this.leftPos && x < this.leftPos + this.imageWidth && y >= this.topPos && y < this.topPos + this.imageHeight;
   }

   private boolean baseInput(Supplier<Boolean> action) {
      this.delegatingBaseInput = true;

      try {
         return action.get();
      } finally {
         this.delegatingBaseInput = false;
      }
   }

   private void clampPosition() {
      this.leftPos = Mth.clamp(this.leftPos, 0, Math.max(0, this.width - this.imageWidth));
      this.topPos = Mth.clamp(this.topPos, 0, Math.max(0, this.height - this.imageHeight));
   }

   private void savePosition() {
      if (this.positionChanged) {
         QuadHotbarConfig.setOverviewPosition(this.leftPos, this.topPos);
         QuadHotbarConfig.save();
         this.positionChanged = false;
      }
   }

   protected boolean hasClickedOutside(double x, double y, int left, int top, int button) {
      return false;
   }

   public boolean mouseClicked(double x, double y, int button) {
      if (this.returning) {
         return true;
      }

      if (this.docked && !this.containsPanel(x, y)) {
         this.baseGesture = true;
         return this.baseInput(() -> this.vanillaScreen.mouseClicked(x, y, button));
      }

      this.baseGesture = false;
      if (button == 0
         && QuadHotbarConfig.overviewDraggable
         && y >= this.topPos
         && y < this.topPos + 24
         && x >= this.leftPos
         && x < this.leftPos + this.imageWidth - 26) {
         this.draggingWindow = true;
         this.dragOffsetX = x - this.leftPos;
         this.dragOffsetY = y - this.topPos;
         return true;
      }

      if (button == 0
         && x >= this.leftPos + this.viewportX + this.viewportWidth + 3
         && x < this.leftPos + this.viewportX + this.viewportWidth + 17
         && y >= this.topPos + this.viewportY
         && y < this.topPos + this.viewportY + this.viewportHeight) {
         this.draggingScrollbar = true;
         int knobTop = this.scrollbarTop();
         this.scrollbarGrabOffset = y >= knobTop && y < knobTop + this.scrollbarHeight() ? y - knobTop : this.scrollbarHeight() / 2.0;
         this.dragScrollbar(y);
         return true;
      }

      boolean overSlot = this.inViewport(x, y)
         && ((QuadHotbarOverviewMenu)this.menu)
            .slots
            .stream()
            .anyMatch(
               slot -> slot.isActive()
                  && x >= this.leftPos + slot.x
                  && x < this.leftPos + slot.x + 16
                  && y >= this.topPos + slot.y
                  && y < this.topPos + slot.y + 16
            );
      if (overSlot || x >= this.leftPos + this.imageWidth - 23 && y < this.topPos + 24) {
         return super.mouseClicked(x, y, button);
      }

      this.safeClick = true;
      return true;
   }

   public boolean mouseReleased(double x, double y, int button) {
      if (this.draggingWindow) {
         this.draggingWindow = false;
         this.savePosition();
         return true;
      }

      if (this.baseGesture) {
         this.baseGesture = false;
         return this.baseInput(
            () -> this.vanillaScreen
               .mouseReleased(
                  this.containsPanel(x, y) ? this.vanillaScreen.getGuiLeft() + 88 : x,
                  this.containsPanel(x, y) ? this.vanillaScreen.getGuiTop() + 4 : y,
                  button
               )
         );
      }

      if (!this.draggingScrollbar && !this.safeClick && !this.returning) {
         return super.mouseReleased(x, y, button);
      }

      this.draggingScrollbar = false;
      this.safeClick = false;
      return true;
   }

   public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
      if (this.draggingWindow) {
         this.leftPos = (int)Math.round(x - this.dragOffsetX);
         this.topPos = (int)Math.round(y - this.dragOffsetY);
         this.clampPosition();
         this.closeButton.setX(this.leftPos + this.imageWidth - 23);
         this.closeButton.setY(this.topPos + 3);
         this.positionChanged = true;
         this.updateSlots();
         return true;
      }

      if (!this.baseGesture) {
         if (this.draggingScrollbar) {
            this.dragScrollbar(y);
            return true;
         } else {
            return super.mouseDragged(x, y, button, dx, dy);
         }
      } else {
         return this.containsPanel(x, y) || this.baseInput(() -> this.vanillaScreen.mouseDragged(x, y, button, dx, dy));
      }
   }

   private int scrollbarHeight() {
      return Math.min(15, Math.max(1, this.viewportHeight - 2));
   }

   private int scrollbarTravel() {
      return Math.max(0, this.viewportHeight - 2 - this.scrollbarHeight());
   }

   private int scrollbarTop() {
      double fraction = Mth.clamp(this.scroll / Math.max(1, this.contentHeight - this.viewportHeight), 0.0, 1.0);
      return this.topPos + this.viewportY + 1 + (int)(fraction * this.scrollbarTravel());
   }

   private void dragScrollbar(double y) {
      double fraction = (y - this.topPos - this.viewportY - 1.0 - this.scrollbarGrabOffset) / Math.max(1, this.scrollbarTravel());
      this.scroll = Mth.clamp(fraction, 0.0, 1.0) * Math.max(0, this.contentHeight - this.viewportHeight);
      this.targetScroll = this.scroll;
      this.updateSlots();
   }

   public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
      if (this.returning) {
         return true;
      }

      if (this.docked && !this.containsPanel(x, y)) {
         return this.baseInput(() -> this.vanillaScreen.mouseScrolled(x, y, horizontal, vertical));
      }

      this.targetScroll = Mth.clamp(
         this.targetScroll - vertical * QuadHotbarConfig.overviewScrollStep, 0.0, Math.max(0, this.contentHeight - this.viewportHeight)
      );
      return true;
   }

   public boolean keyPressed(int key, int scanCode, int modifiers) {
      if (this.returning) {
         return true;
      }

      if (key == 256 || this.minecraft.options.keyInventory.matches(key, scanCode)) {
         this.onClose();
         return true;
      }

      if (QuadHotbarKeyMappings.OPEN_OVERVIEW.matches(key, scanCode)) {
         this.returnToInventoryWithCursor();
         return true;
      }

      if (key != 267 && key != 266) {
         return this.docked && this.hoveredSlot == null
            ? this.baseInput(() -> this.vanillaScreen.keyPressed(key, scanCode, modifiers))
            : super.keyPressed(key, scanCode, modifiers);
      }

      this.targetScroll = Mth.clamp(
         this.targetScroll + (key == 267 ? this.viewportHeight : -this.viewportHeight), 0.0, Math.max(0, this.contentHeight - this.viewportHeight)
      );
      return true;
   }

   public boolean charTyped(char character, int modifiers) {
      return this.docked ? this.baseInput(() -> this.vanillaScreen.charTyped(character, modifiers)) : super.charTyped(character, modifiers);
   }

   protected void containerTick() {
      if (this.vanillaScreen != null) {
         this.vanillaScreen.tick();
      }
   }

   public void onClose() {
      this.savePosition();
      if (((QuadHotbarOverviewMenu)this.menu).external) {
         QuadHotbarContainerOverview.close(((QuadHotbarOverviewMenu)this.menu).containerId, this.externalToken);
      } else if (this.minecraft.player.hasInfiniteMaterials()) {
         QuadHotbarOverviewNetwork.syncCreativeCursor();
      }

      super.onClose();
   }

   public void removed() {
      this.savePosition();
      if (((QuadHotbarOverviewMenu)this.menu).external) {
         QuadHotbarContainerOverview.close(((QuadHotbarOverviewMenu)this.menu).containerId, this.externalToken);
      } else if (this.vanillaScreen != null) {
         this.vanillaScreen.removed();
      }

      super.removed();
   }

   public void returnToInventoryWithCursor() {
      this.savePosition();
      if (((QuadHotbarOverviewMenu)this.menu).external) {
         this.closeExternalPanel();
      } else {
         if (!this.returning) {
            QuadHotbarInventoryPageUi.rememberOverviewState(false);
            this.returning = true;
            QuadHotbarOverviewNetwork.returnToInventory();
         }
      }
   }

   public void closeExternalPanel() {
      this.savePosition();
      this.returning = true;
      QuadHotbarContainerOverview.close(((QuadHotbarOverviewMenu)this.menu).containerId, this.externalToken);
      if (this.minecraft.player != null && this.minecraft.player.containerMenu == this.vanillaScreen.getMenu()) {
         this.minecraft.setScreen(this.vanillaScreen);
         QuadHotbarInventoryPageUi.suppressAutoOpen(this.vanillaScreen);
      }
   }

   protected void slotClicked(Slot slot, int index, int button, ClickType type) {
      if (((QuadHotbarOverviewMenu)this.menu).external) {
         QuadHotbarContainerOverview.click(((QuadHotbarOverviewMenu)this.menu).containerId, this.externalToken, index, button, type);
      } else {
         super.slotClicked(slot, index, button, type);
      }
   }

   public boolean isReturningToInventory() {
      return this.returning;
   }

   public static void returnToInventory() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && minecraft.player.containerMenu == minecraft.player.inventoryMenu) {
         minecraft.setScreen(new InventoryScreen(minecraft.player));
         QuadHotbarInventoryPageUi.suppressAutoOpen(minecraft.screen);
      }
   }

   public void showGhostRecipe(RecipeHolder<?> recipe) {
      if (this.vanillaScreen instanceof InventoryScreen inventory) {
         inventory.getRecipeBookComponent().setupGhostRecipe(recipe, this.minecraft.player.inventoryMenu.slots);
      }
   }
}
