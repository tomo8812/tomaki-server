package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import org.millenaire.network.BuildingForgetRequestPayload;
import org.millenaire.network.BuildingProjectCancelRequestPayload;
import org.millenaire.network.BuildingProjectReorderRequestPayload;
import org.millenaire.network.BuildingUpgradeToggleRequestPayload;
import org.millenaire.network.ControlledProjectsPayload;

public class ControlledProjectsScreen extends AbstractMillenaireScreen {
   private static final int PARCHMENT_WIDTH = 204;
   private static final int PARCHMENT_HEIGHT = 220;
   private static final int TOGGLE_WIDTH = 80;
   private static final int TOGGLE_HEIGHT = 14;
   private static final int REORDER_WIDTH = 18;
   private static final int ROW_GAP = 6;
   private final ControlledProjectsPayload data;
   private final List<Button> pageButtons = new ArrayList<>();
   private final List<Integer> pageStarts = new ArrayList<>();
   private int rowCount;
   private List<FormattedCharSequence> noteLines = List.of();
   private List<FormattedCharSequence> subtitleLines = List.of();
   private List<List<ControlledProjectsScreen.TextRow>> rowTexts = List.of();

   public ControlledProjectsScreen(ControlledProjectsPayload data) {
      super(Component.translatable("gui.millenaire.controlled_projects.title"));
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
      this.noteLines = this.font.split(Component.translatable("gui.millenaire.controlled_projects.town_hall_stores"), textWidth(204));
      this.rowCount = this.data.queue().size() + this.data.entries().size();
      List<List<ControlledProjectsScreen.TextRow>> texts = new ArrayList<>(this.rowCount);

      for (ControlledProjectsPayload.QueueEntry q : this.data.queue()) {
         texts.add(this.queueText(q));
      }

      for (ControlledProjectsPayload.ProjectEntry e : this.data.entries()) {
         texts.add(this.entryText(e));
      }

      this.rowTexts = texts;
      this.pageStarts.clear();
      int acc = 0;

      for (int i = 0; i < this.rowCount; i++) {
         int h = this.rowHeightAt(i);
         if (this.pageStarts.isEmpty() || acc > 0 && acc + h > availableHeight) {
            this.pageStarts.add(i);
            acc = 0;
         }

         acc += h;
      }

      this.totalPages = Math.max(1, this.pageStarts.size());
      this.currentPage = 0;
      this.initPaginationButtons(parchX, parchY, 204, 220, 16, true);
      this.rebuildPageButtons();
      this.updatePaginationButtons();
   }

   @Override
   protected void onPageChanged() {
      super.onPageChanged();
      this.rebuildPageButtons();
   }

   private int subtitleOffset() {
      return this.subtitleLines.isEmpty() ? 0 : this.subtitleLines.size() * (9 + 1) + 3;
   }

   private List<ControlledProjectsScreen.TextRow> queueText(ControlledProjectsPayload.QueueEntry q) {
      List<ControlledProjectsScreen.TextRow> rows = new ArrayList<>();
      this.addWrapped(rows, Component.translatable("gui.millenaire.controlled_projects.pending_label"), 0, 6955040);
      MutableComponent heading = Component.literal(q.index() + 1 + ". ").append(Component.translatable(q.displayName()));
      if (!q.distanceLabel().isEmpty()) {
         heading.append(Component.literal(" — " + q.distanceLabel()));
      }

      this.addWrapped(rows, heading, 0, 4202512);

      for (ControlledProjectsPayload.ResourceLine r : q.resources()) {
         Component name = r.translatable() ? Component.translatable(r.displayKey()) : Component.literal(r.displayKey());
         int col = r.present() >= r.required() ? 1997342 : 11149344;
         this.addWrapped(rows, Component.empty().append(name).append(Component.literal(": " + r.present() + "/" + r.required())), 4, col);
      }

      for (FormattedCharSequence noteLine : this.noteLines) {
         rows.add(new ControlledProjectsScreen.TextRow(noteLine, 0, 5914656));
      }

      return rows;
   }

   private List<ControlledProjectsScreen.TextRow> entryText(ControlledProjectsPayload.ProjectEntry entry) {
      List<ControlledProjectsScreen.TextRow> rows = new ArrayList<>();
      this.addWrapped(rows, Component.translatable(entry.displayName()), 0, 4202512);
      String levelStr = Component.translatable(
            "gui.millenaire.controlled_projects.level", new Object[]{String.valueOf(entry.currentLevel() + 1), String.valueOf(entry.maxLevel())}
         )
         .getString();
      String suffix = entry.distanceLabel().isEmpty() ? levelStr : levelStr + " — " + entry.distanceLabel();
      this.addWrapped(rows, Component.literal(suffix), 0, 6963232);
      return rows;
   }

   private void addWrapped(List<ControlledProjectsScreen.TextRow> rows, Component text, int indent, int color) {
      for (FormattedCharSequence line : this.font.split(text, textWidth(204) - indent)) {
         rows.add(new ControlledProjectsScreen.TextRow(line, indent, color));
      }
   }

   private int textHeight(int rowIdx) {
      return this.rowTexts.get(rowIdx).size() * (9 + 1);
   }

   private int rowHeightAt(int rowIdx) {
      return rowIdx < this.data.queue().size()
         ? this.queueRowHeight(rowIdx)
         : this.entryRowHeight(rowIdx, this.data.entries().get(rowIdx - this.data.queue().size()));
   }

   private static boolean hasToggle(ControlledProjectsPayload.ProjectEntry entry) {
      return entry.maxLevel() > 1 && entry.currentLevel() < entry.maxLevel() - 1;
   }

   private static boolean hasForget(ControlledProjectsPayload.ProjectEntry entry) {
      return !entry.isTownHall();
   }

   private int entryButtonY(int rowIdx) {
      return this.textHeight(rowIdx) + 2;
   }

   private int entryRowHeight(int rowIdx, ControlledProjectsPayload.ProjectEntry entry) {
      int h = this.entryButtonY(rowIdx);
      if (hasToggle(entry) || hasForget(entry)) {
         h += 16;
      }

      return h + 6;
   }

   private int pageStart(int page) {
      return this.pageStarts.isEmpty() ? 0 : this.pageStarts.get(page);
   }

   private int pageEnd(int page) {
      return page + 1 < this.pageStarts.size() ? this.pageStarts.get(page + 1) : this.rowCount;
   }

   private int queueButtonY(int rowIdx) {
      return this.textHeight(rowIdx) + 2;
   }

   private int queueRowHeight(int rowIdx) {
      return this.queueButtonY(rowIdx) + 14 + 4;
   }

   private void rebuildPageButtons() {
      for (Button btn : this.pageButtons) {
         this.removeWidget(btn);
      }

      this.pageButtons.clear();
      int parchX = (this.width - 204) / 2;
      int parchY = (this.height - 220) / 2;
      int contentX = parchX + 16;
      int rowY = parchY + 42 + this.subtitleOffset();
      int startIdx = this.pageStart(this.currentPage);
      int endIdx = this.pageEnd(this.currentPage);
      int queueSize = this.data.queue().size();

      for (int i = startIdx; i < endIdx; i++) {
         int btnX = contentX + textWidth(204) - 80;
         if (i < queueSize) {
            ControlledProjectsPayload.QueueEntry q = this.data.queue().get(i);
            int btnRowY = rowY + this.queueButtonY(i);
            Button cancelBtn = Button.builder(
                  Component.translatable("gui.millenaire.controlled_projects.cancel"),
                  b -> PacketDistributor.sendToServer(
                     new BuildingProjectCancelRequestPayload(this.data.villageUuid(), q.index(), q.planSetId(), q.plannedX(), q.plannedY(), q.plannedZ(), true),
                     new CustomPacketPayload[0]
                  )
               )
               .bounds(btnX, btnRowY, 80, 14)
               .build();
            this.addRenderableWidget(cancelBtn);
            this.pageButtons.add(cancelBtn);
            int downX = btnX - 2 - 18;
            int upX = downX - 2 - 18;
            if (q.index() > 0) {
               Button up = Button.builder(
                     Component.literal("↑"),
                     b -> PacketDistributor.sendToServer(
                        new BuildingProjectReorderRequestPayload(
                           this.data.villageUuid(), q.index(), q.index() - 1, q.planSetId(), q.plannedX(), q.plannedY(), q.plannedZ()
                        ),
                        new CustomPacketPayload[0]
                     )
                  )
                  .bounds(upX, btnRowY, 18, 14)
                  .build();
               this.addRenderableWidget(up);
               this.pageButtons.add(up);
            }

            if (q.index() < queueSize - 1) {
               Button down = Button.builder(
                     Component.literal("↓"),
                     b -> PacketDistributor.sendToServer(
                        new BuildingProjectReorderRequestPayload(
                           this.data.villageUuid(), q.index(), q.index() + 1, q.planSetId(), q.plannedX(), q.plannedY(), q.plannedZ()
                        ),
                        new CustomPacketPayload[0]
                     )
                  )
                  .bounds(downX, btnRowY, 18, 14)
                  .build();
               this.addRenderableWidget(down);
               this.pageButtons.add(down);
            }

            rowY += this.queueRowHeight(i);
         } else {
            int entryIdx = i - queueSize;
            ControlledProjectsPayload.ProjectEntry entry = this.data.entries().get(entryIdx);
            int btnY = rowY + this.entryButtonY(i);
            int slotX = btnX;
            if (hasForget(entry)) {
               Button forget = Button.builder(
                     Component.translatable("gui.millenaire.controlled_projects.forget"),
                     b -> PacketDistributor.sendToServer(
                        new BuildingForgetRequestPayload(this.data.villageUuid(), entry.buildingId()), new CustomPacketPayload[0]
                     )
                  )
                  .bounds(slotX, btnY, 80, 14)
                  .build();
               this.addRenderableWidget(forget);
               this.pageButtons.add(forget);
               slotX -= 84;
            }

            if (hasToggle(entry)) {
               String labelKey = entry.upgradesAllowed() ? "gui.millenaire.controlled_projects.forbid" : "gui.millenaire.controlled_projects.allow";
               boolean newAllow = !entry.upgradesAllowed();
               Button toggle = Button.builder(
                     Component.translatable(labelKey),
                     b -> PacketDistributor.sendToServer(
                        new BuildingUpgradeToggleRequestPayload(this.data.villageUuid(), entry.buildingId(), newAllow), new CustomPacketPayload[0]
                     )
                  )
                  .bounds(slotX, btnY, 80, 14)
                  .build();
               this.addRenderableWidget(toggle);
               this.pageButtons.add(toggle);
            }

            rowY += this.entryRowHeight(i, entry);
         }
      }
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
      if (this.rowCount == 0) {
         for (FormattedCharSequence line : this.font.split(Component.translatable("gui.millenaire.controlled_projects.empty"), textWidth(204))) {
            graphics.drawString(this.font, line, parchX + 16, y, 5914656, false);
            y += 9 + 1;
         }
      } else {
         this.renderRows(graphics, parchX, y);
      }

      this.renderPageCounter(graphics, parchX, parchY, 204, 220);
      super.render(graphics, mouseX, mouseY, partialTick);
   }

   private void renderRows(GuiGraphics graphics, int parchX, int startY) {
      int contentX = parchX + 16;
      int rowY = startY;
      int startIdx = this.pageStart(this.currentPage);
      int endIdx = this.pageEnd(this.currentPage);

      for (int i = startIdx; i < endIdx; i++) {
         int ry = rowY;

         for (ControlledProjectsScreen.TextRow row : this.rowTexts.get(i)) {
            graphics.drawString(this.font, row.text(), contentX + row.indent(), ry, row.color(), false);
            ry += 9 + 1;
         }

         rowY += this.rowHeightAt(i);
      }
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   private record TextRow(FormattedCharSequence text, int indent, int color) {
   }
}
