package dev.emi.emi.chess;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.Mth;

class ChessBoard {
   public ChessPiece[] pieces = new ChessPiece[64];
   public ChessMove lastMove;
   public int castles;

   private ChessBoard() {
   }

   public ChessBoard copy() {
      ChessBoard c = new ChessBoard();
      c.pieces = (ChessPiece[])this.pieces.clone();
      c.lastMove = this.lastMove;
      c.castles = this.castles;
      return c;
   }

   public ChessPiece get(int x, int y) {
      return this.get(x + y * 8);
   }

   public ChessPiece get(int position) {
      return position >= 0 && position < this.pieces.length ? this.pieces[position] : null;
   }

   public void set(int x, int y, ChessPiece piece) {
      this.set(x + y * 8, piece);
   }

   public void set(int position, ChessPiece piece) {
      if (position >= 0 && position <= this.pieces.length) {
         this.pieces[position] = piece;
      }
   }

   public void unmove(ChessMove move) {
      ChessPiece piece = this.get(move.end());
      if (move.type() == 1) {
         this.set(move.end() + (piece.color() == PieceColor.WHITE ? 8 : -8), ChessPiece.of(PieceType.PAWN, piece.color().opposite()));
      } else if (move.type() == 2) {
         int mDiff = move.end() - move.start();
         int y = move.start() / 8;
         int rx = Mth.clamp(mDiff * 8, 0, 7);
         ChessPiece rook = ChessPiece.of(PieceType.ROOK, piece.color());
         this.set(rx, y, rook);
         this.set(move.start() + mDiff / 2, null);
      } else if (move.type() > 2) {
         piece = ChessPiece.of(PieceType.PAWN, piece.color());
      }

      this.set(move.start(), piece);
      this.set(move.end(), null);
   }

   public void move(ChessMove move) {
      ChessPiece piece = this.get(move.start());
      this.set(move.start(), null);
      this.set(move.end(), piece);
      if (move.type() == 1) {
         this.set(this.lastMove.end(), null);
      } else if (move.type() == 2) {
         int mDiff = move.end() - move.start();
         int y = move.start() / 8;
         int rx = Mth.clamp(mDiff * 8, 0, 7);
         ChessPiece rook = this.get(rx, y);
         this.set(rx, y, null);
         this.set(move.start() + mDiff / 2, rook);
      } else if (move.type() == 3) {
         this.set(move.end(), ChessPiece.of(PieceType.QUEEN, piece.color()));
      } else if (move.type() == 4) {
         this.set(move.end(), ChessPiece.of(PieceType.KNIGHT, piece.color()));
      } else if (move.type() == 5) {
         this.set(move.end(), ChessPiece.of(PieceType.ROOK, piece.color()));
      } else if (move.type() == 6) {
         this.set(move.end(), ChessPiece.of(PieceType.BISHOP, piece.color()));
      }

      this.lastMove = move;
      if (piece.type() == PieceType.KING) {
         int colorShift = piece.color() == PieceColor.WHITE ? 0 : 2;
         this.castles |= 3 << colorShift;
      } else if (piece.type() == PieceType.ROOK) {
         int ex = move.end() % 8;
         int ey = move.end() / 8;
         if (ex == 0 && ey == 0) {
            this.castles |= 2;
         } else if (ex == 7 && ey == 0) {
            this.castles |= 1;
         } else if (ex == 0 && ey == 7) {
            this.castles |= 8;
         } else if (ex == 7 && ey == 7) {
            this.castles |= 4;
         }
      }
   }

   public int getKing(PieceColor color) {
      for (int i = 0; i < 64; i++) {
         ChessPiece p = this.get(i);
         if (p != null && p.type() == PieceType.KING && p.color() == color) {
            return i;
         }
      }

      return -1;
   }

   public boolean isChecked(PieceColor color) {
      int k = this.getKing(color);

      for (ChessMove move : this.getAllMoves(color.opposite())) {
         if (move.end() == k) {
            return true;
         }
      }

      return false;
   }

   public List<ChessMove> getLegal(List<ChessMove> moves) {
      if (moves.isEmpty()) {
         return moves;
      }

      ChessPiece piece = this.get(moves.get(0).start());
      PieceColor color = piece.color();
      return moves.stream().filter(m -> {
         ChessBoard copy = this.copy();
         copy.move(m);
         return !copy.isChecked(color);
      }).toList();
   }

   public List<ChessMove> getAllMoves(PieceColor color) {
      List<ChessMove> list = Lists.newArrayList();

      for (int i = 0; i < 64; i++) {
         ChessPiece p = this.get(i);
         if (p != null && p.color() == color) {
            list.addAll(this.getMoves(i));
         }
      }

      return list;
   }

   public List<ChessMove> getMoves(int position) {
      ChessPiece self = this.get(position);
      int x = position % 8;
      int y = position / 8;
      if (self == null) {
         return List.of();
      }

      List<ChessMove> list = Lists.newArrayList();
      switch (self.type()) {
         case PAWN:
            int pOff = self.color() == PieceColor.BLACK ? 1 : -1;
            if ((pOff == -1 && y == 6 || pOff == 1 && y == 1) && this.get(x, y + pOff) == null && this.get(x, y + pOff * 2) == null) {
               this.addMove(list, self, position, x, y + pOff * 2, 0);
            }

            int moveType = 0;
            if (y + pOff == 0 || y + pOff == 7) {
               moveType = 3;
            }

            if (this.get(x, y + pOff) == null) {
               this.addMove(list, self, position, x, y + pOff, moveType);
            }

            if (this.get(x - 1, y + pOff) != null) {
               this.addMove(list, self, position, x - 1, y + pOff, moveType);
            }

            if (this.get(x + 1, y + pOff) != null) {
               this.addMove(list, self, position, x + 1, y + pOff, moveType);
            }

            if (this.lastMove != null) {
               ChessPiece lastPiece = this.get(this.lastMove.end());
               if (lastPiece != null && lastPiece.type() == PieceType.PAWN && this.lastMove.end() / 8 == y && this.lastMove.start() / 8 == y + pOff * 2) {
                  int lx = this.lastMove.end() % 8;
                  if (lx == x - 1) {
                     this.addMove(list, self, position, x - 1, y + pOff, 1);
                  } else if (lx == x + 1) {
                     this.addMove(list, self, position, x + 1, y + pOff, 1);
                  }
               }
            }
            break;
         case ROOK:
            this.addRookMoves(list, self, position, x, y);
            break;
         case KNIGHT:
            this.addMove(list, self, position, x + 1, y + 2, 0);
            this.addMove(list, self, position, x + 1, y - 2, 0);
            this.addMove(list, self, position, x - 1, y + 2, 0);
            this.addMove(list, self, position, x - 1, y - 2, 0);
            this.addMove(list, self, position, x + 2, y + 1, 0);
            this.addMove(list, self, position, x + 2, y - 1, 0);
            this.addMove(list, self, position, x - 2, y + 1, 0);
            this.addMove(list, self, position, x - 2, y - 1, 0);
            break;
         case BISHOP:
            this.addBishopMoves(list, self, position, x, y);
            break;
         case QUEEN:
            this.addRookMoves(list, self, position, x, y);
            this.addBishopMoves(list, self, position, x, y);
            break;
         case KING:
            this.addMove(list, self, position, x - 1, y - 1, 0);
            this.addMove(list, self, position, x, y - 1, 0);
            this.addMove(list, self, position, x + 1, y - 1, 0);
            this.addMove(list, self, position, x - 1, y, 0);
            this.addMove(list, self, position, x + 1, y, 0);
            this.addMove(list, self, position, x - 1, y + 1, 0);
            this.addMove(list, self, position, x, y + 1, 0);
            this.addMove(list, self, position, x + 1, y + 1, 0);
            int colorShift = self.color() == PieceColor.WHITE ? 0 : 2;
            int castles = this.castles >> colorShift;
            if ((castles & 2) == 0) {
               int cx = 1;

               label128:
               while (true) {
                  if (cx >= 4) {
                     for (int cxx = 5; cxx > 2; cxx--) {
                        ChessBoard board = this.copy();
                        board.move(new ChessMove(position, y * 8 + cxx, 0));
                        if (board.isChecked(self.color())) {
                           break label128;
                        }
                     }

                     this.addMove(list, self, position, x - 2, y, 2);
                     break;
                  }

                  if (this.get(cx, y) != null) {
                     break;
                  }

                  cx++;
               }
            }

            if ((castles & 1) == 0) {
               int cx = 5;

               while (true) {
                  if (cx >= 7) {
                     for (int cxx = 4; cxx < 7; cxx++) {
                        ChessBoard board = this.copy();
                        board.move(new ChessMove(position, y * 8 + cxx, 0));
                        if (board.isChecked(self.color())) {
                           return list;
                        }
                     }

                     this.addMove(list, self, position, x + 2, y, 2);
                     break;
                  }

                  if (this.get(cx, y) != null) {
                     break;
                  }

                  cx++;
               }
            }
      }

      return list;
   }

   private void addRookMoves(List<ChessMove> list, ChessPiece self, int position, int x, int y) {
      for (int i = 0; i < 4; i++) {
         int xm = 0;
         int ym = 0;
         if (i < 2) {
            xm = i * 2 - 1;
         } else {
            ym = (i - 2) * 2 - 1;
         }

         int xo = x + xm;

         for (int yo = y + ym; xo >= 0 && xo < 8 && yo >= 0 && yo < 8; yo += ym) {
            this.addMove(list, self, position, xo, yo, 0);
            if (this.get(xo, yo) != null) {
               break;
            }

            xo += xm;
         }
      }
   }

   private void addBishopMoves(List<ChessMove> list, ChessPiece self, int position, int x, int y) {
      for (int i = 0; i < 4; i++) {
         int xm = (i & 1) * 2 - 1;
         int ym = (i & 2) - 1;
         int xo = x + xm;

         for (int yo = y + ym; xo >= 0 && xo < 8 && yo >= 0 && yo < 8; yo += ym) {
            this.addMove(list, self, position, xo, yo, 0);
            if (this.get(xo, yo) != null) {
               break;
            }

            xo += xm;
         }
      }
   }

   private void addMove(List<ChessMove> list, ChessPiece self, int position, int x, int y, int type) {
      ChessPiece target = this.get(x, y);
      if (x >= 0 && x < 8 && y >= 0 && y < 8 && (target == null || target.color() != self.color())) {
         list.add(ChessMove.of(position, x + y * 8, type));
         if (type == 3) {
            list.add(new ChessMove(position, x + y * 8, 4));
            list.add(new ChessMove(position, x + y * 8, 5));
            list.add(new ChessMove(position, x + y * 8, 6));
         }
      }
   }

   public static ChessBoard setupBoard() {
      ChessBoard board = new ChessBoard();

      for (int x = 0; x < 8; x++) {
         board.set(x, 1, ChessPiece.of(PieceType.PAWN, PieceColor.BLACK));
         board.set(x, 6, ChessPiece.of(PieceType.PAWN, PieceColor.WHITE));
      }

      for (int i = 0; i < 2; i++) {
         int y = i * 7;
         PieceColor color = i == 0 ? PieceColor.BLACK : PieceColor.WHITE;
         board.set(0, y, ChessPiece.of(PieceType.ROOK, color));
         board.set(1, y, ChessPiece.of(PieceType.KNIGHT, color));
         board.set(2, y, ChessPiece.of(PieceType.BISHOP, color));
         board.set(3, y, ChessPiece.of(PieceType.QUEEN, color));
         board.set(4, y, ChessPiece.of(PieceType.KING, color));
         board.set(5, y, ChessPiece.of(PieceType.BISHOP, color));
         board.set(6, y, ChessPiece.of(PieceType.KNIGHT, color));
         board.set(7, y, ChessPiece.of(PieceType.ROOK, color));
      }

      return board;
   }
}
