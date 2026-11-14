package com.chaosthedude.naturescompass.gui;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TransparentTextField extends EditBox {
   private Font font;
   private Component label;
   private int labelColor = 8421504;
   private boolean pseudoIsEnabled = true;
   private boolean pseudoEnableBackgroundDrawing = true;
   private int pseudoMaxStringLength = 32;
   private int pseudoLineScrollOffset;
   private int pseudoEnabledColor = 14737632;
   private int pseudoDisabledColor = 7368816;
   private int pseudoSelectionEnd;
   private long pseudoFocusedTime;

   public TransparentTextField(Font font, int x, int y, int width, int height, Component label) {
      super(font, x, y, width, height, label);
      this.font = font;
      this.label = label;
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      if (this.isVisible()) {
         if (this.pseudoEnableBackgroundDrawing) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 2130706432);
         }

         boolean showLabel = !this.isFocused() && this.getValue().isEmpty();
         int i = showLabel ? this.labelColor : (this.pseudoIsEnabled ? this.pseudoEnabledColor : this.pseudoDisabledColor);
         int j = this.getCursorPosition() - this.pseudoLineScrollOffset;
         int k = this.pseudoSelectionEnd - this.pseudoLineScrollOffset;
         String text = showLabel ? this.label.getString() : this.getValue();
         String s = this.font.plainSubstrByWidth(text.substring(this.pseudoLineScrollOffset), this.getWidth());
         boolean flag = j >= 0 && j <= s.length();
         boolean flag1 = this.isFocused() && (Util.getMillis() - this.pseudoFocusedTime) / 300L % 2L == 0L && flag;
         int l = this.pseudoEnableBackgroundDrawing ? this.getX() + 4 : this.getX();
         int i1 = this.pseudoEnableBackgroundDrawing ? this.getY() + (this.height - 8) / 2 : this.getY();
         int j1 = l;
         if (k > s.length()) {
            k = s.length();
         }

         if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = guiGraphics.drawString(this.font, s1, l, i1, i, true);
         }

         boolean flag2 = this.getCursorPosition() < this.getValue().length() || this.getValue().length() >= this.pseudoMaxStringLength;
         int k1 = j1;
         if (!flag) {
            k1 = j > 0 ? l + this.width : l;
         } else if (flag2) {
            k1 = j1 - 1;
            j1--;
         }

         if (!s.isEmpty() && flag && j < s.length()) {
            j1 = guiGraphics.drawString(this.font, s.substring(j), j1, i1, i, true);
         }

         if (flag1) {
            if (flag2) {
               guiGraphics.fill(RenderType.guiOverlay(), k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
            } else {
               guiGraphics.drawString(this.font, "_", k1, i1, i, true);
            }
         }

         if (k != j) {
            int l1 = l + this.font.width(s.substring(0, k));
            this.drawSelectionBox(guiGraphics, k1, i1 - 1, l1 - 1, i1 + 1 + 9);
         }
      }
   }

   public void setEditable(boolean enabled) {
      super.setEditable(enabled);
      this.pseudoIsEnabled = enabled;
   }

   public void setTextColor(int color) {
      super.setTextColor(color);
      this.pseudoEnabledColor = color;
   }

   public void setTextColorUneditable(int color) {
      super.setTextColorUneditable(color);
      this.pseudoDisabledColor = color;
   }

   public void setFocused(boolean isFocused) {
      if (isFocused && !this.isFocused()) {
         this.pseudoFocusedTime = Util.getMillis();
      }

      super.setFocused(isFocused);
   }

   public void setBordered(boolean enableBackgroundDrawing) {
      super.setBordered(enableBackgroundDrawing);
      this.pseudoEnableBackgroundDrawing = enableBackgroundDrawing;
   }

   public void setMaxLength(int length) {
      super.setMaxLength(length);
      this.pseudoMaxStringLength = length;
   }

   public void setHighlightPos(int position) {
      super.setHighlightPos(position);
      int i = this.getValue().length();
      this.pseudoSelectionEnd = Mth.clamp(position, 0, i);
      if (this.font != null) {
         if (this.pseudoLineScrollOffset > i) {
            this.pseudoLineScrollOffset = i;
         }

         int j = this.getInnerWidth();
         String s = this.font.plainSubstrByWidth(this.getValue().substring(this.pseudoLineScrollOffset), j, false);
         int k = s.length() + this.pseudoLineScrollOffset;
         if (this.pseudoSelectionEnd == this.pseudoLineScrollOffset) {
            this.pseudoLineScrollOffset = this.pseudoLineScrollOffset - this.font.plainSubstrByWidth(this.getValue(), j, true).length();
         }

         if (this.pseudoSelectionEnd > k) {
            this.pseudoLineScrollOffset = this.pseudoLineScrollOffset + (this.pseudoSelectionEnd - k);
         } else if (this.pseudoSelectionEnd <= this.pseudoLineScrollOffset) {
            this.pseudoLineScrollOffset = this.pseudoLineScrollOffset - (this.pseudoLineScrollOffset - this.pseudoSelectionEnd);
         }

         this.pseudoLineScrollOffset = Mth.clamp(this.pseudoLineScrollOffset, 0, i);
      }
   }

   public void setLabel(Component label) {
      this.label = label;
   }

   public void setLabelColor(int labelColor) {
      this.labelColor = labelColor;
   }

   private void drawSelectionBox(GuiGraphics guiGraphics, int startX, int startY, int endX, int endY) {
      if (startX < endX) {
         int i = startX;
         startX = endX;
         endX = i;
      }

      if (startY < endY) {
         int j = startY;
         startY = endY;
         endY = j;
      }

      if (endX > this.getX() + this.width) {
         endX = this.getX() + this.width;
      }

      if (startX > this.getX() + this.width) {
         startX = this.getX() + this.width;
      }

      guiGraphics.fill(RenderType.guiTextHighlight(), startX, startY, endX, endY, -16776961);
   }
}
