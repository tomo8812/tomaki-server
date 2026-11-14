package dev.diorama;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class RadarCategoryScreen extends Screen {
   private static final int WIDTH = 260;
   private static final int ROW = 24;
   private static final int LABEL = -6250336;
   private final Screen parent;
   private final RadarCategories.Entry entry;
   private final RadarCategories.@Nullable Category own;
   private @Nullable EditBox name;
   private boolean armed;

   public RadarCategoryScreen(Screen parent, RadarCategories.Entry entry) {
      super(entry.name());
      this.parent = parent;
      this.entry = entry;
      this.own = entry instanceof RadarCategories.Category category ? category : null;
   }

   private int rows() {
      return 3 + (this.own != null ? 2 : 0);
   }

   private int top() {
      return Math.max(34, this.height / 2 - (this.rows() * 24 + 30) / 2);
   }

   protected void init() {
      Settings settings = Settings.get();
      int left = this.width / 2 - 130;
      int y = this.top();
      RadarCategories.Category own = this.own;
      if (own != null) {
         String typed = this.name != null ? this.name.getValue() : (own.name == null ? "" : own.name);
         EditBox box = new EditBox(this.font, left + 60, y, 200, 20, Component.translatable("diorama.waypoint.name"));
         box.setMaxLength(32);
         box.setValue(typed);
         box.setHint(own.name().copy().withStyle(ChatFormatting.DARK_GRAY));
         box.setResponder(text -> {
            own.name = text.isBlank() ? null : text.trim();
            settings.save();
         });
         this.name = (EditBox)this.addRenderableWidget(box);
         this.setInitialFocus(box);
         y += 24;
      }

      Checkbox shown = SettingsScreen.checkbox(Component.translatable("diorama.radar_category.shown"), this.font, this.entry.shown(), 260, this.entry::shown);
      shown.setPosition(left, y);
      shown.setTooltip(Tooltip.create(Component.translatable("diorama.radar_category.shown.tooltip")));
      this.addRenderableWidget(shown);
      y += 24;
      int steps = 7;
      int scale = this.entry.iconScale();
      int step = scale == 0 ? 0 : 1 + (Settings.snapScale(scale) - 50) / 25;
      SettingsScreen.StepSlider size = new SettingsScreen.StepSlider(
         Component.translatable("diorama.settings.icon_size"),
         step,
         steps + 1,
         i -> this.entry.iconScale(i == 0 ? 0 : 50 + (i - 1) * 25),
         i -> i == 0
            ? Component.translatable("diorama.radar_category.same_size", new Object[]{settings.radarIconScale + "%"})
            : Component.literal(50 + (i - 1) * 25 + "%")
      );
      size.setPosition(left, y);
      size.setWidth(260);
      size.setTooltip(Tooltip.create(Component.translatable("diorama.radar_category.icon_size.tooltip")));
      this.addRenderableWidget(size);
      y += 24;
      if (this.entry.hasNames()) {
         Settings.NameTags[] values = Settings.NameTags.values();
         Component label = Component.translatable("diorama.radar_category.names");
         this.addRenderableWidget(Button.builder(SettingsScreen.label(label, namesValue(this.entry.names())), b -> {
            Settings.NameTags next = values[(this.entry.names().ordinal() + 1) % values.length];
            this.entry.names(next);
            settings.save();
            b.setMessage(SettingsScreen.label(label, namesValue(next)));
         }).bounds(left, y, 260, 20).tooltip(Tooltip.create(Component.translatable("diorama.radar_category.names.tooltip"))).build());
      }

      y += 24;
      if (own != null) {
         this.addRenderableWidget(
            Button.builder(
                  SettingsScreen.label(Component.translatable("diorama.radar_category.mobs"), Component.literal(Integer.toString(own.mobs.size()))),
                  b -> this.minecraft.setScreen(new RadarMobsScreen(this, own))
               )
               .bounds(left, y, 260, 20)
               .tooltip(Tooltip.create(Component.translatable("diorama.radar_category.mobs.tooltip")))
               .build()
         );
         y += 24;
      }

      int bottom = y + 10;
      if (own != null) {
         Component delete = Component.translatable(this.armed ? "diorama.menu.delete_confirm" : "diorama.waypoint.delete").withStyle(ChatFormatting.RED);
         this.addRenderableWidget(Button.builder(delete, b -> this.delete()).bounds(left, bottom, 128, 20).build());
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(left + 130 + 2, bottom, 128, 20).build());
      } else {
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(this.width / 2 - 100, bottom, 200, 20).build());
      }
   }

   private static Component namesValue(Settings.NameTags names) {
      return Component.translatable("diorama.settings.player_names." + names.name().toLowerCase(Locale.ROOT));
   }

   private void delete() {
      if (!this.armed) {
         this.armed = true;
         this.rebuildWidgets();
      } else {
         Settings.get().radarCategories.remove(this.own);
         RadarCategories.changed();
         Settings.get().save();
         this.onClose();
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      graphics.drawCenteredString(this.font, this.entry.name(), this.width / 2, this.top() - 20, -1);
      if (this.own != null) {
         graphics.drawString(this.font, Component.translatable("diorama.waypoint.name"), this.width / 2 - 130, this.top() + 6, -6250336, true);
      }
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
