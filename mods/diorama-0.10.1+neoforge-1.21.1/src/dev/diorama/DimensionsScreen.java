package dev.diorama;

import dev.diorama.waypoint.Waypoints;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class DimensionsScreen extends Screen {
   private static final int WIDTH = 280;
   private static final int COLUMN_GAP = 8;
   private static final int ROW = 22;
   private static final int TOP = 62;
   private final Screen parent;
   private int page;

   public DimensionsScreen(Screen parent) {
      super(Component.translatable("diorama.settings.map_off"));
      this.parent = parent;
   }

   public static List<String> dimensions() {
      List<String> dimensions = new ArrayList<>(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"));
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      Set<String> others = new TreeSet<>();
      if (connection != null) {
         connection.levels().forEach(level -> others.add(level.location().toString()));
         dimensions.retainAll(others);
      }

      others.addAll(Settings.get().mapOffDimensions);
      others.addAll(serverSet());
      others.removeAll(dimensions);
      dimensions.addAll(others);
      return dimensions;
   }

   private static Set<String> serverSet() {
      Set<String> set = new TreeSet<>();
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      if (connection != null) {
         connection.levels().forEach(level -> {
            String id = level.location().toString();
            if (ServerRules.mapOffByServer(id)) {
               set.add(id);
            }
         });
      }

      return set;
   }

   public static Component summary() {
      Set<String> off = new TreeSet<>(Settings.get().mapOffDimensions);
      off.addAll(serverSet());

      return (Component)(switch (off.size()) {
         case 0 -> Component.translatable("diorama.settings.map_off.none");
         case 1 -> Waypoints.dimensionName(off.iterator().next());
         default -> Component.literal(Integer.toString(off.size()));
      });
   }

   private int rowsPerPage() {
      return Math.max(1, (this.height - 62 - 36) / 22);
   }

   protected void init() {
      int left = this.width / 2 - 140;
      int column = 136;
      MultiLineTextWidget about = new MultiLineTextWidget(Component.translatable("diorama.map_off.about").withStyle(ChatFormatting.GRAY), this.font);
      about.setMaxWidth(280);
      about.setCentered(true);
      about.setPosition(this.width / 2 - about.getWidth() / 2, 30);
      this.addRenderableWidget(about);
      List<String> dimensions = dimensions();
      Set<String> mine = Settings.get().mapOffDimensions;
      int perPage = this.rowsPerPage() * 2;
      int pages = Math.max(1, (dimensions.size() + perPage - 1) / perPage);
      this.page = Math.min(this.page, pages - 1);

      for (int i = this.page * perPage; i < Math.min(dimensions.size(), (this.page + 1) * perPage); i++) {
         String dimension = dimensions.get(i);
         boolean server = ServerRules.mapOffByServer(dimension);
         Checkbox checkbox = SettingsScreen.checkbox(Waypoints.dimensionName(dimension), this.font, server || mine.contains(dimension), column, off -> {
            if (off) {
               mine.add(dimension);
            } else {
               mine.remove(dimension);
            }
         });
         int slot = i - this.page * perPage;
         checkbox.setPosition(left + slot % 2 * (column + 8), 62 + slot / 2 * 22);
         Component id = Component.literal(dimension);
         if (server) {
            checkbox.active = false;
            checkbox.setAlpha(0.5F);
            checkbox.setTooltip(Tooltip.create(Component.empty().append(SettingsScreen.serverSetLine(false)).append("\n\n").append(id)));
         } else {
            checkbox.setTooltip(Tooltip.create(id));
         }

         this.addRenderableWidget(checkbox);
      }

      int bottom = this.height - 26;
      if (pages > 1) {
         Button back = Button.builder(Component.literal("<"), b -> this.turn(-1)).bounds(left, bottom, 20, 20).build();
         back.active = this.page > 0;
         this.addRenderableWidget(back);
         Button on = Button.builder(Component.literal(">"), b -> this.turn(1)).bounds(left + 280 - 20, bottom, 20, 20).build();
         on.active = this.page < pages - 1;
         this.addRenderableWidget(on);
      }

      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(this.width / 2 - 100, bottom, 200, 20).build());
   }

   private void turn(int pages) {
      this.page += pages;
      this.rebuildWidgets();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 14, -1);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
