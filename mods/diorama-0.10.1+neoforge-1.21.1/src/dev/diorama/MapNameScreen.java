package dev.diorama;

import dev.diorama.waypoint.Waypoints;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class MapNameScreen extends Screen {
   private static final int WIDTH = 200;
   private static final int LABEL = -6250336;
   private static final int RED = -43691;
   private final MapsScreen parent;
   private final String dimension;
   private final @Nullable String existing;
   private @Nullable EditBox name;
   private @Nullable Button save;
   private @Nullable Component problem;

   public MapNameScreen(MapsScreen parent, String dimension, @Nullable String existing) {
      super(Component.translatable(existing == null ? "diorama.maps.new" : "diorama.maps.rename_title"));
      this.parent = parent;
      this.dimension = dimension;
      this.existing = existing;
   }

   private int left() {
      return this.width / 2 - 100;
   }

   private int top() {
      return this.height / 2 - 40;
   }

   protected void init() {
      int left = this.left();
      int top = this.top();
      Maps maps = Maps.get();
      String typed = this.name != null
         ? this.name.getValue()
         : (maps == null ? "" : (this.existing != null ? maps.name(this.dimension, this.existing) : maps.suggestedName(this.dimension)));
      EditBox box = new EditBox(this.font, left, top + 16, 200, 20, Component.translatable("diorama.maps.name"));
      box.setMaxLength(32);
      box.setValue(typed);
      box.setResponder(text -> this.validate());
      this.name = (EditBox)this.addRenderableWidget(box);
      int half = 98;
      this.save = (Button)this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.waypoint.save"), b -> this.save()).bounds(left, top + 64, half, 20).build()
      );
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(left + 200 - half, top + 64, half, 20).build());
      this.validate();
      this.setInitialFocus(box);
   }

   private void validate() {
      Maps maps = Maps.get();
      String typed = this.name == null ? "" : this.name.getValue().trim();
      this.problem = !typed.isEmpty() && maps != null && maps.taken(this.dimension, typed, this.existing) ? Component.translatable("diorama.maps.taken") : null;
      if (this.save != null) {
         this.save.active = maps != null && !typed.isEmpty() && this.problem == null;
      }
   }

   private void save() {
      Maps maps = Maps.get();
      if (maps != null && this.name != null) {
         String typed = this.name.getValue().trim();
         if (this.existing == null) {
            String id = maps.create(this.dimension, typed);
            if (id != null) {
               this.parent.show(this.dimension, id);
               this.onClose();
               return;
            }
         } else if (maps.rename(this.dimension, this.existing, typed)) {
            this.parent.show(this.dimension, this.existing);
            this.onClose();
            return;
         }

         this.problem = Component.translatable("diorama.maps.not_saved");
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      int left = this.left();
      int top = this.top();
      graphics.drawCenteredString(this.font, this.title, this.width / 2, top - 24, -1);
      graphics.drawCenteredString(this.font, Waypoints.dimensionName(this.dimension), this.width / 2, top - 12, -6250336);
      graphics.drawString(this.font, Component.translatable("diorama.maps.name"), left, top + 5, -6250336, true);
      if (this.problem != null) {
         graphics.drawString(this.font, this.problem, left, top + 42, -43691, true);
      } else if (this.existing == null) {
         graphics.drawString(this.font, Component.translatable("diorama.maps.new_hint"), left, top + 42, -6250336, true);
      }
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
