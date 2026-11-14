package dev.diorama;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class RadarCategoriesScreen extends Screen {
   private static final int WIDTH = 300;
   private static final int ROW = 24;
   private static final int TOP = 62;
   private static final int EDIT = 50;
   private static final int SUMMARY = -6250336;
   private int edit = 50;
   private final Screen parent;
   private int page;
   private final List<Component> summaries = new ArrayList<>();

   public RadarCategoriesScreen(Screen parent) {
      super(Component.translatable("diorama.radar_categories.title"));
      this.parent = parent;
   }

   public static Component summary() {
      return Component.literal(Integer.toString(RadarCategories.all().size()));
   }

   private int rowsPerPage() {
      return Math.max(1, (this.height - 62 - 36) / 24);
   }

   protected void init() {
      int left = this.width / 2 - 150;
      this.edit = Math.max(50, this.font.width(Component.translatable("diorama.radar_categories.edit")) + 10);
      MultiLineTextWidget about = new MultiLineTextWidget(Component.translatable("diorama.radar_categories.about").withStyle(ChatFormatting.GRAY), this.font);
      about.setMaxWidth(300);
      about.setCentered(true);
      about.setPosition(this.width / 2 - about.getWidth() / 2, 30);
      this.addRenderableWidget(about);
      List<RadarCategories.Entry> entries = RadarCategories.all();
      int perPage = this.rowsPerPage();
      int pages = Math.max(1, (entries.size() + perPage - 1) / perPage);
      this.page = Math.min(this.page, pages - 1);
      this.summaries.clear();

      for (int i = this.page * perPage; i < Math.min(entries.size(), (this.page + 1) * perPage); i++) {
         RadarCategories.Entry entry = entries.get(i);
         int y = 62 + (i - this.page * perPage) * 24;
         Component summary = summary(entry);
         Checkbox checkbox = SettingsScreen.checkbox(entry.name(), this.font, entry.shown(), 300 - this.edit - 12 - this.font.width(summary), entry::shown);
         checkbox.setPosition(left, y);
         this.addRenderableWidget(checkbox);
         this.summaries.add(summary);
         this.addRenderableWidget(
            Button.builder(Component.translatable("diorama.radar_categories.edit"), b -> this.minecraft.setScreen(new RadarCategoryScreen(this, entry)))
               .bounds(left + 300 - this.edit, y, this.edit, 20)
               .build()
         );
      }

      int bottom = this.height - 26;
      if (pages > 1) {
         Button back = Button.builder(Component.literal("<"), b -> this.turn(-1)).bounds(left, bottom, 20, 20).build();
         back.active = this.page > 0;
         this.addRenderableWidget(back);
         Button on = Button.builder(Component.literal(">"), b -> this.turn(1)).bounds(left + 300 - 20, bottom, 20, 20).build();
         on.active = this.page < pages - 1;
         this.addRenderableWidget(on);
      }

      this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.radar_categories.new"), b -> this.add()).bounds(this.width / 2 - 122, bottom, 120, 20).build()
      );
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(this.width / 2 + 2, bottom, 120, 20).build());
   }

   private static Component summary(RadarCategories.Entry entry) {
      List<Component> parts = new ArrayList<>();
      if (entry instanceof RadarCategories.Category category) {
         parts.add(Component.translatable("diorama.radar_categories.mobs_count", new Object[]{category.mobs.size()}));
      }

      if (entry.iconScale() != 0) {
         parts.add(Component.literal(entry.iconScale() + "%"));
      }

      if (entry.hasNames() && entry.names() != Settings.NameTags.NEVER) {
         parts.add(Component.translatable("diorama.radar_categories.names_shown"));
      }

      MutableComponent line = Component.empty();

      for (int i = 0; i < parts.size(); i++) {
         if (i > 0) {
            line.append(" · ");
         }

         line.append(parts.get(i));
      }

      return line;
   }

   private void add() {
      RadarCategories.Category category = new RadarCategories.Category();
      Settings.get().radarCategories.add(0, category);
      RadarCategories.changed();
      Settings.get().save();
      this.page = 0;
      this.minecraft.setScreen(new RadarCategoryScreen(this, category));
   }

   private void turn(int pages) {
      this.page += pages;
      this.rebuildWidgets();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 14, -1);
      int right = this.width / 2 + 150 - this.edit - 6;

      for (int i = 0; i < this.summaries.size(); i++) {
         Component summary = this.summaries.get(i);
         graphics.drawString(this.font, summary, right - this.font.width(summary), 62 + i * 24 + 6, -6250336, true);
      }
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
