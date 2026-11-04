package dev.emi.emi.chess;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.tooltip.EmiTooltipComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

class ChessTooltipComponent implements EmiTooltipComponent {
   private final ChessPiece dragged;
   private final ChessPiece hovered;
   private final Component description;

   public ChessTooltipComponent(ChessPiece dragged, ChessPiece hovered, Component description) {
      this.dragged = dragged;
      this.hovered = hovered;
      this.description = description;
   }

   public int getHeight() {
      return 30;
   }

   public int getWidth(Font textRenderer) {
      return Math.max(textRenderer.width(this.description), 48);
   }

   @Override
   public void drawTooltip(EmiDrawContext context, EmiTooltipComponent.TooltipRenderData tooltip) {
      context.drawTexture(EmiRenderHelper.PIECES, 0, 14, 100, this.dragged.type().u, this.dragged.color() == PieceColor.BLACK ? 0.0F : 16.0F, 16, 16);
      context.drawTexture(EmiRenderHelper.PIECES, 32, 14, 100, this.hovered.type().u, this.hovered.color() == PieceColor.BLACK ? 0.0F : 16.0F, 16, 16);
   }

   @Override
   public void drawTooltipText(EmiTooltipComponent.TextRenderData text) {
      text.draw(this.description, 0, 4, 16777215, true);
      text.draw("->", 18, 19, 16777215, true);
   }
}
