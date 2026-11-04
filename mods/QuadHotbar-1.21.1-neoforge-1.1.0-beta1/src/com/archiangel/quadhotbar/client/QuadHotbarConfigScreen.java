package com.archiangel.quadhotbar.client;

import com.archiangel.quadhotbar.QuadHotbarConfig;
import com.archiangel.quadhotbar.QuadHotbarLayout;
import com.archiangel.quadhotbar.QuadHotbarPages;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class QuadHotbarConfigScreen extends Screen {
   private final Screen parent;

   private static Component profileTitle(String key) {
      return Component.translatable(
         "quadhotbar.config.profile_title",
         new Object[]{
            Component.translatable(key),
            Component.translatable(QuadHotbarConfig.multiplayerContext() ? "quadhotbar.config.profile.multiplayer" : "quadhotbar.config.profile.singleplayer")
         }
      );
   }

   public QuadHotbarConfigScreen(Screen parent) {
      super(profileTitle("quadhotbar.config.title"));
      this.parent = parent;
   }

   protected void init() {
      int size = Math.min(220, this.width - 40);
      this.addRenderableWidget(
         Button.builder(
               Component.translatable("quadhotbar.config.client_settings"),
               b -> this.minecraft.setScreen(new QuadHotbarConfigScreen.GeneralSettingsScreen(this))
            )
            .bounds((this.width - size) / 2, 54, size, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.done"), b -> this.onClose()).bounds((this.width - size) / 2, this.height - 29, size, 20).build()
      );
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void render(GuiGraphics graphics, int x, int y, float tick) {
      super.render(graphics, x, y, tick);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
   }

   private static final class GeneralSettingsScreen extends Screen {
      private static long warningUntil;
      private final Screen parent;
      private final List<QuadHotbarConfigScreen.GeneralSettingsScreen.Setting> settings = new ArrayList<>();
      private final List<QuadHotbarConfigScreen.GeneralSettingsScreen.Section> sections = new ArrayList<>();
      private Button done;
      private int labelX;
      private int labelWidth;
      private int valueX;
      private int valueWidth;
      private int resetX;
      private int contentHeight;
      private double scroll;

      private GeneralSettingsScreen(Screen parent) {
         super(QuadHotbarConfigScreen.profileTitle("quadhotbar.config.client_settings"));
         this.parent = parent;
      }

      protected void init() {
         this.settings.clear();
         this.sections.clear();
         this.contentHeight = 0;
         int contentWidth = Math.min(480, this.width - 28);
         this.labelX = (this.width - contentWidth) / 2;
         this.valueWidth = Math.min(150, Math.max(96, contentWidth / 3));
         this.resetX = this.labelX + contentWidth - 46;
         this.valueX = this.resetX - this.valueWidth - 4;
         this.labelWidth = Math.max(0, this.valueX - this.labelX - 10);
         this.toggle(
            "quadhotbar.config.enabled.description",
            () -> QuadHotbarConfig.enabled,
            v -> QuadHotbarClientEvents.setModEnabled(this.minecraft, v),
            true,
            () -> true
         );
         this.button(
            "quadhotbar.config.hotbar_rows.description",
            () -> Component.translatable("quadhotbar.config.hotbar_rows.value", new Object[]{displayedRows()}),
            this::changeRows,
            () -> QuadHotbarConfig.setHotbarRows(2),
            () -> QuadHotbarConfig.hotbarRows == 2,
            () -> true
         );
         this.toggle(
            "quadhotbar.config.scroll_between_hotbars.description",
            () -> QuadHotbarConfig.scrollBetweenHotbars,
            QuadHotbarConfig::setScrollBetweenHotbars,
            true,
            () -> true
         );
         this.toggle("quadhotbar.config.show_all_keybinds", () -> QuadHotbarConfig.showAllKeybinds, QuadHotbarConfig::setShowAllKeybinds, false, () -> true);
         this.button(
               "quadhotbar.layout.order",
               () -> Component.translatable(QuadHotbarConfig.inventoryRowOrder.translationKey()),
               d -> QuadHotbarConfig.setInventoryRowOrder(
                  QuadHotbarConfig.inventoryRowOrder == QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM
                     ? QuadHotbarLayout.InventoryOrder.BOTTOM_TO_TOP
                     : QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM
               ),
               () -> QuadHotbarConfig.setInventoryRowOrder(QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM),
               () -> QuadHotbarConfig.inventoryRowOrder == QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM,
               () -> true
            )
            .value
            .setTooltip(Tooltip.create(Component.translatable("quadhotbar.layout.order_hint")));
         this.button(
               "quadhotbar.layout.corner",
               () -> Component.translatable(QuadHotbarConfig.mainHotbarCorner.translationKey()),
               d -> {
                  QuadHotbarLayout.MainCorner[] corners = QuadHotbarLayout.MainCorner.values();
                  QuadHotbarConfig.setMainHotbarCorner(corners[Math.floorMod(QuadHotbarConfig.mainHotbarCorner.ordinal() + d, corners.length)]);
               },
               () -> QuadHotbarConfig.setMainHotbarCorner(QuadHotbarLayout.MainCorner.BOTTOM_LEFT),
               () -> QuadHotbarConfig.mainHotbarCorner == QuadHotbarLayout.MainCorner.BOTTOM_LEFT,
               () -> true
            )
            .value
            .setTooltip(Tooltip.create(Component.translatable("quadhotbar.layout.corner_hint")));
         this.section("quadhotbar.config.experiments.title");
         this.toggle("quadhotbar.config.experiments.description", () -> QuadHotbarConfig.experimentsEnabled && freedomAllowed(), v -> {
            QuadHotbarConfig.setExperimentsEnabled(v);
            warningUntil = v ? System.nanoTime() + 4000000000L : 0L;
         }, false, QuadHotbarConfigScreen.GeneralSettingsScreen::freedomAllowed);
         this.number(
            "quadhotbar.config.inventory_pages.description",
            () -> available().pages(),
            this::setPages,
            () -> QuadHotbarPages.pageStep(available().rows()),
            QuadHotbarConfigScreen.GeneralSettingsScreen::maxPages,
            () -> QuadHotbarPages.pageStep(available().rows()),
            QuadHotbarConfigScreen.GeneralSettingsScreen::freedom
         );
         this.toggle(
            "quadhotbar.config.page_buttons.description",
            () -> QuadHotbarConfig.showPageButtons,
            QuadHotbarConfig::setShowPageButtons,
            true,
            QuadHotbarConfigScreen.GeneralSettingsScreen::freedom
         );
         this.toggle(
            "quadhotbar.config.page_labels.description",
            () -> QuadHotbarConfig.alwaysShowPageLabels,
            QuadHotbarConfig::setAlwaysShowPageLabels,
            false,
            QuadHotbarConfigScreen.GeneralSettingsScreen::freedom
         );
         this.section("quadhotbar.overview.settings", QuadHotbarConfigScreen.GeneralSettingsScreen::overviewPermitted);
         this.toggle(
            "quadhotbar.overview.enabled",
            () -> QuadHotbarConfig.overviewEnabled,
            QuadHotbarConfig::setOverviewEnabled,
            true,
            QuadHotbarConfigScreen.GeneralSettingsScreen::overviewPermitted
         );
         this.toggle(
            "quadhotbar.overview.button",
            () -> QuadHotbarConfig.showOverviewButton,
            QuadHotbarConfig::setShowOverviewButton,
            true,
            () -> overviewAllowed() && (!server() || QuadHotbarClientEvents.serverPolicy().showOverviewButton())
         );
         this.button(
            "quadhotbar.overview.width",
            () -> Component.literal(Integer.toString(QuadHotbarOverviewSizing.effectiveWidth())),
            QuadHotbarConfigScreen.GeneralSettingsScreen::changeWidth,
            () -> QuadHotbarConfig.setOverviewWidth(9),
            () -> QuadHotbarConfig.overviewWidth == 9,
            () -> overviewAllowed() && maxWidth() > 9
         );
         this.button(
            "quadhotbar.overview.first_page_side",
            () -> Component.translatable(QuadHotbarConfig.overviewFirstPageRight ? "quadhotbar.overview.right" : "quadhotbar.overview.left"),
            d -> QuadHotbarConfig.setOverviewFirstPageRight(!QuadHotbarConfig.overviewFirstPageRight),
            () -> QuadHotbarConfig.setOverviewFirstPageRight(false),
            () -> !QuadHotbarConfig.overviewFirstPageRight,
            () -> overviewAllowed() && QuadHotbarOverviewSizing.effectiveWidth() > 9
         );
         this.button(
            "quadhotbar.overview.page_order",
            () -> Component.translatable(QuadHotbarConfig.overviewHorizontal ? "quadhotbar.overview.horizontal" : "quadhotbar.overview.vertical"),
            d -> QuadHotbarConfig.setOverviewHorizontal(!QuadHotbarConfig.overviewHorizontal),
            () -> QuadHotbarConfig.setOverviewHorizontal(true),
            () -> QuadHotbarConfig.overviewHorizontal,
            () -> overviewAllowed() && QuadHotbarOverviewSizing.effectiveWidth() > 9
         );
         this.toggle(
            "quadhotbar.overview.auto_open",
            () -> QuadHotbarConfig.autoOpenOverview,
            QuadHotbarConfig::setAutoOpenOverview,
            true,
            QuadHotbarConfigScreen.GeneralSettingsScreen::overviewAllowed
         );
         this.number(
            "quadhotbar.overview.height",
            () -> Math.min(maxHeight(), QuadHotbarConfig.overviewHeight),
            QuadHotbarConfig::setOverviewHeight,
            () -> 1,
            QuadHotbarConfigScreen.GeneralSettingsScreen::maxHeight,
            () -> Math.min(9, maxHeight()),
            QuadHotbarConfigScreen.GeneralSettingsScreen::overviewAllowed
         );
         this.toggle(
            "quadhotbar.overview.draggable",
            () -> QuadHotbarConfig.overviewDraggable,
            QuadHotbarConfig::setOverviewDraggable,
            true,
            QuadHotbarConfigScreen.GeneralSettingsScreen::overviewAllowed
         );
         this.number(
            "quadhotbar.overview.scroll_step",
            () -> QuadHotbarConfig.overviewScrollStep,
            QuadHotbarConfig::setOverviewScrollStep,
            () -> 1,
            () -> 180,
            () -> 20,
            QuadHotbarConfigScreen.GeneralSettingsScreen::overviewAllowed
         );
         this.button(
               "quadhotbar.overview.position",
               () -> Component.translatable(customPosition() ? "quadhotbar.overview.custom" : "quadhotbar.overview.automatic"),
               d -> QuadHotbarConfig.setOverviewPosition(customPosition() ? -1 : Math.max(0, this.width - 196), customPosition() ? -1 : 36),
               () -> QuadHotbarConfig.setOverviewPosition(-1, -1),
               () -> !customPosition(),
               QuadHotbarConfigScreen.GeneralSettingsScreen::overviewAllowed
            )
            .value
            .setTooltip(Tooltip.create(Component.translatable("quadhotbar.overview.drag_hint")));
         this.number(
            "quadhotbar.overview.position_x",
            () -> Math.max(0, QuadHotbarConfig.overviewX),
            v -> QuadHotbarConfig.setOverviewPosition(v, QuadHotbarConfig.overviewY),
            () -> 0,
            () -> 10000,
            () -> 0,
            () -> overviewAllowed() && customPosition()
         );
         this.number(
            "quadhotbar.overview.position_y",
            () -> Math.max(0, QuadHotbarConfig.overviewY),
            v -> QuadHotbarConfig.setOverviewPosition(QuadHotbarConfig.overviewX, v),
            () -> 0,
            () -> 10000,
            () -> 0,
            () -> overviewAllowed() && customPosition()
         );
         this.done = (Button)this.addRenderableWidget(
            Button.builder(Component.translatable("gui.done"), b -> this.onClose()).bounds((this.width - 180) / 2, this.height - 27, 180, 20).build()
         );
         this.refresh();
      }

      private void section(String key) {
         this.section(key, () -> true);
      }

      private void section(String key, BooleanSupplier allowed) {
         this.sections.add(new QuadHotbarConfigScreen.GeneralSettingsScreen.Section(key, this.contentHeight, allowed));
         this.contentHeight += 30;
      }

      private QuadHotbarConfigScreen.GeneralSettingsScreen.Setting button(
         String key, Supplier<Component> message, IntConsumer change, Runnable reset, BooleanSupplier isDefault, BooleanSupplier allowed
      ) {
         QuadHotbarConfigScreen.GeneralSettingsScreen.Setting s = new QuadHotbarConfigScreen.GeneralSettingsScreen.Setting(
            key, this.contentHeight, allowed, isDefault
         );
         this.settings.add(s);
         this.contentHeight += 26;
         s.change = change;
         s.message = message;
         s.value = (Button)this.addRenderableWidget(
            Button.builder(message.get(), b -> this.apply(() -> change.accept(1))).bounds(this.valueX, 0, this.valueWidth, 20).build()
         );
         s.widgets.add(s.value);
         this.reset(s, reset);
         return s;
      }

      private void toggle(String key, BooleanSupplier get, Consumer<Boolean> set, boolean defaultValue, BooleanSupplier allowed) {
         this.button(
            key,
            () -> Component.translatable(get.getAsBoolean() ? "quadhotbar.config.on" : "quadhotbar.config.off"),
            d -> set.accept(!get.getAsBoolean()),
            () -> set.accept(defaultValue),
            () -> get.getAsBoolean() == defaultValue,
            allowed
         );
      }

      private void number(
         String key, IntSupplier get, IntConsumer set, IntSupplier minimum, IntSupplier maximum, IntSupplier defaultValue, BooleanSupplier allowed
      ) {
         QuadHotbarConfigScreen.GeneralSettingsScreen.Setting s = new QuadHotbarConfigScreen.GeneralSettingsScreen.Setting(
            key, this.contentHeight, allowed, () -> get.getAsInt() == defaultValue.getAsInt()
         );
         this.settings.add(s);
         this.contentHeight += 26;
         s.get = get;
         s.set = v -> set.accept(Mth.clamp(v, minimum.getAsInt(), maximum.getAsInt()));
         s.minimum = minimum;
         s.maximum = maximum;
         s.previous = (Button)this.addRenderableWidget(
            Button.builder(Component.literal("<"), b -> this.apply(() -> s.set.accept(get.getAsInt() - this.step(key)))).bounds(this.valueX, 0, 20, 20).build()
         );
         s.field = (EditBox)this.addRenderableWidget(new EditBox(this.font, this.valueX + 24, 0, this.valueWidth - 48, 20, Component.translatable(key)));
         s.field.setMaxLength(5);
         s.field.setFilter(text -> text.matches("[0-9]*"));
         s.next = (Button)this.addRenderableWidget(
            Button.builder(Component.literal(">"), b -> this.apply(() -> s.set.accept(get.getAsInt() + this.step(key))))
               .bounds(this.valueX + this.valueWidth - 20, 0, 20, 20)
               .build()
         );
         s.widgets.addAll(List.of(s.previous, s.field, s.next));
         this.reset(s, () -> s.set.accept(defaultValue.getAsInt()));
      }

      private int step(String key) {
         return key.equals("quadhotbar.config.inventory_pages.description") ? QuadHotbarPages.pageStep(available().rows()) : 1;
      }

      private void reset(QuadHotbarConfigScreen.GeneralSettingsScreen.Setting s, Runnable action) {
         s.reset = (Button)this.addRenderableWidget(
            Button.builder(Component.translatable("quadhotbar.config.reset"), b -> this.apply(action)).bounds(this.resetX, 0, 46, 20).build()
         );
         s.widgets.add(s.reset);
         String tooltipKey = !s.key.equals("quadhotbar.overview.auto_open")
               && !s.key.equals("quadhotbar.overview.width")
               && !s.key.equals("quadhotbar.overview.page_order")
               && !s.key.equals("quadhotbar.overview.first_page_side")
            ? s.key
            : s.key + ".description";

         for (AbstractWidget widget : s.widgets) {
            widget.setTooltip(Tooltip.create(Component.translatable(tooltipKey)));
         }
      }

      private void apply(Runnable action) {
         this.commitInputs();
         action.run();
         this.save();
      }

      private void save() {
         QuadHotbarConfig.save();
         QuadHotbarClientEvents.syncPageSettings();
         this.refresh();
      }

      private void commitInputs() {
         for (QuadHotbarConfigScreen.GeneralSettingsScreen.Setting s : this.settings) {
            if (s.field != null && s.field.isFocused()) {
               if (s.allowed.getAsBoolean() && !s.field.getValue().isEmpty() && !s.field.getValue().equals(s.displayedInput)) {
                  int entered = Integer.parseInt(s.field.getValue());
                  s.set.accept(entered);
               }

               s.field.setFocused(false);
            }
         }

         this.setFocused(null);
      }

      private void refresh() {
         this.scroll = Mth.clamp(this.scroll, 0.0, Math.max(0, this.contentHeight - (this.height - 74)));

         for (QuadHotbarConfigScreen.GeneralSettingsScreen.Setting s : this.settings) {
            int y = 40 + s.y - (int)this.scroll;
            boolean visible = y >= 36 && y + 20 <= this.height - 34;
            boolean enabled = s.allowed.getAsBoolean();

            for (AbstractWidget widget : s.widgets) {
               widget.setY(y);
               widget.visible = visible;
               widget.active = enabled;
            }

            s.reset.active = enabled && !s.isDefault.getAsBoolean();
            if (s.value != null) {
               s.value.setMessage(s.message.get());
            }

            if (s.field != null) {
               s.field.setEditable(enabled);
               if (!s.field.isFocused()) {
                  s.displayedInput = Integer.toString(s.get.getAsInt());
                  s.field.setValue(s.displayedInput);
               }

               s.previous.active = enabled && s.get.getAsInt() > s.minimum.getAsInt();
               s.next.active = enabled && s.get.getAsInt() < s.maximum.getAsInt();
            }
         }
      }

      private void changeRows(int d) {
         if (!freedom() && QuadHotbarConfig.hotbarRows > maxRows()) {
            QuadHotbarConfig.setHotbarRows(maxRows());
         } else {
            int next = available().rows() + d;

            while (next >= 2 && next <= maxRows() && QuadHotbarPages.pageStep(next) > maxPages()) {
               next += d;
            }

            if (next < 2 || next > maxRows()) {
               next = d > 0 ? 2 : maxRows();
            }

            QuadHotbarConfig.setHotbarRows(next);
         }
      }

      private static int displayedRows() {
         return !QuadHotbarConfig.experimentsEnabled && QuadHotbarConfig.hotbarRows > 4 ? QuadHotbarConfig.hotbarRows : available().rows();
      }

      private void setPages(int pages) {
         QuadHotbarConfig.setInventoryPages(Math.min(pages, maxPages()));
      }

      private static boolean server() {
         return QuadHotbarClientEvents.hasModdedServer();
      }

      private static boolean freedomAllowed() {
         return !server() || QuadHotbarClientEvents.serverPolicy().allowFreedom();
      }

      private static boolean freedom() {
         return QuadHotbarConfig.experimentsEnabled && freedomAllowed();
      }

      private static boolean overviewAllowed() {
         return QuadHotbarConfig.overviewEnabled && overviewPermitted();
      }

      private static boolean overviewPermitted() {
         return freedom() && available().pages() > 1 && (!server() || QuadHotbarClientEvents.serverPolicy().allowPageOverview());
      }

      private static int maxPages() {
         return server() ? QuadHotbarClientEvents.serverPolicy().maxInventoryPages() : 100;
      }

      private static int maxRows() {
         int rows = QuadHotbarConfig.experimentsEnabled ? 8 : 4;
         if (server()) {
            rows = Math.min(rows, QuadHotbarClientEvents.serverPolicy().maxHotbarRows());
         }

         return QuadHotbarPages.limitSettings(maxPages(), rows, maxPages(), rows).rows();
      }

      private static QuadHotbarPages.PageSettings available() {
         return QuadHotbarPages.limitSettings(QuadHotbarConfig.inventoryPages, QuadHotbarConfig.hotbarRows, maxPages(), maxRows());
      }

      private static int maxHeight() {
         return server() ? QuadHotbarClientEvents.serverPolicy().maxOverviewHeight() : 50;
      }

      private static int maxWidth() {
         return QuadHotbarOverviewSizing.maxWidth();
      }

      private static void changeWidth(int direction) {
         int choices = maxWidth() / 9;
         int current = QuadHotbarOverviewSizing.effectiveWidth() / 9 - 1;
         QuadHotbarConfig.setOverviewWidth((Math.floorMod(current + direction, choices) + 1) * 9);
      }

      private static boolean customPosition() {
         return QuadHotbarConfig.overviewX >= 0 || QuadHotbarConfig.overviewY >= 0;
      }

      public boolean mouseClicked(double x, double y, int b) {
         for (QuadHotbarConfigScreen.GeneralSettingsScreen.Setting s : this.settings) {
            if (s.field != null && s.field.isFocused() && !s.field.isMouseOver(x, y)) {
               this.commitInputs();
               this.save();
               break;
            }
         }

         if (b == 1) {
            for (QuadHotbarConfigScreen.GeneralSettingsScreen.Setting s : this.settings) {
               if (s.value != null && s.value.visible && s.value.active && s.value.isMouseOver(x, y)) {
                  this.apply(() -> s.change.accept(-1));
                  return true;
               }
            }
         }

         return super.mouseClicked(x, y, b);
      }

      public boolean mouseScrolled(double x, double y, double h, double v) {
         this.commitInputs();
         this.save();
         this.scroll -= v * 26.0;
         this.refresh();
         return true;
      }

      public boolean keyPressed(int key, int scanCode, int modifiers) {
         if ((key == 257 || key == 335 || key == 258) && this.settings.stream().anyMatch(s -> s.field != null && s.field.isFocused())) {
            this.commitInputs();
            this.save();
            if (key != 258) {
               return true;
            }
         }

         if (key != 267 && key != 266) {
            return super.keyPressed(key, scanCode, modifiers);
         }

         this.commitInputs();
         this.save();
         this.scroll = this.scroll + (key == 267 ? 1 : -1) * (this.height - 74);
         this.refresh();
         return true;
      }

      public void onClose() {
         this.commitInputs();
         this.save();
         this.minecraft.setScreen(this.parent);
      }

      public void render(GuiGraphics graphics, int x, int y, float tick) {
         this.renderBackground(graphics, x, y, tick);
         this.refresh();
         QuadHotbarVanillaUi.optionsList(graphics, this.width, 36, this.height - 34, (int)this.scroll);
         graphics.enableScissor(0, 36, this.width, this.height - 34);

         for (QuadHotbarConfigScreen.GeneralSettingsScreen.Section s : this.sections) {
            int rowY = 40 + s.y - (int)this.scroll;
            graphics.drawString(this.font, Component.translatable(s.key), this.labelX, rowY + 4, s.allowed.getAsBoolean() ? 16777215 : 10066329);
            graphics.hLine(this.labelX, this.resetX + 46, rowY + 19, -8355712);
         }

         for (QuadHotbarConfigScreen.GeneralSettingsScreen.Setting s : this.settings) {
            int rowY = 40 + s.y - (int)this.scroll;
            if (rowY >= 36 && rowY + 20 <= this.height - 34) {
               graphics.drawString(
                  this.font,
                  this.font.plainSubstrByWidth(Component.translatable(s.key).getString(), this.labelWidth),
                  this.labelX,
                  rowY + 6,
                  s.allowed.getAsBoolean() ? 16777215 : 10066329,
                  true
               );

               for (AbstractWidget widget : s.widgets) {
                  widget.render(graphics, x, y, tick);
               }
            }
         }

         graphics.disableScissor();
         if (this.contentHeight > this.height - 74) {
            int trackHeight = this.height - 74;
            int knobHeight = Math.max(12, trackHeight * trackHeight / this.contentHeight);
            int knobY = 38 + (int)(this.scroll / Math.max(1, this.contentHeight - trackHeight) * (trackHeight - knobHeight));
            QuadHotbarVanillaUi.scrollbar(graphics, this.width - 8, 38, trackHeight, knobY, knobHeight);
         }

         graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
         this.done.render(graphics, x, y, tick);
         int color = QuadHotbarFade.color(warningUntil, 16733525);
         if (QuadHotbarConfig.experimentsEnabled && color != 0) {
            List<FormattedCharSequence> lines = this.font.split(Component.translatable("quadhotbar.config.experiments.warning"), this.width - 32);
            int warningY = this.height - 37 - lines.size() * 10;
            graphics.fill(8, warningY - 3, this.width - 8, this.height - 34, color & 0xFF000000 | 1381653);

            for (int i = 0; i < lines.size(); i++) {
               graphics.drawCenteredString(this.font, lines.get(i), this.width / 2, warningY + i * 10, color);
            }
         }
      }

      private record Section(String key, int y, BooleanSupplier allowed) {
      }

      private static final class Setting {
         final String key;
         final int y;
         final BooleanSupplier allowed;
         final BooleanSupplier isDefault;
         final List<AbstractWidget> widgets = new ArrayList<>();
         Button value;
         Button reset;
         Button previous;
         Button next;
         EditBox field;
         String displayedInput;
         Supplier<Component> message;
         IntConsumer change;
         IntConsumer set;
         IntSupplier get;
         IntSupplier minimum;
         IntSupplier maximum;

         Setting(String key, int y, BooleanSupplier allowed, BooleanSupplier isDefault) {
            this.key = key;
            this.y = y;
            this.allowed = allowed;
            this.isDefault = isDefault;
         }
      }
   }
}
