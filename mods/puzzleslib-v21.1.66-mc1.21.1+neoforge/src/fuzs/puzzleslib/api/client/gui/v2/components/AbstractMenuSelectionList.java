package fuzs.puzzleslib.api.client.gui.v2.components;

import fuzs.puzzleslib.api.client.gui.v2.ScreenHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractMenuSelectionList<E extends net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry<E>>
   extends UpdatedContainerObjectSelectionList<E> {
   public static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
   public static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");

   public AbstractMenuSelectionList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight) {
      super(minecraft, width, height, y, itemHeight);
      this.setX(x);
   }

   public int addEntry(E entry) {
      return super.addEntry(entry);
   }

   public int getRowWidth() {
      return this.getWidth();
   }

   @Override
   protected void extractScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      ResourceLocation sprite = this.scrollable() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
      guiGraphics.blitSprite(sprite, this.scrollBarX(), this.scrollBarY(), this.scrollerWidth(), this.scrollerHeight());
   }

   protected void renderListSeparators(GuiGraphics guiGraphics) {
   }

   protected void renderListBackground(GuiGraphics guiGraphics) {
   }

   @Override
   protected int contentHeight() {
      return super.contentHeight() - 4;
   }

   @Override
   public int scrollbarWidth() {
      return this.scrollerWidth();
   }

   public int scrollbarHeight() {
      return this.getHeight();
   }

   @Override
   protected int scrollerWidth() {
      return 12;
   }

   @Override
   protected int scrollerHeight() {
      return 15;
   }

   @Override
   public int scrollBarY() {
      if (this.scrollable() && this.maxScrollAmount() != 0) {
         double scrollScale = this.scrollAmount() / this.maxScrollAmount();
         return this.getY() + Math.max(0, (int)(scrollScale * (this.getHeight() - this.scrollerHeight())));
      } else {
         return this.getY();
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.updateScrolling(mouseX, mouseY, button) && this.scrolling) {
         this.setMouseButtonScrollAmount(mouseX, mouseY, button);
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.scrolling) {
         this.setMouseButtonScrollAmount(mouseX, mouseY, button);
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
      }
   }

   protected void setMouseButtonScrollAmount(double mouseX, double mouseY, int button) {
      double scrollOffset = (mouseY - this.getY() - this.scrollerHeight() / 2.0) / (this.scrollbarHeight() - this.scrollerHeight());
      this.setScrollAmount(Mth.clamp(scrollOffset, 0.0, 1.0) * this.maxScrollAmount());
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return super.isMouseOver(mouseX, mouseY) || this.isOverScrollbar(mouseX, mouseY);
   }

   @Override
   protected boolean isOverScrollbar(double mouseX, double mouseY) {
      return ScreenHelper.isHovering(this.scrollBarX(), this.getY(), this.scrollbarWidth(), this.scrollbarHeight(), mouseX, mouseY);
   }

   public int getRowLeft() {
      return this.getX();
   }

   public E getEntryAtPosition(double mouseX, double mouseY) {
      this.headerHeight -= 4;
      E entry = (E)super.getEntryAtPosition(mouseX, mouseY);
      this.headerHeight += 4;
      return entry;
   }

   protected void renderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int index, int left, int top, int width, int height) {
      super.renderItem(guiGraphics, mouseX, mouseY, partialTick, index, left, top, width, height + 4);
   }

   protected int getRowTop(int index) {
      return super.getRowTop(index) - 4;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public net.minecraft.client.gui.components.AbstractSelectionList.Entry getEntryAtPosition(double param1, double nullx) {
      return this.getEntryAtPosition(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public int addEntry(net.minecraft.client.gui.components.AbstractSelectionList.Entry param1) {
      return this.addEntry((E)var1);
   }

   public static class Entry<E extends AbstractMenuSelectionList.Entry<E>> extends net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry<E> {
      private final List<AbstractWidget> children = new ArrayList<>();

      public <T extends AbstractWidget> T addRenderableWidget(T widget) {
         this.children.add(widget);
         return widget;
      }

      public void render(
         GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick
      ) {
         for (AbstractWidget widget : this.children) {
            widget.setY(top);
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
         }
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }
   }
}
