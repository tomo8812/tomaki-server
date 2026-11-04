package dev.emi.emi.screen.widget.config;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import dev.emi.emi.EmiPort;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class ListWidget extends AbstractContainerEventHandler implements Renderable, NarratableEntry {
   private static final ResourceLocation MENU_LIST_BACKGROUND_TEXTURE = EmiPort.id("minecraft", "textures/gui/menu_list_background.png");
   private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND_TEXTURE = EmiPort.id("minecraft", "textures/gui/inworld_menu_list_background.png");
   protected final Minecraft client;
   private final List<ListWidget.Entry> children = Lists.newArrayList();
   protected int width;
   protected int height;
   protected int top;
   protected int bottom;
   protected int right;
   protected int left;
   private double scrollAmount;
   private boolean renderSelection = true;
   private boolean scrolling;
   private ListWidget.Entry selected;
   private ListWidget.Entry hoveredEntry;
   public int padding = 4;

   public ListWidget(Minecraft client, int width, int height, int top, int bottom) {
      this.client = client;
      this.width = width;
      this.height = height;
      this.top = top;
      this.bottom = bottom;
      this.left = 0;
      this.right = width;
   }

   public void setRenderSelection(boolean renderSelection) {
      this.renderSelection = renderSelection;
   }

   public int getRowWidth() {
      return Math.min(400, this.width - 60);
   }

   public int getLogicalHeight() {
      return this.bottom - this.top;
   }

   @Nullable
   public ListWidget.Entry getSelectedOrNull() {
      return this.selected;
   }

   public void setSelected(@Nullable ListWidget.Entry entry) {
      this.selected = entry;
   }

   public final List<ListWidget.Entry> children() {
      return this.children;
   }

   protected final void clearEntries() {
      this.children.clear();
   }

   protected void replaceEntries(Collection<ListWidget.Entry> newEntries) {
      this.children.clear();
      this.children.addAll(newEntries);
   }

   protected ListWidget.Entry getEntry(int index) {
      return this.children().get(index);
   }

   public int addEntry(ListWidget.Entry entry) {
      this.children.add(entry);
      entry.parentList = this;
      return this.children.size() - 1;
   }

   protected int getEntryCount() {
      return this.children().size();
   }

   protected boolean isSelectedEntry(int index) {
      return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
   }

   @Nullable
   protected final ListWidget.Entry getEntryAtPosition(double x, double y) {
      int rowWidth = this.getRowWidth() / 2;
      int mid = this.left + this.width / 2;
      int rowLeft = mid - rowWidth;
      int rowRight = mid + rowWidth;
      int m = Mth.floor(y - this.top) + (int)this.getScrollAmount() - 4;
      if (x < this.getScrollbarPositionX() && x >= rowLeft && x <= rowRight && m >= 0) {
         int h = 0;

         for (int i = 0; i < this.getEntryCount(); i++) {
            int eh = this.getEntryHeight(i);
            if (m >= h && m < h + eh - this.padding) {
               return this.getEntry(i);
            }

            h += eh;
         }
      }

      return null;
   }

   public void updateSize(int width, int height, int top, int bottom) {
      this.width = width;
      this.height = height;
      this.top = top;
      this.bottom = bottom;
      this.left = 0;
      this.right = width;
   }

   public void setLeftPos(int left) {
      this.left = left;
      this.right = left + this.width;
   }

   protected int getMaxPosition() {
      return this.getTotalHeight();
   }

   public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
      int i = this.getScrollbarPositionX();
      int j = i + 6;
      Tesselator tessellator = Tesselator.getInstance();
      BufferBuilder bufferBuilder = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
      RenderSystem.enableBlend();
      ResourceLocation identifier = this.client.level == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
      draw.blit(identifier, this.left, this.top, this.right, this.bottom + (int)this.scrollAmount, this.right - this.left, this.bottom - this.top, 32, 32);
      RenderSystem.disableBlend();
      draw.enableScissor(this.left, this.top, this.right, this.bottom);
      int k = this.getRowLeft();
      int l = this.top + 4 - (int)this.getScrollAmount();
      this.renderList(draw, k, l, mouseX, mouseY, delta);
      draw.disableScissor();
      RenderSystem.enableBlend();
      ResourceLocation identifierx = this.client.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
      ResourceLocation identifier2 = this.client.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
      draw.blit(identifierx, this.left, this.top - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      draw.blit(identifier2, this.left, this.bottom, 0.0F, 0.0F, this.width, 2, 32, 2);
      RenderSystem.disableBlend();
      int o;
      if ((o = this.getMaxScroll()) > 0) {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         int m = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / this.getMaxPosition());
         m = Mth.clamp(m, 32, this.bottom - this.top - 8);
         int n = (int)this.getScrollAmount() * (this.bottom - this.top - m) / o + this.top;
         if (n < this.top) {
            n = this.top;
         }

         bufferBuilder = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         bufferBuilder.addVertex(i, this.bottom, 0.0F).setColor(0, 0, 0, 255);
         bufferBuilder.addVertex(j, this.bottom, 0.0F).setColor(0, 0, 0, 255);
         bufferBuilder.addVertex(j, this.top, 0.0F).setColor(0, 0, 0, 255);
         bufferBuilder.addVertex(i, this.top, 0.0F).setColor(0, 0, 0, 255);
         bufferBuilder.addVertex(i, n + m, 0.0F).setColor(128, 128, 128, 255);
         bufferBuilder.addVertex(j, n + m, 0.0F).setColor(128, 128, 128, 255);
         bufferBuilder.addVertex(j, n, 0.0F).setColor(128, 128, 128, 255);
         bufferBuilder.addVertex(i, n, 0.0F).setColor(128, 128, 128, 255);
         bufferBuilder.addVertex(i, n + m - 1, 0.0F).setColor(192, 192, 192, 255);
         bufferBuilder.addVertex(j - 1, n + m - 1, 0.0F).setColor(192, 192, 192, 255);
         bufferBuilder.addVertex(j - 1, n, 0.0F).setColor(192, 192, 192, 255);
         bufferBuilder.addVertex(i, n, 0.0F).setColor(192, 192, 192, 255);
         BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
      }

      RenderSystem.disableBlend();
   }

   public void centerScrollOn(ListWidget.Entry entry) {
      int i = 0;

      for (ListWidget.Entry e : this.children()) {
         if (e == entry) {
            this.setScrollAmount(i - 42);
            return;
         }

         i += this.getEntryHeight(e);
      }
   }

   protected void ensureVisible(ListWidget.Entry entry) {
      int i = this.getRowTop(this.children().indexOf(entry));
      int j = i - this.top - 4 - entry.getHeight();
      int k = this.bottom - i - entry.getHeight() * 2;
      if (j < 0) {
         this.scroll(j);
      }

      if (k < 0) {
         this.scroll(-k);
      }
   }

   private void scroll(int amount) {
      this.setScrollAmount(this.getScrollAmount() + amount);
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double amount) {
      this.scrollAmount = Mth.clamp(amount, 0.0, this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4) + 40);
   }

   protected void updateScrollingState(double mouseX, double mouseY, int button) {
      this.scrolling = button == 0 && mouseX >= this.getScrollbarPositionX() && mouseX < this.getScrollbarPositionX() + 6;
   }

   protected int getScrollbarPositionX() {
      return this.width - 6;
   }

   public void unfocusTextField() {
      for (ListWidget.Entry e : this.children) {
         for (GuiEventListener el : e.children()) {
            if (el instanceof EditBox tfw) {
               EmiPort.focus(tfw, false);
            }
         }
      }
   }

   public EditBox getFocusedTextField() {
      for (ListWidget.Entry e : this.children) {
         for (GuiEventListener el : e.children()) {
            if (el instanceof EditBox tfw && tfw.isFocused()) {
               return tfw;
            }
         }
      }

      return null;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.updateScrollingState(mouseX, mouseY, button);
      this.unfocusTextField();
      if (!this.isMouseOver(mouseX, mouseY)) {
         return false;
      } else {
         ListWidget.Entry entry = this.getEntryAtPosition(mouseX, mouseY);
         if (entry != null && entry.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(entry);
            this.setDragging(true);
            return true;
         } else {
            return this.scrolling;
         }
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(mouseX, mouseY, button);
      }

      return false;
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
         return true;
      }

      if (button == 0 && this.scrolling) {
         if (mouseY < this.top) {
            this.setScrollAmount(0.0);
         } else if (mouseY > this.bottom) {
            this.setScrollAmount(this.getMaxScroll());
         } else {
            double d = Math.max(1, this.getMaxScroll());
            int i = this.bottom - this.top;
            int j = Mth.clamp((int)((float)(i * i) / this.getMaxPosition()), 32, i - 8);
            double e = Math.max(1.0, d / (i - j));
            this.setScrollAmount(this.getScrollAmount() + deltaY * e);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double amount) {
      this.setScrollAmount(this.getScrollAmount() - amount * 22.0);
      return true;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   protected void moveSelection(ListWidget.MoveDirection direction) {
      this.moveSelectionIf(direction, entry -> true);
   }

   protected void ensureSelectedEntryVisible() {
      ListWidget.Entry entry = this.getSelectedOrNull();
      if (entry != null) {
         this.setSelected(entry);
         this.ensureVisible(entry);
      }
   }

   protected void moveSelectionIf(ListWidget.MoveDirection direction, Predicate<ListWidget.Entry> predicate) {
      int i = direction == ListWidget.MoveDirection.UP ? -1 : 1;
      if (!this.children().isEmpty()) {
         int j = this.children().indexOf(this.getSelectedOrNull());

         int k;
         while (j != (k = Mth.clamp(j + i, 0, this.getEntryCount() - 1))) {
            ListWidget.Entry entry = this.children().get(k);
            if (predicate.test(entry)) {
               this.setSelected(entry);
               this.ensureVisible(entry);
               break;
            }

            j = k;
         }
      }
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return mouseY >= this.top && mouseY <= this.bottom && mouseX >= this.left && mouseX <= this.right;
   }

   protected void renderList(GuiGraphics draw, int x, int y, int mouseX, int mouseY, float delta) {
      int i = this.getEntryCount();
      Tesselator tessellator = Tesselator.getInstance();
      BufferBuilder bufferBuilder = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);

      for (int j = 0; j < i; j++) {
         int k = this.getRowTop(j);
         int l = this.getRowBottom(j);
         if (l >= this.top && k <= this.bottom) {
            int m = k;
            int n = this.getEntryHeight(j);
            if (n != 0) {
               n -= 4;
               ListWidget.Entry entry = this.getEntry(j);
               int o = this.getRowWidth();
               if (this.renderSelection && this.isSelectedEntry(j)) {
                  int p = this.left + this.width / 2 - o / 2;
                  int q = this.left + this.width / 2 + o / 2;
                  RenderSystem.setShader(GameRenderer::getPositionShader);
                  float f = this.isFocused() ? 1.0F : 0.5F;
                  RenderSystem.setShaderColor(f, f, f, 1.0F);
                  bufferBuilder.addVertex(p, m + n + 2, 0.0F);
                  bufferBuilder.addVertex(q, m + n + 2, 0.0F);
                  bufferBuilder.addVertex(q, m - 2, 0.0F);
                  bufferBuilder.addVertex(p, m - 2, 0.0F);
                  BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
                  RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                  bufferBuilder = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
                  bufferBuilder.addVertex(p + 1, m + n + 1, 0.0F);
                  bufferBuilder.addVertex(q - 1, m + n + 1, 0.0F);
                  bufferBuilder.addVertex(q - 1, m - 1, 0.0F);
                  bufferBuilder.addVertex(p + 1, m - 1, 0.0F);
                  BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
               }

               int p = this.getRowLeft();
               entry.render(draw, j, k, p, o - 3, n, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
            }
         }
      }
   }

   public int getRowLeft() {
      return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   private int getEntryHeight(int i) {
      return this.getEntryHeight(this.getEntry(i));
   }

   private int getEntryHeight(ListWidget.Entry entry) {
      int h = entry.getHeight();
      return h == 0 ? 0 : h + this.padding;
   }

   protected int getRowTop(int index) {
      int height = 0;

      for (int i = 0; i < index; i++) {
         height += this.getEntryHeight(i);
      }

      return this.top + 4 - (int)this.getScrollAmount() + height;
   }

   private int getRowBottom(int index) {
      return this.getRowTop(index) + this.getEntry(index).getHeight();
   }

   public boolean isFocused() {
      return false;
   }

   public NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarrationPriority.FOCUSED;
      } else {
         return this.hoveredEntry != null ? NarrationPriority.HOVERED : NarrationPriority.NONE;
      }
   }

   @Nullable
   public ListWidget.Entry getHoveredEntry() {
      return this.hoveredEntry;
   }

   void setEntryParentList(ListWidget.Entry entry) {
      entry.parentList = this;
   }

   protected void appendNarrations(NarrationElementOutput builder, ListWidget.Entry entry) {
      List<ListWidget.Entry> list = this.children();
      int i;
      if (list.size() > 1 && (i = list.indexOf(entry)) != -1) {
         builder.add(NarratedElementType.POSITION, EmiPort.translatable("narrator.position.list", i + 1, list.size()));
      }
   }

   public int getTotalHeight() {
      int height = 0;

      for (int i = 0; i < this.getEntryCount(); i++) {
         height += this.getEntryHeight(i);
      }

      if (height > 0) {
         height -= this.padding;
      }

      return height;
   }

   public void updateNarration(NarrationElementOutput var1) {
   }

   public abstract static class Entry extends AbstractContainerEventHandler {
      public ListWidget parentList;

      public abstract void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

      public boolean isMouseOver(double mouseX, double mouseY) {
         return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
      }

      public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
         return List.of();
      }

      public abstract int getHeight();
   }

   protected enum MoveDirection {
      UP,
      DOWN;

      // $VF: synthetic method
      private static ListWidget.MoveDirection[] $values() {
         return new ListWidget.MoveDirection[]{UP, DOWN};
      }
   }
}
