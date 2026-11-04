package dev.emi.emi.screen;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.RecipeFillButtonWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.SidebarSide;
import dev.emi.emi.input.EmiInput;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.runtime.EmiHistory;
import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.screen.widget.ResolutionButtonWidget;
import dev.emi.emi.screen.widget.SizedButtonWidget;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

public class RecipeScreen extends Screen {
   private static final ResourceLocation TEXTURE = EmiPort.id("emi", "textures/gui/background.png");
   @Nullable
   public static EmiIngredient resolve = null;
   private Map<EmiRecipeCategory, List<EmiRecipe>> recipes;
   public AbstractContainerScreen<?> old;
   private List<RecipeTab> tabs = Lists.newArrayList();
   private int tabPageSize = 6;
   private int tabPage = 0;
   private int tab = 0;
   private int page = 0;
   private List<SizedButtonWidget> arrows;
   private List<WidgetGroup> currentPage = Lists.newArrayList();
   private int buttonOff = 0;
   private int tabOff = 0;
   private Widget hoveredWidget = null;
   private Widget pressedSlot = null;
   private ResolutionButtonWidget resolutionButton;
   private double scrollAcc = 0.0;
   private int minimumWidth = 176;
   int backgroundWidth = this.minimumWidth;
   int backgroundHeight = 200;
   int x = (this.width - this.backgroundWidth) / 2;
   int y = (this.height - this.backgroundHeight) / 2;

   public RecipeScreen(AbstractContainerScreen<?> old, Map<EmiRecipeCategory, List<EmiRecipe>> recipes) {
      super(EmiPort.translatable("screen.emi.recipe"));
      this.old = old;
      this.arrows = List.of(
         new SizedButtonWidget(
            this.x + 2, this.y - 18, 12, 12, 0, 0, () -> this.tabs.size() > this.tabPageSize, w -> this.setPage(this.tabPage - 1, this.tab, this.page)
         ),
         new SizedButtonWidget(
            this.x + this.backgroundWidth - 14,
            this.y - 18,
            12,
            12,
            12,
            0,
            () -> this.tabs.size() > this.tabPageSize,
            w -> this.setPage(this.tabPage + 1, this.tab, this.page)
         ),
         new SizedButtonWidget(this.x + 5, this.y + 5, 12, 12, 0, 0, () -> this.tabs.size() > 1, w -> this.setPage(this.tabPage, this.tab - 1, 0)),
         new SizedButtonWidget(
            this.x + this.backgroundWidth - 17, this.y + 5, 12, 12, 12, 0, () -> this.tabs.size() > 1, w -> this.setPage(this.tabPage, this.tab + 1, 0)
         ),
         new SizedButtonWidget(
            this.x + 5, this.y + 18, 12, 12, 0, 0, () -> this.tabs.get(this.tab).getPageCount() > 1, w -> this.setPage(this.tabPage, this.tab, this.page - 1)
         ),
         new SizedButtonWidget(
            this.x + this.backgroundWidth - 17,
            this.y + 18,
            12,
            12,
            12,
            0,
            () -> this.tabs.get(this.tab).getPageCount() > 1,
            w -> this.setPage(this.tabPage, this.tab, this.page + 1)
         )
      );
      resolve = null;
      this.recipes = recipes;
   }

   protected void init() {
      super.init();
      this.minimumWidth = Math.max(EmiConfig.minimumRecipeScreenWidth, 56);
      this.backgroundWidth = this.minimumWidth;
      this.backgroundHeight = Math.min(EmiConfig.maximumRecipeScreenHeight, this.height - 52 - EmiConfig.verticalMargin);
      this.x = (this.width - this.backgroundWidth) / 2;
      this.y = (this.height - this.backgroundHeight) / 2 + 1;
      this.tabPageSize = (this.minimumWidth - 32) / 24;

      for (SizedButtonWidget widget : this.arrows) {
         this.addRenderableWidget(widget);
      }

      EmiScreenManager.addWidgets(this);
      if (resolve != null) {
         this.resolutionButton = new ResolutionButtonWidget(this.x - 18, this.y + 10, 18, 18, resolve, () -> this.hoveredWidget);
         this.addRenderableWidget(this.resolutionButton);
      }

      if (this.recipes != null) {
         EmiRecipe current = null;
         if (this.tab < this.tabs.size() && this.page < this.tabs.get(this.tab).getPageCount() && this.tabs.get(this.tab).getPage(this.page).size() > 0) {
            current = this.tabs.get(this.tab).getPage(this.page).get(0).recipe;
         }

         this.tabs.clear();
         if (!this.recipes.isEmpty()) {
            for (Entry<EmiRecipeCategory, List<EmiRecipe>> entry : this.recipes.entrySet().stream().sorted((a, b) -> {
               int ai = EmiApi.getRecipeManager().getCategories().indexOf(a.getKey());
               int bi = EmiApi.getRecipeManager().getCategories().indexOf(b.getKey());
               if (ai < 0) {
                  ai = Integer.MAX_VALUE;
               }

               if (bi < 0) {
                  bi = Integer.MAX_VALUE;
               }

               return ai - bi;
            }).toList()) {
               List<EmiRecipe> set = entry.getValue();
               if (!set.isEmpty()) {
                  RecipeTab tab = new RecipeTab(entry.getKey(), set);
                  tab.bakePages(this.backgroundHeight);
                  this.tabs.add(tab);
               }
            }

            this.tab = -1;
            this.setPage(this.tabPage, 0, 0);
         }

         if (current != null) {
            this.focusRecipe(current);
         }
      }

      this.setRecipePageWidth(this.backgroundWidth);
   }

   private void setRecipePageWidth(int width) {
      if ((width & 1) == 1) {
         width++;
      }

      this.backgroundWidth = width;
      this.x = (this.width - this.backgroundWidth) / 2;
      this.buttonOff = (this.backgroundWidth - this.minimumWidth) / 2;
      int tabExtra = (this.minimumWidth - 32) % 24 / 2;
      this.tabOff = this.buttonOff + tabExtra;
      this.arrows.get(0).x = this.x + 2 + this.buttonOff + tabExtra;
      this.arrows.get(1).x = this.x + this.minimumWidth - 14 + this.buttonOff - tabExtra;
      this.arrows.get(2).x = this.x + 5 + this.buttonOff;
      this.arrows.get(3).x = this.x + this.minimumWidth - 17 + this.buttonOff;
      this.arrows.get(4).x = this.x + 5 + this.buttonOff;
      this.arrows.get(5).x = this.x + this.minimumWidth - 17 + this.buttonOff;
      this.arrows.get(0).y = this.y - 18;
      this.arrows.get(1).y = this.y - 18;
      this.arrows.get(2).y = this.y + 5;
      this.arrows.get(3).y = this.y + 5;
      this.arrows.get(4).y = this.y + 19;
      this.arrows.get(5).y = this.y + 19;
   }

   public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      this.renderTransparentBackground(context.raw());
      context.resetColor();
      EmiRenderHelper.drawNinePatch(context, TEXTURE, this.x, this.y, this.backgroundWidth, this.backgroundHeight, 0, 0, 4, 1);
      int tp = this.tabPage * this.tabPageSize;
      int off = 0;

      for (int i = tp; i < this.tabs.size() && i < tp + this.tabPageSize; i++) {
         RecipeTab tab = this.tabs.get(i);
         int sOff = i == this.tab ? 2 : 0;
         EmiRenderHelper.drawNinePatch(
            context, TEXTURE, this.x + this.tabOff + off * 24 + 16, this.y - 24 - sOff, 24, 27 + sOff, i == this.tab ? 9 : 18, 0, 4, 1
         );
         tab.category.render(context.raw(), this.x + this.tabOff + off++ * 24 + 20, this.y - 20 - (i == this.tab ? 2 : 0), delta);
      }

      EmiRenderHelper.drawNinePatch(context, TEXTURE, this.x + 19 + this.buttonOff, this.y + 5, this.minimumWidth - 38, 12, 0, 16, 3, 6);
      EmiRenderHelper.drawNinePatch(context, TEXTURE, this.x + 19 + this.buttonOff, this.y + 19, this.minimumWidth - 38, 12, 0, 16, 3, 6);
      boolean categoryHovered = mouseX >= this.x + 19 + this.buttonOff
         && mouseY >= this.y + 5
         && mouseX < this.x + this.minimumWidth + this.buttonOff - 19
         && mouseY < this.y + 5 + 12;
      int categoryNameColor = categoryHovered ? 2293759 : 16777215;
      RecipeTab tab = this.tabs.get(this.tab);
      Component text = tab.category.getName();
      if (this.minecraft.font.width(text) > this.minimumWidth - 40) {
         int extraWidth = this.minecraft.font.width("...");
         text = EmiPort.literal(this.minecraft.font.substrByWidth(text, this.minimumWidth - 40 - extraWidth).getString() + "...");
      }

      context.drawCenteredTextWithShadow(text, this.x + this.backgroundWidth / 2, this.y + 7, categoryNameColor);
      context.drawCenteredTextWithShadow(
         EmiRenderHelper.getPageText(this.page + 1, tab.getPageCount(), this.minimumWidth - 40), this.x + this.backgroundWidth / 2, this.y + 21, 16777215
      );
      List<EmiIngredient> workstations = EmiApi.getRecipeManager().getWorkstations(tab.category);
      int workstationAmount = Math.min(workstations.size(), this.getMaxWorkstations());
      if (workstationAmount > 0 || resolve != null) {
         Bounds bounds = this.getWorkstationBounds(-1);
         int offset = this.getResolveOffset();
         if (workstationAmount <= 0) {
            offset = 18;
         }

         if (EmiConfig.workstationLocation == SidebarSide.LEFT) {
            EmiRenderHelper.drawNinePatch(context, TEXTURE, bounds.x() - 5, bounds.y() - 5, 28, 10 + 18 * workstationAmount + offset, 36, 0, 5, 1);
         } else if (EmiConfig.workstationLocation == SidebarSide.RIGHT) {
            EmiRenderHelper.drawNinePatch(context, TEXTURE, bounds.x() - 5, bounds.y() - 5, 28, 10 + 18 * workstationAmount + offset, 47, 0, 5, 1);
         } else if (EmiConfig.workstationLocation == SidebarSide.BOTTOM) {
            EmiRenderHelper.drawNinePatch(context, TEXTURE, bounds.x() - 5, bounds.y() - 5, 10 + 18 * workstationAmount + offset, 28, 58, 0, 5, 1);
         }
      }

      for (WidgetGroup group : this.currentPage) {
         int mx = mouseX - group.x();
         int my = mouseY - group.y();
         context.push();
         context.matrices().translate(group.x(), group.y(), 0.0F);
         EmiPort.applyModelViewMatrix();

         try {
            for (Widget widget : group.widgets) {
               widget.render(context.raw(), mx, my, delta);
            }
         } catch (Throwable e) {
            EmiLog.error("Error rendering widget", e);
            group.error(e);
         }

         Iterator var35 = group.widgets.iterator();

         while (true) {
            if (var35.hasNext()) {
               Widget widget = (Widget)var35.next();
               if (!(widget instanceof RecipeFillButtonWidget) || !widget.getBounds().contains(mx, my)) {
                  continue;
               }

               AbstractContainerScreen hs = EmiApi.getHandledScreen();
               EmiRecipeHandler handler = EmiRecipeFiller.getFirstValidHandler(group.recipe, hs);
               if (handler != null) {
                  handler.render(
                     group.recipe, new EmiCraftContext(hs, handler.getInventory(hs), EmiCraftContext.Type.FILL_BUTTON), group.widgets, context.raw()
                  );
               } else if (EmiScreenManager.lastPlayerInventory != null) {
                  StandardRecipeHandler.renderMissing(group.recipe, EmiScreenManager.lastPlayerInventory, group.widgets, context.raw());
               }
            }

            context.pop();
            EmiPort.applyModelViewMatrix();
            break;
         }
      }

      EmiScreenManager.drawBackground(context, mouseX, mouseY, delta);
      EmiScreenManager.render(context, mouseX, mouseY, delta);
      EmiScreenManager.drawForeground(context, mouseX, mouseY, delta);
      super.render(context.raw(), mouseX, mouseY, delta);
      if (categoryHovered) {
         context.raw()
            .renderComponentTooltip(this.minecraft.font, List.of(tab.category.getName(), EmiPort.translatable("emi.view_all_recipes")), mouseX, mouseY);
      }

      this.hoveredWidget = null;

      label108:
      for (WidgetGroup group : this.currentPage) {
         try {
            int mx = mouseX - group.x();
            int my = mouseY - group.y();

            for (Widget widget : group.widgets) {
               if (widget.getBounds().contains(mx, my)) {
                  List<ClientTooltipComponent> tooltip = widget.getTooltip(mx, my);
                  if (!tooltip.isEmpty()) {
                     EmiRenderHelper.drawTooltip(this, context, tooltip, mouseX, mouseY);
                     this.hoveredWidget = widget;
                     break label108;
                  }
               }
            }
         } catch (Throwable e) {
            EmiLog.error("Error rendering widget group", e);
            group.error(e);
         }
      }

      RecipeTab rTab = this.getTabAt(mouseX, mouseY);
      if (rTab != null) {
         EmiRenderHelper.drawTooltip(this, context, rTab.category.getTooltip(), mouseX, mouseY);
      }
   }

   public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
   }

   public EmiIngredient getHoveredStack() {
      return this.hoveredWidget instanceof SlotWidget slot ? slot.getStack() : EmiStack.EMPTY;
   }

   public RecipeTab getTabAt(int mx, int my) {
      if (mx >= this.x + 16 + this.tabOff && mx < this.x + this.backgroundWidth && my >= this.y - 24 && my < this.y) {
         int n = (mx - this.x - 16 - this.tabOff) / 24 + this.tabPage * this.tabPageSize;
         if (n < this.tabs.size() && n >= this.tabPage * this.tabPageSize && n < (this.tabPage + 1) * this.tabPageSize) {
            return this.tabs.get(n);
         }
      }

      return null;
   }

   public int getMaxWorkstations() {
      return switch (EmiConfig.workstationLocation) {
         case LEFT, RIGHT -> (this.backgroundHeight - this.getResolveOffset() - 18) / 18;
         case BOTTOM -> (this.backgroundWidth - this.getResolveOffset() - 18) / 18;
         default -> 0;
      };
   }

   public int getResolveOffset() {
      return resolve == null ? 0 : 23;
   }

   public Bounds getWorkstationBounds(int i) {
      int offset = 0;
      if (i == -1) {
         i = 0;
         offset = -this.getResolveOffset();
      }

      if (EmiConfig.workstationLocation == SidebarSide.LEFT) {
         return new Bounds(this.x - 18, this.y + 9 + this.getResolveOffset() + i * 18 + offset, 18, 18);
      } else if (EmiConfig.workstationLocation == SidebarSide.RIGHT) {
         return new Bounds(this.x + this.backgroundWidth, this.y + 9 + this.getResolveOffset() + i * 18 + offset, 18, 18);
      } else {
         return EmiConfig.workstationLocation == SidebarSide.BOTTOM
            ? new Bounds(this.x + 5 + this.getResolveOffset() + i * 18 + offset, this.y + this.backgroundHeight - 23, 18, 18)
            : Bounds.EMPTY;
      }
   }

   public EmiRecipeCategory getFocusedCategory() {
      return this.tabs.get(this.tab).category;
   }

   public void focusCategory(EmiRecipeCategory category) {
      for (int i = 0; i < this.tabs.size(); i++) {
         if (this.tabs.get(i).category == category) {
            this.setPage(this.tabPage, i, 0);
            return;
         }
      }
   }

   public void focusRecipe(EmiRecipe recipe) {
      for (int i = 0; i < this.tabs.size(); i++) {
         RecipeTab tab = this.tabs.get(i);

         for (int j = 0; j < tab.getPageCount(); j++) {
            for (RecipeDisplay d : tab.getPage(j)) {
               if (d.recipe == recipe) {
                  this.setPage(this.tabPage, i, j);
                  return;
               }
            }
         }
      }
   }

   public void setPage(int tp, int t, int p) {
      this.currentPage.clear();
      if (!this.tabs.isEmpty()) {
         boolean snapTabPage = tp == this.tabPage && t != this.tab;
         this.tab = this.wrap(t, this.tabs.size());
         if (snapTabPage) {
            tp = this.tab / this.tabPageSize;
         }

         this.tabPage = this.wrap(tp, (this.tabs.size() - 1) / this.tabPageSize + 1);
         RecipeTab tab = this.tabs.get(this.tab);
         this.page = this.wrap(p, tab.getPageCount());
         if (this.page < tab.getPageCount()) {
            int width = Math.max(this.minimumWidth - 16, tab.getWidth());
            this.setRecipePageWidth(width + 16);
            this.currentPage = Lists.newArrayList();
            this.currentPage.addAll(tab.constructWidgets(this.page, this.x, this.y, this.backgroundWidth, this.backgroundHeight));
            List<EmiIngredient> workstations = EmiApi.getRecipeManager().getWorkstations(tab.category);
            if (!workstations.isEmpty()) {
               WidgetGroup widgets = new WidgetGroup(null, 0, 0, 0, 0);
               int maxWorkstations = this.getMaxWorkstations();

               for (int i = 0; i < workstations.size() && i < maxWorkstations; i++) {
                  Bounds bounds = this.getWorkstationBounds(i);
                  if (i == maxWorkstations - 1 && workstations.size() > maxWorkstations) {
                     EmiIngredient ingredient = EmiIngredient.of(workstations.subList(i, workstations.size()));
                     widgets.add(new SlotWidget(ingredient, bounds.x(), bounds.y()));
                  } else {
                     widgets.add(new SlotWidget(workstations.get(i), bounds.x(), bounds.y()));
                  }
               }

               this.currentPage.add(widgets);
            }
         }

         if (resolve != null && this.resolutionButton != null) {
            Bounds bounds = this.getWorkstationBounds(-1);
            this.resolutionButton.x = bounds.x();
            this.resolutionButton.y = bounds.y();
         }
      }
   }

   public int wrap(int value, int size) {
      if (value >= size) {
         return 0;
      } else {
         return value < 0 ? size - 1 : value;
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      int mx = (int)mouseX;
      int my = (int)mouseY;
      this.pressedSlot = null;
      if (mouseX >= this.x + 19 + this.buttonOff
         && mouseY >= this.y + 5
         && mouseX < this.x + this.minimumWidth + this.buttonOff - 19
         && mouseY <= this.y + 5 + 12) {
         EmiApi.displayAllRecipes();
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         return true;
      }

      for (WidgetGroup group : this.currentPage) {
         try {
            int ox = mx - group.x();
            int oy = my - group.y();
            boolean groupHovered = new Bounds(group.x(), group.y(), group.getWidth(), group.getHeight()).contains(mx, my);

            for (Widget widget : group.widgets) {
               if (widget.getBounds().contains(ox, oy)) {
                  if (widget instanceof SlotWidget slot) {
                     if (this.pressedSlot == null) {
                        this.pressedSlot = widget;
                     }
                  } else if (widget.mouseClicked(ox, oy, button)) {
                     return true;
                  }

                  groupHovered = true;
               }
            }

            if (groupHovered && EmiScreenManager.recipeInteraction(group.recipe, bind -> bind.matchesMouse(button))) {
               return true;
            }
         } catch (Throwable e) {
            EmiLog.error("Error handling widget input", e);
            group.error(e);
         }
      }

      if (EmiScreenManager.mouseClicked(mouseX, mouseY, button)) {
         return true;
      } else {
         RecipeTab rTab = this.getTabAt(mx, my);
         if (rTab != null) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.focusCategory(rTab.category);
            return true;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (EmiScreenManager.mouseReleased(mouseX, mouseY, button)) {
         return true;
      }

      if (this.pressedSlot instanceof SlotWidget slot) {
         WidgetGroup group = this.getGroup(slot);
         if (group != null) {
            try {
               int ox = (int)mouseX - group.x();
               int oy = (int)mouseY - group.y();
               if (slot.getBounds().contains(ox, oy) && slot.mouseClicked(ox, oy, button)) {
                  return true;
               }
            } catch (Throwable e) {
               EmiLog.error("Error handling widget input", e);
               group.error(e);
            }
         }

         this.pressedSlot = null;
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (EmiScreenManager.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
         return true;
      }

      if (this.pressedSlot instanceof SlotWidget slot) {
         WidgetGroup group = this.getGroup(slot);
         if (group != null) {
            int ox = (int)mouseX - group.x();
            int oy = (int)mouseY - group.y();
            if (!slot.getBounds().contains(ox, oy) && button == 0) {
               EmiIngredient stack = slot.getStack();
               if (slot.getRecipe() != null) {
                  stack = new EmiFavorite(stack, slot.getRecipe());
               }

               EmiScreenManager.pressedStack = stack;
               EmiScreenManager.draggedStack = stack;
               this.pressedSlot = null;
            }
         }
      }

      return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double amount) {
      if (EmiScreenManager.mouseScrolled(mouseX, mouseY, amount)) {
         return true;
      }

      if (mouseX > this.x && mouseX < this.x + this.backgroundWidth && mouseY < this.x + this.backgroundHeight) {
         this.scrollAcc += amount;
         int sa = (int)this.scrollAcc;
         this.scrollAcc %= 1.0;
         if (!EmiInput.isShiftDown() && !(mouseY < this.y)) {
            this.setPage(this.tabPage, this.tab, this.page - sa);
         } else {
            this.setPage(this.tabPage, this.tab - sa, 0);
         }
      }

      return super.mouseScrolled(mouseX, mouseY, horizontal, amount);
   }

   public boolean charTyped(char chr, int modifiers) {
      return EmiScreenManager.search.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.onClose();
         return true;
      }

      if (EmiScreenManager.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      }

      if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
         this.onClose();
         return true;
      }

      for (WidgetGroup group : this.currentPage) {
         try {
            int mx = EmiScreenManager.lastMouseX - group.x();
            int my = EmiScreenManager.lastMouseY - group.y();
            boolean groupHovered = new Bounds(group.x(), group.y(), group.getWidth(), group.getHeight())
               .contains(EmiScreenManager.lastMouseX, EmiScreenManager.lastMouseY);

            for (Widget widget : group.widgets) {
               if (widget.getBounds().contains(mx, my)) {
                  if (widget.keyPressed(keyCode, scanCode, modifiers)) {
                     return true;
                  }

                  groupHovered = true;
               }
            }

            if (groupHovered && EmiScreenManager.recipeInteraction(group.recipe, bind -> bind.matchesKey(keyCode, scanCode))) {
               return true;
            }
         } catch (Throwable e) {
            EmiLog.error("Error handling widget input", e);
            group.error(e);
         }
      }

      if (keyCode == 263) {
         this.setPage(this.tabPage, this.tab - 1, 0);
      } else if (keyCode == 262) {
         this.setPage(this.tabPage, this.tab + 1, 0);
      }

      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   public WidgetGroup getGroup(Widget widget) {
      for (WidgetGroup group : this.currentPage) {
         if (group.widgets.contains(widget)) {
            return group;
         }
      }

      return null;
   }

   public void onClose() {
      EmiHistory.popUntil(s -> !(s instanceof RecipeScreen), this.old);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public Bounds getBounds() {
      int top = this.y - 26;
      int bottom = this.y + this.backgroundHeight;
      int left = this.x;
      int right = this.x + this.backgroundWidth;
      if (EmiConfig.workstationLocation == SidebarSide.LEFT) {
         left -= 22;
      } else if (EmiConfig.workstationLocation == SidebarSide.RIGHT) {
         right += 22;
      }

      return new Bounds(left, top, right - left, bottom - top);
   }
}
