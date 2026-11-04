package dev.emi.emi.chess;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

class ChessEmiStack extends EmiStack {
   public final int position;

   public ChessEmiStack(int position) {
      this.position = position;
   }

   @Override
   public void render(GuiGraphics raw, int x, int y, float delta, int flags) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      ChessPiece piece = EmiChess.getBoard().get(this.position);
      context.enableDepthTest();
      EmiChess chess = EmiChess.get();
      if (chess.pendingPromotion != -1) {
         PieceType type = null;
         int dir = chess.pendingPromotion > 31 ? -8 : 8;
         if (this.position == chess.pendingPromotion) {
            type = PieceType.QUEEN;
         } else if (this.position == chess.pendingPromotion + dir) {
            type = PieceType.KNIGHT;
         } else if (this.position == chess.pendingPromotion + dir * 2) {
            type = PieceType.ROOK;
         } else if (this.position == chess.pendingPromotion + dir * 3) {
            type = PieceType.BISHOP;
         }

         if (type != null) {
            context.push();
            context.matrices().translate(0.0F, 0.0F, 10.0F);
            context.fill(x - 1, y - 1, 18, 18, 1426063360);
            context.matrices().translate(0.0F, 0.0F, 90.0F);
            context.drawTexture(EmiRenderHelper.PIECES, x, y, 100, type.u, chess.pendingPromotion > 31 ? 0.0F : 16.0F, 16, 16, 256, 256);
            context.pop();
            return;
         }
      }

      context.push();
      context.matrices().translate(0.0F, 0.0F, 10.0F);
      if (chess.isTarget(this.position)) {
         context.fill(x - 1, y - 1, 18, 18, 1431699200);
      }

      boolean dragging = !EmiScreenManager.draggedStack.isEmpty();
      ChessMove move = chess.board.lastMove;
      if (!dragging && move != null && (move.start() == this.position || move.end() == this.position)) {
         context.fill(x - 1, y - 1, 18, 18, 1437248000);
      }

      if (!dragging && piece != null && piece.type() == PieceType.KING && chess.board.isChecked(piece.color())) {
         context.fill(x - 1, y - 1, 18, 18, 1442775040);
      }

      context.pop();
      if (piece != null) {
         context.push();
         context.matrices().translate(0.0F, 0.0F, 100.0F);
         context.drawTexture(EmiRenderHelper.PIECES, x, y, 100, piece.type().u, piece.color() == PieceColor.BLACK ? 0.0F : 16.0F, 16, 16, 256, 256);
         context.pop();
      }
   }

   @Override
   public EmiStack copy() {
      return this;
   }

   @Override
   public boolean isEmpty() {
      return EmiChess.get().board.get(this.position) == null;
   }

   @Override
   public DataComponentPatch getComponentChanges() {
      return DataComponentPatch.EMPTY;
   }

   @Override
   public Object getKey() {
      return this.position;
   }

   @Override
   public ResourceLocation getId() {
      return EmiPort.id("emi:/chess/" + this.position);
   }

   @Override
   public List<Component> getTooltipText() {
      return List.of();
   }

   @Override
   public List<ClientTooltipComponent> getTooltip() {
      ChessPiece piece = EmiChess.getBoard().get(this.position);
      if (piece != null) {
         List<ClientTooltipComponent> list = Lists.newArrayList();
         list.add(
            ClientTooltipComponent.create(
               EmiPort.ordered(EmiPort.translatable("emi.chess.piece." + piece.color().toString().toLowerCase() + "_" + piece.type().toString().toLowerCase()))
            )
         );
         Minecraft client = Minecraft.getInstance();
         if (!EmiChess.get().started) {
            if (piece.type() == PieceType.KING) {
               list.add(
                  new ChessTooltipComponent(
                     ChessPiece.of(PieceType.PAWN, PieceColor.BLACK),
                     ChessPiece.of(PieceType.KING, PieceColor.BLACK),
                     EmiPort.translatable("emi.chess.tooltip.invite")
                  )
               );
               if (EmiChess.get().pending != null) {
                  Player player = client.level.getPlayerByUUID(EmiChess.get().pending);
                  if (player != null) {
                     list.add(
                        new ChessTooltipComponent(
                           ChessPiece.of(PieceType.KING, PieceColor.WHITE),
                           ChessPiece.of(PieceType.KING, PieceColor.BLACK),
                           EmiPort.translatable("emi.chess.tooltip.accept", player.getName())
                        )
                     );
                     list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.chess.tooltip.decline", player.getName()))));
                  }
               }
            }
         } else if (piece.type() == PieceType.KING && piece.color() == PieceColor.WHITE) {
            list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.chess.tooltip.restart"))));
         }

         return list;
      } else {
         return List.of();
      }
   }

   @Override
   public Component getName() {
      return EmiPort.literal("Chess Piece");
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public EmiIngredient copy() {
      return this.copy();
   }
}
