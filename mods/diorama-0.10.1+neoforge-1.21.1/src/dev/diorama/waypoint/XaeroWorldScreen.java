package dev.diorama.waypoint;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

final class XaeroWorldScreen extends Screen {
   private static final int ROW = 36;
   private static final int WIDTH = 300;
   private final WaypointListScreen parent;
   private final List<XaeroImport.World> worlds;
   private int scroll;
   private int listTop;
   private int visibleRows;

   XaeroWorldScreen(WaypointListScreen parent, List<XaeroImport.World> worlds) {
      super(Component.translatable("diorama.xaero.pick_title"));
      this.parent = parent;
      this.worlds = worlds;
   }

   protected void init() {
      this.listTop = 50;
      this.visibleRows = Math.max(1, (this.height - this.listTop - 60) / 36);
      this.scroll = Math.max(0, Math.min(this.scroll, this.worlds.size() - this.visibleRows));
      WaypointStore store = WaypointStore.get();

      for (int i = 0; i < this.visibleRows && this.scroll + i < this.worlds.size(); i++) {
         XaeroImport.World world = this.worlds.get(this.scroll + i);
         int fresh = store == null ? 0 : XaeroImport.missing(world, store).size();
         Component label = fresh == 0
            ? Component.translatable("diorama.xaero.world_done", new Object[]{world.label(), dimensionName(world.dimension())})
            : Component.translatable("diorama.xaero.world", new Object[]{world.label(), dimensionName(world.dimension()), fresh});
         if (world.likely()) {
            label = Component.translatable("diorama.xaero.likely", new Object[]{label});
         }

         Button button = Button.builder(label, b -> {
            this.parent.importFrom(world);
            this.rebuildWidgets();
         }).bounds((this.width - 300) / 2, this.listTop + i * 36, 300, 20).build();
         button.active = fresh > 0;
         this.addRenderableWidget(button);
      }

      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.done"), b -> this.onClose()).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build()
      );
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 16, -1);
      graphics.drawCenteredString(this.font, Component.translatable("diorama.xaero.pick_detail"), this.width / 2, 30, -6250336);

      for (int i = 0; i < this.visibleRows && this.scroll + i < this.worlds.size(); i++) {
         XaeroImport.World world = this.worlds.get(this.scroll + i);
         String names = world.entries().stream().limit(4L).map(entry -> entry.waypoint().name).collect(Collectors.joining(", "));
         if (world.entries().size() > 4) {
            names = names + ", ...";
         }

         graphics.drawCenteredString(this.font, names, this.width / 2, this.listTop + i * 36 + 23, -7303024);
      }

      if (this.parent.imported() != null) {
         graphics.drawCenteredString(this.font, this.parent.imported(), this.width / 2, this.height - 46, -6233952);
      }
   }

   private static Component dimensionName(@Nullable String id) {
      if (id == null) {
         return Component.translatable("diorama.xaero.all_dimensions");
      }

      return switch (id) {
         case "minecraft:overworld" -> Component.translatable("diorama.xaero.overworld");
         case "minecraft:the_nether" -> Component.translatable("diorama.xaero.nether");
         case "minecraft:the_end" -> Component.translatable("diorama.xaero.end");
         default -> Component.literal(id);
      };
   }

   public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
      int next = Math.max(0, Math.min(this.scroll - (int)Math.signum(scrollY), this.worlds.size() - this.visibleRows));
      if (next != this.scroll) {
         this.scroll = next;
         this.rebuildWidgets();
      }

      return true;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
