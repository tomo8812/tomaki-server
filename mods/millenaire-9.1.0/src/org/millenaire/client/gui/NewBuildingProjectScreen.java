package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.millenaire.client.AdjustModeController;
import org.millenaire.network.BuildingProjectListPayload;

public class NewBuildingProjectScreen extends AbstractMillenaireScreen {
   private static final int PARCHMENT_WIDTH = 204;
   private static final int PARCHMENT_HEIGHT = 220;
   private static final int BUTTON_ROW_HEIGHT = 18;
   private static final int BUTTON_WIDTH = 170;
   private static final int BUTTON_HEIGHT = 16;
   private final BuildingProjectListPayload data;
   private final List<Button> pageButtons = new ArrayList<>();
   private int rowsPerPage;
   private List<FormattedCharSequence> subtitleLines = List.of();

   public NewBuildingProjectScreen(BuildingProjectListPayload data) {
      super(Component.translatable("gui.millenaire.building_project.title"));
      this.data = data;
   }

   protected void init() {
      super.init();
      int parchY = (this.height - 220) / 2;
      int parchX = (this.width - 204) / 2;
      this.subtitleLines = this.data.villageName().isEmpty() ? List.of() : this.font.split(Component.literal(this.data.villageName()), textWidth(204));
      int contentStartY = parchY + 42 + this.subtitleOffset();
      int contentEndY = parchY + 220 - 15;
      int availableHeight = contentEndY - contentStartY;
      this.rowsPerPage = Math.max(1, availableHeight / 18);
      this.totalPages = this.data.entries().isEmpty() ? 1 : (this.data.entries().size() + this.rowsPerPage - 1) / this.rowsPerPage;
      this.currentPage = 0;
      this.initPaginationButtons(parchX, parchY, 204, 220, 16, false);
      this.rebuildPageButtons();
      this.updatePaginationButtons();
   }

   private int subtitleOffset() {
      return this.subtitleLines.isEmpty() ? 0 : this.subtitleLines.size() * (9 + 1) + 3;
   }

   @Override
   protected void onPageChanged() {
      super.onPageChanged();
      this.rebuildPageButtons();
   }

   private void rebuildPageButtons() {
      for (Button btn : this.pageButtons) {
         this.removeWidget(btn);
      }

      this.pageButtons.clear();
      if (!this.data.entries().isEmpty()) {
         int parchX = (this.width - 204) / 2;
         int parchY = (this.height - 220) / 2;
         int contentX = parchX + 16;
         int y = parchY + 42 + this.subtitleOffset();
         int startIdx = this.currentPage * this.rowsPerPage;
         int endIdx = Math.min(startIdx + this.rowsPerPage, this.data.entries().size());

         for (int i = startIdx; i < endIdx; i++) {
            BuildingProjectListPayload.BuildingEntry entry = this.data.entries().get(i);
            Button btn = Button.builder(Component.translatable(entry.displayName()), button -> {
               AdjustModeController.arm(this.data, entry);
               this.onClose();
            }).bounds(contentX + (textWidth(204) - 170) / 2, y, 170, 16).build();
            this.addRenderableWidget(btn);
            this.pageButtons.add(btn);
            y += 18;
         }
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      graphics.fill(0, 0, this.width, this.height, 1610612736);
      int parchX = (this.width - 204) / 2;
      int parchY = (this.height - 220) / 2;
      PanelRenderHelper.renderParchmentBackground(graphics, parchX, parchY, 204, 220);
      int y = this.renderParchmentHeader(graphics, this.getTitle(), parchX, parchY, 204);
      int textY = y;

      for (FormattedCharSequence line : this.subtitleLines) {
         graphics.drawString(this.font, line, parchX + 16, textY, 5914656, false);
         textY += 9 + 1;
      }

      y += this.subtitleOffset();
      if (this.data.entries().isEmpty()) {
         for (FormattedCharSequence line : this.font.split(Component.translatable("gui.millenaire.building_project.empty"), textWidth(204))) {
            graphics.drawString(this.font, line, parchX + 16, y, 5914656, false);
            y += 9 + 1;
         }
      }

      this.renderPageCounter(graphics, parchX, parchY, 204, 220);
      super.render(graphics, mouseX, mouseY, partialTick);
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }
}
