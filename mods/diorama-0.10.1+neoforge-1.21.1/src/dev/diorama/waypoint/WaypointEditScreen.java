package dev.diorama.waypoint;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.diorama.MapScreen;
import dev.diorama.MapSession;
import dev.diorama.render.Pins;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public final class WaypointEditScreen extends Screen {
   private static final int WIDTH = 292;
   private static final int FORM = 200;
   private static final int PREVIEW_LEFT = 212;
   private static final int SLOT = 12;
   private static final int BAR_HEIGHT = 8;
   private static final int BAR_WIDTH = 96;
   private static final int BAR_GAP = 8;
   private static final int INVALID_TEXT = -65536;
   private static final int LABEL = -6250336;
   private static final int MAX_NAME = 32;
   private final @Nullable Screen parent;
   private final Waypoint waypoint;
   private final boolean isNew;
   private final Waypoint preview = new Waypoint();
   private final MapPiece piece = new MapPiece(64, 40, 12);
   private EditBox name;
   private EditBox initials;
   private EditBox x;
   private EditBox y;
   private EditBox z;
   private @Nullable Button save;
   private @Nullable Button share;
   private @Nullable Button teleport;
   private int color;
   private boolean visible;
   private EditBox hex;
   private int swatchX;
   private int swatchY;
   private float hue;
   private float shade = 0.5F;
   private int dragging;
   private String folder;
   private @Nullable Button folderButton;
   private @Nullable FolderMenu folderMenu;
   private String icon;
   private @Nullable Button iconButton;

   public WaypointEditScreen(@Nullable Screen parent, Waypoint waypoint, boolean isNew) {
      super(Component.translatable(isNew ? "diorama.waypoint.new" : "diorama.waypoint.edit"));
      this.parent = parent;
      this.waypoint = waypoint;
      this.isNew = isNew;
      this.color = waypoint.color;
      this.visible = waypoint.visible;
      this.icon = waypoint.icon == null ? "" : waypoint.icon;
      WaypointStore store = WaypointStore.get();
      String known = store == null ? null : store.folderName(waypoint.folder);
      this.folder = known == null ? "" : known;
      this.matchBars();
   }

   private int left() {
      return this.width / 2 - 146;
   }

   private int top() {
      return this.height / 2 - 92;
   }

   protected void init() {
      int left = this.left();
      int top = this.top();
      int cx = left + 100;
      this.name = this.field(left, top + 16, 112, draft(this.name, this.waypoint.shownName()), "diorama.waypoint.name", 32);
      this.iconButton = (Button)this.addRenderableWidget(Button.builder(this.iconLabel(), b -> this.pickIcon()).bounds(left + 116, top + 16, 40, 20).build());
      this.iconButton.active = this.waypoint.kind != Waypoint.Kind.DEATH;
      this.initials = this.field(left + 160, top + 16, 40, draft(this.initials, this.waypoint.initials), "diorama.waypoint.initials", 4);
      this.x = this.field(left, top + 56, 64, draft(this.x, Integer.toString(this.waypoint.x)), "diorama.waypoint.x", 9);
      this.y = this.field(left + 68, top + 56, 64, draft(this.y, Integer.toString(this.waypoint.y)), "diorama.waypoint.y", 9);
      this.z = this.field(left + 136, top + 56, 64, draft(this.z, Integer.toString(this.waypoint.z)), "diorama.waypoint.z", 9);
      this.x.setResponder(text -> this.validate());
      this.y.setResponder(text -> this.validate());
      this.z.setResponder(text -> this.validate());
      this.hintHere();
      this.swatchX = cx - 96;
      this.swatchY = top + 92;
      this.hex = this.field(left + 24, top + 132, 70, draft(this.hex, WaypointColours.hex(this.color)), "diorama.waypoint.hex", 7);
      this.hex.setResponder(text -> {
         Integer picked = WaypointColours.parse(text);
         this.hex.setTextColor(picked != null ? 14737632 : -65536);
         if (picked != null && (picked & 16777215) != (this.color & 16777215)) {
            this.color = picked;
            this.matchBars();
         }
      });
      this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.waypoint.random"), b -> this.pick(WaypointColours.random(this.color)))
            .bounds(left + 98, top + 132, 102, 20)
            .build()
      );
      this.addRenderableWidget(
         Checkbox.builder(Component.translatable("diorama.waypoint.show_on_maps"), this.font)
            .pos(left, top + 157)
            .selected(this.visible)
            .onValueChange((box, value) -> this.visible = value)
            .build()
      );
      int previewLeft = left + 212;
      int previewWidth = 80;
      this.share = null;
      this.teleport = null;
      this.folderMenu = null;
      this.folderButton = (Button)this.addRenderableWidget(
         Button.builder(this.folderLabel(), b -> this.openFolderMenu())
            .bounds(previewLeft, this.previewBottom() + 14, previewWidth, 20)
            .tooltip(Tooltip.create(Component.translatable("diorama.waypoint.folder_tooltip")))
            .build()
      );
      if (!this.isNew) {
         this.share = (Button)this.addRenderableWidget(
            Button.builder(Component.translatable("diorama.waypoint.share"), b -> this.share())
               .bounds(previewLeft, this.previewBottom() + 38, previewWidth, 20)
               .build()
         );
         this.teleport = (Button)this.addRenderableWidget(
            Button.builder(Component.translatable("diorama.waypoint.teleport"), b -> this.teleport())
               .bounds(previewLeft, this.previewBottom() + 62, previewWidth, 20)
               .build()
         );
      }

      int half = 144;
      this.save = (Button)this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.waypoint.save"), b -> this.save()).bounds(left, top + 192, half, 20).build()
      );
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(left + 292 - half, top + 192, half, 20).build());
      this.validate();
      this.setInitialFocus(this.name);
   }

   private Component iconLabel() {
      String text = WaypointIconScreen.name(this.icon).getString();
      if (this.font.width(text) > 34) {
         text = this.font.plainSubstrByWidth(text, 34 - this.font.width("..")) + "..";
      }

      return Component.literal(text);
   }

   private void pickIcon() {
      this.preview.name = this.typedName();
      this.preview.initials = this.initials.getValue().trim();
      this.preview.color = this.color;
      this.minecraft.setScreen(new WaypointIconScreen(this, this.preview, this.icon, id -> {
         this.icon = id;
         if (this.iconButton != null) {
            this.iconButton.setMessage(this.iconLabel());
         }
      }));
   }

   private int previewTop() {
      return this.top() + 16;
   }

   private int previewBottom() {
      return this.top() + 98;
   }

   private List<String> folders() {
      List<String> out = new ArrayList<>();
      out.add("");
      WaypointStore store = WaypointStore.get();
      if (store != null) {
         out.addAll(store.customFolders());
      }

      return out;
   }

   private void openFolderMenu() {
      Button button = this.folderButton;
      if (button != null) {
         WaypointStore store = WaypointStore.get();
         List<FolderMenu.Entry> entries = new ArrayList<>();

         for (String folder : this.folders()) {
            String name = folder.isEmpty() ? Waypoints.dimensionName(this.waypoint.dimension).getString() : folder;
            String key = folder.isEmpty() ? WaypointStore.dimensionKey(this.waypoint.dimension) : WaypointStore.customKey(folder);
            entries.add(new FolderMenu.Entry(folder, name, -1, store != null && store.folderHidden(key)));
         }

         this.folderMenu = new FolderMenu(
            this.font, entries, this.folder, button.getX(), button.getY(), button.getY() + button.getHeight(), button.getWidth(), this.height
         );
         button.setTooltip(null);
      }
   }

   private void closeFolderMenu() {
      this.folderMenu = null;
      if (this.folderButton != null) {
         this.folderButton.setTooltip(Tooltip.create(Component.translatable("diorama.waypoint.folder_tooltip")));
      }
   }

   private void clickFolderMenu(double x, double y, int button) {
      FolderMenu menu = this.folderMenu;
      this.closeFolderMenu();
      this.pickFolder(menu != null && button == 0 ? menu.under(x, y) : null);
   }

   private void keyFolderMenu(int key) {
      FolderMenu menu = this.folderMenu;
      if (menu != null && key != 256) {
         FolderMenu.Entry picked = menu.keyPressed(key);
         if (picked != null) {
            this.closeFolderMenu();
            this.pickFolder(picked);
         }
      } else {
         this.closeFolderMenu();
      }
   }

   private void pickFolder(FolderMenu.@Nullable Entry picked) {
      if (picked != null) {
         this.folder = picked.key();
         if (this.folderButton != null) {
            this.folderButton.setMessage(this.folderLabel());
         }
      }
   }

   private Component folderLabel() {
      Component name = (Component)(this.folder.isEmpty() ? Waypoints.dimensionName(this.waypoint.dimension) : Component.literal(this.folder));
      String text = name.getString();
      int room = 72;
      if (this.font.width(text) > room) {
         text = this.font.plainSubstrByWidth(text, room - this.font.width("...")) + "...";
      }

      return Component.literal(text);
   }

   private static String draft(@Nullable EditBox box, String saved) {
      return box != null ? box.getValue() : saved;
   }

   private void validate() {
      boolean valid = true;

      for (EditBox box : new EditBox[]{this.x, this.y, this.z}) {
         boolean reads = this.coordinate(box) != null;
         box.setTextColor(reads ? 14737632 : -65536);
         valid &= reads;
      }

      if (this.save != null) {
         this.save.active = valid;
      }

      if (this.share != null) {
         this.share.active = valid;
      }

      if (this.teleport != null) {
         this.teleport.active = valid && Waypoints.canTeleport(this.waypoint.dimension);
      }
   }

   private EditBox field(int x, int y, int width, String value, String hint, int maxLength) {
      EditBox box = new EditBox(this.font, x, y, width, 20, Component.translatable(hint));
      box.setMaxLength(maxLength);
      box.setValue(value);
      box.setHint(Component.translatable(hint));
      return (EditBox)this.addRenderableWidget(box);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      boolean menuOpen = this.folderMenu != null;
      super.render(graphics, menuOpen ? -1 : mouseX, menuOpen ? -1 : mouseY, a);
      int left = this.left();
      int top = this.top();
      graphics.drawCenteredString(this.font, this.title, this.width / 2, top - 12, -1);
      graphics.drawString(this.font, Component.translatable("diorama.waypoint.name"), left, top + 5, -6250336, true);
      graphics.drawString(this.font, Component.translatable("diorama.waypoint.icon"), left + 116, top + 5, -6250336, true);
      graphics.drawString(this.font, Component.translatable("diorama.waypoint.initials"), left + 160, top + 5, -6250336, true);
      graphics.drawString(this.font, Component.translatable("diorama.waypoint.position"), left, top + 45, -6250336, true);
      Component colourLabel = Component.translatable("diorama.waypoint.colour");
      graphics.drawString(this.font, colourLabel, left, top + 81, -6250336, true);
      WaypointColours.Swatch current = WaypointColours.named(this.color);
      if (current != null) {
         graphics.drawString(this.font, Component.literal("· ").append(current.name()), left + this.font.width(colourLabel) + 4, top + 81, -1, true);
      }

      this.drawSwatches(graphics, menuOpen ? -1 : mouseX, menuOpen ? -1 : mouseY);
      this.drawPreview(graphics);
      FolderMenu menu = this.folderMenu;
      if (menu != null) {
         graphics.flush();
         PoseStack pose = graphics.pose();
         pose.pushPose();
         pose.translate(0.0F, 0.0F, 400.0F);
         menu.draw(graphics, mouseX, mouseY);
         pose.popPose();
      }
   }

   public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
      if (this.folderMenu != null) {
         this.folderMenu.scroll(scrollY);
         return true;
      } else {
         return super.mouseScrolled(x, y, scrollX, scrollY);
      }
   }

   private void drawSwatches(GuiGraphics graphics, int mouseX, int mouseY) {
      int rows = WaypointColours.rows();
      int x0 = this.swatchX;
      int y0 = this.swatchY;
      int x1 = x0 + 192;
      int y1 = y0 + rows * 12;
      graphics.fill(x0 - 1, y0 - 1, x1 + 1, y1 + 1, Integer.MIN_VALUE);
      int hovered = this.swatchAt(mouseX, mouseY);
      int selected = -1;

      for (int i = 0; i < WaypointColours.ALL.size(); i++) {
         int sx = x0 + i % 16 * 12;
         int sy = y0 + i / 16 * 12;
         int argb = WaypointColours.ALL.get(i).argb();
         if (i == hovered) {
            graphics.fill(sx, sy, sx + 12, sy + 12, -6250336);
         }

         graphics.fill(sx + 1, sy + 1, sx + 12 - 1, sy + 12 - 1, argb);
         if ((argb & 16777215) == (this.color & 16777215) && selected < 0) {
            selected = i;
         }
      }

      if (selected >= 0) {
         int sx = x0 + selected % 16 * 12;
         int sy = y0 + selected / 16 * 12;
         ring(graphics, sx - 1, sy - 1, sx + 12 + 1, sy + 12 + 1, -16777216);
         ring(graphics, sx, sy, sx + 12, sy + 12, -1);
      }

      int barY = this.barY();
      int hueX = this.hueX();
      int shadeX = hueX + 96 + 8;
      graphics.fill(hueX - 1, barY - 1, hueX + 96 + 1, barY + 8 + 1, -16777216);
      graphics.fill(shadeX - 1, barY - 1, shadeX + 96 + 1, barY + 8 + 1, -16777216);

      for (int i = 0; i < 96; i++) {
         float t = i / 95.0F;
         graphics.fill(hueX + i, barY, hueX + i + 1, barY + 8, WaypointColours.rgb(t, 1.0F, 1.0F));
         graphics.fill(shadeX + i, barY, shadeX + i + 1, barY + 8, WaypointColours.shade(this.hue, t));
      }

      marker(graphics, hueX + Math.round(this.hue * 95.0F), barY);
      marker(graphics, shadeX + Math.round(this.shade * 95.0F), barY);
      int chipX = this.left();
      int chipY = this.top() + 132;
      graphics.fill(chipX, chipY, chipX + 20, chipY + 20, -6250336);
      graphics.fill(chipX + 1, chipY + 1, chipX + 19, chipY + 19, -16777216);
      graphics.fill(chipX + 2, chipY + 2, chipX + 18, chipY + 18, 0xFF000000 | this.color);
      if (hovered >= 0) {
         WaypointColours.Swatch swatch = WaypointColours.ALL.get(hovered);
         this.setTooltipForNextRenderPass(Component.empty().append(swatch.name()).append(" " + WaypointColours.hex(swatch.argb())));
      }
   }

   private static void ring(GuiGraphics graphics, int x0, int y0, int x1, int y1, int color) {
      graphics.fill(x0, y0, x1, y0 + 1, color);
      graphics.fill(x0, y1 - 1, x1, y1, color);
      graphics.fill(x0, y0 + 1, x0 + 1, y1 - 1, color);
      graphics.fill(x1 - 1, y0 + 1, x1, y1 - 1, color);
   }

   private static void marker(GuiGraphics graphics, int x, int barY) {
      graphics.fill(x - 1, barY - 2, x + 2, barY + 8 + 2, -16777216);
      graphics.fill(x, barY - 1, x + 1, barY + 8 + 1, -1);
   }

   private int barY() {
      return this.swatchY + WaypointColours.rows() * 12 + 4;
   }

   private int hueX() {
      return this.left() + 0;
   }

   private int swatchAt(double x, double y) {
      if (!(x < this.swatchX) && !(y < this.swatchY)) {
         int column = (int)((x - this.swatchX) / 12.0);
         int row = (int)((y - this.swatchY) / 12.0);
         int i = row * 16 + column;
         return column < 16 && i < WaypointColours.ALL.size() ? i : -1;
      } else {
         return -1;
      }
   }

   private int barAt(double x, double y) {
      int barY = this.barY();
      if (!(y < barY - 2) && !(y >= barY + 8 + 2)) {
         int hueX = this.hueX();
         int shadeX = hueX + 96 + 8;
         if (x >= hueX - 1 && x < hueX + 96 + 1) {
            return 1;
         } else {
            return x >= shadeX - 1 && x < shadeX + 96 + 1 ? 2 : 0;
         }
      } else {
         return 0;
      }
   }

   private void dragBar(int bar, double x) {
      int start = bar == 1 ? this.hueX() : this.hueX() + 96 + 8;
      float t = (float)Math.max(0.0, Math.min(1.0, (x - start) / 95.0));
      if (bar == 1) {
         this.hue = Math.min(t, 0.999F);
      } else {
         this.shade = t;
      }

      this.color = WaypointColours.shade(this.hue, this.shade);
      this.hex.setValue(WaypointColours.hex(this.color));
   }

   private void pick(int argb) {
      this.color = argb;
      this.matchBars();
      this.hex.setValue(WaypointColours.hex(argb));
   }

   private void matchBars() {
      float[] hsv = WaypointColours.hsv(this.color);
      if (hsv[1] > 0.05F) {
         this.hue = hsv[0];
      }

      this.shade = WaypointColours.shadeOf(this.color);
   }

   private void drawPreview(GuiGraphics graphics) {
      int x0 = this.left() + 212;
      int x1 = this.left() + 292;
      int y0 = this.previewTop();
      int y1 = this.previewBottom();
      graphics.drawString(this.font, Component.translatable("diorama.waypoint.preview"), x0, this.top() + 5, -6250336, true);
      ScreenArt.list(graphics, x0, y0, x1, y1);
      this.preview.name = this.typedName();
      this.preview.initials = this.initials.getValue().trim();
      this.preview.color = this.color;
      this.preview.kind = this.waypoint.kind;
      this.preview.icon = this.icon;
      float radius = Pins.reach(this.preview, 15) - 1.0F;
      int rise = Math.round(2.0F * (Pins.headAbove(this.preview, 15) + radius)) - this.piece.tipY();
      int total = rise + this.piece.outerHeight();
      int pieceX = (x0 + x1) / 2 - this.piece.outerWidth() / 2;
      int pieceY = y0 + (y1 - y0 - total) / 2 + rise;
      this.piece
         .draw(
            graphics,
            pieceX,
            pieceY,
            new MapPiece.Place(
               this.waypoint.dimension, this.typed(this.x, this.waypoint.x), this.typed(this.y, this.waypoint.y), this.typed(this.z, this.waypoint.z)
            )
         );
      graphics.drawString(this.font, Component.translatable("diorama.waypoint.folder_caption"), x0, y1 + 3, -6250336, true);
      graphics.pose().pushPose();
      graphics.pose().translate(pieceX + this.piece.tipX(), pieceY + this.piece.tipY(), 0.0F);
      graphics.pose().scale(2.0F, 2.0F, 1.0F);
      Pins.standing(graphics, this.preview, 0.0F, 0.0F, 15);
      graphics.pose().popPose();
   }

   public boolean keyPressed(int key, int scancode, int modifiers) {
      if (this.folderMenu != null) {
         this.keyFolderMenu(key);
         return true;
      } else {
         return super.keyPressed(key, scancode, modifiers);
      }
   }

   public boolean mouseClicked(double x, double y, int button) {
      if (this.folderMenu != null) {
         this.clickFolderMenu(x, y, button);
         return true;
      } else {
         return this.clicked(x, y, button) || super.mouseClicked(x, y, button);
      }
   }

   public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
      return this.dragged(x) || super.mouseDragged(x, y, button, dx, dy);
   }

   public boolean mouseReleased(double x, double y, int button) {
      this.dragging = 0;
      return super.mouseReleased(x, y, button);
   }

   private boolean clicked(double x, double y, int button) {
      if (button == 0) {
         int i = this.swatchAt(x, y);
         if (i >= 0) {
            this.pick(WaypointColours.ALL.get(i).argb());
            return true;
         }

         int bar = this.barAt(x, y);
         if (bar != 0) {
            this.dragging = bar;
            this.dragBar(bar, x);
            return true;
         }
      }

      return false;
   }

   private boolean dragged(double x) {
      if (this.dragging == 0) {
         return false;
      }

      this.dragBar(this.dragging, x);
      return true;
   }

   public @Nullable FolderMenu folderMenu() {
      return this.folderMenu;
   }

   public @Nullable Button folderButton() {
      return this.folderButton;
   }

   public Waypoint waypoint() {
      return this.waypoint;
   }

   private String typedName() {
      String typed = this.name.getValue().trim();
      String shown = this.waypoint.shownName();
      shown = shown.length() > 32 ? shown.substring(0, 32) : shown;
      return !typed.isEmpty() && !typed.equals(shown.trim()) ? typed : this.waypoint.name;
   }

   private @Nullable Waypoint shown() {
      Integer x = this.coordinate(this.x);
      Integer y = this.coordinate(this.y);
      Integer z = this.coordinate(this.z);
      if (x != null && y != null && z != null) {
         Waypoint shown = new Waypoint(this.typedName(), x, y, z, this.waypoint.dimension, this.color);
         shown.kind = this.waypoint.kind;
         shown.created = this.waypoint.created;
         String initials = this.initials.getValue().trim();
         shown.initials = initials.isEmpty() ? shown.initials : initials;
         return shown;
      } else {
         return null;
      }
   }

   private void share() {
      Waypoint shown = this.shown();
      if (shown != null) {
         if (WaypointShare.send(shown)) {
            this.onClose();
         }
      }
   }

   private void teleport() {
      Waypoint shown = this.shown();
      if (shown != null) {
         Waypoints.teleport(shown.dimension, shown.x, shown.y, shown.z);
         this.onClose();
      }
   }

   private void save() {
      Waypoint shown = this.shown();
      if (shown != null) {
         WaypointStore store = WaypointStore.get();
         if (store == null) {
            this.onClose();
         } else {
            this.waypoint.edited = this.waypoint.edited
               | (
                  this.waypoint.kind.isPortal()
                     && (!shown.name.equals(this.waypoint.name) || shown.x != this.waypoint.x || shown.y != this.waypoint.y || shown.z != this.waypoint.z)
               );
            this.waypoint.name = shown.name;
            this.waypoint.initials = this.initials.getValue().trim();
            this.waypoint.x = shown.x;
            this.waypoint.y = shown.y;
            this.waypoint.z = shown.z;
            this.waypoint.color = this.color;
            this.waypoint.icon = this.icon;
            this.waypoint.visible = this.visible;
            this.waypoint.folder = this.folder;
            if (this.waypoint.kind == Waypoint.Kind.TEMPORARY) {
               this.waypoint.kind = Waypoint.Kind.MANUAL;
            }

            if (this.isNew) {
               store.add(this.waypoint);
            } else {
               store.changed();
            }

            this.onClose();
         }
      }
   }

   private int typed(EditBox box, int saved) {
      Integer value = this.coordinate(box);
      return value == null ? saved : value;
   }

   private @Nullable Integer coordinate(EditBox box) {
      return box.getValue().isBlank() ? this.here(box) : parse(box.getValue());
   }

   private @Nullable Integer here(EditBox box) {
      Entity viewer = MapSession.viewer();
      String dimension = Waypoints.dimension();
      if (viewer == null || dimension == null) {
         return null;
      }

      if (box == this.y) {
         return viewer.blockPosition().getY();
      }

      double scale = dimension.equals(this.waypoint.dimension)
         ? 1.0
         : viewer.level().dimensionType().coordinateScale() / MapScreen.dimensionScale(this.waypoint.dimension);
      return Mth.floor((box == this.x ? viewer.blockPosition().getX() : viewer.blockPosition().getZ()) * scale);
   }

   private void hintHere() {
      this.hintHere(this.x, "diorama.waypoint.x");
      this.hintHere(this.y, "diorama.waypoint.y");
      this.hintHere(this.z, "diorama.waypoint.z");
   }

   private void hintHere(EditBox box, String axis) {
      Integer here = this.here(box);
      box.setHint((here == null ? Component.translatable(axis) : Component.literal(Integer.toString(here))).withStyle(ChatFormatting.DARK_GRAY));
   }

   private static @Nullable Integer parse(String text) {
      try {
         return Integer.parseInt(text.trim());
      } catch (NumberFormatException e) {
         return null;
      }
   }

   public void tick() {
      super.tick();
      this.hintHere();
      this.piece.tick();
   }

   public void removed() {
      this.piece.close();
      super.removed();
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
