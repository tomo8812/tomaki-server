package com.archiangel.quadhotbar.client;

import com.archiangel.quadhotbar.QuadHotbarConfig;
import com.archiangel.quadhotbar.QuadHotbarLayout;
import com.archiangel.quadhotbar.QuadHotbarNetwork;
import com.archiangel.quadhotbar.QuadHotbarOverviewMenu;
import com.archiangel.quadhotbar.QuadHotbarPages;
import com.archiangel.quadhotbar.QuadHotbarPolicy;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Pre;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = "quadhotbar", value = Dist.CLIENT)
public final class QuadHotbarClientEvents {
   private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
   private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
   private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");
   private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_right");
   private static final int HOTBAR_WIDTH = 182;
   private static final int HOTBAR_HEIGHT = 22;
   private static final int SELECTOR_WIDTH = 24;
   private static final int SELECTOR_HEIGHT = 23;
   private static final int VANILLA_HOTBAR_SLOTS = 9;
   private static final long PAGE_LABEL_DURATION_NANOS = 3000000000L;
   private static int activeVisualRow = 0;
   private static int pendingVisualSlot = -1;
   private static int pendingServerSelectedSlot = -1;
   private static int savedSelectedSlotBeforeVanilla = -1;
   private static boolean movingExperienceLayer = false;
   private static boolean serverSupportsExtendedSlots = false;
   private static boolean pageSettingsSent = false;
   private static int requestedVisualSlot = -1;
   private static boolean awaitingRecenter = false;
   private static int latestSlotRequestId = 0;
   private static int nextSlotRequestId = 0;
   private static boolean receivedPageSnapshot = false;
   private static long inventoryPageLabelUntilNanos = 0L;
   private static long hotbarPageLabelUntilNanos = 0L;
   private static QuadHotbarPolicy serverPolicy = QuadHotbarPolicy.UNRESTRICTED;
   private static boolean policySettingsPending;

   @SubscribeEvent
   public static void onRenderLayer(Pre event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (QuadHotbarConfig.enabled && minecraft.player != null && !minecraft.options.hideGui) {
         if (VanillaGuiLayers.HOTBAR.equals(event.getName())) {
            event.setCanceled(true);
            updateSelectionMode(minecraft);
            clampSelectedSlot(minecraft.player);
            renderHotbars(event.getGuiGraphics(), minecraft);
            ensureHudAboveExtraRows(minecraft);
         } else if (VanillaGuiLayers.SELECTED_ITEM_NAME.equals(event.getName())) {
            ensureHudAboveExtraRows(minecraft);
         } else if ((VanillaGuiLayers.EXPERIENCE_BAR.equals(event.getName()) || VanillaGuiLayers.EXPERIENCE_LEVEL.equals(event.getName()))
            && shouldLiftExtraHud()) {
            movingExperienceLayer = true;
            event.getGuiGraphics().pose().pushPose();
            event.getGuiGraphics().pose().translate(0.0F, -22 * (hotbarLevels() - 1), 0.0F);
         }
      }
   }

   @SubscribeEvent
   public static void onRenderLayerPost(Post event) {
      if (movingExperienceLayer && (VanillaGuiLayers.EXPERIENCE_BAR.equals(event.getName()) || VanillaGuiLayers.EXPERIENCE_LEVEL.equals(event.getName()))) {
         event.getGuiGraphics().pose().popPose();
         movingExperienceLayer = false;
      }
   }

   public static boolean onVanillaHotbarScroll(Inventory inventory, double direction) {
      Minecraft minecraft = Minecraft.getInstance();
      if (QuadHotbarConfig.enabled
         && minecraft.player != null
         && minecraft.player.getInventory() == inventory
         && minecraft.screen == null
         && (getRows() > 1 || usesPages())
         && direction != 0.0) {
         updateSelectionMode(minecraft);
         int step = (int)Math.signum(direction);
         if (pendingVisualSlot >= 0) {
            pendingVisualSlot = QuadHotbarConfig.scrollBetweenHotbars
               ? Math.floorMod(pendingVisualSlot - step, getRows() * 9)
               : pendingVisualSlot / 9 * 9 + Math.floorMod(pendingVisualSlot % 9 - step, 9);
            return true;
         }

         int selected = requestedVisualSlot >= 0 ? requestedVisualSlot : getSelectedSlot(minecraft.player);
         if (usesPages() && (awaitingRecenter || selected < 0)) {
            awaitingRecenter = true;
            requestedVisualSlot = -1;
            QuadHotbarNetwork.requestScroll(step, !QuadHotbarConfig.scrollBetweenHotbars, nextSelectionRequestId());
         } else {
            int next = QuadHotbarConfig.scrollBetweenHotbars ? selected - step : selected / 9 * 9 + Math.floorMod(selected % 9 - step, 9);
            activateVisualSlot(minecraft, next);
         }

         return true;
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void onKey(Key event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (QuadHotbarConfig.enabled && minecraft.player != null && minecraft.screen == null && event.getAction() == 1) {
         updateSelectionMode(minecraft);

         for (int slot = 0; slot < 9; slot++) {
            if (minecraft.options.keyHotbarSlots[slot].matches(event.getKey(), event.getScanCode())) {
               int currentVisualSlot = pendingVisualSlot >= 0
                  ? pendingVisualSlot
                  : (requestedVisualSlot >= 0 ? requestedVisualSlot : getSelectedSlot(minecraft.player));
               int row = currentVisualSlot >= 0 && currentVisualSlot % 9 == slot ? currentVisualSlot / 9 + 1 : 0;
               if (row >= getRows()) {
                  row = 0;
               }

               pendingVisualSlot = row * 9 + slot;
               if (!usesPages()) {
                  activateVisualSlot(minecraft, pendingVisualSlot);
               }

               return;
            }
         }
      }
   }

   @SubscribeEvent
   public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (policySettingsPending && hasModdedServer() && minecraft.player.containerMenu == minecraft.player.inventoryMenu) {
         policySettingsPending = false;
         syncPageSettings();
      }

      if (QuadHotbarKeyMappings.OPEN_OVERVIEW.consumeClick() && minecraft.screen == null) {
         QuadHotbarInventoryPageUi.openOverview();
      }

      applyPendingServerSelection(minecraft);
      QuadHotbarInventoryPageUi.tickAutoOpen(minecraft);
      if (!QuadHotbarConfig.enabled && minecraft.player != null && minecraft.player.getInventory().selected >= 9) {
         setSelectedSlot(minecraft.player, minecraft.player.getInventory().selected % 9);
      }

      if (minecraft.player != null && minecraft.screen == null && QuadHotbarKeyMappings.OPEN_CONFIG.consumeClick()) {
         minecraft.setScreen(new QuadHotbarConfigScreen(null));
      }

      if (minecraft.player != null && minecraft.screen == null && QuadHotbarKeyMappings.TOGGLE_HOTBARS.consumeClick()) {
         setModEnabled(minecraft, !QuadHotbarConfig.enabled);
      }

      if (minecraft.player != null && minecraft.screen == null && usesPages()) {
         if (QuadHotbarKeyMappings.PREVIOUS_HOTBAR_PAGE.consumeClick()) {
            requestHotbarPage(-1);
         }

         if (QuadHotbarKeyMappings.NEXT_HOTBAR_PAGE.consumeClick()) {
            requestHotbarPage(1);
         }
      }

      int boundSlot = QuadHotbarKeyMappings.consumeHotbarSlot();
      if (QuadHotbarConfig.enabled && minecraft.player != null && minecraft.screen == null && boundSlot >= 0 && boundSlot < getRows() * 9) {
         pendingVisualSlot = -1;
         activateVisualSlot(minecraft, boundSlot);
      }

      if (QuadHotbarConfig.enabled && pendingVisualSlot >= 0) {
         if (minecraft.player != null) {
            updateSelectionMode(minecraft);
            activateVisualSlot(minecraft, pendingVisualSlot);
         }

         pendingVisualSlot = -1;
      } else {
         pendingVisualSlot = -1;
      }
   }

   @SubscribeEvent
   public static void onClientLogin(LoggingIn event) {
      setServerSupportsExtendedSlots(false);
      QuadHotbarConfig.setMultiplayerContext(!Minecraft.getInstance().hasSingleplayerServer());
   }

   @SubscribeEvent
   public static void onClientLogout(LoggingOut event) {
      setServerSupportsExtendedSlots(false);
      QuadHotbarConfig.setMultiplayerContext(false);
   }

   @SubscribeEvent
   public static void onClientRespawn(Clone event) {
      activeVisualRow = 0;
      pendingVisualSlot = -1;
      requestedVisualSlot = -1;
      awaitingRecenter = false;
      latestSlotRequestId = 0;
      receivedPageSnapshot = false;
      inventoryPageLabelUntilNanos = 0L;
      hotbarPageLabelUntilNanos = 0L;
   }

   public static void setServerSupportsExtendedSlots(boolean supported) {
      serverSupportsExtendedSlots = supported;
      if (!supported) {
         serverPolicy = QuadHotbarPolicy.UNRESTRICTED;
         policySettingsPending = false;
         pendingServerSelectedSlot = -1;
         savedSelectedSlotBeforeVanilla = -1;
         pageSettingsSent = false;
         requestedVisualSlot = -1;
         awaitingRecenter = false;
         latestSlotRequestId = 0;
         receivedPageSnapshot = false;
         inventoryPageLabelUntilNanos = 0L;
         hotbarPageLabelUntilNanos = 0L;
      }
   }

   public static QuadHotbarPolicy serverPolicy() {
      return serverPolicy;
   }

   public static void setServerPolicy(QuadHotbarPolicy policy) {
      if (!serverPolicy.equals(policy)) {
         serverPolicy = policy;
         policySettingsPending = hasModdedServer();
      }
   }

   public static void onPageSync() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && supportsExtendedSlots(minecraft) && !pageSettingsSent) {
         pageSettingsSent = true;
         syncPageSettings();
      }
   }

   public static void syncPagePayload(
      int pages,
      int rows,
      int activePage,
      int hotbarPage,
      int selectedSlot,
      int requestId,
      boolean bottomToTop,
      List<ItemStack> first,
      List<ItemStack> second,
      List<ItemStack> middle
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null) {
         QuadHotbarPages.State previous = QuadHotbarPages.state(minecraft.player);
         long now = System.nanoTime();
         if (receivedPageSnapshot) {
            if (previous.activePage != activePage) {
               inventoryPageLabelUntilNanos = now + 3000000000L;
            }

            if (previous.hotbarPage != hotbarPage) {
               hotbarPageLabelUntilNanos = now + 3000000000L;
            }
         }

         QuadHotbarPages.receiveSnapshot(minecraft.player, pages, rows, activePage, hotbarPage, selectedSlot, bottomToTop, first, second, middle);
         if (minecraft.player.containerMenu instanceof QuadHotbarOverviewMenu menu) {
            menu.activePage = activePage;
         }

         acknowledgeSlotRequest(requestId);
         receivedPageSnapshot = true;
         onPageSync();
      }
   }

   public static int inventoryPageLabelColor() {
      if (!receivedPageSnapshot) {
         return 0;
      } else {
         return QuadHotbarConfig.alwaysShowPageLabels ? -1 : QuadHotbarFade.color(inventoryPageLabelUntilNanos, 16777215);
      }
   }

   public static void hideInventoryPageLabel() {
      if (!QuadHotbarConfig.alwaysShowPageLabels) {
         inventoryPageLabelUntilNanos = 0L;
      }
   }

   public static int hotbarPageLabelColor() {
      if (!receivedPageSnapshot) {
         return 0;
      } else {
         return QuadHotbarConfig.alwaysShowPageLabels ? -1 : QuadHotbarFade.color(hotbarPageLabelUntilNanos, 16777215);
      }
   }

   public static void requestInventoryPage(int direction) {
      requestedVisualSlot = -1;
      awaitingRecenter = false;
      latestSlotRequestId = 0;
      inventoryPageLabelUntilNanos = System.nanoTime() + 3000000000L;
      QuadHotbarNetwork.requestInventoryPage(direction);
   }

   private static void requestHotbarPage(int direction) {
      requestedVisualSlot = -1;
      awaitingRecenter = false;
      latestSlotRequestId = 0;
      hotbarPageLabelUntilNanos = System.nanoTime() + 3000000000L;
      QuadHotbarNetwork.requestHotbarPage(direction);
   }

   public static void syncPageSelection(int selectedSlot, int requestId) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null) {
         minecraft.player.getInventory().selected = Mth.clamp(selectedSlot, 0, 35);
         acknowledgeSlotRequest(requestId);
         syncActiveRowFromSelected(minecraft.player);
      }
   }

   private static void acknowledgeSlotRequest(int requestId) {
      if (requestId != 0 && requestId == latestSlotRequestId) {
         requestedVisualSlot = -1;
         awaitingRecenter = false;
         latestSlotRequestId = 0;
      }
   }

   private static int nextSelectionRequestId() {
      nextSlotRequestId = nextSlotRequestId == Integer.MAX_VALUE ? 1 : nextSlotRequestId + 1;
      latestSlotRequestId = nextSlotRequestId;
      return latestSlotRequestId;
   }

   public static void syncPageSettings() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && supportsExtendedSlots(minecraft)) {
         requestedVisualSlot = -1;
         awaitingRecenter = false;
         latestSlotRequestId = 0;
         QuadHotbarNetwork.requestPageSettings(
            QuadHotbarConfig.enabled && QuadHotbarConfig.experimentsEnabled ? QuadHotbarConfig.inventoryPages : 1,
            QuadHotbarConfig.enabled && QuadHotbarConfig.experimentsEnabled ? QuadHotbarConfig.hotbarRows : Math.min(4, QuadHotbarConfig.hotbarRows)
         );
      }
   }

   public static boolean hasModdedServer() {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft.player != null && supportsExtendedSlots(minecraft);
   }

   public static void syncServerSelectedSlot(int selectedSlot) {
      serverSupportsExtendedSlots = true;
      pendingServerSelectedSlot = Mth.clamp(selectedSlot, 0, 35);
      applyPendingServerSelection(Minecraft.getInstance());
      onPageSync();
   }

   private static void renderHotbars(GuiGraphics graphics, Minecraft minecraft) {
      int rows = getRows();
      syncActiveRowFromSelected(minecraft.player);
      int selectedVisualSlot = usesPages() ? getVisualSlot(minecraft.player) : activeVisualRow * 9;
      int selectedRow = selectedVisualSlot < 0 ? -1 : selectedVisualSlot / 9;
      int selectedColumn = getSelectedColumn(minecraft.player);
      RenderSystem.enableBlend();
      graphics.pose().pushPose();
      graphics.pose().translate(0.0F, 0.0F, -90.0F);

      for (int row = 0; row < rows; row++) {
         int[] coords = getHotbarCoords(row, rows, graphics.guiWidth(), graphics.guiHeight());
         graphics.blitSprite(HOTBAR_SPRITE, coords[0], coords[1], 182, 22);
      }

      if (selectedRow >= 0) {
         int[] selectedCoords = getHotbarCoords(selectedRow, rows, graphics.guiWidth(), graphics.guiHeight());
         graphics.blitSprite(HOTBAR_SELECTION_SPRITE, selectedCoords[0] - 1 + selectedColumn * 20, selectedCoords[1] - 1, 24, 23);
      }

      renderOffhandSlotBackground(graphics, minecraft, rows);
      graphics.pose().popPose();
      RenderSystem.disableBlend();

      for (int row = 0; row < rows; row++) {
         int[] coords = getHotbarCoords(row, rows, graphics.guiWidth(), graphics.guiHeight());
         renderRowItems(graphics, minecraft, row, coords[0], coords[1]);
      }

      renderOffhandSlotItem(graphics, minecraft, rows);
      if (usesPages()) {
         QuadHotbarPages.State state = QuadHotbarPages.state(minecraft.player);
         int count = QuadHotbarPages.hotbarPageCount(state);
         int labelColor = hotbarPageLabelColor();
         if (count > 1 && labelColor != 0) {
            int[] top = getTopRowBounds(rows, graphics.guiWidth(), graphics.guiHeight());
            graphics.drawString(minecraft.font, state.hotbarPage + 1 + "/" + count, top[0] + 6, top[1] + 7, labelColor, true);
         }
      }
   }

   private static void applyPendingServerSelection(Minecraft minecraft) {
      if (minecraft.player != null && pendingServerSelectedSlot >= 0) {
         minecraft.player.getInventory().selected = pendingServerSelectedSlot;
         pendingServerSelectedSlot = -1;
         if (!QuadHotbarConfig.enabled && minecraft.player.getInventory().selected >= 9) {
            setSelectedSlot(minecraft.player, minecraft.player.getInventory().selected % 9);
         } else {
            syncActiveRowFromSelected(minecraft.player);
         }
      }
   }

   private static void ensureHudAboveExtraRows(Minecraft minecraft) {
      if (shouldLiftExtraHud()) {
         int minHeight = 39 + 22 * (hotbarLevels() - 1);
         minecraft.gui.leftHeight = Math.max(minecraft.gui.leftHeight, minHeight);
         minecraft.gui.rightHeight = Math.max(minecraft.gui.rightHeight, minHeight);
      }
   }

   private static int[] getHotbarCoords(int row, int rows, int screenWidth, int screenHeight) {
      return QuadHotbarLayout.coordinates(row, rows, screenWidth, screenHeight, 182, 22, QuadHotbarConfig.mainHotbarCorner);
   }

   private static int[] getTopRowBounds(int rows, int screenWidth, int screenHeight) {
      int topY = screenHeight - 22 * ((rows + 1) / 2);
      int rightX = 0;

      for (int row = 0; row < rows; row++) {
         int[] coords = getHotbarCoords(row, rows, screenWidth, screenHeight);
         if (coords[1] == topY) {
            rightX = Math.max(rightX, coords[0] + 182);
         }
      }

      return new int[]{rightX, topY};
   }

   private static void renderOffhandSlotBackground(GuiGraphics graphics, Minecraft minecraft, int rows) {
      if (!minecraft.player.getOffhandItem().isEmpty()) {
         int[] bounds = getBottomRowBounds(rows, graphics.guiWidth(), graphics.guiHeight());
         boolean offhandOnLeft = minecraft.player.getMainArm() == HumanoidArm.RIGHT;
         if (offhandOnLeft) {
            graphics.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, bounds[0] - 29, graphics.guiHeight() - 23, 29, 24);
         } else {
            graphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, bounds[1], graphics.guiHeight() - 23, 29, 24);
         }
      }
   }

   private static void renderOffhandSlotItem(GuiGraphics graphics, Minecraft minecraft, int rows) {
      ItemStack offhand = minecraft.player.getOffhandItem();
      if (!offhand.isEmpty()) {
         int[] bounds = getBottomRowBounds(rows, graphics.guiWidth(), graphics.guiHeight());
         boolean offhandOnLeft = minecraft.player.getMainArm() == HumanoidArm.RIGHT;
         int x = offhandOnLeft ? bounds[0] - 26 : bounds[1] + 10;
         int y = graphics.guiHeight() - 16 - 3;
         graphics.renderItem(offhand, x, y);
         graphics.renderItemDecorations(minecraft.font, offhand, x, y);
      }
   }

   private static int[] getBottomRowBounds(int rows, int screenWidth, int screenHeight) {
      int leftX = screenWidth;
      int rightX = 0;
      int slotY = screenHeight - 22;

      for (int row = 0; row < rows; row++) {
         int[] coords = getHotbarCoords(row, rows, screenWidth, screenHeight);
         if (coords[1] == slotY) {
            leftX = Math.min(leftX, coords[0]);
            rightX = Math.max(rightX, coords[0] + 182);
         }
      }

      return new int[]{leftX, rightX};
   }

   private static void renderRowItems(GuiGraphics graphics, Minecraft minecraft, int row, int hotbarX, int hotbarY) {
      for (int column = 0; column < 9; column++) {
         ItemStack stack = usesPages()
            ? QuadHotbarPages.visibleStack(minecraft.player, row, column)
            : minecraft.player.getInventory().getItem(QuadHotbarConfig.inventoryRowOrder.inventoryRow(row) * 9 + column);
         if (!stack.isEmpty()) {
            int x = hotbarX + 3 + column * 20;
            int y = hotbarY + 3;
            graphics.renderItem(stack, x, y);
            graphics.renderItemDecorations(minecraft.font, stack, x, y);
         }
      }
   }

   private static void activateVisualSlot(Minecraft minecraft, int visualSlot) {
      if (minecraft.player != null) {
         int max = getRows() * 9 - 1;
         if (visualSlot < 0) {
            visualSlot = max;
         } else if (visualSlot > max) {
            visualSlot = 0;
         }

         int targetRow = visualSlot / 9;
         int column = visualSlot % 9;
         if (usesPages()) {
            awaitingRecenter = false;
            requestedVisualSlot = visualSlot;
            QuadHotbarNetwork.requestSlot(targetRow, column, nextSelectionRequestId());
         } else {
            setSelectedSlot(minecraft.player, targetRow * 9 + column);
         }
      }
   }

   public static void setModEnabled(Minecraft minecraft, boolean enabled) {
      if (QuadHotbarConfig.enabled != enabled) {
         if (enabled) {
            QuadHotbarConfig.setEnabled(true);
            if (savedSelectedSlotBeforeVanilla >= 0 && minecraft.player != null) {
               if (QuadHotbarConfig.experimentsEnabled && QuadHotbarConfig.inventoryPages > 1) {
                  setSelectedSlot(minecraft.player, savedSelectedSlotBeforeVanilla % 9);
               } else {
                  setSelectedSlot(minecraft.player, savedSelectedSlotBeforeVanilla);
               }
            }

            savedSelectedSlotBeforeVanilla = -1;
         } else {
            savedSelectedSlotBeforeVanilla = minecraft.player == null ? -1 : getSelectedSlot(minecraft.player);
            QuadHotbarConfig.setEnabled(false);
            pendingVisualSlot = -1;
            if (minecraft.player != null) {
               setSelectedSlot(minecraft.player, minecraft.player.getInventory().selected % 9);
            }
         }

         QuadHotbarConfig.save();
         syncPageSettings();
      }
   }

   private static void clampSelectedSlot(LocalPlayer player) {
      if (!usesPages()) {
         setSelectedSlot(player, getSelectedSlot(player));
      }
   }

   private static int getSelectedColumn(LocalPlayer player) {
      return usesPages() ? Math.floorMod(player.getInventory().selected, 9) : Mth.clamp(getSelectedSlot(player) % 9, 0, 8);
   }

   private static int getSelectedSlot(LocalPlayer player) {
      if (usesPages()) {
         return getVisualSlot(player);
      }

      int physicalSlot = Mth.clamp(player.getInventory().selected, 0, 35);
      int logicalRow = QuadHotbarConfig.inventoryRowOrder.hotbarRow(physicalSlot / 9);
      return Mth.clamp(logicalRow, 0, getRows() - 1) * 9 + physicalSlot % 9;
   }

   private static int getVisualSlot(LocalPlayer player) {
      QuadHotbarPages.State state = QuadHotbarPages.state(player);
      int globalRow = QuadHotbarPages.orderedRow(state, state.activePage, Mth.clamp(player.getInventory().selected, 0, 35));
      int row = globalRow - state.hotbarPage * state.hotbarRows;
      return row >= 0 && row < getRows() ? row * 9 + Math.floorMod(player.getInventory().selected, 9) : -1;
   }

   private static void setSelectedSlot(LocalPlayer player, int slot) {
      int max = getRows() * 9 - 1;
      int visualSlot = Mth.clamp(slot, 0, max);
      int clampedSlot = QuadHotbarConfig.inventoryRowOrder.inventoryRow(visualSlot / 9) * 9 + visualSlot % 9;
      if (player.getInventory().selected != clampedSlot) {
         player.getInventory().selected = clampedSlot;
         player.connection.send(new ServerboundSetCarriedItemPacket(clampedSlot));
      }

      syncActiveRowFromSelected(player);
   }

   private static void syncActiveRowFromSelected(LocalPlayer player) {
      int rows = getRows();
      if (activeVisualRow >= rows) {
         activeVisualRow = 0;
      }

      if (usesPages()) {
         int visualSlot = getVisualSlot(player);
         activeVisualRow = visualSlot < 0 ? 0 : visualSlot / 9;
      } else {
         if (QuadHotbarConfig.enabled) {
            activeVisualRow = Mth.clamp(getSelectedSlot(player) / 9, 0, rows - 1);
         }
      }
   }

   public static int getRows() {
      if (!QuadHotbarConfig.enabled || !supportsExtendedSlots(Minecraft.getInstance())) {
         return 1;
      } else {
         return usesPages() ? QuadHotbarPages.visibleRows(QuadHotbarPages.state(Minecraft.getInstance().player)) : Mth.clamp(QuadHotbarConfig.hotbarRows, 2, 4);
      }
   }

   private static boolean usesPages() {
      Minecraft minecraft = Minecraft.getInstance();
      return QuadHotbarConfig.enabled
         && QuadHotbarConfig.experimentsEnabled
         && minecraft.player != null
         && supportsExtendedSlots(minecraft)
         && serverPolicy().allowFreedom();
   }

   private static boolean shouldLiftExtraHud() {
      return hotbarLevels() > 1;
   }

   private static int hotbarLevels() {
      return (getRows() + 1) / 2;
   }

   private static void updateSelectionMode(Minecraft minecraft) {
      if (minecraft.player != null && !supportsExtendedSlots(minecraft)) {
         activeVisualRow = 0;
         int selectedColumn = getSelectedColumn(minecraft.player);
         if (minecraft.player.getInventory().selected != selectedColumn) {
            setSelectedSlot(minecraft.player, selectedColumn);
         }
      }
   }

   private static boolean supportsExtendedSlots(Minecraft minecraft) {
      return minecraft.getSingleplayerServer() != null || serverSupportsExtendedSlots;
   }
}
