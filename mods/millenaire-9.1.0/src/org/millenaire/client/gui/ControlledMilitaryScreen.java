package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import org.millenaire.combat.raid.RaidButtonState;
import org.millenaire.network.ControlledMilitaryPayload;
import org.millenaire.network.ControlledRelationPayload;
import org.millenaire.network.RaidActionPayload;
import org.millenaire.village.VillageRelations;
import org.millenaire.village.panel.PanelLine;

public class ControlledMilitaryScreen extends AbstractMillenaireScreen {
   private static final int PARCHMENT_WIDTH = 204;
   private static final int PARCHMENT_HEIGHT = 220;
   private static final int ROW_GAP = 6;
   private static final int REL_BUTTON_WIDTH = 52;
   private static final int RAID_BUTTON_WIDTH = 90;
   private static final int BUTTON_HEIGHT = 14;
   private static final int BUTTON_GAP = 2;
   private static final int STATUS_OFFSET = 96;
   private final ControlledMilitaryPayload data;
   private final List<Button> pageButtons = new ArrayList<>();
   private List<PanelLine> wrappedMilitaryLines = List.of();
   private int rowCount;
   private final List<Integer> pageStarts = new ArrayList<>();
   private List<FormattedCharSequence> subtitleLines = List.of();
   private List<List<ControlledMilitaryScreen.TextRow>> rowTexts = List.of();
   private List<List<FormattedCharSequence>> statusTexts = List.of();

   public ControlledMilitaryScreen(ControlledMilitaryPayload data) {
      super(Component.translatable("panel.millenaire.panel_type.controlled_military"));
      this.data = data;
   }

   private ControlledMilitaryPayload data() {
      return this.data;
   }

   protected void init() {
      super.init();
      int parchY = (this.height - 220) / 2;
      int parchX = (this.width - 204) / 2;
      int width = textWidth(204);
      this.subtitleLines = this.data().villageName().isEmpty() ? List.of() : this.font.split(Component.literal(this.data().villageName()), width);
      int contentStartY = parchY + 42 + this.subtitleOffset();
      int contentEndY = parchY + 220 - 15;
      int availableHeight = contentEndY - contentStartY;
      this.wrappedMilitaryLines = this.wrapLines(this.data().militaryLines(), 204);
      String currentRaidTarget = this.data().currentRaidTargetVillageId().isEmpty() ? null : this.data().currentRaidTargetVillageId();
      List<List<ControlledMilitaryScreen.TextRow>> texts = new ArrayList<>();
      List<List<FormattedCharSequence>> statuses = new ArrayList<>();

      for (ControlledMilitaryPayload.RelationEntry re : this.data().relations()) {
         List<ControlledMilitaryScreen.TextRow> rows = new ArrayList<>();

         for (FormattedCharSequence line : this.font.split(Component.literal(re.villageName() + " (" + re.cultureName() + ")"), width)) {
            rows.add(new ControlledMilitaryScreen.TextRow(line, 4202512));
         }

         ChatFormatting cf = VillageRelations.getRelationColor(re.relation());
         int relColor = cf.getColor() != null ? 0xFF000000 | cf.getColor() : 6963232;
         String relLabel = Component.translatable(re.relationLabel()).getString();

         for (FormattedCharSequence line : this.font.split(Component.literal(relLabel + " (" + re.relation() + ")"), width)) {
            rows.add(new ControlledMilitaryScreen.TextRow(line, relColor));
         }

         texts.add(rows);
         String statusKey = statusKey(RaidButtonState.compute(currentRaidTarget, this.data().raidInProgress(), re.villageId()));
         statuses.add(statusKey == null ? List.of() : this.font.split(Component.translatable(statusKey), width - 96));
      }

      this.rowTexts = texts;
      this.statusTexts = statuses;
      this.rowCount = this.data().relations().size();
      this.pageStarts.clear();
      int acc = 0;

      for (int i = 0; i < this.rowCount; i++) {
         int h = this.rowHeight(i);
         if (this.pageStarts.isEmpty() || acc > 0 && acc + h > availableHeight) {
            this.pageStarts.add(i);
            acc = 0;
         }

         acc += h;
      }

      this.totalPages = Math.max(1, this.pageStarts.size() + 1);
      this.currentPage = 0;
      this.initPaginationButtons(parchX, parchY, 204, 220, 16, true);
      this.rebuildPageButtons();
      this.updatePaginationButtons();
   }

   private int subtitleOffset() {
      return this.subtitleLines.isEmpty() ? 0 : this.subtitleLines.size() * (9 + 1) + 3;
   }

   @Nullable
   private static String statusKey(RaidButtonState state) {
      return switch (state) {
         case CANCEL -> "gui.millenaire.chief.raid_planned";
         case PLANNED_OTHER -> "gui.millenaire.chief.other_raid_planned";
         case IN_PROGRESS_HERE -> "gui.millenaire.chief.raid_in_progress";
         case IN_PROGRESS_OTHER -> "gui.millenaire.chief.other_raid_in_progress";
         case PLAN -> null;
      };
   }

   private int relationButtonsY(int rowIdx) {
      return this.rowTexts.get(rowIdx).size() * (9 + 1) + 2;
   }

   private int rowHeight(int rowIdx) {
      int statusHeight = this.statusTexts.get(rowIdx).size() * (9 + 1) + 3;
      return this.relationButtonsY(rowIdx) + 14 + 2 + Math.max(14, statusHeight) + 6;
   }

   private int pageStart(int page) {
      return this.pageStarts.isEmpty() ? 0 : this.pageStarts.get(page);
   }

   private int pageEnd(int page) {
      return page + 1 < this.pageStarts.size() ? this.pageStarts.get(page + 1) : this.rowCount;
   }

   private int militaryPageIndex() {
      return this.totalPages - 1;
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
      if (this.currentPage != this.militaryPageIndex()) {
         int parchX = (this.width - 204) / 2;
         int parchY = (this.height - 220) / 2;
         int contentX = parchX + 16;
         int rowY = parchY + 42 + this.subtitleOffset();
         int startIdx = this.pageStart(this.currentPage);
         int endIdx = this.pageEnd(this.currentPage);
         String currentRaidTarget = this.data().currentRaidTargetVillageId().isEmpty() ? null : this.data().currentRaidTargetVillageId();

         for (int i = startIdx; i < endIdx; i++) {
            ControlledMilitaryPayload.RelationEntry re = this.data().relations().get(i);
            int btnRow1Y = rowY + this.relationButtonsY(i);
            int gx = contentX;
            this.addPageButton(
               Button.builder(
                     Component.translatable("gui.millenaire.controlled_military.relgood"),
                     b -> PacketDistributor.sendToServer(
                        new ControlledRelationPayload(this.data().villageUuid(), re.villageId(), 100), new CustomPacketPayload[0]
                     )
                  )
                  .bounds(gx, btnRow1Y, 52, 14)
                  .build()
            );
            gx += 54;
            this.addPageButton(
               Button.builder(
                     Component.translatable("gui.millenaire.controlled_military.relneutral"),
                     b -> PacketDistributor.sendToServer(
                        new ControlledRelationPayload(this.data().villageUuid(), re.villageId(), 0), new CustomPacketPayload[0]
                     )
                  )
                  .bounds(gx, btnRow1Y, 52, 14)
                  .build()
            );
            gx += 54;
            this.addPageButton(
               Button.builder(
                     Component.translatable("gui.millenaire.controlled_military.relbad"),
                     b -> PacketDistributor.sendToServer(
                        new ControlledRelationPayload(this.data().villageUuid(), re.villageId(), -100), new CustomPacketPayload[0]
                     )
                  )
                  .bounds(gx, btnRow1Y, 52, 14)
                  .build()
            );
            int btnRow2Y = btnRow1Y + 14 + 2;
            RaidButtonState state = RaidButtonState.compute(currentRaidTarget, this.data().raidInProgress(), re.villageId());
            switch (state) {
               case CANCEL:
                  this.addPageButton(
                     Button.builder(
                           Component.translatable("gui.millenaire.controlled_military.raidcancel"),
                           b -> PacketDistributor.sendToServer(
                              new RaidActionPayload(this.data().villageUuid(), re.villageId(), 1, true), new CustomPacketPayload[0]
                           )
                        )
                        .bounds(contentX, btnRow2Y, 90, 14)
                        .build()
                  );
                  break;
               case PLANNED_OTHER:
               case PLAN:
                  this.addPageButton(
                     Button.builder(
                           Component.translatable("gui.millenaire.controlled_military.raid"),
                           b -> PacketDistributor.sendToServer(
                              new RaidActionPayload(this.data().villageUuid(), re.villageId(), 0, true), new CustomPacketPayload[0]
                           )
                        )
                        .bounds(contentX, btnRow2Y, 90, 14)
                        .build()
                  );
               case IN_PROGRESS_HERE:
               case IN_PROGRESS_OTHER:
            }

            rowY += this.rowHeight(i);
         }
      }
   }

   private void addPageButton(Button btn) {
      this.addRenderableWidget(btn);
      this.pageButtons.add(btn);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      graphics.fill(0, 0, this.width, this.height, 1610612736);
      int parchX = (this.width - 204) / 2;
      int parchY = (this.height - 220) / 2;
      PanelRenderHelper.renderTexturedBackground(graphics, PanelRenderHelper.PANEL_TEXTURE, parchX, parchY, 204, 220);
      int y = this.renderParchmentHeader(graphics, this.getTitle(), parchX, parchY, 204);
      int textY = y;

      for (FormattedCharSequence line : this.subtitleLines) {
         graphics.drawString(this.font, line, parchX + 16, textY, 5914656, false);
         textY += 9 + 1;
      }

      y += this.subtitleOffset();
      if (this.currentPage == this.militaryPageIndex()) {
         this.renderMilitaryPage(graphics, parchX, parchY, y);
      } else if (this.rowCount == 0) {
         for (FormattedCharSequence line : this.font.split(Component.translatable("gui.millenaire.chief.diplomacy_none"), textWidth(204))) {
            graphics.drawString(this.font, line, parchX + 16, y, 5914656, false);
            y += 9 + 1;
         }
      } else {
         this.renderRelationRows(graphics, parchX, y);
      }

      this.renderPageCounter(graphics, parchX, parchY, 204, 220);
      super.render(graphics, mouseX, mouseY, partialTick);
   }

   private void renderRelationRows(GuiGraphics graphics, int parchX, int startY) {
      int contentX = parchX + 16;
      int rowY = startY;
      int startIdx = this.pageStart(this.currentPage);
      int endIdx = this.pageEnd(this.currentPage);
      String currentRaidTarget = this.data().currentRaidTargetVillageId().isEmpty() ? null : this.data().currentRaidTargetVillageId();

      for (int i = startIdx; i < endIdx; i++) {
         ControlledMilitaryPayload.RelationEntry re = this.data().relations().get(i);
         int ry = rowY;

         for (ControlledMilitaryScreen.TextRow row : this.rowTexts.get(i)) {
            graphics.drawString(this.font, row.text(), contentX, ry, row.color(), false);
            ry += 9 + 1;
         }

         RaidButtonState state = RaidButtonState.compute(currentRaidTarget, this.data().raidInProgress(), re.villageId());
         int color = state != RaidButtonState.CANCEL && state != RaidButtonState.PLANNED_OTHER ? 11141120 : 16733525;
         int sy = rowY + this.relationButtonsY(i) + 14 + 2 + 3;

         for (FormattedCharSequence line : this.statusTexts.get(i)) {
            graphics.drawString(this.font, line, contentX + 96, sy, 0xFF000000 | color, false);
            sy += 9 + 1;
         }

         rowY += this.rowHeight(i);
      }
   }

   private void renderMilitaryPage(GuiGraphics graphics, int parchX, int parchY, int startY) {
      graphics.drawString(this.font, Component.translatable("gui.millenaire.chief.military_title"), parchX + 16, startY, -14018560, false);
      int linesStartY = startY + 9 + 4;
      int contentEndY = parchY + 220 - 15;
      int maxLines = Math.max(0, (contentEndY - linesStartY) / 11);
      PanelRenderHelper.renderPanelLines(graphics, this.font, this.wrappedMilitaryLines, parchX + 16, linesStartY, textWidth(204), 0, maxLines);
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   private record TextRow(FormattedCharSequence text, int color) {
   }
}
