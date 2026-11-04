package net.satisfy.candlelight.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.font.TextFieldHelper.CursorStep;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.candlelight.Candlelight;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public abstract class NoteGui extends Screen {
   public static final ResourceLocation NOTE_TEXTURE = Candlelight.identifier("textures/gui/note_paper_gui.png");
   private static final Component EDIT_TITLE_TEXT = Component.literal("Enter Note Title");
   private static final Component FINALIZE_WARNING_TEXT = Component.translatable("book.finalizeWarning");
   private static final FormattedCharSequence BLACK_CURSOR_TEXT = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.BLACK));
   private static final FormattedCharSequence GRAY_CURSOR_TEXT = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));
   protected final Player player;
   protected final ItemStack itemStack;
   private final List<String> text = Lists.newArrayList();
   private final Component signedByText;
   protected boolean dirty;
   private boolean signing;
   private int frameTick;
   private String title = "";
   private final TextFieldHelper noteTitleSelectionManager = new TextFieldHelper(
      () -> this.title, title -> this.title = title, this::getClipboard, this::setClipboard, stringx -> stringx.length() < 16
   );
   private long lastClickTime;
   private int lastClickIndex = -1;
   private Button doneButton;
   private Button signButton;
   private Button finalizeButton;
   private Button cancelButton;
   @Nullable
   private NoteGui.DisplayCache pageContent;
   private final TextFieldHelper currentPageSelectionManager = new TextFieldHelper(
      this::getCurrentDisplayCache,
      this::setDisplayCache,
      this::getClipboard,
      this::setClipboard,
      stringx -> stringx.length() < 1024 && this.font.wordWrapHeight(stringx, 114) <= 128
   );
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !NoteGui.class.desiredAssertionStatus();

   public NoteGui(Player player, ItemStack itemStack) {
      super(GameNarrator.NO_TITLE);
      this.pageContent = NoteGui.DisplayCache.EMPTY;
      this.player = player;
      this.itemStack = itemStack;
      CustomData data = (CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      CompoundTag nbtCompound = data.copyTag();
      List<String> string = this.text;
      this.loadPages(nbtCompound, string::add);
      if (this.text.isEmpty()) {
         this.text.add("");
      }

      this.signedByText = Component.translatable("book.byAuthor", new Object[]{player.getName()})
         .withStyle(style -> style.withItalic(true).withColor(15917491));
   }

   static int getLineFromOffset(int[] lineStarts, int position) {
      int i = Arrays.binarySearch(lineStarts, position);
      return i < 0 ? -(i + 2) : i;
   }

   static int findLineFromPos(int[] is, int i) {
      int j = Arrays.binarySearch(is, i);
      return j < 0 ? -(j + 2) : j;
   }

   protected void init() {
      this.invalidateDisplayCache();
      this.signButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("book.signButton"), button -> {
         this.signing = true;
         this.updateButtons();
      }).bounds(this.width / 2 - 100, 196, 98, 20).build());
      this.doneButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
         assert this.minecraft != null;
         this.minecraft.setScreen(null);
         this.finalizeNote(false);
      }).bounds(this.width / 2 + 2, 196, 98, 20).build());
      this.finalizeButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("book.finalizeButton"), button -> {
         if (this.signing) {
            this.finalizeNote(true);
            assert this.minecraft != null;
            this.minecraft.setScreen(null);
         }
      }).bounds(this.width / 2 - 100, 196, 98, 20).build());
      this.cancelButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> {
         if (this.signing) {
            this.signing = false;
         }

         this.updateButtons();
      }).bounds(this.width / 2 + 2, 196, 98, 20).build());
      this.updateButtons();
   }

   public void loadPages(CompoundTag compoundTag, Consumer<String> consumer) {
      ListTag listTag = compoundTag.getList("text", 8).copy();
      IntFunction<String> intFunction;
      if (Minecraft.getInstance().isTextFilteringEnabled() && compoundTag.contains("filtered_pages", 10)) {
         CompoundTag compoundTag2 = compoundTag.getCompound("filtered_pages");
         intFunction = i -> {
            String string = String.valueOf(i);
            return compoundTag2.contains(string) ? compoundTag2.getString(string) : listTag.getString(i);
         };
      } else {
         intFunction = listTag::getString;
      }

      for (int i2 = 0; i2 < listTag.size(); i2++) {
         consumer.accept(intFunction.apply(i2));
      }
   }

   protected void removeEmptyPages() {
      ListIterator<String> listIterator = this.text.listIterator(this.text.size());

      while (listIterator.hasPrevious() && listIterator.previous().isEmpty()) {
         listIterator.remove();
      }
   }

   protected abstract void finalizeNote(boolean var1);

   protected void writeNbtData(boolean signNote) {
      ListTag nbtList = new ListTag();

      for (String page : this.text) {
         nbtList.add(StringTag.valueOf(page));
      }

      CompoundTag tag = new CompoundTag();
      tag.put("text", nbtList);
      if (signNote) {
         tag.putString("author", this.player.getGameProfile().getName());
         tag.putString("title", this.title.trim());
      }

      this.itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
   }

   private static boolean makeSureTagIsValid(@Nullable CompoundTag tag) {
      if (tag != null && tag.contains("text", 9)) {
         ListTag pages = tag.getList("text", 8);

         for (int i = 0; i < pages.size(); i++) {
            if (!(pages.get(i) instanceof StringTag)) {
               return false;
            }

            if (pages.getString(i).length() > 32767) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private String getClipboard() {
      return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
   }

   private void setClipboard(String clipboard) {
      if (this.minecraft != null) {
         TextFieldHelper.setClipboardContents(this.minecraft, clipboard);
      }
   }

   public void tick() {
      super.tick();
      this.frameTick++;
   }

   private void updateButtons() {
      this.doneButton.visible = !this.signing;
      this.signButton.visible = !this.signing;
      this.cancelButton.visible = this.signing;
      this.finalizeButton.visible = this.signing;
      this.finalizeButton.active = !this.title.trim().isEmpty();
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (this.signing) {
         return this.keyPressedSignMode(keyCode);
      } else {
         boolean bl = this.keyPressedEditMode(keyCode);
         if (bl) {
            this.invalidateDisplayCache();
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean charTyped(char chr, int modifiers) {
      if (super.charTyped(chr, modifiers)) {
         return true;
      }

      if (this.signing) {
         boolean bl = this.noteTitleSelectionManager.charTyped(chr);
         if (bl) {
            this.updateButtons();
            this.dirty = true;
            return true;
         } else {
            return false;
         }
      } else if (StringUtil.isAllowedChatCharacter(chr)) {
         this.currentPageSelectionManager.insertText(Character.toString(chr));
         this.invalidateDisplayCache();
         return true;
      } else {
         return false;
      }
   }

   private boolean keyPressedEditMode(int keyCode) {
      if (Screen.isSelectAll(keyCode)) {
         this.currentPageSelectionManager.selectAll();
         return true;
      }

      if (Screen.isCopy(keyCode)) {
         this.currentPageSelectionManager.copy();
         return true;
      }

      if (Screen.isPaste(keyCode)) {
         this.currentPageSelectionManager.paste();
         return true;
      }

      if (Screen.isCut(keyCode)) {
         this.currentPageSelectionManager.cut();
         return true;
      }

      CursorStep selectionType = Screen.hasControlDown() ? CursorStep.WORD : CursorStep.CHARACTER;
      switch (keyCode) {
         case 257:
         case 335:
            this.currentPageSelectionManager.insertText("\n");
            return true;
         case 259:
            this.currentPageSelectionManager.removeFromCursor(-1, selectionType);
            return true;
         case 261:
            this.currentPageSelectionManager.removeFromCursor(1, selectionType);
            return true;
         case 262:
            this.currentPageSelectionManager.moveBy(1, Screen.hasShiftDown(), selectionType);
            return true;
         case 263:
            this.currentPageSelectionManager.moveBy(-1, Screen.hasShiftDown(), selectionType);
            return true;
         case 264:
            this.keyDown();
            return true;
         case 265:
            this.keyUp();
            return true;
         case 268:
            this.moveToLineStart();
            return true;
         case 269:
            this.moveToLineEnd();
            return true;
         default:
            return false;
      }
   }

   private void moveUpLine() {
      this.moveVertically(-1);
   }

   private void moveDownLine() {
      this.moveVertically(1);
   }

   private void moveVertically(int lines) {
      int i = this.currentPageSelectionManager.getCursorPos();
      int j = this.getDisplayCache().changeLine(i, lines);
      this.currentPageSelectionManager.setCursorPos(j, Screen.hasShiftDown());
   }

   private void keyUp() {
      this.changeLine(-1);
   }

   private void keyDown() {
      this.changeLine(1);
   }

   private void changeLine(int i) {
      int j = this.currentPageSelectionManager.getCursorPos();
      int k = this.getDisplayCache().changeLine(j, i);
      this.currentPageSelectionManager.setCursorPos(k, Screen.hasShiftDown());
   }

   private void moveToLineStart() {
      if (Screen.hasControlDown()) {
         this.currentPageSelectionManager.setCursorToStart(Screen.hasShiftDown());
      } else {
         int i = this.currentPageSelectionManager.getCursorPos();
         int j = this.getDisplayCache().findLineStart(i);
         this.currentPageSelectionManager.setCursorPos(j, Screen.hasShiftDown());
      }
   }

   private void moveToLineEnd() {
      if (Screen.hasControlDown()) {
         this.currentPageSelectionManager.setCursorToEnd(Screen.hasShiftDown());
      } else {
         NoteGui.DisplayCache pageContent = this.getDisplayCache();
         int i = this.currentPageSelectionManager.getCursorPos();
         int j = pageContent.findLineEnd(i);
         this.currentPageSelectionManager.setCursorPos(j, Screen.hasShiftDown());
      }
   }

   private boolean keyPressedSignMode(int keyCode) {
      switch (keyCode) {
         case 257:
         case 335:
            if (!this.title.isEmpty()) {
               this.finalizeNote(true);
               if (this.minecraft != null) {
                  this.minecraft.setScreen(null);
               }
            }

            return true;
         case 259:
            this.noteTitleSelectionManager.removeCharsFromCursor(-1);
            this.updateButtons();
            this.dirty = true;
            return true;
         default:
            return false;
      }
   }

   private String getCurrentDisplayCache() {
      return !this.text.isEmpty() ? this.text.get(0) : "";
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.renderBackground(guiGraphics, mouseX, mouseY, delta);
      this.setFocused(null);
      int x = (this.width - 192) / 2;
      guiGraphics.blit(NOTE_TEXTURE, x, 2, 0, 0, 192, 192);
      if (this.signing) {
         boolean bl = this.frameTick / 6 % 2 == 0;
         FormattedCharSequence orderedText = FormattedCharSequence.composite(
            FormattedCharSequence.forward(this.title, Style.EMPTY.withColor(15785648)), bl ? BLACK_CURSOR_TEXT : GRAY_CURSOR_TEXT
         );
         int titleLabelWidth = this.font.width(EDIT_TITLE_TEXT);
         guiGraphics.drawString(this.font, EDIT_TITLE_TEXT, x + 36 + (114 - titleLabelWidth) / 2, 34, 15917491);
         int titleWidth = this.font.width(orderedText);
         guiGraphics.drawString(this.font, orderedText, x + 36 + (114 - titleWidth) / 2, 50, 14469539);
         int authorWidth = this.font.width(this.signedByText);
         guiGraphics.drawString(this.font, this.signedByText, x + 36 + (114 - authorWidth) / 2, 60, 13018471);
         List<FormattedCharSequence> lines = this.font.split(FINALIZE_WARNING_TEXT, 114);

         for (int i = 0; i < lines.size(); i++) {
            FormattedCharSequence line = lines.get(i);
            int lineWidth = this.font.width(line);
            int lineX = x + 36 + (114 - lineWidth) / 2;
            int lineY = 82 + i * 9;
            guiGraphics.drawString(this.font, line, lineX, lineY, 5921370, false);
         }
      } else {
         NoteGui.DisplayCache displayCache = this.getDisplayCache();

         for (NoteGui.LineInfo line : displayCache.lines) {
            guiGraphics.drawString(this.font, line.asComponent, line.x, line.y, 0, false);
         }

         this.renderHighlight(guiGraphics, displayCache.selection);
         this.renderCursor(guiGraphics, displayCache.cursor, displayCache.cursorAtEnd);
      }

      super.render(guiGraphics, mouseX, mouseY, delta);
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.renderMenuBackground(graphics);
   }

   private void renderCursor(GuiGraphics guiGraphics, NoteGui.Pos2i pos2i, boolean bl) {
      if (this.frameTick / 6 % 2 == 0) {
         pos2i = this.convertLocalToScreen(pos2i);
         if (!bl) {
            int var10001 = pos2i.x;
            int var10002 = pos2i.y - 1;
            int var10003 = pos2i.x + 1;
            int var10004 = pos2i.y;
            Objects.requireNonNull(this.font);
            guiGraphics.fill(var10001, var10002, var10003, var10004 + 9, -16777216);
         } else {
            guiGraphics.drawString(this.font, "_", pos2i.x, pos2i.y, 0, false);
         }
      }
   }

   private void renderHighlight(GuiGraphics guiGraphics, Rect2i[] rect2is) {
      int var4 = rect2is.length;

      for (Rect2i rect2i : rect2is) {
         int i = rect2i.getX();
         int j = rect2i.getY();
         int k = i + rect2i.getWidth();
         int l = j + rect2i.getHeight();
         guiGraphics.fill(RenderType.guiTextHighlight(), i, j, k, l, -16776961);
      }
   }

   private NoteGui.Pos2i convertScreenToLocal(NoteGui.Pos2i pos2i) {
      return new NoteGui.Pos2i(pos2i.x - (this.width - 192) / 2 - 36, pos2i.y - 32);
   }

   private NoteGui.Position absolutePositionToScreenPosition(NoteGui.Position position) {
      return new NoteGui.Position(position.x + (this.width - 192) / 2 + 36, position.y + 32);
   }

   private NoteGui.Pos2i convertLocalToScreen(NoteGui.Pos2i pos2i) {
      return new NoteGui.Pos2i(pos2i.x + (this.width - 192) / 2 + 36, pos2i.y + 32);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (!super.mouseClicked(mouseX, mouseY, button) && button == 0) {
         long l = Util.getMillis();
         NoteGui.DisplayCache pageContent = this.getDisplayCache();
         int i = pageContent.getIndexAtPosition(this.font, this.convertScreenToLocal(new NoteGui.Pos2i((int)mouseX, (int)mouseY)));
         if (i >= 0) {
            if (i != this.lastClickIndex || l - this.lastClickTime >= 250L) {
               this.currentPageSelectionManager.setCursorPos(i, Screen.hasShiftDown());
            } else if (!this.currentPageSelectionManager.isSelecting()) {
               this.selectCurrentWord(i);
            } else {
               this.currentPageSelectionManager.selectAll();
            }

            this.invalidateDisplayCache();
         }

         this.lastClickIndex = i;
         this.lastClickTime = l;
      }

      return true;
   }

   private void selectCurrentWord(int cursor) {
      String string = this.getCurrentDisplayCache();
      this.currentPageSelectionManager
         .setSelectionRange(StringSplitter.getWordPosition(string, -1, cursor, false), StringSplitter.getWordPosition(string, 1, cursor, false));
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (!super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) && button == 0) {
         NoteGui.DisplayCache pageContent = this.getDisplayCache();
         int i = pageContent.getIndexAtPosition(this.font, this.convertScreenToLocal(new NoteGui.Pos2i((int)mouseX, (int)mouseY)));
         this.currentPageSelectionManager.setCursorPos(i, true);
         this.invalidateDisplayCache();
      }

      return true;
   }

   private NoteGui.DisplayCache getDisplayCache() {
      if (this.pageContent == null) {
         this.pageContent = this.createDisplayCache();
      }

      return this.pageContent;
   }

   private void setDisplayCache(String newContent) {
      if (!this.text.isEmpty()) {
         this.text.set(0, newContent);
         this.dirty = true;
         this.invalidateDisplayCache();
      }
   }

   private void invalidateDisplayCache() {
      this.pageContent = null;
   }

   private NoteGui.DisplayCache createDisplayCache() {
      String string = this.getCurrentDisplayCache();
      if (string.isEmpty()) {
         return NoteGui.DisplayCache.EMPTY;
      }

      int i = this.currentPageSelectionManager.getCursorPos();
      int j = this.currentPageSelectionManager.getSelectionPos();
      List<Integer> intList = new ArrayList<>();
      List<NoteGui.LineInfo> list = new ArrayList<>();
      MutableInt mutableInt = new MutableInt();
      MutableBoolean mutableBoolean = new MutableBoolean();
      StringSplitter textHandler = this.font.getSplitter();
      textHandler.splitLines(string, 114, Style.EMPTY, true, (style, start, end) -> {
         int h = mutableInt.getAndIncrement();
         String substring = string.substring(start, end);
         mutableBoolean.setValue(substring.endsWith("\n"));
         String string2x = StringUtils.stripEnd(substring, " \n");
         Objects.requireNonNull(this.font);
         int b = h * 9;
         NoteGui.Position positionx = this.absolutePositionToScreenPosition(new NoteGui.Position(0, b));
         intList.add(start);
         list.add(new NoteGui.LineInfo(style, string2x, positionx.x, positionx.y));
      });
      int[] is = intList.stream().mapToInt(Integer::intValue).toArray();
      boolean bl = i == string.length();
      NoteGui.Pos2i position;
      if (bl && mutableBoolean.isTrue()) {
         int var10003 = list.size();
         Objects.requireNonNull(this.font);
         position = new NoteGui.Pos2i(0, var10003 * 9);
      } else {
         int k = getLineFromOffset(is, i);
         int l = this.font.width(string.substring(is[k], i));
         Objects.requireNonNull(this.font);
         position = new NoteGui.Pos2i(l, k * 9);
      }

      List<Rect2i> list2 = new ArrayList<>();
      if (i != j) {
         int l = Math.min(i, j);
         int m = Math.max(i, j);
         int n = getLineFromOffset(is, l);
         int o = getLineFromOffset(is, m);
         if (n == o) {
            Objects.requireNonNull(this.font);
            int p = n * 9;
            int q = is[n];
            list2.add(this.getLineSelectionRectangle(string, textHandler, l, m, p, q));
         } else {
            int p = n + 1 > is.length ? string.length() : is[n + 1];
            Objects.requireNonNull(this.font);
            list2.add(this.getLineSelectionRectangle(string, textHandler, l, p, n * 9, is[n]));

            for (int q = n + 1; q < o; q++) {
               Objects.requireNonNull(this.font);
               int r = q * 9;
               String string2 = string.substring(is[q], is[q + 1]);
               int s = (int)textHandler.stringWidth(string2);
               NoteGui.Position var10002 = new NoteGui.Position(0, r);
               Objects.requireNonNull(this.font);
               list2.add(this.getRectFromCorners(var10002, new NoteGui.Position(s, r + 9)));
            }

            int var10004 = is[o];
            Objects.requireNonNull(this.font);
            list2.add(this.getLineSelectionRectangle(string, textHandler, var10004, m, o * 9, is[o]));
         }
      }

      return new NoteGui.DisplayCache(string, position, bl, is, list.toArray(new NoteGui.LineInfo[0]), list2.toArray(new Rect2i[0]));
   }

   private Rect2i getLineSelectionRectangle(String string, StringSplitter handler, int selectionStart, int selectionEnd, int lineY, int lineStart) {
      String string2 = string.substring(lineStart, selectionStart);
      String string3 = string.substring(lineStart, selectionEnd);
      NoteGui.Position position = new NoteGui.Position((int)handler.stringWidth(string2), lineY);
      int var10002 = (int)handler.stringWidth(string3);
      Objects.requireNonNull(this.font);
      NoteGui.Position position2 = new NoteGui.Position(var10002, lineY + 9);
      return this.getRectFromCorners(position, position2);
   }

   private Rect2i getRectFromCorners(NoteGui.Position start, NoteGui.Position end) {
      NoteGui.Position position = this.absolutePositionToScreenPosition(start);
      NoteGui.Position position2 = this.absolutePositionToScreenPosition(end);
      int i = Math.min(position.x, position2.x);
      int j = Math.max(position.x, position2.x);
      int k = Math.min(position.y, position2.y);
      int l = Math.max(position.y, position2.y);
      return new Rect2i(i, k, j - i, l - k);
   }

   @OnlyIn(Dist.CLIENT)
   static class DisplayCache {
      static final NoteGui.DisplayCache EMPTY = new NoteGui.DisplayCache(
         "", new NoteGui.Pos2i(0, 0), true, new int[]{0}, new NoteGui.LineInfo[]{new NoteGui.LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]
      );
      final NoteGui.Pos2i cursor;
      final boolean cursorAtEnd;
      final NoteGui.LineInfo[] lines;
      final Rect2i[] selection;
      private final String fullText;
      private final int[] lineStarts;

      public DisplayCache(String string, NoteGui.Pos2i pos2i, boolean bl, int[] is, NoteGui.LineInfo[] lineInfos, Rect2i[] rect2is) {
         this.fullText = string;
         this.cursor = pos2i;
         this.cursorAtEnd = bl;
         this.lineStarts = is;
         this.lines = lineInfos;
         this.selection = rect2is;
      }

      public int getIndexAtPosition(Font font, NoteGui.Pos2i pos2i) {
         int var10000 = pos2i.y;
         Objects.requireNonNull(font);
         int i = var10000 / 9;
         if (i < 0) {
            return 0;
         }

         if (i >= this.lines.length) {
            return this.fullText.length();
         }

         NoteGui.LineInfo lineInfo = this.lines[i];
         return this.lineStarts[i] + font.getSplitter().plainIndexAtWidth(lineInfo.contents, pos2i.x, lineInfo.style);
      }

      public int changeLine(int i, int j) {
         int k = NoteGui.findLineFromPos(this.lineStarts, i);
         int l = k + j;
         int o;
         if (l >= 0 && l < this.lineStarts.length) {
            int m = i - this.lineStarts[k];
            int n = this.lines[l].contents.length();
            o = this.lineStarts[l] + Math.min(m, n);
         } else {
            o = i;
         }

         return o;
      }

      public int findLineStart(int i) {
         int j = NoteGui.findLineFromPos(this.lineStarts, i);
         return this.lineStarts[j];
      }

      public int findLineEnd(int i) {
         int j = NoteGui.findLineFromPos(this.lineStarts, i);
         return this.lineStarts[j] + this.lines[j].contents.length();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static class LineInfo {
      final Style style;
      final String contents;
      final Component asComponent;
      final int x;
      final int y;

      public LineInfo(Style style, String string, int i, int j) {
         this.style = style;
         this.contents = string;
         this.x = i;
         this.y = j;
         this.asComponent = Component.literal(string).setStyle(style);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Pos2i {
      public final int x;
      public final int y;

      Pos2i(int i, int j) {
         this.x = i;
         this.y = j;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private record Position(int x, int y) {
   }
}
