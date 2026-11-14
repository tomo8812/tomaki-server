package dev.diorama.waypoint;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class WaypointFolderScreen extends Screen {
   private static final int WIDTH = 200;
   private static final int LABEL = -6250336;
   private static final int RED = -43691;
   private final WaypointListScreen parent;
   private final @Nullable String existing;
   private @Nullable EditBox name;
   private @Nullable Button save;
   private @Nullable Component problem;

   public WaypointFolderScreen(WaypointListScreen parent, @Nullable String existing) {
      super(Component.translatable(existing == null ? "diorama.folder.new" : "diorama.folder.rename_title"));
      this.parent = parent;
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
      String typed = this.name != null ? this.name.getValue() : (this.existing != null ? this.existing : "");
      EditBox box = new EditBox(this.font, left, top + 16, 200, 20, Component.translatable("diorama.folder.name"));
      box.setMaxLength(32);
      box.setValue(typed);
      box.setHint(Component.translatable("diorama.folder.name_hint"));
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
      WaypointStore store = WaypointStore.get();
      String typed = this.name == null ? "" : this.name.getValue().trim();
      String clash = store == null ? null : store.folderName(typed);
      if (typed.isEmpty()) {
         this.problem = null;
      } else if (clash != null && !clash.equals(this.existing)) {
         this.problem = Component.translatable("diorama.folder.taken");
      } else {
         this.problem = null;
      }

      if (this.save != null) {
         this.save.active = store != null && !typed.isEmpty() && this.problem == null;
      }
   }

   private void save() {
      WaypointStore store = WaypointStore.get();
      if (store != null && this.name != null) {
         String typed = this.name.getValue().trim();
         boolean done = this.existing == null ? store.addFolder(typed) : store.renameFolder(this.existing, typed);
         if (done) {
            this.parent.showFolder(WaypointStore.customKey(store.folderName(typed)));
            this.onClose();
         }
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      int left = this.left();
      int top = this.top();
      graphics.drawCenteredString(this.font, this.title, this.width / 2, top - 12, -1);
      graphics.drawString(this.font, Component.translatable("diorama.folder.name"), left, top + 5, -6250336, true);
      if (this.problem != null) {
         graphics.drawString(this.font, this.problem, left, top + 42, -43691, true);
      } else {
         graphics.drawString(this.font, Component.translatable("diorama.folder.hint"), left, top + 42, -6250336, true);
      }
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
