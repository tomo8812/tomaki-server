package dev.diorama;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class RadarMobsScreen extends Screen {
   private static final int WIDTH = 300;
   private static final int COLUMN_GAP = 8;
   private static final int ROW = 22;
   private static final int TOP = 58;
   private final Screen parent;
   private final RadarCategories.Category category;
   private final List<RadarCategories.Mob> mobs = RadarCategories.mobs();
   private final List<RadarMobsScreen.Row> rows = new ArrayList<>();
   private final List<AbstractWidget> listed = new ArrayList<>();
   private @Nullable EditBox search;
   private String typed = "";
   private int page;

   public RadarMobsScreen(Screen parent, RadarCategories.Category category) {
      super(Component.translatable("diorama.radar_mobs.title", new Object[]{category.name()}));
      this.parent = parent;
      this.category = category;
      Set<String> known = new HashSet<>();
      this.mobs.forEach(mobx -> known.add(mobx.id()));

      for (String listed : category.mobs) {
         String mod = listed.endsWith(":*") ? listed.substring(0, listed.length() - 2) : null;
         if (!known.contains(listed) && (mod == null || !RadarCategories.mobMods(this.mobs).contains(mod))) {
            this.rows.add(new RadarMobsScreen.Row(listed, Component.literal(listed), Component.literal(listed)));
         }
      }

      for (String mod : RadarCategories.mobMods(this.mobs)) {
         this.rows
            .add(
               new RadarMobsScreen.Row(
                  mod + ":*", Component.translatable("diorama.radar_mobs.every", new Object[]{RadarCategories.modName(mod)}), Component.literal(mod + ":*")
               )
            );
      }

      for (RadarCategories.Mob mob : this.mobs) {
         this.rows.add(new RadarMobsScreen.Row(mob.id(), mob.name(), Component.literal(mob.id())));
      }
   }

   private int rowsPerPage() {
      return Math.max(1, (this.height - 58 - 36) / 22);
   }

   protected void init() {
      int left = this.width / 2 - 150;
      EditBox box = new EditBox(this.font, left, 32, 300, 20, Component.translatable("diorama.radar_mobs.search"));
      box.setMaxLength(64);
      box.setValue(this.typed);
      box.setResponder(text -> {
         if (!text.equals(this.typed)) {
            this.typed = text;
            this.page = 0;
            this.list();
         }
      });
      this.search = (EditBox)this.addRenderableWidget(box);
      this.listed.clear();
      this.list();
      this.setInitialFocus(box);
   }

   private List<RadarMobsScreen.Row> found() {
      String wanted = this.typed.trim().toLowerCase(Locale.ROOT);
      if (wanted.isEmpty()) {
         return this.rows;
      }

      List<RadarMobsScreen.Row> found = new ArrayList<>();

      for (RadarMobsScreen.Row row : this.rows) {
         if (row.label().getString().toLowerCase(Locale.ROOT).contains(wanted) || row.entry().contains(wanted)) {
            found.add(row);
         }
      }

      return found;
   }

   private void list() {
      this.listed.forEach(x$0 -> this.removeWidget(x$0));
      this.listed.clear();
      int left = this.width / 2 - 150;
      int column = 146;
      List<RadarMobsScreen.Row> found = this.found();
      int perPage = this.rowsPerPage() * 2;
      int pages = Math.max(1, (found.size() + perPage - 1) / perPage);
      this.page = Math.max(0, Math.min(this.page, pages - 1));

      for (int i = this.page * perPage; i < Math.min(found.size(), (this.page + 1) * perPage); i++) {
         RadarMobsScreen.Row row = found.get(i);
         boolean own = this.category.mobs.contains(row.entry());
         boolean byPattern = false;

         for (String mob : this.category.mobs) {
            byPattern |= !mob.equals(row.entry()) && RadarCategories.matches(mob, row.entry());
         }

         Checkbox checkbox = SettingsScreen.checkbox(row.label(), this.font, own || byPattern, column, ticked -> {
            this.category.mobs.remove(row.entry());
            if (ticked) {
               this.category.mobs.add(row.entry());
            }

            RadarCategories.changed();
            if (row.entry().endsWith("*")) {
               this.list();
            }
         });
         int slot = i - this.page * perPage;
         checkbox.setPosition(left + slot % 2 * (column + 8), 58 + slot / 2 * 22);
         checkbox.setTooltip(Tooltip.create(row.tooltip()));
         if (byPattern) {
            checkbox.active = false;
            checkbox.setAlpha(0.5F);
         }

         this.listed.add((AbstractWidget)this.addRenderableWidget(checkbox));
      }

      int bottom = this.height - 26;
      if (pages > 1) {
         Button back = Button.builder(Component.literal("<"), b -> this.turn(-1)).bounds(left, bottom, 20, 20).build();
         back.active = this.page > 0;
         this.listed.add((AbstractWidget)this.addRenderableWidget(back));
         Button on = Button.builder(Component.literal(">"), b -> this.turn(1)).bounds(left + 300 - 20, bottom, 20, 20).build();
         on.active = this.page < pages - 1;
         this.listed.add((AbstractWidget)this.addRenderableWidget(on));
      }

      this.listed
         .add(
            (AbstractWidget)this.addRenderableWidget(
               Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(this.width / 2 - 100, bottom, 200, 20).build()
            )
         );
   }

   private void turn(int pages) {
      this.page += pages;
      this.list();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 14, -1);
      if (this.search != null && this.search.getValue().isEmpty()) {
         graphics.drawString(this.font, Component.translatable("diorama.radar_mobs.search"), this.search.getX() + 4, this.search.getY() + 6, -9408400, false);
      }
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private record Row(String entry, Component label, Component tooltip) {
   }
}
