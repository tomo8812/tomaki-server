package net.satisfy.candlelight.client.gui;

import dev.architectury.networking.NetworkManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.satisfy.candlelight.core.item.TableSignItem;
import net.satisfy.candlelight.core.networking.packet.SetTableSignTextPacket;

public class TableSignEditScreen extends Screen {
   private static final int TEXTURE_SIZE = 192;
   private static final int TEXT_X = 36;
   private static final int TEXT_Y = 32;
   private static final int TEXT_WIDTH = 114;
   private static final int LINE_HEIGHT = 10;
   private static final int TEXT_COLOR = 0;
   private final InteractionHand hand;
   private final List<StringBuilder> lines = new ArrayList<>();
   private int cursorLine;
   private int cursorColumn;
   private int frameTick;

   public TableSignEditScreen(InteractionHand hand, String initialText) {
      super(Component.translatable("gui.candlelight.table_sign.edit"));
      this.hand = hand;

      for (String line : TableSignItem.getLines(initialText)) {
         if (this.lines.size() < TableSignItem.maxLines()) {
            this.lines.add(new StringBuilder(line));
         }
      }

      if (this.lines.isEmpty()) {
         this.lines.add(new StringBuilder());
      }

      this.cursorLine = this.lines.size() - 1;
      this.cursorColumn = this.lines.get(this.cursorLine).length();
   }

   protected void init() {
      this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose()).bounds(this.width / 2 - 50, 196, 100, 20).build());
   }

   public void tick() {
      this.frameTick++;
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      int left = (this.width - 192) / 2;

      for (int i = 0; i < this.lines.size(); i++) {
         graphics.drawString(this.font, this.lines.get(i).toString(), left + 36, 32 + i * 10, 0, false);
      }

      if (this.frameTick / 6 % 2 == 0) {
         String beforeCursor = this.lines.get(this.cursorLine).substring(0, this.cursorColumn);
         int cursorX = left + 36 + this.font.width(beforeCursor);
         int cursorY = 32 + this.cursorLine * 10;
         if (this.cursorColumn < this.lines.get(this.cursorLine).length()) {
            graphics.fill(cursorX, cursorY - 1, cursorX + 1, cursorY + 9, -16777216);
         } else {
            graphics.drawString(this.font, "_", cursorX, cursorY, 0, false);
         }
      }
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      graphics.blit(NoteGui.NOTE_TEXTURE, (this.width - 192) / 2, 2, 0, 0, 192, 192);
   }

   public boolean charTyped(char character, int modifiers) {
      if (!StringUtil.isAllowedChatCharacter(character)) {
         return false;
      }

      StringBuilder line = this.lines.get(this.cursorLine);
      String candidate = new StringBuilder(line).insert(this.cursorColumn, character).toString();
      if (candidate.length() <= 40 && this.font.width(candidate) <= 114) {
         line.insert(this.cursorColumn, character);
         this.cursorColumn++;
      }

      return true;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      StringBuilder line = this.lines.get(this.cursorLine);
      switch (keyCode) {
         case 256:
            this.onClose();
            return true;
         case 257:
         case 335:
            if (this.lines.size() < TableSignItem.maxLines()) {
               String rest = line.substring(this.cursorColumn);
               line.setLength(this.cursorColumn);
               this.lines.add(this.cursorLine + 1, new StringBuilder(rest));
               this.cursorLine++;
               this.cursorColumn = 0;
            }

            return true;
         case 259:
            if (this.cursorColumn > 0) {
               line.deleteCharAt(this.cursorColumn - 1);
               this.cursorColumn--;
            } else if (this.cursorLine > 0) {
               StringBuilder previous = this.lines.get(this.cursorLine - 1);
               String merged = previous.toString() + line;
               if (merged.length() <= 40 && this.font.width(merged) <= 114) {
                  this.cursorColumn = previous.length();
                  previous.append(line);
                  this.lines.remove(this.cursorLine);
                  this.cursorLine--;
               }
            }

            return true;
         case 261:
            if (this.cursorColumn < line.length()) {
               line.deleteCharAt(this.cursorColumn);
            } else if (this.cursorLine < this.lines.size() - 1) {
               String merged = line.toString() + this.lines.get(this.cursorLine + 1);
               if (merged.length() <= 40 && this.font.width(merged) <= 114) {
                  line.append(this.lines.remove(this.cursorLine + 1));
               }
            }

            return true;
         case 262:
            if (this.cursorColumn < line.length()) {
               this.cursorColumn++;
            } else if (this.cursorLine < this.lines.size() - 1) {
               this.cursorLine++;
               this.cursorColumn = 0;
            }

            return true;
         case 263:
            if (this.cursorColumn > 0) {
               this.cursorColumn--;
            } else if (this.cursorLine > 0) {
               this.cursorLine--;
               this.cursorColumn = this.lines.get(this.cursorLine).length();
            }

            return true;
         case 264:
            if (this.cursorLine < this.lines.size() - 1) {
               this.cursorLine++;
               this.cursorColumn = Math.min(this.cursorColumn, this.lines.get(this.cursorLine).length());
            }

            return true;
         case 265:
            if (this.cursorLine > 0) {
               this.cursorLine--;
               this.cursorColumn = Math.min(this.cursorColumn, this.lines.get(this.cursorLine).length());
            }

            return true;
         case 268:
            this.cursorColumn = 0;
            return true;
         case 269:
            this.cursorColumn = line.length();
            return true;
         default:
            return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public void onClose() {
      List<String> text = this.lines.stream().map(StringBuilder::toString).toList();
      NetworkManager.sendToServer(new SetTableSignTextPacket(this.hand, String.join("\n", text)));
      super.onClose();
   }

   public boolean isPauseScreen() {
      return false;
   }
}
