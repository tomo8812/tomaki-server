package dev.emi.emi.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.chess.EmiChess;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.HeaderType;
import dev.emi.emi.config.HelpLevel;
import dev.emi.emi.config.Margins;
import dev.emi.emi.config.ScreenAlign;
import dev.emi.emi.config.SidebarPages;
import dev.emi.emi.config.SidebarSettings;
import dev.emi.emi.config.SidebarSide;
import dev.emi.emi.config.SidebarSubpanels;
import dev.emi.emi.config.SidebarTheme;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.input.EmiInput;
import dev.emi.emi.mixin.accessor.HandledScreenAccessor;
import dev.emi.emi.network.CreateItemC2SPacket;
import dev.emi.emi.network.EmiNetwork;
import dev.emi.emi.platform.EmiClient;
import dev.emi.emi.registry.EmiDragDropHandlers;
import dev.emi.emi.registry.EmiExclusionAreas;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.registry.EmiRecipes;
import dev.emi.emi.registry.EmiStackProviders;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.runtime.EmiFavorites;
import dev.emi.emi.runtime.EmiHidden;
import dev.emi.emi.runtime.EmiHistory;
import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.runtime.EmiProfiler;
import dev.emi.emi.runtime.EmiReloadLog;
import dev.emi.emi.runtime.EmiReloadManager;
import dev.emi.emi.runtime.EmiSidebars;
import dev.emi.emi.screen.tooltip.RecipeTooltipComponent;
import dev.emi.emi.screen.widget.EmiSearchWidget;
import dev.emi.emi.screen.widget.SidebarButtonWidget;
import dev.emi.emi.screen.widget.SizedButtonWidget;
import dev.emi.emi.search.EmiSearch;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;

public class EmiScreenManager {
   private static final int PADDING_SIZE = 1;
   private static final int ENTRY_SIZE = 18;
   private static final int SUBPANEL_SEPARATOR_SIZE = 3;
   private static Minecraft client = Minecraft.getInstance();
   private static List<? extends EmiIngredient> searchedStacks = List.of();
   private static int lastWidth;
   private static int lastHeight;
   private static List<Bounds> lastExclusion;
   private static StackBatcher.ClaimedCollection batchers = new StackBatcher.ClaimedCollection();
   private static List<EmiScreenManager.SidebarPanel> panels = List.of(
      new EmiScreenManager.SidebarPanel(SidebarSide.LEFT, EmiConfig.leftSidebarPages),
      new EmiScreenManager.SidebarPanel(SidebarSide.RIGHT, EmiConfig.rightSidebarPages),
      new EmiScreenManager.SidebarPanel(SidebarSide.TOP, EmiConfig.topSidebarPages),
      new EmiScreenManager.SidebarPanel(SidebarSide.BOTTOM, EmiConfig.bottomSidebarPages)
   );
   public static ItemStack lastStackTooltipRendered;
   private static long lastPlayerInventorySync = 0L;
   public static EmiPlayerInventory lastPlayerInventory;
   public static int lastMouseX;
   public static int lastMouseY;
   public static EmiIngredient pressedStack = EmiStack.EMPTY;
   public static EmiIngredient draggedStack = EmiStack.EMPTY;
   private static EmiStackInteraction lastHoveredCraftable = null;
   private static boolean lastHoveredCraftableSturdy = false;
   private static int lastHoveredCraftableOffset = -1;
   private static double scrollAcc = 0.0;
   public static EmiSearchWidget search = new EmiSearchWidget(client.font, 0, 0, 160, 18);
   public static SizedButtonWidget emi = new SizedButtonWidget(
      0,
      0,
      20,
      20,
      204,
      0,
      () -> true,
      w -> client.setScreen(new ConfigScreen(client.screen)),
      List.of(EmiPort.translatable("tooltip.emi.config", EmiRenderHelper.getEmiText()))
   );
   public static SizedButtonWidget tree = new SizedButtonWidget(
      0, 0, 20, 20, 184, 0, () -> true, w -> EmiApi.viewRecipeTree(), List.of(EmiPort.translatable("tooltip.emi.recipe_tree"))
   );

   public static boolean isDisabled() {
      return !EmiReloadManager.isLoaded() || !EmiConfig.enabled;
   }

   public static void recalculate() {
      updateCraftables();
      EmiScreenManager.SidebarPanel searchPanel = getSearchPanel();
      if (searchPanel != null && searchPanel.space != null && searchedStacks != EmiSearch.stacks) {
         searchPanel.space.batcher.repopulate();
         searchedStacks = EmiSearch.stacks;
      }

      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (!base.isEmpty()) {
         Screen screen = base.screen();
         List<Bounds> exclusion = EmiExclusionAreas.getExclusion(base);
         if (lastWidth == screen.width && lastHeight == screen.height && exclusion.size() == lastExclusion.size()) {
            boolean same = true;

            for (int i = 0; i < exclusion.size(); i++) {
               Bounds a = exclusion.get(i);
               Bounds b = lastExclusion.get(i);
               if (a.x() != b.x() || a.y() != b.y() || a.width() != b.width() || a.height() != b.height()) {
                  same = false;
                  break;
               }
            }

            if (same) {
               return;
            }
         }

         for (EmiScreenManager.SidebarPanel panel : panels) {
            for (EmiScreenManager.ScreenSpace space : panel.getSpaces()) {
               space.batcher.repopulate();
            }
         }

         lastWidth = screen.width;
         lastHeight = screen.height;
         lastExclusion = exclusion;
         Bounds bounds = base.bounds();
         int left = Math.max(36, bounds.left());
         int right = Math.min(screen.width - 36, bounds.right());
         int top = bounds.left();
         int bottom = bounds.bottom();
         batchers.unclaimAll();
         List<Bounds> spaceExclusion = Lists.newArrayList();
         spaceExclusion.addAll(exclusion);
         createScreenSpace(panels.get(0), screen, spaceExclusion, false, new Bounds(0, 0, left, screen.height), SidebarSettings.LEFT);
         createScreenSpace(panels.get(1), screen, spaceExclusion, true, new Bounds(right, 0, screen.width - right, screen.height), SidebarSettings.RIGHT);
         spaceExclusion = Lists.newArrayList();
         if (panels.get(0).isVisible()) {
            spaceExclusion.add(panels.get(0).getBounds());
         }

         if (panels.get(1).isVisible()) {
            spaceExclusion.add(panels.get(1).getBounds());
         }

         spaceExclusion.addAll(exclusion);
         int topCenter = EmiConfig.topSidebarSize.values.getInt(0) * 18 / 2 + EmiConfig.topSidebarTheme.horizontalPadding;

         int topSpaceBottom = switch (EmiConfig.topSidebarAlign.horizontal) {
            case LEFT -> getVerticalConstraint(panels.get(0), EmiConfig.topSidebarMargins.left() + topCenter, top, screen.height, true);
            case CENTER -> top;
            case RIGHT -> getVerticalConstraint(panels.get(1), right - EmiConfig.topSidebarMargins.right() + topCenter, top, screen.height, true);
         };
         boolean topRtl = EmiConfig.topSidebarAlign.horizontal == ScreenAlign.Horizontal.RIGHT;
         createScreenSpace(panels.get(2), screen, spaceExclusion, topRtl, new Bounds(0, 0, screen.width, topSpaceBottom), SidebarSettings.TOP);
         int bottomCenter = EmiConfig.bottomSidebarSize.values.getInt(0) * 18 / 2 + EmiConfig.bottomSidebarTheme.horizontalPadding;

         int bottomSpaceTop = switch (EmiConfig.bottomSidebarAlign.horizontal) {
            case LEFT -> getVerticalConstraint(panels.get(0), EmiConfig.bottomSidebarMargins.left() + bottomCenter, bottom, 0, false);
            case CENTER -> bottom;
            case RIGHT -> getVerticalConstraint(panels.get(1), EmiConfig.bottomSidebarMargins.right() + bottomCenter, bottom, 0, false);
         };
         boolean bottomRtl = EmiConfig.bottomSidebarAlign.horizontal == ScreenAlign.Horizontal.RIGHT;
         createScreenSpace(
            panels.get(3),
            screen,
            spaceExclusion,
            bottomRtl,
            new Bounds(0, bottomSpaceTop, screen.width, screen.height - bottomSpaceTop),
            SidebarSettings.BOTTOM
         );
         updateSidebarButtons();
      }
   }

   private static void updateCraftables() {
      int minDelay = 400;
      if (hasSidebarVisible(SidebarType.CRAFTABLES)) {
         minDelay = 50;
      }

      if (lastPlayerInventory == null || Math.abs(System.currentTimeMillis() - lastPlayerInventorySync) >= minDelay) {
         lastPlayerInventorySync = System.currentTimeMillis();
         EmiPlayerInventory inv = EmiPlayerInventory.of(client.player);
         EmiScreenManager.SidebarPanel searchPanel = getSearchPanel();
         if (!inv.isEqual(lastPlayerInventory)) {
            lastPlayerInventory = inv;
            EmiSidebars.craftables = lastPlayerInventory.getCraftables();
            if (searchPanel != null && searchPanel.space != null) {
               searchPanel.space.batcher.repopulate();
               if (searchPanel.getType() == SidebarType.CRAFTABLES) {
                  EmiSearch.update();
               }
            }

            EmiFavorites.updateSynthetic(inv);
            repopulatePanels(SidebarType.CRAFTABLES);
         }
      }
   }

   public static void forceRecalculate() {
      lastWidth = -1;
      lastPlayerInventory = null;
      recalculate();
   }

   public static void updateSearchSidebar() {
      if (!search.isFocused && search.getValue().isEmpty()) {
         focusSearchSidebarType(EmiConfig.emptySearchSidebarFocus);
      } else {
         focusSearchSidebarType(EmiConfig.searchSidebarFocus);
      }
   }

   private static int getVerticalConstraint(EmiScreenManager.SidebarPanel panel, int cx, int def, int max, boolean top) {
      if (panel.isVisible()) {
         Bounds bounds = panel.getBounds();
         if (bounds.x() <= cx && bounds.right() >= cx) {
            return top ? Math.max(def, bounds.top()) : Math.min(def, bounds.bottom());
         }
      }

      return max;
   }

   private static void createScreenSpace(
      EmiScreenManager.SidebarPanel panel, Screen screen, List<Bounds> exclusion, boolean rtl, Bounds bounds, SidebarSettings settings
   ) {
      Margins margins = settings.margins();
      ScreenAlign align = settings.align();
      SidebarTheme theme = settings.theme();
      SidebarSubpanels subpanels = settings.subpanels();
      boolean header = settings.header() == HeaderType.VISIBLE;
      int maxWidth = settings.size().values.getInt(0);
      int maxHeight = settings.size().values.getInt(1);
      int subpanelHeight = 0;

      for (SidebarSubpanels.Subpanel subpanel : subpanels.subpanels) {
         subpanelHeight += subpanel.rows() * 18 + 3;
         maxHeight -= subpanel.rows();
      }

      if (panel.getType() == SidebarType.CHESS) {
         maxWidth = 8;
         maxHeight = 8;
         theme = SidebarTheme.MODERN;
      }

      int cx = bounds.x() + bounds.width() / 2;
      int cy = bounds.y() + bounds.height() / 2;
      int headerOffset = header ? 18 : 0;
      int idealWidth = Math.min(maxWidth * 18 + margins.left() + margins.right() + theme.horizontalPadding * 2, bounds.width());
      int idealHeight = Math.min(maxHeight * 18 + margins.top() + margins.bottom() + theme.verticalPadding * 2 + headerOffset + subpanelHeight, bounds.height());

      int idealX = switch (align.horizontal) {
         case LEFT -> bounds.x();
         case CENTER -> bounds.x() + bounds.width() / 2 - idealWidth / 2;
         case RIGHT -> bounds.right() - idealWidth;
      };

      int idealY = switch (align.horizontal) {
         case LEFT -> bounds.y();
         case CENTER -> bounds.y() + bounds.height() / 2 - idealHeight / 2;
         case RIGHT -> bounds.bottom() - idealHeight;
      };
      Bounds idealBounds = constrainBounds(exclusion, new Bounds(idealX, idealY, idealWidth, idealHeight), align, headerOffset);
      bounds = constrainBounds(exclusion, bounds, align, headerOffset);
      if (Math.min(idealWidth, idealBounds.width()) * Math.min(idealHeight, idealBounds.height())
         > Math.min(idealWidth, bounds.width()) * Math.min(idealHeight, bounds.height())) {
         bounds = idealBounds;
      }

      int xMin = bounds.left() + margins.left() + theme.horizontalPadding;
      int xMax = bounds.right() - margins.right() - theme.horizontalPadding;
      int yMin = bounds.top() + margins.top() + theme.verticalPadding;
      int yMax = bounds.bottom() - margins.bottom() - theme.verticalPadding;
      int xSpan = xMax - xMin;
      int ySpan = yMax - yMin;
      int tw = Math.max(0, Math.min(xSpan / 18, maxWidth));
      int th = Math.max(0, Math.min((ySpan - headerOffset - subpanelHeight) / 18, maxHeight));
      int hl = xMin;
      int hr = xMax - tw * 18;

      int tx = switch (align.horizontal) {
         case LEFT -> hl;
         case CENTER -> Mth.clamp(cx - tw * 18 / 2, hl, hr);
         case RIGHT -> hr;
      };
      int vt = yMin + headerOffset;
      int vb = yMax - th * 18 - subpanelHeight;

      int ty = switch (align.vertical) {
         case TOP -> vt;
         case CENTER -> Mth.clamp(cy - (th * 18 - headerOffset + subpanelHeight + theme.verticalPadding / 2) / 2, vt, vb);
         case BOTTOM -> vb;
      };
      panel.header = header;
      panel.theme = theme;
      EmiScreenManager.ScreenSpace space = new EmiScreenManager.ScreenSpace(tx, ty, tw, th, rtl, exclusion, () -> panel.getType(), panel.isSearch());
      List<EmiScreenManager.ScreenSpace> subspaces = Lists.newArrayList();

      for (SidebarSubpanels.Subpanel subpanel : subpanels.subpanels) {
         ty += th * 18 + 3;
         th = subpanel.rows();
         subspaces.add(new EmiScreenManager.ScreenSpace(tx, ty, tw, th, rtl, exclusion, () -> subpanel.type, false));
      }

      panel.setSpaces(space, subspaces);
   }

   private static Bounds constrainBounds(List<Bounds> exclusion, Bounds bounds, ScreenAlign align, int headerOffset) {
      for (int i = 0; i < exclusion.size(); i++) {
         Bounds overlap = exclusion.get(i).overlap(bounds);
         if (!overlap.empty()
            && !bounds.empty()
            && (overlap.top() < bounds.top() + 18 + headerOffset || overlap.width() >= bounds.width() * 2 / 3 || overlap.height() >= bounds.height() / 3)) {
            int widthFactor = overlap.width() * 10 / bounds.width();
            int heightFactor = overlap.height() * 10 / bounds.height();
            if (heightFactor < widthFactor) {
               int cy = bounds.y() + bounds.height() / 2;
               int ocy = overlap.y() + overlap.height() / 2;

               cy += switch (align.vertical) {
                  case TOP -> -bounds.height() / 4;
                  case CENTER -> 0;
                  case BOTTOM -> bounds.height() / 4;
               };
               if (cy < ocy) {
                  bounds = new Bounds(bounds.x(), bounds.y(), bounds.width(), overlap.top() - bounds.top());
               } else {
                  bounds = new Bounds(bounds.x(), overlap.bottom(), bounds.width(), bounds.bottom() - overlap.bottom());
               }
            } else {
               int cx = bounds.x() + bounds.width() / 2;
               int ocx = overlap.x() + overlap.width() / 2;

               cx += switch (align.horizontal) {
                  case LEFT -> -bounds.width() / 4;
                  case CENTER -> 0;
                  case RIGHT -> bounds.width() / 4;
               };
               if (cx < ocx) {
                  bounds = new Bounds(bounds.x(), bounds.y(), overlap.left() - bounds.left(), bounds.height());
               } else {
                  bounds = new Bounds(overlap.right(), bounds.y(), bounds.right() - overlap.right(), bounds.height());
               }
            }

            i = -1;
         }
      }

      return bounds.empty() ? Bounds.EMPTY : bounds;
   }

   public static void focusSearchSidebarType(SidebarType type) {
      EmiScreenManager.SidebarPanel search = getSearchPanel();
      if (search != null && search.supportsType(type)) {
         search.setType(type);
      }
   }

   public static void focusSidebarType(SidebarType type) {
      for (EmiScreenManager.SidebarPanel panel : panels) {
         if (panel.supportsType(type)) {
            panel.setType(type);
         }
      }
   }

   @Nullable
   public static EmiScreenManager.SidebarPanel getPanelFor(SidebarSide side) {
      for (EmiScreenManager.SidebarPanel panel : panels) {
         if (panel.side == side) {
            return panel;
         }
      }

      return null;
   }

   @Nullable
   public static EmiScreenManager.SidebarPanel getPanelFor(SidebarType type) {
      for (EmiScreenManager.SidebarPanel panel : panels) {
         if (panel.getType() == type) {
            return panel;
         }
      }

      return null;
   }

   @Nullable
   public static EmiScreenManager.SidebarPanel getHoveredPanel(int mouseX, int mouseY) {
      for (EmiScreenManager.SidebarPanel panel : panels) {
         if (panel.getBounds().contains(mouseX, mouseY) && panel.isVisible()) {
            return panel;
         }
      }

      return null;
   }

   @Nullable
   public static EmiScreenManager.ScreenSpace getHoveredSpace(int mouseX, int mouseY) {
      EmiScreenManager.SidebarPanel panel = getHoveredPanel(mouseX, mouseY);
      return panel != null ? panel.getHoveredSpace(mouseX, mouseY) : null;
   }

   public static boolean hasSidebarVisible(SidebarType type) {
      for (EmiScreenManager.SidebarPanel panel : panels) {
         for (EmiScreenManager.ScreenSpace space : panel.getSpaces()) {
            if (type == space.getType()) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean hasSidebarAvailable(SidebarType type) {
      for (EmiScreenManager.SidebarPanel panel : panels) {
         if (panel.supportsType(type)) {
            return true;
         }
      }

      return false;
   }

   public static void repopulatePanels(SidebarType type) {
      for (EmiScreenManager.SidebarPanel panel : panels) {
         for (EmiScreenManager.ScreenSpace space : panel.getSpaces()) {
            if (space.getType() == type) {
               space.batcher.repopulate();
            }
         }
      }
   }

   public static EmiScreenManager.SidebarPanel getSearchPanel() {
      for (EmiScreenManager.SidebarPanel panel : panels) {
         if (panel.isSearch()) {
            return panel;
         }
      }

      return null;
   }

   public static void toggleSidebarType(SidebarType type) {
      boolean visible = false;

      for (EmiScreenManager.SidebarPanel panel : panels) {
         if (panel.getType() == type) {
            visible = true;
            panel.cycleType(1);
         }
      }

      if (!visible) {
         focusSidebarType(type);
      }
   }

   public static List<? extends EmiIngredient> getSearchSource() {
      EmiScreenManager.SidebarPanel search = getSearchPanel();
      return search == null ? List.of() : EmiSidebars.getStacks(search.getType());
   }

   public static EmiStackInteraction getHoveredStack(int mouseX, int mouseY, boolean notClick) {
      return getHoveredStack(mouseX, mouseY, notClick, false);
   }

   public static int getDebugTextX() {
      int x = 4;
      if (emi.visible) {
         x = Math.max(4, 26);
      }

      if (tree.visible) {
         x = Math.max(4, 48);
      }

      return x;
   }

   public static EmiStackInteraction getHoveredStack(int mouseX, int mouseY, boolean notClick, boolean ignoreLastHoveredCraftable) {
      if (client.screen == null) {
         return EmiStackInteraction.EMPTY;
      }

      EmiStackInteraction stack = EmiStackProviders.getStackAt(client.screen, mouseX, mouseY, notClick);
      if (!stack.isEmpty()) {
         return stack;
      }

      if (!ignoreLastHoveredCraftable && lastHoveredCraftable != null) {
         if (lastHoveredCraftable.getRecipeContext() != null
            && (lastHoveredCraftableSturdy || lastPlayerInventory == null || lastPlayerInventory.canCraft(lastHoveredCraftable.getRecipeContext()))) {
            return lastHoveredCraftable;
         }

         lastHoveredCraftable = null;
      }

      for (EmiScreenManager.SidebarPanel panel : panels) {
         for (EmiScreenManager.ScreenSpace space : panel.getSpaces()) {
            if (panel.isVisible() && space.pageSize > 0 && space.contains(mouseX, mouseY) && mouseX >= space.tx && mouseY >= space.ty) {
               int x = (mouseX - space.tx) / 18;
               int y = (mouseY - space.ty) / 18;
               int n = space.getRawOffset(x, y);
               if (n >= 0 && space == panel.space) {
                  n += space.pageSize * panel.page;
               }

               if (n >= 0 && n < space.getStacks().size()) {
                  EmiIngredient hovered = space.getStacks().get(n);
                  if (hovered instanceof EmiFavorite fav) {
                     return new EmiScreenManager.SidebarEmiStackInteraction(hovered, space, fav.getRecipe(), true);
                  }

                  return new EmiScreenManager.SidebarEmiStackInteraction(hovered, space);
               }
            }
         }
      }

      return lastStackTooltipRendered != null && notClick ? new EmiStackInteraction(EmiStack.of(lastStackTooltipRendered)) : EmiStackInteraction.EMPTY;
   }

   private static void updateMouse(int mouseX, int mouseY) {
      if (lastHoveredCraftable != null) {
         EmiScreenManager.ScreenSpace space = getHoveredSpace(mouseX, mouseY);
         if (space != null && (space.getType() == SidebarType.CRAFTABLES || space.getType() == SidebarType.CRAFT_HISTORY)) {
            int offset = space.getRawOffsetFromMouse(mouseX, mouseY);
            if (offset != lastHoveredCraftableOffset) {
               lastHoveredCraftable = null;
            }
         } else {
            lastHoveredCraftable = null;
         }
      }

      lastMouseX = mouseX;
      lastMouseY = mouseY;
   }

   public static void drawBackground(EmiDrawContext context, int mouseX, int mouseY, float delta) {
      updateMouse(mouseX, mouseY);
      recalculate();
      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (!base.isEmpty()) {
         EmiProfiler.push("sidebar");

         for (EmiScreenManager.SidebarPanel panel : panels) {
            panel.drawBackground(context, mouseX, mouseY, delta);
         }
      }
   }

   public static void render(EmiDrawContext context, int mouseX, int mouseY, float delta) {
      EmiProfiler.push("emi");
      updateMouse(mouseX, mouseY);
      recalculate();
      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (base.isEmpty()) {
         EmiProfiler.pop();
      } else {
         boolean visible = !isDisabled();
         emi.visible = EmiConfig.emiConfigButtonVisibility.resolve(visible);
         tree.visible = EmiConfig.recipeTreeButtonVisibility.resolve(visible);

         for (EmiScreenManager.SidebarPanel panel : panels) {
            panel.updateWidgetVisibility();
         }

         renderWidgets(context, mouseX, mouseY, delta, base);
         if (isDisabled()) {
            int screenHeight = base.screen().height;
            if (!EmiReloadManager.isLoaded()) {
               int reloadInfoX = getDebugTextX();
               if (EmiReloadManager.getStatus() == -1) {
                  context.drawTextWithShadow(EmiPort.translatable("emi.reloading.error"), reloadInfoX, screenHeight - 16);
               } else if (EmiReloadManager.getStatus() == 0) {
                  context.drawTextWithShadow(EmiPort.translatable("emi.reloading.waiting"), reloadInfoX, screenHeight - 16);
               } else {
                  context.drawTextWithShadow(EmiPort.translatable("emi.reloading"), reloadInfoX, screenHeight - 16);
                  context.drawTextWithShadow(EmiReloadManager.reloadStep, reloadInfoX, screenHeight - 26);
                  if (System.currentTimeMillis() > EmiReloadManager.reloadWorry) {
                     context.drawTextWithShadow(EmiPort.translatable("emi.reloading.worry"), reloadInfoX, screenHeight - 36);
                  }
               }
            }

            EmiProfiler.pop();
            lastHoveredCraftable = null;
         } else {
            if (EmiRecipes.activeWorker != null) {
               int bakingX = getDebugTextX();
               context.drawTextWithShadow(EmiPort.translatable("emi.reloading.still_baking_recipes"), bakingX, base.screen().height - 16);
            } else {
               renderDevMode(context, mouseX, mouseY, delta, base);
            }

            EmiProfiler.push("sidebars");

            for (EmiScreenManager.SidebarPanel panel : panels) {
               panel.render(context, mouseX, mouseY, delta);
            }

            renderLastHoveredCraftable(context, mouseX, mouseY, delta, base);
            EmiProfiler.pop();
            renderExclusionAreas(context, mouseX, mouseY, delta, base);
            EmiProfiler.swap("slots");
            renderSlotOverlays(context, mouseX, mouseY, delta, base);
            EmiProfiler.pop();
            context.disableDepthTest();
         }
      }
   }

   public static void drawForeground(EmiDrawContext context, int mouseX, int mouseY, float delta) {
      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (!base.isEmpty() && !isDisabled()) {
         renderDraggedStack(context, mouseX, mouseY, delta, base);
         renderCurrentTooltip(context, mouseX, mouseY, delta, base);
      }
   }

   private static void renderWidgets(EmiDrawContext context, int mouseX, int mouseY, float delta, EmiScreenBase base) {
      context.push();
      context.matrices().translate(0.0F, 0.0F, 100.0F);
      emi.render(context.raw(), mouseX, mouseY, delta);
      tree.render(context.raw(), mouseX, mouseY, delta);
      search.render(context.raw(), mouseX, mouseY, delta);
      context.pop();
   }

   private static void renderLastHoveredCraftable(EmiDrawContext context, int mouseX, int mouseY, float delta, EmiScreenBase base) {
      if (lastHoveredCraftable != null && lastHoveredCraftableOffset != -1) {
         EmiStackInteraction cur = getHoveredStack(mouseX, mouseY, false, true);
         if (cur.getRecipeContext() != lastHoveredCraftable.getRecipeContext()) {
            EmiScreenManager.ScreenSpace space = getHoveredSpace(mouseX, mouseY);
            if (space != null && (space.getType() == SidebarType.CRAFTABLES || space.getType() == SidebarType.CRAFT_HISTORY)) {
               Matrix4fStack view = RenderSystem.getModelViewStack();
               view.pushMatrix();
               view.translate(0.0F, 0.0F, 200.0F);
               EmiPort.applyModelViewMatrix();
               int lhx = space.getRawX(lastHoveredCraftableOffset);
               int lhy = space.getRawY(lastHoveredCraftableOffset);
               context.fill(lhx, lhy, 18, 18, 1151992063);
               lastHoveredCraftable.getStack().render(context.raw(), lhx + 1, lhy + 1, delta, 1);
               view.popMatrix();
               EmiPort.applyModelViewMatrix();
            }
         }
      }
   }

   private static void renderDraggedStack(EmiDrawContext context, int mouseX, int mouseY, float delta, EmiScreenBase base) {
      if (!draggedStack.isEmpty()) {
         EmiScreenManager.SidebarPanel panel = getHoveredPanel(mouseX, mouseY);
         if (panel != null) {
            EmiScreenManager.ScreenSpace space = panel.getHoveredSpace(mouseX, mouseY);
            if (space != null && space.getType() == SidebarType.FAVORITES) {
               int pageSize = space.pageSize;
               int page = panel.page;
               int index = space.getClosestEdge(mouseX, mouseY);
               if (index + pageSize * page > EmiFavorites.favorites.size()) {
                  index = EmiFavorites.favorites.size() - pageSize * page;
               }

               if (index + pageSize * page > space.getStacks().size()) {
                  index = space.getStacks().size() - pageSize * page;
               }

               if (index >= 0) {
                  context.push();
                  context.matrices().translate(0.0F, 0.0F, 200.0F);
                  int dx = space.getEdgeX(index);
                  int dy = space.getEdgeY(index);
                  context.fill(dx - 1, dy, 2, 18, -16711681);
                  context.pop();
               }
            }
         }

         context.push();
         context.matrices().translate(0.0F, 0.0F, 400.0F);
         EmiDragDropHandlers.render(base.screen(), draggedStack, context.raw(), mouseX, mouseY, delta);
         draggedStack.render(context.raw(), mouseX - 8, mouseY - 8, delta, 1);
         context.pop();
      }
   }

   private static void renderCurrentTooltip(EmiDrawContext context, int mouseX, int mouseY, float delta, EmiScreenBase base) {
      try {
         ItemStack cursor = ItemStack.EMPTY;
         if (client.screen instanceof AbstractContainerScreen<?> handled) {
            cursor = handled.getMenu().getCarried();
         }

         EmiScreenManager.ScreenSpace space = getHoveredSpace(mouseX, mouseY);
         if (EmiApi.isCheatMode() && !cursor.isEmpty() && space != null && space.getType() == SidebarType.INDEX && EmiConfig.deleteCursorStack.isBound()) {
            List<ClientTooltipComponent> list = List.of(
               ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.delete_stack"))),
               ClientTooltipComponent.create(EmiPort.ordered(EmiConfig.deleteCursorStack.getBindText()))
            );
            if (space.rtl) {
               EmiRenderHelper.drawLeftTooltip(base.screen(), context, list, mouseX, mouseY);
            } else {
               EmiRenderHelper.drawTooltip(base.screen(), context, list, mouseX, mouseY);
            }
         }

         if (cursor.isEmpty() && draggedStack.isEmpty()) {
            EmiProfiler.swap("hover");

            try {
               EmiIngredient hov = EmiStack.EMPTY;
               SidebarType sidebar = SidebarType.NONE;
               if (getHoveredStack(mouseX, mouseY, false) instanceof EmiScreenManager.SidebarEmiStackInteraction sesi) {
                  hov = sesi.getStack();
                  sidebar = sesi.getType();
               }

               List<ClientTooltipComponent> list = Lists.newArrayList();
               list.addAll(hov.getTooltip());
               if (EmiApi.getRecipeContext(hov) == null && EmiConfig.showCraft.isHeld()) {
                  EmiRecipe recipe = EmiUtil.getPreferredRecipe(hov, lastPlayerInventory, false);
                  if (recipe != null) {
                     list.add(new RecipeTooltipComponent(recipe, true));
                  }
               }

               if (EmiConfig.editMode && sidebar == SidebarType.INDEX) {
                  list.add(
                     ClientTooltipComponent.create(EmiPort.translatable("emi.edit_mode.hide_one", EmiConfig.hideStack.getBindText()).getVisualOrderText())
                  );
                  list.add(
                     ClientTooltipComponent.create(EmiPort.translatable("emi.edit_mode.hide_all", EmiConfig.hideStackById.getBindText()).getVisualOrderText())
                  );
               }

               if (space != null && space.rtl) {
                  EmiRenderHelper.drawLeftTooltip(base.screen(), context, list, mouseX, mouseY);
               } else {
                  EmiRenderHelper.drawTooltip(base.screen(), context, list, mouseX, mouseY);
               }
            } finally {
               EmiProfiler.pop();
            }
         }
      } catch (Exception var17) {
         Exception e = var17;

         try {
            EmiLog.error("Error rendering tooltip", e);
            List<ClientTooltipComponent> list = List.of(
               EmiTooltipComponents.of(EmiPort.literal("Error rendering tooltip", ChatFormatting.RED)),
               EmiTooltipComponents.of(EmiPort.literal("See log", ChatFormatting.GRAY))
            );
            EmiRenderHelper.drawTooltip(base.screen(), context, list, mouseX, mouseY);
         } catch (Exception e2) {
            EmiLog.error("Critical error rendering tooltip", var17);
         }
      }

      lastStackTooltipRendered = null;
   }

   private static void renderDevMode(EmiDrawContext context, int mouseX, int mouseY, float delta, EmiScreenBase base) {
      if (EmiConfig.devMode) {
         Screen screen = base.screen();
         EmiProfiler.swap("dev");
         int color = 16777215;
         Component title = EmiPort.literal("EMI Dev Mode");
         int off = -16;
         int devTextX = getDebugTextX();
         if (!EmiReloadLog.warnings.isEmpty()) {
            color = 16711680;
            off = -11;
            String warnCount = EmiReloadLog.warningCount + " Warnings";
            context.drawTextWithShadow(EmiPort.literal(warnCount), devTextX, screen.height - 21, color);
            int width = Math.max(client.font.width(title), client.font.width(warnCount));
            if (mouseX >= devTextX && mouseX < width + devTextX && mouseY > screen.height - 28) {
               context.raw()
                  .renderComponentTooltip(
                     client.font, Stream.concat(Stream.of(" EMI detected some issues, see log for full details"), EmiReloadLog.warnings.stream()).map(s -> {
                        String a = s;
                        if (a.length() > 10 && client.font.width(a) > screen.width - 20) {
                           a = client.font.plainSubstrByWidth(a, screen.width - 30) + "...";
                        }

                        return EmiPort.literal(a);
                     }).collect(Collectors.toList()), 0, 20
                  );
            }
         }

         context.drawTextWithShadow(title, devTextX, screen.height + off, color);
      }
   }

   private static void renderExclusionAreas(EmiDrawContext context, int mouseX, int mouseY, float delta, EmiScreenBase base) {
      if (EmiConfig.highlightExclusionAreas) {
         for (EmiScreenManager.SidebarPanel panel : panels) {
            if (panel.isVisible()) {
               Bounds bounds = panel.getBounds();
               context.fill(bounds.left(), bounds.top(), bounds.width(), bounds.height(), 1140850943);
            }
         }

         List<Bounds> exclusions = EmiExclusionAreas.getExclusion(base);
         if (exclusions.size() == 0) {
            return;
         }

         for (int i = 0; i < exclusions.size(); i++) {
            Bounds b = exclusions.get(i);
            context.fill(b.x(), b.y(), b.width(), b.height(), i == 0 ? 1140915968 : 1157562368);
         }
      }
   }

   private static void renderSlotOverlays(EmiDrawContext context, int mouseX, int mouseY, float delta, EmiScreenBase base) {
      EmiSearch.CompiledQuery query = null;
      if (search.highlight) {
         query = EmiSearch.compiledQuery;
      }

      Set<Slot> ignoredSlots = Sets.newHashSet();
      Set<EmiStack> synfavs = Sets.newHashSet();
      if (BoM.craftingMode && BoM.tree != null) {
         for (EmiFavorite.Synthetic fav : EmiFavorites.syntheticFavorites) {
            synfavs.addAll(fav.getEmiStacks());
         }

         try {
            AbstractContainerScreen<?> hs = EmiApi.getHandledScreen();

            for (EmiRecipeHandler handler : EmiRecipeFiller.getAllHandlers(hs)) {
               if (handler instanceof StandardRecipeHandler standard) {
                  ignoredSlots.addAll(standard.getInputSources(hs.getMenu()));
                  ignoredSlots.addAll(standard.getCraftingSlots(hs.getMenu()));
               }
            }
         } catch (Throwable t) {
            EmiLog.error("Recipe handler is throwing in renderSlotOverlays:", t);
         }
      }

      if (base.screen() instanceof AbstractContainerScreen<?> hs && hs instanceof HandledScreenAccessor hsa) {
         context.push();
         context.matrices().translate(hsa.getX(), hsa.getY(), 0.0F);

         for (Slot slot : hs.getMenu().slots) {
            if (slot.isActive()) {
               EmiStack stack = EmiStack.of(slot.getItem());
               context.push();
               context.matrices().translate(0.0F, 0.0F, 300.0F);
               if (query != null) {
                  if (!query.test(stack)) {
                     context.fill(slot.x - 1, slot.y - 1, 18, 18, 1996488704);
                  }
               } else if (BoM.craftingMode
                  && BoM.tree != null
                  && !(slot.container instanceof Inventory)
                  && !ignoredSlots.contains(slot)
                  && synfavs.contains(stack)) {
                  context.fill(slot.x - 1, slot.y - 1, 18, 18, 1996536831);
               }

               context.pop();
            }
         }

         context.pop();
      }
   }

   public static void addWidgets(Screen screen) {
      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (!base.isEmpty()) {
         forceRecalculate();
         if (EmiConfig.centerSearchBar || panels.get(0).space == null || panels.get(1).space == null) {
            search.x = (screen.width - 160) / 2;
            search.y = screen.height - 21;
            search.setWidth(160);
         } else if (EmiConfig.searchSidebar == SidebarSide.RIGHT) {
            search.x = panels.get(1).space.tx;
            search.y = screen.height - 21;
            search.setWidth(panels.get(1).space.tw * 18);
         } else {
            search.x = panels.get(0).space.tx;
            search.y = screen.height - 21 - 21;
            search.setWidth(panels.get(0).space.tw * 18);
         }

         EmiPort.focus(search, false);
         search.setVisible(EmiConfig.searchSidebar != SidebarSide.NONE);
         emi.x = 2;
         emi.y = screen.height - 22;
         tree.x = 24;
         tree.y = screen.height - 22;
         updateSidebarButtons();
      }
   }

   private static void updateSidebarButtons() {
      for (EmiScreenManager.SidebarPanel panel : panels) {
         panel.updateWidgetPosition();
         panel.updateWidgetVisibility();
      }
   }

   private static boolean isClickClicky(int button) {
      return button >= 0 && button < 3;
   }

   public static boolean mouseScrolled(double mouseX, double mouseY, double amount) {
      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (base.isEmpty()) {
         return false;
      }

      scrollAcc += amount;
      int sa = (int)scrollAcc;
      scrollAcc %= 1.0;
      if (isDisabled()) {
         return false;
      }

      recalculate();
      EmiScreenManager.SidebarPanel panel = getHoveredPanel((int)mouseX, (int)mouseY);
      if (panel != null) {
         int mx = (int)mouseX;
         int my = (int)mouseY;

         for (Bounds bounds : EmiExclusionAreas.getExclusion(base)) {
            if (bounds.contains(mx, my)) {
               return false;
            }
         }

         panel.scroll(-sa);
         return true;
      } else {
         return false;
      }
   }

   public static boolean mouseClicked(double mouseX, double mouseY, int button) {
      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (base.isEmpty()) {
         return false;
      }

      if (search.mouseClicked(mouseX, mouseY, button)) {
         return true;
      }

      if (emi.mouseClicked(mouseX, mouseY, button)) {
         return true;
      }

      if (tree.mouseClicked(mouseX, mouseY, button)) {
         return true;
      }

      for (EmiScreenManager.SidebarPanel panel : panels) {
         if (panel.cycle.mouseClicked(mouseX, mouseY, button)) {
            return true;
         }

         if (panel.pageLeft.mouseClicked(mouseX, mouseY, button)) {
            return true;
         }

         if (panel.pageRight.mouseClicked(mouseX, mouseY, button)) {
            return true;
         }
      }

      if (!isDisabled()) {
         recalculate();
         EmiIngredient ingredient = getHoveredStack((int)mouseX, (int)mouseY, !isClickClicky(button)).getStack();
         pressedStack = ingredient;
         if (!ingredient.isEmpty()) {
            ingredient = getHoveredStack((int)mouseX, (int)mouseY, false).getStack();
            if (!ingredient.isEmpty()) {
               return true;
            }
         } else if (genericInteraction(bind -> bind.matchesMouse(button))) {
            return true;
         }

         return false;
      } else if (EmiConfig.toggleVisibility.matchesMouse(button)) {
         toggleVisibility(true);
         return true;
      } else {
         return false;
      }
   }

   public static boolean mouseReleased(double mouseX, double mouseY, int button) {
      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (base.isEmpty()) {
         return false;
      }

      try {
         if (isDisabled()) {
            return false;
         }

         int mx = (int)mouseX;
         int my = (int)mouseY;
         recalculate();
         if (EmiApi.isCheatMode() && EmiConfig.deleteCursorStack.matchesMouse(button) && deleteCursor(mx, my)) {
            return false;
         }

         EmiScreenManager.SidebarPanel panel = getHoveredPanel(mx, my);
         if (draggedStack == EmiStack.EMPTY && panel != null && panel.getType() == SidebarType.CHESS) {
            EmiChess.interact(pressedStack, button);
            return true;
         }

         if (!pressedStack.isEmpty()) {
            if (!draggedStack.isEmpty()) {
               if (panel != null) {
                  EmiScreenManager.ScreenSpace space = panel.getHoveredSpace(mx, my);
                  if (space != null && space.getType() == SidebarType.FAVORITES) {
                     int page = panel.page;
                     int pageSize = space.pageSize;
                     int index = Math.min(space.getClosestEdge(mx, my), EmiFavorites.favorites.size());
                     if (index + pageSize * page > EmiFavorites.favorites.size()) {
                        index = EmiFavorites.favorites.size() - pageSize * page;
                     }

                     if (index >= 0) {
                        EmiFavorites.addFavoriteAt(draggedStack, index + pageSize * page);
                        space.batcher.repopulate();
                     }

                     return true;
                  }

                  if (panel.getType() == SidebarType.CHESS) {
                     EmiChess.drop(draggedStack, getHoveredStack(mx, my, true).getStack());
                  }
               } else if (client.screen != null && EmiDragDropHandlers.dropStack(client.screen, draggedStack, mx, my)) {
                  return true;
               }
            } else {
               EmiStackInteraction hovered = getHoveredStack((int)mouseX, (int)mouseY, !isClickClicky(button));
               if (draggedStack.isEmpty() && stackInteraction(hovered, bind -> bind.matchesMouse(button))) {
                  return true;
               }
            }

            if (genericInteraction(bind -> bind.matchesMouse(button))) {
               return true;
            }
         }

         return false;
      } finally {
         pressedStack = EmiStack.EMPTY;
         draggedStack = EmiStack.EMPTY;
      }
   }

   public static boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (base.isEmpty()) {
         return false;
      }

      if (isDisabled()) {
         return false;
      }

      if (draggedStack.isEmpty() && button == 0) {
         if (client.screen instanceof AbstractContainerScreen<?> handled && !handled.getMenu().getCarried().isEmpty()) {
            return false;
         }

         recalculate();
         EmiStackInteraction hovered = getHoveredStack((int)mouseX, (int)mouseY, !isClickClicky(button));
         if (hovered.getStack() != pressedStack && !(pressedStack instanceof EmiFavorite.Synthetic)) {
            draggedStack = pressedStack;
         }
      }

      return false;
   }

   public static boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      EmiScreenBase base = EmiScreenBase.getCurrent();
      if (base.isEmpty()) {
         return false;
      }

      if (isDisabled()) {
         if (EmiConfig.toggleVisibility.matchesKey(keyCode, scanCode)) {
            toggleVisibility(true);
            return true;
         } else {
            return false;
         }
      } else if (search.keyPressed(keyCode, scanCode, modifiers) || search.canConsumeInput()) {
         return true;
      } else if (hasFocusedTextField(client.screen, 10)) {
         return false;
      } else if (EmiApi.isCheatMode() && EmiConfig.deleteCursorStack.matchesKey(keyCode, scanCode) && deleteCursor(lastMouseX, lastMouseY)) {
         return true;
      } else if (EmiInput.isControlDown() && keyCode == 89) {
         EmiApi.displayAllRecipes();
         return true;
      } else {
         recalculate();
         return stackInteraction(getHoveredStack(lastMouseX, lastMouseY, true), bind -> bind.matchesKey(keyCode, scanCode))
            ? true
            : genericInteraction(bind -> bind.matchesKey(keyCode, scanCode));
      }
   }

   private static boolean hasFocusedTextField(ContainerEventHandler parent, int depthBail) {
      if (depthBail <= 0) {
         return false;
      }

      for (GuiEventListener e : parent.children()) {
         if (e instanceof EditBox tfw && tfw.canConsumeInput() && tfw.visible) {
            return true;
         }

         if (e instanceof ContainerEventHandler p) {
            return hasFocusedTextField(p, depthBail - 1);
         }
      }

      return false;
   }

   public static boolean genericInteraction(Function<EmiBind, Boolean> function) {
      if (function.apply(EmiConfig.toggleVisibility)) {
         toggleVisibility(true);
         return true;
      }

      boolean searchBreak = false;
      if (function.apply(EmiConfig.focusSearch) && client.screen != null) {
         client.screen.setFocused(search);
         EmiPort.focus(search, true);
         searchBreak = true;
      }

      if (function.apply(EmiConfig.clearSearch)) {
         search.setValue("");
         searchBreak = true;
      }

      if (searchBreak) {
         return true;
      }

      if (function.apply(EmiConfig.viewTree)) {
         EmiApi.viewRecipeTree();
         return true;
      }

      if (function.apply(EmiConfig.back)) {
         if (!EmiHistory.isEmpty()) {
            EmiHistory.pop();
            return true;
         }
      } else if (function.apply(EmiConfig.forward) && !EmiHistory.isForwardEmpty()) {
         EmiHistory.forward();
         return true;
      }

      return false;
   }

   public static boolean stackInteraction(EmiStackInteraction stack, Function<EmiBind, Boolean> function) {
      EmiIngredient ingredient = stack.getStack();
      EmiRecipe context = EmiApi.getRecipeContext(ingredient);
      if (!ingredient.isEmpty()) {
         if (EmiConfig.editMode && stack instanceof EmiScreenManager.SidebarEmiStackInteraction sesi && sesi.getType() == SidebarType.INDEX) {
            if (function.apply(EmiConfig.hideStack)) {
               EmiHidden.setVisibility(sesi.getStack(), !EmiHidden.isHidden(sesi.getStack()), false);
               return true;
            }

            if (function.apply(EmiConfig.hideStackById)) {
               EmiHidden.setVisibility(sesi.getStack(), !EmiHidden.isHidden(sesi.getStack()), true);
               return true;
            }
         }

         if (craftInteraction(ingredient, () -> context, stack, function)) {
            return true;
         }

         if (EmiApi.isCheatMode() && ingredient.getEmiStacks().size() == 1 && stack instanceof EmiScreenManager.SidebarEmiStackInteraction) {
            if (function.apply(EmiConfig.cheatOneToInventory)) {
               return give(ingredient.getEmiStacks().get(0), 1, 0);
            }

            if (function.apply(EmiConfig.cheatStackToInventory)) {
               return give(ingredient.getEmiStacks().get(0), ingredient.getEmiStacks().get(0).getItemStack().getMaxStackSize(), 0);
            }

            if (function.apply(EmiConfig.cheatOneToCursor)) {
               return give(ingredient.getEmiStacks().get(0), 1, 1);
            }

            if (function.apply(EmiConfig.cheatStackToCursor)) {
               return give(ingredient.getEmiStacks().get(0), ingredient.getEmiStacks().get(0).getItemStack().getMaxStackSize(), 1);
            }
         }

         if (function.apply(EmiConfig.viewRecipes)) {
            EmiApi.displayRecipes(ingredient);
            if (stack.getRecipeContext() != null) {
               EmiApi.focusRecipe(stack.getRecipeContext());
            }

            return true;
         }

         if (function.apply(EmiConfig.viewUses)) {
            EmiApi.displayUses(ingredient);
            return true;
         }

         if (function.apply(EmiConfig.favorite)) {
            EmiFavorites.addFavorite(ingredient, stack.getRecipeContext());
            repopulatePanels(SidebarType.FAVORITES);
            return true;
         }

         if (function.apply(EmiConfig.viewStackTree) && stack.getRecipeContext() != null) {
            BoM.setGoal(stack.getRecipeContext());
            EmiApi.viewRecipeTree();
            return true;
         }

         Supplier<EmiRecipe> supplier = () -> EmiUtil.getPreferredRecipe(ingredient, lastPlayerInventory, true);
         if (craftInteraction(ingredient, supplier, stack, function)) {
            return true;
         }
      }

      return recipeInteraction(context, function);
   }

   private static boolean craftInteraction(
      EmiIngredient ingredient, Supplier<EmiRecipe> contextSupplier, EmiStackInteraction stack, Function<EmiBind, Boolean> function
   ) {
      if (!(stack instanceof EmiScreenManager.SidebarEmiStackInteraction)) {
         return false;
      }

      EmiCraftContext.Destination destination = null;
      boolean all = false;
      if (function.apply(EmiConfig.craftAllToInventory)) {
         destination = EmiCraftContext.Destination.INVENTORY;
         all = true;
      } else if (function.apply(EmiConfig.craftOneToInventory)) {
         destination = EmiCraftContext.Destination.INVENTORY;
      } else if (function.apply(EmiConfig.craftOneToCursor)) {
         destination = EmiCraftContext.Destination.CURSOR;
      } else if (function.apply(EmiConfig.craftAll)) {
         destination = EmiCraftContext.Destination.NONE;
         all = true;
      } else if (function.apply(EmiConfig.craftOne)) {
         destination = EmiCraftContext.Destination.NONE;
      }

      if (destination != null) {
         EmiRecipe context = contextSupplier.get();
         if (context != null) {
            if (EmiConfig.miscraftPrevention) {
               EmiScreenManager.ScreenSpace space = getHoveredSpace(lastMouseX, lastMouseY);
               if (space != null && (space.getType() == SidebarType.CRAFTABLES || space.getType() == SidebarType.CRAFT_HISTORY)) {
                  lastHoveredCraftableOffset = space.getRawOffsetFromMouse(lastMouseX, lastMouseY);
                  if (lastHoveredCraftableOffset != -1) {
                     lastHoveredCraftableSturdy = lastHoveredCraftable != null;
                     lastHoveredCraftable = stack;
                     if (!all) {
                        lastHoveredCraftableSturdy = true;
                     }
                  }
               }
            }

            int amount = all ? Integer.MAX_VALUE : 1;
            if (stack.getStack() instanceof EmiFavorite.Synthetic syn) {
               int batches = (int)syn.batches;
               if (syn.getRecipe() == null) {
                  int oc = EmiUtil.getOutputCount(context, syn.getStack());
                  if (oc > 0) {
                     batches /= oc;
                  }
               }

               amount = Math.min(amount, batches);
            }

            if (EmiRecipeFiller.performFill(context, EmiApi.getHandledScreen(), EmiCraftContext.Type.CRAFTABLE, destination, amount)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               return true;
            }
         }
      }

      return false;
   }

   public static boolean recipeInteraction(EmiRecipe recipe, Function<EmiBind, Boolean> function) {
      if (recipe == null) {
         return false;
      } else if (function.apply(EmiConfig.favorite) && recipe.getOutputs().size() > 0) {
         EmiFavorites.addFavorite(recipe.getOutputs().get(0), recipe);
         repopulatePanels(SidebarType.FAVORITES);
         return true;
      } else if (function.apply(EmiConfig.copyId)) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         client.keyboardHandler.setClipboard(recipe.getId() + "");
         return true;
      } else {
         return false;
      }
   }

   public static void toggleVisibility(boolean notify) {
      EmiConfig.enabled = !EmiConfig.enabled;
      EmiConfig.writeConfig();
      if (notify && !EmiConfig.enabled && EmiConfig.helpLevel.has(HelpLevel.VERBOSE)) {
         client.getToasts().addToast(new DisabledToast());
      }

      if (EmiConfig.enabled) {
         forceRecalculate();
      }
   }

   private static boolean give(EmiStack stack, int amount, int mode) {
      if (stack.getItemStack().isEmpty()) {
         return false;
      }

      ItemStack is = stack.getItemStack().copy();
      is.setCount(amount);
      if (mode == 1 && client.player.getAbilities().instabuild && client.screen instanceof CreativeModeInventoryScreen) {
         client.player.containerMenu.setCarried(is);
         return true;
      }

      if (EmiClient.onServer) {
         EmiNetwork.sendToServer(new CreateItemC2SPacket(mode, is));
         return true;
      }

      if (!is.isEmpty()) {
         ItemInput argument = new ItemInput(is.getItemHolder(), is.getComponentsPatch());
         String command = "give @s " + argument.serialize(client.level.registryAccess());
         command = command + " " + amount;
         if (command.length() < 256) {
            client.player.connection.sendCommand(command);
            return true;
         }
      }

      return false;
   }

   private static boolean deleteCursor(int mx, int my) {
      if (client.screen instanceof AbstractContainerScreen<?> handled) {
         ItemStack cursor = handled.getMenu().getCarried();
         EmiScreenManager.ScreenSpace space = getHoveredSpace(mx, my);
         if (!cursor.isEmpty() && space != null && space.getType() == SidebarType.INDEX) {
            handled.getMenu().setCarried(ItemStack.EMPTY);
            EmiNetwork.sendToServer(new CreateItemC2SPacket(1, ItemStack.EMPTY));
            return true;
         }
      }

      return false;
   }

   public static class ScreenSpace {
      public final StackBatcher batcher = EmiScreenManager.batchers.claim();
      private final Supplier<SidebarType> typeSupplier;
      public final int tx;
      public final int ty;
      public final int tw;
      public final int th;
      public final int pageSize;
      public final boolean rtl;
      public final int[] widths;
      public final boolean search;

      public ScreenSpace(int tx, int ty, int tw, int th, boolean rtl, List<Bounds> exclusion, Supplier<SidebarType> typeSupplier, boolean search) {
         this.tx = tx;
         this.ty = ty;
         this.tw = tw;
         this.th = th;
         this.rtl = rtl;
         this.typeSupplier = typeSupplier;
         this.search = search;
         int[] widths = new int[th];
         int pageSize = 0;

         for (int y = 0; y < th; y++) {
            int width = 0;
            int cy = ty + y * 18;

            label42:
            for (int x = 0; x < tw; x++) {
               int cx = tx + (rtl ? tw - 1 - x : x) * 18;
               int rx = cx + 18 - 1;
               int ry = cy + 18 - 1;

               for (Bounds rect : exclusion) {
                  if (rect.contains(cx, cy) || rect.contains(rx, cy) || rect.contains(cx, ry) || rect.contains(rx, ry)) {
                     break label42;
                  }
               }

               width++;
            }

            widths[y] = width;
            pageSize += width;
         }

         this.pageSize = pageSize;
         this.widths = widths;
      }

      public List<? extends EmiIngredient> getStacks() {
         return this.search && this.getType() != SidebarType.CHESS ? EmiScreenManager.searchedStacks : EmiSidebars.getStacks(this.getType());
      }

      public List<? extends EmiIngredient> getPage(int page) {
         List<? extends EmiIngredient> stacks = this.getStacks();
         int start = page * this.pageSize;
         int end = Math.min(start + this.pageSize, stacks.size());
         return end > start ? stacks.subList(start, end) : List.of();
      }

      public SidebarType getType() {
         return this.typeSupplier.get();
      }

      public void render(EmiDrawContext context, int mouseX, int mouseY, float delta, int startIndex) {
         if (this.pageSize > 0) {
            context.enableDepthTest();
            EmiPort.setPositionTexShader();
            context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            int hx = -1;
            int hy = -1;
            this.batcher.begin(0, 0, 0);
            int i = startIndex;
            List<? extends EmiIngredient> stacks = this.getStacks();
            int hovered = this.getRawOffsetFromMouse(mouseX, mouseY);
            if (hovered != -1 && EmiConfig.showHoverOverlay && startIndex + hovered < stacks.size()) {
               hx = this.getRawX(hovered);
               hy = this.getRawY(hovered);
               EmiRenderHelper.drawSlotHightlight(context, hx, hy, 18, 18, 0);
            }

            context.push();

            label50:
            for (int yo = 0; yo < this.th; yo++) {
               for (int xo = 0; xo < this.getWidth(yo); xo++) {
                  if (i >= stacks.size()) {
                     break label50;
                  }

                  int cx = this.getX(xo, yo);
                  int cy = this.getY(xo, yo);
                  EmiIngredient stack = stacks.get(i++);
                  this.batcher.render(stack, context.raw(), cx + 1, cy + 1, delta);
                  if (this.getType() == SidebarType.INDEX) {
                     if (EmiConfig.editMode && EmiHidden.isHidden(stack)) {
                        context.enableDepthTest();
                        context.fill(cx, cy, 18, 18, 872349696);
                     } else if (EmiConfig.highlightDefaulted && BoM.getRecipe(stack) != null) {
                        context.enableDepthTest();
                        context.fill(cx, cy, 18, 18, 855703296);
                     }
                  }
               }
            }

            this.batcher.draw();
            context.pop();
         }
      }

      public int getWidth(int y) {
         return this.widths[y];
      }

      public int getX(int x, int y) {
         return this.tx + (this.rtl ? x + this.tw - this.getWidth(y) : x) * 18;
      }

      public int getY(int x, int y) {
         return this.ty + y * 18;
      }

      public int getEdgeX(int off) {
         int t = 0;
         int y = 0;

         while (y < this.th && t + this.getWidth(y) < off) {
            t += this.getWidth(y++);
         }

         return this.getX(off - t, y);
      }

      public int getEdgeY(int off) {
         int t = 0;
         int y = 0;

         while (y < this.th && t + this.getWidth(y) < off) {
            t += this.getWidth(y++);
         }

         return this.ty + y * 18;
      }

      public int getRawX(int off) {
         int t = 0;
         int y = 0;

         while (y < this.th && t + this.getWidth(y) <= off) {
            t += this.getWidth(y++);
         }

         return this.getX(off - t, y);
      }

      public int getRawY(int off) {
         int t = 0;
         int y = 0;

         while (y < this.th && t + this.getWidth(y) <= off) {
            t += this.getWidth(y++);
         }

         return this.ty + y * 18;
      }

      public int getClosestEdge(int x, int y) {
         if (y < this.ty) {
            return 0;
         }

         if (y >= this.ty + this.th * 18) {
            return this.pageSize;
         }

         x = (x - this.tx) / 18;
         y = (y - this.ty) / 18;
         int off = 0;

         for (int i = 0; i < y; i++) {
            off += this.widths[i];
         }

         if (x < 0) {
            return y;
         }

         if (x >= this.widths[y]) {
            return y + this.widths[y];
         }

         if (this.rtl) {
            int to = this.tw - this.widths[y];
            if (x >= to) {
               off += x - to;
            }
         } else if (x < this.widths[y]) {
            off += x;
         } else {
            off += this.widths[y];
         }

         return off;
      }

      public int getRawOffsetFromMouse(int mouseX, int mouseY) {
         return mouseX >= this.tx && mouseY >= this.ty ? this.getRawOffset((mouseX - this.tx) / 18, (mouseY - this.ty) / 18) : -1;
      }

      public int getRawOffset(int x, int y) {
         if (x >= 0 && y >= 0 && x < this.tw && y < this.th) {
            int off = 0;

            for (int i = 0; i < y; i++) {
               off += this.widths[i];
            }

            if (this.rtl) {
               int to = this.tw - this.widths[y];
               if (x >= to) {
                  return off + x - to;
               }
            } else if (x < this.widths[y]) {
               return off + x;
            }
         }

         return -1;
      }

      public boolean contains(int x, int y) {
         return x >= this.tx && x < this.tx + this.tw * 18 && y >= this.ty && y < this.ty + this.th * 18;
      }

      public boolean containsNotExcluded(int x, int y) {
         EmiScreenBase base = EmiScreenBase.getCurrent();
         if (base.isEmpty()) {
            return false;
         }

         if (this.contains(EmiScreenManager.lastMouseX, EmiScreenManager.lastMouseY)) {
            for (Bounds bounds : EmiExclusionAreas.getExclusion(base)) {
               if (bounds.contains(x, y)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static class SidebarEmiStackInteraction extends EmiStackInteraction {
      public final EmiScreenManager.ScreenSpace space;

      public SidebarEmiStackInteraction(EmiIngredient stack, EmiScreenManager.ScreenSpace space) {
         super(stack);
         this.space = space;
      }

      public SidebarEmiStackInteraction(EmiIngredient stack, EmiScreenManager.ScreenSpace space, EmiRecipe recipe, boolean clickable) {
         super(stack, recipe, clickable);
         this.space = space;
      }

      public SidebarType getType() {
         return this.space.getType();
      }
   }

   public static class SidebarPanel {
      public final SizedButtonWidget pageLeft;
      public final SizedButtonWidget pageRight;
      public final SidebarButtonWidget cycle;
      public final SidebarPages pages;
      public final SidebarSide side;
      public List<EmiScreenManager.ScreenSpace> spaces;
      public EmiScreenManager.ScreenSpace space;
      public SidebarTheme theme;
      public boolean header;
      public int sidebarPage;
      public int page;

      public SidebarPanel(SidebarSide side, SidebarPages pages) {
         this.side = side;
         this.pages = pages;
         this.pageLeft = new SizedButtonWidget(0, 0, 16, 16, 224, 0, this::hasMultiplePages, w -> this.scroll(-1));
         this.pageRight = new SizedButtonWidget(0, 0, 16, 16, 240, 0, this::hasMultiplePages, w -> this.scroll(1));
         this.cycle = new SidebarButtonWidget(0, 0, 16, 16, this);
      }

      public void setSpaces(EmiScreenManager.ScreenSpace main, List<EmiScreenManager.ScreenSpace> subpanels) {
         this.space = main;
         this.spaces = Stream.concat(Stream.of(main), subpanels.stream()).toList();
      }

      public List<EmiScreenManager.ScreenSpace> getSpaces() {
         return this.spaces == null ? List.of() : this.spaces;
      }

      public EmiScreenManager.ScreenSpace getHoveredSpace(int mouseX, int mouseY) {
         for (EmiScreenManager.ScreenSpace space : this.getSpaces()) {
            if (space.containsNotExcluded(mouseX, mouseY)) {
               return space;
            }
         }

         return null;
      }

      public SidebarType getType() {
         return this.sidebarPage >= 0 && this.sidebarPage < this.pages.pages.size() ? this.pages.pages.get(this.sidebarPage).type : SidebarType.NONE;
      }

      public boolean supportsType(SidebarType type) {
         for (SidebarPages.SidebarPage page : this.pages.pages) {
            if (page.type == type) {
               return true;
            }
         }

         return false;
      }

      public void setSidebarPage(int page) {
         if (page != this.sidebarPage) {
            boolean forceRecalculate = this.getType() == SidebarType.CHESS;
            this.sidebarPage = page;
            forceRecalculate |= this.getType() == SidebarType.CHESS;
            if (forceRecalculate && EmiScreenManager.client.screen != null) {
               EmiScreenManager.lastWidth = -1;
               EmiScreenManager.recalculate();
            }

            if (this.isSearch()) {
               EmiSearch.search(EmiScreenManager.search.getValue());
            }

            if (this.space != null) {
               this.space.batcher.repopulate();
            }
         }
      }

      public void setType(SidebarType type) {
         for (int i = 0; i < this.pages.pages.size(); i++) {
            SidebarPages.SidebarPage page = this.pages.pages.get(i);
            if (page.type == type) {
               this.setSidebarPage(i);
            }
         }
      }

      public void cycleType(int amount) {
         int page = this.sidebarPage + amount;
         if (page >= this.pages.pages.size()) {
            page = 0;
         } else if (page < 0) {
            page = Math.max(this.pages.pages.size() - 1, 0);
         }

         this.setSidebarPage(page);
      }

      public void render(EmiDrawContext context, int mouseX, int mouseY, float delta) {
         if (this.space != null) {
            try {
               this.cycleType(0);
               if (this.getType() == SidebarType.CHESS) {
                  EmiChess.get().update();
                  if (this.space.tw != 8 || this.space.th != 8) {
                     this.cycleType(1);
                  }
               }

               if (this.isVisible()) {
                  EmiProfiler.swap(this.side.getName());
                  context.push();
                  context.matrices().translate(0.0F, 0.0F, 100.0F);
                  this.pageLeft.render(context.raw(), mouseX, mouseY, delta);
                  this.cycle.render(context.raw(), mouseX, mouseY, delta);
                  this.pageRight.render(context.raw(), mouseX, mouseY, delta);
                  context.pop();
                  int totalPages = (this.space.getStacks().size() - 1) / this.space.pageSize + 1;
                  this.wrapPage();
                  this.drawHeader(context, mouseX, mouseY, delta, this.page, totalPages);

                  for (EmiScreenManager.ScreenSpace space : this.getSpaces()) {
                     if (space == this.space) {
                        space.render(context, mouseX, mouseY, delta, space.pageSize * this.page);
                     } else {
                        space.render(context, mouseX, mouseY, delta, 0);
                     }
                  }
               }
            } catch (Exception e) {
               EmiLog.error("Error rendering sidebar", e);
               this.drawError(context, mouseX, mouseY, delta);
            }
         }
      }

      private void drawBackground(EmiDrawContext context, int mouseX, int mouseY, float delta) {
         if (this.space != null) {
            try {
               this.cycleType(0);
               if (this.getType() == SidebarType.CHESS && (this.space.tw != 8 || this.space.th != 8)) {
                  this.cycleType(1);
               }

               if (this.isVisible()) {
                  context.enableDepthTest();
                  context.resetColor();
                  int headerOffset = this.header ? 18 : 0;
                  if (this.theme != SidebarTheme.VANILLA) {
                     if (this.theme == SidebarTheme.MODERN) {
                        int offset = 2;

                        for (EmiScreenManager.ScreenSpace space : this.getSpaces()) {
                           context.enableBlend();
                           context.drawTexture(
                              EmiRenderHelper.GRID, space.tx, space.ty, space.tw * 18, space.th * 18, 0.0F, 0.0F, space.tw, space.th, 2, offset
                           );
                           if (space.th % 2 == 1) {
                              offset *= -1;
                           }

                           context.disableBlend();
                        }
                     }
                  } else {
                     int totalHeight = 18 + headerOffset;

                     for (EmiScreenManager.ScreenSpace space : this.getSpaces()) {
                        totalHeight += space.th * 18 + 3;
                     }

                     EmiRenderHelper.drawNinePatch(
                        context,
                        EmiRenderHelper.BACKGROUND,
                        this.space.tx - 9,
                        this.space.ty - 9 - headerOffset,
                        this.space.tw * 18 + 18,
                        totalHeight,
                        0,
                        32,
                        8,
                        1
                     );
                  }

                  for (EmiScreenManager.ScreenSpace space : this.getSpaces()) {
                     if (space != this.space) {
                        context.drawTexture(EmiRenderHelper.DASH, space.tx + 1, space.ty - 2, space.tw * 18, 1, 0.0F, 0.0F, space.tw * 18, 1, 6, 1);
                     }
                  }
               }
            } catch (Exception e) {
               EmiLog.error("Error rendering sidebar background", e);
               this.drawError(context, mouseX, mouseY, delta);
            }
         }
      }

      private void drawError(EmiDrawContext context, int mouseX, int mouseY, float delta) {
         try {
            Bounds bounds = this.getBounds();
            int color = -65536;
            int border = 4;
            context.fill(bounds.left(), bounds.top(), bounds.width(), border, color);
            context.fill(bounds.left(), bounds.bottom() - border, bounds.width(), border, color);
            context.fill(bounds.left(), bounds.top(), border, bounds.height(), color);
            context.fill(bounds.right() - border, bounds.top(), border, bounds.height(), color);
            int cx = (bounds.left() + bounds.right()) / 2;
            int cy = (bounds.top() + bounds.bottom()) / 2;
            context.drawCenteredText(EmiPort.literal("Render Error"), cx, cy - 6, color);
            context.drawCenteredText(EmiPort.literal("See Log"), cx, cy + 6, color);
         } catch (Exception e) {
            EmiLog.error("Critical error rendering sidebar", e);
         }
      }

      private void drawHeader(EmiDrawContext context, int mouseX, int mouseY, float delta, int page, int totalPages) {
         if (this.header) {
            Component text = EmiRenderHelper.getPageText(page + 1, totalPages, (this.space.tw - 3) * 18);
            int x = this.space.tx + this.space.tw * 18 / 2;
            int maxLeft = (this.space.tw - 2) * 18 / 2 - 18;
            int w = EmiScreenManager.client.font.width(text) / 2;
            if (w > maxLeft) {
               x += w - maxLeft;
            }

            context.drawCenteredText(text, x, this.space.ty - 15);
            if (totalPages > 1 && this.space.tw > 2) {
               int scrollLeft = this.space.tx + 18;
               int scrollWidth = this.space.tw * 18 - 36;
               int scrollY = this.space.ty - 4;
               context.fill(scrollLeft, scrollY, scrollWidth, 2, 1431655765);
               EmiRenderHelper.drawScroll(context, scrollLeft, scrollY, scrollWidth, 2, page, totalPages, -1);
            }
         }
      }

      private void wrapPage() {
         int totalPages = (this.space.getStacks().size() - 1) / this.space.pageSize + 1;
         if (this.page >= totalPages) {
            this.page = 0;
            this.space.batcher.repopulate();
         } else if (this.page < 0) {
            this.page = totalPages - 1;
            this.space.batcher.repopulate();
         }
      }

      public boolean isSearch() {
         return this.side == EmiConfig.searchSidebar;
      }

      public void updateWidgetPosition() {
         if (this.space != null) {
            this.pageLeft.x = this.space.tx;
            this.pageLeft.y = this.space.ty - 18;
            this.pageRight.x = this.space.tx + this.space.tw * 18 - 16;
            this.pageRight.y = this.pageLeft.y;
            this.cycle.x = this.space.tx + 18;
            this.cycle.y = this.pageLeft.y - 1;
         }
      }

      public boolean isVisible() {
         if (this.space == null) {
            return false;
         } else {
            return this.getType() != SidebarType.CHESS || this.space.tw == 8 && this.space.th == 8
               ? !EmiScreenManager.isDisabled() && this.space.pageSize > 0 && this.pages.pages.size() > 0
               : false;
         }
      }

      public void updateWidgetVisibility() {
         boolean visible = this.header && this.isVisible();
         this.pageLeft.visible = visible;
         this.cycle.visible = visible;
         this.pageRight.visible = visible;
      }

      public boolean hasMultiplePages() {
         return this.space != null && this.space.getStacks().size() > this.space.pageSize;
      }

      public void scroll(int delta) {
         if (this.space != null) {
            if (this.space.pageSize != 0) {
               this.page += delta;
               int pageSize = this.space.pageSize;
               int totalPages = (this.space.getStacks().size() - 1) / pageSize + 1;
               if (totalPages > 1) {
                  if (this.page >= totalPages) {
                     this.page = 0;
                  } else if (this.page < 0) {
                     this.page = totalPages - 1;
                  }

                  this.space.batcher.repopulate();
               }
            }
         }
      }

      public Bounds getBounds() {
         List<EmiScreenManager.ScreenSpace> spaces = this.getSpaces();
         if (this.space != null && !spaces.isEmpty()) {
            int headerOffset = this.header ? 18 : 0;
            EmiScreenManager.ScreenSpace end = this.getSpaces().get(this.getSpaces().size() - 1);
            return new Bounds(
               this.space.tx - this.theme.horizontalPadding,
               this.space.ty - this.theme.verticalPadding - headerOffset,
               this.space.tw * 18 + this.theme.horizontalPadding * 2,
               end.ty - this.space.ty + end.th * 18 + this.theme.verticalPadding * 2 + headerOffset
            );
         } else {
            return Bounds.EMPTY;
         }
      }
   }
}
