package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.millenaire.village.panel.PanelLine;

public abstract class AbstractMillenaireScreen extends Screen {
   protected static final int PARCHMENT_PADDING = 16;
   protected static final int PARCHMENT_TITLE_HEIGHT = 20;
   protected static final int SEPARATOR_GAP = 6;
   protected static final int HEADER_HEIGHT = 42;
   protected static final int CORNER_BUTTON_SIZE = 16;
   protected static final int CORNER_BUTTON_MARGIN = 1;
   protected static final int TITLE_TOP = 18;
   protected static final int TITLE_ROW_HEIGHT = 10;
   private static final int MAX_TITLE_ROWS = 2;
   protected static final int BOTTOM_RESERVE = 15;
   protected int currentPage = 0;
   protected int totalPages = 1;
   protected AbstractWidget prevButton;
   protected AbstractWidget nextButton;
   private static final int ARROW_WIDTH = 32;
   private static final int ARROW_HEIGHT = 13;
   private static final int ARROW_LEFT_X = 1;
   private static final int ARROW_INSET_RIGHT = 32;
   private static final int ARROW_INSET_BOTTOM = 13;

   protected AbstractMillenaireScreen(Component title) {
      super(title);
   }

   protected static int computeMaxLinesPerPage(int parchmentHeight) {
      return (parchmentHeight - 42 - 15) / 11;
   }

   protected static int textWidth(int parchmentWidth) {
      return parchmentWidth - 32;
   }

   protected Component fitBoldTitle(Component title, int maxWidth) {
      Component styledTitle = title.copy().withStyle(s -> s.withBold(true));
      if (this.font.width(styledTitle) <= maxWidth) {
         return styledTitle;
      }

      String raw = styledTitle.getString();
      String ellipsis = "...";
      StringBuilder truncated = new StringBuilder();

      for (int i = 0; i < raw.length() && this.boldWidth(truncated.toString() + raw.charAt(i) + ellipsis) <= maxWidth; i++) {
         truncated.append(raw.charAt(i));
      }

      return Component.literal(truncated + ellipsis).withStyle(s -> s.withBold(true));
   }

   private int boldWidth(String text) {
      return this.font.width(Component.literal(text).withStyle(s -> s.withBold(true)));
   }

   protected int renderParchmentHeader(GuiGraphics graphics, Component title, int parchX, int parchY, int parchWidth) {
      this.drawTitleRow(graphics, this.fitBoldTitle(title, parchWidth - 32), parchX, parchY + 18, parchWidth);
      return this.renderHeaderSeparator(graphics, parchX, parchY, parchWidth, 0);
   }

   protected List<String> titleRows(Component title, @Nullable String villageName, int parchWidth) {
      int maxWidth = parchWidth - 32;
      List<String> rows = TextWrapper.wrapTitle(title.getString(), villageName, maxWidth, this::boldWidth);
      if (rows.size() <= 2) {
         return rows;
      }

      String rest = String.join(" ", rows.subList(1, rows.size()));
      List<String> capped = new ArrayList<>(rows.subList(0, 1));
      capped.add(this.fitBoldTitle(Component.literal(rest), maxWidth).getString());
      return capped;
   }

   protected static int titleExtraHeight(List<String> titleRows) {
      return (titleRows.size() - 1) * 10;
   }

   protected void drawTitleRows(GuiGraphics graphics, List<String> titleRows, int parchX, int parchY, int parchWidth) {
      int y = parchY + 18;

      for (String row : titleRows) {
         this.drawTitleRow(graphics, Component.literal(row).withStyle(s -> s.withBold(true)), parchX, y, parchWidth);
         y += 10;
      }
   }

   protected int renderWrappedParchmentHeader(GuiGraphics graphics, List<String> titleRows, int parchX, int parchY, int parchWidth) {
      this.drawTitleRows(graphics, titleRows, parchX, parchY, parchWidth);
      return this.renderHeaderSeparator(graphics, parchX, parchY, parchWidth, titleExtraHeight(titleRows));
   }

   private void drawTitleRow(GuiGraphics graphics, Component styledRow, int parchX, int y, int parchWidth) {
      int titleX = parchX + parchWidth / 2 - this.font.width(styledRow) / 2;
      graphics.drawString(this.font, styledRow, titleX, y, -14018560, false);
   }

   private int renderHeaderSeparator(GuiGraphics graphics, int parchX, int parchY, int parchWidth, int titleExtra) {
      int y = parchY + 16 + 20 + titleExtra;
      int contentX = parchX + 16;
      graphics.fill(contentX, y, parchX + parchWidth - 16, y + 1, -5601178);
      return y + 6;
   }

   protected void addTravelBookButton(int parchX, int parchY, int parchWidth, OnPress action) {
      int tbButtonSize = 16;
      int tbButtonX = parchX + parchWidth - tbButtonSize - 1;
      int tbButtonY = parchY + 1;
      this.addRenderableWidget(
         new IconButton(
            tbButtonX,
            tbButtonY,
            tbButtonSize,
            tbButtonSize,
            new ItemStack(Items.WRITABLE_BOOK),
            Component.translatable("gui.millenaire.travel_book.open"),
            action
         )
      );
   }

   protected List<PanelLine> wrapLines(List<PanelLine> lines, int parchmentWidth) {
      return PanelRenderHelper.wrapLines(lines, this.font, textWidth(parchmentWidth));
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
   }

   public boolean isPauseScreen() {
      return false;
   }

   protected void initPaginationButtons(int parchX, int parchY, int parchWidth, int parchHeight, int padding, boolean texturedArrows) {
      int arrowY = parchY + parchHeight - 13;
      this.prevButton = new AbstractMillenaireScreen.ArrowZone(parchX + 1, arrowY, 32, 13, true, !texturedArrows, () -> {
         if (this.currentPage > 0) {
            this.currentPage--;
            this.onPageChanged();
         }
      });
      this.addRenderableWidget(this.prevButton);
      this.nextButton = new AbstractMillenaireScreen.ArrowZone(parchX + parchWidth - 32, arrowY, 32, 13, false, !texturedArrows, () -> {
         if (this.currentPage < this.totalPages - 1) {
            this.currentPage++;
            this.onPageChanged();
         }
      });
      this.addRenderableWidget(this.nextButton);
   }

   protected void updatePaginationButtons() {
      this.prevButton.visible = this.totalPages > 1;
      this.nextButton.visible = this.totalPages > 1;
      this.prevButton.active = this.currentPage > 0;
      this.nextButton.active = this.currentPage < this.totalPages - 1;
   }

   protected void onPageChanged() {
      this.updatePaginationButtons();
   }

   protected void renderPageCounter(GuiGraphics graphics, int parchX, int parchY, int parchWidth, int parchHeight) {
      if (this.totalPages > 1) {
         String pageStr = this.currentPage + 1 + "/" + this.totalPages;
         PanelRenderHelper.drawCenteredText(graphics, this.font, Component.literal(pageStr), parchX + parchWidth / 2, parchY + parchHeight - 10, -14018560);
      }
   }

   protected static class ArrowZone extends AbstractWidget {
      private final Runnable onClick;
      private final boolean pointsLeft;
      private final boolean drawTriangle;

      ArrowZone(int x, int y, int width, int height, boolean pointsLeft, boolean drawTriangle, Runnable onClick) {
         super(x, y, width, height, Component.empty());
         this.pointsLeft = pointsLeft;
         this.drawTriangle = drawTriangle;
         this.onClick = onClick;
      }

      public void onClick(double mouseX, double mouseY) {
         this.onClick.run();
      }

      protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
         if (this.drawTriangle) {
            int color = !this.active ? 1431646976 : (this.isHovered() ? -8762112 : -14018560);
            int aw = 7;
            int ah = 11;
            int baseX = this.getX() + (this.getWidth() - aw) / 2;
            int cy = this.getY() + this.getHeight() / 2;

            for (int i = 0; i < aw; i++) {
               int half = Math.round(ah / 2.0F * ((float)i / (aw - 1)));
               int colX = this.pointsLeft ? baseX + i : baseX + (aw - 1 - i);
               graphics.fill(colX, cy - half, colX + 1, cy + half + 1, color);
            }
         }
      }

      protected void updateWidgetNarration(NarrationElementOutput output) {
      }
   }
}
